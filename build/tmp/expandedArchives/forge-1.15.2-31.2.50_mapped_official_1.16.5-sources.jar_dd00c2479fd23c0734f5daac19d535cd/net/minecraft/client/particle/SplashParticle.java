package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SplashParticle extends RainParticle {
   private SplashParticle(World p_i1230_1_, double p_i1230_2_, double p_i1230_4_, double p_i1230_6_, double p_i1230_8_, double p_i1230_10_, double p_i1230_12_) {
      super(p_i1230_1_, p_i1230_2_, p_i1230_4_, p_i1230_6_);
      this.gravity = 0.04F;
      if (p_i1230_10_ == 0.0D && (p_i1230_8_ != 0.0D || p_i1230_12_ != 0.0D)) {
         this.xd = p_i1230_8_;
         this.yd = 0.1D;
         this.zd = p_i1230_12_;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50679_1_) {
         this.sprite = p_i50679_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SplashParticle splashparticle = new SplashParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         splashparticle.pickSprite(this.sprite);
         return splashparticle;
      }
   }
}