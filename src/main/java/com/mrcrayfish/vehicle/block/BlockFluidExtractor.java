package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.FluidExtractorTileEntity;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockFluidExtractor extends BlockRotatedObject
{
    public BlockFluidExtractor()
    {
        super(Names.Block.FLUID_EXTRACTOR, Block.Properties.create(Material.ANVIL).hardnessAndResistance(1.0F));
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(!world.isRemote)
        {
            ItemStack stack = playerEntity.getHeldItem(hand);
            if(stack.getItem() == Items.BUCKET)
            {
                FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getFace());
                //if())
                //{
                    /*TileEntity tileEntity = world.getPipeTileEntity(pos);
                    if(tileEntity instanceof FluidExtractorTileEntity)
                    {
                        ((FluidExtractorTileEntity) tileEntity).syncFluidLevelToClients();
                    }*/
                //}
                return ActionResultType.SUCCESS;
            }

            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof INamedContainerProvider)
            {
                NetworkHooks.openGui((ServerPlayerEntity) playerEntity, (INamedContainerProvider) tileEntity, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }

    /*@Override
        //public boolean onBlockActivated(World worldIn, BlockPos pos, BlockState state, PlayerEntity playerIn, Hand hand, Direction facing, float hitX, float hitY, float hitZ)
        {

        }
    */
    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
    {
        if(!world.isRemote && !player.isCreative())
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof FluidExtractorTileEntity)
            {
                FluidExtractorTileEntity fluidExtractor = (FluidExtractorTileEntity) tileEntity;
                CompoundNBT tileEntityTag = new CompoundNBT();
                tileEntity.write(tileEntityTag);
                tileEntityTag.remove("x");
                tileEntityTag.remove("y");
                tileEntityTag.remove("z");
                tileEntityTag.remove("id");
                tileEntityTag.remove("RemainingFuel");
                tileEntityTag.remove("FuelMaxProgress");
                tileEntityTag.remove("ExtractionProgress");

                CompoundNBT compound = new CompoundNBT();
                compound.put("BlockEntityTag", tileEntityTag);

                ItemStack drop = new ItemStack(Item.getItemFromBlock(this));
                drop.setTag(compound);
                if(fluidExtractor.hasCustomName())
                {
                    drop.setDisplayName(fluidExtractor.getDisplayName());
                }
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public boolean hasTileEntity(BlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world)
    {
        return new FluidExtractorTileEntity();
    }
}
