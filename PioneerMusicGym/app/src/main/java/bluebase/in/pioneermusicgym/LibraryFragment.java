package bluebase.in.pioneermusicgym;

import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class LibraryFragment extends Fragment {
    Context context;

    ViewPager viewPager;
    PagerAdapter pagerAdapter;
    TabLayout libraryTabLayout;

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
        fragmentList.add(new CollectionsFragment());

        List<String> titlesList = new ArrayList<>();
        titlesList.add("SONGS");
        titlesList.add("ARTISTS");
        titlesList.add("COLLECTIONS");

        viewPager = view.findViewById(R.id.viewPager);
        pagerAdapter = new SectionsPagerAdapter(context, getFragmentManager(),fragmentList, titlesList);
        viewPager.setAdapter(pagerAdapter);
        libraryTabLayout = view.findViewById(R.id.libraryTabLayout);
        libraryTabLayout.setupWithViewPager(viewPager);
        CommonUtils.openTab = 1;

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        context = getContext();

        libraryTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch(tab.getPosition()){
                    case 0:
                        CommonUtils.openTab = 1;
                        System.out.println("Songs Fragment");
                        break;

                    case 1:
                        CommonUtils.openTab = 2;
                        System.out.println("Singers Fragment");
                        break;

                    case 2:
                        CommonUtils.openTab = 4;
                        System.out.println("Movies Fragment");

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
