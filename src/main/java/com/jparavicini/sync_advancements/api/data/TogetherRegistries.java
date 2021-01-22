package com.jparavicini.sync_advancements.api.data;

import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.api.IOfflineSyncRecovery;
import com.jparavicini.sync_advancements.api.IPlayerInformation;
import com.jparavicini.sync_advancements.api.ISyncAction;
import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TogetherRegistries
{
    private static final HashMap<String, ISyncAction<?, ? extends IOfflineSyncRecovery>> SYNC_ACTION_REGISTRY = new HashMap<>();
    private static final HashMap<String, Class<? extends ITeam>> TEAM_REGISTRY = new HashMap<>();
    private static final HashMap<String, Class<? extends IPlayerInformation>> PLAYER_REGISTRY = new HashMap<>();

    /**
     * Registers a sync action
     *
     * @param id     The ID of the sync action
     * @param action The ISyncAction to be registered
     */
    public static void registerSyncAction(String id, ISyncAction<?, ? extends IOfflineSyncRecovery> action) {
        SyncAdvancements.LOGGER.info("Registering SyncAction with id " + id + " and class " + action.getClass().getName());
        SYNC_ACTION_REGISTRY.put(id, action);
    }

    /**
     * Gets a sync action id from it's recovery
     *
     * @param recovery The IOfflineSyncRecovery
     * @return The id of the SyncAction, null if doesn't exist
     */
    public static String getSyncActionIdFromOfflineRecovery(IOfflineSyncRecovery recovery) {
        for (String key : SYNC_ACTION_REGISTRY.keySet()) {
            if (SYNC_ACTION_REGISTRY.get(key).getOfflineRecovery().equals(recovery.getClass())) return key;
        }
        return null;
    }

    /**
     * Gets a SyncAction from it's id
     *
     * @param id The id of the SyncAction
     * @return the SyncAction, null if it doesn't exist
     */
    public static ISyncAction<?, ? extends IOfflineSyncRecovery> getSyncActionFromID(String id) {
        if (SYNC_ACTION_REGISTRY.containsKey(id)) {
            return SYNC_ACTION_REGISTRY.get(id);
        }
        return null;
    }

    /**
     * Registers a together team class
     *
     * @param id   The ID of the team type
     * @param team The class of the TogetherTeam
     */
    public static void registerTogetherTeam(String id, Class<? extends ITeam> team) {
        SyncAdvancements.LOGGER.info("Registering TogetherTeam with id " + id + " and class " + team.getName());
        TEAM_REGISTRY.put(id, team);
    }

    /**
     * Registers a PlayerInformation class
     *
     * @param id     The id of the PlayerInformation
     * @param player The PlayerInformation class
     */
    public static void registerPlayerInformation(String id, Class<? extends IPlayerInformation> player) {
        SyncAdvancements.LOGGER.info("Registering PlayerInformation with id " + id + " and class " + player.getName());
        PLAYER_REGISTRY.put(id, player);
    }

    /**
     * Gets the ID of a PlayerInformation
     *
     * @param aClass The class of the PlayerInformation
     * @return The ID, null if doesn't exist
     */
    public static String getPlayerInformationID(Class<? extends IPlayerInformation> aClass) {
        for (Map.Entry<String, Class<? extends IPlayerInformation>> stringClassEntry : PLAYER_REGISTRY.entrySet()) {
            if (aClass.equals(stringClassEntry.getValue())) return stringClassEntry.getKey();
        }
        return null;
    }

    /**
     * Gets the PlayerInformation class from it's id
     *
     * @param id The id of the PlayerInformation
     * @return The PlayerInformation class, null if it doesn't exist
     */
    public static Class<? extends IPlayerInformation> getPlayerInformationClass(String id) {
        if (PLAYER_REGISTRY.containsKey(id)) {
            return PLAYER_REGISTRY.get(id);
        }
        return null;
    }

    /**
     * Gets the ID of a TogetherTeam class
     *
     * @param aClass The Together team class
     * @return The id of the team, null if doesn't exist
     */
    public static String getTogetherTeamID(Class<? extends ITeam> aClass) {
        for (Map.Entry<String, Class<? extends ITeam>> stringClassEntry : TEAM_REGISTRY.entrySet()) {
            if (aClass.equals(stringClassEntry.getValue())) return stringClassEntry.getKey();
        }
        return null;
    }

    /**
     * Gets the TogetherTeam class for the ID
     *
     * @param id The id of the team
     * @return The TeamTogether class, null if it doesn't exist
     */
    public static Class<? extends ITeam> getTogetherTeamClass(String id) {
        if (TEAM_REGISTRY.containsKey(id)) {
            return TEAM_REGISTRY.get(id);
        }
        return null;
    }

    public static Collection<ISyncAction<?, ? extends IOfflineSyncRecovery>> getSyncActions() {
        return SYNC_ACTION_REGISTRY.values();
    }
}
