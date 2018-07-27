package com.sendhan.mybusinessassistant;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.drive.DriveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import Database.MBADatabase;
import Utils.RestoreFile;

/**
 * Created by USER on 27-07-2018.
 */

public class ManageBackup extends Fragment {
    MBADatabase mHelper;
    Button btn_upload, btn_restore, btn_off_backup, btn_off_restore;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    ProgressDialog progressDialog;
    private static final String username = "myaccappv1@gmail.com";
    private static final String password = "Admin@9500";

    DriveFile file;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.dbcloud, container, false);
        btn_upload = (AppCompatButton) view.findViewById(R.id.btn_backup);
        btn_restore = (AppCompatButton) view.findViewById(R.id.btn_restore);
        btn_off_backup = (AppCompatButton) view.findViewById(R.id.btn_off_backup);
        btn_off_restore = (AppCompatButton) view.findViewById(R.id.btn_off_restore);
        mHelper = new MBADatabase(getActivity());
        progressDialog = new ProgressDialog(getActivity());

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UploadData().execute();
            }
        });
        btn_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFileFromAppFolder();
            }
        });

        btn_off_backup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateOfflineBackup();
            }
        });
        btn_off_restore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                //sets the select file to all types of files
                intent.setType("file/*");
                //allows to select data and return it
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //starts new activity to select file and return data
                getActivity().startActivityForResult(Intent.createChooser(intent,
                        "Choose File to Restore.."),((MainActivity)getActivity()).REQUEST_PICK_FILE);
            }
        });

        return view;
    }

    private class UploadData extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            createFileInAppFolder();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void getFileFromAppFolder() {
        new RestoreFile(getActivity()).appFolder();
    }

    public void createFileInAppFolder() {
        ((MainActivity) getActivity()).uploadFile.UploadFile();
    }

    protected void showMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
    }

    public void CreateOfflineBackup() {
        final File currentDB = new File("/data/data/com.sendhan.mybusinessassistant/databases/MyBusinessAssistant.db");
        if (currentDB.exists()) {
            File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Business Assistant/");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Business Assistant/Database");
            if (!myDir.exists()) {
                myDir.mkdir();
            }
            if (myDir.canWrite()) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
                String formattedDate = df.format(c.getTime());
                File backupDB = new File(myDir, "MyBusinessAssistant"+formattedDate+".db");

                FileChannel src = null;
                FileChannel dst = null;
                try {
                    src = new FileInputStream(currentDB).getChannel();
                    dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                showMessage("Backup Successful at " + backupDB.getAbsolutePath());
            }
        }
    }
    public void restoreOffline(File file)
    {

        final File myDir = new File("/data/data/com.sendhan.mybusinessassistant/databases");
        if(myDir.exists()) {
            File backupDB = new File(myDir, "MyBusinessAssistant.db");

            FileChannel src = null;
            FileChannel dst = null;
            try {
                src = new FileInputStream(file).getChannel();
                dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            showMessage("Restore Successful");
        }
    }

}
