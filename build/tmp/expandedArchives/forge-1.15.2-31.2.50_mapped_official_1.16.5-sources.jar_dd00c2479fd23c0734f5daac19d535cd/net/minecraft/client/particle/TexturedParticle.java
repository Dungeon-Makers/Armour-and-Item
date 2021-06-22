package net.minecraft.client.particle;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class TexturedParticle extends Particle {
   protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;

   protected TexturedParticle(World p_i51011_1_, double p_i51011_2_, double p_i51011_4_, double p_i51011_6_) {
      super(p_i51011_1_, p_i51011_2_, p_i51011_4_, p_i51011_6_);
   }

   protected TexturedParticle(World p_i51012_1_, double p_i51012_2_, double p_i51012_4_, double p_i51012_6_, double p_i51012_8_, double p_i51012_10_, double p_i51012_12_) {
      super(p_i51012_1_, p_i51012_2_, p_i51012_4_, p_i51012_6_, p_i51012_8_, p_i51012_10_, p_i51012_12_);
   }

   public void render(IVertexBuilder p_225606_1_, ActiveRenderInfo p_225606_2_, float p_225606_3_) {
      Vec3d vec3d = p_225606_2_.getPosition();
      float f = (float)(MathHelper.lerp((double)p_225606_3_, this.xo, this.x) - vec3d.x());
      float f1 = (float)(MathHelper.lerp((double)p_225606_3_, this.yo, this.y) - vec3d.y());
      float f2 = (float)(MathHelper.lerp((double)p_225606_3_, this.zo, this.z) - vec3d.z());
      Quaternion quaternion;
      if (this.roll == 0.0F) {
         quaternion = p_225606_2_.rotation();
      } else {
         quaternion = new Quaternion(p_225606_2_.rotation());
         float f3 = MathHelper.lerp(p_225606_3_, this.oRoll, this.roll);
         quaternion.mul(Vector3f.ZP.rotation(f3));
      }

      Vector3f vector3f1 = new Vector3f(-1.0F, -1.0F, 0.0F);
      vector3f1.transform(quaternion);
      Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
      float f4 = this.getQuadSize(p_225606_3_);

      for(int i = 0; i < 4; ++i) {
         Vector3f vector3f = avector3f[i];
         vector3f.transform(quaternion);
         vector3f.mul(f4);
         vector3f.add(f, f1, f2);
      }

      float f7 = this.getU0();
      float f8 = this.getU1();
      float f5 = this.getV0();
      float f6 = this.getV1();
      int j = this.getLightColor(p_225606_3_);
      p_225606_1_.vertex((double)avector3f[0].x(), (double)avector3f[0].y(), (double)avector3f[0].z()).uv(f8, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_225606_1_.vertex((double)avector3f[1].x(), (double)avector3f[1].y(), (double)avector3f[1].z()).uv(f8, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_225606_1_.vertex((double)avector3f[2].x(), (double)avector3f[2].y(), (double)avector3f[2].z()).uv(f7, f5).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
      p_225606_1_.vertex((double)avector3f[3].x(), (double)avector3f[3].y(), (double)avector3f[3].z()).uv(f7, f6).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(j).endVertex();
   }

   public float getQuadSize(float p_217561_1_) {
      return this.quadSize;
   }

   public Particle scale(float p_70541_1_) {
      this.quadSize *= p_70541_1_;
      return super.scale(p_70541_1_);
   }

   protected abstract float getU0();

   protected abstract float getU1();

   protected abstract float getV0();

   protected abstract float getV1();
}