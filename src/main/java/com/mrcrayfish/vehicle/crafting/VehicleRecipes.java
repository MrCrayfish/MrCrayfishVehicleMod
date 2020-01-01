package com.mrcrayfish.vehicle.crafting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Author: MrCrayfish
 */
public class VehicleRecipes
{
    public static ImmutableMap<EntityType<?>, VehicleRecipe> recipes;

    public static void register()
    {
        ImmutableMap.Builder<EntityType<?>, VehicleRecipe> mapBuilder = ImmutableMap.builder();

        Builder builder;

        /* Aluminum Boat */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(ModEntities.ALUMINUM_BOAT, builder.build());

        /* ATV */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 64));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 4));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 6));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(ModEntities.ATV, builder.build());

        /* Bumper Car */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 8));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(ModEntities.BUMPER_CAR, builder.build());

        /* Dune Buggy */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Blocks.YELLOW_CONCRETE, 16));
        builder.addMaterial(new ItemStack(Blocks.BLUE_CONCRETE, 8));
        builder.addMaterial(new ItemStack(Blocks.RED_CONCRETE, 4));
        mapBuilder.put(ModEntities.DUNE_BUGGY, builder.build());

        /* Go Kart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 4));
        mapBuilder.put(ModEntities.GO_KART, builder.build());

        /* Golf Cart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 128));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(Blocks.WHITE_WOOL, 8));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 12));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        mapBuilder.put(ModEntities.GOLF_CART, builder.build());

        /* Jet Ski */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(ModEntities.JET_SKI, builder.build());

        /* Lawn Mower */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 64));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(ModEntities.LAWN_MOWER, builder.build());

        /* Mini Bike */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 24));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 2));
        mapBuilder.put(ModEntities.MINI_BIKE, builder.build());

        /* Moped */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 2));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 6));
        mapBuilder.put(ModEntities.MOPED, builder.build());

        /* Off-Roader */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 150));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 8));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 6));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 12));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 24));
        mapBuilder.put(ModEntities.OFF_ROADER, builder.build());

        /* Shopping Cart */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 8));
        builder.addMaterial(new ItemStack(Blocks.IRON_BARS, 4));
        builder.addMaterial(new ItemStack(Items.RED_DYE, 2));
        mapBuilder.put(ModEntities.SHOPPING_CART, builder.build());

        /* Smart Car */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 8));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 6));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 8));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        mapBuilder.put(ModEntities.SMART_CAR, builder.build());

        /* Speed Boat */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 80));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 8));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 4));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 10));
        mapBuilder.put(ModEntities.SPEED_BOAT, builder.build());

        /* Sports Plane */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 180));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 16));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 18));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 32));
        mapBuilder.put(ModEntities.SPORTS_PLANE, builder.build());

        /* Tractor */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 128));
        builder.addMaterial(new ItemStack(Blocks.BLACK_WOOL, 4));
        builder.addMaterial(new ItemStack(Items.REDSTONE, 8));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        mapBuilder.put(ModEntities.TRACTOR, builder.build());

        /* Vehicle Trailer */
        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 48));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 2));
        mapBuilder.put(ModEntities.VEHICLE_TRAILER, builder.build());

        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 2));
        builder.addMaterial(new ItemStack(Blocks.CHEST));
        mapBuilder.put(ModEntities.STORAGE_TRAILER, builder.build());

        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 48));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 16));
        builder.addMaterial(new ItemStack(Blocks.GLASS_PANE, 2));
        mapBuilder.put(ModEntities.FLUID_TRAILER, builder.build());

        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 42));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(ModEntities.SEEDER, builder.build());

        builder = new Builder();
        builder.addMaterial(new ItemStack(Items.IRON_INGOT, 36));
        builder.addMaterial(new ItemStack(ModItems.PANEL, 8));
        mapBuilder.put(ModEntities.FERTILIZER, builder.build());

        if(ModList.get().isLoaded("cfm") && false) //TODO fix these recipes
        {
            /* Bath */
            builder = new Builder();
            builder.addMaterial(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:bath")), 1));
            builder.addMaterial(new ItemStack(Items.NETHER_STAR, 1));
            mapBuilder.put(ModEntities.BATH, builder.build());

            /* Couch */
            builder = new Builder();
            builder.addMaterial(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:rainbow_sofa")), 1));
            builder.addMaterial(new ItemStack(Items.IRON_INGOT, 8));
            mapBuilder.put(ModEntities.SOFA, builder.build());

            /* Sofacopter */
            builder = new Builder();
            builder.addMaterial(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:red_sofa")), 1));
            builder.addMaterial(new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:ceiling_fan"))));
            builder.addMaterial(new ItemStack(Items.IRON_INGOT, 16));
            mapBuilder.put(ModEntities.SOFACOPTER, builder.build());
        }

        recipes = mapBuilder.build();
    }

    @Nullable
    public static VehicleRecipe getRecipe(EntityType<?> entityType)
    {
        return recipes.get(entityType);
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
