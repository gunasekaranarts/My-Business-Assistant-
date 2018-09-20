package com.sendhan.mybusinessassistant;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveClient;
import com.google.android.gms.drive.DriveResourceClient;
import com.google.android.gms.tasks.Task;
import com.itextpdf.text.xml.simpleparser.NewLineHandler;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import Database.MBADatabase;
import POJO.SecurityProfile;
import Utils.UploadFile;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public MBADatabase mHelper;
    TextView txtuserName,txtusermail;
    public Fragment fragment = null;
    boolean doubleBackToExitPressedOnce = false;
    SecurityProfile securityProfile;
    public GoogleSignInClient mGoogleSignInClient;
    public DriveResourceClient mDriveResourceClient;
    public DriveClient mDriveClient;
    private static final int REQUEST_CODE_SIGN_IN = 0;
    public static final int REQUEST_PICK_FILE = 2;
    private static final int REQUEST_PICK_PROFILE=200;
    ImageView nav_profile;
    public UploadFile uploadFile;

    @Override
    protected void onStart() {
        super.onStart();
        signIn();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mHelper=new MBADatabase(this);
        uploadFile=new UploadFile(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        final View headerView = navigationView.getHeaderView(0);
        txtuserName= (TextView) headerView.findViewById(R.id.userName);
        txtusermail= (TextView) headerView.findViewById(R.id.emailId);
        nav_profile=(ImageView) headerView.findViewById(R.id.nav_profile);
        UpdateUserDetails();
        fragment = new OrderItems_Fragment();
        FragmentTransaction fragmentTransaction=
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

    }
    private void UpdateUserDetails() {
        securityProfile = mHelper.getProfile();
        txtuserName.setText(securityProfile.getName());
        txtusermail.setText(securityProfile.getEmail());
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Accounts/1.png");
        if(file.exists())
            nav_profile.setImageURI(Uri.fromFile(file));
        else
            nav_profile.setImageResource(R.drawable.profile_user);
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if (doubleBackToExitPressedOnce) {
//                super.onBackPressed();
//                return;
//            }
            if(fragment instanceof EmpSalaryTransaction){
                fragment = new ManageEmployee();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
            }
            else if(!(fragment instanceof OrderItems_Fragment))
            {
                fragment = new OrderItems_Fragment();
                FragmentTransaction fragmentTransaction=
                        getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
                //fab.show();
            }else {
                this.doubleBackToExitPressedOnce = true;
                showAlertWithCancels("Do you want to backup data before exist?");
//                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

//                new Handler().postDelayed(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        doubleBackToExitPressedOnce = false;
//                    }
//                }, 2000);
            }
        }
    }
    public void signIn() {
        Set<Scope> requiredScopes = new HashSet<>(2);
        requiredScopes.add(Drive.SCOPE_FILE);
        requiredScopes.add(Drive.SCOPE_APPFOLDER);
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if (signInAccount != null && signInAccount.getGrantedScopes().containsAll(requiredScopes)) {
            initializeDriveClient(signInAccount);
        } else {
            mGoogleSignInClient = buildGoogleSignInClient();
            this.startActivityForResult(mGoogleSignInClient.getSignInIntent(), REQUEST_CODE_SIGN_IN);
        }

    }
    private GoogleSignInClient buildGoogleSignInClient() {
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestScopes(Drive.SCOPE_FILE)
                        .requestScopes(Drive.SCOPE_APPFOLDER)
                        .requestEmail()
                        .build();
        return GoogleSignIn.getClient(this, signInOptions);
    }
    public void showAlertWithCancels(String BuilderText) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(this, R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Business Assistant -Backup Data");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
                System.exit(0);

            }
        });
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadFile.UploadFile();
                dialog.dismiss();
                finish();
            }
        });
        builder.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                    return;
                }

                Task<GoogleSignInAccount> getAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                if (getAccountTask.isSuccessful()) {
                    initializeDriveClient(getAccountTask.getResult());

                } else {
                    Toast.makeText(this,
                            "Unable to sign in to google account",Toast.LENGTH_LONG).show();
                }
                break;
            case REQUEST_PICK_FILE:
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
                        File myFile=new File(filepath);
                        if (fragment instanceof ManageBackup)
                            ((ManageBackup) fragment).restoreOffline(myFile);
                    }
                }
                break;
            case REQUEST_PICK_PROFILE :
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
//                    if(!filepath.equals("")) {
//                        if(fragment instanceof Profile) {
//                            ((Profile)fragment).ScalandSetProfile(filepath);
//                        }
//                    }
                }
                break;

        }
        fragment.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_Home) {
            if(!(fragment instanceof Home))
                fragment = new Home();
            // Handle the camera action
        } else if (id == R.id.nav_employee) {
            if(!(fragment instanceof ManageEmployee))
                fragment = new ManageEmployee();
        } else if(id==R.id.nav_customers) {
            if(!(fragment instanceof ManageCustomers))
                fragment = new ManageCustomers();
        }
        else if (id == R.id.nav_masteritem) {
            if(!(fragment instanceof MasterItemFragment))
                fragment = new MasterItemFragment();

        } else if (id == R.id.nav_order) {
            if(!(fragment instanceof OrderItems_Fragment))
                fragment = new OrderItems_Fragment();

        }else if (id == R.id.nav_profile) {
            if(!(fragment instanceof Profile) ){
                fragment=new Profile();
            }
        }else if(id==R.id.nav_backup) {
            if(!(fragment instanceof ManageBackup))
                fragment = new ManageBackup();
        }else if(id==R.id.nav_about){
            ShowDialogTransDate();
        }
//        else if (id == R.id.nav_send) {
//
//        }
        FragmentTransaction fragmentTransaction=
                getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        fragmentTransaction.replace(R.id.fragment_container,fragment);
        fragmentTransaction.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void initializeDriveClient(GoogleSignInAccount signInAccount) {
        mDriveClient = Drive.getDriveClient(getApplicationContext(), signInAccount);
        mDriveResourceClient = Drive.getDriveResourceClient(getApplicationContext(), signInAccount);
        //onDriveClientReady();
    }
    private void ShowDialogTransDate() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.about_dialog);
        dialog.show();

    }
}
