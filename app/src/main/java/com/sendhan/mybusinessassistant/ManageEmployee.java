package com.sendhan.mybusinessassistant;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import CustomAdapters.RecyclerViewAdapter;
import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.Employee;
import TableData.Employees;
import Utils.AppUtil;

import static android.app.Activity.RESULT_OK;

/**
 * Created by USER on 06-08-2018.
 */

public class ManageEmployee extends Fragment {
    Employee employee;
    Button btn_employee;
    TextAwesome txt_pick_contact;
    ArrayList<Employee> employees;
    AppCompatEditText txt_Name,txt_Mobile;
    AppCompatImageView img_employee;
    Context mContext;
    MBADatabase mSqlHelper;
    RecyclerView rcv_employees;
    RecyclerView.LayoutManager mLayoutManager;
    RecyclerViewAdapter rcvAdapter;
    String userChoosenTask;
    final static int REQUEST_CAMERA=101;
    final static int REQUEST_FILE=102;
    private static final int REQUEST_CODE = 100;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.manage_employee,container,false);
        txt_Name=view.findViewById(R.id.txt_employeeName);
        txt_Mobile=view.findViewById(R.id.txt_employeeMobile);
        img_employee=view.findViewById(R.id.img_employee_image);
        rcv_employees=view.findViewById(R.id.rcv_employees);
        btn_employee=view.findViewById(R.id.btn_employee);
        txt_pick_contact=view.findViewById(R.id.txt_pick_contact);
        rcv_employees.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        rcv_employees.setLayoutManager(mLayoutManager);
        mContext=getContext();
        mSqlHelper=new MBADatabase(mContext);
        employee=new Employee();
       new LoadEmployee().execute();

        rcvAdapter=new RecyclerViewAdapter(mContext,
                R.layout.employee_item, new RecyclerViewAdapter.OnBindCallback() {
            @Override
            public void call(View view, final int position) {
                displayItems(view,position);

            }
        });
        txt_pick_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse("content://contacts");
                Intent intent = new Intent(Intent.ACTION_PICK, uri);
                intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                getActivity().startActivityForResult(intent, REQUEST_CODE);
            }
        });

        btn_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_Name.getText().toString().equals("")){
                    txt_Name.setError("Employee Name should not be empty");
                    txt_Name.requestFocus();
                }else if(txt_Mobile.getText().toString().equals(""))
                {
                    txt_Mobile.setError("Mobile no should not be empty");
                    txt_Mobile.requestFocus();
                }else{

                    employee.setEmployeeName(txt_Name.getText().toString());
                    employee.setEmpMobile(txt_Mobile.getText().toString());
                    employee.setEmpStatus("Active");
                    employee.setEmpRemarks("");
                    employee.setEmpJoinDate("");
                    employee.setEmployeeTypeId(1);
                    new SaveEmployee().execute();

                }
            }
        });

        img_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        return view;
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= AppUtil.checkPermission(getContext());
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),REQUEST_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                    Cursor cursor =getActivity().getContentResolver().query(uri, projection,
                            null, null, null);
                    cursor.moveToFirst();

                    int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String number = cursor.getString(numberColumnIndex);

                    int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                    String name = cursor.getString(nameColumnIndex);
                    txt_Name.setText(name);
                    txt_Mobile.setText(number.replace(" ",""));


                }
            }
        }
    }
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG,50,bytes);
                bm=AppUtil.getImage(bytes.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        employee.setEmployeePhoto(AppUtil.getBytes(bm));
        img_employee.setImageBitmap(bm);
    }
    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        employee.setEmployeePhoto(AppUtil.getBytes(thumbnail));
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        img_employee.setImageBitmap(thumbnail);
    }
    private void displayItems(View view, int position) {

        final AppCompatTextView txt_empname,txt_employeeMobile;
        final AppCompatImageView img_employee;
        final TextAwesome btn_detete,btn_edit;
        final Employee item=employees.get(position);
        txt_empname =(AppCompatTextView) view.findViewById(R.id.txt_empname);
        txt_employeeMobile =(AppCompatTextView) view.findViewById(R.id.txt_employeeMobile);
        img_employee=(AppCompatImageView)view.findViewById(R.id.img_employee);
        txt_empname.setText(item.getEmployeeName());
        txt_employeeMobile.setText(item.getEmpMobile());
        if(item.getEmployeePhoto().length>0)
            img_employee.setImageBitmap(AppUtil.getImage(item.getEmployeePhoto()));
        else {
            img_employee.setImageResource(R.drawable.profile_user);
            img_employee.setBackgroundColor(Color.BLACK);
        }
        btn_detete = (TextAwesome) view.findViewById(R.id.btn_detete);
        btn_edit=(TextAwesome) view.findViewById(R.id.btn_edit);
        btn_detete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
                builder.setTitle("My Bisuness Assistant");
                builder.setMessage("Are you sure want to delete?" );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SQLiteDatabase dataBase = mSqlHelper.getWritableDatabase();
                        dataBase.beginTransaction();
                        dataBase.delete(Employees.EmployeesTable, Employees.EmployeeId + "=?",new String[] {String.valueOf(item.getEmployeeId())});
                        dataBase.setTransactionSuccessful();
                        dataBase.endTransaction();
                        dataBase.close();
                        dialog.dismiss();
                        employees=mSqlHelper.GetEmployees();
                        rcvAdapter.setData(employees);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.setCancelable(false);
                builder.show();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_Name.setText(item.getEmployeeName());
                txt_Mobile.setText(item.getEmpMobile());
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).fragment = new EmpSalaryTransaction().Initialize(item.getEmployeeId());
//                FloatingActionButton floatingActionButton = ((MainActivity) getActivity())
//                        .getFloatingActionButton();
//                if(floatingActionButton!=null)
//                    floatingActionButton.show();
                FragmentTransaction fragmentTransaction=
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
                fragmentTransaction.replace(R.id.fragment_container,((MainActivity)getActivity()).fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });


    }
    private class SaveEmployee extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mSqlHelper.AddOrUpdateEmployee(employee);
            return "sucessfully added";
        }

        @Override
        protected void onPostExecute(String s) {
            AppUtil.ShowMessage(mContext,"Employee saved successfully", Toast.LENGTH_SHORT);
            employees=mSqlHelper.GetEmployees();
            rcvAdapter.setData(employees);

            ClearAll();
            super.onPostExecute(s);
        }
    }
    private class LoadEmployee extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            employees=mSqlHelper.GetEmployees();
            return "sucessfully added";
        }

        @Override
        protected void onPostExecute(String s) {

            rcvAdapter.setData(employees);
            rcv_employees.setAdapter(rcvAdapter);
            super.onPostExecute(s);
        }
    }

    private void ClearAll() {
        txt_Name.setText("");
        txt_Mobile.setText("");
        img_employee.setImageResource(R.drawable.add_photo);
        employee=new Employee();
    }
}
