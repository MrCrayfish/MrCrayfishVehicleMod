package com.mrcrayfish.vehicle.client.util;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4f;
import javax.vecmath.Quat4f;
import javax.vecmath.Vector3f;

import net.minecraftforge.client.model.ForgeBlockStateV1;
import net.minecraftforge.common.model.TRSRTransformation;

public class TransformationBuilder 
{
	private float tx, ty, tz;
	private float rx, ry, rz;
	private float s;
    
    public TransformationBuilder setTranslation(float translateX, float translateY, float translateZ) 
    {
		this.tx = translateX;
		this.ty = translateY;
		this.tz = translateZ;
		return this;
	}
    
    public TransformationBuilder setRotation(float rotationX, float rotationY, float rotationZ) 
    {
    	this.rx = rotationX;
    	this.ry = rotationY;
    	this.rz = rotationZ;
		return this;
	}
    
    public TransformationBuilder setScale(float scale)
    {
    	this.s = scale;
    	return this;
    }
    
    public TRSRTransformation build()
    {
    	Vector3f translation = new Vector3f(tx / 16, ty / 16, tz / 16);
    	Quat4f rotation = TRSRTransformation.quatFromXYZDegrees(new Vector3f(rx, ry, rz));
    	Vector3f scale = new Vector3f(s, s, s);
    	return new TRSRTransformation(translation, rotation, scale, null);
    }
}
