package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HugeExplosionParticle extends MetaParticle {
   private int life;
   private final int lifeTime = 8;

   private HugeExplosionParticle(World p_i51026_1_, double p_i51026_2_, double p_i51026_4_, double p_i51026_6_) {
      super(p_i51026_1_, p_i51026_2_, p_i51026_4_, p_i51026_6_, 0.0D, 0.0D, 0.0D);
   }

   public void tick() {
      for(int i = 0; i < 6; ++i) {
         double d0 = this.x + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double d1 = this.y + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         double d2 = this.z + (this.random.nextDouble() - this.random.nextDouble()) * 4.0D;
         this.level.addParticle(ParticleTypes.EXPLOSION, d0, d1, d2, (double)((float)this.life / (float)this.lifeTime), 0.0D, 0.0D);
      }

      ++this.life;
      if (this.life == this.lifeTime) {
         this.remove();
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         return new HugeExplosionParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
      }
   }
}