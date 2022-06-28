package com.mrcrayfish.vehicle;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class Config
{
    public static class Client
    {
        public final ForgeConfigSpec.BooleanValue renderOutlines;
        public final ForgeConfigSpec.BooleanValue renderDebugging;
        public final ForgeConfigSpec.BooleanValue reloadRayTracerEachTick;
        public final ForgeConfigSpec.BooleanValue enabledLeftClick;
        public final ForgeConfigSpec.BooleanValue enabledSpeedometer;
        public final ForgeConfigSpec.BooleanValue autoPerspective;
        public final ForgeConfigSpec.BooleanValue forceFirstPersonOnExit;
        public final ForgeConfigSpec.BooleanValue workstationAnimation;
        public final ForgeConfigSpec.BooleanValue reloadVehiclePropertiesEachTick;
        public final ForgeConfigSpec.BooleanValue forceRenderAllInteractableBoxes;
        public final ForgeConfigSpec.BooleanValue debugCamera;
        public final ForgeConfigSpec.IntValue hoseSegments;

        public final ForgeConfigSpec.BooleanValue immersiveCamera;
        public final ForgeConfigSpec.BooleanValue followVehicleOrientation;
        public final ForgeConfigSpec.BooleanValue useVehicleAsFocusPoint;
        public final ForgeConfigSpec.BooleanValue shouldFollowYaw;
        public final ForgeConfigSpec.BooleanValue shouldFollowPitch;
        public final ForgeConfigSpec.BooleanValue shouldFollowRoll;

        Client(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client configuration settings").push("client");
            {
                builder.comment("Configuration options for debugging vehicles").push("debug");
                {
                    this.renderOutlines = builder.comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.").translation(Reference.MOD_ID + ".config.client.debug.render_outlines").define("renderOutlines", false);
                    this.renderDebugging = builder.comment("If true, renders lines to help visualise steering direction and target position.").translation(Reference.MOD_ID + ".config.client.debug.render_steering_debug").define("renderSteeringDebug", false);
                    this.reloadRayTracerEachTick = builder.comment("If true, the raytracer will be reloaded each tick.").translation(Reference.MOD_ID + ".config.client.debug.raytracer.continuous_reload").define("reloadRaytracerEachTick", false);
                    this.reloadVehiclePropertiesEachTick = builder.comment("If true, the vehicle properties will be reloaded each tick.").translation(Reference.MOD_ID + ".config.client.debug.properties.continuous_reload").define("reloadVehiclePropertiesEachTick", false);
                    this.forceRenderAllInteractableBoxes = builder.comment("If true, when rendering debug outlines all interactables boxes will be rendered rather than just the active").translation(Reference.MOD_ID + ".config.client.debug.properties.force_render_all_interactable_boxes").define("forceRenderAllInteractableBoxes", false);
                    this.debugCamera = builder.comment("Enables use of debug camera tools").define("debugCamera", false);
                }
                builder.pop();

                builder.comment("Configuration options for vehicle interaction").push("interaction");
                {
                    this.enabledLeftClick = builder.comment("If true, raytraces will be performed on nearby vehicles when left-clicking the mouse, rather than just right-clicking it. This allows one to be damaged/broken when clicking anywhere on it, rather than just on its bounding box.").translation(Reference.MOD_ID + ".config.client.interaction.left_click").define("enabledLeftClick", true);
                }
                builder.pop();

                builder.comment("Configuration for display related options").push("display");
                {
                    this.enabledSpeedometer = builder.comment("If true, displays a speedometer on the HUD when driving a vehicle").translation(Reference.MOD_ID + ".config.client.display.speedometer").define("enabledSpeedometer", true);
                    this.autoPerspective = builder.comment("If true, automatically switches to third person when mounting vehicles").translation(Reference.MOD_ID + ".config.client.display.auto_perspective").define("autoPerspective", true);
                    this.forceFirstPersonOnExit = builder.comment("If enabled, camera perspective will always be forced back to first person when exiting a vehicle.").translation(Reference.MOD_ID + ".config.client.display.force_first_person_on_exit").define("forceFirstPersonOnExit", false);
                    this.workstationAnimation = builder.comment("If true, an animation is performed while cycling vehicles in the workstation").translation(Reference.MOD_ID + ".config.client.display.workstation_animation").define("workstationAnimation", true);
                    this.hoseSegments = builder.comment("The amount of segments to use to render the hose on a gas pump. The lower the value, the better the performance but renders a less realistically looking hose").translation(Reference.MOD_ID + ".config.client.display.hose_segments").defineInRange("hoseSegments", 10, 1, 100);

                    builder.comment("Configuration for camera related options").push("camera");
                    {
                        this.immersiveCamera = builder.comment("If true, uses an improved camera system when riding vehicles. Disabling this option will restore the default camera but it will break the experience of some vehicles. If you do disable this, clearly you don't care about the weeks MrCrayfish spent developing this awesome new camera system and want to hurt your eyes, you've been warned!").translation(Reference.MOD_ID + ".config.client.display.immersive_camera").define("immersiveCamera", true);
                        this.followVehicleOrientation = builder.comment("Makes the camera follow the vehicles traveling direction and rotations. This will be limited to yaw if immersiveCamera is disabled.").translation(Reference.MOD_ID + ".config.client.display.follow_vehicle_orientation").define("followVehicleOrientation", true);
                        this.useVehicleAsFocusPoint = builder.comment("In third person, uses the vehicle as the focus point rather than the players head. This only has an effect when immersiveCamera is enabled.").translation(Reference.MOD_ID + ".config.client.display.use_vehicle_as_focus_point").define("useVehicleAsFocusPoint", true);
                        this.shouldFollowPitch = builder.comment("Makes the camera follow vehicle rotations on the x-axis (pitch). This only has an effect when followVehicleOrientation is enabled.").translation(Reference.MOD_ID + ".config.client.display.should_follow_pitch").define("shouldFollowPitch", true);
                        this.shouldFollowYaw = builder.comment("Makes the camera follow vehicle rotations on the y-axis (yaw). This only has an effect when followVehicleOrientation is enabled.").translation(Reference.MOD_ID + ".config.client.display.should_follow_yaw").define("shouldFollowYaw", true);
                        this.shouldFollowRoll = builder.comment("Makes the camera follow vehicle rotations on the z-axis (roll). This only has an effect when followVehicleOrientation is enabled.").translation(Reference.MOD_ID + ".config.client.display.should_follow_roll").define("shouldFollowRoll", true);
                    }
                    builder.pop();
                }
                builder.pop();
            }
            builder.pop();
        }
    }

    public static class Server
    {
        public final ForgeConfigSpec.BooleanValue fuelEnabled;
        public final ForgeConfigSpec.BooleanValue vehicleDamage;
        public final ForgeConfigSpec.DoubleValue trailerDetachThreshold;
        public final ForgeConfigSpec.IntValue trailerSyncCooldown;
        public final ForgeConfigSpec.IntValue trailerInventorySyncCooldown;
        public final ForgeConfigSpec.BooleanValue pickUpVehicles;
        public final ForgeConfigSpec.DoubleValue maxHoseDistance;
        public final ForgeConfigSpec.IntValue pumpTransferAmount;
        public final ForgeConfigSpec.IntValue gasPumpCapacity;
        public final ForgeConfigSpec.IntValue pumpCapacity;
        public final ForgeConfigSpec.IntValue extractorCapacity;
        public final ForgeConfigSpec.IntValue extractorExtractTime;
        public final ForgeConfigSpec.IntValue mixerInputCapacity;
        public final ForgeConfigSpec.IntValue mixerOutputCapacity;
        public final ForgeConfigSpec.IntValue mixerMixTime;
        public final ForgeConfigSpec.IntValue fuelDrumCapacity;
        public final ForgeConfigSpec.IntValue industrialFuelDrumCapacity;
        public final ForgeConfigSpec.DoubleValue energyConsumptionFactor;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> disabledVehicles;
        public final ForgeConfigSpec.ConfigValue<List<? extends String>> validFuels;
        public final ForgeConfigSpec.IntValue jerryCanCapacity;
        public final ForgeConfigSpec.IntValue industrialJerryCanCapacity;
        public final ForgeConfigSpec.IntValue jerryCanFillRate;
        public final ForgeConfigSpec.IntValue sprayCanCapacity;
        public final ForgeConfigSpec.DoubleValue globalSpeedLimit;

        Server(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Server configuration settings").push("common");
            {
                builder.comment("General configuration options").push("general");
                this.fuelEnabled = builder.comment("If true, vehicles will require fuel for them to be driven.").translation(Reference.MOD_ID + ".config.server.fuel_enabled").define("fuelEnabled", true);
                this.vehicleDamage = builder.comment("If true, vehicles will take damage.").translation(Reference.MOD_ID + ".config.server.vehicle_damage").define("vehicleDamage", true);
                this.pickUpVehicles = builder.comment("Allows players to pick up vehicles by crouching and right clicking").translation(Reference.MOD_ID + ".config.server.pick_up_vehicles").define("pickUpVehicles", true);
                this.energyConsumptionFactor = builder.comment("Change the amount of fuel vehicles consumes by multiplying the consumption rate by this factor").translation(Reference.MOD_ID + ".config.server.fuel_consumption_modifier").defineInRange("fuelConsumptionModifier", 1.0, 0.0, Double.MAX_VALUE);
                this.disabledVehicles = builder.comment("A list of vehicles that are prevented from being crafted in the workstation").defineList("disabledVehicles", Collections.emptyList(), o -> true);
                this.validFuels = builder.comment("A list of fluids that can be used as fuel for vehicles").defineList("validFuels", Arrays.asList("vehicle:fuelium", "immersiveengineering:biodiesel", "immersivepetroleum:diesel"), o -> true);
                this.globalSpeedLimit = builder.comment("The maximum speed (in blocks per second) vehicles are allowed to travel. This will prevent vehicles travelling faster than the specified amount").defineInRange("globalSpeedLimit", 100F, 0F, 100F);
                builder.pop();

                builder.comment("Configuration options for trailers").push("trailer");
                this.trailerDetachThreshold = builder.comment("The distance threshold before the trailer detaches from a vehicle").translation(Reference.MOD_ID + ".config.server.trailer_detach_threshold").defineInRange("trailerDetachThreshold", 6.0, 1.0, 10.0);
                this.trailerSyncCooldown = builder.comment("The amount of ticks to wait before syncing data to clients about the trailer connection. This is important for smooth trailer movement on client side.").translation(Reference.MOD_ID + ".config.server.trailer_sync_cooldown").defineInRange("trailerSyncCooldown", 100, 1, Integer.MAX_VALUE);
                this.trailerInventorySyncCooldown = builder.comment("The amount of ticks to wait before syncing trailer inventory to tracking clients. If the value is set to 0 or less, the inventory will not sync and will save on network usage.").translation(Reference.MOD_ID + ".config.server.trailer_inventory_sync_cooldown").defineInRange("trailerInventorySyncCooldown", 20, 1, Integer.MAX_VALUE);
                builder.pop();

                builder.comment("Configuration options for blocks").push("blocks");
                {
                    builder.comment("Configuration options for Gas Pumps").push("gas_pump");
                    this.maxHoseDistance = builder.comment("The maximum distance before the hose from the gas pump or fluid hose breaks").translation(Reference.MOD_ID + ".config.server.max_hose_distance").defineInRange("maxHoseDistance", 10.0, 1.0, 100.0);
                    this.gasPumpCapacity = builder.comment("The fluid capacity of the gas pump in millibuckets").translation(Reference.MOD_ID + ".config.server.gas_pump_capacity").defineInRange("gasPumpCapacity", 50000, 1, Integer.MAX_VALUE);
                    builder.pop();

                    builder.comment("Configuration options for fluid pumps").push("fluid_pump");
                    this.pumpTransferAmount = builder.comment("The amount of fluid a pump will transfer each tick").translation(Reference.MOD_ID + ".config.server.pump_transfer_amount").defineInRange("pumpTransferAmount", 50, 1, Integer.MAX_VALUE);
                    this.pumpCapacity = builder.comment("The fluid capacity of the fluid pump in millibuckets").translation(Reference.MOD_ID + ".config.server.fluid_pump_capacity").defineInRange("pumpCapacity", 500, 1, Integer.MAX_VALUE);
                    builder.pop();

                    builder.comment("Configuration options for fluid extractors").push("fluid_extractor");
                    this.extractorExtractTime = builder.comment("The amount of ticks before fluid is extracted from an item").translation(Reference.MOD_ID + ".config.server.fluid_extractor_time").defineInRange("extractorExtractTime", 600, 1, Integer.MAX_VALUE);
                    this.extractorCapacity = builder.comment("The fluid capacity of the fluid extractor in millibuckets").translation(Reference.MOD_ID + ".config.server.fluid_extractor_capacity").defineInRange("extractorCapacity", 5000, 1, Integer.MAX_VALUE);
                    builder.pop();

                    builder.comment("Configuration options for fluid mixers").push("fluid_mixer");
                    this.mixerMixTime = builder.comment("The amount of ticks to mix fluids together").translation(Reference.MOD_ID + ".config.server.fluid_mixer_time").defineInRange("mixerMixTime", 100, 1, Integer.MAX_VALUE);
                    this.mixerInputCapacity = builder.comment("The input fluid capacity of the fluid mixer in millibuckets").translation(Reference.MOD_ID + ".config.server.fluid_mixer_input_capacity").defineInRange("mixerInputCapacity", 5000, 1, Integer.MAX_VALUE);
                    this.mixerOutputCapacity = builder.comment("The output fluid capacity of the fluid mixer in millibuckets").translation(Reference.MOD_ID + ".config.server.fluid_mixer_output_capacity").defineInRange("mixerOutputCapacity", 10000, 1, Integer.MAX_VALUE);
                    builder.pop();

                    builder.comment("Configuration options for fuel drums").push("fuel_drum");
                    this.fuelDrumCapacity = builder.comment("The fluid capacity of the fuel drum in millibuckets").translation(Reference.MOD_ID + ".config.server.fuel_drum_capacity").defineInRange("fuelDrumCapacity", 40000, 1, Integer.MAX_VALUE);
                    this.industrialFuelDrumCapacity = builder.comment("The fluid capacity of the industrial fuel drum in millibuckets").translation(Reference.MOD_ID + ".config.server.industrial_fuel_drum_capacity").defineInRange("industrialFuelDrumCapacity", 75000, 1, Integer.MAX_VALUE);
                    builder.pop();
                }
                builder.pop();

                builder.comment("Configuration options for items").push("items");
                {
                    builder.comment("Configuration options for jerry cans").push("jerry_can");
                    this.jerryCanCapacity = builder.comment("The fluid capacity of the jerry can in millibuckets").translation(Reference.MOD_ID + ".config.server.jerry_can_capacity").defineInRange("jerryCanCapacity", 5000, 1, Integer.MAX_VALUE);
                    this.industrialJerryCanCapacity = builder.comment("The fluid capacity of the industrial jerry can in millibuckets").translation(Reference.MOD_ID + ".config.server.industrial_jerry_can_capacity").defineInRange("industrialJerryCanCapacity", 15000, 1, Integer.MAX_VALUE);
                    this.jerryCanFillRate = builder.comment("The amount of fluid transferred when pouring or filling a jerry can").translation(Reference.MOD_ID + ".config.server.jerry_can_fill_rate").defineInRange("fillRate", 500, 1, Integer.MAX_VALUE);
                    builder.pop();

                    builder.comment("Configuration options for spray cans").push("spray_can");
                    this.sprayCanCapacity = builder.comment("The amount of sprays before a spray can becomes empty").translation(Reference.MOD_ID + ".config.server.spray_can_capacity").defineInRange("sprayCanCapacity", 20, 1, Integer.MAX_VALUE);
                    builder.pop();
                }
                builder.pop();
            }
            builder.pop();
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;

    static final ForgeConfigSpec serverSpec;
    public static final Server SERVER;

    static
    {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Server, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Server::new);
        serverSpec = commonSpecPair.getRight();
        SERVER = commonSpecPair.getLeft();
    }
}
