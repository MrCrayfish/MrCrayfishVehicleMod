package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer;
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
public class EntityGolfCart extends EntityLandVehicle implements EntityRaytracer.IEntityRaytraceable
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityGolfCart(World worldIn)
    {
        super(worldIn);
        this.setSize(2F, 1F);
    }

    @Override
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.GOLF_CART_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.ELECTRIC_ENGINE_STEREO;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.6F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.4F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 11 * 0.0625;
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
                    xOffset -= (index / 2) * 0.6875F;
                    zOffset -= (index % 2) * 0.8F;
                }

                Vec3d vec3d = (new Vec3d(xOffset, 0.0D, zOffset)).rotateYaw(-(this.rotationYaw - additionalYaw) * 0.017453292F - ((float)Math.PI / 2F));
                passenger.setPosition(this.posX + vec3d.x, this.posY + (double)yOffset, this.posZ + vec3d.z);
                passenger.rotationYaw -= deltaYaw;
                passenger.setRotationYawHead(passenger.rotationYaw);
                this.applyYawToEntity(passenger, index > 1);
            }
        }
    }

    private void applyYawToEntity(Entity entityToUpdate, boolean isBackSeat)
    {
        entityToUpdate.setRenderYawOffset(this.rotationYaw - this.additionalYaw + (isBackSeat ? 180F : 0F));
        float f = MathHelper.wrapDegrees(entityToUpdate.rotationYaw - this.rotationYaw + (isBackSeat ? 180F : 0F));
        float f1 = MathHelper.clamp(f, -120.0F, 120.0F);
        entityToUpdate.prevRotationYaw += f1 - f;
        entityToUpdate.rotationYaw += f1 - f;
        entityToUpdate.setRotationYawHead(entityToUpdate.rotationYaw);
    }

    @SideOnly(Side.CLIENT)
    public void applyOrientationToEntity(Entity entityToUpdate)
    {
        int index = this.getPassengers().indexOf(entityToUpdate);
        this.applyYawToEntity(entityToUpdate, index > 1);
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return this.getPassengers().size() < 4;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return false;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
