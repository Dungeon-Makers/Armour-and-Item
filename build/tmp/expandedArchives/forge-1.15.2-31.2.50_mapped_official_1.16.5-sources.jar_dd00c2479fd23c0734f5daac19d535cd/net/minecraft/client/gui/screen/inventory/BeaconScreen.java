package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.AbstractButton;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.BeaconContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.play.client.CCloseWindowPacket;
import net.minecraft.network.play.client.CUpdateBeaconPacket;
import net.minecraft.potion.Effect;
import net.minecraft.potion.Effects;
import net.minecraft.tileentity.BeaconTileEntity;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BeaconScreen extends ContainerScreen<BeaconContainer> {
   private static final ResourceLocation BEACON_LOCATION = new ResourceLocation("textures/gui/container/beacon.png");
   private BeaconScreen.ConfirmButton confirmButton;
   private boolean initPowerButtons;
   private Effect primary;
   private Effect secondary;

   public BeaconScreen(final BeaconContainer p_i51102_1_, PlayerInventory p_i51102_2_, ITextComponent p_i51102_3_) {
      super(p_i51102_1_, p_i51102_2_, p_i51102_3_);
      this.imageWidth = 230;
      this.imageHeight = 219;
      p_i51102_1_.addSlotListener(new IContainerListener() {
         public void refreshContainer(Container p_71110_1_, NonNullList<ItemStack> p_71110_2_) {
         }

         public void slotChanged(Container p_71111_1_, int p_71111_2_, ItemStack p_71111_3_) {
         }

         public void setContainerData(Container p_71112_1_, int p_71112_2_, int p_71112_3_) {
            BeaconScreen.this.primary = p_i51102_1_.getPrimaryEffect();
            BeaconScreen.this.secondary = p_i51102_1_.getSecondaryEffect();
            BeaconScreen.this.initPowerButtons = true;
         }
      });
   }

   protected void init() {
      super.init();
      this.confirmButton = this.addButton(new BeaconScreen.ConfirmButton(this.leftPos + 164, this.topPos + 107));
      this.addButton(new BeaconScreen.CancelButton(this.leftPos + 190, this.topPos + 107));
      this.initPowerButtons = true;
      this.confirmButton.active = false;
   }

   public void tick() {
      super.tick();
      int i = this.menu.getLevels();
      if (this.initPowerButtons && i >= 0) {
         this.initPowerButtons = false;

         for(int j = 0; j <= 2; ++j) {
            int k = BeaconTileEntity.BEACON_EFFECTS[j].length;
            int l = k * 22 + (k - 1) * 2;

            for(int i1 = 0; i1 < k; ++i1) {
               Effect effect = BeaconTileEntity.BEACON_EFFECTS[j][i1];
               BeaconScreen.PowerButton beaconscreen$powerbutton = new BeaconScreen.PowerButton(this.leftPos + 76 + i1 * 24 - l / 2, this.topPos + 22 + j * 25, effect, true);
               this.addButton(beaconscreen$powerbutton);
               if (j >= i) {
                  beaconscreen$powerbutton.active = false;
               } else if (effect == this.primary) {
                  beaconscreen$powerbutton.setSelected(true);
               }
            }
         }

         int j1 = 3;
         int k1 = BeaconTileEntity.BEACON_EFFECTS[3].length + 1;
         int l1 = k1 * 22 + (k1 - 1) * 2;

         for(int i2 = 0; i2 < k1 - 1; ++i2) {
            Effect effect1 = BeaconTileEntity.BEACON_EFFECTS[3][i2];
            BeaconScreen.PowerButton beaconscreen$powerbutton2 = new BeaconScreen.PowerButton(this.leftPos + 167 + i2 * 24 - l1 / 2, this.topPos + 47, effect1, false);
            this.addButton(beaconscreen$powerbutton2);
            if (3 >= i) {
               beaconscreen$powerbutton2.active = false;
            } else if (effect1 == this.secondary) {
               beaconscreen$powerbutton2.setSelected(true);
            }
         }

         if (this.primary != null) {
            BeaconScreen.PowerButton beaconscreen$powerbutton1 = new BeaconScreen.PowerButton(this.leftPos + 167 + (k1 - 1) * 24 - l1 / 2, this.topPos + 47, this.primary, false);
            this.addButton(beaconscreen$powerbutton1);
            if (3 >= i) {
               beaconscreen$powerbutton1.active = false;
            } else if (this.primary == this.secondary) {
               beaconscreen$powerbutton1.setSelected(true);
            }
         }
      }

      this.confirmButton.active = this.menu.hasPayment() && this.primary != null;
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.primary"), 62, 10, 14737632);
      this.drawCenteredString(this.font, I18n.get("block.minecraft.beacon.secondary"), 169, 10, 14737632);

      for(Widget widget : this.buttons) {
         if (widget.isHovered()) {
            widget.renderToolTip(p_146979_1_ - this.leftPos, p_146979_2_ - this.topPos);
            break;
         }
      }

   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BEACON_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      this.itemRenderer.blitOffset = 100.0F;
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.EMERALD), i + 42, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.DIAMOND), i + 42 + 22, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.GOLD_INGOT), i + 42 + 44, j + 109);
      this.itemRenderer.renderAndDecorateItem(new ItemStack(Items.IRON_INGOT), i + 42 + 66, j + 109);
      this.itemRenderer.blitOffset = 0.0F;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Button extends AbstractButton {
      private boolean selected;

      protected Button(int p_i50826_1_, int p_i50826_2_) {
         super(p_i50826_1_, p_i50826_2_, 22, 22, "");
      }

      public void renderButton(int p_renderButton_1_, int p_renderButton_2_, float p_renderButton_3_) {
         Minecraft.getInstance().getTextureManager().bind(BeaconScreen.BEACON_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int i = 219;
         int j = 0;
         if (!this.active) {
            j += this.width * 2;
         } else if (this.selected) {
            j += this.width * 1;
         } else if (this.isHovered()) {
            j += this.width * 3;
         }

         this.blit(this.x, this.y, j, 219, this.width, this.height);
         this.func_212945_a();
      }

      protected abstract void func_212945_a();

      public boolean isSelected() {
         return this.selected;
      }

      public void setSelected(boolean p_146140_1_) {
         this.selected = p_146140_1_;
      }
   }

   @OnlyIn(Dist.CLIENT)
   class CancelButton extends BeaconScreen.SpriteButton {
      public CancelButton(int p_i50829_2_, int p_i50829_3_) {
         super(p_i50829_2_, p_i50829_3_, 112, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.player.connection.send(new CCloseWindowPacket(BeaconScreen.this.minecraft.player.containerMenu.containerId));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.cancel"), p_renderToolTip_1_, p_renderToolTip_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class ConfirmButton extends BeaconScreen.SpriteButton {
      public ConfirmButton(int p_i50828_2_, int p_i50828_3_) {
         super(p_i50828_2_, p_i50828_3_, 90, 220);
      }

      public void onPress() {
         BeaconScreen.this.minecraft.getConnection().send(new CUpdateBeaconPacket(Effect.getId(BeaconScreen.this.primary), Effect.getId(BeaconScreen.this.secondary)));
         BeaconScreen.this.minecraft.player.connection.send(new CCloseWindowPacket(BeaconScreen.this.minecraft.player.containerMenu.containerId));
         BeaconScreen.this.minecraft.setScreen((Screen)null);
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         BeaconScreen.this.renderTooltip(I18n.get("gui.done"), p_renderToolTip_1_, p_renderToolTip_2_);
      }
   }

   @OnlyIn(Dist.CLIENT)
   class PowerButton extends BeaconScreen.Button {
      private final Effect effect;
      private final TextureAtlasSprite sprite;
      private final boolean isPrimary;

      public PowerButton(int p_i50827_2_, int p_i50827_3_, Effect p_i50827_4_, boolean p_i50827_5_) {
         super(p_i50827_2_, p_i50827_3_);
         this.effect = p_i50827_4_;
         this.sprite = Minecraft.getInstance().getMobEffectTextures().get(p_i50827_4_);
         this.isPrimary = p_i50827_5_;
      }

      public void onPress() {
         if (!this.isSelected()) {
            if (this.isPrimary) {
               BeaconScreen.this.primary = this.effect;
            } else {
               BeaconScreen.this.secondary = this.effect;
            }

            BeaconScreen.this.buttons.clear();
            BeaconScreen.this.children.clear();
            BeaconScreen.this.init();
            BeaconScreen.this.tick();
         }
      }

      public void renderToolTip(int p_renderToolTip_1_, int p_renderToolTip_2_) {
         String s = I18n.get(this.effect.getDescriptionId());
         if (!this.isPrimary && this.effect != Effects.REGENERATION) {
            s = s + " II";
         }

         BeaconScreen.this.renderTooltip(s, p_renderToolTip_1_, p_renderToolTip_2_);
      }

      protected void func_212945_a() {
         Minecraft.getInstance().getTextureManager().bind(this.sprite.atlas().location());
         blit(this.x + 2, this.y + 2, this.getBlitOffset(), 18, 18, this.sprite);
      }
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class SpriteButton extends BeaconScreen.Button {
      private final int iconX;
      private final int iconY;

      protected SpriteButton(int p_i50825_1_, int p_i50825_2_, int p_i50825_3_, int p_i50825_4_) {
         super(p_i50825_1_, p_i50825_2_);
         this.iconX = p_i50825_3_;
         this.iconY = p_i50825_4_;
      }

      protected void func_212945_a() {
         this.blit(this.x + 2, this.y + 2, this.iconX, this.iconY, 18, 18);
      }
   }
}