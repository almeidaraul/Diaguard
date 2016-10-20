package com.faltenreich.diaguard.ui.view;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.faltenreich.diaguard.R;
import com.faltenreich.diaguard.adapter.FoodEditableAdapter;
import com.faltenreich.diaguard.adapter.SimpleDividerItemDecoration;
import com.faltenreich.diaguard.data.PreferenceHelper;
import com.faltenreich.diaguard.data.entity.Food;
import com.faltenreich.diaguard.data.entity.FoodEaten;
import com.faltenreich.diaguard.data.entity.Meal;
import com.faltenreich.diaguard.data.entity.Measurement;
import com.faltenreich.diaguard.event.Events;
import com.faltenreich.diaguard.event.ui.FoodEatenRemovedEvent;
import com.faltenreich.diaguard.event.ui.FoodEatenUpdatedEvent;
import com.faltenreich.diaguard.event.ui.FoodSelectedEvent;
import com.faltenreich.diaguard.ui.activity.FoodSearchActivity;
import com.faltenreich.diaguard.ui.fragment.FoodSearchFragment;
import com.faltenreich.diaguard.util.NumberUtils;
import com.faltenreich.diaguard.util.StringUtils;
import com.faltenreich.diaguard.util.SystemUtils;
import com.j256.ormlite.dao.ForeignCollection;
import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Faltenreich on 13.10.2016.
 */

public class FoodListView extends LinearLayout {

    private static final int ANIMATION_DURATION_IN_MILLIS = 750;

    @BindView(R.id.food_list_icon) ImageView icon;
    @BindView(R.id.food_list_label) TextView label;
    @BindView(R.id.food_list_value_input) EditText valueInput;
    @BindView(R.id.food_list_value_calculated_integral) TickerView valueCalculatedIntegral;
    @BindView(R.id.food_list_value_calculated_point) TextView valueCalculatedPoint;
    @BindView(R.id.food_list_value_calculated_fractional) TickerView valueCalculatedFractional;
    @BindView(R.id.food_list_value_sign) TextView valueSign;
    @BindView(R.id.food_list_separator) View separator;
    @BindView(R.id.food_list) RecyclerView foodList;

    private FoodEditableAdapter adapter;

    private boolean showLabelIcon;
    private Meal meal;

    public FoodListView(Context context) {
        super(context);
        init();
    }

