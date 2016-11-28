package chanhbc.com.zingmp3.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import chanhbc.com.zingmp3.App;
import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.model.ItemAlbum;

public class AlbumAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private ArrayList<ItemAlbum> ablums;

    public AlbumAdapter(ArrayList<ItemAlbum> ablums) {
        this.ablums = ablums;
        inflater = LayoutInflater.from(App.getContext());
    }

    public void addItem(ItemAlbum itemAblum) {
        ablums.add(itemAblum);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ablums.size();
    }

    @Override
    public ItemAlbum getItem(int i) {
        return ablums.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Holder holder;
        if (view == null) {
            view = inflater.inflate(R.layout.item_album, viewGroup, false);
            holder = new Holder();
            holder.ivCover = (ImageView) view.findViewById(R.id.iv_album_cover);
            holder.tvTitle = (TextView) view.findViewById(R.id.tv_album_title);
            holder.tvArtist = (TextView) view.findViewById(R.id.tv_album_artist);
            view.setTag(holder);
        } else {
            holder = (Holder) view.getTag();
        }
        ItemAlbum itemAlbum = ablums.get(i);
        Glide.with(viewGroup.getContext()).load(itemAlbum.getCover()).into(holder.ivCover);
        holder.tvTitle.setText(itemAlbum.getTitle());
        holder.tvArtist.setText(itemAlbum.getArtist());
        return view;
    }

    private class Holder {
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
    }
}
