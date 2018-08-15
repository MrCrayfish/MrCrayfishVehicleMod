package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.util.Bounds;
import com.mrcrayfish.vehicle.util.StateHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockBoostRamp extends BlockRotatedObject
{
    public static final PropertyBool STACKED = PropertyBool.create("stacked");
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");

    private static final AxisAlignedBB COLLISION_BASE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    private static final AxisAlignedBB COLLISION_STACKED_BASE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.5625, 1.0);

    private static final AxisAlignedBB[] COLLISION_ONE = new Bounds(2, 1, 0, 16, 2, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_TWO = new Bounds(4, 2, 0, 16, 3, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_THREE = new Bounds(6, 3, 0, 16, 4, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FOUR = new Bounds(8, 4, 0, 16, 5, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FIVE = new Bounds(10, 5, 0, 16, 6, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_SIX = new Bounds(12, 6, 0, 16, 7, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_SEVEN = new Bounds(14, 7, 0, 16, 8, 16).getRotatedBounds();

    private static final AxisAlignedBB[] COLLISION_STACKED_ONE = new Bounds(2, 9, 0, 16, 10, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_TWO = new Bounds(4, 10, 0, 16, 11, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_THREE = new Bounds(6, 11, 0, 16, 12, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_FOUR = new Bounds(8, 12, 0, 16, 13, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_FIVE = new Bounds(10, 13, 0, 16, 14, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_SIX = new Bounds(12, 14, 0, 16, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_STACKED_SEVEN = new Bounds(14, 15, 0, 16, 16, 16).getRotatedBounds();

    public BlockBoostRamp()
    {
        super(Material.ROCK, "boost_ramp");
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if(entityIn instanceof EntityPoweredVehicle && entityIn.getControllingPassenger() != null)
        {
            EnumFacing facing = state.getValue(FACING);
            if(facing == entityIn.getHorizontalFacing())
            {
                EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) entityIn;
                if(!poweredVehicle.isBoosting())
                {
                    worldIn.playSound(null, pos, ModSounds.BOOST_PAD, SoundCategory.BLOCKS, 2.0F, 0.5F);
                }
                poweredVehicle.setBoosting(true);
                poweredVehicle.setLaunching(true);
                poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
                poweredVehicle.speedMultiplier = 1.0F;
                poweredVehicle.motionY = (poweredVehicle.currentSpeed * 0.5) / 20F + 0.1;
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        EnumFacing facing = state.getValue(FACING);
        if(state.getValue(STACKED))
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_BASE);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_ONE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_TWO[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_THREE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_FOUR[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_FIVE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_SIX[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_STACKED_SEVEN[facing.getHorizontalIndex()]);
        }
        else
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BASE);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_ONE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_TWO[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_THREE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FOUR[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FIVE[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_SIX[facing.getHorizontalIndex()]);
            addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_SEVEN[facing.getHorizontalIndex()]);
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing facing = state.getValue(FACING);
        state = state.withProperty(LEFT, false);
        state = state.withProperty(RIGHT, false);
        if(StateHelper.getBlock(worldIn, pos, facing, StateHelper.Direction.LEFT) == this)
        {
            if(StateHelper.getRotation(worldIn, pos, facing, StateHelper.Direction.LEFT) == StateHelper.Direction.DOWN)
            {
                state = state.withProperty(RIGHT, true);
            }
        }
        if(StateHelper.getBlock(worldIn, pos, facing, StateHelper.Direction.RIGHT) == this)
        {
            if(StateHelper.getRotation(worldIn, pos, facing, StateHelper.Direction.RIGHT) == StateHelper.Direction.DOWN)
            {
                state = state.withProperty(LEFT, true);
            }
        }
        return state;
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(STACKED) ? 4 : 0);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta % 4)).withProperty(STACKED, meta / 4 == 1);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, STACKED, LEFT, RIGHT);
    }
}
