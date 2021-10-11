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
    //private List<Action> actions = new ArrayList<>(); //TODO implement actions
    private List<ResourceLocation> validModels = new ArrayList<>();

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

    public void setValidModels(List<ResourceLocation> validModels)
    {
        this.validModels = validModels;
    }

    public List<ResourceLocation> getValidModels()
    {
        return this.validModels;
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
}
