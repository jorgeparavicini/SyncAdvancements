package com.jparavicini.sync_advancements.api.command;

import net.minecraft.server.MinecraftServer;

public abstract class SubCommandAction
{
    private final String subCommandName;

    public SubCommandAction(String subCommandName) {
        this.subCommandName = subCommandName;
    }

}
