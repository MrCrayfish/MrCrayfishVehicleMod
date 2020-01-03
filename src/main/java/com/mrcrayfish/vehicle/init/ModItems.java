package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.item.*;
import com.mrcrayfish.vehicle.util.Names;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

/**
 * Author: MrCrayfish
 */
@ObjectHolder(Reference.MOD_ID)
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModItems
{
    public static final Item PANEL = null;
    public static final Item STANDARD_WHEEL = null;
    public static final Item SPORTS_WHEEL = null;
    public static final Item RACING_WHEEL = null;
    public static final Item OFF_ROAD_WHEEL = null;
    public static final Item SNOW_WHEEL = null;
    public static final Item ALL_TERRAIN_WHEEL = null;
    public static final Item PLASTIC_WHEEL = null;

    public static final Item WOOD_SMALL_ENGINE = null;
    public static final Item STONE_SMALL_ENGINE = null;
    public static final Item IRON_SMALL_ENGINE = null;
    public static final Item GOLD_SMALL_ENGINE = null;
    public static final Item DIAMOND_SMALL_ENGINE = null;
    public static final Item WOOD_LARGE_ENGINE = null;
    public static final Item STONE_LARGE_ENGINE = null;
    public static final Item IRON_LARGE_ENGINE = null;
    public static final Item GOLD_LARGE_ENGINE = null;
    public static final Item DIAMOND_LARGE_ENGINE = null;
    public static final Item WOOD_ELECTRIC_ENGINE = null;
    public static final Item STONE_ELECTRIC_ENGINE = null;
    public static final Item IRON_ELECTRIC_ENGINE = null;
    public static final Item GOLD_ELECTRIC_ENGINE = null;
    public static final Item DIAMOND_ELECTRIC_ENGINE = null;
    public static final Item SPRAY_CAN = null;
    public static final Item JERRY_CAN = null;
    public static final Item INDUSTRIAL_JERRY_CAN = null;
    public static final Item WRENCH = null;
    public static final Item HAMMER = null;
    public static final Item KEY = null;
    public static final Item FUELIUM_BUCKET = null;
    public static final Item ENDER_SAP_BUCKET = null;
    public static final Item BLAZE_JUICE_BUCKET = null;

    //register("water_bucket", );

    @SubscribeEvent
    public static void register(RegistryEvent.Register<Item> event)
    {
        IForgeRegistry<Item> registry = event.getRegistry();
        registry.register(new PanelItem());
        registry.register(new WheelItem(Names.Item.STANDARD_WHEEL, WheelType.STANDARD).setColored());
        registry.register(new WheelItem(Names.Item.SPORTS_WHEEL, WheelType.SPORTS).setColored());
        registry.register(new WheelItem(Names.Item.RACING_WHEEL, WheelType.RACING).setColored());
        registry.register(new WheelItem(Names.Item.OFF_ROAD_WHEEL, WheelType.OFF_ROAD).setColored());
        registry.register(new WheelItem(Names.Item.SNOW_WHEEL, WheelType.SNOW).setColored());
        registry.register(new WheelItem(Names.Item.ALL_TERRAIN_WHEEL, WheelType.ALL_TERRAIN).setColored());
        registry.register(new WheelItem(Names.Item.PLASTIC_WHEEL, WheelType.PLASTIC));
        registry.register(new EngineItem(Names.Item.WOOD_SMALL_ENGINE, EngineType.SMALL_MOTOR, EngineTier.WOOD));
        registry.register(new EngineItem(Names.Item.STONE_SMALL_ENGINE, EngineType.SMALL_MOTOR, EngineTier.STONE));
        registry.register(new EngineItem(Names.Item.IRON_SMALL_ENGINE, EngineType.SMALL_MOTOR, EngineTier.IRON));
        registry.register(new EngineItem(Names.Item.GOLD_SMALL_ENGINE, EngineType.SMALL_MOTOR, EngineTier.GOLD));
        registry.register(new EngineItem(Names.Item.DIAMOND_SMALL_ENGINE, EngineType.SMALL_MOTOR, EngineTier.DIAMOND));
        registry.register(new EngineItem(Names.Item.WOOD_LARGE_ENGINE, EngineType.LARGE_MOTOR, EngineTier.WOOD));
        registry.register(new EngineItem(Names.Item.STONE_LARGE_ENGINE, EngineType.LARGE_MOTOR, EngineTier.STONE));
        registry.register(new EngineItem(Names.Item.IRON_LARGE_ENGINE, EngineType.LARGE_MOTOR, EngineTier.IRON));
        registry.register(new EngineItem(Names.Item.GOLD_LARGE_ENGINE, EngineType.LARGE_MOTOR, EngineTier.GOLD));
        registry.register(new EngineItem(Names.Item.DIAMOND_LARGE_ENGINE, EngineType.LARGE_MOTOR, EngineTier.DIAMOND));
        registry.register(new EngineItem(Names.Item.WOOD_ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR, EngineTier.WOOD));
        registry.register(new EngineItem(Names.Item.STONE_ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR, EngineTier.STONE));
        registry.register(new EngineItem(Names.Item.IRON_ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR, EngineTier.IRON));
        registry.register(new EngineItem(Names.Item.GOLD_ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR, EngineTier.GOLD));
        registry.register(new EngineItem(Names.Item.DIAMOND_ELECTRIC_ENGINE, EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND));
        registry.register(new SprayCanItem());
        registry.register(new JerryCanItem(Names.Item.JERRY_CAN, 5000, 100));
        registry.register(new JerryCanItem(Names.Item.INDUSTRIAL_JERRY_CAN, 15000, 150));
        registry.register(new WrenchItem());
        registry.register(new HammerItem());
        registry.register(new KeyItem());
        registry.register(new BucketItem(() -> ModFluids.FUELIUM, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(VehicleMod.CREATIVE_TAB)).setRegistryName(Reference.MOD_ID, "fuelium_bucket"));
        registry.register(new BucketItem(() -> ModFluids.ENDER_SAP, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(VehicleMod.CREATIVE_TAB)).setRegistryName(Reference.MOD_ID, "ender_sap_bucket"));
        registry.register(new BucketItem(() -> ModFluids.BLAZE_JUICE, (new Item.Properties()).containerItem(Items.BUCKET).maxStackSize(1).group(VehicleMod.CREATIVE_TAB)).setRegistryName(Reference.MOD_ID, "blaze_juice_bucket"));
    }
}
