package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

public class BreedGoal extends Goal {
   private static final EntityPredicate PARTNER_TARGETING = (new EntityPredicate()).range(8.0D).allowInvulnerable().allowSameTeam().allowUnseeable();
   protected final AnimalEntity animal;
   private final Class<? extends AnimalEntity> partnerClass;
   protected final World level;
   protected AnimalEntity partner;
   private int loveTime;
   private final double speedModifier;

   public BreedGoal(AnimalEntity p_i1619_1_, double p_i1619_2_) {
      this(p_i1619_1_, p_i1619_2_, p_i1619_1_.getClass());
   }

   public BreedGoal(AnimalEntity p_i47306_1_, double p_i47306_2_, Class<? extends AnimalEntity> p_i47306_4_) {
      this.animal = p_i47306_1_;
      this.level = p_i47306_1_.level;
      this.partnerClass = p_i47306_4_;
      this.speedModifier = p_i47306_2_;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   public boolean canUse() {
      if (!this.animal.isInLove()) {
         return false;
      } else {
         this.partner = this.getFreePartner();
         return this.partner != null;
      }
   }

   public boolean canContinueToUse() {
      return this.partner.isAlive() && this.partner.isInLove() && this.loveTime < 60;
   }

   public void stop() {
      this.partner = null;
      this.loveTime = 0;
   }

   public void tick() {
      this.animal.getLookControl().setLookAt(this.partner, 10.0F, (float)this.animal.getMaxHeadXRot());
      this.animal.getNavigation().moveTo(this.partner, this.speedModifier);
      ++this.loveTime;
      if (this.loveTime >= 60 && this.animal.distanceToSqr(this.partner) < 9.0D) {
         this.breed();
      }

   }

   @Nullable
   private AnimalEntity getFreePartner() {
      List<AnimalEntity> list = this.level.getNearbyEntities(this.partnerClass, PARTNER_TARGETING, this.animal, this.animal.getBoundingBox().inflate(8.0D));
      double d0 = Double.MAX_VALUE;
      AnimalEntity animalentity = null;

      for(AnimalEntity animalentity1 : list) {
         if (this.animal.canMate(animalentity1) && this.animal.distanceToSqr(animalentity1) < d0) {
            animalentity = animalentity1;
            d0 = this.animal.distanceToSqr(animalentity1);
         }
      }

      return animalentity;
   }

   protected void breed() {
      AgeableEntity ageableentity = this.animal.func_90011_a(this.partner);
      final net.minecraftforge.event.entity.living.BabyEntitySpawnEvent event = new net.minecraftforge.event.entity.living.BabyEntitySpawnEvent(animal, partner, ageableentity);
      final boolean cancelled = net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);
      ageableentity = event.getChild();
      if (cancelled) {
         //Reset the "inLove" state for the animals
         this.animal.setAge(6000);
         this.partner.setAge(6000);
         this.animal.resetLove();
         this.partner.resetLove();
         return;
      }
      if (ageableentity != null) {
         ServerPlayerEntity serverplayerentity = this.animal.getLoveCause();
         if (serverplayerentity == null && this.partner.getLoveCause() != null) {
            serverplayerentity = this.partner.getLoveCause();
         }

         if (serverplayerentity != null) {
            serverplayerentity.awardStat(Stats.ANIMALS_BRED);
            CriteriaTriggers.BRED_ANIMALS.trigger(serverplayerentity, this.animal, this.partner, ageableentity);
         }

         this.animal.setAge(6000);
         this.partner.setAge(6000);
         this.animal.resetLove();
         this.partner.resetLove();
         ageableentity.setAge(-24000);
         ageableentity.moveTo(this.animal.getX(), this.animal.getY(), this.animal.getZ(), 0.0F, 0.0F);
         this.level.addFreshEntity(ageableentity);
         this.level.broadcastEntityEvent(this.animal, (byte)18);
         if (this.level.getGameRules().getBoolean(GameRules.RULE_DOMOBLOOT)) {
            this.level.addFreshEntity(new ExperienceOrbEntity(this.level, this.animal.getX(), this.animal.getY(), this.animal.getZ(), this.animal.getRandom().nextInt(7) + 1));
         }

      }
   }
}
