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
import com.mrcrayfish.vehicle.entity.IEngineType;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
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
    private static final Map<ResourceLocation, ExtendedProperties> GLOBAL_EXTENDED_PROPERTIES = new HashMap<>();

    //TODO ideas: canBeDamaged, canPickUp, canBePlacedInTrailer
    private final float axleOffset;
    private final float wheelOffset;
    private final Vector3d heldOffset;
    private final Vector3d towBarPosition;
    private final Vector3d trailerOffset;
    private final boolean canChangeWheels;
    private final List<Wheel> wheels;
    private final Transform bodyTransform;
    private final Transform displayTransform;
    private final List<Seat> seats;
    private final boolean colored;
    private final CameraProperties camera;
    private final ImmutableMap<ResourceLocation, ExtendedProperties> extended;

    private VehicleProperties(float axleOffset, float wheelOffset, Vector3d heldOffset, Vector3d towBarPosition, Vector3d trailerOffset, boolean canChangeWheels, List<Wheel> wheels, Transform bodyTransform, Transform displayTransform, List<Seat> seats, boolean colored, CameraProperties camera, Map<ResourceLocation, ExtendedProperties> extended)
    {
        this.axleOffset = axleOffset;
        this.wheelOffset = wheelOffset;
        this.heldOffset = heldOffset;
        this.towBarPosition = towBarPosition;
        this.trailerOffset = trailerOffset;
        this.canChangeWheels = canChangeWheels;
        this.wheels = wheels;
        this.bodyTransform = bodyTransform;
        this.displayTransform = displayTransform;
        this.seats = seats;
        this.colored = colored;
        this.camera = camera;
        this.extended = ImmutableMap.copyOf(extended);
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

    public Vector3d getTowBarPosition()
    {
        return this.towBarPosition;
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

    public boolean isColored()
    {
        return this.colored;
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

            JsonObject general = new JsonObject();
            if(properties.colored) general.addProperty("canBeColored", true);
            if(properties.canChangeWheels) general.addProperty("canChangeWheels", true);
            if(general.size() > 0) object.add("general", general);

            JsonObject axles = new JsonObject();
            if(properties.axleOffset != 0) axles.addProperty("offsetToGround", properties.axleOffset);
            if(axles.size() > 0) object.add("axles", axles);

            JsonObject display = new JsonObject();
            this.addVector3dProperty(display, "held", properties.heldOffset);
            this.addVector3dProperty(display, "trailer", properties.trailerOffset);
            this.addPartPositionProperty(display, "gui", properties.displayTransform);
            if(display.size() > 0) object.add("display", display);

            JsonObject position = new JsonObject();
            this.addPartPositionProperty(position, "body", properties.bodyTransform);
            this.addVector3dProperty(position, "towBar", properties.towBarPosition);
            if(position.size() > 0) object.add("position", position);

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

            JsonObject general = JSONUtils.getAsJsonObject(object, "general", new JsonObject());
            builder.setColored(JSONUtils.getAsBoolean(general, "canBeColored", false));
            builder.setCanChangeWheels(JSONUtils.getAsBoolean(general, "canChangeWheels", false));

            JsonObject axles = JSONUtils.getAsJsonObject(object, "axles", new JsonObject());
            builder.setAxleOffset(JSONUtils.getAsFloat(axles, "offsetToGround", 0F));

            JsonObject display = JSONUtils.getAsJsonObject(object, "display", new JsonObject());
            builder.setHeldOffset(this.getAsVector3d(display, "held", Vector3d.ZERO));
            builder.setTrailerOffset(this.getAsVector3d(display, "trailer", Vector3d.ZERO));
            this.callIfNonNull(builder::setDisplayTransform, ExtraJSONUtils.getAsTransform(display, "gui", Transform.DEFAULT));

            JsonObject positions = JSONUtils.getAsJsonObject(object, "position", new JsonObject());
            this.callIfNonNull(builder::setBodyTransform, ExtraJSONUtils.getAsTransform(positions, "body", Transform.DEFAULT));
            builder.setTowBarPosition(this.getAsVector3d(positions, "towBar", Vector3d.ZERO));

            this.readWheels(builder, object);
            this.readSeats(builder, object);
            this.readCamera(builder, object);
            this.readExtended(builder, object);

            return builder.build(true);
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

        private void readWheels(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("wheels"))
            {
                JsonArray jsonArray = JSONUtils.getAsJsonArray(object, "wheels");
                for(JsonElement element : jsonArray)
                {
                    JsonObject wheelObject = element.getAsJsonObject();
                    Vector3d offset = this.getAsVector3d(wheelObject, "offset", Vector3d.ZERO);
                    Vector3d scale = this.getAsVector3d(wheelObject, "scale", Vector3d.ZERO);
                    Wheel.Side side = this.getAsEnum(wheelObject, "side", Wheel.Side.class, Wheel.Side.NONE);
                    Wheel.Position position = this.getAsEnum(wheelObject, "position", Wheel.Position.class, Wheel.Position.NONE);
                    boolean autoScale = JSONUtils.getAsBoolean(wheelObject, "autoScale", false);
                    boolean particles = JSONUtils.getAsBoolean(wheelObject, "particles", false);
                    boolean render = JSONUtils.getAsBoolean(wheelObject, "render", true);
                    builder.addWheel(Wheel.builder()
                            .setSide(side)
                            .setPosition(position)
                            .setOffset(offset.x, offset.y, offset.z)
                            .setScale(scale.x, scale.y, scale.z)
                            .setAutoScale(autoScale)
                            .setParticles(particles)
                            .setRender(render));
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
                    if(wheel.getSide() != Wheel.Side.NONE) wheelObject.addProperty("side", wheel.getSide().name().toLowerCase(Locale.ENGLISH));
                    if(wheel.getPosition() != Wheel.Position.NONE) wheelObject.addProperty("position", wheel.getPosition().name().toLowerCase(Locale.ENGLISH));
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

        private void readSeats(VehicleProperties.Builder builder, JsonObject object)
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
                    builder.addSeat(new Seat(position, driver, yawOffset));
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

        private void readCamera(VehicleProperties.Builder builder, JsonObject object)
        {
            if(object.has("camera"))
            {
                JsonObject cameraObject = JSONUtils.getAsJsonObject(object, "camera", new JsonObject());
                CameraProperties.Type type = CameraProperties.Type.fromId(JSONUtils.getAsString(cameraObject, "type", "locked"));
                float strength = JSONUtils.getAsFloat(cameraObject, "strength", 1.0F);
                Vector3d position = this.getAsVector3d(cameraObject, "position", Vector3d.ZERO);
                Vector3d rotation = this.getAsVector3d(cameraObject, "rotation", Vector3d.ZERO);
                double distance = JSONUtils.getAsFloat(cameraObject, "distance", 4.0F);
                builder.setCamera(new CameraProperties(type, strength, position, rotation, distance));
            }
        }

        private void writeCamera(VehicleProperties properties, JsonObject object)
        {
            CameraProperties camera = properties.getCamera();
            if(camera == CameraProperties.DEFAULT_CAMERA)
                return;
            JsonObject cameraObject = new JsonObject();
            if(camera.getType() != CameraProperties.Type.LOCKED) cameraObject.addProperty("type", camera.getType().getId());
            if(camera.getStrength() != 1.0F) cameraObject.addProperty("strength", camera.getStrength());
            this.addVector3dProperty(cameraObject, "position", camera.getPosition());
            this.addVector3dProperty(cameraObject, "rotation", camera.getRotation());
            if(camera.getDistance() != 4.0) cameraObject.addProperty("distance", camera.getDistance());
            object.add("camera", cameraObject);
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

        private <T extends Enum<?>> T getAsEnum(JsonObject object, String memberName, Class<T> enumClass, T defaultValue)
        {
            if(object.has(memberName))
            {
                String enumString = JSONUtils.getAsString(object, memberName);
                return Stream.of(enumClass.getEnumConstants()).filter(side -> side.name().equalsIgnoreCase(enumString)).findFirst().orElse(defaultValue);
            }
            return defaultValue;
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

        private void addPartPositionProperty(JsonObject parent, String memberName, Transform position)
        {
            if(position != null && position != Transform.DEFAULT)
            {
                JsonObject partPositionObject = new JsonObject();
                this.addVector3dProperty(partPositionObject, "translate", position.getTranslate());
                this.addVector3dProperty(partPositionObject, "rotation", position.getRotation());
                if(position.getScale() != 1) partPositionObject.addProperty("scale", Double.parseDouble(FORMAT.format(position.getScale())));
                if(partPositionObject.size() > 0) parent.add(memberName, partPositionObject);
            }
        }
    }

    public static Builder builder()
    {
        return new Builder();
    }

    public static class Builder
    {
        private float axleOffset;
        private float wheelOffset;
        private Vector3d heldOffset = Vector3d.ZERO;
        private Vector3d towBarPosition = Vector3d.ZERO;
        private Vector3d trailerOffset = Vector3d.ZERO;
        private boolean canChangeWheels = false;
        private List<Wheel> wheels = new ArrayList<>();
        private Transform bodyTransform = Transform.DEFAULT;
        private Transform displayTransform = Transform.DEFAULT;
        private List<Seat> seats = new ArrayList<>();
        private boolean colored;
        private CameraProperties camera = CameraProperties.DEFAULT_CAMERA;
        private Map<ResourceLocation, ExtendedProperties> extended = new HashMap<>();

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
            this.towBarPosition = new Vector3d(x, y, z);
            return this;
        }

        public Builder setTowBarPosition(Vector3d vec)
        {
            this.towBarPosition = vec;
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
            this.calculateWheelOffset();
            List<Wheel> wheels = scaleWheels ? this.generateScaledWheels() : this.wheels;
            return new VehicleProperties(this.axleOffset, this.wheelOffset, this.heldOffset, this.towBarPosition, this.trailerOffset, this.canChangeWheels, wheels, this.bodyTransform, this.displayTransform, this.seats, this.colored, this.camera, this.extended);
        }

        private void validate()
        {
            if(this.seats.stream().filter(Seat::isDriverSeat).count() > 1)
            {
                throw new RuntimeException("Unable to build vehicles properties. The maximum amount of drivers seats is one but tried to add more.");
            }
        }

        private List<Wheel> generateScaledWheels()
        {
            return this.wheels.stream().map(wheel -> {
                if(!wheel.isAutoScale()) return wheel;
                double scale = ((this.wheelOffset + wheel.getOffsetY()) * 2) / WHEEL_RADIUS;
                double xScale = wheel.getScale().x != 0.0 ? wheel.getScale().x : scale;
                double yScale = wheel.getScale().y != 0.0 ? wheel.getScale().y : scale;
                double zScale = wheel.getScale().z != 0.0 ? wheel.getScale().z : scale;
                Vector3d newScale = new Vector3d(xScale, yScale, zScale);
                return wheel.rescale(newScale);
            }).collect(Collectors.toList());
        }

        private void calculateWheelOffset()
        {
            this.wheels.stream().filter(wheel -> !wheel.isAutoScale()).max((w1, w2) -> (int) (this.getLowestSittingPosition(w1) - this.getLowestSittingPosition(w2))).ifPresent(wheel -> {
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
    }
}
