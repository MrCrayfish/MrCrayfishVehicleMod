package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.client.render.Wheel;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class VehicleProperties
{
    public static final Map<Class<? extends EntityVehicle>, VehicleProperties> PROPERTIES_MAP = new HashMap<>();

    public static void setProperties(Class<? extends EntityVehicle> clazz, VehicleProperties properties)
    {
        if(!PROPERTIES_MAP.containsKey(clazz))
        {
            PROPERTIES_MAP.put(clazz, properties);
        }
    }

    public static VehicleProperties getProperties(Class<? extends EntityVehicle> clazz)
    {
        return PROPERTIES_MAP.get(clazz);
    }

    private float axleOffset;
    private float wheelOffset;
    private Vec3d heldOffset = Vec3d.ZERO;
    private Vec3d towBarVec = Vec3d.ZERO;
    private Vec3d trailerOffset = Vec3d.ZERO;
    private List<Wheel> wheels;
    private PartPosition bodyPosition = PartPosition.DEFAULT;
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;
    private PartPosition keyPortPosition;
    private PartPosition keyPosition;

    public void setAxleOffset(float axleOffset)
    {
        this.axleOffset = axleOffset;
    }

    public float getAxleOffset()
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

    public void setHeldOffset(Vec3d heldOffset)
    {
        this.heldOffset = heldOffset;
    }

    public Vec3d getHeldOffset()
    {
        return heldOffset;
    }

    public void setTowBarPosition(Vec3d towBarVec)
    {
        this.towBarVec = towBarVec;
    }

    public Vec3d getTowBarPosition()
    {
        return towBarVec;
    }

    public void setTrailerOffset(Vec3d trailerOffset)
    {
        this.trailerOffset = trailerOffset;
    }

    public Vec3d getTrailerOffset()
    {
        return trailerOffset;
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ)
    {
        if(wheels == null)
        {
            wheels = new ArrayList<>();
        }
        wheels.add(new Wheel(side, position, 2.0F, 1.0F, offsetX, 0F, offsetZ));
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale)
    {
        if(wheels == null)
        {
            wheels = new ArrayList<>();
        }
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, 0F, offsetZ));
    }

    public void addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale)
    {
        if(wheels == null)
        {
            wheels = new ArrayList<>();
        }
        wheels.add(new Wheel(side, position, 2.0F, scale, offsetX, offsetY, offsetZ));
    }

    public List<Wheel> getWheels()
    {
        return wheels;
    }

    public void setBodyPosition(PartPosition bodyPosition)
    {
        this.bodyPosition = bodyPosition;
    }

    public PartPosition getBodyPosition()
    {
        return bodyPosition;
    }

    public void setEnginePosition(PartPosition enginePosition)
    {
        this.enginePosition = enginePosition;
    }

    public PartPosition getEnginePosition()
    {
        return enginePosition;
    }

    public void setFuelPortPosition(PartPosition fuelPortPosition)
    {
        this.fuelPortPosition = fuelPortPosition;
        this.fuelPortLidPosition = new PartPosition(fuelPortPosition.getX(),
                fuelPortPosition.getY(),
                fuelPortPosition.getZ(),
                fuelPortPosition.getRotX(),
                fuelPortPosition.getRotY() - 110,
                fuelPortPosition.getRotZ(),
                fuelPortPosition.getScale());
    }

    public PartPosition getFuelPortPosition()
    {
        return fuelPortPosition;
    }

    public void setFuelPortLidPosition(PartPosition fuelPortLidPosition)
    {
        this.fuelPortLidPosition = fuelPortLidPosition;
    }

    public PartPosition getFuelPortLidPosition()
    {
        return fuelPortLidPosition;
    }

    public void setKeyPortPosition(PartPosition keyPortPosition)
    {
        this.keyPortPosition = keyPortPosition;
        this.keyPosition = new PartPosition(keyPortPosition.getX(), keyPortPosition.getY(), keyPortPosition.getZ(), keyPortPosition.getRotX() + 90, 0, 0, 0.15);
    }

    public PartPosition getKeyPortPosition()
    {
        return keyPortPosition;
    }

    public void setKeyPosition(PartPosition keyPosition)
    {
        this.keyPosition = keyPosition;
    }

    public PartPosition getKeyPosition()
    {
        return keyPosition;
    }
}
