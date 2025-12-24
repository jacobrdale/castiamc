package com.castiamc.client.hack;

import com.castiamc.client.events.UpdateListener;

import java.util.ArrayList;
import java.util.List;

public abstract class Hack {
    private final String name;
    private boolean enabled;
    private final List<Object> settings = new ArrayList<>();

    public Hack(String name) {
        this.name = name;
        this.enabled = false;
    }

    public String getName() {
        return name;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void addSetting(Object setting) {
        settings.add(setting);
    }
}
