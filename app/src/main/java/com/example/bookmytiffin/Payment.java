package com.example.bookmytiffin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shakebugs.shake.Shake;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.paytm.pgsdk.TransactionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class Payment extends AppCompatActivity {

    TextView amountTv, upiIdTv, nameTv, selectedpaymentdisplay, disclaimer, qrcodetext;

    FirebaseStorage firebaseStorage;
    StorageReference storageReference;

    StorageReference reference;

    private String notifykey, order_id, txnToken, midString = "ovDGVB84680066015981" ;

    int PaymentActivityRequestCode = 101;

    DatabaseReference mesref,scanFirebaseref;

    private FirebaseFunctions mFunctions;

    ImageView qrcode;
    //EditText noteEt;
    Button send;
    String amount, finalpaymentoption = "Online Payment";
    RadioGroup paymentoption;
    Tiffininfo tif;
    static Userinfo ownerinfo = new Userinfo();
    RadioButton scanRadio;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Shake.start(getApplication());
        setContentView(R.layout.activity_payment);

        Intent intent = getIntent();
        amount = intent.getStringExtra("amount");

        tif = Uploadtiffin.currtif;

        qrcode = findViewById(R.id.qrcode);
        qrcodetext = findViewById(R.id.qrcodetext);

        disclaimer = findViewById(R.id.disclaimer);
        send = findViewById(R.id.send);
        amountTv = findViewById(R.id.amount_et);
        nameTv = findViewById(R.id.name);
        //noteEt = findViewById(R.id.note);
        paymentoption = findViewById(R.id.paymentoption);
        selectedpaymentdisplay = findViewById(R.id.selectedpaymentoption);
        scanRadio = findViewById(R.id.scanradio);

        amountTv.setText("\u20B9"+amount);

        qrcode.setVisibility(View.GONE);
        qrcodetext.setVisibility(View.GONE);

        scanFirebaseref = FirebaseDatabase.getInstance().getReference("ScanAndPay");
        scanFirebaseref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String visible = snapshot.child("Visible").getValue(String.class);
                if(visible.equals("true")){
                    scanRadio.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                scanRadio.setVisibility(View.VISIBLE);
            }
        });


        send.setOnClickListener(view -> {
            setOrderId();
            if(finalpaymentoption.equals("Online Payment")){
                mFunctions = FirebaseFunctions.getInstance();
                initializePayment();
            }
            else {
                recordOrder();
            }
        });

        qrcodetext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                download();
                Toast.makeText(Payment.this, "QR code will be downloaded", Toast.LENGTH_LONG).show();

            }
        });

        qrcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                download();
                Toast.makeText(Payment.this, "QR code will be downloaded", Toast.LENGTH_LONG).show();

            }
        });

        paymentoption.setOnCheckedChangeListener((paymentoption,i)->{
            if (i == R.id.onlineradio) {
                qrcode.setVisibility(View.GONE);
                qrcodetext.setVisibility(View.GONE);
                disclaimer.setVisibility(View.GONE);
                selectedpaymentdisplay.setText("Online UPI Payment");
                send.setText("Pay using Online UPI");
                finalpaymentoption = "Online Payment";
            }
            else if(i == R.id.scanradio)
            {
                qrcode.setVisibility(View.VISIBLE);
                qrcodetext.setVisibility(View.VISIBLE);
                disclaimer.setVisibility(View.VISIBLE);
                selectedpaymentdisplay.setText("Scan & Pay");
                send.setText("Confirm and Place Order");
                finalpaymentoption = "Scan & Pay";
            }
        });
    }


    private void download() {
        storageReference = firebaseStorage.getInstance().getReference();
        reference = storageReference.child("qrcode_gpay.jpeg");

        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                String url = uri.toString();
                downloadFiles(Payment.this, "qrcode_gpay", ".jpeg", DIRECTORY_DOWNLOADS, url);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    private void downloadFiles(Context context, String fileName, String fileExtension, String destinationDirectory, String url) {

        DownloadManager downloadManager = (DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new DownloadManager.Request(uri);

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalFilesDir(context, destinationDirectory, fileName + fileExtension);

        downloadManager.enqueue(request);
    }


    private void setOrderId() {
        mesref = FirebaseDatabase.getInstance().getReference("messages").child(tif.getOwnerid());
        mesref = mesref.push();
        order_id = mesref.getKey();
    }


    private Task<String> getToken() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderid", order_id);
        data.put("amount", amount);

        return mFunctions
                .getHttpsCallable("getTxnToken")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        HttpsCallableResult taskResult = task.getResult();
                        Map<String, Object> result = (Map<String, Object>) task.getResult().getData();
                        System.out.println("Return value" + result.get("token"));
                        return (String)result.get("token");
                    }
                });
    }

    private  void initializePayment() {
        getToken().addOnCompleteListener(task -> {
            if(!task.isSuccessful()){
                Toast.makeText(getApplicationContext(),"Something went wrong, Please try payment again Token error",Toast.LENGTH_LONG).show();
                setOrderId();
                return;
            }
            txnToken = task.getResult();
            System.out.println("Token returned: " + txnToken);
            Toast.makeText(this,"Token " + txnToken,Toast.LENGTH_SHORT).show();
            startPaytmPayment();
        });
    }

    private void startPaytmPayment(){
        String callBackUrl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID="+order_id;
        PaytmOrder paytmOrder = new PaytmOrder(order_id, midString, txnToken, amount, callBackUrl);
        TransactionManager transactionManager = new TransactionManager(paytmOrder, new PaytmPaymentTransactionCallback(){
            @Override
            public void onTransactionResponse(Bundle bundle) {
                String status = bundle.getString("STATUS");
                if( status!=null && status.equals("TXN_SUCCESS")) {
                    Toast.makeText(Payment.this,"Transaction successfull",Toast.LENGTH_LONG).show();
                    recordOrder();
                }
                else{
                    Toast.makeText(Payment.this,"Transaction failed. Please try payment again",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void networkNotAvailable() {
                Toast.makeText(getApplicationContext(),"Network not available. Enable your network and try again",Toast.LENGTH_LONG).show();
                setOrderId();
            }
            @Override
            public void onErrorProceed(String s) {
                Toast.makeText(getApplicationContext(),"Error in proceeding. Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }
            @Override
            public void clientAuthenticationFailed(String s) {
                Toast.makeText(getApplicationContext(),"Client Authentication Failed.  Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }
            @Override
            public void someUIErrorOccurred(String s) {
                Toast.makeText(getApplicationContext(),"",Toast.LENGTH_LONG).show();
                setOrderId();
            }
            @Override
            public void onErrorLoadingWebPage(int i, String s, String s1) {
                Toast.makeText(getApplicationContext(),"Something went wrong, Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }

            @Override
            public void onBackPressedCancelTransaction() {
                Toast.makeText(getApplicationContext(),"Transaction cancelled. Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }

            @Override
            public void onTransactionCancel(String s, Bundle bundle) {
                Toast.makeText(getApplicationContext(),"Transaction cancelled. Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }

        });

        //transactionManager.setShowPaymentUrl(host + "theia/api/v1/showPaymentPage");
        transactionManager.startTransaction(this, PaymentActivityRequestCode);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PaymentActivityRequestCode && resultCode == Activity.RESULT_OK && data != null) {
            /*
             data response - {"BANKNAME":"WALLET","BANKTXNID":"1395841115",
             "CHECKSUMHASH":"7jRCFIk6eRmrep+IhnmQrlrL43KSCSXrmMP5pH0hekXaaxjt3MEgd1N9mLtWyu4VwpWexHOILCTAhybOo5EVDmAEV33rg2VAS/p0PXdk\u003d",
             "CURRENCY":"INR","GATEWAYNAME":"WALLET","MID":"EAcR4116","ORDERID":"100620202152",
             "PAYMENTMODE":"PPI","RESPCODE":"01","RESPMSG":"Txn Success","STATUS":"TXN_SUCCESS",
             "TXNAMOUNT":"2.00","TXNDATE":"2020-06-10 16:57:45.0","TXNID":"202006101112128001101683631290118"}
              */
            String response =data.getStringExtra("response");
            if(response!=null && response.contains("TXN_SUCCESS")){
                Toast.makeText(Payment.this,"Payment Successfull",Toast.LENGTH_LONG).show();
                recordOrder();
            }else{
                Toast.makeText(this,"Payment failed. Please try payment again",Toast.LENGTH_LONG).show();
                setOrderId();
            }
            Log.e("Response",data.getStringExtra("response"));
        }else{
            Toast.makeText(this,"Payment failed. Please try payment again",Toast.LENGTH_LONG).show();
            setOrderId();
        }
    }


    public void recordOrder() {
        if(tif == null)
            tif = Uploadtiffin.currtif;

        Current_Location curr_loc = (Current_Location) getApplication();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

        StringBuilder message = new StringBuilder("New Order Placed for your " + tif.getName() + "\nOrder Details:\n" + "Order ID : " + order_id + "\n\n");
        message.append("Name : ").append(Splash.curruser.getName()).append("\n");
        for (int i = 0; i < Selection.selectdata.size(); i++) {
            Dataforselection selected_items = Selection.selectdata.get(i);
            message.append("\n").append(selected_items.getItem()).append(" : ").append(selected_items.getCounter());
        }

        message.append("\n\n" + "Total amount : ").append(amount);
        message.append("\nPayment : ").append(finalpaymentoption);
        message.append("\nContact no : ").append(Splash.curruser.getMobileno());
        message.append("\nMode : ").append(Selection.selectedmode);
        message.append("\nDelivery time: ").append(Selection.delitimeslot.getSelectedItem().toString());

        if(Selection.selectedmode.equals("Delivery"))
            message.append("\n\nAddress : ").append(curr_loc.getCurr_address());

        String orderpassword = String.valueOf((int) (Math.random() * 9000) + 1000);

        Messageinfo messageinfo = new Messageinfo(firebaseAuth.getUid(), message.toString(), orderpassword, "none", Splash.curruser.getMobileno());
        mesref.setValue(messageinfo);

        mesref.child("payment").setValue(finalpaymentoption);

        String currenttime = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy ", Locale.getDefault()).format(new Date());
        mesref.child("ordertime").setValue(currenttime);
        mesref.child("amount").setValue(amount);

        if(Selection.selectedmode.equals("Delivery")) {
            mesref.child("latitude").setValue(curr_loc.getCurr_lat());
            mesref.child("longitude").setValue(curr_loc.getCurr_long());
        }

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(tif.getOwnerid());
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ownerinfo = dataSnapshot.getValue(Userinfo.class);

                JSONObject notification = new JSONObject();
                JSONObject notificationBody = new JSONObject();
                try
                {
                    notificationBody.put("title", "New Order is placed");
                    notificationBody.put("message", "Accept Order Now");

                    notification.put("to", ownerinfo.getToken());
                    notification.put("data", notificationBody);
                }
                catch (JSONException e) {
                    System.out.println(e.toString());
                }
                sendNotification(notification);


                DatabaseReference managementref = FirebaseDatabase.getInstance().getReference("management").child("token");
                managementref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        JSONObject notification = new JSONObject();
                        JSONObject notificationBody = new JSONObject();
                        try
                        {
                            notificationBody.put("title", "Order placed by " + Splash.curruser.getName() + " for " + tif.getName());
                            String message = "Order ID : " + order_id;
                            message += "\nVendor ID :" + tif.getOwnerid();
                            message += "\nVendor No :" + ownerinfo.getMobileno();
                            message += "\nCustomer No : " + Splash.curruser.getMobileno();
                            message += "\nAmount :" + amount;
                            message += "\n" + finalpaymentoption;
                            notificationBody.put("message", message);

                            notification.put("to", snapshot.getValue(String.class));
                            notification.put("data", notificationBody);
                        }
                        catch (JSONException e) {
                            System.out.println(e.toString());
                        }
                        sendNotification(notification);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

        ref = FirebaseDatabase.getInstance().getReference("orders").child(firebaseAuth.getUid()).child(order_id);

        String vendorAddress = tif.getAddress();

        if(Selection.selectedmode.equals("Takeaway")) {
            tif.setDelivery(0);
            tif.setTakeaway(true);
        }
        else if(Selection.selectedmode.equals("Delivery")) {
            tif.setAddress(curr_loc.getCurr_address());
            tif.setTakeaway(false);
        }

        ref.setValue(tif);

        if(Selection.selectedmode.equals("Takeaway")) {
            ref.child("customeraddress").setValue(curr_loc.getCurr_address());
        }
        else if(Selection.selectedmode.equals("Delivery")) {
            ref.child("vendoraddress").setValue(vendorAddress);
        }

        double tempamount = Math.round(Float.parseFloat(amount) * 100.0) / 100.0;
        ref.child("amount").setValue((float)tempamount);
        ref.child("orderrating").setValue(-1);
        ref.child("password").setValue(orderpassword);
        ref.child("orderstatus").setValue("none");
        ref.child("payment").setValue(finalpaymentoption);
        ref.child("ordertime").setValue(currenttime);
        ref = ref.child("menu");
        for (int i = 0; i < Selection.selectdata.size(); i++) {
            Dataforselection selected_items = Selection.selectdata.get(i);
            ref.child(selected_items.getItem().replace("/","_")).setValue(selected_items.getPrice() + " : " + selected_items.getCounter());
        }

        Intent i = new Intent(Payment.this, Donepayment.class);
        i.putExtra("amount",amount);
        i.putExtra("orderid",order_id);
        i.putExtra("vendorname",tif.getName());
        startActivity(i);
        finish();

    }


    private void sendNotification(JSONObject notification) {

        DatabaseReference notifyref = FirebaseDatabase.getInstance().getReference("notify").child("key");
        if(notifykey == null) {
            notifyref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notifykey = snapshot.getValue(String.class);

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                            response -> System.out.println(response.toString()),
                            error -> Toast.makeText(Payment.this, "Request error", Toast.LENGTH_LONG).show()){
                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> params = new HashMap<>();
                            params.put("Authorization", "key="+notifykey);
                            params.put("Content-Type", "application/json");
                            return params;
                        }
                    };
                    MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
        else
        {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("https://fcm.googleapis.com/fcm/send", notification,
                    response -> System.out.println(response.toString()),
                    error -> Toast.makeText(Payment.this, "Request error", Toast.LENGTH_LONG).show()){
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "key="+notifykey);
                    params.put("Content-Type", "application/json");
                    return params;
                }
            };
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
        }
    }
}