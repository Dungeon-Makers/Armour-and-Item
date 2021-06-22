package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CampfireParticle extends SpriteTexturedParticle {
   private CampfireParticle(World p_i51046_1_, double p_i51046_2_, double p_i51046_4_, double p_i51046_6_, double p_i51046_8_, double p_i51046_10_, double p_i51046_12_, boolean p_i51046_14_) {
      super(p_i51046_1_, p_i51046_2_, p_i51046_4_, p_i51046_6_);
      this.scale(3.0F);
      this.setSize(0.25F, 0.25F);
      if (p_i51046_14_) {
         this.lifetime = this.random.nextInt(50) + 280;
      } else {
         this.lifetime = this.random.nextInt(50) + 80;
      }

      this.gravity = 3.0E-6F;
      this.xd = p_i51046_8_;
      this.yd = p_i51046_10_ + (double)(this.random.nextFloat() / 500.0F);
      this.zd = p_i51046_12_;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ < this.lifetime && !(this.alpha <= 0.0F)) {
         this.xd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.zd += (double)(this.random.nextFloat() / 5000.0F * (float)(this.random.nextBoolean() ? 1 : -1));
         this.yd -= (double)this.gravity;
         this.move(this.xd, this.yd, this.zd);
         if (this.age >= this.lifetime - 60 && this.alpha > 0.01F) {
            this.alpha -= 0.015F;
         }

      } else {
         this.remove();
      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class CozySmokeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public CozySmokeFactory(IAnimatedSprite p_i51180_1_) {
         this.sprites = p_i51180_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CampfireParticle campfireparticle = new CampfireParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, false);
         campfireparticle.setAlpha(0.9F);
         campfireparticle.pickSprite(this.sprites);
         return campfireparticle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class SignalSmokeFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public SignalSmokeFactory(IAnimatedSprite p_i51179_1_) {
         this.sprites = p_i51179_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         CampfireParticle campfireparticle = new CampfireParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, true);
         campfireparticle.setAlpha(0.95F);
         campfireparticle.pickSprite(this.sprites);
         return campfireparticle;
      }
   }
}