package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.block.*;
import com.mrcrayfish.vehicle.item.ItemBoostRamp;
import com.mrcrayfish.vehicle.item.ItemTrafficCone;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
public class ModBlocks
{
    private static final List<Block> BLOCKS = new LinkedList<>();
    private static final List<Item> ITEMS = new LinkedList<>();

    public static final Block TRAFFIC_CONE = null;
    public static final Block BOOST_PAD = null;
    public static final Block BOOST_RAMP = null;
    public static final Block STEEP_BOOST_RAMP = null;
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

    //public static final Block FUELIUM = null;
    //public static final Block ENDER_SAP = null;
    //public static final Block BLAZE_JUICE = null;

    private static void register()
    {
        register(new BlockTrafficCone(), ItemTrafficCone::new);
        register(new BlockBoostPad());
        register(new BlockBoostRamp(), ItemBoostRamp::new);
        register(new BlockSteepBoostRamp());
        register(new BlockGasPump());
        register(new BlockFluidExtractor());
        register(new BlockFluidMixer());
        register(new BlockFluidPipe());
        register(new BlockFluidPump());
        register(new BlockFuelDrum(Names.Block.FUEL_DRUM, 40000));
        register(new BlockFuelDrum(Names.Block.INDUSTRIAL_FUEL_DRUM, 75000));
        register(new BlockWorkstation());
        register(new BlockVehicleCrate(), block -> new BlockItem(block, new Item.Properties().maxStackSize(1)));
        register(new BlockJack());
        register(new BlockObject(Names.Block.JACK_HEAD, Block.Properties.create(Material.WOOD)), null);

        //buildSound(new BlockLiquid(BlockNames.FUELIUM, ModFluids.FUELIUM, Material.WATER, 148, 242, 45), null);
        //buildSound(new BlockLiquid(BlockNames.ENDER_SAP, ModFluids.ENDER_SAP, Material.WATER, 10, 93, 80), null);
        //buildSound(new BlockLiquid(BlockNames.BLAZE_JUICE, ModFluids.BLAZE_JUICE, Material.WATER, 254, 198, 0), null);
    }

    private static void register(Block block)
    {
        register(block, block1 -> new BlockItem(block1, new Item.Properties()));
    }

    private static void register(Block block, @Nullable Function<Block, BlockItem> supplier)
    {
        if(block.getRegistryName() == null)
            throw new IllegalArgumentException("A block being registered does not have a registry name and could be successfully registered.");

        BLOCKS.add(block);
        if(supplier != null)
        {
            BlockItem item = supplier.apply(block);
            item.setRegistryName(block.getRegistryName());
            ITEMS.add(item);
        }
    }

    @SubscribeEvent
    public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        ModBlocks.register();
        BLOCKS.forEach(block -> event.getRegistry().register(block));
    }

    @SubscribeEvent
    public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        ITEMS.forEach(block -> event.getRegistry().register(block));
    }
}
