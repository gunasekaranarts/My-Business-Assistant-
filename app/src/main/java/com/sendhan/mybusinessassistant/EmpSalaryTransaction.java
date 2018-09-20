package com.sendhan.mybusinessassistant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;

import CustomAdapters.RecyclerViewAdapter;
import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.EmpSalaryTransactions;
import POJO.Employee;
import TableData.Employees;
import TableData.SalaryTransaction;
import Utils.AppUtil;
import Utils.DateUtil;
import fr.ganfra.materialspinner.MaterialSpinner;

/**
 * Created by USER on 06-08-2018.
 */

public class EmpSalaryTransaction extends Fragment {
    Context mContext;
    MBADatabase mSqlHelper;
    TextView txt_summary;
    AppCompatEditText transdate,txt_transdesc,transamount;
    MaterialSpinner sprTransaction_Type;
    Button btn_employee;
    RecyclerView rcv_emp_transactions;
    RecyclerViewAdapter rcvAdapter;
    RecyclerView.LayoutManager mlayoutManager;
    ArrayList<EmpSalaryTransactions> salaryTransactions;
    Employee employee;
    EmpSalaryTransactions salaryTransaction;
    DecimalFormat format = new DecimalFormat("0.00");
    int EmpId=0;
    public Fragment Initialize(int empId) {
        this.EmpId = empId;
        return this;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_emp_transactions,container,false);
        txt_summary=view.findViewById(R.id.txt_summary);
        transdate=view.findViewById(R.id.transdate);
        txt_transdesc=view.findViewById(R.id.txt_transdesc);
        transamount=view.findViewById(R.id.transamount);
        sprTransaction_Type=view.findViewById(R.id.sprTransaction_Type);
        btn_employee=view.findViewById(R.id.btn_employee);
        rcv_emp_transactions=view.findViewById(R.id.rcv_emp_transactions);
        mContext=getContext();
        mlayoutManager=new LinearLayoutManager(mContext);
        rcv_emp_transactions.setLayoutManager(mlayoutManager);
        mSqlHelper=new MBADatabase(mContext);
        employee=mSqlHelper.GetEmployeeById(EmpId);

