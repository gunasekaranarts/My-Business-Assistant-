package com.sendhan.mybusinessassistant;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatAutoCompleteTextView;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import CustomAdapters.RecyclerViewAdapter;
import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.Customers;
import POJO.Item;
import POJO.OrderItem;
import POJO.POrders;
import POJO.SecurityProfile;
import TableData.OrderItems;
import Utils.AppUtil;

/**
 * Created by USER on 26-07-2018.
 */

public class OrderItems_Fragment extends Fragment {

    AppCompatSpinner spr_orders,spr_customers;
    AppCompatButton btn_add_new,btn_add,btn_print;
    AppCompatAutoCompleteTextView auto_txt_itemname;
    AppCompatEditText txt_qty,txt_suggest;
    MBADatabase mSqlHelper;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Context mContext;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<POrders>orders;
    ArrayList<OrderItem> orderItems;
    ArrayList<Customers> customers = new ArrayList<>();
    ArrayAdapter customerAdapter,ordersAdapter;
    Customers selectedCustomer;
    OrderItem orderItem;
    POrders selectedOrder;

    Fragment fragment;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.fragment_orderitems, container, false);
        spr_customers=view.findViewById(R.id.spr_customers);
        spr_orders=view.findViewById(R.id.spr_orders);
        btn_add=view.findViewById(R.id.btn_add);
        btn_add_new=view.findViewById(R.id.btn_add_new);
        btn_print=view.findViewById(R.id.btn_print);
        auto_txt_itemname=view.findViewById(R.id.auto_txt_itemname);
        txt_qty=view.findViewById(R.id.txt_qty);
        txt_suggest=view.findViewById(R.id.txt_suggest);
        recyclerView=view.findViewById(R.id.rcv_order_items);
        layoutManager=new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        mContext=getContext();
        layoutManager = new LinearLayoutManager(mContext);
        mSqlHelper=new MBADatabase(mContext);
        setCustomerAdapter();
        setOrdersAdapter(0);
        recyclerViewAdapter=new RecyclerViewAdapter(mContext,
                R.layout.master_list_item, new RecyclerViewAdapter.OnBindCallback() {
            @Override
            public void call(View view, final int position) {
                displayItems(view,position);

            }
        });
        ArrayAdapter autoCompleteAdapter = new ArrayAdapter(mContext, android.R.layout.simple_list_item_1, mSqlHelper.GetMasterItems());
        spr_customers.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCustomer=customers.get(position);
                setOrdersAdapter(selectedCustomer.getCustomerID());
                clearItems();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spr_orders.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearItems();
                setOrderItemsAdapter();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        auto_txt_itemname.setHint("Enter Item Name");
        auto_txt_itemname.setThreshold(1);
        auto_txt_itemname.setAdapter(autoCompleteAdapter);
        btn_add_new.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewOrder();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message="";
                if(spr_orders.getSelectedItemPosition()==0){
                    spr_orders.performClick();
                    spr_orders.requestFocus();
                    message="Please select an order or click new order! \n";
                }
                if(auto_txt_itemname.getText().toString().equals("")){
                    if(message.equals(""))
                        auto_txt_itemname.requestFocus();
                    auto_txt_itemname.setError("You must enter Item name");
                    message+="You must enter Item name \n ";
                }
                if(txt_qty.getText().toString().equals("")){
                    txt_qty.setError("You must enter quantity!");
                    if(message.equals(""))
                        txt_qty.requestFocus();
                    message+="You must enter quantity!";
                }
                if(message.equals("")){
                    if(orderItem==null)
                        orderItem=new OrderItem();
                    orderItem.setItemName(auto_txt_itemname.getText().toString());
                    orderItem.setItemQty(txt_qty.getText().toString());
                    orderItem.setItemSuggest(txt_suggest.getText().toString());
                    selectedOrder=orders.get(spr_orders.getSelectedItemPosition());
                    orderItem.setOrderId(selectedOrder.getOrderId());
                    mSqlHelper.InsertOrUpdateOrderItem(orderItem);
                    Item item=new Item();
                    item.setMasterItemName(auto_txt_itemname.getText().toString());
                    mSqlHelper.CheckInsertMasterItem(item);
                    refreshRecyclerView();
                    clearItems();
                }else{
                    AppUtil.ShowMessage(mContext,message,0);
                }
            }
        });
        btn_print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragment =new PrintPreview();
