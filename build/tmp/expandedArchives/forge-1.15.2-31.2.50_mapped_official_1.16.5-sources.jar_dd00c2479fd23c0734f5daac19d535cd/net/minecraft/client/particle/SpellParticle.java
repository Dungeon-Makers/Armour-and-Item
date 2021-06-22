package net.minecraft.client.particle;

import java.util.Random;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SpellParticle extends SpriteTexturedParticle {
   private static final Random RANDOM = new Random();
   private final IAnimatedSprite sprites;

   private SpellParticle(World p_i51008_1_, double p_i51008_2_, double p_i51008_4_, double p_i51008_6_, double p_i51008_8_, double p_i51008_10_, double p_i51008_12_, IAnimatedSprite p_i51008_14_) {
      super(p_i51008_1_, p_i51008_2_, p_i51008_4_, p_i51008_6_, 0.5D - RANDOM.nextDouble(), p_i51008_10_, 0.5D - RANDOM.nextDouble());
      this.sprites = p_i51008_14_;
      this.yd *= (double)0.2F;
      if (p_i51008_8_ == 0.0D && p_i51008_12_ == 0.0D) {
         this.xd *= (double)0.1F;
         this.zd *= (double)0.1F;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0D / (Math.random() * 0.8D + 0.2D));
      this.hasPhysics = false;
      this.setSpriteFromAge(p_i51008_14_);
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
   public static class AmbientMobFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public AmbientMobFactory(IAnimatedSprite p_i50846_1_) {
         this.sprite = p_i50846_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
         particle.setAlpha(0.15F);
         particle.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50843_1_) {
         this.sprite = p_i50843_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class InstantFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public InstantFactory(IAnimatedSprite p_i50845_1_) {
         this.sprite = p_i50845_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class MobFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public MobFactory(IAnimatedSprite p_i50844_1_) {
         this.sprite = p_i50844_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         Particle particle = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
         particle.setColor((float)p_199234_9_, (float)p_199234_11_, (float)p_199234_13_);
         return particle;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WitchFactory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public WitchFactory(IAnimatedSprite p_i50842_1_) {
         this.sprite = p_i50842_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         SpellParticle spellparticle = new SpellParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_, this.sprite);
         float f = p_199234_2_.random.nextFloat() * 0.5F + 0.35F;
         spellparticle.setColor(1.0F * f, 0.0F * f, 1.0F * f);
         return spellparticle;
      }
   }
}