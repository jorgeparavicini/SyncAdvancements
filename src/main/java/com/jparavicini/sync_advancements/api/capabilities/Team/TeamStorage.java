package com.jparavicini.sync_advancements.api.capabilities.Team;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TeamStorage implements Capability.IStorage<ITeam>
{

    @Nullable
    @Override
    public INBT writeNBT(final Capability<ITeam> capability, final ITeam instance, final Direction side)
    {
        return instance.getNBTTag();
    }

    @Override
    public void readNBT(final Capability<ITeam> capability, final ITeam instance, final Direction side, final INBT nbt)
    {
        instance.readFromNBT((CompoundNBT) nbt);
    }
}
