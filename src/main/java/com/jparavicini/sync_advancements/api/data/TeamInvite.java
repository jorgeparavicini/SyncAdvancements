package com.jparavicini.sync_advancements.api.data;

import com.jparavicini.sync_advancements.api.IOfflineSyncRecovery;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import com.jparavicini.sync_advancements.api.ISyncAction;
import com.jparavicini.sync_advancements.api.SyncAdvancementsAPI;
import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;
import com.jparavicini.sync_advancements.api.capabilities.Team.DefaultTeam;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class TeamInvite
{
    private final IPlayerInformation sender;
    private final IPlayerInformation receiver;
    private long createdTime;

    public TeamInvite(IPlayerInformation sender, IPlayerInformation receiver)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.createdTime = System.currentTimeMillis();
    }

    public IPlayerInformation getSender()
    {
        return sender;
    }

    public IPlayerInformation getReceiver()
    {
        return receiver;
    }

    public void acceptInvite(boolean announce, boolean syncActions)
    {
        ITeam team = SyncAdvancementsAPI.getInstance().getPlayerTeam(sender.getUUID());
        if (team == null)
        {
            team = new DefaultTeam();
            team.addPlayer(sender);
            TogetherForeverAPI.getInstance().addTeam(team);
        }
        if (announce)
        {
            for (IPlayerInformation info : team.getPlayers())
            {
                if (info.getPlayer() != null)
                    info.getPlayer().sendMessage(new StringTextComponent(
                            TextFormatting.GREEN + receiver.getName() + " has joined your team."), sender.getUUID());
            }
            if (receiver.getPlayer() != null)
                receiver.getPlayer().sendMessage(new StringTextComponent(
                        TextFormatting.GREEN + "You have joined " + sender.getName() + "'s team."), sender.getUUID());
        }
        if (syncActions)
        {
            for (ISyncAction<?, ? extends IOfflineSyncRecovery> action : TogetherRegistries.getSyncActions())
            {
                action.syncJoinPlayer(receiver, sender);
            }
        }
        TogetherForeverAPI.getInstance().addPlayerToTeam(team, receiver);
    }
}
