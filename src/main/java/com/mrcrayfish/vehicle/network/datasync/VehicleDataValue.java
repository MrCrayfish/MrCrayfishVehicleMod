package com.mrcrayfish.vehicle.network.datasync;

import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.datasync.DataParameter;

/**
 * A wrapper class for data parameters that are registered on vehicles. The returned value depends
 * if the local player is currently the controlling passenger of the vehicle. Since data parameters
 * update from server to client, tick sensitive logic uses the data can sometimes be messed up due
 * to latency. For a good experience driving vehicles, the physics need to be updated instantly with
 * local input. Due to the latency issues faced with sending the input to the server then having it
 * synced back to the client via a data parameter, you can see how this would affect driving, especially
 * on a high latency server. This also helps in the aid of reducing code, since remote client players
 * should continue to use the data parameter value, not the local value.
 *
 * Author: MrCrayfish
 */
public class VehicleDataValue<T>
{
    private final DataParameter<T> key;
    private T localValue;

    public VehicleDataValue(VehicleEntity vehicle, DataParameter<T> key)
    {
        this.key = key;
        this.localValue = vehicle.getEntityData().get(key);
        vehicle.registerDataValue(this);
    }

    public void set(VehicleEntity vehicle, T value)
    {
        vehicle.getEntityData().set(this.key, value);
        this.localValue = value;
    }

    public T get(VehicleEntity vehicle)
    {
        return this.isLocalPlayerDriving(vehicle) ? this.localValue : vehicle.getEntityData().get(this.key);
    }

    private boolean isLocalPlayerDriving(VehicleEntity vehicle)
    {
        Entity entity = vehicle.getControllingPassenger();
        return entity instanceof PlayerEntity && ((PlayerEntity) entity).isLocalPlayer();
    }

    public DataParameter<T> getKey()
    {
        return this.key;
    }

    public T getLocalValue()
    {
        return this.localValue;
    }

    public void updateLocal(VehicleEntity vehicle)
    {
        this.localValue = vehicle.getEntityData().get(this.key);
    }
}
