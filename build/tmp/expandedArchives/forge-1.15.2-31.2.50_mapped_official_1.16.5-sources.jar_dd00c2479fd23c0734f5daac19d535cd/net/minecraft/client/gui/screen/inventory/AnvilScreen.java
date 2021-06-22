package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.inventory.container.RepairContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CRenameItemPacket;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AnvilScreen extends ContainerScreen<RepairContainer> implements IContainerListener {
   private static final ResourceLocation ANVIL_LOCATION = new ResourceLocation("textures/gui/container/anvil.png");
   private TextFieldWidget name;

   public AnvilScreen(RepairContainer p_i51103_1_, PlayerInventory p_i51103_2_, ITextComponent p_i51103_3_) {
      super(p_i51103_1_, p_i51103_2_, p_i51103_3_);
   }

   protected void init() {
      super.init();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.name = new TextFieldWidget(this.font, i + 62, j + 24, 103, 12, I18n.get("container.repair"));
      this.name.setCanLoseFocus(false);
      this.name.changeFocus(true);
      this.name.setTextColor(-1);
      this.name.setTextColorUneditable(-1);
      this.name.setBordered(false);
      this.name.setMaxLength(35);
      this.name.setResponder(this::onNameChanged);
      this.children.add(this.name);
      this.menu.addSlotListener(this);
      this.setInitialFocus(this.name);
   }

   public void resize(Minecraft p_resize_1_, int p_resize_2_, int p_resize_3_) {
      String s = this.name.getValue();
      this.init(p_resize_1_, p_resize_2_, p_resize_3_);
      this.name.setValue(s);
   }

   public void removed() {
      super.removed();
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
      this.menu.removeSlotListener(this);
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (p_keyPressed_1_ == 256) {
         this.minecraft.player.closeContainer();
      }

      return !this.name.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) && !this.name.canConsumeInput() ? super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : true;
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      RenderSystem.disableBlend();
      this.font.func_211126_b(this.title.func_150254_d(), 60.0F, 6.0F, 4210752);
      int i = this.menu.getCost();
      if (i > 0) {
         int j = 8453920;
         boolean flag = true;
         String s = I18n.get("container.repair.cost", i);
         if (i >= 40 && !this.minecraft.player.abilities.instabuild) {
            s = I18n.get("container.repair.expensive");
            j = 16736352;
         } else if (!this.menu.getSlot(2).hasItem()) {
            flag = false;
         } else if (!this.menu.getSlot(2).mayPickup(this.inventory.player)) {
            j = 16736352;
         }

         if (flag) {
            int k = this.imageWidth - 8 - this.font.width(s) - 2;
            int l = 69;
            fill(k - 2, 67, this.imageWidth - 8, 79, 1325400064);
            this.font.func_175063_a(s, (float)k, 69.0F, j);
         }
      }

   }

   private void onNameChanged(String p_214075_1_) {
      if (!p_214075_1_.isEmpty()) {
         String s = p_214075_1_;
         Slot slot = this.menu.getSlot(0);
         if (slot != null && slot.hasItem() && !slot.getItem().hasCustomHoverName() && p_214075_1_.equals(slot.getItem().getHoverName().getString())) {
            s = "";
         }

         this.menu.setItemName(s);
         this.minecraft.player.connection.send(new CRenameItemPacket(s));
      }
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      RenderSystem.disableBlend();
      this.name.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ANVIL_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.blit(i + 59, j + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
      if ((this.menu.getSlot(0).hasItem() || this.menu.getSlot(1).hasItem()) && !this.menu.getSlot(2).hasItem()) {
         this.blit(i + 99, j + 45, this.imageWidth, 0, 28, 21);
      }

   }

   public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
      this.slotChanged(p_71110_1_, 0, p_71110_1_.getSlot(0).getItem());
   }

   public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
      if (p_71111_2_ == 0) {
         this.name.setValue(p_71111_3_.isEmpty() ? "" : p_71111_3_.getHoverName().getString());
         this.name.setEditable(!p_71111_3_.isEmpty());
      }

   }

   public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
   }
}