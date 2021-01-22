package com.jparavicini.sync_advancements.api.capabilities.Team;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeamProvider implements ICapabilitySerializable<INBT>
{
    @CapabilityInject(ITeam.class)
    public static final Capability<ITeam> CAPABILITY = null;

    private final LazyOptional<ITeam> instance = LazyOptional.of(CAPABILITY::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull final Capability<T> cap, @Nullable final Direction side)
    {
        return cap == CAPABILITY ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT()
    {
        return CAPABILITY.getStorage().writeNBT(
                CAPABILITY,
                this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")),
                null);
    }

    @Override
    public void deserializeNBT(final INBT nbt)
    {
        CAPABILITY.getStorage().readNBT(
                CAPABILITY,
                this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")),
                null,
                nbt);
    }
}
