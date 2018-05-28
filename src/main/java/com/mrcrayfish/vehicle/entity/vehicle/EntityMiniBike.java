package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredMotorcycle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityMiniBike extends EntityColoredMotorcycle
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    public EntityMiniBike(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(18F);
        this.setTurnSensitivity(12);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.MINI_BIKE_BODY);
            wheel = new ItemStack(ModItems.WHEEL);
            handleBar = new ItemStack(ModItems.MINI_BIKE_HANDLE_BAR);
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                body.setItemDamage(this.dataManager.get(COLOR));
                handleBar.setItemDamage(this.dataManager.get(COLOR));
            }
        }
    }

    @Override
    public SoundEvent getMovingSound()
    {
        return ModSounds.GO_KART_ENGINE_MONO;
    }

    @Override
    public SoundEvent getRidingSound()
    {
        return ModSounds.GO_KART_ENGINE_STEREO;
    }

    @Override
    public float getMinEnginePitch()
    {
        return 0.5F;
    }

    @Override
    public float getMaxEnginePitch()
    {
        return 1.8F;
    }

    @Override
    public boolean shouldShowEngineSmoke()
    {
        return true;
    }

    @Override
    public Vec3d getEngineSmokePosition()
    {
        return new Vec3d(0, 0.55, 0);
    }

    @Override
    public double getMountedYOffset()
    {
        return 9.5 * 0.0625;
    }
}
