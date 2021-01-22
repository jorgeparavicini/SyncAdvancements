package com.jparavicini.sync_advancements.action.recovery;

import com.jparavicini.sync_advancements.api.IPlayerInformation;
import net.minecraft.nbt.CompoundNBT;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStageOfflineRecovery extends AbstractOfflineRecovery
{
    public GameStageOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(final IPlayerInformation playerInformation)
    {
        List<Map.Entry<IPlayerInformation, CompoundNBT>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, CompoundNBT> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                String stage = entry.getValue().getString("Stage");
                if (playerInformation.getPlayer() != null && !GameStageHelper.hasStage(playerInformation.getPlayer(), stage)) {
                    GameStageEventSyncAction.unlockPlayerStage(playerInformation.getPlayer(), stage);
                }
                removeList.add(entry);
            }
        }
        for (Map.Entry<IPlayerInformation, CompoundNBT> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}
