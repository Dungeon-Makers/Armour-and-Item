package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.entity.model.MinecartModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MinecartRenderer<T extends AbstractMinecartEntity> extends EntityRenderer<T> {
   private static final ResourceLocation MINECART_LOCATION = new ResourceLocation("textures/entity/minecart.png");
   protected final EntityModel<T> model = new MinecartModel<>();

   public MinecartRenderer(EntityRendererManager p_i46155_1_) {
      super(p_i46155_1_);
      this.shadowRadius = 0.7F;
   }

   public void render(T p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      p_225623_4_.pushPose();
      long i = (long)p_225623_1_.getId() * 493286711L;
      i = i * i * 4392167121L + i * 98761L;
      float f = (((float)(i >> 16 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f1 = (((float)(i >> 20 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      float f2 = (((float)(i >> 24 & 7L) + 0.5F) / 8.0F - 0.5F) * 0.004F;
      p_225623_4_.translate((double)f, (double)f1, (double)f2);
      double d0 = MathHelper.lerp((double)p_225623_3_, p_225623_1_.xOld, p_225623_1_.getX());
      double d1 = MathHelper.lerp((double)p_225623_3_, p_225623_1_.yOld, p_225623_1_.getY());
      double d2 = MathHelper.lerp((double)p_225623_3_, p_225623_1_.zOld, p_225623_1_.getZ());
      double d3 = (double)0.3F;
      Vec3d vec3d = p_225623_1_.getPos(d0, d1, d2);
      float f3 = MathHelper.lerp(p_225623_3_, p_225623_1_.xRotO, p_225623_1_.xRot);
      if (vec3d != null) {
         Vec3d vec3d1 = p_225623_1_.getPosOffs(d0, d1, d2, (double)0.3F);
         Vec3d vec3d2 = p_225623_1_.getPosOffs(d0, d1, d2, (double)-0.3F);
         if (vec3d1 == null) {
            vec3d1 = vec3d;
         }

         if (vec3d2 == null) {
            vec3d2 = vec3d;
         }

         p_225623_4_.translate(vec3d.x - d0, (vec3d1.y + vec3d2.y) / 2.0D - d1, vec3d.z - d2);
         Vec3d vec3d3 = vec3d2.add(-vec3d1.x, -vec3d1.y, -vec3d1.z);
         if (vec3d3.length() != 0.0D) {
            vec3d3 = vec3d3.normalize();
            p_225623_2_ = (float)(Math.atan2(vec3d3.z, vec3d3.x) * 180.0D / Math.PI);
            f3 = (float)(Math.atan(vec3d3.y) * 73.0D);
         }
      }

      p_225623_4_.translate(0.0D, 0.375D, 0.0D);
      p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(180.0F - p_225623_2_));
      p_225623_4_.mulPose(Vector3f.ZP.rotationDegrees(-f3));
      float f5 = (float)p_225623_1_.getHurtTime() - p_225623_3_;
      float f6 = p_225623_1_.getDamage() - p_225623_3_;
      if (f6 < 0.0F) {
         f6 = 0.0F;
      }

      if (f5 > 0.0F) {
         p_225623_4_.mulPose(Vector3f.XP.rotationDegrees(MathHelper.sin(f5) * f5 * f6 / 10.0F * (float)p_225623_1_.getHurtDir()));
      }

      int j = p_225623_1_.getDisplayOffset();
      BlockState blockstate = p_225623_1_.getDisplayBlockState();
      if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
         p_225623_4_.pushPose();
         float f4 = 0.75F;
         p_225623_4_.scale(0.75F, 0.75F, 0.75F);
         p_225623_4_.translate(-0.5D, (double)((float)(j - 8) / 16.0F), 0.5D);
         p_225623_4_.mulPose(Vector3f.YP.rotationDegrees(90.0F));
         this.renderMinecartContents(p_225623_1_, p_225623_3_, blockstate, p_225623_4_, p_225623_5_, p_225623_6_);
         p_225623_4_.popPose();
      }

      p_225623_4_.scale(-1.0F, -1.0F, 1.0F);
      this.model.setupAnim(p_225623_1_, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F);
      IVertexBuilder ivertexbuilder = p_225623_5_.getBuffer(this.model.renderType(this.getTextureLocation(p_225623_1_)));
      this.model.renderToBuffer(p_225623_4_, ivertexbuilder, p_225623_6_, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
      p_225623_4_.popPose();
   }

   public ResourceLocation getTextureLocation(T p_110775_1_) {
      return MINECART_LOCATION;
   }

   protected void renderMinecartContents(T p_225630_1_, float p_225630_2_, BlockState p_225630_3_, MatrixStack p_225630_4_, IRenderTypeBuffer p_225630_5_, int p_225630_6_) {
      Minecraft.getInstance().getBlockRenderer().renderSingleBlock(p_225630_3_, p_225630_4_, p_225630_5_, p_225630_6_, OverlayTexture.NO_OVERLAY);
   }
}