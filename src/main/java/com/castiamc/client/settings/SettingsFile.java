package com.castiamc.client.settings;

import com.castiamc.client.command.CmdList;
import com.castiamc.client.hack.HackList;
import com.castiamc.client.other_feature.OtfList;

import java.nio.file.Path;

import com.castiamc.client.util.json.JsonException;
import java.io.IOException;

public class SettingsFile {
    public SettingsFile(Path file, HackList hax, CmdList cmds, OtfList otfs) {
        // placeholder
    }

    public void load() {}
    public void save() {}
    public void loadProfile(Path file) throws IOException, JsonException {}
    public void saveProfile(Path file) throws IOException, JsonException {}
}
