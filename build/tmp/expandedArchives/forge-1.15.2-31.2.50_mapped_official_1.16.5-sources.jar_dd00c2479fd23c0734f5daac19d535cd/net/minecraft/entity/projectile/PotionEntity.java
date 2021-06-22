package net.minecraft.entity.projectile;

import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.AreaEffectCloudEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.BlazeEntity;
import net.minecraft.entity.monster.EndermanEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class PotionEntity extends ThrowableEntity implements IRendersAsItem {
   private static final DataParameter<ItemStack> field_184545_d = EntityDataManager.defineId(PotionEntity.class, DataSerializers.ITEM_STACK);
   private static final Logger field_184546_e = LogManager.getLogger();
   public static final Predicate<LivingEntity> WATER_SENSITIVE = PotionEntity::func_190544_c;

   public PotionEntity(EntityType<? extends PotionEntity> p_i50149_1_, World p_i50149_2_) {
      super(p_i50149_1_, p_i50149_2_);
   }

   public PotionEntity(World p_i50150_1_, LivingEntity p_i50150_2_) {
      super(EntityType.POTION, p_i50150_2_, p_i50150_1_);
   }

   public PotionEntity(World p_i50151_1_, double p_i50151_2_, double p_i50151_4_, double p_i50151_6_) {
      super(EntityType.POTION, p_i50151_2_, p_i50151_4_, p_i50151_6_, p_i50151_1_);
   }

   protected void defineSynchedData() {
      this.getEntityData().define(field_184545_d, ItemStack.EMPTY);
   }

   public ItemStack getItem() {
      ItemStack itemstack = this.getEntityData().get(field_184545_d);
      if (itemstack.getItem() != Items.SPLASH_POTION && itemstack.getItem() != Items.LINGERING_POTION) {
         if (this.level != null) {
            field_184546_e.error("ThrownPotion entity {} has no item?!", (int)this.getId());
         }

         return new ItemStack(Items.SPLASH_POTION);
      } else {
         return itemstack;
      }
   }

   public void func_184541_a(ItemStack p_184541_1_) {
      this.getEntityData().set(field_184545_d, p_184541_1_.copy());
   }

   protected float getGravity() {
      return 0.05F;
   }

   protected void func_70184_a(RayTraceResult p_70184_1_) {
      if (!this.level.isClientSide) {
         ItemStack itemstack = this.getItem();
         Potion potion = PotionUtils.getPotion(itemstack);
         List<EffectInstance> list = PotionUtils.getMobEffects(itemstack);
         boolean flag = potion == Potions.WATER && list.isEmpty();
         if (p_70184_1_.getType() == RayTraceResult.Type.BLOCK && flag) {
            BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult)p_70184_1_;
            Direction direction = blockraytraceresult.getDirection();
            BlockPos blockpos = blockraytraceresult.getBlockPos().relative(direction);
            this.dowseFire(blockpos, direction);
            this.dowseFire(blockpos.relative(direction.getOpposite()), direction);

            for(Direction direction1 : Direction.Plane.HORIZONTAL) {
               this.dowseFire(blockpos.relative(direction1), direction1);
            }
         }

         if (flag) {
            this.applyWater();
         } else if (!list.isEmpty()) {
            if (this.isLingering()) {
               this.makeAreaOfEffectCloud(itemstack, potion);
            } else {
               this.applySplash(list, p_70184_1_.getType() == RayTraceResult.Type.ENTITY ? ((EntityRayTraceResult)p_70184_1_).getEntity() : null);
            }
         }

         int i = potion.hasInstantEffects() ? 2007 : 2002;
         this.level.levelEvent(i, new BlockPos(this), PotionUtils.getColor(itemstack));
         this.remove();
      }
   }

   private void applyWater() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb, WATER_SENSITIVE);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            double d0 = this.distanceToSqr(livingentity);
            if (d0 < 16.0D && func_190544_c(livingentity)) {
               livingentity.hurt(DamageSource.indirectMagic(livingentity, this.func_85052_h()), 1.0F);
            }
         }
      }

   }

   private void applySplash(List<EffectInstance> p_213888_1_, @Nullable Entity p_213888_2_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().inflate(4.0D, 2.0D, 4.0D);
      List<LivingEntity> list = this.level.getEntitiesOfClass(LivingEntity.class, axisalignedbb);
      if (!list.isEmpty()) {
         for(LivingEntity livingentity : list) {
            if (livingentity.isAffectedByPotions()) {
               double d0 = this.distanceToSqr(livingentity);
               if (d0 < 16.0D) {
                  double d1 = 1.0D - Math.sqrt(d0) / 4.0D;
                  if (livingentity == p_213888_2_) {
                     d1 = 1.0D;
                  }

                  for(EffectInstance effectinstance : p_213888_1_) {
                     Effect effect = effectinstance.getEffect();
                     if (effect.isInstantenous()) {
                        effect.applyInstantenousEffect(this, this.func_85052_h(), livingentity, effectinstance.getAmplifier(), d1);
                     } else {
                        int i = (int)(d1 * (double)effectinstance.getDuration() + 0.5D);
                        if (i > 20) {
                           livingentity.addEffect(new EffectInstance(effect, i, effectinstance.getAmplifier(), effectinstance.isAmbient(), effectinstance.isVisible()));
                        }
                     }
                  }
               }
            }
         }
      }

   }

   private void makeAreaOfEffectCloud(ItemStack p_190542_1_, Potion p_190542_2_) {
      AreaEffectCloudEntity areaeffectcloudentity = new AreaEffectCloudEntity(this.level, this.getX(), this.getY(), this.getZ());
      areaeffectcloudentity.setOwner(this.func_85052_h());
      areaeffectcloudentity.setRadius(3.0F);
      areaeffectcloudentity.setRadiusOnUse(-0.5F);
      areaeffectcloudentity.setWaitTime(10);
      areaeffectcloudentity.setRadiusPerTick(-areaeffectcloudentity.getRadius() / (float)areaeffectcloudentity.getDuration());
      areaeffectcloudentity.setPotion(p_190542_2_);

      for(EffectInstance effectinstance : PotionUtils.getCustomEffects(p_190542_1_)) {
         areaeffectcloudentity.addEffect(new EffectInstance(effectinstance));
      }

      CompoundNBT compoundnbt = p_190542_1_.getTag();
      if (compoundnbt != null && compoundnbt.contains("CustomPotionColor", 99)) {
         areaeffectcloudentity.setFixedColor(compoundnbt.getInt("CustomPotionColor"));
      }

      this.level.addFreshEntity(areaeffectcloudentity);
   }

   private boolean isLingering() {
      return this.getItem().getItem() == Items.LINGERING_POTION;
   }

   private void dowseFire(BlockPos p_184542_1_, Direction p_184542_2_) {
      BlockState blockstate = this.level.getBlockState(p_184542_1_);
      Block block = blockstate.getBlock();
      if (block == Blocks.FIRE) {
         this.level.func_175719_a((PlayerEntity)null, p_184542_1_.relative(p_184542_2_), p_184542_2_.getOpposite());
      } else if (block == Blocks.CAMPFIRE && blockstate.getValue(CampfireBlock.LIT)) {
         this.level.levelEvent((PlayerEntity)null, 1009, p_184542_1_, 0);
         this.level.setBlockAndUpdate(p_184542_1_, blockstate.setValue(CampfireBlock.LIT, Boolean.valueOf(false)));
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      ItemStack itemstack = ItemStack.of(p_70037_1_.getCompound("Potion"));
      if (itemstack.isEmpty()) {
         this.remove();
      } else {
         this.func_184541_a(itemstack);
      }

   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      ItemStack itemstack = this.getItem();
      if (!itemstack.isEmpty()) {
         p_213281_1_.put("Potion", itemstack.save(new CompoundNBT()));
      }

   }

   private static boolean func_190544_c(LivingEntity p_190544_0_) {
      return p_190544_0_ instanceof EndermanEntity || p_190544_0_ instanceof BlazeEntity;
   }
}