package bluebase.in.pioneermusicgym;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.recyclerview.widget.RecyclerView;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder> implements Filterable {
    private ArrayList<PlaylistItems> playlistItemsArrayList;
    private ArrayList<PlaylistItems> playlistItemsArrayListFull;

    public static class PlaylistViewHolder extends RecyclerView.ViewHolder {
        private int playlistId;
        private TextView playlistTitle;
        private TextView createdOn;
        private TextView numberOfSongs;
        private RelativeLayout playlistItemRelativeLayout;

        public PlaylistViewHolder(View itemView) {
            super(itemView);
            playlistTitle = itemView.findViewById(R.id.playlistTitle);
            createdOn = itemView.findViewById(R.id.createdOn);
            numberOfSongs = itemView.findViewById(R.id.numberOfSongs);
            playlistItemRelativeLayout = itemView.findViewById(R.id.playlistItemRelativeLayout);
        }

    }

    public PlaylistAdapter(ArrayList<PlaylistItems> playlistItemsArrayList) {
        this.playlistItemsArrayList = playlistItemsArrayList;
        this.playlistItemsArrayListFull = new ArrayList<>(playlistItemsArrayList);
    }

    @Override
    public PlaylistViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, parent, false);
        PlaylistViewHolder playlistViewHolder = new PlaylistViewHolder(v);
        return playlistViewHolder;
    }

    @Override
    public void onBindViewHolder(PlaylistViewHolder holder, int position) {
        PlaylistItems currentItem = playlistItemsArrayList.get(position);
        holder.playlistId = currentItem.getPlaylistId();
        holder.playlistTitle.setText(currentItem.getPlaylistTitle());

        try{
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(currentItem.getCreatedOn());
            holder.createdOn.setText(new SimpleDateFormat("dd, MMMM, yyyy", Locale.ENGLISH).format(date));
        }catch(ParseException e){
            e.printStackTrace();
        }

        holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Songs");

        if (position % 2 == 0) {
            holder.playlistItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.playlistItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
    }

    @Override
    public int getItemCount() {
        return playlistItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return playlistFilter;
    }

    private Filter playlistFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<PlaylistItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(playlistItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (PlaylistItems item : playlistItemsArrayListFull) {
                    if (item.getPlaylistTitle().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            playlistItemsArrayList.clear();
            playlistItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}