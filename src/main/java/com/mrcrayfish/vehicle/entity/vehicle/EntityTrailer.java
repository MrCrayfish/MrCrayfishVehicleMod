package com.mrcrayfish.vehicle.entity.vehicle;

import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.client.EntityRaytracer;
import com.mrcrayfish.vehicle.entity.EntityLandVehicle;
import com.mrcrayfish.vehicle.entity.EntityVehicle;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MoverType;
import net.minecraft.item.ItemStack;
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
public class EntityTrailer extends EntityVehicle implements EntityRaytracer.IEntityRaytraceable
{
    private static final EntityRaytracer.RayTracePart CONNECTION_BOX = new EntityRaytracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 6.9 * 0.0625F, 24 * 0.0625, 1.1));
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
        prevWheelRotation = wheelRotation;

        if(this.pullingEntity != null)
        {
            if(this.pullingEntity.isDead || (this.pullingEntity instanceof EntityLandVehicle && ((EntityLandVehicle) this.pullingEntity).getTrailer() != this))
            {
                this.pullingEntity = null;
                return;
            }

            Vec3d towBar = pullingEntity.getPositionVector();
            if(pullingEntity instanceof EntityLandVehicle)
            {
                EntityLandVehicle landVehicle = (EntityLandVehicle) pullingEntity;
                towBar = towBar.add(landVehicle.getTowBarVec().rotateYaw((float) Math.toRadians(-landVehicle.rotationYaw + landVehicle.additionalYaw)));
            }

            this.rotationYaw = (float) Math.toDegrees(Math.atan2(towBar.z - this.posZ, towBar.x - this.posX)) - 90F;
            Vec3d vec = new Vec3d(0, 0, -23 * 0.0625).rotateYaw((float) Math.toRadians(-this.rotationYaw)).add(towBar); //TOWING POS
            this.setPosition(vec.x, vec.y, vec.z);
        }
        else if(!world.isRemote)
        {
            this.motionY -= 0.08D;
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
        }

        float speed = (float) (Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posY - this.prevPosY, 2) + Math.pow(this.posZ - this.prevPosZ, 2)) * 20);
        wheelRotation -= 90F * (speed / 30F);
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
        return false;
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        if(passenger instanceof EntityVehicle)
        {
            Vec3d offset = ((EntityVehicle) passenger).getTrailerOffset().rotateYaw((float) Math.toRadians(-this.rotationYaw));
            passenger.setPosition(this.posX + offset.x, this.posY + getMountedYOffset() + offset.y, this.posZ + offset.z);
            passenger.rotationYaw = this.rotationYaw;
        }
    }

    public void setPullingEntity(Entity pullingEntity)
    {
        this.pullingEntity = pullingEntity;
    }

    public Entity getPullingEntity()
    {
        return pullingEntity;
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
        RenderGlobal.drawSelectionBoundingBox(CONNECTION_BOX.getBox(), 0, 1, 0, 0.4F);
    }

    @Override
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
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double) yaw;
        this.lerpPitch = (double) pitch;
        this.lerpSteps = 1;
    }

    @Override
    public boolean canMountTrailer()
    {
        return false;
    }
}
