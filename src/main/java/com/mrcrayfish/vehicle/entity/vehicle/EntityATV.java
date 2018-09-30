package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
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
public class EntityATV extends EntityLandVehicle implements IEntityRaytraceable
{
    public static final float AXLE_OFFSET = -1.5F;
    public static final float WHEEL_OFFSET = 4.375F;
    public static final PartPosition BODY_POSITION = new PartPosition(0.0F, 0.0F, 0.25F, 0.0F, 0.0F, 0.0F, 1.25F);
    public static final PartPosition FUEL_PORT_POSITION = new PartPosition(-1.57F, 6.55F, 5.3F, -90.0F, 0.0F, 0.0F, 0.35F);
    public static final PartPosition KEY_PORT_POSITION = new PartPosition(-5F, 4.5F, 6.5F, -45.0F, 0.0F, 0.0F, 0.5F);
    private static final Vec3d HELD_OFFSET_VEC = new Vec3d(4.0D, 3.5D, 0.0D);
    private static final Vec3d TOW_BAR_VEC = new Vec3d(0.0D, 0.0D, -20.8D);
    private static final Vec3d TRAILER_OFFSET_VEC = new Vec3d(0.0D, 0.0D, -0.55D); //TODO may be able to get rid of this

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityATV(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15);
        this.setSize(1.5F, 1.0F);
        this.setFuelCapacity(20000F);
        this.setAxleOffset(AXLE_OFFSET);
        this.setWheelOffset(WHEEL_OFFSET);
        this.setBodyPosition(BODY_POSITION);
        this.setKeyHolePosition(KEY_PORT_POSITION);
        this.setHeldOffset(HELD_OFFSET_VEC);
        this.setTowBarPosition(TOW_BAR_VEC);
        this.setTrailerOffset(TRAILER_OFFSET_VEC);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        this.body = new ItemStack(ModItems.ATV_BODY);
        this.handleBar = new ItemStack(ModItems.ATV_HANDLE_BAR);
        this.wheel = new ItemStack(ModItems.WHEEL);
        this.setFuelPort(FuelPort.CAP);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ATV_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ATV_ENGINE_STEREO;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9.5 * 0.0625;
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

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 2;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(this.isPassenger(passenger))
        {
            float offset = 0.0F;
            float yOffset = (float) ((this.isDead ? 0.01D : this.getMountedYOffset()) + passenger.getYOffset());

            if(this.getPassengers().size() > 1)
            {
                int index = this.getPassengers().indexOf(passenger);
                if(index > 0)
                {
                    offset += index * -0.625F;
                    yOffset += 0.1F;
                }
            }

            Vec3d vec3d = (new Vec3d((double) offset, 0.0D, 0.0D)).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float) Math.PI / 2F));
            passenger.setPosition(this.posX + vec3d.x, this.posY + (double) yOffset, this.posZ + vec3d.z);
            passenger.rotationYaw -= deltaYaw;
            passenger.setRotationYawHead(passenger.rotationYaw);
            this.applyYawToEntity(passenger);
        }
    }

    @Override
    protected void applyYawToEntity(Entity entityToUpdate)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw - this.additionalYaw);
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw);
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }
}
