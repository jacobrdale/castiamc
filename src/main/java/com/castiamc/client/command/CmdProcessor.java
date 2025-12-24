package com.castiamc.client.command;

import com.castiamc.client.events.ChatOutputListener;

public class CmdProcessor implements ChatOutputListener {
    private final CmdList cmds;

    public CmdProcessor(CmdList cmds) {
        this.cmds = cmds;
    }

    @Override
    public void onChatMessage(String msg) {
        // placeholder, could parse commands later
    }
}
