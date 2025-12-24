package com.castiamc.client.settings;

public class CheckboxSetting {
    private final String name;
    private boolean checked;

    public CheckboxSetting(String name, String description, boolean defaultValue) {
        this.name = name;
        this.checked = defaultValue;
    }

    public boolean isChecked() {
        return checked;
    }
}