//                FragmentTransaction fragmentTransaction=
//                        getActivity().getSupportFragmentManager().beginTransaction();
//                fragmentTransaction.setCustomAnimations(R.anim.enter_from_left,
//                        R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
//                fragmentTransaction.replace(R.id.fragment_container,fragment);
//                fragmentTransaction.commit();
                showAlertPrint("Do you want to print this bill?" ,1);
            }
        });
        return view;
    }

    private void clearItems() {
        orderItem=null;
        auto_txt_itemname.setText("");
        txt_qty.setText("");
        txt_suggest.setText("");
    }

    private void setOrderItemsAdapter() {
        if(spr_orders.getSelectedItemPosition()>=1) {
            selectedOrder = orders.get(spr_orders.getSelectedItemPosition());
            orderItems = mSqlHelper.getOrderItems(selectedOrder.getOrderId());
            recyclerViewAdapter.setData(orderItems);
            recyclerView.setAdapter(recyclerViewAdapter);
        }else{
            if(orderItems!=null) {
                orderItems.clear();
                recyclerViewAdapter.setData(orderItems);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        }
    }
    public void refreshRecyclerView(){
        if(spr_orders.getSelectedItemPosition()>=1) {
            orderItems.clear();
            selectedOrder = orders.get(spr_orders.getSelectedItemPosition());
            orderItems = mSqlHelper.getOrderItems(selectedOrder.getOrderId());
            recyclerViewAdapter.setData(orderItems);
            recyclerView.setAdapter(recyclerViewAdapter);
        }else{
            orderItems.clear();
            recyclerViewAdapter.setData(orderItems);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
    }

    private void displayItems(View view, int position) {
        final TextView txtvw_itemname;
        final TextAwesome btn_detele;
        orderItem=orderItems.get(position);
        txtvw_itemname = (TextView) view.findViewById(R.id.txtvw_itemname);
        txtvw_itemname.setText(orderItem.getItemName()+" \nQty: "+orderItem.getItemQty()+"\nSuggest:"+orderItem.getItemSuggest());
        btn_detele = (TextAwesome) view.findViewById(R.id.btn_detele);
        btn_detele.setOnClickListener(new View.OnClickListener() {
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
                        dataBase.delete(OrderItems.OrderItemsTable, OrderItems.OrderItemId + "=?",new String[] {String.valueOf(orderItem.getItemId())});
                        dataBase.setTransactionSuccessful();
                        dataBase.endTransaction();
                        dataBase.close();
                        dialog.dismiss();
                        setOrderItemsAdapter();
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
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auto_txt_itemname.setText(orderItem.getItemName());
                txt_qty.setText(orderItem.getItemQty());
                txt_suggest.setText(orderItem.getItemSuggest());
            }
        });

    }

    private void AddNewOrder() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.dialog_add_new_order);
        final AppCompatEditText txt_order_name=(AppCompatEditText) dialog.findViewById(R.id.txt_order_name);
        Button Ok = (Button) dialog.findViewById(R.id.btn_add);
        Ok.requestFocus();
        Ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_order_name.getText().toString().equals("")){
                    txt_order_name.setError("Should not be empty!!!");
                    txt_order_name.requestFocus();
                }else {
                    Calendar calendar = Calendar.getInstance();
                    String cdate=android.text.format.DateFormat.format("yyyyMMddhhmm", calendar.getTime()).toString();
                    POrders po=new POrders();
                    po.setOrderDate(cdate);
                    po.setOrderName(txt_order_name.getText().toString());
                    po.setCustomerId(selectedCustomer.getCustomerID());
                    mSqlHelper.InsertOrder(po);
                    setOrdersAdapter(selectedCustomer.getCustomerID());
                    spr_orders.setSelection(1,true);
                    dialog.dismiss();
                }

            }
        });
        dialog.show();
    }

    private void setCustomerAdapter() {
        customers=new ArrayList<Customers>();
        Customers tc=new Customers();
        tc.setCustomerName("General Bill");
        tc.setCustomerID(0);
        tc.setCustomerPlace("");
        customers=mSqlHelper.getCustomers();
        customers.add(0,tc);
        customerAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, customers);
        spr_customers.setAdapter(customerAdapter);
    }
    private void setOrdersAdapter(int CustomerId) {
        orders=new ArrayList<POrders>();
        orders=mSqlHelper.getOrders(CustomerId);
        POrders to=new POrders();
        to.setOrderName("Select any Order");
        orders.add(0,to);
        ordersAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_dropdown_item, orders);
        spr_orders.setAdapter(ordersAdapter);
    }

    public void showAlertPrint(String BuilderText,final int i) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Business Assistant");
        builder.setMessage(BuilderText);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(i==0)
                    SavePDF(false);
                else if (i==1)
                    SavePDF(true);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//                if(i==0)
