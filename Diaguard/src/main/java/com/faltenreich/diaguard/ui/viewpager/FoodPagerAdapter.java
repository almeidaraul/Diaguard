package com.faltenreich.diaguard.ui.viewpager;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.faltenreich.diaguard.data.entity.Food;
import com.faltenreich.diaguard.ui.fragment.BaseFoodFragment;
import com.faltenreich.diaguard.ui.fragment.FoodDetailFragment;
import com.faltenreich.diaguard.ui.fragment.FoodHistoryFragment;
import com.faltenreich.diaguard.ui.fragment.NutrientsFragment;
import com.faltenreich.diaguard.ui.view.ToolbarBehavior;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Faltenreich on 27.10.2016.
 */

public class FoodPagerAdapter extends FragmentStatePagerAdapter {

    private List<BaseFoodFragment> fragments;

    public FoodPagerAdapter(FragmentManager fragmentManager, Food food) {
        super(fragmentManager);
        initWithFood(food);
    }

    private void initWithFood(Food food) {
        Bundle bundle = new Bundle();
        bundle.putLong(BaseFoodFragment.EXTRA_FOOD_ID, food.getId());

        fragments = new ArrayList<>();

        FoodDetailFragment detailFragment = new FoodDetailFragment();
        detailFragment.setArguments(bundle);
        fragments.add(detailFragment);

        NutrientsFragment nutrientsFragment = new NutrientsFragment();
        nutrientsFragment.setArguments(bundle);
        fragments.add(nutrientsFragment);

        FoodHistoryFragment foodHistoryFragment = new FoodHistoryFragment();
        foodHistoryFragment.setArguments(bundle);
        fragments.add(foodHistoryFragment);
    }

    @Override
    public Fragment getItem(int position) {
        return position < fragments.size() ? fragments.get(position) : null;
    }

    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = getItem(position);
        return fragment instanceof ToolbarBehavior ? ((ToolbarBehavior) fragment).getTitle() : fragment.toString();
    }
}