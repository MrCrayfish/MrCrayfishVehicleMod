package net.hdt.hva.items;

import net.hdt.hva.enums.TrainEngineTypes;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEngine extends ItemPart {

    private TrainEngineTypes type;

    public ItemEngine(TrainEngineTypes type) {
        super(type.getRegistryName());
        this.type = type;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        tooltip.add(type.getTierColor() + TextFormatting.BOLD.toString() + type.getTierName() + " Tier");
        if (GuiScreen.isShiftKeyDown()) {
            tooltip.add(TextFormatting.YELLOW + "Fuel Consumption: " + TextFormatting.RESET + type.getFuelConsumption() + "pt");
        } else {
            tooltip.add(TextFormatting.YELLOW + "Hold SHIFT for Stats");
        }
    }

}
