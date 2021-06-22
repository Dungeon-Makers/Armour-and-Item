package net.minecraft.client.renderer.model;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BakedQuad implements net.minecraftforge.client.model.pipeline.IVertexProducer {
   protected final int[] vertices;
   protected final int tintIndex;
   protected final Direction direction;
   protected final TextureAtlasSprite sprite;

   /**
    * @deprecated Use constructor with the format argument.
    */
   @Deprecated
   public BakedQuad(int[] p_i46574_1_, int p_i46574_2_, Direction p_i46574_3_, TextureAtlasSprite p_i46574_4_) {
      this(p_i46574_1_, p_i46574_2_, p_i46574_3_, p_i46574_4_, true);
   }

   public BakedQuad(int[] p_i46574_1_, int p_i46574_2_, Direction p_i46574_3_, TextureAtlasSprite p_i46574_4_, boolean applyDiffuseLighting) {
      this.applyDiffuseLighting = applyDiffuseLighting;
      this.vertices = p_i46574_1_;
      this.tintIndex = p_i46574_2_;
      this.direction = p_i46574_3_;
      this.sprite = p_i46574_4_;
   }

   public int[] getVertices() {
      return this.vertices;
   }

   public boolean isTinted() {
      return this.tintIndex != -1;
   }

   public int getTintIndex() {
      return this.tintIndex;
   }

   public Direction getDirection() {
      return this.direction;
   }

   // Forge start
   protected final boolean applyDiffuseLighting;

   @Override
   public void pipe(net.minecraftforge.client.model.pipeline.IVertexConsumer consumer) {
      net.minecraftforge.client.model.pipeline.LightUtil.putBakedQuad(consumer, this);
   }

   public TextureAtlasSprite getSprite() {
      return sprite;
   }

   public boolean shouldApplyDiffuseLighting() {
       return applyDiffuseLighting;
   }
}
