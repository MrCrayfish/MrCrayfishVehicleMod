package com.mrcrayfish.vehicle.client.model;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public enum SpecialModels implements ISpecialModel
{
    ATV_BODY("atv_body"),
    ATV_HANDLES("atv_handles"),
    DUNE_BUGGY_BODY("dune_buggy_body"),
    DUNE_BUGGY_HANDLES("dune_buggy_handles"),
    GO_KART_BODY("go_kart_body"),
    GO_KART_STEERING_WHEEL("go_kart_steering_wheel"),
    SHOPPING_CART_BODY("shopping_cart_body"),
    MINI_BIKE_BODY("mini_bike_body"),
    MINI_BIKE_HANDLES("mini_bike_handles"),
    BUMPER_CAR_BODY("bumper_car_body"),
    JET_SKI_BODY("jet_ski_body"),
    SPEED_BOAT_BODY("speed_boat_body"),
    ALUMINUM_BOAT_BODY("aluminum_boat_body"),
    SMART_CAR_BODY("smart_car_body"),
    LAWN_MOWER_BODY("lawn_mower_body"),
    MOPED_BODY("moped_body"),
    MOPED_MUD_GUARD("moped_mud_guard"),
    MOPED_HANDLES("moped_handles"),
    SPORTS_PLANE("sports_plane_body"),
    SPORTS_PLANE_WING("sports_plane_wing"),
    SPORTS_PLANE_WHEEL_COVER("sports_plane_wheel_cover"),
    SPORTS_PLANE_LEG("sports_plane_leg"),
    SPORTS_PLANE_PROPELLER("sports_plane_propeller"),
    GOLF_CART_BODY("golf_cart_body"),
    OFF_ROADER_BODY("off_roader_body"),
    TRACTOR("tractor_body"),
    MINI_BUS_BODY("mini_bus_body"),
    DIRT_BIKE_BODY("dirt_bike_body"),
    DIRT_BIKE_HANDLES("dirt_bike_handles"),
    VEHICLE_TRAILER("trailer_body"),
    STORAGE_TRAILER("trailer_chest_body"),
    SEEDER_TRAILER("trailer_seeder_body"),
    FERTILIZER_TRAILER("trailer_fertilizer_body"),
    FLUID_TRAILER("trailer_fluid_body"),

    VEHICLE_CRATE_SIDE("vehicle_crate_panel_side"),
    VEHICLE_CRATE_TOP("vehicle_crate_panel_top"),
    JACK_PISTON_HEAD("jack_piston_head"),
    SEED_SPIKER("seed_spiker"),
    NOZZLE("nozzle"),
    TOW_BAR("tow_bar"),
    BIG_TOW_BAR("big_tow_bar"),
    FUEL_DOOR_CLOSED("fuel_door_closed"),
    FUEL_DOOR_OPEN("fuel_door_open"),
    SMALL_FUEL_DOOR_CLOSED("small_fuel_door_closed"),
    SMALL_FUEL_DOOR_OPEN("small_fuel_door_open"),
    KEY_HOLE("key_hole"),
    SOFA_HELICOPTER_ARM("sofa_helicopter_arm"),
    SOFA_HELICOPTER_SKID("sofa_helicopter_skid"),

    /* Mod dependent models */
    RED_SOFA(new ModelResourceLocation("cfm:red_sofa", "inventory"), false),
    RAINBOW_SOFA(new ModelResourceLocation("cfm:rainbow_sofa", "inventory"), false);

    // Add spray can lid
    /**
     * The location of an item model in the [MOD_ID]/models/vehicle/[NAME] folder
     */
    private ResourceLocation modelLocation;

    /**
     * Determines if the model should be loaded as a special model
     */
    private boolean specialModel;

    /**
     * Cached model
     */
    @OnlyIn(Dist.CLIENT)
    private IBakedModel cachedModel;

    /**
     * Sets the model's location
     *
     * @param modelName name of the model file
     */
    SpecialModels(String modelName)
    {
        this(new ResourceLocation(Reference.MOD_ID, "vehicle/" + modelName), true);
    }

    /**
     * Sets the model's location
     *
     * @param resource name of the model file
     */
    SpecialModels(ResourceLocation resource, boolean specialModel)
    {
        this.modelLocation = resource;
        this.specialModel = specialModel;
    }

    /**
     * Gets the model
     *
     * @return isolated model
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            IBakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            if(model == Minecraft.getInstance().getModelManager().getMissingModel())
            {
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void register(ModelRegistryEvent event)
    {
        for(SpecialModels model : values())
        {
            if(model.specialModel)
            {
                ModelLoader.addSpecialModel(model.modelLocation);
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static void clearModelCache()
    {
        for(SpecialModels model : values())
        {
            model.cachedModel = null;
        }
    }
}
