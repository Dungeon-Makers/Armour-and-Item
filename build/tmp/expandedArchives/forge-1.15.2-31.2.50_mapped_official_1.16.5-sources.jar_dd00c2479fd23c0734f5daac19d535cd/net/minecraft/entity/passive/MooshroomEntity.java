package net.minecraft.entity.passive;

import java.util.Random;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowerBlock;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.Effect;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.Pair;

public class MooshroomEntity extends CowEntity implements net.minecraftforge.common.IShearable {
   private static final DataParameter<String> DATA_TYPE = EntityDataManager.defineId(MooshroomEntity.class, DataSerializers.STRING);
   private Effect effect;
   private int effectDuration;
   private UUID lastLightningBoltUUID;

   public MooshroomEntity(EntityType<? extends MooshroomEntity> p_i50257_1_, World p_i50257_2_) {
      super(p_i50257_1_, p_i50257_2_);
   }

   public float getWalkTargetValue(BlockPos p_205022_1_, IWorldReader p_205022_2_) {
      return p_205022_2_.getBlockState(p_205022_1_.below()).getBlock() == Blocks.MYCELIUM ? 10.0F : p_205022_2_.getBrightness(p_205022_1_) - 0.5F;
   }

   public static boolean checkMushroomSpawnRules(EntityType<MooshroomEntity> p_223318_0_, IWorld p_223318_1_, SpawnReason p_223318_2_, BlockPos p_223318_3_, Random p_223318_4_) {
      return p_223318_1_.getBlockState(p_223318_3_.below()).getBlock() == Blocks.MYCELIUM && p_223318_1_.getRawBrightness(p_223318_3_, 0) > 8;
   }

