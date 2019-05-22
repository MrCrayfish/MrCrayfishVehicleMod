package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;

/**
 * Enum of values that register and retrieve models that do not need to be associated with a particular item
 * Author: Phylogeny (https://github.com/Phylogeny)
 */
public enum Models
{
    VEHICLE_CRATE("vehicle_crate_panel"),
    CHEST_TRAILER("trailer_chest_body"),
    SEEDER_TRAILER("trailer_seeder_body"),
    SEED_SPIKER("seed_spiker"),
    FERTILIZER_TRAILER("trailer_fertilizer_body"),
    FLUID_TRAILER("trailer_fluid_body"),
    NOZZLE("nozzle");

    /**
     * An arbitrary item in your mod to register isolated models as variants of
     */
    private static Item item;

    /**
     * The location of an item model in the [MOD_ID]/models/item folder
     */
    private ModelResourceLocation modelLocation;

    /**
     * Sets the model's location
     *
     * @param modelName name of the model file
     */
    Models(String modelName)
    {
        modelLocation = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, modelName), null);
    }

    /**
     * Registers all isolated models as variants of any arbitrary item in your mod. Call this client-side in the event where your other item models are registered.
     *
     * @param item arbitrary item in your mod
     */
    public static void registerModels(Item item)
    {
        Models.item = item;
        for (Models model : Models.values())
            model.register();
    }

    /**
     * Registers isolated model
     */
    private void register()
    {
        ModelLoader.registerItemVariants(item, modelLocation);
    }

    /**
     * Gets the isolated model
     *
     * @return isolated model
     */
    public IBakedModel getModel()
    {
        return Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(modelLocation);
    }
}
