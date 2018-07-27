package CustomAdapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by USER on 24-07-2018.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder>{
    public interface OnBindCallback {
        public void call(View view, int position);
    }
    private OnBindCallback onBindCallback;
    private LayoutInflater inflater;
    private ArrayList arrValues=new ArrayList();
    int layoutId;
    private View inflatedView;
    public RecyclerViewAdapter(Context context, int layoutId, OnBindCallback onBindCallback){
        inflater=LayoutInflater.from(context);
        this.onBindCallback=onBindCallback;
        this.layoutId=layoutId;
    }
    public void setValues(ArrayList arrValues){
        this.arrValues=arrValues;
        notifyItemRangeChanged(0,this.arrValues.size());
    }
    public void setData(ArrayList arrValues){
        this.arrValues=arrValues;
        if(this.arrValues.size()==0) this.arrValues=new ArrayList();
        notifyDataSetChanged();
    }
    @Override
    public RecyclerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view=inflater.inflate(layoutId,parent,false);

        RecyclerViewHolder viewHolderMyBookings=new RecyclerViewHolder(view);
        inflatedView=view;
        return viewHolderMyBookings;
    }
    @Override
    public void onBindViewHolder(RecyclerViewHolder holder, int position) {

        onBindCallback.call(holder.getView(),position);
    }

    @Override
    public int getItemCount() {
        return arrValues.size();
    }
    static class RecyclerViewHolder extends RecyclerView.ViewHolder{


        View view;
        private Context mContext;

        public RecyclerViewHolder(View view){
            super(view);
            mContext=view.getContext();
            this.view=view;

        }

        public View getView(){
            return view;
        }
    }
}
