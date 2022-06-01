package com.mrcrayfish.vehicle.client.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ComponentLoader
{
    private final List<ComponentModel> registeredModels = new ArrayList<>();
    private final String modId;

    public ComponentLoader(String modId)
    {
        this.modId = modId;
    }

    public ComponentModel create(String path)
    {
        ComponentModel model = new ComponentModel(new ResourceLocation(this.modId, path));
        this.registeredModels.add(model);
        return model;
    }

    public String getModId()
    {
        return this.modId;
    }

    public List<ComponentModel> getModels()
    {
        return ImmutableList.copyOf(this.registeredModels);
    }
}
