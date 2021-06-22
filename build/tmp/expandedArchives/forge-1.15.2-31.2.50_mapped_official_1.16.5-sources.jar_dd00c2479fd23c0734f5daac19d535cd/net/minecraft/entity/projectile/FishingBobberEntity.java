package net.minecraft.entity.projectile;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FishingBobberEntity extends Entity {
   private static final DataParameter<Integer> DATA_HOOKED_ENTITY = EntityDataManager.defineId(FishingBobberEntity.class, DataSerializers.INT);
   private boolean field_146051_au;
   private int life;
   private final PlayerEntity field_146042_b;
   private int field_146047_aw;
   private int nibble;
   private int timeUntilLured;
   private int timeUntilHooked;
   private float fishAngle;
   public Entity hookedIn;
   private FishingBobberEntity.State currentState = FishingBobberEntity.State.FLYING;
   private final int luck;
   private final int lureSpeed;

   private FishingBobberEntity(World p_i50219_1_, PlayerEntity p_i50219_2_, int p_i50219_3_, int p_i50219_4_) {
      super(EntityType.FISHING_BOBBER, p_i50219_1_);
      this.noCulling = true;
      this.field_146042_b = p_i50219_2_;
      this.field_146042_b.fishing = this;
      this.luck = Math.max(0, p_i50219_3_);
      this.lureSpeed = Math.max(0, p_i50219_4_);
   }

   @OnlyIn(Dist.CLIENT)
   public FishingBobberEntity(World p_i47290_1_, PlayerEntity p_i47290_2_, double p_i47290_3_, double p_i47290_5_, double p_i47290_7_) {
      this(p_i47290_1_, p_i47290_2_, 0, 0);
      this.setPos(p_i47290_3_, p_i47290_5_, p_i47290_7_);
      this.xo = this.getX();
      this.yo = this.getY();
      this.zo = this.getZ();
   }

   public FishingBobberEntity(PlayerEntity p_i50220_1_, World p_i50220_2_, int p_i50220_3_, int p_i50220_4_) {
      this(p_i50220_2_, p_i50220_1_, p_i50220_3_, p_i50220_4_);
      float f = this.field_146042_b.xRot;
      float f1 = this.field_146042_b.yRot;
      float f2 = MathHelper.cos(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f3 = MathHelper.sin(-f1 * ((float)Math.PI / 180F) - (float)Math.PI);
      float f4 = -MathHelper.cos(-f * ((float)Math.PI / 180F));
      float f5 = MathHelper.sin(-f * ((float)Math.PI / 180F));
      double d0 = this.field_146042_b.getX() - (double)f3 * 0.3D;
      double d1 = this.field_146042_b.getEyeY();
      double d2 = this.field_146042_b.getZ() - (double)f2 * 0.3D;
      this.moveTo(d0, d1, d2, f1, f);
      Vec3d vec3d = new Vec3d((double)(-f3), (double)MathHelper.clamp(-(f5 / f4), -5.0F, 5.0F), (double)(-f2));
      double d3 = vec3d.length();
      vec3d = vec3d.multiply(0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D, 0.6D / d3 + 0.5D + this.random.nextGaussian() * 0.0045D);
      this.setDeltaMovement(vec3d);
      this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
      this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)MathHelper.sqrt(getHorizontalDistanceSqr(vec3d))) * (double)(180F / (float)Math.PI));
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   protected void defineSynchedData() {
      this.getEntityData().define(DATA_HOOKED_ENTITY, 0);
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_HOOKED_ENTITY.equals(p_184206_1_)) {
         int i = this.getEntityData().get(DATA_HOOKED_ENTITY);
         this.hookedIn = i > 0 ? this.level.getEntity(i - 1) : null;
      }

      super.onSyncedDataUpdated(p_184206_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = 64.0D;
      return p_70112_1_ < 4096.0D;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
   }

   public void tick() {
      super.tick();
      if (this.field_146042_b == null) {
         this.remove();
      } else if (this.level.isClientSide || !this.func_190625_o()) {
         if (this.field_146051_au) {
            ++this.life;
            if (this.life >= 1200) {
               this.remove();
               return;
            }
         }

         float f = 0.0F;
         BlockPos blockpos = new BlockPos(this);
         IFluidState ifluidstate = this.level.getFluidState(blockpos);
         if (ifluidstate.is(FluidTags.WATER)) {
            f = ifluidstate.getHeight(this.level, blockpos);
         }

         if (this.currentState == FishingBobberEntity.State.FLYING) {
            if (this.hookedIn != null) {
               this.setDeltaMovement(Vec3d.ZERO);
               this.currentState = FishingBobberEntity.State.HOOKED_IN_ENTITY;
               return;
            }

            if (f > 0.0F) {
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.3D, 0.2D, 0.3D));
               this.currentState = FishingBobberEntity.State.BOBBING;
               return;
            }

            if (!this.level.isClientSide) {
               this.checkCollision();
            }

            if (!this.field_146051_au && !this.onGround && !this.horizontalCollision) {
               ++this.field_146047_aw;
            } else {
               this.field_146047_aw = 0;
               this.setDeltaMovement(Vec3d.ZERO);
            }
         } else {
            if (this.currentState == FishingBobberEntity.State.HOOKED_IN_ENTITY) {
               if (this.hookedIn != null) {
                  if (this.hookedIn.removed) {
                     this.hookedIn = null;
                     this.currentState = FishingBobberEntity.State.FLYING;
                  } else {
                     this.setPos(this.hookedIn.getX(), this.hookedIn.getY(0.8D), this.hookedIn.getZ());
                  }
               }

               return;
            }

            if (this.currentState == FishingBobberEntity.State.BOBBING) {
               Vec3d vec3d = this.getDeltaMovement();
               double d0 = this.getY() + vec3d.y - (double)blockpos.getY() - (double)f;
               if (Math.abs(d0) < 0.01D) {
                  d0 += Math.signum(d0) * 0.1D;
               }

               this.setDeltaMovement(vec3d.x * 0.9D, vec3d.y - d0 * (double)this.random.nextFloat() * 0.2D, vec3d.z * 0.9D);
               if (!this.level.isClientSide && f > 0.0F) {
                  this.catchingFish(blockpos);
               }
            }
         }

         if (!ifluidstate.is(FluidTags.WATER)) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.03D, 0.0D));
         }

         this.move(MoverType.SELF, this.getDeltaMovement());
         this.func_190623_q();
         double d1 = 0.92D;
         this.setDeltaMovement(this.getDeltaMovement().scale(0.92D));
         this.reapplyPosition();
      }
   }

   private boolean func_190625_o() {
      ItemStack itemstack = this.field_146042_b.getMainHandItem();
      ItemStack itemstack1 = this.field_146042_b.getOffhandItem();
      boolean flag = itemstack.getItem() instanceof net.minecraft.item.FishingRodItem;
      boolean flag1 = itemstack1.getItem() instanceof net.minecraft.item.FishingRodItem;
      if (!this.field_146042_b.removed && this.field_146042_b.isAlive() && (flag || flag1) && !(this.distanceToSqr(this.field_146042_b) > 1024.0D)) {
         return false;
      } else {
         this.remove();
         return true;
      }
   }

   private void func_190623_q() {
      Vec3d vec3d = this.getDeltaMovement();
      float f = MathHelper.sqrt(getHorizontalDistanceSqr(vec3d));
      this.yRot = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));

      for(this.xRot = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI)); this.xRot - this.xRotO < -180.0F; this.xRotO -= 360.0F) {
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
   }

   private void checkCollision() {
      RayTraceResult raytraceresult = ProjectileHelper.func_221267_a(this, this.getBoundingBox().expandTowards(this.getDeltaMovement()).inflate(1.0D), (p_213856_1_) -> {
         return !p_213856_1_.isSpectator() && (p_213856_1_.isPickable() || p_213856_1_ instanceof ItemEntity) && (p_213856_1_ != this.field_146042_b || this.field_146047_aw >= 5);
      }, RayTraceContext.BlockMode.COLLIDER, true);
      if (raytraceresult.getType() != RayTraceResult.Type.MISS) {
         if (raytraceresult.getType() == RayTraceResult.Type.ENTITY) {
            this.hookedIn = ((EntityRayTraceResult)raytraceresult).getEntity();
            this.setHookedEntity();
         } else {
            this.field_146051_au = true;
         }
      }

   }

   private void setHookedEntity() {
      this.getEntityData().set(DATA_HOOKED_ENTITY, this.hookedIn.getId() + 1);
   }

   private void catchingFish(BlockPos p_190621_1_) {
      ServerWorld serverworld = (ServerWorld)this.level;
      int i = 1;
      BlockPos blockpos = p_190621_1_.above();
      if (this.random.nextFloat() < 0.25F && this.level.isRainingAt(blockpos)) {
         ++i;
      }

      if (this.random.nextFloat() < 0.5F && !this.level.canSeeSky(blockpos)) {
         --i;
      }

      if (this.nibble > 0) {
         --this.nibble;
         if (this.nibble <= 0) {
            this.timeUntilLured = 0;
            this.timeUntilHooked = 0;
         } else {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0D, -0.2D * (double)this.random.nextFloat() * (double)this.random.nextFloat(), 0.0D));
         }
      } else if (this.timeUntilHooked > 0) {
         this.timeUntilHooked -= i;
         if (this.timeUntilHooked > 0) {
            this.fishAngle = (float)((double)this.fishAngle + this.random.nextGaussian() * 4.0D);
            float f = this.fishAngle * ((float)Math.PI / 180F);
            float f1 = MathHelper.sin(f);
            float f2 = MathHelper.cos(f);
            double d0 = this.getX() + (double)(f1 * (float)this.timeUntilHooked * 0.1F);
            double d1 = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
            double d2 = this.getZ() + (double)(f2 * (float)this.timeUntilHooked * 0.1F);
            Block block = serverworld.getBlockState(new BlockPos(d0, d1 - 1.0D, d2)).getBlock();
            if (serverworld.getBlockState(new BlockPos((int)d0, (int)d1 - 1, (int)d2)).getMaterial() == net.minecraft.block.material.Material.WATER) {
               if (this.random.nextFloat() < 0.15F) {
                  serverworld.sendParticles(ParticleTypes.BUBBLE, d0, d1 - (double)0.1F, d2, 1, (double)f1, 0.1D, (double)f2, 0.0D);
               }

               float f3 = f1 * 0.04F;
               float f4 = f2 * 0.04F;
               serverworld.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)f4, 0.01D, (double)(-f3), 1.0D);
               serverworld.sendParticles(ParticleTypes.FISHING, d0, d1, d2, 0, (double)(-f4), 0.01D, (double)f3, 1.0D);
            }
         } else {
            Vec3d vec3d = this.getDeltaMovement();
            this.setDeltaMovement(vec3d.x, (double)(-0.4F * MathHelper.nextFloat(this.random, 0.6F, 1.0F)), vec3d.z);
            this.playSound(SoundEvents.FISHING_BOBBER_SPLASH, 0.25F, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            double d3 = this.getY() + 0.5D;
            serverworld.sendParticles(ParticleTypes.BUBBLE, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
            serverworld.sendParticles(ParticleTypes.FISHING, this.getX(), d3, this.getZ(), (int)(1.0F + this.getBbWidth() * 20.0F), (double)this.getBbWidth(), 0.0D, (double)this.getBbWidth(), (double)0.2F);
            this.nibble = MathHelper.nextInt(this.random, 20, 40);
         }
      } else if (this.timeUntilLured > 0) {
         this.timeUntilLured -= i;
         float f5 = 0.15F;
         if (this.timeUntilLured < 20) {
            f5 = (float)((double)f5 + (double)(20 - this.timeUntilLured) * 0.05D);
         } else if (this.timeUntilLured < 40) {
            f5 = (float)((double)f5 + (double)(40 - this.timeUntilLured) * 0.02D);
         } else if (this.timeUntilLured < 60) {
            f5 = (float)((double)f5 + (double)(60 - this.timeUntilLured) * 0.01D);
         }

         if (this.random.nextFloat() < f5) {
            float f6 = MathHelper.nextFloat(this.random, 0.0F, 360.0F) * ((float)Math.PI / 180F);
            float f7 = MathHelper.nextFloat(this.random, 25.0F, 60.0F);
            double d4 = this.getX() + (double)(MathHelper.sin(f6) * f7 * 0.1F);
            double d5 = (double)((float)MathHelper.floor(this.getY()) + 1.0F);
            double d6 = this.getZ() + (double)(MathHelper.cos(f6) * f7 * 0.1F);
            Block block1 = serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getBlock();
            if (serverworld.getBlockState(new BlockPos(d4, d5 - 1.0D, d6)).getMaterial() == net.minecraft.block.material.Material.WATER) {
               serverworld.sendParticles(ParticleTypes.SPLASH, d4, d5, d6, 2 + this.random.nextInt(2), (double)0.1F, 0.0D, (double)0.1F, 0.0D);
            }
         }

         if (this.timeUntilLured <= 0) {
            this.fishAngle = MathHelper.nextFloat(this.random, 0.0F, 360.0F);
            this.timeUntilHooked = MathHelper.nextInt(this.random, 20, 80);
         }
      } else {
         this.timeUntilLured = MathHelper.nextInt(this.random, 100, 600);
         this.timeUntilLured -= this.lureSpeed * 20 * 5;
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   public int retrieve(ItemStack p_146034_1_) {
      if (!this.level.isClientSide && this.field_146042_b != null) {
         int i = 0;
         net.minecraftforge.event.entity.player.ItemFishedEvent event = null;
         if (this.hookedIn != null) {
            this.bringInHookedEntity();
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.field_146042_b, p_146034_1_, this, Collections.emptyList());
            this.level.broadcastEntityEvent(this, (byte)31);
            i = this.hookedIn instanceof ItemEntity ? 3 : 5;
         } else if (this.nibble > 0) {
            LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withParameter(LootParameters.field_216286_f, new BlockPos(this)).withParameter(LootParameters.TOOL, p_146034_1_).withRandom(this.random).withLuck((float)this.luck + this.field_146042_b.getLuck());
            lootcontext$builder.withParameter(LootParameters.KILLER_ENTITY, this.field_146042_b).withParameter(LootParameters.THIS_ENTITY, this);
            LootTable loottable = this.level.getServer().getLootTables().get(LootTables.FISHING);
            List<ItemStack> list = loottable.getRandomItems(lootcontext$builder.create(LootParameterSets.FISHING));
            event = new net.minecraftforge.event.entity.player.ItemFishedEvent(list, this.field_146051_au ? 2 : 1, this);
            net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
               this.remove();
               return event.getRodDamage();
            }
            CriteriaTriggers.FISHING_ROD_HOOKED.trigger((ServerPlayerEntity)this.field_146042_b, p_146034_1_, this, list);

            for(ItemStack itemstack : list) {
               ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY(), this.getZ(), itemstack);
               double d0 = this.field_146042_b.getX() - this.getX();
               double d1 = this.field_146042_b.getY() - this.getY();
               double d2 = this.field_146042_b.getZ() - this.getZ();
               double d3 = 0.1D;
               itementity.setDeltaMovement(d0 * 0.1D, d1 * 0.1D + Math.sqrt(Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2)) * 0.08D, d2 * 0.1D);
               this.level.addFreshEntity(itementity);
               this.field_146042_b.level.addFreshEntity(new ExperienceOrbEntity(this.field_146042_b.level, this.field_146042_b.getX(), this.field_146042_b.getY() + 0.5D, this.field_146042_b.getZ() + 0.5D, this.random.nextInt(6) + 1));
               if (itemstack.getItem().is(ItemTags.FISHES)) {
                  this.field_146042_b.awardStat(Stats.FISH_CAUGHT, 1);
               }
            }

            i = 1;
         }

         if (this.field_146051_au) {
            i = 2;
         }

         this.remove();
         return event == null ? i : event.getRodDamage();
      } else {
         return 0;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 31 && this.level.isClientSide && this.hookedIn instanceof PlayerEntity && ((PlayerEntity)this.hookedIn).isLocalPlayer()) {
         this.bringInHookedEntity();
      }

      super.handleEntityEvent(p_70103_1_);
   }

   protected void bringInHookedEntity() {
      if (this.field_146042_b != null) {
         Vec3d vec3d = (new Vec3d(this.field_146042_b.getX() - this.getX(), this.field_146042_b.getY() - this.getY(), this.field_146042_b.getZ() - this.getZ())).scale(0.1D);
         this.hookedIn.setDeltaMovement(this.hookedIn.getDeltaMovement().add(vec3d));
      }
   }

   protected boolean isMovementNoisy() {
      return false;
   }

   @Override
   public void remove(boolean keepData) {
      super.remove(keepData);
      if (this.field_146042_b != null) {
         this.field_146042_b.fishing = null;
      }

   }

   @Nullable
   public PlayerEntity func_190619_l() {
      return this.field_146042_b;
   }

   public boolean canChangeDimensions() {
      return false;
   }

   public IPacket<?> getAddEntityPacket() {
      Entity entity = this.func_190619_l();
      return new SSpawnObjectPacket(this, entity == null ? this.getId() : entity.getId());
   }

   static enum State {
      FLYING,
      HOOKED_IN_ENTITY,
      BOBBING;
   }
}
