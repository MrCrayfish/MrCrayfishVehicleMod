package com.mrcrayfish.vehicle.datagen;

import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModRecipeSerializers;
import net.minecraft.data.CustomRecipeBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapedRecipeBuilder;
import net.minecraft.data.SmithingRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.common.Tags;

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
                .pattern("TDT")
                .pattern("IPI")
                .define('I', Tags.Items.INGOTS_IRON)
                .define('R', Items.REPEATER)
                .define('T', Items.REDSTONE_TORCH)
                .define('P', ModItems.PANEL.get())
                .unlockedBy("has_iron_ingot", has(Tags.Items.INGOTS_IRON))
                .unlockedBy("has_repeater", has(Items.REPEATER))
                .unlockedBy("has_redstone_torch", has(Items.REDSTONE_TORCH))
                .unlockedBy("has_panel", has(ModItems.PANEL.get()))
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

        CustomRecipeBuilder.special(ModRecipeSerializers.COLOR_SPRAY_CAN.get()).save(consumer, "color_spray_can");
        CustomRecipeBuilder.special(ModRecipeSerializers.REFILL_SPRAY_CAN.get()).save(consumer, "refill_spray_can");
    }

    private static void netheriteSmithing(Consumer<IFinishedRecipe> consumer, Item inputItem, Item resultItem)
    {
        SmithingRecipeBuilder.smithing(Ingredient.of(inputItem), Ingredient.of(Items.NETHERITE_INGOT), resultItem).unlocks("has_netherite_ingot", has(Items.NETHERITE_INGOT)).save(consumer, Registry.ITEM.getKey(resultItem.asItem()).getPath() + "_smithing");
    }
}
