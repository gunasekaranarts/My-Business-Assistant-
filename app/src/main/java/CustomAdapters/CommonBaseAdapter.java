package CustomAdapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

/**
 * Created by USER on 24-07-2018.
 */

public class CommonBaseAdapter extends BaseAdapter {
    Context mContext;
    ArrayList mAdapterList = new ArrayList();
    int mLayoutId;
    OnAdapterCallBack callBack;

    CommonBaseAdapter(Context context, int layoutId, OnAdapterCallBack callBack)
    {
        mContext=context;
        mLayoutId=layoutId;
        this.callBack=callBack;

    }

    @Override
    public int getCount() {
        return mAdapterList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAdapterList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        if(convertView==null)
        {
            viewHolder=new ViewHolder();
            convertView= LayoutInflater.from(mContext).inflate(mLayoutId,parent,false);
            viewHolder.convertView=convertView;
            convertView.setTag(viewHolder);

        }else{

            viewHolder= (ViewHolder) convertView.getTag();
        }

        callBack.onAdapterBindView(position,viewHolder.convertView,parent);


        return convertView;
    }
    interface OnAdapterCallBack{

        public void onAdapterBindView(int position, View adapterView, ViewGroup parent);


    }
    public void setAdapterData(ArrayList values)
    {

        mAdapterList=new ArrayList();

        if(values.size()>0)
        {

            mAdapterList=values;
            notifyDataSetChanged();
        }

    }
    public static class ViewHolder{
        View convertView;

    }

}
