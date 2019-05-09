package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityTractor extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityTractor(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(6);
        this.setTurnSensitivity(3);
        this.setSize(1.5F, 1.5F);
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.TRACTOR_BODY);
        engine = new ItemStack(ModItems.LARGE_ENGINE);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.tractorEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.tractorEngineStereo;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.6F;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.LARGE_MOTOR;
    }

    @Override
    public double getMountedYOffset()
    {
        return 12 * 0.0625;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return true;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(-0.125, 1.9375, 1.125);
    }

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            float yOffset = (float) ((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());
            float zOffset = -10F * 0.0625F;
            Vec3d vec3d = new Vec3d(zOffset, yOffset, 0).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + vec3d.y, this.posZ + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }
}
