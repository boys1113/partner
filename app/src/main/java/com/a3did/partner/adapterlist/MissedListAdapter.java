package com.a3did.partner.adapterlist;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.a3did.partner.partner.R;

import java.util.ArrayList;

/**
 * Created by edward on 11/26/2016.
 */
public class MissedListAdapter  extends BaseAdapter{


    private ArrayList<MissedListData> listViewItemList = new ArrayList<>();
    public void setList(ArrayList<MissedListData> list){
        listViewItemList = list;
    }
    public MissedListAdapter(){

    }

    @Override
    public int getCount() {
        return listViewItemList.size();
    }

    @Override
    public Object getItem(int i) {
        return listViewItemList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_assistant, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView1) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView1) ;
        //TextView descTextView = (TextView) convertView.findViewById(R.id.textView2) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조
        MissedListData listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        //descTextView.setText(listViewItem.getDate());

        return convertView;
    }

    public void addItem(Drawable icon, String title) {
        MissedListData item = new MissedListData();

        item.setIcon(icon);
        item.setTitle(title);

        listViewItemList.add(item);
    }
}
