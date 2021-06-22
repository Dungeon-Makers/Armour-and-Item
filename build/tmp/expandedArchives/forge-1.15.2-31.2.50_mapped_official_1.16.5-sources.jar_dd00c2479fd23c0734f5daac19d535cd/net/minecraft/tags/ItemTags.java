package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ItemTags {
   private static TagCollection<Item> HELPER = new TagCollection<>((p_203643_0_) -> {
      return Optional.empty();
   }, "", false, "");
   private static int field_199907_d;
   public static final Tag<Item> WOOL = bind("wool");
   public static final Tag<Item> PLANKS = bind("planks");
   public static final Tag<Item> STONE_BRICKS = bind("stone_bricks");
   public static final Tag<Item> WOODEN_BUTTONS = bind("wooden_buttons");
   public static final Tag<Item> BUTTONS = bind("buttons");
   public static final Tag<Item> CARPETS = bind("carpets");
   public static final Tag<Item> WOODEN_DOORS = bind("wooden_doors");
   public static final Tag<Item> WOODEN_STAIRS = bind("wooden_stairs");
   public static final Tag<Item> WOODEN_SLABS = bind("wooden_slabs");
   public static final Tag<Item> WOODEN_FENCES = bind("wooden_fences");
   public static final Tag<Item> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final Tag<Item> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final Tag<Item> DOORS = bind("doors");
   public static final Tag<Item> SAPLINGS = bind("saplings");
   public static final Tag<Item> LOGS = bind("logs");
   public static final Tag<Item> DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final Tag<Item> OAK_LOGS = bind("oak_logs");
   public static final Tag<Item> BIRCH_LOGS = bind("birch_logs");
   public static final Tag<Item> ACACIA_LOGS = bind("acacia_logs");
   public static final Tag<Item> JUNGLE_LOGS = bind("jungle_logs");
   public static final Tag<Item> SPRUCE_LOGS = bind("spruce_logs");
   public static final Tag<Item> BANNERS = bind("banners");
   public static final Tag<Item> SAND = bind("sand");
   public static final Tag<Item> STAIRS = bind("stairs");
   public static final Tag<Item> SLABS = bind("slabs");
   public static final Tag<Item> WALLS = bind("walls");
   public static final Tag<Item> ANVIL = bind("anvil");
   public static final Tag<Item> RAILS = bind("rails");
   public static final Tag<Item> LEAVES = bind("leaves");
   public static final Tag<Item> TRAPDOORS = bind("trapdoors");
   public static final Tag<Item> SMALL_FLOWERS = bind("small_flowers");
   public static final Tag<Item> BEDS = bind("beds");
   public static final Tag<Item> FENCES = bind("fences");
   public static final Tag<Item> TALL_FLOWERS = bind("tall_flowers");
   public static final Tag<Item> FLOWERS = bind("flowers");
   public static final Tag<Item> BOATS = bind("boats");
   public static final Tag<Item> FISHES = bind("fishes");
   public static final Tag<Item> SIGNS = bind("signs");
   public static final Tag<Item> MUSIC_DISCS = bind("music_discs");
   public static final Tag<Item> COALS = bind("coals");
   public static final Tag<Item> ARROWS = bind("arrows");
   public static final Tag<Item> LECTERN_BOOKS = bind("lectern_books");

   public static void func_199902_a(TagCollection<Item> p_199902_0_) {
      HELPER = p_199902_0_;
      ++field_199907_d;
   }

   public static TagCollection<Item> getAllTags() {
      return HELPER;
   }

   public static int getGeneration() {
      return field_199907_d;
   }

   private static Tag<Item> bind(String p_199901_0_) {
      return new ItemTags.Wrapper(new ResourceLocation(p_199901_0_));
   }

   public static class Wrapper extends Tag<Item> {
      private int field_199890_a = -1;
      private Tag<Item> field_199891_b;

      public Wrapper(ResourceLocation p_i48212_1_) {
         super(p_i48212_1_);
      }

      public boolean func_199685_a_(Item p_199685_1_) {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_199685_a_(p_199685_1_);
      }

      public Collection<Item> func_199885_a() {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_199885_a();
      }

      public Collection<Tag.ITagEntry<Item>> func_200570_b() {
         if (this.field_199890_a != ItemTags.field_199907_d) {
            this.field_199891_b = ItemTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199890_a = ItemTags.field_199907_d;
         }

         return this.field_199891_b.func_200570_b();
      }
   }
}
