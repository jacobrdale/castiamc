package com.castiamc.client.command;

import java.util.ArrayList;

public class CmdList {
    private final ArrayList<Command> commands = new ArrayList<>();

    public void add(Command cmd) {
        commands.add(cmd);
    }

    public Command getCmdByName(String name) {
        for (Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(name)) return cmd;
        }
        return null;
    }
}
