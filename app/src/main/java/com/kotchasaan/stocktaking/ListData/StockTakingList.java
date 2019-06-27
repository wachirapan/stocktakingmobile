package com.kotchasaan.stocktaking.ListData;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kotchasaan.stocktaking.R;
import com.kotchasaan.stocktaking.util.StockTake;


import java.util.ArrayList;

public class StockTakingList  extends BaseAdapter {
    Context mcontext;
    ArrayList<StockTake> mlist ;
    public StockTakingList(Context mcontext, ArrayList<StockTake> mlist){
        this.mcontext = mcontext ;
        this.mlist = mlist ;
    }
    @Override
    public int getCount() {
        return mlist.size();
    }

    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mcontext, R.layout.list_stock_taking,null);
        TextView m_text_title = (TextView) v.findViewById(R.id.text_title);
        TextView m_text_description = (TextView) v.findViewById(R.id.text_description);
        m_text_title.setText(mlist.get(position).getTagCODE());
        m_text_description.setText(mlist.get(position).getTotalCount());
        return v;
    }
}
