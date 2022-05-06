package com.mrcrayfish.vehicle.client.model;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.util.Pair;
import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.util.ExtraJSONUtils;
import net.minecraft.client.renderer.model.*;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.Direction;
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
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/**
 * A custom block model that removes the restrictions on the size of elements
 *
 * Author: MrCrayfish
 */
public class BigModel implements IModelGeometry<BigModel>
{
    private final BlockModel model;

    public BigModel(BlockModel model)
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
    public static class Loader implements IModelLoader<BigModel>
    {
        @Override
        public void onResourceManagerReload(IResourceManager resourceManager) {}

        @Override
        public BigModel read(JsonDeserializationContext context, JsonObject object)
        {
            return new BigModel(Deserializer.INSTANCE.deserialize(object, BlockModel.class, context));
        }

        @SubscribeEvent
        public static void onModelRegister(ModelRegistryEvent event)
        {
            ModelLoaderRegistry.registerLoader(new ResourceLocation("vehicle:big_model"), new Loader());
        }
    }

    public static class Deserializer extends BlockModel.Deserializer
    {
        private static final Method GET_ROTATION = ObfuscationReflectionHelper.findMethod(BlockPart.Deserializer.class, "func_178256_a", JsonObject.class);
        private static final Method GET_FACES = ObfuscationReflectionHelper.findMethod(BlockPart.Deserializer.class, "func_178250_a", JsonDeserializationContext.class, JsonObject.class);
        private static final BlockPart.Deserializer BLOCK_PART_DESERIALIZER = new BlockPart.Deserializer();
        private static final Deserializer INSTANCE = new Deserializer();

        @Override
        protected List<BlockPart> getElements(JsonDeserializationContext context, JsonObject object)
        {
            List<BlockPart> list = Lists.newArrayList();
            if(object.has("components"))
            {
                for(JsonElement element : JSONUtils.getAsJsonArray(object, "components"))
                {
                    list.add(this.readBlockPart(element, context));
                }
            }
            return list;
        }

        @SuppressWarnings("unchecked")
        private BlockPart readBlockPart(JsonElement element, JsonDeserializationContext context)
        {
            try
            {
                JsonObject object = element.getAsJsonObject();
                Vector3f from = ExtraJSONUtils.getAsVector3f(object, "from");
                Vector3f to = ExtraJSONUtils.getAsVector3f(object, "to");
                BlockPartRotation rotation = (BlockPartRotation) GET_ROTATION.invoke(BLOCK_PART_DESERIALIZER, object);
                Map<Direction, BlockPartFace> map = (Map<Direction, BlockPartFace>) GET_FACES.invoke(BLOCK_PART_DESERIALIZER, context, object);
                if(object.has("shade") && !JSONUtils.isBooleanValue(object, "shade"))
                    throw new JsonParseException("Expected shade to be a Boolean");
                boolean shade = JSONUtils.getAsBoolean(object, "shade", true);
                return new BlockPart(from, to, map, rotation, shade);
            }
            catch(Exception e)
            {
                throw new JsonParseException("Failed to read BlockPart");
            }
        }
    }
}
