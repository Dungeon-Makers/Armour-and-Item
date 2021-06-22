package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SmokeParticle extends SpriteTexturedParticle {
   private final IAnimatedSprite field_217585_C;

   protected SmokeParticle(World p_i51010_1_, double p_i51010_2_, double p_i51010_4_, double p_i51010_6_, double p_i51010_8_, double p_i51010_10_, double p_i51010_12_, float p_i51010_14_, IAnimatedSprite p_i51010_15_) {
      super(p_i51010_1_, p_i51010_2_, p_i51010_4_, p_i51010_6_, 0.0D, 0.0D, 0.0D);
      this.field_217585_C = p_i51010_15_;
      this.xd *= (double)0.1F;
      this.yd *= (double)0.1F;
      this.zd *= (double)0.1F;
      this.xd += p_i51010_8_;
      this.yd += p_i51010_10_;
      this.zd += p_i51010_12_;
      float f = (float)(Math.random() * (double)0.3F);
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize *= 0.75F * p_i51010_14_;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.lifetime = (int)((float)this.lifetime * p_i51010_14_);
      this.lifetime = Math.max(this.lifetime, 1);
      this.setSpriteFromAge(p_i51010_15_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public float getQuadSize(float p_217561_1_) {
      return this.quadSize * MathHelper.clamp(((float)this.age + p_217561_1_) / (float)this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.field_217585_C);
         this.yd += 0.004D;
         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1D;
            this.zd *= 1.1D;
         }

         this.xd *= (double)0.96F;
         this.yd *= (double)0.96F;
         this.zd *= (double)0.96F;
         if (this.onGround) {
            this.xd *= (double)0.7F;
            this.zd *= (double)0.7F;
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprites;

      public Factory(IAnimatedSprite p_i51045_1_) {
         this.sprites = p_i51045_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SmokeParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, 1.0F, this.sprites);
      }
   }
}