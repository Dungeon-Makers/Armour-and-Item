package net.minecraft.client.particle;

import net.minecraft.particles.BasicParticleType;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BubbleColumnUpParticle extends SpriteTexturedParticle {
   private BubbleColumnUpParticle(World p_i48833_1_, double p_i48833_2_, double p_i48833_4_, double p_i48833_6_, double p_i48833_8_, double p_i48833_10_, double p_i48833_12_) {
      super(p_i48833_1_, p_i48833_2_, p_i48833_4_, p_i48833_6_);
      this.setSize(0.02F, 0.02F);
      this.quadSize *= this.random.nextFloat() * 0.6F + 0.2F;
      this.xd = p_i48833_8_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.yd = p_i48833_10_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.zd = p_i48833_12_ * (double)0.2F + (Math.random() * 2.0D - 1.0D) * (double)0.02F;
      this.lifetime = (int)(40.0D / (Math.random() * 0.8D + 0.2D));
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.yd += 0.005D;
      if (this.lifetime-- <= 0) {
         this.remove();
      } else {
         this.move(this.xd, this.yd, this.zd);
         this.xd *= (double)0.85F;
         this.yd *= (double)0.85F;
         this.zd *= (double)0.85F;
         if (!this.level.getFluidState(new BlockPos(this.x, this.y, this.z)).is(FluidTags.WATER)) {
            this.remove();
         }

      }
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i50448_1_) {
         this.sprite = p_i50448_1_;
      }

      public Particle createParticle(BasicParticleType p_199234_1_, World p_199234_2_, double p_199234_3_, double p_199234_5_, double p_199234_7_, double p_199234_9_, double p_199234_11_, double p_199234_13_) {
         BubbleColumnUpParticle bubblecolumnupparticle = new BubbleColumnUpParticle(p_199234_2_, p_199234_3_, p_199234_5_, p_199234_7_, p_199234_9_, p_199234_11_, p_199234_13_);
         bubblecolumnupparticle.pickSprite(this.sprite);
         return bubblecolumnupparticle;
      }
   }
}