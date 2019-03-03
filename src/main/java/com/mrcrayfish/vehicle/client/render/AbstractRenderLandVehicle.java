package com.mrcrayfish.vehicle.client.render;

import com.mrcrayfish.vehicle.entity.EntityPoweredVehicle;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Author: MrCrayfish
 */
@SideOnly(Side.CLIENT)
public abstract class AbstractRenderLandVehicle<T extends EntityPoweredVehicle> extends AbstractRenderVehicle<T>
{

}