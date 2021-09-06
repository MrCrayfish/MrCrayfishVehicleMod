package com.mrcrayfish.vehicle.util;

import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * Author: MrCrayfish
 */
public class LazyValueFunction<U, T>
{
    private final Function<U, T> function;
    @Nullable
    private T value;

    public LazyValueFunction(Function<U, T> function)
    {
        this.function = function;
    }

    public T get(U u)
    {
        if(this.value == null)
        {
            this.value = this.function.apply(u);
        }
        return this.value;
    }

    public void invalidate()
    {
        this.value = null;
    }
}
