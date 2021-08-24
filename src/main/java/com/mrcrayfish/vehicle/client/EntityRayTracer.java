package com.mrcrayfish.vehicle.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.model.ISpecialModel;
import com.mrcrayfish.vehicle.client.model.SpecialModels;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageInteractKey;
import com.mrcrayfish.vehicle.network.message.MessagePickupVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.BiFunction;

/**
 * Author: Phylogeny, MrCrayfish
 * <p>
 * This class allows precise ray trace to be performed on the rendered model item parts, as well as on additional interaction boxes, of entities.
 */
public class EntityRayTracer
{
    private static EntityRayTracer instance;

    private final Map<EntityType<? extends IEntityRayTraceable>, IRayTraceTransforms> entityRayTraceTransforms = new HashMap<>();

    /**
     * Maps raytraceable entities to maps, which map rendered model item parts to the triangles that comprise static versions of the faces of their BakedQuads
     */
    private final Map<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRayTraceTrianglesStatic = new HashMap<>();

    /**
     * Contains all data in entityRaytraceTrianglesStatic and entityRaytraceTrianglesDynamic
     */
    private final Map<EntityType<? extends IEntityRayTraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRayTraceTriangles = new HashMap<>();

    /**
     * Scales and offsets for rendering the entities in crates
     */
    private final Map<EntityType<? extends IEntityRayTraceable>, Pair<Float, Float>> entityCrateScalesAndOffsets = new HashMap<>();
    private final Pair<Float, Float> SCALE_AND_OFFSET_DEFAULT = new ImmutablePair<>(0.25F, 0.0F);

    /**
     * The result of clicking and holding on a continuously interactable raytrace part. Every tick that this is not null,
     * both the raytrace and the interaction of this part will be performed.
     */
    private RayTraceResultRotated continuousInteraction;

    /**
     * The object returned by the interaction function of the result of clicking and holding on a continuously interactable raytrace part.
     */
    private Hand continuousInteractionHand;

    /**
     * Counts the number of ticks that a continuous interaction has been performed for
     */
    private int continuousInteractionTickCounter;

    private EntityRayTracer() {}

    public static EntityRayTracer instance()
    {
        if(instance == null)
        {
            instance = new EntityRayTracer();
        }
        return instance;
    }

    /**
     * Clears registration data and triggers re-registration in the next client tick
     */
    public void clearDataForReregistration()
    {
        this.entityRayTraceTrianglesStatic.clear();
        this.entityRayTraceTriangles.clear();
        this.entityCrateScalesAndOffsets.clear();
    }

    /**
     * Getter for the current continuously interacting raytrace result
     * 
     * @return result of the raytrace
     */
    @Nullable
    public RayTraceResultRotated getContinuousInteraction()
    {
        return this.continuousInteraction;
    }

    /**
     * Getter for the object returned by the current continuously interacting raytrace result's interaction function
     * 
     * @return interaction function result
     */
    @Nullable
    public Hand getContinuousInteractionHand()
    {
        return this.continuousInteractionHand;
    }

    /**
     *
     * @return
     */
    public int getContinuousInteractionTickCounter()
    {
        return this.continuousInteractionTickCounter;
    }

    /**
     * Registers a {@link IRayTraceTransforms} for a vehicle. This allows triangles to be generated
     * for preforming ray tracing on a vehicle. This allows for advanced interactions like being able
     * to click on any part of the vehicle to mount it, fueling the vehicle through it's fuel port,
     * and using a key in the key hole.
     *
     * @param type the entity type of the vehicle
     * @param transforms the ray trace transforms for the vehicle
     * @param <T> an entity type that is a vehicle entity and implements IEntityRayTraceable
     */
    public synchronized <T extends VehicleEntity & IEntityRayTraceable> void registerTransforms(EntityType<T> type, IRayTraceTransforms transforms)
    {
        this.entityRayTraceTransforms.putIfAbsent(type, transforms);
    }

