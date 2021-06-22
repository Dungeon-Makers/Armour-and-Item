package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class FluidTagsProvider extends TagsProvider<Fluid> {
   public FluidTagsProvider(DataGenerator p_i49156_1_) {
      super(p_i49156_1_, Registry.FLUID);
   }

   protected void addTags() {
      this.func_200426_a(FluidTags.WATER).func_200573_a(Fluids.WATER, Fluids.FLOWING_WATER);
      this.func_200426_a(FluidTags.LAVA).func_200573_a(Fluids.LAVA, Fluids.FLOWING_LAVA);
   }

   protected Path getPath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/fluids/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Fluid Tags";
   }

   protected void func_200429_a(TagCollection<Fluid> p_200429_1_) {
      FluidTags.func_206953_a(p_200429_1_);
   }
}