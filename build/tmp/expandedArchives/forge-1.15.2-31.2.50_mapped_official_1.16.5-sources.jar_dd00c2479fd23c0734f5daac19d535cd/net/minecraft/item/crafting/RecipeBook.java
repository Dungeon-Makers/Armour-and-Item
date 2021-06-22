package net.minecraft.item.crafting;

import com.google.common.collect.Sets;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.inventory.container.BlastFurnaceContainer;
import net.minecraft.inventory.container.FurnaceContainer;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.SmokerContainer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RecipeBook {
   protected final Set<ResourceLocation> known = Sets.newHashSet();
   protected final Set<ResourceLocation> highlight = Sets.newHashSet();
   protected boolean field_192818_b;
   protected boolean field_192819_c;
   protected boolean field_202885_e;
   protected boolean field_202886_f;
   protected boolean field_216763_g;
   protected boolean field_216764_h;
   protected boolean field_216765_i;
   protected boolean field_216766_j;

   public void copyOverData(RecipeBook p_193824_1_) {
      this.known.clear();
      this.highlight.clear();
      this.known.addAll(p_193824_1_.known);
      this.highlight.addAll(p_193824_1_.highlight);
   }

   public void add(IRecipe<?> p_194073_1_) {
      if (!p_194073_1_.isSpecial()) {
         this.add(p_194073_1_.getId());
      }

   }

   protected void add(ResourceLocation p_209118_1_) {
      this.known.add(p_209118_1_);
   }

   public boolean contains(@Nullable IRecipe<?> p_193830_1_) {
      return p_193830_1_ == null ? false : this.known.contains(p_193830_1_.getId());
   }

   public boolean contains(ResourceLocation p_226144_1_) {
      return this.known.contains(p_226144_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public void remove(IRecipe<?> p_193831_1_) {
      this.remove(p_193831_1_.getId());
   }

   protected void remove(ResourceLocation p_209119_1_) {
      this.known.remove(p_209119_1_);
      this.highlight.remove(p_209119_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean willHighlight(IRecipe<?> p_194076_1_) {
      return this.highlight.contains(p_194076_1_.getId());
   }

   public void removeHighlight(IRecipe<?> p_194074_1_) {
      this.highlight.remove(p_194074_1_.getId());
   }

   public void addHighlight(IRecipe<?> p_193825_1_) {
      this.addHighlight(p_193825_1_.getId());
   }

   protected void addHighlight(ResourceLocation p_209120_1_) {
      this.highlight.add(p_209120_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_192812_b() {
      return this.field_192818_b;
   }

   public void func_192813_a(boolean p_192813_1_) {
      this.field_192818_b = p_192813_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_203432_a(RecipeBookContainer<?> p_203432_1_) {
      if (p_203432_1_ instanceof FurnaceContainer) {
         return this.field_202886_f;
      } else if (p_203432_1_ instanceof BlastFurnaceContainer) {
         return this.field_216764_h;
      } else {
         return p_203432_1_ instanceof SmokerContainer ? this.field_216766_j : this.field_192819_c;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_192815_c() {
      return this.field_192819_c;
   }

   public void func_192810_b(boolean p_192810_1_) {
      this.field_192819_c = p_192810_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202883_c() {
      return this.field_202885_e;
   }

   public void func_202881_c(boolean p_202881_1_) {
      this.field_202885_e = p_202881_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202884_d() {
      return this.field_202886_f;
   }

   public void func_202882_d(boolean p_202882_1_) {
      this.field_202886_f = p_202882_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216758_e() {
      return this.field_216763_g;
   }

   public void func_216755_e(boolean p_216755_1_) {
      this.field_216763_g = p_216755_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216761_f() {
      return this.field_216764_h;
   }

   public void func_216756_f(boolean p_216756_1_) {
      this.field_216764_h = p_216756_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216759_g() {
      return this.field_216765_i;
   }

   public void func_216757_g(boolean p_216757_1_) {
      this.field_216765_i = p_216757_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_216762_h() {
      return this.field_216766_j;
   }

   public void func_216760_h(boolean p_216760_1_) {
      this.field_216766_j = p_216760_1_;
   }
}