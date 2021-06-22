package net.minecraft.client.renderer.entity.layers;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.ShulkerRenderer;
import net.minecraft.client.renderer.entity.model.ShulkerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ShulkerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShulkerColorLayer extends LayerRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> {
   public ShulkerColorLayer(IEntityRenderer<ShulkerEntity, ShulkerModel<ShulkerEntity>> p_i50924_1_) {
      super(p_i50924_1_);
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, ShulkerEntity p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      p_225628_1_.pushPose();
      p_225628_1_.translate(0.0D, 1.0D, 0.0D);
      p_225628_1_.scale(-1.0F, -1.0F, 1.0F);
      Quaternion quaternion = p_225628_4_.getAttachFace().getOpposite().getRotation();
      quaternion.conj();
      p_225628_1_.mulPose(quaternion);
      p_225628_1_.scale(-1.0F, -1.0F, 1.0F);
      p_225628_1_.translate(0.0D, -1.0D, 0.0D);
      ModelRenderer modelrenderer = this.getParentModel().getHead();
      modelrenderer.yRot = p_225628_9_ * ((float)Math.PI / 180F);
      modelrenderer.xRot = p_225628_10_ * ((float)Math.PI / 180F);
      DyeColor dyecolor = p_225628_4_.getColor();
      ResourceLocation resourcelocation;
      if (dyecolor == null) {
         resourcelocation = ShulkerRenderer.DEFAULT_TEXTURE_LOCATION;
      } else {
         resourcelocation = ShulkerRenderer.TEXTURE_LOCATION[dyecolor.getId()];
      }

      IVertexBuilder ivertexbuilder = p_225628_2_.getBuffer(RenderType.entitySolid(resourcelocation));
      modelrenderer.render(p_225628_1_, ivertexbuilder, p_225628_3_, LivingRenderer.getOverlayCoords(p_225628_4_, 0.0F));
      p_225628_1_.popPose();
   }
}