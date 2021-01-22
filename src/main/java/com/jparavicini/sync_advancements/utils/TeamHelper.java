package com.jparavicini.sync_advancements.utils;

import com.jparavicini.sync_advancements.api.IPlayerInformation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.UUID;

public class TeamHelper
{

    @Nullable
    public static IPlayerInformation findPlayerInfo(@Nonnull Collection<IPlayerInformation> information, @Nonnull UUID playerUUID)
    {
        for (IPlayerInformation player : information)
        {
            if (player.getUUID().equals(playerUUID)) return player;
        }
        return null;
    }
}
