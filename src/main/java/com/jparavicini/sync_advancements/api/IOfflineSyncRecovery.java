package com.jparavicini.sync_advancements.api;

import net.minecraft.nbt.CompoundNBT;

import java.util.List;

public interface IOfflineSyncRecovery
{
    /**
     * Stores a list of players into the system
     *
     * @param playersInformation The List of players that will need recovery
     * @param store              The NBT necessary to do a recovery when possible
     */
    void storeMissingPlayers(List<IPlayerInformation> playersInformation, CompoundNBT store);

    /**
     * Stores a player into the system
     *
     * @param playerInformation The Player that will need recovery
     * @param store             The NBT necessary to do a recovery when possible
     */
    void storeMissingPlayer(IPlayerInformation playerInformation, CompoundNBT store);

    /**
     * Syncs all the actions that a player missed when he was offline
     *
     * @param playerInformation The Player information that needs a recovery
     */
    void recoverMissingPlayer(IPlayerInformation playerInformation);

    /**
     * Transforms the OfflineSyncRecovery into NBT to be stored in the world so it can be saved properly
     *
     * @return The OfflineSyncRecovery transformed into NBT
     */
    CompoundNBT writeToNBT();

    /**
     * Reads all the information stored as NBT into the OfflineSyncRecovery
     *
     * @param compound The OfflineSyncRecovery previously stored as NBT
     */
    void readFromNBT(CompoundNBT compound);
}
