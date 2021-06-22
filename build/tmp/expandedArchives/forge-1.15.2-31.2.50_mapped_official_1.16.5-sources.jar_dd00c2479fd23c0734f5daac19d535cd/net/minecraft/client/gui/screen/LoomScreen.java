package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.BannerTileEntityRenderer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.LoomContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.BannerItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LoomScreen extends ContainerScreen<LoomContainer> {
   private static final ResourceLocation BG_LOCATION = new ResourceLocation("textures/gui/container/loom.png");
   private static final int TOTAL_PATTERN_ROWS = (BannerPattern.COUNT - 5 - 1 + 4 - 1) / 4;
   private final ModelRenderer flag;
   @Nullable
   private List<Pair<BannerPattern, DyeColor>> resultBannerPatterns;
   private ItemStack bannerStack = ItemStack.EMPTY;
   private ItemStack dyeStack = ItemStack.EMPTY;
   private ItemStack patternStack = ItemStack.EMPTY;
   private boolean displayPatterns;
   private boolean displaySpecialPattern;
   private boolean hasMaxPatterns;
   private float scrollOffs;
   private boolean scrolling;
   private int startIndex = 1;

   public LoomScreen(LoomContainer p_i51081_1_, PlayerInventory p_i51081_2_, ITextComponent p_i51081_3_) {
      super(p_i51081_1_, p_i51081_2_, p_i51081_3_);
      this.flag = BannerTileEntityRenderer.makeFlag();
      p_i51081_1_.registerUpdateListener(this::containerChanged);
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
      this.minecraft.getTextureManager().bind(BG_LOCATION);
      int i = this.leftPos;
      int j = this.topPos;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      Slot slot = this.menu.getBannerSlot();
      Slot slot1 = this.menu.getDyeSlot();
      Slot slot2 = this.menu.getPatternSlot();
      Slot slot3 = this.menu.getResultSlot();
      if (!slot.hasItem()) {
         this.blit(i + slot.x, j + slot.y, this.imageWidth, 0, 16, 16);
      }

      if (!slot1.hasItem()) {
         this.blit(i + slot1.x, j + slot1.y, this.imageWidth + 16, 0, 16, 16);
      }

      if (!slot2.hasItem()) {
         this.blit(i + slot2.x, j + slot2.y, this.imageWidth + 32, 0, 16, 16);
      }

      int k = (int)(41.0F * this.scrollOffs);
      this.blit(i + 119, j + 13 + k, 232 + (this.displayPatterns ? 0 : 12), 0, 12, 15);
      RenderHelper.setupForFlatItems();
      if (this.resultBannerPatterns != null && !this.hasMaxPatterns) {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
         MatrixStack matrixstack = new MatrixStack();
         matrixstack.translate((double)(i + 139), (double)(j + 52), 0.0D);
         matrixstack.scale(24.0F, -24.0F, 1.0F);
         matrixstack.translate(0.5D, 0.5D, 0.5D);
         float f = 0.6666667F;
         matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
         this.flag.xRot = 0.0F;
         this.flag.y = -32.0F;
         BannerTileEntityRenderer.renderPatterns(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, this.resultBannerPatterns);
         irendertypebuffer$impl.endBatch();
      } else if (this.hasMaxPatterns) {
         this.blit(i + slot3.x - 2, j + slot3.y - 2, this.imageWidth, 17, 17, 16);
      }

      if (this.displayPatterns) {
         int i2 = i + 60;
         int k2 = j + 13;
         int i3 = this.startIndex + 16;

         for(int l = this.startIndex; l < i3 && l < BannerPattern.COUNT - 5; ++l) {
            int i1 = l - this.startIndex;
            int j1 = i2 + i1 % 4 * 14;
            int k1 = k2 + i1 / 4 * 14;
            this.minecraft.getTextureManager().bind(BG_LOCATION);
            int l1 = this.imageHeight;
            if (l == this.menu.getSelectedBannerPatternIndex()) {
               l1 += 14;
            } else if (p_146976_2_ >= j1 && p_146976_3_ >= k1 && p_146976_2_ < j1 + 14 && p_146976_3_ < k1 + 14) {
               l1 += 28;
            }

            this.blit(j1, k1, 0, l1, 14, 14);
            this.renderPattern(l, j1, k1);
         }
      } else if (this.displaySpecialPattern) {
         int j2 = i + 60;
         int l2 = j + 13;
         this.minecraft.getTextureManager().bind(BG_LOCATION);
         this.blit(j2, l2, 0, this.imageHeight, 14, 14);
         int j3 = this.menu.getSelectedBannerPatternIndex();
         this.renderPattern(j3, j2, l2);
      }

      RenderHelper.setupFor3DItems();
   }

   private void renderPattern(int p_228190_1_, int p_228190_2_, int p_228190_3_) {
      ItemStack itemstack = new ItemStack(Items.GRAY_BANNER);
      CompoundNBT compoundnbt = itemstack.getOrCreateTagElement("BlockEntityTag");
      ListNBT listnbt = (new BannerPattern.Builder()).addPattern(BannerPattern.BASE, DyeColor.GRAY).addPattern(BannerPattern.values()[p_228190_1_], DyeColor.WHITE).toListTag();
      compoundnbt.put("Patterns", listnbt);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.pushPose();
      matrixstack.translate((double)((float)p_228190_2_ + 0.5F), (double)(p_228190_3_ + 16), 0.0D);
      matrixstack.scale(6.0F, -6.0F, 1.0F);
      matrixstack.translate(0.5D, 0.5D, 0.0D);
      matrixstack.translate(0.5D, 0.5D, 0.5D);
      float f = 0.6666667F;
      matrixstack.scale(0.6666667F, -0.6666667F, -0.6666667F);
      IRenderTypeBuffer.Impl irendertypebuffer$impl = this.minecraft.renderBuffers().bufferSource();
      this.flag.xRot = 0.0F;
      this.flag.y = -32.0F;
      List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.createPatterns(DyeColor.GRAY, BannerTileEntity.getItemPatterns(itemstack));
      BannerTileEntityRenderer.renderPatterns(matrixstack, irendertypebuffer$impl, 15728880, OverlayTexture.NO_OVERLAY, this.flag, ModelBakery.BANNER_BASE, true, list);
      matrixstack.popPose();
      irendertypebuffer$impl.endBatch();
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      this.scrolling = false;
      if (this.displayPatterns) {
         int i = this.leftPos + 60;
         int j = this.topPos + 13;
         int k = this.startIndex + 16;

         for(int l = this.startIndex; l < k; ++l) {
            int i1 = l - this.startIndex;
            double d0 = p_mouseClicked_1_ - (double)(i + i1 % 4 * 14);
            double d1 = p_mouseClicked_3_ - (double)(j + i1 / 4 * 14);
            if (d0 >= 0.0D && d1 >= 0.0D && d0 < 14.0D && d1 < 14.0D && this.menu.clickMenuButton(this.minecraft.player, l)) {
               Minecraft.getInstance().getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_LOOM_SELECT_PATTERN, 1.0F));
               this.minecraft.gameMode.handleInventoryButtonClick((this.menu).containerId, l);
               return true;
            }
         }

         i = this.leftPos + 119;
         j = this.topPos + 9;
         if (p_mouseClicked_1_ >= (double)i && p_mouseClicked_1_ < (double)(i + 12) && p_mouseClicked_3_ >= (double)j && p_mouseClicked_3_ < (double)(j + 56)) {
            this.scrolling = true;
         }
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (this.scrolling && this.displayPatterns) {
         int i = this.topPos + 13;
         int j = i + 56;
         this.scrollOffs = ((float)p_mouseDragged_3_ - (float)i - 7.5F) / ((float)(j - i) - 15.0F);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         int k = TOTAL_PATTERN_ROWS - 4;
         int l = (int)((double)(this.scrollOffs * (float)k) + 0.5D);
         if (l < 0) {
            l = 0;
         }

         this.startIndex = 1 + l * 4;
         return true;
      } else {
         return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
      }
   }

   public boolean mouseScrolled(double p_mouseScrolled_1_, double p_mouseScrolled_3_, double p_mouseScrolled_5_) {
      if (this.displayPatterns) {
         int i = TOTAL_PATTERN_ROWS - 4;
         this.scrollOffs = (float)((double)this.scrollOffs - p_mouseScrolled_5_ / (double)i);
         this.scrollOffs = MathHelper.clamp(this.scrollOffs, 0.0F, 1.0F);
         this.startIndex = 1 + (int)((double)(this.scrollOffs * (float)i) + 0.5D) * 4;
      }

      return true;
   }

   protected boolean hasClickedOutside(double p_195361_1_, double p_195361_3_, int p_195361_5_, int p_195361_6_, int p_195361_7_) {
      return p_195361_1_ < (double)p_195361_5_ || p_195361_3_ < (double)p_195361_6_ || p_195361_1_ >= (double)(p_195361_5_ + this.imageWidth) || p_195361_3_ >= (double)(p_195361_6_ + this.imageHeight);
   }

   private void containerChanged() {
      ItemStack itemstack = this.menu.getResultSlot().getItem();
      if (itemstack.isEmpty()) {
         this.resultBannerPatterns = null;
      } else {
         this.resultBannerPatterns = BannerTileEntity.createPatterns(((BannerItem)itemstack.getItem()).getColor(), BannerTileEntity.getItemPatterns(itemstack));
      }

      ItemStack itemstack1 = this.menu.getBannerSlot().getItem();
      ItemStack itemstack2 = this.menu.getDyeSlot().getItem();
      ItemStack itemstack3 = this.menu.getPatternSlot().getItem();
      CompoundNBT compoundnbt = itemstack1.getOrCreateTagElement("BlockEntityTag");
      this.hasMaxPatterns = compoundnbt.contains("Patterns", 9) && !itemstack1.isEmpty() && compoundnbt.getList("Patterns", 10).size() >= 6;
      if (this.hasMaxPatterns) {
         this.resultBannerPatterns = null;
      }

      if (!ItemStack.matches(itemstack1, this.bannerStack) || !ItemStack.matches(itemstack2, this.dyeStack) || !ItemStack.matches(itemstack3, this.patternStack)) {
         this.displayPatterns = !itemstack1.isEmpty() && !itemstack2.isEmpty() && itemstack3.isEmpty() && !this.hasMaxPatterns;
         this.displaySpecialPattern = !this.hasMaxPatterns && !itemstack3.isEmpty() && !itemstack1.isEmpty() && !itemstack2.isEmpty();
      }

      this.bannerStack = itemstack1.copy();
      this.dyeStack = itemstack2.copy();
      this.patternStack = itemstack3.copy();
   }
}