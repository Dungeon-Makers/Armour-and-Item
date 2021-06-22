package net.minecraft.client.renderer;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FogRenderer {
   private static float fogRed;
   private static float fogGreen;
   private static float fogBlue;
   private static int targetBiomeFog = -1;
   private static int previousBiomeFog = -1;
   private static long biomeChangedTime = -1L;

   public static void setupColor(ActiveRenderInfo p_228371_0_, float p_228371_1_, ClientWorld p_228371_2_, int p_228371_3_, float p_228371_4_) {
      IFluidState ifluidstate = p_228371_0_.getFluidInCamera();
      if (ifluidstate.is(FluidTags.WATER)) {
         long i = Util.getMillis();
         int j = p_228371_2_.getBiome(new BlockPos(p_228371_0_.getPosition())).getWaterFogColor();
         if (biomeChangedTime < 0L) {
            targetBiomeFog = j;
            previousBiomeFog = j;
            biomeChangedTime = i;
         }

         int k = targetBiomeFog >> 16 & 255;
         int l = targetBiomeFog >> 8 & 255;
         int i1 = targetBiomeFog & 255;
         int j1 = previousBiomeFog >> 16 & 255;
         int k1 = previousBiomeFog >> 8 & 255;
         int l1 = previousBiomeFog & 255;
         float f = MathHelper.clamp((float)(i - biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
         float f1 = MathHelper.lerp(f, (float)j1, (float)k);
         float f2 = MathHelper.lerp(f, (float)k1, (float)l);
         float f3 = MathHelper.lerp(f, (float)l1, (float)i1);
         fogRed = f1 / 255.0F;
         fogGreen = f2 / 255.0F;
         fogBlue = f3 / 255.0F;
         if (targetBiomeFog != j) {
            targetBiomeFog = j;
            previousBiomeFog = MathHelper.floor(f1) << 16 | MathHelper.floor(f2) << 8 | MathHelper.floor(f3);
            biomeChangedTime = i;
         }
      } else if (ifluidstate.is(FluidTags.LAVA)) {
         fogRed = 0.6F;
         fogGreen = 0.1F;
         fogBlue = 0.0F;
         biomeChangedTime = -1L;
      } else {
         float f4 = 0.25F + 0.75F * (float)p_228371_3_ / 32.0F;
         f4 = 1.0F - (float)Math.pow((double)f4, 0.25D);
         Vec3d vec3d = p_228371_2_.getSkyColor(p_228371_0_.getBlockPosition(), p_228371_1_);
         float f5 = (float)vec3d.x;
         float f8 = (float)vec3d.y;
         float f11 = (float)vec3d.z;
         Vec3d vec3d1 = p_228371_2_.func_228329_i_(p_228371_1_);
         fogRed = (float)vec3d1.x;
         fogGreen = (float)vec3d1.y;
         fogBlue = (float)vec3d1.z;
         if (p_228371_3_ >= 4) {
            float f12 = MathHelper.sin(p_228371_2_.getSunAngle(p_228371_1_)) > 0.0F ? -1.0F : 1.0F;
            Vector3f vector3f = new Vector3f(f12, 0.0F, 0.0F);
            float f16 = p_228371_0_.getLookVector().dot(vector3f);
            if (f16 < 0.0F) {
               f16 = 0.0F;
            }

            if (f16 > 0.0F) {
               float[] afloat = p_228371_2_.dimension.func_76560_a(p_228371_2_.func_72826_c(p_228371_1_), p_228371_1_);
               if (afloat != null) {
                  f16 = f16 * afloat[3];
                  fogRed = fogRed * (1.0F - f16) + afloat[0] * f16;
                  fogGreen = fogGreen * (1.0F - f16) + afloat[1] * f16;
                  fogBlue = fogBlue * (1.0F - f16) + afloat[2] * f16;
               }
            }
         }

         fogRed += (f5 - fogRed) * f4;
         fogGreen += (f8 - fogGreen) * f4;
         fogBlue += (f11 - fogBlue) * f4;
         float f13 = p_228371_2_.getRainLevel(p_228371_1_);
         if (f13 > 0.0F) {
            float f14 = 1.0F - f13 * 0.5F;
            float f17 = 1.0F - f13 * 0.4F;
            fogRed *= f14;
            fogGreen *= f14;
            fogBlue *= f17;
         }

         float f15 = p_228371_2_.getThunderLevel(p_228371_1_);
         if (f15 > 0.0F) {
            float f18 = 1.0F - f15 * 0.5F;
            fogRed *= f18;
            fogGreen *= f18;
            fogBlue *= f18;
         }

         biomeChangedTime = -1L;
      }

      double d0 = p_228371_0_.getPosition().y * p_228371_2_.dimension.func_76565_k();
      if (p_228371_0_.getEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getEntity()).hasEffect(Effects.BLINDNESS)) {
         int i2 = ((LivingEntity)p_228371_0_.getEntity()).getEffect(Effects.BLINDNESS).getDuration();
         if (i2 < 20) {
            d0 *= (double)(1.0F - (float)i2 / 20.0F);
         } else {
            d0 = 0.0D;
         }
      }

      if (d0 < 1.0D) {
         if (d0 < 0.0D) {
            d0 = 0.0D;
         }

         d0 = d0 * d0;
         fogRed = (float)((double)fogRed * d0);
         fogGreen = (float)((double)fogGreen * d0);
         fogBlue = (float)((double)fogBlue * d0);
      }

      if (p_228371_4_ > 0.0F) {
         fogRed = fogRed * (1.0F - p_228371_4_) + fogRed * 0.7F * p_228371_4_;
         fogGreen = fogGreen * (1.0F - p_228371_4_) + fogGreen * 0.6F * p_228371_4_;
         fogBlue = fogBlue * (1.0F - p_228371_4_) + fogBlue * 0.6F * p_228371_4_;
      }

      if (ifluidstate.is(FluidTags.WATER)) {
         float f6 = 0.0F;
         if (p_228371_0_.getEntity() instanceof ClientPlayerEntity) {
            ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)p_228371_0_.getEntity();
            f6 = clientplayerentity.getWaterVision();
         }

         float f9 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         // Forge: fix MC-4647 and MC-10480
         if (Float.isInfinite(f9)) f9 = Math.nextAfter(f9, 0.0);
         fogRed = fogRed * (1.0F - f6) + fogRed * f9 * f6;
         fogGreen = fogGreen * (1.0F - f6) + fogGreen * f9 * f6;
         fogBlue = fogBlue * (1.0F - f6) + fogBlue * f9 * f6;
      } else if (p_228371_0_.getEntity() instanceof LivingEntity && ((LivingEntity)p_228371_0_.getEntity()).hasEffect(Effects.NIGHT_VISION)) {
         float f7 = GameRenderer.getNightVisionScale((LivingEntity)p_228371_0_.getEntity(), p_228371_1_);
         float f10 = Math.min(1.0F / fogRed, Math.min(1.0F / fogGreen, 1.0F / fogBlue));
         // Forge: fix MC-4647 and MC-10480
         if (Float.isInfinite(f10)) f10 = Math.nextAfter(f10, 0.0);
         fogRed = fogRed * (1.0F - f7) + fogRed * f10 * f7;
         fogGreen = fogGreen * (1.0F - f7) + fogGreen * f10 * f7;
         fogBlue = fogBlue * (1.0F - f7) + fogBlue * f10 * f7;
      }

      net.minecraftforge.client.event.EntityViewRenderEvent.FogColors event = new net.minecraftforge.client.event.EntityViewRenderEvent.FogColors(p_228371_0_, p_228371_1_, fogRed, fogGreen, fogBlue);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(event);

      fogRed = event.getRed();
      fogGreen = event.getGreen();
      fogBlue = event.getBlue();

      RenderSystem.clearColor(fogRed, fogGreen, fogBlue, 0.0F);
   }

   public static void setupNoFog() {
      RenderSystem.fogDensity(0.0F);
      RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
   }

   @Deprecated // FORGE: Pass in partialTicks
   public static void setupFog(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_) {
      setupFog(p_228372_0_, p_228372_1_, p_228372_2_, p_228372_3_, 0);
   }

   public static void setupFog(ActiveRenderInfo p_228372_0_, FogRenderer.FogType p_228372_1_, float p_228372_2_, boolean p_228372_3_, float partialTicks) {
      IFluidState ifluidstate = p_228372_0_.getFluidInCamera();
      Entity entity = p_228372_0_.getEntity();
      boolean flag = ifluidstate.getType() != Fluids.EMPTY;
      float hook = net.minecraftforge.client.ForgeHooksClient.getFogDensity(p_228372_1_, p_228372_0_, partialTicks, 0.1F);
      if (hook >= 0) RenderSystem.fogDensity(hook);
      else
      if (flag) {
         float f = 1.0F;
         if (ifluidstate.is(FluidTags.WATER)) {
            f = 0.05F;
            if (entity instanceof ClientPlayerEntity) {
               ClientPlayerEntity clientplayerentity = (ClientPlayerEntity)entity;
               f -= clientplayerentity.getWaterVision() * clientplayerentity.getWaterVision() * 0.03F;
               Biome biome = clientplayerentity.level.getBiome(new BlockPos(clientplayerentity));
               if (biome == Biomes.SWAMP || biome == Biomes.SWAMP_HILLS) {
                  f += 0.005F;
               }
            }
         } else if (ifluidstate.is(FluidTags.LAVA)) {
            f = 2.0F;
         }

         RenderSystem.fogDensity(f);
         RenderSystem.fogMode(GlStateManager.FogMode.EXP2);
      } else {
         float f2;
         float f3;
         if (entity instanceof LivingEntity && ((LivingEntity)entity).hasEffect(Effects.BLINDNESS)) {
            int i = ((LivingEntity)entity).getEffect(Effects.BLINDNESS).getDuration();
            float f1 = MathHelper.lerp(Math.min(1.0F, (float)i / 20.0F), p_228372_2_, 5.0F);
            if (p_228372_1_ == FogRenderer.FogType.FOG_SKY) {
               f2 = 0.0F;
               f3 = f1 * 0.8F;
            } else {
               f2 = f1 * 0.25F;
               f3 = f1;
            }
         } else if (p_228372_3_) {
            f2 = p_228372_2_ * 0.05F;
            f3 = Math.min(p_228372_2_, 192.0F) * 0.5F;
         } else if (p_228372_1_ == FogRenderer.FogType.FOG_SKY) {
            f2 = 0.0F;
            f3 = p_228372_2_;
         } else {
            f2 = p_228372_2_ * 0.75F;
            f3 = p_228372_2_;
         }

         RenderSystem.fogStart(f2);
         RenderSystem.fogEnd(f3);
         RenderSystem.fogMode(GlStateManager.FogMode.LINEAR);
         RenderSystem.setupNvFogDistance();
         net.minecraftforge.client.ForgeHooksClient.onFogRender(p_228372_1_, p_228372_0_, partialTicks, f3);
      }
   }

   public static void levelFogColor() {
      RenderSystem.fog(2918, fogRed, fogGreen, fogBlue, 1.0F);
   }

   @OnlyIn(Dist.CLIENT)
   public static enum FogType {
      FOG_SKY,
      FOG_TERRAIN;
   }
}
