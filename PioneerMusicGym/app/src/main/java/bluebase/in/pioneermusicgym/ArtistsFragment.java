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

public class ArtistsFragment extends Fragment {
    Context context;

    ViewPager viewPager2;
    PagerAdapter pagerAdapter2;
    TabLayout artistsTabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_artists, container, false);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SingersFragment());
        fragmentList.add(new ComposersFragment());

        List<String> titlesList = new ArrayList<>();
        titlesList.add("SINGERS");
        titlesList.add("COMPOSERS");

        viewPager2 = view.findViewById(R.id.artistsViewPager);
        pagerAdapter2 = new SectionsPagerAdapter(context, getFragmentManager(),fragmentList, titlesList);
        artistsTabLayout = view.findViewById(R.id.artistsTabLayout);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        viewPager2.setAdapter(pagerAdapter2);
        artistsTabLayout.setupWithViewPager(viewPager2);

        artistsTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 2;
                        LibraryFragment.searchView.setQuery("", false);
                        break;

                    case 1:
                        CommonUtils.openTab = 3;
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
