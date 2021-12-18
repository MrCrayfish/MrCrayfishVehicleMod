package com.mrcrayfish.vehicle.client.screen;

import com.mrcrayfish.vehicle.client.screen.toolbar.AbstractToolbarScreen;
import com.mrcrayfish.vehicle.client.screen.toolbar.widget.Spacer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Author: MrCrayfish
 */
public class DashboardScreen extends AbstractToolbarScreen
{
    public DashboardScreen(@Nullable Screen parent)
    {
        super(StringTextComponent.EMPTY, parent);
    }

    @Override
    protected void loadWidgets(List<Widget> widgets)
    {
        widgets.add(Spacer.of(100));
    }

    @Override
    public boolean isPauseScreen()
    {
        return false;
    }
}
