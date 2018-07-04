package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityMotorcycle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityMoped extends EntityMotorcycle
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handleBar;

    @SideOnly(Side.CLIENT)
    public ItemStack mudGuard;

    public EntityMoped(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(12F);
        this.setTurnSensitivity(15);
        this.setMaxTurnAngle(45);
        this.setHeldOffset(new Vec3d(6D, 0D, 0D));
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        body = new ItemStack(ModItems.MOPED_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
        handleBar = new ItemStack(ModItems.MOPED_HANDLE_BAR);
        mudGuard = new ItemStack(ModItems.MOPED_MUD_GUARD);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                int color = this.dataManager.get(COLOR);
                this.setPartColor(handleBar, color);
                this.setPartColor(mudGuard, color);
            }
        }
    }

    private void setPartColor(ItemStack stack, int color)
    {
        if(!stack.hasTagCompound())
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("color", color);
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
    public boolean shouldRenderEngine()
    {
        return false;
    }

    @Override
    public double getMountedYOffset()
    {
        return 8.5 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }
}
