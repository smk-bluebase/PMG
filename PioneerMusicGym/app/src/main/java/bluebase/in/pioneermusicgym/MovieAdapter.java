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

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> implements Filterable {
    private ArrayList<MovieItems> movieItemsArrayList;
    private ArrayList<MovieItems> movieItemsArrayListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {
        private int movieId;
        private TextView movieTitle;
        private TextView year;
        private TextView numberOfSongs;
        private RelativeLayout movieItemRelativeLayout;

        public MovieViewHolder (View itemView, final OnItemClickListener listener) {
            super(itemView);
            movieTitle = itemView.findViewById(R.id.movieTitle);
            year = itemView.findViewById(R.id.year);
            numberOfSongs = itemView.findViewById(R.id.numberOfSongs);
            movieItemRelativeLayout = itemView.findViewById(R.id.movieItemRelativeLayout);

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

    public MovieAdapter(ArrayList<MovieItems> movieItemsArrayList) {
        this.movieItemsArrayList = movieItemsArrayList;
        this.movieItemsArrayListFull = new ArrayList<>(movieItemsArrayList);
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        MovieAdapter.MovieViewHolder movieViewHolder = new MovieAdapter.MovieViewHolder(v, mlistener);
        return movieViewHolder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        MovieItems currentItem = movieItemsArrayList.get(position);
        holder.movieId = currentItem.getMovieId();
        holder.movieTitle.setText(currentItem.getMovieTitle());
        holder.year.setText(currentItem.getYear());

        if(currentItem.getNumberOfSongs() == 1) holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Song");
        else holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Songs");

        if (position % 2 == 0) {
            holder.movieItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.movieItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
    }

    @Override
    public int getItemCount() {
        return movieItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return movieFilter;
    }

    private Filter movieFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<MovieItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(movieItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (MovieItems item : movieItemsArrayListFull) {
                    if (item.getMovieTitle().toLowerCase().contains(filterPattern)) {
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
            movieItemsArrayList.clear();
            movieItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public ArrayList<MovieItems> getData() {
        return movieItemsArrayList;
    }

}
