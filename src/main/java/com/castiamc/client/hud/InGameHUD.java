package com.castiamc.client;

import com.castiamc.client.events.GUIRenderListener;
import com.castiamc.client.hack.Hack;
import com.castiamc.client.hack.HackList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class IngameHUD implements GUIRenderListener {

    private final Minecraft mc;

    public IngameHUD() {
        this.mc = CastiaClient.MC;
    }

    @Override
    public void onRender() {
        renderHackList();
    }

    private void renderHackList() {
        HackList hacks = CastiaClient.INSTANCE.getHax();
        int x = 2; // left side of screen
        int y = 0; // start at top
        int lineHeight = 10;

        for (Hack hack : hacks.getHacks()) {
            if (hack.isEnabled()) {
                drawText(hack.getName(), x, y);
                y += lineHeight;
            }
        }
    }

    // Basic text rendering using Minecraft's built-in font renderer
    private void drawText(String text, int x, int y) {
        mc.font.drawShadow(mc.gui, Component.literal(text), x, y, 0xFFFFFF);
    }
}