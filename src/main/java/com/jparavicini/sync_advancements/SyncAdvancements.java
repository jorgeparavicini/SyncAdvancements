package com.jparavicini.sync_advancements;

import com.jparavicini.sync_advancements.api.SyncAdvancementsAPI;
import com.jparavicini.sync_advancements.api.capabilities.Team.ITeam;
import com.jparavicini.sync_advancements.api.capabilities.Team.DefaultTeam;
import com.jparavicini.sync_advancements.api.capabilities.Team.TeamStorage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SyncAdvancementsAPI.MOD_NAME)
public class SyncAdvancements
{

    // Directly reference a log4j logger.
    public static final Logger LOGGER = LogManager.getLogger();

    public SyncAdvancements() {
        // Register the setup method for mod loading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        CapabilityManager.INSTANCE.register(ITeam.class, new TeamStorage(), DefaultTeam::new);
    }

    private void registerSyncActions() {
    }
}
