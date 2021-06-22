package net.minecraft.client.gui.overlay;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.ISoundEventListener;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SubtitleOverlayGui extends AbstractGui implements ISoundEventListener {
   private final Minecraft minecraft;
   private final List<SubtitleOverlayGui.Subtitle> subtitles = Lists.newArrayList();
   private boolean isListening;

   public SubtitleOverlayGui(Minecraft p_i46603_1_) {
      this.minecraft = p_i46603_1_;
   }

   public void render() {
      if (!this.isListening && this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().addListener(this);
         this.isListening = true;
      } else if (this.isListening && !this.minecraft.options.showSubtitles) {
         this.minecraft.getSoundManager().removeListener(this);
         this.isListening = false;
      }

      if (this.isListening && !this.subtitles.isEmpty()) {
         RenderSystem.pushMatrix();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         Vec3d vec3d = new Vec3d(this.minecraft.player.getX(), this.minecraft.player.getEyeY(), this.minecraft.player.getZ());
         Vec3d vec3d1 = (new Vec3d(0.0D, 0.0D, -1.0D)).xRot(-this.minecraft.player.xRot * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float)Math.PI / 180F));
         Vec3d vec3d2 = (new Vec3d(0.0D, 1.0D, 0.0D)).xRot(-this.minecraft.player.xRot * ((float)Math.PI / 180F)).yRot(-this.minecraft.player.yRot * ((float)Math.PI / 180F));
         Vec3d vec3d3 = vec3d1.cross(vec3d2);
         int i = 0;
         int j = 0;
         Iterator<SubtitleOverlayGui.Subtitle> iterator = this.subtitles.iterator();

         while(iterator.hasNext()) {
            SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle = iterator.next();
            if (subtitleoverlaygui$subtitle.getTime() + 3000L <= Util.getMillis()) {
               iterator.remove();
            } else {
               j = Math.max(j, this.minecraft.font.width(subtitleoverlaygui$subtitle.func_186824_a()));
            }
         }

         j = j + this.minecraft.font.width("<") + this.minecraft.font.width(" ") + this.minecraft.font.width(">") + this.minecraft.font.width(" ");

         for(SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle1 : this.subtitles) {
            int k = 255;
            String s = subtitleoverlaygui$subtitle1.func_186824_a();
            Vec3d vec3d4 = subtitleoverlaygui$subtitle1.getLocation().subtract(vec3d).normalize();
            double d0 = -vec3d3.dot(vec3d4);
            double d1 = -vec3d1.dot(vec3d4);
            boolean flag = d1 > 0.5D;
            int l = j / 2;
            int i1 = 9;
            int j1 = i1 / 2;
            float f = 1.0F;
            int k1 = this.minecraft.font.width(s);
            int l1 = MathHelper.floor(MathHelper.clampedLerp(255.0D, 75.0D, (double)((float)(Util.getMillis() - subtitleoverlaygui$subtitle1.getTime()) / 3000.0F)));
            int i2 = l1 << 16 | l1 << 8 | l1;
            RenderSystem.pushMatrix();
            RenderSystem.translatef((float)this.minecraft.getWindow().getGuiScaledWidth() - (float)l * 1.0F - 2.0F, (float)(this.minecraft.getWindow().getGuiScaledHeight() - 30) - (float)(i * (i1 + 1)) * 1.0F, 0.0F);
            RenderSystem.scalef(1.0F, 1.0F, 1.0F);
            fill(-l - 1, -j1 - 1, l + 1, j1 + 1, this.minecraft.options.getBackgroundColor(0.8F));
            RenderSystem.enableBlend();
            if (!flag) {
               if (d0 > 0.0D) {
                  this.minecraft.font.func_211126_b(">", (float)(l - this.minecraft.font.width(">")), (float)(-j1), i2 + -16777216);
               } else if (d0 < 0.0D) {
                  this.minecraft.font.func_211126_b("<", (float)(-l), (float)(-j1), i2 + -16777216);
               }
            }

            this.minecraft.font.func_211126_b(s, (float)(-k1 / 2), (float)(-j1), i2 + -16777216);
            RenderSystem.popMatrix();
            ++i;
         }

         RenderSystem.disableBlend();
         RenderSystem.popMatrix();
      }
   }

   public void onPlaySound(ISound p_184067_1_, SoundEventAccessor p_184067_2_) {
      if (p_184067_2_.getSubtitle() != null) {
         String s = p_184067_2_.getSubtitle().func_150254_d();
         if (!this.subtitles.isEmpty()) {
            for(SubtitleOverlayGui.Subtitle subtitleoverlaygui$subtitle : this.subtitles) {
               if (subtitleoverlaygui$subtitle.func_186824_a().equals(s)) {
                  subtitleoverlaygui$subtitle.refresh(new Vec3d((double)p_184067_1_.getX(), (double)p_184067_1_.getY(), (double)p_184067_1_.getZ()));
                  return;
               }
            }
         }

         this.subtitles.add(new SubtitleOverlayGui.Subtitle(s, new Vec3d((double)p_184067_1_.getX(), (double)p_184067_1_.getY(), (double)p_184067_1_.getZ())));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class Subtitle {
      private final String text;
      private long time;
      private Vec3d location;

      public Subtitle(String p_i47104_2_, Vec3d p_i47104_3_) {
         this.text = p_i47104_2_;
         this.location = p_i47104_3_;
         this.time = Util.getMillis();
      }

      public String func_186824_a() {
         return this.text;
      }

      public long getTime() {
         return this.time;
      }

      public Vec3d getLocation() {
         return this.location;
      }

      public void refresh(Vec3d p_186823_1_) {
         this.location = p_186823_1_;
         this.time = Util.getMillis();
      }
   }
}