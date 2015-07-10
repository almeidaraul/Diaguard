package com.faltenreich.diaguard.database.measurements;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by Filip on 11.05.2015.
 */
@DatabaseTable
public class Insulin extends Measurement {

    public static final String BOLUS = "bolus";
    public static final String CORRECTION = "correction";
    public static final String BASAL = "basal";

    @DatabaseField
    private float bolus;

    @DatabaseField
    private float correction;

    @DatabaseField
    private float basal;

    public float getBolus() {
        return bolus;
    }

    public void setBolus(float bolus) {
        this.bolus = bolus;
    }

    public float getCorrection() {
        return correction;
    }

    public void setCorrection(float correction) {
        this.correction = correction;
    }

    public float getBasal() {
        return basal;
    }

    public void setBasal(float basal) {
        this.basal = basal;
    }

    public Category getMeasurementType() {
        return Category.Insulin;
    }
}