package zabuton.entity;

import io.netty.buffer.ByteBuf;

import java.util.List;

import cpw.mods.fml.common.ObfuscationReflectionHelper;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import zabuton.Zabuton;

public class EntityZabuton extends Entity implements IProjectile, IEntityAdditionalSpawnData {

    protected double zabutonX;
    protected double zabutonY;
    protected double zabutonZ;
    protected double zabutonYaw;
    protected double zabutonPitch;
    protected double velocityX;
    protected double velocityY;
    protected double velocityZ;
    protected float health;
    public boolean isDispensed;
    public byte color;

    protected int boatPosRotationIncrements;

    public EntityZabuton(World world) {
        super(world);
        preventEntitySpawning = true;
        setSize(0.81F, 0.2F);
        yOffset = 0F;
        health = 20.0f;
        isDispensed = false;
        color = (byte) 0xFF;
    }

    public EntityZabuton(World world, byte pColor) {
        this(world);
        color = pColor;
    }

    public EntityZabuton(World world, ItemStack itemstack) {
        this(world, (byte) (itemstack.getItemDamage() & 0x0f));
    }

    public EntityZabuton(World world, double x, double y, double z, byte pColor) {
        this(world, pColor);
        setPositionAndRotation(x, y + (double) yOffset, z, 0F, 0F);
        motionX = 0.0D;
        motionY = 0.0D;
        motionZ = 0.0D;
    }

    @Override
    public void setThrowableHeading(double x, double y, double z, float speed, float f1) {
        // ディスペンサー用
        float dist = MathHelper.sqrt_double(x * x + y * y + z * z);
        x /= dist;
        y /= dist;
        z /= dist;
        x += rand.nextGaussian() * 0.0074999998323619366D * (double) f1;
        y += rand.nextGaussian() * 0.0074999998323619366D * (double) f1;
        z += rand.nextGaussian() * 0.0074999998323619366D * (double) f1;
        x *= speed;
        y *= speed;
        z *= speed;
        motionX = x;
        motionY = y;
        motionZ = z;
        float f3 = MathHelper.sqrt_double(x * x + z * z);
        prevRotationYaw = rotationYaw = (float) ((Math.atan2(x, z) * 180D) / 3.1415927410125732D);
        prevRotationPitch = rotationPitch = (float) ((Math.atan2(y, f3) * 180D) / 3.1415927410125732D);
        setDispensed(true);
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    @Override
    protected void entityInit() {
        dataWatcher.addObject(17, (byte) (isDispensed ? 0x01 : 0x00));
        dataWatcher.addObject(18, 0);
        dataWatcher.addObject(19, (byte) 0xFF);
    }

    @Override
    public AxisAlignedBB getCollisionBox(Entity par1Entity) {
        return par1Entity.boundingBox;
    }

    @Override
    public AxisAlignedBB getBoundingBox() {
        return this.boundingBox;
    }

    @Override
    public boolean canBePushed() {
        return true;
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        color = nbttagcompound.getByte("Color");
        health = nbttagcompound.getFloat("Health");
    }

    @Override
    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Color", (byte) (color & 0x0f));
        nbttagcompound.setFloat("Health", health);
    }

    @Override
    public void writeSpawnData(ByteBuf data) {
        data.writeByte(color);
        data.writeFloat(rotationYaw);
    }

    @Override
    public void readSpawnData(ByteBuf data) {
        color = data.readByte();
        setRotation(data.readFloat(), 0.0F);
    }

    @Override
    public double getMountedYOffset() {
        if (riddenByEntity instanceof EntitySpider) {
            return (double) height * 0.0D - 0.1D;
        }
        if (riddenByEntity instanceof EntityZombie ||
                riddenByEntity instanceof EntityEnderman) {
            return (double) height * 0.0D - 0.4D;
        }

        return (double) height * 0.0D + 0.1D;
    }

    @Override
    public boolean handleWaterMovement() {
        // 独自の水没判定
        int var4 = MathHelper.floor_double(boundingBox.minX);
        int var5 = MathHelper.floor_double(boundingBox.maxX + 1.0D);
        int var6 = MathHelper.floor_double(boundingBox.minY);
        int var7 = MathHelper.floor_double(boundingBox.maxY + 1.0D);
        int var8 = MathHelper.floor_double(boundingBox.minZ);
        int var9 = MathHelper.floor_double(boundingBox.maxZ + 1.0D);

        if (!worldObj.checkChunksExist(var4, var6, var8, var5, var7, var9)) {
            return false;
        } else {
            boolean var10 = false;

            for (int var12 = var4; var12 < var5; ++var12) {
                for (int var13 = var6; var13 < var7; ++var13) {
                    for (int var14 = var8; var14 < var9; ++var14) {
                        Block var15 = worldObj.getBlock(var12, var13, var14);

                        if (var15 != null && var15.getMaterial() == Material.water) {
                            inWater = true;
                            double var16 = (float) (var13 + 1) - BlockLiquid.getLiquidHeightPercent(worldObj.getBlockMetadata(var12, var13, var14));

                            if ((double) var7 >= var16) {
                                var10 = true;
                            }
                        } else {
                            inWater = false;
                        }
                    }
                }
            }
            return var10;
        }
    }

