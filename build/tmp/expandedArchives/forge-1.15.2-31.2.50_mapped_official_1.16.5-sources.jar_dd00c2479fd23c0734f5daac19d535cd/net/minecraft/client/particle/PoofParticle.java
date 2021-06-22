package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PoofParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite sprites;

   protected PoofParticle(World p_i51035_1_, double p_i51035_2_, double p_i51035_4_, double p_i51035_6_, double p_i51035_8_, double p_i51035_10_, double p_i51035_12_, IAnimatedSprite p_i51035_14_) {
      super(p_i51035_1_, p_i51035_2_, p_i51035_4_, p_i51035_6_);
      this.sprites = p_i51035_14_;
      this.xd = p_i51035_8_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.yd = p_i51035_10_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      this.zd = p_i51035_12_ + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
      float f = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
      this.lifetime = (int)(16.0D / ((double)this.random.nextFloat() * 0.8D + 0.2D)) + 2;
      this.setSpriteFromAge(p_i51035_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         this.yd += 0.004D;
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.9F;
         this.yd *= (double)0.9F;
         this.zd *= (double)0.9F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i49913_1_) {
         this.sprites = p_i49913_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new PoofParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprites);
      }
   }
}