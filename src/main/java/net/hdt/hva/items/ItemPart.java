package net.hdt.hva.items;

import net.hdt.hva.HuskysVehicleAddon;
import net.minecraft.item.Item;

import static net.hdt.hva.Reference.MOD_ID;

public class ItemPart extends Item
{
    public ItemPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(MOD_ID, id);
        this.setCreativeTab(HuskysVehicleAddon.CREATIVE_TAB);
    }
}
