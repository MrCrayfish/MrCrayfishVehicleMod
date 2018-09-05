package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPipe extends BlockObject
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool[] CONNECTED_PIPES;
    public static AxisAlignedBB[] BOXES = new AxisAlignedBB[] {new AxisAlignedBB(0.34375, 0, 0.34375, 0.65625, 0.3125, 0.65625),
            new AxisAlignedBB(0.34375, 0.6875, 0.34375, 0.65625, 1, 0.65625), new AxisAlignedBB(0.34375, 0.34375, 0, 0.65625, 0.65625, 0.3125),
            new AxisAlignedBB(0.34375, 0.34375, 0.6875, 0.65625, 0.65625, 1), new AxisAlignedBB(0, 0.34375, 0.34375, 0.3125, 0.65625, 0.65625),
            new AxisAlignedBB(0.6875, 0.34375, 0.34375, 1, 0.65625, 0.65625), new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875)};

    static
    {
        CONNECTED_PIPES = new PropertyBool[EnumFacing.values().length];
        for(EnumFacing facing : EnumFacing.values())
        {
            CONNECTED_PIPES[facing.getIndex()] = PropertyBool.create("pipe_" + facing.getName());
        }
    }

    public BlockFluidPipe()
    {
        super(Material.IRON, "fluid_pipe");
        IBlockState defaultState = this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH);
        for(EnumFacing facing : EnumFacing.values())
        {
            defaultState = defaultState.withProperty(CONNECTED_PIPES[facing.getIndex()], false);
        }
        this.setDefaultState(defaultState);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format("vehicle.tile.fluid_pipe.info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        if (!isActualState)
        {
            state = state.getActualState(world, pos);
        }
        for (int i = 0; i < EnumFacing.values().length; i++)
        {
            if (state.getValue(CONNECTED_PIPES[i]))
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[i]);
            }
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[state.getValue(FACING).getIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BOXES[6]);
    }

    @Override
    @Nullable
    protected RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox)
    {
        double distanceSq;
        double distanceSqShortest = Double.POSITIVE_INFINITY;
        RayTraceResult resultClosest = null;
        RayTraceResult result;
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        Vec3d eyes = player.getPositionEyes(1);
        Vec3d look = eyes.add(player.getLookVec().scale(Minecraft.getMinecraft().playerController.getBlockReachDistance()));
        World world = Minecraft.getMinecraft().world;
        IBlockState state = world.getBlockState(pos);
        state = state.getActualState(world, pos);
        if (!(state.getBlock() instanceof BlockFluidPipe))
        {
            return null;
        }
        for (int i = 0; i < BOXES.length + 1; i++)
        {
            if (i < EnumFacing.values().length && !state.getValue(CONNECTED_PIPES[i]))
            {
                continue;
            }
            result = BOXES[i < BOXES.length ? i : state.getValue(FACING).getIndex()].offset(pos).calculateIntercept(eyes, look);
            if (result != null)
            {
                distanceSq = result.hitVec.squareDistanceTo(eyes);
                if (distanceSq < distanceSqShortest)
                {
                    distanceSqShortest = distanceSq;
                    resultClosest = result;
                }
            }
        }
        return resultClosest == null ? resultClosest : new RayTraceResult(Type.BLOCK,
                resultClosest.hitVec.addVector(pos.getX(), pos.getY(), pos.getZ()), resultClosest.sideHit, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        double minX = 5 * 0.0625;
        double minY = 5 * 0.0625;
        double minZ = 5 * 0.0625;
        double maxX = 11 * 0.0625;
        double maxY = 11 * 0.0625;
        double maxZ = 11 * 0.0625;

        state = this.getActualState(state, source, pos);
        EnumFacing originalFacing = state.getValue(FACING);
        switch(originalFacing)
        {
            case DOWN:
                minY = 0.0F;
                break;
            case UP:
                maxY = 1.0F;
                break;
            case NORTH:
                minZ = 0.0F;
                break;
            case SOUTH:
                maxZ = 1.0F;
                break;
            case WEST:
                minX = 0.0F;
                break;
            case EAST:
                maxX = 1.0F;
                break;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.NORTH.getIndex()]))
        {
            minZ = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.EAST.getIndex()]))
        {
            maxX = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.SOUTH.getIndex()]))
        {
            maxZ = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.WEST.getIndex()]))
        {
            minX = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.DOWN.getIndex()]))
        {
            minY = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.UP.getIndex()]))
        {
            maxY = 1.0F;
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing originalFacing = state.getValue(FACING);
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing == originalFacing)
                continue;

            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = worldIn.getBlockState(adjacentPos);
            if(adjacentState.getBlock() == this)
            {
                EnumFacing adjacentFacing = adjacentState.getValue(FACING);
                if(adjacentPos.offset(adjacentFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
                }
            }
            else if(adjacentState.getBlock() == ModBlocks.FLUID_PUMP)
            {
                state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], true);
            }
        }
        return state;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer)
    {
        IBlockState state = super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer);
        return state.withProperty(FACING, facing.getOpposite());
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getIndex();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getFront(meta));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        BlockStateContainer.Builder builder = new BlockStateContainer.Builder(this);
        builder.add(FACING);
        builder.add(CONNECTED_PIPES);
        return builder.build();
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFluidPipe();
    }
}
