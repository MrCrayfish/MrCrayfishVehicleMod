package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.client.EntityRaytracer.IEntityRaytraceable;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.trailer.EntityStorageTrailer;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

/**
 * Author: MrCrayfish
 */
public class EntityLawnMower extends EntityLandVehicle implements IEntityRaytraceable
{
    public EntityLawnMower(World worldIn)
    {
        super(worldIn);
        this.setMaxSpeed(8);
        this.setSize(1.2F, 1.0F);
        this.setFuelCapacity(5000F);
    }

    @Override
    public void updateVehicle()
    {
        super.updateVehicle();

        if(!world.isRemote && this.getControllingPassenger() != null)
        {
            AxisAlignedBB axisAligned = this.getEntityBoundingBox().grow(0.25);
            Vec3d lookVec = this.getLookVec().scale(0.5);
            int minX = MathHelper.floor(axisAligned.minX + lookVec.x);
            int maxX = MathHelper.ceil(axisAligned.maxX + lookVec.x);
            int minZ = MathHelper.floor(axisAligned.minZ + lookVec.z);
            int maxZ = MathHelper.ceil(axisAligned.maxZ + lookVec.z);

            for(int x = minX; x < maxX; x++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, axisAligned.minY + 0.5, z);
                    IBlockState state = world.getBlockState(pos);

                    EntityStorageTrailer trailer = null;
                    if(getTrailer() instanceof EntityStorageTrailer)
                    {
                        trailer = (EntityStorageTrailer) getTrailer();
                    }

                    if(state.getBlock() instanceof BlockBush)
                    {
                        NonNullList<ItemStack> drops = NonNullList.create();
                        state.getBlock().getDrops(drops, world, pos, state, 1);

                        for(ItemStack stack : drops)
                        {
                            this.addItemToStorage(trailer, stack);
                        }

                        world.setBlockToAir(pos);
                        world.playSound(null, pos, state.getBlock().getSoundType(state, world, pos, this).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                        world.playEvent(2001, pos, Block.getStateId(state));
                    }
                }
            }
        }
    }

    private void addItemToStorage(EntityStorageTrailer storageTrailer, ItemStack stack)
    {
        if(stack.isEmpty())
            return;

        if(storageTrailer != null && storageTrailer.getInventory() != null)
        {
            StorageInventory storage = storageTrailer.getInventory();
            storage.addItemStack(stack);
            if(!stack.isEmpty())
            {
                if(storageTrailer.getTrailer() instanceof EntityStorageTrailer)
                {
                    this.addItemToStorage((EntityStorageTrailer) storageTrailer.getTrailer(), stack);
                }
                else
                {
                    spawnItemStack(world, stack);
                }
            }
        }
        else
        {
            spawnItemStack(world, stack);
        }
    }

    private void spawnItemStack(World worldIn, ItemStack stack)
    {
        while(!stack.isEmpty())
        {
            EntityItem entityItem = new EntityItem(worldIn, prevPosX, prevPosY, prevPosZ, stack.splitStack(rand.nextInt(21) + 10));
            entityItem.setPickupDelay(20);
            entityItem.motionX = -this.motionX / 4.0;
            entityItem.motionY = rand.nextGaussian() * 0.05000000074505806D + 0.20000000298023224D;
            entityItem.motionZ = -this.motionZ / 4.0;
            worldIn.spawnEntity(entityItem);
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

    //TODO remove and add key support
    @Override
    public boolean isLockable()
    {
        return false;
    }
}
