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
    }

    public static class Debug
    {
        @Config.Name("Render Vehicle Outlines")
        @Config.Comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug.render_outlines")
        public boolean renderOutlines = false;
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