        UpdateHeader();
        salaryTransactions=mSqlHelper.GetSalTransactions(EmpId);
        rcvAdapter=new RecyclerViewAdapter(mContext, R.layout.emp_trans_item, new RecyclerViewAdapter.OnBindCallback() {
            @Override
            public void call(View view, int position) {
                displayTransactions(view,position);     
            }
        });
        transdate.setText("" + DateUtil.getCurrentDateHuman());
        transdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialogTransDate("Transaction Date", 1);
            }
        });
        rcvAdapter.setData(salaryTransactions);
        rcv_emp_transactions.setAdapter(rcvAdapter);
        btn_employee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(salaryTransaction==null)
                 salaryTransaction=new EmpSalaryTransactions();
                if(transdate.getText().toString().equals("")){
                    transdate.setError("Transaction date should not be empty");
                    transdate.requestFocus();
                }else if(sprTransaction_Type.getSelectedItemPosition()==-1){
                    sprTransaction_Type.setError("Transaction type should not be empty");
                    sprTransaction_Type.requestFocus();
                }else if(transamount.getText().toString().equals("")){
                    transamount.setError("Amount can't be empty");
                    transamount.requestFocus();
                }else{
                    salaryTransaction.setTransactionDate(transdate.getText().toString());
                    salaryTransaction.setEmployeeId(EmpId);
                    salaryTransaction.setTransDesc(txt_transdesc.getText().toString());
                    if(sprTransaction_Type.getSelectedItem().toString().equals("Salary"))
                        salaryTransaction.setSalaryTransType("1");
                    else if(sprTransaction_Type.getSelectedItem().toString().equals("Advance Salary"))
                        salaryTransaction.setSalaryTransType("2");
                    else if(sprTransaction_Type.getSelectedItem().toString().equals("Salary Paid"))
                        salaryTransaction.setSalaryTransType("3");
                    else if(sprTransaction_Type.getSelectedItem().toString().equals("Advance Return"))
                        salaryTransaction.setSalaryTransType("4");
                    salaryTransaction.setTransactionAmount(Integer.parseInt(transamount.getText().toString()));
                    mSqlHelper.AddOrUpdateSalTrans(salaryTransaction);
                    AppUtil.ShowMessage(mContext,"Transaction Added Successfully",1);
                    ClearAll();

                }

            }
        });
        return view;
    }

    private void UpdateHeader() {

        txt_summary.setText(employee.getEmployeeName()+"\n"+employee.getEmpMobile()+"\nPending Salary:₹"
                +format.format(mSqlHelper.GetSalarySum(EmpId))+"\t Advance :₹"+format.format(mSqlHelper.GetAdvanceSalarySum(EmpId)));

    }

    private void ClearAll() {
        UpdateHeader();
        txt_transdesc.setText("");
        transamount.setText("");
        sprTransaction_Type.setSelection(0);
        salaryTransaction=null;
        salaryTransactions=mSqlHelper.GetSalTransactions(EmpId);
        rcvAdapter.setData(salaryTransactions);
    }

    private void ShowDialogTransDate(String Titile, final int i) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_datetimepicker);
        TextView title = (TextView) dialog.findViewById(R.id.dialog_datetime_title);
        title.setText("" + Titile);
        Button Ok = (Button) dialog.findViewById(R.id.setdatetime_ok);
        final DatePicker dp = (DatePicker) dialog.findViewById(R.id.date_picker);
        Button cancel = (Button) dialog.findViewById(R.id.setdatetime_cancel);
        dp.setMaxDate((System.currentTimeMillis() - 1000));
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String month = String.valueOf(dp.getMonth() + 1);
                String day = String.valueOf(dp.getDayOfMonth());
                String strDateTime = ( dp.getYear() + "-" + (((month.length() == 1 ? "0" + month : month))) + "-" + (day.length() == 1 ? "0" + day : day));

                switch (i) {
                    case 1:
                        transdate.setText("" + strDateTime);
                        break;
                    default:
                }
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }
    private void displayTransactions(View view, int position) {
        final AppCompatTextView txt_trans_type,txt_trans_date,txt_trans_desc,txt_trans_amt;
        final TextAwesome btn_detete,btn_edit;
        final EmpSalaryTransactions item=salaryTransactions.get(position);
        txt_trans_type=view.findViewById(R.id.txt_trans_type);
        txt_trans_date=view.findViewById(R.id.txt_trans_date);
        txt_trans_desc=view.findViewById(R.id.txt_trans_desc);
        txt_trans_amt=view.findViewById(R.id.txt_trans_amt);
        btn_detete=view.findViewById(R.id.btn_detete);
        btn_edit=view.findViewById(R.id.btn_edit);
        String transtype="";
        if(item.getSalaryTransType().equals("1"))
            transtype="Salary";
        else if(item.getSalaryTransType().equals("2"))
            transtype="Advance Salary";
        else if(item.getSalaryTransType().equals("3"))
            transtype="Salary Paid";
        else if(item.getSalaryTransType().equals("4"))
            transtype="Advance Return";
        txt_trans_type.setText(transtype);
        txt_trans_date.setText(item.getTransactionDate());
        txt_trans_desc.setText(item.getTransDesc());
        txt_trans_amt.setText("₹ "+format.format(item.getTransactionAmount()));

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
                        dataBase.delete(SalaryTransaction.EmpSalaryTransactionTable, SalaryTransaction.EmpSalTransId + "=?",new String[] {String.valueOf(item.getSalaryTransactionId())});
                        dataBase.setTransactionSuccessful();
                        dataBase.endTransaction();
                        dataBase.close();
                        dialog.dismiss();
                        salaryTransactions=mSqlHelper.GetSalTransactions(EmpId);
                        rcvAdapter.setData(salaryTransactions);
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
                transdate.setText(item.getTransactionDate());
                sprTransaction_Type.setSelection(Integer.parseInt(item.getSalaryTransType()));
                txt_transdesc.setText(item.getTransDesc());
                transamount.setText(format.format(item.getTransactionAmount()));
                salaryTransaction=item;
            }
        });
    }
}

