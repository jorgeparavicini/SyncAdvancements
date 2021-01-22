package com.jparavicini.sync_advancements.api.capabilities.Team;

import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import com.jparavicini.sync_advancements.api.data.TogetherRegistries;
import com.jparavicini.sync_advancements.utils.TeamHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.StringNBT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class DefaultTeam implements ITeam
{
    private String teamName;
    private UUID owner;
    private final List<IPlayerInformation> playersInformation = new ArrayList<>();


    @Override
    public void addPlayer(final IPlayerInformation playerInformation)
    {
        if (teamName == null)
        {
            teamName = playerInformation.getName();
            owner = playerInformation.getUUID();
        }
        if (!this.playersInformation.contains(playerInformation))
        {
            this.playersInformation.add(playerInformation);
        }
    }

    @Override
    public void removePlayer(final IPlayerInformation playerInformation)
    {
        playersInformation.remove(playerInformation);
    }

    @Override
    public void removePlayer(final UUID playerUUID)
    {
        IPlayerInformation information = TeamHelper.findPlayerInfo(playersInformation, playerUUID);
        if (information != null) playersInformation.remove(information);
    }

    @Override
    public Collection<IPlayerInformation> getPlayers()
    {
        return playersInformation;
    }

    @Override
    public CompoundNBT getNBTTag()
    {
        CompoundNBT compound = new CompoundNBT();
        compound.put("Name", StringNBT.valueOf(getTeamName()));
        compound.put("Owner", StringNBT.valueOf(getOwner().toString()));

        for (IPlayerInformation information : getPlayers())
        {
            CompoundNBT playerCompound = new CompoundNBT();
            String playerId = TogetherRegistries.getPlayerInformationID(information.getClass());
            if (playerId == null)
            {
                SyncAdvancements.LOGGER.warn("Player not found " + information.getName());
                continue;
            }
            playerCompound.put("PlayerID", StringNBT.valueOf(playerId));
            playerCompound.put("Value", information.getNBTTag());
            compound.put(information.getUUID().toString(), playerCompound);
        }

        return compound;
    }

    @Override
    public void readFromNBT(final CompoundNBT compound)
    {
        teamName = compound.getString("Name");
        owner = UUID.fromString(compound.getString("Owner"));
        for (String key : compound.keySet())
        {
            if (key.equalsIgnoreCase("Name")) continue;
            CompoundNBT informationCompound = compound.getCompound(key);
            Class<? extends IPlayerInformation> plClass = TogetherRegistries.getPlayerInformationClass(informationCompound.getString("PlayerID"));
            if (plClass != null)
            {
                try
                {
                    IPlayerInformation info = plClass.newInstance();
                    info.readFromNBT(informationCompound.getCompound("Value"));
                    playersInformation.add(info);
                } catch (InstantiationException | IllegalAccessException e)
                {
                    SyncAdvancements.LOGGER.error("Failed to read Team NBT", e);
                }
            }
        }
    }

    @Override
    public String getTeamName()
    {
        return teamName;
    }

    @Override
    public UUID getOwner()
    {
        return owner;
    }
}
