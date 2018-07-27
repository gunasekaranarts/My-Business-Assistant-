package CustomAdapters;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sendhan.mybusinessassistant.ManageCustomers;
import com.sendhan.mybusinessassistant.R;

import java.util.ArrayList;

import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.Customers;
import TableData.CustomersTableData;

/**
 * Created by USER on 31-01-2018.
 */

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {

    ArrayList<Customers> addCustomer = new ArrayList<>();
    String  cName;
    AppCompatActivity mContext;
    ProgressDialog progressDialog;
    MBADatabase mSQLHelper;
    SQLiteDatabase dataBase;
    int customersID,positions;
    ManageCustomers manageCustomers;

    public CustomersAdapter(ArrayList<Customers> addCustomer, AppCompatActivity mContext, ManageCustomers manageCustomers) {
        this.addCustomer = addCustomer;
        this.mContext = mContext;
        this.manageCustomers = manageCustomers;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new ViewHolder(view1);
    }
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final Customers customer = addCustomer.get(position);
        mSQLHelper = new MBADatabase(mContext);
        if(customer.getCustomerName()!=null ||customer.getCustomerPlace()!=null){
            holder.customerName.setText(""+customer.toString());
        }else {
            holder.customerName.setText("-");
        }
        if(customer.getCustomerMobile()!=null){
            holder.customerMobile.setText(""+customer.getCustomerMobile());
        }else {
            holder.customerMobile.setText("-");
        }
        holder.Edit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                manageCustomers.EditCustomer(addCustomer.get(position));
            }
        });
        holder.Delete.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                positions=position;
                cName = addCustomer.get(position).getCustomerName();
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
                builder.setTitle("My Business Assistant");
                builder.setMessage("Are you sure want to Remove " +cName+ " ?" );
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        customersID = addCustomer.get(position).getCustomerID();

                        new DeleteAsync().execute();
                        dialog.dismiss();
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

    }
    @Override
    public int getItemCount() {return addCustomer.size();
    }
    public class ViewHolder  extends RecyclerView.ViewHolder {
        TextView customerName, customerMobile;
        TextAwesome Edit, Delete;
        public ViewHolder(View itemView) {
            super(itemView);
            customerName=(TextView) itemView.findViewById(R.id.txt_customerview);
            customerMobile=(TextView) itemView.findViewById(R.id.txt_custmobileview);
            Edit = (TextAwesome) itemView.findViewById(R.id.txt_edit);
            Delete = (TextAwesome) itemView.findViewById(R.id.txt_delete);

        }
    }
    private class DeleteAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please wait...");
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            DeleteRow(customersID);
            return "sucess full deleted";
        }
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext, R.style.Theme_AppCompat_DayNight_Dialog);
            builder.setTitle("My Accounts");
            builder.setMessage("Customer " + cName + " details deleted successfully.");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialo, int which) {
                    ReloadData(positions);
                    dialo.dismiss();
                }
            });
            builder.setCancelable(false);
            builder.show();
        }
    }

    private void ReloadData(int positions) {
        addCustomer.remove(positions);
        notifyDataSetChanged();
    }


    private void DeleteRow(int customerID) {
        dataBase = mSQLHelper.getWritableDatabase();
        dataBase.beginTransaction();
        dataBase.delete(CustomersTableData.CustomerTableName, CustomersTableData.CustomerID + "=?",new String[] {String.valueOf(customerID)});
        dataBase.setTransactionSuccessful();
        dataBase.endTransaction();
        dataBase.close();
    }
}
