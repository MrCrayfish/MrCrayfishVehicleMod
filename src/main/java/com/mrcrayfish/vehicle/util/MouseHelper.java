package com.mrcrayfish.vehicle.util;

public class MouseHelper
{
	public static boolean isMouseWithin(int mouseX, int mouseY, int x, int y, int width, int height)
	{
		return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}
}
