package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
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
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModBlocks
{
    private static final List<Block> BLOCKS = new LinkedList<>();
    private static final List<Item> ITEMS = new LinkedList<>();

    public static final Block TRAFFIC_CONE = register(new BlockTrafficCone(), ItemTrafficCone::new);
    public static final Block BOOST_PAD = register(new BlockBoostPad());
    public static final Block BOOST_RAMP = register(new BlockBoostRamp(), ItemBoostRamp::new);
    public static final Block STEEP_BOOST_RAMP = register(new BlockSteepBoostRamp());
    public static final Block FLUID_EXTRACTOR = register(new BlockFluidExtractor());
    public static final Block FLUID_MIXER = register(new BlockFluidMixer());
    public static final Block GAS_PUMP = register(new BlockGasPump());
    public static final Block FLUID_PIPE = register(new BlockFluidPipe());
    public static final Block FLUID_PUMP = register(new BlockFluidPump());
    public static final Block FUEL_DRUM = register(new BlockFuelDrum(Names.Block.FUEL_DRUM, 40000));
    public static final Block INDUSTRIAL_FUEL_DRUM = register(new BlockFuelDrum(Names.Block.INDUSTRIAL_FUEL_DRUM, 75000));
    public static final Block WORKSTATION = register(new BlockWorkstation());
    public static final Block VEHICLE_CRATE = register(new BlockVehicleCrate(), block -> new BlockItem(block, new Item.Properties().maxStackSize(1)));
    public static final Block JACK = register(new BlockJack());
    public static final Block JACK_HEAD = register(new BlockObject(Names.Block.JACK_HEAD, Block.Properties.create(Material.WOOD)), null);

    //public static final Block FUELIUM = null;
    //public static final Block ENDER_SAP = null;
    //public static final Block BLAZE_JUICE = null;

    private static void register()
    {
        //buildSound(new BlockLiquid(BlockNames.FUELIUM, ModFluids.FUELIUM, Material.WATER, 148, 242, 45), null);
        //buildSound(new BlockLiquid(BlockNames.ENDER_SAP, ModFluids.ENDER_SAP, Material.WATER, 10, 93, 80), null);
        //buildSound(new BlockLiquid(BlockNames.BLAZE_JUICE, ModFluids.BLAZE_JUICE, Material.WATER, 254, 198, 0), null);
    }

    private static Block register(Block block)
    {
        return register(block, block1 -> new BlockItem(block1, new Item.Properties().group(VehicleMod.CREATIVE_TAB)));
    }

    private static Block register(Block block, @Nullable Function<Block, BlockItem> supplier)
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
        return block;
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
