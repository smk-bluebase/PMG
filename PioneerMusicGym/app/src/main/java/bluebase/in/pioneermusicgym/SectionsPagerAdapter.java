package bluebase.in.pioneermusicgym;

import android.content.Context;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

public class SectionsPagerAdapter extends FragmentStatePagerAdapter {
    Context context;
    private List<Fragment> fragmentList;
    private List<String> titlesList;

    public SectionsPagerAdapter(Context context,  @NonNull FragmentManager fm, List<Fragment> fragmentList, List<String> titlesList) {
        super(fm);
        this.context = context;
        this.fragmentList = fragmentList;
        this.titlesList = titlesList;
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titlesList.get(position);
    }

}
