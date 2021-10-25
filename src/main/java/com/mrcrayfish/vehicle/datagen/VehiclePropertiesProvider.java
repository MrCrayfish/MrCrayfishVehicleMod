package com.mrcrayfish.vehicle.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Author: MrCrayfish
 */
public abstract class VehiclePropertiesProvider implements IDataProvider
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().registerTypeAdapter(VehicleProperties.class, new VehicleProperties.Serializer()).create();

    private final DataGenerator generator;
    private final Map<ResourceLocation, VehicleProperties> vehiclePropertiesMap = new HashMap<>();

    protected VehiclePropertiesProvider(DataGenerator generator)
    {
        this.generator = generator;
    }

    protected final void add(EntityType<? extends VehicleEntity> type, VehicleProperties.Builder builder)
    {
        this.add(type.getRegistryName(), builder);
    }

    protected final void add(ResourceLocation id, VehicleProperties.Builder builder)
    {
        this.vehiclePropertiesMap.put(id, builder.build(false));
    }

    protected abstract void registerProperties();

    @Override
    public void run(DirectoryCache cache) throws IOException
    {
        this.vehiclePropertiesMap.clear();
        this.registerProperties();
        this.vehiclePropertiesMap.forEach((id, properties) ->
        {
            String modId = id.getNamespace();
            String vehicleId = id.getPath();
            Path path = this.generator.getOutputFolder().resolve("data/" + modId + "/vehicles/properties/" + vehicleId + ".json");
            try
            {
                String rawJson = GSON.toJson(properties);
                String hash = SHA1.hashUnencodedChars(rawJson).toString();
                if(!Objects.equals(cache.getHash(path), hash) || !Files.exists(path))
                {
                    Files.createDirectories(path.getParent());
                    try(BufferedWriter writer = Files.newBufferedWriter(path))
                    {
                        writer.write(rawJson);
                    }
                }
                cache.putNew(path, hash);
            }
            catch(IOException e)
            {
                LOGGER.error("Couldn't save vehicle properties to {}", path, e);
            }

            if(properties.getCosmetics().isEmpty())
                return;

            path = this.generator.getOutputFolder().resolve("data/" + modId + "/vehicles/cosmetics/" + vehicleId + ".json");
            try
            {
                JsonObject object = new JsonObject();
                object.addProperty("replace", false);
                JsonObject validModels = new JsonObject();
                properties.getCosmetics().forEach((cosmeticId, cosmeticProperties) ->
                {
                    JsonArray array = new JsonArray();
                    cosmeticProperties.getModelLocations().forEach(location ->
                    {
                        List<ResourceLocation> disabledCosmetics = cosmeticProperties.getDisabledCosmetics().getOrDefault(location, Collections.emptyList());
                        if(disabledCosmetics.isEmpty())
                        {
                            array.add(location.toString());
                        }
                        else
                        {
                            JsonObject modelObject = new JsonObject();
                            modelObject.addProperty("model", location.toString());
                            JsonArray disables = new JsonArray();
                            disabledCosmetics.forEach(disabledCosmeticId -> disables.add(disabledCosmeticId.toString()));
                            modelObject.add("disables", disables);
                            array.add(modelObject);
                        }
                    });
                    validModels.add(cosmeticId.toString(), array);
                });
                object.add("valid_models", validModels);
                String rawJson = GSON.toJson(object);
                String hash = SHA1.hashUnencodedChars(rawJson).toString();
                if(!Objects.equals(cache.getHash(path), hash) || !Files.exists(path))
                {
                    Files.createDirectories(path.getParent());
                    try(BufferedWriter writer = Files.newBufferedWriter(path))
                    {
                        writer.write(rawJson);
                    }
                }
                cache.putNew(path, hash);
            }
            catch(IOException e)
            {
                LOGGER.error("Couldn't save vehicle cosmetics to {}", path, e);
            }
        });
    }

    @Nonnull
    @Override
    public String getName()
    {
        return "VehicleProperties";
    }
}
