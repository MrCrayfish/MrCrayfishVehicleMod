package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
public class EntityLawnMower extends EntityLandVehicle implements IEntityRaytraceable
{
    /**
     * ItemStack instances used for rendering
     */
    @SideOnly(Side.CLIENT)
    public ItemStack steeringWheel;

    public EntityLawnMower(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(8);
        this.setSize(1.2F, 1.0F);
        this.setHeldOffset(new Vec3d(4D, 3.5D, 0D));
        this.setTowBarPosition(new Vec3d(0.0, 0.0, -0.6));
        this.setTrailerOffset(new Vec3d(0D, -0.01D, -1.0D));
        this.setFuelCapacity(5F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onClientInit()
    {
        super.onClientInit();
        body = new ItemStack(ModItems.LAWN_MOWER_BODY);
        steeringWheel = new ItemStack(ModItems.GO_KART_STEERING_WHEEL);
        wheel = new ItemStack(ModItems.WHEEL);
    }

    @Override
    public void updateVehicle()
    {
        super.updateVehicle();

        if(this.getControllingPassenger() != null)
        {
            AxisAlignedBB axisAligned = this.getEntityBoundingBox();
            Vec3d lookVec = this.getLookVec().scale(0.5);
            int minX = MathHelper.floor(axisAligned.minX + lookVec.x);
            int maxX = MathHelper.ceil(axisAligned.maxX + lookVec.x);
            int minZ = MathHelper.floor(axisAligned.minZ + lookVec.z);
            int maxZ = MathHelper.ceil(axisAligned.maxZ + lookVec.z);

            for(int x = minX; x < maxX; x++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, axisAligned.minY, z);
                    IBlockState state = world.getBlockState(pos);
                    if(state.getBlock() instanceof BlockTallGrass)
                    {
                        BlockTallGrass.EnumType type = state.getValue(BlockTallGrass.TYPE);
                        if(type == BlockTallGrass.EnumType.GRASS || type == BlockTallGrass.EnumType.FERN)
                        {
                            world.setBlockToAir(pos);
                            world.playSound(null, pos, SoundType.PLANT.getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            world.playEvent(2001, pos, Block.getStateId(state));
                        }
                    }
                    else if(state.getBlock() instanceof BlockDoublePlant)
                    {
                        BlockDoublePlant.EnumPlantType type = state.getValue(BlockDoublePlant.VARIANT);
                        if(type == BlockDoublePlant.EnumPlantType.GRASS || type == BlockDoublePlant.EnumPlantType.FERN)
                        {
                            world.setBlockToAir(pos);
                            world.playSound(null, pos, SoundType.PLANT.getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                            world.playEvent(2001, pos, Block.getStateId(state));
                        }
                    }
                }
            }
        }
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
        return 11.5 * 0.0625;
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

    @Override
    public boolean canTowTrailer()
    {
        return true;
    }
}
