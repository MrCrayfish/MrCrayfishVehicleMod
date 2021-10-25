package com.mrcrayfish.vehicle.common.cosmetic;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.mrcrayfish.vehicle.common.cosmetic.actions.Action;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Author: MrCrayfish
 */
public class CosmeticProperties
{
    public static final Vector3d DEFAULT_OFFSET = Vector3d.ZERO;

    private final ResourceLocation id;
    private final Vector3d offset;
    private final List<Supplier<Action>> actions;
    private List<ResourceLocation> modelLocations = new ArrayList<>();
    private Map<ResourceLocation, List<ResourceLocation>> disabledCosmetics = new HashMap<>();

    public CosmeticProperties(ResourceLocation id, Vector3d offset, List<Supplier<Action>> actions)
    {
        this.id = id;
        this.offset = offset;
        this.actions = actions;
    }

    public CosmeticProperties(JsonObject object)
    {
        this.id = new ResourceLocation(JSONUtils.getAsString(object, "id"));
        this.offset = ExtraJSONUtils.getAsVector3d(object, "offset", DEFAULT_OFFSET);
        List<Supplier<Action>> actions = new ArrayList<>();
        JsonArray array = JSONUtils.getAsJsonArray(object, "actions", new JsonArray());
        StreamSupport.stream(array.spliterator(), false).filter(JsonElement::isJsonObject).forEach(element -> {
            JsonObject action = element.getAsJsonObject();
            ResourceLocation type = new ResourceLocation(JSONUtils.getAsString(action, "type"));
            Supplier<Action> actionSupplier = CosmeticActions.getSupplier(type, action);
            Objects.requireNonNull(actionSupplier, "Unregistered cosmetic action: " + type);
            actions.add(actionSupplier);
        });
        this.actions = actions;
    }

    public ResourceLocation getId()
    {
        return this.id;
    }

    public Vector3d getOffset()
    {
        return this.offset;
    }

    public void setModelLocations(List<ResourceLocation> locations)
    {
        this.modelLocations = ImmutableList.copyOf(locations);
    }

    public List<ResourceLocation> getModelLocations()
    {
        return this.modelLocations;
    }

    public void setDisabledCosmetics(Map<ResourceLocation, List<ResourceLocation>> disabledCosmetics)
    {
        this.disabledCosmetics = ImmutableMap.copyOf(disabledCosmetics);
    }

    public Map<ResourceLocation, List<ResourceLocation>> getDisabledCosmetics()
    {
        return this.disabledCosmetics;
    }

    public List<Supplier<Action>> getActions()
    {
        return this.actions;
    }

    public void serialize(JsonObject object)
    {
        object.addProperty("id", this.id.toString());
        ExtraJSONUtils.write(object, "offset", this.offset, DEFAULT_OFFSET);
        if(this.actions.isEmpty())
            return;
        JsonArray actions = new JsonArray();
        this.actions.forEach(actionSupplier -> {
            Action action = actionSupplier.get();
            ResourceLocation type = CosmeticActions.getId(action.getClass());
            if(type == null)
                return;
            JsonObject actionData = new JsonObject();
            actionData.addProperty("type", type.toString());
            action.serialize(actionData);
            actions.add(actionData);
        });
        object.add("actions", actions);
    }

    public static void deserializeModels(ResourceLocation cosmeticLocation, IResourceManager manager, Map<ResourceLocation, List<Pair<ResourceLocation, List<ResourceLocation>>>> modelMap)
    {
        try
        {
            IResource resource = manager.getResource(cosmeticLocation);
            JsonObject object = JSONUtils.parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            boolean replace = JSONUtils.getAsBoolean(object, "replace", false);
            if(replace) modelMap.clear();
            JsonObject validModelsObject = JSONUtils.getAsJsonObject(object, "valid_models", new JsonObject());
            validModelsObject.entrySet().stream().filter(entry -> entry.getValue().isJsonArray()).forEach(entry ->
            {
                JsonArray modelArray = entry.getValue().getAsJsonArray();
                ResourceLocation cosmeticId = new ResourceLocation(entry.getKey());
                modelArray.forEach(modelElement ->
                {
                    if(modelElement.isJsonPrimitive() && modelElement.getAsJsonPrimitive().isString())
                    {
                        ResourceLocation location = new ResourceLocation(modelElement.getAsString());
                        modelMap.computeIfAbsent(cosmeticId, id -> new ArrayList<>()).add(Pair.of(location, Collections.emptyList()));
                    }
                    else if(modelElement.isJsonObject())
                    {
                        JsonObject modelObject = modelElement.getAsJsonObject();
                        ResourceLocation location = new ResourceLocation(JSONUtils.getAsString(modelObject, "model"));
                        JsonArray disabledArray = JSONUtils.getAsJsonArray(modelObject, "disables", new JsonArray());
                        List<ResourceLocation> disabledCosmetics = StreamSupport.stream(disabledArray.spliterator(), false)
                                .filter(JsonElement::isJsonPrimitive)
                                .filter(e -> e.getAsJsonPrimitive().isString())
                                .map(e -> new ResourceLocation(e.getAsString()))
                                .collect(Collectors.toList());
                        modelMap.computeIfAbsent(cosmeticId, id -> new ArrayList<>()).add(Pair.of(location, disabledCosmetics));
                    }
                    else
                    {
                        throw new JsonSyntaxException("Expected a string or an object for cosmetic model definition");
                    }
                });
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static Builder builder(ResourceLocation id)
    {
        return new Builder(id);
    }

    public static class Builder
    {
        private final ResourceLocation id;
        private Vector3d offset = DEFAULT_OFFSET;
        private List<ResourceLocation> models = new ArrayList<>();
        private Map<ResourceLocation, List<ResourceLocation>> disabledCosmetics = new HashMap<>();
        private List<Supplier<Action>> actions = new ArrayList<>();

        public Builder(ResourceLocation id)
        {
            this.id = id;
        }

        public Builder setOffset(Vector3d offset)
        {
            this.offset = offset;
            return this;
        }

        public Builder setOffset(double x, double y, double z)
        {
            this.offset = new Vector3d(x, y, z);
            return this;
        }

        public Builder addModelLocation(ResourceLocation location)
        {
            this.models.add(location);
            return this;
        }

        public Builder addModelLocation(ResourceLocation location, ResourceLocation ... disabledCosmetics)
        {
            this.models.add(location);
            this.disabledCosmetics.computeIfAbsent(location, l -> new ArrayList<>()).addAll(Arrays.asList(disabledCosmetics));
            return this;
        }

        public Builder addAction(Action action)
        {
            this.actions.add(() -> action);
            return this;
        }

        public CosmeticProperties build()
        {
            CosmeticProperties properties = new CosmeticProperties(this.id, this.offset, this.actions);
            properties.setModelLocations(this.models);
            properties.setDisabledCosmetics(this.disabledCosmetics);
            return properties;
        }
    }
}
