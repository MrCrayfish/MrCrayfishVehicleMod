package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.screen.DashboardScreen;
import com.mrcrayfish.vehicle.common.cosmetic.CosmeticProperties;
import com.mrcrayfish.vehicle.common.cosmetic.actions.OpenableAction;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageInteractCosmetic;
import net.minecraft.util.text.StringTextComponent;

/**
 * Author: MrCrayfish
 */
public class DoorButton extends IconButton
{
    private final OpenableAction action;

    public DoorButton(VehicleEntity entity, CosmeticProperties properties, OpenableAction action)
    {
        super(20, 20, DashboardScreen.Icons.DOOR, StringTextComponent.EMPTY, onPress -> {
            PacketHandler.getPlayChannel().sendToServer(new MessageInteractCosmetic(entity.getId(), properties.getId()));
        });
        this.action = action;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        int backgroundColor = this.action.isOpen() ? 0xFFFFB64C : 0xFF941400;
        int foregroundColor = this.action.isOpen() ? 0xFFFFC54C : 0xFFBD2008;
        fill(matrixStack, this.x + 3, this.y + this.height - 6, this.x + this.width - 3, this.y + this.height - 3, backgroundColor);
        fill(matrixStack, this.x + 3, this.y + this.height - 6, this.x + this.width - 4, this.y + this.height - 4, foregroundColor);
    }

    @Override
    protected void drawIcon(int x, int y, int u, int v)
    {
        super.drawIcon(x, y - 2, u, v);
    }
}
