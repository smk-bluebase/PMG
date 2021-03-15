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

public class ComposerAdapter extends RecyclerView.Adapter<ComposerAdapter.ComposerViewHolder> implements Filterable {
    private ArrayList<ComposerItems> composerItemsArrayList;
    private ArrayList<ComposerItems> composerItemsArrayListFull;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    public static class ComposerViewHolder extends RecyclerView.ViewHolder {
        private int composerId;
        private TextView composerTitle;
        private TextView numberOfSongs;
        private TextView numberOfMovies;
        private RelativeLayout composerItemRelativeLayout;

        public ComposerViewHolder (View itemView, final OnItemClickListener listener) {
            super(itemView);
            composerTitle = itemView.findViewById(R.id.artistTitle);
            numberOfSongs = itemView.findViewById(R.id.numberOfSongs);
            numberOfMovies = itemView.findViewById(R.id.numberOfMovies);
            composerItemRelativeLayout = itemView.findViewById(R.id.artistItemRelativeLayout);

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

    public ComposerAdapter(ArrayList<ComposerItems> composerItemsArrayList) {
        this.composerItemsArrayList = composerItemsArrayList;
        this.composerItemsArrayListFull = new ArrayList<>(composerItemsArrayList);
    }

    @Override
    public ComposerAdapter.ComposerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.artist_item, parent, false);
        ComposerAdapter.ComposerViewHolder composerViewHolder = new ComposerAdapter.ComposerViewHolder(v, mlistener);
        return composerViewHolder;
    }

    @Override
    public void onBindViewHolder(ComposerAdapter.ComposerViewHolder holder, int position) {
        ComposerItems currentItem = composerItemsArrayList.get(position);
        holder.composerId = currentItem.getComposerId();
        holder.composerTitle.setText(currentItem.getComposerTitle());

        if(currentItem.getNumberOfSongs() == 1) holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Song");
        else holder.numberOfSongs.setText(currentItem.getNumberOfSongs() + " Songs");

        if(currentItem.getNumberOfMovies() == 1) holder.numberOfMovies.setText(currentItem.getNumberOfMovies() + " Movie");
        else holder.numberOfMovies.setText(currentItem.getNumberOfMovies() + " Movies");

        if (position % 2 == 0) {
            holder.composerItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.composerItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
    }

    @Override
    public int getItemCount() {
        return composerItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return composerFilter;
    }

    private Filter composerFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ComposerItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(composerItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ComposerItems item : composerItemsArrayListFull) {
                    if (item.getComposerTitle().toLowerCase().contains(filterPattern)) {
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
            composerItemsArrayList.clear();
            composerItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public ArrayList<ComposerItems> getData() {
        return composerItemsArrayList;
    }

}
