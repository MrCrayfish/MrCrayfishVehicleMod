package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import org.lwjgl.glfw.GLFW;

/**
 * Author: MrCrayfish
 */
public class KeyBinds
{
    public static final IKeyConflictContext RIDING_VEHICLE = new IKeyConflictContext()
    {
        @Override
        public boolean isActive()
        {
            PlayerEntity player = Minecraft.getInstance().player;
            if(player != null && player.getVehicle() instanceof VehicleEntity)
            {
                return KeyConflictContext.IN_GAME.isActive();
            }
            return false;
        }

        @Override
        public boolean conflicts(IKeyConflictContext other)
        {
            return other == this;
        }
    };

    public static final KeyBinding KEY_HORN = new KeyBinding("key.vehicle.horn", GLFW.GLFW_KEY_H, "key.categories.vehicle");
    public static final KeyBinding KEY_CYCLE_SEATS = new KeyBinding("key.vehicle.cycle_seats", GLFW.GLFW_KEY_C, "key.categories.vehicle");
    public static final KeyBinding KEY_HITCH_TRAILER = new KeyBinding("key.vehicle.hitch_trailer", GLFW.GLFW_KEY_LEFT_CONTROL, "key.categories.vehicle");

    static
    {
        KEY_HORN.setKeyConflictContext(RIDING_VEHICLE);
        KEY_CYCLE_SEATS.setKeyConflictContext(RIDING_VEHICLE);
        KEY_HITCH_TRAILER.setKeyConflictContext(RIDING_VEHICLE);
    }

    public static void register()
    {
        ClientRegistry.registerKeyBinding(KEY_HORN);
        ClientRegistry.registerKeyBinding(KEY_CYCLE_SEATS);
        ClientRegistry.registerKeyBinding(KEY_HITCH_TRAILER);
    }
}