    /**
     * Creates a body transformation based on a PartPosition for a raytraceable entity's body. These
     * arguments should be the same as the static properties defined for the vehicle.
     *
     * @param transforms the global transformation matrix
     * @param entityType the vehicle entity type
     */
    private static void createBodyTransforms(List<MatrixTransformation> transforms, EntityType<? extends VehicleEntity> entityType)
    {
        VehicleProperties properties = VehicleProperties.get(entityType);
        PartPosition bodyPosition = properties.getBodyPosition();
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) bodyPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) bodyPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, (float) bodyPosition.getRotZ()));
        transforms.add(MatrixTransformation.createTranslation((float) bodyPosition.getX(), (float) bodyPosition.getY(), (float) bodyPosition.getZ()));
        transforms.add(MatrixTransformation.createScale((float) bodyPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, properties.getAxleOffset() * 0.0625F, 0.0F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, properties.getWheelOffset() * 0.0625F, 0.0F));
    }

    public static void createPartTransforms(ISpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        List<MatrixTransformation> transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float) partPosition.getX() * 0.0625F, (float) partPosition.getY() * 0.0625F, (float) partPosition.getZ() * 0.0625F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransformation.createScale((float) partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, (float) partPosition.getRotZ()));
        createTransformListForPart(model, parts, transforms);
    }

    public static void createPartTransforms(ISpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, RayTraceFunction function)
    {
        List<MatrixTransformation> transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float) partPosition.getX() * 0.0625F, (float) partPosition.getY() * 0.0625F, (float) partPosition.getZ() * 0.0625F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransformation.createScale((float) partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, (float) partPosition.getRotZ()));
        createTransformListForPart(model, parts, transforms, function);
    }

    public static void createPartTransforms(Item part, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, RayTraceFunction function)
    {
        List<MatrixTransformation> transforms = Lists.newArrayList();
        transforms.addAll(transformsGlobal);
        transforms.add(MatrixTransformation.createTranslation((float) partPosition.getX() * 0.0625F, (float) partPosition.getY() * 0.0625F, (float) partPosition.getZ() * 0.0625F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransformation.createScale((float) partPosition.getScale()));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, (float) partPosition.getRotX()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, (float) partPosition.getRotY()));
        transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, (float) partPosition.getRotZ()));
        createTransformListForPart(new ItemStack(part), parts, transforms, function);
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part.
     * 
     * @param x part's x position in pixels
     * @param y part's y position in pixels
     * @param z part's z position in pixels
     * @param rotation part's rotation vector
     * @param scale part's scale
     * @param transforms list that part transforms are added to
     */
    public static void createPartTransforms(double x, double y, double z, Vector3f rotation, double scale, List<MatrixTransformation> transforms)
    {
        transforms.add(MatrixTransformation.createTranslation((float) x * 0.0625F, (float) y * 0.0625F, (float) z * 0.0625F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransformation.createScale((float) scale));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        if(rotation.x() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, rotation.x()));
        }
        if(rotation.y() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, rotation.y()));
        }
        if(rotation.z() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, rotation.y()));
        }
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part and adds them the list of transforms
     * for the given entity.
     *
     * @param entityType the vehicle entity type
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     */
    public static void createFuelPartTransforms(EntityType<? extends VehicleEntity> entityType, ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        PartPosition fuelPortPosition = VehicleProperties.get(entityType).getFuelPortPosition();
        createPartTransforms(model, fuelPortPosition, parts, transformsGlobal, RayTraceFunction.FUNCTION_FUELING);
    }

    /**
     * Version of {@link EntityRayTracer#createFuelPartTransforms createFuelPartTransforms} that sets the axis of rotation to Y
     * 
     * @param part the rendered item part
     * @param xMeters part's x offset meters
     * @param yMeters part's y offset meters
     * @param zMeters part's z offset meters
     * @param xPixel part's x position in pixels
     * @param yPixel part's y position in pixels
     * @param zPixel part's z position in pixels
     * @param rotation part's rotation yaw (Y axis)
     * @param scale part's scale
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     */
    public static void createFuelPartTransforms(Item part, double xMeters, double yMeters, double zMeters, double xPixel, double yPixel, double zPixel, double rotation, double scale, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        List<MatrixTransformation> partTransforms = Lists.newArrayList();
        partTransforms.add(MatrixTransformation.createTranslation((float) xMeters, (float) yMeters, (float) zMeters));
        createPartTransforms(xPixel, yPixel, zPixel, new Vector3f(0.0F, (float) rotation, 0.0F), scale, partTransforms);
        transformsGlobal.addAll(partTransforms);
        createTransformListForPart(new ItemStack(part), parts, transformsGlobal, RayTraceFunction.FUNCTION_FUELING);
    }

    public static void createKeyPortTransforms(EntityType<? extends VehicleEntity> entityType, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        PartPosition keyPortPosition = VehicleProperties.get(entityType).getKeyPortPosition();
        createPartTransforms(SpecialModels.KEY_HOLE, keyPortPosition, parts, transformsGlobal);
    }

    /**
     * Adds all global and part-specific transforms for an item part to the list of transforms for the given entity
     * 
     * @param part the rendered item part in a stack
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     * @param continuousInteraction interaction to be performed each tick
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, @Nullable RayTraceFunction continuousInteraction, MatrixTransformation... transforms)
    {
        List<MatrixTransformation> transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart(part, continuousInteraction), transformsAll);
    }

    /**
     * Version of {@link EntityRayTracer#createTransformListForPart(ItemStack, HashMap, List, RayTraceFunction, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     * 
     * @param part the rendered item part in a stack
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(part, parts, transformsGlobal, null, transforms);
    }

    /**
     * Version of {@link EntityRayTracer#createTransformListForPart(ItemStack, HashMap, List, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(new ItemStack(part), parts, transformsGlobal, transforms);
    }

    /**
     * Version of {@link EntityRayTracer#createTransformListForPart(Item, HashMap, List, MatrixTransformation[]) createTransformListForPart} without global transform list
     *
     * @param model the special model
     * @param parts map of all parts to their transforms
     * @param transforms part-specific transforms for the given part
     */
    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation... transforms)
    {
        createTransformListForPart(model, parts, Lists.newArrayList(), transforms);
    }

    /**
     * Version of {@link EntityRayTracer#createTransformListForPart(Item, HashMap, List, MatrixTransformation[]) createTransformListForPart} without global transform list
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation... transforms)
    {
        createTransformListForPart(part, parts, Lists.newArrayList(), transforms);
    }

    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, @Nullable RayTraceFunction continuousInteraction, MatrixTransformation... transforms)
    {
        List<MatrixTransformation> transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart(model, continuousInteraction), transformsAll);
    }

    /**
     * Version of {@link EntityRayTracer#createTransformListForPart(ItemStack, HashMap, List, RayTraceFunction, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     *
     * @param model the ibakedmodel of the part
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that construct to all parts for this entity
     * @param transforms part-specific transforms for the given part
     */
    public static void createTransformListForPart(ISpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(model, parts, transformsGlobal, null, transforms);
    }

    /**
     * Create static triangles for raytraceable entity
     * 
     * @param entityType the entity type
     * @param transforms matrix transforms for each part
     */
    private <T extends VehicleEntity> void generateEntityTriangles(EntityType<T> entityType, Map<RayTracePart, List<MatrixTransformation>> transforms)
    {
        Map<RayTracePart, TriangleRayTraceList> partTriangles = new HashMap<>();
        for(Entry<RayTracePart, List<MatrixTransformation>> entryPart : transforms.entrySet())
        {
            /* Creates a new matrix for each part */
            Matrix4f matrix = new Matrix4f();
            matrix.setIdentity();
            for (MatrixTransformation transform : entryPart.getValue())
            {
                transform.transform(matrix);
            }
            finalizePartStackMatrix(matrix);

            RayTracePart part = entryPart.getKey();
            partTriangles.put(part, new TriangleRayTraceList(generateTriangles(getModel(part), matrix)));
        }
        this.entityRayTraceTrianglesStatic.put(entityType, partTriangles);
        HashMap<RayTracePart, TriangleRayTraceList> partTrianglesCopy = new HashMap<>(partTriangles);
        Map<RayTracePart, TriangleRayTraceList> partTrianglesAll = this.entityRayTraceTriangles.get(entityType);
        if (partTrianglesAll != null)
        {
            partTrianglesCopy.putAll(partTrianglesAll);
        }
        this.entityRayTraceTriangles.put(entityType, partTrianglesCopy);
    }

    /**
     * Gets entity's scale and offset for rendering in a crate
     * 
     * @param entityType the entity type
     * 
     * @return pair of scale and offset
     */
    public Pair<Float, Float> getCrateScaleAndOffset(EntityType<? extends VehicleEntity> entityType)
    {
        Pair<Float, Float> scaleAndOffset = this.entityCrateScalesAndOffsets.get(entityType);
        return scaleAndOffset == null ? SCALE_AND_OFFSET_DEFAULT : scaleAndOffset;
    }

    /**
     * Gets an IBakedModel from a RayTracePart
     * 
     * @param part a ray trace part
     * 
     * @return stack's model
     */
    private static IBakedModel getModel(RayTracePart part)
    {
        if(part.model != null)
        {
            return part.model.getModel();
        }
        return Minecraft.getInstance().getItemRenderer().getModel(part.partStack, null, Minecraft.getInstance().player);
    }

    /**
     * Converts a model into triangles that represent its quads
     * 
     * @param model rendered model of entity
     * @param matrix part-specific matrix mirroring the static GL operations performed on that part during rendering - should be null for dynamic triangles
     * 
     * @return list of all triangles
     */
    private static List<TriangleRayTrace> generateTriangles(IBakedModel model, @Nullable Matrix4f matrix)
    {
        List<TriangleRayTrace> triangles = Lists.newArrayList();
        try
        {
            Random random = new Random();
            random.setSeed(42L);
            // Generate triangles for all faceless and faced quads
            generateTriangles(model.getQuads(null, null, random), matrix, triangles);
            for(Direction facing : Direction.values())
            {
                random.setSeed(42L);
                generateTriangles(model.getQuads(null, facing, random), matrix, triangles);
            }
        }
        catch(Exception ignored)
        {
        }
        return triangles;
    }

    /**
     * Converts quads into pairs of transformed triangles that represent them
     * 
     * @param list list of BakedQuad
     * @param matrix part-specific matrix mirroring the static GL operations performed on that part during rendering - should be null for dynamic triangles
     * @param triangles list of all triangles for the given raytraceable entity class
     */
    private static void generateTriangles(List<BakedQuad> list, @Nullable Matrix4f matrix, List<TriangleRayTrace> triangles)
    {
        for(BakedQuad quad : list)
        {
            int size = DefaultVertexFormats.BLOCK.getIntegerSize();
            int[] data = quad.getVertices();
            // Two triangles that represent the BakedQuad
            float[] triangle1 = new float[9];
            float[] triangle2 = new float[9];

            // Corner 1
            triangle1[0] = Float.intBitsToFloat(data[0]);
            triangle1[1] = Float.intBitsToFloat(data[1]);
            triangle1[2] = Float.intBitsToFloat(data[2]);
            // Corner 2
            triangle1[3] = triangle2[6] = Float.intBitsToFloat(data[size]);
            triangle1[4] = triangle2[7] = Float.intBitsToFloat(data[size + 1]);
            triangle1[5] = triangle2[8] = Float.intBitsToFloat(data[size + 2]);
            // Corner 3
            size *= 2;
            triangle2[0] = Float.intBitsToFloat(data[size]);
            triangle2[1] = Float.intBitsToFloat(data[size + 1]);
            triangle2[2] = Float.intBitsToFloat(data[size + 2]);
            // Corner 4
            size *= 1.5;
            triangle1[6] = triangle2[3] = Float.intBitsToFloat(data[size]);
            triangle1[7] = triangle2[4] = Float.intBitsToFloat(data[size + 1]);
            triangle1[8] = triangle2[5] = Float.intBitsToFloat(data[size + 2]);

            transformTriangleAndAdd(triangle1, matrix, triangles);
            transformTriangleAndAdd(triangle2, matrix, triangles);
        }
    }

    /**
     * Transforms a static triangle by the part-specific matrix and adds it to the list of all triangles for the given raytraceable entity class, or simply
     * adds a dynamic triangle to that list
     * 
     * @param triangle array of the three vertices that comprise the triangle
     * @param matrix part-specific matrix mirroring the static GL operations performed on that part during rendering - should be null for dynamic triangles
     * @param triangles list of all triangles for the given raytraceable entity class
     */
    private static void transformTriangleAndAdd(float[] triangle, @Nullable Matrix4f matrix, List<TriangleRayTrace> triangles)
    {
        triangles.add(new TriangleRayTrace(matrix != null ? getTransformedTriangle(triangle, matrix) : triangle));
    }

    /**
     * gets a new triangle transformed by the passed part-specific matrix
     * 
     * @param triangle array of the three vertices that comprise the triangle
     * @param matrix part-specific matrix mirroring the GL operation performed on that part during rendering
     * 
     * @return new transformed triangle
     */
    private static float[] getTransformedTriangle(float[] triangle, Matrix4f matrix)
    {
        float[] triangleNew = new float[9];
        for (int i = 0; i < 9; i += 3)
        {
            Vector4f vec = new Vector4f(triangle[i], triangle[i + 1], triangle[i + 2], 1);
            vec.transform(matrix);
            triangleNew[i] = vec.x();
            triangleNew[i + 1] = vec.y();
            triangleNew[i + 2] = vec.z();
        }
        return triangleNew;
    }

    /**
     * Adds the final required translation to the part stack's matrix
     * 
     * @param matrix part-specific matrix mirroring the GL operation performed on that part during rendering
     */
    public static void finalizePartStackMatrix(Matrix4f matrix)
    {
        MatrixTransformation.createTranslation(-0.5F, -0.5F, -0.5F).transform(matrix);
    }

    /**
     * Matrix transformation that corresponds to one of the three supported GL operations that might be performed on a rendered item part
     */
    public static class MatrixTransformation
    {
        private final MatrixTransformationType type;
        private float x, y, z;
        private float angle;

        /**
         * Three matrix transformations that correspond to the three supported GL operations that might be performed on a rendered item part
         */
        private enum MatrixTransformationType
        {
            TRANSLATION, ROTATION, SCALE
        }

        public MatrixTransformation(MatrixTransformationType type, float x, float y, float z)
        {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MatrixTransformation(MatrixTransformationType type, float x, float y, float z, float angle)
        {
            this(type, x, y, z);
            this.angle = angle;
        }

        public static MatrixTransformation createTranslation(float x, float y, float z)
        {
            return new MatrixTransformation(MatrixTransformationType.TRANSLATION, x, y, z);
        }

        public static MatrixTransformation createRotation(Vector3f axis, float angle)
        {
            return new MatrixTransformation(MatrixTransformationType.ROTATION, axis.x(), axis.y(), axis.z(), angle);
        }

        public static MatrixTransformation createScale(float x, float y, float z)
        {
            return new MatrixTransformation(MatrixTransformationType.SCALE, x, y, z);
        }

        public static MatrixTransformation createScale(float xyz)
        {
            return new MatrixTransformation(MatrixTransformationType.SCALE, xyz, xyz, xyz);
        }

        /**
         * Applies the matrix transformation that this class represents to the passed matrix
         *
         * @param matrix matrix to construct this transformation to
         */
        public void transform(Matrix4f matrix)
        {
            MatrixStack matrixStack = new MatrixStack();
            switch(type)
            {
                case ROTATION:
                    matrixStack.mulPose(new Vector3f(this.x, this.y, this.z).rotationDegrees(this.angle));
                    break;
                case TRANSLATION:
                    matrixStack.translate(this.x, this.y, this.z);
                    break;
                case SCALE:
                    matrixStack.scale(this.x, this.y, this.z);
                    break;
            }
            matrix.multiply(matrixStack.last().pose());
        }
    }

    /**
     * Performs a specific and general interaction with a raytraceable entity
     * 
     * @param entity raytraceable entity
     * @param result the result of the raytrace
     */
    public static void interactWithEntity(IEntityRayTraceable entity, EntityRayTraceResult result)
    {
        Minecraft.getInstance().gameMode.interact(Minecraft.getInstance().player, (Entity) entity, Hand.MAIN_HAND);
        Minecraft.getInstance().gameMode.interactAt(Minecraft.getInstance().player, (Entity) entity, result, Hand.MAIN_HAND);
    }

    /**
     * Performs a raytrace and interaction each tick that a continuously interactable part is right-clicked and held while looking at
     * 
     * @param event tick event
     */
    @SubscribeEvent
    public void rayTraceEntitiesContinuously(TickEvent.ClientTickEvent event)
    {
        if(event.phase != TickEvent.Phase.START)
        {
            return;
        }

        if(this.continuousInteraction == null || Minecraft.getInstance().player == null)
        {
            return;
        }

        RayTraceResultRotated result = rayTraceEntities(this.continuousInteraction.isRightClick());
        if(result == null || result.getEntity() != this.continuousInteraction.getEntity() || result.getPartHit() != this.continuousInteraction.getPartHit())
        {
            this.continuousInteraction = null;
            this.continuousInteractionTickCounter = 0;
            return;
        }
        this.continuousInteractionHand = result.performContinuousInteraction();
        if(this.continuousInteractionHand == null)
        {
            this.continuousInteraction = null;
            this.continuousInteractionTickCounter = 0;
        }
        else
        {
            this.continuousInteractionTickCounter++;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event)
    {
        if(event.phase == TickEvent.Phase.END)
        {
            return;
        }

        Minecraft mc = Minecraft.getInstance();
        if(mc.player == null)
        {
            return;
        }

        if(Config.CLIENT.reloadRayTracerEachTick.get())
        {
            this.entityRayTraceTransforms.keySet().forEach(type ->
            {
                this.initializeTransforms((EntityType<? extends VehicleEntity>) type, true);
            });
        }
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of all raytraceable entities within reach of the player upon click,
     * and cancels it if the clicked raytraceable entity returns true from {@link IEntityRayTraceable#processHit processHit}
     * 
     * @param event mouse event
     */
    @SubscribeEvent
    public void onMouseEvent(InputEvent.RawMouseEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.overlay != null || mc.screen != null)
        {
            return;
        }

        // Return if not right and/or left clicking, if the mouse is being released, or if there are no entity classes to raytrace
        //boolean rightClick = Minecraft.getInstance().gameSettings.keyBindUseItem.getKeyCode() + 100 == event.getButton();
        boolean rightClick = event.getButton() == GLFW.GLFW_MOUSE_BUTTON_RIGHT;
        boolean leftClick = event.getButton() == GLFW.GLFW_MOUSE_BUTTON_LEFT;
        if((!rightClick && (!Config.CLIENT.enabledLeftClick.get() || !leftClick)) || event.getAction() == GLFW.GLFW_RELEASE)
        {
            return;
        }
        if(this.performRayTrace(rightClick))
        {
            // Cancel click
            event.setCanceled(true);
        }
    }

    private boolean performRayTrace(boolean rightClick)
    {
        RayTraceResultRotated result = this.rayTraceEntities(rightClick);
        if(result != null)
        {
            this.continuousInteractionHand = result.performContinuousInteraction();
            if(this.continuousInteractionHand != null)
            {
                this.continuousInteraction = result;
                this.continuousInteractionTickCounter = 1;
            }
            return true;
        }
        return false;
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of all raytraceable entities within reach of the player,
     * and returns the result if the clicked raytraceable entity returns true from {@link IEntityRayTraceable#processHit processHit}
     * 
     * @param rightClick whether the click was a right-click or a left-click
     * 
     * @return the result of the raytrace - returns null, if it fails
     */
    @Nullable
    @SuppressWarnings("unchecked")
    private <T extends VehicleEntity> RayTraceResultRotated rayTraceEntities(boolean rightClick)
    {
        float reach = Minecraft.getInstance().gameMode.getPickRange();
        Vector3d eyeVec = Minecraft.getInstance().player.getEyePosition(1.0F);
        Vector3d forwardVec = eyeVec.add(Minecraft.getInstance().player.getViewVector(1.0F).scale(reach));
        AxisAlignedBB box = new AxisAlignedBB(eyeVec, eyeVec).inflate(reach);
        RayTraceResultRotated closestRayTraceResult = null;
        double closestDistance = Double.MAX_VALUE;
        for(VehicleEntity entity : Minecraft.getInstance().level.getEntitiesOfClass(VehicleEntity.class, box))
        {
            EntityType<T> type = (EntityType<T>) entity.getType();
            if(this.entityRayTraceTransforms.containsKey(type))
            {
                /* Initialize the vehicle triangles if they don't exist. Lazy loading for memory
                 * sake, not that it does much */
                this.initializeTransforms(type, false);

                RayTraceResultRotated rayTraceResult = this.rayTraceEntityRotated(entity, eyeVec, forwardVec, reach, rightClick);
                if(rayTraceResult != null)
                {
                    double distance = rayTraceResult.getDistanceToEyes();
                    if(distance < closestDistance)
                    {
                        closestRayTraceResult = rayTraceResult;
                        closestDistance = distance;
                    }
                }
            }
            else
            {
                VehicleMod.LOGGER.warn("The vehicle '" + type.getRegistryName() + "' does not have any registered ray trace transforms.");
            }
        }
        if(closestRayTraceResult != null)
        {
            double eyeDistance = closestRayTraceResult.getDistanceToEyes();
            if(eyeDistance <= reach)
            {
                /* If the hit entity is a raytraceable entity, and if the player's eyes are inside what MC
                 * thinks the player is looking at, then process the hit regardless of what MC thinks */
                boolean bypass = this.entityRayTraceTrianglesStatic.keySet().contains(closestRayTraceResult.getEntity().getType());
                RayTraceResult result = Minecraft.getInstance().hitResult;
                if(bypass && result != null && result.getType() != Type.MISS)
                {
                    AxisAlignedBB boxMC = null;
                    if(result.getType() == Type.ENTITY)
                    {
                        boxMC = closestRayTraceResult.getEntity().getBoundingBox();
                    }
                    else if(result.getType() == Type.BLOCK)
                    {
                        BlockPos pos = ((BlockRayTraceResult) result).getBlockPos();
                        boxMC = closestRayTraceResult.getEntity().level.getBlockState(pos).getShape(closestRayTraceResult.getEntity().level, pos).bounds();
                    }
                    bypass = boxMC != null && boxMC.contains(eyeVec);
                }

                Vector3d hit = forwardVec;
                if(!bypass && result != null && result.getType() != Type.MISS)
                {
                    /* Set hit to what MC thinks the player is looking at if the player is not
                     * looking at the hit entity */
                    if(result.getType() == Type.ENTITY && ((EntityRayTraceResult) result).getEntity() == closestRayTraceResult.getEntity())
                    {
                        bypass = true;
                    }
                    else
                    {
                        hit = result.getLocation();
                    }
                }

                /* If not bypassed, process the hit only if it is closer to the player's eyes than
                 * what MC thinks the player is looking */
                if(bypass || eyeDistance < hit.distanceTo(eyeVec))
                {
                    if(((IEntityRayTraceable) closestRayTraceResult.getEntity()).processHit(closestRayTraceResult, rightClick))
                    {
                        return closestRayTraceResult;
                    }
                }
            }
        }
        return null;
    }

    /**
     *
     * @param type
     * @param <T>
     */
    private <T extends VehicleEntity> void initializeTransforms(EntityType<T> type, boolean reload)
    {
        if(!this.entityRayTraceTrianglesStatic.containsKey(type) || reload)
        {
            List<MatrixTransformation> transforms = new ArrayList<>();
            createBodyTransforms(transforms, type);
            HashMap<RayTracePart, List<MatrixTransformation>> parts = Maps.newHashMap();
            IRayTraceTransforms rayTraceTransforms = this.entityRayTraceTransforms.get(type);
            rayTraceTransforms.load(this, transforms, parts);
            this.generateEntityTriangles(type, parts);

            float min = 0;
            float max = 0;
            for(Entry<RayTracePart, TriangleRayTraceList> entry : this.entityRayTraceTriangles.get(type).entrySet())
            {
                for(TriangleRayTrace triangle : entry.getValue().getTriangles())
                {
                    float[] data = triangle.getData();
                    for(int i = 0; i < data.length; i += 3)
                    {
                        float x = data[i];
                        float y = data[i + 1];
                        float z = data[i + 2];
                        if(x < min) min = x;
                        if(y < min) min = y;
                        if(z < min) min = z;
                        if(x > max) max = x;
                        if(y > max) max = y;
                        if(z > max) max = z;
                    }
                }
            }
            float range = max - min;
            this.entityCrateScalesAndOffsets.put(type, new ImmutablePair<>(1 / (range * 1.25F), -(min + range * 0.5F)));
        }
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of raytraceable entity
     * 
     * @param entity the vehicle entity
     * @param eyeVec position of the player's eyes
     * @param forwardVec eyeVec extended by reach distance in the direction the player is looking in
     * @param reach distance at which players can interact with objects in the world
     * @param rightClick whether the click was a right-click or a left-click
     * 
     * @return the result of the raytrace
     */
    @Nullable
    public RayTraceResultRotated rayTraceEntityRotated(VehicleEntity entity, Vector3d eyeVec, Vector3d forwardVec, double reach, boolean rightClick)
    {
        Vector3d pos = entity.position();
        double angle = Math.toRadians(-entity.yRot);

        // Rotate the ray trace vectors in the opposite direction as the entity's rotation yaw
        Vector3d eyeVecRotated = rotateVecXZ(eyeVec, angle, pos);
        Vector3d forwardVecRotated = rotateVecXZ(forwardVec, angle, pos);

        float[] eyes = new float[]{(float) eyeVecRotated.x, (float) eyeVecRotated.y, (float) eyeVecRotated.z};
        Vector3d look = forwardVecRotated.subtract(eyeVecRotated).normalize().scale(reach);
        float[] direction = new float[]{(float) look.x, (float) look.y, (float) look.z};

        // Perform ray trace on the entity's interaction boxes
        double distanceShortest = Double.MAX_VALUE;
        RayTraceResultTriangle lookBox = rayTracePartTriangles(entity, pos, eyeVecRotated, null, distanceShortest, eyes, direction, entity.getApplicableInteractionBoxes(), false, entity.getStaticInteractionBoxMap());
        distanceShortest = updateShortestDistance(lookBox, distanceShortest);
        RayTraceResultTriangle lookPart = rayTracePartTriangles(entity, pos, eyeVecRotated, null, distanceShortest, eyes, direction, entity.getNonApplicableParts(), true, this.entityRayTraceTrianglesStatic.get(entity.getType()));

        // Return the result object of hit with hit vector rotated back in the same direction as the entity's rotation yaw, or null it no hit occurred
        if (lookPart != null)
        {
            return new RayTraceResultRotated(entity, rotateVecXZ(lookPart.getHit(), -angle, pos), lookPart.getDistance(), lookPart.getPart(), rightClick);
        }

        return lookBox == null ? null : new RayTraceResultRotated(entity, rotateVecXZ(lookBox.getHit(), -angle, pos), lookBox.getDistance(), lookBox.getPart(), rightClick);
    }

    /**
     * Sets the current shortest distance to the current closest viewed object
     * 
     * @param lookObject current closest viewed object
     * @param distanceShortest distance from eyes to the current closest viewed object
     * 
     * @return new shortest distance
     */
    private static double updateShortestDistance(RayTraceResultTriangle lookObject, double distanceShortest)
    {
        if (lookObject != null)
        {
            distanceShortest = lookObject.getDistance();
        }
        return distanceShortest;
    }

    /**
     * Performs raytrace on part triangles of raytraceable entity
     * 
     * @param entity raytraced entity
     * @param pos position of the raytraced entity 
     * @param eyeVecRotated position of the player's eyes taking into account the rotation yaw of the raytraced entity
     * @param lookPart current closest viewed object
     * @param distanceShortest distance from eyes to the current closest viewed object
     * @param eyes position of the eyes of the player
     * @param direction normalized direction vector the player is looking in scaled by the player reach distance
     * @param partsApplicable list of parts that currently construct to the raytraced entity - if null, all are applicable
     * @param parts triangles for the part
     * 
     * @return the result of the part raytrace
     */
    private static RayTraceResultTriangle rayTracePartTriangles(Entity entity, Vector3d pos, Vector3d eyeVecRotated, RayTraceResultTriangle lookPart, double distanceShortest, float[] eyes, float[] direction, @Nullable List<RayTracePart> partsApplicable, boolean invalidateParts, Map<RayTracePart, TriangleRayTraceList> parts)
    {
        if(parts != null)
        {
            for(Entry<RayTracePart, TriangleRayTraceList> entry : parts.entrySet())
            {
                if(partsApplicable == null || (invalidateParts != partsApplicable.contains(entry.getKey())))
                {
                    RayTraceResultTriangle lookObjectPutative;
                    double distance;
                    RayTracePart part = entry.getKey();
                    for(TriangleRayTrace triangle : entry.getValue().getTriangles(part, entity))
                    {
                        lookObjectPutative = RayTraceResultTriangle.calculateIntercept(eyes, direction, pos, triangle.getData(), part);
                        if(lookObjectPutative != null)
                        {
                            distance = lookObjectPutative.calculateAndSaveDistance(eyeVecRotated);
                            if(distance < distanceShortest)
                            {
                                lookPart = lookObjectPutative;
                                distanceShortest = distance;
                            }
                        }
                    }
                }
            }
        }
        return lookPart;
    }

    /**
     * Rotates the x and z components of a vector about the y axis
     * 
     * @param vec vector to rotate
     * @param angle angle in radians to rotate about the y axis
     * @param rotationPoint vector containing the x/z position to rotate around
     * 
     * @return the passed vector rotated by 'angle' around 'rotationPoint'
     */
    private static Vector3d rotateVecXZ(Vector3d vec, double angle, Vector3d rotationPoint)
    {
        double x = rotationPoint.x + Math.cos(angle) * (vec.x - rotationPoint.x) - Math.sin(angle) * (vec.z - rotationPoint.z);
        double z = rotationPoint.z + Math.sin(angle) * (vec.x - rotationPoint.x) + Math.cos(angle) * (vec.z - rotationPoint.z);
        return new Vector3d(x, vec.y, z);
    }

    /**
     * <strong>Debug Method:</strong> Renders the interaction boxes of, and the triangles of the parts of, a raytraceable entity
     * <p>
     * <strong>Note:</strong>
     * <ul>
     *     <li><strong>The inner if statement must be hard-coded to true, in order to take effect.</strong></li>
     *     <li>This should be called in the entity's renderer at the end of doRender.</li>
     * </ul>
     * 
     * @param entity raytraced entity
     * @param matrixStack the matrix stack of the entity
     * @param yaw entity's rotation yaw
     */
    public <T extends VehicleEntity & IEntityRayTraceable> void renderRayTraceElements(T entity, MatrixStack matrixStack, IRenderTypeBuffer renderTypeBuffer, float yaw)
    {
        if(Config.CLIENT.renderOutlines.get())
        {
            matrixStack.pushPose();
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(-yaw));

            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.last().pose());
            RenderSystem.lineWidth(Math.max(2.0F, (float)Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.0F));
            RenderSystem.disableTexture();
            RenderSystem.enableDepthTest();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            this.renderRayTraceTriangles(entity, tessellator, buffer);

            RenderSystem.disableDepthTest();
            RenderSystem.enableTexture();
            RenderSystem.popMatrix();

            // Draw interaction boxes
            IVertexBuilder builder = renderTypeBuffer.getBuffer(RenderType.lines());
            entity.getApplicableInteractionBoxes().stream().filter(rayTracePart -> rayTracePart.partBox != null).forEach(rayTracePart -> {
                renderShape(matrixStack, builder, VoxelShapes.create(rayTracePart.partBox), 0.0F, 1.0F, 0.0F, 1.0F);
            });

            matrixStack.popPose();
        }
    }

    /**
     * Renders the triangles of the parts of a raytraceable entity
     * 
     * @param entity raytraced entity
     * @param tessellator rendered plane tiler
     * @param buffer tessellator's vertex buffer
     */
    private <T extends VehicleEntity> void renderRayTraceTriangles(T entity, Tessellator tessellator, BufferBuilder buffer)
    {
        EntityType<T> type = (EntityType<T>) entity.getType();
        this.initializeTransforms(type, false);
        Map<RayTracePart, TriangleRayTraceList> map = this.entityRayTraceTriangles.get(type);
        if(map != null)
        {
            List<RayTracePart> partsNonApplicable = entity.getNonApplicableParts();
            for(Entry<RayTracePart, TriangleRayTraceList> entry : map.entrySet())
            {
                if(partsNonApplicable == null || !partsNonApplicable.contains(entry.getKey()))
                {
                    for(TriangleRayTrace triangle : entry.getValue().getTriangles(entry.getKey(), entity))
                    {
                        triangle.draw(tessellator, buffer, 1, 0, 0, 0.4F);
                    }
                }
            }
        }
    }

    public static void renderShape(MatrixStack matrixStack, IVertexBuilder builder, VoxelShape shape, float red, float green, float blue, float alpha)
    {
        Matrix4f pose = matrixStack.last().pose();
        shape.forAllEdges((minX, minY, minZ, maxX, maxY, maxZ) -> {
            builder.vertex(pose, (float) minX, (float) minY, (float) minZ).color(red, green, blue, alpha).endVertex();
            builder.vertex(pose, (float) maxX, (float) maxY, (float) maxZ).color(red, green, blue, alpha).endVertex();
        });
    }

    /**
     * Converts interaction box to list of triangles that represents it
     * 
     * @param box raytraceable interaction box
     * @param matrixFactory function for dynamic triangles that takes the part and the raytraced
     * entity as arguments and outputs that part's dynamically generated transformation matrix
     * 
     * @return triangle list
     */
    public static TriangleRayTraceList boxToTriangles(AxisAlignedBB box, @Nullable BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory)
    {
        List<TriangleRayTrace> triangles = Lists.newArrayList();
        getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.minZ, box.maxX, box.maxY, box.minZ, box.maxX, box.minY, box.minZ, box.minX, box.minY, box.minZ);
        getTrianglesFromQuadAndAdd(triangles, box.maxX, box.maxY, box.minZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.minY, box.maxZ, box.maxX, box.minY, box.minZ);
        getTrianglesFromQuadAndAdd(triangles, box.maxX, box.maxY, box.maxZ, box.minX, box.maxY, box.maxZ, box.minX, box.minY, box.maxZ, box.maxX, box.minY, box.maxZ);
        getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.maxZ, box.minX, box.maxY, box.minZ, box.minX, box.minY, box.minZ, box.minX, box.minY, box.maxZ);
        getTrianglesFromQuadAndAdd(triangles, box.minX, box.maxY, box.maxZ, box.maxX, box.maxY, box.maxZ, box.maxX, box.maxY, box.minZ, box.minX, box.maxY, box.minZ);
        getTrianglesFromQuadAndAdd(triangles, box.maxX, box.minY, box.maxZ, box.minX, box.minY, box.maxZ, box.minX, box.minY, box.minZ, box.maxX, box.minY, box.minZ);
        return new TriangleRayTraceList(triangles, matrixFactory);
    }

    /**
     * Version of {@link EntityRayTracer#boxToTriangles(AxisAlignedBB, BiFunction) boxToTriangles}
     * without a matrix-generating function for static interaction boxes
     * 
     * @param box raytraceable interaction box
     * 
     * @return triangle list
     */
    public static TriangleRayTraceList boxToTriangles(AxisAlignedBB box)
    {
        return boxToTriangles(box, null);
    }

    /**
     * Converts quad into a pair of triangles that represents it
     * 
     * @param triangles list of all triangles for the given raytraceable entity class
     * @param data four vertices of a quad
     */
    private static void getTrianglesFromQuadAndAdd(List<TriangleRayTrace> triangles, double... data)
    {
        int size = 3;
        // Two triangles that represent the BakedQuad
        float[] triangle1 = new float[9];
        float[] triangle2 = new float[9];

        // Corner 1
        triangle1[0] = (float) data[0];
        triangle1[1] = (float) data[1];
        triangle1[2] = (float) data[2];
        // Corner 2
        triangle1[3] = triangle2[6] = (float) data[size];
        triangle1[4] = triangle2[7] = (float) data[size + 1];
        triangle1[5] = triangle2[8] = (float) data[size + 2];
        // Corner 3
        size *= 2;
        triangle2[0] = (float) data[size];
        triangle2[1] = (float) data[size + 1];
        triangle2[2] = (float) data[size + 2];
        // Corner 4
        size *= 1.5;
        triangle1[6] = triangle2[3] = (float) data[size];
        triangle1[7] = triangle2[4] = (float) data[size + 1];
        triangle1[8] = triangle2[5] = (float) data[size + 2];

        transformTriangleAndAdd(triangle1, null, triangles);
        transformTriangleAndAdd(triangle2, null, triangles);
    }

    /**
     * Triangle used in raytracing
     */
    public static class TriangleRayTrace
    {
        private final float[] data;

        public TriangleRayTrace(float[] data)
        {
            this.data = data;
        }

        public float[] getData()
        {
            return data;
        }

        public void draw(Tessellator tessellator, BufferBuilder buffer, float red, float green, float blue, float alpha)
        {
            buffer.begin(GL11.GL_LINE_STRIP, DefaultVertexFormats.POSITION_COLOR);
            buffer.vertex(data[6], data[7], data[8]).color(red, green, blue, alpha).endVertex();
            buffer.vertex(data[0], data[1], data[2]).color(red, green, blue, alpha).endVertex();
            buffer.vertex(data[3], data[4], data[5]).color(red, green, blue, alpha).endVertex();
            tessellator.end();
        }
    }

    /**
     * Wrapper class for raytraceable triangles
     */
    public static class TriangleRayTraceList
    {
        private final List<TriangleRayTrace> triangles;
        private final BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory;

        /**
         * Constructor for static triangles
         * 
         * @param triangles raytraceable triangle list
         */
        public TriangleRayTraceList(List<TriangleRayTrace> triangles)
        {
            this(triangles, null);
        }

        /**
         * Constructor for dynamic triangles
         * 
         * @param triangles raytraceable triangle list
         * @param matrixFactory function for dynamic triangles that takes the part and the raytraced
         * entity as arguments and outputs that part's dynamically generated transformation matrix
         */
        public TriangleRayTraceList(List<TriangleRayTrace> triangles, @Nullable BiFunction<RayTracePart, Entity, Matrix4f> matrixFactory)
        {
            this.triangles = triangles;
            this.matrixFactory = matrixFactory;
        }

        /**
         * Gets list of static pre-transformed triangles, or gets a new list of dynamically transformed triangles
         * 
         * @param part rendered item-part
         * @param entity raytraced entity
         */
        public List<TriangleRayTrace> getTriangles(RayTracePart part, Entity entity)
        {
            if (matrixFactory != null)
            {
                List<TriangleRayTrace> triangles = Lists.newArrayList();
                Matrix4f matrix = matrixFactory.apply(part, entity);
                for (TriangleRayTrace triangle : this.triangles)
                {
                    triangles.add(new TriangleRayTrace(getTransformedTriangle(triangle.getData(), matrix)));
                }
                return triangles;
            }
            return this.triangles;
        }

        /**
         * Gets list of triangles directly
         */
        public List<TriangleRayTrace> getTriangles()
        {
            return triangles;
        }
    }

    /**
     * The result of a raytrace on a triangle.
     * <p>
     * This class utilizes a Möller/Trumbore intersection algorithm.
     */
    private static class RayTraceResultTriangle
    {
        private static final float EPSILON = 0.000001F;
        private final float x, y, z;
        private final RayTracePart part;
        private double distance; 

        public RayTraceResultTriangle(RayTracePart part, float x, float y, float z)
        {
            this.part = part;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vector3d getHit()
        {
            return new Vector3d(x, y, z);
        }

        public RayTracePart getPart()
        {
            return part;
        }

        public double calculateAndSaveDistance(Vector3d eyeVec)
        {
            distance = eyeVec.distanceTo(getHit());
            return distance;
        }

        public double getDistance()
        {
            return distance;
        }

        /**
         * Raytrace a triangle using a Möller/Trumbore intersection algorithm
         * 
         * @param eyes position of the eyes of the player
         * @param direction normalized direction vector scaled by reach distance that represents the player's looking direction
         * @param posEntity position of the entity being raytraced
         * @param data triangle data of a part of the entity being raytraced
         * @param part raytrace part
         * 
         * @return new instance of this class, if the ray intersect the triangle - null if the ray does not
         */
        public static RayTraceResultTriangle calculateIntercept(float[] eyes, float[] direction, Vector3d posEntity, float[] data, RayTracePart part)
        {
            float[] vec0 = {data[0] + (float) posEntity.x, data[1] + (float) posEntity.y, data[2] + (float) posEntity.z};
            float[] vec1 = {data[3] + (float) posEntity.x, data[4] + (float) posEntity.y, data[5] + (float) posEntity.z};
            float[] vec2 = {data[6] + (float) posEntity.x, data[7] + (float) posEntity.y, data[8] + (float) posEntity.z};
            float[] edge1 = new float[3];
            float[] edge2 = new float[3];
            float[] tvec = new float[3];
            float[] pvec = new float[3];
            float[] qvec = new float[3];
            float det;
            float inv_det;
            subtract(edge1, vec1, vec0);
            subtract(edge2, vec2, vec0);
            crossProduct(pvec, direction, edge2);
            det = dotProduct(edge1, pvec);
            if (det <= -EPSILON || det >= EPSILON)
            {
                inv_det = 1f / det;
                subtract(tvec, eyes, vec0);
                float u = dotProduct(tvec, pvec) * inv_det;
                if (u >= 0 && u <= 1)
                {
                    crossProduct(qvec, tvec, edge1);
                    float v = dotProduct(direction, qvec) * inv_det;
                    if (v >= 0 && u + v <= 1 && inv_det * dotProduct(edge2, qvec) > EPSILON)
                    {
                        return new RayTraceResultTriangle(part, edge1[0] * u + edge2[0] * v + vec0[0], edge1[1] * u + edge2[1] * v + vec0[1], edge1[2] * u + edge2[2] * v + vec0[2]);
                    }
                }
            }
            return null;
        }

        private static void crossProduct(float[] result, float[] v1, float[] v2)
        {
            result[0] = v1[1] * v2[2] - v1[2] * v2[1];
            result[1] = v1[2] * v2[0] - v1[0] * v2[2];
            result[2] = v1[0] * v2[1] - v1[1] * v2[0];
        }

        private static float dotProduct(float[] v1, float[] v2)
        {
            return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        }

        private static void subtract(float[] result, float[] v1, float[] v2)
        {
            result[0] = v1[0] - v2[0];
            result[1] = v1[1] - v2[1];
            result[2] = v1[2] - v2[2];
        }
    }

    /**
     * A raytrace part representing either an item or a box
     */
    public static class RayTracePart
    {
        private final ItemStack partStack;
        private final AxisAlignedBB partBox;
        private final ISpecialModel model;
        private final RayTraceFunction continuousInteraction;

        public RayTracePart(ItemStack partStack, @Nullable RayTraceFunction continuousInteraction)
        {
            this(partStack, null, null, continuousInteraction);
        }

        public RayTracePart(AxisAlignedBB partBox, @Nullable RayTraceFunction continuousInteraction)
        {
            this(ItemStack.EMPTY, partBox, null, continuousInteraction);
        }

        public RayTracePart(ISpecialModel model, @Nullable RayTraceFunction continuousInteraction)
        {
            this(ItemStack.EMPTY, null, model, continuousInteraction);
        }

        public RayTracePart(AxisAlignedBB partBox)
        {
            this(ItemStack.EMPTY, partBox, null, null);
        }

        private RayTracePart(ItemStack partStack, @Nullable AxisAlignedBB partBox, @Nullable ISpecialModel model, @Nullable RayTraceFunction continuousInteraction)
        {
            this.partStack = partStack;
            this.partBox = partBox;
            this.model = model;
            this.continuousInteraction = continuousInteraction;
        }

        public ItemStack getStack()
        {
            return this.partStack;
        }

        @Nullable
        public AxisAlignedBB getBox()
        {
            return this.partBox;
        }

        @Nullable
        public ISpecialModel getModel()
        {
            return this.model;
        }

        public RayTraceFunction getContinuousInteraction()
        {
            return this.continuousInteraction;
        }
    }

    /**
     * The result of a rotated raytrace
     */
    public static class RayTraceResultRotated extends EntityRayTraceResult
    {
        private final RayTracePart partHit;
        private final double distanceToEyes;
        private final boolean rightClick;

        private RayTraceResultRotated(Entity entityHit, Vector3d hitVec, double distanceToEyes, RayTracePart partHit, boolean rightClick)
        {
            super(entityHit, hitVec);
            this.distanceToEyes = distanceToEyes;
            this.partHit = partHit;
            this.rightClick = rightClick;
        }

        public RayTracePart getPartHit()
        {
            return this.partHit;
        }

        public double getDistanceToEyes()
        {
            return this.distanceToEyes;
        }

        public boolean isRightClick()
        {
            return this.rightClick;
        }

        public Hand performContinuousInteraction()
        {
            return this.partHit.getContinuousInteraction() == null ? null : this.partHit.getContinuousInteraction().apply(EntityRayTracer.instance(), this, Minecraft.getInstance().player);
        }

        public <R> boolean equalsContinuousInteraction(RayTraceFunction function)
        {
            return function.equals(this.partHit.getContinuousInteraction());
        }
    }

    /**
     * Interface that allows entities to be raytraceable
     * <p>
     * <strong>Note:</strong>
     * <ul>
     *     <li>This must be implemented by all entities that raytraces are to be performed on.</li>
     *     <li>Only classes that extend {@link net.minecraft.entity.Entity Entity} should implement this interface.</li>
     * </ul>
     */
    public interface IEntityRayTraceable
    {
        /**
         * Called when either an item part is clicked or an entity-specific interaction box is clicked.
         * <p>
         * Default behavior is to perform a general interaction with the entity when a part is clicked.
         * 
         * @param result item part hit - null if none was hit
         * @param rightClick whether the click was a right-click or a left-click
         * 
         * @return whether or not the click that initiated the hit should be canceled
         */
        @OnlyIn(Dist.CLIENT)
        default boolean processHit(RayTraceResultRotated result, boolean rightClick)
        {
            if(result.getPartHit().getModel() == SpecialModels.KEY_HOLE)
            {
                PacketHandler.instance.sendToServer(new MessageInteractKey((Entity) this));
                return true;
            }

            Minecraft mc = Minecraft.getInstance();
            boolean isContinuous = result.partHit.getContinuousInteraction() != null;
            if(isContinuous || !(mc.hitResult != null && mc.hitResult.getType() == Type.ENTITY && ((EntityRayTraceResult)mc.hitResult).getEntity() == this))
            {
                PlayerEntity player = mc.player;
                boolean notRiding = player.getVehicle() != this;
                if(!rightClick && notRiding)
                {
                    mc.gameMode.attack(player, (Entity) this);
                    return true;
                }
                if(result.getPartHit().model != null || result.getPartHit().partStack != null)
                {
                    if(notRiding)
                    {
                        if(player.isCrouching() && !player.isSpectator())
                        {
                            PacketHandler.instance.sendToServer(new MessagePickupVehicle((Entity) this));
                            return true;
                        }
                        if(!isContinuous)
                        {
                            interactWithEntity(this, result);
                        }
                    }
                    return notRiding;
                }
            }
            return false;
        }

        /**
         * Mapping of static interaction boxes for the entity to lists of triangles that represent them
         * 
         * @return box to triangle map
         */
        @OnlyIn(Dist.CLIENT)
        default Map<RayTracePart, TriangleRayTraceList> getStaticInteractionBoxMap()
        {
            return Maps.newHashMap();
        }

        /**
         * Mapping of dynamic interaction boxes for the entity to lists of triangles that represent them
         * 
         * @return box to triangle map
         */
        @OnlyIn(Dist.CLIENT)
        default Map<RayTracePart, TriangleRayTraceList> getDynamicInteractionBoxMap()
        {
            return Maps.newHashMap();
        }

        /**
         * List of all currently applicable interaction boxes for the entity
         * 
         * @return box list - if null, all box are assumed to be applicable
         */
        @OnlyIn(Dist.CLIENT)
        default List<RayTracePart> getApplicableInteractionBoxes()
        {
            return Collections.emptyList();
        }

        /**
         * List of all currently non-applicable item parts for the entity
         * 
         * @return part list - if null, all parts are assumed to be applicable
         */
        @Nullable
        @OnlyIn(Dist.CLIENT)
        default List<RayTracePart> getNonApplicableParts()
        {
            return null;
        }
    }

    public interface IRayTraceTransforms
    {
        void load(EntityRayTracer tracer, List<MatrixTransformation> transforms, HashMap<RayTracePart, List<MatrixTransformation>> parts);
    }
}
