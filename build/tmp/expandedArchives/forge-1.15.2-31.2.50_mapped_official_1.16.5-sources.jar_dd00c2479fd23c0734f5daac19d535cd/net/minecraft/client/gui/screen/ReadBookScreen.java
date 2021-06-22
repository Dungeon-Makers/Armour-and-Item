package net.minecraft.client.gui.screen;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ReadBookScreen extends Screen {
   public static final ReadBookScreen.IBookInfo EMPTY_ACCESS = new ReadBookScreen.IBookInfo() {
      public int getPageCount() {
         return 0;
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         return new StringTextComponent("");
      }
   };
   public static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/gui/book.png");
   private ReadBookScreen.IBookInfo bookAccess;
   private int currentPage;
   private List<ITextComponent> cachedPageComponents = Collections.emptyList();
   private int cachedPage = -1;
   private ChangePageButton forwardButton;
   private ChangePageButton backButton;
   private final boolean playTurnSound;

   public ReadBookScreen(ReadBookScreen.IBookInfo p_i51098_1_) {
      this(p_i51098_1_, true);
   }

   public ReadBookScreen() {
      this(EMPTY_ACCESS, false);
   }

   private ReadBookScreen(ReadBookScreen.IBookInfo p_i51099_1_, boolean p_i51099_2_) {
      super(NarratorChatListener.NO_TITLE);
      this.bookAccess = p_i51099_1_;
      this.playTurnSound = p_i51099_2_;
   }

   public void setBookAccess(ReadBookScreen.IBookInfo p_214155_1_) {
      this.bookAccess = p_214155_1_;
      this.currentPage = MathHelper.clamp(this.currentPage, 0, p_214155_1_.getPageCount());
      this.updateButtonVisibility();
      this.cachedPage = -1;
   }

   public boolean setPage(int p_214160_1_) {
      int i = MathHelper.clamp(p_214160_1_, 0, this.bookAccess.getPageCount() - 1);
      if (i != this.currentPage) {
         this.currentPage = i;
         this.updateButtonVisibility();
         this.cachedPage = -1;
         return true;
      } else {
         return false;
      }
   }

   protected boolean forcePage(int p_214153_1_) {
      return this.setPage(p_214153_1_);
   }

   protected void init() {
      this.createMenuControls();
      this.createPageControlButtons();
   }

   protected void createMenuControls() {
      this.addButton(new Button(this.width / 2 - 100, 196, 200, 20, I18n.get("gui.done"), (p_214161_1_) -> {
         this.minecraft.setScreen((Screen)null);
      }));
   }

   protected void createPageControlButtons() {
      int i = (this.width - 192) / 2;
      int j = 2;
      this.forwardButton = this.addButton(new ChangePageButton(i + 116, 159, true, (p_214159_1_) -> {
         this.pageForward();
      }, this.playTurnSound));
      this.backButton = this.addButton(new ChangePageButton(i + 43, 159, false, (p_214158_1_) -> {
         this.pageBack();
      }, this.playTurnSound));
      this.updateButtonVisibility();
   }

   private int getNumPages() {
      return this.bookAccess.getPageCount();
   }

   protected void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
      }

      this.updateButtonVisibility();
   }

   protected void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
      }

      this.updateButtonVisibility();
   }

   private void updateButtonVisibility() {
      this.forwardButton.visible = this.currentPage < this.getNumPages() - 1;
      this.backButton.visible = this.currentPage > 0;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         switch(p_keyPressed_1_) {
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         default:
            return false;
         }
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BOOK_LOCATION);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.blit(i, 2, 0, 0, 192, 192);
      String s = I18n.get("book.pageIndicator", this.currentPage + 1, Math.max(this.getNumPages(), 1));
      if (this.cachedPage != this.currentPage) {
         ITextComponent itextcomponent = this.bookAccess.func_216916_b(this.currentPage);
         this.cachedPageComponents = RenderComponentsUtil.func_178908_a(itextcomponent, 114, this.font, true, true);
      }

      this.cachedPage = this.currentPage;
      int i1 = this.func_214156_a(s);
      this.font.func_211126_b(s, (float)(i - i1 + 192 - 44), 18.0F, 0);
      int k = Math.min(128 / 9, this.cachedPageComponents.size());

      for(int l = 0; l < k; ++l) {
         ITextComponent itextcomponent1 = this.cachedPageComponents.get(l);
         this.font.func_211126_b(itextcomponent1.func_150254_d(), (float)(i + 36), (float)(32 + l * 9), 0);
      }

      ITextComponent itextcomponent2 = this.func_214154_c((double)p_render_1_, (double)p_render_2_);
      if (itextcomponent2 != null) {
         this.renderComponentHoverEffect(itextcomponent2, p_render_1_, p_render_2_);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private int func_214156_a(String p_214156_1_) {
      return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(p_214156_1_) : p_214156_1_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         ITextComponent itextcomponent = this.func_214154_c(p_mouseClicked_1_, p_mouseClicked_3_);
         if (itextcomponent != null && this.handleComponentClicked(itextcomponent)) {
            return true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean handleComponentClicked(ITextComponent p_handleComponentClicked_1_) {
      ClickEvent clickevent = p_handleComponentClicked_1_.getStyle().getClickEvent();
      if (clickevent == null) {
         return false;
      } else if (clickevent.getAction() == ClickEvent.Action.CHANGE_PAGE) {
         String s = clickevent.getValue();

         try {
            int i = Integer.parseInt(s) - 1;
            return this.forcePage(i);
         } catch (Exception var5) {
            return false;
         }
      } else {
         boolean flag = super.handleComponentClicked(p_handleComponentClicked_1_);
         if (flag && clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
            this.minecraft.setScreen((Screen)null);
         }

         return flag;
      }
   }

   @Nullable
   public ITextComponent func_214154_c(double p_214154_1_, double p_214154_3_) {
      if (this.cachedPageComponents == null) {
         return null;
      } else {
         int i = MathHelper.floor(p_214154_1_ - (double)((this.width - 192) / 2) - 36.0D);
         int j = MathHelper.floor(p_214154_3_ - 2.0D - 30.0D);
         if (i >= 0 && j >= 0) {
            int k = Math.min(128 / 9, this.cachedPageComponents.size());
            if (i <= 114 && j < 9 * k + k) {
               int l = j / 9;
               if (l >= 0 && l < this.cachedPageComponents.size()) {
                  ITextComponent itextcomponent = this.cachedPageComponents.get(l);
                  int i1 = 0;

                  for(ITextComponent itextcomponent1 : itextcomponent) {
                     if (itextcomponent1 instanceof StringTextComponent) {
                        i1 += this.minecraft.font.width(itextcomponent1.func_150254_d());
                        if (i1 > i) {
                           return itextcomponent1;
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
      }
   }

   public static List<String> convertPages(CompoundNBT p_214157_0_) {
      ListNBT listnbt = p_214157_0_.getList("pages", 8).copy();
      Builder<String> builder = ImmutableList.builder();

      for(int i = 0; i < listnbt.size(); ++i) {
         builder.add(listnbt.getString(i));
      }

      return builder.build();
   }

   @OnlyIn(Dist.CLIENT)
   public interface IBookInfo {
      int getPageCount();

      ITextComponent func_216915_a(int p_216915_1_);

      default ITextComponent func_216916_b(int p_216916_1_) {
         return (ITextComponent)(p_216916_1_ >= 0 && p_216916_1_ < this.getPageCount() ? this.func_216915_a(p_216916_1_) : new StringTextComponent(""));
      }

      static ReadBookScreen.IBookInfo fromItem(ItemStack p_216917_0_) {
         Item item = p_216917_0_.getItem();
         if (item == Items.WRITTEN_BOOK) {
            return new ReadBookScreen.WrittenBookInfo(p_216917_0_);
         } else {
            return (ReadBookScreen.IBookInfo)(item == Items.WRITABLE_BOOK ? new ReadBookScreen.UnwrittenBookInfo(p_216917_0_) : ReadBookScreen.EMPTY_ACCESS);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class UnwrittenBookInfo implements ReadBookScreen.IBookInfo {
      private final List<String> pages;

      public UnwrittenBookInfo(ItemStack p_i50617_1_) {
         this.pages = readPages(p_i50617_1_);
      }

      private static List<String> readPages(ItemStack p_216919_0_) {
         CompoundNBT compoundnbt = p_216919_0_.getTag();
         return (List<String>)(compoundnbt != null ? ReadBookScreen.convertPages(compoundnbt) : ImmutableList.of());
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         return new StringTextComponent(this.pages.get(p_216915_1_));
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class WrittenBookInfo implements ReadBookScreen.IBookInfo {
      private final List<String> pages;

      public WrittenBookInfo(ItemStack p_i50616_1_) {
         this.pages = readPages(p_i50616_1_);
      }

      private static List<String> readPages(ItemStack p_216921_0_) {
         CompoundNBT compoundnbt = p_216921_0_.getTag();
         return (List<String>)(compoundnbt != null && WrittenBookItem.makeSureTagIsValid(compoundnbt) ? ReadBookScreen.convertPages(compoundnbt) : ImmutableList.of((new TranslationTextComponent("book.invalid.tag")).func_211708_a(TextFormatting.DARK_RED).func_150254_d()));
      }

      public int getPageCount() {
         return this.pages.size();
      }

      public ITextComponent func_216915_a(int p_216915_1_) {
         String s = this.pages.get(p_216915_1_);

         try {
            ITextComponent itextcomponent = ITextComponent.Serializer.func_150699_a(s);
            if (itextcomponent != null) {
               return itextcomponent;
            }
         } catch (Exception var4) {
            ;
         }

         return new StringTextComponent(s);
      }
   }
}