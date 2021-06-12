package com.mrcrayfish.vehicle.util;

import com.mrcrayfish.vehicle.block.RotatedObjectBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class StateHelper
{
    public static Block getBlock(IWorldReader world, BlockPos pos, Direction facing, RelativeDirection dir)
    {
        BlockPos target = getBlockPosRelativeTo(world, pos, facing, dir);
        return world.getBlockState(target).getBlock();
    }

    public static RelativeDirection getRotation(IWorldReader world, BlockPos pos, Direction facing, RelativeDirection dir)
    {
        BlockPos target = getBlockPosRelativeTo(world, pos, facing, dir);
        Direction other = world.getBlockState(target).getValue(RotatedObjectBlock.DIRECTION);
        return getDirectionRelativeTo(facing, other);
    }

    public static boolean isAirBlock(IWorldReader world, BlockPos pos, Direction facing, RelativeDirection dir)
    {
        BlockPos target = getBlockPosRelativeTo(world, pos, facing, dir);
        return world.getBlockState(target).isAir();
    }

    private static BlockPos getBlockPosRelativeTo(IWorldReader world, BlockPos pos, Direction facing, RelativeDirection dir)
    {
        switch(dir)
        {
            case LEFT:
                return pos.relative(facing.getClockWise());
            case RIGHT:
                return pos.relative(facing.getCounterClockWise());
            case UP:
                return pos.relative(facing);
            case DOWN:
                return pos.relative(facing.getOpposite());
            default:
                return pos;
        }
    }

    private static RelativeDirection getDirectionRelativeTo(Direction thisBlock, Direction otherBlock)
    {
        int num = thisBlock.get2DDataValue() - otherBlock.get2DDataValue();
        switch(num)
        {
            case -3:
                return RelativeDirection.LEFT;
            case -2:
                return RelativeDirection.UP;
            case -1:
                return RelativeDirection.RIGHT;
            case 0:
                return RelativeDirection.DOWN;
            case 1:
                return RelativeDirection.LEFT;
            case 2:
                return RelativeDirection.UP;
            case 3:
                return RelativeDirection.RIGHT;
        }
        return RelativeDirection.NONE;
    }

    public enum RelativeDirection
    {
        UP,
        DOWN,
        LEFT,
        RIGHT,
        NONE
    }
}
