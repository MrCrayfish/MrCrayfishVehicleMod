package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.entity.trailer.EntityChestTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityTrailer;
import com.mrcrayfish.vehicle.entity.trailer.EntityVehicleTrailer;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class VehicleRecipes
{
    public static final ImmutableMap<Class<? extends Entity>, VehicleRecipe> RECIPES;

    static
    {
        ImmutableMap.Builder<Class<? extends Entity>, VehicleRecipe> mapBuilder = ImmutableMap.builder();

        Builder builder;

        /* Aluminum Boat */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(EntityAluminumBoat.class, builder.build());

        /* ATV */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 64));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 4, 15));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 6));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(EntityATV.class, builder.build());

        /* Bumper Car */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 8));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(EntityBumperCar.class, builder.build());

        /* Dune Buggy */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Blocks.CONCRETE, 16, EnumDyeColor.YELLOW.getMetadata()));
        builder.addMaterial(new ItemStack(Blocks.CONCRETE, 8, EnumDyeColor.BLUE.getMetadata()));
        builder.addMaterial(new ItemStack(Blocks.CONCRETE, 4, EnumDyeColor.RED.getMetadata()));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 3));
        mapBuilder.put(EntityDuneBuggy.class, builder.build());

        /* Go Kart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 4));
        mapBuilder.put(EntityGoKart.class, builder.build());

        /* Golf Cart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 128));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 8, 0));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 12));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        mapBuilder.put(EntityGolfCart.class, builder.build());

        /* Jet Ski */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(EntityJetSki.class, builder.build());

        /* Lawn Mower */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 64));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 4, 15));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(EntityLawnMower.class, builder.build());

        /* Mini Bike */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 24));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 2, 15));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 2));
        mapBuilder.put(EntityMiniBike.class, builder.build());

        /* Moped */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 2));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 4, 15));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 2));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 6));
        mapBuilder.put(EntityMoped.class, builder.build());

        /* Off-Roader */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 150));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 8, 15));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 6));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 12));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 24));
        mapBuilder.put(EntityOffRoader.class, builder.build());

        /* Shopping Cart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 8));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(Items.DYE, 2, EnumDyeColor.RED.getDyeDamage()));
        mapBuilder.put(EntityShoppingCart.class, builder.build());

        /* Smart Car */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 8, 15));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 6));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 8));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        mapBuilder.put(EntitySmartCar.class, builder.build());

        /* Speed Boat */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(Blocks.WOOL, 8, 15));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(EntitySpeedBoat.class, builder.build());

        /* Sports Plane */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 180));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 16));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 18));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 3));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 32));
        mapBuilder.put(EntitySportsPlane.class, builder.build());

        /* Vehicle Trailer */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 48));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 2));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 2));
        mapBuilder.put(EntityVehicleTrailer.class, builder.build());

        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.WHEEL, 2));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 2));
        builder.addMaterial(new ItemStack(Blocks.CHEST));
        mapBuilder.put(EntityChestTrailer.class, builder.build());

        if(Loader.isModLoaded("cfm"))
        {
            /* Bath */
            builder = new Builder();
            builder.addMaterial(new ItemStack(Item.getByNameOrId("cfm:bath_bottom"), 1));
            builder.addMaterial(new ItemStack(Items.NETHER_STAR, 1));
            mapBuilder.put(EntityBath.class, builder.build());

            /* Couch */
            builder = new Builder();
            builder.addMaterial(new ItemStack(Item.getByNameOrId("cfm:couch_jeb"), 1));
            builder.addMaterial(new ItemStack(Items.IRON_INGOT, 8));
            builder.addMaterial(new ItemStack(ModItems.WHEEL, 4));
            mapBuilder.put(EntityCouch.class, builder.build());

            /* Sofacopter */
            builder = new Builder();
            builder.addMaterial(new ItemStack(Item.getByNameOrId("cfm:couch"), 1, 14));
            builder.addMaterial(new ItemStack(Item.getByNameOrId("cfm:ceiling_fan")));
            builder.addMaterial(new ItemStack(Items.IRON_INGOT, 16));
            mapBuilder.put(EntitySofacopter.class, builder.build());
        }

        RECIPES = mapBuilder.build();
    }

    @Nullable
    public static VehicleRecipe getRecipe(Class<? extends Entity> clazz)
    {
        return RECIPES.get(clazz);
    }

    public static class VehicleRecipe
    {
        private final ImmutableList<ItemStack> materials;

        public VehicleRecipe(Set<ItemStack> materialSet)
        {
            materials = ImmutableList.copyOf(materialSet);
        }

        public ImmutableList<ItemStack> getMaterials()
        {
            return materials;
        }
    }

    public static class Builder
    {
        private Set<ItemStack> materials = new LinkedHashSet<>();

        public void addMaterial(ItemStack stack)
        {
            materials.add(stack);
        }

        public VehicleRecipe build()
        {
            return new VehicleRecipe(materials);
        }
    }
}
