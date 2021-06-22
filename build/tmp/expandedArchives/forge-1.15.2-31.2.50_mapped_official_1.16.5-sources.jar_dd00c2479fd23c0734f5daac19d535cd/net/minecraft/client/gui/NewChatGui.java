package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class NewChatGui extends AbstractGui {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List<String> recentChat = Lists.newArrayList();
   private final List<ChatLine> allMessages = Lists.newArrayList();
   private final List<ChatLine> trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;

   public NewChatGui(Minecraft p_i1022_1_) {
      this.minecraft = p_i1022_1_;
   }

   public void func_146230_a(int p_146230_1_) {
      if (this.func_228091_i_()) {
         int i = this.getLinesPerPage();
         int j = this.trimmedMessages.size();
         if (j > 0) {
            boolean flag = false;
            if (this.isChatFocused()) {
               flag = true;
            }

            double d0 = this.getScale();
            int k = MathHelper.ceil((double)this.getWidth() / d0);
            RenderSystem.pushMatrix();
            RenderSystem.translatef(2.0F, 8.0F, 0.0F);
            RenderSystem.scaled(d0, d0, 1.0D);
            double d1 = this.minecraft.options.chatOpacity * (double)0.9F + (double)0.1F;
            double d2 = this.minecraft.options.textBackgroundOpacity;
            int l = 0;
            Matrix4f matrix4f = Matrix4f.createTranslateMatrix(0.0F, 0.0F, -100.0F);

            for(int i1 = 0; i1 + this.chatScrollbarPos < this.trimmedMessages.size() && i1 < i; ++i1) {
               ChatLine chatline = this.trimmedMessages.get(i1 + this.chatScrollbarPos);
               if (chatline != null) {
                  int j1 = p_146230_1_ - chatline.getAddedTime();
                  if (j1 < 200 || flag) {
                     double d3 = flag ? 1.0D : getTimeFactor(j1);
                     int l1 = (int)(255.0D * d3 * d1);
                     int i2 = (int)(255.0D * d3 * d2);
                     ++l;
                     if (l1 > 3) {
                        int j2 = 0;
                        int k2 = -i1 * 9;
                        fill(matrix4f, -2, k2 - 9, 0 + k + 4, k2, i2 << 24);
                        String s = chatline.func_151461_a().func_150254_d();
                        RenderSystem.enableBlend();
                        this.minecraft.font.func_175063_a(s, 0.0F, (float)(k2 - 8), 16777215 + (l1 << 24));
                        RenderSystem.disableAlphaTest();
                        RenderSystem.disableBlend();
                     }
                  }
               }
            }

            if (flag) {
               int l2 = 9;
               RenderSystem.translatef(-3.0F, 0.0F, 0.0F);
               int i3 = j * l2 + j;
               int j3 = l * l2 + l;
               int k3 = this.chatScrollbarPos * j3 / j;
               int k1 = j3 * j3 / i3;
               if (i3 != j3) {
                  int l3 = k3 > 0 ? 170 : 96;
                  int i4 = this.newMessageSinceScroll ? 13382451 : 3355562;
                  fill(0, -k3, 2, -k3 - k1, i4 + (l3 << 24));
                  fill(2, -k3, 1, -k3 - k1, 13421772 + (l3 << 24));
               }
            }

            RenderSystem.popMatrix();
         }
      }
   }

   private boolean func_228091_i_() {
      return this.minecraft.options.chatVisibility != ChatVisibility.HIDDEN;
   }

   private static double getTimeFactor(int p_212915_0_) {
      double d0 = (double)p_212915_0_ / 200.0D;
      d0 = 1.0D - d0;
      d0 = d0 * 10.0D;
      d0 = MathHelper.clamp(d0, 0.0D, 1.0D);
      d0 = d0 * d0;
      return d0;
   }

   public void clearMessages(boolean p_146231_1_) {
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if (p_146231_1_) {
         this.recentChat.clear();
      }

   }

   public void addMessage(ITextComponent p_146227_1_) {
      this.addMessage(p_146227_1_, 0);
   }

   public void addMessage(ITextComponent p_146234_1_, int p_146234_2_) {
      this.func_146237_a(p_146234_1_, p_146234_2_, this.minecraft.gui.getGuiTicks(), false);
      LOGGER.info("[CHAT] {}", (Object)p_146234_1_.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void func_146237_a(ITextComponent p_146237_1_, int p_146237_2_, int p_146237_3_, boolean p_146237_4_) {
      if (p_146237_2_ != 0) {
         this.removeById(p_146237_2_);
      }

      int i = MathHelper.floor((double)this.getWidth() / this.getScale());
      List<ITextComponent> list = RenderComponentsUtil.func_178908_a(p_146237_1_, i, this.minecraft.font, false, false);
      boolean flag = this.isChatFocused();

      for(ITextComponent itextcomponent : list) {
         if (flag && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1.0D);
         }

         this.trimmedMessages.add(0, new ChatLine(p_146237_3_, itextcomponent, p_146237_2_));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if (!p_146237_4_) {
         this.allMessages.add(0, new ChatLine(p_146237_3_, p_146237_1_, p_146237_2_));

         while(this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
         }
      }

   }

   public void rescaleChat() {
      this.trimmedMessages.clear();
      this.resetChatScroll();

      for(int i = this.allMessages.size() - 1; i >= 0; --i) {
         ChatLine chatline = this.allMessages.get(i);
         this.func_146237_a(chatline.func_151461_a(), chatline.getId(), chatline.getAddedTime(), true);
      }

   }

   public List<String> getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String p_146239_1_) {
      if (this.recentChat.isEmpty() || !this.recentChat.get(this.recentChat.size() - 1).equals(p_146239_1_)) {
         this.recentChat.add(p_146239_1_);
      }

   }

   public void resetChatScroll() {
      this.chatScrollbarPos = 0;
      this.newMessageSinceScroll = false;
   }

   public void scrollChat(double p_194813_1_) {
      this.chatScrollbarPos = (int)((double)this.chatScrollbarPos + p_194813_1_);
      int i = this.trimmedMessages.size();
      if (this.chatScrollbarPos > i - this.getLinesPerPage()) {
         this.chatScrollbarPos = i - this.getLinesPerPage();
      }

      if (this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   @Nullable
   public ITextComponent func_194817_a(double p_194817_1_, double p_194817_3_) {
      if (this.isChatFocused() && !this.minecraft.options.hideGui && this.func_228091_i_()) {
         double d0 = this.getScale();
         double d1 = p_194817_1_ - 2.0D;
         double d2 = (double)this.minecraft.getWindow().getGuiScaledHeight() - p_194817_3_ - 40.0D;
         d1 = (double)MathHelper.floor(d1 / d0);
         d2 = (double)MathHelper.floor(d2 / d0);
         if (!(d1 < 0.0D) && !(d2 < 0.0D)) {
            int i = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if (d1 <= (double)MathHelper.floor((double)this.getWidth() / this.getScale()) && d2 < (double)(9 * i + i)) {
               int j = (int)(d2 / 9.0D + (double)this.chatScrollbarPos);
               if (j >= 0 && j < this.trimmedMessages.size()) {
                  ChatLine chatline = this.trimmedMessages.get(j);
                  int k = 0;

                  for(ITextComponent itextcomponent : chatline.func_151461_a()) {
                     if (itextcomponent instanceof StringTextComponent) {
                        k += this.minecraft.font.width(RenderComponentsUtil.func_178909_a(((StringTextComponent)itextcomponent).getText(), false));
                        if ((double)k > d1) {
                           return itextcomponent;
                        }
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   public void removeById(int p_146242_1_) {
      Iterator<ChatLine> iterator = this.trimmedMessages.iterator();

      while(iterator.hasNext()) {
         ChatLine chatline = iterator.next();
         if (chatline.getId() == p_146242_1_) {
            iterator.remove();
         }
      }

      iterator = this.allMessages.iterator();

      while(iterator.hasNext()) {
         ChatLine chatline1 = iterator.next();
         if (chatline1.getId() == p_146242_1_) {
            iterator.remove();
            break;
         }
      }

   }

   public int getWidth() {
      return getWidth(this.minecraft.options.chatWidth);
   }

   public int getHeight() {
      return getHeight(this.isChatFocused() ? this.minecraft.options.chatHeightFocused : this.minecraft.options.chatHeightUnfocused);
   }

   public double getScale() {
      return this.minecraft.options.chatScale;
   }

   public static int getWidth(double p_194814_0_) {
      int i = 320;
      int j = 40;
      return MathHelper.floor(p_194814_0_ * 280.0D + 40.0D);
   }

   public static int getHeight(double p_194816_0_) {
      int i = 180;
      int j = 20;
      return MathHelper.floor(p_194816_0_ * 160.0D + 20.0D);
   }

   public int getLinesPerPage() {
      return this.getHeight() / 9;
   }
}