package com.sendhan.mybusinessassistant;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import Database.MBADatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;
import Utils.ImageConvertor;

/**
 * Created by USER on 30-07-2018.
 */

public class Profile extends Fragment {LinearLayout passwordLayout,layout_change_password;
    AppCompatEditText txtName,txtEmail,txtMobile,txtCompany,txtAddress,txtBillHeader;
    Button btn_Save;
    SQLiteDatabase dataBase;
    MBADatabase mHelper;
    SecurityProfile securityProfile;
    AppCompatImageView img_profile;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.security_profile, container, false);
        passwordLayout=(LinearLayout)view.findViewById(R.id.layout_password);
        layout_change_password=(LinearLayout)view.findViewById(R.id.layout_change_password);
        passwordLayout.setVisibility(View.GONE);
        layout_change_password.setVisibility(View.GONE);
        txtName=(AppCompatEditText)view.findViewById(R.id.txt_Name);
        txtEmail=(AppCompatEditText)view.findViewById(R.id.txt_Email);
        txtMobile=(AppCompatEditText)view.findViewById(R.id.txt_Mobile);
        txtCompany=(AppCompatEditText)view.findViewById(R.id.txt_CompanyName);
        txtAddress=(AppCompatEditText)view.findViewById(R.id.txt_Address);
        txtBillHeader=view.findViewById(R.id.txt_bill_header);
        btn_Save=(Button) view.findViewById(R.id.btn_save);
        img_profile=(AppCompatImageView)view.findViewById(R.id.img_profile);
        mHelper = new MBADatabase(getActivity());


        Initialize();
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtName.getText().toString().equals("")) {
                    txtName.requestFocus();
                    txtName.setError("Name cannot be empty");
                    btn_Save.setEnabled(true);
                } else if (txtEmail.getText().toString().equals("")) {
                    txtEmail.requestFocus();
                    txtEmail.setError("Email cannot be empty");
                    btn_Save.setEnabled(true);
                }else{
                    securityProfile.setName(txtName.getText().toString().trim());
                    securityProfile.setEmail(txtEmail.getText().toString().trim());
                    securityProfile.setMobile(txtMobile.getText().toString().trim());
                    securityProfile.setCompanyName(txtCompany.getText().toString().trim());
                    securityProfile.setAddress(txtAddress.getText().toString().trim());
                    securityProfile.setBillHeader(txtBillHeader.getText().toString().trim());
                    new UpdateProfileSetup().execute();
                }
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
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Choose File to Restore.."),200);
            }
        });
        return view;
    }
    public void ScalandSetProfile(String filepath) {
        File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Business Assistant/");
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

    private void Initialize() {
        securityProfile=new SecurityProfile();
        securityProfile=mHelper.getProfile();
        txtName.setText(securityProfile.getName());
        txtEmail.setText(securityProfile.getEmail());
        txtMobile.setText(securityProfile.getMobile());
        txtAddress.setText(securityProfile.getAddress());
        txtCompany.setText(securityProfile.getCompanyName());
        txtBillHeader.setText(securityProfile.getBillHeader());
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Business Assistant/1.png");
        if(file.exists())
            img_profile.setImageURI(Uri.fromFile(file));
        else
            img_profile.setImageResource(R.drawable.profile);
    }
    public void showAlertWithCancels(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.AppCompatAlertDialogStyle);
        builder.setTitle("My Accounts");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private class UpdateProfileSetup extends AsyncTask<String, String, String> {
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
            updateView();
        }
    }
    private void updateView() {

        showAlertWithCancels("Profile has been updated successfully.");

    }
    private void SaveProfileSetupData() {
        dataBase = mHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SecurityTableData.Name, securityProfile.getName());
        values.put(SecurityTableData.Email, securityProfile.getEmail());
        values.put(SecurityTableData.Mobile, securityProfile.getMobile());
        values.put(SecurityTableData.CompanyName,securityProfile.getCompanyName());
        values.put(SecurityTableData.Address,securityProfile.getAddress());
        values.put(SecurityTableData.BillHeader,securityProfile.getBillHeader());
        dataBase.beginTransaction();
        dataBase.update(SecurityTableData.SecurityTableName,values,SecurityTableData.ProfileId+"=?",new String[] {String.valueOf(securityProfile.getProfileId())});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
