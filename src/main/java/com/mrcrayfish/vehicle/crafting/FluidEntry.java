package com.mrcrayfish.vehicle.crafting;

import com.google.gson.JsonObject;
import net.minecraft.fluid.Fluid;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * Author: MrCrayfish
 */
public class FluidEntry
{
    private Fluid fluid;
    private int amount;

    public FluidEntry(Fluid fluid, int amount)
    {
        this.fluid = fluid;
        this.amount = amount;
    }

    public Fluid getFluid()
    {
        return this.fluid;
    }

    public int getAmount()
    {
        return this.amount;
    }

    public FluidStack createStack()
    {
        return new FluidStack(this.fluid, this.amount);
    }

    public static FluidEntry fromJson(JsonObject object)
    {
        if(!object.has("fluid") || !object.has("amount"))
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, missing fluid and amount");
        }
        ResourceLocation fluidId = new ResourceLocation(JSONUtils.getAsString(object, "fluid"));
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidId);
        if(fluid == null)
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, unknown fluid: " + fluidId.toString());
        }
        int amount = JSONUtils.getAsInt(object, "amount");
        if(amount < 1)
        {
            throw new com.google.gson.JsonSyntaxException("Invalid fluid entry, amount must be more than zero");
        }
        return new FluidEntry(fluid, amount);
    }

    public JsonObject toJson()
    {
        JsonObject object = new JsonObject();
        object.addProperty("fluid", this.fluid.getRegistryName().toString());
        object.addProperty("amount", this.amount);
        return object;
    }

    public void write(PacketBuffer buffer)
    {
        buffer.writeUtf(this.fluid.getRegistryName().toString(), 256);
        buffer.writeInt(this.amount);
    }

    public static FluidEntry read(PacketBuffer buffer)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(buffer.readUtf(256)));
        int amount = buffer.readInt();
        return new FluidEntry(fluid, amount);
    }

    public static FluidEntry of(Fluid fluid, int amount)
    {
        return new FluidEntry(fluid, amount);
    }
}
