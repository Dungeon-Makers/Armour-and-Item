package net.minecraft.client.gui.advancements;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementEntryGui extends AbstractGui {
   private static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation("textures/gui/advancements/widgets.png");
   private static final Pattern field_192996_f = Pattern.compile("(.+) \\S+");
   private final AdvancementTabGui tab;
   private final Advancement advancement;
   private final DisplayInfo display;
   private final String title;
   private final int width;
   private final List<String> description;
   private final Minecraft minecraft;
   private AdvancementEntryGui parent;
   private final List<AdvancementEntryGui> children = Lists.newArrayList();
   private AdvancementProgress progress;
   private final int x;
   private final int y;

   public AdvancementEntryGui(AdvancementTabGui p_i47385_1_, Minecraft p_i47385_2_, Advancement p_i47385_3_, DisplayInfo p_i47385_4_) {
      this.tab = p_i47385_1_;
      this.advancement = p_i47385_3_;
      this.display = p_i47385_4_;
      this.minecraft = p_i47385_2_;
      this.title = p_i47385_2_.font.func_78269_a(p_i47385_4_.getTitle().func_150254_d(), 163);
      this.x = MathHelper.floor(p_i47385_4_.getX() * 28.0F);
      this.y = MathHelper.floor(p_i47385_4_.getY() * 27.0F);
      int i = p_i47385_3_.getMaxCriteraRequired();
      int j = String.valueOf(i).length();
      int k = i > 1 ? p_i47385_2_.font.width("  ") + p_i47385_2_.font.width("0") * j * 2 + p_i47385_2_.font.width("/") : 0;
      int l = 29 + p_i47385_2_.font.width(this.title) + k;
      String s = p_i47385_4_.getDescription().func_150254_d();
      this.description = this.func_192995_a(s, l);

      for(String s1 : this.description) {
         l = Math.max(l, p_i47385_2_.font.width(s1));
      }

      this.width = l + 3 + 5;
   }

   private List<String> func_192995_a(String p_192995_1_, int p_192995_2_) {
      if (p_192995_1_.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> list = this.minecraft.font.func_78271_c(p_192995_1_, p_192995_2_);
         if (list.size() < 2) {
            return list;
         } else {
            String s = list.get(0);
            String s1 = list.get(1);
            int i = this.minecraft.font.width(s + ' ' + s1.split(" ")[0]);
            if (i - p_192995_2_ <= 10) {
               return this.minecraft.font.func_78271_c(p_192995_1_, i);
            } else {
               Matcher matcher = field_192996_f.matcher(s);
               if (matcher.matches()) {
                  int j = this.minecraft.font.width(matcher.group(1));
                  if (p_192995_2_ - j <= 10) {
                     return this.minecraft.font.func_78271_c(p_192995_1_, j);
                  }
               }

               return list;
            }
         }
      }
   }

   @Nullable
   private AdvancementEntryGui getFirstVisibleParent(Advancement p_191818_1_) {
      while(true) {
         p_191818_1_ = p_191818_1_.getParent();
         if (p_191818_1_ == null || p_191818_1_.getDisplay() != null) {
            break;
         }
      }

      return p_191818_1_ != null && p_191818_1_.getDisplay() != null ? this.tab.getWidget(p_191818_1_) : null;
   }

   public void func_191819_a(int p_191819_1_, int p_191819_2_, boolean p_191819_3_) {
      if (this.parent != null) {
         int i = p_191819_1_ + this.parent.x + 13;
         int j = p_191819_1_ + this.parent.x + 26 + 4;
         int k = p_191819_2_ + this.parent.y + 13;
         int l = p_191819_1_ + this.x + 13;
         int i1 = p_191819_2_ + this.y + 13;
         int j1 = p_191819_3_ ? -16777216 : -1;
         if (p_191819_3_) {
            this.hLine(j, i, k - 1, j1);
            this.hLine(j + 1, i, k, j1);
            this.hLine(j, i, k + 1, j1);
            this.hLine(l, j - 1, i1 - 1, j1);
            this.hLine(l, j - 1, i1, j1);
            this.hLine(l, j - 1, i1 + 1, j1);
            this.vLine(j - 1, i1, k, j1);
            this.vLine(j + 1, i1, k, j1);
         } else {
            this.hLine(j, i, k, j1);
            this.hLine(l, j, i1, j1);
            this.vLine(j, i1, k, j1);
         }
      }

      for(AdvancementEntryGui advancemententrygui : this.children) {
         advancemententrygui.func_191819_a(p_191819_1_, p_191819_2_, p_191819_3_);
      }

   }

   public void func_191817_b(int p_191817_1_, int p_191817_2_) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         float f = this.progress == null ? 0.0F : this.progress.getPercent();
         AdvancementState advancementstate;
         if (f >= 1.0F) {
            advancementstate = AdvancementState.OBTAINED;
         } else {
            advancementstate = AdvancementState.UNOBTAINED;
         }

         this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
         this.blit(p_191817_1_ + this.x + 3, p_191817_2_ + this.y, this.display.getFrame().getTexture(), 128 + advancementstate.getIndex() * 26, 26, 26);
         this.minecraft.getItemRenderer().renderAndDecorateItem((LivingEntity)null, this.display.getIcon(), p_191817_1_ + this.x + 8, p_191817_2_ + this.y + 5);
      }

      for(AdvancementEntryGui advancemententrygui : this.children) {
         advancemententrygui.func_191817_b(p_191817_1_, p_191817_2_);
      }

   }

   public void setProgress(AdvancementProgress p_191824_1_) {
      this.progress = p_191824_1_;
   }

   public void addChild(AdvancementEntryGui p_191822_1_) {
      this.children.add(p_191822_1_);
   }

   public void func_191821_a(int p_191821_1_, int p_191821_2_, float p_191821_3_, int p_191821_4_, int p_191821_5_) {
      boolean flag = p_191821_4_ + p_191821_1_ + this.x + this.width + 26 >= this.tab.getScreen().width;
      String s = this.progress == null ? null : this.progress.getProgressText();
      int i = s == null ? 0 : this.minecraft.font.width(s);
      boolean flag1 = 113 - p_191821_2_ - this.y - 26 <= 6 + this.description.size() * 9;
      float f = this.progress == null ? 0.0F : this.progress.getPercent();
      int j = MathHelper.floor(f * (float)this.width);
      AdvancementState advancementstate;
      AdvancementState advancementstate1;
      AdvancementState advancementstate2;
      if (f >= 1.0F) {
         j = this.width / 2;
         advancementstate = AdvancementState.OBTAINED;
         advancementstate1 = AdvancementState.OBTAINED;
         advancementstate2 = AdvancementState.OBTAINED;
      } else if (j < 2) {
         j = this.width / 2;
         advancementstate = AdvancementState.UNOBTAINED;
         advancementstate1 = AdvancementState.UNOBTAINED;
         advancementstate2 = AdvancementState.UNOBTAINED;
      } else if (j > this.width - 2) {
         j = this.width / 2;
         advancementstate = AdvancementState.OBTAINED;
         advancementstate1 = AdvancementState.OBTAINED;
         advancementstate2 = AdvancementState.UNOBTAINED;
      } else {
         advancementstate = AdvancementState.OBTAINED;
         advancementstate1 = AdvancementState.UNOBTAINED;
         advancementstate2 = AdvancementState.UNOBTAINED;
      }

      int k = this.width - j;
      this.minecraft.getTextureManager().bind(WIDGETS_LOCATION);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      int l = p_191821_2_ + this.y;
      int i1;
      if (flag) {
         i1 = p_191821_1_ + this.x - this.width + 26 + 6;
      } else {
         i1 = p_191821_1_ + this.x;
      }

      int j1 = 32 + this.description.size() * 9;
      if (!this.description.isEmpty()) {
         if (flag1) {
            this.func_192994_a(i1, l + 26 - j1, this.width, j1, 10, 200, 26, 0, 52);
         } else {
            this.func_192994_a(i1, l, this.width, j1, 10, 200, 26, 0, 52);
         }
      }

      this.blit(i1, l, 0, advancementstate.getIndex() * 26, j, 26);
      this.blit(i1 + j, l, 200 - k, advancementstate1.getIndex() * 26, k, 26);
      this.blit(p_191821_1_ + this.x + 3, p_191821_2_ + this.y, this.display.getFrame().getTexture(), 128 + advancementstate2.getIndex() * 26, 26, 26);
      if (flag) {
         this.minecraft.font.func_175063_a(this.title, (float)(i1 + 5), (float)(p_191821_2_ + this.y + 9), -1);
         if (s != null) {
            this.minecraft.font.func_175063_a(s, (float)(p_191821_1_ + this.x - i), (float)(p_191821_2_ + this.y + 9), -1);
         }
      } else {
         this.minecraft.font.func_175063_a(this.title, (float)(p_191821_1_ + this.x + 32), (float)(p_191821_2_ + this.y + 9), -1);
         if (s != null) {
            this.minecraft.font.func_175063_a(s, (float)(p_191821_1_ + this.x + this.width - i - 5), (float)(p_191821_2_ + this.y + 9), -1);
         }
      }

      if (flag1) {
         for(int k1 = 0; k1 < this.description.size(); ++k1) {
            this.minecraft.font.func_211126_b(this.description.get(k1), (float)(i1 + 5), (float)(l + 26 - j1 + 7 + k1 * 9), -5592406);
         }
      } else {
         for(int l1 = 0; l1 < this.description.size(); ++l1) {
            this.minecraft.font.func_211126_b(this.description.get(l1), (float)(i1 + 5), (float)(p_191821_2_ + this.y + 9 + 17 + l1 * 9), -5592406);
         }
      }

      this.minecraft.getItemRenderer().renderAndDecorateItem((LivingEntity)null, this.display.getIcon(), p_191821_1_ + this.x + 8, p_191821_2_ + this.y + 5);
   }

   protected void func_192994_a(int p_192994_1_, int p_192994_2_, int p_192994_3_, int p_192994_4_, int p_192994_5_, int p_192994_6_, int p_192994_7_, int p_192994_8_, int p_192994_9_) {
      this.blit(p_192994_1_, p_192994_2_, p_192994_8_, p_192994_9_, p_192994_5_, p_192994_5_);
      this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
      this.blit(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_, p_192994_5_, p_192994_5_);
      this.blit(p_192994_1_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
      this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_);
      this.blit(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_4_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_7_ - p_192994_5_, p_192994_5_, p_192994_5_);
      this.func_192993_a(p_192994_1_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
      this.func_192993_a(p_192994_1_ + p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_3_ - p_192994_5_ - p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_ - p_192994_5_ - p_192994_5_, p_192994_7_ - p_192994_5_ - p_192994_5_);
      this.func_192993_a(p_192994_1_ + p_192994_3_ - p_192994_5_, p_192994_2_ + p_192994_5_, p_192994_5_, p_192994_4_ - p_192994_5_ - p_192994_5_, p_192994_8_ + p_192994_6_ - p_192994_5_, p_192994_9_ + p_192994_5_, p_192994_6_, p_192994_7_ - p_192994_5_ - p_192994_5_);
   }

   protected void func_192993_a(int p_192993_1_, int p_192993_2_, int p_192993_3_, int p_192993_4_, int p_192993_5_, int p_192993_6_, int p_192993_7_, int p_192993_8_) {
      for(int i = 0; i < p_192993_3_; i += p_192993_7_) {
         int j = p_192993_1_ + i;
         int k = Math.min(p_192993_7_, p_192993_3_ - i);

         for(int l = 0; l < p_192993_4_; l += p_192993_8_) {
            int i1 = p_192993_2_ + l;
            int j1 = Math.min(p_192993_8_, p_192993_4_ - l);
            this.blit(j, i1, p_192993_5_, p_192993_6_, k, j1);
         }
      }

   }

   public boolean isMouseOver(int p_191816_1_, int p_191816_2_, int p_191816_3_, int p_191816_4_) {
      if (!this.display.isHidden() || this.progress != null && this.progress.isDone()) {
         int i = p_191816_1_ + this.x;
         int j = i + 26;
         int k = p_191816_2_ + this.y;
         int l = k + 26;
         return p_191816_3_ >= i && p_191816_3_ <= j && p_191816_4_ >= k && p_191816_4_ <= l;
      } else {
         return false;
      }
   }

   public void attachToParent() {
      if (this.parent == null && this.advancement.getParent() != null) {
         this.parent = this.getFirstVisibleParent(this.advancement);
         if (this.parent != null) {
            this.parent.addChild(this);
         }
      }

   }

   public int getY() {
      return this.y;
   }

   public int getX() {
      return this.x;
   }
}