    public FoodListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public FoodListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Events.register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        Events.unregister(this);
    }

    private void init(AttributeSet attributeSet) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attributeSet, R.styleable.FoodListView);
        try {
            showLabelIcon = typedArray.getBoolean(R.styleable.FoodListView_showLabelIcon, false);
        } finally {
            typedArray.recycle();
        }
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_food_list, this);
        ButterKnife.bind(this);

        meal = new Meal();

        int visibility = showLabelIcon ? VISIBLE : GONE;
        label.setVisibility(visibility);
        icon.setVisibility(visibility);

        valueInput.setHint(PreferenceHelper.getInstance().getUnitAcronym(Measurement.Category.MEAL));
        valueCalculatedIntegral.setCharacterList(TickerUtils.getDefaultNumberList());
        valueCalculatedFractional.setCharacterList(TickerUtils.getDefaultNumberList());
        valueCalculatedIntegral.setAnimationDuration(ANIMATION_DURATION_IN_MILLIS);
        valueCalculatedFractional.setAnimationDuration(ANIMATION_DURATION_IN_MILLIS);

        adapter = new FoodEditableAdapter(getContext());
        foodList.setLayoutManager(new LinearLayoutManager(getContext()));
        foodList.addItemDecoration(new SimpleDividerItemDecoration(getContext()));
        foodList.setAdapter(adapter);

        update();
    }

    public void setupWithMeal(Meal meal) {
        this.meal = meal;
        this.valueInput.setText(meal.getValuesForUI()[0]);
        addItems(meal.getFoodEaten());
        update();
    }

    public boolean isValid() {
        boolean isValid = true;

        String input = valueInput.getText().toString().trim();

        if (StringUtils.isBlank(input) && adapter.getTotalCarbohydrates() == 0) {
            valueInput.setError(getContext().getString(R.string.validator_value_empty));
            isValid = false;
        } else {
            if (!StringUtils.isBlank(input)) {
                isValid = PreferenceHelper.isValueValid(valueInput, Measurement.Category.MEAL);
            }
        }
        return isValid;
    }

    public Meal getMeal() {
        if (isValid()) {
            meal.setValues(valueInput.getText().toString().length() > 0 ?
                    PreferenceHelper.getInstance().formatCustomToDefaultUnit(
                            meal.getCategory(),
                            NumberUtils.parseNumber(valueInput.getText().toString())) : 0);
            meal.setFoodEatenCache(adapter.getItems());
            return meal;
        } else {
            return null;
        }
    }

    private void update() {
        boolean hasFood = adapter.getItemCount() > 0;
        separator.setVisibility(hasFood ? VISIBLE : GONE);

        float newValue = PreferenceHelper.getInstance().formatDefaultToCustomUnit(Measurement.Category.MEAL, adapter.getTotalCarbohydrates());
        boolean hasFoodEaten = newValue > 0;
        valueCalculatedIntegral.setVisibility(hasFoodEaten ? VISIBLE : GONE);
        valueCalculatedPoint.setVisibility(hasFoodEaten ? VISIBLE : GONE);
        valueCalculatedPoint.setText(SystemUtils.getDecimalSeparator());
        valueCalculatedFractional.setVisibility(hasFoodEaten ? VISIBLE : GONE);
        valueSign.setVisibility(hasFoodEaten ? VISIBLE : GONE);

        float totalCarbohydrates = adapter.getTotalCarbohydrates();
        float totalMeal = PreferenceHelper.getInstance().formatDefaultToCustomUnit(Measurement.Category.MEAL, totalCarbohydrates);

        int integral = (int) totalMeal;
        int fractional = Math.round((totalMeal - integral) * 100);

        valueCalculatedIntegral.setText(String.valueOf(integral), true);
        valueCalculatedFractional.setText(String.valueOf(fractional), true);
    }

    public List<FoodEaten> getItems() {
        return adapter.getItems();
    }

    public void addItem(FoodEaten foodEaten) {
        adapter.addItem(foodEaten);
        adapter.notifyItemInserted(this.adapter.getItemCount() - 1);
        update();
    }

    public void addItem(Food food) {
        FoodEaten foodEaten = new FoodEaten();
        foodEaten.setFood(food);
        foodEaten.setMeal(meal);
        addItem(foodEaten);
    }

    public void addItems(ForeignCollection<FoodEaten> foodEatenList) {
        int oldCount = adapter.getItemCount();
        for (FoodEaten foodEaten : foodEatenList) {
            adapter.addItem(foodEaten);
        }
        adapter.notifyItemRangeInserted(oldCount, adapter.getItemCount());
        update();
    }

    public void removeItem(int position) {
        adapter.removeItem(position);
        adapter.notifyItemRemoved(position);
        update();
    }

    public void updateItem(FoodEaten foodEaten, int position) {
        adapter.updateItem(position, foodEaten);
        adapter.notifyItemChanged(position);
        update();
    }

    public float getTotalCarbohydrates() {
        return getInputCarbohydrates() + getCalculatedCarbohydrates();
    }

    public float getInputCarbohydrates() {
        float input = NumberUtils.parseNumber(valueInput.getText().toString());
        return PreferenceHelper.getInstance().formatCustomToDefaultUnit(Measurement.Category.MEAL, input);
    }

    public float getCalculatedCarbohydrates() {
        return adapter.getTotalCarbohydrates();
    }

    @SuppressWarnings("unused")
    @OnClick(R.id.food_list_button)
    public void searchForFood() {
        Intent intent = new Intent(getContext(), FoodSearchActivity.class);
        intent.putExtra(FoodSearchFragment.EXTRA_MODE, FoodSearchFragment.Mode.SELECT);
        getContext().startActivity(intent);
    }

    @SuppressWarnings("unused")
    public void onEvent(FoodSelectedEvent event) {
        addItem(event.context);
    }

    @SuppressWarnings("unused")
    public void onEvent(FoodEatenUpdatedEvent event) {
        updateItem(event.context, event.position);
    }

    @SuppressWarnings("unused")
    public void onEvent(FoodEatenRemovedEvent event) {
        removeItem(event.position);
    }
}