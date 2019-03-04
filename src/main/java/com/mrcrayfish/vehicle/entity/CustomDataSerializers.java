package com.mrcrayfish.vehicle.entity;

import net.minecraft.item.EnumDyeColor;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;

/**
 * Author: MrCrayfish
 */
public class CustomDataSerializers
{
    public static final DataSerializer<EnumDyeColor> DYE_COLOR = new DataSerializer<EnumDyeColor>()
    {
        @Override
        public void write(PacketBuffer buf, EnumDyeColor value)
        {
            buf.writeInt(value.getDyeDamage());
        }

        @Override
        public EnumDyeColor read(PacketBuffer buf)
        {
            return EnumDyeColor.byDyeDamage(buf.readInt());
        }

        @Override
        public DataParameter<EnumDyeColor> createKey(int id)
        {
            return new DataParameter<>(id, this);
        }

        @Override
        public EnumDyeColor copyValue(EnumDyeColor value)
        {
            return value;
        }
    };

    public static void register()
    {
        DataSerializers.registerSerializer(DYE_COLOR);
    }
}
