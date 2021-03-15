package bluebase.in.pioneermusicgym;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.SongViewHolder> implements Filterable {
    private ArrayList<SongItems> songItemsArrayList;
    private ArrayList<SongItems> songItemsArrayListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class SongViewHolder extends RecyclerView.ViewHolder {
        private int songId;
        private TextView songTitle;
        private TextView subMenu;
        private TextView duration;
        private RelativeLayout songItemRelativeLayout;

        public SongViewHolder (View itemView, final OnItemClickListener listener) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            subMenu = itemView.findViewById(R.id.subMenu);
            duration = itemView.findViewById(R.id.duration);
            songItemRelativeLayout = itemView.findViewById(R.id.songItemRelativeLayout);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            });
        }

    }

    public SongAdapter(ArrayList<SongItems> songItemsArrayList) {
        this.songItemsArrayList = songItemsArrayList;
        this.songItemsArrayListFull = new ArrayList<>(songItemsArrayList);
    }

    @Override
    public SongAdapter.SongViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.song_item, parent, false);
        SongAdapter.SongViewHolder songViewHolder = new SongAdapter.SongViewHolder(v, mlistener);
        return songViewHolder;
    }

    @Override
    public void onBindViewHolder(SongAdapter.SongViewHolder holder, int position) {
        SongItems currentItem = songItemsArrayList.get(position);
        holder.songId = currentItem.getSongId();
        holder.songTitle.setText(currentItem.getSongTitle());

        String subMenu = "";

        if(!currentItem.getMovieName().equals("")){
            if(!currentItem.getMovieSinger().equals("")) subMenu = currentItem.getMovieSinger() + ", ";
            subMenu += currentItem.getMovieName() + ", ";
            if(!currentItem.getYear().equals("")) subMenu += currentItem.getYear();
        }

        holder.subMenu.setText(subMenu);

        holder.duration.setText(currentItem.getDuration());

        if (position % 2 == 0) {
            holder.songItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.songItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }

    }

    @Override
    public int getItemCount() {
        return songItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return songFilter;
    }

    private Filter songFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SongItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(songItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SongItems item : songItemsArrayListFull) {
                    if (item.getSongTitle().toLowerCase().contains(filterPattern)) {
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
            songItemsArrayList.clear();
            songItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public ArrayList<SongItems> getData() {
        return songItemsArrayList;
    }

    public int getPosition(SongItems item){
        return songItemsArrayListFull.indexOf(item);
    }

}
