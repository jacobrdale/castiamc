package com.castiamc.client.hud;

import com.castiamc.client.hack.Hack;
import com.castiamc.client.hack.HackList;
import com.castiamc.client.CastiaClient;

import net.minecraft.client.Minecraft;

public class IngameHUD {

    private final Minecraft mc;
    private final HackList hackList;
    private final int x = 2; // distance from left edge
    private final int lineHeight = 10; // space between hack names

    public IngameHUD() {
        mc = Minecraft.getInstance();
        hackList = CastiaClient.INSTANCE.getHax();
    }

    public void render() {
        int y = 0; // start at top of the screen

        for (Hack hack : hackList.getAllHacks()) {
            if (hack.isEnabled()) {
                // Green text for enabled hacks
                mc.fontRenderer.drawStringWithShadow(hack.getName(), x, y, 0x00FF00);
                y += lineHeight;
            }
        }
    }
}
