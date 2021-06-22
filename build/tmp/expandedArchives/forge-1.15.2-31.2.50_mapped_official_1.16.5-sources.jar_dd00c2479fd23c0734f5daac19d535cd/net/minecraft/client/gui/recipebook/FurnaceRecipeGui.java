package net.minecraft.client.gui.recipebook;

import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FurnaceRecipeGui extends AbstractRecipeBookGui {
   protected boolean func_212962_b() {
      return this.book.func_202884_d();
   }

   protected void func_212959_a(boolean p_212959_1_) {
      this.book.func_202882_d(p_212959_1_);
   }

   protected boolean func_212963_d() {
      return this.book.func_202883_c();
   }

   protected void func_212957_c(boolean p_212957_1_) {
      this.book.func_202881_c(p_212957_1_);
   }

   protected String func_212960_g() {
      return "gui.recipebook.toggleRecipes.smeltable";
   }

   protected Set<Item> getFuelItems() {
      return AbstractFurnaceTileEntity.getFuel().keySet();
   }
}