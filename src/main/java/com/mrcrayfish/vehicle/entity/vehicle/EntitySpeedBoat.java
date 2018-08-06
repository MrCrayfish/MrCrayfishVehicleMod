package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EntitySeaVehicle;
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
public class EntitySpeedBoat extends EntitySeaVehicle implements IEntityRaytraceable
{
    public float prevLeanAngle;
    public float leanAngle;

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
        this.setHeldOffset(new Vec3d(6D, -0.5D, 0D));
        this.setTrailerOffset(new Vec3d(0D, -0.09375D, -0.75D));
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
    public void updateVehicle()
    {
        super.updateVehicle();
        this.prevLeanAngle = this.leanAngle;
        this.leanAngle = this.turnAngle / (float) getMaxTurnAngle();
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.SPEED_BOAT_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.SPEED_BOAT_ENGINE_STEREO;
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
        return 4 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
