package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.entity.IWheelType;
import com.mrcrayfish.vehicle.entity.PoweredVehicleEntity;
import com.mrcrayfish.vehicle.entity.VehicleProperties;
import com.mrcrayfish.vehicle.entity.Wheel;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static net.minecraft.block.material.Material.*;

/**
 * Categories materials into a surface type to determine the
 * Author: MrCrayfish
 */
public class SurfaceHelper
{
    private static final ImmutableMap<Material, SurfaceType> MATERIAL_TO_SURFACE_TYPE;

    static
    {
        ImmutableMap.Builder<Material, SurfaceType> builder = new ImmutableMap.Builder<>();
        builder.put(CLOTH_DECORATION, SurfaceType.DIRT);
        builder.put(PLANT, SurfaceType.DIRT);
        builder.put(WATER_PLANT, SurfaceType.DIRT);
        builder.put(TOP_SNOW, SurfaceType.SNOW);
        builder.put(CLAY, SurfaceType.DIRT);
        builder.put(DIRT, SurfaceType.DIRT);
        builder.put(GRASS, SurfaceType.DIRT);
        builder.put(ICE_SOLID, SurfaceType.SOLID);
        builder.put(SAND, SurfaceType.DIRT);
        builder.put(SPONGE, SurfaceType.DIRT);
        builder.put(SHULKER_SHELL, SurfaceType.SOLID);
        builder.put(WOOD, SurfaceType.SOLID);
        builder.put(NETHER_WOOD, SurfaceType.SOLID);
        builder.put(BAMBOO, SurfaceType.SOLID);
        builder.put(WOOL, SurfaceType.DIRT);
        builder.put(EXPLOSIVE, SurfaceType.SNOW);
        builder.put(LEAVES, SurfaceType.SNOW);
        builder.put(GLASS, SurfaceType.SOLID);
        builder.put(ICE, SurfaceType.SOLID);
        builder.put(CACTUS, SurfaceType.SNOW);
        builder.put(STONE, SurfaceType.SOLID);
        builder.put(METAL, SurfaceType.SOLID);
        builder.put(SNOW, SurfaceType.SNOW);
        builder.put(HEAVY_METAL, SurfaceType.SOLID);
        builder.put(BARRIER, SurfaceType.SOLID);
        builder.put(PISTON, SurfaceType.SOLID);
        builder.put(CORAL, SurfaceType.SNOW);
        builder.put(CAKE, SurfaceType.SNOW);
        MATERIAL_TO_SURFACE_TYPE = builder.build();
    }

    public static SurfaceType getSurfaceTypeForMaterial(Material material)
    {
        return MATERIAL_TO_SURFACE_TYPE.getOrDefault(material, SurfaceType.NONE);
    }

    public static float getSurfaceModifier(PoweredVehicleEntity vehicle)
    {
        VehicleProperties properties = vehicle.getProperties();
        List<Wheel> wheels = properties.getWheels();
        if(!vehicle.hasWheelStack() || wheels.isEmpty())
            return 1.0F;

        Optional<IWheelType> optional = vehicle.getWheelType();
        if(!optional.isPresent())
            return 1.0F;

        int wheelCount = 0;
        float surfaceModifier = 0F;
        for(int i = 0; i < wheels.size(); i++)
        {
            double wheelX = vehicle.getWheelPositions()[i * 3];
            double wheelY = vehicle.getWheelPositions()[i * 3 + 1];
            double wheelZ = vehicle.getWheelPositions()[i * 3 + 2];
            int x = MathHelper.floor(vehicle.getX() + wheelX);
            int y = MathHelper.floor(vehicle.getY() + wheelY - 0.2D);
            int z = MathHelper.floor(vehicle.getZ() + wheelZ);
            BlockState state = vehicle.level.getBlockState(new BlockPos(x, y, z));
            SurfaceType surfaceType = getSurfaceTypeForMaterial(state.getMaterial());
            if(surfaceType == SurfaceType.NONE)
                continue;
            IWheelType wheelType = optional.get();
            surfaceModifier += (1.0F - surfaceType.wheelFunction.apply(wheelType));
            wheelCount++;
        }
        return 1.0F - (surfaceModifier / Math.max(1F, wheelCount));
    }

    public enum SurfaceType
    {
        SOLID(IWheelType::getRoadMultiplier),
        DIRT(IWheelType::getDirtMultiplier),
        SNOW(IWheelType::getSnowMultiplier),
        NONE(type -> 0F);

        private Function<IWheelType, Float> wheelFunction;

        SurfaceType(Function<IWheelType, Float> wheelFunction)
        {
            this.wheelFunction = wheelFunction;
        }
    }
}
