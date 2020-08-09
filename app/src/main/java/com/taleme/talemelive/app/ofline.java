package com.taleme.talemelive.app;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class ofline extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofline);
        if (!isNetworkConnected()) {

            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(ofline.this).create();
            alertDialog.setTitle("تعليمي لايف");
            alertDialog.setIcon(R.drawable.logo);
            alertDialog.setMessage("تحقق من اتصال الانترنت  ");
            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS));
                            finish();
                        }
                    });
            alertDialog.show();
            return;
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
