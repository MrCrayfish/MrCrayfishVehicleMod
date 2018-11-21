package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityOffRoader extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -1.0F;
    public static final float WHEEL_OFFSET = 5.4F;
    public static final PartPosition BODY_POSITION = new PartPosition(0, 0, -0.125, 0, 0, 0, 1.4);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-12.25, 8.5, -7.3, 0, -90, 0, 0.25);
    public static final PartPosition KEY_PORT_POSITION = new PartPosition(0, 7, 6.2, -67.5, 0, 0, 0.5);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(0.0D, 3.5D, 0.0D);

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityOffRoader(World worldIn)
    {
        super(worldIn);
        this.setSize(2F, 1F);
        this.setMaxSpeed(18F);
        this.setFuelCapacity(25000F);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setKeyHolePosition(KEY_PORT_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.OFF_ROADER_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
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
    public EngineType getEngineType()
    {
        return EngineType.LARGE_MOTOR;
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
    public double getMountedYOffset()
    {
        return 12 * 0.0625;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if (this.isPassenger(passenger))
        {
            float xOffset = -0.3875F;
            float yOffset = (float)((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());
            float zOffset = 0.4F;

            if (this.getPassengers().size() > 0)
            {
                int index = this.getPassengers().indexOf(passenger);
                if (index > 0)
                {
                    xOffset -= (index / 2) * 1.0F;
                    zOffset -= (index % 2) * 0.8125F;
                }

                if(index == 2)
                {
                    yOffset += 0.625F;
                }
                else if(index == 3)
                {
                    xOffset -= 0.4375F;
                }

                Vec3d vec3d = (new Vec3d(xOffset, 0.0D, zOffset)).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float)Math.PI / 2F));
                passenger.setPosition(this.posX + vec3d.x, this.posY + (double)yOffset, this.posZ + vec3d.z);
                passenger.rotationYaw -= deltaYaw;
                passenger.setRotationYawHead(passenger.rotationYaw);
                this.applyYawToEntity(passenger);
            }
        }
    }

    @Override
    public void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw - this.additionalYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        this.applyYawToEntity(entityToUpdate);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 4;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
