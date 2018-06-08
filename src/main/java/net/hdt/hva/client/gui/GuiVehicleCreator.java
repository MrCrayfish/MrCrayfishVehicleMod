package net.hdt.hva.client.gui;

import com.mrcrayfish.vehicle.Reference;
import net.hdt.hva.container.ContainerVehicleCreator;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiVehicleCreator extends GuiContainer {

    private static ResourceLocation texture_normal = new ResourceLocation(Reference.MOD_ID,
            "textures/gui/container/vehicle_creator.png");

    public GuiVehicleCreator(IInventory playerInventory) {
        super(new ContainerVehicleCreator(playerInventory));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(texture_normal);
        int i = this.guiLeft;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

}