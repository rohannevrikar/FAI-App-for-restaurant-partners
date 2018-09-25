package tastifai.restaurant.Adapters;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static tastifai.restaurant.Activities.MainActivity.currentCount;
import static tastifai.restaurant.Activities.MainActivity.deliveryCount;
import static tastifai.restaurant.Activities.MainActivity.progressCount;


public class FragmentAdapter extends FragmentPagerAdapter {
    public static List<Fragment> mFragments = new ArrayList<>();
    public static List<String> mFragmentTitles = new ArrayList<>();

    public FragmentAdapter(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {

        mFragments.add(fragment);
        mFragmentTitles.add(title);
    }
    public void changeFragmentTitle(int pos, String title){
        if(mFragmentTitles.size()!=0)
        mFragmentTitles.set(pos, title);
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        Log.d("FragmentAdapter", "getItem: " + mFragmentTitles.get(position));

        return mFragments.get(position);
    }


    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0: return "ORDER" + "(" + currentCount+ ")";
            case 1: return "PROGRESS(" + progressCount + ")";
            case 2: return "DELIVERY(" + deliveryCount + ")";
        }
        return "";
    }
}