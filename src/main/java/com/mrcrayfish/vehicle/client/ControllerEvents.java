package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleConfig;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Controller;
import org.lwjgl.input.Controllers;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
public class ControllerEvents
{
    public static Controller controller = null;

    @SubscribeEvent
    public static void onRender(RenderWorldLastEvent event)
    {
        if(!VehicleConfig.CLIENT.experimental.controllerSupport || controller == null)
            return;
        controller.poll();
    }
}
