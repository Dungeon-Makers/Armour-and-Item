package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;

public class EntityTypeTags {
   private static TagCollection<EntityType<?>> HELPER = new TagCollection<>((p_219758_0_) -> {
      return Optional.empty();
   }, "", false, "");
   private static int field_219767_d;
   public static final Tag<EntityType<?>> SKELETONS = func_219763_a("skeletons");
   public static final Tag<EntityType<?>> RAIDERS = func_219763_a("raiders");
   public static final Tag<EntityType<?>> BEEHIVE_INHABITORS = func_219763_a("beehive_inhabitors");
   public static final Tag<EntityType<?>> ARROWS = func_219763_a("arrows");

   public static void func_219759_a(TagCollection<EntityType<?>> p_219759_0_) {
      HELPER = p_219759_0_;
      ++field_219767_d;
   }

   public static TagCollection<EntityType<?>> getAllTags() {
      return HELPER;
   }

   public static int getGeneration() {
      return field_219767_d;
   }

   private static Tag<EntityType<?>> func_219763_a(String p_219763_0_) {
      return new EntityTypeTags.Wrapper(new ResourceLocation(p_219763_0_));
   }

   public static class Wrapper extends Tag<EntityType<?>> {
      private int field_219743_a = -1;
      private Tag<EntityType<?>> field_219744_b;

      public Wrapper(ResourceLocation p_i50383_1_) {
         super(p_i50383_1_);
      }

      public boolean func_199685_a_(EntityType<?> p_199685_1_) {
         if (this.field_219743_a != EntityTypeTags.field_219767_d) {
            this.field_219744_b = EntityTypeTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_219743_a = EntityTypeTags.field_219767_d;
         }

         return this.field_219744_b.func_199685_a_(p_199685_1_);
      }

      public Collection<EntityType<?>> func_199885_a() {
         if (this.field_219743_a != EntityTypeTags.field_219767_d) {
            this.field_219744_b = EntityTypeTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_219743_a = EntityTypeTags.field_219767_d;
         }

         return this.field_219744_b.func_199885_a();
      }

      public Collection<Tag.ITagEntry<EntityType<?>>> func_200570_b() {
         if (this.field_219743_a != EntityTypeTags.field_219767_d) {
            this.field_219744_b = EntityTypeTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_219743_a = EntityTypeTags.field_219767_d;
         }

         return this.field_219744_b.func_200570_b();
      }
   }
}
