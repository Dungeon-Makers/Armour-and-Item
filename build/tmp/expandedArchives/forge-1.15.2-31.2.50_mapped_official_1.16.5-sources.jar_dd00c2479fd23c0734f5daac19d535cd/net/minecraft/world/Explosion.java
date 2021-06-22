package net.minecraft.world;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class Explosion {
   private final boolean fire;
   private final Explosion.Mode blockInteraction;
   private final Random random = new Random();
   private final World level;
   private final double x;
   private final double y;
   private final double z;
   @Nullable
   private final Entity source;
   private final float radius;
   private DamageSource damageSource;
   private final List<BlockPos> toBlow = Lists.newArrayList();
   private final Map<PlayerEntity, Vec3d> hitPlayers = Maps.newHashMap();
   private final Vec3d position;

   @OnlyIn(Dist.CLIENT)
   public Explosion(World p_i45752_1_, @Nullable Entity p_i45752_2_, double p_i45752_3_, double p_i45752_5_, double p_i45752_7_, float p_i45752_9_, List<BlockPos> p_i45752_10_) {
      this(p_i45752_1_, p_i45752_2_, p_i45752_3_, p_i45752_5_, p_i45752_7_, p_i45752_9_, false, Explosion.Mode.DESTROY, p_i45752_10_);
   }

   @OnlyIn(Dist.CLIENT)
   public Explosion(World p_i50006_1_, @Nullable Entity p_i50006_2_, double p_i50006_3_, double p_i50006_5_, double p_i50006_7_, float p_i50006_9_, boolean p_i50006_10_, Explosion.Mode p_i50006_11_, List<BlockPos> p_i50006_12_) {
      this(p_i50006_1_, p_i50006_2_, p_i50006_3_, p_i50006_5_, p_i50006_7_, p_i50006_9_, p_i50006_10_, p_i50006_11_);
      this.toBlow.addAll(p_i50006_12_);
   }

   public Explosion(World p_i50007_1_, @Nullable Entity p_i50007_2_, double p_i50007_3_, double p_i50007_5_, double p_i50007_7_, float p_i50007_9_, boolean p_i50007_10_, Explosion.Mode p_i50007_11_) {
      this.level = p_i50007_1_;
      this.source = p_i50007_2_;
      this.radius = p_i50007_9_;
      this.x = p_i50007_3_;
      this.y = p_i50007_5_;
      this.z = p_i50007_7_;
      this.fire = p_i50007_10_;
      this.blockInteraction = p_i50007_11_;
      this.damageSource = DamageSource.explosion(this);
      this.position = new Vec3d(this.x, this.y, this.z);
   }

   public static float getSeenPercent(Vec3d p_222259_0_, Entity p_222259_1_) {
      AxisAlignedBB axisalignedbb = p_222259_1_.getBoundingBox();
      double d0 = 1.0D / ((axisalignedbb.maxX - axisalignedbb.minX) * 2.0D + 1.0D);
      double d1 = 1.0D / ((axisalignedbb.maxY - axisalignedbb.minY) * 2.0D + 1.0D);
      double d2 = 1.0D / ((axisalignedbb.maxZ - axisalignedbb.minZ) * 2.0D + 1.0D);
      double d3 = (1.0D - Math.floor(1.0D / d0) * d0) / 2.0D;
      double d4 = (1.0D - Math.floor(1.0D / d2) * d2) / 2.0D;
      if (!(d0 < 0.0D) && !(d1 < 0.0D) && !(d2 < 0.0D)) {
         int i = 0;
         int j = 0;

         for(float f = 0.0F; f <= 1.0F; f = (float)((double)f + d0)) {
            for(float f1 = 0.0F; f1 <= 1.0F; f1 = (float)((double)f1 + d1)) {
               for(float f2 = 0.0F; f2 <= 1.0F; f2 = (float)((double)f2 + d2)) {
                  double d5 = MathHelper.lerp((double)f, axisalignedbb.minX, axisalignedbb.maxX);
                  double d6 = MathHelper.lerp((double)f1, axisalignedbb.minY, axisalignedbb.maxY);
                  double d7 = MathHelper.lerp((double)f2, axisalignedbb.minZ, axisalignedbb.maxZ);
                  Vec3d vec3d = new Vec3d(d5 + d3, d6, d7 + d4);
                  if (p_222259_1_.level.clip(new RayTraceContext(vec3d, p_222259_0_, RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, p_222259_1_)).getType() == RayTraceResult.Type.MISS) {
                     ++i;
                  }

                  ++j;
               }
            }
         }

         return (float)i / (float)j;
      } else {
         return 0.0F;
      }
   }

   public void explode() {
      Set<BlockPos> set = Sets.newHashSet();
      int i = 16;

      for(int j = 0; j < 16; ++j) {
         for(int k = 0; k < 16; ++k) {
            for(int l = 0; l < 16; ++l) {
               if (j == 0 || j == 15 || k == 0 || k == 15 || l == 0 || l == 15) {
                  double d0 = (double)((float)j / 15.0F * 2.0F - 1.0F);
                  double d1 = (double)((float)k / 15.0F * 2.0F - 1.0F);
                  double d2 = (double)((float)l / 15.0F * 2.0F - 1.0F);
                  double d3 = Math.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
                  d0 = d0 / d3;
                  d1 = d1 / d3;
                  d2 = d2 / d3;
                  float f = this.radius * (0.7F + this.level.random.nextFloat() * 0.6F);
                  double d4 = this.x;
                  double d6 = this.y;
                  double d8 = this.z;

                  for(float f1 = 0.3F; f > 0.0F; f -= 0.22500001F) {
                     BlockPos blockpos = new BlockPos(d4, d6, d8);
                     BlockState blockstate = this.level.getBlockState(blockpos);
                     IFluidState ifluidstate = this.level.getFluidState(blockpos);
                     if (!blockstate.isAir(this.level, blockpos) || !ifluidstate.isEmpty()) {
                        float f2 = Math.max(blockstate.getExplosionResistance(this.level, blockpos, source, this), ifluidstate.getExplosionResistance(this.level, blockpos, source, this));
                        if (this.source != null) {
                           f2 = this.source.getBlockExplosionResistance(this, this.level, blockpos, blockstate, ifluidstate, f2);
                        }

                        f -= (f2 + 0.3F) * 0.3F;
                     }

                     if (f > 0.0F && (this.source == null || this.source.shouldBlockExplode(this, this.level, blockpos, blockstate, f))) {
                        set.add(blockpos);
                     }

                     d4 += d0 * (double)0.3F;
                     d6 += d1 * (double)0.3F;
                     d8 += d2 * (double)0.3F;
                  }
               }
            }
         }
      }

      this.toBlow.addAll(set);
      float f3 = this.radius * 2.0F;
      int k1 = MathHelper.floor(this.x - (double)f3 - 1.0D);
      int l1 = MathHelper.floor(this.x + (double)f3 + 1.0D);
      int i2 = MathHelper.floor(this.y - (double)f3 - 1.0D);
      int i1 = MathHelper.floor(this.y + (double)f3 + 1.0D);
      int j2 = MathHelper.floor(this.z - (double)f3 - 1.0D);
      int j1 = MathHelper.floor(this.z + (double)f3 + 1.0D);
      List<Entity> list = this.level.getEntities(this.source, new AxisAlignedBB((double)k1, (double)i2, (double)j2, (double)l1, (double)i1, (double)j1));
      net.minecraftforge.event.ForgeEventFactory.onExplosionDetonate(this.level, this, list, f3);
      Vec3d vec3d = new Vec3d(this.x, this.y, this.z);

      for(int k2 = 0; k2 < list.size(); ++k2) {
         Entity entity = list.get(k2);
         if (!entity.ignoreExplosion()) {
            double d12 = (double)(MathHelper.sqrt(entity.distanceToSqr(vec3d)) / f3);
            if (d12 <= 1.0D) {
               double d5 = entity.getX() - this.x;
               double d7 = entity.getEyeY() - this.y;
               double d9 = entity.getZ() - this.z;
               double d13 = (double)MathHelper.sqrt(d5 * d5 + d7 * d7 + d9 * d9);
               if (d13 != 0.0D) {
                  d5 = d5 / d13;
                  d7 = d7 / d13;
                  d9 = d9 / d13;
                  double d14 = (double)getSeenPercent(vec3d, entity);
                  double d10 = (1.0D - d12) * d14;
                  entity.hurt(this.getDamageSource(), (float)((int)((d10 * d10 + d10) / 2.0D * 7.0D * (double)f3 + 1.0D)));
                  double d11 = d10;
                  if (entity instanceof LivingEntity) {
                     d11 = ProtectionEnchantment.getExplosionKnockbackAfterDampener((LivingEntity)entity, d10);
                  }

                  entity.setDeltaMovement(entity.getDeltaMovement().add(d5 * d11, d7 * d11, d9 * d11));
                  if (entity instanceof PlayerEntity) {
                     PlayerEntity playerentity = (PlayerEntity)entity;
                     if (!playerentity.isSpectator() && (!playerentity.isCreative() || !playerentity.abilities.flying)) {
                        this.hitPlayers.put(playerentity, new Vec3d(d5 * d10, d7 * d10, d9 * d10));
                     }
                  }
               }
            }
         }
      }

   }

   public void finalizeExplosion(boolean p_77279_1_) {
      if (this.level.isClientSide) {
         this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.GENERIC_EXPLODE, SoundCategory.BLOCKS, 4.0F, (1.0F + (this.level.random.nextFloat() - this.level.random.nextFloat()) * 0.2F) * 0.7F, false);
      }

      boolean flag = this.blockInteraction != Explosion.Mode.NONE;
      if (p_77279_1_) {
         if (!(this.radius < 2.0F) && flag) {
            this.level.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         } else {
            this.level.addParticle(ParticleTypes.EXPLOSION, this.x, this.y, this.z, 1.0D, 0.0D, 0.0D);
         }
      }

      if (flag) {
         ObjectArrayList<Pair<ItemStack, BlockPos>> objectarraylist = new ObjectArrayList<>();
         Collections.shuffle(this.toBlow, this.level.random);

         for(BlockPos blockpos : this.toBlow) {
            BlockState blockstate = this.level.getBlockState(blockpos);
            Block block = blockstate.getBlock();
            if (!blockstate.isAir(this.level, blockpos)) {
               BlockPos blockpos1 = blockpos.immutable();
               this.level.getProfiler().push("explosion_blocks");
               if (blockstate.canDropFromExplosion(this.level, blockpos, this) && this.level instanceof ServerWorld) {
                  TileEntity tileentity = blockstate.hasTileEntity() ? this.level.getBlockEntity(blockpos) : null;
                  LootContext.Builder lootcontext$builder = (new LootContext.Builder((ServerWorld)this.level)).withRandom(this.level.random).withParameter(LootParameters.field_216286_f, blockpos).withParameter(LootParameters.TOOL, ItemStack.EMPTY).withOptionalParameter(LootParameters.BLOCK_ENTITY, tileentity).withOptionalParameter(LootParameters.THIS_ENTITY, this.source);
                  if (this.blockInteraction == Explosion.Mode.DESTROY) {
                     lootcontext$builder.withParameter(LootParameters.EXPLOSION_RADIUS, this.radius);
                  }

                  blockstate.getDrops(lootcontext$builder).forEach((p_229977_2_) -> {
                     addBlockDrops(objectarraylist, p_229977_2_, blockpos1);
                  });
               }

               blockstate.onBlockExploded(this.level, blockpos, this);
               this.level.getProfiler().pop();
            }
         }

         for(Pair<ItemStack, BlockPos> pair : objectarraylist) {
            Block.popResource(this.level, pair.getSecond(), pair.getFirst());
         }
      }

      if (this.fire) {
         for(BlockPos blockpos2 : this.toBlow) {
            if (this.random.nextInt(3) == 0 && this.level.getBlockState(blockpos2).isAir() && this.level.getBlockState(blockpos2.below()).isSolidRender(this.level, blockpos2.below())) {
               this.level.setBlockAndUpdate(blockpos2, Blocks.FIRE.defaultBlockState());
            }
         }
      }

   }

   private static void addBlockDrops(ObjectArrayList<Pair<ItemStack, BlockPos>> p_229976_0_, ItemStack p_229976_1_, BlockPos p_229976_2_) {
      int i = p_229976_0_.size();

      for(int j = 0; j < i; ++j) {
         Pair<ItemStack, BlockPos> pair = p_229976_0_.get(j);
         ItemStack itemstack = pair.getFirst();
         if (ItemEntity.areMergable(itemstack, p_229976_1_)) {
            ItemStack itemstack1 = ItemEntity.merge(itemstack, p_229976_1_, 16);
            p_229976_0_.set(j, Pair.of(itemstack1, pair.getSecond()));
            if (p_229976_1_.isEmpty()) {
               return;
            }
         }
      }

      p_229976_0_.add(Pair.of(p_229976_1_, p_229976_2_));
   }

   public DamageSource getDamageSource() {
      return this.damageSource;
   }

   public void func_199592_a(DamageSource p_199592_1_) {
      this.damageSource = p_199592_1_;
   }

   public Map<PlayerEntity, Vec3d> getHitPlayers() {
      return this.hitPlayers;
   }

   @Nullable
   public LivingEntity getSourceMob() {
      if (this.source == null) {
         return null;
      } else if (this.source instanceof TNTEntity) {
         return ((TNTEntity)this.source).getOwner();
      } else if (this.source instanceof LivingEntity) {
         return (LivingEntity)this.source;
      } else {
         return this.source instanceof DamagingProjectileEntity ? ((DamagingProjectileEntity)this.source).field_70235_a : null;
      }
   }

   public void clearToBlow() {
      this.toBlow.clear();
   }

   public List<BlockPos> getToBlow() {
      return this.toBlow;
   }

   public Vec3d getPosition() {
      return this.position;
   }

   public static enum Mode {
      NONE,
      BREAK,
      DESTROY;
   }
}
