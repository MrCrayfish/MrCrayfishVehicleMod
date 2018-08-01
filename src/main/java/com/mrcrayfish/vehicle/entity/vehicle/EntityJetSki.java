package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EntitySeaVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
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
public class EntityJetSki extends EntitySeaVehicle implements IEntityRaytraceable
{
    public float prevLeanAngle;
    public float leanAngle;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityJetSki(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15F);
        this.setTurnSensitivity(15);
        this.setSize(1.5F, 1.0F);
        this.setHeldOffset(new Vec3d(6D, 0D, 0D));
        this.setTrailerOffset(new Vec3d(0D, -0.09375D, -0.65D));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.JET_SKI_BODY);
        handleBar = new ItemStack(ModItems.ATV_HANDLE_BAR);
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
        return 1.2F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 2.2F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 10 * 0.0625;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            float offset = 0.0F;
            float yOffset = (float)((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());

            if (this.getPassengers().size() > 1)
            {
                int index = this.getPassengers().indexOf(passenger);
                if (index > 0)
                {
                    offset += index * -0.5F;
                }
            }

            Vec3d vec3d = (new Vec3d((double)offset, 0.0D, 0.0D)).rotateYaw(-this.rotationYaw * 0.017453292F - ((float)Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double)yOffset, this.posZ + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 3;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
