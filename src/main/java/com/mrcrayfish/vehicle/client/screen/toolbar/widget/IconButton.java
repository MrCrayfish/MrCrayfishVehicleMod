package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mrcrayfish.vehicle.client.screen.toolbar.IToolbarLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class IconButton extends Button implements IToolbarLabel
{
    private IconProvider icon;
    private ITextComponent label;

    public IconButton(int width, int height, @Nullable IconProvider icon, ITextComponent label, IPressable onPress)
    {
        super(0, 0, width, height, StringTextComponent.EMPTY, onPress);
        this.icon = icon;
        this.label = label;
    }

    public IconButton setLabel(ITextComponent label)
    {
        this.label = label;
        return this;
    }

    @Override
    public ITextComponent getLabel()
    {
        return this.label;
    }

    public IconButton setIcon(@Nullable IconProvider icon)
    {
        this.icon = icon;
        return this;
    }

    @Override
    public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        super.renderButton(matrixStack, mouseX, mouseY, partialTicks);
        Minecraft mc = Minecraft.getInstance();
        FontRenderer font = mc.font;
        int combinedWidth = this.icon != null ? 10 : 0;
        String message = this.getMessage().getContents().trim();
        if(!message.isEmpty())
        {
            combinedWidth += font.width(message);
            if(this.icon != null)
            {
                combinedWidth += 4;
            }
        }
        if(this.icon != null)
        {
            mc.getTextureManager().bind(this.icon.getTextureLocation());
            this.drawIcon(this.x + this.width / 2 - combinedWidth / 2, this.y + 5, this.icon.getU(), this.icon.getV());
        }
        if(!message.isEmpty())
        {
            font.drawShadow(matrixStack, message, this.x + this.width / 2 - combinedWidth / 2 + 10 + (this.icon == null ? 0 : 4), this.y + 6, 0xFFFFFF);
        }
    }

    protected void drawIcon(int x, int y, int u, int v)
    {
        int size = 10;
        float uScale = 1.0F / 100.0F;
        float vScale = 1.0F / 100.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.vertex(x, y + size, 0).uv(u * uScale, (v + size) * vScale).endVertex();
        buffer.vertex(x + size, y + size, 0).uv((u + size) * uScale, (v + size) * vScale).endVertex();
        buffer.vertex(x + size, y, 0).uv((u + size) * uScale, v * vScale).endVertex();
        buffer.vertex(x, y, 0).uv(u * uScale, v * vScale).endVertex();
        tessellator.end();
    }

    public interface IconProvider
    {
        ResourceLocation getTextureLocation();

        int getU();

        int getV();
    }
}
