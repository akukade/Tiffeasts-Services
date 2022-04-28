package com.example.bookmytiffin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class About extends AppCompatActivity {

    TextView help, infoaboutus, terms, contactus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        contactus = findViewById(R.id.contactus);

        help = findViewById(R.id.help);
        infoaboutus = findViewById(R.id.infoaboutus);
        terms = findViewById(R.id.terms);

        infoaboutus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setMessage(" We are team of engineers who are passionate about the use of technology for the right cause. We strongly believe that if technology used wisely and positively, it can do wonders. Use of technology with the right kind of knowledge is all about democratising it, so that it reaches people. This application is our small effort to bring a change in the food industry. The platform will give an opportunity to homemakers, caterers, tiffin services to reach a wider audience and set up their own goals and a business front. Along with that people living away from home, elderly, office going people will have hot and delicious homemade food at their door step.")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/
                gotoUrl("https://www.tiffeasts.com/");

            }
        });


        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i  = new Intent(About.this, Feedback.class);
                startActivity(i);

            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setMessage("Help")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();/*
                 */

                Intent i  = new Intent(About.this, Faq.class);
                startActivity(i);
            }
        });

        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* AlertDialog.Builder builder = new AlertDialog.Builder(About.this);
                builder.setMessage("Terms of Service")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        }).setNegativeButton("Cancel", null);

                AlertDialog alertDialog = builder.create();
                alertDialog.show();*/
                gotoUrl("https://www.tiffeasts.com/terms-of-services");

            }
        });
    }

    private void gotoUrl(String s) {
        Uri uri = Uri.parse(s);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }
}