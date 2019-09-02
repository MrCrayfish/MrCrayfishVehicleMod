package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.tileentity.TileEntityBoost;
import com.mrcrayfish.vehicle.util.BlockNames;
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
public class BlockBoostPad extends BlockRotatedObject
{
    public static final PropertyBool LEFT = PropertyBool.create("left");
    public static final PropertyBool RIGHT = PropertyBool.create("right");

    protected static final AxisAlignedBB COLLISION_BOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.0625D, 1.0D);

    public BlockBoostPad()
    {
        super(Material.ROCK, BlockNames.BOOST_PAD);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(LEFT, false).withProperty(RIGHT, false));
        this.setHardness(0.6F);
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
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return COLLISION_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
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
                    worldIn.playSound(null, pos, ModSounds.BOOST_PAD, SoundCategory.BLOCKS, 1.0F, 0.5F);
                }
                poweredVehicle.setBoosting(true);
                poweredVehicle.currentSpeed = poweredVehicle.getActualMaxSpeed();
                poweredVehicle.speedMultiplier = speedMultiplier;
            }
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        EnumFacing facing = state.getValue(FACING);
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
        return new TileEntityBoost(0.5F);
    }
}
