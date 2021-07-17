package com.mrcrayfish.vehicle.entity.vehicle;

import com.mrcrayfish.vehicle.entity.LandVehicleEntity;
import com.mrcrayfish.vehicle.entity.trailer.StorageTrailerEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BushBlock;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class LawnMowerEntity extends LandVehicleEntity
{
    public LawnMowerEntity(EntityType<? extends LawnMowerEntity> type, World worldIn)
    {
        super(type, worldIn);
        this.setMaxSpeed(8);
        this.setFuelCapacity(5000F);
    }

    @Override
    public void updateVehicle()
    {
        super.updateVehicle();

        if(!level.isClientSide && this.getControllingPassenger() != null)
        {
            AxisAlignedBB axisAligned = this.getBoundingBox().inflate(0.25);
            Vector3d lookVec = this.getLookAngle().scale(0.5);
            int minX = MathHelper.floor(axisAligned.minX + lookVec.x);
            int maxX = MathHelper.ceil(axisAligned.maxX + lookVec.x);
            int minZ = MathHelper.floor(axisAligned.minZ + lookVec.z);
            int maxZ = MathHelper.ceil(axisAligned.maxZ + lookVec.z);

            for(int x = minX; x < maxX; x++)
            {
                for(int z = minZ; z < maxZ; z++)
                {
                    BlockPos pos = new BlockPos(x, axisAligned.minY + 0.5, z);
                    BlockState state = level.getBlockState(pos);

                    StorageTrailerEntity trailer = null;
                    if(getTrailer() instanceof StorageTrailerEntity)
                    {
                        trailer = (StorageTrailerEntity) getTrailer();
                    }

                    if(state.getBlock() instanceof BushBlock)
                    {
                        List<ItemStack> drops = Block.getDrops(state, (ServerWorld) level, pos, null);
                        for(ItemStack stack : drops)
                        {
                            this.addItemToStorage(trailer, stack);
                        }
                        level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
                        level.playSound(null, pos, state.getBlock().getSoundType(state, level, pos, this).getBreakSound(), SoundCategory.BLOCKS, 1.0F, 1.0F);
                        level.levelEvent(2001, pos, Block.getId(state));
                    }
                }
            }
        }
    }

    private void addItemToStorage(StorageTrailerEntity storageTrailer, ItemStack stack)
    {
        if(stack.isEmpty())
            return;

        if(storageTrailer != null && storageTrailer.getInventory() != null)
        {
            Inventory storage = storageTrailer.getInventory();
            stack = storage.addItem(stack);
            if(!stack.isEmpty())
            {
                if(storageTrailer.getTrailer() instanceof StorageTrailerEntity)
                {
                    this.addItemToStorage((StorageTrailerEntity) storageTrailer.getTrailer(), stack);
                }
                else
                {
                    spawnItemStack(level, stack);
                }
            }
        }
        else
        {
            spawnItemStack(level, stack);
        }
    }

    private void spawnItemStack(World worldIn, ItemStack stack)
    {
        while(!stack.isEmpty())
        {
            ItemEntity itemEntity = new ItemEntity(worldIn, xo, yo, zo, stack.split(random.nextInt(21) + 10));
            itemEntity.setPickUpDelay(20);
            itemEntity.setDeltaMovement(-this.getDeltaMovement().x / 4.0, random.nextGaussian() * 0.05D + 0.2D, -this.getDeltaMovement().z / 4.0);
            worldIn.addFreshEntity(itemEntity);
        }
    }

    @Override
    public SoundEvent getEngineSound()
    {
        return ModSounds.ENTITY_ATV_ENGINE.get();
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
