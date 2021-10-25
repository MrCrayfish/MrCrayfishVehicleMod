package com.mrcrayfish.vehicle.common;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mrcrayfish.vehicle.common.cosmetic.CosmeticActions;
import com.mrcrayfish.vehicle.common.cosmetic.CosmeticProperties;
import com.mrcrayfish.vehicle.common.cosmetic.actions.Action;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageSyncCosmetics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.network.PacketDistributor;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Author: MrCrayfish
 */
public class CosmeticTracker
{
    private final ImmutableMap<ResourceLocation, Entry> selectedCosmetics;
    private final WeakReference<VehicleEntity> vehicleRef;
    private boolean dirty = false;

    public CosmeticTracker(VehicleEntity vehicle)
    {
        this.vehicleRef = new WeakReference<>(vehicle);
        ImmutableMap.Builder<ResourceLocation, Entry> builder = ImmutableMap.builder();
        vehicle.getProperties().getCosmetics().forEach((cosmeticId, cosmeticProperties) -> {
            builder.put(cosmeticId, new Entry(cosmeticProperties));
        });
        this.selectedCosmetics = builder.build();
    }

    public void tick(VehicleEntity vehicle)
    {
        if(!vehicle.level.isClientSide() && this.dirty)
        {
            PacketHandler.getPlayChannel().send(PacketDistributor.TRACKING_ENTITY.with(() -> vehicle), new MessageSyncCosmetics(vehicle.getId(), this.getDirtyEntries()));
            this.resetDirty();
        }

        this.selectedCosmetics.forEach((cosmeticId, entry) -> {
            entry.getActions().forEach(action -> action.tick(vehicle));
        });
    }

    public void setSelectedModel(ResourceLocation cosmeticId, ResourceLocation modelLocation)
    {
        if(FMLLoader.isProduction() && !this.isValidCosmeticModel(cosmeticId, modelLocation))
            return;
        Optional.ofNullable(this.selectedCosmetics.get(cosmeticId)).ifPresent(entry -> entry.setModelLocation(modelLocation));
        this.dirty = true;
    }

