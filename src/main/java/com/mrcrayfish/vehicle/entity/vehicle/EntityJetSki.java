package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.EntityColoredSeaVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityJetSki extends EntityColoredSeaVehicle
{
    public float prevLeanAngle;
    public float leanAngle;

    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack handle_bar;

    public EntityJetSki(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(15F);
    }

    @Override
    public void entityInit()
    {
        super.entityInit();

        if(world.isRemote)
        {
            body = new ItemStack(ModItems.JET_SKI_BODY);
            handle_bar = new ItemStack(ModItems.ATV_HANDLE_BAR);
        }
    }

    @Override
    public void createParticles()
    {
        if(this.getAcceleration() == AccelerationDirection.FORWARD)
        {
            for(int i = 0; i < 5; i++)
            {
                this.world.spawnParticle(EnumParticleTypes.WATER_SPLASH, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 4.0D, 0.0D, -this.motionZ * 4.0D);
            }

            for(int i = 0; i < 5; i++)
            {
                this.world.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, this.getEntityBoundingBox().minY + 0.1D, this.posZ + ((double) this.rand.nextFloat() - 0.5D) * (double) this.width, -this.motionX * 2.0D, 0.0D, -this.motionZ * 2.0D);
            }
        }
    }

    @Override
    public void updateVehicle()
    {
        this.prevLeanAngle = this.leanAngle;
        this.leanAngle = this.turnAngle / (float) getMaxTurnAngle();
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
        return 10 * 0.0625;
    }
}
