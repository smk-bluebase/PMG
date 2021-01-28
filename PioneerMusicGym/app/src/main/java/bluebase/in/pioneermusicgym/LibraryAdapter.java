package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class LibraryAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<LibraryItems> items;

    public LibraryAdapter(Context context, ArrayList<LibraryItems> items){
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.song_list, parent, false);
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            convertView.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

        LibraryItems currentItem = (LibraryItems) getItem(position);

        TextView title = convertView.findViewById(R.id.title);
        TextView artistName = convertView.findViewById(R.id.artistName);
        TextView composerName = convertView.findViewById(R.id.composerName);
        TextView albumName = convertView.findViewById(R.id.albumName);
        TextView movieName = convertView.findViewById(R.id.movieName);
        TextView duration = convertView.findViewById(R.id.duration);

        title.setText(currentItem.getSongTitle());

        if(!currentItem.getArtistName().equals("")) artistName.setText(currentItem.getArtistName() + ",");
        else artistName.setVisibility(View.GONE);

        if(!currentItem.getComposerName().equals("") && !currentItem.getComposerName().equals(currentItem.getArtistName())) composerName.setText(currentItem.getComposerName() + ",");
        else composerName.setVisibility(View.GONE);

        if(!currentItem.getAlbumName().equals("")) albumName.setText(currentItem.getAlbumName());
        else albumName.setVisibility(View.GONE);

        if(!currentItem.getMovieName().equals("")) movieName.setText(currentItem.getMovieName());
        else movieName.setVisibility(View.GONE);

        duration.setText(currentItem.getDuration());

        return convertView;
    }
}
