package bluebase.in.pioneermusicgym;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.HomeViewHolder> {
    private ArrayList<HomeItems> homeItemsArrayList;

    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView subMenu1;
        private TextView subMenu2;
        private RelativeLayout homeItemRelativeLayout;

        public HomeViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            subMenu1 = itemView.findViewById(R.id.subMenu1);
            subMenu2 = itemView.findViewById(R.id.subMenu2);
            homeItemRelativeLayout = itemView.findViewById(R.id.homeItemRelativeLayout);
        }

    }

    public HomeAdapter(ArrayList<HomeItems> homeItemsArrayList) {
        this.homeItemsArrayList = homeItemsArrayList;
    }

    @Override
    public HomeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_item, parent, false);
        HomeViewHolder homeViewHolder = new HomeViewHolder(v);
        return homeViewHolder;
    }

    @Override
    public void onBindViewHolder(HomeViewHolder holder, int position) {
        HomeItems currentItem = homeItemsArrayList.get(position);
        holder.title.setText(currentItem.getTitle());
        holder.subMenu1.setText(currentItem.getSubMenu1());
        holder.subMenu2.setText(currentItem.getSubmenu2());
        holder.homeItemRelativeLayout.setBackgroundColor(currentItem.getBackgroundColor());
    }

    @Override
    public int getItemCount() {
        return homeItemsArrayList.size();
    }

}
