package net.minecraft.client.gui.toasts;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeToast implements IToast {
   private final List<IRecipe<?>> recipes = Lists.newArrayList();
   private long lastChanged;
   private boolean changed;

   public RecipeToast(IRecipe<?> p_i48624_1_) {
      this.recipes.add(p_i48624_1_);
   }

   public IToast.Visibility func_193653_a(ToastGui p_193653_1_, long p_193653_2_) {
      if (this.changed) {
         this.lastChanged = p_193653_2_;
         this.changed = false;
      }

      if (this.recipes.isEmpty()) {
         return IToast.Visibility.HIDE;
      } else {
         p_193653_1_.getMinecraft().getTextureManager().bind(TEXTURE);
         RenderSystem.color3f(1.0F, 1.0F, 1.0F);
         p_193653_1_.blit(0, 0, 0, 32, 160, 32);
         p_193653_1_.getMinecraft().font.func_211126_b(I18n.get("recipe.toast.title"), 30.0F, 7.0F, -11534256);
         p_193653_1_.getMinecraft().font.func_211126_b(I18n.get("recipe.toast.description"), 30.0F, 18.0F, -16777216);
         IRecipe<?> irecipe = this.recipes.get((int)((p_193653_2_ * (long)this.recipes.size() / 5000L) % (long)this.recipes.size())); //Forge: fix math so that it doesn't divide by 0 when there are more than 5000 recipes
         ItemStack itemstack = irecipe.getToastSymbol();
         RenderSystem.pushMatrix();
         RenderSystem.scalef(0.6F, 0.6F, 1.0F);
         p_193653_1_.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, itemstack, 3, 3);
         RenderSystem.popMatrix();
         p_193653_1_.getMinecraft().getItemRenderer().renderAndDecorateItem((LivingEntity)null, irecipe.getResultItem(), 8, 8);
         return p_193653_2_ - this.lastChanged >= 5000L ? IToast.Visibility.HIDE : IToast.Visibility.SHOW;
      }
   }

   public void addItem(IRecipe<?> p_202905_1_) {
      if (this.recipes.add(p_202905_1_)) {
         this.changed = true;
      }

   }

   public static void addOrUpdate(ToastGui p_193665_0_, IRecipe<?> p_193665_1_) {
      RecipeToast recipetoast = p_193665_0_.getToast(RecipeToast.class, NO_TOKEN);
      if (recipetoast == null) {
         p_193665_0_.addToast(new RecipeToast(p_193665_1_));
      } else {
         recipetoast.addItem(p_193665_1_);
      }

   }
}
