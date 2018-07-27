package com.sendhan.mybusinessassistant;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by USER on 27-07-2018.
 */

public class PrintPreview extends Fragment {

    TableLayout tbl_items;
    TextView Sn,NameSuggest,Qty,Check,Empty;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.pdf_print, container, false);
        tbl_items=view.findViewById(R.id.tbl_items);
        TableRow rowHeading= new TableRow(getContext());
        TableRow.LayoutParams lp = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
        rowHeading.setLayoutParams(lp);

        Sn = new TextView(getContext());
        NameSuggest=new TextView(getContext());
        Qty = new TextView(getContext());
        Check = new TextView(getContext());
        Empty = new TextView(getContext());
        Sn.setTextColor(getResources().getColor(R.color.black));
        NameSuggest.setTextColor(getResources().getColor(R.color.black));
        Qty.setTextColor(getResources().getColor(R.color.black));
        Check.setTextColor(getResources().getColor(R.color.black));
        Sn.setText(String.valueOf("Sno"));
        Sn.setPadding(5,5,5,5);
        rowHeading.addView(Sn);
        NameSuggest.setText("Name & Suggestions");
        rowHeading.addView(NameSuggest);
        Qty.setText("Qty");
        rowHeading.addView(Qty);
        Check.setText("Check");
        rowHeading.addView(Check);
        rowHeading.addView(Empty);
        rowHeading.setBackgroundResource(R.drawable.row_border);
        tbl_items.addView(rowHeading);
        for (int i = 0; i <5; i++) {

            TableRow row= new TableRow(getContext());
            TableRow.LayoutParams lp1 = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT);
            row.setLayoutParams(lp1);

            Sn = new TextView(getContext());
            NameSuggest=new TextView(getContext());
            Qty = new TextView(getContext());
            Sn.setTextColor(getResources().getColor(R.color.black));
            NameSuggest.setTextColor(getResources().getColor(R.color.black));
            Qty.setTextColor(getResources().getColor(R.color.black));
           // Check.setTextColor(getResources().getColor(R.color.black));
            Sn.setText(String.valueOf(i));

            row.addView(Sn);
            NameSuggest.setText("asd asdasdasd asbjdija asbd iabdiabsd aiabdia ja siad as ia ja dia ");
            row.addView(NameSuggest);
            Qty.setText("123");
            row.addView(Qty);
            Check = new TextView(getContext());
            Empty = new TextView(getContext());
            row.addView(Check);
            row.addView(Empty);
            row.setBackgroundResource(R.drawable.row_border);
            tbl_items.addView(row,i);
        }

        return view;
    }
}
