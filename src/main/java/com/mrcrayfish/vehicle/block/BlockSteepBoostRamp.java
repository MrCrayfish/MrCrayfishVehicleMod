package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.TileEntityBoost;
import com.mrcrayfish.vehicle.util.BlockNames;
import com.mrcrayfish.vehicle.util.Bounds;
import com.mrcrayfish.vehicle.util.StateHelper;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class BlockSteepBoostRamp extends BlockRotatedObject
{
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");

    private static final AxisAlignedBB COLLISION_BASE = new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.0625, 1.0);
    private static final AxisAlignedBB[] COLLISION_ONE = new Bounds(1, 1, 0, 16, 2, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_TWO = new Bounds(2, 2, 0, 16, 3, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_THREE = new Bounds(3, 3, 0, 16, 4, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FOUR = new Bounds(4, 4, 0, 16, 5, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FIVE = new Bounds(5, 5, 0, 16, 6, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_SIX = new Bounds(6, 6, 0, 16, 7, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_SEVEN = new Bounds(7, 7, 0, 16, 8, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_EIGHT = new Bounds(8, 9, 0, 16, 9, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_NINE = new Bounds(9, 10, 0, 16, 10, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_TEN = new Bounds(10, 11, 0, 16, 11, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_ELEVEN = new Bounds(11, 12, 0, 16, 12, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_TWELVE = new Bounds(12, 13, 0, 16, 13, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_THIRTEEN = new Bounds(13, 14, 0, 16, 14, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FOURTEEN = new Bounds(14, 15, 0, 16, 15, 16).getRotatedBounds();
    private static final AxisAlignedBB[] COLLISION_FIFTEEN = new Bounds(15, 15, 0, 16, 16, 16).getRotatedBounds();

    public BlockSteepBoostRamp()
    {
        super(Material.ROCK, BlockNames.STEEP_BOOST_RAMP);
        this.setHardness(1.0F);
    }

    @Override
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

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if(entityIn instanceof EntityPoweredVehicle && entityIn.getControllingPassenger() != null)
        {
            EnumFacing facing = state.getValue(FACING);
            if(facing == entityIn.getHorizontalFacing())
            {
                float speedMultiplier = 0.0F;
                TileEntity tileEntity = worldIn.getTileEntity(pos);
                if(tileEntity instanceof TileEntityBoost)
                {
                    speedMultiplier = ((TileEntityBoost) tileEntity).getSpeedMultiplier();
                }

                EntityPoweredVehicle poweredVehicle = (EntityPoweredVehicle) entityIn;
                if(!poweredVehicle.isBoosting())
                {
                    worldIn.playSound(null, pos, ModSounds.BOOST_PAD, SoundCategory.BLOCKS, 2.0F, 0.5F);
                }
                poweredVehicle.setBoosting(true);
                poweredVehicle.setLaunching(3);
                poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
                poweredVehicle.speedMultiplier = speedMultiplier;
                poweredVehicle.motionY = poweredVehicle.currentSpeed / 20F + 0.1;
            }
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        EnumFacing facing = state.getValue(FACING);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_BASE);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_ONE[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_TWO[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_THREE[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FOUR[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FIVE[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_SIX[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_SEVEN[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_EIGHT[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_NINE[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_TEN[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_ELEVEN[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_TWELVE[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_THIRTEEN[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FOURTEEN[facing.getHorizontalIndex()]);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, COLLISION_FIFTEEN[facing.getHorizontalIndex()]);
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
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, LEFT, RIGHT);
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
        return new TileEntityBoost(1.0F);
    }
}
