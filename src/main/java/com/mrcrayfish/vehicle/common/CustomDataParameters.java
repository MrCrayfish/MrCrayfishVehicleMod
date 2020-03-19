package com.mrcrayfish.vehicle.common;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class CustomDataParameters
{
    public static final DataParameter<CompoundNBT> HELD_VEHICLE = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.COMPOUND_NBT);
    public static final DataParameter<Integer> TRAILER = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.VARINT);
    public static final DataParameter<Optional<BlockPos>> GAS_PUMP = EntityDataManager.createKey(PlayerEntity.class, DataSerializers.OPTIONAL_BLOCK_POS);
}
