package com.jparavicini.sync_advancements.api.data;

import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.api.IOfflineSyncRecovery;
import com.jparavicini.sync_advancements.api.ISyncAction;
import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.storage.WorldSavedData;

import java.util.ArrayList;
import java.util.List;

public class DataManager extends WorldSavedData
{
    public static final String NAME = "SyncAdvancements";
    public static final String TEAM = "Teams";
    public static final String RECOVERY = "Recovery";

    private List<ITeam> teams;
    private final List<IOfflineSyncRecovery> recoveries;

    public DataManager(String string)
    {
        super(string);
        this.teams = new ArrayList<>();
        this.recoveries = new ArrayList<>();
    }

    public DataManager()
    {
        this(NAME);
    }

    @Override
    public void read(final CompoundNBT nbt)
    {
        teams = new ArrayList<>();

        CompoundNBT raw = nbt.getCompound(NAME);
        CompoundNBT teamCompound = raw.getCompound(TEAM);
        for (String teamNames : teamCompound.keySet()) {
            CompoundNBT team = teamCompound.getCompound(teamNames);
            String teamID = team.getString("TeamID");
            Class<? extends ITeam> aClass = TogetherRegistries.getTogetherTeamClass(teamID);
            if (aClass != null) {
                try {
                    ITeam togetherTeam = aClass.newInstance();
                    togetherTeam.readFromNBT(team.getCompound("Value"));
                    teams.add(togetherTeam);
                } catch (InstantiationException | IllegalAccessException e) {
                    SyncAdvancements.LOGGER.error("Failed reading Team from NBT", e);
                }
            }
        }

        // Offline Recovery
        CompoundNBT offlineRecovery = nbt.getCompound(RECOVERY);
        for (String key : offlineRecovery.keySet()) {
            ISyncAction<?, ? extends IOfflineSyncRecovery> action = TogetherRegistries.getSyncActionFromID(key);
            if (action != null) {
                try {
                    IOfflineSyncRecovery recovery = action.getOfflineRecovery().newInstance();
                    recovery.readFromNBT(offlineRecovery.getCompound(key));
                    recoveries.add(recovery);
                } catch (InstantiationException | IllegalAccessException e) {
                    SyncAdvancements.LOGGER.error("Failed reading Recovery from NBT", e);
                }
            }
        }
    }

    @Override
    public CompoundNBT write(final CompoundNBT compound)
    {
        CompoundNBT custom = new CompoundNBT();

        // Team Saving
        CompoundNBT teamCompound = new CompoundNBT();
        for (ITeam togetherTeam : teams) {
            CompoundNBT team = new CompoundNBT();
            team.putString("TeamID", TogetherRegistries.getTogetherTeamID(togetherTeam.getClass()));
            team.put("Value", togetherTeam.getNBTTag());
            teamCompound.put(togetherTeam.getTeamName(), team);
        }

        custom.put(TEAM, teamCompound);

        // Offline Recovery Saving
        CompoundNBT offlineRecovery = new CompoundNBT();
        for (IOfflineSyncRecovery recovery : recoveries) {
            offlineRecovery.put(TogetherRegistries.getSyncActionIdFromOfflineRecovery(recovery), recovery.writeToNBT());
        }
        custom.put(RECOVERY, offlineRecovery);
        compound.put(NAME, custom);
        return compound;
    }

    public List<ITeam> getTeams() {
        return teams;
    }

    public List<IOfflineSyncRecovery> getRecoveries() {
        return recoveries;
    }
}
