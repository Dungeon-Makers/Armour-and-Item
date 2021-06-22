package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.PigModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SaddleLayer extends LayerRenderer<PigEntity, PigModel<PigEntity>> {
   private static final ResourceLocation field_177158_a = new ResourceLocation("textures/entity/pig/pig_saddle.png");
   private final PigModel<PigEntity> field_177157_c = new PigModel<>(0.5F);

   public SaddleLayer(IEntityRenderer<PigEntity, PigModel<PigEntity>> p_i50927_1_) {
      super(p_i50927_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, PigEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      if (p_225628_4_.func_70901_n()) {
         this.getParentModel().copyPropertiesTo(this.field_177157_c);
         this.field_177157_c.prepareMobModel(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_);
         this.field_177157_c.setupAnim(p_225628_4_, p_225628_5_, p_225628_6_, p_225628_8_, p_225628_9_, p_225628_10_);
         IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entityCutoutNoCull(field_177158_a));
         this.field_177157_c.renderToBuffer(p_225628_1_, ivertexbuilder, p_225628_3_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      }
   }
}