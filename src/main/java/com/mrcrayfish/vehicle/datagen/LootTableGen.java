package com.mrcrayfish.vehicle.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModBlocks;
import com.mrcrayfish.vehicle.world.storage.loot.functions.CopyFluidTanks;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.loot.ConstantRange;
import net.minecraft.loot.ItemLootEntry;
import net.minecraft.loot.LootParameterSet;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.functions.CopyNbt;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.data.ForgeLootTableProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class LootTableGen extends ForgeLootTableProvider
{
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = ImmutableList.of(Pair.of(BlockProvider::new, LootParameterSets.BLOCK));

    public LootTableGen(DataGenerator generator)
    {
        super(generator);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
    {
        return this.tables;
    }

    private static class BlockProvider extends BlockLootTables
    {
        @Override
        protected void addTables()
        {
            this.add(ModBlocks.FLUID_EXTRACTOR.get(), BlockProvider::createFluidTankDrop);
            this.add(ModBlocks.FLUID_MIXER.get(), BlockProvider::createFluidTankDrop);
            this.add(ModBlocks.FUEL_DRUM.get(), BlockProvider::createFluidTankDrop);
            this.add(ModBlocks.INDUSTRIAL_FUEL_DRUM.get(), BlockProvider::createFluidTankDrop);
            this.dropSelf(ModBlocks.FLUID_PIPE.get());
            this.dropSelf(ModBlocks.FLUID_PUMP.get());
            this.dropSelf(ModBlocks.GAS_PUMP.get());
            this.dropSelf(ModBlocks.TRAFFIC_CONE.get());
            this.dropSelf(ModBlocks.WORKSTATION.get());
            this.dropSelf(ModBlocks.WORKSTATION.get());
            this.dropSelf(ModBlocks.JACK.get());
            this.dropSelf(ModBlocks.JACK_HEAD.get());
            this.add(ModBlocks.VEHICLE_CRATE.get(), BlockProvider::createVehicleCrateDrop);
        }

        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return ForgeRegistries.BLOCKS.getValues().stream().filter(block -> block.getRegistryName() != null && Reference.MOD_ID.equals(block.getRegistryName().getNamespace())).collect(Collectors.toSet());
        }

        protected static LootTable.Builder createFluidTankDrop(Block block)
        {
            return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(block).apply(CopyFluidTanks.copyFluidTanks()))));
        }

        protected static LootTable.Builder createVehicleCrateDrop(Block block)
        {
            return LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(ItemLootEntry.lootTableItem(block).apply(CopyNbt.copyData(CopyNbt.Source.BLOCK_ENTITY).copy("Vehicle", "BlockEntityTag.Vehicle").copy("Color", "BlockEntityTag.Color").copy("EngineStack", "BlockEntityTag.EngineStack").copy("Creative", "BlockEntityTag.Creative").copy("WheelStack", "BlockEntityTag.WheelStack")))));
        }
    }
}
