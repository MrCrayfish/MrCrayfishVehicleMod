package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class VehicleEntityTrailer extends TrailerEntity
{
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 8.5 * 0.0625F, 24 * 0.0625, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
        Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    });

    public VehicleEntityTrailer(EntityType<? extends VehicleEntityTrailer> type, World worldIn)
    {
        super(type, worldIn);
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
        if(passenger instanceof VehicleEntity)
        {
            Vector3d offset = ((VehicleEntity) passenger).getProperties().getTrailerOffset().rotateYaw((float) Math.toRadians(-this.rotationYaw));
            passenger.setPosition(this.getPosX() + offset.x, this.getPosY() + getMountedYOffset() + offset.y, this.getPosZ() + offset.z);
            passenger.prevRotationYaw = this.prevRotationYaw;
            passenger.rotationYaw = this.rotationYaw;
        }
    }

    @Override
    protected boolean canFitPassenger(Entity passenger)
    {
        return passenger instanceof VehicleEntity && this.getPassengers().size() == 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> getStaticInteractionBoxMap()
    {
        return interactionBoxMapStatic;
    }

    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public List<EntityRayTracer.RayTracePart> getApplicableInteractionBoxes()
    {
        return Collections.singletonList(CONNECTION_BOX);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer)
    {
        //TODO fix rendering boxes
        //RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(result.getPartHit() == CONNECTION_BOX && rightClick)
        {
            PacketHandler.instance.sendToServer(new MessageAttachTrailer(this.getEntityId(), Minecraft.getInstance().player.getEntityId()));
            return true;
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public double getHitchOffset()
    {
        return -25.0;
    }
}
