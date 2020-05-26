package com.mrcrayfish.vehicle.fluid;

import com.mrcrayfish.vehicle.Reference;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Supplier;

public abstract class ModFluid extends ForgeFlowingFluid
{
    private final float fogDensity, fogRed, fogGreen, fogBlue;

    protected ModFluid(Supplier<? extends Fluid> still, Supplier<? extends Fluid> flowing, RegistryObject<FlowingFluidBlock> block,
                       int density, int viscosity, float fogDensity, int fogRed, int fogGreen, int fogBlue)
    {
        super(new Properties(still, flowing,
                FluidAttributes.builder(getTexture(block, "_still"), getTexture(block, "_flowing"))
                        .density(density).viscosity(viscosity)
                        .sound(SoundEvents.ITEM_BUCKET_FILL, SoundEvents.ITEM_BUCKET_EMPTY))
                .block(block));

        this.fogDensity = fogDensity;
        this.fogRed = fogRed / 255F;
        this.fogGreen = fogGreen / 255F;
        this.fogBlue = fogBlue / 255F;
    }

    private static ResourceLocation getTexture(RegistryObject<FlowingFluidBlock> block, String suffix)
    {
        return new ResourceLocation(Reference.MOD_ID, "block/" + block.getId().getPath() + suffix);
    }

    public float getFogDensity()
    {
        return fogDensity;
    }

    public float getFogRed()
    {
        return fogRed;
    }

    public float getFogGreen()
    {
        return fogGreen;
    }

    public float getFogBlue()
    {
        return fogBlue;
    }
}
