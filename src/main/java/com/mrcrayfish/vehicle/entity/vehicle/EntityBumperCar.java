package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityBumperCar extends EntityColoredVehicle
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityBumperCar(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(10);
        this.setSize(1.5F, 1.0F);
        this.setTurnSensitivity(20);
        this.stepHeight = 0.625F;
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.BUMPER_CAR_BODY);
            wheel = new ItemStack(ModItems.WHEEL);
            steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
        }
    }

    @Override
    public void applyEntityCollision(Entity entityIn)
    {
        if(entityIn instanceof EntityBumperCar && this.isBeingRidden())
        {
            applyBumperCollision((EntityBumperCar) entityIn);
        }
    }

    private void applyBumperCollision(EntityBumperCar entity)
    {
        entity.additionalMotionX += motionX * 2 * currentSpeed;
        entity.additionalMotionZ += motionZ * 2 * currentSpeed;
        world.playSound(null, this.posX, this.posY, this.posZ, ModSounds.BONK, SoundCategory.NEUTRAL, 1.0F, 0.6F + 0.1F * this.getNormalSpeed());
        this.currentSpeed *= 0.25F;
    }

    @Override
    public boolean shouldRenderEngine()
    {
        return false;
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
    public float getMaxEnginePitch()
    {
        return 0.8F;
    }

    @Override
    public double getMountedYOffset()
    {
        return 3 * 0.0625F;
    }
}
