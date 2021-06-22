package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChestScreen extends ContainerScreen<ChestContainer> implements IHasContainer<ChestContainer> {
   private static final ResourceLocation CONTAINER_BACKGROUND = new ResourceLocation("textures/gui/container/generic_54.png");
   private final int containerRows;

   public ChestScreen(ChestContainer p_i51095_1_, PlayerInventory p_i51095_2_, ITextComponent p_i51095_3_) {
      super(p_i51095_1_, p_i51095_2_, p_i51095_3_);
      this.passEvents = false;
      int i = 222;
      int j = 114;
      this.containerRows = p_i51095_1_.getRowCount();
      this.imageHeight = 114 + this.containerRows * 18;
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
      this.minecraft.getTextureManager().bind(CONTAINER_BACKGROUND);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(i, j, 0, 0, this.imageWidth, this.containerRows * 18 + 17);
      this.blit(i, j + this.containerRows * 18 + 17, 0, 126, this.imageWidth, 96);
   }
}