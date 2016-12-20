package chanhbc.com.zingmp3.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;

import java.util.ArrayList;

import chanhbc.com.zingmp3.R;
import chanhbc.com.zingmp3.adapter.ItemAlbumAdapter;
import chanhbc.com.zingmp3.manager.AlbumManager;
import chanhbc.com.zingmp3.model.ItemAlbum;
import chanhbc.com.zingmp3.model.StaticFinal;

public class ListAlbumFragment extends Fragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
    private ArrayList<ItemAlbum> albumsAdd;
    private ArrayList<ItemAlbum> albums;
    private ItemAlbumAdapter itemAlbumAdapter;
    private AlbumManager albumManager;
    private GridView gvAlbum;
    private Handler handler;
    private int scrollStale = 15;
    private OnLoadItemSongListener onLoadItemSongListener;
    private int type;

    public ListAlbumFragment(int type) {
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        albumManager = new AlbumManager();
        albums = new ArrayList<>();
        scrollStale = 15;
        albumManager.getItemAlbums(15, 0, type, new AlbumManager.OnGetAlbumOnlineListener() {
            @Override
            public void completed(ArrayList<ItemAlbum> itemAlbums) {
                for (int i = 0; i < itemAlbums.size(); i++) {
                    albums.add(itemAlbums.get(i));
                }
                itemAlbumAdapter = new ItemAlbumAdapter(albums);
                Message message = new Message();
                message.what = StaticFinal.UPDATE_GRIDVIEW;
                handler.sendMessage(message);
            }

            @Override
            public void error(Exception e) {

            }
        });
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case StaticFinal.UPDATE_GRIDVIEW:
                        gvAlbum.setAdapter(itemAlbumAdapter);
                        if (scrollStale - 20 < 0) {
                            gvAlbum.setSelection(scrollStale - 15);
                        } else {
                            gvAlbum.setSelection(scrollStale - 20);
                        }
                        gvAlbum.setOnItemClickListener(ListAlbumFragment.this);
                        gvAlbum.setOnScrollListener(ListAlbumFragment.this);
                        break;

                    case StaticFinal.ADD_ITEM_GRIDVIEW:
                        for (int i = 0; i < albumsAdd.size(); i++) {
                            itemAlbumAdapter.addItem(albumsAdd.get(i));
                        }
                        break;

                    default:
                        break;
                }
            }
        };
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_album, container, false);
        gvAlbum = (GridView) view.findViewById(R.id.gv_album);
        return view;
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int i) {
    }

    @Override
    public void onScroll(AbsListView absListView, int i, int i1, int i2) {
        if (scrollStale - i < 7) {
            albumManager.getItemAlbums(15, scrollStale, type, new AlbumManager.OnGetAlbumOnlineListener() {
                @Override
                public void completed(ArrayList<ItemAlbum> itemAlbums) {
                    albumsAdd = new ArrayList<>();
                    for (int i = 0; i < itemAlbums.size(); i++) {
                        albumsAdd.add(itemAlbums.get(i));
                    }
                    Message message = new Message();
                    message.what = StaticFinal.ADD_ITEM_GRIDVIEW;
                    handler.sendMessage(message);
                }

                @Override
                public void error(Exception e) {

                }
            });
            scrollStale += 15;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.gv_album:
                ItemAlbum itemAlbum = itemAlbumAdapter.getItem(position);
                onLoadItemSongListener.onSongLoad(itemAlbum);
                break;

            default:
                break;
        }
    }

    public void setOnLoadItemSongListener(OnLoadItemSongListener listener) {
        this.onLoadItemSongListener = listener;
    }

    public interface OnLoadItemSongListener {
        void onSongLoad(ItemAlbum itemAlbum);
    }

}
