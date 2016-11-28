package chanhbc.com.zingmp3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import chanhbc.com.zingmp3.App;
import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.model.ItemSong;

public class SongAdapter extends BaseAdapter{
    private ArrayList<ItemSong> itemSongs;
    private LayoutInflater inflater;

    public SongAdapter(ArrayList<ItemSong> itemSongs){
        this.itemSongs = itemSongs;
        inflater = LayoutInflater.from(App.getContext());
    }

    @Override
    public int getCount() {
        return itemSongs.size();
    }

    @Override
    public ItemSong getItem(int position) {
        return itemSongs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            convertView = inflater.inflate(R.layout.item_song, parent, false);
            holder = new Holder();
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tv_song_title);
            holder.tvArtist = (TextView) convertView.findViewById(R.id.tv_song_artist);
            convertView.setTag(holder);
        }else {
            holder = (Holder) convertView.getTag();
        }
        ItemSong itemSong = itemSongs.get(position);
        holder.tvTitle.setText(itemSong.getTitle());
        holder.tvArtist.setText(itemSong.getArtist());
        return convertView;
    }

    private class Holder{
        private TextView tvTitle;
        private TextView tvArtist;
    }
}
