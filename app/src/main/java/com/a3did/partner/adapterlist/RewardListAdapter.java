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
 * Created by Joonhyun on 2016-11-19.
 */
public class RewardListAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<RewardListData> listViewItemList = new ArrayList<RewardListData>() ;
    public void setList(ArrayList<RewardListData> list){
        listViewItemList = list;
    }
    // ListViewAdapter의 생성자
    public RewardListAdapter() {

    }

    public ArrayList<RewardListData> getList(){ return listViewItemList;}

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_row_reward, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.imageView_reward) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.textView_reward) ;
        TextView starNumTextView = (TextView) convertView.findViewById(R.id.text_starnum_reward) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        RewardListData listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        iconImageView.setImageDrawable(listViewItem.getIcon());
        titleTextView.setText(listViewItem.getTitle());
        starNumTextView.setText(listViewItem.getStarNum() +"");

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Drawable icon, String title, int starNum) {
        RewardListData item = new RewardListData();

        item.setIcon(icon);
        item.setTitle(title);
        item.setStarNum(starNum);

        listViewItemList.add(item);
    }
}
