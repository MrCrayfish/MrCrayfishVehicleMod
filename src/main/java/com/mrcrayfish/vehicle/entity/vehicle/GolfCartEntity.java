package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.HelicopterEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class GolfCartEntity extends HelicopterEntity
{
    public GolfCartEntity(EntityType<? extends GolfCartEntity> type, World worldIn)
    {
        super(type, worldIn);
        //TODO figure out electric vehicles
    }
}
