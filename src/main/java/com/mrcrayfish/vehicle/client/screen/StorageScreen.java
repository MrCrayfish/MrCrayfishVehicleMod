package com.mrcrayfish.vehicle.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.common.inventory.StorageInventory;
import com.mrcrayfish.vehicle.inventory.container.StorageContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Author: MrCrayfish
 */
@OnlyIn(Dist.CLIENT)
public class StorageScreen extends ContainerScreen<StorageContainer>
{
    private static final ResourceLocation CHEST_GUI_TEXTURE = new ResourceLocation("textures/gui/container/generic_54.png");
    private final PlayerInventory playerInventory;
    private final int inventoryRows;

    public StorageScreen(int windowId, PlayerInventory playerInventory, StorageInventory storageInventory)
    {
        super(new StorageContainer(windowId, playerInventory, storageInventory, playerInventory.player), playerInventory, storageInventory.getTitle());
        this.playerInventory = playerInventory;
        this.passEvents = false;
        this.inventoryRows = storageInventory.getSizeInventory() / 9;
        this.ySize = 114 + this.inventoryRows * 18;
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.minecraft.fontRenderer.drawString(this.getTitle().getFormattedText(), 8, 6, 4210752);
        this.minecraft.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.minecraft.getTextureManager().bindTexture(CHEST_GUI_TEXTURE);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.blit(i, j, 0, 0, this.xSize, this.inventoryRows * 18 + 17);
        this.blit(i, j + this.inventoryRows * 18 + 17, 0, 126, this.xSize, 96);
    }
}