    private boolean isValidCosmeticModel(ResourceLocation cosmeticId, ResourceLocation modelLocation)
    {
        VehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle != null)
        {
            CosmeticProperties properties = vehicle.getProperties().getCosmetics().get(cosmeticId);
            return properties != null && properties.getModelLocations().contains(modelLocation);
        }
        return false;
    }

    @Nullable
    public ResourceLocation getSelectedModelLocation(ResourceLocation cosmeticId)
    {
        return Optional.ofNullable(this.selectedCosmetics.get(cosmeticId)).map(Entry::getModelLocation).orElse(null);
    }

    @Nullable
    @OnlyIn(Dist.CLIENT)
    public IBakedModel getSelectedBakedModel(ResourceLocation cosmeticId)
    {
        return Optional.ofNullable(this.selectedCosmetics.get(cosmeticId)).map(Entry::getBakedModel).orElse(Minecraft.getInstance().getModelManager().getMissingModel());
    }

    public List<Action> getActions(ResourceLocation cosmeticId)
    {
        return Optional.ofNullable(this.selectedCosmetics.get(cosmeticId)).map(Entry::getActions).orElse(Collections.emptyList());
    }

    private List<Pair<ResourceLocation, ResourceLocation>> getDirtyEntries()
    {
        List<Pair<ResourceLocation, ResourceLocation>> dirtyEntries = new ArrayList<>();
        this.selectedCosmetics.forEach((cosmeticId, entry) ->
        {
            if(entry.dirty)
            {
                dirtyEntries.add(Pair.of(cosmeticId, entry.getModelLocation()));
            }
        });
        return dirtyEntries;
    }

    private void resetDirty()
    {
        this.dirty = false;
        this.selectedCosmetics.forEach((cosmeticId, entry) -> entry.dirty = false);
    }

    public CompoundNBT write()
    {
        CompoundNBT tag = new CompoundNBT();
        ListNBT list = new ListNBT();
        this.selectedCosmetics.forEach((cosmeticId, entry) -> {
            CompoundNBT cosmeticTag = new CompoundNBT();
            cosmeticTag.putString("Id", cosmeticId.toString());
            cosmeticTag.putString("Model", entry.getModelLocation().toString());
            CompoundNBT actions = new CompoundNBT();
            entry.getActions().forEach(action -> {
                ResourceLocation id = CosmeticActions.getId(action.getClass());
                actions.put(id.toString(), action.save());
            });
            cosmeticTag.put("Actions", actions);
            list.add(cosmeticTag);
        });
        tag.put("Cosmetics", list);
        return tag;
    }

    public void read(CompoundNBT tag)
    {
        if(tag.contains("Cosmetics", Constants.NBT.TAG_LIST))
        {
            ListNBT list = tag.getList("Cosmetics", Constants.NBT.TAG_COMPOUND);
            list.forEach(nbt -> {
                CompoundNBT cosmeticTag = (CompoundNBT) nbt;
                ResourceLocation cosmeticId = new ResourceLocation(cosmeticTag.getString("Id"));
                ResourceLocation modelLocation = new ResourceLocation(cosmeticTag.getString("Model"));
                this.setSelectedModel(cosmeticId, modelLocation);
                CompoundNBT actions = cosmeticTag.getCompound("Actions");
                this.selectedCosmetics.get(cosmeticId).getActions().forEach(action -> {
                    ResourceLocation id = CosmeticActions.getId(action.getClass());
                    action.load(actions.getCompound(id.toString()));
                });
            });
        }
    }

    public void write(PacketBuffer buffer)
    {
        buffer.writeInt(this.selectedCosmetics.size());
        this.selectedCosmetics.forEach((cosmeticId, entry) -> {
            buffer.writeResourceLocation(cosmeticId);
            buffer.writeResourceLocation(entry.getModelLocation());
            buffer.writeInt(entry.getActions().size());
            entry.getActions().forEach(action -> {
                buffer.writeResourceLocation(CosmeticActions.getId(action.getClass()));
                buffer.writeNbt(action.save());
            });
        });
    }

    public void read(PacketBuffer buffer)
    {
        int size = buffer.readInt();
        for(int i = 0; i < size; i++)
        {
            ResourceLocation cosmeticId = buffer.readResourceLocation();
            ResourceLocation modelLocation = buffer.readResourceLocation();
            this.setSelectedModel(cosmeticId, modelLocation);
            int actionLength = buffer.readInt();
            if(actionLength > 0)
            {
                Map<ResourceLocation, CompoundNBT> dataMap = new HashMap<>();
                for(int j = 0; j < actionLength; j++)
                {
                    ResourceLocation id = buffer.readResourceLocation();
                    CompoundNBT data = buffer.readNbt();
                    dataMap.put(id, data);
                }
                this.selectedCosmetics.get(cosmeticId).getActions().forEach(action ->
                {
                    ResourceLocation id = CosmeticActions.getId(action.getClass());
                    CompoundNBT data = dataMap.get(id);
                    if(data != null)
                    {
                        action.load(data);
                    }
                });
            }
        }
    }

    private static class Entry
    {
        private ResourceLocation modelLocation;
        private final List<Action> actions;
        private boolean dirty;

        @Nullable
        @OnlyIn(Dist.CLIENT)
        private IBakedModel cachedModel;

        public Entry(CosmeticProperties properties)
        {
            this.modelLocation = properties.getModelLocations().get(0);
            this.actions = ImmutableList.copyOf(properties.getActions().stream().map(Supplier::get).collect(Collectors.toList()));
        }

        public void setModelLocation(ResourceLocation modelLocation)
        {
            this.modelLocation = modelLocation;
            this.dirty = true;
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> this.cachedModel = null);
        }

        public ResourceLocation getModelLocation()
        {
            return this.modelLocation;
        }

        @OnlyIn(Dist.CLIENT)
        public IBakedModel getBakedModel()
        {
            if(this.cachedModel == null)
            {
                this.cachedModel = Minecraft.getInstance().getModelManager().getModel(this.modelLocation);
            }
            return this.cachedModel;
        }

        public List<Action> getActions()
        {
            return this.actions;
        }
    }
}