//                    SaveExcel(false);
//                else if (i==1)
//                    SaveExcel(true);

            }
        });
        builder.show();
    }
    private void SavePDF(boolean is_Share) {

        if(orderItems.size()>0){
            File myDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/My Business Assistant/");
            if (!myDir.exists()) {
                myDir.mkdir();
            }

            Calendar c = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmm");
            String formattedDate = df.format(c.getTime());

            String pdfFile = selectedOrder.getOrderName()+selectedOrder.getOrderDate()+".pdf";
            File file = new File(myDir, pdfFile);
//            Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18,
//                    Font.BOLD);
//            Font greenFont = new Font(Font.FontFamily.TIMES_ROMAN, 22,
//                    Font.BOLDITALIC, BaseColor.GREEN);
//            Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//                    Font.NORMAL, BaseColor.RED);
//            Font blueFont = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//                    Font.NORMAL, BaseColor.BLUE);
//            Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16,
//                    Font.BOLD);
//            Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12,
//                    Font.BOLD);

            try{
                Document document = new Document(PageSize.A4);
                PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();
                addMetaData(document);
                document.newPage();
                Paragraph preface = new Paragraph();
                addEmptyLine(preface, 1);
                Paragraph heading=new Paragraph("My Business Assistant App- Bill");
                heading.setAlignment(Element.ALIGN_CENTER);
                document.add(heading);

                addEmptyLine(preface, 1);
                SecurityProfile securityProfile = mSqlHelper.getProfile();
                preface.add(new Paragraph(
                        "Order No :"+selectedOrder.getOrderId()));
                preface.add(new Paragraph(
                        "Order Name\t:\t "+selectedOrder.toString()));
                preface.add(new Paragraph(
                        "This Order sheet generated by My Business Assitant App(MBA)"));
                preface.setAlignment(Element.ALIGN_LEFT);
                document.add(preface);
                Paragraph preface1=new Paragraph();
                addEmptyLine(preface1,1);
                document.add(preface1);
                addTransactionTable(document);
                Paragraph thks=new Paragraph("* * * Thanks * * *");
                thks.setAlignment(Element.ALIGN_CENTER);
                document.add(thks);
                document.close();
                Toast.makeText(getActivity(), "Data Exported as a PDF file at - "+file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                if(is_Share)
                {
                    if(file.exists())
                        Share(file.getAbsolutePath());
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (DocumentException e) {
                e.printStackTrace();
            }
        }else{
            showAlertWithCancel("Add atleast one item to print!");
        }
    }
    private void addTransactionTable(Document document ) {
        PdfPTable table = new PdfPTable(5);
        try {
            table.setWidths(new int[]{1,4, 1, 1,1});

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        PdfPCell c1 = new PdfPCell(new Phrase("SNo"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Name and Suggestion"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Qty"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Check"));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase(""));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(c1);

        table.setHeaderRows(1);
        int i=0;
        for (OrderItem t:
                orderItems) {
            i+=1;
            c1 = new PdfPCell(new Phrase(String.valueOf(i)));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            AppUtil.ShowMessage(mContext,t.getItemSuggest(),1);

            try {
                table.addCell(URLEncoder.encode(t.getItemName(), "UTF-8")+" \n"+t.getItemSuggest());
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            c1=new PdfPCell(new Phrase(t.getItemQty()));
            c1.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(c1);
            table.addCell("");
            table.addCell("");
        }
        try {
            document.add(table);
            Paragraph preface = new Paragraph();
            addEmptyLine(preface,1);
            document.add(preface);
        } catch (DocumentException e) {
            e.printStackTrace();
        }

    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }
//    public void setupFilter(Paragraph preface,Font subFont){
//        preface.add(new Paragraph(
//                "Bill Details",
//                subFont));
//        if(filter.getFromDate()!=null)
//            preface.add(new Paragraph(
//                    "From Date : "+filter.getFromDate().toString(),
//                    subFont));
//        if(filter.getToDate()!=null)
//            preface.add(new Paragraph(
//                    "To Date : "+filter.getToDate().toString(),
//                    subFont));
//        if(filter.getTransactionDate()!=null)
//            preface.add(new Paragraph(
//                    "Date : "+filter.getTransactionDate().toString(),
//                    subFont));
//        if(filter.getKeyword()!=null)
//            preface.add(new Paragraph(
//                    "Keyword : "+filter.getKeyword().toString(),
//                    subFont));
//        if(filter.getTransactionTypeId()!=0)
//            preface.add(new Paragraph(
//                    "Transaction Type : "+TransactionType.getItemById(transactionType
//                            ,filter.getTransactionTypeId()).getTransactionType(),
//                    subFont));
//        if(filter.getPersonId()!=0)
//            preface.add(new Paragraph(
//                    "Beneficiary Name : "+Persons.getItemById(persons,
//                            filter.getPersonId()).getPersonName(),
//                    subFont));
//        if(filter.getPersonId()!=0)
//            preface.add(new Paragraph(
//                    "Beneficiary Mobile : "+Persons.getItemById(persons,
//                            filter.getPersonId()).getMobile(),
//                    subFont));
//    }
    private static void addMetaData(Document document) {
        document.addTitle("My Business Assistant");
        document.addSubject("Created by My Business Assistant app");
        document.addKeywords("Java, PDF, iText, My Business Assistant, Guna,");
        document.addAuthor("Guna");
        document.addCreator("Guna");
    }
    public void showAlertWithCancel(String BuilderText) {
        android.support.v7.app.AlertDialog.Builder builder =
                new AlertDialog.Builder(getActivity(), R.style.Theme_AppCompat_DayNight_Dialog);
        builder.setTitle("My Business Assitant");
        builder.setMessage(BuilderText);
        builder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
    public void Share(String path){
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        String aEmailList[] = { "" };
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, aEmailList);
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "My Business Assistant Order List");
        emailIntent.setType("plain/text");
        File fileIn = new File(path);
        String message = "My Business Assistant app order list. \n\n Attachment Name: "+fileIn.getName()+"\n PFA";
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,message);
        emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(fileIn));
        startActivity(Intent.createChooser(emailIntent, "Send via..."));
    }
}
