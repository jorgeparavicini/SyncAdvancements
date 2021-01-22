package com.jparavicini.sync_advancements.api.capabilities.Team;

import com.jparavicini.sync_advancements.SyncAdvancements;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = SyncAdvancements.MOD_NAME, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TeamHandler
{
    public static final ResourceLocation TEAM_DATA = new ResourceLocation(SyncAdvancements.MOD_NAME, "api/capabilities/defaultteam.java");

    @SubscribeEvent
    public static void attachCapability(AttachCapabilitiesEvent<World> event) {
        event.addCapability(TEAM_DATA, new TeamProvider());
    }
}
