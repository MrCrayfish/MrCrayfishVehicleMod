package com.mrcrayfish.framework.client.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.client.renderer.model.BlockModel;
import net.minecraft.client.renderer.model.BlockPart;
import net.minecraft.client.renderer.model.BlockPartRotation;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.IModelTransform;
import net.minecraft.client.renderer.model.IUnbakedModel;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.RenderMaterial;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.IModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * Open Model format from Framework. Allows larger models and removes rotation step restriction.
 *
 * Author: MrCrayfish
 */
public class OpenModel implements IModelGeometry<OpenModel>
{
    private final BlockModel model;

    public OpenModel(BlockModel model)
    {
        this.model = model;
    }

    @Override
    public IBakedModel bake(IModelConfiguration owner, ModelBakery bakery, Function<RenderMaterial, TextureAtlasSprite> spriteGetter, IModelTransform modelTransform, ItemOverrideList overrides, ResourceLocation modelLocation)
    {
        return this.model.bake(bakery, this.model, spriteGetter, modelTransform, modelLocation, true);
    }

    @Override
    public Collection<RenderMaterial> getTextures(IModelConfiguration owner, Function<ResourceLocation, IUnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors)
    {
        return this.model.getMaterials(modelGetter, missingTextureErrors);
    }

    @Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class Loader implements IModelLoader<OpenModel>
    {
        @Override
        public void onResourceManagerReload(IResourceManager manager) {}

        @Override
        public OpenModel read(JsonDeserializationContext context, JsonObject object)
        {
            return new OpenModel(Deserializer.INSTANCE.deserialize(object, BlockModel.class, context));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelRegistryEvent event)
        {
            ModelLoaderRegistry.registerLoader(new ResourceLocation("framework", "open_model"), new Loader());
        }
    }

    public static class Deserializer extends BlockModel.Deserializer
    {
        private static final BlockPart.Deserializer BLOCK_PART_DESERIALIZER = new BlockPart.Deserializer();
        private static final Deserializer INSTANCE = new Deserializer();

        /**
         * Reads the bl
         */
        @Override
        protected List<BlockPart> getElements(JsonDeserializationContext context, JsonObject object)
        {
            try
            {
                List<BlockPart> list = new ArrayList<>();
                for(JsonElement element : Objects.requireNonNull(JSONUtils.getAsJsonArray(object, "components", new JsonArray())))
                {
                    list.add(this.readBlockElement(element, context));
                }
                return list;
            }
            catch(Exception e)
            {
                throw new JsonParseException(e);
            }
        }

        /**
         * Reads a block element without restrictions on the size and rotation angle.
         */
        private BlockPart readBlockElement(JsonElement element, JsonDeserializationContext context)
        {
            JsonObject object = element.getAsJsonObject();

            // Get copy of custom size and angle properties
            Vector3f from = ExtraJSONUtils.getAsVector3f(object, "from");
            Vector3f to = ExtraJSONUtils.getAsVector3f(object, "to");
            JsonObject rotation = JSONUtils.getAsJsonObject(object, "rotation", new JsonObject());
            float angle = JSONUtils.getAsFloat(rotation, "angle", 0F);

            // Make valid for vanilla block element deserializer
            JsonArray zero = new JsonArray();
            zero.add(0F);
            zero.add(0F);
            zero.add(0F);
            object.add("from", zero);
            object.add("to", zero);
            rotation.addProperty("angle", 0F);

            // Read vanilla element and construct new element with custom properties
            BlockPart e = BLOCK_PART_DESERIALIZER.deserialize(element, BlockPart.class, context);
            BlockPartRotation r = e.rotation != null ? new BlockPartRotation(e.rotation.origin, e.rotation.axis, angle, e.rotation.rescale) : null;
            return new BlockPart(from, to, e.faces, r, e.shade);
        }
    }
}
