package com.example.callmonitor;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SensitiveActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensitive);

        // Get the TextView by its ID
        TextView linkTextView1 = findViewById(R.id.link1);

        // Set an OnClickListener to make the text clickable
        linkTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the URL in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://cybercrime.gov.in"));
                startActivity(browserIntent);
            }
        });

        TextView linkTextView2 = findViewById(R.id.link2);

        // Set an OnClickListener to make the text clickable
        linkTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to open the URL in a browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ksp.karnataka.gov.in/page/FAQ/Cybercrime/en"));
                startActivity(browserIntent);
            }
        });
    }
}
