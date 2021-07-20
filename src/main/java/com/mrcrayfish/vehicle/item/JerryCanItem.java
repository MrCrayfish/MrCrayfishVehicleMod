package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.util.FluidUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;

import javax.annotation.Nullable;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class JerryCanItem extends Item
{
    private final DecimalFormat FUEL_FORMAT = new DecimalFormat("0.#%");

    private final Supplier<Integer> capacitySupplier;

    public JerryCanItem(Supplier<Integer> capacity, Item.Properties properties)
    {
        super(properties);
        this.capacitySupplier = capacity;
    }

    @Override
    public void fillItemCategory(ItemGroup group, NonNullList<ItemStack> items)
    {
        if(this.allowdedIn(group))
        {
            ItemStack stack = new ItemStack(this);
            items.add(stack);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            tooltip.addAll(RenderUtil.lines(new TranslationTextComponent(this.getDescriptionId() + ".info"), 150));
        }
        else if(worldIn != null)
        {
            stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(handler ->
            {
                FluidStack fluidStack = handler.getFluidInTank(0);
                if(!fluidStack.isEmpty())
                {
                    tooltip.add(new TranslationTextComponent(fluidStack.getTranslationKey()).withStyle(TextFormatting.BLUE));
                    tooltip.add(new StringTextComponent(this.getCurrentFuel(stack) + " / " + this.capacitySupplier.get() + "mb").withStyle(TextFormatting.GRAY));
                }
                else
                {
                    tooltip.add(new TranslationTextComponent("item.vehicle.jerry_can.empty").withStyle(TextFormatting.RED));
                }
            });
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.get("vehicle.info_help")));
        }
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context)
    {
        // This is such ugly code
        TileEntity tileEntity = context.getLevel().getBlockEntity(context.getClickedPos());
        if(tileEntity != null && context.getPlayer() != null)
        {
            LazyOptional<IFluidHandler> lazyOptional = tileEntity.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, context.getClickedFace());
            if(lazyOptional.isPresent())
            {
                Optional<IFluidHandler> optional = lazyOptional.resolve();
                if(optional.isPresent())
                {
                    IFluidHandler source = optional.get();
                    Optional<IFluidHandlerItem> itemOptional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
                    if(itemOptional.isPresent())
                    {
                        if(context.getPlayer().isCrouching())
                        {
                            FluidUtils.transferFluid(source, itemOptional.get(), this.getFillRate());
                        }
                        else
                        {
                            FluidUtils.transferFluid(itemOptional.get(), source, this.getFillRate());
                        }
                        return ActionResultType.SUCCESS;
                    }
                }
            }
        }
        return super.onItemUseFirst(stack, context);
    }

    public int getCurrentFuel(ItemStack stack)
    {
        Optional<IFluidHandlerItem> optional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        return optional.map(handler -> handler.getFluidInTank(0).getAmount()).orElse(0);
    }

    public int getCapacity()
    {
        return this.capacitySupplier.get();
    }

    public int getFillRate()
    {
        return Config.SERVER.jerryCanFillRate.get();
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack)
    {
        return this.getCurrentFuel(stack) > 0;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack)
    {
        return 1.0 - (this.getCurrentFuel(stack) / (double) this.capacitySupplier.get());
    }

    @Override
    public int getRGBDurabilityForDisplay(ItemStack stack)
    {
        Optional<IFluidHandlerItem> optional = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).resolve();
        return optional.map(handler -> {
            int color = handler.getFluidInTank(0).getFluid().getAttributes().getColor();
            if(color == 0xFFFFFFFF) color = FluidUtils.getAverageFluidColor(handler.getFluidInTank(0).getFluid());
            return color;
        }).orElse(0);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
    {
        return slotChanged;
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt)
    {
        return new FluidHandlerItemStack(stack, this.capacitySupplier.get());
    }
}
