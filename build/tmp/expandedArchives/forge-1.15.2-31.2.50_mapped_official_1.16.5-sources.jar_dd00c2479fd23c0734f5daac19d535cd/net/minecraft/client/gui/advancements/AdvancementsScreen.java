package net.minecraft.client.gui.advancements;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.multiplayer.ClientAdvancementManager;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.CSeenAdvancementsPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AdvancementsScreen extends Screen implements ClientAdvancementManager.IListener {
   private static final ResourceLocation WINDOW_LOCATION = new ResourceLocation("textures/gui/advancements/window.png");
   private static final ResourceLocation TABS_LOCATION = new ResourceLocation("textures/gui/advancements/tabs.png");
   private final ClientAdvancementManager advancements;
   private final Map<Advancement, AdvancementTabGui> tabs = Maps.newLinkedHashMap();
   private AdvancementTabGui selectedTab;
   private boolean isScrolling;
   private static int tabPage, maxPages;

   public AdvancementsScreen(ClientAdvancementManager p_i47383_1_) {
      super(NarratorChatListener.NO_TITLE);
      this.advancements = p_i47383_1_;
   }

   protected void init() {
      this.tabs.clear();
      this.selectedTab = null;
      this.advancements.setListener(this);
      if (this.selectedTab == null && !this.tabs.isEmpty()) {
         this.advancements.setSelectedTab(this.tabs.values().iterator().next().getAdvancement(), true);
      } else {
         this.advancements.setSelectedTab(this.selectedTab == null ? null : this.selectedTab.getAdvancement(), true);
      }
      if (this.tabs.size() > AdvancementTabType.MAX_TABS) {
          int guiLeft = (this.width - 252) / 2;
          int guiTop = (this.height - 140) / 2;
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft,            guiTop - 50, 20, 20, "<", b -> tabPage = Math.max(tabPage - 1, 0       )));
          addButton(new net.minecraft.client.gui.widget.button.Button(guiLeft + 252 - 20, guiTop - 50, 20, 20, ">", b -> tabPage = Math.min(tabPage + 1, maxPages)));
          maxPages = this.tabs.size() / AdvancementTabType.MAX_TABS;
      }
   }

   public void removed() {
      this.advancements.setListener((ClientAdvancementManager.IListener)null);
      ClientPlayNetHandler clientplaynethandler = this.minecraft.getConnection();
      if (clientplaynethandler != null) {
         clientplaynethandler.send(CSeenAdvancementsPacket.closedScreen());
      }

   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         int i = (this.width - 252) / 2;
         int j = (this.height - 140) / 2;

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.isMouseOver(i, j, p_mouseClicked_1_, p_mouseClicked_3_)) {
               this.advancements.setSelectedTab(advancementtabgui.getAdvancement(), true);
               break;
            }
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (this.minecraft.options.keyAdvancements.matches(p_keyPressed_1_, p_keyPressed_2_)) {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
         return true;
      } else {
         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = (this.width - 252) / 2;
      int j = (this.height - 140) / 2;
      this.renderBackground();
      this.func_191936_c(p_render_1_, p_render_2_, i, j);
      if (maxPages != 0) {
         String page = String.format("%d / %d", tabPage + 1, maxPages + 1);
         int width = this.font.width(page);
         RenderSystem.disableLighting();
         this.font.func_175063_a(page, i + (252 / 2) - (width / 2), j - 44, -1);
      }
      this.func_191934_b(i, j);
      this.func_191937_d(p_render_1_, p_render_2_, i, j);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ != 0) {
         this.isScrolling = false;
         return false;
      } else {
         if (!this.isScrolling) {
            this.isScrolling = true;
         } else if (this.selectedTab != null) {
            this.selectedTab.scroll(p_mouseDragged_6_, p_mouseDragged_8_);
         }

         return true;
      }
   }

   private void func_191936_c(int p_191936_1_, int p_191936_2_, int p_191936_3_, int p_191936_4_) {
      AdvancementTabGui advancementtabgui = this.selectedTab;
      if (advancementtabgui == null) {
         fill(p_191936_3_ + 9, p_191936_4_ + 18, p_191936_3_ + 9 + 234, p_191936_4_ + 18 + 113, -16777216);
         String s = I18n.get("advancements.empty");
         int i = this.font.width(s);
         this.font.func_211126_b(s, (float)(p_191936_3_ + 9 + 117 - i / 2), (float)(p_191936_4_ + 18 + 56 - 9 / 2), -1);
         this.font.func_211126_b(":(", (float)(p_191936_3_ + 9 + 117 - this.font.width(":(") / 2), (float)(p_191936_4_ + 18 + 113 - 9), -1);
      } else {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)(p_191936_3_ + 9), (float)(p_191936_4_ + 18), 0.0F);
         advancementtabgui.func_191799_a();
         RenderSystem.popMatrix();
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
      }
   }

   public void func_191934_b(int p_191934_1_, int p_191934_2_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableBlend();
      this.minecraft.getTextureManager().bind(WINDOW_LOCATION);
      this.blit(p_191934_1_, p_191934_2_, 0, 0, 252, 140);
      if (this.tabs.size() > 1) {
         this.minecraft.getTextureManager().bind(TABS_LOCATION);

         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage)
            advancementtabgui.func_191798_a(p_191934_1_, p_191934_2_, advancementtabgui == this.selectedTab);
         }

         RenderSystem.enableRescaleNormal();
         RenderSystem.defaultBlendFunc();

         for(AdvancementTabGui advancementtabgui1 : this.tabs.values()) {
            if (advancementtabgui1.getPage() == tabPage)
            advancementtabgui1.drawIcon(p_191934_1_, p_191934_2_, this.itemRenderer);
         }

         RenderSystem.disableBlend();
      }

      this.font.func_211126_b(I18n.get("gui.advancements"), (float)(p_191934_1_ + 8), (float)(p_191934_2_ + 6), 4210752);
   }

   private void func_191937_d(int p_191937_1_, int p_191937_2_, int p_191937_3_, int p_191937_4_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      if (this.selectedTab != null) {
         RenderSystem.pushMatrix();
         RenderSystem.enableDepthTest();
         RenderSystem.translatef((float)(p_191937_3_ + 9), (float)(p_191937_4_ + 18), 400.0F);
         this.selectedTab.func_192991_a(p_191937_1_ - p_191937_3_ - 9, p_191937_2_ - p_191937_4_ - 18, p_191937_3_, p_191937_4_);
         RenderSystem.disableDepthTest();
         RenderSystem.popMatrix();
      }

      if (this.tabs.size() > 1) {
         for(AdvancementTabGui advancementtabgui : this.tabs.values()) {
            if (advancementtabgui.getPage() == tabPage && advancementtabgui.isMouseOver(p_191937_3_, p_191937_4_, (double)p_191937_1_, (double)p_191937_2_)) {
               this.renderTooltip(advancementtabgui.func_191795_d(), p_191937_1_, p_191937_2_);
            }
         }
      }

   }

   public void onAddAdvancementRoot(Advancement p_191931_1_) {
      AdvancementTabGui advancementtabgui = AdvancementTabGui.create(this.minecraft, this, this.tabs.size(), p_191931_1_);
      if (advancementtabgui != null) {
         this.tabs.put(p_191931_1_, advancementtabgui);
      }
   }

   public void onRemoveAdvancementRoot(Advancement p_191928_1_) {
   }

   public void onAddAdvancementTask(Advancement p_191932_1_) {
      AdvancementTabGui advancementtabgui = this.getTab(p_191932_1_);
      if (advancementtabgui != null) {
         advancementtabgui.addAdvancement(p_191932_1_);
      }

   }

   public void onRemoveAdvancementTask(Advancement p_191929_1_) {
   }

   public void onUpdateAdvancementProgress(Advancement p_191933_1_, AdvancementProgress p_191933_2_) {
      AdvancementEntryGui advancemententrygui = this.getAdvancementWidget(p_191933_1_);
      if (advancemententrygui != null) {
         advancemententrygui.setProgress(p_191933_2_);
      }

   }

   public void onSelectedTabChanged(@Nullable Advancement p_193982_1_) {
      this.selectedTab = this.tabs.get(p_193982_1_);
   }

   public void onAdvancementsCleared() {
      this.tabs.clear();
      this.selectedTab = null;
   }

   @Nullable
   public AdvancementEntryGui getAdvancementWidget(Advancement p_191938_1_) {
      AdvancementTabGui advancementtabgui = this.getTab(p_191938_1_);
      return advancementtabgui == null ? null : advancementtabgui.getWidget(p_191938_1_);
   }

   @Nullable
   private AdvancementTabGui getTab(Advancement p_191935_1_) {
      while(p_191935_1_.getParent() != null) {
         p_191935_1_ = p_191935_1_.getParent();
      }

      return this.tabs.get(p_191935_1_);
   }
}
