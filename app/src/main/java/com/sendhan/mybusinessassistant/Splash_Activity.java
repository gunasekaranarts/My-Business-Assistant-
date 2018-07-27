package com.sendhan.mybusinessassistant;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.drive.MetadataBuffer;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import Database.MBADatabase;
import POJO.SecurityProfile;
import TableData.SecurityTableData;
import Utils.AppPreferences;
import Utils.NetworkUtil;

public class Splash_Activity extends AppCompatActivity {
    private static int INITIAL_TIME_OUT = 3000;
    private static int SPLASH_TIME_OUT = 500;
    SecurityProfile securityProfile;
    MBADatabase mHelper;
    SQLiteDatabase dataBase;
    ProgressDialog progressDialog;
    ArrayList<SecurityProfile> securityProfiles = new ArrayList<>();
    public static GoogleSignInClient mGoogleSignInClient;
    public static DriveResourceClient mDriveResourceClient;
    private static DriveClient mDriveClient;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    private final int REQUEST_PERMISSION_1 = 21;
    private final int REQUEST_PERMISSION_2 = 22;
    private final int REQUEST_PERMISSION_3 = 23;
    private final int REQUEST_PERMISSION_4 = 24;
    private final int REQUEST_PERMISSION_5 = 25;
    private final int REQUEST_PERMISSION_6 = 26;
    private final int REQUEST_PERMISSION_7 = 27;
    private final int REQUEST_PERMISSION_8 = 28;
    private final int REQUEST_PERMISSION_9 = 29;
    private final int REQUEST_PERMISSION_10 = 30;
    boolean Is_backup_exist=false;

    @Override
    protected void onStart() {
        super.onStart();
        signIn();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spalsh);
        getSupportActionBar().hide();
        progressDialog = new ProgressDialog(this);
        PermissionCheckUP();
        mHelper = new MBADatabase(Splash_Activity.this);
        SecurityProfileView();

    }
    public void PermissionCheckUP() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_1);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.CAMERA},
                    REQUEST_PERMISSION_3);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION_4);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                    REQUEST_PERMISSION_5);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION_7);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                    REQUEST_PERMISSION_9);
        }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                    REQUEST_PERMISSION_10);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != REQUEST_PERMISSION_1 && requestCode != REQUEST_PERMISSION_2 && requestCode != REQUEST_PERMISSION_3 && requestCode != REQUEST_PERMISSION_4 && requestCode != REQUEST_PERMISSION_5 && requestCode != REQUEST_PERMISSION_6
                && requestCode != REQUEST_PERMISSION_7 && requestCode != REQUEST_PERMISSION_8 && requestCode != REQUEST_PERMISSION_9 && requestCode != REQUEST_PERMISSION_10) {
        } else {
            switch (requestCode) {
                case REQUEST_PERMISSION_1:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, android.Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_PHONE_STATE},
                                REQUEST_PERMISSION_2);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_2:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.CAMERA},
                                REQUEST_PERMISSION_3);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_3:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                                REQUEST_PERMISSION_4);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_4:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.GET_ACCOUNTS},
                                REQUEST_PERMISSION_5);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_5:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                REQUEST_PERMISSION_6);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_6:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                                REQUEST_PERMISSION_7);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_7:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                REQUEST_PERMISSION_8);
                        return;
                    }
                    break;
                case REQUEST_PERMISSION_8:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.CALL_PHONE},
                                REQUEST_PERMISSION_9);
                        return;
                    }
                    break;

                case REQUEST_PERMISSION_9:
                    break;
                case REQUEST_PERMISSION_10:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                            && ContextCompat.checkSelfPermission(Splash_Activity.this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(Splash_Activity.this, new String[]{android.Manifest.permission.READ_CONTACTS},
                                REQUEST_PERMISSION_9);
                        return;
                    }
                    break;
            }

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:

                if (resultCode != RESULT_OK) {
                    // Sign-in may fail or be cancelled by the user. For this sample, sign-in is
                    // required and is fatal. For apps where sign-in is optional, handle
                    // appropriately

                    gotoSetupProfile();
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    if (securityProfile == null) {
                        initializeDriveClient(getAccountTask.getResult());
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(Splash_Activity.this);
                                if(NetworkUtil.isNetworkAvailable(Splash_Activity.this))
                                    new Checkingbackup().execute();
                                else
                                    gotoSetupProfile();
                            }
                        },INITIAL_TIME_OUT);
                    }else{
                        Intent send = new Intent(getApplicationContext(), Password_Activity.class);
                        startActivity(send);
                        finish();
                    }

                } else {
                    gotoSetupProfile();
                }
                break;

        }
    }
    private void SecurityProfileView() {
        dataBase=mHelper.getWritableDatabase();
        Cursor mCursor = dataBase.rawQuery("SELECT * FROM " + SecurityTableData.SecurityTableName, null);
        if (mCursor!=null && mCursor.moveToFirst()){
            do{
                securityProfile=new SecurityProfile();
                securityProfile.setName(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Name)));
                securityProfile.setPassword(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Password)));
                securityProfile.setEmail(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Email)));
                securityProfile.setMobile(mCursor.getString(mCursor.getColumnIndex(SecurityTableData.Mobile)));
            }while (mCursor.moveToNext());
        }
        mCursor.close();
    }
    //    private void ViewProfile() {
