package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.crafting.FluidEntry;
import com.mrcrayfish.vehicle.crafting.WorkstationIngredient;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModFluids;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * Author: MrCrayfish
 */
public class RecipeGen extends RecipeProvider
{
    public RecipeGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<IFinishedRecipe> consumer)
    {
        netheriteSmithing(consumer, ModItems.DIAMOND_ELECTRIC_ENGINE.get(), ModItems.NETHERITE_ELECTRIC_ENGINE.get());

        ShapedRecipeBuilder.shaped(ModItems.DIAMOND_ELECTRIC_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.GEMS_DIAMOND)
                .define('E', ModItems.GOLD_ELECTRIC_ENGINE.get())
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_gold_electric_engine", has(ModItems.GOLD_ELECTRIC_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GOLD_ELECTRIC_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.INGOTS_GOLD)
                .define('E', ModItems.IRON_ELECTRIC_ENGINE.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_iron_electric_engine", has(ModItems.IRON_ELECTRIC_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.IRON_ELECTRIC_ENGINE.get())
                .pattern("IRI")
                .pattern("TBT")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REPEATER)
                .define('T', Items.REDSTONE_TORCH)
                .define('P', ModItems.PANEL.get())
                .define('B', Items.REDSTONE_BLOCK)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_repeater", has(Items.REPEATER))
                .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_panel", has(ModItems.PANEL.get()))
                .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                .save(consumer);

        netheriteSmithing(consumer, ModItems.DIAMOND_SMALL_ENGINE.get(), ModItems.NETHERITE_SMALL_ENGINE.get());

        ShapedRecipeBuilder.shaped(ModItems.DIAMOND_SMALL_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.GEMS_DIAMOND)
                .define('E', ModItems.GOLD_SMALL_ENGINE.get())
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_gold_small_engine", has(ModItems.GOLD_SMALL_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GOLD_SMALL_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.INGOTS_GOLD)
                .define('E', ModItems.IRON_SMALL_ENGINE.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_iron_small_engine", has(ModItems.IRON_SMALL_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.IRON_SMALL_ENGINE.get())
                .pattern("IRI")
                .pattern("PFP")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REPEATER)
                .define('P', ModItems.PANEL.get())
                .define('F', Items.FURNACE)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_repeater", has(Items.REPEATER))
                .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_panel", has(ModItems.PANEL.get()))
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .save(consumer);

        netheriteSmithing(consumer, ModItems.DIAMOND_LARGE_ENGINE.get(), ModItems.NETHERITE_LARGE_ENGINE.get());

        ShapedRecipeBuilder.shaped(ModItems.DIAMOND_LARGE_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.GEMS_DIAMOND)
                .define('E', ModItems.GOLD_LARGE_ENGINE.get())
                .unlockedBy("has_diamond", has(Tags.Items.GEMS_DIAMOND))
                .unlockedBy("has_gold_large_engine", has(ModItems.GOLD_LARGE_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.GOLD_LARGE_ENGINE.get())
                .pattern(" U ")
                .pattern("UEU")
                .pattern(" U ")
                .define('U', Tags.Items.INGOTS_GOLD)
                .define('E', ModItems.IRON_LARGE_ENGINE.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_iron_large_engine", has(ModItems.IRON_LARGE_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.IRON_LARGE_ENGINE.get())
                .pattern("BRB")
                .pattern("PFP")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE)
                .define('P', ModItems.PANEL.get())
                .define('F', Items.FURNACE)
                .define('B', Items.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_panel", has(ModItems.PANEL.get()))
                .unlockedBy("has_furnace", has(Items.FURNACE))
                .unlockedBy("has_iron_block", has(Items.IRON_BLOCK))
                .save(consumer);

        //TODO eventually add a battery component item
        ShapedRecipeBuilder.shaped(ModBlocks.FLUID_EXTRACTOR.get())
                .pattern("III")
                .pattern("GPR")
                .pattern("IEI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.GLASS)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE_BLOCK)
                .define('E', ModItems.IRON_ELECTRIC_ENGINE.get()) //TODO convert to tag
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_glass", has(Tags.Items.GLASS))
                .unlockedBy("has_piston", has(Items.PISTON))
                .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                .unlockedBy("has_engine", has(ModItems.IRON_ELECTRIC_ENGINE.get()))
                .save(consumer);

        //TODO eventually add a battery component item
        ShapedRecipeBuilder.shaped(ModBlocks.FLUID_MIXER.get())
                .pattern("III")
                .pattern("HRH")
                .pattern("IEI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE_BLOCK)
                .define('H', Items.HOPPER)
                .define('E', ModItems.IRON_ELECTRIC_ENGINE.get()) //TODO convert to tag
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .unlockedBy("has_engine", has(ModItems.IRON_ELECTRIC_ENGINE.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.FLUID_PIPE.get(), 8)
                .pattern("IRI")
                .pattern("GGG")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE)
                .define('G', Tags.Items.GLASS_PANES)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_glass_pane", has(Tags.Items.GLASS_PANES))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.FLUID_PUMP.get(), 2)
                .pattern("IRI")
                .pattern("GDG")
                .pattern("IHI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE)
                .define('G', Tags.Items.GLASS_PANES)
                .define('D', Items.DISPENSER)
                .define('H', Items.HOPPER)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .unlockedBy("has_glass_pane", has(Tags.Items.GLASS_PANES))
                .unlockedBy("has_dispenser", has(Items.DISPENSER))
                .unlockedBy("has_hopper", has(Items.HOPPER))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.FUEL_DRUM.get())
                .pattern("III")
                .pattern("PBP")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('B', Items.BUCKET)
                .define('P', ModItems.PANEL.get())
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .unlockedBy("has_panel", has(ModItems.PANEL.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.INDUSTRIAL_FUEL_DRUM.get())
                .pattern("III")
                .pattern("IFI")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('F', ModBlocks.FUEL_DRUM.get())
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_fuel_drum", has(ModBlocks.FUEL_DRUM.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.GAS_PUMP.get())
                .pattern("IRI")
                .pattern("GPG")
                .pattern("IFI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REDSTONE_BLOCK)
                .define('P', ModBlocks.FLUID_PUMP.get())
                .define('F', ModBlocks.FUEL_DRUM.get())
                .define('G', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_redstone_block", has(Items.REDSTONE_BLOCK))
                .unlockedBy("has_fluid_pump", has(ModBlocks.FLUID_PUMP.get()))
                .unlockedBy("has_fuel_drum", has(ModBlocks.FUEL_DRUM.get()))
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.HAMMER.get())
                .pattern("III")
                .pattern(" G ")
                .pattern(" W ")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('W', Items.BLACK_WOOL)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_black_wool", has(Items.BLACK_WOOL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.WRENCH.get())
                .pattern("I")
                .pattern("G")
                .pattern("W")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('W', Items.BLACK_WOOL)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_black_wool", has(Items.BLACK_WOOL))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.JERRY_CAN.get())
                .pattern("III")
                .pattern("IDI")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('D', Tags.Items.DYES_PURPLE)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_purple_dye", has(Tags.Items.DYES_PURPLE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.INDUSTRIAL_JERRY_CAN.get())
                .pattern("III")
                .pattern("IJI")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('J', ModItems.JERRY_CAN.get())
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_jerry_can", has(ModItems.JERRY_CAN.get()))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.JACK.get())
                .pattern("IPI")
                .pattern("IRI")
                .define('I', Tags.Items.INGOTS_GOLD)
                .define('P', Items.PISTON)
                .define('R', Items.REDSTONE)
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_piston", has(Items.PISTON))
                .unlockedBy("has_redstone", has(Items.REDSTONE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.KEY.get())
                .pattern("WII")
                .define('W', Items.BLACK_WOOL)
                .define('I', Tags.Items.INGOTS_GOLD)
                .unlockedBy("has_black_wool", has(Items.BLACK_WOOL))
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.PANEL.get(), 2)
                .pattern("III")
                .pattern("III")
                .define('I', Tags.Items.NUGGETS_IRON)
                .unlockedBy("has_iron_nugget", has(Tags.Items.NUGGETS_IRON))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.TRAFFIC_CONE.get(), 8)
                .pattern("O")
                .pattern("W")
                .pattern("O")
                .define('O', Items.ORANGE_CONCRETE)
                .define('W', Items.WHITE_CONCRETE)
                .unlockedBy("has_orange_concrete", has(Items.ORANGE_CONCRETE))
                .unlockedBy("has_white_concrete", has(Items.WHITE_CONCRETE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModBlocks.WORKSTATION.get())
                .pattern("III")
                .pattern("GCG")
                .pattern("GGG")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('G', Tags.Items.INGOTS_GOLD)
                .define('C', Items.CRAFTING_TABLE)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_gold_ingot", has(Tags.Items.INGOTS_GOLD))
                .unlockedBy("has_crafting_table", has(Items.CRAFTING_TABLE))
                .save(consumer);

        ShapedRecipeBuilder.shaped(ModItems.SPRAY_CAN.get())
                .pattern("IDI")
                .pattern("IWI")
                .pattern("III")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('D', Tags.Items.DYES_WHITE)
                .define('W', Items.BUCKET)
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_white_dye", has(Tags.Items.DYES_WHITE))
                .unlockedBy("has_bucket", has(Items.BUCKET))
                .save(consumer);

        //TODO crafting wheels, boost ramp and pads,

        CustomRecipeBuilder.special(ModRecipeSerializers.COLOR_SPRAY_CAN.get()).save(consumer, "vehicle:color_spray_can");
        CustomRecipeBuilder.special(ModRecipeSerializers.REFILL_SPRAY_CAN.get()).save(consumer, "vehicle:refill_spray_can");

        // Vehicles
        workstationCrafting(consumer, ModEntities.ALUMINUM_BOAT.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(ModItems.PANEL.get(), 10));
        workstationCrafting(consumer, ModEntities.ATV.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(Items.IRON_BARS, 4), WorkstationIngredient.of(Items.BLACK_WOOL, 4), WorkstationIngredient.of(Items.REDSTONE, 6), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.BUMPER_CAR.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of(Items.REDSTONE, 8), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.DIRT_BIKE.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 32), WorkstationIngredient.of(ModItems.PANEL.get(), 2), WorkstationIngredient.of(Items.GRAY_WOOL, 2));
        workstationCrafting(consumer, ModEntities.DUNE_BUGGY.get(), WorkstationIngredient.of(Items.YELLOW_CONCRETE, 8), WorkstationIngredient.of(Items.BLUE_CONCRETE, 4), WorkstationIngredient.of(Items.RED_CONCRETE, 2));
        workstationCrafting(consumer, ModEntities.GO_KART.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of(ModItems.PANEL.get(), 4));
        workstationCrafting(consumer, ModEntities.GOLF_CART.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(Items.IRON_BARS, 4), WorkstationIngredient.of(Items.WHITE_WOOL, 8), WorkstationIngredient.of(Items.REDSTONE, 12), WorkstationIngredient.of(ModItems.PANEL.get(), 16));
        workstationCrafting(consumer, ModEntities.JET_SKI.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 64), WorkstationIngredient.of(ModItems.PANEL.get(), 10));
        workstationCrafting(consumer, ModEntities.LAWN_MOWER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of(Items.BLACK_WOOL, 4), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.MINI_BIKE.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 24), WorkstationIngredient.of(Items.BLACK_WOOL, 2));
        workstationCrafting(consumer, ModEntities.MINI_BUS.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of(Items.GRAY_WOOL, 5), WorkstationIngredient.of(Tags.Items.GLASS_PANES, 9), WorkstationIngredient.of(Items.REDSTONE, 12), WorkstationIngredient.of(ModItems.PANEL.get(), 16));
        workstationCrafting(consumer, ModEntities.MOPED.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of(Items.IRON_BARS, 2), WorkstationIngredient.of(Items.BLACK_WOOL, 4), WorkstationIngredient.of(ModItems.PANEL.get(), 6));
        workstationCrafting(consumer, ModEntities.OFF_ROADER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of(Items.BLACK_WOOL, 8), WorkstationIngredient.of(Tags.Items.GLASS_PANES, 6), WorkstationIngredient.of(Items.REDSTONE, 12), WorkstationIngredient.of(ModItems.PANEL.get(), 24));
        workstationCrafting(consumer, ModEntities.SHOPPING_CART.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 8), WorkstationIngredient.of(Items.IRON_BARS, 4));
        workstationCrafting(consumer, ModEntities.SMART_CAR.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(Items.BLACK_WOOL, 8), WorkstationIngredient.of(Tags.Items.GLASS_PANES, 6), WorkstationIngredient.of(Items.REDSTONE, 8), WorkstationIngredient.of(ModItems.PANEL.get(), 16));
        workstationCrafting(consumer, ModEntities.SPEED_BOAT.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(Items.BLACK_WOOL, 8), WorkstationIngredient.of(Tags.Items.GLASS_PANES, 4), WorkstationIngredient.of(ModItems.PANEL.get(), 10));
        workstationCrafting(consumer, ModEntities.SPORTS_PLANE.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 180), WorkstationIngredient.of(Tags.Items.GLASS_PANES, 16), WorkstationIngredient.of(Items.REDSTONE, 18), WorkstationIngredient.of(ModItems.PANEL.get(), 32));
        workstationCrafting(consumer, ModEntities.TRACTOR.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 128), WorkstationIngredient.of(Items.BLACK_WOOL, 4), WorkstationIngredient.of(Items.REDSTONE, 8), WorkstationIngredient.of(ModItems.PANEL.get(), 16));

        // Trailers
        workstationCrafting(consumer, ModEntities.FERTILIZER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.FLUID_TRAILER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.SEEDER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 42), WorkstationIngredient.of(ModItems.PANEL.get(), 8));
        workstationCrafting(consumer, ModEntities.STORAGE_TRAILER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 36), WorkstationIngredient.of(ModItems.PANEL.get(), 2), WorkstationIngredient.of(Items.CHEST, 1));
        workstationCrafting(consumer, ModEntities.VEHICLE_TRAILER.get(), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 48), WorkstationIngredient.of(ModItems.PANEL.get(), 2));

        // Furniture
        //workstationCrafting(consumer, new ResourceLocation("cfm:bath"), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(ModItems.PANEL.get(), 10));
        dependantWorkstationCrafting(consumer, "cfm", new ResourceLocation("vehicle:sofa"), WorkstationIngredient.of(new ResourceLocation("cfm:rainbow_sofa"), 1), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 8));
        //workstationCrafting(consumer, new ResourceLocation("cfm:sofacopter"), WorkstationIngredient.of(Tags.Items.INGOTS_IRON, 80), WorkstationIngredient.of(ModItems.PANEL.get(), 10));

        fluidExtracting(consumer, Items.BLAZE_ROD, FluidEntry.of(ModFluids.BLAZE_JUICE.get(), 450));
        fluidExtracting(consumer, Items.ENDER_PEARL, FluidEntry.of(ModFluids.ENDER_SAP.get(), 600));
        fluidMixing(consumer, FluidEntry.of(ModFluids.ENDER_SAP.get(), 200),  FluidEntry.of(ModFluids.BLAZE_JUICE.get(), 200), Items.GLOWSTONE_DUST, FluidEntry.of(ModFluids.FUELIUM.get(), 400));
    }

    private static void netheriteSmithing(Consumer<IFinishedRecipe> consumer, Item inputItem, Item resultItem)
    {
        ResourceLocation id = Registry.ITEM.getKey(resultItem.asItem());
        SmithingRecipeBuilder.smithing(Ingredient.of(inputItem), Ingredient.of(Items.NETHERITE_INGOT), resultItem).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(consumer, new ResourceLocation(id.getNamespace(), id.getPath() + "_smithing"));
    }

    private static void workstationCrafting(Consumer<IFinishedRecipe> consumer, EntityType<? extends VehicleEntity> type, WorkstationIngredient ... materials)
    {
        ResourceLocation entityId = Objects.requireNonNull(type.getRegistryName());
        WorkstationRecipeBuilder.crafting(entityId, Arrays.asList(materials)).save(consumer, new ResourceLocation(entityId.getNamespace(), entityId.getPath() + "_crafting"));
    }

    private static void dependantWorkstationCrafting(Consumer<IFinishedRecipe> consumer, String modId, ResourceLocation entityId, WorkstationIngredient ... materials)
    {
        WorkstationRecipeBuilder.crafting(entityId, Arrays.asList(materials)).addCondition(new ModLoadedCondition(modId)).save(consumer, new ResourceLocation(entityId.getNamespace(), entityId.getPath() + "_crafting"));
    }

    private static void fluidExtracting(Consumer<IFinishedRecipe> consumer, IItemProvider provider, FluidEntry output)
    {
        ResourceLocation id = Objects.requireNonNull(output.getFluid().getRegistryName());
        FluidExtractorRecipeBuilder.extracting(Ingredient.of(provider), output).save(consumer, new ResourceLocation(id.getNamespace(), id.getPath() + "_extracting"));
    }

    private static void fluidMixing(Consumer<IFinishedRecipe> consumer, FluidEntry inputOne, FluidEntry inputTwo, IItemProvider provider, FluidEntry output)
    {
        ResourceLocation id = Objects.requireNonNull(output.getFluid().getRegistryName());
        FluidMixerRecipeBuilder.mixing(inputOne, inputTwo, Ingredient.of(provider), output).save(consumer, new ResourceLocation(id.getNamespace(), id.getPath() + "_mixing"));
    }
}
