package net.minecraft.client.gui.widget.list;

import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.ResourcePacksScreen;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.resources.PackCompatibility;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractResourcePackList extends ExtendedList<AbstractResourcePackList.ResourcePackEntry> {
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/resource_packs.png");
   private static final ITextComponent INCOMPATIBLE_TITLE = new TranslationTextComponent("resourcePack.incompatible");
   private static final ITextComponent INCOMPATIBLE_CONFIRM_TITLE = new TranslationTextComponent("resourcePack.incompatible.confirm.title");
   protected final Minecraft field_148205_k;
   private final ITextComponent title;

   public AbstractResourcePackList(Minecraft p_i51074_1_, int p_i51074_2_, int p_i51074_3_, ITextComponent p_i51074_4_) {
      super(p_i51074_1_, p_i51074_2_, p_i51074_3_, 32, p_i51074_3_ - 55 + 4, 36);
      this.field_148205_k = p_i51074_1_;
      this.centerListVertically = false;
      this.setRenderHeader(true, (int)(9.0F * 1.5F));
      this.title = p_i51074_4_;
   }

   protected void renderHeader(int p_renderHeader_1_, int p_renderHeader_2_, Tessellator p_renderHeader_3_) {
      ITextComponent itextcomponent = (new StringTextComponent("")).func_150257_a(this.title).func_211709_a(TextFormatting.UNDERLINE, TextFormatting.BOLD);
      this.field_148205_k.font.func_211126_b(itextcomponent.func_150254_d(), (float)(p_renderHeader_1_ + this.width / 2 - this.field_148205_k.font.width(itextcomponent.func_150254_d()) / 2), (float)Math.min(this.y0 + 3, p_renderHeader_2_), 16777215);
   }

   public int getRowWidth() {
      return this.width;
   }

   protected int getScrollbarPosition() {
      return this.x1 - 6;
   }

   public void func_214365_a(AbstractResourcePackList.ResourcePackEntry p_214365_1_) {
      this.addEntry(p_214365_1_);
      p_214365_1_.parent = this;
   }

   @OnlyIn(Dist.CLIENT)
   public static class ResourcePackEntry extends ExtendedList.AbstractListEntry<AbstractResourcePackList.ResourcePackEntry> {
      private AbstractResourcePackList parent;
      protected final Minecraft minecraft;
      protected final ResourcePacksScreen screen;
      private final ClientResourcePackInfo pack;

      public ResourcePackEntry(AbstractResourcePackList p_i50749_1_, ResourcePacksScreen p_i50749_2_, ClientResourcePackInfo p_i50749_3_) {
         this.screen = p_i50749_2_;
         this.minecraft = Minecraft.getInstance();
         this.pack = p_i50749_3_;
         this.parent = p_i50749_1_;
      }

      public void func_214422_a(SelectedResourcePackList p_214422_1_) {
         this.func_214418_e().getDefaultPosition().insert(p_214422_1_.children(), this, AbstractResourcePackList.ResourcePackEntry::func_214418_e, true);
         this.func_230009_b_(p_214422_1_);
      }

      public void func_230009_b_(SelectedResourcePackList p_230009_1_) {
         this.parent = p_230009_1_;
      }

      protected void func_214419_a() {
         this.pack.func_195808_a(this.minecraft.getTextureManager());
      }

      protected PackCompatibility func_214423_b() {
         return this.pack.getCompatibility();
      }

      protected String func_214420_c() {
         return this.pack.getDescription().func_150254_d();
      }

      protected String func_214416_d() {
         return this.pack.getTitle().func_150254_d();
      }

      public ClientResourcePackInfo func_214418_e() {
         return this.pack;
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         PackCompatibility packcompatibility = this.func_214423_b();
         if (!packcompatibility.isCompatible()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            AbstractGui.fill(p_render_3_ - 1, p_render_2_ - 1, p_render_3_ + p_render_4_ - 9, p_render_2_ + p_render_5_ + 1, -8978432);
         }

         this.func_214419_a();
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         String s = this.func_214416_d();
         String s1 = this.func_214420_c();
         if (this.func_214424_f() && (this.minecraft.options.touchscreen || p_render_8_)) {
            this.minecraft.getTextureManager().bind(AbstractResourcePackList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int i = p_render_6_ - p_render_3_;
            int j = p_render_7_ - p_render_2_;
            if (!packcompatibility.isCompatible()) {
               s = AbstractResourcePackList.INCOMPATIBLE_TITLE.func_150254_d();
               s1 = packcompatibility.getDescription().func_150254_d();
            }

            if (this.func_214425_g()) {
               if (i < 32) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            } else {
               if (this.func_214426_h()) {
                  if (i < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214414_i()) {
                  if (i < 32 && i > 16 && j < 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
                  }
               }

               if (this.func_214427_j()) {
                  if (i < 32 && i > 16 && j > 16) {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
                  } else {
                     AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
                  }
               }
            }
         }

         int l = this.minecraft.font.width(s);
         if (l > 157) {
            s = this.minecraft.font.func_78269_a(s, 157 - this.minecraft.font.width("...")) + "...";
         }

         this.minecraft.font.func_175063_a(s, (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 1), 16777215);
         List<String> list = this.minecraft.font.func_78271_c(s1, 157);

         for(int k = 0; k < 2 && k < list.size(); ++k) {
            this.minecraft.font.func_175063_a(list.get(k), (float)(p_render_3_ + 32 + 2), (float)(p_render_2_ + 12 + 10 * k), 8421504);
         }

      }

      protected boolean func_214424_f() {
         return !this.pack.isFixedPosition() || !this.pack.isRequired();
      }

      protected boolean func_214425_g() {
         return !this.screen.func_214299_c(this);
      }

      protected boolean func_214426_h() {
         return this.screen.func_214299_c(this) && !this.pack.isRequired();
      }

      protected boolean func_214414_i() {
         List<AbstractResourcePackList.ResourcePackEntry> list = this.parent.children();
         int i = list.indexOf(this);
         return i > 0 && !(list.get(i - 1)).pack.isFixedPosition();
      }

      protected boolean func_214427_j() {
         List<AbstractResourcePackList.ResourcePackEntry> list = this.parent.children();
         int i = list.indexOf(this);
         return i >= 0 && i < list.size() - 1 && !(list.get(i + 1)).pack.isFixedPosition();
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         double d0 = p_mouseClicked_1_ - (double)this.parent.getRowLeft();
         double d1 = p_mouseClicked_3_ - (double)this.parent.getRowTop(this.parent.children().indexOf(this));
         if (this.func_214424_f() && d0 <= 32.0D) {
            if (this.func_214425_g()) {
               this.func_214415_k().func_175288_g();
               PackCompatibility packcompatibility = this.func_214423_b();
               if (packcompatibility.isCompatible()) {
                  this.func_214415_k().func_214300_a(this);
               } else {
                  ITextComponent itextcomponent = packcompatibility.getConfirmation();
                  this.minecraft.setScreen(new ConfirmScreen((p_214417_1_) -> {
                     this.minecraft.setScreen(this.func_214415_k());
                     if (p_214417_1_) {
                        this.func_214415_k().func_214300_a(this);
                     }

                  }, AbstractResourcePackList.INCOMPATIBLE_CONFIRM_TITLE, itextcomponent));
               }

               return true;
            }

            if (d0 < 16.0D && this.func_214426_h()) {
               this.func_214415_k().func_214297_b(this);
               return true;
            }

            if (d0 > 16.0D && d1 < 16.0D && this.func_214414_i()) {
               List<AbstractResourcePackList.ResourcePackEntry> list1 = this.parent.children();
               int j = list1.indexOf(this);
               list1.remove(j);
               list1.add(j - 1, this);
               this.func_214415_k().func_175288_g();
               return true;
            }

            if (d0 > 16.0D && d1 > 16.0D && this.func_214427_j()) {
               List<AbstractResourcePackList.ResourcePackEntry> list = this.parent.children();
               int i = list.indexOf(this);
               list.remove(i);
               list.add(i + 1, this);
               this.func_214415_k().func_175288_g();
               return true;
            }
         }

         return false;
      }

      public ResourcePacksScreen func_214415_k() {
         return this.screen;
      }
   }
}