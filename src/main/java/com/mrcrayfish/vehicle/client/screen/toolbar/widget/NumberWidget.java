package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.util.text.StringTextComponent;

/**
 * Author: MrCrayfish
 */
public class NumberWidget extends TextFieldWidget
{
    private int min = Integer.MIN_VALUE;
    private int max = Integer.MAX_VALUE;

    public NumberWidget(int width)
    {
        super(Minecraft.getInstance().font, 0, 0, width, 20, StringTextComponent.EMPTY);
        this.setFilter(s ->
        {
            try
            {
                int value = Integer.parseInt(s);
                return value >= this.getMin() && value <= this.getMax();
            }
            catch(NumberFormatException e)
            {
                return false;
            }
        });
    }

    public NumberWidget setMin(int min)
    {
        this.min = min;
        return this;
    }

    public int getMin()
    {
        return this.min;
    }

    public NumberWidget setMax(int min)
    {
        this.min = min;
        return this;
    }

    public int getMax()
    {
        return this.max;
    }
}
