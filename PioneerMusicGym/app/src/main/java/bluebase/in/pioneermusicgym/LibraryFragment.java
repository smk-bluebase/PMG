package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LibraryFragment extends Fragment {
    Context context;

    ViewPager viewPager1;
    PagerAdapter pagerAdapter1;
    TabLayout libraryTabLayout;

    public static SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        height = (int) (height / 1.53);

        ImageView background = view.findViewById(R.id.background);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 200, height);
        background.setLayoutParams(layoutParams);

        List<Fragment> fragmentList = new ArrayList<>();
        fragmentList.add(new SongsFragment());
        fragmentList.add(new ArtistsFragment());
        fragmentList.add(new MoviesFragment());

        List<String> titlesList = new ArrayList<>();
        titlesList.add("SONGS");
        titlesList.add("ARTISTS");
        titlesList.add("MOVIES");

        viewPager1 = view.findViewById(R.id.viewPager);
        libraryTabLayout = view.findViewById(R.id.libraryTabLayout);
        pagerAdapter1 = new SectionsPagerAdapter(context, getFragmentManager(), fragmentList, titlesList);

        searchView = view.findViewById(R.id.searchLibrarySearchView);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        viewPager1.setAdapter(pagerAdapter1);
        libraryTabLayout.setupWithViewPager(viewPager1);
        CommonUtils.openTab = 1;

        searchView.setImeOptions(EditorInfo.IME_ACTION_DONE);

        libraryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 1;
                        searchView.setQuery("", false);
                        break;

                    case 1:
                        CommonUtils.openTab = 2;
                        searchView.setQuery("", false);
                        break;

                    case 2:
                        CommonUtils.openTab = 4;
                        searchView.setQuery("", false);
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

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                switch (CommonUtils.openTab){
                    case 1:
                        SongsFragment.onQuerySubmit(query);
                        break;
                    case 2:
                        SingersFragment.onQuerySubmit(query);
                        break;
                    case 3:
                        ComposersFragment.onQuerySubmit(query);
                        break;
                    case 4:
                        TamilFragment.onQuerySubmit(query);
                        break;
                    case 5:
                        MalayalamFragment.onQuerySubmit(query);
                        break;
                    case 6:
                        HindiFragment.onQuerySubmit(query);
                        break;

                    default:
                        //  Do Nothing!
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("CommonUtils.openTab : " + CommonUtils.openTab);

                switch (CommonUtils.openTab){
                    case 1:
                        SongsFragment.onQueryChange(newText);
                        break;
                    case 2:
                        SingersFragment.onQueryChange(newText);
                        break;
                    case 3:
                        ComposersFragment.onQueryChange(newText);
                        break;
                    case 4:
                        TamilFragment.onQueryChange(newText);
                        break;
                    case 5:
                        MalayalamFragment.onQueryChange(newText);
                        break;
                    case 6:
                        HindiFragment.onQueryChange(newText);
                        break;

                    default:
                        //  Do Nothing!
                }

                return false;
            }
        });

    }

}
