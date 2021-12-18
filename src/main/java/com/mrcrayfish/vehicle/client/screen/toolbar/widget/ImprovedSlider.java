package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.lwjgl.glfw.GLFW;

/**
 * A class that fixes a bug with the Forge slider. The slider won't stop sliding if the mouse
 * was released outside of the sliders area.
 *
 * Author: MrCrayfish
 */
public class ImprovedSlider extends Slider
{
    private ISlider handler;

    public ImprovedSlider(int xPos, int yPos, int width, int height, String prefix, String suf, double minVal, double maxVal, double currentVal, boolean showDec, boolean drawStr, ISlider handler)
    {
        super(xPos, yPos, width, height, new StringTextComponent(prefix), new StringTextComponent(suf), minVal, maxVal, currentVal, showDec, drawStr, b -> {});
        this.handler = handler;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
        {
            this.onRelease(mouseX, mouseY);
            this.dragging = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void updateSlider()
    {
        super.updateSlider();
        this.handler.onChangeSliderValue(this);

        /* Fixes the slider not being released when mouse is released outside of slider area */
        Minecraft mc = Minecraft.getInstance();
        if(this.dragging && GLFW.glfwGetMouseButton(mc.getWindow().getWindow(), GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_RELEASE)
        {
            double mouseX = mc.mouseHandler.xpos() * (double) mc.getWindow().getGuiScaledWidth() / (double) mc.getWindow().getWidth();
            double mouseY = mc.mouseHandler.ypos() * (double) mc.getWindow().getGuiScaledHeight() / (double) mc.getWindow().getHeight();
            this.onRelease(mouseX, mouseY);
            this.dragging = false;
        }
    }
}
