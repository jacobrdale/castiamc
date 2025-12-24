/*
 * Copyright (c) 2014-2025 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 *
 * Modified for Castia Client by Hexon
 */
package com.castiamc.client;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.client.Minecraft;

import com.castiamc.client.altmanager.AltManager;
import com.castiamc.client.altmanager.Encryption;
import com.castiamc.client.analytics.PlausibleAnalytics;
import com.castiamc.client.clickgui.ClickGui;
import com.castiamc.client.command.CmdList;
import com.castiamc.client.command.CmdProcessor;
import com.castiamc.client.command.Command;
import com.castiamc.client.event.EventManager;
import com.castiamc.client.events.ChatOutputListener;
import com.castiamc.client.events.GUIRenderListener;
import com.castiamc.client.events.KeyPressListener;
import com.castiamc.client.events.PostMotionListener;
import com.castiamc.client.events.PreMotionListener;
import com.castiamc.client.events.UpdateListener;
import com.castiamc.client.hack.HackList; // Only allowed hacks loaded here
import com.castiamc.client.hud.IngameHUD;
import com.castiamc.client.keybinds.KeybindList;
import com.castiamc.client.keybinds.KeybindProcessor;
import com.castiamc.client.mixinterface.IMinecraftClient;
import com.castiamc.client.navigator.Navigator;
import com.castiamc.client.other_feature.OtfList;
import com.castiamc.client.other_feature.OtherFeature;
import com.castiamc.client.settings.SettingsFile;
import com.castiamc.client.update.ProblematicResourcePackDetector;
import com.castiamc.client.update.WurstUpdater; // updater can stay
import com.castiamc.client.util.json.JsonException;

public enum CastiaClient
{
    INSTANCE;

    public static Minecraft MC;
    public static IMinecraftClient IMC;

    public static final String VERSION = "1.0.0";
    public static final String MC_VERSION = "1.21.11";

    private PlausibleAnalytics plausible;
    private EventManager eventManager;
    private AltManager altManager;
    private HackList hax; // only safe hacks like Fullbright
    private CmdList cmds;
    private OtfList otfs;
    private SettingsFile settingsFile;
    private Path settingsProfileFolder;
    private KeybindList keybinds;
    private ClickGui gui;
    private Navigator navigator;
    private CmdProcessor cmdProcessor;
    private IngameHUD hud;

    private boolean enabled = true;
    private static boolean guiInitialized;
    private WurstUpdater updater; // optional updater
    private ProblematicResourcePackDetector problematicPackDetector;
    private Path clientFolder;

    public void initialize()
    {
        System.out.println("Starting Castia Client...");

        MC = Minecraft.getInstance();
        IMC = (IMinecraftClient) MC;
        clientFolder = createClientFolder();

        Path analyticsFile = clientFolder.resolve("analytics.json");
        plausible = new PlausibleAnalytics(analyticsFile);
        plausible.pageview("/");

        eventManager = new EventManager(this);

        Path enabledHacksFile = clientFolder.resolve("enabled-hacks.json");
        hax = new HackList(enabledHacksFile); // will only load allowed hacks

        cmds = new CmdList();

        otfs = new OtfList();

        Path settingsFile = clientFolder.resolve("settings.json");
        settingsProfileFolder = clientFolder.resolve("settings");
        this.settingsFile = new SettingsFile(settingsFile, hax, cmds, otfs);
        this.settingsFile.load();

        Path keybindsFile = clientFolder.resolve("keybinds.json");
        keybinds = new KeybindList(keybindsFile);

        Path guiFile = clientFolder.resolve("windows.json");
        gui = new ClickGui(guiFile);

        Path preferencesFile = clientFolder.resolve("preferences.json");
        navigator = new Navigator(preferencesFile, hax, cmds, otfs);

        cmdProcessor = new CmdProcessor(cmds);
        eventManager.add(ChatOutputListener.class, cmdProcessor);

        KeybindProcessor keybindProcessor =
            new KeybindProcessor(hax, keybinds, cmdProcessor);
        eventManager.add(KeyPressListener.class, keybindProcessor);

        hud = new IngameHUD();
        eventManager.add(GUIRenderListener.class, hud);

        updater = new WurstUpdater();
        eventManager.add(UpdateListener.class, updater);

        problematicPackDetector = new ProblematicResourcePackDetector();
        problematicPackDetector.start();

        Path altsFile = clientFolder.resolve("alts.encrypted_json");
        Path encFolder = Encryption.chooseEncryptionFolder();
        altManager = new AltManager(altsFile, encFolder);
    }

    private Path createClientFolder()
    {
        Path dotMinecraftFolder = MC.gameDirectory.toPath().normalize();
        Path folder = dotMinecraftFolder.resolve("castia-client");

        try
        {
            Files.createDirectories(folder);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Couldn't create client folder.", e);
        }

        return folder;
    }

    public String translate(String key, Object... args)
    {
        // placeholder translator
        return key;
    }

    public PlausibleAnalytics getPlausible() { return plausible; }
    public EventManager getEventManager() { return eventManager; }
    public void saveSettings() { settingsFile.save(); }

    public ArrayList<Path> listSettingsProfiles()
    {
        if(!Files.isDirectory(settingsProfileFolder))
            return new ArrayList<>();

        try(Stream<Path> files = Files.list(settingsProfileFolder))
        {
            return files.filter(Files::isRegularFile)
                .collect(Collectors.toCollection(ArrayList::new));
        }
        catch(IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public void loadSettingsProfile(String fileName) throws IOException, JsonException
    {
        settingsFile.loadProfile(settingsProfileFolder.resolve(fileName));
    }

    public void saveSettingsProfile(String fileName) throws IOException, JsonException
    {
        settingsFile.saveProfile(settingsProfileFolder.resolve(fileName));
    }

    public HackList getHax() { return hax; }
    public CmdList getCmds() { return cmds; }
    public OtfList getOtfs() { return otfs; }

    public OtherFeature getFeatureByName(String name)
    {
        Hack hack = getHax().getHackByName(name);
        if(hack != null)
            return hack;

        Command cmd = getCmds().getCmdByName(name.substring(1));
        if(cmd != null)
            return cmd;

        OtherFeature otf = getOtfs().getOtfByName(name);
        return otf;
    }

    public KeybindList getKeybinds() { return keybinds; }

    public ClickGui getGui()
    {
        if(!guiInitialized)
        {
            guiInitialized = true;
            gui.init();
        }
        return gui;
    }

    public Navigator getNavigator() { return navigator; }
    public CmdProcessor getCmdProcessor() { return cmdProcessor; }
    public IngameHUD getHud() { return hud; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public WurstUpdater getUpdater() { return updater; }
    public ProblematicResourcePackDetector getProblematicPackDetector() { return problematicPackDetector; }
    public Path getClientFolder() { return clientFolder; }
    public AltManager getAltManager() { return altManager; }
}
