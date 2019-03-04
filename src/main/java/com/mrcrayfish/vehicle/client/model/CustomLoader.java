package com.mrcrayfish.vehicle.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mrcrayfish.vehicle.Reference;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

public class CustomLoader implements ICustomModelLoader
{
	private static final ImmutableMap<String, IModel> BLOCK_MODELS;
	
	static 
	{
		Builder<String, IModel> builder = new Builder<>();
		builder.put("boost_ramp", new ModelRamp());
		builder.put("steep_boost_ramp", new ModelSteepRamp());
		BLOCK_MODELS = builder.build();
	}

	@Override
	public boolean accepts(ResourceLocation resource)
	{
		if(resource.getResourceDomain().equals(Reference.MOD_ID))
		{
			for(String name : BLOCK_MODELS.keySet())
			{
				if(name.equals(resource.getResourcePath()))
				{
					return true;
				}
				if(("models/item/" + name).equals(resource.getResourcePath()))
				{
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public IModel loadModel(ResourceLocation resource)
	{
		return BLOCK_MODELS.get(resource.getResourcePath().replace("models/item/", ""));
	}
	
	@Override
	public void onResourceManagerReload(IResourceManager manager) {}
}
