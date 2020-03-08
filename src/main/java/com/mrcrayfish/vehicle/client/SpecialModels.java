package com.mrcrayfish.vehicle.client;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.init.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Enum of values that register and retrieve models that do not need to be associated with a particular item
 * Author: Phylogeny (https://github.com/Phylogeny)
 */
@Mod.EventBusSubscriber(modid = Reference.MOD_ID, value = Side.CLIENT)
public enum SpecialModels
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
     * Cached model
     */
    private IBakedModel cachedModel;

    /**
     * Sets the model's location
     *
     * @param modelName name of the model file
     */
    SpecialModels(String modelName)
    {
        this.modelLocation = new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, modelName), null);
    }

    /**
     * Gets the isolated model
     *
     * @return isolated model
     */
    public IBakedModel getModel()
    {
        if(this.cachedModel == null)
        {
            IBakedModel model = Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getModel(this.modelLocation);
            if(model == Minecraft.getMinecraft().getBlockRendererDispatcher().getBlockModelShapes().getModelManager().getMissingModel())
            {
                return model;
            }
            this.cachedModel = model;
        }
        return this.cachedModel;
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void register(ModelRegistryEvent event)
    {
        SpecialModels.item = ModItems.MODELS;
        for(SpecialModels model : SpecialModels.values())
        {
            ModelLoader.registerItemVariants(item, model.modelLocation);
        }
    }
}
