package com.example.bookmytiffin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

public class Feedback extends AppCompatActivity {

    Button submitfeedback;
    ImageButton back;
    TextInputEditText  yourfeedback;
    TextView ouremail;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);

        //back = findViewById(R.id.backarrow);

        submitfeedback = findViewById(R.id.submitfeedback);
        ouremail = findViewById(R.id.feedbackmail1);
        //yourfeedback = findViewById(R.id.feedbacktext1);



        submitfeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_VIEW , Uri.parse("mailto:"+ouremail.getText().toString()));
                //intent.putExtra(Intent.EXTRA_TEXT,yourfeedback.getText().toString());
                startActivity(intent);

            }
        });




    }
}