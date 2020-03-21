package com.mrcrayfish.vehicle;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Author: MrCrayfish
 */
@Config(modid = Reference.MOD_ID)
@Config.LangKey(Reference.MOD_ID + ".config.title")
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class VehicleConfig
{
    @Config.Name("Client")
    @Config.Comment("Client-only configs")
    @Config.LangKey(Reference.MOD_ID + ".config.client")
    public static final Client CLIENT = new Client();

    @Config.Name("Server")
    @Config.Comment("Server-only configs")
    @Config.LangKey(Reference.MOD_ID + ".config.server")
    public static final Server SERVER = new Server();

    public static class Server
    {
        @Config.Name("Fuel Enabled")
        @Config.Comment("If true, vehicles will require fuel for them to be driven.")
        @Config.LangKey(Reference.MOD_ID + ".config.server.fuel_enabled")
        public boolean fuelEnabled = true;

        @Config.Name("Vehicle Damage")
        @Config.Comment("If true, vehicles will take damage.")
        @Config.LangKey(Reference.MOD_ID + ".config.server.vehicle_damage")
        public boolean vehicleDamage = true;

        @Config.Name("Trailer Detach Distance")
        @Config.Comment("The distance threshold before the trailer detaches from a vehicle")
        @Config.LangKey(Reference.MOD_ID + ".config.server.trailer_detach_threshold")
        public double trailerDetachThreshold = 6.0;

        @Config.Name("Trailer Sync Cooldown")
        @Config.Comment("The amount of ticks to wait before syncing data to clients about the trailer connection. This is important for smooth trailer movement on client side.")
        @Config.LangKey(Reference.MOD_ID + ".config.server.trailer_sync_cooldown")
        public int trailerSyncCooldown = 100;

        @Config.Name("Trailer Inventory Sync Cooldown")
        @Config.Comment("The amount of ticks to wait before syncing trailer inventory to tracking clients. If the value is set to 0 or less, the inventory will not sync and will save on network usage.")
        @Config.LangKey(Reference.MOD_ID + ".config.server.trailer_inventory_sync_cooldown")
        public int trailerInventorySyncCooldown = 20;

        @Config.Name("Pickup Vehicles")
        @Config.Comment("Allows players to pick up vehicles by crouching and right clicking")
        @Config.LangKey(Reference.MOD_ID + ".config.server.pick_up_vehicles")
        public boolean pickUpVehicles = true;

        @Config.Name("Max Hose Distance")
        @Config.Comment("The maximum distance before the hose from the gas pump or fluid hose breaks")
        @Config.LangKey(Reference.MOD_ID + ".config.server.max_hose_distance")
        public double maxHoseDistance = 6.0;

        @Config.Name("Pipe Transfer Amount")
        @Config.Comment("The amount of fluid a pipe will transfer each tick")
        @Config.LangKey(Reference.MOD_ID + ".config.server.pipe_transfer_amount")
        @Config.RangeInt(min = 1)
        public int pipeTransferAmount = 50;

        @Config.Name("Pump Transfer Amount")
        @Config.Comment("The amount of fluid a pump will transfer each tick")
        @Config.LangKey(Reference.MOD_ID + ".config.server.pump_transfer_amount")
        @Config.RangeInt(min = 1)
        public int pumpTransferAmount = 50;

        @Config.Name("Fuel Consumption Factor")
        @Config.Comment("Change the amount of fuel vehicles consumes by multiplying the consumption rate by this factor")
        @Config.LangKey(Reference.MOD_ID + ".config.erver.fuel_consumption_modifier")
        @Config.RangeDouble(min = 0.0)
        public double fuelConsumptionFactor = 1.0;
    }

    public static class Client
    {
        @Config.Name("Debug")
        @Config.Comment("Configuration options for debugging vehicles")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug")
        public Debug debug = new Debug();

        @Config.Name("Interaction")
        @Config.Comment("Configuration options for vehicle interaction")
        @Config.LangKey(Reference.MOD_ID + ".config.client.interaction")
        public Interaction interaction = new Interaction();

        @Config.Name("Display")
        @Config.Comment("Configuration for display related options")
        @Config.LangKey(Reference.MOD_ID + ".config.client.display")
        public Display display = new Display();

        @Config.Name("Controller")
        @Config.Comment("Configuration options for controller support (Must have Controllable install)")
        @Config.LangKey(Reference.MOD_ID + ".config.client.controller")
        public Controller controller = new Controller();
    }

    public static class Interaction
    {
        @Config.Name("Left-Click Enabled")
        @Config.Comment("If true, raytraces will be performed on nearby vehicles when left-clicking the mouse, rather than just right-clicking it. "
                + "This allows one to be damaged/broken when clicking anywhere on it, rather than just on its bounding box.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.interaction.left_click")
        public boolean enabledLeftClick = true;
    }

    public static class Display
    {
        @Config.Name("Show Speedometer")
        @Config.Comment("If true, displays a speedometer on the HUD when driving a vehicle")
        @Config.LangKey(Reference.MOD_ID + ".config.client.display.speedometer")
        public boolean enabledSpeedometer = true;

        @Config.Name("Auto Perspective")
        @Config.Comment("If true, automatically switches to third person when mounting vehicles")
        @Config.LangKey(Reference.MOD_ID + ".config.client.display.auto_perspective")
        public boolean autoPerspective = true;

        @Config.Name("Workstation Animation")
        @Config.Comment("If true, an animation is performed while cycling vehicles in the workstation")
        @Config.LangKey(Reference.MOD_ID + ".config.client.display.workstation_animation")
        public boolean workstationAnimation = true;

        @Config.Name("Hose Segments")
        @Config.Comment("The amount of segments to use to render the hose on a gas pump. The lower the value, the better the performance but renders a less realistically looking hose")
        @Config.LangKey(Reference.MOD_ID + ".config.client.display.hose_segments")
        @Config.RangeInt(min = 1, max = 100)
        public int hoseSegments = 10;
    }

    public static class Controller
    {
        @Config.Name("Use Triggers")
        @Config.Comment("If true, will use the triggers on controller to control the acceleration of the vehicle.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.controller.use_triggers")
        public boolean useTriggers = false;
    }

    public static class Debug
    {
        @Config.Name("Render Vehicle Outlines")
        @Config.Comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug.render_outlines")
        public boolean renderOutlines = false;

        @Config.Name("Reload Raytracer Each Tick")
        @Config.Comment("If true, the raytracer will be reloaded each tick.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug.raytracer.continuous_reload")
        public boolean reloadRaytracerEachTick = false;

        @Config.Name("Reload Vehicle Properties Each Tick")
        @Config.Comment("If true, the vehicle properties will be reloaded each tick.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug.raytracer.vehicle_properties_reload")
        public boolean reloadVehiclePropertiesEachTick = false;
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event)
    {
        if(event.getModID().equalsIgnoreCase(Reference.MOD_ID))
        {
            ConfigManager.sync(Reference.MOD_ID, Config.Type.INSTANCE);
        }
    }
}
