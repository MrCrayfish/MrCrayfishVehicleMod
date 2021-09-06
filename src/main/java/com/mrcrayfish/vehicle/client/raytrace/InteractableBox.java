package com.mrcrayfish.vehicle.client.raytrace;

import com.mrcrayfish.vehicle.client.raytrace.data.InteractableBoxRayTraceData;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Author: MrCrayfish
 */
public class InteractableBox<T extends VehicleEntity>
{
    private final Supplier<AxisAlignedBB> boxSupplier;
    private final BiConsumer<T, Boolean> handler;
    private final Predicate<T> active;
    private final InteractableBoxRayTraceData data;

    public InteractableBox(Supplier<AxisAlignedBB> boxSupplier, BiConsumer<T, Boolean> handler, Predicate<T> active)
    {
        this.boxSupplier = boxSupplier;
        this.handler = handler;
        this.active = active;
        this.data = new InteractableBoxRayTraceData(this);
    }

    public Supplier<AxisAlignedBB> getBoxSupplier()
    {
        return this.boxSupplier;
    }

    public void handle(VehicleEntity entity, boolean rightClick)
    {
        this.handler.accept((T) entity, rightClick);
    }

    public boolean isActive(VehicleEntity entity)
    {
        return this.active.test((T) entity);
    }

    public InteractableBoxRayTraceData getData()
    {
        return this.data;
    }
}
