package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.gen.FlatGenerationSettings;
import net.minecraft.world.gen.FlatLayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CreateFlatWorldScreen extends Screen {
   private final CreateWorldScreen parent;
   private FlatGenerationSettings generator = FlatGenerationSettings.func_82649_e();
   private String columnType;
   private String columnHeight;
   private CreateFlatWorldScreen.DetailsList list;
   private Button deleteLayerButton;

   public CreateFlatWorldScreen(CreateWorldScreen p_i49700_1_, CompoundNBT p_i49700_2_) {
      super(new TranslationTextComponent("createWorld.customize.flat.title"));
      this.parent = p_i49700_1_;
      this.func_210503_a(p_i49700_2_);
   }

   public String func_210501_h() {
      return this.generator.toString();
   }

   public CompoundNBT func_210504_i() {
      return (CompoundNBT)this.generator.func_210834_a(NBTDynamicOps.INSTANCE).getValue();
   }

   public void func_210502_a(String p_210502_1_) {
      this.generator = FlatGenerationSettings.func_82651_a(p_210502_1_);
   }

   public void func_210503_a(CompoundNBT p_210503_1_) {
      this.generator = FlatGenerationSettings.func_210835_a(new Dynamic<>(NBTDynamicOps.INSTANCE, p_210503_1_));
   }

   protected void init() {
      this.columnType = I18n.get("createWorld.customize.flat.tile");
      this.columnHeight = I18n.get("createWorld.customize.flat.height");
      this.list = new CreateFlatWorldScreen.DetailsList();
      this.children.add(this.list);
      this.deleteLayerButton = this.addButton(new Button(this.width / 2 - 155, this.height - 52, 150, 20, I18n.get("createWorld.customize.flat.removeLayer"), (p_213007_1_) -> {
         if (this.hasValidSelection()) {
            List<FlatLayerInfo> list = this.generator.getLayersInfo();
            int i = this.list.children().indexOf(this.list.getSelected());
            int j = list.size() - i - 1;
            list.remove(j);
            this.list.setSelected(list.isEmpty() ? null : this.list.children().get(Math.min(i, list.size() - 1)));
            this.generator.updateLayers();
            this.updateButtonValidity();
         }
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 52, 150, 20, I18n.get("createWorld.customize.presets"), (p_213011_1_) -> {
         this.minecraft.setScreen(new FlatPresetsScreen(this));
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("gui.done"), (p_213010_1_) -> {
         this.parent.field_146334_a = this.func_210504_i();
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (p_213009_1_) -> {
         this.minecraft.setScreen(this.parent);
         this.generator.updateLayers();
         this.updateButtonValidity();
      }));
      this.generator.updateLayers();
      this.updateButtonValidity();
   }

   public void updateButtonValidity() {
      this.deleteLayerButton.active = this.hasValidSelection();
      this.list.resetRows();
   }

   private boolean hasValidSelection() {
      return this.list.getSelected() != null;
   }

   public void onClose() {
      this.minecraft.setScreen(this.parent);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.list.render(p_render_1_, p_render_2_, p_render_3_);
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 8, 16777215);
      int i = this.width / 2 - 92 - 16;
      this.drawString(this.font, this.columnType, i, 32, 16777215);
      this.drawString(this.font, this.columnHeight, i + 2 + 213 - this.font.width(this.columnHeight), 32, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @OnlyIn(Dist.CLIENT)
   class DetailsList extends ExtendedList<CreateFlatWorldScreen.DetailsList.LayerEntry> {
      public DetailsList() {
         super(CreateFlatWorldScreen.this.minecraft, CreateFlatWorldScreen.this.width, CreateFlatWorldScreen.this.height, 43, CreateFlatWorldScreen.this.height - 60, 24);

         for(int i = 0; i < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++i) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

      }

      public void setSelected(@Nullable CreateFlatWorldScreen.DetailsList.LayerEntry p_setSelected_1_) {
         super.setSelected(p_setSelected_1_);
         if (p_setSelected_1_ != null) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - this.children().indexOf(p_setSelected_1_) - 1);
            Item item = flatlayerinfo.getBlockState().getBlock().asItem();
            if (item != Items.AIR) {
               NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", item.getName(new ItemStack(item)))).getString());
            }
         }

      }

      protected void moveSelection(int p_moveSelection_1_) {
         super.moveSelection(p_moveSelection_1_);
         CreateFlatWorldScreen.this.updateButtonValidity();
      }

      protected boolean isFocused() {
         return CreateFlatWorldScreen.this.getFocused() == this;
      }

      protected int getScrollbarPosition() {
         return this.width - 70;
      }

      public void resetRows() {
         int i = this.children().indexOf(this.getSelected());
         this.clearEntries();

         for(int j = 0; j < CreateFlatWorldScreen.this.generator.getLayersInfo().size(); ++j) {
            this.addEntry(new CreateFlatWorldScreen.DetailsList.LayerEntry());
         }

         List<CreateFlatWorldScreen.DetailsList.LayerEntry> list = this.children();
         if (i >= 0 && i < list.size()) {
            this.setSelected(list.get(i));
         }

      }

      @OnlyIn(Dist.CLIENT)
      class LayerEntry extends ExtendedList.AbstractListEntry<CreateFlatWorldScreen.DetailsList.LayerEntry> {
         private LayerEntry() {
         }

         public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
            FlatLayerInfo flatlayerinfo = CreateFlatWorldScreen.this.generator.getLayersInfo().get(CreateFlatWorldScreen.this.generator.getLayersInfo().size() - p_render_1_ - 1);
            BlockState blockstate = flatlayerinfo.getBlockState();
            Block block = blockstate.getBlock();
            Item item = block.asItem();
            if (item == Items.AIR) {
               if (block == Blocks.WATER) {
                  item = Items.WATER_BUCKET;
               } else if (block == Blocks.LAVA) {
                  item = Items.LAVA_BUCKET;
               }
            }

            ItemStack itemstack = new ItemStack(item);
            String s = item.getName(itemstack).func_150254_d();
            this.func_214389_a(p_render_3_, p_render_2_, itemstack);
            CreateFlatWorldScreen.this.font.func_211126_b(s, (float)(p_render_3_ + 18 + 5), (float)(p_render_2_ + 3), 16777215);
            String s1;
            if (p_render_1_ == 0) {
               s1 = I18n.get("createWorld.customize.flat.layer.top", flatlayerinfo.getHeight());
            } else if (p_render_1_ == CreateFlatWorldScreen.this.generator.getLayersInfo().size() - 1) {
               s1 = I18n.get("createWorld.customize.flat.layer.bottom", flatlayerinfo.getHeight());
            } else {
               s1 = I18n.get("createWorld.customize.flat.layer", flatlayerinfo.getHeight());
            }

            CreateFlatWorldScreen.this.font.func_211126_b(s1, (float)(p_render_3_ + 2 + 213 - CreateFlatWorldScreen.this.font.width(s1)), (float)(p_render_2_ + 3), 16777215);
         }

         public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
            if (p_mouseClicked_5_ == 0) {
               DetailsList.this.setSelected(this);
               CreateFlatWorldScreen.this.updateButtonValidity();
               return true;
            } else {
               return false;
            }
         }

         private void func_214389_a(int p_214389_1_, int p_214389_2_, ItemStack p_214389_3_) {
            this.func_214390_a(p_214389_1_ + 1, p_214389_2_ + 1);
            RenderSystem.enableRescaleNormal();
            if (!p_214389_3_.isEmpty()) {
               CreateFlatWorldScreen.this.itemRenderer.renderGuiItem(p_214389_3_, p_214389_1_ + 2, p_214389_2_ + 2);
            }

            RenderSystem.disableRescaleNormal();
         }

         private void func_214390_a(int p_214390_1_, int p_214390_2_) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            DetailsList.this.minecraft.getTextureManager().bind(AbstractGui.STATS_ICON_LOCATION);
            AbstractGui.blit(p_214390_1_, p_214390_2_, CreateFlatWorldScreen.this.getBlitOffset(), 0.0F, 0.0F, 18, 18, 128, 128);
         }
      }
   }
}