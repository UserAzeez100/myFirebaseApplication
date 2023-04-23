package com.example.myfirebaseapplication.other;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.developer.kalert.KAlertDialog;
import com.example.myfirebaseapplication.MainActivity;
import com.example.myfirebaseapplication.R;
import com.google.firebase.auth.FirebaseAuth;

public class NetworkCheck extends BroadcastReceiver {

    ConnectivityManager connectivityManager;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (!isNetworkAvailable(context)) {
            showNoInternetDialog(context);
        }
    }

    private boolean isNetworkAvailable(Context context) {
         connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }

    void showNoInternetDialog (Context context){

        new KAlertDialog(context, KAlertDialog.WARNING_TYPE)
                .setTitleText("No Internet Connection")
                .setContentText("Please check your internet connection and try again")
                .setConfirmClickListener("OK", new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        if(connectivityManager != null){
                            kAlertDialog.dismissWithAnimation();
                        }
                    }
                }).show();


    }

}
