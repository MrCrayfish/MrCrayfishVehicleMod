package com.mrcrayfish.vehicle.init;

import com.mojang.brigadier.CommandDispatcher;
import com.mrcrayfish.vehicle.common.command.SetCosmeticCommand;
import net.minecraft.command.CommandSource;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.loading.FMLLoader;

/**
 * Author: MrCrayfish
 */
public class ModCommands
{
    @SubscribeEvent
    public void onServerStart(FMLServerAboutToStartEvent event)
    {
        if(FMLLoader.isProduction())
            return;
        CommandDispatcher<CommandSource> dispatcher = event.getServer().getCommands().getDispatcher();
        this.registerCommands(dispatcher, event.getServer().isDedicatedServer());
    }

    private void registerCommands(CommandDispatcher<CommandSource> dispatcher, boolean dedicated)
    {
        if(!dedicated)
        {
            SetCosmeticCommand.register(dispatcher);
        }
    }
}
