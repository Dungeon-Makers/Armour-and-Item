package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedModel<T extends LivingEntity> extends AgeableModel<T> implements IHasArm, IHasHead {
   public ModelRenderer head;
   public ModelRenderer hat;
   public ModelRenderer body;
   public ModelRenderer rightArm;
   public ModelRenderer leftArm;
   public ModelRenderer rightLeg;
   public ModelRenderer leftLeg;
   public BipedModel.ArmPose leftArmPose = BipedModel.ArmPose.EMPTY;
   public BipedModel.ArmPose rightArmPose = BipedModel.ArmPose.EMPTY;
   public boolean crouching;
   public float swimAmount;
   private float field_217149_a;

   public BipedModel(float p_i1148_1_) {
      this(RenderType::entityCutoutNoCull, p_i1148_1_, 0.0F, 64, 32);
   }

   protected BipedModel(float p_i1149_1_, float p_i1149_2_, int p_i1149_3_, int p_i1149_4_) {
      this(RenderType::entityCutoutNoCull, p_i1149_1_, p_i1149_2_, p_i1149_3_, p_i1149_4_);
   }

   public BipedModel(Function<ResourceLocation, RenderType> p_i225946_1_, float p_i225946_2_, float p_i225946_3_, int p_i225946_4_, int p_i225946_5_) {
      super(p_i225946_1_, true, 16.0F, 0.0F, 2.0F, 2.0F, 24.0F);
      this.texWidth = p_i225946_4_;
      this.texHeight = p_i225946_5_;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i225946_2_);
      this.head.setPos(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.hat = new ModelRenderer(this, 32, 0);
      this.hat.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, p_i225946_2_ + 0.5F);
      this.hat.setPos(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.body = new ModelRenderer(this, 16, 16);
      this.body.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, p_i225946_2_);
      this.body.setPos(0.0F, 0.0F + p_i225946_3_, 0.0F);
      this.rightArm = new ModelRenderer(this, 40, 16);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.rightArm.setPos(-5.0F, 2.0F + p_i225946_3_, 0.0F);
      this.leftArm = new ModelRenderer(this, 40, 16);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.leftArm.setPos(5.0F, 2.0F + p_i225946_3_, 0.0F);
      this.rightLeg = new ModelRenderer(this, 0, 16);
      this.rightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.rightLeg.setPos(-1.9F, 12.0F + p_i225946_3_, 0.0F);
      this.leftLeg = new ModelRenderer(this, 0, 16);
      this.leftLeg.mirror = true;
      this.leftLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i225946_2_);
      this.leftLeg.setPos(1.9F, 12.0F + p_i225946_3_, 0.0F);
   }

   protected Iterable<ModelRenderer> headParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> bodyParts() {
      return ImmutableList.of(this.body, this.rightArm, this.leftArm, this.rightLeg, this.leftLeg, this.hat);
   }

   public void prepareMobModel(T p_212843_1_, float p_212843_2_, float p_212843_3_, float p_212843_4_) {
      this.swimAmount = p_212843_1_.getSwimAmount(p_212843_4_);
      this.field_217149_a = (float)p_212843_1_.getTicksUsingItem();
      super.prepareMobModel(p_212843_1_, p_212843_2_, p_212843_3_, p_212843_4_);
   }

   public void setupAnim(T p_225597_1_, float p_225597_2_, float p_225597_3_, float p_225597_4_, float p_225597_5_, float p_225597_6_) {
      boolean flag = p_225597_1_.getFallFlyingTicks() > 4;
      boolean flag1 = p_225597_1_.isVisuallySwimming();
      this.head.yRot = p_225597_5_ * ((float)Math.PI / 180F);
      if (flag) {
         this.head.xRot = (-(float)Math.PI / 4F);
      } else if (this.swimAmount > 0.0F) {
         if (flag1) {
            this.head.xRot = this.rotlerpRad(this.head.xRot, (-(float)Math.PI / 4F), this.swimAmount);
         } else {
            this.head.xRot = this.rotlerpRad(this.head.xRot, p_225597_6_ * ((float)Math.PI / 180F), this.swimAmount);
         }
      } else {
         this.head.xRot = p_225597_6_ * ((float)Math.PI / 180F);
      }

      this.body.yRot = 0.0F;
      this.rightArm.z = 0.0F;
      this.rightArm.x = -5.0F;
      this.leftArm.z = 0.0F;
      this.leftArm.x = 5.0F;
      float f = 1.0F;
      if (flag) {
         f = (float)p_225597_1_.getDeltaMovement().lengthSqr();
         f = f / 0.2F;
         f = f * f * f;
      }

      if (f < 1.0F) {
         f = 1.0F;
      }

      this.rightArm.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * 2.0F * p_225597_3_ * 0.5F / f;
      this.leftArm.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * 2.0F * p_225597_3_ * 0.5F / f;
      this.rightArm.zRot = 0.0F;
      this.leftArm.zRot = 0.0F;
      this.rightLeg.xRot = MathHelper.cos(p_225597_2_ * 0.6662F) * 1.4F * p_225597_3_ / f;
      this.leftLeg.xRot = MathHelper.cos(p_225597_2_ * 0.6662F + (float)Math.PI) * 1.4F * p_225597_3_ / f;
      this.rightLeg.yRot = 0.0F;
      this.leftLeg.yRot = 0.0F;
      this.rightLeg.zRot = 0.0F;
      this.leftLeg.zRot = 0.0F;
      if (this.riding) {
         this.rightArm.xRot += (-(float)Math.PI / 5F);
         this.leftArm.xRot += (-(float)Math.PI / 5F);
         this.rightLeg.xRot = -1.4137167F;
         this.rightLeg.yRot = ((float)Math.PI / 10F);
         this.rightLeg.zRot = 0.07853982F;
         this.leftLeg.xRot = -1.4137167F;
         this.leftLeg.yRot = (-(float)Math.PI / 10F);
         this.leftLeg.zRot = -0.07853982F;
      }

      this.rightArm.yRot = 0.0F;
      this.rightArm.zRot = 0.0F;
      switch(this.leftArmPose) {
      case EMPTY:
         this.leftArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - 0.9424779F;
         this.leftArm.yRot = ((float)Math.PI / 6F);
         break;
      case ITEM:
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - ((float)Math.PI / 10F);
         this.leftArm.yRot = 0.0F;
      }

      switch(this.rightArmPose) {
      case EMPTY:
         this.rightArm.yRot = 0.0F;
         break;
      case BLOCK:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - 0.9424779F;
         this.rightArm.yRot = (-(float)Math.PI / 6F);
         break;
      case ITEM:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - ((float)Math.PI / 10F);
         this.rightArm.yRot = 0.0F;
         break;
      case THROW_SPEAR:
         this.rightArm.xRot = this.rightArm.xRot * 0.5F - (float)Math.PI;
         this.rightArm.yRot = 0.0F;
      }

      if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BOW_AND_ARROW) {
         this.leftArm.xRot = this.leftArm.xRot * 0.5F - (float)Math.PI;
         this.leftArm.yRot = 0.0F;
      }

      if (this.attackTime > 0.0F) {
         HandSide handside = this.getAttackArm(p_225597_1_);
         ModelRenderer modelrenderer = this.getArm(handside);
         float f1 = this.attackTime;
         this.body.yRot = MathHelper.sin(MathHelper.sqrt(f1) * ((float)Math.PI * 2F)) * 0.2F;
         if (handside == HandSide.LEFT) {
            this.body.yRot *= -1.0F;
         }

         this.rightArm.z = MathHelper.sin(this.body.yRot) * 5.0F;
         this.rightArm.x = -MathHelper.cos(this.body.yRot) * 5.0F;
         this.leftArm.z = -MathHelper.sin(this.body.yRot) * 5.0F;
         this.leftArm.x = MathHelper.cos(this.body.yRot) * 5.0F;
         this.rightArm.yRot += this.body.yRot;
         this.leftArm.yRot += this.body.yRot;
         this.leftArm.xRot += this.body.yRot;
         f1 = 1.0F - this.attackTime;
         f1 = f1 * f1;
         f1 = f1 * f1;
         f1 = 1.0F - f1;
         float f2 = MathHelper.sin(f1 * (float)Math.PI);
         float f3 = MathHelper.sin(this.attackTime * (float)Math.PI) * -(this.head.xRot - 0.7F) * 0.75F;
         modelrenderer.xRot = (float)((double)modelrenderer.xRot - ((double)f2 * 1.2D + (double)f3));
         modelrenderer.yRot += this.body.yRot * 2.0F;
         modelrenderer.zRot += MathHelper.sin(this.attackTime * (float)Math.PI) * -0.4F;
      }

      if (this.crouching) {
         this.body.xRot = 0.5F;
         this.rightArm.xRot += 0.4F;
         this.leftArm.xRot += 0.4F;
         this.rightLeg.z = 4.0F;
         this.leftLeg.z = 4.0F;
         this.rightLeg.y = 12.2F;
         this.leftLeg.y = 12.2F;
         this.head.y = 4.2F;
         this.body.y = 3.2F;
         this.leftArm.y = 5.2F;
         this.rightArm.y = 5.2F;
      } else {
         this.body.xRot = 0.0F;
         this.rightLeg.z = 0.1F;
         this.leftLeg.z = 0.1F;
         this.rightLeg.y = 12.0F;
         this.leftLeg.y = 12.0F;
         this.head.y = 0.0F;
         this.body.y = 0.0F;
         this.leftArm.y = 2.0F;
         this.rightArm.y = 2.0F;
      }

      this.rightArm.zRot += MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      this.leftArm.zRot -= MathHelper.cos(p_225597_4_ * 0.09F) * 0.05F + 0.05F;
      this.rightArm.xRot += MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      this.leftArm.xRot -= MathHelper.sin(p_225597_4_ * 0.067F) * 0.05F;
      if (this.rightArmPose == BipedModel.ArmPose.BOW_AND_ARROW) {
         this.rightArm.yRot = -0.1F + this.head.yRot;
         this.leftArm.yRot = 0.1F + this.head.yRot + 0.4F;
         this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
      } else if (this.leftArmPose == BipedModel.ArmPose.BOW_AND_ARROW && this.rightArmPose != BipedModel.ArmPose.THROW_SPEAR && this.rightArmPose != BipedModel.ArmPose.BLOCK) {
         this.rightArm.yRot = -0.1F + this.head.yRot - 0.4F;
         this.leftArm.yRot = 0.1F + this.head.yRot;
         this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
         this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot;
      }

      float f4 = (float)CrossbowItem.getChargeDuration(p_225597_1_.getUseItem());
      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.rightArm.yRot = -0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         float f5 = MathHelper.clamp(this.field_217149_a, 0.0F, f4);
         this.leftArm.yRot = MathHelper.lerp(f5 / f4, 0.4F, 0.85F);
         this.leftArm.xRot = MathHelper.lerp(f5 / f4, this.leftArm.xRot, (-(float)Math.PI / 2F));
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_CHARGE) {
         this.leftArm.yRot = 0.8F;
         this.rightArm.xRot = -0.97079635F;
         this.leftArm.xRot = -0.97079635F;
         float f6 = MathHelper.clamp(this.field_217149_a, 0.0F, f4);
         this.rightArm.yRot = MathHelper.lerp(f6 / f4, -0.4F, -0.85F);
         this.rightArm.xRot = MathHelper.lerp(f6 / f4, this.rightArm.xRot, (-(float)Math.PI / 2F));
      }

      if (this.rightArmPose == BipedModel.ArmPose.CROSSBOW_HOLD && this.attackTime <= 0.0F) {
         this.rightArm.yRot = -0.3F + this.head.yRot;
         this.leftArm.yRot = 0.6F + this.head.yRot;
         this.rightArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot + 0.1F;
         this.leftArm.xRot = -1.5F + this.head.xRot;
      } else if (this.leftArmPose == BipedModel.ArmPose.CROSSBOW_HOLD) {
         this.rightArm.yRot = -0.6F + this.head.yRot;
         this.leftArm.yRot = 0.3F + this.head.yRot;
         this.rightArm.xRot = -1.5F + this.head.xRot;
         this.leftArm.xRot = (-(float)Math.PI / 2F) + this.head.xRot + 0.1F;
      }

      if (this.swimAmount > 0.0F) {
         float f7 = p_225597_2_ % 26.0F;
         float f8 = this.attackTime > 0.0F ? 0.0F : this.swimAmount;
         if (f7 < 14.0F) {
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, 0.0F, this.swimAmount);
            this.rightArm.xRot = MathHelper.lerp(f8, this.rightArm.xRot, 0.0F);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, (float)Math.PI, this.swimAmount);
            this.rightArm.yRot = MathHelper.lerp(f8, this.rightArm.yRot, (float)Math.PI);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, (float)Math.PI + 1.8707964F * this.quadraticArmUpdate(f7) / this.quadraticArmUpdate(14.0F), this.swimAmount);
            this.rightArm.zRot = MathHelper.lerp(f8, this.rightArm.zRot, (float)Math.PI - 1.8707964F * this.quadraticArmUpdate(f7) / this.quadraticArmUpdate(14.0F));
         } else if (f7 >= 14.0F && f7 < 22.0F) {
            float f10 = (f7 - 14.0F) / 8.0F;
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, ((float)Math.PI / 2F) * f10, this.swimAmount);
            this.rightArm.xRot = MathHelper.lerp(f8, this.rightArm.xRot, ((float)Math.PI / 2F) * f10);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, (float)Math.PI, this.swimAmount);
            this.rightArm.yRot = MathHelper.lerp(f8, this.rightArm.yRot, (float)Math.PI);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, 5.012389F - 1.8707964F * f10, this.swimAmount);
            this.rightArm.zRot = MathHelper.lerp(f8, this.rightArm.zRot, 1.2707963F + 1.8707964F * f10);
         } else if (f7 >= 22.0F && f7 < 26.0F) {
            float f9 = (f7 - 22.0F) / 4.0F;
            this.leftArm.xRot = this.rotlerpRad(this.leftArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9, this.swimAmount);
            this.rightArm.xRot = MathHelper.lerp(f8, this.rightArm.xRot, ((float)Math.PI / 2F) - ((float)Math.PI / 2F) * f9);
            this.leftArm.yRot = this.rotlerpRad(this.leftArm.yRot, (float)Math.PI, this.swimAmount);
            this.rightArm.yRot = MathHelper.lerp(f8, this.rightArm.yRot, (float)Math.PI);
            this.leftArm.zRot = this.rotlerpRad(this.leftArm.zRot, (float)Math.PI, this.swimAmount);
            this.rightArm.zRot = MathHelper.lerp(f8, this.rightArm.zRot, (float)Math.PI);
         }

         float f11 = 0.3F;
         float f12 = 0.33333334F;
         this.leftLeg.xRot = MathHelper.lerp(this.swimAmount, this.leftLeg.xRot, 0.3F * MathHelper.cos(p_225597_2_ * 0.33333334F + (float)Math.PI));
         this.rightLeg.xRot = MathHelper.lerp(this.swimAmount, this.rightLeg.xRot, 0.3F * MathHelper.cos(p_225597_2_ * 0.33333334F));
      }

      this.hat.copyFrom(this.head);
   }

   protected float rotlerpRad(float p_205060_1_, float p_205060_2_, float p_205060_3_) {
      float f = (p_205060_2_ - p_205060_1_) % ((float)Math.PI * 2F);
      if (f < -(float)Math.PI) {
         f += ((float)Math.PI * 2F);
      }

      if (f >= (float)Math.PI) {
         f -= ((float)Math.PI * 2F);
      }

      return p_205060_1_ + p_205060_3_ * f;
   }

   private float quadraticArmUpdate(float p_203068_1_) {
      return -65.0F * p_203068_1_ + p_203068_1_ * p_203068_1_;
   }

   public void copyPropertiesTo(BipedModel<T> p_217148_1_) {
      super.copyPropertiesTo(p_217148_1_);
      p_217148_1_.leftArmPose = this.leftArmPose;
      p_217148_1_.rightArmPose = this.rightArmPose;
      p_217148_1_.crouching = this.crouching;
   }

   public void setAllVisible(boolean p_178719_1_) {
      this.head.visible = p_178719_1_;
      this.hat.visible = p_178719_1_;
      this.body.visible = p_178719_1_;
      this.rightArm.visible = p_178719_1_;
      this.leftArm.visible = p_178719_1_;
      this.rightLeg.visible = p_178719_1_;
      this.leftLeg.visible = p_178719_1_;
   }

   public void translateToHand(HandSide p_225599_1_, MatrixStack p_225599_2_) {
      this.getArm(p_225599_1_).translateAndRotate(p_225599_2_);
   }

   protected ModelRenderer getArm(HandSide p_187074_1_) {
      return p_187074_1_ == HandSide.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelRenderer getHead() {
      return this.head;
   }

   protected HandSide getAttackArm(T p_217147_1_) {
      HandSide handside = p_217147_1_.getMainArm();
      return p_217147_1_.swingingArm == Hand.MAIN_HAND ? handside : handside.getOpposite();
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      EMPTY,
      ITEM,
      BLOCK,
      BOW_AND_ARROW,
      THROW_SPEAR,
      CROSSBOW_CHARGE,
      CROSSBOW_HOLD;
   }
}