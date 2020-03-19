package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Enum of values that register and retrieve models that do not need to be associated with a particular item
 * Author: Phylogeny (https://github.com/Phylogeny)
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public enum SpecialModels
{
    VEHICLE_CRATE("vehicle_crate_panel"),
    CHEST_TRAILER("trailer_chest_body"),
    SEEDER_TRAILER("trailer_seeder_body"),
    SEED_SPIKER("seed_spiker"),
    FERTILIZER_TRAILER("trailer_fertilizer_body"),
    FLUID_TRAILER("trailer_fluid_body"),
    NOZZLE("nozzle"),
    ATV_BODY("atv_body"),
    ATV_HANDLE_BAR("handle_bar"),
    DUNE_BUGGY_BODY("dune_buggy_body"),
    DUNE_BUGGY_HANDLE_BAR("dune_buggy_handle_bar"),
    GO_KART_BODY("go_kart_body"),
    GO_KART_STEERING_WHEEL("go_kart_steering_wheel"),
    SHOPPING_CART_BODY("shopping_cart_body"),
    MINI_BIKE_BODY("mini_bike_body"),
    MINI_BIKE_HANDLE_BAR("mini_bike_handle_bar"),
    BUMPER_CAR_BODY("bumper_car_body"),
    JET_SKI_BODY("jet_ski_body"),
    SPEED_BOAT_BODY("speed_boat_body"),
    ALUMINUM_BOAT_BODY("aluminum_boat_body"),
    SMART_CAR_BODY("smart_car_body"),
    LAWN_MOWER_BODY("lawn_mower_body"),
    MOPED_BODY("moped_body"),
    MOPED_MUD_GUARD("moped_mud_guard"),
    MOPED_HANDLE_BAR("moped_handle_bar"),
    SPORTS_PLANE_BODY("sports_plane_body"),
    SPORTS_PLANE_WING("sports_plane_wing"),
    SPORTS_PLANE_WHEEL_COVER("sports_plane_wheel_cover"),
    SPORTS_PLANE_LEG("sports_plane_leg"),
    SPORTS_PLANE_PROPELLER("sports_plane_propeller"),
    GOLF_CART_BODY("golf_cart_body"),
    OFF_ROADER_BODY("off_roader_body"),
    TRACTOR_BODY("tractor_body"),
    TRAILER_BODY("trailer_body"),
    MINI_BUS_BODY("mini_bus_body"),
    TOW_BAR("tow_bar"),
    BIG_TOW_BAR("big_tow_bar"),
    FUEL_PORT_CLOSED("fuel_port_closed"),
    FUEL_PORT_BODY("fuel_port_body"),
    FUEL_PORT_LID("fuel_port_lid"),
    FUEL_PORT_2_CLOSED("fuel_port_2_closed"),
    FUEL_PORT_2_PIPE("fuel_port_2_pipe"),
    KEY_HOLE("key_hole"),
    COUCH_HELICOPTER_ARM("couch_helicopter_arm"),
    COUCH_HELICOPTER_SKID("couch_helicopter_skid"),

    COUCH(new ModelResourceLocation("cfm:couch", "colour=14,facing=east,type=both"), false),
    BLADE(new ModelResourceLocation("cfm:ceiling_fan_fans", "inventory"), false),
    BATH(new ModelResourceLocation("cfm:bath_bottom", "inventory"), false);

    /**
     * An arbitrary item in your mod to register isolated models as variants of
     */
    private static Item item;

    /**
     * The location of an item model in the [MOD_ID]/models/item folder
     */
    private ModelResourceLocation modelLocation;

    /**
     * If true, registers this model as a new variant
     */
    private boolean specialModel;

    /**
     * Cached model
     */
    @SideOnly(Side.CLIENT)
    private IBakedModel cachedModel;

    /**
     * Sets the model's location
     *
     * @param modelName name of the model file
     */
    SpecialModels(String modelName)
    {
        this(new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, modelName), null), true);
    }

    SpecialModels(ModelResourceLocation resourceLocation, boolean specialModel)
    {
        this.modelLocation = resourceLocation;
        this.specialModel = specialModel;
    }

    /**
     * Gets the isolated model
     *
     * @return isolated model
     */
    @SideOnly(Side.CLIENT)
    public IBakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            ModelManager modelManager = RenderUtil.getModelManager();
            IBakedModel model = modelManager.getModel(this.modelLocation);
            if(model == modelManager.getMissingModel())
            {
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void register(ModelRegistryEvent event)
    {
        SpecialModels.item = ModItems.MODELS;
        for(SpecialModels model : SpecialModels.values())
        {
            if(model.specialModel)
            {
                ModelLoader.registerItemVariants(item, model.modelLocation);
            }
        }
    }
}
