package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UnderwaterParticle extends SpriteTexturedParticle {
   private UnderwaterParticle(World p_i51001_1_, double p_i51001_2_, double p_i51001_4_, double p_i51001_6_) {
      super(p_i51001_1_, p_i51001_2_, p_i51001_4_ - 0.125D, p_i51001_6_);
      this.rCol = 0.4F;
      this.gCol = 0.4F;
      this.bCol = 0.7F;
      this.setSize(0.01F, 0.01F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.lifetime = (int)(16.0D / (Math.random() * 0.8D + 0.2D));
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         if (!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
         }

      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite field_217548_a;

      public Factory(IAnimatedSprite p_i50567_1_) {
         this.field_217548_a = p_i50567_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         UnderwaterParticle underwaterparticle = new UnderwaterParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_);
         underwaterparticle.pickSprite(this.field_217548_a);
         return underwaterparticle;
      }
   }
}