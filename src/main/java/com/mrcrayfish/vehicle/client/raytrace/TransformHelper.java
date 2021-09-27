package com.mrcrayfish.vehicle.client.raytrace;

import com.google.common.collect.Lists;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.raytrace.data.ItemStackRayTraceData;
import com.mrcrayfish.vehicle.client.raytrace.data.RayTraceData;
import com.mrcrayfish.vehicle.client.raytrace.data.SpecialModelRayTraceData;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.PoweredProperties;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class TransformHelper
{
    /**
     * Creates a body transformation based on a PartPosition for a raytraceable entity's body. These
     * arguments should be the same as the static properties defined for the vehicle.
     *
     * @param transforms the global transformation matrix
     * @param entityType the vehicle entity type
     */
    public static void createBodyTransforms(List<MatrixTransform> transforms, EntityType<? extends VehicleEntity> entityType)
    {
        VehicleProperties properties = VehicleProperties.get(entityType);
        Transform bodyPosition = properties.getBodyTransform();
        transforms.add(MatrixTransform.scale((float) bodyPosition.getScale()));
        transforms.add(MatrixTransform.translate((float) bodyPosition.getX() * 0.0625F, (float) bodyPosition.getY() * 0.0625F, (float) bodyPosition.getZ() * 0.0625F));
        transforms.add(MatrixTransform.translate(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransform.translate(0.0F, properties.getAxleOffset() * 0.0625F, 0.0F));
        transforms.add(MatrixTransform.translate(0.0F, properties.getWheelOffset() * 0.0625F, 0.0F));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees((float) bodyPosition.getRotX())));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees((float) bodyPosition.getRotY())));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Z.rotationDegrees((float) bodyPosition.getRotZ())));
    }

    public static void createSimpleTransforms(List<MatrixTransform> transforms, Transform transform)
    {
        transforms.add(MatrixTransform.scale((float) transform.getScale()));
        transforms.add(MatrixTransform.translate((float) transform.getX() * 0.0625F, (float) transform.getY() * 0.0625F, (float) transform.getZ() * 0.0625F));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees((float) transform.getRotX())));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees((float) transform.getRotY())));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Z.rotationDegrees((float) transform.getRotZ())));
    }

    public static void createPartTransforms(ISpecialModel model, Transform transform, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, @Nullable RayTraceFunction function)
    {
        createPartTransforms(new SpecialModelRayTraceData(model, function), transform.getTranslate(), transform.getRotation(), (float) transform.getScale(), parts, globalTransforms);
    }

    public static void createPartTransforms(Item part, Transform transform, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, @Nullable RayTraceFunction function)
    {
        createPartTransforms(new ItemStackRayTraceData(new ItemStack(part), function), transform.getTranslate(), transform.getRotation(), (float) transform.getScale(), parts, globalTransforms);
    }

    public static void createPartTransforms(RayTraceData data, Vector3d offset, Vector3d rotation, float scale, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> transformsGlobal)
    {
        List<MatrixTransform> transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransform.translate((float) offset.x * 0.0625F, (float) offset.y * 0.0625F, (float) offset.z * 0.0625F));
        transforms.add(MatrixTransform.translate(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransform.scale(scale));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_X.rotationDegrees((float) rotation.x)));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Y.rotationDegrees((float) rotation.y)));
        transforms.add(MatrixTransform.rotate(Axis.POSITIVE_Z.rotationDegrees((float) rotation.z)));
        createTransformListForPart(data, parts, transforms);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, MatrixTransform... transforms)
    {
        createTransformListForPart(new SpecialModelRayTraceData(model), parts, globalTransforms, transforms);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, @Nullable RayTraceFunction function, MatrixTransform... transforms)
    {
        createTransformListForPart(new SpecialModelRayTraceData(model, function), parts, globalTransforms, transforms);
    }

    public static void createTransformListForPart(Item item, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, MatrixTransform... transforms)
    {
        createTransformListForPart(new ItemStackRayTraceData(new ItemStack(item)), parts, globalTransforms, transforms);
    }

    public static void createTransformListForPart(Item item, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, @Nullable RayTraceFunction function, MatrixTransform... transforms)
    {
        createTransformListForPart(new ItemStackRayTraceData(new ItemStack(item), function), parts, globalTransforms, transforms);
    }

    public static void createTransformListForPart(RayTraceData data, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, MatrixTransform... transforms)
    {
        List<MatrixTransform> transformsAll = Lists.newArrayList();
        transformsAll.addAll(globalTransforms);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(data, transformsAll);
        data.clearTriangles();
        data.setMatrix(createMatrixFromTransformsForPart(transformsAll));
    }

    public static void createEngineTransforms(Item engineItem, EntityType<? extends VehicleEntity> entityType, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms, @Nullable RayTraceFunction function)
    {
        Transform engineTransform = VehicleProperties.get(entityType).getExtended(PoweredProperties.class).getEngineTransform();
        List<MatrixTransform> transforms = new ArrayList<>(globalTransforms);
        transforms.add(MatrixTransform.translate(0.0F, 0.5F * (float) engineTransform.getScale(), 0.0F));
        createPartTransforms(engineItem, engineTransform, parts, transforms, function);
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part and adds them the list of transforms
     * for the given entity.
     *
     * @param entityType the vehicle entity type
     * @param parts map of all parts to their transforms
     */
    public static void createTowBarTransforms(EntityType<? extends VehicleEntity> entityType, ISpecialModel model, HashMap<RayTraceData, List<MatrixTransform>> parts)
    {
        VehicleProperties properties = VehicleProperties.get(entityType);
        double bodyScale = properties.getBodyTransform().getScale();
        List<MatrixTransform> transforms = new ArrayList<>();
        transforms.add(MatrixTransform.rotate(Vector3f.YP.rotationDegrees(180F)));
        transforms.add(MatrixTransform.translate(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransform.translate(0.0F, 0.5F, 0.0F)); // Need extra translate to prevent translation in #createPartTransforms call
        Vector3d towBarOffset = properties.getTowBarOffset().scale(bodyScale).multiply(1, 1, -1);
        createPartTransforms(new SpecialModelRayTraceData(model), towBarOffset, Vector3d.ZERO, 1.0F, parts, transforms);
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part and adds them the list of transforms
     * for the given entity.
     *
     * @param entityType the vehicle entity type
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     */
    public static void createFuelFillerTransforms(EntityType<? extends VehicleEntity> entityType, ISpecialModel model, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> transformsGlobal)
    {
        Transform fuelPortPosition = VehicleProperties.get(entityType).getExtended(PoweredProperties.class).getFuelFillerTransform();
        createPartTransforms(model, fuelPortPosition, parts, transformsGlobal, RayTraceFunction.FUNCTION_FUELING);
    }

    public static void createIgnitionTransforms(EntityType<? extends VehicleEntity> entityType, HashMap<RayTraceData, List<MatrixTransform>> parts, List<MatrixTransform> globalTransforms)
    {
        Transform ignitionTransform = VehicleProperties.get(entityType).getExtended(PoweredProperties.class).getIgnitionTransform();
        createPartTransforms(SpecialModels.KEY_HOLE, ignitionTransform, parts, globalTransforms, null);
    }

    public static Matrix4f createMatrixFromTransformsForPart(List<MatrixTransform> transforms)
    {
        return createMatrixFromTransforms(transforms, -0.5F, -0.5F, -0.5F);
    }

    public static Matrix4f createMatrixFromTransformsForInteractionBox(List<MatrixTransform> transforms)
    {
        return createMatrixFromTransforms(transforms, 0.0F, -0.5F, 0.0F);
    }

    public static Matrix4f createMatrixFromTransforms(List<MatrixTransform> transforms, float xOffset, float yOffset, float zOffset)
    {
        Matrix4f matrix = new Matrix4f();
        matrix.setIdentity();
        transforms.forEach(t -> t.transform(matrix));
        MatrixTransform.translate(xOffset, yOffset, zOffset).transform(matrix);
        return matrix;
    }
}
