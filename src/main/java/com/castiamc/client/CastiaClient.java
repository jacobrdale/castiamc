/*
 * Copyright (c) 2024-2025 Hexon
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Castia Client main class
 */
package com.castiamc.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;

import com.castiamc.client.analytics.PlausibleAnalytics;
import com.castiamc.client.command.CmdList;
import com.castiamc.client.command.CmdProcessor;
import com.castiamc.client.command.Command;
import com.castiamc.client.event.EventManager;
import com.castiamc.client.events.ChatOutputListener;
import com.castiamc.client.events.KeyPressListener;
import com.castiamc.client.events.UpdateListener;
import com.castiamc.client.hack.HackList;
import com.castiamc.client.hud.IngameHUD;
import com.castiamc.client.keybinds.KeybindList;
import com.castiamc.client.keybinds.KeybindProcessor;
import com.castiamc.client.mixinterface.IMinecraftClient;
import com.castiamc.client.navigator.Navigator;
import com.castiamc.client.other_feature.OtfList;
import com.castiamc.client.other_feature.OtherFeature;
import com.castiamc.client.settings.SettingsFile;
import com.castiamc.client.update.ProblematicResourcePackDetector;
import com.castiamc.client.update.CCUpdater; // Your custom updater
import com.castiamc.client.util.json.JsonException;

public enum CastiaClient {
    INSTANCE;

    public static Minecraft MC;
    public static IMinecraftClient IMC;

    public static final String VERSION = "1.0.0";
    public static final String MC_VERSION = "1.21.11";

    private PlausibleAnalytics plausible;
    private EventManager eventManager;
    private HackList hax; // only allowed hacks
    private CmdList cmds;
    private OtfList otfs;
    private SettingsFile settingsFile;
    private Path settingsProfileFolder;
    private KeybindList keybinds;
    private Navigator navigator;
    private CmdProcessor cmdProcessor;
    private IngameHUD hud;
    private CCUpdater updater; // Castia Client updater
    private ProblematicResourcePackDetector problematicPackDetector;
    private Path clientFolder;

    private boolean enabled = true;

    public void initialize() {
        System.out.println("Starting Castia Client...");

        MC = Minecraft.getInstance();
        IMC = (IMinecraftClient) MC;
        clientFolder = createClientFolder();

        // Analytics
        Path analyticsFile = clientFolder.resolve("analytics.json");
        plausible = new PlausibleAnalytics(analyticsFile);
        plausible.pageview("/");

        // Event manager
        eventManager = new EventManager(this);

        // Hacks
        Path enabledHacksFile = clientFolder.resolve("enabled-hacks.json");
        hax = new HackList(enabledHacksFile);

        // Commands
        cmds = new CmdList();

        // Other features
        otfs = new OtfList();

        // Settings
        Path settingsFile = clientFolder.resolve("settings.json");
        settingsProfileFolder = clientFolder.resolve("settings");
        this.settingsFile = new SettingsFile(settingsFile, hax, cmds, otfs);
        this.settingsFile.load();

        // Keybinds
        Path keybindsFile = clientFolder.resolve("keybinds.json");
        keybinds = new KeybindList(keybindsFile);

        // Navigator
        Path preferencesFile = clientFolder.resolve("preferences.json");
        navigator = new Navigator(preferencesFile, hax, cmds, otfs);

        // Command processor
        cmdProcessor = new CmdProcessor(cmds);
        eventManager.add(ChatOutputListener.class, cmdProcessor);

        KeybindProcessor keybindProcessor = new KeybindProcessor(hax, keybinds, cmdProcessor);
        eventManager.add(KeyPressListener.class, keybindProcessor);

        // HUD
        hud = new IngameHUD();
        eventManager.add(com.castiamc.client.events.GUIRenderListener.class, hud);

        // Updater
        updater = new CCUpdater();
        eventManager.add(UpdateListener.class, updater);

        // Problematic resource pack detector
        problematicPackDetector = new ProblematicResourcePackDetector();
        problematicPackDetector.start();
    }

    private Path createClientFolder() {
        Path dotMinecraftFolder = MC.gameDirectory.toPath().normalize();
        Path folder = dotMinecraftFolder.resolve("castia-client");

        try {
            Files.createDirectories(folder);
        } catch (IOException e) {
            throw new RuntimeException("Couldn't create client folder.", e);
        }

        return folder;
    }

    // Getters
    public PlausibleAnalytics getPlausible() { return plausible; }
    public EventManager getEventManager() { return eventManager; }
    public HackList getHax() { return hax; }
    public CmdList getCmds() { return cmds; }
    public OtfList getOtfs() { return otfs; }
    public KeybindList getKeybinds() { return keybinds; }
    public Navigator getNavigator() { return navigator; }
    public CmdProcessor getCmdProcessor() { return cmdProcessor; }
    public IngameHUD getHud() { return hud; }
    public CCUpdater getUpdater() { return updater; }
    public ProblematicResourcePackDetector getProblematicPackDetector() { return problematicPackDetector; }
    public Path getClientFolder() { return clientFolder; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    // Settings methods
    public void saveSettings() { settingsFile.save(); }

    public ArrayList<Path> listSettingsProfiles() {
        if (!Files.isDirectory(settingsProfileFolder)) return new ArrayList<>();
        try (Stream<Path> files = Files.list(settingsProfileFolder)) {
            return files.filter(Files::isRegularFile)
                        .collect(Collectors.toCollection(ArrayList::new));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void loadSettingsProfile(String fileName) throws IOException, JsonException {
        settingsFile.loadProfile(settingsProfileFolder.resolve(fileName));
    }

    public void saveSettingsProfile(String fileName) throws IOException, JsonException {
        settingsFile.saveProfile(settingsProfileFolder.resolve(fileName));
    }

    // Helper to get any feature by name
    public OtherFeature getFeatureByName(String name) {
        Hack hack = getHax().getHackByName(name);
        if (hack != null) return hack;

        Command cmd = getCmds().getCmdByName(name.substring(1));
        if (cmd != null) return cmd;

        OtherFeature otf = getOtfs().getOtfByName(name);
        return otf;
    }
}
