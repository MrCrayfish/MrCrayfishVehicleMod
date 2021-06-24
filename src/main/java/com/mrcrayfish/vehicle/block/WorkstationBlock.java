package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.tileentity.WorkstationTileEntity;
import com.mrcrayfish.vehicle.util.VoxelShapeHelper;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class WorkstationBlock extends RotatedObjectBlock
{
    private static final VoxelShape SHAPE = Util.make(() -> {
        List<VoxelShape> shapes = new ArrayList<>();
        shapes.add(Block.box(0, 1, 0, 16, 16, 16));
        shapes.add(Block.box(1, 0, 1, 3, 1, 3));
        shapes.add(Block.box(1, 0, 13, 3, 1, 15));
        shapes.add(Block.box(13, 0, 1, 15, 1, 3));
        shapes.add(Block.box(13, 0, 13, 15, 1, 15));
        return VoxelShapeHelper.combineAll(shapes);
    });

    public WorkstationBlock()
    {
        super(AbstractBlock.Properties.of(Material.METAL).strength(1.0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(!world.isClientSide)
        {
            TileEntity tileEntity = world.getBlockEntity(pos);
            if(tileEntity instanceof INamedContainerProvider)
            {
                NetworkHooks.openGui((ServerPlayerEntity) playerEntity, (INamedContainerProvider) tileEntity, pos);
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.SUCCESS;
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
        return new WorkstationTileEntity();
    }
}
