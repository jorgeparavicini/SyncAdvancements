package com.jparavicini.sync_advancements.api.action;

import com.jparavicini.sync_advancements.SyncAdvancements;
import com.jparavicini.sync_advancements.api.IOfflineSyncRecovery;
import com.jparavicini.sync_advancements.api.ISyncAction;
import com.jparavicini.sync_advancements.api.SyncAdvancementsAPI;
import com.sun.corba.se.impl.orbutil.concurrent.Sync;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public abstract class EventSyncAction<T extends PlayerEvent, S extends IOfflineSyncRecovery> implements ISyncAction<T, S>
{
    private final Class<T> eventClass;
    private final Class<S> recovery;

    public EventSyncAction(Class<T> eventClass, Class<S> offlineRecovery) {
        this.recovery = offlineRecovery;
        this.eventClass = eventClass;
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent(receiveCanceled = true)
    public void onEvent(T event) {
        if (!event.getClass().equals(eventClass)) return;

        SyncAdvancements.LOGGER.debug("Triggering event class: " + event.getClass().toString());

        if (SyncAdvancementsAPI.getInstance().getWorld() == null) return;
        SyncAdvancements.LOGGER.debug("World is not null");
    }
}
