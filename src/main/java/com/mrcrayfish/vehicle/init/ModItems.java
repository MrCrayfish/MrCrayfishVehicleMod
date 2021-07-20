package com.mrcrayfish.vehicle.init;

import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.entity.EngineTier;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.WheelType;
import com.mrcrayfish.vehicle.item.EngineItem;
import com.mrcrayfish.vehicle.item.HammerItem;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.item.KeyItem;
import com.mrcrayfish.vehicle.item.SprayCanItem;
import com.mrcrayfish.vehicle.item.WheelItem;
import com.mrcrayfish.vehicle.item.WrenchItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class ModItems
{
    public static final DeferredRegister<Item> REGISTER = DeferredRegister.create(ForgeRegistries.ITEMS, Reference.MOD_ID);

    public static final RegistryObject<Item> PANEL = register("panel", new Item(new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> STANDARD_WHEEL = register("standard_wheel", new WheelItem(WheelType.STANDARD, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> SPORTS_WHEEL = register("sports_wheel", new WheelItem(WheelType.SPORTS, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> RACING_WHEEL = register("racing_wheel", new WheelItem(WheelType.RACING, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> OFF_ROAD_WHEEL = register("off_road_wheel", new WheelItem(WheelType.OFF_ROAD, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> SNOW_WHEEL = register("snow_wheel", new WheelItem(WheelType.SNOW, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> ALL_TERRAIN_WHEEL = register("all_terrain_wheel", new WheelItem(WheelType.ALL_TERRAIN, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)).setColored());
    public static final RegistryObject<Item> PLASTIC_WHEEL = register("plastic_wheel", new WheelItem(WheelType.PLASTIC, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> IRON_SMALL_ENGINE = register("iron_small_engine", new EngineItem(EngineType.SMALL_MOTOR, EngineTier.IRON, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> GOLD_SMALL_ENGINE = register("gold_small_engine", new EngineItem(EngineType.SMALL_MOTOR, EngineTier.GOLD, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> DIAMOND_SMALL_ENGINE = register("diamond_small_engine", new EngineItem(EngineType.SMALL_MOTOR, EngineTier.DIAMOND, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> NETHERITE_SMALL_ENGINE = register("netherite_small_engine", new EngineItem(EngineType.SMALL_MOTOR, EngineTier.NETHERITE, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> IRON_LARGE_ENGINE = register("iron_large_engine", new EngineItem(EngineType.LARGE_MOTOR, EngineTier.IRON, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> GOLD_LARGE_ENGINE = register("gold_large_engine", new EngineItem(EngineType.LARGE_MOTOR, EngineTier.GOLD, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> DIAMOND_LARGE_ENGINE = register("diamond_large_engine", new EngineItem(EngineType.LARGE_MOTOR, EngineTier.DIAMOND, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> NETHERITE_LARGE_ENGINE = register("netherite_large_engine", new EngineItem(EngineType.LARGE_MOTOR, EngineTier.NETHERITE, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> IRON_ELECTRIC_ENGINE = register("iron_electric_engine", new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.IRON, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> GOLD_ELECTRIC_ENGINE = register("gold_electric_engine", new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.GOLD, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> DIAMOND_ELECTRIC_ENGINE = register("diamond_electric_engine", new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.DIAMOND, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> NETHERITE_ELECTRIC_ENGINE = register("netherite_electric_engine", new EngineItem(EngineType.ELECTRIC_MOTOR, EngineTier.NETHERITE, new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<SprayCanItem> SPRAY_CAN = register("spray_can", new SprayCanItem(new Item.Properties().tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> JERRY_CAN = register("jerry_can", new JerryCanItem(Config.SERVER.jerryCanCapacity::get, new Item.Properties().stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> INDUSTRIAL_JERRY_CAN = register("industrial_jerry_can", new JerryCanItem(Config.SERVER.industrialJerryCanCapacity::get, new Item.Properties().stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> WRENCH = register("wrench", new WrenchItem(new Item.Properties().stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> HAMMER = register("hammer", new HammerItem(new Item.Properties().durability(200).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<Item> KEY = register("key", new KeyItem(new Item.Properties().stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<BucketItem> FUELIUM_BUCKET = register("fuelium_bucket", new BucketItem(ModFluids.FUELIUM, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<BucketItem> ENDER_SAP_BUCKET = register("ender_sap_bucket", new BucketItem(ModFluids.ENDER_SAP, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));
    public static final RegistryObject<BucketItem> BLAZE_JUICE_BUCKET = register("blaze_juice_bucket", new BucketItem(ModFluids.BLAZE_JUICE, (new Item.Properties()).craftRemainder(Items.BUCKET).stacksTo(1).tab(VehicleMod.CREATIVE_TAB)));

    private static <T extends Item> RegistryObject<T> register(String id, T item)
    {
        return ModItems.REGISTER.register(id,() -> item);
    }
}