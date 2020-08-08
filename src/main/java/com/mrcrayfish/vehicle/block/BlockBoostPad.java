package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.BoostTileEntity;
import com.mrcrayfish.vehicle.util.StateHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class BlockBoostPad extends BlockRotatedObject
{
    public static final BooleanProperty LEFT = BooleanProperty.create("left");
    public static final BooleanProperty RIGHT = BooleanProperty.create("right");

    protected static final VoxelShape SHAPE = Block.makeCuboidShape(0, 0, 0, 16, 1, 16);

    public BlockBoostPad()
    {
        super(Block.Properties.create(Material.ROCK).hardnessAndResistance(0.6F));
        this.setDefaultState(this.getStateContainer().getBaseState().with(DIRECTION, Direction.NORTH).with(LEFT, false).with(RIGHT, false));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(Minecraft.getInstance().fontRenderer.func_238425_b_(new TranslationTextComponent(this.getTranslationKey() + ".info"), 150).stream().map(text -> new StringTextComponent(text.getString())).collect(Collectors.toList()));
        }
        else
        {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return VoxelShapes.empty();
    }

    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn)
    {
        if(entityIn instanceof PoweredVehicleEntity && entityIn.getControllingPassenger() != null)
        {
            Direction facing = state.get(DIRECTION);
            if(facing == entityIn.getHorizontalFacing())
            {
                float speedMultiplier = 0.0F;
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity instanceof BoostTileEntity)
                {
                    speedMultiplier = ((BoostTileEntity) tileEntity).getSpeedMultiplier();
                }

                PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entityIn;
                if(!poweredVehicle.isBoosting())
                {
                    worldIn.playSound(null, pos, ModSounds.BOOST_PAD.get(), SoundCategory.BLOCKS, 1.0F, 0.5F);
                }
                poweredVehicle.setBoosting(true);
                poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
                poweredVehicle.speedMultiplier = speedMultiplier;
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState state, Direction facing, BlockState facingState, IWorld worldIn, BlockPos pos, BlockPos facingPos)
    {
        return this.getBoostPadState(state, state.get(DIRECTION), worldIn, pos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        return this.getBoostPadState(super.getStateForPlacement(context), context.getPlacementHorizontalFacing(), context.getWorld(), context.getPos());
    }

    private BlockState getBoostPadState(BlockState state, Direction direction, IWorld world, BlockPos pos)
    {
        if(StateHelper.getBlock(world, pos, direction, StateHelper.RelativeDirection.LEFT) == this)
        {
            if(StateHelper.getRotation(world, pos, direction, StateHelper.RelativeDirection.LEFT) == StateHelper.RelativeDirection.DOWN)
            {
                state = state.with(RIGHT, true);
            }
        }
        if(StateHelper.getBlock(world, pos, direction, StateHelper.RelativeDirection.RIGHT) == this)
        {
            if(StateHelper.getRotation(world, pos, direction, StateHelper.RelativeDirection.RIGHT) == StateHelper.RelativeDirection.DOWN)
            {
                state = state.with(LEFT, true);
            }
        }
        return state;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(LEFT);
        builder.add(RIGHT);
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
        return new BoostTileEntity(0.5F);
    }
}
