package com.mrcrayfish.vehicle.entity.trailer;

import com.mrcrayfish.vehicle.client.EntityRayTracer;
import com.mrcrayfish.vehicle.entity.TrailerEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAttachTrailer;
import com.mrcrayfish.vehicle.network.message.MessageEntityFluid;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class FluidTrailerEntity extends TrailerEntity implements IEntityAdditionalSpawnData
{
    private static final EntityRayTracer.RayTracePart CONNECTION_BOX = new EntityRayTracer.RayTracePart(createScaledBoundingBox(-7 * 0.0625, 4.3 * 0.0625, 14 * 0.0625, 7 * 0.0625, 8.5 * 0.0625F, 24 * 0.0625, 1.1));
    private static final Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> interactionBoxMapStatic = DistExecutor.callWhenOn(Dist.CLIENT, () -> () -> {
        Map<EntityRayTracer.RayTracePart, EntityRayTracer.TriangleRayTraceList> map = new HashMap<>();
        map.put(CONNECTION_BOX, EntityRayTracer.boxToTriangles(CONNECTION_BOX.getBox(), null));
        return map;
    });

    protected FluidTank tank = new FluidTank(FluidAttributes.BUCKET_VOLUME * 100)
    {
        @Override
        protected void onContentsChanged()
        {
            syncTank();
        }
    };

    public FluidTrailerEntity(EntityType<? extends FluidTrailerEntity> type, World worldIn)
    {
        super(type, worldIn);
    }

    @Override
    public boolean canBeColored()
    {
        return true;
    }

    @Override
    public ActionResultType interact(PlayerEntity player, Hand hand)
    {
        if(!level.isClientSide && !player.isCrouching())
        {
            if(FluidUtil.interactWithFluidHandler(player, hand, tank))
            {
                return ActionResultType.SUCCESS;
            }
        }
        return super.interact(player, hand);
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
    public boolean processHit(EntityRayTracer.RayTraceResultRotated result, boolean rightClick)
    {
        if(result.getPartHit() == CONNECTION_BOX && rightClick)
        {
            PacketHandler.instance.sendToServer(new MessageAttachTrailer(this.getId(), Minecraft.getInstance().player.getId()));
            return true;
        }
        return super.processHit(result, rightClick);
    }

    @Override
    public double getHitchOffset()
    {
        return -25.0;
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound)
    {
        super.readAdditionalSaveData(compound);
        if(compound.contains("Tank", Constants.NBT.TAG_COMPOUND))
        {
            this.tank.readFromNBT(compound.getCompound("Tank"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound)
    {
        super.addAdditionalSaveData(compound);
        CompoundNBT tankTag = new CompoundNBT();
        this.tank.writeToNBT(tankTag);
        compound.put("Tank", tankTag);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
            return LazyOptional.of(() -> this.tank).cast();
        return super.getCapability(cap);
    }

    public FluidTank getTank()
    {
        return this.tank;
    }

    public void syncTank()
    {
        if(!this.level.isClientSide)
        {
            PacketHandler.instance.send(PacketDistributor.TRACKING_ENTITY.with(() -> this), new MessageEntityFluid(this.getId(), this.tank.getFluid()));
        }
    }

    @Override
    public void writeSpawnData(PacketBuffer buffer)
    {
        super.writeSpawnData(buffer);
        buffer.writeNbt(this.tank.writeToNBT(new CompoundNBT()));
    }

    @Override
    public void readSpawnData(PacketBuffer buffer)
    {
        super.readSpawnData(buffer);
        this.tank.readFromNBT(buffer.readNbt());
    }
}
