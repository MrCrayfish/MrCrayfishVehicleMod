package com.mrcrayfish.vehicle.client.model;

public class CustomLoader
{
	/*private static final ImmutableMap<String, IModel> BLOCK_MODELS;
	
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

	@Override
	public ISimpleModelGeometry read(JsonDeserializationContext deserializationContext, JsonObject modelContents)
	{
		return null;
	}

	@Override
	public void onResourceManagerReload(IResourceManager resourceManager)
	{

	}*/
}
