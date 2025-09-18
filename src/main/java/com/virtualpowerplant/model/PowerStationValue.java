package com.virtualpowerplant.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PowerStationValue {

    @JsonProperty("unit")
    private String unit;

    @JsonProperty("value")
    private String value;

    public PowerStationValue() {}

    public PowerStationValue(String unit, String value) {
        this.unit = unit;
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Double getDoubleValue() {
        try {
            if (value == null || value.equals("--") || value.trim().isEmpty()) {
                return null;
            }
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public boolean hasValue() {
        return value != null && !value.equals("--") && !value.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "PowerStationValue{" +
                "unit='" + unit + '\'' +
                ", value='" + value + '\'' +
                '}';
    }
}