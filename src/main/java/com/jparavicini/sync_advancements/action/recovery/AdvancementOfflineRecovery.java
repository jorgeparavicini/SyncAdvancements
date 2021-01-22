package com.jparavicini.sync_advancements.action.recovery;

import com.jparavicini.sync_advancements.action.AdvancementEventSyncAction;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.loading.FMLEnvironment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AdvancementOfflineRecovery extends AbstractOfflineRecovery
{
    public AdvancementOfflineRecovery() {
        super();
    }

    @Override
    public void recoverMissingPlayer(final IPlayerInformation playerInformation)
    {
        List<Map.Entry<IPlayerInformation, CompoundNBT>> removeList = new ArrayList<>();
        for (Map.Entry<IPlayerInformation, CompoundNBT> entry : new ArrayList<>(offlineRecoveries.entries())) {
            if (entry.getKey().getUUID().equals(playerInformation.getUUID())) {
                ResourceLocation location = new ResourceLocation(entry.getValue().getString("AdvancementId"));
                Advancement advancement = Minecraft.getInstance().world.getServer().getAdvancementManager().getAdvancement(location);
                if (advancement != null) {
                    AdvancementEventSyncAction.grantAllParentAchievements(playerInformation.getPlayer(), advancement);
                }
                removeList.add(entry);
            }
        }

        for (Map.Entry<IPlayerInformation, CompoundNBT> entry : removeList) {
            offlineRecoveries.remove(entry.getKey(), entry.getValue());
        }
    }
}
