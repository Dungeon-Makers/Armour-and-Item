package net.minecraft.client.gui.screen.inventory;

import com.google.common.collect.Sets;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.util.InputMappings;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ClickType;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ContainerScreen<T extends Container> extends Screen implements IHasContainer<T> {
   public static final ResourceLocation INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/inventory.png");
   protected int imageWidth = 176;
   protected int imageHeight = 166;
   protected final T menu;
   protected final PlayerInventory inventory;
   protected int leftPos;
   protected int topPos;
   protected Slot hoveredSlot;
   private Slot clickedSlot;
   private boolean isSplittingStack;
   private ItemStack draggingItem = ItemStack.EMPTY;
   private int snapbackStartX;
   private int snapbackStartY;
   private Slot snapbackEnd;
   private long snapbackTime;
   private ItemStack snapbackItem = ItemStack.EMPTY;
   private Slot quickdropSlot;
   private long quickdropTime;
   protected final Set<Slot> quickCraftSlots = Sets.newHashSet();
   protected boolean isQuickCrafting;
   private int quickCraftingType;
   private int quickCraftingButton;
   private boolean skipNextRelease;
   private int quickCraftingRemainder;
   private long lastClickTime;
   private Slot lastClickSlot;
   private int lastClickButton;
   private boolean doubleclick;
   private ItemStack lastQuickMoved = ItemStack.EMPTY;

   public ContainerScreen(T p_i51105_1_, PlayerInventory p_i51105_2_, ITextComponent p_i51105_3_) {
      super(p_i51105_3_);
      this.menu = p_i51105_1_;
      this.inventory = p_i51105_2_;
      this.skipNextRelease = true;
   }

   protected void init() {
      super.init();
      this.leftPos = (this.width - this.imageWidth) / 2;
      this.topPos = (this.height - this.imageHeight) / 2;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      int i = this.leftPos;
      int j = this.topPos;
      this.func_146976_a(p_render_3_, p_render_1_, p_render_2_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawBackground(this, p_render_1_, p_render_2_));
      RenderSystem.disableRescaleNormal();
      RenderSystem.disableDepthTest();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      RenderSystem.pushMatrix();
      RenderSystem.translatef((float)i, (float)j, 0.0F);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.enableRescaleNormal();
      this.hoveredSlot = null;
      int k = 240;
      int l = 240;
      RenderSystem.glMultiTexCoord2f(33986, 240.0F, 240.0F);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

      for(int i1 = 0; i1 < this.menu.slots.size(); ++i1) {
         Slot slot = this.menu.slots.get(i1);
         if (slot.isActive()) {
            this.func_146977_a(slot);
         }

         if (this.isHovering(slot, (double)p_render_1_, (double)p_render_2_) && slot.isActive()) {
            this.hoveredSlot = slot;
            RenderSystem.disableDepthTest();
            int j1 = slot.x;
            int k1 = slot.y;
            RenderSystem.colorMask(true, true, true, false);
            int slotColor = this.getSlotColor(i1);
            this.fillGradient(j1, k1, j1 + 16, k1 + 16, slotColor, slotColor);
            RenderSystem.colorMask(true, true, true, true);
            RenderSystem.enableDepthTest();
         }
      }

      this.func_146979_b(p_render_1_, p_render_2_);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.client.event.GuiContainerEvent.DrawForeground(this, p_render_1_, p_render_2_));
      PlayerInventory playerinventory = this.minecraft.player.inventory;
      ItemStack itemstack = this.draggingItem.isEmpty() ? playerinventory.getCarried() : this.draggingItem;
      if (!itemstack.isEmpty()) {
         int j2 = 8;
         int k2 = this.draggingItem.isEmpty() ? 8 : 16;
         String s = null;
         if (!this.draggingItem.isEmpty() && this.isSplittingStack) {
            itemstack = itemstack.copy();
            itemstack.setCount(MathHelper.ceil((float)itemstack.getCount() / 2.0F));
         } else if (this.isQuickCrafting && this.quickCraftSlots.size() > 1) {
            itemstack = itemstack.copy();
            itemstack.setCount(this.quickCraftingRemainder);
            if (itemstack.isEmpty()) {
               s = "" + TextFormatting.YELLOW + "0";
            }
         }

         this.renderFloatingItem(itemstack, p_render_1_ - i - 8, p_render_2_ - j - k2, s);
      }

      if (!this.snapbackItem.isEmpty()) {
         float f = (float)(Util.getMillis() - this.snapbackTime) / 100.0F;
         if (f >= 1.0F) {
            f = 1.0F;
            this.snapbackItem = ItemStack.EMPTY;
         }

         int l2 = this.snapbackEnd.x - this.snapbackStartX;
         int i3 = this.snapbackEnd.y - this.snapbackStartY;
         int l1 = this.snapbackStartX + (int)((float)l2 * f);
         int i2 = this.snapbackStartY + (int)((float)i3 * f);
         this.renderFloatingItem(this.snapbackItem, l1, i2, (String)null);
      }

      RenderSystem.popMatrix();
      RenderSystem.enableDepthTest();
   }

   protected void func_191948_b(int p_191948_1_, int p_191948_2_) {
      if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         this.renderTooltip(this.hoveredSlot.getItem(), p_191948_1_, p_191948_2_);
      }

   }

   private void renderFloatingItem(ItemStack p_146982_1_, int p_146982_2_, int p_146982_3_, String p_146982_4_) {
      RenderSystem.translatef(0.0F, 0.0F, 32.0F);
      this.setBlitOffset(200);
      this.itemRenderer.blitOffset = 200.0F;
      net.minecraft.client.gui.FontRenderer font = p_146982_1_.getItem().getFontRenderer(p_146982_1_);
      if (font == null) font = this.font;
      this.itemRenderer.renderAndDecorateItem(p_146982_1_, p_146982_2_, p_146982_3_);
      this.itemRenderer.renderGuiItemDecorations(font, p_146982_1_, p_146982_2_, p_146982_3_ - (this.draggingItem.isEmpty() ? 0 : 8), p_146982_4_);
      this.setBlitOffset(0);
      this.itemRenderer.blitOffset = 0.0F;
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
   }

   protected abstract void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_);

   private void func_146977_a(Slot p_146977_1_) {
      int i = p_146977_1_.x;
      int j = p_146977_1_.y;
      ItemStack itemstack = p_146977_1_.getItem();
      boolean flag = false;
      boolean flag1 = p_146977_1_ == this.clickedSlot && !this.draggingItem.isEmpty() && !this.isSplittingStack;
      ItemStack itemstack1 = this.minecraft.player.inventory.getCarried();
      String s = null;
      if (p_146977_1_ == this.clickedSlot && !this.draggingItem.isEmpty() && this.isSplittingStack && !itemstack.isEmpty()) {
         itemstack = itemstack.copy();
         itemstack.setCount(itemstack.getCount() / 2);
      } else if (this.isQuickCrafting && this.quickCraftSlots.contains(p_146977_1_) && !itemstack1.isEmpty()) {
         if (this.quickCraftSlots.size() == 1) {
            return;
         }

         if (Container.canItemQuickReplace(p_146977_1_, itemstack1, true) && this.menu.canDragTo(p_146977_1_)) {
            itemstack = itemstack1.copy();
            flag = true;
            Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack, p_146977_1_.getItem().isEmpty() ? 0 : p_146977_1_.getItem().getCount());
            int k = Math.min(itemstack.getMaxStackSize(), p_146977_1_.getMaxStackSize(itemstack));
            if (itemstack.getCount() > k) {
               s = TextFormatting.YELLOW.toString() + k;
               itemstack.setCount(k);
            }
         } else {
            this.quickCraftSlots.remove(p_146977_1_);
            this.recalculateQuickCraftRemaining();
         }
      }

      this.setBlitOffset(100);
      this.itemRenderer.blitOffset = 100.0F;
      if (itemstack.isEmpty() && p_146977_1_.isActive()) {
         Pair<ResourceLocation, ResourceLocation> pair = p_146977_1_.getNoItemIcon();
         if (pair != null) {
            TextureAtlasSprite textureatlassprite = this.minecraft.getTextureAtlas(pair.getFirst()).apply(pair.getSecond());
            this.minecraft.getTextureManager().bind(textureatlassprite.atlas().location());
            blit(i, j, this.getBlitOffset(), 16, 16, textureatlassprite);
            flag1 = true;
         }
      }

      if (!flag1) {
         if (flag) {
            fill(i, j, i + 16, j + 16, -2130706433);
         }

         RenderSystem.enableDepthTest();
         this.itemRenderer.renderAndDecorateItem(this.minecraft.player, itemstack, i, j);
         this.itemRenderer.renderGuiItemDecorations(this.font, itemstack, i, j, s);
      }

      this.itemRenderer.blitOffset = 0.0F;
      this.setBlitOffset(0);
   }

   private void recalculateQuickCraftRemaining() {
      ItemStack itemstack = this.minecraft.player.inventory.getCarried();
      if (!itemstack.isEmpty() && this.isQuickCrafting) {
         if (this.quickCraftingType == 2) {
            this.quickCraftingRemainder = itemstack.getMaxStackSize();
         } else {
            this.quickCraftingRemainder = itemstack.getCount();

            for(Slot slot : this.quickCraftSlots) {
               ItemStack itemstack1 = itemstack.copy();
               ItemStack itemstack2 = slot.getItem();
               int i = itemstack2.isEmpty() ? 0 : itemstack2.getCount();
               Container.getQuickCraftSlotCount(this.quickCraftSlots, this.quickCraftingType, itemstack1, i);
               int j = Math.min(itemstack1.getMaxStackSize(), slot.getMaxStackSize(itemstack1));
               if (itemstack1.getCount() > j) {
                  itemstack1.setCount(j);
               }

               this.quickCraftingRemainder -= itemstack1.getCount() - i;
            }

         }
      }
   }

   private Slot findSlot(double p_195360_1_, double p_195360_3_) {
      for(int i = 0; i < this.menu.slots.size(); ++i) {
         Slot slot = this.menu.slots.get(i);
         if (this.isHovering(slot, p_195360_1_, p_195360_3_) && slot.isActive()) {
            return slot;
         }
      }

      return null;
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
         return true;
      } else {
         InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrCreate(p_mouseClicked_5_);
         boolean flag = this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey);
         Slot slot = this.findSlot(p_mouseClicked_1_, p_mouseClicked_3_);
         long i = Util.getMillis();
         this.doubleclick = this.lastClickSlot == slot && i - this.lastClickTime < 250L && this.lastClickButton == p_mouseClicked_5_;
         this.skipNextRelease = false;
         if (p_mouseClicked_5_ == 0 || p_mouseClicked_5_ == 1 || flag) {
            int j = this.leftPos;
            int k = this.topPos;
            boolean flag1 = this.hasClickedOutside(p_mouseClicked_1_, p_mouseClicked_3_, j, k, p_mouseClicked_5_);
            if (slot != null) flag1 = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
            int l = -1;
            if (slot != null) {
               l = slot.index;
            }

            if (flag1) {
               l = -999;
            }

            if (this.minecraft.options.touchscreen && flag1 && this.minecraft.player.inventory.getCarried().isEmpty()) {
               this.minecraft.setScreen((Screen)null);
               return true;
            }

            if (l != -1) {
               if (this.minecraft.options.touchscreen) {
                  if (slot != null && slot.hasItem()) {
                     this.clickedSlot = slot;
                     this.draggingItem = ItemStack.EMPTY;
                     this.isSplittingStack = p_mouseClicked_5_ == 1;
                  } else {
                     this.clickedSlot = null;
                  }
               } else if (!this.isQuickCrafting) {
                  if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                     if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                        this.slotClicked(slot, l, p_mouseClicked_5_, ClickType.CLONE);
                     } else {
                        boolean flag2 = l != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
                        ClickType clicktype = ClickType.PICKUP;
                        if (flag2) {
                           this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
                           clicktype = ClickType.QUICK_MOVE;
                        } else if (l == -999) {
                           clicktype = ClickType.THROW;
                        }

                        this.slotClicked(slot, l, p_mouseClicked_5_, clicktype);
                     }

                     this.skipNextRelease = true;
                  } else {
                     this.isQuickCrafting = true;
                     this.quickCraftingButton = p_mouseClicked_5_;
                     this.quickCraftSlots.clear();
                     if (p_mouseClicked_5_ == 0) {
                        this.quickCraftingType = 0;
                     } else if (p_mouseClicked_5_ == 1) {
                        this.quickCraftingType = 1;
                     } else if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
                        this.quickCraftingType = 2;
                     }
                  }
               }
            }
         }

         this.lastClickSlot = slot;
         this.lastClickTime = i;
         this.lastClickButton = p_mouseClicked_5_;
         return true;
      }
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      Slot slot = this.findSlot(p_mouseDragged_1_, p_mouseDragged_3_);
      ItemStack itemstack = this.minecraft.player.inventory.getCarried();
      if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
         if (p_mouseDragged_5_ == 0 || p_mouseDragged_5_ == 1) {
            if (this.draggingItem.isEmpty()) {
               if (slot != this.clickedSlot && !this.clickedSlot.getItem().isEmpty()) {
                  this.draggingItem = this.clickedSlot.getItem().copy();
               }
            } else if (this.draggingItem.getCount() > 1 && slot != null && Container.canItemQuickReplace(slot, this.draggingItem, false)) {
               long i = Util.getMillis();
               if (this.quickdropSlot == slot) {
                  if (i - this.quickdropTime > 500L) {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.slotClicked(slot, slot.index, 1, ClickType.PICKUP);
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, 0, ClickType.PICKUP);
                     this.quickdropTime = i + 750L;
                     this.draggingItem.shrink(1);
                  }
               } else {
                  this.quickdropSlot = slot;
                  this.quickdropTime = i;
               }
            }
         }
      } else if (this.isQuickCrafting && slot != null && !itemstack.isEmpty() && (itemstack.getCount() > this.quickCraftSlots.size() || this.quickCraftingType == 2) && Container.canItemQuickReplace(slot, itemstack, true) && slot.mayPlace(itemstack) && this.menu.canDragTo(slot)) {
         this.quickCraftSlots.add(slot);
         this.recalculateQuickCraftRemaining();
      }

      return true;
   }

   public boolean mouseReleased(double p_mouseReleased_1_, double p_mouseReleased_3_, int p_mouseReleased_5_) {
      super.mouseReleased(p_mouseReleased_1_, p_mouseReleased_3_, p_mouseReleased_5_); //Forge, Call parent to release buttons
      Slot slot = this.findSlot(p_mouseReleased_1_, p_mouseReleased_3_);
      int i = this.leftPos;
      int j = this.topPos;
      boolean flag = this.hasClickedOutside(p_mouseReleased_1_, p_mouseReleased_3_, i, j, p_mouseReleased_5_);
      if (slot != null) flag = false; // Forge, prevent dropping of items through slots outside of GUI boundaries
      InputMappings.Input mouseKey = InputMappings.Type.MOUSE.getOrCreate(p_mouseReleased_5_);
      int k = -1;
      if (slot != null) {
         k = slot.index;
      }

      if (flag) {
         k = -999;
      }

      if (this.doubleclick && slot != null && p_mouseReleased_5_ == 0 && this.menu.canTakeItemForPickAll(ItemStack.EMPTY, slot)) {
         if (hasShiftDown()) {
            if (!this.lastQuickMoved.isEmpty()) {
               for(Slot slot2 : this.menu.slots) {
                  if (slot2 != null && slot2.mayPickup(this.minecraft.player) && slot2.hasItem() && slot2.isSameInventory(slot) && Container.canItemQuickReplace(slot2, this.lastQuickMoved, true)) {
                     this.slotClicked(slot2, slot2.index, p_mouseReleased_5_, ClickType.QUICK_MOVE);
                  }
               }
            }
         } else {
            this.slotClicked(slot, k, p_mouseReleased_5_, ClickType.PICKUP_ALL);
         }

         this.doubleclick = false;
         this.lastClickTime = 0L;
      } else {
         if (this.isQuickCrafting && this.quickCraftingButton != p_mouseReleased_5_) {
            this.isQuickCrafting = false;
            this.quickCraftSlots.clear();
            this.skipNextRelease = true;
            return true;
         }

         if (this.skipNextRelease) {
            this.skipNextRelease = false;
            return true;
         }

         if (this.clickedSlot != null && this.minecraft.options.touchscreen) {
            if (p_mouseReleased_5_ == 0 || p_mouseReleased_5_ == 1) {
               if (this.draggingItem.isEmpty() && slot != this.clickedSlot) {
                  this.draggingItem = this.clickedSlot.getItem();
               }

               boolean flag2 = Container.canItemQuickReplace(slot, this.draggingItem, false);
               if (k != -1 && !this.draggingItem.isEmpty() && flag2) {
                  this.slotClicked(this.clickedSlot, this.clickedSlot.index, p_mouseReleased_5_, ClickType.PICKUP);
                  this.slotClicked(slot, k, 0, ClickType.PICKUP);
                  if (this.minecraft.player.inventory.getCarried().isEmpty()) {
                     this.snapbackItem = ItemStack.EMPTY;
                  } else {
                     this.slotClicked(this.clickedSlot, this.clickedSlot.index, p_mouseReleased_5_, ClickType.PICKUP);
                     this.snapbackStartX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
                     this.snapbackStartY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
                     this.snapbackEnd = this.clickedSlot;
                     this.snapbackItem = this.draggingItem;
                     this.snapbackTime = Util.getMillis();
                  }
               } else if (!this.draggingItem.isEmpty()) {
                  this.snapbackStartX = MathHelper.floor(p_mouseReleased_1_ - (double)i);
                  this.snapbackStartY = MathHelper.floor(p_mouseReleased_3_ - (double)j);
                  this.snapbackEnd = this.clickedSlot;
                  this.snapbackItem = this.draggingItem;
                  this.snapbackTime = Util.getMillis();
               }

               this.draggingItem = ItemStack.EMPTY;
               this.clickedSlot = null;
            }
         } else if (this.isQuickCrafting && !this.quickCraftSlots.isEmpty()) {
            this.slotClicked((Slot)null, -999, Container.getQuickcraftMask(0, this.quickCraftingType), ClickType.QUICK_CRAFT);

            for(Slot slot1 : this.quickCraftSlots) {
               this.slotClicked(slot1, slot1.index, Container.getQuickcraftMask(1, this.quickCraftingType), ClickType.QUICK_CRAFT);
            }

            this.slotClicked((Slot)null, -999, Container.getQuickcraftMask(2, this.quickCraftingType), ClickType.QUICK_CRAFT);
         } else if (!this.minecraft.player.inventory.getCarried().isEmpty()) {
            if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
               this.slotClicked(slot, k, p_mouseReleased_5_, ClickType.CLONE);
            } else {
               boolean flag1 = k != -999 && (InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 340) || InputMappings.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), 344));
               if (flag1) {
                  this.lastQuickMoved = slot != null && slot.hasItem() ? slot.getItem().copy() : ItemStack.EMPTY;
               }

               this.slotClicked(slot, k, p_mouseReleased_5_, flag1 ? ClickType.QUICK_MOVE : ClickType.PICKUP);
            }
         }
      }

      if (this.minecraft.player.inventory.getCarried().isEmpty()) {
         this.lastClickTime = 0L;
      }

      this.isQuickCrafting = false;
      return true;
   }

   private boolean isHovering(Slot p_195362_1_, double p_195362_2_, double p_195362_4_) {
      return this.isHovering(p_195362_1_.x, p_195362_1_.y, 16, 16, p_195362_2_, p_195362_4_);
   }

   protected boolean isHovering(int p_195359_1_, int p_195359_2_, int p_195359_3_, int p_195359_4_, double p_195359_5_, double p_195359_7_) {
      int i = this.leftPos;
      int j = this.topPos;
      p_195359_5_ = p_195359_5_ - (double)i;
      p_195359_7_ = p_195359_7_ - (double)j;
      return p_195359_5_ >= (double)(p_195359_1_ - 1) && p_195359_5_ < (double)(p_195359_1_ + p_195359_3_ + 1) && p_195359_7_ >= (double)(p_195359_2_ - 1) && p_195359_7_ < (double)(p_195359_2_ + p_195359_4_ + 1);
   }

   protected void slotClicked(Slot p_184098_1_, int p_184098_2_, int p_184098_3_, ClickType p_184098_4_) {
      if (p_184098_1_ != null) {
         p_184098_2_ = p_184098_1_.index;
      }

      this.minecraft.gameMode.handleInventoryMouseClick(this.menu.containerId, p_184098_2_, p_184098_3_, p_184098_4_, this.minecraft.player);
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         InputMappings.Input mouseKey = InputMappings.getKey(p_keyPressed_1_, p_keyPressed_2_);
         if (p_keyPressed_1_ == 256 || this.minecraft.options.keyInventory.isActiveAndMatches(mouseKey)) {
            this.minecraft.player.closeContainer();
            return true; // Forge MC-146650: Needs to return true when the key is handled.
         }

         if (this.checkHotbarKeyPressed(p_keyPressed_1_, p_keyPressed_2_))
            return true; // Forge MC-146650: Needs to return true when the key is handled.
         if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.minecraft.options.keyPickItem.isActiveAndMatches(mouseKey)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, 0, ClickType.CLONE);
               return true; // Forge MC-146650: Needs to return true when the key is handled.
            } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, hasControlDown() ? 1 : 0, ClickType.THROW);
               return true; // Forge MC-146650: Needs to return true when the key is handled.
            }
         } else if (this.minecraft.options.keyDrop.isActiveAndMatches(mouseKey)) {
            return true; // Forge MC-146650: Emulate MC bug, so we don't drop from hotbar when pressing drop without hovering over a item.
         }

         return false; // Forge MC-146650: Needs to return false when the key is not handled.
      }
   }

   protected boolean checkHotbarKeyPressed(int p_195363_1_, int p_195363_2_) {
      if (this.minecraft.player.inventory.getCarried().isEmpty() && this.hoveredSlot != null) {
         for(int i = 0; i < 9; ++i) {
            if (this.minecraft.options.keyHotbarSlots[i].isActiveAndMatches(InputMappings.getKey(p_195363_1_, p_195363_2_))) {
               this.slotClicked(this.hoveredSlot, this.hoveredSlot.index, i, ClickType.SWAP);
               return true;
            }
         }
      }

      return false;
   }

   public void removed() {
      if (this.minecraft.player != null) {
         this.menu.removed(this.minecraft.player);
      }
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      if (!this.minecraft.player.isAlive() || this.minecraft.player.removed) {
         this.minecraft.player.closeContainer();
      }

   }

   public T getMenu() {
      return this.menu;
   }

   @javax.annotation.Nullable
   public Slot getSlotUnderMouse() { return this.hoveredSlot; }
   public int getGuiLeft() { return leftPos; }
   public int getGuiTop() { return topPos; }
   public int getXSize() { return imageWidth; }
   public int getYSize() { return imageHeight; }

   protected int slotColor = -2130706433;
   public int getSlotColor(int index) {
      return slotColor;
   }
}
