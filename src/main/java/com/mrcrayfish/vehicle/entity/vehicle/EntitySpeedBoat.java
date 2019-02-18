package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityBoat;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntitySpeedBoat extends EntityBoat implements IEntityRaytraceable
{
    public static final float WHEEL_OFFSET = 2.5F;
    public static final PartPosition BODY_POSITION = new PartPosition(0.0, -0.03125, 0.6875, 1.0);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-12.25, 17.25, -19.5, 0.0, -90.0, 0.0, 1.0);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(6.0, -0.5, 0.0);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0.0, -0.09375, -0.75);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntitySpeedBoat(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(20F);
        this.setTurnSensitivity(15);
        this.setSize(1.5F, 1.0F);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
        this.setFuelCapacity(25000F);
        this.setFuelConsumption(3.0F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.SPEED_BOAT_BODY);
        handleBar = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
    }

    @Override
    public void createParticles()
    {
        if(state == State.ON_WATER)
        {
            if(this.getAcceleration() == AccelerationDirection.FORWARD)
            {
                for(int i = 0; i < 5; i++)
                {
                    this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 1.5D, -this.motionZ * 4.0D);
                }

                for(int i = 0; i < 5; i++)
                {
                    this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 2.0D, 0.0D, -this.motionZ * 2.0D);
                }
            }
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.speedBoatEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.speedBoatEngineStereo;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.LARGE_MOTOR;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 1.0F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 2.0F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }
}
