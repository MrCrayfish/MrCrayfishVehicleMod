package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.*;
import com.mrcrayfish.vehicle.item.ItemBoostRamp;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import com.mrcrayfish.vehicle.util.BlockNames;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Author: MrCrayfish
 */
@GameRegistry.ObjectHolder(Reference.MOD_ID)
public class ModBlocks
{
    public static final Block TRAFFIC_CONE = null;
    public static final Block BOOST_PAD = null;
    public static final Block BOOST_RAMP = null;
    public static final Block STEEP_BOOST_RAMP = null;
    public static final Block FUELIUM = null;
    public static final Block ENDER_SAP = null;
    public static final Block BLAZE_JUICE = null;
    public static final Block FLUID_EXTRACTOR = null;
    public static final Block FLUID_MIXER = null;
    public static final Block GAS_PUMP = null;
    public static final Block FLUID_PIPE = null;
    public static final Block FLUID_PUMP = null;
    public static final Block FUEL_DRUM = null;
    public static final Block INDUSTRIAL_FUEL_DRUM = null;
    public static final Block WORKSTATION = null;
    public static final Block VEHICLE_CRATE = null;
    public static final Block JACK = null;
    public static final Block JACK_HEAD = null;

    public static void register()
    {
        Block blockTrafficCone = new BlockTrafficCone();
        register(blockTrafficCone, new ItemTrafficCone(blockTrafficCone));
        register(new BlockBoostPad());
        Block blockBoostRamp = new BlockBoostRamp();
        register(blockBoostRamp, new ItemBoostRamp(blockBoostRamp));
        register(new BlockSteepBoostRamp());
        register(new BlockLiquid(BlockNames.FUELIUM, ModFluids.FUELIUM, Material.WATER, 148, 242, 45), null);
        register(new BlockLiquid(BlockNames.ENDER_SAP, ModFluids.ENDER_SAP, Material.WATER, 10, 93, 80), null);
        register(new BlockLiquid(BlockNames.BLAZE_JUICE, ModFluids.BLAZE_JUICE, Material.WATER, 254, 198, 0), null);
        register(new BlockGasPump());
        register(new BlockFluidExtractor());
        register(new BlockFluidMixer());
        register(new BlockFluidPipe());
        register(new BlockFluidPump());
        register(new BlockFuelDrum(BlockNames.FUEL_DRUM, 40000));
        register(new BlockFuelDrum(BlockNames.INDUSTRIAL_FUEL_DRUM, 75000));
        register(new BlockWorkstation());
        Block blockVehicleCrate = new BlockVehicleCrate();
        register(blockVehicleCrate, (ItemBlock) new ItemBlock(blockVehicleCrate).setMaxStackSize(1));
        register(new BlockJack());
        register(new BlockObject(Material.WOOD, BlockNames.JACK_HEAD), null);
    }

    private static void register(Block block)
    {
        register(block, new ItemBlock(block));
    }

    private static void register(Block block, ItemBlock item)
    {
        if(block.getRegistryName() == null)
            throw new IllegalArgumentException("A block being registered does not have a registry name and could be successfully registered.");

        RegistrationHandler.Blocks.add(block);
        if(item != null)
        {
            item.setRegistryName(block.getRegistryName());
            RegistrationHandler.Items.add(item);
        }
    }
}
