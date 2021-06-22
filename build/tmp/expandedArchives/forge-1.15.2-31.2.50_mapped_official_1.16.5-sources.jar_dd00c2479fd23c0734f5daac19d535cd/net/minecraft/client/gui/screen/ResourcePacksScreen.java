package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.AbstractResourcePackList;
import net.minecraft.client.gui.widget.list.AvailableResourcePackList;
import net.minecraft.client.gui.widget.list.SelectedResourcePackList;
import net.minecraft.client.resources.ClientResourcePackInfo;
import net.minecraft.client.resources.I18n;
import net.minecraft.resources.ResourcePackList;
import net.minecraft.util.Util;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ResourcePacksScreen extends SettingsScreen {
   private AvailableResourcePackList field_146970_i;
   private SelectedResourcePackList field_146967_r;
   private boolean field_175289_s;

   public ResourcePacksScreen(Screen p_i225933_1_, GameSettings p_i225933_2_) {
      super(p_i225933_1_, p_i225933_2_, new TranslationTextComponent("resourcePack.title"));
   }

   protected void init() {
      this.addButton(new Button(this.width / 2 - 154, this.height - 48, 150, 20, I18n.get("resourcePack.openFolder"), (p_214298_1_) -> {
         Util.getPlatform().openFile(this.minecraft.getResourcePackDirectory());
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 48, 150, 20, I18n.get("gui.done"), (p_214296_1_) -> {
         if (this.field_175289_s) {
            List<ClientResourcePackInfo> list1 = Lists.newArrayList();

            for(AbstractResourcePackList.ResourcePackEntry abstractresourcepacklist$resourcepackentry : this.field_146967_r.children()) {
               list1.add(abstractresourcepacklist$resourcepackentry.func_214418_e());
            }

            Collections.reverse(list1);
            this.minecraft.getResourcePackRepository().setSelected(list1);
            this.options.resourcePacks.clear();
            this.options.incompatibleResourcePacks.clear();

            for(ClientResourcePackInfo clientresourcepackinfo2 : list1) {
               if (!clientresourcepackinfo2.isFixedPosition()) {
                  this.options.resourcePacks.add(clientresourcepackinfo2.getId());
                  if (!clientresourcepackinfo2.getCompatibility().isCompatible()) {
                     this.options.incompatibleResourcePacks.add(clientresourcepackinfo2.getId());
                  }
               }
            }

            this.options.save();
            this.minecraft.setScreen(this.lastScreen);
            this.minecraft.reloadResourcePacks();
         } else {
            this.minecraft.setScreen(this.lastScreen);
         }

      }));
      AvailableResourcePackList availableresourcepacklist = this.field_146970_i;
      SelectedResourcePackList selectedresourcepacklist = this.field_146967_r;
      this.field_146970_i = new AvailableResourcePackList(this.minecraft, 200, this.height);
      this.field_146970_i.setLeftPos(this.width / 2 - 4 - 200);
      if (availableresourcepacklist != null) {
         this.field_146970_i.children().addAll(availableresourcepacklist.children());
      }

      this.children.add(this.field_146970_i);
      this.field_146967_r = new SelectedResourcePackList(this.minecraft, 200, this.height);
      this.field_146967_r.setLeftPos(this.width / 2 + 4);
      if (selectedresourcepacklist != null) {
         selectedresourcepacklist.children().forEach((p_230008_1_) -> {
            this.field_146967_r.children().add(p_230008_1_);
            p_230008_1_.func_230009_b_(this.field_146967_r);
         });
      }

      this.children.add(this.field_146967_r);
      if (!this.field_175289_s) {
         this.field_146970_i.children().clear();
         this.field_146967_r.children().clear();
         ResourcePackList<ClientResourcePackInfo> resourcepacklist = this.minecraft.getResourcePackRepository();
         resourcepacklist.reload();
         List<ClientResourcePackInfo> list = Lists.newArrayList(resourcepacklist.getAvailablePacks());
         list.removeAll(resourcepacklist.getSelectedPacks());
         list.removeIf(net.minecraft.resources.ResourcePackInfo::isHidden); // Forge: Hide some resource packs from the UI entirely

         for(ClientResourcePackInfo clientresourcepackinfo : list) {
            this.field_146970_i.func_214365_a(new AbstractResourcePackList.ResourcePackEntry(this.field_146970_i, this, clientresourcepackinfo));
         }

         java.util.Collection<ClientResourcePackInfo> enabledList = resourcepacklist.getSelectedPacks();
         enabledList.removeIf(net.minecraft.resources.ResourcePackInfo::isHidden); // Forge: Hide some resource packs from the UI entirely
         for(ClientResourcePackInfo clientresourcepackinfo1 : Lists.reverse(Lists.newArrayList(resourcepacklist.getSelectedPacks()))) {
            this.field_146967_r.func_214365_a(new AbstractResourcePackList.ResourcePackEntry(this.field_146967_r, this, clientresourcepackinfo1));
         }
      }

   }

   public void func_214300_a(AbstractResourcePackList.ResourcePackEntry p_214300_1_) {
      this.field_146970_i.children().remove(p_214300_1_);
      p_214300_1_.func_214422_a(this.field_146967_r);
      this.func_175288_g();
   }

   public void func_214297_b(AbstractResourcePackList.ResourcePackEntry p_214297_1_) {
      this.field_146967_r.children().remove(p_214297_1_);
      this.field_146970_i.func_214365_a(p_214297_1_);
      this.func_175288_g();
   }

   public boolean func_214299_c(AbstractResourcePackList.ResourcePackEntry p_214299_1_) {
      return this.field_146967_r.children().contains(p_214299_1_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderDirtBackground(0);
      this.field_146970_i.render(p_render_1_, p_render_2_, p_render_3_);
      this.field_146967_r.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 16, 16777215);
      this.drawCenteredString(this.font, I18n.get("resourcePack.folderInfo"), this.width / 2 - 77, this.height - 26, 8421504);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void func_175288_g() {
      this.field_175289_s = true;
   }
}
