package com.mrcrayfish.vehicle.entity.trailer;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityTrailer;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class EntityVehicleTrailer extends EntityTrailer implements EntityRaytracer.IEntityRaytraceable
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 8.5 * 0.0625F, 24 * 0.0625, 1.1));
    private static final Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> interactionBoxMapStatic = Maps.newHashMap();

    static
    {
        if(FMLCommonHandler.instance().getSide().isClient())
        {
            interactionBoxMapStatic.put(CONNECTION_BOX, EntityRaytracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        }
    }

    public EntityVehicleTrailer(World worldIn)
    {
        super(worldIn);
        this.setSize(1.5F, 0.75F);
    }

    @Override
    public double getMountedYOffset()
    {
        return 8 * 0.0625;
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(passenger instanceof EntityVehicle)
        {
            Vec3d offset = ((EntityVehicle) passenger).getProperties().getTrailerOffset().rotateYaw((float) Math.toRadians(-this.rotationYaw));
            passenger.setPosition(this.posX + offset.x, this.posY + getMountedYOffset() + offset.y, this.posZ + offset.z);
            passenger.prevRotationYaw = this.prevRotationYaw;
            passenger.rotationYaw = this.rotationYaw;
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return passenger instanceof EntityVehicle && this.getPassengers().size() == 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Map<EntityRaytracer.RayTracePart, EntityRaytracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @SideOnly(Side.CLIENT)
    public List<EntityRaytracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return Collections.singletonList(CONNECTION_BOX);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean processHit(EntityRaytracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(result.getPartHit() == CONNECTION_BOX && rightClick)
        {
            PacketHandler.INSTANCE.sendToServer(new MessageAttachTrailer(this.getEntityId(), Minecraft.getMinecraft().player.getEntityId()));
            return true;
        }
        return EntityRaytracer.IEntityRaytraceable.super.processHit(result, rightClick);
    }

    @Override
    public double getHitchOffset()
    {
        return -25.0;
    }
}
