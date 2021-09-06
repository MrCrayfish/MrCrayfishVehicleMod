package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.InteractableBox;
import net.minecraft.util.math.AxisAlignedBB;

/**
 * Author: MrCrayfish
 */
public class InteractableBoxRayTraceData extends BoxRayTraceData
{
    private final InteractableBox<?> interactableBox;

    public InteractableBoxRayTraceData(InteractableBox<?> interactableBox)
    {
        super(interactableBox.getBoxSupplier().get());
        this.interactableBox = interactableBox;
    }

    @Override
    public AxisAlignedBB getBox()
    {
        return this.interactableBox.getBoxSupplier().get();
    }
}
