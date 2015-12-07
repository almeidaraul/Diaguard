package com.faltenreich.diaguard.ui.fragments;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;

import com.faltenreich.diaguard.DiaguardApplication;
import com.faltenreich.diaguard.R;
import com.faltenreich.diaguard.adapter.list.ListItem;
import com.faltenreich.diaguard.adapter.SafeLinearLayoutManager;
import com.faltenreich.diaguard.adapter.StickyHeaderDecoration;
import com.faltenreich.diaguard.util.ViewHelper;
import com.faltenreich.diaguard.ui.view.DayOfMonthDrawable;
import com.faltenreich.diaguard.adapter.LogRecyclerAdapter;

import org.joda.time.DateTime;

import butterknife.Bind;

/**
 * Created by Filip on 05.07.2015.
 */
public class LogFragment extends BaseFragment implements BaseFragment.ToolbarCallback, LogRecyclerAdapter.OnAdapterChangesListener {

    @Bind(R.id.list)
    protected RecyclerView recyclerView;

    private LogRecyclerAdapter listAdapter;
    private StickyHeaderDecoration listDecoration;
    private LinearLayoutManager listLayoutManager;

    public LogFragment() {
        super(R.layout.fragment_log);
    }

    @Override
    public void onViewCreated (View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
        initialize();
        goToDay(DateTime.now());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        updateMonthForUi(getFirstVisibleDay());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.date, menu);

        MenuItem menuItem = menu.findItem(R.id.action_today);
         if (menuItem != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                LayerDrawable icon = (LayerDrawable) menuItem.getIcon();
                setTodayIcon(icon, getActivity());
            } else {
                menuItem.setIcon(R.drawable.ic_action_today);
            }
        }
    }

    private void initialize() {

        // Fragment updates
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                DateTime dateTime = getFirstVisibleDay();
                if (dateTime != null) {
                    // Update month in Toolbar when section is being crossed
                    boolean isScrollingUp = dy < 0;
                    boolean isCrossingMonth = isScrollingUp ?
                            dateTime.dayOfMonth().get() == dateTime.dayOfMonth().getMaximumValue() :
                            dateTime.dayOfMonth().get() == dateTime.dayOfMonth().getMinimumValue();
                    if (isCrossingMonth) {
                        updateMonthForUi(dateTime);
                    }
                }
            }
        });
    }

    private DateTime getFirstVisibleDay() {
        int firstVisibleItemPosition = listLayoutManager.findFirstVisibleItemPosition();
        if (firstVisibleItemPosition >= 0 && firstVisibleItemPosition < listAdapter.getItemCount()) {
            ListItem item = listAdapter.getItem(listLayoutManager.findFirstVisibleItemPosition());
            return item.getDateTime();
        } else {
            return null;
        }
    }

    private void goToDay(DateTime dateTime) {
        int firstVisibleDayPosition = listAdapter != null ?
                listAdapter.getDayPosition(dateTime) :
                -1;
        if (firstVisibleDayPosition >= 0) {
            recyclerView.scrollToPosition(firstVisibleDayPosition);
        } else {
            listLayoutManager = new SafeLinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(listLayoutManager);
            listAdapter = new LogRecyclerAdapter(getActivity(), this, dateTime);
            recyclerView.setAdapter(listAdapter);
            recyclerView.removeItemDecoration(listDecoration);
            listDecoration = new StickyHeaderDecoration(listAdapter, true);
            recyclerView.addItemDecoration(listDecoration);
        }

        updateMonthForUi(dateTime);
    }

    private void updateMonthForUi(DateTime dateTime) {
        boolean isCurrentYear = dateTime.year().get() == DateTime.now().year().get();
        String format = "MMMM";
        if (!isCurrentYear) {
            if (ViewHelper.isLandscape(getActivity()) || ViewHelper.isLargeScreen(getActivity())) {
                format = "MMMM YYYY";
            } else {
                format = "MMM YYYY";
            }
        }
        getActionView().setText(dateTime.toString(format));
    }

    private void setTodayIcon(LayerDrawable icon, Context context) {
        DayOfMonthDrawable today = new DayOfMonthDrawable(context);
        today.setDayOfMonth(DateTime.now().dayOfMonth().get());
        icon.mutate();
        icon.setDrawableByLayerId(R.id.today_icon_day, today);
    }

    public void showDatePicker () {
        DatePickerFragment fragment = new DatePickerFragment() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int day) {
                goToDay(DateTime.now().withYear(year).withMonthOfYear(month + 1).withDayOfMonth(day));
            }
        };
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(DatePickerFragment.DATE, getFirstVisibleDay());
        fragment.setArguments(bundle);
        fragment.show(getActivity().getSupportFragmentManager(), "DatePicker");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_today:
                goToDay(DateTime.now());
                break;
        }
        return true;
    }

    @Override
    public String getTitle() {
        return DiaguardApplication.getContext().getString(R.string.log);
    }

    @Override
    public void action() {
        showDatePicker();
    }

    @Override
    public void changesOrder() {
        if (listDecoration != null) {
            listDecoration.clearHeaderCache();
        }
    }
}
