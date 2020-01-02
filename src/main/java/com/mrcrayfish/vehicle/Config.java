package com.mrcrayfish.vehicle;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Author: MrCrayfish
 */
public class Config
{
    public static class Client
    {
        public final ForgeConfigSpec.BooleanValue renderOutlines;
        public final ForgeConfigSpec.BooleanValue reloadRayTracerEachTick;
        public final ForgeConfigSpec.BooleanValue enabledLeftClick;
        public final ForgeConfigSpec.BooleanValue enabledSpeedometer;
        public final ForgeConfigSpec.BooleanValue autoPerspective;
        public final ForgeConfigSpec.BooleanValue workstationAnimation;
        public final ForgeConfigSpec.BooleanValue useTriggers;

        Client(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Client configuration settings").push("client");
            builder.comment("Configuration options for debugging vehicles").push("debug");
            this.renderOutlines = builder.comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.")
                .translation(Reference.MOD_ID + ".config.client.debug.render_outlines")
                .define("renderOutlines", false);
            this.reloadRayTracerEachTick = builder.comment("If true, the raytracer will be reloaded each tick.")
                .translation(Reference.MOD_ID + ".config.client.debug.raytracer.continuous_reload")
                .define("reloadRaytracerEachTick", false);
            builder.pop();
            builder.comment("Configuration options for vehicle interaction").push("interaction");
            this.enabledLeftClick = builder.comment("If true, raytraces will be performed on nearby vehicles when left-clicking the mouse, rather than just right-clicking it. This allows one to be damaged/broken when clicking anywhere on it, rather than just on its bounding box.")
                .translation(Reference.MOD_ID + ".config.client.interaction.left_click")
                .define("enabledLeftClick", true);
            builder.pop();
            builder.comment("Configuration for display related options").push("display");
            this.enabledSpeedometer = builder.comment("If true, displays a speedometer on the HUD when driving a vehicle")
                .translation(Reference.MOD_ID + ".config.client.display.speedometer")
                .define("enabledSpeedometer", true);
            this.autoPerspective = builder.comment("If true, automatically switches to third person when mounting vehicles")
                .translation(Reference.MOD_ID + ".config.client.display.auto_perspective")
                .define("autoPerspective", true);
            this.workstationAnimation = builder.comment("If true, an animation is performed while cycling vehicles in the workstation")
                .translation(Reference.MOD_ID + ".config.client.display.workstation_animation")
                .define("workstationAnimation", true);
            builder.pop();
            builder.comment("Configuration options for controller support (Must have Controllable install)").push("controller");
            this.useTriggers = builder.comment("If true, will use the triggers on controller to control the acceleration of the vehicle.")
                    .translation(Reference.MOD_ID + ".config.client.controller.use_triggers")
                    .define("useTriggers", false);
            builder.pop();
            builder.pop();
        }
    }

    public static class Common
    {
        public final ForgeConfigSpec.BooleanValue fuelEnabled;
        public final ForgeConfigSpec.BooleanValue vehicleDamage;
        public final ForgeConfigSpec.DoubleValue trailerDetachThreshold;
        public final ForgeConfigSpec.IntValue trailerSyncCooldown;
        public final ForgeConfigSpec.IntValue trailerInventorySyncCooldown;
        public final ForgeConfigSpec.BooleanValue pickUpVehicles;
        public final ForgeConfigSpec.DoubleValue maxHoseDistance;

        Common(ForgeConfigSpec.Builder builder)
        {
            builder.comment("Common configuration settings").push("common");
            this.fuelEnabled = builder.comment("If true, vehicles will require fuel for them to be driven.")
                .translation(Reference.MOD_ID + ".config.server.fuel_enabled")
                .define("fuelEnabled", true);
            this.vehicleDamage = builder.comment("If true, vehicles will take damage.")
                .translation(Reference.MOD_ID + ".config.server.vehicle_damage")
                .define("vehicleDamage", true);
            this.trailerDetachThreshold = builder.comment("The distance threshold before the trailer detaches from a vehicle")
                .translation(Reference.MOD_ID + ".config.server.trailer_detach_threshold")
                .defineInRange("trailerDetachThreshold", 6.0, 1.0, 10.0);
            this.trailerSyncCooldown = builder.comment("The amount of ticks to wait before syncing data to clients about the trailer connection. This is important for smooth trailer movement on client side.")
                .translation(Reference.MOD_ID + ".config.server.trailer_sync_cooldown")
                .defineInRange("trailerSyncCooldown", 100, 1, Integer.MAX_VALUE);
            this.trailerInventorySyncCooldown = builder.comment("The amount of ticks to wait before syncing trailer inventory to tracking clients. If the value is set to 0 or less, the inventory will not sync and will save on network usage.")
                .translation(Reference.MOD_ID + ".config.server.trailer_inventory_sync_cooldown")
                .defineInRange("trailerInventorySyncCooldown", 20, 1, Integer.MAX_VALUE);
            this.pickUpVehicles = builder.comment("Allows players to pick up vehicles by crouching and right clicking")
                .translation(Reference.MOD_ID + ".config.server.pick_up_vehicles")
                .define("pickUpVehicles", true);
            this.maxHoseDistance = builder.comment("The maximum distance before the hose from the gas pump or fluid hose breaks")
                .translation(Reference.MOD_ID + ".config.server.max_hose_distance")
                .defineInRange("maxHoseDistance", 6.0, 1.0, 20.0);
            builder.pop();
        }
    }

    static final ForgeConfigSpec clientSpec;
    public static final Config.Client CLIENT;

    static final ForgeConfigSpec commonSpec;
    public static final Config.Common COMMON;

    static
    {
        final Pair<Client, ForgeConfigSpec> clientSpecPair = new ForgeConfigSpec.Builder().configure(Config.Client::new);
        clientSpec = clientSpecPair.getRight();
        CLIENT = clientSpecPair.getLeft();

        final Pair<Common, ForgeConfigSpec> commonSpecPair = new ForgeConfigSpec.Builder().configure(Config.Common::new);
        commonSpec = commonSpecPair.getRight();
        COMMON = commonSpecPair.getLeft();
    }
}
