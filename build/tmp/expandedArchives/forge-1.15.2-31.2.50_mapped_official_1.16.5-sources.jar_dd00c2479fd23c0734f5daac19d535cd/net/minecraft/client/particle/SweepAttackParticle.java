package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SweepAttackParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite sprites;

   private SweepAttackParticle(World p_i51054_1_, double p_i51054_2_, double p_i51054_4_, double p_i51054_6_, double p_i51054_8_, IAnimatedSprite p_i51054_10_) {
      super(p_i51054_1_, p_i51054_2_, p_i51054_4_, p_i51054_6_, 0.0D, 0.0D, 0.0D);
      this.sprites = p_i51054_10_;
      this.lifetime = 4;
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 1.0F - (float)p_i51054_8_ * 0.5F;
      this.setSpriteFromAge(p_i51054_10_);
   }

   public int getLightColor(float p_189214_1_) {
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_LIT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i50563_1_) {
         this.sprites = p_i50563_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SweepAttackParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, this.sprites);
      }
   }
}