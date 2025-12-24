package com.castiamc.client.hack;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class HackList {
    private final List<Hack> hacks = new ArrayList<>();

    public HackList(Path file) {
        // placeholder constructor, could load hacks from JSON later
    }

    public void add(Hack hack) {
        hacks.add(hack);
    }

    public Hack getHackByName(String name) {
        for (Hack hack : hacks) {
            if (hack.getName().equalsIgnoreCase(name)) return hack;
        }
        return null;
    }
}
