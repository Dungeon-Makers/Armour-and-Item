package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;

public class FluidTags {
   private static TagCollection<Fluid> HELPER = new TagCollection<>((p_206955_0_) -> {
      return Optional.empty();
   }, "", false, "");
   private static int field_206962_d;
   public static final Tag<Fluid> WATER = bind("water");
   public static final Tag<Fluid> LAVA = bind("lava");

   public static void func_206953_a(TagCollection<Fluid> p_206953_0_) {
      HELPER = p_206953_0_;
      ++field_206962_d;
   }

   public static TagCollection<Fluid> getAllTags() {
      return HELPER;
   }

   public static int getGeneration() {
      return field_206962_d;
   }

   private static Tag<Fluid> bind(String p_206956_0_) {
      return new FluidTags.Wrapper(new ResourceLocation(p_206956_0_));
   }

   public static class Wrapper extends Tag<Fluid> {
      private int field_206950_a = -1;
      private Tag<Fluid> field_206951_b;

      public Wrapper(ResourceLocation p_i49117_1_) {
         super(p_i49117_1_);
      }

      public boolean func_199685_a_(Fluid p_199685_1_) {
         if (this.field_206950_a != FluidTags.field_206962_d) {
            this.field_206951_b = FluidTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_206950_a = FluidTags.field_206962_d;
         }

         return this.field_206951_b.func_199685_a_(p_199685_1_);
      }

      public Collection<Fluid> func_199885_a() {
         if (this.field_206950_a != FluidTags.field_206962_d) {
            this.field_206951_b = FluidTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_206950_a = FluidTags.field_206962_d;
         }

         return this.field_206951_b.func_199885_a();
      }

      public Collection<Tag.ITagEntry<Fluid>> func_200570_b() {
         if (this.field_206950_a != FluidTags.field_206962_d) {
            this.field_206951_b = FluidTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_206950_a = FluidTags.field_206962_d;
         }

         return this.field_206951_b.func_200570_b();
      }
   }
}
