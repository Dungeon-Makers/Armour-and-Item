package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieModel<T extends MonsterEntity> extends BipedModel<T> {
   protected AbstractZombieModel(float p_i51070_1_, float p_i51070_2_, int p_i51070_3_, int p_i51070_4_) {
      super(p_i51070_1_, p_i51070_2_, p_i51070_3_, p_i51070_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      super.setupAnim(p_225597_1_, p_225597_2_, p_225597_3_, p_225597_4_, p_225597_5_, p_225597_6_);
      boolean flag = this.isAggressive(p_225597_1_);
      float f = MathHelper.sin(this.attackTime * (float)Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - this.attackTime) * (1.0F - this.attackTime)) * (float)Math.PI);
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightArm.yRot = -(0.1F - f * 0.6F);
      this.leftArm.yRot = 0.1F - f * 0.6F;
      float f2 = -(float)Math.PI / (flag ? 1.5F : 2.25F);
      this.rightArm.xRot = f2;
      this.leftArm.xRot = f2;
      this.rightArm.xRot += f * 1.2F - f1 * 0.4F;
      this.leftArm.xRot += f * 1.2F - f1 * 0.4F;
      this.rightArm.zRot += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      this.leftArm.zRot -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      this.rightArm.xRot += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      this.leftArm.xRot -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
   }

   public abstract boolean isAggressive(T p_212850_1_);
}