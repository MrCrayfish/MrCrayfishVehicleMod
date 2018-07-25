package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class EntityTrailer extends EntityVehicle implements EntityRaytracer.IEntityRaytraceable
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(1 * 0.0625, -0.5 * 0.0625, -22 * 0.0625, 15 * 0.0625, 2.1 * 0.0625F, -32 * 0.0625, 1.1));
    private static final Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> interactionBoxMapStatic = Maps.newHashMap();

    static
    {
        if(FMLCommonHandler.instance().getSide().isClient())
        {
            interactionBoxMapStatic.put(CONNECTION_BOX, EntityRaytracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        }
    }

    private Entity pullingEntity;

    public float wheelRotation;
    public float prevWheelRotation;

    public EntityTrailer(World worldIn)
    {
        super(worldIn);
    }

    @Override
    public void onClientInit()
    {
        body = new ItemStack(ModItems.TRAILER_BODY);
        wheel = new ItemStack(ModItems.WHEEL);
    }

    @Override
    protected void onUpdateVehicle()
    {

    }

    @Override
    public double getMountedYOffset()
    {
        return 0;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    public List<EntityRaytracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return Collections.singletonList(CONNECTION_BOX);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        RenderGlobal.drawSelectionBoundingBox(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 6.9 * 0.0625F, 24 * 0.0625, 1.1), 0, 1, 0, 0.4F);
    }
}
