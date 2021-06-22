package net.minecraft.entity.boss.dragon.phase;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;

public class LandingPhase extends Phase {
   private Vec3d targetLocation;

   public LandingPhase(EnderDragonEntity p_i46788_1_) {
      super(p_i46788_1_);
   }

   public void doClientTick() {
      Vec3d vec3d = this.dragon.getHeadLookVector(1.0F).normalize();
      vec3d.yRot((-(float)Math.PI / 4F));
      double d0 = this.dragon.head.getX();
      double d1 = this.dragon.head.getY(0.5D);
      double d2 = this.dragon.head.getZ();

      for(int i = 0; i < 8; ++i) {
         Random random = this.dragon.getRandom();
         double d3 = d0 + random.nextGaussian() / 2.0D;
         double d4 = d1 + random.nextGaussian() / 2.0D;
         double d5 = d2 + random.nextGaussian() / 2.0D;
         Vec3d vec3d1 = this.dragon.getDeltaMovement();
         this.dragon.level.addParticle(ParticleTypes.DRAGON_BREATH, d3, d4, d5, -vec3d.x * (double)0.08F + vec3d1.x, -vec3d.y * (double)0.3F + vec3d1.y, -vec3d.z * (double)0.08F + vec3d1.z);
         vec3d.yRot(0.19634955F);
      }

   }

   public void doServerTick() {
      if (this.targetLocation == null) {
         this.targetLocation = new Vec3d(this.dragon.level.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION));
      }

      if (this.targetLocation.distanceToSqr(this.dragon.getX(), this.dragon.getY(), this.dragon.getZ()) < 1.0D) {
         this.dragon.getPhaseManager().getPhase(PhaseType.SITTING_FLAMING).resetFlameCount();
         this.dragon.getPhaseManager().setPhase(PhaseType.SITTING_SCANNING);
      }

   }

   public float getFlySpeed() {
      return 1.5F;
   }

   public float getTurnSpeed() {
      float f = MathHelper.sqrt(Entity.getHorizontalDistanceSqr(this.dragon.getDeltaMovement())) + 1.0F;
      float f1 = Math.min(f, 40.0F);
      return f1 / f;
   }

   public void begin() {
      this.targetLocation = null;
   }

   @Nullable
   public Vec3d getFlyTargetLocation() {
      return this.targetLocation;
   }

   public PhaseType<LandingPhase> getPhase() {
      return PhaseType.LANDING;
   }
}