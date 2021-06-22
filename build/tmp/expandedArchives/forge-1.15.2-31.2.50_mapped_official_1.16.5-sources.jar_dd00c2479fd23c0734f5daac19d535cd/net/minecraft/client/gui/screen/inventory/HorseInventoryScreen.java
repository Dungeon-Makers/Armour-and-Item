package net.minecraft.client.gui.screen.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.passive.horse.LlamaEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.HorseInventoryContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HorseInventoryScreen extends ContainerScreen<HorseInventoryContainer> {
   private static final ResourceLocation HORSE_INVENTORY_LOCATION = new ResourceLocation("textures/gui/container/horse.png");
   private final AbstractHorseEntity horse;
   private float xMouse;
   private float yMouse;

   public HorseInventoryScreen(HorseInventoryContainer p_i51084_1_, PlayerInventory p_i51084_2_, AbstractHorseEntity p_i51084_3_) {
      super(p_i51084_1_, p_i51084_2_, p_i51084_3_.getDisplayName());
      this.horse = p_i51084_3_;
      this.passEvents = false;
   }

   protected void func_146979_b(int p_146979_1_, int p_146979_2_) {
      this.font.func_211126_b(this.title.func_150254_d(), 8.0F, 6.0F, 4210752);
      this.font.func_211126_b(this.inventory.getDisplayName().func_150254_d(), 8.0F, (float)(this.imageHeight - 96 + 2), 4210752);
   }

   protected void func_146976_a(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(HORSE_INVENTORY_LOCATION);
      int i = (this.width - this.imageWidth) / 2;
      int j = (this.height - this.imageHeight) / 2;
      this.blit(i, j, 0, 0, this.imageWidth, this.imageHeight);
      if (this.horse instanceof AbstractChestedHorseEntity) {
         AbstractChestedHorseEntity abstractchestedhorseentity = (AbstractChestedHorseEntity)this.horse;
         if (abstractchestedhorseentity.hasChest()) {
            this.blit(i + 79, j + 17, 0, this.imageHeight, abstractchestedhorseentity.getInventoryColumns() * 18, 54);
         }
      }

      if (this.horse.func_190685_dA()) {
         this.blit(i + 7, j + 35 - 18, 18, this.imageHeight + 54, 18, 18);
      }

      if (this.horse.func_190677_dK()) {
         if (this.horse instanceof LlamaEntity) {
            this.blit(i + 7, j + 35, 36, this.imageHeight + 54, 18, 18);
         } else {
            this.blit(i + 7, j + 35, 0, this.imageHeight + 54, 18, 18);
         }
      }

      InventoryScreen.renderEntityInInventory(i + 51, j + 60, 17, (float)(i + 51) - this.xMouse, (float)(j + 75 - 50) - this.yMouse, this.horse);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.xMouse = (float)p_render_1_;
      this.yMouse = (float)p_render_2_;
      super.render(p_render_1_, p_render_2_, p_render_3_);
      this.func_191948_b(p_render_1_, p_render_2_);
   }
}