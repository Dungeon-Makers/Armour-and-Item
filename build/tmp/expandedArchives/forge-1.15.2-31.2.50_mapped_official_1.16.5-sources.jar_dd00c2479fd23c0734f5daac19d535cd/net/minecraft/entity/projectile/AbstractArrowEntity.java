package net.minecraft.entity.projectile;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Pose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractArrowEntity extends Entity implements IProjectile {
   private static final DataParameter<Byte> ID_FLAGS = EntityDataManager.defineId(AbstractArrowEntity.class, DataSerializers.BYTE);
   protected static final DataParameter<Optional<UUID>> field_212362_a = EntityDataManager.defineId(AbstractArrowEntity.class, DataSerializers.OPTIONAL_UUID);
   private static final DataParameter<Byte> PIERCE_LEVEL = EntityDataManager.defineId(AbstractArrowEntity.class, DataSerializers.BYTE);
   @Nullable
   private BlockState lastState;
   protected boolean inGround;
   protected int inGroundTime;
   public AbstractArrowEntity.PickupStatus pickup = AbstractArrowEntity.PickupStatus.DISALLOWED;
   public int shakeTime;
   public UUID field_70250_c;
   private int life;
   private int field_70257_an;
   private double baseDamage = 2.0D;
   private int knockback;
   private SoundEvent soundEvent = this.getDefaultHitGroundSoundEvent();
   private IntOpenHashSet piercingIgnoreEntityIds;
   private List<Entity> piercedAndKilledEntities;

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48546_1_, World p_i48546_2_) {
      super(p_i48546_1_, p_i48546_2_);
   }

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48547_1_, double p_i48547_2_, double p_i48547_4_, double p_i48547_6_, World p_i48547_8_) {
      this(p_i48547_1_, p_i48547_8_);
      this.setPos(p_i48547_2_, p_i48547_4_, p_i48547_6_);
   }

   protected AbstractArrowEntity(EntityType<? extends AbstractArrowEntity> p_i48548_1_, LivingEntity p_i48548_2_, World p_i48548_3_) {
      this(p_i48548_1_, p_i48548_2_.getX(), p_i48548_2_.getEyeY() - (double)0.1F, p_i48548_2_.getZ(), p_i48548_3_);
      this.setOwner(p_i48548_2_);
      if (p_i48548_2_ instanceof PlayerEntity) {
         this.pickup = AbstractArrowEntity.PickupStatus.ALLOWED;
      }

   }

   public void setSoundEvent(SoundEvent p_213869_1_) {
      this.soundEvent = p_213869_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize() * 10.0D;
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * getViewScale();
      return p_70112_1_ < d0 * d0;
   }

   protected void defineSynchedData() {
      this.entityData.define(ID_FLAGS, (byte)0);
      this.entityData.define(field_212362_a, Optional.empty());
      this.entityData.define(PIERCE_LEVEL, (byte)0);
   }

   public void func_184547_a(Entity p_184547_1_, float p_184547_2_, float p_184547_3_, float p_184547_4_, float p_184547_5_, float p_184547_6_) {
      float f = -MathHelper.sin(p_184547_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184547_2_ * ((float)Math.PI / 180F));
      float f1 = -MathHelper.sin(p_184547_2_ * ((float)Math.PI / 180F));
      float f2 = MathHelper.cos(p_184547_3_ * ((float)Math.PI / 180F)) * MathHelper.cos(p_184547_2_ * ((float)Math.PI / 180F));
      this.shoot((double)f, (double)f1, (double)f2, p_184547_5_, p_184547_6_);
      this.setDeltaMovement(this.getDeltaMovement().add(p_184547_1_.getDeltaMovement().x, p_184547_1_.onGround ? 0.0D : p_184547_1_.getDeltaMovement().y, p_184547_1_.getDeltaMovement().z));
   }

   public void shoot(double p_70186_1_, double p_70186_3_, double p_70186_5_, float p_70186_7_, float p_70186_8_) {
      Vec3d vec3d = (new Vec3d(p_70186_1_, p_70186_3_, p_70186_5_)).normalize().add(this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_, this.random.nextGaussian() * (double)0.0075F * (double)p_70186_8_).scale((double)p_70186_7_);
      this.setDeltaMovement(vec3d);
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
      this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
      this.life = 0;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPos(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRot(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
         this.xRot = (float)(MathHelper.atan2(p_70016_3_, (double)f) * (double)(180F / (float)Math.PI));
         this.yRot = (float)(MathHelper.atan2(p_70016_1_, p_70016_5_) * (double)(180F / (float)Math.PI));
         this.xRotO = this.xRot;
         this.yRotO = this.yRot;
         this.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
         this.life = 0;
      }

   }

   public void tick() {
      super.tick();
      boolean flag = this.isNoPhysics();
      Vec3d vec3d = this.getDeltaMovement();
      if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
         float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
         this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
         this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
      }

      BlockPos blockpos = new BlockPos(this);
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.isAir(this.level, blockpos) && !flag) {
         VoxelShape voxelshape = blockstate.getCollisionShape(this.level, blockpos);
         if (!voxelshape.isEmpty()) {
            Vec3d vec3d1 = this.position();

            for(AxisAlignedBB axisalignedbb : voxelshape.toAabbs()) {
               if (axisalignedbb.move(blockpos).contains(vec3d1)) {
                  this.inGround = true;
                  break;
               }
            }
         }
      }

      if (this.shakeTime > 0) {
         --this.shakeTime;
      }

      if (this.isInWaterOrRain()) {
         this.clearFire();
      }

      if (this.inGround && !flag) {
         if (this.lastState != blockstate && this.level.noCollision(this.getBoundingBox().inflate(0.06D))) {
            this.inGround = false;
            this.setDeltaMovement(vec3d.multiply((double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F), (double)(this.random.nextFloat() * 0.2F)));
            this.life = 0;
            this.field_70257_an = 0;
         } else if (!this.level.isClientSide) {
            this.tickDespawn();
         }

         ++this.inGroundTime;
      } else {
         this.inGroundTime = 0;
         ++this.field_70257_an;
         Vec3d vec3d2 = this.position();
         Vec3d vec3d3 = vec3d2.add(vec3d);
         RayTraceResult raytraceresult = this.level.clip(new RayTraceContext(vec3d2, vec3d3, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
         if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
            vec3d3 = raytraceresult.getLocation();
         }

         while(!this.removed) {
            EntityRayTraceResult entityraytraceresult = this.findHitEntity(vec3d2, vec3d3);
            if (entityraytraceresult != null) {
               raytraceresult = entityraytraceresult;
            }

            if (raytraceresult != null && raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
               Entity entity = ((EntityRayTraceResult)raytraceresult).getEntity();
               Entity entity1 = this.func_212360_k();
               if (entity instanceof PlayerEntity && entity1 instanceof PlayerEntity && !((PlayerEntity)entity1).canHarmPlayer((PlayerEntity)entity)) {
                  raytraceresult = null;
                  entityraytraceresult = null;
               }
            }

            if (raytraceresult != null && raytraceresult.getType() != RayTraceResult.Type.MISS && !flag && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, raytraceresult)) {
               this.func_184549_a(raytraceresult);
               this.hasImpulse = true;
            }

            if (entityraytraceresult == null || this.getPierceLevel() <= 0) {
               break;
            }

            raytraceresult = null;
         }

         vec3d = this.getDeltaMovement();
         double d3 = vec3d.x;
         double d4 = vec3d.y;
         double d0 = vec3d.z;
         if (this.isCritArrow()) {
            for(int i = 0; i < 4; ++i) {
               this.level.addParticle(ParticleTypes.CRIT, this.getX() + d3 * (double)i / 4.0D, this.getY() + d4 * (double)i / 4.0D, this.getZ() + d0 * (double)i / 4.0D, -d3, -d4 + 0.2D, -d0);
            }
         }

         double d5 = this.getX() + d3;
         double d1 = this.getY() + d4;
         double d2 = this.getZ() + d0;
         float f1 = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
         if (flag) {
            this.yRot = (float)(MathHelper.atan2(-d3, -d0) * (double)(180F / (float)Math.PI));
         } else {
            this.yRot = (float)(MathHelper.atan2(d3, d0) * (double)(180F / (float)Math.PI));
         }

         for(this.xRot = (float)(MathHelper.atan2(d4, (double)f1) * (double)(180F / (float)Math.PI)); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
            ;
         }

         while(this.xRot - this.xRotO >= 180.0F) {
            this.xRotO += 360.0F;
         }

         while(this.yRot - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
         }

         while(this.yRot - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
         }

         this.xRot = MathHelper.lerp(0.2F, this.xRotO, this.xRot);
         this.yRot = MathHelper.lerp(0.2F, this.yRotO, this.yRot);
         float f2 = 0.99F;
         float f3 = 0.05F;
         if (this.isInWater()) {
            for(int j = 0; j < 4; ++j) {
               float f4 = 0.25F;
               this.level.addParticle(ParticleTypes.BUBBLE, d5 - d3 * 0.25D, d1 - d4 * 0.25D, d2 - d0 * 0.25D, d3, d4, d0);
            }

            f2 = this.getWaterInertia();
         }

         this.setDeltaMovement(vec3d.scale((double)f2));
         if (!this.isNoGravity() && !flag) {
            Vec3d vec3d4 = this.getDeltaMovement();
            this.setDeltaMovement(vec3d4.x, vec3d4.y - (double)0.05F, vec3d4.z);
         }

         this.setPos(d5, d1, d2);
         this.checkInsideBlocks();
      }
   }

   protected void tickDespawn() {
      ++this.life;
      if (this.life >= 1200) {
         this.remove();
      }

   }

   protected void func_184549_a(RayTraceResult p_184549_1_) {
      RayTraceResult.Type raytraceresult$type = p_184549_1_.getType();
      if (raytraceresult$type == RayTraceResult.Type.ENTITY) {
         this.onHitEntity((EntityRayTraceResult)p_184549_1_);
      } else if (raytraceresult$type == RayTraceResult.Type.BLOCK) {
         BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_184549_1_;
         BlockState blockstate = this.level.getBlockState(blockraytraceresult.getBlockPos());
         this.lastState = blockstate;
         Vec3d vec3d = blockraytraceresult.getLocation().subtract(this.getX(), this.getY(), this.getZ());
         this.setDeltaMovement(vec3d);
         Vec3d vec3d1 = vec3d.normalize().scale((double)0.05F);
         this.setPosRaw(this.getX() - vec3d1.x, this.getY() - vec3d1.y, this.getZ() - vec3d1.z);
         this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         this.inGround = true;
         this.shakeTime = 7;
         this.setCritArrow(false);
         this.setPierceLevel((byte)0);
         this.setSoundEvent(SoundEvents.ARROW_HIT);
         this.setShotFromCrossbow(false);
         this.resetPiercedEntities();
         blockstate.onProjectileHit(this.level, blockstate, blockraytraceresult, this);
      }

   }

   private void resetPiercedEntities() {
      if (this.piercedAndKilledEntities != null) {
         this.piercedAndKilledEntities.clear();
      }

      if (this.piercingIgnoreEntityIds != null) {
         this.piercingIgnoreEntityIds.clear();
      }

   }

   protected void onHitEntity(EntityRayTraceResult p_213868_1_) {
      Entity entity = p_213868_1_.getEntity();
      float f = (float)this.getDeltaMovement().length();
      int i = MathHelper.ceil(Math.max((double)f * this.baseDamage, 0.0D));
      if (this.getPierceLevel() > 0) {
         if (this.piercingIgnoreEntityIds == null) {
            this.piercingIgnoreEntityIds = new IntOpenHashSet(5);
         }

         if (this.piercedAndKilledEntities == null) {
            this.piercedAndKilledEntities = Lists.newArrayListWithCapacity(5);
         }

         if (this.piercingIgnoreEntityIds.size() >= this.getPierceLevel() + 1) {
            this.remove();
            return;
         }

         this.piercingIgnoreEntityIds.add(entity.getId());
      }

      if (this.isCritArrow()) {
         i += this.random.nextInt(i / 2 + 2);
      }

      Entity entity1 = this.func_212360_k();
      DamageSource damagesource;
      if (entity1 == null) {
         damagesource = DamageSource.arrow(this, this);
      } else {
         damagesource = DamageSource.arrow(this, entity1);
         if (entity1 instanceof LivingEntity) {
            ((LivingEntity)entity1).setLastHurtMob(entity);
         }
      }

      boolean flag = entity.getType() == EntityType.ENDERMAN;
      int j = entity.getRemainingFireTicks();
      if (this.isOnFire() && !flag) {
         entity.setSecondsOnFire(5);
      }

      if (entity.hurt(damagesource, (float)i)) {
         if (flag) {
            return;
         }

         if (entity instanceof LivingEntity) {
            LivingEntity livingentity = (LivingEntity)entity;
            if (!this.level.isClientSide && this.getPierceLevel() <= 0) {
               livingentity.setArrowCount(livingentity.getArrowCount() + 1);
            }

            if (this.knockback > 0) {
               Vec3d vec3d = this.getDeltaMovement().multiply(1.0D, 0.0D, 1.0D).normalize().scale((double)this.knockback * 0.6D);
               if (vec3d.lengthSqr() > 0.0D) {
                  livingentity.push(vec3d.x, 0.1D, vec3d.z);
               }
            }

            if (!this.level.isClientSide && entity1 instanceof LivingEntity) {
               EnchantmentHelper.doPostHurtEffects(livingentity, entity1);
               EnchantmentHelper.doPostDamageEffects((LivingEntity)entity1, livingentity);
            }

            this.doPostHurtEffects(livingentity);
            if (entity1 != null && livingentity != entity1 && livingentity instanceof PlayerEntity && entity1 instanceof ServerPlayerEntity) {
               ((ServerPlayerEntity)entity1).connection.send(new SChangeGameStatePacket(6, 0.0F));
            }

            if (!entity.isAlive() && this.piercedAndKilledEntities != null) {
               this.piercedAndKilledEntities.add(livingentity);
            }

            if (!this.level.isClientSide && entity1 instanceof ServerPlayerEntity) {
               ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)entity1;
               if (this.piercedAndKilledEntities != null && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, this.piercedAndKilledEntities, this.piercedAndKilledEntities.size());
               } else if (!entity.isAlive() && this.shotFromCrossbow()) {
                  CriteriaTriggers.KILLED_BY_CROSSBOW.func_215105_a(serverplayerentity, Arrays.asList(entity), 0);
               }
            }
         }

         this.playSound(this.soundEvent, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
         if (this.getPierceLevel() <= 0) {
            this.remove();
         }
      } else {
         entity.func_223308_g(j);
         this.setDeltaMovement(this.getDeltaMovement().scale(-0.1D));
         this.yRot += 180.0F;
         this.yRotO += 180.0F;
         this.field_70257_an = 0;
         if (!this.level.isClientSide && this.getDeltaMovement().lengthSqr() < 1.0E-7D) {
            if (this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED) {
               this.spawnAtLocation(this.getPickupItem(), 0.1F);
            }

            this.remove();
         }
      }

   }

   protected SoundEvent getDefaultHitGroundSoundEvent() {
      return SoundEvents.ARROW_HIT;
   }

   protected final SoundEvent getHitGroundSoundEvent() {
      return this.soundEvent;
   }

   protected void doPostHurtEffects(LivingEntity p_184548_1_) {
   }

   @Nullable
   protected EntityRayTraceResult findHitEntity(Vec3d p_213866_1_, Vec3d p_213866_2_) {
      return ProjectileHelper.func_221271_a(this.level, this, p_213866_1_, p_213866_2_, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_213871_1_) -> {
         return !p_213871_1_.isSpectator() && p_213871_1_.isAlive() && p_213871_1_.isPickable() && (p_213871_1_ != this.func_212360_k() || this.field_70257_an >= 5) && (this.piercingIgnoreEntityIds == null || !this.piercingIgnoreEntityIds.contains(p_213871_1_.getId()));
      });
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      p_213281_1_.putShort("life", (short)this.life);
      if (this.lastState != null) {
         p_213281_1_.put("inBlockState", NBTUtil.writeBlockState(this.lastState));
      }

      p_213281_1_.putByte("shake", (byte)this.shakeTime);
      p_213281_1_.putBoolean("inGround", this.inGround);
      p_213281_1_.putByte("pickup", (byte)this.pickup.ordinal());
      p_213281_1_.putDouble("damage", this.baseDamage);
      p_213281_1_.putBoolean("crit", this.isCritArrow());
      p_213281_1_.putByte("PierceLevel", this.getPierceLevel());
      if (this.field_70250_c != null) {
         p_213281_1_.putUUID("OwnerUUID", this.field_70250_c);
      }

      p_213281_1_.putString("SoundEvent", Registry.SOUND_EVENT.getKey(this.soundEvent).toString());
      p_213281_1_.putBoolean("ShotFromCrossbow", this.shotFromCrossbow());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      this.life = p_70037_1_.getShort("life");
      if (p_70037_1_.contains("inBlockState", 10)) {
         this.lastState = NBTUtil.readBlockState(p_70037_1_.getCompound("inBlockState"));
      }

      this.shakeTime = p_70037_1_.getByte("shake") & 255;
      this.inGround = p_70037_1_.getBoolean("inGround");
      if (p_70037_1_.contains("damage", 99)) {
         this.baseDamage = p_70037_1_.getDouble("damage");
      }

      if (p_70037_1_.contains("pickup", 99)) {
         this.pickup = AbstractArrowEntity.PickupStatus.byOrdinal(p_70037_1_.getByte("pickup"));
      } else if (p_70037_1_.contains("player", 99)) {
         this.pickup = p_70037_1_.getBoolean("player") ? AbstractArrowEntity.PickupStatus.ALLOWED : AbstractArrowEntity.PickupStatus.DISALLOWED;
      }

      this.setCritArrow(p_70037_1_.getBoolean("crit"));
      this.setPierceLevel(p_70037_1_.getByte("PierceLevel"));
      if (p_70037_1_.hasUUID("OwnerUUID")) {
         this.field_70250_c = p_70037_1_.getUUID("OwnerUUID");
      }

      if (p_70037_1_.contains("SoundEvent", 8)) {
         this.soundEvent = Registry.SOUND_EVENT.func_218349_b(new ResourceLocation(p_70037_1_.getString("SoundEvent"))).orElse(this.getDefaultHitGroundSoundEvent());
      }

      this.setShotFromCrossbow(p_70037_1_.getBoolean("ShotFromCrossbow"));
   }

   public void setOwner(@Nullable Entity p_212361_1_) {
      this.field_70250_c = p_212361_1_ == null ? null : p_212361_1_.getUUID();
      if (p_212361_1_ instanceof PlayerEntity) {
         this.pickup = ((PlayerEntity)p_212361_1_).abilities.instabuild ? AbstractArrowEntity.PickupStatus.CREATIVE_ONLY : AbstractArrowEntity.PickupStatus.ALLOWED;
      }

   }

   @Nullable
   public Entity func_212360_k() {
      return this.field_70250_c != null && this.level instanceof ServerWorld ? ((ServerWorld)this.level).getEntity(this.field_70250_c) : null;
   }

   public void playerTouch(PlayerEntity p_70100_1_) {
      if (!this.level.isClientSide && (this.inGround || this.isNoPhysics()) && this.shakeTime <= 0) {
         boolean flag = this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED || this.pickup == AbstractArrowEntity.PickupStatus.CREATIVE_ONLY && p_70100_1_.abilities.instabuild || this.isNoPhysics() && this.func_212360_k().getUUID() == p_70100_1_.getUUID();
         if (this.pickup == AbstractArrowEntity.PickupStatus.ALLOWED && !p_70100_1_.inventory.add(this.getPickupItem())) {
            flag = false;
         }

         if (flag) {
            p_70100_1_.take(this, 1);
            this.remove();
         }

      }
   }

   protected abstract ItemStack getPickupItem();

   protected boolean isMovementNoisy() {
      return false;
   }

   public void setBaseDamage(double p_70239_1_) {
      this.baseDamage = p_70239_1_;
   }

   public double getBaseDamage() {
      return this.baseDamage;
   }

   public void setKnockback(int p_70240_1_) {
      this.knockback = p_70240_1_;
   }

   public boolean isAttackable() {
      return false;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return 0.0F;
   }

   public void setCritArrow(boolean p_70243_1_) {
      this.setFlag(1, p_70243_1_);
   }

   public void setPierceLevel(byte p_213872_1_) {
      this.entityData.set(PIERCE_LEVEL, p_213872_1_);
   }

   private void setFlag(int p_203049_1_, boolean p_203049_2_) {
      byte b0 = this.entityData.get(ID_FLAGS);
      if (p_203049_2_) {
         this.entityData.set(ID_FLAGS, (byte)(b0 | p_203049_1_));
      } else {
         this.entityData.set(ID_FLAGS, (byte)(b0 & ~p_203049_1_));
      }

   }

   public boolean isCritArrow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 1) != 0;
   }

   public boolean shotFromCrossbow() {
      byte b0 = this.entityData.get(ID_FLAGS);
      return (b0 & 4) != 0;
   }

   public byte getPierceLevel() {
      return this.entityData.get(PIERCE_LEVEL);
   }

   public void setEnchantmentEffectsFromEntity(LivingEntity p_190547_1_, float p_190547_2_) {
      int i = EnchantmentHelper.getEnchantmentLevel(Enchantments.POWER_ARROWS, p_190547_1_);
      int j = EnchantmentHelper.getEnchantmentLevel(Enchantments.PUNCH_ARROWS, p_190547_1_);
      this.setBaseDamage((double)(p_190547_2_ * 2.0F) + this.random.nextGaussian() * 0.25D + (double)((float)this.level.getDifficulty().getId() * 0.11F));
      if (i > 0) {
         this.setBaseDamage(this.getBaseDamage() + (double)i * 0.5D + 0.5D);
      }

      if (j > 0) {
         this.setKnockback(j);
      }

      if (EnchantmentHelper.getEnchantmentLevel(Enchantments.FLAMING_ARROWS, p_190547_1_) > 0) {
         this.setSecondsOnFire(100);
      }

   }

   protected float getWaterInertia() {
      return 0.6F;
   }

   public void setNoPhysics(boolean p_203045_1_) {
      this.noPhysics = p_203045_1_;
      this.setFlag(2, p_203045_1_);
   }

   public boolean isNoPhysics() {
      if (!this.level.isClientSide) {
         return this.noPhysics;
      } else {
         return (this.entityData.get(ID_FLAGS) & 2) != 0;
      }
   }

   public void setShotFromCrossbow(boolean p_213865_1_) {
      this.setFlag(4, p_213865_1_);
   }

   public IPacket<?> getAddEntityPacket() {
      Entity entity = this.func_212360_k();
      return new SSpawnObjectPacket(this, entity == null ? 0 : entity.getId());
   }

   public static enum PickupStatus {
      DISALLOWED,
      ALLOWED,
      CREATIVE_ONLY;

      public static AbstractArrowEntity.PickupStatus byOrdinal(int p_188795_0_) {
         if (p_188795_0_ < 0 || p_188795_0_ > values().length) {
            p_188795_0_ = 0;
         }

         return values()[p_188795_0_];
      }
   }
}
