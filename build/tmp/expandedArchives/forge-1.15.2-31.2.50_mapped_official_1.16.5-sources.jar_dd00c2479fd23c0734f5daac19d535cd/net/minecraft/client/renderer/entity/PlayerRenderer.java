package net.minecraft.client.renderer.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.layers.ArrowLayer;
import net.minecraft.client.renderer.entity.layers.BeeStingerLayer;
import net.minecraft.client.renderer.entity.layers.BipedArmorLayer;
import net.minecraft.client.renderer.entity.layers.CapeLayer;
import net.minecraft.client.renderer.entity.layers.Deadmau5HeadLayer;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.HeadLayer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.ParrotVariantLayer;
import net.minecraft.client.renderer.entity.layers.SpinAttackEffectLayer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerModelPart;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.UseAction;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerRenderer extends LivingRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {
   public PlayerRenderer(EntityRendererManager p_i46102_1_) {
      this(p_i46102_1_, false);
   }

   public PlayerRenderer(EntityRendererManager p_i46103_1_, boolean p_i46103_2_) {
      super(p_i46103_1_, new PlayerModel<>(0.0F, p_i46103_2_), 0.5F);
      this.addLayer(new BipedArmorLayer<>(this, new BipedModel(0.5F), new BipedModel(1.0F)));
      this.addLayer(new HeldItemLayer<>(this));
      this.addLayer(new ArrowLayer<>(this));
      this.addLayer(new Deadmau5HeadLayer(this));
      this.addLayer(new CapeLayer(this));
      this.addLayer(new HeadLayer<>(this));
      this.addLayer(new ElytraLayer<>(this));
      this.addLayer(new ParrotVariantLayer<>(this));
      this.addLayer(new SpinAttackEffectLayer<>(this));
      this.addLayer(new BeeStingerLayer<>(this));
   }

   public void render(AbstractClientPlayerEntity p_225623_1_, float p_225623_2_, float p_225623_3_, MatrixStack p_225623_4_, IRenderTypeBuffer p_225623_5_, int p_225623_6_) {
      this.setModelProperties(p_225623_1_);
      if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Pre(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_))) return;
      super.render(p_225623_1_, p_225623_2_, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.RenderPlayerEvent.Post(p_225623_1_, this, p_225623_3_, p_225623_4_, p_225623_5_, p_225623_6_));
   }

   public Vec3d getRenderOffset(AbstractClientPlayerEntity p_225627_1_, float p_225627_2_) {
      return p_225627_1_.isCrouching() ? new Vec3d(0.0D, -0.125D, 0.0D) : super.getRenderOffset(p_225627_1_, p_225627_2_);
   }

   private void setModelProperties(AbstractClientPlayerEntity p_177137_1_) {
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
      if (p_177137_1_.isSpectator()) {
         playermodel.setAllVisible(false);
         playermodel.head.visible = true;
         playermodel.hat.visible = true;
      } else {
         ItemStack itemstack = p_177137_1_.getMainHandItem();
         ItemStack itemstack1 = p_177137_1_.getOffhandItem();
         playermodel.setAllVisible(true);
         playermodel.hat.visible = p_177137_1_.isModelPartShown(PlayerModelPart.HAT);
         playermodel.jacket.visible = p_177137_1_.isModelPartShown(PlayerModelPart.JACKET);
         playermodel.leftPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_PANTS_LEG);
         playermodel.rightPants.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_PANTS_LEG);
         playermodel.leftSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.LEFT_SLEEVE);
         playermodel.rightSleeve.visible = p_177137_1_.isModelPartShown(PlayerModelPart.RIGHT_SLEEVE);
         playermodel.crouching = p_177137_1_.isCrouching();
         BipedModel.ArmPose bipedmodel$armpose = this.func_217766_a(p_177137_1_, itemstack, itemstack1, Hand.MAIN_HAND);
         BipedModel.ArmPose bipedmodel$armpose1 = this.func_217766_a(p_177137_1_, itemstack, itemstack1, Hand.OFF_HAND);
         if (p_177137_1_.getMainArm() == HandSide.RIGHT) {
            playermodel.rightArmPose = bipedmodel$armpose;
            playermodel.leftArmPose = bipedmodel$armpose1;
         } else {
            playermodel.rightArmPose = bipedmodel$armpose1;
            playermodel.leftArmPose = bipedmodel$armpose;
         }
      }

   }

   private BipedModel.ArmPose func_217766_a(AbstractClientPlayerEntity p_217766_1_, ItemStack p_217766_2_, ItemStack p_217766_3_, Hand p_217766_4_) {
      BipedModel.ArmPose bipedmodel$armpose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = p_217766_4_ == Hand.MAIN_HAND ? p_217766_2_ : p_217766_3_;
      if (!itemstack.isEmpty()) {
         bipedmodel$armpose = BipedModel.ArmPose.ITEM;
         if (p_217766_1_.getUseItemRemainingTicks() > 0) {
            UseAction useaction = itemstack.getUseAnimation();
            if (useaction == UseAction.BLOCK) {
               bipedmodel$armpose = BipedModel.ArmPose.BLOCK;
            } else if (useaction == UseAction.BOW) {
               bipedmodel$armpose = BipedModel.ArmPose.BOW_AND_ARROW;
            } else if (useaction == UseAction.SPEAR) {
               bipedmodel$armpose = BipedModel.ArmPose.THROW_SPEAR;
            } else if (useaction == UseAction.CROSSBOW && p_217766_4_ == p_217766_1_.getUsedItemHand()) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_CHARGE;
            }
         } else {
            boolean flag3 = p_217766_2_.getItem() == Items.CROSSBOW;
            boolean flag = CrossbowItem.isCharged(p_217766_2_);
            boolean flag1 = p_217766_3_.getItem() == Items.CROSSBOW;
            boolean flag2 = CrossbowItem.isCharged(p_217766_3_);
            if (flag3 && flag) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
            }

            if (flag1 && flag2 && p_217766_2_.getItem().getUseAnimation(p_217766_2_) == UseAction.NONE) {
               bipedmodel$armpose = BipedModel.ArmPose.CROSSBOW_HOLD;
            }
         }
      }

      return bipedmodel$armpose;
   }

   public ResourceLocation getTextureLocation(AbstractClientPlayerEntity p_110775_1_) {
      return p_110775_1_.getSkinTextureLocation();
   }

   protected void scale(AbstractClientPlayerEntity p_225620_1_, MatrixStack p_225620_2_, float p_225620_3_) {
      float f = 0.9375F;
      p_225620_2_.scale(0.9375F, 0.9375F, 0.9375F);
   }

   protected void renderNameTag(AbstractClientPlayerEntity p_225629_1_, String p_225629_2_, MatrixStack p_225629_3_, IRenderTypeBuffer p_225629_4_, int p_225629_5_) {
      double d0 = this.entityRenderDispatcher.distanceToSqr(p_225629_1_);
      p_225629_3_.pushPose();
      if (d0 < 100.0D) {
         Scoreboard scoreboard = p_225629_1_.getScoreboard();
         ScoreObjective scoreobjective = scoreboard.getDisplayObjective(2);
         if (scoreobjective != null) {
            Score score = scoreboard.getOrCreatePlayerScore(p_225629_1_.getScoreboardName(), scoreobjective);
            super.renderNameTag(p_225629_1_, score.getScore() + " " + scoreobjective.getDisplayName().func_150254_d(), p_225629_3_, p_225629_4_, p_225629_5_);
            p_225629_3_.translate(0.0D, (double)(9.0F * 1.15F * 0.025F), 0.0D);
         }
      }

      super.renderNameTag(p_225629_1_, p_225629_2_, p_225629_3_, p_225629_4_, p_225629_5_);
      p_225629_3_.popPose();
   }

   public void renderRightHand(MatrixStack p_229144_1_, IRenderTypeBuffer p_229144_2_, int p_229144_3_, AbstractClientPlayerEntity p_229144_4_) {
      this.renderHand(p_229144_1_, p_229144_2_, p_229144_3_, p_229144_4_, (this.model).rightArm, (this.model).rightSleeve);
   }

   public void renderLeftHand(MatrixStack p_229146_1_, IRenderTypeBuffer p_229146_2_, int p_229146_3_, AbstractClientPlayerEntity p_229146_4_) {
      this.renderHand(p_229146_1_, p_229146_2_, p_229146_3_, p_229146_4_, (this.model).leftArm, (this.model).leftSleeve);
   }

   private void renderHand(MatrixStack p_229145_1_, IRenderTypeBuffer p_229145_2_, int p_229145_3_, AbstractClientPlayerEntity p_229145_4_, ModelRenderer p_229145_5_, ModelRenderer p_229145_6_) {
      PlayerModel<AbstractClientPlayerEntity> playermodel = this.getModel();
      this.setModelProperties(p_229145_4_);
      playermodel.attackTime = 0.0F;
      playermodel.crouching = false;
      playermodel.swimAmount = 0.0F;
      playermodel.setupAnim(p_229145_4_, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
      p_229145_5_.xRot = 0.0F;
      p_229145_5_.render(p_229145_1_, p_229145_2_.getBuffer(RenderType.entitySolid(p_229145_4_.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
      p_229145_6_.xRot = 0.0F;
      p_229145_6_.render(p_229145_1_, p_229145_2_.getBuffer(RenderType.entityTranslucent(p_229145_4_.getSkinTextureLocation())), p_229145_3_, OverlayTexture.NO_OVERLAY);
   }

   protected void setupRotations(AbstractClientPlayerEntity p_225621_1_, MatrixStack p_225621_2_, float p_225621_3_, float p_225621_4_, float p_225621_5_) {
      float f = p_225621_1_.getSwimAmount(p_225621_5_);
      if (p_225621_1_.isFallFlying()) {
         super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
         float f1 = (float)p_225621_1_.getFallFlyingTicks() + p_225621_5_;
         float f2 = MathHelper.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
         if (!p_225621_1_.isAutoSpinAttack()) {
            p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(f2 * (-90.0F - p_225621_1_.xRot)));
         }

         Vec3d vec3d = p_225621_1_.getViewVector(p_225621_5_);
         Vec3d vec3d1 = p_225621_1_.getDeltaMovement();
         double d0 = Entity.getHorizontalDistanceSqr(vec3d1);
         double d1 = Entity.getHorizontalDistanceSqr(vec3d);
         if (d0 > 0.0D && d1 > 0.0D) {
            double d2 = (vec3d1.x * vec3d.x + vec3d1.z * vec3d.z) / (Math.sqrt(d0) * Math.sqrt(d1));
            double d3 = vec3d1.x * vec3d.z - vec3d1.z * vec3d.x;
            p_225621_2_.mulPose(Vector3f.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
         }
      } else if (f > 0.0F) {
         super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
         float f3 = p_225621_1_.isInWater() ? -90.0F - p_225621_1_.xRot : -90.0F;
         float f4 = MathHelper.lerp(f, 0.0F, f3);
         p_225621_2_.mulPose(Vector3f.XP.rotationDegrees(f4));
         if (p_225621_1_.isVisuallySwimming()) {
            p_225621_2_.translate(0.0D, -1.0D, (double)0.3F);
         }
      } else {
         super.setupRotations(p_225621_1_, p_225621_2_, p_225621_3_, p_225621_4_, p_225621_5_);
      }

   }
}
