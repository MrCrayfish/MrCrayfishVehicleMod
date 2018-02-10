package com.mrcrayfish.vehicle.entity;

import com.mrcrayfish.vehicle.init.ModItems;
import com.mrcrayfish.vehicle.init.ModSounds;
import com.mrcrayfish.vehicle.network.PacketHandler;
import com.mrcrayfish.vehicle.network.message.MessageAccelerating;
import com.mrcrayfish.vehicle.network.message.MessageDrift;
import com.mrcrayfish.vehicle.network.message.MessageTurn;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

/**
 * Author: MrCrayfish
 */
public abstract class EntityVehicle extends Entity
{
    private static final DataParameter<EnumDyeColor> COLOR = EntityDataManager.createKey(EntityVehicle.class, CustomDataSerializers.DYE_COLOR);
    private static final DataParameter<Float> SPEED = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.FLOAT);
    private static final DataParameter<Integer> TURN_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);
    private static final DataParameter<Boolean> DRIFTING = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ACCELERATION_DIRECTION = EntityDataManager.createKey(EntityVehicle.class, DataSerializers.VARINT);

    public double maxSpeed = 10.0;
    public float currentSpeed;

    public float wheelAngle;
    public float prevWheelAngle;
    public float frontWheelRotation;
    public float prevFrontWheelRotation;
    public float rearWheelRotation;
    public float prevRearWheelRotation;

    public float deltaYaw;
    public float additionalYaw;
    public float prevAdditionalYaw;

    private int lerpSteps;
    private double lerpX;
    private double lerpY;
    private double lerpZ;
    private double lerpYaw;
    private double lerpPitch;

    public int soundLoop;

    @SideOnly(Side.CLIENT)
    public ItemStack body;

    protected EntityVehicle(World worldIn)
    {
        super(worldIn);
        this.setSize(1F, 1F);
        this.stepHeight = 1.0F;
    }

    public EntityVehicle(World worldIn, double posX, double posY, double posZ)
    {
        this(worldIn);
        this.setPosition(posX, posY, posZ);
    }

    public void setMaxSpeed(double maxSpeed)
    {
        this.maxSpeed = maxSpeed;
    }

    @Override
    public boolean canBeCollidedWith()
    {
        return true;
    }

    @Override
    protected void entityInit()
    {
        this.dataManager.register(SPEED, 0F);
        this.dataManager.register(COLOR, EnumDyeColor.BLUE);
        this.dataManager.register(TURN_DIRECTION, TurnDirection.FORWARD.ordinal());
        this.dataManager.register(DRIFTING, false);
        this.dataManager.register(ACCELERATION_DIRECTION, Acceleration.NONE.ordinal());
    }

    @Override
    public void onUpdate()
    {
        super.onUpdate();
        this.tickLerp();
    }

    /**
     * Smooths the rendering on servers
     */
    private void tickLerp()
    {
        if (this.lerpSteps > 0 && !this.canPassengerSteer())
        {
            double d0 = this.posX + (this.lerpX - this.posX) / (double)this.lerpSteps;
            double d1 = this.posY + (this.lerpY - this.posY) / (double)this.lerpSteps;
            double d2 = this.posZ + (this.lerpZ - this.posZ) / (double)this.lerpSteps;
            double d3 = MathHelper.wrapDegrees(this.lerpYaw - (double)this.rotationYaw);
            this.rotationYaw = (float)((double)this.rotationYaw + d3 / (double)this.lerpSteps);
            this.rotationPitch = (float)((double)this.rotationPitch + (this.lerpPitch - (double)this.rotationPitch) / (double)this.lerpSteps);
            --this.lerpSteps;
            this.setPosition(d0, d1, d2);
            this.setRotation(this.rotationYaw, this.rotationPitch);
        }
    }

    @Override
    public void onEntityUpdate()
    {
        super.onEntityUpdate();

        this.prevWheelAngle = this.wheelAngle;
        this.prevFrontWheelRotation = this.frontWheelRotation;
        this.prevRearWheelRotation = this.rearWheelRotation;
        this.prevAdditionalYaw = this.additionalYaw;

        if(world.isRemote)
        {
            this.onClientUpdate();
        }

        this.currentSpeed = this.getSpeed();

        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null)
        {
            /* Handle the current speed of the vehicle based on rider's forward movement */
            this.updateSpeed();
            this.updateDrifting();
            this.updateWheels();

            this.setSpeed(currentSpeed);

            this.rotationYaw -= this.deltaYaw;
            this.motionY -= 0.08D;

            this.moveRelative(0, 0, currentSpeed, 0.01F);
            this.move(MoverType.SELF, this.motionX * Math.abs(currentSpeed), this.motionY, this.motionZ * Math.abs(currentSpeed));

            this.motionY *= 0.9800000190734863D;
            this.motionX *= 0.8;
            this.motionZ *= 0.8;

            this.doBlockCollisions();
            this.createParticles();
            this.playSounds();
        }
        else
        {
            currentSpeed = 0F;
            this.setSpeed(currentSpeed);
        }
    }

    private void updateSpeed()
    {
        Acceleration acceleration = this.getAcceleration();
        if(acceleration == Acceleration.FORWARD)
        {
            this.currentSpeed += 0.5F;
            if(this.currentSpeed > this.maxSpeed)
            {
                this.currentSpeed = (float) this.maxSpeed;
            }
        }
        else if(acceleration == Acceleration.REVERSE)
        {
            this.currentSpeed -= 4.0F;
            if(this.currentSpeed < -4.0F)
            {
                this.currentSpeed = -4.0F;
            }
        }
        else
        {
            this.currentSpeed *= 0.85;
        }

        TurnDirection turnDirection = this.getTurnDirection();
        if(this.isDrifting() && acceleration == Acceleration.FORWARD && turnDirection != TurnDirection.FORWARD)
        {
            this.currentSpeed *= 0.95F;
        }
    }

    private void updateDrifting()
    {
        TurnDirection turnDirection = this.getTurnDirection();
        if(this.isDrifting() && turnDirection != TurnDirection.FORWARD)
        {
            this.additionalYaw = MathHelper.clamp(this.additionalYaw + 5F * turnDirection.getDir(), -35F, 35F);
        }
        else
        {
            this.additionalYaw *= 0.85F;
        }
    }

    private void updateWheels()
    {
        float wheelRotation = 45F; //change to global var
        float speedPercent = (float) (this.currentSpeed / this.maxSpeed);

        TurnDirection direction = this.getTurnDirection();
        this.wheelAngle = direction == TurnDirection.RIGHT ? -wheelRotation : direction == TurnDirection.LEFT ? wheelRotation : wheelAngle * 0.5F;
        this.wheelAngle *= Math.max(0.25F, 1.0F - Math.abs(speedPercent));
        this.deltaYaw = this.wheelAngle * speedPercent / (this.isDrifting() ? 1.5F : 2F);

        Acceleration acceleration = this.getAcceleration();
        if(acceleration == Acceleration.FORWARD)
        {
            this.rearWheelRotation -= 68F * (1.0 - speedPercent);
        }
        this.frontWheelRotation -= (68F * speedPercent);
        this.rearWheelRotation -= (68F * speedPercent);
    }

    private void createParticles()
    {
        if(this.getAcceleration() == Acceleration.FORWARD)
        {
            if(this.isDrifting())
            {
                for(int i = 0; i < 3; i++)
                {
                    this.createRunningParticles();
                }
            }
            else
            {
                this.createRunningParticles();
            }
        }
    }

    private void playSounds()
    {
        if(soundLoop % 4 == 0)
        {
            if(this.getAcceleration() == Acceleration.FORWARD)
            {
                world.playSound(posX, posY, posZ, ModSounds.DRIVING, SoundCategory.BLOCKS, 0.5F, 1.0F, false);
            }
            else
            {
                world.playSound(posX, posY, posZ, ModSounds.IDLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }
        }
        soundLoop++;
    }

    @SideOnly(Side.CLIENT)
    public void onClientUpdate()
    {
        EntityLivingBase entity = (EntityLivingBase) this.getControllingPassenger();
        if(entity != null && entity.equals(Minecraft.getMinecraft().player))
        {
            Acceleration acceleration = Acceleration.fromEntity(entity);
            if(this.getAcceleration() != acceleration)
            {
                this.setAcceleration(acceleration);
                PacketHandler.INSTANCE.sendToServer(new MessageAccelerating(acceleration));
            }

            boolean drifting = Minecraft.getMinecraft().gameSettings.keyBindJump.isKeyDown();
            if(this.isDrifting() != drifting)
            {
                this.setDrifting(drifting);
                PacketHandler.INSTANCE.sendToServer(new MessageDrift(drifting));
            }

            TurnDirection direction = TurnDirection.FORWARD;
            if(entity.moveStrafing < 0)
            {
                direction = TurnDirection.RIGHT;
            }
            else if(entity.moveStrafing > 0)
            {
                direction = TurnDirection.LEFT;
            }
            if(this.getTurnDirection() != direction)
            {
                this.setTurnDirection(direction);
                PacketHandler.INSTANCE.sendToServer(new MessageTurn(direction));
            }
        }
    }

    @Override
    public boolean processInitialInteract(EntityPlayer player, EnumHand hand)
    {
        if(!world.isRemote)
        {
            ItemStack heldItem = player.getHeldItem(hand);
            if(!heldItem.isEmpty() && heldItem.getItem() instanceof ItemDye)
            {
                this.setColor(EnumDyeColor.byDyeDamage(heldItem.getItemDamage()));
            }
            else
            {
                player.startRiding(this);
            }
        }
        return true;
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
        this.setDead();
        return true;
    }

    @Override
    public double getMountedYOffset()
    {
        return 9 * 0.0625;
    }

    @Override
    protected boolean canBeRidden(Entity entityIn)
    {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound compound)
    {
        if(compound.hasKey("color", Constants.NBT.TAG_INT))
        {
            this.setColor(EnumDyeColor.byMetadata(compound.getInteger("color")));
        }
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound compound)
    {
        compound.setInteger("color", this.getColor().getMetadata());
    }

    @Nullable
    public Entity getControllingPassenger()
    {
        return this.getPassengers().isEmpty() ? null : (Entity)this.getPassengers().get(0);
    }

    @Override
    public void updatePassenger(Entity passenger)
    {
        super.updatePassenger(passenger);
        //TODO change to config option
        //passenger.rotationYaw -= deltaYaw;
        //passenger.setRotationYawHead(this.rotationYaw);
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {}

    public boolean isMoving()
    {
        return this.currentSpeed != 0;
    }

    public void setColor(EnumDyeColor color)
    {
        this.dataManager.set(COLOR, color);
    }

    public EnumDyeColor getColor()
    {
        return this.dataManager.get(COLOR);
    }

    public void setSpeed(float speed)
    {
        this.dataManager.set(SPEED, speed);
    }

    public float getSpeed()
    {
        return this.currentSpeed;
    }

    public float getNormalSpeed()
    {
        return (float) (this.currentSpeed / maxSpeed);
    }

    public double getKilometersPreHour()
    {
        return Math.sqrt(Math.pow(this.posX - this.prevPosX, 2) + Math.pow(this.posZ - this.prevPosZ, 2)) * 3.6;
    }

    public void setTurnDirection(TurnDirection turnDirection)
    {
        this.dataManager.set(TURN_DIRECTION, turnDirection.ordinal());
    }

    public TurnDirection getTurnDirection()
    {
        return TurnDirection.values()[this.dataManager.get(TURN_DIRECTION)];
    }

    public void setDrifting(boolean drifting)
    {
        this.dataManager.set(DRIFTING, drifting);
    }

    public boolean isDrifting()
    {
        return this.dataManager.get(DRIFTING);
    }

    public void setAcceleration(Acceleration direction)
    {
        this.dataManager.set(ACCELERATION_DIRECTION, direction.ordinal());
    }

    public Acceleration getAcceleration()
    {
        return Acceleration.values()[this.dataManager.get(ACCELERATION_DIRECTION)];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport)
    {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = (double)yaw;
        this.lerpPitch = (double)pitch;
        this.lerpSteps = 10;
    }

    @Override
    protected void addPassenger(Entity passenger)
    {
        super.addPassenger(passenger);
        if(this.canPassengerSteer() && this.lerpSteps > 0)
        {
            this.lerpSteps = 0;
            this.posX = this.lerpX;
            this.posY = this.lerpY;
            this.posZ = this.lerpZ;
            this.rotationYaw = (float)this.lerpYaw;
            this.rotationPitch = (float)this.lerpPitch;
        }
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key)
    {
        super.notifyDataManagerChange(key);
        if(world.isRemote)
        {
            if(COLOR.equals(key))
            {
                body = new ItemStack(ModItems.BODY, 1, this.dataManager.get(COLOR).getMetadata());
            }
        }
    }

    public enum TurnDirection
    {
        LEFT(1), FORWARD(0), RIGHT(-1);

        final int dir;

        TurnDirection(int dir)
        {
            this.dir = dir;
        }

        public int getDir()
        {
            return dir;
        }
    }

    public enum Acceleration
    {
        FORWARD, NONE, REVERSE;

        public static Acceleration fromEntity(EntityLivingBase entity)
        {
            if(entity.moveForward > 0)
            {
                return FORWARD;
            }
            else if(entity.moveForward < 0)
            {
                return REVERSE;
            }
            return NONE;
        }
    }
}
