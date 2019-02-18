package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractRenderLandVehicle<T extends EntityPoweredVehicle> extends AbstractRenderVehicle<T>
{
    private final List<Wheel> wheels = new ArrayList<>();

    public List<Wheel> getWheels()
    {
        return wheels;
    }

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ)
    {
        wheels.add(new Wheel(side, position, 2.0F, 1.0F, offsetX, 0F, offsetZ));
    }

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, 0F, offsetZ));
    }

    protected void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale)
    {
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, offsetY, offsetZ));
    }
}