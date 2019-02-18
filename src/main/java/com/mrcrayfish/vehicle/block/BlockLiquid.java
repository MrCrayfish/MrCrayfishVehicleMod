package com.mrcrayfish.vehicle.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public class BlockLiquid extends BlockFluidClassic
{
    private Vec3d colorFog;

    public BlockLiquid(String id, Fluid fluid, Material material)
    {
        this(id, fluid, material, null);
    }

    public BlockLiquid(String id, Fluid fluid, Material material, double red, double green, double blue)
    {
        this(id, fluid, material, new Vec3d(red / 255.0, green / 255.0, blue / 255.0));
    }

    public BlockLiquid(String id, Fluid fluid, Material material, @Nullable Vec3d colorFog)
    {
        super(fluid, material);
        this.setUnlocalizedName(id);
        this.setRegistryName(id);
        this.colorFog = colorFog;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Vec3d getFogColor(World world, BlockPos pos, IBlockState state, Entity entity, Vec3d originalColor, float partialTicks)
    {
        return colorFog == null ? originalColor : colorFog;
    }
}
