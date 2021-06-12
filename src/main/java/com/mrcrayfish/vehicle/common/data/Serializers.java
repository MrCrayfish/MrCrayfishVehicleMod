package com.mrcrayfish.vehicle.common.data;

import com.mrcrayfish.obfuscate.common.data.IDataSerializer;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;

import java.util.Optional;

/**
 * Author: MrCrayfish
 */
public class Serializers
{
    public static final IDataSerializer<Optional<BlockPos>> OPTIONAL_BLOCK_POS = new IDataSerializer<Optional<BlockPos>>()
    {
        @Override
        public void write(PacketBuffer buffer, Optional<BlockPos> optional)
        {
            buffer.writeBoolean(optional.isPresent());
            optional.ifPresent(buffer::writeBlockPos);
        }

        @Override
        public Optional<BlockPos> read(PacketBuffer buffer)
        {
            if(buffer.readBoolean())
            {
                return Optional.of(buffer.readBlockPos());
            }
            return Optional.empty();
        }

        @Override
        public INBT write(Optional<BlockPos> value)
        {
            CompoundNBT compound = new CompoundNBT();
            compound.putBoolean("Present", value.isPresent());
            value.ifPresent(blockPos -> compound.putLong("BlockPos", value.get().asLong()));
            return compound;
        }

        @Override
        public Optional<BlockPos> read(INBT nbt)
        {
            CompoundNBT compound = (CompoundNBT) nbt;
            if(compound.getBoolean("Present"))
            {
                BlockPos pos = BlockPos.of(compound.getLong("BlockPos"));
                return Optional.of(pos);
            }
            return Optional.empty();
        }
    };
}
