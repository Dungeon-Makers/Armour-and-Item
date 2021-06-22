package net.minecraft.client.particle;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeBuffers;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ItemPickupParticle extends Particle {
   private final RenderTypeBuffers renderBuffers;
   private final Entity itemEntity;
   private final Entity target;
   private int life;
   private final EntityRendererManager entityRenderDispatcher;

   public ItemPickupParticle(EntityRendererManager p_i225963_1_, RenderTypeBuffers p_i225963_2_, World p_i225963_3_, Entity p_i225963_4_, Entity p_i225963_5_) {
      this(p_i225963_1_, p_i225963_2_, p_i225963_3_, p_i225963_4_, p_i225963_5_, p_i225963_4_.getDeltaMovement());
   }

   private ItemPickupParticle(EntityRendererManager p_i225964_1_, RenderTypeBuffers p_i225964_2_, World p_i225964_3_, Entity p_i225964_4_, Entity p_i225964_5_, Vec3d p_i225964_6_) {
      super(p_i225964_3_, p_i225964_4_.getX(), p_i225964_4_.getY(), p_i225964_4_.getZ(), p_i225964_6_.x, p_i225964_6_.y, p_i225964_6_.z);
      this.renderBuffers = p_i225964_2_;
      this.itemEntity = p_i225964_4_;
      this.target = p_i225964_5_;
      this.entityRenderDispatcher = p_i225964_1_;
   }

   public IParticleRenderType getRenderType() {
      return IParticleRenderType.CUSTOM;
   }

   public void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      float f = ((float)this.life + p_225606_3_) / 3.0F;
      f = f * f;
      double d0 = MathHelper.lerp((double)p_225606_3_, this.target.xOld, this.target.getX());
      double d1 = MathHelper.lerp((double)p_225606_3_, this.target.yOld, this.target.getY()) + 0.5D;
      double d2 = MathHelper.lerp((double)p_225606_3_, this.target.zOld, this.target.getZ());
      double d3 = MathHelper.lerp((double)f, this.itemEntity.getX(), d0);
      double d4 = MathHelper.lerp((double)f, this.itemEntity.getY(), d1);
      double d5 = MathHelper.lerp((double)f, this.itemEntity.getZ(), d2);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = this.renderBuffers.bufferSource();
      Vec3d vec3d = p_225606_2_.getPosition();
      this.entityRenderDispatcher.render(this.itemEntity, d3 - vec3d.x(), d4 - vec3d.y(), d5 - vec3d.z(), this.itemEntity.yRot, p_225606_3_, new MatrixStack(), irendertypebuffer$impl, this.entityRenderDispatcher.getPackedLightCoords(this.itemEntity, p_225606_3_));
      irendertypebuffer$impl.endBatch();
   }

   public void tick() {
      ++this.life;
      if (this.life == 3) {
         this.remove();
      }

   }
}