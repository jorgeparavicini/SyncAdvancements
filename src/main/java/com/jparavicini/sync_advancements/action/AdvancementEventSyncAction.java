package com.jparavicini.sync_advancements.action;

import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.action.recovery.AdvancementOfflineRecovery;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import com.jparavicini.sync_advancements.api.action.EventSyncAction;
import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;
import net.minecraft.advancements.Advancement;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.AdvancementEvent;

import java.util.List;

public class AdvancementEventSyncAction extends EventSyncAction<AdvancementEvent, AdvancementOfflineRecovery>
{
    public AdvancementEventSyncAction()
    {
        super(AdvancementEvent.class, AdvancementOfflineRecovery.class);
    }

    public static void grantAllParentAchievements(ServerPlayerEntity player, Advancement advancement)
    {
        if (advancement.getParent() != null) grantAllParentAchievements(player, advancement.getParent());
        SyncAdvancements.LOGGER.debug("Advancement granting: " + advancement.getId().toString());
        for (String string : player.getAdvancements().getProgress(advancement).getRemaningCriteria()) {
            player.getAdvancements().grantCriterion(advancement, string);
        }
    }

    @Override
    public List<IPlayerInformation> triggerSync(final AdvancementEvent object, final ITeam togetherTeam)
    {
        return null;
    }

    @Override
    public void syncJoinPlayer(final IPlayerInformation toBeSynced, final IPlayerInformation teamMember)
    {

    }
}
