package com.mrcrayfish.vehicle.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mrcrayfish.vehicle.Config;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.render.Axis;
import com.mrcrayfish.vehicle.common.CustomDataParameters;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.init.ModEntities;
import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.item.JerryCanItem;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageFuelVehicle;
import com.mrcrayfish.vehicle.network.message.MessageInteractKey;
import com.mrcrayfish.vehicle.network.message.MessagePickupVehicle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import java.util.*;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Author: Phylogeny
 * <p>
 * This class allows precise ratraces to be performed on the rendered model item parts, as well as on additional interaction boxes, of entities.
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class EntityRaytracer
{
    /**
     * Whether or not this class has been initialized
     */
    private static boolean initialized;

    /**
     * Maps raytraceable entities to maps, which map rendered model item parts to the triangles that comprise static versions of the faces of their BakedQuads
     */
    private static final Map<EntityType<? extends IEntityRaytraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRaytraceTrianglesStatic = Maps.newHashMap();

    /**
     * Maps raytraceable entities to maps, which map rendered model item parts to the triangles that comprise dynamic versions of the faces of their BakedQuads
     */
    private static final Map<EntityType<? extends IEntityRaytraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRaytraceTrianglesDynamic = Maps.newHashMap();

    /**
     * Contains all data in entityRaytraceTrianglesStatic and entityRaytraceTrianglesDynamic
     */
    private static final Map<EntityType<? extends IEntityRaytraceable>, Map<RayTracePart, TriangleRayTraceList>> entityRaytraceTriangles = new HashMap<>();

    /**
     * Scales and offsets for rendering the entities in crates
     */
    private static final Map<EntityType<? extends IEntityRaytraceable>, Pair<Float, Float>> entityCrateScalesAndOffsets = new HashMap<>();
    private static final Pair<Float, Float> SCALE_AND_OFFSET_DEFAULT = new ImmutablePair<>(0.25F, 0.0F);

    /**
     * NBT key for a name string tag that parts stacks with NBT tags will have.
     * <p>
     * <strong>Example:</strong> <code>partStack.getTagCompound().getString(EntityRaytracer.PART_NAME)</code>
     */
    public static final String PART_NAME = "nameRaytrace";

    /**
     * The result of clicking and holding on a continuously interactable raytrace part. Every tick that this is not null,
     * both the raytrace and the interaction of this part will be performed.
     */
    private static RayTraceResultRotated continuousInteraction;

    /**
     * The object returned by the interaction function of the result of clicking and holding on a continuously interactable raytrace part.
     */
    private static Object continuousInteractionObject;

    /**
     * Counts the number of ticks that a continuous interaction has been performed for
     */
    private static int continuousInteractionTickCounter;

    /**
     * Clears registration data and triggers re-registration in the next client tick
     */
    public static void clearDataForReregistration()
    {
        entityRaytraceTrianglesStatic.clear();
        entityRaytraceTrianglesDynamic.clear();
        entityRaytraceTriangles.clear();
        entityCrateScalesAndOffsets.clear();
        initialized = false;
    }

    /**
     * Getter for the current continuously interacting raytrace result
     * 
     * @return result of the raytrace
     */
    @Nullable
    public static RayTraceResultRotated getContinuousInteraction()
    {
        return continuousInteraction;
    }

    /**
     * Getter for the object returned by the current continuously interacting raytrace result's interaction function
     * 
     * @return interaction function result
     */
    @Nullable
    public static Object getContinuousInteractionObject()
    {
        return continuousInteractionObject;
    }

    /**
     * Checks if fuel can be transferred from a jerry can to a powered vehicle, and sends a packet to do so every other tick, if it can
     * 
     * @return whether or not fueling can continue
     */
    public static final Function<RayTraceResultRotated, Hand> FUNCTION_FUELING = (rayTraceResult) ->
    {
        PlayerEntity player = Minecraft.getInstance().player;
        if(player.getDataManager().get(CustomDataParameters.GAS_PUMP).isPresent() && ControllerEvents.isRightClicking())
        {
            Entity entity = rayTraceResult.getEntity();
            if(entity instanceof PoweredVehicleEntity)
            {
                PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                if(poweredVehicle.requiresFuel() && poweredVehicle.getCurrentFuel() < poweredVehicle.getFuelCapacity())
                {
                    if(continuousInteractionTickCounter % 2 == 0)
                    {
                        PacketHandler.instance.sendToServer(new MessageFuelVehicle(rayTraceResult.getEntity().getEntityId(), Hand.MAIN_HAND));
                        poweredVehicle.fuelVehicle(player, Hand.MAIN_HAND);
                    }
                    return Hand.MAIN_HAND;
                }
            }
        }

        for(Hand hand : Hand.values())
        {
            ItemStack stack = Minecraft.getInstance().player.getHeldItem(hand);
            if(!stack.isEmpty() && stack.getItem() instanceof JerryCanItem && ControllerEvents.isRightClicking())
            {
                Entity entity = rayTraceResult.getEntity();
                if(entity instanceof PoweredVehicleEntity)
                {
                    PoweredVehicleEntity poweredVehicle = (PoweredVehicleEntity) entity;
                    if(poweredVehicle.requiresFuel() && poweredVehicle.getCurrentFuel() < poweredVehicle.getFuelCapacity())
                    {
                        int fuel = ((JerryCanItem) stack.getItem()).getCurrentFuel(stack);
                        if(fuel > 0)
                        {
                            if(continuousInteractionTickCounter % 2 == 0)
                            {
                                PacketHandler.instance.sendToServer(new MessageFuelVehicle(entity.getEntityId(), hand));
                            }
                            return hand;
                        }
                    }
                }
            }
        }
        return null;
    };

    /**
     * Create static triangles for raytraceable entities
     * <p>
     * For a static raytrace, all static GL operation performed on each item part during rendering must be accounted
     * for by performing the same matrix transformations on the triangles that will comprise the faces their BakedQuads
     */
    private static void registerEntitiesStatic()
    {
        // Aluminum boat
        List<MatrixTransformation> aluminumBoatTransformGlobal = new ArrayList<>();
        createBodyTransforms(aluminumBoatTransformGlobal, ModEntities.ALUMINUM_BOAT);
        HashMap<RayTracePart, List<MatrixTransformation>> aluminumBoatParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.ALUMINUM_BOAT_BODY, aluminumBoatParts, aluminumBoatTransformGlobal);
        createFuelablePartTransforms(ModEntities.ALUMINUM_BOAT, SpecialModel.FUEL_DOOR_CLOSED, aluminumBoatParts, aluminumBoatTransformGlobal);
        registerEntityStatic(ModEntities.ALUMINUM_BOAT, aluminumBoatParts);

        // ATV
        List<MatrixTransformation> atvTransformGlobal = Lists.newArrayList();
        createBodyTransforms(atvTransformGlobal, ModEntities.ATV);
        HashMap<RayTracePart, List<MatrixTransformation>> atvParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.ATV_BODY, atvParts, atvTransformGlobal);
        createTransformListForPart(SpecialModel.ATV_HANDLES, atvParts, atvTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.3375F, 0.25F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, -0.025F, 0.0F));
        createTransformListForPart(SpecialModel.TOW_BAR, atvParts,
                MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                MatrixTransformation.createTranslation(0.0F, 0.5F, 1.05F));
        createFuelablePartTransforms(ModEntities.ATV, SpecialModel.SMALL_FUEL_DOOR_CLOSED, atvParts, atvTransformGlobal);
        createKeyPortTransforms(ModEntities.ATV, atvParts, atvTransformGlobal);
        registerEntityStatic(ModEntities.ATV, atvParts);

        // Bumper car
        List<MatrixTransformation> bumperCarTransformGlobal = Lists.newArrayList();
        createBodyTransforms(bumperCarTransformGlobal, ModEntities.BUMPER_CAR);
        HashMap<RayTracePart, List<MatrixTransformation>> bumperCarParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.BUMPER_CAR_BODY, bumperCarParts, bumperCarTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, bumperCarParts, bumperCarTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.2F, 0.0F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.9F));
        createFuelablePartTransforms(ModEntities.BUMPER_CAR, SpecialModel.FUEL_DOOR_CLOSED, bumperCarParts, bumperCarTransformGlobal);
        registerEntityStatic(ModEntities.BUMPER_CAR, bumperCarParts);

        // Dune buggy
        List<MatrixTransformation> duneBuggyTransformGlobal = Lists.newArrayList();
        createBodyTransforms(duneBuggyTransformGlobal, ModEntities.DUNE_BUGGY);
        HashMap<RayTracePart, List<MatrixTransformation>> duneBuggyParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.DUNE_BUGGY_BODY, duneBuggyParts, duneBuggyTransformGlobal);
        createTransformListForPart(SpecialModel.DUNE_BUGGY_HANDLES, duneBuggyParts, duneBuggyTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.0F, -0.0046875F));
        createFuelablePartTransforms(ModEntities.DUNE_BUGGY, SpecialModel.FUEL_DOOR_CLOSED, duneBuggyParts, duneBuggyTransformGlobal);
        registerEntityStatic(ModEntities.DUNE_BUGGY, duneBuggyParts);

        // Go kart
        List<MatrixTransformation> goKartTransformGlobal = Lists.newArrayList();
        createBodyTransforms(goKartTransformGlobal, ModEntities.GO_KART);
        HashMap<RayTracePart, List<MatrixTransformation>> goKartParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.GO_KART_BODY, goKartParts, goKartTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, goKartParts, goKartTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.09F, 0.49F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.9F));
        createPartTransforms(ModItems.WOOD_SMALL_ENGINE, VehicleProperties.getProperties(ModEntities.GO_KART).getEnginePosition(), goKartParts, goKartTransformGlobal, FUNCTION_FUELING);
        registerEntityStatic(ModEntities.GO_KART, goKartParts);

        // Jet ski
        List<MatrixTransformation> jetSkiTransformGlobal = Lists.newArrayList();
        createBodyTransforms(jetSkiTransformGlobal, ModEntities.JET_SKI);
        HashMap<RayTracePart, List<MatrixTransformation>> jetSkiParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.JET_SKI_BODY, jetSkiParts, jetSkiTransformGlobal);
        createTransformListForPart(SpecialModel.ATV_HANDLES, jetSkiParts, jetSkiTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.375F, 0.25F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, 0.02F, 0.0F));
        createFuelablePartTransforms(ModEntities.JET_SKI, SpecialModel.SMALL_FUEL_DOOR_CLOSED, jetSkiParts, jetSkiTransformGlobal);
        registerEntityStatic(ModEntities.JET_SKI, jetSkiParts);

        // Lawn mower
        List<MatrixTransformation> lawnMowerTransformGlobal = Lists.newArrayList();
        createBodyTransforms(lawnMowerTransformGlobal, ModEntities.LAWN_MOWER);
        HashMap<RayTracePart, List<MatrixTransformation>> lawnMowerParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.LAWN_MOWER_BODY, lawnMowerParts, lawnMowerTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, lawnMowerParts, lawnMowerTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.4F, -0.15F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createScale(0.9F));
        createTransformListForPart(SpecialModel.TOW_BAR, lawnMowerParts,
                MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                MatrixTransformation.createTranslation(0.0F, 0.5F, 0.6F));
        createFuelablePartTransforms(ModEntities.LAWN_MOWER, SpecialModel.FUEL_DOOR_CLOSED, lawnMowerParts, lawnMowerTransformGlobal);
        registerEntityStatic(ModEntities.LAWN_MOWER, lawnMowerParts);

        // Mini bike
        List<MatrixTransformation> miniBikeTransformGlobal = Lists.newArrayList();
        createBodyTransforms(miniBikeTransformGlobal, ModEntities.MINI_BIKE);
        HashMap<RayTracePart, List<MatrixTransformation>> miniBikeParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.MINI_BIKE_BODY, miniBikeParts, miniBikeTransformGlobal);
        createTransformListForPart(SpecialModel.MINI_BIKE_HANDLES, miniBikeParts, miniBikeTransformGlobal);
        createPartTransforms(ModItems.WOOD_SMALL_ENGINE, VehicleProperties.getProperties(ModEntities.MINI_BIKE).getEnginePosition(), miniBikeParts, miniBikeTransformGlobal, FUNCTION_FUELING);
        registerEntityStatic(ModEntities.MINI_BIKE, miniBikeParts);

        // Moped
        List<MatrixTransformation> mopedTransformGlobal = Lists.newArrayList();
        createBodyTransforms(mopedTransformGlobal, ModEntities.MOPED);
        HashMap<RayTracePart, List<MatrixTransformation>> mopedParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.MOPED_BODY, mopedParts, mopedTransformGlobal);
        createTransformListForPart(SpecialModel.MOPED_HANDLES, mopedParts, mopedTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                MatrixTransformation.createTranslation(0.0F, 0.835F, 0.525F),
                MatrixTransformation.createScale(0.8F));
        createTransformListForPart(SpecialModel.MOPED_MUD_GUARD, mopedParts, mopedTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, -0.0625F, 0.0F),
                MatrixTransformation.createTranslation(0.0F, -0.12F, 0.785F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -22.5F),
                MatrixTransformation.createScale(0.9F));
        createFuelablePartTransforms(ModEntities.MOPED, SpecialModel.FUEL_DOOR_CLOSED, mopedParts, mopedTransformGlobal);
        registerEntityStatic(ModEntities.MOPED, mopedParts);

        // Shopping cart
        List<MatrixTransformation> cartTransformGlobal = Lists.newArrayList();
        createBodyTransforms(cartTransformGlobal, ModEntities.SHOPPING_CART);
        HashMap<RayTracePart, List<MatrixTransformation>> cartParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.SHOPPING_CART_BODY, cartParts, cartTransformGlobal);
        registerEntityStatic(ModEntities.SHOPPING_CART, cartParts);

        // Smart car
        List<MatrixTransformation> smartCarTransformGlobal = Lists.newArrayList();
        createBodyTransforms(smartCarTransformGlobal, ModEntities.SMART_CAR);
        HashMap<RayTracePart, List<MatrixTransformation>> smartCarParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.SMART_CAR_BODY, smartCarParts, smartCarTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, smartCarParts, smartCarTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.2F, 0.3F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.9F));
        createTransformListForPart(SpecialModel.TOW_BAR, smartCarParts,
                MatrixTransformation.createRotation(Axis.POSITIVE_Y, 180F),
                MatrixTransformation.createTranslation(0.0F, 0.5F, 1.35F));
        createFuelablePartTransforms(ModEntities.SMART_CAR, SpecialModel.FUEL_DOOR_CLOSED, smartCarParts, smartCarTransformGlobal);
        registerEntityStatic(ModEntities.SMART_CAR, smartCarParts);

        // Speed boat
        List<MatrixTransformation> speedBoatTransformGlobal = Lists.newArrayList();
        createBodyTransforms(speedBoatTransformGlobal, ModEntities.SPEED_BOAT);
        HashMap<RayTracePart, List<MatrixTransformation>> speedBoatParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.SPEED_BOAT_BODY, speedBoatParts, speedBoatTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, speedBoatParts, speedBoatTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.215F, -0.125F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, 0.02F, 0.0F));
        createFuelablePartTransforms(ModEntities.SPEED_BOAT, SpecialModel.FUEL_DOOR_CLOSED, speedBoatParts, speedBoatTransformGlobal);
        registerEntityStatic(ModEntities.SPEED_BOAT, speedBoatParts);

        // Sports plane
        List<MatrixTransformation> sportsPlaneTransformGlobal = Lists.newArrayList();
        createBodyTransforms(sportsPlaneTransformGlobal, ModEntities.SPORTS_PLANE);
        HashMap<RayTracePart, List<MatrixTransformation>> sportsPlaneParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.SPORTS_PLANE, sportsPlaneParts, sportsPlaneTransformGlobal);
        createFuelablePartTransforms(ModEntities.SPORTS_PLANE, SpecialModel.FUEL_DOOR_CLOSED, sportsPlaneParts, sportsPlaneTransformGlobal);
        createKeyPortTransforms(ModEntities.SPORTS_PLANE, sportsPlaneParts, sportsPlaneTransformGlobal);
        createTransformListForPart(SpecialModel.SPORTS_PLANE_WING, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0, -0.1875F, 0.5F),
                MatrixTransformation.createRotation(Axis.POSITIVE_Z, 180F),
                MatrixTransformation.createTranslation(0.875F, 0.0625F, 0.0F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, 5F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_WING, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.875F, -0.1875F, 0.5F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -5F));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        sportsPlaneTransformGlobal.add(MatrixTransformation.createScale(0.85F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_WHEEL_COVER, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_LEG, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, -0.1875F, 1.5F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_WHEEL_COVER, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_LEG, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(-0.46875F, -0.1875F, 0.125F),
                MatrixTransformation.createRotation(Axis.POSITIVE_Y, -100F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_WHEEL_COVER, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F));
        createTransformListForPart(SpecialModel.SPORTS_PLANE_LEG, sportsPlaneParts, sportsPlaneTransformGlobal,
                MatrixTransformation.createTranslation(0.46875F, -0.1875F, 0.125F),
                MatrixTransformation.createRotation(Axis.POSITIVE_Y, 100F));
        registerEntityStatic(ModEntities.SPORTS_PLANE, sportsPlaneParts);

        // Golf Cart
        List<MatrixTransformation> golfCartTransformGlobal = Lists.newArrayList();
        createBodyTransforms(golfCartTransformGlobal, ModEntities.GOLF_CART);
        HashMap<RayTracePart, List<MatrixTransformation>> golfCartParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.GOLF_CART_BODY, golfCartParts, golfCartTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, golfCartParts, golfCartTransformGlobal,
                MatrixTransformation.createTranslation(-0.345F, 0.425F, 0.1F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.95F));
        createFuelablePartTransforms(ModEntities.GOLF_CART, SpecialModel.FUEL_DOOR_CLOSED, golfCartParts, golfCartTransformGlobal);
        createKeyPortTransforms(ModEntities.GOLF_CART, golfCartParts, golfCartTransformGlobal);
        registerEntityStatic(ModEntities.GOLF_CART, golfCartParts);

        // Off-Roader
        List<MatrixTransformation> offRoaderTransformGlobal = Lists.newArrayList();
        createBodyTransforms(offRoaderTransformGlobal, ModEntities.OFF_ROADER);
        HashMap<RayTracePart, List<MatrixTransformation>> offRoaderParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.OFF_ROADER_BODY, offRoaderParts, offRoaderTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, offRoaderParts, offRoaderTransformGlobal,
                MatrixTransformation.createTranslation(-0.3125F, 0.35F, 0.2F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -45F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.75F));
        createFuelablePartTransforms(ModEntities.OFF_ROADER, SpecialModel.FUEL_DOOR_CLOSED, offRoaderParts, offRoaderTransformGlobal);
        createKeyPortTransforms(ModEntities.OFF_ROADER, offRoaderParts, offRoaderTransformGlobal);
        registerEntityStatic(ModEntities.OFF_ROADER, offRoaderParts);

        List<MatrixTransformation> tractorTransformGlobal = Lists.newArrayList();
        createBodyTransforms(tractorTransformGlobal, ModEntities.TRACTOR);
        HashMap<RayTracePart, List<MatrixTransformation>> tractorParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.TRACTOR, tractorParts, tractorTransformGlobal);
        createTransformListForPart(SpecialModel.GO_KART_STEERING_WHEEL, tractorParts, tractorTransformGlobal,
                MatrixTransformation.createTranslation(0.0F, 0.66F, -0.475F),
                MatrixTransformation.createRotation(Axis.POSITIVE_X, -67.5F),
                MatrixTransformation.createTranslation(0.0F, -0.02F, 0.0F),
                MatrixTransformation.createScale(0.9F));
        createFuelablePartTransforms(ModEntities.TRACTOR, SpecialModel.FUEL_DOOR_CLOSED, tractorParts, tractorTransformGlobal);
        createKeyPortTransforms(ModEntities.TRACTOR, tractorParts, tractorTransformGlobal);
        registerEntityStatic(ModEntities.TRACTOR, tractorParts);

        if(ModList.get().isLoaded("cfm"))
        {
            // Bath
            List<MatrixTransformation> bathTransformGlobal = Lists.newArrayList();
            createBodyTransforms(bathTransformGlobal, ModEntities.BATH);
            HashMap<RayTracePart, List<MatrixTransformation>> bathParts = Maps.newHashMap();
            createTransformListForPart(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:bath")), bathParts, bathTransformGlobal,
                    MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F));
            registerEntityStatic(ModEntities.BATH, bathParts);

            // Couch
            List<MatrixTransformation> couchTransformGlobal = Lists.newArrayList();
            createBodyTransforms(couchTransformGlobal, ModEntities.SOFA);
            HashMap<RayTracePart, List<MatrixTransformation>> couchParts = Maps.newHashMap();
            createTransformListForPart(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:rainbow_sofa")), couchParts, couchTransformGlobal,
                    MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F),
                    MatrixTransformation.createTranslation(0.0F, 0.0625F, 0.0F));
            registerEntityStatic(ModEntities.SOFA, couchParts);

            // Sofacopter
            List<MatrixTransformation> sofacopterTransformGlobal = Lists.newArrayList();
            createBodyTransforms(sofacopterTransformGlobal, ModEntities.SOFACOPTER);
            HashMap<RayTracePart, List<MatrixTransformation>> sofacopterParts = Maps.newHashMap();
            createTransformListForPart(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cfm:sofa")), sofacopterParts, sofacopterTransformGlobal,
                    MatrixTransformation.createRotation(Axis.POSITIVE_Y, 90F));
            createTransformListForPart(SpecialModel.SOFA_HELICOPTER_ARM, sofacopterParts, sofacopterTransformGlobal,
                    MatrixTransformation.createTranslation(0.0F, 8 * 0.0625F, 0.0F));
            createFuelablePartTransforms(ModEntities.SOFACOPTER, SpecialModel.FUEL_DOOR_CLOSED, sofacopterParts, sofacopterTransformGlobal);
            createKeyPortTransforms(ModEntities.SOFACOPTER, sofacopterParts, sofacopterTransformGlobal);
            registerEntityStatic(ModEntities.SOFACOPTER, sofacopterParts);
        }

        // Vehicle Trailer
        List<MatrixTransformation> trailerVehicleTransformGlobal = Lists.newArrayList();
        createBodyTransforms(trailerVehicleTransformGlobal, ModEntities.VEHICLE_TRAILER);
        HashMap<RayTracePart, List<MatrixTransformation>> trailerVehicleParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.VEHICLE_TRAILER, trailerVehicleParts, trailerVehicleTransformGlobal);
        registerEntityStatic(ModEntities.VEHICLE_TRAILER, trailerVehicleParts);

        // Chest Trailer
        List<MatrixTransformation> trailerStorageTransformGlobal = Lists.newArrayList();
        createBodyTransforms(trailerStorageTransformGlobal, ModEntities.STORAGE_TRAILER);
        HashMap<RayTracePart, List<MatrixTransformation>> trailerStorageParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.STORAGE_TRAILER, trailerStorageParts, trailerStorageTransformGlobal);
        registerEntityStatic(ModEntities.STORAGE_TRAILER, trailerStorageParts);

        // Seeder Trailer
        List<MatrixTransformation> seederTransformGlobal = Lists.newArrayList();
        createBodyTransforms(seederTransformGlobal, ModEntities.SEEDER);
        HashMap<RayTracePart, List<MatrixTransformation>> seederParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.SEEDER_TRAILER, seederParts, seederTransformGlobal);
        registerEntityStatic(ModEntities.SEEDER, seederParts);

        // Fertilizer
        List<MatrixTransformation> fertilizerTransformGlobal = Lists.newArrayList();
        createBodyTransforms(fertilizerTransformGlobal, ModEntities.FERTILIZER);
        HashMap<RayTracePart, List<MatrixTransformation>> fertilizerParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.FERTILIZER_TRAILER, fertilizerParts, fertilizerTransformGlobal);
        registerEntityStatic(ModEntities.FERTILIZER, fertilizerParts);

        // Fluid
        List<MatrixTransformation> trailerFluidTransformGlobal = Lists.newArrayList();
        createBodyTransforms(trailerFluidTransformGlobal, ModEntities.FLUID_TRAILER);
        HashMap<RayTracePart, List<MatrixTransformation>> trailerFluidParts = Maps.newHashMap();
        createTransformListForPart(SpecialModel.FLUID_TRAILER, trailerFluidParts, trailerFluidTransformGlobal);
        registerEntityStatic(ModEntities.FLUID_TRAILER, trailerFluidParts);
    }

    /**
     * Create dynamic triangles for raytraceable entities
     * <p>
     * For a dynamic raytrace, all GL operation performed be accounted for
     */
    private static void registerEntitiesDynamic()
    {
        /* Map<RayTracePart, BiFunction<RayTracePart, Entity, Matrix4d>> aluminumBoatPartsDynamic = Maps.<RayTracePart, BiFunction<RayTracePart, Entity, Matrix4d>>newHashMap();
        aluminumBoatPartsDynamic.put(new RayTracePart(new ItemStack(ModItems.ALUMINUM_BOAT_BODY)), (part, entity) ->
        {
            VehicleEntity aluminumBoat = (VehicleEntity) entity;
            Matrix4d matrix = new Matrix4d();
            matrix.setIdentity();
            MatrixTransformation.createScale(1.1).transform(matrix);
            MatrixTransformation.createTranslation(0, 0.5, 0.2).transform(matrix);
            double currentSpeedNormal = aluminumBoat.currentSpeed / aluminumBoat.getMaxSpeed();
            double turnAngleNormal = aluminumBoat.turnAngle / 45.0;
            MatrixTransformation.createRotation(turnAngleNormal * currentSpeedNormal * -15, 0, 0, 1).transform(matrix);
            MatrixTransformation.createRotation(-8 * Math.min(1.0F, currentSpeedNormal), 1, 0, 0).transform(matrix);
            finalizePartStackMatrix(matrix);
            return matrix;
        });
        registerDynamicClass(AluminumBoatEntity.class, aluminumBoatPartsDynamic); */
    }

    /**
     * Creates a body transformation based on a PartPosition for a raytraceable entity's body. These
     * arguments should be the same as the static properties defined for the vehicle.
     *
     * @param transforms the global transformation matrix
     * @param entityType the vehicle entity type
     */
    public static void createBodyTransforms(List<MatrixTransformation> transforms, EntityType<? extends VehicleEntity> entityType)
    {
        VehicleProperties properties = VehicleProperties.getProperties(entityType);
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

    public static void createPartTransforms(SpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
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

    public static void createPartTransforms(SpecialModel model, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, Function<RayTraceResultRotated, Hand> function)
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

    public static void createPartTransforms(Item part, PartPosition partPosition, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, Function<RayTraceResultRotated, Hand> function)
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
     * @param xPixel part's x position
     * @param yPixel part's y position
     * @param zPixel part's z position
     * @param rotation part's rotation vector
     * @param scale part's scale
     * @param transforms list that part transforms are added to
     */
    public static void createPartTransforms(double xPixel, double yPixel, double zPixel, Vector3f rotation, double scale, List<MatrixTransformation> transforms)
    {
        transforms.add(MatrixTransformation.createTranslation((float) xPixel * 0.0625F, (float) yPixel * 0.0625F, (float) zPixel * 0.0625F));
        transforms.add(MatrixTransformation.createTranslation(0.0F, -0.5F, 0.0F));
        transforms.add(MatrixTransformation.createScale((float) scale));
        transforms.add(MatrixTransformation.createTranslation(0.0F, 0.5F, 0.0F));
        if (rotation.getX() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_X, rotation.getX()));
        }
        if (rotation.getY() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Y, rotation.getY()));
        }
        if (rotation.getZ() != 0)
        {
            transforms.add(MatrixTransformation.createRotation(Axis.POSITIVE_Z, rotation.getY()));
        }
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part and adds them the list of transforms
     * for the given entity.
     * 
     * @param part the rendered item part
     * @param xMeters part's x offset meters
     * @param yMeters part's y offset meters
     * @param zMeters part's z offset meters
     * @param xPixel part's x position in pixels
     * @param yPixel part's y position in pixels
     * @param zPixel part's z position in pixels
     * @param rotation part's rotation vector
     * @param scale part's scale
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     */
    public static void createFuelablePartTransforms(Item part, double xMeters, double yMeters, double zMeters, double xPixel, double yPixel, double zPixel,
            Vector3f rotation, double scale, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        List<MatrixTransformation> partTransforms = Lists.newArrayList();
        partTransforms.add(MatrixTransformation.createTranslation((float) xMeters, (float) yMeters, (float) zMeters));
        createPartTransforms(xPixel, yPixel, zPixel, rotation, scale, partTransforms);
        transformsGlobal.addAll(partTransforms);
        createTransformListForPart(new ItemStack(part), parts, transformsGlobal, FUNCTION_FUELING);
    }

    /**
     * Creates part-specific transforms for a raytraceable entity's rendered part and adds them the list of transforms
     * for the given entity.
     *
     * @param entityType the vehicle entity type
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     */
    public static void createFuelablePartTransforms(EntityType<? extends VehicleEntity> entityType, SpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        PartPosition fuelPortPosition = VehicleProperties.getProperties(entityType).getFuelPortPosition();
        createPartTransforms(model, fuelPortPosition, parts, transformsGlobal, FUNCTION_FUELING);
    }

    /**
     * Version of {@link EntityRaytracer#createFuelablePartTransforms createFuelablePartTransforms} that sets the axis of rotation to Y
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
     * @param transformsGlobal transforms that apply to all parts for this entity
     */
    public static void createFuelablePartTransforms(Item part, double xMeters, double yMeters, double zMeters, double xPixel, double yPixel, double zPixel,
            double rotation, double scale, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        List<MatrixTransformation> partTransforms = Lists.newArrayList();
        partTransforms.add(MatrixTransformation.createTranslation((float) xMeters, (float) yMeters, (float) zMeters));
        createPartTransforms(xPixel, yPixel, zPixel, new Vector3f(0.0F, (float) rotation, 0.0F), scale, partTransforms);
        transformsGlobal.addAll(partTransforms);
        createTransformListForPart(new ItemStack(part), parts, transformsGlobal, FUNCTION_FUELING);
    }

    public static void createKeyPortTransforms(EntityType<? extends VehicleEntity> entityType, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal)
    {
        PartPosition keyPortPosition = VehicleProperties.getProperties(entityType).getKeyPortPosition();
        createPartTransforms(SpecialModel.KEY_HOLE, keyPortPosition, parts, transformsGlobal);
    }

    /**
     * Adds all global and part-specific transforms for an item part to the list of transforms for the given entity
     * 
     * @param part the rendered item part in a stack
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param continuousInteraction interaction to be performed each tick
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal,
                                                  @Nullable Function<RayTraceResultRotated, Hand> continuousInteraction, MatrixTransformation... transforms)
    {
        List<MatrixTransformation> transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart<>(part, continuousInteraction), transformsAll);
    }

    /**
     * Version of {@link EntityRaytracer#createTransformListForPart(ItemStack, HashMap, List, Function, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     * 
     * @param part the rendered item part in a stack
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(ItemStack part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(part, parts, transformsGlobal, null, transforms);
    }

    /**
     * Version of {@link EntityRaytracer#createTransformListForPart(ItemStack, HashMap, List, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(new ItemStack(part), parts, transformsGlobal, transforms);
    }

    /**
     * Version of {@link EntityRaytracer#createTransformListForPart(Item, HashMap, List, MatrixTransformation[]) createTransformListForPart} without global transform list
     *
     * @param model the special model
     * @param parts map of all parts to their transforms
     * @param transforms part-specific transforms for the given part
     */
    public static void createTransformListForPart(SpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation... transforms)
    {
        createTransformListForPart(model, parts, Lists.newArrayList(), transforms);
    }

    /**
     * Version of {@link EntityRaytracer#createTransformListForPart(Item, HashMap, List, MatrixTransformation[]) createTransformListForPart} without global transform list
     * 
     * @param part the rendered item part
     * @param parts map of all parts to their transforms
     * @param transforms part-specific transforms for the given part 
     */
    public static void createTransformListForPart(Item part, HashMap<RayTracePart, List<MatrixTransformation>> parts, MatrixTransformation... transforms)
    {
        createTransformListForPart(part, parts, Lists.newArrayList(), transforms);
    }

    public static void createTransformListForPart(SpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal,
                                                  @Nullable Function<RayTraceResultRotated, Hand> continuousInteraction, MatrixTransformation... transforms)
    {
        List<MatrixTransformation> transformsAll = Lists.newArrayList();
        transformsAll.addAll(transformsGlobal);
        transformsAll.addAll(Arrays.asList(transforms));
        parts.put(new RayTracePart<>(model, continuousInteraction), transformsAll);
    }

    /**
     * Version of {@link EntityRaytracer#createTransformListForPart(ItemStack, HashMap, List, Function, MatrixTransformation[]) createTransformListForPart} that accepts the part as an item, rather than a stack
     *
     * @param model the ibakedmodel of the part
     * @param parts map of all parts to their transforms
     * @param transformsGlobal transforms that apply to all parts for this entity
     * @param transforms part-specific transforms for the given part
     */
    public static void createTransformListForPart(SpecialModel model, HashMap<RayTracePart, List<MatrixTransformation>> parts, List<MatrixTransformation> transformsGlobal, MatrixTransformation... transforms)
    {
        createTransformListForPart(model, parts, transformsGlobal, null, transforms);
    }

    /**
     * Generates lists of dynamic matrix-generating triangles and static lists of transformed triangles that represent each dynamic/static IBakedModel 
     * of each rendered item part for each raytraceable entity class, and finds the nearest superclass in common between those classes.
     * <p>
     * 
     * <strong>Note:</strong> this must be called on the client during the {@link net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent} phase.
     */
    public static void init()
    {
        clearDataForReregistration();

        // Create triangles for raytraceable entities
        registerEntitiesDynamic();
        registerEntitiesStatic();

        for (EntityType<? extends IEntityRaytraceable> entityType : entityRaytraceTriangles.keySet())
        {
            // Calculate scale and offset for rendering the entity in a crate
            float min = 0;
            float max = 0;
            float[] data;
            float x, y, z;
            Entity entity = entityType.create(Minecraft.getInstance().world);
            for (Entry<RayTracePart, TriangleRayTraceList> entry : entityRaytraceTriangles.get(entityType).entrySet())
            {
                for (TriangleRayTrace triangle : entity == null ? entry.getValue().getTriangles() : entry.getValue().getTriangles(entry.getKey(), entity))
                {
                    data = triangle.getData();
                    for (int i = 0; i < data.length; i += 3)
                    {
                        x = data[i];
                        y = data[i + 1];
                        z = data[i + 2];
                        if (x < min) min = x;
                        if (y < min) min = y;
                        if (z < min) min = z;
                        if (x > max) max = x;
                        if (y > max) max = y;
                        if (z > max) max = z;
                    }
                }
            }
            float range = max - min;
            entityCrateScalesAndOffsets.put(entityType, new ImmutablePair<>(1 / (range * 1.25F), -(min + range * 0.5F)));
        }
        initialized = true;
    }

    /**
     * Create static triangles for raytraceable entity
     * 
     * @param entityType the entity type
     * @param transforms matrix transforms for each part
     */
    private static <T extends VehicleEntity & IEntityRaytraceable> void registerEntityStatic(EntityType<T> entityType, Map<RayTracePart, List<MatrixTransformation>> transforms)
    {
        Map<RayTracePart, TriangleRayTraceList> partTriangles = Maps.newHashMap();
        for (Entry<RayTracePart, List<MatrixTransformation>> entryPart : transforms.entrySet())
        {
            RayTracePart part = entryPart.getKey();

            // Generate part-specific matrix
            Matrix4f matrix = new Matrix4f();
            matrix.func_226591_a_();
            for (MatrixTransformation tranform : entryPart.getValue())
                tranform.transform(matrix);

            finalizePartStackMatrix(matrix);

            partTriangles.put(part, new TriangleRayTraceList(generateTriangles(getModel(part), matrix)));
        }
        entityRaytraceTrianglesStatic.put(entityType, partTriangles);
        HashMap<RayTracePart, TriangleRayTraceList> partTrianglesCopy = new HashMap<>(partTriangles);
        Map<RayTracePart, TriangleRayTraceList> partTrianglesAll = entityRaytraceTriangles.get(entityType);
        if (partTrianglesAll != null)
        {
            partTrianglesCopy.putAll(partTrianglesAll);
        }
        entityRaytraceTriangles.put(entityType, partTrianglesCopy);
    }

    /**
     * Create dynamic triangles for raytraceable entity
     * 
     * @param entityType the entity type
     * @param matrixFactories functions for dynamic triangles that take the part and the raytraced
     * entity as arguments and output that part's dynamically generated transformation matrix
     */
    @SuppressWarnings("unused")
    private static <T extends VehicleEntity & IEntityRaytraceable> void registerEntityDynamic(EntityType<T> entityType, Map<RayTracePart, BiFunction<RayTracePart, Entity, Matrix4f>> matrixFactories)
    {
        Map<RayTracePart, TriangleRayTraceList> partTriangles = Maps.newHashMap();
        for (Entry<RayTracePart, BiFunction<RayTracePart, Entity, Matrix4f>> entryPart : matrixFactories.entrySet())
        {
            RayTracePart part = entryPart.getKey();
            partTriangles.put(part, new TriangleRayTraceList(generateTriangles(getModel(part), null), entryPart.getValue()));
        }
        entityRaytraceTrianglesDynamic.put(entityType, partTriangles);
        entityRaytraceTriangles.put(entityType, partTriangles);
    }

    /**
     * Gets entity's scale and offset for rendering in a crate
     * 
     * @param raytraceClass class of entity
     * 
     * @return pair of scale and offset
     */
    public static Pair<Float, Float> getCrateScaleAndOffset(Class<? extends Entity> raytraceClass)
    {
        Pair<Float, Float> scaleAndOffset = entityCrateScalesAndOffsets.get(raytraceClass);
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
        return Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(part.partStack, null, Minecraft.getInstance().player);
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
            for (Direction facing : Direction.values())
            {
                random.setSeed(42L);
                generateTriangles(model.getQuads(null, facing, random), matrix, triangles);
            }
        }
        catch (Exception ignored) {}
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
            vec.func_229372_a_(matrix);
            triangleNew[i] = vec.getX();
            triangleNew[i + 1] = vec.getY();
            triangleNew[i + 2] = vec.getZ();
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
            return new MatrixTransformation(MatrixTransformationType.ROTATION, axis.getX(), axis.getY(), axis.getZ(), angle);
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
         * @param matrix matrix to apply this transformation to
         */
        public void transform(Matrix4f matrix)
        {
            MatrixStack matrixStack = new MatrixStack();
            switch(type)
            {
                case ROTATION:
                    matrixStack.func_227863_a_(new Vector3f(this.x, this.y, this.z).func_229187_a_(this.angle));
                    break;
                case TRANSLATION:
                    matrixStack.func_227861_a_(this.x, this.y, this.z);
                    break;
                case SCALE:
                    matrixStack.func_227862_a_(this.x, this.y, this.z);
                    break;
            }
            matrix.func_226595_a_(matrixStack.func_227866_c_().func_227870_a_());
        }
    }

    /**
     * Performs a specific and general interaction with a raytraceable entity
     * 
     * @param entity raytraceable entity
     * @param result the result of the raytrace
     */
    public static void interactWithEntity(IEntityRaytraceable entity, EntityRayTraceResult result)
    {
        Minecraft.getInstance().playerController.interactWithEntity(Minecraft.getInstance().player, (Entity) entity, Hand.MAIN_HAND);
        Minecraft.getInstance().playerController.interactWithEntity(Minecraft.getInstance().player, (Entity) entity, result, Hand.MAIN_HAND);
    }

    /**
     * Performs a raytrace and interaction each tick that a continuously interactable part is right-clicked and held while looking at
     * 
     * @param event tick event
     */
    @SubscribeEvent
    public static void raytraceEntitiesContinuously(TickEvent.ClientTickEvent event)
    {
        if (event.phase != TickEvent.Phase.START)
            return;

        if ((!initialized || Config.CLIENT.reloadRayTracerEachTick.get()) && Minecraft.getInstance().world != null)
            init();

        if (continuousInteraction == null || Minecraft.getInstance().player == null)
            return;

        RayTraceResultRotated result = raytraceEntities(continuousInteraction.isRightClick());
        if (result == null || result.getEntity() != continuousInteraction.getEntity() || result.getPartHit() != continuousInteraction.getPartHit())
        {
            continuousInteraction = null;
            continuousInteractionTickCounter = 0;
            return;
        }
        continuousInteractionObject = result.performContinuousInteraction();
        if (continuousInteractionObject == null)
        {
            continuousInteraction = null;
            continuousInteractionTickCounter = 0;
        }
        else
        {
            continuousInteractionTickCounter++;
        }
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of all raytraceable entities within reach of the player upon click,
     * and cancels it if the clicked raytraceable entity returns true from {@link IEntityRaytraceable#processHit processHit}
     * 
     * @param event mouse event
     */
    @SubscribeEvent
    public static void onMouseEvent(InputEvent.RawMouseEvent event)
    {
        Minecraft mc = Minecraft.getInstance();
        if(mc.loadingGui != null || mc.currentScreen != null)
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
        if(performRayTrace(rightClick))
        {
            // Cancel click
            event.setCanceled(true);
        }
    }

    public static boolean performRayTrace(boolean rightClick)
    {
        RayTraceResultRotated result = raytraceEntities(rightClick);
        if (result != null)
        {
            continuousInteractionObject = result.performContinuousInteraction();
            if (continuousInteractionObject != null)
            {
                continuousInteraction = result;
                continuousInteractionTickCounter = 1;
            }
            return true;
        }
        return false;
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of all raytraceable entities within reach of the player,
     * and returns the result if the clicked raytraceable entity returns true from {@link IEntityRaytraceable#processHit processHit}
     * 
     * @param rightClick whether the click was a right-click or a left-click
     * 
     * @return the result of the raytrace - returns null, if it fails
     */
    @Nullable
    //@SuppressWarnings("SuspiciousMethodCalls")
    private static RayTraceResultRotated raytraceEntities(boolean rightClick)
    {
        float reach = Minecraft.getInstance().playerController.getBlockReachDistance();
        Vec3d eyeVec = Minecraft.getInstance().player.getEyePosition(1.0F);
        Vec3d forwardVec = eyeVec.add(Minecraft.getInstance().player.getLook(1.0F).scale(reach));
        AxisAlignedBB box = new AxisAlignedBB(eyeVec, eyeVec).grow(reach);
        RayTraceResultRotated lookObject = null;
        double distanceShortest = Double.MAX_VALUE;
        // Perform raytrace on all raytraceable entities within reach of the player
        RayTraceResultRotated lookObjectPutative;
        double distance;
        for (Entity entity : Minecraft.getInstance().world.getEntitiesWithinAABB(VehicleEntity.class, box, entity -> entity instanceof IEntityRaytraceable))
        {
            if (entityRaytraceTrianglesDynamic.containsKey(entity.getType()) || entityRaytraceTrianglesStatic.containsKey(entity.getType()))
            {
                lookObjectPutative = rayTraceEntityRotated((IEntityRaytraceable) entity, eyeVec, forwardVec, reach, rightClick);
                if (lookObjectPutative != null)
                {
                    distance = lookObjectPutative.getDistanceToEyes();
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
                RayTraceResult result = Minecraft.getInstance().objectMouseOver;
                // If the hit entity is a raytraceable entity, and if the player's eyes are inside what MC
                // thinks the player is looking at, then process the hit regardless of what MC thinks
                boolean bypass = entityRaytraceTrianglesStatic.keySet().contains(lookObject.getEntity().getType());
                if (bypass && result != null && result.getType() != Type.MISS)
                {
                    AxisAlignedBB boxMC = null;
                    if (result.getType() == Type.ENTITY)
                    {
                        boxMC = lookObject.getEntity().getBoundingBox();
                    }
                    else if(result.getType() == Type.BLOCK)
                    {
                        BlockPos pos = ((BlockRayTraceResult)result).getPos();
                        boxMC = lookObject.getEntity().world.getBlockState(pos).getShape(lookObject.getEntity().world, pos).getBoundingBox();
                    }
                    bypass = boxMC != null && boxMC.contains(eyeVec);
                }

                if (!bypass && result != null && result.getType() != Type.MISS)
                {
                    // Set hit to what MC thinks the player is looking at if the player is not looking at the hit entity
                    if (result.getType() == Type.ENTITY && ((EntityRayTraceResult) result).getEntity() == lookObject.getEntity())
                    {
                        bypass = true;
                    }
                    else
                    {
                        hit = result.getHitVec();
                    }
                }
                // If not bypassed, process the hit only if it is closer to the player's eyes than what MC thinks the player is looking
                if (bypass || eyeDistance < hit.distanceTo(eyeVec))
                {
                    if (((IEntityRaytraceable) lookObject.getEntity()).processHit(lookObject, rightClick))
                    {
                        return lookObject;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Performs raytrace on interaction boxes and item part triangles of raytraceable entity
     * 
     * @param boxProvider raytraceable entity
     * @param eyeVec position of the player's eyes
     * @param forwardVec eyeVec extended by reach distance in the direction the player is looking in
     * @param reach distance at which players can interact with objects in the world
     * @param rightClick whether the click was a right-click or a left-click
     * 
     * @return the result of the raytrace
     */
    @Nullable
    public static RayTraceResultRotated rayTraceEntityRotated(IEntityRaytraceable boxProvider, Vec3d eyeVec, Vec3d forwardVec, double reach, boolean rightClick)
    {
        Entity entity = (Entity) boxProvider;
        Vec3d pos = entity.getPositionVector();
        double angle = Math.toRadians(-entity.rotationYaw);

        // Rotate the raytrace vectors in the opposite direction as the entity's rotation yaw
        Vec3d eyeVecRotated = rotateVecXZ(eyeVec, angle, pos);
        Vec3d forwardVecRotated = rotateVecXZ(forwardVec, angle, pos);

        float[] eyes = new float[]{(float) eyeVecRotated.x, (float) eyeVecRotated.y, (float) eyeVecRotated.z};
        Vec3d look = forwardVecRotated.subtract(eyeVecRotated).normalize().scale(reach);
        float[] direction = new float[]{(float) look.x, (float) look.y, (float) look.z};
        // Perform raytrace on the entity's interaction boxes
        RayTraceResultTriangle lookBox = null;
        RayTraceResultTriangle lookPart = null;
        double distanceShortest = Double.MAX_VALUE;
        List<RayTracePart> boxesApplicable = boxProvider.getApplicableInteractionBoxes();
        List<RayTracePart> partsNonApplicable = boxProvider.getNonApplicableParts();

        // Perform raytrace on the dynamic boxes and triangles of the entity's parts
        lookBox = raytracePartTriangles(entity, pos, eyeVecRotated, lookBox, distanceShortest, eyes, direction, boxesApplicable, false, boxProvider.getDynamicInteractionBoxMap());
        distanceShortest = updateShortestDistance(lookBox, distanceShortest);
        lookPart = raytracePartTriangles(entity, pos, eyeVecRotated, lookPart, distanceShortest, eyes, direction, partsNonApplicable, true, entityRaytraceTrianglesDynamic.get(entity.getType()));
        distanceShortest = updateShortestDistance(lookPart, distanceShortest);

        boolean isDynamic = lookBox != null || lookPart != null;

        // If no closer intersection than that of the dynamic boxes and triangles found, then perform raytrace on the static boxes and triangles of the entity's parts
        if (!isDynamic)
        {
            lookBox = raytracePartTriangles(entity, pos, eyeVecRotated, lookBox, distanceShortest, eyes, direction, boxesApplicable, false, boxProvider.getStaticInteractionBoxMap());
            distanceShortest = updateShortestDistance(lookBox, distanceShortest);
            lookPart = raytracePartTriangles(entity, pos, eyeVecRotated, lookPart, distanceShortest, eyes, direction, partsNonApplicable, true, entityRaytraceTrianglesStatic.get(entity.getType()));
        }
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
     * @param partsApplicable list of parts that currently apply to the raytraced entity - if null, all are applicable
     * @param parts triangles for the part
     * 
     * @return the result of the part raytrace
     */
    private static RayTraceResultTriangle raytracePartTriangles(Entity entity, Vec3d pos, Vec3d eyeVecRotated, RayTraceResultTriangle lookPart, double distanceShortest,
            float[] eyes, float[] direction, @Nullable List<RayTracePart> partsApplicable, boolean invalidateParts, Map<RayTracePart, TriangleRayTraceList> parts)
    {
        if (parts != null)
        {
            for (Entry<RayTracePart, TriangleRayTraceList> entry : parts.entrySet())
            {
                if (partsApplicable == null || (invalidateParts != partsApplicable.contains(entry.getKey())))
                {
                    RayTraceResultTriangle lookObjectPutative;
                    double distance;
                    RayTracePart part = entry.getKey();
                    for (TriangleRayTrace triangle : entry.getValue().getTriangles(part, entity))
                    {
                        lookObjectPutative = RayTraceResultTriangle.calculateIntercept(eyes, direction, pos, triangle.getData(), part);
                        if (lookObjectPutative != null)
                        {
                            distance = lookObjectPutative.calculateAndSaveDistance(eyeVecRotated);
                            if (distance < distanceShortest)
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
     * @param entity raytraced entity
     * @param matrixStack the matrix stack of the entity
     * @param yaw entity's rotation yaw
     */
    public static <T extends VehicleEntity & IEntityRaytraceable> void renderRaytraceElements(T entity, MatrixStack matrixStack, float yaw)
    {
        if(Config.CLIENT.renderOutlines.get())
        {
            matrixStack.func_227860_a_();
            matrixStack.func_227863_a_(Vector3f.field_229181_d_.func_229187_a_(-yaw));

            RenderSystem.pushMatrix();
            RenderSystem.multMatrix(matrixStack.func_227866_c_().func_227870_a_());

            //RenderSystem.enableBlend();
            //RenderSystem.defaultBlendFunc();
            RenderSystem.lineWidth(Math.max(2.0F, (float)Minecraft.getInstance().func_228018_at_().getFramebufferWidth() / 1920.0F * 2.0F));
            RenderSystem.disableTexture();
            RenderSystem.disableLighting();
            RenderSystem.enableDepthTest();

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            renderRaytraceTriangles(entity, tessellator, buffer, entityRaytraceTrianglesStatic);
            renderRaytraceTriangles(entity, tessellator, buffer, entityRaytraceTrianglesDynamic);
            entity.drawInteractionBoxes(tessellator, buffer);

            RenderSystem.enableLighting();
            RenderSystem.enableTexture();
           // RenderSystem.disableBlend();

            RenderSystem.popMatrix();

            matrixStack.func_227865_b_();
        }
    }

    /**
     * Renders the triangles of the parts of a raytraceable entity
     * 
     * @param entity raytraced entity
     * @param tessellator rendered plane tiler
     * @param buffer tessellator's vertex buffer
     * @param entityTriangles map containing the triangles for the given ray traceable entity
     */
    private static <T extends VehicleEntity & IEntityRaytraceable> void renderRaytraceTriangles(T entity, Tessellator tessellator, BufferBuilder buffer,
            Map<EntityType<? extends IEntityRaytraceable>, Map<RayTracePart, TriangleRayTraceList>> entityTriangles)
    {
        Map<RayTracePart, TriangleRayTraceList> map = entityTriangles.get(entity.getType());
        if (map != null)
        {
            List<RayTracePart> partsNonApplicable = entity.getNonApplicableParts();
            for (Entry<RayTracePart, TriangleRayTraceList> entry : map.entrySet())
            {
                if (partsNonApplicable == null || !partsNonApplicable.contains(entry.getKey()))
                {
                    for (TriangleRayTrace triangle : entry.getValue().getTriangles(entry.getKey(), entity))
                    {
                        triangle.draw(tessellator, buffer, 1, 0, 0, 0.4F);
                    }
                }
            }
        }
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
     * Version of {@link EntityRaytracer#boxToTriangles(AxisAlignedBB, BiFunction) boxToTriangles}
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
            buffer.func_225582_a_(data[6], data[7], data[8]).func_227885_a_(red, green, blue, alpha).endVertex();
            buffer.func_225582_a_(data[0], data[1], data[2]).func_227885_a_(red, green, blue, alpha).endVertex();
            buffer.func_225582_a_(data[3], data[4], data[5]).func_227885_a_(red, green, blue, alpha).endVertex();
            tessellator.draw();
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

        public Vec3d getHit()
        {
            return new Vec3d(x, y, z);
        }

        public RayTracePart getPart()
        {
            return part;
        }

        public double calculateAndSaveDistance(Vec3d eyeVec)
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
        public static RayTraceResultTriangle calculateIntercept(float[] eyes, float[] direction, Vec3d posEntity, float[] data, RayTracePart part)
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
    public static class RayTracePart<R>
    {
        private final ItemStack partStack;
        private final AxisAlignedBB partBox;
        private final SpecialModel model;
        private final Function<RayTraceResultRotated, R> continuousInteraction;

        public RayTracePart(ItemStack partStack, @Nullable Function<RayTraceResultRotated, R> continuousInteraction)
        {
            this(partStack, null, null, continuousInteraction);
        }

        public RayTracePart(AxisAlignedBB partBox, @Nullable Function<RayTraceResultRotated, R> continuousInteraction)
        {
            this(ItemStack.EMPTY, partBox, null, continuousInteraction);
        }

        public RayTracePart(SpecialModel model, @Nullable Function<RayTraceResultRotated, R> continuousInteraction)
        {
            this(ItemStack.EMPTY, null, model, continuousInteraction);
        }

        public RayTracePart(AxisAlignedBB partBox)
        {
            this(ItemStack.EMPTY, partBox, null, null);
        }

        private RayTracePart(ItemStack partStack, @Nullable AxisAlignedBB partBox, @Nullable SpecialModel model, @Nullable Function<RayTraceResultRotated, R> continuousInteraction)
        {
            this.partStack = partStack;
            this.partBox = partBox;
            this.model = model;
            this.continuousInteraction = continuousInteraction;
        }

        public ItemStack getStack()
        {
            return partStack;
        }

        @Nullable
        public AxisAlignedBB getBox()
        {
            return partBox;
        }

        @Nullable
        public SpecialModel getModel()
        {
            return model;
        }

        public Function<RayTraceResultRotated, R> getContinuousInteraction()
        {
            return continuousInteraction;
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

        private RayTraceResultRotated(Entity entityHit, Vec3d hitVec, double distanceToEyes, RayTracePart partHit, boolean rightClick)
        {
            super(entityHit, hitVec);
            this.distanceToEyes = distanceToEyes;
            this.partHit = partHit;
            this.rightClick = rightClick;
        }

        public RayTracePart getPartHit()
        {
            return partHit;
        }

        public double getDistanceToEyes()
        {
            return distanceToEyes;
        }

        public boolean isRightClick()
        {
            return rightClick;
        }

        public Object performContinuousInteraction()
        {
            return partHit.getContinuousInteraction() == null ? null : partHit.getContinuousInteraction().apply(this);
        }

        public <R> boolean equalsContinuousInteraction(Function<RayTraceResultRotated, R> function)
        {
            return function.equals(partHit.getContinuousInteraction());
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
    public interface IEntityRaytraceable
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
            if(result.getPartHit().getModel() == SpecialModel.KEY_HOLE)
            {
                PacketHandler.instance.sendToServer(new MessageInteractKey((Entity) this));
                return true;
            }

            Minecraft mc = Minecraft.getInstance();
            boolean isContinuous = result.partHit.getContinuousInteraction() != null;
            if(isContinuous || !(mc.objectMouseOver != null && mc.objectMouseOver.getType() == Type.ENTITY && ((EntityRayTraceResult)mc.objectMouseOver).getEntity() == this))
            {
                PlayerEntity player = mc.player;
                boolean notRiding = player.getRidingEntity() != this;
                if(!rightClick && notRiding)
                {
                    mc.playerController.attackEntity(player, (Entity) this);
                    return true;
                }
                if(result.getPartHit().model != null)
                {
                    if(notRiding)
                    {
                        if(player.isCrouching() && !player.isSpectator())
                        {
                            PacketHandler.instance.sendToServer(new MessagePickupVehicle((Entity) this));
                            return true;
                        }
                        if(!isContinuous) interactWithEntity(this, result);
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
        @Nullable
        @OnlyIn(Dist.CLIENT)
        default List<RayTracePart> getApplicableInteractionBoxes()
        {
            return null;
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

        /**
         * Opportunity to draw representations of applicable interaction boxes for the entity
         * 
         * @param tessellator rendered plane tiler
         * @param buffer tessellator's vertex buffer
         */
        @OnlyIn(Dist.CLIENT)
        default void drawInteractionBoxes(Tessellator tessellator, BufferBuilder buffer) {}
    }
}
