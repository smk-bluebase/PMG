package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class CollectionsFragment extends Fragment {
    Context context;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    TabLayout collectionsTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_collections, container, false);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new MoviesFragment());
        fragmentList.add(new AlbumsFragment());

        List<String> titlesList = new ArrayList<>();
        titlesList.add("MOVIES");
        titlesList.add("ALBUMS");

        viewPager = view.findViewById(R.id.collectionsViewPager);
        pagerAdapter = new SectionsPagerAdapter(context, getFragmentManager(),fragmentList, titlesList);
        viewPager.setAdapter(pagerAdapter);
        collectionsTabLayout = view.findViewById(R.id.collectionsTabLayout);
        collectionsTabLayout.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        collectionsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 4;
                        System.out.println("Movies Fragment");
                        break;

                    case 1:
                        CommonUtils.openTab = 5;
                        System.out.println("Albums Fragment");
                        break;

                    default:
                        // Do Nothing!
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do Nothing!
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do Nothing!
            }
        });

    }

}
