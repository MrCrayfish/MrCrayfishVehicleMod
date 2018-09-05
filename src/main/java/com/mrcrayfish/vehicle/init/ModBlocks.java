package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.block.*;
import com.mrcrayfish.vehicle.item.ItemBoostRamp;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;

/**
 * Author: MrCrayfish
 */
public class ModBlocks
{
    public static final Block TRAFFIC_CONE;
    public static final Block BOOST_PAD;
    public static final Block BOOST_RAMP;
    public static final Block STEEP_BOOST_RAMP;

    public static final Block FUELIUM;
    public static final Block ENDER_SAP;
    public static final Block BLAZE_JUICE;
    public static final Block REFINERY;
    public static final Block GAS_PUMP;
    public static final Block FLUID_PIPE;
    public static final Block FLUID_PUMP;
    public static final Block FUEL_DRUM;

    static
    {
        TRAFFIC_CONE = new BlockTrafficCone();
        BOOST_PAD = new BlockBoostPad();
        BOOST_RAMP = new BlockBoostRamp();
        STEEP_BOOST_RAMP = new BlockSteepBoostRamp();
        FUELIUM = new BlockLiquid("fuelium", ModFluids.FUELIUM, Material.WATER);
        ENDER_SAP = new BlockLiquid("ender_sap", ModFluids.ENDER_SAP, Material.WATER);
        BLAZE_JUICE = new BlockLiquid("blaze_juice", ModFluids.BLAZE_JUICE, Material.WATER);
        GAS_PUMP = new BlockGasPump();
        REFINERY = new BlockFluidExtractor();
        FLUID_PIPE = new BlockFluidPipe();
        FLUID_PUMP = new BlockFluidPump();
        FUEL_DRUM = new BlockFuelDrum("fuel_drum", 25000);
    }

    public static void register()
    {
        registerBlock(TRAFFIC_CONE, new ItemTrafficCone(TRAFFIC_CONE));
        registerBlock(BOOST_PAD);
        registerBlock(BOOST_RAMP, new ItemBoostRamp(BOOST_RAMP));
        registerBlock(STEEP_BOOST_RAMP);
        registerBlock(FUELIUM, null);
        registerBlock(ENDER_SAP, null);
        registerBlock(BLAZE_JUICE, null);
        registerBlock(GAS_PUMP);
        registerBlock(REFINERY);
        registerBlock(FLUID_PIPE);
        registerBlock(FLUID_PUMP);
        registerBlock(FUEL_DRUM);
    }

    private static void registerBlock(Block block)
    {
        registerBlock(block, new ItemBlock(block));
    }

    private static void registerBlock(Block block, ItemBlock item)
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
