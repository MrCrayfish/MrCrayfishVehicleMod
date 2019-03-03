package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
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
public class EntityATV extends EntityLandVehicle implements IEntityRaytraceable
{
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
        return ModSounds.atvEngineMono;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.atvEngineStereo;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9.5 * 0.0625;
    }

    @Override
    public EngineType getEngineType()
    {
        return EngineType.SMALL_MOTOR;
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
}
