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

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> implements Filterable {
    private ArrayList<FavouriteItems> favouriteItemsArrayList;
    private ArrayList<FavouriteItems> favouriteItemsArrayListFull;

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        private int songId;
        private TextView songTitle;
        private TextView subMenu;
        private TextView duration;
        private RelativeLayout favouriteItemRelativeLayout;

        public FavouriteViewHolder(View itemView) {
            super(itemView);
            songTitle = itemView.findViewById(R.id.songTitle);
            subMenu = itemView.findViewById(R.id.subMenu);
            duration = itemView.findViewById(R.id.duration);
            favouriteItemRelativeLayout = itemView.findViewById(R.id.favouriteItemRelativeLayout);
        }

    }

    public FavouriteAdapter(ArrayList<FavouriteItems> favouriteItemsArrayList) {
        this.favouriteItemsArrayList = favouriteItemsArrayList;
        this.favouriteItemsArrayListFull = new ArrayList<>(favouriteItemsArrayList);
    }

    @Override
    public FavouriteAdapter.FavouriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_item, parent, false);
        FavouriteAdapter.FavouriteViewHolder favouriteViewHolder = new FavouriteAdapter.FavouriteViewHolder(v);
        return favouriteViewHolder;
    }

    @Override
    public void onBindViewHolder(FavouriteAdapter.FavouriteViewHolder holder, int position) {
        FavouriteItems currentItem = favouriteItemsArrayList.get(position);
        holder.songId = currentItem.getSongId();
        holder.songTitle.setText(currentItem.getSongTitle());

        String subMenu = "";

        if(!currentItem.getAlbumName().equals("")){
            if(!currentItem.getAlbumSinger().equals("")) subMenu = currentItem.getAlbumSinger() + ", ";
            subMenu += currentItem.getAlbumName() + ", ";
            if(!currentItem.getYear().equals("")) subMenu += currentItem.getYear();
        }else if(!currentItem.getMovieName().equals("")){
            if(!currentItem.getMovieSinger().equals("")) subMenu = currentItem.getMovieSinger() + ", ";
            subMenu += currentItem.getMovieName() + ", ";
            if(!currentItem.getYear().equals("")) subMenu += currentItem.getYear();
        }

        holder.subMenu.setText(subMenu);

        holder.duration.setText(currentItem.getDuration());

        if (position % 2 == 0) {
            holder.favouriteItemRelativeLayout.setBackgroundColor(Color.parseColor("#F5F5F5"));
        }else{
            holder.favouriteItemRelativeLayout.setBackgroundColor(Color.parseColor("#D3D3D3"));
        }
    }

    @Override
    public int getItemCount() {
        return favouriteItemsArrayList.size();
    }

    @Override
    public Filter getFilter() {
        return favouriteFilter;
    }

    private Filter favouriteFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<FavouriteItems> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(favouriteItemsArrayListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (FavouriteItems item : favouriteItemsArrayListFull) {
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
            favouriteItemsArrayList.clear();
            favouriteItemsArrayList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

}
