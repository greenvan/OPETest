package tk.greenvan.opetest.ui.main;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import tk.greenvan.opetest.R;

/**
 * A [FragmentPagerAdapter] that returns a fragment corresponding to
 * one of the sections/tabs/pages.
 */
public class QuestionFragmentAdapter extends FragmentPagerAdapter {

    //TODO remove this lines
    // @StringRes
    //private static final int[] TAB_TITLES = new int[]{R.string.tab_text_1, R.string.tab_text_2};
    private final Context mContext;
    private List<QuestionFragment> mFragmentList;

    public QuestionFragmentAdapter(Context context, FragmentManager fm, List<QuestionFragment> fragmentList) {
        super(fm);
        mContext = context;
        mFragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        // getItem is called to instantiate the fragment for the given page.
        // Return a QuestionFragment (defined as a static inner class below).
        //return QuestionFragment.newInstance(position + 1);
        return mFragmentList.get(position);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        //TODO remove
        // return mContext.getResources().getString(TAB_TITLES[position]);
        return String.valueOf(mFragmentList.get(position).getArguments().getInt("question_id"));
        //return String.valueOf(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }
}