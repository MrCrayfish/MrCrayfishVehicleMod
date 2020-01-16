package com.mrcrayfish.vehicle.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.client.render.RenderVehicleWrapper;
import com.mrcrayfish.vehicle.client.render.VehicleRenderRegistry;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EngineType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.inventory.container.EditVehicleContainer;
import com.mrcrayfish.vehicle.util.CommonUtils;
import com.mrcrayfish.vehicle.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;

/**
 * Author: MrCrayfish
 */
public class EditVehicleScreen extends ContainerScreen<EditVehicleContainer>
{
    private static final ResourceLocation GUI_TEXTURES = new ResourceLocation("vehicle:textures/gui/edit_vehicle.png");

    private final PlayerInventory playerInventory;
    private final IInventory vehicleInventory;
    private final PoweredVehicleEntity vehicle;

    private boolean showHelp = true;
    private int windowZoom = 10;
    private int windowX, windowY;
    private float windowRotationX, windowRotationY;
    private boolean mouseGrabbed;
    private int mouseGrabbedButton;
    private int mouseClickedX, mouseClickedY;

    public EditVehicleScreen(EditVehicleContainer container, PlayerInventory playerInventory, ITextComponent title)
    {
        super(container, playerInventory, title);
        this.playerInventory = playerInventory;
        this.vehicleInventory = container.getVehicleInventory();
        this.vehicle = container.getVehicle();
        this.ySize = 184;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.getTextureManager().bindTexture(GUI_TEXTURES);
        int left = (this.width - this.xSize) / 2;
        int top = (this.height - this.ySize) / 2;
        this.blit(left, top, 0, 0, this.xSize, this.ySize);

        if(this.vehicle.getEngineType() != EngineType.NONE)
        {
            if(this.vehicleInventory.getStackInSlot(0).isEmpty())
            {
                this.blit(left + 8, top + 17, 176, 0, 16, 16);
            }
        }
        else if(this.vehicleInventory.getStackInSlot(0).isEmpty())
        {
            this.blit(left + 8, top + 17, 176, 32, 16, 16);
        }

        if(this.vehicle.canChangeWheels())
        {
            if(this.vehicleInventory.getStackInSlot(1).isEmpty())
            {
                this.blit(left + 8, top + 35, 176, 16, 16, 16);
            }
        }
        else if(this.vehicleInventory.getStackInSlot(1).isEmpty())
        {
            this.blit(left + 8, top + 35, 176, 32, 16, 16);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        Minecraft minecraft = Minecraft.getInstance();
        minecraft.fontRenderer.drawString(this.title.getFormattedText(), 8, 6, 4210752);
        minecraft.fontRenderer.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8, this.ySize - 96 + 2, 4210752);

        RenderVehicleWrapper wrapper = VehicleRenderRegistry.getRenderWrapper((EntityType<? extends VehicleEntity>) vehicle.getType());
        if(wrapper != null)
        {
            int startX = (this.width - this.xSize) / 2;
            int startY = (this.height - this.ySize) / 2;

            RenderSystem.pushMatrix();
            RenderSystem.translatef(96, 78, 1050.0F);
            RenderSystem.scalef(-1.0F, -1.0F, -1.0F);

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            RenderUtil.scissor(startX + 26, startY + 17, 142, 70);

            MatrixStack matrixStack = new MatrixStack();
            matrixStack.func_227861_a_(0.0, 0.0, 1000.0);
            matrixStack.func_227861_a_(windowX - (mouseGrabbed && mouseGrabbedButton == 0 ? mouseX - mouseClickedX : 0), 0, 0);
            matrixStack.func_227861_a_(0, windowY - (mouseGrabbed && mouseGrabbedButton == 0 ? mouseY - mouseClickedY : 0), 0);

            Quaternion quaternion = Axis.POSITIVE_X.func_229187_a_(-10F);
            quaternion.multiply(Axis.POSITIVE_X.func_229187_a_(windowRotationY - (mouseGrabbed && mouseGrabbedButton == 1 ? mouseY - mouseClickedY : 0)));
            quaternion.multiply(Axis.POSITIVE_Y.func_229187_a_(windowRotationX + (mouseGrabbed && mouseGrabbedButton == 1 ? mouseX - mouseClickedX : 0)));
            quaternion.multiply(Axis.POSITIVE_Y.func_229187_a_(135F));
            matrixStack.func_227863_a_(quaternion);

            matrixStack.func_227862_a_(windowZoom / 10F, windowZoom / 10F, windowZoom / 10F);
            matrixStack.func_227862_a_(22F, 22F, 22F);

            VehicleProperties properties = VehicleProperties.getProperties(vehicle.getType());
            PartPosition position = PartPosition.DEFAULT;
            if(properties != null)
            {
                position = properties.getDisplayPosition();
            }
            matrixStack.func_227862_a_((float) position.getScale(), (float) position.getScale(), (float) position.getScale());
            matrixStack.func_227863_a_(Axis.POSITIVE_X.func_229187_a_((float) position.getRotX()));
            matrixStack.func_227863_a_(Axis.POSITIVE_Y.func_229187_a_((float) position.getRotY()));
            matrixStack.func_227863_a_(Axis.POSITIVE_Z.func_229187_a_((float) position.getRotZ()));
            matrixStack.func_227861_a_(position.getX(), position.getY(), position.getZ());

            EntityRendererManager renderManager = Minecraft.getInstance().getRenderManager();
            renderManager.setRenderShadow(false);
            renderManager.func_229089_a_(quaternion);
            IRenderTypeBuffer.Impl renderTypeBuffer = Minecraft.getInstance().func_228019_au_().func_228487_b_();
            wrapper.render(vehicle, matrixStack, renderTypeBuffer, Minecraft.getInstance().getRenderPartialTicks(), 15728880);
            renderTypeBuffer.func_228461_a_();
            renderManager.setRenderShadow(true);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            RenderSystem.popMatrix();
        }

        if(showHelp)
        {
            RenderSystem.pushMatrix();
            RenderSystem.scalef(0.5F, 0.5F, 0.5F);
            minecraft.fontRenderer.drawString(I18n.format("container.edit_vehicle.window_help"), 56, 38, Color.WHITE.getRGB());
            RenderSystem.popMatrix();
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll)
    {
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;
        if(CommonUtils.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 142, 70))
        {
            if(scroll < 0 && this.windowZoom > 0)
            {
                this.showHelp = false;
                this.windowZoom--;
            }
            else if(scroll > 0)
            {
                this.showHelp = false;
                this.windowZoom++;
            }
        }
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(CommonUtils.isMouseWithin((int) mouseX, (int) mouseY, startX + 26, startY + 17, 142, 70))
        {
            if(!this.mouseGrabbed && (button == GLFW.GLFW_MOUSE_BUTTON_LEFT || button == GLFW.GLFW_MOUSE_BUTTON_RIGHT))
            {
                this.mouseGrabbed = true;
                this.mouseGrabbedButton = button == GLFW.GLFW_MOUSE_BUTTON_RIGHT ? 1 : 0;
                this.mouseClickedX = (int) mouseX;
                this.mouseClickedY = (int) mouseY;
                this.showHelp = false;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        if(this.mouseGrabbed)
        {
            if(this.mouseGrabbedButton == 0 && button == GLFW.GLFW_MOUSE_BUTTON_LEFT)
            {
                this.mouseGrabbed = false;
                this.windowX -= (mouseX - this.mouseClickedX);
                this.windowY -= (mouseY - this.mouseClickedY);
            }
            else if(mouseGrabbedButton == 1 && button == GLFW.GLFW_MOUSE_BUTTON_RIGHT)
            {
                this.mouseGrabbed = false;
                this.windowRotationX += (mouseX - this.mouseClickedX);
                this.windowRotationY -= (mouseY - this.mouseClickedY);
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks)
    {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);

        int startX = (this.width - this.xSize) / 2;
        int startY = (this.height - this.ySize) / 2;

        if(this.vehicleInventory.getStackInSlot(0).isEmpty())
        {
            if(CommonUtils.isMouseWithin(mouseX, mouseY, startX + 7, startY + 16, 18, 18))
            {
                if(vehicle.getEngineType() != EngineType.NONE)
                {
                    this.renderTooltip(Collections.singletonList("Engine"), mouseX, mouseY, this.minecraft.fontRenderer);
                }
                else
                {
                    this.renderTooltip(Arrays.asList("Engine", TextFormatting.GRAY + "Not applicable"), mouseX, mouseY, this.minecraft.fontRenderer);
                }
            }
        }

        if(this.vehicleInventory.getStackInSlot(1).isEmpty())
        {
            if(CommonUtils.isMouseWithin(mouseX, mouseY, startX + 7, startY + 34, 18, 18))
            {
                if(vehicle.canChangeWheels())
                {
                    this.renderTooltip(Collections.singletonList("Wheels"), mouseX, mouseY, this.minecraft.fontRenderer);
                }
                else
                {
                    this.renderTooltip(Arrays.asList("Wheels", TextFormatting.GRAY + "Not applicable"), mouseX, mouseY, this.minecraft.fontRenderer);
                }
            }
        }
    }
}
