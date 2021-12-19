package com.mrcrayfish.vehicle.client.screen;

import com.mrcrayfish.vehicle.Reference;
import com.mrcrayfish.vehicle.client.screen.toolbar.AbstractToolbarScreen;
import com.mrcrayfish.vehicle.client.screen.toolbar.widget.DoorButton;
import com.mrcrayfish.vehicle.client.screen.toolbar.widget.IconButton;
import com.mrcrayfish.vehicle.client.screen.toolbar.widget.Spacer;
import com.mrcrayfish.vehicle.common.cosmetic.actions.OpenableAction;
import com.mrcrayfish.vehicle.entity.VehicleEntity;
import com.mrcrayfish.vehicle.entity.properties.VehicleProperties;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class DashboardScreen extends AbstractToolbarScreen
{
    private WeakReference<VehicleEntity> vehicleRef;

    public DashboardScreen(@Nullable Screen parent, VehicleEntity vehicle)
    {
        super(StringTextComponent.EMPTY, parent);
        this.vehicleRef = new WeakReference<>(vehicle);
    }

    @Override
    protected void loadWidgets(List<Widget> widgets)
    {
        widgets.add(new IconButton(20, 20, Icons.BACK, new TranslationTextComponent("vehicle.toolbar.label.back"), onPress -> {}));
        widgets.add(Spacer.of(5));

        VehicleEntity vehicle = this.vehicleRef.get();
        if(vehicle != null)
        {
            VehicleProperties properties = vehicle.getProperties();
            properties.getCosmetics().forEach((cosmeticId, cosmeticProperties) -> {
                vehicle.getCosmeticTracker().getSelectedCosmeticEntry(cosmeticId)
                    .flatMap(entry -> entry.getActions().stream().filter(action -> action instanceof OpenableAction).findAny())
                    .ifPresent(action -> widgets.add(new DoorButton(vehicle, cosmeticProperties, (OpenableAction) action)));
            });
        }
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }

    public enum Icons implements IconButton.IconProvider
    {
        BACK,
        DOOR;

        private static final ResourceLocation ICON_TEXTURE = new ResourceLocation(Reference.MOD_ID, "textures/gui/icons.png");

        @Override
        public ResourceLocation getTextureLocation()
        {
            return ICON_TEXTURE;
        }

        @Override
        public int getU()
        {
            return (this.ordinal() % 10) * 10;
        }

        @Override
        public int getV()
        {
            return (this.ordinal() / 10) * 10;
        }
    }
}
