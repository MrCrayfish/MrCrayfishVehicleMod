package com.mrcrayfish.vehicle.common.cosmetic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Author: MrCrayfish
 */
public class CosmeticProperties
{
    public static final Vector3d DEFAULT_OFFSET = Vector3d.ZERO;

    private final ResourceLocation id;
    private final Vector3d offset;
    private final List<ResourceLocation> modelLocations = new ArrayList<>();
    private final List<ResourceLocation> actions = new ArrayList<>();

    public CosmeticProperties(ResourceLocation id, Vector3d offset)
    {
        this.id = id;
        this.offset = offset;
    }

    public CosmeticProperties(JsonObject object)
    {
        this.id = new ResourceLocation(JSONUtils.getAsString(object, "id"));
        this.offset = ExtraJSONUtils.getAsVector3d(object, "offset", DEFAULT_OFFSET);
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
        this.modelLocations.clear();
        this.modelLocations.addAll(locations);
    }

    public List<ResourceLocation> getModelLocations()
    {
        return this.modelLocations;
    }

    public void serialize(JsonObject object)
    {
        object.addProperty("id", this.id.toString());
        ExtraJSONUtils.write(object, "offset", this.offset, DEFAULT_OFFSET);
    }

    public static void deserializeModels(ResourceLocation location, IResourceManager manager, Map<ResourceLocation, List<ResourceLocation>> modelMap)
    {
        try
        {
            IResource resource = manager.getResource(location);
            JsonObject object = JSONUtils.parse(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            boolean replace = JSONUtils.getAsBoolean(object, "replace", false);
            if(replace) modelMap.clear();
            JsonObject validModelsObject = JSONUtils.getAsJsonObject(object, "valid_models", new JsonObject());
            validModelsObject.entrySet().stream().filter(entry -> entry.getValue().isJsonArray()).forEach(entry -> {
                JsonArray modelArray = entry.getValue().getAsJsonArray();
                ResourceLocation cosmeticId = new ResourceLocation(entry.getKey());
                modelArray.forEach(element -> {
                    modelMap.computeIfAbsent(cosmeticId, id -> new ArrayList<>()).add(new ResourceLocation(element.getAsString()));
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
        private List<ResourceLocation> modelLocations = new ArrayList<>();
        private List<ResourceLocation> actions = new ArrayList<>();

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
            this.modelLocations.add(location);
            return this;
        }

        public Builder addModelLocation(ResourceLocation ... locations)
        {
            this.modelLocations.addAll(Arrays.asList(locations));
            return this;
        }

        public CosmeticProperties build()
        {
            CosmeticProperties properties = new CosmeticProperties(this.id, this.offset);
            properties.setModelLocations(this.modelLocations);
            return properties;
        }
    }
}
