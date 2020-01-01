package com.mrcrayfish.vehicle.item;

import com.mrcrayfish.vehicle.VehicleMod;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class ItemTrafficCone extends BlockItem
{
    public ItemTrafficCone(Block block)
    {
        super(block, new Item.Properties().group(VehicleMod.CREATIVE_TAB));
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack)
    {
        return EquipmentSlotType.HEAD;
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn)
    {
        if(Screen.hasShiftDown())
        {
            String info = I18n.format("tile.vehicle.traffic_cone.info");
            tooltip.addAll(Minecraft.getInstance().fontRenderer.listFormattedStringToWidth(info, 150).stream().map((Function<String, ITextComponent>) StringTextComponent::new).collect(Collectors.toList()));
        }
        else
        {
            tooltip.add(new StringTextComponent(TextFormatting.YELLOW + I18n.format("vehicle.info_help")));
        }
    }
}
