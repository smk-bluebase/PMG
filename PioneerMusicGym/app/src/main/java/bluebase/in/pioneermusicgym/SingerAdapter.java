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

public class SingerAdapter extends RecyclerView.Adapter<SingerAdapter.SingerViewHolder> implements Filterable {
    private ArrayList<SingerItems> singerItemsArrayList;
    private ArrayList<SingerItems> singerItemsArrayListFull;

    public static class SingerViewHolder extends RecyclerView.ViewHolder {
        private int singerId;
        private TextView singerTitle;
        private TextView numberOfSongs;
        private TextView numberOfMovies;
        private RelativeLayout singerItemRelativeLayout;

        public SingerViewHolder (View itemView) {
            super(itemView);
            singerTitle = itemView.findViewById(R.id.artistTitle);
            numberOfSongs = itemView.findViewById(R.id.numberOfSongs);
            numberOfMovies = itemView.findViewById(R.id.numberOfMovies);
            singerItemRelativeLayout = itemView.findViewById(R.id.artistItemRelativeLayout);
        }

    }

    public SingerAdapter(ArrayList<SingerItems> singerItemsArrayList) {
        this.singerItemsArrayList = singerItemsArrayList;
        this.singerItemsArrayListFull = new ArrayList<>(singerItemsArrayList);
    }

    @Override
    public SingerAdapter.SingerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        SingerAdapter.SingerViewHolder singerViewHolder = new SingerAdapter.SingerViewHolder(v);
        return singerViewHolder;
    }

    @Override
    public void onBindViewHolder(SingerAdapter.SingerViewHolder holder, int position) {
        SingerItems currentItem = singerItemsArrayList.get(position);
        holder.singerId = currentItem.getSingerId();
        holder.singerTitle.setText(currentItem.getSingerTitle());

        if(currentItem.getNumberOfSongs() == 1) holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Song");
        else holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Songs");

        if(currentItem.getNumberOfMovies() == 1) holder.numberOfMovies.setText(currentItem.getNumberOfMovies() + " Movie");
        else holder.numberOfMovies.setText(currentItem.getNumberOfMovies() + " Movies");

        if (position % 2 == 0) {
            holder.singerItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.singerItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
    }

    @Override
    public int getItemCount() {
        return singerItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return singerFilter;
    }

    private Filter singerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SingerItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(singerItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SingerItems item : singerItemsArrayListFull) {
                    if (item.getSingerTitle().toLowerCase().contains(filterPattern)) {
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
            singerItemsArrayList.clear();
            singerItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
