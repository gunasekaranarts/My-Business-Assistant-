package com.sendhan.mybusinessassistant;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import CustomAdapters.CustomersAdapter;
import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.Customers;
import TableData.CustomersTableData;

import static android.app.Activity.RESULT_OK;

/**
 * Created by USER on 26-07-2018.
 */

public class ManageCustomers extends Fragment {AppCompatEditText txtName,txtMobile,txtPlace;
    Button btnSave;
    MBADatabase mSQLHelper;
    SQLiteDatabase dataBase;
    Customers customer;
    RecyclerView customerList;
    RecyclerView.LayoutManager mLayoutManager;
    CustomersAdapter customersAdapter;
    ArrayList<Customers> addCustomers = new ArrayList<>();
    TextAwesome txt_pick_contact;
    private static final int REQUEST_CODE = 1;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.manage_customers, container, false);
        txtName=(AppCompatEditText) view.findViewById(R.id.txt_customerName);
        txtMobile=(AppCompatEditText) view.findViewById(R.id.txt_customerMobile);
        txtPlace=(AppCompatEditText) view.findViewById(R.id.txt_customer_place);
        btnSave=(Button) view.findViewById(R.id.btn_customer);
        txt_pick_contact=(TextAwesome) view.findViewById(R.id.txt_pick_contact);
        mSQLHelper=new MBADatabase(getContext());
        customerList = (RecyclerView) view.findViewById(R.id.customerlist);
        customerList.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        customerList.setLayoutManager(mLayoutManager);
        displayCustomers();
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(customer==null){
                    if(txtName.getText().toString().equals("")){
                        txtName.requestFocus();
                        txtName.setError("Name can't be empty");
                    }else if(txtMobile.getText().toString().equals("")){
                        txtMobile.requestFocus();
                        txtMobile.setError("Mobile no can't be empty");
                    }else{
                        customer=new Customers();
                        customer.setCustomerName(txtName.getText().toString().trim());
                        customer.setCustomerMobile(txtMobile.getText().toString().trim());
                        customer.setCustomerPlace(txtPlace.getText().toString().trim());
                        new AddCustomer().execute();

                    }
                }else{
                    customer.setCustomerName(txtName.getText().toString().trim());
                    customer.setCustomerMobile(txtMobile.getText().toString().trim());
                    customer.setCustomerPlace(txtPlace.getText().toString().trim());
                    new EditCustomer().execute();
                }

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

        return  view;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent intent) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = intent.getData();
                String[] projection = { ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME };

                Cursor cursor =getActivity().getContentResolver().query(uri, projection,
                        null, null, null);
                cursor.moveToFirst();

                int numberColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(numberColumnIndex);

                int nameColumnIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                String name = cursor.getString(nameColumnIndex);
                txtName.setText(name);
                txtMobile.setText(number.replace(" ",""));


            }
        }
    };
    private void displayCustomers() {
        addCustomers=mSQLHelper.getCustomers();
        customersAdapter = new CustomersAdapter( addCustomers,(AppCompatActivity) getActivity(),ManageCustomers.this);
        customerList.setAdapter(customersAdapter);
        customersAdapter.notifyDataSetChanged();
        customer=null;

    }
    public void EditCustomer(Customers c){
        customer=new Customers();
        customer.setCustomerID(c.getCustomerID());
        customer.setCustomerName(c.getCustomerName());
        customer.setCustomerMobile(c.getCustomerMobile());
        customer.setCustomerPlace(c.getCustomerPlace());
        txtName.setText(c.getCustomerName());
        txtMobile.setText(c.getCustomerMobile());
        txtPlace.setText(c.getCustomerPlace());
    }

    public void showAlertWithCancels(String BuilderText) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Accounts - Manage Custmomer");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReloadOperation();
                dialog.dismiss();
            }
        });
        builder.show();
    }
    private void ReloadOperation() {
        addCustomers.clear();
        displayCustomers();
    }
    private class AddCustomer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            mSQLHelper.SaveCustomerData(customer);
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
            showAlertWithCancels("Customer has been added successfully.");
        }
    }
    private class EditCustomer extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            UpdateCustomerData();
            return "sucess full added";
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            clearOperation();
            showAlertWithCancels("Customer has been updated successfully.");
        }
    }
    private void UpdateCustomerData() {
        ContentValues values=new ContentValues();
        dataBase=mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        values.put(CustomersTableData.CustomerName, customer.getCustomerName());
        values.put(CustomersTableData.CustomerMobile, customer.getCustomerMobile());
        values.put(CustomersTableData.CustomerPlace, customer.getCustomerPlace());
        dataBase.update(CustomersTableData.CustomerTableName,values,CustomersTableData.CustomerID+"=?",new String[] {String.valueOf(customer.getCustomerID())});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }

    private void clearOperation() {
        txtName.setText("");
        txtMobile.setText("");
        txtPlace.setText("");
        txtName.requestFocus();
        customer=null;
    }
}

