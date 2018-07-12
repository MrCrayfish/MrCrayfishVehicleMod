package com.mrcrayfish.vehicle.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nullable;
import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.lwjgl.opengl.GL11;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mrcrayfish.vehicle.entity.vehicle.*;
import com.mrcrayfish.vehicle.init.ModItems;

/**
 * Author: Phylogeny
 * <p>
 * This class allows precise ratraces to be performed on the rendered model item parts, as well as on additional interaction boxes, of entities.
 */
@EventBusSubscriber
public class EntityRaytracer
{
    /**
     * Maps raytraceable entities to maps, which map matrix transformations that correspond to the static GL operation performed on them during rendering
     */
    private static final Map<Class<? extends Entity>, Map<ItemStack, List<MatrixTransformation>>> entityRaytraceParts = Maps.<Class<? extends Entity>, Map<ItemStack, List<MatrixTransformation>>>newHashMap();

    /**
     * Maps raytraceable entities to maps, which map rendered model item parts to static versions of the triangles that comprise the faces of their BakedQuads
     */
    public static final Map<Class<? extends IEntityRaytraceBoxProvider>, Map<ItemStack, List<TriangleRayTrace>>> entityRaytraceTriangles = Maps.<Class<? extends IEntityRaytraceBoxProvider>, Map<ItemStack, List<TriangleRayTrace>>>newHashMap();

    /**
     * Nearest common superclass shared by all raytraceable entity classes
     */
    private static Class<? extends Entity> entityRaytraceSuperclass;

    /**
     * NBT key for a name string tag that parts stacks with NBT tags will have.
     * <p>
     * <strong>Example:</strong> <code>partStack.getTagCompound().getString(EntityRaytracer.PART_NAME)</code>
     */
    public static final String PART_NAME = "nameRaytrace";

    static
    {
        /**
         * All GL operation performed on each item part during rendering must be accounted for by performing
         * the same matrix transformations on the triangles that will comprise the faces their BakedQuads
         */

        // Aluminum boat
        HashMap<ItemStack, List<MatrixTransformation>> aluminumBoatParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.ALUMINUM_BOAT_BODY, aluminumBoatParts,
                MatrixTransformation.createScale(1.1),
                MatrixTransformation.createTranslation(0, 0.5, 0.2));
        entityRaytraceParts.put(EntityAluminumBoat.class, aluminumBoatParts);

