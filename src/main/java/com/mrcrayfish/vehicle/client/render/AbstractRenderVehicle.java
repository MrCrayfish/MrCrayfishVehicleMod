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
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;

    public abstract void render(T entity, float partialTicks);

    public void applyPlayerModel(T entity, EntityPlayer player, ModelPlayer model, float partialTicks) {};

    public void applyPlayerRender(T entity, EntityPlayer player, float partialTicks) {};

    protected void setEnginePosition(double x, double y, double z, double rotation, double scale)
    {
        this.enginePosition = new PartPosition(x, y, z, 0, rotation, 0, scale);
    }

    public void setFuelPortPosition(PartPosition fuelPortPosition)
    {
        this.fuelPortPosition = fuelPortPosition;
    }

    protected void setFuelPortPosition(double x, double y, double z, double rotation)
    {
        this.setFuelPortPosition(x, y, z, 0, rotation, 0, 0.25);
    }

    protected void setFuelPortPosition(double x, double y, double z, double rotX, double rotY, double rotZ, double scale)
    {
        this.fuelPortPosition = new PartPosition(x, y, z, rotX, rotY, rotZ, scale);
        this.fuelPortLidPosition = new PartPosition(x, y, z, rotX, rotY - 110, rotZ, scale);
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