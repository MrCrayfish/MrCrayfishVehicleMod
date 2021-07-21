package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.EntityType;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class MiniBusEntity extends LandVehicleEntity
{
    public MiniBusEntity(EntityType<? extends MiniBusEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(15F);
        this.setTurnSensitivity(4);
        this.setFuelCapacity(30000F);
        this.setFuelConsumption(0.375F);
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_MINI_BUS_ENGINE.get();
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.75F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.25F;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }
}