//        if (securityProfile == null) {
//            GoogleSignInAccount acc= GoogleSignIn.getLastSignedInAccount(this);
//            if(NetworkUtil.isNetworkAvailable(this))
//                new Checkingbackup().execute();
//            else
//                gotoSetupProfile();
//        } else {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent send = new Intent(getApplicationContext(), Password_Activity.class);
//                    startActivity(send);
//                    finish();
//                }
//            }, SPLASH_TIME_OUT);
//        }
//    }
    public void signIn() {
        mGoogleSignInClient = buildGoogleSignInClient();
        this.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }
    public GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }
    public void showBackupDetails(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Business Assistant");
        builder.setMessage(BuilderText);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                RestoreDB();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                gotoSetupProfile();
                finish();
            }
        });
        builder.show();
    }
    private void RestoreDB() {
        getFileFromAppFolder();
    }
    public void getFileFromAppFolder() {
        final Task<DriveFolder> appFolderTask =mDriveResourceClient.getAppFolder();
        appFolderTask.addOnSuccessListener(this,new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                getFile(driveFolder);
            }
        });
    }
    private void getFile(DriveFolder parent) {
        Query query = new Query.Builder()
                .addFilter(Filters.contains(SearchableField.TITLE, mHelper.getDatabaseName()))
                .build();
        Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(parent, query);
        queryTask
                .addOnSuccessListener(this,
                        new OnSuccessListener<MetadataBuffer>() {
                            @Override
                            public void onSuccess(MetadataBuffer metadataBuffer) {
                                AppPreferences.getInstance(getApplicationContext())
                                        .setDriveId(metadataBuffer.get(0).getDriveId().encodeToString());
                                DriveFile file  = metadataBuffer.get(0).getDriveId().asDriveFile();
                                downloadFile(file);
                            }
                        })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Unable to download file",
                                Toast.LENGTH_LONG).show();
                        gotoSetupProfile();
                    }
                });
    }
    private void downloadFile(DriveFile file) {
        final Task<DriveContents> openFileTask =
                mDriveResourceClient.openFile(file, DriveFile.MODE_READ_ONLY);
        openFileTask.addOnCompleteListener(new OnCompleteListener<DriveContents>() {
            @Override
            public void onComplete(@NonNull Task<DriveContents> task) {
                openFileTask
                        .continueWithTask(new Continuation<DriveContents, Task<Void>>() {
                            @Override
                            public Task<Void> then(@NonNull Task<DriveContents> task) throws Exception {
                                DriveContents contents = task.getResult();
                                // Process contents...
                                // [START_EXCLUDE]
                                // [START read_as_string]
                                File NewDB = new File("/data/data/com.sendhan.mybusinessassistant/databases/MyBusinessAssistant.db");
                                OutputStream outStream = new FileOutputStream(NewDB);
                                try {
                                    InputStream is=contents.getInputStream();
                                    byte[] buffer = new byte[is.available()];
                                    is.read(buffer);
                                    outStream.write(buffer);
                                    outStream.close();
                                    outStream.flush();
                                    showMessage("Database Restored");
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent send = new Intent(getApplicationContext(), Password_Activity.class);
                                            startActivity(send);
                                            finish();
                                        }
                                    }, SPLASH_TIME_OUT);

                                }catch (Exception ex){
                                    showMessage("Unable to Restore");
                                    outStream.close();
                                    outStream.flush();
                                    gotoSetupProfile();
                                }
                                // [END read_as_string]
                                // [END_EXCLUDE]
                                // [START discard_contents]
                                Task<Void> discardTask = mDriveResourceClient.discardContents(contents);
                                // [END discard_contents]
                                return discardTask;
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Handle failure
                                // [START_EXCLUDE]

                                showMessage("Unable to restore database");
                                gotoSetupProfile();

                                // [END_EXCLUDE]
                            }
                        });
            }
        });
    }
    public void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        // AppPreferences.getInstance(getApplicationContext()).setDriveId(signInAccount.getEmail().toString());
        //onDriveClientReady();
    }
    protected void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    private class Checkingbackup extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
            checkBackup();
//                }
//            },INITIAL_TIME_OUT);
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(Is_backup_exist) {
                        showBackupDetails("Backup found. Do you want to restore?");
                    }else{
                        gotoSetupProfile();
                    }
                }
            }, INITIAL_TIME_OUT);
        }
    }
    public void gotoSetupProfile()
    {
        if(securityProfile==null) {
            Intent send = new Intent(getApplicationContext(), SecurityProfile_Activity.class);
            startActivity(send);
            finish();
        }else{
            Intent send = new Intent(getApplicationContext(), Password_Activity.class);
            startActivity(send);
            finish();
        }
    }
    public void checkBackup(){
        final Task<DriveFolder> appFolderTask =mDriveResourceClient.getAppFolder();
        appFolderTask.addOnSuccessListener(Splash_Activity.this,new OnSuccessListener<DriveFolder>() {
            @Override
            public void onSuccess(DriveFolder driveFolder) {
                Query query = new Query.Builder()
                        .addFilter(Filters.contains(SearchableField.TITLE,"MyBusinessAssistant.db"))
                        .build();

                Task<MetadataBuffer> queryTask = mDriveResourceClient.queryChildren(driveFolder, query);
                queryTask
                        .addOnSuccessListener(Splash_Activity.this,
                                new OnSuccessListener<MetadataBuffer>() {
                                    @Override
                                    public void onSuccess(MetadataBuffer metadataBuffer) {

                                        if(metadataBuffer.getCount()==0)
                                            Is_backup_exist=false;
                                        else
                                            Is_backup_exist=true;
                                    }
                                })
                        .addOnFailureListener(Splash_Activity.this, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Is_backup_exist=false;
                            }
                        });
            }
        }).addOnFailureListener(Splash_Activity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Is_backup_exist= false;
            }
        });
    }


}
