package com.castiamc.client.settings;

public class SliderSetting {
    public enum ValueDisplay { PERCENTAGE }

    private double value;

    public SliderSetting(String name, String description, double defaultValue, double min, double max, double step, ValueDisplay display) {
        this.value = defaultValue;
    }

    public double getValue() { return value; }
    public void setValue(double val) { this.value = val; }
}
