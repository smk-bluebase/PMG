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
    public static ViewPager viewPager3;
    PagerAdapter pagerAdapter3;
    TabLayout moviesTabLayout;

    public static boolean isLoaded = false;

    public static Context context;

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

        moviesTabLayout = view.findViewById(R.id.moviesTabLayout);
        viewPager3 = view.findViewById(R.id.moviesViewPager);

        pagerAdapter3 = new SectionsPagerAdapter(getChildFragmentManager(), fragmentList, titlesList);

        viewPager3.setOffscreenPageLimit(3);
        viewPager3.setAdapter(pagerAdapter3);
        moviesTabLayout.setupWithViewPager(viewPager3);

        moviesTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 4;
                        if(!CommonUtils.isSearching) TamilFragment.onOpen();
                        else LibraryFragment.searchView.setQuery(LibraryFragment.searchView.getQuery(), true);
                        break;

                    case 1:
                        CommonUtils.openTab = 5;
                        if(!CommonUtils.isSearching) MalayalamFragment.onOpen();
                        else LibraryFragment.searchView.setQuery(LibraryFragment.searchView.getQuery(), true);
                        break;

                    case 2:
                        CommonUtils.openTab = 6;
                        if(!CommonUtils.isSearching) HindiFragment.onOpen();
                        else LibraryFragment.searchView.setQuery(LibraryFragment.searchView.getQuery(), true);
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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        isLoaded = true;
    }

    public static void onOpen(){
        if(isLoaded) {
            CommonUtils.startDatabaseHelper(context);
            MoviesFragment.viewPager3.setCurrentItem(CommonUtils.dataBaseHelper.getLanguageCode());
            switch(CommonUtils.dataBaseHelper.getLanguageCode()){
                case 0:
                    TamilFragment.onOpen();
                    break;
                case 1:
                    MalayalamFragment.onOpen();
                    break;
                case 2:
                    HindiFragment.onOpen();
                    break;
                default:
                    TamilFragment.onOpen();
            }
            CommonUtils.closeDataBaseHelper();
        }
    }

}
