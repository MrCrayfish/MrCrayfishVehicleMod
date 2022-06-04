package com.mrcrayfish.vehicle.client.raytrace.data;

import com.mrcrayfish.vehicle.client.model.ComponentManager;
import com.mrcrayfish.vehicle.client.model.ComponentModel;
import com.mrcrayfish.vehicle.client.raytrace.EntityRayTracer;
import com.mrcrayfish.vehicle.client.raytrace.ITriangleList;
import com.mrcrayfish.vehicle.client.raytrace.MatrixTransform;
import com.mrcrayfish.vehicle.client.raytrace.RayTraceFunction;
import com.mrcrayfish.vehicle.client.raytrace.TransformHelper;
import com.mrcrayfish.vehicle.client.raytrace.Triangle;
import com.mrcrayfish.vehicle.client.render.complex.ComplexModel;
import com.mrcrayfish.vehicle.client.render.complex.transforms.Transform;
import com.mrcrayfish.vehicle.common.CosmeticTracker;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

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
    protected ITriangleList createTriangleList()
    {
        ComponentModel model = ComponentManager.lookupModel(this.modelLocation);
        if(model != null && model.getComplexModel() != null)
        {
            return new CosmeticTriangleList(model.getComplexModel(), this.matrix);
        }
        IBakedModel bakedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
        return new CosmeticTriangleList(bakedModel, this.matrix);
    }

    public static class CosmeticTriangleList implements ITriangleList
    {
        private final Matrix4f baseMatrix;
        private final List<Pair<List<Triangle>, BiFunction<RayTraceData, Entity, Matrix4f>>> matrixPairs = new ArrayList<>();

        public CosmeticTriangleList(ComponentModel model, Matrix4f baseMatrix)
        {
            this.baseMatrix = baseMatrix;
            if(model.getComplexModel() != null)
            {
                this.createAndAddMatrixPair(model.getComplexModel(), new ArrayList<>());
            }
            else
            {
                this.matrixPairs.add(this.createEntry(model.getBaseModel(), Collections.emptyList()));
            }
        }

        public CosmeticTriangleList(ComplexModel model, Matrix4f baseMatrix)
        {
            this.baseMatrix = baseMatrix;
            this.createAndAddMatrixPair(model, new ArrayList<>());
        }

        public CosmeticTriangleList(IBakedModel model, Matrix4f baseMatrix)
        {
            this.baseMatrix = baseMatrix;
            this.matrixPairs.add(this.createEntry(model, Collections.emptyList()));
        }

        private void createAndAddMatrixPair(ComplexModel model, List<Transform> complexTransforms)
        {
            complexTransforms.addAll(model.getTransforms());
            List<Transform> modelTransforms = new ArrayList<>(complexTransforms);
            this.matrixPairs.add(this.createEntry(model.getModel(), modelTransforms));
            model.getChildren().forEach(child -> this.createAndAddMatrixPair(child, complexTransforms));
            complexTransforms.removeAll(model.getTransforms());
        }

        private Pair<List<Triangle>, BiFunction<RayTraceData, Entity, Matrix4f>> createEntry(IBakedModel model, List<Transform> modelTransforms)
        {
            return Pair.of(EntityRayTracer.createTrianglesFromBakedModel(model, this.baseMatrix), (data, entity) -> {
                VehicleEntity vehicle = ((VehicleEntity) entity);
                CosmeticRayTraceData cosmeticData = ((CosmeticRayTraceData) data);
                List<MatrixTransform> baseTransforms = new ArrayList<>();
                VehicleProperties properties = vehicle.getProperties();
                TransformHelper.createSimpleTransforms(baseTransforms, properties.getBodyTransform());
                baseTransforms.add(MatrixTransform.translate((float) cosmeticData.offset.x, (float) cosmeticData.offset.y, (float) cosmeticData.offset.z));
                baseTransforms.add(MatrixTransform.translate(0.0F, properties.getAxleOffset() * 0.0625F, 0.0F));
                baseTransforms.add(MatrixTransform.translate(0.0F, properties.getWheelOffset() * 0.0625F, 0.0F));
                CosmeticTracker tracker = ((VehicleEntity) entity).getCosmeticTracker();
                tracker.getActions(cosmeticData.cosmeticId).forEach(action -> action.gatherTransforms(baseTransforms));
                modelTransforms.forEach(transform -> baseTransforms.add(transform.create(vehicle, 0F)));
                return TransformHelper.createMatrixFromTransformsForPart(baseTransforms);
            });
        }

        @Override
        public List<Triangle> getTriangles(RayTraceData data, Entity entity)
        {
            List<Triangle> triangles = new ArrayList<>();
            this.matrixPairs.forEach(pair ->
            {
                Matrix4f matrix = pair.getRight().apply(data, entity);
                for(Triangle triangle : pair.getLeft())
                {
                    triangles.add(new Triangle(EntityRayTracer.getTransformedTriangle(triangle.getVertices(), matrix)));
                }
            });
            return triangles;
        }

        @Override
        public List<Triangle> getTriangles()
        {
            return Collections.emptyList();
        }
    }
}