   public void func_70077_a(LightningBoltEntity p_70077_1_) {
      UUID uuid = p_70077_1_.getUUID();
      if (!uuid.equals(this.lastLightningBoltUUID)) {
         this.setMushroomType(this.getMushroomType() == MooshroomEntity.Type.RED ? MooshroomEntity.Type.BROWN : MooshroomEntity.Type.RED);
         this.lastLightningBoltUUID = uuid;
         this.playSound(SoundEvents.MOOSHROOM_CONVERT, 2.0F, 1.0F);
      }

   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_TYPE, MooshroomEntity.Type.RED.type);
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (itemstack.getItem() == Items.BOWL && !this.isBaby() && !p_184645_1_.abilities.instabuild) {
         itemstack.shrink(1);
         boolean flag = false;
         ItemStack itemstack1;
         if (this.effect != null) {
            flag = true;
            itemstack1 = new ItemStack(Items.SUSPICIOUS_STEW);
            SuspiciousStewItem.saveMobEffect(itemstack1, this.effect, this.effectDuration);
            this.effect = null;
            this.effectDuration = 0;
         } else {
            itemstack1 = new ItemStack(Items.MUSHROOM_STEW);
         }

         if (itemstack.isEmpty()) {
            p_184645_1_.setItemInHand(p_184645_2_, itemstack1);
         } else if (!p_184645_1_.inventory.add(itemstack1)) {
            p_184645_1_.drop(itemstack1, false);
         }

         SoundEvent soundevent;
         if (flag) {
            soundevent = SoundEvents.MOOSHROOM_MILK_SUSPICIOUSLY;
         } else {
            soundevent = SoundEvents.MOOSHROOM_MILK;
         }

         this.playSound(soundevent, 1.0F, 1.0F);
         return true;
      } else if (false && itemstack.getItem() == Items.SHEARS && !this.isBaby()) { //Forge: Moved to onSheared
         this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5D), this.getZ(), 0.0D, 0.0D, 0.0D);
         if (!this.level.isClientSide) {
            this.remove();
            CowEntity cowentity = EntityType.COW.create(this.level);
            cowentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
            cowentity.setHealth(this.getHealth());
            cowentity.yBodyRot = this.yBodyRot;
            if (this.hasCustomName()) {
               cowentity.setCustomName(this.getCustomName());
               cowentity.setCustomNameVisible(this.isCustomNameVisible());
            }

            if (this.isPersistenceRequired()) {
               cowentity.setPersistenceRequired();
            }

            cowentity.setInvulnerable(this.isInvulnerable());
            this.level.addFreshEntity(cowentity);

            for(int k = 0; k < 5; ++k) {
               this.level.addFreshEntity(new ItemEntity(this.level, this.getX(), this.getY(1.0D), this.getZ(), new ItemStack(this.getMushroomType().blockState.getBlock())));
            }

            itemstack.hurtAndBreak(1, p_184645_1_, (p_213442_1_) -> {
               p_213442_1_.broadcastBreakEvent(p_184645_2_);
            });
            this.playSound(SoundEvents.MOOSHROOM_SHEAR, 1.0F, 1.0F);
         }

         return true;
      } else {
         if (this.getMushroomType() == MooshroomEntity.Type.BROWN && itemstack.getItem().is(ItemTags.SMALL_FLOWERS)) {
            if (this.effect != null) {
               for(int i = 0; i < 2; ++i) {
                  this.level.addParticle(ParticleTypes.SMOKE, this.getX() + (double)(this.random.nextFloat() / 2.0F), this.getY(0.5D), this.getZ() + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
               }
            } else {
               Pair<Effect, Integer> pair = this.getEffectFromItemStack(itemstack);
               if (!p_184645_1_.abilities.instabuild) {
                  itemstack.shrink(1);
               }

               for(int j = 0; j < 4; ++j) {
                  this.level.addParticle(ParticleTypes.EFFECT, this.getX() + (double)(this.random.nextFloat() / 2.0F), this.getY(0.5D), this.getZ() + (double)(this.random.nextFloat() / 2.0F), 0.0D, (double)(this.random.nextFloat() / 5.0F), 0.0D);
               }

               this.effect = pair.getLeft();
               this.effectDuration = pair.getRight();
               this.playSound(SoundEvents.MOOSHROOM_EAT, 2.0F, 1.0F);
            }
         }

         return super.func_184645_a(p_184645_1_, p_184645_2_);
      }
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putString("Type", this.getMushroomType().type);
      if (this.effect != null) {
         p_213281_1_.putByte("EffectId", (byte)Effect.getId(this.effect));
         p_213281_1_.putInt("EffectDuration", this.effectDuration);
      }

   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setMushroomType(MooshroomEntity.Type.byType(p_70037_1_.getString("Type")));
      if (p_70037_1_.contains("EffectId", 1)) {
         this.effect = Effect.byId(p_70037_1_.getByte("EffectId"));
      }

      if (p_70037_1_.contains("EffectDuration", 3)) {
         this.effectDuration = p_70037_1_.getInt("EffectDuration");
      }

   }

   private Pair<Effect, Integer> getEffectFromItemStack(ItemStack p_213443_1_) {
      FlowerBlock flowerblock = (FlowerBlock)((BlockItem)p_213443_1_.getItem()).getBlock();
      return Pair.of(flowerblock.getSuspiciousStewEffect(), flowerblock.getEffectDuration());
   }

   private void setMushroomType(MooshroomEntity.Type p_213446_1_) {
      this.entityData.set(DATA_TYPE, p_213446_1_.type);
   }

   public MooshroomEntity.Type getMushroomType() {
      return MooshroomEntity.Type.byType(this.entityData.get(DATA_TYPE));
   }

   public MooshroomEntity func_90011_a(AgeableEntity p_90011_1_) {
      MooshroomEntity mooshroomentity = EntityType.MOOSHROOM.create(this.level);
      mooshroomentity.setMushroomType(this.getOffspringType((MooshroomEntity)p_90011_1_));
      return mooshroomentity;
   }

   private MooshroomEntity.Type getOffspringType(MooshroomEntity p_213445_1_) {
      MooshroomEntity.Type mooshroomentity$type = this.getMushroomType();
      MooshroomEntity.Type mooshroomentity$type1 = p_213445_1_.getMushroomType();
      MooshroomEntity.Type mooshroomentity$type2;
      if (mooshroomentity$type == mooshroomentity$type1 && this.random.nextInt(1024) == 0) {
         mooshroomentity$type2 = mooshroomentity$type == MooshroomEntity.Type.BROWN ? MooshroomEntity.Type.RED : MooshroomEntity.Type.BROWN;
      } else {
         mooshroomentity$type2 = this.random.nextBoolean() ? mooshroomentity$type : mooshroomentity$type1;
      }

      return mooshroomentity$type2;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, net.minecraft.util.math.BlockPos pos) {
      return !this.isBaby();
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, net.minecraft.util.math.BlockPos pos, int fortune) {
      java.util.List<ItemStack> ret = new java.util.ArrayList<>();
      this.level.addParticle(ParticleTypes.EXPLOSION, this.getX(), this.getY(0.5D), this.getZ(), 0.0D, 0.0D, 0.0D);
      if (!this.level.isClientSide) {
         this.remove();
         CowEntity cowentity = EntityType.COW.create(this.level);
         cowentity.moveTo(this.getX(), this.getY(), this.getZ(), this.yRot, this.xRot);
         cowentity.setHealth(this.getHealth());
         cowentity.yBodyRot = this.yBodyRot;
         if (this.hasCustomName()) {
             cowentity.setCustomName(this.getCustomName());
             cowentity.setCustomNameVisible(this.isCustomNameVisible());
         }
         this.level.addFreshEntity(cowentity);
         for(int i = 0; i < 5; ++i) {
            ret.add(new ItemStack(this.getMushroomType().blockState.getBlock()));
         }
         this.playSound(SoundEvents.MOOSHROOM_SHEAR, 1.0F, 1.0F);
      }
      return ret;
   }

   public static enum Type {
      RED("red", Blocks.RED_MUSHROOM.defaultBlockState()),
      BROWN("brown", Blocks.BROWN_MUSHROOM.defaultBlockState());

      private final String type;
      private final BlockState blockState;

      private Type(String p_i50425_3_, BlockState p_i50425_4_) {
         this.type = p_i50425_3_;
         this.blockState = p_i50425_4_;
      }

      @OnlyIn(Dist.CLIENT)
      public BlockState getBlockState() {
         return this.blockState;
      }

      private static MooshroomEntity.Type byType(String p_221097_0_) {
         for(MooshroomEntity.Type mooshroomentity$type : values()) {
            if (mooshroomentity$type.type.equals(p_221097_0_)) {
               return mooshroomentity$type;
            }
         }

         return RED;
      }
   }
}
