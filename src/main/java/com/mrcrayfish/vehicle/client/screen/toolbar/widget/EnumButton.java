package com.mrcrayfish.vehicle.client.screen.toolbar.widget;

import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

/**
 * Author: MrCrayfish
 */
public class EnumButton<T extends Enum<?>> extends IconButton
{
    private final Class<T> enumClass;
    private T currentEnum;

    public EnumButton(int x, int y, int width, int height, ITextComponent label, Class<T> enumClass, T initialEnum, Button.IPressable onPress)
    {
        super(x, y, width, height, initialEnum instanceof IconProvider ? (IconProvider) initialEnum : null, label, onPress);
        this.enumClass = enumClass;
        this.currentEnum = initialEnum;
        this.updateLabel();
    }

    @Override
    public void onPress()
    {
        this.next();
        super.onPress();
    }

    public T getCurrentEnum()
    {
        return this.currentEnum;
    }

    private void next()
    {
        T[] enums = this.enumClass.getEnumConstants();
        this.currentEnum = enums[(this.currentEnum.ordinal() + 1) % enums.length];
        this.updateLabel();
    }

    private void updateLabel()
    {
        String enumName = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(this.enumClass.getSimpleName()), "_").toLowerCase(Locale.ENGLISH);
        this.setMessage(new TranslationTextComponent(".enum." + enumName + "." + this.currentEnum.name().toLowerCase()));
        if(this.currentEnum instanceof IconProvider)
        {
            this.setIcon((IconProvider) this.currentEnum);
        }
    }
}
