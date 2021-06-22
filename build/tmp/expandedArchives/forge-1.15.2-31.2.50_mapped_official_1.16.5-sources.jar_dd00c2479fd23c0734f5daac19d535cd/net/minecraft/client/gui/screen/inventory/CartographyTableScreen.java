package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.CartographyContainer;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.storage.MapData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CartographyTableScreen extends ContainerScreen<CartographyContainer> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/cartography_table.png");

   public CartographyTableScreen(CartographyContainer p_i51096_1_, PlayerInventory p_i51096_2_, ITextComponent p_i51096_3_) {
      super(p_i51096_1_, p_i51096_2_, p_i51096_3_);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      this.font.func_211126_b(this.title.func_150254_d(), 8.0F, 4.0F, 4210752);
      this.font.func_211126_b(this.inventory.getDisplayName().func_150254_d(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      this.renderBackground();
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      Item item = this.menu.getSlot(1).getItem().getItem();
      boolean flag = item == Items.MAP;
      boolean flag1 = item == Items.PAPER;
      boolean flag2 = item == Items.GLASS_PANE;
      ItemStack itemstack = this.menu.getSlot(0).getItem();
      boolean flag3 = false;
      MapData mapdata;
      if (itemstack.getItem() == Items.FILLED_MAP) {
         mapdata = FilledMapItem.getSavedData(itemstack, this.minecraft.level);
         if (mapdata != null) {
            if (mapdata.locked) {
               flag3 = true;
               if (flag1 || flag2) {
                  this.blit(i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
               }
            }

            if (flag1 && mapdata.scale >= 4) {
               flag3 = true;
               this.blit(i + 35, j + 31, this.imageWidth + 50, 132, 28, 21);
            }
         }
      } else {
         mapdata = null;
      }

      this.func_214107_a(mapdata, flag, flag1, flag2, flag3);
   }

   private void func_214107_a(@Nullable MapData p_214107_1_, boolean p_214107_2_, boolean p_214107_3_, boolean p_214107_4_, boolean p_214107_5_) {
      int i = this.leftPos;
      int j = this.topPos;
      if (p_214107_3_ && !p_214107_5_) {
         this.blit(i + 67, j + 13, this.imageWidth, 66, 66, 66);
         this.renderMap(p_214107_1_, i + 85, j + 31, 0.226F);
      } else if (p_214107_2_) {
         this.blit(i + 67 + 16, j + 13, this.imageWidth, 132, 50, 66);
         this.renderMap(p_214107_1_, i + 86, j + 16, 0.34F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(i + 67, j + 13 + 16, this.imageWidth, 132, 50, 66);
         this.renderMap(p_214107_1_, i + 70, j + 32, 0.34F);
         RenderSystem.popMatrix();
      } else if (p_214107_4_) {
         this.blit(i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_214107_1_, i + 71, j + 17, 0.45F);
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 1.0F);
         this.blit(i + 66, j + 12, 0, this.imageHeight, 66, 66);
         RenderSystem.popMatrix();
      } else {
         this.blit(i + 67, j + 13, this.imageWidth, 0, 66, 66);
         this.renderMap(p_214107_1_, i + 71, j + 17, 0.45F);
      }

   }

   private void renderMap(@Nullable MapData p_214108_1_, int p_214108_2_, int p_214108_3_, float p_214108_4_) {
      if (p_214108_1_ != null) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef((float)p_214108_2_, (float)p_214108_3_, 1.0F);
         RenderSystem.scalef(p_214108_4_, p_214108_4_, 1.0F);
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
         this.minecraft.gameRenderer.getMapRenderer().render(new MatrixStack(), irendertypebuffer$impl, p_214108_1_, true, 15728880);
         irendertypebuffer$impl.endBatch();
         RenderSystem.popMatrix();
      }

   }
}