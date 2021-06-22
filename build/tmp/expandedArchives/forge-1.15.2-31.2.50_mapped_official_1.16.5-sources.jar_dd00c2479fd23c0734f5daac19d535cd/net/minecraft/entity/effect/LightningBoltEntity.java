package net.minecraft.entity.effect;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnGlobalEntityPacket;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class LightningBoltEntity extends Entity {
   private int life;
   public long seed;
   private int flashes;
   private final boolean visualOnly;
   @Nullable
   private ServerPlayerEntity cause;

   public LightningBoltEntity(World p_i46780_1_, double p_i46780_2_, double p_i46780_4_, double p_i46780_6_, boolean p_i46780_8_) {
      super(EntityType.LIGHTNING_BOLT, p_i46780_1_);
      this.noCulling = true;
      this.moveTo(p_i46780_2_, p_i46780_4_, p_i46780_6_, 0.0F, 0.0F);
      this.life = 2;
      this.seed = this.random.nextLong();
      this.flashes = this.random.nextInt(3) + 1;
      this.visualOnly = p_i46780_8_;
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.WEATHER;
   }

   public void setCause(@Nullable ServerPlayerEntity p_204809_1_) {
      this.cause = p_204809_1_;
   }

   public void tick() {
      super.tick();
      if (this.life == 2) {
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_THUNDER, SoundCategory.WEATHER, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.LIGHTNING_BOLT_IMPACT, SoundCategory.WEATHER, 2.0F, 0.5F + this.random.nextFloat() * 0.2F);
         Difficulty difficulty = this.level.getDifficulty();
         if (difficulty == Difficulty.NORMAL || difficulty == Difficulty.HARD) {
            this.spawnFire(4);
         }
      }

      --this.life;
      if (this.life < 0) {
         if (this.flashes == 0) {
            this.remove();
         } else if (this.life < -this.random.nextInt(10)) {
            --this.flashes;
            this.life = 1;
            this.seed = this.random.nextLong();
            this.spawnFire(0);
         }
      }

      if (this.life >= 0) {
         if (this.level.isClientSide) {
            this.level.setSkyFlashTime(2);
         } else if (!this.visualOnly) {
            double d0 = 3.0D;
            List<Entity> list = this.level.getEntities(this, new AxisAlignedBB(this.getX() - 3.0D, this.getY() - 3.0D, this.getZ() - 3.0D, this.getX() + 3.0D, this.getY() + 6.0D + 3.0D, this.getZ() + 3.0D), Entity::isAlive);

            for(Entity entity : list) {
               if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, this))
               entity.func_70077_a(this);
            }

            if (this.cause != null) {
               CriteriaTriggers.CHANNELED_LIGHTNING.trigger(this.cause, list);
            }
         }
      }

   }

   private void spawnFire(int p_195053_1_) {
      if (!this.visualOnly && !this.level.isClientSide && this.level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
         BlockState blockstate = Blocks.FIRE.defaultBlockState();
         BlockPos blockpos = new BlockPos(this);
         if (this.level.getBlockState(blockpos).isAir() && blockstate.canSurvive(this.level, blockpos)) {
            this.level.setBlockAndUpdate(blockpos, blockstate);
         }

         for(int i = 0; i < p_195053_1_; ++i) {
            BlockPos blockpos1 = blockpos.offset(this.random.nextInt(3) - 1, this.random.nextInt(3) - 1, this.random.nextInt(3) - 1);
            if (this.level.getBlockState(blockpos1).isAir() && blockstate.canSurvive(this.level, blockpos1)) {
               this.level.setBlockAndUpdate(blockpos1, blockstate);
            }
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = 64.0D * getViewScale();
      return p_70112_1_ < d0 * d0;
   }

   protected void defineSynchedData() {
   }

   protected void readAdditionalSaveData(CompoundNBT p_70037_1_) {
   }

   protected void addAdditionalSaveData(CompoundNBT p_213281_1_) {
   }

   public IPacket<?> getAddEntityPacket() {
      return new SSpawnGlobalEntityPacket(this);
   }
}
