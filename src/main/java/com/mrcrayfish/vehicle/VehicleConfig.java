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
    }

    public static class Debug
    {
        @Config.Name("Render Vehicle Outlines")
        @Config.Comment("If true, renders an outline of all the elements on a vehicle's model. Useful for debugging interactions.")
        @Config.LangKey(Reference.MOD_ID + ".config.client.debug.render_outlines")
        public boolean renderOutlines;
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
