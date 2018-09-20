package com.sendhan.mybusinessassistant;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alimuzaffar.lib.pin.PinEntryEditText;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Database.MBADatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;
import Utils.ImageConvertor;

/**
 * Created by USER on 21-06-2018.
 */

public class SecurityProfile_Activity extends AppCompatActivity {
    AppCompatEditText txtName,txtEmail,txtMobile,txtCompany,txtAddress,txtbillheader;
    PinEntryEditText txtPassword,txtConfirmPassword;
    AppCompatImageView img_profile;
    Button btn_Save;
    SQLiteDatabase dataBase;
    File myBitmap;
    MBADatabase mHelper;
    SecurityProfile securityProfile;
    private static final int REQUEST_PICK_FILE=200;
    LinearLayout layout_change_password;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_profile);
        txtName=(AppCompatEditText) findViewById(R.id.txt_Name);
        txtEmail=(AppCompatEditText) findViewById(R.id.txt_Email);
        img_profile=(AppCompatImageView) findViewById(R.id.img_profile);
        txtCompany=findViewById(R.id.txt_CompanyName);
        txtAddress=findViewById(R.id.txt_Address);
        txtbillheader=findViewById(R.id.txt_bill_header);
        GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(acc!=null)
        {
            txtEmail.setText(acc.getEmail());
            txtName.setText(acc.getDisplayName());
        }
        txtMobile=(AppCompatEditText) findViewById(R.id.txt_Mobile);
        txtPassword=(PinEntryEditText) findViewById(R.id.txt_password);
        txtConfirmPassword=(PinEntryEditText) findViewById(R.id.txt_confirm_password);
        layout_change_password=(LinearLayout)findViewById(R.id.layout_change_password);
        layout_change_password.setVisibility(View.GONE);
        btn_Save=(Button) findViewById(R.id.btn_save);
        mHelper = new MBADatabase(this);
        btn_Save.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().equals("")) {
                    txtName.requestFocus();
                    txtName.setError("Name cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtEmail.getText().toString().equals("")){
                    txtEmail.requestFocus();
                    txtEmail.setError("Email cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtPassword.getText().toString().equals("")){
                    txtPassword.requestFocus();
                    txtPassword.setError("Pin cannot be empty");
                    btn_Save.setEnabled(true);
                }else if(txtPassword.getText().toString().length()<=3){
                    txtPassword.requestFocus();
                    txtPassword.setError("Pin length must be 4 numbers");
                    btn_Save.setEnabled(true);
                }else if(!(txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString()))){
                    txtConfirmPassword.requestFocus();
                    txtConfirmPassword.setError("Pin does not match");
                    btn_Save.setEnabled(true);
                }else{
                    securityProfile=new SecurityProfile();
                    securityProfile.setName(txtName.getText().toString().trim());
                    securityProfile.setEmail(txtEmail.getText().toString().trim());
                    securityProfile.setMobile(txtMobile.getText().toString().trim());
                    securityProfile.setPassword(txtPassword.getText().toString().trim());
                    securityProfile.setCompanyName(txtCompany.getText().toString());
                    securityProfile.setAddress(txtAddress.getText().toString());
                    securityProfile.setBillHeader(txtbillheader.getText().toString());
                    new SaveProfileSetup().execute();
                }
            }
        });
        txtPassword.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                txtConfirmPassword.requestFocus();
            }
        });
        txtConfirmPassword.setOnPinEnteredListener(new PinEntryEditText.OnPinEnteredListener() {
            @Override
            public void onPinEntered(CharSequence pwd) {
                btn_Save.requestFocus();
            }
        });
        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                //sets the select file to all types of files
                intent.setType("image/*");
                //allows to select data and return it
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //starts new activity to select file and return data
                startActivityForResult(Intent.createChooser(intent,
                        "Choose File to Restore.."),REQUEST_PICK_FILE);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PICK_FILE :
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String filepath="";
                    if ("content".equalsIgnoreCase(uri.getScheme())) {
                        String[] projection = {"_data"};
                        Cursor cursor = null;
                        try{
                            cursor = getApplicationContext().getContentResolver().query(uri, projection, null, null, null);
                            int column_index = cursor.getColumnIndexOrThrow("_data");
                            if (cursor.moveToFirst()) {
                                filepath= cursor.getString(column_index);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else if ("file".equalsIgnoreCase(uri.getScheme())) {
                        filepath= uri.getPath();
                    }
                    if(!filepath.equals("")) {
                        ScalandSetProfile(filepath);
                    }
                }else{

                }
                break;
        }
    }

    private void ScalandSetProfile(String filepath) {
        File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/");
        if (!myDir.exists()) {
            myDir.mkdir();
        }
        if (myDir.canWrite()) {
            try {
                Bitmap b= BitmapFactory.decodeFile(filepath);
                Bitmap out= ImageConvertor.getRoundedCornerBitmap(b,100);
                File file = new File(myDir, "1.png");
                FileOutputStream fOut;
                fOut = new FileOutputStream(file);
                out.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                out.recycle();
                fOut.flush();
                fOut.close();
                b.recycle();
                out.recycle();
                img_profile.setImageURI(Uri.fromFile(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }


    private class SaveProfileSetup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            SaveProfileSetupData();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
        }
    }
    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent=new Intent(SecurityProfile_Activity.this,Password_Activity.class);
                startActivity(intent);
                finish();
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void clearOperation() {
        txtName.setText("");
        txtEmail.setText("");
        txtMobile.setText("");
        txtPassword.setText("");
        txtConfirmPassword.setText("");
        txtAddress.setText("");
        txtCompany.setText("");
        txtbillheader.setText("");
        showAlertWithCancels("Profile has been created successfully.");

    }
    private void SaveProfileSetupData() {
        dataBase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(SecurityTableData.Name, securityProfile.getName());
        values.put(SecurityTableData.Email, securityProfile.getEmail());
        values.put(SecurityTableData.Mobile, securityProfile.getMobile());
        values.put(SecurityTableData.Password, securityProfile.getPassword());
        values.put(SecurityTableData.CompanyName, securityProfile.getCompanyName());
        values.put(SecurityTableData.Address, securityProfile.getAddress());
        values.put(SecurityTableData.BillHeader,securityProfile.getBillHeader());
        dataBase.insert(SecurityTableData.SecurityTableName, null, values);
        dataBase.close();

    }
}

