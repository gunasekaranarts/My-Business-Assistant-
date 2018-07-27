package com.sendhan.mybusinessassistant;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alimuzaffar.lib.pin.PinEntryEditText;

import Database.MBADatabase;
import POJO.SecurityProfile;
import Utils.GMailSender;
import Utils.NetworkUtil;

/**
 * Created by USER on 21-06-2018.
 */

public class Password_Activity extends AppCompatActivity {
    MBADatabase mHelper;
    SecurityProfile securityProfile;
    TextView lnkforgorpwd;
    public String teamNo;
    private static final String username = "myaccappv1@gmail.com";
    private static final String password = "Admin@9500";
    ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.HONEYCOMB) {
        //To prevent from taking screenshot or screen recording
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }
    setContentView(R.layout.passsword);
    lnkforgorpwd=(TextView) findViewById(R.id.lnk_forgot_password);
    mHelper=new MBADatabase(this);
    securityProfile=mHelper.getProfile();
    progressDialog = new ProgressDialog(this);
    final PinEntryEditText pinEntry = (PinEntryEditText) findViewById(R.id.txt_pin_entry);
        if (pinEntry != null) {
        pinEntry.requestFocus();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
        pinEntry.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                String pin = pwd.toString();
                if(securityProfile.getPassword().equals(pin)){
                    imm.hideSoftInputFromWindow(pinEntry.getApplicationWindowToken(),0);
                    Intent send = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(send);
                    finish();
                } else {
                    Toast.makeText(Password_Activity.this, "Invalid pin", Toast.LENGTH_SHORT).show();
                    pinEntry.setText(null);
                }
            }
        });
    }
         lnkforgorpwd.setOnClickListener(new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            if(NetworkUtil.isNetworkAvailable(getApplicationContext()))
                new sendmail().execute();
            else
                Toast.makeText(getApplicationContext(),"No internet connection found!",Toast.LENGTH_LONG).show();
        }
    });

}
private class sendmail extends AsyncTask<String, String, String> {
    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        progressDialog.setMessage("Please wait...");
        progressDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        SendPinMail();
        return "sucess full added";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        progressDialog.dismiss();
//        showAlertWithCancels("Your new pin has been sent to your email id : "
//                +securityProfile.getEmail());
    }
}

    private void SendPinMail() {
        try {
            GMailSender sender = new GMailSender(username, password);
//            sender.sendMail("My Accounts App - Forgot pin",
//                    "Dear, " +securityProfile.getName()+", \n \n " +
//                            "Your pin for the app is : "+securityProfile.getPassword()+" \n\n\n Thanks \n With regards \n - Admin",
//                    username,
//                    securityProfile.getEmail());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Accounts - Password Reset");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
