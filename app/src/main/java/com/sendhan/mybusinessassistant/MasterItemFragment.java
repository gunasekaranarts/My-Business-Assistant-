package com.sendhan.mybusinessassistant;

import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import CustomAdapters.RecyclerViewAdapter;
import CustomWidget.TextAwesome;
import Database.MBADatabase;
import POJO.Item;
import TableData.MasterItems;
import Utils.AppUtil;

/**
 * Created by USER on 24-07-2018.
 */

public class MasterItemFragment extends Fragment {
    AppCompatEditText txt_ItemName;
    AppCompatButton btn_Save;
    Item item;
    ArrayList<Item> items;
    MBADatabase mSqlHelper;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    Context mContext;
    RecyclerViewAdapter recyclerViewAdapter;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view= inflater.inflate(R.layout.item_master, container, false);
        mContext=getContext();
        mSqlHelper=new MBADatabase(mContext);

        txt_ItemName=view.findViewById(R.id.txt_itemName);
        btn_Save=view.findViewById(R.id.btn_add);
        recyclerView=view.findViewById(R.id.masterItemList);
        layoutManager=new LinearLayoutManager(mContext);
        recyclerView.setLayoutManager(layoutManager);
        items=mSqlHelper.GetMasterItems();
        recyclerViewAdapter=new RecyclerViewAdapter(mContext,
                R.layout.master_list_item, new RecyclerViewAdapter.OnBindCallback() {
            @Override
            public void call(View view, final int position) {
                displayItems(view,position);

            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(txt_ItemName.getText().equals("")){
                    txt_ItemName.setError("Item name required!");
                    txt_ItemName.requestFocus();
                }else{
                    boolean isExist=false;
                    if(item==null)
                        item=new Item();
                    item.setMasterItemName(txt_ItemName.getText().toString());
                    for(Item t :items){
                        if(t.getMasterItemName().equals(item.getMasterItemName()))
                            isExist=true;
                    }
                    if (isExist)
                        AppUtil.ShowMessage(mContext,"This item already exists!", 0);
                    else{
                        mSqlHelper.AddOrUpdateItem(item);
                        item=null;
                        txt_ItemName.setText("");
                        RefreshAdapter();
                    }
                }
            }
        });

        recyclerViewAdapter.setData(items);
        recyclerView.setAdapter(recyclerViewAdapter);
        return view;
    }
    private void displayItems(View view, final int position) {

        final TextView txtvw_itemname;
       final TextAwesome btn_detele;
        txtvw_itemname = (TextView) view.findViewById(R.id.txtvw_itemname);
        txtvw_itemname.setText(items.get(position).getMasterItemName());
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
                        dataBase.delete(MasterItems.MasterItemsTable, MasterItems.MasterItemId + "=?",new String[] {String.valueOf(items.get(position).getMasterItemId())});
                        dataBase.setTransactionSuccessful();
                        dataBase.endTransaction();
                        dataBase.close();
                        dialog.dismiss();
                        RefreshAdapter();
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
                txt_ItemName.setText(items.get(position).getMasterItemName());
            }
        });
    }
    public void RefreshAdapter()
    {
        items.clear();
        items=mSqlHelper.GetMasterItems();
        recyclerViewAdapter.setData(items);
        recyclerViewAdapter.notifyDataSetChanged();
    }
}
