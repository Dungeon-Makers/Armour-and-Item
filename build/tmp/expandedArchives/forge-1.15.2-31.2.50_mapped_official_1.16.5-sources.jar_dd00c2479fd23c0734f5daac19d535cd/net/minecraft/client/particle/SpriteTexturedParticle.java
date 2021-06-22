package net.minecraft.client.particle;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class SpriteTexturedParticle extends TexturedParticle {
   protected TextureAtlasSprite sprite;

   protected SpriteTexturedParticle(World p_i50998_1_, double p_i50998_2_, double p_i50998_4_, double p_i50998_6_) {
      super(p_i50998_1_, p_i50998_2_, p_i50998_4_, p_i50998_6_);
   }

   protected SpriteTexturedParticle(World p_i50999_1_, double p_i50999_2_, double p_i50999_4_, double p_i50999_6_, double p_i50999_8_, double p_i50999_10_, double p_i50999_12_) {
      super(p_i50999_1_, p_i50999_2_, p_i50999_4_, p_i50999_6_, p_i50999_8_, p_i50999_10_, p_i50999_12_);
   }

   protected void setSprite(TextureAtlasSprite p_217567_1_) {
      this.sprite = p_217567_1_;
   }

   protected float getU0() {
      return this.sprite.getU0();
   }

   protected float getU1() {
      return this.sprite.getU1();
   }

   protected float getV0() {
      return this.sprite.getV0();
   }

   protected float getV1() {
      return this.sprite.getV1();
   }

   public void pickSprite(IAnimatedSprite p_217568_1_) {
      this.setSprite(p_217568_1_.get(this.random));
   }

   public void setSpriteFromAge(IAnimatedSprite p_217566_1_) {
      this.setSprite(p_217566_1_.get(this.age, this.lifetime));
   }
}