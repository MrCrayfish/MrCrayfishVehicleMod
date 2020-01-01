package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.util.Bounds;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class BlockFuelDrum extends BlockRotatedObject
{
    private static final AxisAlignedBB BOUNDING_BOX = new Bounds(1, 0, 1, 15, 16, 15).toAABB();

    private int capacity;

    public BlockFuelDrum(String id, int capacity)
    {
        super(id, Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F));
        this.capacity = capacity;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        if(Screen.hasShiftDown())
        {
            String info = I18n.format("tile.vehicle.fuel_drum.info");
            list.addAll(Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(info, 150).stream().map((Function<String, ITextComponent>) StringTextComponent::new).collect(Collectors.toList()));
        }
        else
        {
            list.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }

    @Override
    public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
    {
        if(!world.isRemote)
        {
            ItemStack stack = playerEntity.getHeldItem(hand);
            if(FluidUtil.interactWithFluidHandler(playerEntity, hand, world, pos, result.getFace()))
            {
                return ActionResultType.SUCCESS;
            }

            if(stack.getItem() instanceof JerryCanItem)
            {
                JerryCanItem jerryCan = (JerryCanItem) stack.getItem();
                if(jerryCan.isFull(stack))
                {
                    return ActionResultType.PASS;
                }

                TileEntity tileEntity = world.getTileEntity(pos);
                IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
                if(handler instanceof FluidTank)
                {
                    FluidTank tank = (FluidTank) handler;
                    if(tank.getFluid().getFluid() != ModFluids.FUELIUM)
                    {
                        return ActionResultType.PASS;
                    }
                    FluidStack fluidStack = handler.drain(50, IFluidHandler.FluidAction.EXECUTE);
                    if(!fluidStack.isEmpty())
                    {
                        int remaining = jerryCan.fill(stack, fluidStack.getAmount());
                        if(remaining > 0)
                        {
                            fluidStack.setAmount(remaining);
                            handler.fill(fluidStack, IFluidHandler.FluidAction.EXECUTE);
                        }
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        }
        return ActionResultType.PASS;
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
        return new FuelDrumTileEntity(this.capacity);
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, IFluidState fluid)
    {
        if(!world.isRemote && !player.isCreative())
        {
            TileEntity tileEntity = world.getTileEntity(pos);
            if(tileEntity instanceof FuelDrumTileEntity)
            {
                ItemStack drop = new ItemStack(Item.getItemFromBlock(this));
                if(((FuelDrumTileEntity) tileEntity).getAmount() > 0)
                {
                    CompoundNBT tileEntityTag = new CompoundNBT();
                    tileEntity.write(tileEntityTag);
                    tileEntityTag.remove("x");
                    tileEntityTag.remove("y");
                    tileEntityTag.remove("z");
                    tileEntityTag.remove("id");

                    CompoundNBT compound = new CompoundNBT();
                    compound.put("BlockEntityTag", tileEntityTag);
                    drop.setTag(compound);
                }
                world.addEntity(new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop));
                return world.setBlockState(pos, Blocks.AIR.getDefaultState());
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }
}
