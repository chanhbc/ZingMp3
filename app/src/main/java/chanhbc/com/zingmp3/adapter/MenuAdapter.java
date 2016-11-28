package chanhbc.com.zingmp3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import chanhbc.com.zingmp3.App;
import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.model.ItemListMusic;

public class MenuAdapter extends BaseAdapter {
    private ArrayList<ItemListMusic> musics;
    private LayoutInflater inflater;

    public MenuAdapter(ArrayList<ItemListMusic> musics) {
        inflater = LayoutInflater.from(App.getContext());
        this.musics = musics;
    }

    @Override
    public int getCount() {
        return musics.size();
    }

    @Override
    public ItemListMusic getItem(int position) {
        return musics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_list_music, parent, false);
            holder = new Holder();
            holder.ivIcon = (ImageView) convertView.findViewById(R.id.iv_icon);
            holder.tvName = (TextView) convertView.findViewById(R.id.tv_name);

            convertView.setTag(holder);
        }else {
            holder = (Holder)convertView.getTag();
        }
        ItemListMusic music = musics.get(position);
        holder.tvName.setText(music.getName());
        holder.ivIcon.setBackgroundResource(music.getIcon());
        return convertView;
    }

    private class Holder {
        private ImageView ivIcon;
        private TextView tvName;
    }
}