        // ATV
        List<MatrixTransformation> atvTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        atvTransformGlobal.add(MatrixTransformation.createScale(1.25));
        atvTransformGlobal.add(MatrixTransformation.createTranslation(0, -0.03125, 0.2));
        HashMap<ItemStack, List<MatrixTransformation>> atvParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.ATV_BODY, atvParts, atvTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.7109375, 0));
        createTranformListForPart(ModItems.ATV_HANDLE_BAR, atvParts, atvTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.9734375, 0.25),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createTranslation(0, 0.02, 0));
        entityRaytraceParts.put(EntityATV.class, atvParts);

        // Bumper car
        List<MatrixTransformation> bumperCarTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        bumperCarTransformGlobal.add(MatrixTransformation.createTranslation(0, 0, 0.4));
        bumperCarTransformGlobal.add(MatrixTransformation.createScale(1.2));
        bumperCarTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.5, 0));
        HashMap<ItemStack, List<MatrixTransformation>> bumperCarParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.BUMPER_CAR_BODY, bumperCarParts, bumperCarTransformGlobal);
        createTranformListForPart(ModItems.GO_KART_STEERING_WHEEL, bumperCarParts, bumperCarTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.2, 0),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createTranslation(0, -0.02, 0),
                MatrixTransformation.createScale(0.9));
        entityRaytraceParts.put(EntityBumperCar.class, bumperCarParts);

        // Dune buggy
        List<MatrixTransformation> duneBuggyTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        duneBuggyTransformGlobal.add(MatrixTransformation.createScale(1.3));
        duneBuggyTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.0, 0.165));
        HashMap<ItemStack, List<MatrixTransformation>> duneBuggyParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.DUNE_BUGGY_BODY, duneBuggyParts, duneBuggyTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.505, 0));
        createTranformListForPart(ModItems.DUNE_BUGGY_HANDLE_BAR, duneBuggyParts, duneBuggyTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.5, -0.0046875));
        entityRaytraceParts.put(EntityDuneBuggy.class, duneBuggyParts);

        // Go kart
        List<MatrixTransformation> goKartTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        goKartTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.5625, 0));
        HashMap<ItemStack, List<MatrixTransformation>> goKartParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.GO_KART_BODY, goKartParts, goKartTransformGlobal);
        createTranformListForPart(ModItems.GO_KART_STEERING_WHEEL, goKartParts, goKartTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.09, 0.49),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createTranslation(0, -0.02, 0),
                MatrixTransformation.createScale(0.9));
        List<MatrixTransformation> goKartEngineTransforms = Lists.<MatrixTransformation>newArrayList();
        goKartEngineTransforms.add(MatrixTransformation.createTranslation(0, -0.34375, 0));
        createEngineTransforms(0, 7.5, -9, 180, 1.2, goKartEngineTransforms);
        goKartTransformGlobal.addAll(goKartEngineTransforms);
        createTranformListForPart(ModItems.ENGINE, goKartParts, goKartTransformGlobal);
        entityRaytraceParts.put(EntityGoKart.class, goKartParts);

        // Jet ski
        List<MatrixTransformation> jetSkiTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        jetSkiTransformGlobal.add(MatrixTransformation.createScale(1.25));
        jetSkiTransformGlobal.add(MatrixTransformation.createTranslation(0, -0.03125, 0.2));
        HashMap<ItemStack, List<MatrixTransformation>> jetSkiParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.JET_SKI_BODY, jetSkiParts, jetSkiTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.7109375, 0));
        createTranformListForPart(ModItems.ATV_HANDLE_BAR, jetSkiParts, jetSkiTransformGlobal,
                MatrixTransformation.createTranslation(0, 1.0734375, 0.25),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createTranslation(0, 0.02, 0));
        entityRaytraceParts.put(EntityJetSki.class, jetSkiParts);

        // Lawn mower
        List<MatrixTransformation> lawnMowerTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        lawnMowerTransformGlobal.add(MatrixTransformation.createTranslation(0, 0, 0.65));
        lawnMowerTransformGlobal.add(MatrixTransformation.createScale(1.25));
        lawnMowerTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.5625, 0));
        HashMap<ItemStack, List<MatrixTransformation>> lawnMowerParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.LAWN_MOWER_BODY, lawnMowerParts, lawnMowerTransformGlobal);
        createTranformListForPart(ModItems.GO_KART_STEERING_WHEEL, lawnMowerParts, lawnMowerTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.4, -0.15),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createScale(0.9));
        entityRaytraceParts.put(EntityLawnMower.class, lawnMowerParts);

        // Mini bike
        List<MatrixTransformation> miniBikeTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        miniBikeTransformGlobal.add(MatrixTransformation.createScale(1.05));
        miniBikeTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.15, 0.15));
        HashMap<ItemStack, List<MatrixTransformation>> miniBikeParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.MINI_BIKE_BODY, miniBikeParts, miniBikeTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.5, 0));
        createTranformListForPart(ModItems.MINI_BIKE_HANDLE_BAR, miniBikeParts, miniBikeTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.5, 0));
        List<MatrixTransformation> miniBikeEngineTransforms = Lists.<MatrixTransformation>newArrayList();
        miniBikeEngineTransforms.add(MatrixTransformation.createTranslation(0, 0.10625, 0));
        createEngineTransforms(0, 7.25, 3, 180, 1, miniBikeEngineTransforms);
        miniBikeTransformGlobal.addAll(miniBikeEngineTransforms);
        createTranformListForPart(ModItems.ENGINE, miniBikeParts, miniBikeTransformGlobal);
        entityRaytraceParts.put(EntityMiniBike.class, miniBikeParts);

        // Moped
        List<MatrixTransformation> mopedTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        mopedTransformGlobal.add(MatrixTransformation.createScale(1.2));
        mopedTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.6, 0.125));
        HashMap<ItemStack, List<MatrixTransformation>> mopedParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.MOPED_BODY, mopedParts, mopedTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.0625, 0));
        createTranformListForPart(ModItems.MOPED_HANDLE_BAR, mopedParts, mopedTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.835, 0.525),
                MatrixTransformation.createScale(0.8));
        createTranformListForPart(ModItems.MOPED_MUD_GUARD, mopedParts, mopedTransformGlobal,
                MatrixTransformation.createTranslation(0, -0.12, 0.785),
                MatrixTransformation.createRotation(-22.5, 1, 0, 0),
                MatrixTransformation.createScale(0.9));
        entityRaytraceParts.put(EntityMoped.class, mopedParts);

        // Shopping cart
        HashMap<ItemStack, List<MatrixTransformation>> cartParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.SHOPPING_CART_BODY, cartParts,
                MatrixTransformation.createScale(1.05),
                MatrixTransformation.createTranslation(0, 0.53, 0.165));
        entityRaytraceParts.put(EntityShoppingCart.class, cartParts);

        // Smart car
        List<MatrixTransformation> smartCarTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        smartCarTransformGlobal.add(MatrixTransformation.createTranslation(0, 0, 0.2));
        smartCarTransformGlobal.add(MatrixTransformation.createScale(1.25));
        smartCarTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.6, 0));
        HashMap<ItemStack, List<MatrixTransformation>> smartCarParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.SMART_CAR_BODY, smartCarParts, smartCarTransformGlobal);
        createTranformListForPart(ModItems.GO_KART_STEERING_WHEEL, smartCarParts, smartCarTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.2, 0.3),
                MatrixTransformation.createRotation(-67.5, 1, 0, 0),
                MatrixTransformation.createTranslation(0, -0.02, 0),
                MatrixTransformation.createScale(0.9));
        entityRaytraceParts.put(EntitySmartCar.class, smartCarParts);

        // Speed boat
        List<MatrixTransformation> speedBoatTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        speedBoatTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.2421875, 0.6875));
        HashMap<ItemStack, List<MatrixTransformation>> speedBoatParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.SPEED_BOAT_BODY, speedBoatParts, speedBoatTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.4375, 0));
        createTranformListForPart(ModItems.GO_KART_STEERING_WHEEL, speedBoatParts, speedBoatTransformGlobal,
                MatrixTransformation.createTranslation(0, 0.65, -0.125),
                MatrixTransformation.createRotation(-45, 1, 0, 0),
                MatrixTransformation.createTranslation(0, 0.02, 0));
        entityRaytraceParts.put(EntitySpeedBoat.class, speedBoatParts);

        // Sports plane
        List<MatrixTransformation> sportsPlaneTransformGlobal = Lists.<MatrixTransformation>newArrayList();
        sportsPlaneTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.6875, -0.5));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createScale(1.8));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createTranslation(0, 0.5, 0));
        HashMap<ItemStack, List<MatrixTransformation>> sportsPlaneParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
        createTranformListForPart(ModItems.SPORTS_PLANE_BODY, sportsPlaneParts, sportsPlaneTransformGlobal);
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_WING, "wingRight"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0, -0.1875, 0.5),
                MatrixTransformation.createRotation(180, 0, 0, 1),
                MatrixTransformation.createTranslation(0.875, 0.0625, 0),
                MatrixTransformation.createRotation(5, 1, 0, 0));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_WING, "wingLeft"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.875, -0.1875, 0.5),
                MatrixTransformation.createRotation(-5, 1, 0, 0));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createTranslation(0, -0.5, 0));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createScale(0.85));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_WHEEL_COVER, "wheelCoverFront"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0, -0.1875, 1.5));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_LEG, "legFront"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0, -0.1875, 1.5));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_WHEEL_COVER, "wheelCoverRight"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(-0.46875, -0.1875, 0.125));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_LEG, "legRight"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(-0.46875, -0.1875, 0.125),
                MatrixTransformation.createRotation(-100, 0, 1, 0));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_WHEEL_COVER, "wheelCoverLeft"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.46875, -0.1875, 0.125));
        createTranformListForPart(getNamedPartStack(ModItems.SPORTS_PLANE_LEG, "legLeft"), sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.46875, -0.1875, 0.125),
                MatrixTransformation.createRotation(100, 0, 1, 0));
        entityRaytraceParts.put(EntitySportsPlane.class, sportsPlaneParts);

        if(Loader.isModLoaded("cfm"))
        {
            // Bath
            HashMap<ItemStack, List<MatrixTransformation>> bathParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
            createTranformListForPart(Item.getByNameOrId("cfm:bath_bottom"), bathParts,
                    MatrixTransformation.createTranslation(0, -0.03125, -0.25),
                    MatrixTransformation.createRotation(90, 0, 1, 0),
                    MatrixTransformation.createTranslation(0, 0.5, 0));
            entityRaytraceParts.put(EntityBath.class, bathParts);

            // Couch
            HashMap<ItemStack, List<MatrixTransformation>> couchParts = Maps.<ItemStack, List<MatrixTransformation>>newHashMap();
            createTranformListForPart(Item.getByNameOrId("cfm:couch_jeb"), couchParts,
                    MatrixTransformation.createTranslation(0, -0.03125, 0.1),
                    MatrixTransformation.createRotation(90, 0, 1, 0),
                    MatrixTransformation.createTranslation(0, 0.7109375, 0));
            entityRaytraceParts.put(EntityCouch.class, couchParts);
        }
    }

    /**
     * Creates a part stack with an NBT tag containing a string name of the part
     * 
     * @param part the rendered item part
     * @param name name of the part
     * 
     * @return the part stack
     */
    private static ItemStack getNamedPartStack(Item part, String name)
    {
        ItemStack partStack = new ItemStack(part);
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString(PART_NAME, name);
        partStack.setTagCompound(nbt);
        return partStack;
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered engine. Arguments passed here should be the same as
     * those passed to {@link com.mrcrayfish.vehicle.client.render.RenderVehicle#setEnginePosition createTranformListForPart} by the entity's renderer.
     * 
     * @param x engine's x position
     * @param y engine's y position
     * @param z engine's z position
     * @param rotation engine's rotation yaw
     * @param scale engine's scale
     * @param transforms list that engine transforms are added to
     */
    private static void createEngineTransforms(double x, double y, double z, double rotation, double scale, List<MatrixTransformation> transforms)
    {
        transforms.add(MatrixTransformation.createTranslation(x * 0.0625, y * 0.0625, z * 0.0625));
        transforms.add(MatrixTransformation.createTranslation(0, -0.5, 0));
        transforms.add(MatrixTransformation.createScale(scale));
        transforms.add(MatrixTransformation.createTranslation(0, 0.5, 0));
        transforms.add(MatrixTransformation.createRotation(rotation, 0, 1, 0));
    }

    /**
     * Adds all global and part-specific transforms for an item part to the list of transforms for the given entity
     * 
     * @param part the rendered item part in a stack
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    private static void createTranformListForPart(ItemStack part, HashMap<ItemStack, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        List<MatrixTransformation> transformsAll = Lists.<MatrixTransformation>newArrayList();
        transformsAll.addAll(transformsGlobal);
        for (MatrixTransformation transform : transforms)
        {
            transformsAll.add(transform);
        }
        parts.put(part, transformsAll);
    }

    /**
     * Version of {@link EntityRaytracer#createTranformListForPart createTranformListForPart} that accepts the part as an item, rather than a stack
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    private static void createTranformListForPart(Item part, HashMap<ItemStack, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTranformListForPart(new ItemStack(part), parts, transformsGlobal, transforms);
    }

    /**
     * Version of {@link EntityRaytracer#createTranformListForPart(Item, HashMap, List, MatrixTransformation[]) createTranformListForPart} without global transform list
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transforms part-specific transforms for the given part 
     */
    private static void createTranformListForPart(Item part, HashMap<ItemStack, List<MatrixTransformation>> parts, MatrixTransformation... transforms)
    {
        createTranformListForPart(part, parts, Lists.<MatrixTransformation>newArrayList(), transforms);
    }

    /**
     * Generates a list of transformed triangles that represent each IBakedModel of each rendered item part for
     * each raytraceable entity class, and finds the nearest superclass in common between those classes.
     * <p>
     * 
     * <strong>Note:</strong> this must be called on the client during the {@link net.minecraftforge.fml.common.event.FMLInitializationEvent init} phase.
     */
    public static void init()
    {
        for (Entry<Class<? extends Entity>, Map<ItemStack, List<MatrixTransformation>>> entry : entityRaytraceParts.entrySet())
        {
            Map<ItemStack, List<TriangleRayTrace>> partTriangles = Maps.<ItemStack, List<TriangleRayTrace>>newHashMap();
            for (Entry<ItemStack, List<MatrixTransformation>> entryPart : entry.getValue().entrySet())
            {
                ItemStack part = entryPart.getKey();
                // Get model
                IBakedModel model = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(part, null, Minecraft.getMinecraft().player);
                // Generate part-specific matrix
                Matrix4d matrix = new Matrix4d();
                matrix.setIdentity();
                for (MatrixTransformation tranform : entryPart.getValue())
                {
                    tranform.transform(matrix);
                }
                finalizeMatrix(matrix);
                ArrayList<TriangleRayTrace> triangles = Lists.<TriangleRayTrace>newArrayList();
                try
                {
                    // Generate triangles for all faceless and faced quads
                    generateTriangles(model.getQuads(null, null, 0L), matrix, triangles);
                    for (EnumFacing facing : EnumFacing.values())
                    {
                        generateTriangles(model.getQuads(null, facing, 0L), matrix, triangles);
                    }
                }
                catch (Exception e) {}
                partTriangles.put(part, triangles);
            }
            entityRaytraceTriangles.put((Class<? extends IEntityRaytraceBoxProvider>) entry.getKey(), partTriangles);
        }
        // Find nearest common superclass
        for (Class<? extends Entity> raytraceClass : entityRaytraceParts.keySet())
        {
            if (entityRaytraceSuperclass != null)
            {
                Class<?> nearestSuperclass = raytraceClass;
                do
                {
                    nearestSuperclass = nearestSuperclass.getSuperclass();
                    if (nearestSuperclass == Entity.class)
                    {
                        break;
                    }
                }
                while (!nearestSuperclass.isAssignableFrom(entityRaytraceSuperclass));
                entityRaytraceSuperclass = (Class<? extends Entity>) nearestSuperclass;
            }
            else
            {
                entityRaytraceSuperclass = raytraceClass;
            }
        }
    }

    /**
     * Converts BakedQuad into pairs of transformed triangles that represent them
     * 
     * @param list list of BakedQuad
     * @param matrix part-specific matrix mirroring the GL operation performed on that part during rendering
     * @param triangles list of all triangles for the given raytraceable entity class
     */
    private static void generateTriangles(List<BakedQuad> list, Matrix4d matrix, List<TriangleRayTrace> triangles)
    {
        for (int i = 0; i < list.size(); i++)
        {
            BakedQuad quad = list.get(i);
            int size = quad.getFormat().getIntegerSize();
            int[] data = quad.getVertexData();
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

            transformAndAdd(triangle1, matrix, triangles);
            transformAndAdd(triangle2, matrix, triangles);
        }
    }

    /**
     * Transforms a triangle by the part-specific matrix and adds it to the list of all triangles for the given raytraceable entity class
     * 
     * @param triangle array of the three vertices that comprise the triangle
     * @param matrix part-specific matrix mirroring the GL operation performed on that part during rendering
     * @param triangles list of all triangles for the given raytraceable entity class
     */
    private static void transformAndAdd(float[] triangle, Matrix4d matrix, List<TriangleRayTrace> triangles)
    {
        for (int i = 0; i < 9; i += 3)
        {
            Vector4d vec = new Vector4d(triangle[i], triangle[i + 1], triangle[i + 2], 1);
            matrix.transform(vec);
            triangle[i] = (float) vec.x;
            triangle[i + 1] = (float) vec.y;
            triangle[i + 2] = (float) vec.z;
        }
        triangles.add(new TriangleRayTrace(triangle));
    }

    /**
     * Adds the final required translation to the part-specific matrix
     * 
     * @param matrix part-specific matrix mirroring the GL operation performed on that part during rendering
     */
    private static void finalizeMatrix(Matrix4d matrix)
    {
        MatrixTransformation.createTranslation(-0.5, -0.5, -0.5).transform(matrix);
    }

    /**
     * Three matrix transformations that correspond to the three supported GL operations that might be performed on a rendered item part
     */
    private static enum MatrixTransformationType
    {
        TRANSLATION, ROTATION, SCALE;
    }

    /**
     * Matrix transformation that corresponds to one of the three supported GL operations that might be performed on a rendered item part
     */
    private static class MatrixTransformation
    {
        private final MatrixTransformationType type;
        private double x, y, z, angle;

        public MatrixTransformation(MatrixTransformationType type, double x, double y, double z)
        {
            this.type = type;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public MatrixTransformation(MatrixTransformationType type, double x, double y, double z, double angle)
        {
            this(type, x, y, z);
            this.angle = angle;
        }

        public static MatrixTransformation createTranslation(double x, double y, double z)
        {
            return new MatrixTransformation(MatrixTransformationType.TRANSLATION, x, y, z);
        }

        public static MatrixTransformation createRotation(double angle, double x, double y, double z)
        {
            return new MatrixTransformation(MatrixTransformationType.ROTATION, x, y, z, angle);
        }

        public static MatrixTransformation createScale(double x, double y, double z)
        {
            return new MatrixTransformation(MatrixTransformationType.SCALE, x, y, z);
        }

        public static MatrixTransformation createScale(double xyz)
        {
            return new MatrixTransformation(MatrixTransformationType.SCALE, xyz, xyz, xyz);
        }

        /**
         * Applies the matrix transformation that this class represents to the passed matrix
         * 
         * @param matrix matrix to apply this transformation to
         */
        public void transform(Matrix4d matrix)
        {
            Matrix4d temp = new Matrix4d();
            switch (type)
            {
                case ROTATION:      temp.set(new AxisAngle4d(x, y, z, (float) Math.toRadians(angle)));
                                    matrix.mul(temp);
                                    break;
                case TRANSLATION:   temp.set(new Vector3d(x, y, z));
                                    matrix.mul(temp);
                                    break;
                case SCALE:         Vector3d scaleVec = new Vector3d(x, y, z);
                                    temp.setIdentity();
                                    temp.m00 = scaleVec.x;
                                    temp.m11 = scaleVec.y;
                                    temp.m22 = scaleVec.z;
                                    matrix.mul(temp);
            }
        }
    }

    /**
     * Performs a general interaction with an entity
     */
    public static void interactWithEntity(IEntityRaytraceBoxProvider entity)
    {
        Minecraft.getMinecraft().playerController.interactWithEntity(Minecraft.getMinecraft().player, (Entity) entity, EnumHand.MAIN_HAND);
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of all raytraceable entities within reach of the player upon right-click,
     * and cancels it if the clicked raytraceable entity returns true from {@link IEntityRaytraceBoxProvider#processHit processHit}
     */
    @SubscribeEvent
    public static void raytraceEntities(MouseEvent event)
    {
        // Return if the mouse is not being right clicked or if the mouse is being released
        if (event.getButton() != 1 || !event.isButtonstate())
        {
            return;
        }
        float reach = Minecraft.getMinecraft().playerController.getBlockReachDistance();
        Vec3d eyeVec = Minecraft.getMinecraft().player.getPositionEyes(1);
        Vec3d forwardVec = eyeVec.add(Minecraft.getMinecraft().player.getLook(1).scale(reach));
        AxisAlignedBB box = new AxisAlignedBB(eyeVec, eyeVec).grow(reach);
        RayTraceResultRotated lookObject = null;
        double distanceShortest = Double.MAX_VALUE;
        // Perform raytrace on all raytraceable entities within reach of the player
        for (Entity entity : Minecraft.getMinecraft().world.getEntitiesWithinAABB(entityRaytraceSuperclass, box))
        {
            if (entityRaytraceParts.keySet().contains(entity.getClass()))
            {
                RayTraceResultRotated lookObjectPutative = rayTraceEntityRotated((IEntityRaytraceBoxProvider) entity, eyeVec, forwardVec, reach);
                if (lookObjectPutative != null)
                {
                    double distance = lookObjectPutative.getDistanceToEyes();
                    if (distance < distanceShortest)
                    {
                        lookObject = lookObjectPutative;
                        distanceShortest = distance;
                    }
                }
            }
        }
        if (lookObject != null)
        {
            double eyeDistance = lookObject.getDistanceToEyes();
            if (eyeDistance <= reach)
            {
                Vec3d hit = forwardVec;
                RayTraceResult lookObjectMC = Minecraft.getMinecraft().objectMouseOver;
                // if the hit entity is a raytraceable entity, process the hit regardless of what MC thinks the player is looking at
                boolean bypass = entityRaytraceParts.keySet().contains(lookObject.entityHit.getClass());
                if (!bypass && lookObjectMC != null && lookObjectMC.typeOfHit != Type.MISS)
                {
                    // Set hit to what MC thinks the player is looking at if the player is not looking at the hit entity
                    if (lookObjectMC.typeOfHit == Type.ENTITY && lookObjectMC.entityHit == lookObject.entityHit)
                    {
                        bypass = true;
                    }
                    else
                    {
                        hit = lookObjectMC.hitVec;
                    }
                }
                // If not bypassed, process the hit only if it is closer to the player's eyes than what MC thinks the player is looking
                if (bypass || eyeDistance < hit.distanceTo(eyeVec))
                {
                    if (((IEntityRaytraceBoxProvider) lookObject.entityHit).processHit(lookObject.getPartHit(), lookObject.getBoxHit()))
                    {
                        // Cancel right-click
                        event.setCanceled(true);
                    }
                }
            }
        }
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of raytraceable entity
     */
    @Nullable
    public static RayTraceResultRotated rayTraceEntityRotated(IEntityRaytraceBoxProvider boxProvider, Vec3d eyeVec, Vec3d forwardVec, double reach)
    {
        Entity entity = (Entity) boxProvider;
        Vec3d pos = entity.getPositionVector();
        double angle = Math.toRadians(-entity.rotationYaw);

        // Rotate the raytrace vectors in the opposite direction as the entity's rotation yaw
        Vec3d eyeVecRotated = rotateVecXZ(eyeVec, angle, pos);
        Vec3d forwardVecRotated = rotateVecXZ(forwardVec, angle, pos);

        RayTraceResult lookObject = null;
        AxisAlignedBB boxHit = null;
        double distanceShortest = Double.MAX_VALUE;
        RayTraceResult lookObjectPutative;
        // Perform raytrace on the entity's interaction boxes
        for (AxisAlignedBB box : boxProvider.getInteractionBoxes())
        {
            lookObjectPutative = box.offset(pos).calculateIntercept(eyeVecRotated, forwardVecRotated);
            if (lookObjectPutative != null)
            {
                double distance = eyeVecRotated.distanceTo(lookObjectPutative.hitVec);
                if (distance < distanceShortest)
                {
                    lookObject = lookObjectPutative;
                    boxHit = box;
                    distanceShortest = distance;
                }
            }
        }
        RayTraceResultTriangle lookObject2 = null;
        RayTraceResultTriangle lookObjectPutative2;
        float[] eyes = new float[]{(float) eyeVecRotated.x, (float) eyeVecRotated.y, (float) eyeVecRotated.z};
        Vec3d look = forwardVecRotated.subtract(eyeVecRotated).normalize().scale(reach);
        float[] direction = new float[]{(float) look.x, (float) look.y, (float) look.z};
        // Perform raytrace on the triangles of the entity's parts
        for (Entry<ItemStack, List<TriangleRayTrace>> entry : entityRaytraceTriangles.get(entity.getClass()).entrySet())
        {
            for (TriangleRayTrace triangle : entry.getValue())
            {
                lookObjectPutative2 = RayTraceResultTriangle.calculateIntercept(eyes, direction, pos, triangle, entry.getKey());
                if (lookObjectPutative2 != null)
                {
                    double distance = eyeVecRotated.distanceTo(lookObjectPutative2.getHit());
                    if (distance < distanceShortest)
                    {
                        // Invalidate the interaction box hit if the part hit is closer to the player's eyes
                        lookObject = null;
                        lookObject2 = lookObjectPutative2;
                        distanceShortest = distance;
                    }
                }
            }
        }
        // Return the result object of hit with hit vector rotated back in the same direction as the entity's rotation yaw, or null it no hit occurred
        if (lookObject == null)
        {
            return lookObject2 == null ? null : new RayTraceResultRotated(entity, rotateVecXZ(lookObject2.getHit(), -angle, pos), distanceShortest, lookObject2.getPart());
        }
        return new RayTraceResultRotated(entity, rotateVecXZ(lookObject.hitVec, -angle, pos), distanceShortest, boxHit);
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
    private static Vec3d rotateVecXZ(Vec3d vec, double angle, Vec3d rotationPoint)
    {
        double x = rotationPoint.x + Math.cos(angle) * (vec.x - rotationPoint.x) - Math.sin(angle) * (vec.z - rotationPoint.z);
        double z = rotationPoint.z + Math.sin(angle) * (vec.x - rotationPoint.x) + Math.cos(angle) * (vec.z - rotationPoint.z);
        return new Vec3d(x, vec.y, z);
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
     * @param entity raytraceable entity
     * @param x entity's x position
     * @param y entity's y position
     * @param z entity's z position
     * @param yaw entity's rotation yaw
     */
    public static void renderRaytraceElements(IEntityRaytraceBoxProvider entity, double x, double y, double z, float yaw)
    {
        //Debug: set true to render raytrace triangles/boxes
        if (true) //TODO keep above comments, but set this to false and delete this comment before release
        {
            GlStateManager.pushMatrix();
            {
                GlStateManager.translate(x, y, z);
                GlStateManager.rotate(-yaw, 0, 1, 0);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.glLineWidth(2.0F);
                GlStateManager.disableTexture2D();
                GlStateManager.disableLighting();
                Tessellator tessellator = Tessellator.getInstance();
                BufferBuilder buffer = tessellator.getBuffer();
                for (List<TriangleRayTrace> partTriangles : EntityRaytracer.entityRaytraceTriangles.get(entity.getClass()).values())
                {
                    for (TriangleRayTrace triangle : partTriangles)
                    {
                        triangle.draw(tessellator, buffer, 1, 0, 0, 0.5F);
                    }
                }
                entity.drawInteractionBoxes();
                GlStateManager.enableLighting();
                GlStateManager.enableTexture2D();
                GlStateManager.disableBlend();
            }
            GlStateManager.popMatrix();
        }
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
            buffer.pos(data[6], data[7], data[8]).color(red, green, blue, alpha).endVertex();
            buffer.pos(data[0], data[1], data[2]).color(red, green, blue, alpha).endVertex();
            buffer.pos(data[3], data[4], data[5]).color(red, green, blue, alpha).endVertex();
            tessellator.draw();
        }
    }

    /**
     * The result of a raytrace on a triangle.
     * <p>
     * This class utilizes a Möller–Trumbore intersection algorithm.
     */
    private static class RayTraceResultTriangle
    {
        private static final float EPSILON = 0.000001F;
        private float x, y, z;
        private ItemStack part;

        public RayTraceResultTriangle(ItemStack part, float x, float y, float z)
        {
            this.part = part;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3d getHit()
        {
            return new Vec3d(x, y, z);
        }

        public ItemStack getPart()
        {
            return part;
        }

        /**
         * Raytrace a triangle using a Möller–Trumbore intersection algorithm
         * 
         * @param eyes position of the eyes of the player
         * @param direction normalized direction vector scaled by reach distance that represents the player's looking direction
         * @param posEntity position of the entity being raytraced
         * @param triangle triangle of a part of the entity being raytraced
         * 
         * @result new instance of this class, if the ray intersect the triangle - null if the ray does not
         */
        public static RayTraceResultTriangle calculateIntercept(float[] eyes, float[] direction, Vec3d posEntity, TriangleRayTrace triangle, ItemStack part)
        {
            float[] data = triangle.getData();
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
            sub(edge1, vec1, vec0);
            sub(edge2, vec2, vec0);
            cross(pvec, direction, edge2);
            det = dot(edge1, pvec);
            if (det >= EPSILON)
            {
                inv_det = 1f / det;
                sub(tvec, eyes, vec0);
                float u = dot(tvec, pvec) * inv_det;
                if (u >= 0 && u <= 1)
                {
                    cross(qvec, tvec, edge1);
                    float v = dot(direction, qvec) * inv_det;
                    if (v >= 0 && u + v <= 1)
                    {
                        return new RayTraceResultTriangle(part, edge1[0] * u + edge2[0] * v + vec0[0], edge1[1] * u + edge2[1] * v + vec0[1], edge1[2] * u + edge2[2] * v + vec0[2]);
                    }
                }
            }
            return null;
        }

        private static void cross(float[] result, float[] v1, float[] v2)
        {
            result[0] = v1[1] * v2[2] - v1[2] * v2[1];
            result[1] = v1[2] * v2[0] - v1[0] * v2[2];
            result[2] = v1[0] * v2[1] - v1[1] * v2[0];
        }

        private static float dot(float[] v1, float[] v2)
        {
            return v1[0] * v2[0] + v1[1] * v2[1] + v1[2] * v2[2];
        }

        private static void sub(float[] result, float[] v1, float[] v2)
        {
            result[0] = v1[0] - v2[0];
            result[1] = v1[1] - v2[1];
            result[2] = v1[2] - v2[2];
        }
    }

    /**
     * The result of a rotated raytrace
     */
    private static class RayTraceResultRotated extends RayTraceResult
    {
        private ItemStack partHit;
        private AxisAlignedBB boxHit;
        private double distanceToEyes;

        public RayTraceResultRotated(Entity entityHit, Vec3d hitVec, double distanceToEyes, ItemStack partHit)
        {
            this(entityHit, hitVec, distanceToEyes, partHit, null);
        }

        public RayTraceResultRotated(Entity entityHit, Vec3d hitVec, double distanceToEyes, AxisAlignedBB boxHit)
        {
            this(entityHit, hitVec, distanceToEyes, null, boxHit);
        }

        private RayTraceResultRotated(Entity entityHit, Vec3d hitVec, double distanceToEyes, @Nullable ItemStack partHit, @Nullable AxisAlignedBB boxHit)
        {
            super(entityHit, hitVec);
            this.partHit = partHit;
            this.boxHit = boxHit;
            this.distanceToEyes = distanceToEyes;
        }

        @Nullable
        public ItemStack getPartHit()
        {
            return partHit;
        }

        @Nullable
        public AxisAlignedBB getBoxHit()
        {
            return boxHit;
        }

        public double getDistanceToEyes()
        {
            return distanceToEyes;
        }
    }

    /**
     * This must be implemented by all entities that raytraces are to be performed on
     */
    public static interface IEntityRaytraceBoxProvider
    {
        /**
         * Called when either an item part is clicked or an entity-specific interaction box is clicked.
         * <p>
         * Default behavior is to perform a general interaction with the entity when a part is clicked.
         * 
         * @param partHit item part hit - null if none was hit
         * @param boxHit interaction box hit - null if none was hit
         * 
         * @return whether or not the right-click that initiated the hit should be canceled
         */
        default boolean processHit(@Nullable ItemStack partHit, @Nullable AxisAlignedBB boxHit)
        {
            if (partHit != null)
            {
                interactWithEntity(this);

                //TODO debug code - delete this code and this comment before release
                {
                    ResourceLocation partName = Item.REGISTRY.getNameForObject(partHit.getItem());
                    if (partName != null)
                    {
                        Minecraft.getMinecraft().player.sendMessage(new TextComponentString(partName.toString()).setStyle(new Style().setColor(TextFormatting.values()[Minecraft.getMinecraft().world.rand.nextInt(15) + 1])));
                    }
                }
                return true;
            }
            return false;
        }

        /**
         * List of all applicable interaction boxes for the entity
         * 
         * @return box list
         */
        default List<AxisAlignedBB> getInteractionBoxes()
        {
            return Lists.<AxisAlignedBB>newArrayList();
        }

        /**
         * Opportunity to draw representations of applicable interaction boxes for the entity
         */
        @SideOnly(Side.CLIENT)
        default void drawInteractionBoxes() {}
    }
}
