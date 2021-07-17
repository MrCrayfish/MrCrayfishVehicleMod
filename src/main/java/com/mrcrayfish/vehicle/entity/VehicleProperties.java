package com.mrcrayfish.vehicle.entity;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.common.entity.PartPosition;
import com.mrcrayfish.vehicle.init.ModEntities;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityType;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLLoader;
import org.apache.commons.io.FileUtils;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class VehicleProperties
{
    private static final double WHEEL_RADIUS = 8.0;
    private static final DecimalFormat FORMAT = new DecimalFormat("#.###");
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, new Serializer()).create();
    private static final Map<ResourceLocation, VehicleProperties> ID_TO_PROPERTIES = new HashMap<>();

    private float axleOffset;
    private float wheelOffset;
    private Vector3d heldOffset = Vector3d.ZERO;
    private Vector3d towBarVec = Vector3d.ZERO;
    private Vector3d trailerOffset = Vector3d.ZERO;
    private boolean canChangeWheels;
    private List<Wheel> wheels = new ArrayList<>();
    private PartPosition bodyPosition = PartPosition.DEFAULT;
    private PartPosition enginePosition;
    private PartPosition fuelPortPosition;
    private PartPosition fuelPortLidPosition;
    private PartPosition keyPortPosition;
    private PartPosition keyPosition;
    private PartPosition displayPosition = PartPosition.DEFAULT;
    private Vector3d frontAxelVec;
    private Vector3d rearAxelVec;
    private List<Seat> seats = new ArrayList<>();
    private IEngineType engineType = EngineType.NONE;
    private boolean colored;

    public VehicleProperties setAxleOffset(float axleOffset)
    {
        this.axleOffset = axleOffset;
        return this;
    }

    public float getAxleOffset()
    {
        return this.axleOffset;
    }

    private void generateWheelScale()
    {
        this.getWheels().stream().filter(Wheel::isAutoScale).forEach(wheel -> {
            double scale = ((this.wheelOffset + wheel.getOffsetY()) * 2) / WHEEL_RADIUS;
            wheel.updateScale(scale);
        });
    }

    private void recalculateWheelOffset()
    {
        this.getWheels().stream().filter(wheel -> !wheel.isAutoScale()).max((w1, w2) -> (int) (this.getLowestSittingPosition(w1) - this.getLowestSittingPosition(w2))).ifPresent(wheel -> {
            this.wheelOffset = this.getLowestSittingPosition(wheel);
        });
    }

    private float getLowestSittingPosition(Wheel wheel)
    {
        double radius = WHEEL_RADIUS * wheel.getScaleY();
        double lowestPosition = radius / 2;
        lowestPosition -= wheel.getOffsetY();
        return (float) lowestPosition;
    }

    public float getWheelOffset()
    {
        return this.wheelOffset;
    }

    public VehicleProperties setHeldOffset(Vector3d heldOffset)
    {
        this.heldOffset = heldOffset;
        return this;
    }

    public Vector3d getHeldOffset()
    {
        return this.heldOffset;
    }

    public VehicleProperties setTowBarPosition(Vector3d towBarVec)
    {
        this.towBarVec = towBarVec;
        return this;
    }

    public Vector3d getTowBarPosition()
    {
        return this.towBarVec;
    }

    public VehicleProperties setTrailerOffset(Vector3d trailerOffset)
    {
        this.trailerOffset = trailerOffset;
        return this;
    }

    public Vector3d getTrailerOffset()
    {
        return this.trailerOffset;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetZ, float scale, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 4.0F, scale, scale, scale, offsetX, 0F, offsetZ, false, particles, render));
        return this;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scale, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 4.0F, scale, scale, scale, offsetX, offsetY, offsetZ, false, particles, render));
        return this;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scaleX, float scaleY, float scaleZ, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 4.0F, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, false, particles, render));
        return this;
    }

    public VehicleProperties addWheel(Wheel.Side side, Wheel.Position position, float offsetX, float offsetY, float offsetZ, float scaleX, float scaleY, float scaleZ, boolean autoScale, boolean particles, boolean render)
    {
        this.wheels.add(new Wheel(side, position, 4.0F, scaleX, scaleY, scaleZ, offsetX, offsetY, offsetZ, autoScale, particles, render));
        return this;
    }

    public List<Wheel> getWheels()
    {
        return this.wheels;
    }

    @Nullable
    public Wheel getFirstFrontWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.FRONT).findFirst().orElse(null);
    }

    @Nullable
    public Wheel getFirstRearWheel()
    {
        return this.wheels.stream().filter(wheel -> wheel.getPosition() == Wheel.Position.REAR).findFirst().orElse(null);
    }

    public VehicleProperties setBodyPosition(PartPosition bodyPosition)
    {
        this.bodyPosition = bodyPosition;
        return this;
    }

    public PartPosition getBodyPosition()
    {
        return this.bodyPosition;
    }

    public VehicleProperties setEnginePosition(PartPosition enginePosition)
    {
        this.enginePosition = enginePosition;
        return this;
    }

    public PartPosition getEnginePosition()
    {
        return this.enginePosition;
    }

    public VehicleProperties setFuelPortPosition(PartPosition fuelPortPosition)
    {
        this.fuelPortPosition = fuelPortPosition;
        this.fuelPortLidPosition = new PartPosition(
                fuelPortPosition.getX() - 6 * fuelPortPosition.getScale(),
                fuelPortPosition.getY(),
                fuelPortPosition.getZ() - 5 * fuelPortPosition.getScale(),
                fuelPortPosition.getRotX(),
                fuelPortPosition.getRotY() - 90,
                fuelPortPosition.getRotZ(),
                fuelPortPosition.getScale());
        return this;
    }

    public PartPosition getFuelPortPosition()
    {
        return this.fuelPortPosition;
    }

    public VehicleProperties setFuelPortLidPosition(PartPosition fuelPortLidPosition)
    {
        this.fuelPortLidPosition = fuelPortLidPosition;
        return this;
    }

    public PartPosition getFuelPortLidPosition()
    {
        return this.fuelPortLidPosition;
    }

    public VehicleProperties setKeyPortPosition(PartPosition keyPortPosition)
    {
        this.keyPortPosition = keyPortPosition;
        this.keyPosition = new PartPosition(keyPortPosition.getX(), keyPortPosition.getY(), keyPortPosition.getZ(), keyPortPosition.getRotX() + 90, 0, 0, 0.15);
        return this;
    }

    public PartPosition getKeyPortPosition()
    {
        return this.keyPortPosition;
    }

    public VehicleProperties setKeyPosition(PartPosition keyPosition)
    {
        this.keyPosition = keyPosition;
        return this;
    }

    public PartPosition getKeyPosition()
    {
        return this.keyPosition;
    }

    public VehicleProperties setDisplayPosition(PartPosition displayPosition)
    {
        this.displayPosition = displayPosition;
        return this;
    }

    public PartPosition getDisplayPosition()
    {
        return this.displayPosition;
    }

    public VehicleProperties setFrontAxelVec(double x, double z)
    {
        this.frontAxelVec = new Vector3d(x, 0, z);
        return this;
    }

    @Nullable
    public Vector3d getFrontAxelVec()
    {
        return this.frontAxelVec;
    }

    public VehicleProperties setRearAxelVec(double x, double z)
    {
        this.rearAxelVec = new Vector3d(x, 0, z);
        return this;
    }

    @Nullable
    public Vector3d getRearAxelVec()
    {
        return this.rearAxelVec;
    }

    public VehicleProperties addSeat(Seat seat)
    {
        this.seats.add(seat);
        return this;
    }

    public List<Seat> getSeats()
    {
        return ImmutableList.copyOf(this.seats);
    }

    public VehicleProperties setEngineType(IEngineType type)
    {
        this.engineType = type;
        return this;
    }

    public IEngineType getEngineType()
    {
        return this.engineType;
    }

    public boolean canChangeWheels()
    {
        return this.canChangeWheels;
    }

    public VehicleProperties setCanChangeWheels(boolean canChangeWheels)
    {
        this.canChangeWheels = canChangeWheels;
        return this;
    }

    public boolean isColored()
    {
        return this.colored;
    }

    public VehicleProperties setColored(boolean coloured)
    {
        this.colored = coloured;
        return this;
    }

    public static void loadProperties()
    {
        for(EntityType<? extends VehicleEntity> entityType : VehicleRegistry.getRegisteredVehicleTypes())
        {
            ID_TO_PROPERTIES.computeIfAbsent(entityType.getRegistryName(), VehicleProperties::loadProperties);
        }
    }

    private static VehicleProperties loadProperties(ResourceLocation id)
    {
        String resource = String.format("/assets/%s/vehicles/%s.json", id.getNamespace(), id.getPath());
        try(InputStream is = VehicleProperties.class.getResourceAsStream(resource))
        {
            VehicleProperties properties = GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), VehicleProperties.class);
            properties.recalculateWheelOffset();
            properties.generateWheelScale();
            return properties;
        }
        catch(JsonParseException | IOException e)
        {
            VehicleMod.LOGGER.error("Couldn't load vehicles properties: " + resource, e);
            return null;
        }
        catch(NullPointerException e)
        {
            VehicleMod.LOGGER.error("Missing vehicle properties file: " + resource, e);
            return null;
        }
    }

    public static VehicleProperties getProperties(EntityType<?> entityType)
    {
        return getProperties(entityType.getRegistryName());
    }

    public static VehicleProperties getProperties(ResourceLocation id)
    {
        VehicleProperties properties = ID_TO_PROPERTIES.get(id);
        if(properties == null)
        {
            throw new IllegalArgumentException("No vehicle properties registered for " + id);
        }
        return properties;
    }

    public static void serialize(EntityType<?> entityType, VehicleProperties properties)
    {
        serialize(entityType.getRegistryName(), properties);
    }

    public static void serialize(ResourceLocation id, VehicleProperties properties)
    {
        try
        {
            String json = GSON.toJson(properties);
            FileUtils.writeStringToFile(new File("output/" + id.getPath() + ".json"), json, "utf-8");
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void generateFiles()
    {
        serialize(ModEntities.ALUMINUM_BOAT.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.0, 0.2, 1.1)).setFuelPortPosition(new PartPosition(-16.0, 3, -18, 0.0, -90.0, 0.0, 0.25)).setHeldOffset(new Vector3d(0.0, 0.0, 0.0)).setDisplayPosition(new PartPosition(1.0F)).addSeat(new Seat(new Vector3d(-7, 8, -15), true)).addSeat(new Seat(new Vector3d(7, 6, -15))).addSeat(new Seat(new Vector3d(-7, 6, 3))).addSeat(new Seat(new Vector3d(7, 6, 3))).setEngineType(EngineType.SMALL_MOTOR).setColored(true));
        serialize(ModEntities.ATV.get(), new VehicleProperties().setAxleOffset(-1.5F).setBodyPosition(new PartPosition(1.25)).setFuelPortPosition(new PartPosition(0, 6.55, 5.0, -90, 0, 0, 0.35)).setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5)).setHeldOffset(new Vector3d(4.0, 3.5, 0.0)).setTowBarPosition(new Vector3d(0.0, 0.0, -20.8)).setTrailerOffset(new Vector3d(0.0, 0.0, -0.55)).setDisplayPosition(new PartPosition(1.5F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.15625F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 10.5F, 1.15625F, true, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 4.0F, -10.5F, 1.15625F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 4.0F, -10.5F, 1.15625F, true, true).setFrontAxelVec(0, 10.5).setRearAxelVec(0, -10.5).addSeat(new Seat(new Vector3d(0, 5, -3), true)).addSeat(new Seat(new Vector3d(0, 5.5, -12))).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.BUMPER_CAR.get(), new VehicleProperties().setAxleOffset(-1.5F).setBodyPosition(new PartPosition(1.2)).setFuelPortPosition(new PartPosition(-8.0, 6, -8.0, 0, -90, 0, 0.25)).setHeldOffset(new Vector3d(6.0, 0.0, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.03125, -0.5625)).setDisplayPosition(new PartPosition(1.5F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.47F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7.0F, 8.5F, 0.47F, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7.0F, -8.5F, 0.47F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7.0F, -8.5F, 0.47F, false, true).setFrontAxelVec(0, 8.5).setRearAxelVec(0, -8.5).addSeat(new Seat(new Vector3d(0, 1, -6), true)).setEngineType(EngineType.ELECTRIC_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.DIRT_BIKE.get(), new VehicleProperties().setAxleOffset(0.0F).setBodyPosition(new PartPosition(1.0)).setEnginePosition(new PartPosition(0, 1, 0, 0, 180, 0, 0.6)).setFuelPortPosition(new PartPosition(0, 12.1, 3, 67.5, 180, 0, 0.35)).setHeldOffset(new Vector3d(0.0, 3.5, 0.0)).setDisplayPosition(new PartPosition(1.4F)).setTrailerOffset(new Vector3d(0, -0.0625, -0.3125)).addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, 0.0F, 14.08F, 0.9375F, 1.4F, 1.4F, false, false).addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, 0.0F, -11.61F, 0.9375F, 1.4F, 1.4F, true, true).setFrontAxelVec(0, 14.08).setRearAxelVec(0, -11.61).addSeat(new Seat(new Vector3d(0, 8, -2), true)).addSeat(new Seat(new Vector3d(0, 9, -9))).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.DUNE_BUGGY.get(), new VehicleProperties().setAxleOffset(-2.3F).setBodyPosition(new PartPosition(1.3)).setFuelPortPosition(new PartPosition(0, 3, -7.0, 0, 180, 0, 0.25)).setHeldOffset(new Vector3d(2.0, 0.0, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.025, -0.25)).setDisplayPosition(new PartPosition(1.75F)).addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 2.4F, 5.3F, 0.625F, false, false).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 2.4F, -5.7F, 0.625F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 2.4F, -5.7F, 0.625F, true, true).setFrontAxelVec(0, 5.3).setRearAxelVec(0, -5.7).addSeat(new Seat(new Vector3d(0, 2, -3), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true));
        serialize(ModEntities.GO_KART.get(), new VehicleProperties().setAxleOffset(-2.5F).setBodyPosition(new PartPosition(1.0)).setEnginePosition(new PartPosition(0, 2, -9, 0, 180, 0, 0.8)).setHeldOffset(new Vector3d(3.0D, 0.5D, 0.0D)).setTrailerOffset(new Vector3d(0D, -0.03125D, -0.375D)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.5F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 13.5F, 0.625F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 13.5F, 0.625F, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -8.5F, 0.625F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -8.5F, 0.625F, true, true).setFrontAxelVec(0, 13.5).setRearAxelVec(0, -8.5).addSeat(new Seat(new Vector3d(0, -2, -1), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.GOLF_CART.get(), new VehicleProperties().setAxleOffset(-0.5F).setBodyPosition(new PartPosition(1.15)).setFuelPortPosition(new PartPosition(-13, 3.5, -6, 0, -90, 0, 0.25)).setKeyPortPosition(new PartPosition(-8.5, 2.75, 8.5, -67.5, 0, 0, 0.5)).setHeldOffset(new Vector3d(1.5D, 2.5D, 0.0D)).setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.1F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 16.0F, 1.1F, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, -12.5F, 1.1F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, -12.5F, 1.1F, true, true).setFrontAxelVec(0, 16.0).setRearAxelVec(0, -12.5).addSeat(new Seat(new Vector3d(5.5, 5, -6), true)).addSeat(new Seat(new Vector3d(-5.5, 5, -6))).addSeat(new Seat(new Vector3d(5.5, 5, -15), 180F)).addSeat(new Seat(new Vector3d(-5.5, 5, -15), 180F)).setEngineType(EngineType.ELECTRIC_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.JET_SKI.get(), new VehicleProperties().setAxleOffset(2.75F).setBodyPosition(new PartPosition(0.0, 0.0, 0.25, 0.0, 0.0, 0.0, 1.25)).setFuelPortPosition(new PartPosition(0.0, 9.25, 8.5, -90, 0, 0, 0.35)).setHeldOffset(new Vector3d(6.0, 0.0, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.09375, -0.65)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.45F, 0.0F, 0.0F, 0.0F, 1.5F)).addSeat(new Seat(new Vector3d(0, 5, 0), true)).addSeat(new Seat(new Vector3d(0, 5, -7))).setEngineType(EngineType.SMALL_MOTOR).setColored(true));
        serialize(ModEntities.LAWN_MOWER.get(), new VehicleProperties().setAxleOffset(-2.0F).setBodyPosition(new PartPosition(1.25)).setFuelPortPosition(new PartPosition(-4.50, 9.5, 4.0, 0, -90, 0, 0.2)).setKeyPortPosition(new PartPosition(-5, 4.5, 6.5, -45, 0, 0, 0.5)).setHeldOffset(new Vector3d(12.0, -1.5, 0.0)).setTowBarPosition(new Vector3d(0.0, 0.0, -20.0)).setTrailerOffset(new Vector3d(0.0, -0.01, -1.0)).setDisplayPosition(new PartPosition(1.5F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 0.0F, 0.0F, 0.0F, true, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 6.0F, 0.0F, 13.5F, 0.0F, 0.0F, 0.0F, true, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 0.97F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 5.0F, 0.8F, -10.7F, 0.97F, true, true).setFrontAxelVec(0, 13.5).setRearAxelVec(0, -10.7).addSeat(new Seat(new Vector3d(0, 7, -9), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.MINI_BIKE.get(), new VehicleProperties().setAxleOffset(-1.7F).setBodyPosition(new PartPosition(1.05)).setEnginePosition(new PartPosition(0, 1, 2.5, 0, 180F, 0, 0.7)).setHeldOffset(new Vector3d(6.0, 0.0, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.0625, -0.5)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0F, 1.5F)).addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 1.03F, true, true).addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, -0.5F + 1.7F * 0.0625F, 13.0F, 1.03F, true, false).setFrontAxelVec(0, 13).setRearAxelVec(0, -6.7).addSeat(new Seat(new Vector3d(0, 7, -2), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.MINI_BUS.get(), new VehicleProperties().setAxleOffset(1.0F).setBodyPosition(new PartPosition(1.3)).setFuelPortPosition(new PartPosition(-12.0, 8.0, -8.75, 0, -90, 0, 0.25)).setKeyPortPosition(new PartPosition(0, 6.75, 19.5, -67.5, 0, 0, 0.5)).setHeldOffset(new Vector3d(0.0, 3.5, 0.0)).setTowBarPosition(new Vector3d(0.0, 0.0, -33.0)).setDisplayPosition(new PartPosition(1.0F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 9.0F, 0.0F, 13.5F, 0.9375F, 1.19F, 1.19F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 9.0F, 0.0F, 13.5F, 0.9375F, 1.19F, 1.19F, true, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 9.0F, 0.0F, -13.5F, 0.9375F, 1.19F, 1.19F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 9.0F, 0.0F, -13.5F, 0.9375F, 1.19F, 1.19F, true, true).setFrontAxelVec(0, 14.5).setRearAxelVec(0, -14.5).addSeat(new Seat(new Vector3d(4.5, 2, 11), true)).addSeat(new Seat(new Vector3d(-4.5, 2, 11))).addSeat(new Seat(new Vector3d(4.5, 2, -3))).addSeat(new Seat(new Vector3d(-4.5, 2, -3))).addSeat(new Seat(new Vector3d(4.5, 2, -15))).setEngineType(EngineType.LARGE_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.MOPED.get(), new VehicleProperties().setAxleOffset(-1.0F).setBodyPosition(new PartPosition(1.2)).setFuelPortPosition(new PartPosition(-2.5, 4.2, -1.5, 0, -90, 0, 0.2)).setHeldOffset(new Vector3d(4.5, 2.0, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.03125, -0.65)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F)).addWheel(Wheel.Side.NONE, Wheel.Position.REAR, 0.0F, -6.7F, 0.8F, true, true).addWheel(Wheel.Side.NONE, Wheel.Position.FRONT, 0.0F, 0.0F, 14.0884F, 0.6F, 0.8F, 0.8F, true, false).setFrontAxelVec(0, 14.5).setRearAxelVec(0, -6.7).addSeat(new Seat(new Vector3d(0, 4, -1), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.OFF_ROADER.get(), new VehicleProperties().setAxleOffset(-1.0F).setBodyPosition(new PartPosition(1.4)).setFuelPortPosition(new PartPosition(-12.0, 8.5, -6.5, 0, -90, 0, 0.25)).setKeyPortPosition(new PartPosition(0, 7, 6.2, -67.5, 0, 0, 0.5)).setHeldOffset(new Vector3d(0.0, 3.5, 0.0)).setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.1F, 0.0F, 0.0F, 0.0F, 1.0F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 10.0F, 14.5F, 1.4F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 10.0F, 14.5F, 1.4F, true, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 10.0F, -14.5F, 1.4F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 10.0F, -14.5F, 1.4F, true, true).setFrontAxelVec(0, 14.5).setRearAxelVec(0, -14.5).addSeat(new Seat(new Vector3d(5, 4, -3), true)).addSeat(new Seat(new Vector3d(-5, 4, -3))).addSeat(new Seat(new Vector3d(5, 11.5, -14.5))).addSeat(new Seat(new Vector3d(-5, 3.5, -18.9))).setEngineType(EngineType.LARGE_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.SHOPPING_CART.get(), new VehicleProperties().setAxleOffset(-1.0F).setBodyPosition(new PartPosition(1.05)).setHeldOffset(new Vector3d(4.0, 9.25, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.03125, -0.25)).setDisplayPosition(new PartPosition(1.45F)).addWheel(Wheel.Side.LEFT, Wheel.Position.NONE, 5.75F, -10.5F, 0.47F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.NONE, 5.75F, -10.5F, 0.47F, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.47F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 4.0F, 9.5F, 0.47F, false, true).setFrontAxelVec(0, 9.5).setRearAxelVec(0, -10.5).addSeat(new Seat(new Vector3d(0, 7, -4), true)).setEngineType(EngineType.NONE).setCanChangeWheels(true));
        serialize(ModEntities.SMART_CAR.get(), new VehicleProperties().setAxleOffset(-1.7F).setBodyPosition(new PartPosition(1.25)).setFuelPortPosition(new PartPosition(-9.0, 8.7, -12.3, 0, -90, 0, 0.25)).setHeldOffset(new Vector3d(3.0, 1.0, 0.0)).setTowBarPosition(new Vector3d(0.0, 0.0, -24.5)).setDisplayPosition(new PartPosition(1.35F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 7F, 12F, 0.9375F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 7F, 12F, 0.9375F, true, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 7F, -12F, 0.9375F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 7F, -12F, 0.9375F, false, true).setFrontAxelVec(0, 12).setRearAxelVec(0, -12).addSeat(new Seat(new Vector3d(0, 0.5, -2), true)).setEngineType(EngineType.ELECTRIC_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.SPEED_BOAT.get(), new VehicleProperties().setAxleOffset(2.5F).setBodyPosition(new PartPosition(0.0, -0.03125, 0.6875, 1.0)).setFuelPortPosition(new PartPosition(0.0, 5.25, -20.5, -90.0, 0.0, 0.0, 0.65)).setHeldOffset(new Vector3d(6.0, -0.5, 0.0)).setTrailerOffset(new Vector3d(0.0, -0.09375, -0.75)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.65F, 0.0F, 0.0F, 0.0F, 1.25F)).addSeat(new Seat(new Vector3d(0, 0, 0), true)).setEngineType(EngineType.LARGE_MOTOR).setColored(true));
        serialize(ModEntities.SPORTS_PLANE.get(), new VehicleProperties().setBodyPosition(new PartPosition(0, 11 * 0.0625, -8 * 0.0625, 0, 0, 0, 1.8)).setFuelPortPosition(new PartPosition(-4.35, 4, -6, 0, -112.5, 0, 0.25)).setKeyPortPosition(new PartPosition(0, 3.75, 12.5, -67.5, 0, 0, 0.5)).setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.85F)).addSeat(new Seat(new Vector3d(0, 6, 0), true)).setEngineType(EngineType.LARGE_MOTOR).setColored(true));
        serialize(ModEntities.TRACTOR.get(), new VehicleProperties().setAxleOffset(-3.0F).setBodyPosition(new PartPosition(1.0)).setEnginePosition(new PartPosition(0, 6, 7.5, 0, 0, 0, 0.85)).setFuelPortPosition(new PartPosition(-6.0, 9.5, -0.5, 0, -90, 0, 0.3)).setKeyPortPosition(new PartPosition(-2.75, 12, -1.75, -45.0, 0, 0, 0.5)).setHeldOffset(new Vector3d(0.0, 3.5, 0.0)).setTowBarPosition(new Vector3d(0.0, 0.0, -24.5)).setDisplayPosition(new PartPosition(1.25F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 0.9375F, 0F, 0F, true, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0F, 14.0F, 0.9375F, 0F, 0F, true, true, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 1.875F, 2.8F, 2.8F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 5.5F, -14.5F, 1.875F, 2.8F, 2.8F, true, true).setFrontAxelVec(0, 14.0).setRearAxelVec(0, -14.5).addSeat(new Seat(new Vector3d(0, 9, -14), true)).setEngineType(EngineType.LARGE_MOTOR).setCanChangeWheels(true).setColored(true));
        serialize(ModEntities.FERTILIZER.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)).setColored(true));
        serialize(ModEntities.FLUID_TRAILER.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1)).setHeldOffset(new Vector3d(0D, 3D, 0D)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)).setColored(true));
        serialize(ModEntities.SEEDER.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)).setColored(true));
        serialize(ModEntities.STORAGE_TRAILER.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1)).setTowBarPosition(new Vector3d(0.0, 0.0, -12.0)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)).setColored(true));
        serialize(ModEntities.VEHICLE_TRAILER.get(), new VehicleProperties().setBodyPosition(new PartPosition(0.0, 0.325, 0.0, 0.0, 0.0, 0.0, 1.1)).setHeldOffset(new Vector3d(0D, 3D, 0D)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.15F, 0.0F, 0.0F, 0.0F, 1.35F)).setColored(true));
        serialize(new ResourceLocation("vehicle", "bath"), new VehicleProperties().setBodyPosition(new PartPosition(1.0)).setHeldOffset(new Vector3d(4.0, 3.5, 0.0)).setTrailerOffset(new Vector3d(0.0, 0.0, -0.4375)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F)).addSeat(new Seat(new Vector3d(0, 0, 0), true)).setEngineType(EngineType.NONE));
        serialize(new ResourceLocation("vehicle", "sofa_car"), new VehicleProperties().setAxleOffset(-1.5F).setBodyPosition(new PartPosition(0, -0.0625, 0, 0, 0, 0, 1.0)).setFuelPortPosition(new PartPosition(0, 2, 8, 0, 0, 0, 0.5)).setHeldOffset(new Vector3d(2.0, 2.0, 0.0)).setTrailerOffset(new Vector3d(0.0, 0.0, -0.25)).setDisplayPosition(new PartPosition(0.0F, 0.0F, -0.25F, 0.0F, 0.0F, 0.0F, 1.5F)).addWheel(Wheel.Side.LEFT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.1F, false, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.FRONT, 8.0F, 0.0625F, 7.0F, 1.1F, false, true).addWheel(Wheel.Side.LEFT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.1F, true, true).addWheel(Wheel.Side.RIGHT, Wheel.Position.REAR, 8.0F, 0.0625F, -7.0F, 1.1F, true, true).setFrontAxelVec(0, 7.0).setRearAxelVec(0, -7.0).addSeat(new Seat(new Vector3d(0, 5, 0), true)).setEngineType(EngineType.SMALL_MOTOR).setCanChangeWheels(true));
        serialize(new ResourceLocation("vehicle", "sofacopter"), new VehicleProperties().setBodyPosition(new PartPosition(0, 0, 0.0625, 0, 0, 0, 1)).setFuelPortPosition(new PartPosition(0.0, 1.5, 8.0, 0, 0, 0, 0.45)).setKeyPortPosition(new PartPosition(-9.25, 8, 5, 0, 0, 0, 0.8)).setDisplayPosition(new PartPosition(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 1.25F)).addSeat(new Seat(new Vector3d(0, 0, 0), true)).setEngineType(EngineType.SMALL_MOTOR));
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public static void onKeyInput(InputEvent.KeyInputEvent event)
    {
        if(FMLLoader.isProduction())
            return;

        Minecraft minecraft = Minecraft.getInstance();
        if(minecraft.overlay != null)
            return;

        if(event.getAction() != GLFW.GLFW_PRESS)
            return;

        if(event.getKey() == GLFW.GLFW_KEY_RIGHT_BRACKET)
        {
            for(EntityType<? extends VehicleEntity> entityType : VehicleRegistry.getRegisteredVehicleTypes())
            {
                ID_TO_PROPERTIES.put(entityType.getRegistryName(), loadProperties(entityType.getRegistryName()));
            }
        }
        else if(event.getKey() == GLFW.GLFW_KEY_LEFT_BRACKET)
        {
            generateFiles();
        }
    }

    public static class Serializer implements JsonDeserializer<VehicleProperties>, JsonSerializer<VehicleProperties>
    {
        @Override
        public JsonElement serialize(VehicleProperties src, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject object = new JsonObject();

            JsonObject general = new JsonObject();
            if(src.engineType != EngineType.NONE) general.addProperty("engineType", src.engineType.getId().toString());
            if(src.colored) general.addProperty("canBeColored", true);
            if(src.canChangeWheels) general.addProperty("canChangeWheels", true);
            if(general.size() > 0) object.add("general", general);

            JsonObject axles = new JsonObject();
            if(src.axleOffset != 0) axles.addProperty("offsetToGround", src.axleOffset);
            if(src.frontAxelVec != null) axles.addProperty("lengthToFront", src.frontAxelVec.z);
            if(src.rearAxelVec != null) axles.addProperty("lengthToRear", src.rearAxelVec.z);
            if(axles.size() > 0) object.add("axles", axles);

            JsonObject display = new JsonObject();
            this.addVector3dProperty(display, "held", src.heldOffset);
            this.addVector3dProperty(display, "trailer", src.trailerOffset);
            this.addPartPositionProperty(display, "gui", src.displayPosition);
            if(display.size() > 0) object.add("display", display);

            JsonObject position = new JsonObject();
            this.addPartPositionProperty(position, "body", src.bodyPosition);
            this.addPartPositionProperty(position, "engine", src.enginePosition);
            this.addPartPositionProperty(position, "fuelPort", src.fuelPortPosition);
            this.addPartPositionProperty(position, "keyPort", src.keyPortPosition);
            this.addVector3dProperty(position, "towBar", src.towBarVec);
            if(position.size() > 0) object.add("position", position);

            this.writeWheels(src, object);
            this.writeSeats(src, object);

            return object;
        }

        @Override
        public VehicleProperties deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = JSONUtils.convertToJsonObject(element, "vehicle property");
            VehicleProperties properties = new VehicleProperties();

            JsonObject general = JSONUtils.getAsJsonObject(object, "general", new JsonObject());
            properties.setEngineType(this.getAsEngineType(general, "engineType", EngineType.NONE));
            properties.setColored(JSONUtils.getAsBoolean(general, "canBeColored", false));
            properties.setCanChangeWheels(JSONUtils.getAsBoolean(general, "canChangeWheels", false));

            JsonObject axles = JSONUtils.getAsJsonObject(object, "axles", new JsonObject());
            properties.setAxleOffset(JSONUtils.getAsFloat(axles, "offsetToGround", 0F));
            if(axles.has("lengthToFront")) properties.setFrontAxelVec(0, JSONUtils.getAsFloat(axles, "lengthToFront", 0F));
            if(axles.has("lengthToRear")) properties.setRearAxelVec(0, JSONUtils.getAsFloat(axles, "lengthToRear", 0F));

            JsonObject display = JSONUtils.getAsJsonObject(object, "display", new JsonObject());
            properties.setHeldOffset(this.getAsVector3d(display, "held", Vector3d.ZERO));
            properties.setTrailerOffset(this.getAsVector3d(display, "trailer", Vector3d.ZERO));
            this.callIfNonNull(properties::setDisplayPosition, this.getAsPartPosition(display, "gui"));

            JsonObject positions = JSONUtils.getAsJsonObject(object, "position", new JsonObject());
            this.callIfNonNull(properties::setBodyPosition, this.getAsPartPosition(positions, "body"));
            this.callIfNonNull(properties::setEnginePosition, this.getAsPartPosition(positions, "engine"));
            this.callIfNonNull(properties::setFuelPortPosition, this.getAsPartPosition(positions, "fuelPort"));
            this.callIfNonNull(properties::setKeyPortPosition, this.getAsPartPosition(positions, "keyPort"));
            properties.setTowBarPosition(this.getAsVector3d(positions, "towBar", Vector3d.ZERO));

            this.readWheels(properties, object);
            this.readSeats(properties, object);

            return properties;
        }

        private IEngineType getAsEngineType(JsonObject object, String memberName, IEngineType defaultValue)
        {
            String rawId = JSONUtils.getAsString(object, memberName, "");
            if(!rawId.isEmpty())
            {
                ResourceLocation id = new ResourceLocation(rawId);
                IEngineType type = VehicleRegistry.getEngineTypeFromId(id);
                return type != null ? type : defaultValue;
            }
            return defaultValue;
        }

        private Vector3d getAsVector3d(JsonObject object, String memberName, Vector3d defaultValue)
        {
            if(object.has(memberName))
            {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(object, memberName);
                if(jsonArray.size() != 3)
                {
                    throw new JsonParseException("Expected 3 " + memberName + " values, found: " + jsonArray.size());
                }
                else
                {
                    double x = JSONUtils.convertToFloat(jsonArray.get(0), memberName + "[0]");
                    double y = JSONUtils.convertToFloat(jsonArray.get(1), memberName + "[1]");
                    double z = JSONUtils.convertToFloat(jsonArray.get(2), memberName + "[2]");
                    return new Vector3d(x, y, z);
                }
            }
            return defaultValue;
        }

        private void readWheels(VehicleProperties properties, JsonObject object)
        {
            if(object.has("wheels"))
            {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(object, "wheels");
                for(JsonElement element : jsonArray)
                {
                    JsonObject wheelObject = element.getAsJsonObject();
                    Vector3d offset = this.getAsVector3d(wheelObject, "offset", Vector3d.ZERO);
                    Vector3d scale = this.getAsVector3d(wheelObject, "scale", new Vector3d(1, 1, 1));
                    Wheel.Side side = this.getAsEnum(wheelObject, "side", Wheel.Side.class, Wheel.Side.NONE);
                    Wheel.Position position = this.getAsEnum(wheelObject, "position", Wheel.Position.class, Wheel.Position.NONE);
                    boolean autoScale = JSONUtils.getAsBoolean(wheelObject, "autoScale", false);
                    boolean particles = JSONUtils.getAsBoolean(wheelObject, "particles", false);
                    boolean render = JSONUtils.getAsBoolean(wheelObject, "render", true);
                    properties.addWheel(side, position, (float) offset.x, (float) offset.y, (float) offset.z, (float) scale.x, (float) scale.y, (float) scale.z, autoScale, particles, render);
                }
            }
        }

        private void writeWheels(VehicleProperties properties, JsonObject object)
        {
            if(properties.getWheels().size() > 0)
            {
                JsonArray wheels = new JsonArray();
                for(Wheel wheel : properties.getWheels())
                {
                    JsonObject wheelObject = new JsonObject();
                    wheelObject.addProperty("side", wheel.getSide().name().toLowerCase(Locale.ENGLISH));
                    wheelObject.addProperty("position", wheel.getPosition().name().toLowerCase(Locale.ENGLISH));
                    this.addVector3dProperty(wheelObject, "offset", wheel.getOffset());
                    this.addVector3dProperty(wheelObject, "scale", wheel.getScale());
                    if(wheel.isAutoScale()) wheelObject.addProperty("autoScale", wheel.isAutoScale());
                    if(wheel.shouldSpawnParticles()) wheelObject.addProperty("particles", wheel.shouldSpawnParticles());
                    if(!wheel.shouldRender()) wheelObject.addProperty("render", wheel.shouldRender());
                    wheels.add(wheelObject);
                }
                object.add("wheels", wheels);
            }
        }

        private void readSeats(VehicleProperties properties, JsonObject object)
        {
            if(object.has("seats"))
            {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(object, "seats");
                for(JsonElement element : jsonArray)
                {
                    JsonObject seatObject = element.getAsJsonObject();
                    Vector3d position = this.getAsVector3d(seatObject, "position", Vector3d.ZERO);
                    boolean driver = JSONUtils.getAsBoolean(seatObject, "driver", false);
                    float yawOffset = JSONUtils.getAsFloat(seatObject, "yawOffset", 0F);
                    properties.addSeat(new Seat(position, driver, yawOffset));
                }
            }
        }

        private void writeSeats(VehicleProperties properties, JsonObject object)
        {
            if(properties.getSeats().size() > 0)
            {
                JsonArray seats = new JsonArray();
                for(Seat seat : properties.getSeats())
                {
                    JsonObject seatObject = new JsonObject();
                    this.addVector3dProperty(seatObject, "position", seat.getPosition());
                    if(seat.isDriverSeat()) seatObject.addProperty("driver", seat.isDriverSeat());
                    if(seat.getYawOffset() != 0) seatObject.addProperty("yawOffset", seat.getYawOffset());
                    seats.add(seatObject);
                }
                object.add("seats", seats);
            }
        }

        private <T extends Enum> T getAsEnum(JsonObject object, String memberName, Class<T> enumClass, T defaultValue)
        {
            if(object.has(memberName))
            {
                String enumString = JSONUtils.getAsString(object, memberName);
                return Stream.of(enumClass.getEnumConstants()).filter(side -> side.name().equalsIgnoreCase(enumString)).findFirst().orElse(defaultValue);
            }
            return defaultValue;
        }

        @Nullable
        private PartPosition getAsPartPosition(JsonObject object, String memberName)
        {
            if(object.has(memberName))
            {
                JsonObject partPositionObject = object.getAsJsonObject(memberName);
                Vector3d translate = this.getAsVector3d(partPositionObject, "translate", Vector3d.ZERO);
                Vector3d rotation = this.getAsVector3d(partPositionObject, "rotation", Vector3d.ZERO);
                double scale = JSONUtils.getAsFloat(partPositionObject, "scale", 1);
                return new PartPosition(translate.x, translate.y, translate.z, rotation.x, rotation.y, rotation.z, scale);
            }
            return null;
        }

        private <T> void callIfNonNull(Consumer<T> consumer, @Nullable T value)
        {
            if(value != null)
            {
                consumer.accept(value);
            }
        }

        private void addVector3dProperty(JsonObject parent, String memberName, Vector3d vec)
        {
            if(vec != null && !vec.equals(Vector3d.ZERO))
            {
                JsonArray array = new JsonArray();
                array.add(Double.parseDouble(FORMAT.format(vec.x)));
                array.add(Double.parseDouble(FORMAT.format(vec.y)));
                array.add(Double.parseDouble(FORMAT.format(vec.z)));
                parent.add(memberName, array);
            }
        }

        private void addPartPositionProperty(JsonObject parent, String memberName, PartPosition position)
        {
            if(position != null && position != PartPosition.DEFAULT)
            {
                JsonObject partPositionObject = new JsonObject();
                this.addVector3dProperty(partPositionObject, "translate", position.getTranslate());
                this.addVector3dProperty(partPositionObject, "rotation", position.getRotation());
                if(position.getScale() != 1) partPositionObject.addProperty("scale", Double.parseDouble(FORMAT.format(position.getScale())));
                if(partPositionObject.size() > 0) parent.add(memberName, partPositionObject);
            }
        }
    }
}
