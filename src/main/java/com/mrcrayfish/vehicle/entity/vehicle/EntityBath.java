package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityPlane;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityBath extends EntityPlane implements IEntityRaytraceable
{
    static
    {
        VehicleProperties properties = new VehicleProperties();
        properties.setBodyPosition(new PartPosition(0, 0, 0, 0, 0, 0, 1.0));
        properties.setHeldOffset(new Vec3d(4.0, 3.5, 0.0));
        properties.setTrailerOffset(new Vec3d(0.0, 0.0, -0.4375));
        VehicleProperties.setProperties(EntityBath.class, properties);
    }

    public EntityBath(World worldIn)
    {
        super(worldIn);
        this.setFuelConsumption(0.0F);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return null;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return null;
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(Item.getByNameOrId("cfm:bath_bottom"), 1, 0);
    }

    @Override
    public void updateVehicle()
    {
        if(this.isFlying() && this.getControllingPassenger() != null)
        {
            for(int i = 0; i < 4; i++)
            {
                world.spawnParticle(EnumParticleTypes.DRIP_WATER, posX - 0.25 + 0.5 * rand.nextGaussian(), posY + 0.5 * rand.nextGaussian(), posZ - 0.25 + 0.5 * rand.nextGaussian(), 0, 0, 0, 0);
            }
        }
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.NONE;
    }

    @Override
    public boolean canBeColored()
    {
        return false;
    }

    @Override
    public boolean isLockable()
    {
        return false;
    }
}
