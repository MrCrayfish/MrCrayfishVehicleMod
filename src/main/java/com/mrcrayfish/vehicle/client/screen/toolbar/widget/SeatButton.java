package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.screen.DashboardScreen;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSetSeat;
import net.minecraft.util.text.TranslationTextComponent;

import java.lang.ref.WeakReference;

/**
 * Author: MrCrayfish
 */
public class SeatButton extends IconButton
{
    private final WeakReference<VehicleEntity> vehicleRef;
    private final int index;

    public SeatButton(VehicleEntity entity, int index, boolean driver)
    {
        super(20, 20, driver ? DashboardScreen.Icons.SEAT_DRIVER : DashboardScreen.Icons.SEAT_PASSENGER, new TranslationTextComponent(driver ? "vehicle.toolbar.label.driver_seat" : "vehicle.toolbar.label.passenger_seat"), onPress -> {
            PacketHandler.getPlayChannel().sendToServer(new MessageSetSeat(index));
        });
        this.vehicleRef = new WeakReference<>(entity);
        this.index = index;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        VehicleEntity entity = this.vehicleRef.get();;
        this.active = entity != null && entity.getSeatTracker().isSeatAvailable(this.index);
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
    }
}
