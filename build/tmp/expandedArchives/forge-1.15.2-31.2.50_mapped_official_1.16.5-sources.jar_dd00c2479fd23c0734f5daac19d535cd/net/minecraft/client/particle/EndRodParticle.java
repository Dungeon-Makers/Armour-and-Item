package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndRodParticle extends SimpleAnimatedParticle {
   private EndRodParticle(World p_i51036_1_, double p_i51036_2_, double p_i51036_4_, double p_i51036_6_, double p_i51036_8_, double p_i51036_10_, double p_i51036_12_, IAnimatedSprite p_i51036_14_) {
      super(p_i51036_1_, p_i51036_2_, p_i51036_4_, p_i51036_6_, p_i51036_14_, -5.0E-4F);
      this.xd = p_i51036_8_;
      this.yd = p_i51036_10_;
      this.zd = p_i51036_12_;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setFadeColor(15916745);
      this.setSpriteFromAge(p_i51036_14_);
   }

   public void move(double p_187110_1_, double p_187110_3_, double p_187110_5_) {
      this.setBoundingBox(this.getBoundingBox().move(p_187110_1_, p_187110_3_, p_187110_5_));
      this.setLocationFromBoundingbox();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i50058_1_) {
         this.sprites = p_i50058_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new EndRodParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}