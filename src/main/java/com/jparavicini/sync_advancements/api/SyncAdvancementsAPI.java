package com.jparavicini.sync_advancements.api;

import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;
import com.jparavicini.sync_advancements.api.data.DataManager;
import com.jparavicini.sync_advancements.api.data.TeamInvite;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.system.CallbackI;
import sun.text.normalizer.Trie;

import javax.annotation.Nullable;
import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

public class SyncAdvancementsAPI
{

    public static final String MOD_NAME = "syncadvancements";
    public static final String API_VERSION = "1";
    public static final String API_ID = MOD_NAME + "api";

    private static final SyncAdvancementsAPI instance = new SyncAdvancementsAPI(teamInvites);
    private List<TeamInvite> teamInvites;

    public SyncAdvancementsAPI(final List<TeamInvite> teamInvites)
    {
        this.teamInvites = teamInvites;
    }

    public static SyncAdvancementsAPI getInstance()
    {
        return instance;
    }

    public List<ITeam> getTeams()
    {
        World world = getWorld();
        if (world == null) return new ArrayList<>();
        return getDataManager(world).getTeams();
    }

    public void addTeam(ITeam team)
    {
        DataManager dataManager = getDataManager(getWorld());
        if (dataManager == null) return;
        TeamEvent.Create create = new TeamEvent.Create(team);
        MinecraftForge.EVENT_BUS.post(create);
        if (!create.isCanceled())
        {
            dataManager.getTeams().add(create.getTogetherTeam());
            dataManager.markDirty();
        }
    }

    public void addPlayerToTeam(ITeam team, IPlayerInformation playerInformation)
    {
        DataManager dataManager = getDataManager(getWorld());
        if (dataManager == null) return;
        for (ITeam t : dataManager.getTeams())
        {
            if (t.getOwner().equals(team.getOwner()) && t.getTeamName().equalsIgnoreCase(team.getTeamName()))
            {
                TeamEvent.PlayerAdd playerAdd = new TeamEvent.PlayerAdd(team, playerInformation);
                MinecraftForge.EVENT_BUS.post(playerAdd);
                if (!playerAdd.IsCancelled())
                {
                    playerAdd.getTogetherTeam().addPlayer(playerAdd.getPlayerInformation());
                    dataManager.markDirty();
                }
            }
        }
    }

    /**
     * Removes a player from a team using the team unique identifiers to search it
     *
     * @param team              The team to remove the player from
     * @param playerInformation The information of the player that needs to be removed from the team
     */
    public void removePlayerFromTeam(ITeam team, IPlayerInformation playerInformation)
    {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (ITeam t : manager.getTeams())
        {
            if (t.getOwner().equals(team.getOwner()) && t.getTeamName().equalsIgnoreCase(team.getTeamName()))
            {
                TeamEvent.RemovePlayer removePlayer = new TeamEvent.RemovePlayer(t, playerInformation);
                if (!removePlayer.isCanceled())
                {
                    removePlayer.getTogetherTeam().removePlayer(removePlayer.getPlayerInformation());
                    manager.markDirty();
                }
            }
        }
    }

    /**
     * Gets the team of a player
     *
     * @param playerUUID The UUID of the player to get the team from
     * @return The team of the player, null if the player isn't in a team
     */
    @Nullable
    public ITeam getPlayerTeam(UUID playerUUID)
    {
        for (ITeam togetherTeam : getTeams())
        {
            for (IPlayerInformation playerInformation : togetherTeam.getPlayers())
            {
                if (playerInformation.getUUID().equals(playerUUID)) return togetherTeam;
            }
        }
        return null;
    }

    /**
     * Creates an invite to join a team
     *
     * @param sender         The player that sends the invite
     * @param receiver       The player that receives the invite
     * @param announceInvite true if the player that gets the invite needs to get a notification
     * @return The created invite
     */
    public TeamInvite createTeamInvite(IPlayerInformation sender, IPlayerInformation receiver, boolean announceInvite)
    {
        TeamInvite invite = new TeamInvite(sender, receiver);
        if (announceInvite)
        {
            ITextComponent accept = new StringTextComponent("[ACCEPT]");
            accept.getStyle().setBold(true).setColor(Color.fromTextFormatting(TextFormatting.GREEN)).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/tofe accept "
                    + sender.getName())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to accept")));
            ITextComponent decline = new StringTextComponent("[DECLINE]");
            decline.getStyle().setBold(true).setColor(Color.fromTextFormatting(TextFormatting.RED)).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                    "/tofe decline "
                    + sender.getName())).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("Click to accept")));
            receiver.getPlayer().sendMessage(new StringTextComponent(
                    "You have been invited to join " + sender.getName() + "'s team. Click ")
                    .append(accept).appendString(" ").append(decline), sender.getUUID());
        }
        teamInvites.add(invite);
        return invite;
    }

    /**
     * Adds a player to a OfflineSyncRecovery
     *
     * @param recoveryClass      The class of the OfflineSyncRecovery
     * @param iPlayerInformation The PlayerInformation that need to be stored
     * @param compound           The recovery information that needs to be stored
     */
    public void addPlayerToOfflineRecovery(Class<? extends IOfflineSyncRecovery> recoveryClass, IPlayerInformation iPlayerInformation, CompoundNBT compound)
    {
        DataManager manager = getDataManager(getWorld());
        if (manager == null) return;
        for (IOfflineSyncRecovery recovery : manager.getRecoveries())
        {
            if (recovery.getClass().equals(recoveryClass))
            {
                recovery.storeMissingPlayer(iPlayerInformation, compound);
                manager.markDirty();
                return;
            }
        }
        try
        {
            IOfflineSyncRecovery recovery = recoveryClass.newInstance();
            recovery.storeMissingPlayer(iPlayerInformation, compound);
            manager.getRecoveries().add(recovery);
            manager.markDirty();
        } catch (InstantiationException | IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Gets the generic DataManager
     *
     * @param world A world of to get the DataManager, it can be any world
     * @return the DataManager
     */
    public DataManager getDataManager(ServerWorld world)
    {
        if (world == null) return null;
        DimensionSavedDataManager storage = world.getSavedData();
        DataManager manager = storage.getOrCreate(DataManager::new, DataManager.NAME);

        if (manager == null)
        {
            manager = new DataManager();
            storage.set(manager);
        }
        return manager;
    }

    /**
     * Gets the player entity
     *
     * @param string The name of the player
     * @return The EntityPlayerMP, null if the player is offline
     */
    @Nullable
    public ServerPlayerEntity getPlayer(String string)
    {
        World.
        return Minecraft.getInstance()..getPlayerList().getPlayerByUsername(string);
    }

    public World getWorld() {
        Minecraft.getInstance().world
    }
}
