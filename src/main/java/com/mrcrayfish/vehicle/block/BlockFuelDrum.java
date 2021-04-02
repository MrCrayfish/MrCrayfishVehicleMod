package com.mrcrayfish.vehicle.block;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.tileentity.FuelDrumTileEntity;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
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
    private static final VoxelShape SHAPE = Block.makeCuboidShape(1, 0, 1, 15, 16, 15);

    public BlockFuelDrum()
    {
        super(Block.Properties.create(Material.IRON).hardnessAndResistance(1.0F));
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader reader, List<ITextComponent> list, ITooltipFlag advanced)
    {
        if(Screen.hasShiftDown())
        {
            list.addAll(RenderUtil.lines(new TranslationTextComponent(ModBlocks.FUEL_DRUM.get().getTranslationKey() + ".info"), 150));
        }
        else
        {
            list.add(new TranslationTextComponent("vehicle.info_help").mergeStyle(TextFormatting.YELLOW));
        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity playerEntity, Hand hand, BlockRayTraceResult result)
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
                    return ActionResultType.SUCCESS;
                }

                TileEntity tileEntity = world.getTileEntity(pos);
                IFluidHandler handler = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).orElse(null);
                if(handler instanceof FluidTank)
                {
                    FluidTank tank = (FluidTank) handler;
                    if(tank.getFluid().getFluid() != ModFluids.FUELIUM.get())
                    {
                        return ActionResultType.SUCCESS;
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
        return new FuelDrumTileEntity();
    }
}
