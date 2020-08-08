package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.block.*;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Reference.MOD_ID);

    public static final RegistryObject<Block> TRAFFIC_CONE = register("traffic_cone", new BlockTrafficCone(), ItemTrafficCone::new);
    public static final RegistryObject<Block> FLUID_EXTRACTOR = register("fluid_extractor", new BlockFluidExtractor());
    public static final RegistryObject<Block> FLUID_MIXER = register("fluid_mixer", new BlockFluidMixer());
    public static final RegistryObject<Block> GAS_PUMP = register("gas_pump", new BlockGasPump());
    public static final RegistryObject<Block> FLUID_PIPE = register("fluid_pipe", new BlockFluidPipe());
    public static final RegistryObject<Block> FLUID_PUMP = register("fluid_pump", new BlockFluidPump());
    public static final RegistryObject<Block> FUEL_DRUM = register("fuel_drum", new BlockFuelDrum());
    public static final RegistryObject<Block> INDUSTRIAL_FUEL_DRUM = register("industrial_fuel_drum", new BlockIndustrialFuelDrum());
    public static final RegistryObject<Block> WORKSTATION = register("workstation", new BlockWorkstation());
    public static final RegistryObject<Block> VEHICLE_CRATE = register("vehicle_crate", new BlockVehicleCrate(), block -> new BlockItem(block, new Item.Properties().maxStackSize(1)));
    public static final RegistryObject<Block> JACK = register("jack", new BlockJack());
    public static final RegistryObject<FlowingFluidBlock> FUELIUM = register("fuelium", new FlowingFluidBlock(ModFluids.FLOWING_FUELIUM, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()), null);
    public static final RegistryObject<FlowingFluidBlock> ENDER_SAP = register("ender_sap", new FlowingFluidBlock(ModFluids.FLOWING_ENDER_SAP, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()), null);
    public static final RegistryObject<FlowingFluidBlock> BLAZE_JUICE = register("blaze_juice", new FlowingFluidBlock(ModFluids.FLOWING_BLAZE_JUICE, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops()), null);
    //public static final Block BOOST_PAD = registerConstructor(new BlockBoostPad(), null);
    //public static final Block BOOST_RAMP = registerConstructor(new BlockBoostRamp(), null); //ItemBoostRamp::new
    //public static final Block STEEP_BOOST_RAMP = registerConstructor(new BlockSteepBoostRamp(), null);

    private static <T extends Block> RegistryObject<T> register(String id, T block)
    {
        return register(id, block, block1 -> new BlockItem(block1, new Item.Properties().group(VehicleMod.CREATIVE_TAB)));
    }

    private static <T extends Block> RegistryObject<T> register(String id, T block, @Nullable Function<T, BlockItem> supplier)
    {
        if(supplier != null)
        {
            ModItems.ITEMS.register(id, () -> supplier.apply(block));
        }
        return ModBlocks.BLOCKS.register(id, () -> block);
    }
}