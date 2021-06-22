package net.minecraft.tags;

import java.util.Collection;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.util.ResourceLocation;

public class BlockTags {
   private static TagCollection<Block> HELPER = new TagCollection<>((p_203641_0_) -> {
      return Optional.empty();
   }, "", false, "");
   private static int field_199900_d;
   public static final Tag<Block> WOOL = bind("wool");
   public static final Tag<Block> PLANKS = bind("planks");
   public static final Tag<Block> STONE_BRICKS = bind("stone_bricks");
   public static final Tag<Block> WOODEN_BUTTONS = bind("wooden_buttons");
   public static final Tag<Block> BUTTONS = bind("buttons");
   public static final Tag<Block> CARPETS = bind("carpets");
   public static final Tag<Block> WOODEN_DOORS = bind("wooden_doors");
   public static final Tag<Block> WOODEN_STAIRS = bind("wooden_stairs");
   public static final Tag<Block> WOODEN_SLABS = bind("wooden_slabs");
   public static final Tag<Block> WOODEN_FENCES = bind("wooden_fences");
   public static final Tag<Block> WOODEN_PRESSURE_PLATES = bind("wooden_pressure_plates");
   public static final Tag<Block> WOODEN_TRAPDOORS = bind("wooden_trapdoors");
   public static final Tag<Block> DOORS = bind("doors");
   public static final Tag<Block> SAPLINGS = bind("saplings");
   public static final Tag<Block> LOGS = bind("logs");
   public static final Tag<Block> DARK_OAK_LOGS = bind("dark_oak_logs");
   public static final Tag<Block> OAK_LOGS = bind("oak_logs");
   public static final Tag<Block> BIRCH_LOGS = bind("birch_logs");
   public static final Tag<Block> ACACIA_LOGS = bind("acacia_logs");
   public static final Tag<Block> JUNGLE_LOGS = bind("jungle_logs");
   public static final Tag<Block> SPRUCE_LOGS = bind("spruce_logs");
   public static final Tag<Block> BANNERS = bind("banners");
   public static final Tag<Block> SAND = bind("sand");
   public static final Tag<Block> STAIRS = bind("stairs");
   public static final Tag<Block> SLABS = bind("slabs");
   public static final Tag<Block> WALLS = bind("walls");
   public static final Tag<Block> ANVIL = bind("anvil");
   public static final Tag<Block> RAILS = bind("rails");
   public static final Tag<Block> LEAVES = bind("leaves");
   public static final Tag<Block> TRAPDOORS = bind("trapdoors");
   public static final Tag<Block> SMALL_FLOWERS = bind("small_flowers");
   public static final Tag<Block> BEDS = bind("beds");
   public static final Tag<Block> FENCES = bind("fences");
   public static final Tag<Block> TALL_FLOWERS = bind("tall_flowers");
   public static final Tag<Block> FLOWERS = bind("flowers");
   public static final Tag<Block> SHULKER_BOXES = bind("shulker_boxes");
   public static final Tag<Block> FLOWER_POTS = bind("flower_pots");
   public static final Tag<Block> ENDERMAN_HOLDABLE = bind("enderman_holdable");
   public static final Tag<Block> ICE = bind("ice");
   public static final Tag<Block> VALID_SPAWN = bind("valid_spawn");
   public static final Tag<Block> IMPERMEABLE = bind("impermeable");
   public static final Tag<Block> UNDERWATER_BONEMEALS = bind("underwater_bonemeals");
   public static final Tag<Block> CORAL_BLOCKS = bind("coral_blocks");
   public static final Tag<Block> WALL_CORALS = bind("wall_corals");
   public static final Tag<Block> CORAL_PLANTS = bind("coral_plants");
   public static final Tag<Block> CORALS = bind("corals");
   public static final Tag<Block> BAMBOO_PLANTABLE_ON = bind("bamboo_plantable_on");
   public static final Tag<Block> STANDING_SIGNS = bind("standing_signs");
   public static final Tag<Block> WALL_SIGNS = bind("wall_signs");
   public static final Tag<Block> SIGNS = bind("signs");
   public static final Tag<Block> DRAGON_IMMUNE = bind("dragon_immune");
   public static final Tag<Block> WITHER_IMMUNE = bind("wither_immune");
   public static final Tag<Block> BEEHIVES = bind("beehives");
   public static final Tag<Block> CROPS = bind("crops");
   public static final Tag<Block> BEE_GROWABLES = bind("bee_growables");
   public static final Tag<Block> PORTALS = bind("portals");

   public static void func_199895_a(TagCollection<Block> p_199895_0_) {
      HELPER = p_199895_0_;
      ++field_199900_d;
   }

   public static TagCollection<Block> getAllTags() {
      return HELPER;
   }

   public static int getGeneration() {
      return field_199900_d;
   }

   private static Tag<Block> bind(String p_199894_0_) {
      return new BlockTags.Wrapper(new ResourceLocation(p_199894_0_));
   }

   public static class Wrapper extends Tag<Block> {
      private int field_199892_a = -1;
      private Tag<Block> field_199893_b;

      public Wrapper(ResourceLocation p_i48217_1_) {
         super(p_i48217_1_);
      }

      public boolean func_199685_a_(Block p_199685_1_) {
         if (this.field_199892_a != BlockTags.field_199900_d) {
            this.field_199893_b = BlockTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199892_a = BlockTags.field_199900_d;
         }

         return this.field_199893_b.func_199685_a_(p_199685_1_);
      }

      public Collection<Block> func_199885_a() {
         if (this.field_199892_a != BlockTags.field_199900_d) {
            this.field_199893_b = BlockTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199892_a = BlockTags.field_199900_d;
         }

         return this.field_199893_b.func_199885_a();
      }

      public Collection<Tag.ITagEntry<Block>> func_200570_b() {
         if (this.field_199892_a != BlockTags.field_199900_d) {
            this.field_199893_b = BlockTags.HELPER.func_199915_b(this.func_199886_b());
            this.field_199892_a = BlockTags.field_199900_d;
         }

         return this.field_199893_b.func_200570_b();
      }
   }
}
