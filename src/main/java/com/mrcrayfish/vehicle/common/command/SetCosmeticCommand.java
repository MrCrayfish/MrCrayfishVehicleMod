package com.mrcrayfish.vehicle.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mrcrayfish.vehicle.common.CosmeticTracker;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.ResourceLocationArgument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.List;

/**
 * Author: MrCrayfish
 */
public class SetCosmeticCommand
{
    public static void register(CommandDispatcher<CommandSource> dispatcher)
    {
        dispatcher.register(Commands.literal("setcosmetic")
            .requires(source -> source.hasPermission(2))
            .then(Commands.argument("id", ResourceLocationArgument.id())
                .then(Commands.argument("model", ResourceLocationArgument.id())
                    .executes(SetCosmeticCommand::handle))));
    }

    private static int handle(CommandContext<CommandSource> context)
    {
        Entity entity = context.getSource().getEntity();
        if(!(entity instanceof PlayerEntity))
            return 1;

        AxisAlignedBB box = entity.getBoundingBox().inflate(10, 10, 10);
        List<VehicleEntity> vehicles = entity.level.getLoadedEntitiesOfClass(VehicleEntity.class, box);
        if(vehicles.isEmpty())
            return 1;

        double closetDistance = Double.MAX_VALUE;
        VehicleEntity closestVehicle = null;
        for(VehicleEntity vehicle : vehicles)
        {
            double distance = vehicle.distanceToSqr(entity);
            if(distance < closetDistance)
            {
                closestVehicle = vehicle;
                closetDistance = distance;
            }
        }

        if(closestVehicle == null)
            return 1;

        ResourceLocation cosmeticId = context.getArgument("id", ResourceLocation.class);
        ResourceLocation modelLocation = context.getArgument("model", ResourceLocation.class);
        CosmeticTracker tracker = closestVehicle.getCosmeticTracker();
        tracker.setSelectedModel(cosmeticId, modelLocation);
        return 1;
    }
}
