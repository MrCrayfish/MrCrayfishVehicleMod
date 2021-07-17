package com.mrcrayfish.vehicle.entity;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;

/**
 * Author: MrCrayfish
 */
public interface IEngineType
{
    ResourceLocation getId();

    int hashCode();

    default TranslationTextComponent getEngineName()
    {
        return new TranslationTextComponent(this.getId().getNamespace() + ".engine_type." + this.getId().getPath() + ".name");
    }
}
