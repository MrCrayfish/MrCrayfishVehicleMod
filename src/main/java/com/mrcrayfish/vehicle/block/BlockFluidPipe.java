package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.tileentity.TileEntityFluidPipe;
import com.mrcrayfish.vehicle.util.BlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
public class BlockFluidPipe extends BlockObject
{
    public static final PropertyDirection FACING = PropertyDirection.create("facing");
    public static final PropertyBool[] CONNECTED_PIPES;
    protected List<AxisAlignedBB> boxes;
    protected AxisAlignedBB boxCenter;
    protected String name;

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
        this(BlockNames.FLUID_PIPE);
        this.setHardness(0.5F);
    }

    public BlockFluidPipe(String name)
    {
        super(Material.IRON, name);
        this.name = name;
        IBlockState defaultState = this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH);
        for(EnumFacing facing : EnumFacing.values())
        {
            defaultState = defaultState.withProperty(CONNECTED_PIPES[facing.getIndex()], false);
        }
        this.setDefaultState(defaultState);
        boxCenter = new AxisAlignedBB(0.3125, 0.3125, 0.3125, 0.6875, 0.6875, 0.6875);
        boxes = Stream.of(new AxisAlignedBB(0.34375, 0, 0.34375, 0.65625, 0.3125, 0.65625), new AxisAlignedBB(0.34375, 0.6875, 0.34375, 0.65625, 1, 0.65625),
                new AxisAlignedBB(0.34375, 0.34375, 0, 0.65625, 0.65625, 0.3125), new AxisAlignedBB(0.34375, 0.34375, 0.6875, 0.65625, 0.65625, 1),
                new AxisAlignedBB(0, 0.34375, 0.34375, 0.3125, 0.65625, 0.65625), new AxisAlignedBB(0.6875, 0.34375, 0.34375, 1, 0.65625, 0.65625),
                boxCenter).collect(Collectors.toList());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World player, List<String> tooltip, ITooltipFlag advanced)
    {
        if(GuiScreen.isShiftKeyDown())
        {
            String info = I18n.format(this.getUnlocalizedName() + ".info");
            tooltip.addAll(Minecraft.getMinecraft().fontRenderer.listFormattedStringToWidth(info, 150));
        }
        else
        {
            tooltip.add(TextFormatting.YELLOW + I18n.format("vehicle.info_help"));
        }
    }

    protected EnumFacing getCollisionFacing(IBlockState state)
    {
        return state.getValue(FACING);
    }

    @Nullable
    public static TileEntityFluidPipe getTileEntity(IBlockAccess world, BlockPos pos)
    {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TileEntityFluidPipe ? (TileEntityFluidPipe) tileEntity : null;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity, boolean isActualState)
    {
        if (!isActualState)
        {
            state = state.getActualState(world, pos);
        }

        boolean[] disabledConnections = TileEntityFluidPipe.getDisabledConnections(getTileEntity(world, pos));
        for (int i = 0; i < EnumFacing.values().length; i++)
        {
            if (state.getValue(CONNECTED_PIPES[i]) && !disabledConnections[i])
            {
                addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(i));
            }
        }
        for (int i = EnumFacing.values().length; i < boxes.size(); i++)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(i));
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, boxes.get(getCollisionFacing(state).getIndex()));
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState blockState, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        double distanceSq;
        double distanceSqShortest = Double.POSITIVE_INFINITY;
        RayTraceResult resultClosest = null;
        RayTraceResult result;
        IBlockState state = world.getBlockState(pos);
        state = state.getActualState(world, pos);
        if (!(state.getBlock() instanceof BlockFluidPipe))
        {
            return null;
        }
        List<AxisAlignedBB> boxes = new ArrayList<>();
        addCollisionBoxToList(state, world, pos, Block.FULL_BLOCK_AABB.offset(pos), boxes, null, true);
        for (AxisAlignedBB box : boxes)
        {
            result = box.calculateIntercept(start, end);
            if (result != null)
            {
                distanceSq = result.hitVec.squareDistanceTo(start);
                if (distanceSq < distanceSqShortest)
                {
                    distanceSqShortest = distanceSq;
                    resultClosest = result;
                }
            }
        }
        return resultClosest == null ? resultClosest : new RayTraceResult(Type.BLOCK, resultClosest.hitVec, resultClosest.sideHit, pos);
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

        boolean[] disabledConnections = TileEntityFluidPipe.getDisabledConnections(getTileEntity(source, pos));

        if(state.getValue(CONNECTED_PIPES[EnumFacing.NORTH.getIndex()]) && !disabledConnections[EnumFacing.NORTH.getIndex()])
        {
            minZ = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.EAST.getIndex()]) && !disabledConnections[EnumFacing.EAST.getIndex()])
        {
            maxX = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.SOUTH.getIndex()]) && !disabledConnections[EnumFacing.SOUTH.getIndex()])
        {
            maxZ = 1.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.WEST.getIndex()]) && !disabledConnections[EnumFacing.WEST.getIndex()])
        {
            minX = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.DOWN.getIndex()]) && !disabledConnections[EnumFacing.DOWN.getIndex()])
        {
            minY = 0.0F;
        }

        if(state.getValue(CONNECTED_PIPES[EnumFacing.UP.getIndex()]) && !disabledConnections[EnumFacing.UP.getIndex()])
        {
            maxY = 1.0F;
        }

        return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TileEntityFluidPipe pipe = getTileEntity(world, pos);
        Pair<AxisAlignedBB, EnumFacing> hit = getWrenchableBox(world, pos, state, player, hand, facing, hitX, hitY, hitZ, pipe);
        if (pipe != null && hit != null)
        {
            facing = hit.getRight();
            pipe.setConnectionDisabled(facing, !pipe.isConnectionDisabled(facing));
            world.markBlockRangeForRenderUpdate(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
            world.scheduleUpdate(pos, state.getBlock(), 0);
            world.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, SoundEvents.ENTITY_IRONGOLEM_STEP, SoundCategory.BLOCKS, 1.0F, 2.0F);
            return true;
        }
        return false;
    }

    @Nullable
    public Pair<AxisAlignedBB, EnumFacing> getWrenchableBox(World world, BlockPos pos, IBlockState state, EntityPlayer player,
            EnumHand hand, EnumFacing facing, double hitX, double hitY, double hitZ, @Nullable TileEntityFluidPipe pipe)
    {
        if (pipe == null || !(player.getHeldItem(hand).getItem() == ModItems.WRENCH))
        {
            return null;
        }
        state = state.getActualState(world, pos);
        Vec3d hit = new Vec3d(hitX, hitY, hitZ);
        for (int i = 0; i < EnumFacing.values().length + 1; i++)
        {
            boolean isCenter = i == EnumFacing.values().length;
            if ((isCenter || state.getValue(CONNECTED_PIPES[i])) && boxes.get(i).grow(0.001).contains(hit))
            {
                if (!isCenter)
                {
                    facing = EnumFacing.getFront(i);
                }
                else if (!state.getValue(CONNECTED_PIPES[facing.getIndex()]))
                {
                    BlockPos adjacentPos = pos.offset(facing);
                    IBlockState adjacentState = world.getBlockState(adjacentPos);
                    TileEntity tileEntity = world.getTileEntity(adjacentPos);
                    Block adjacentBlock = adjacentState.getBlock();
                    if ((this == ModBlocks.FLUID_PUMP && adjacentBlock == ModBlocks.FLUID_PUMP)
                            || (this == ModBlocks.FLUID_PIPE && adjacentBlock == ModBlocks.FLUID_PIPE && getCollisionFacing(adjacentState) != facing.getOpposite())
                            || tileEntity == null || !tileEntity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, facing.getOpposite()))
                    {
                        return null;
                    }
                }
                if (world.getBlockState(pos.offset(facing)).getBlock() != Blocks.LEVER && getCollisionFacing(state) != facing)
                {
                    return new ImmutablePair<>(boxes.get(i).offset(pos), facing);
                }
            }
        }
        return null;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing originalFacing = state.getValue(FACING);
        boolean[] disabledConnections = TileEntityFluidPipe.getDisabledConnections(getTileEntity(world, pos));
        for(EnumFacing facing : EnumFacing.VALUES)
        {
            if(facing == originalFacing)
                continue;

            BlockPos adjacentPos = pos.offset(facing);
            IBlockState adjacentState = world.getBlockState(adjacentPos);
            boolean enabled = !disabledConnections[facing.getIndex()];
            if(adjacentState.getBlock() == ModBlocks.FLUID_PIPE)
            {
                EnumFacing adjacentFacing = adjacentState.getValue(FACING);
                if(adjacentPos.offset(adjacentFacing).equals(pos))
                {
                    state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], enabled);
                }
            }
            else if(adjacentState.getBlock() == ModBlocks.FLUID_PUMP)
            {
                state = state.withProperty(CONNECTED_PIPES[facing.getIndex()], enabled);
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
