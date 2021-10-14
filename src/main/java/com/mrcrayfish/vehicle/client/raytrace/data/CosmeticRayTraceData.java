package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.raytrace.TriangleList;
import com.mrcrayfish.vehicle.common.CosmeticTracker;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class CosmeticRayTraceData extends RayTraceData
{
    private final ResourceLocation cosmeticId;
    private final ResourceLocation modelLocation;
    private final Vector3d offset;

    public CosmeticRayTraceData(ResourceLocation cosmeticId, ResourceLocation modelLocation, Vector3d offset)
    {
        this(cosmeticId, modelLocation, offset, null);
    }

    public CosmeticRayTraceData(ResourceLocation cosmeticId, ResourceLocation modelLocation, Vector3d offset, @Nullable RayTraceFunction function)
    {
        super(function);
        this.cosmeticId = cosmeticId;
        this.modelLocation = modelLocation;
        this.offset = offset;
    }

    public ResourceLocation getCosmeticId()
    {
        return this.cosmeticId;
    }

    @Override
    protected TriangleList createTriangleList()
    {
        IBakedModel model = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        return new TriangleList(EntityRayTracer.createTrianglesFromBakedModel(model, this.matrix), (data, entity) -> {
            VehicleEntity vehicle = ((VehicleEntity) entity);
            List<MatrixTransform> transforms = new ArrayList<>();
            VehicleProperties properties = vehicle.getProperties();
            TransformHelper.createSimpleTransforms(transforms, properties.getBodyTransform());
            transforms.add(MatrixTransform.translate((float) this.offset.x, (float) this.offset.y, (float) this.offset.z));
            transforms.add(MatrixTransform.translate(0.0F, properties.getAxleOffset() * 0.0625F, 0.0F));
            transforms.add(MatrixTransform.translate(0.0F, properties.getWheelOffset() * 0.0625F, 0.0F));
            CosmeticTracker tracker = ((VehicleEntity) entity).getCosmeticTracker();
            tracker.getActions(this.cosmeticId).forEach(action -> action.gatherTransforms(transforms));
            return TransformHelper.createMatrixFromTransformsForPart(transforms);
        });
    }
}
