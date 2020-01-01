package com.mrcrayfish.vehicle.entity;

import net.minecraft.item.DyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.IDataSerializer;

/**
 * Author: MrCrayfish
 */
public class CustomDataSerializers
{
    public static final IDataSerializer<DyeColor> DYE_COLOR = new IDataSerializer<DyeColor>()
    {
        @Override
        public void write(PacketBuffer buf, DyeColor value)
        {
            buf.writeInt(value.getId());
        }

        @Override
        public DyeColor read(PacketBuffer buf)
        {
            return DyeColor.byId(buf.readInt());
        }

        @Override
        public DataParameter<DyeColor> createKey(int id)
        {
            return new DataParameter<>(id, this);
        }

        @Override
        public DyeColor copyValue(DyeColor value)
        {
            return value;
        }
    };

    public static void register()
    {
        DataSerializers.registerSerializer(DYE_COLOR);
    }
}
