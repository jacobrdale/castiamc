package com.castiamc.client.settings;

public class EnumSetting<T extends Enum<T>> {
    private final String name;
    private T selected;

    public EnumSetting(String name, String description, T[] values, T defaultValue) {
        this.name = name;
        this.selected = defaultValue;
    }

    public T getSelected() {
        return selected;
    }
}
