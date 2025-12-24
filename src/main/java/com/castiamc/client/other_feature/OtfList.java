package com.castiamc.client.other_feature;

import java.util.ArrayList;
import java.util.List;

public class OtfList {
    private final List<OtherFeature> features = new ArrayList<>();

    public void add(OtherFeature feature) {
        features.add(feature);
    }

    public OtherFeature getOtfByName(String name) {
        for (OtherFeature f : features) {
            if (f.getName().equalsIgnoreCase(name)) return f;
        }
        return null;
    }
}
