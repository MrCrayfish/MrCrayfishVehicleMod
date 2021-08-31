package com.mrcrayfish.vehicle.entity.properties;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.VehicleMod;
import com.mrcrayfish.vehicle.client.CameraProperties;
import com.mrcrayfish.vehicle.common.Seat;
import com.mrcrayfish.vehicle.common.VehicleRegistry;
import com.mrcrayfish.vehicle.common.entity.Transform;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.Wheel;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
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
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Dist.CLIENT)
public class VehicleProperties
{
    private static final double WHEEL_RADIUS = 8.0;
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, new Serializer()).create();
    private static final Map<ResourceLocation, VehicleProperties> ID_TO_PROPERTIES = new HashMap<>();
    private static final Map<ResourceLocation, ExtendedProperties> GLOBAL_EXTENDED_PROPERTIES = new HashMap<>();

    public static final float DEFAULT_MAX_HEALTH = 100F;
    public static final float DEFAULT_AXLE_OFFSET = 0F;
    public static final Vector3d DEFAULT_HELD_OFFSET = Vector3d.ZERO;
    public static final Vector3d DEFAULT_TOW_BAR_OFFSET = Vector3d.ZERO;
    public static final Vector3d DEFAULT_TRAILER_OFFSET = Vector3d.ZERO;
    public static final boolean DEFAULT_CAN_CHANGE_WHEELS = false;
    public static final Transform DEFAULT_BODY_TRANSFORM = Transform.DEFAULT;
    public static final Transform DEFAULT_DISPLAY_TRANSFORM = Transform.DEFAULT;
    public static final boolean DEFAULT_CAN_BE_PAINTED = false;

    //TODO ideas: canBeDamaged, canPickUp, canBePlacedInTrailer
    private final float maxHealth;
    private final float axleOffset;
    private final float wheelOffset;
    private final Vector3d heldOffset;
    private final Vector3d towBarOffset;
    private final Vector3d trailerOffset;
    private final boolean canChangeWheels;
    private final List<Wheel> wheels;
    private final Transform bodyTransform;
    private final Transform displayTransform;
    private final List<Seat> seats;
    private final boolean canBePainted;
    private final CameraProperties camera;
    private final ImmutableMap<ResourceLocation, ExtendedProperties> extended;

    private VehicleProperties(float maxHealth, float axleOffset, float wheelOffset, Vector3d heldOffset, Vector3d towBarOffset, Vector3d trailerOffset, boolean canChangeWheels, List<Wheel> wheels, Transform bodyTransform, Transform displayTransform, List<Seat> seats, boolean canBePainted, CameraProperties camera, Map<ResourceLocation, ExtendedProperties> extended)
    {
        this.maxHealth = maxHealth;
        this.axleOffset = axleOffset;
        this.wheelOffset = wheelOffset;
        this.heldOffset = heldOffset;
        this.towBarOffset = towBarOffset;
        this.trailerOffset = trailerOffset;
        this.canChangeWheels = canChangeWheels;
        this.wheels = wheels;
        this.bodyTransform = bodyTransform;
        this.displayTransform = displayTransform;
        this.seats = seats;
        this.canBePainted = canBePainted;
        this.camera = camera;
        this.extended = ImmutableMap.copyOf(extended);
    }

    public float getMaxHealth()
    {
        return this.maxHealth;
    }

    public float getAxleOffset()
    {
        return this.axleOffset;
    }

    public float getWheelOffset()
    {
        return this.wheelOffset;
    }

    public Vector3d getHeldOffset()
    {
        return this.heldOffset;
    }

    public Vector3d getTowBarOffset()
    {
        return this.towBarOffset;
    }

    public Vector3d getTrailerOffset()
    {
        return this.trailerOffset;
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

    public Transform getBodyTransform()
    {
        return this.bodyTransform;
    }

    public Transform getDisplayTransform()
    {
        return this.displayTransform;
    }

    public List<Seat> getSeats()
    {
        return ImmutableList.copyOf(this.seats);
    }

    public boolean canChangeWheels()
    {
        return this.canChangeWheels;
    }

    public boolean isCanBePainted()
    {
        return this.canBePainted;
    }

    public CameraProperties getCamera()
    {
        return this.camera;
    }

    @SuppressWarnings("unchecked")
    public <T extends ExtendedProperties> T getExtended(Class<T> properties)
    {
        ResourceLocation id = ExtendedProperties.getId(properties);
        T t = (T) this.extended.get(id);
        if(t != null)
        {
            return t;
        }
        GLOBAL_EXTENDED_PROPERTIES.computeIfAbsent(id, id2 -> ExtendedProperties.create(id2, new JsonObject()));
        return (T) GLOBAL_EXTENDED_PROPERTIES.get(id);
    }

    public ImmutableMap<ResourceLocation, ExtendedProperties> getExtendedMap()
    {
        return this.extended;
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
            return GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), VehicleProperties.class);
        }
        catch(JsonParseException | IOException e)
        {
            VehicleMod.LOGGER.error("Couldn't load vehicles properties: " + resource, e);
        }
        catch(NullPointerException e)
        {
            VehicleMod.LOGGER.error("Missing vehicle properties file: " + resource, e);
        }
        return null;
    }

    public static VehicleProperties get(EntityType<?> entityType)
    {
        return get(entityType.getRegistryName());
    }

    public static VehicleProperties get(ResourceLocation id)
    {
        VehicleProperties properties = ID_TO_PROPERTIES.get(id);
        if(properties == null)
        {
            throw new IllegalArgumentException("No vehicle properties registered for " + id);
        }
        return properties;
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
    }

    public static class Serializer implements JsonDeserializer<VehicleProperties>, JsonSerializer<VehicleProperties>
    {
        @Override
        public JsonElement serialize(VehicleProperties properties, Type typeOfSrc, JsonSerializationContext context)
        {
            JsonObject object = new JsonObject();
            ExtraJSONUtils.write(object, "canBePainted", properties.canBePainted, DEFAULT_CAN_BE_PAINTED);
            ExtraJSONUtils.write(object, "canChangeWheels", properties.canChangeWheels, DEFAULT_CAN_CHANGE_WHEELS);
            ExtraJSONUtils.write(object, "offsetToGround", properties.axleOffset, DEFAULT_AXLE_OFFSET);
            ExtraJSONUtils.write(object, "heldOffset", properties.heldOffset, DEFAULT_HELD_OFFSET);
            ExtraJSONUtils.write(object, "trailerOffset", properties.trailerOffset, DEFAULT_TRAILER_OFFSET);
            ExtraJSONUtils.write(object, "towBarOffset", properties.towBarOffset, DEFAULT_TOW_BAR_OFFSET);
            ExtraJSONUtils.write(object, "displayTransform", properties.displayTransform, DEFAULT_DISPLAY_TRANSFORM);
            ExtraJSONUtils.write(object, "bodyTransform", properties.bodyTransform, DEFAULT_BODY_TRANSFORM);
            this.writeWheels(properties, object);
            this.writeSeats(properties, object);
            this.writeCamera(properties, object);
            this.writeExtended(properties, object);
            return object;
        }

        @Override
        public VehicleProperties deserialize(JsonElement element, Type type, JsonDeserializationContext context) throws JsonParseException
        {
            JsonObject object = JSONUtils.convertToJsonObject(element, "vehicle property");
            VehicleProperties.Builder builder = VehicleProperties.builder();
            builder.setColored(JSONUtils.getAsBoolean(object, "canBePainted", DEFAULT_CAN_BE_PAINTED));
            builder.setCanChangeWheels(JSONUtils.getAsBoolean(object, "canChangeWheels", DEFAULT_CAN_CHANGE_WHEELS));
            builder.setAxleOffset(JSONUtils.getAsFloat(object, "offsetToGround", DEFAULT_AXLE_OFFSET));
            builder.setHeldOffset(ExtraJSONUtils.getAsVector3d(object, "heldOffset", DEFAULT_HELD_OFFSET));
            builder.setTrailerOffset(ExtraJSONUtils.getAsVector3d(object, "trailerOffset", DEFAULT_TRAILER_OFFSET));
            builder.setTowBarOffset(ExtraJSONUtils.getAsVector3d(object, "towBarOffset", DEFAULT_TOW_BAR_OFFSET));
            builder.setDisplayTransform(ExtraJSONUtils.getAsTransform(object, "displayTransform", DEFAULT_DISPLAY_TRANSFORM));
            builder.setBodyTransform(ExtraJSONUtils.getAsTransform(object, "bodyTransform", DEFAULT_BODY_TRANSFORM));
            this.readWheels(builder, object);
            this.readSeats(builder, object);
            this.readCamera(builder, object);
            this.readExtended(builder, object);
            return builder.build(true);
        }

        private void readWheels(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("wheels"))
            {
                JsonArray wheelArray = JSONUtils.getAsJsonArray(object, "wheels");
                for(JsonElement wheelElement : wheelArray)
                {
                    JsonObject wheelObject = wheelElement.getAsJsonObject();
                    builder.addWheel(Wheel.fromJsonObject(wheelObject));
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
                    wheels.add(wheel.toJsonObject());
                }
                object.add("wheels", wheels);
            }
        }

        private void readSeats(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("seats"))
            {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(object, "seats");
                for(JsonElement element : jsonArray)
                {
                    JsonObject seatObject = element.getAsJsonObject();
                    builder.addSeat(Seat.fromJsonObject(seatObject));
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
                    seats.add(seat.toJsonObject());
                }
                object.add("seats", seats);
            }
        }

        private void readCamera(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("camera"))
            {
                JsonObject cameraObject = JSONUtils.getAsJsonObject(object, "camera", new JsonObject());
                builder.setCamera(CameraProperties.fromJsonObject(cameraObject));
            }
        }

        private void writeCamera(VehicleProperties properties, JsonObject object)
        {
            CameraProperties camera = properties.getCamera();
            if(camera == CameraProperties.DEFAULT_CAMERA)
                return;
            JsonObject cameraObject = camera.toJsonObject();
            if(cameraObject.size() > 0)
            {
                object.add("camera", cameraObject);
            }
        }

        private void readExtended(VehicleProperties.Builder builder, JsonObject object)
        {
            JsonObject extended = JSONUtils.getAsJsonObject(object, "extended", new JsonObject());
            extended.entrySet().stream().filter(entry -> entry.getValue().isJsonObject()).forEach(entry -> {
                ResourceLocation id = ResourceLocation.tryParse(entry.getKey());
                JsonObject content = entry.getValue().getAsJsonObject();
                builder.addExtended(ExtendedProperties.create(id, content));
            });
        }

        private void writeExtended(VehicleProperties properties, JsonObject object)
        {
            JsonObject extended = new JsonObject();
            properties.getExtendedMap().forEach((id, extendedProperties) -> {
                JsonObject content = new JsonObject();
                extendedProperties.serialize(content);
                extended.add(extendedProperties.getId().toString(), content);
            });
            if(extended.size() > 0)
            {
                object.add("extended", extended);
            }
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private float maxHealth = DEFAULT_MAX_HEALTH;
        private float axleOffset = DEFAULT_AXLE_OFFSET;
        private Vector3d heldOffset = DEFAULT_HELD_OFFSET;
        private Vector3d towBarOffset = DEFAULT_TOW_BAR_OFFSET;
        private Vector3d trailerOffset = DEFAULT_TRAILER_OFFSET;
        private boolean canChangeWheels = DEFAULT_CAN_CHANGE_WHEELS;
        private List<Wheel> wheels = new ArrayList<>();
        private Transform bodyTransform = DEFAULT_BODY_TRANSFORM;
        private Transform displayTransform = DEFAULT_DISPLAY_TRANSFORM;
        private List<Seat> seats = new ArrayList<>();
        private boolean colored = DEFAULT_CAN_BE_PAINTED;
        private CameraProperties camera = CameraProperties.DEFAULT_CAMERA;
        private Map<ResourceLocation, ExtendedProperties> extended = new HashMap<>();

        public Builder setMaxHealth(float maxHealth)
        {
            this.maxHealth = maxHealth;
            return this;
        }

        public Builder setAxleOffset(float axleOffset)
        {
            this.axleOffset = axleOffset;
            return this;
        }

        public Builder setHeldOffset(double x, double y, double z)
        {
            this.heldOffset = new Vector3d(x, y, z);
            return this;
        }

        public Builder setHeldOffset(Vector3d vec)
        {
            this.heldOffset = vec;
            return this;
        }

        public Builder setTowBarPosition(double x, double y, double z)
        {
            this.towBarOffset = new Vector3d(x, y, z);
            return this;
        }

        public Builder setTowBarOffset(Vector3d vec)
        {
            this.towBarOffset = vec;
            return this;
        }

        public Builder setTrailerOffset(double x, double y, double z)
        {
            this.trailerOffset = new Vector3d(x, y, z);
            return this;
        }

        public Builder setTrailerOffset(Vector3d vec)
        {
            this.trailerOffset = vec;
            return this;
        }

        public Builder setCanChangeWheels(boolean canChangeWheels)
        {
            this.canChangeWheels = canChangeWheels;
            return this;
        }

        public Builder addWheel(Wheel wheel)
        {
            this.wheels.add(wheel);
            return this;
        }

        public Builder addWheel(Wheel.Builder builder)
        {
            this.wheels.add(builder.build());
            return this;
        }

        public Builder setBodyTransform(Transform bodyTransform)
        {
            this.bodyTransform = bodyTransform;
            return this;
        }

        public Builder setDisplayTransform(Transform displayTransform)
        {
            this.displayTransform = displayTransform;
            return this;
        }

        public Builder addSeat(Seat seat)
        {
            this.seats.add(seat);
            return this;
        }

        public Builder setColored(boolean colored)
        {
            this.colored = colored;
            return this;
        }

        public Builder setCamera(CameraProperties.Builder builder)
        {
            this.camera = builder.build();
            return this;
        }

        public Builder setCamera(CameraProperties camera)
        {
            this.camera = camera;
            return this;
        }

        public Builder addExtended(ExtendedProperties properties)
        {
            this.extended.putIfAbsent(properties.getId(), properties);
            return this;
        }

        public VehicleProperties build(boolean scaleWheels)
        {
            this.validate();
            float wheelOffset = this.calculateWheelOffset();
            List<Wheel> wheels = scaleWheels ? this.generateScaledWheels(wheelOffset) : this.wheels;
            return new VehicleProperties(this.maxHealth, this.axleOffset, wheelOffset, this.heldOffset, this.towBarOffset, this.trailerOffset, this.canChangeWheels, wheels, this.bodyTransform, this.displayTransform, this.seats, this.colored, this.camera, this.extended);
        }

        private void validate()
        {
            if(this.seats.stream().filter(Seat::isDriver).count() > 1)
            {
                throw new RuntimeException("Unable to build vehicles properties. The maximum amount of drivers seats is one but tried to add more.");
            }
        }

        private List<Wheel> generateScaledWheels(float wheelOffset)
        {
            return this.wheels.stream().map(wheel -> {
                if(!wheel.isAutoScale()) return wheel;
                double scale = ((wheelOffset + wheel.getOffsetY()) * 2) / WHEEL_RADIUS;
                double xScale = wheel.getScale().x != 0.0 ? wheel.getScale().x : scale;
                double yScale = wheel.getScale().y != 0.0 ? wheel.getScale().y : scale;
                double zScale = wheel.getScale().z != 0.0 ? wheel.getScale().z : scale;
                Vector3d newScale = new Vector3d(xScale, yScale, zScale);
                return wheel.rescale(newScale);
            }).collect(Collectors.toList());
        }

        private float calculateWheelOffset()
        {
            return this.wheels.stream().filter(wheel -> !wheel.isAutoScale()).max((w1, w2) -> (int) (this.getLowestSittingPosition(w1) - this.getLowestSittingPosition(w2))).map(this::getLowestSittingPosition).orElse(0F);
        }

        private float getLowestSittingPosition(Wheel wheel)
        {
            double radius = WHEEL_RADIUS * wheel.getScaleY();
            double lowestPosition = radius / 2;
            lowestPosition -= wheel.getOffsetY();
            return (float) lowestPosition;
        }
    }
}
