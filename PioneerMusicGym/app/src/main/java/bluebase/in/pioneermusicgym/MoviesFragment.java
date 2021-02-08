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

public class MoviesFragment extends Fragment {
    Context context;

    ViewPager viewPager3;
    PagerAdapter pagerAdapter3;
    TabLayout collectionsTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movies, container, false);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new TamilFragment());
        fragmentList.add(new MalayalamFragment());
        fragmentList.add(new HindiFragment());

        List<String> titlesList = new ArrayList<>();
        titlesList.add("TAMIL");
        titlesList.add("MALAYALAM");
        titlesList.add("HINDI");

        viewPager3 = view.findViewById(R.id.moviesViewPager);
        pagerAdapter3 = new SectionsPagerAdapter(context, getFragmentManager(), fragmentList, titlesList);
        collectionsTabLayout = view.findViewById(R.id.moviesTabLayout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        viewPager3.setAdapter(pagerAdapter3);
        collectionsTabLayout.setupWithViewPager(viewPager3);

        collectionsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 4;
                        LibraryFragment.searchView.setQuery("", false);
                        break;

                    case 1:
                        CommonUtils.openTab = 5;
                        LibraryFragment.searchView.setQuery("", false);
                        break;

                    case 2:
                        CommonUtils.openTab = 6;
                        LibraryFragment.searchView.setQuery("", false);
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
