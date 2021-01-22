package com.jparavicini.sync_advancements.action.recovery;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.api.IOfflineSyncRecovery;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import com.jparavicini.sync_advancements.api.data.TogetherRegistries;
import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public abstract class AbstractOfflineRecovery implements IOfflineSyncRecovery
{
    protected ListMultimap<IPlayerInformation, CompoundNBT> offlineRecoveries;

    public AbstractOfflineRecovery()
    {
        this.offlineRecoveries = ArrayListMultimap.create();
    }

    @Override
    public void storeMissingPlayers(List<IPlayerInformation> playersInformation, CompoundNBT store)
    {
        for (IPlayerInformation playerInformation : playersInformation)
        {
            storeMissingPlayer(playerInformation, store);
        }
    }

    @Override
    public void storeMissingPlayer(final IPlayerInformation playerInformation, final CompoundNBT store)
    {
        offlineRecoveries.put(playerInformation, store);
    }

    @Override
    public CompoundNBT writeToNBT()
    {
        CompoundNBT compound = new CompoundNBT();
        for (IPlayerInformation playerInformation : offlineRecoveries.keySet())
        {
            String uuid = playerInformation.getUUID().toString();
            CompoundNBT recovery = new CompoundNBT();
            recovery.put("ID", playerInformation.getNBTTag());
            recovery.putString("PlayerID", TogetherRegistries.getPlayerInformationID(playerInformation.getClass()));
            int id = 0;
            for (CompoundNBT comp : offlineRecoveries.get(playerInformation))
            {
                recovery.put(Integer.toString(id), comp);
                id++;
            }
            compound.put(uuid, recovery);
        }
        return compound;
    }

    @Override
    public void readFromNBT(final CompoundNBT compound)
    {
        offlineRecoveries.clear();
        for (String uuid : compound.keySet())
        {
            CompoundNBT recovery = compound.getCompound(uuid);
            Class<? extends IPlayerInformation> plClass = TogetherRegistries.getPlayerInformationClass(recovery.getString("PlayerID"));
            if (plClass != null) {
                try {
                    IPlayerInformation info = plClass.newInstance();
                    info.readFromNBT(recovery.getCompound("ID"));
                    for (String id : recovery.keySet()) {
                        if (!id.equalsIgnoreCase("ID") && !id.equalsIgnoreCase("PlayerID")) {
                            offlineRecoveries.put(info, recovery.getCompound(id));
                        }
                    }
                } catch (InstantiationException | IllegalAccessException e) {
                    SyncAdvancements.LOGGER.error("Failed reading compound", e);
                }
            }
        }
    }
}
