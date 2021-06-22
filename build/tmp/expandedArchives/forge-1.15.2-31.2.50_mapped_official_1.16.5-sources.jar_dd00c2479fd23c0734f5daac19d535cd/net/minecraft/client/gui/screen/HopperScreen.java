package net.minecraft.client.gui.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HopperContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HopperScreen extends ContainerScreen<HopperContainer> {
   private static final ResourceLocation HOPPER_LOCATION = new ResourceLocation("textures/gui/container/hopper.png");

   public HopperScreen(HopperContainer p_i51085_1_, PlayerInventory p_i51085_2_, ITextComponent p_i51085_3_) {
      super(p_i51085_1_, p_i51085_2_, p_i51085_3_);
      this.passEvents = false;
      this.imageHeight = 133;
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      this.font.func_211126_b(this.title.func_150254_d(), 8.0F, 6.0F, 4210752);
      this.font.func_211126_b(this.inventory.getDisplayName().func_150254_d(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(HOPPER_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
   }
}