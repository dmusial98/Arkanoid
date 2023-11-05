package com.example.arkanoid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class SpinnerAdapter extends BaseAdapter
{
    ArrayList<Integer> colors;
    Context context;
    public boolean areBricks;

    public SpinnerAdapter(Context context, boolean _areBricks)
    {
        this.context=context;
        colors=new ArrayList<Integer>();
        int retrieve [];
        if(_areBricks)
            retrieve = context.getResources().getIntArray(R.array.bricksColors);
        else
            retrieve = context.getResources().getIntArray(R.array.backgroundColors);

        areBricks = _areBricks;

        for(int re:retrieve)
        {
            colors.add(re);
        }
    }
    @Override
    public int getCount()
    {
        return colors.size();
    }
    @Override
    public Object getItem(int arg0)
    {
        return colors.get(arg0);
    }
    @Override
    public long getItemId(int arg0)
    {
        return arg0;
    }
    @Override
    public View getView(int pos, View view, ViewGroup parent)
    {
        LayoutInflater inflater=LayoutInflater.from(context);
        view=inflater.inflate(android.R.layout.simple_spinner_dropdown_item, null);
        TextView txv=(TextView)view.findViewById(android.R.id.text1);
        txv.setBackgroundColor(colors.get(pos));
        txv.setTextSize(10f);
        return view;
    }
}
