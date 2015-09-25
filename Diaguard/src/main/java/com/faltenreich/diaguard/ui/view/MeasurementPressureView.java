package com.faltenreich.diaguard.ui.view;

import android.content.Context;
import android.widget.EditText;

import com.faltenreich.diaguard.R;
import com.faltenreich.diaguard.data.PreferenceHelper;
import com.faltenreich.diaguard.data.entity.Insulin;
import com.faltenreich.diaguard.data.entity.Measurement;
import com.faltenreich.diaguard.data.entity.Pressure;
import com.faltenreich.diaguard.util.StringUtils;

import butterknife.Bind;

/**
 * Created by Faltenreich on 20.09.2015.
 */
public class MeasurementPressureView extends MeasurementAbstractView<Pressure> {

    @Bind(R.id.edittext_systolic)
    protected EditText editTextSystolic;

    @Bind(R.id.edittext_diastolic)
    protected EditText editTextDiastolic;

    public MeasurementPressureView(Context context) {
        super(context, Measurement.Category.PRESSURE);
    }

    public MeasurementPressureView(Context context, Pressure pressure) {
        super(context, pressure);
    }

    @Override
    protected int getLayoutResourceId() {
        return R.layout.list_item_measurement_pressure;
    }

    @Override
    protected void initLayout() {
        editTextSystolic.setHint(getContext().getString(R.string.systolic));
        editTextDiastolic.setHint(getContext().getString(R.string.diastolic));
    }

    @Override
    protected boolean isValid() {
        return isValueValid(editTextSystolic) && isValueValid(editTextDiastolic);
    }

    private boolean isValueValid(EditText editText) {
        boolean isValid = true;

        String input = editText.getText().toString().trim();
        if (StringUtils.isBlank(input)) {
            editText.setError(getContext().getString(R.string.validator_value_empty));
            isValid = false;
        } else {
            try {
                float value = Float.parseFloat(input);
                if (!PreferenceHelper.getInstance().validateEventValue(measurement.getMeasurementType(), value)) {
                    editText.setError(getContext().getString(R.string.validator_value_unrealistic));
                    isValid = false;
                }
            } catch (NumberFormatException exception) {
                editText.setError(getContext().getString(R.string.validator_value_number));
                isValid = false;
            }
        }
        return isValid;
    }

    @Override
    public Measurement getMeasurement() {
        if (isValid()) {
            measurement.setValues(
                    Float.parseFloat(editTextSystolic.getText().toString()),
                    Float.parseFloat(editTextDiastolic.getText().toString()));
            return measurement;
        } else {
            return null;
        }
    }
    
}