    @Override
    public boolean attackEntityFrom(DamageSource source, float damage) {
        Entity entity = source.getEntity();
        if (worldObj.isRemote || isDead) {
            return true;
        }
        setBeenAttacked();
        if (entity instanceof EntityPlayer) {
            if (color >= 0 && color < 16 && !((EntityPlayer) entity).capabilities.isCreativeMode) {
                entityDropItem(new ItemStack(Zabuton.zabuton, 1, color), 0.0F);
            }
            setDead();
        } else {
            health -= damage;
            if (health <= 0.0f) {
                setDead();
            }
        }
        if (isDead && riddenByEntity != null) {
            riddenByEntity.mountEntity(null);
            setRiddenByEntityID(riddenByEntity);
        }
        return true;
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    @Override
    public void setPositionAndRotation2(double px, double py, double pz, float f, float f1, int i) {
        this.setPosition(px, py, pz);
        this.setRotation(f, f1);

        //super.setPositionAndRotation2(px, py, pz, f, f1, i);
//		mod_VZN_zabuton.Debug("ID:%d - %f,  %f, %f", entityId, px, py, pz);
//		mod_VZN_zabuton.Debug("ID:%d - %f,  %f, %f", entityId, posX, posY, posZ);
/*
//        this.setPosition(px, py, pz);
//        this.setRotation(f, f1);
		this.boatPosRotationIncrements = i + 5;
		
		
		this.zabutonX = px;
		this.zabutonY = py;
		this.zabutonZ = pz;
		this.zabutonYaw = (double)f;
		this.zabutonPitch = (double)f1;

//        motionX = velocityX;
//        motionY = velocityY;
//        motionZ = velocityZ;
*/
    }

    @Override
    public void setVelocity(double d, double d1, double d2) {
        velocityX = motionX = d;
        velocityY = motionY = d1;
        velocityZ = motionZ = d2;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        // クライアントへはパケットで送ってたと思われる。dataWatcherに切り替え。
        if (!this.worldObj.isRemote) {
            dataWatcher.updateObject(19, color);
        } else {
            color = dataWatcher.getWatchableObjectByte(19);
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;

        // ボートの判定のコピー
        // ボートは直接サーバーと位置情報を同期させているわけではなく、予測位置計算系に値を渡している。
        // 因みにボートの座標同期間隔は結構長めなので動きが変。


        double var6;
        double var8;
        double var12;
        double var26;
        double var24 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);

        if (this.worldObj.isRemote) {
            // Client
            if (this.boatPosRotationIncrements > 0) {
                var6 = this.posX + (this.zabutonX - this.posX) / (double) this.boatPosRotationIncrements;
                var8 = this.posY + (this.zabutonY - this.posY) / (double) this.boatPosRotationIncrements;
                var26 = this.posZ + (this.zabutonZ - this.posZ) / (double) this.boatPosRotationIncrements;
                var12 = MathHelper.wrapAngleTo180_double(this.zabutonYaw - (double) this.rotationYaw);
                this.rotationYaw = (float) ((double) this.rotationYaw + var12 / (double) this.boatPosRotationIncrements);
                this.rotationPitch = (float) ((double) this.rotationPitch + (this.zabutonPitch - (double) this.rotationPitch) / (double) this.boatPosRotationIncrements);
                --this.boatPosRotationIncrements;
                this.setPosition(var6, var8, var26);
                this.setRotation(this.rotationYaw, this.rotationPitch);
            } else {
                motionY -= 0.08D;
                if (this.onGround) {
                    this.motionX *= 0.5D;
                    this.motionY *= 0.5D;
                    this.motionZ *= 0.5D;
                    setDispensed(false);
                }
                this.moveEntity(this.motionX, this.motionY, this.motionZ);

                this.motionX *= 0.9900000095367432D;
                this.motionY *= 0.949999988079071D;
                this.motionZ *= 0.9900000095367432D;
            }
        } else {
            // Server
            // 落下
            motionY -= 0.08D;

            // 搭乗者によるベクトル操作
            if (this.riddenByEntity != null && this.riddenByEntity instanceof EntityPlayer) {
                this.motionX += this.riddenByEntity.motionX * 0.2D;
                this.motionZ += this.riddenByEntity.motionZ * 0.2D;
            }

            // 最高速度判定
            double maxSpeed = isDispensed() ? 10.0 : 0.35;
            var6 = Math.sqrt(this.motionX * this.motionX + this.motionZ * this.motionZ);
            if (var6 > maxSpeed) {
                var8 = maxSpeed / var6;
                this.motionX *= var8;
                this.motionZ *= var8;
                var6 = maxSpeed;
            }
            if (this.onGround) {
                this.motionX *= 0.5D;
                this.motionY *= 0.5D;
                this.motionZ *= 0.5D;
                setDispensed(false);
                // setVelocityの呼ばれる回数が少なくて変な動きをするので対策
//                this.velocityChanged = true;
            }
            this.moveEntity(this.motionX, this.motionY, this.motionZ);

            this.motionX *= 0.9900000095367432D;
            this.motionY *= 0.949999988079071D;
            this.motionZ *= 0.9900000095367432D;

            // ヘッディング
            this.rotationPitch = 0.0F;
            var8 = this.rotationYaw;
            var26 = this.prevPosX - this.posX;
            var12 = this.prevPosZ - this.posZ;

            if (var26 * var26 + var12 * var12 > 0.001D) {
                var8 = (float) (Math.atan2(var12, var26) * 180.0D / Math.PI);
            }

            double var14 = MathHelper.wrapAngleTo180_double(var8 - (double) this.rotationYaw);
            if (var14 > 20.0D) {
                var14 = 20.0D;
            }
            if (var14 < -20.0D) {
                var14 = -20.0D;
            }

            this.rotationYaw = (float) ((double) this.rotationYaw + var14);
            this.setRotation(this.rotationYaw, this.rotationPitch);

            // 当たり判定
            List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity(this, this.boundingBox.expand(0.17D, 0.0D, 0.17D));
            if (entities != null && !entities.isEmpty()) {
                for (Object o : entities) {
                    Entity var18 = (Entity) o;

                    if (var18 != this.riddenByEntity && var18.canBePushed() && var18 instanceof EntityZabuton) {
                        var18.applyEntityCollision(this);
                    }
                }
            }
        }
        if (this.riddenByEntity != null) {
            if (this.riddenByEntity instanceof EntityMob) {
                // 座ってる間は消滅させない
                setEntityLivingAge((EntityLivingBase) riddenByEntity, 0);
            }
            if (riddenByEntity.isDead) {
                // 着座対象が死んだら無人化
                riddenByEntity = null;
                setRiddenByEntityID(null);
            } else if (inWater) {
                // ぬれた座布団はひゃぁってなる
                riddenByEntity.mountEntity(null);
                setRiddenByEntityID(riddenByEntity);
            }
        }
    }

    public void setEntityLivingAge(EntityLivingBase entity, int entityAge) {
        ObfuscationReflectionHelper.setPrivateValue(EntityLivingBase.class, entity, entityAge, "field_70708_bq", "entityAge");
    }

    @Override
    public void applyEntityCollision(Entity entity) {
        // 吸着判定
        if (worldObj.isRemote) {
            return;
        }
        if (entity == riddenByEntity) {
            return;
        }
        if (entity instanceof EntityLiving && riddenByEntity == null && entity.ridingEntity == null) {
            entity.mountEntity(this);
            setRiddenByEntityID(riddenByEntity);
        }
        super.applyEntityCollision(entity);
    }

    @Override
    public boolean interactFirst(EntityPlayer entityplayer) {
        // ラーイド・オン！
        if (riddenByEntity != null && (riddenByEntity instanceof EntityPlayer) && riddenByEntity != entityplayer) {
            return true;
        }
        if (!worldObj.isRemote) {
            entityplayer.mountEntity(this);
        }
        return true;
    }

    // 射出判定
    public boolean isDispensed() {
        return dataWatcher.getWatchableObjectByte(17) > 0x00;
    }

    public void setDispensed(boolean isDispensed) {
        dataWatcher.updateObject(17, (byte) (isDispensed ? 0x01 : 0x00));
    }

    // クライアント側補正用
    public int getRiddenByEntityID() {
        return dataWatcher.getWatchableObjectInt(18);
    }

    public Entity getRiddenByEntity() {
        return worldObj.getEntityByID(getRiddenByEntityID());
    }

    public void setRiddenByEntityID(Entity entity) {
        dataWatcher.updateObject(18, entity == null ? 0 : entity.getEntityId());
    }

}
