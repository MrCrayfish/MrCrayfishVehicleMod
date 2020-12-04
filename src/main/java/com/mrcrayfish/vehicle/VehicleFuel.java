package com.mrcrayfish.vehicle;

import java.util.HashMap;
import java.util.Map;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class VehicleFuel
	{
	public static double DEFAULT_CONSUMPTION_FACTOR = 100.0;
	public static Map<String, Double> getDefaultFuels()
		{
		Map<String,Double> fuelMap = new HashMap<>();
		fuelMap.put("fuelium", 1.0);
		fuelMap.put("lava", 10.0);
    	return fuelMap;
		}

	public static String getFluidName(FluidTank tank)
		{
		return getFluidName(tank.getFluid());
		}
	public static String getFluidName(FluidStack fluid)
		{
		return getFluidName(fluid.getFluid());
		}
	private static String getFluidName(Fluid fluid)
		{
		return fluid.getName();
		}
	public static boolean isFuel(FluidStack fluid)
		{
		return isFuel(fluid.getFluid());
		}

	private static boolean isFuel(Fluid fluid)
		{
		return isFuel(fluid.getName());
		}

	public static boolean isFuel(FluidTank tank)
		{
		return isFuel(tank.getFluid());
		}
	private static boolean isFuel(String fuelName)
		{
		return VehicleConfig.SERVER.fuel_list.containsKey(fuelName);
		}
	public static double getFuelConsumptionFactorForFuel(String fuelName)
		{
		if (isFuel(fuelName))
			{
			return VehicleConfig.SERVER.fuel_list.get(fuelName);
			}
		return DEFAULT_CONSUMPTION_FACTOR;
		}

	}
