package net.hdt.hva.items;

import net.hdt.hva.HuskysVehicleAddon;
import net.minecraft.item.Item;

import static net.hdt.hva.Reference.MOD_ID;

public class ItemColoredPart extends Item
{
    public ItemColoredPart(String id)
    {
        this.setUnlocalizedName(id);
        this.setRegistryName(MOD_ID, id);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
        this.setCreativeTab(HuskysVehicleAddon.CREATIVE_TAB);
    }

}
