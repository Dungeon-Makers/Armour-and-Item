package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TotemOfUndyingParticle extends SimpleAnimatedParticle {
   private TotemOfUndyingParticle(World p_i50996_1_, double p_i50996_2_, double p_i50996_4_, double p_i50996_6_, double p_i50996_8_, double p_i50996_10_, double p_i50996_12_, IAnimatedSprite p_i50996_14_) {
      super(p_i50996_1_, p_i50996_2_, p_i50996_4_, p_i50996_6_, p_i50996_14_, -0.05F);
      this.xd = p_i50996_8_;
      this.yd = p_i50996_10_;
      this.zd = p_i50996_12_;
      this.quadSize *= 0.75F;
      this.lifetime = 60 + this.random.nextInt(12);
      this.setSpriteFromAge(p_i50996_14_);
      if (this.random.nextInt(4) == 0) {
         this.setColor(0.6F + this.random.nextFloat() * 0.2F, 0.6F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      } else {
         this.setColor(0.1F + this.random.nextFloat() * 0.2F, 0.4F + this.random.nextFloat() * 0.3F, this.random.nextFloat() * 0.2F);
      }

      this.setBaseAirFriction(0.6F);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i50316_1_) {
         this.sprites = p_i50316_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new TotemOfUndyingParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}