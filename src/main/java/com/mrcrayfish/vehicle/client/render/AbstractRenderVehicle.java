package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractRenderVehicle<T extends EntityVehicle>
{
    private float axleOffset;
    private float wheelOffset;
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;

    public abstract void render(T entity, float partialTicks);

    public void applyPlayerModel(T entity, EntityPlayer player, ModelPlayer model, float partialTicks) {};

    public void applyPlayerRender(T entity, EntityPlayer player) {};

    public void setAxleOffset(float axelOffset)
    {
        this.axleOffset = axelOffset;
    }

    public float getAxelOffset()
    {
        return axleOffset;
    }

    public void setWheelOffset(float wheelOffset)
    {
        this.wheelOffset = wheelOffset;
    }

    public float getWheelOffset()
    {
        return wheelOffset;
    }

    protected void setEnginePosition(float x, float y, float z, float rotation, float scale)
    {
        this.enginePosition = new PartPosition(x, y, z, 0.0F, rotation, 0.0F, scale);
    }

    protected void setFuelPortPosition(float x, float y, float z, float rotation)
    {
        this.setFuelPortPosition(x, y, z, 0.0F, rotation, 0.0F, 0.25F);
    }

    protected void setFuelPortPosition(float x, float y, float z, float rotX, float rotY, float rotZ, float scale)
    {
        this.fuelPortPosition = new PartPosition(x, y, z, rotX, rotY, rotZ, scale);
        this.fuelPortLidPosition = new PartPosition(x, y, z, rotX, rotY - 110.0F, rotZ, scale);
    }

    protected boolean shouldRenderFuelLid()
    {
        return true;
    }

    @Nullable
    public PartPosition getEnginePosition()
    {
        return enginePosition;
    }

    @Nullable
    public PartPosition getFuelPortPosition()
    {
        return fuelPortPosition;
    }

    @Nullable
    public PartPosition getFuelPortLidPosition()
    {
        return fuelPortLidPosition;
    }
}