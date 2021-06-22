package net.minecraft.data;

import com.google.common.collect.Lists;
import java.nio.file.Path;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ItemTagsProvider extends TagsProvider<Item> {
   private static final Logger field_203426_d = LogManager.getLogger();

   public ItemTagsProvider(DataGenerator p_i48255_1_) {
      super(p_i48255_1_, Registry.ITEM);
   }

   protected void addTags() {
      this.func_200438_a(BlockTags.WOOL, ItemTags.WOOL);
      this.func_200438_a(BlockTags.PLANKS, ItemTags.PLANKS);
      this.func_200438_a(BlockTags.STONE_BRICKS, ItemTags.STONE_BRICKS);
      this.func_200438_a(BlockTags.WOODEN_BUTTONS, ItemTags.WOODEN_BUTTONS);
      this.func_200438_a(BlockTags.BUTTONS, ItemTags.BUTTONS);
      this.func_200438_a(BlockTags.CARPETS, ItemTags.CARPETS);
      this.func_200438_a(BlockTags.WOODEN_DOORS, ItemTags.WOODEN_DOORS);
      this.func_200438_a(BlockTags.WOODEN_STAIRS, ItemTags.WOODEN_STAIRS);
      this.func_200438_a(BlockTags.WOODEN_SLABS, ItemTags.WOODEN_SLABS);
      this.func_200438_a(BlockTags.WOODEN_FENCES, ItemTags.WOODEN_FENCES);
      this.func_200438_a(BlockTags.WOODEN_PRESSURE_PLATES, ItemTags.WOODEN_PRESSURE_PLATES);
      this.func_200438_a(BlockTags.DOORS, ItemTags.DOORS);
      this.func_200438_a(BlockTags.SAPLINGS, ItemTags.SAPLINGS);
      this.func_200438_a(BlockTags.OAK_LOGS, ItemTags.OAK_LOGS);
      this.func_200438_a(BlockTags.DARK_OAK_LOGS, ItemTags.DARK_OAK_LOGS);
      this.func_200438_a(BlockTags.BIRCH_LOGS, ItemTags.BIRCH_LOGS);
      this.func_200438_a(BlockTags.ACACIA_LOGS, ItemTags.ACACIA_LOGS);
      this.func_200438_a(BlockTags.SPRUCE_LOGS, ItemTags.SPRUCE_LOGS);
      this.func_200438_a(BlockTags.JUNGLE_LOGS, ItemTags.JUNGLE_LOGS);
      this.func_200438_a(BlockTags.LOGS, ItemTags.LOGS);
      this.func_200438_a(BlockTags.SAND, ItemTags.SAND);
      this.func_200438_a(BlockTags.SLABS, ItemTags.SLABS);
      this.func_200438_a(BlockTags.WALLS, ItemTags.WALLS);
      this.func_200438_a(BlockTags.STAIRS, ItemTags.STAIRS);
      this.func_200438_a(BlockTags.ANVIL, ItemTags.ANVIL);
      this.func_200438_a(BlockTags.RAILS, ItemTags.RAILS);
      this.func_200438_a(BlockTags.LEAVES, ItemTags.LEAVES);
      this.func_200438_a(BlockTags.WOODEN_TRAPDOORS, ItemTags.WOODEN_TRAPDOORS);
      this.func_200438_a(BlockTags.TRAPDOORS, ItemTags.TRAPDOORS);
      this.func_200438_a(BlockTags.SMALL_FLOWERS, ItemTags.SMALL_FLOWERS);
      this.func_200438_a(BlockTags.BEDS, ItemTags.BEDS);
      this.func_200438_a(BlockTags.FENCES, ItemTags.FENCES);
      this.func_200438_a(BlockTags.TALL_FLOWERS, ItemTags.TALL_FLOWERS);
      this.func_200438_a(BlockTags.FLOWERS, ItemTags.FLOWERS);
      this.func_200426_a(ItemTags.BANNERS).func_200573_a(Items.WHITE_BANNER, Items.ORANGE_BANNER, Items.MAGENTA_BANNER, Items.LIGHT_BLUE_BANNER, Items.YELLOW_BANNER, Items.LIME_BANNER, Items.PINK_BANNER, Items.GRAY_BANNER, Items.LIGHT_GRAY_BANNER, Items.CYAN_BANNER, Items.PURPLE_BANNER, Items.BLUE_BANNER, Items.BROWN_BANNER, Items.GREEN_BANNER, Items.RED_BANNER, Items.BLACK_BANNER);
      this.func_200426_a(ItemTags.BOATS).func_200573_a(Items.OAK_BOAT, Items.SPRUCE_BOAT, Items.BIRCH_BOAT, Items.JUNGLE_BOAT, Items.ACACIA_BOAT, Items.DARK_OAK_BOAT);
      this.func_200426_a(ItemTags.FISHES).func_200573_a(Items.COD, Items.COOKED_COD, Items.SALMON, Items.COOKED_SALMON, Items.PUFFERFISH, Items.TROPICAL_FISH);
      this.func_200438_a(BlockTags.STANDING_SIGNS, ItemTags.SIGNS);
      this.func_200426_a(ItemTags.MUSIC_DISCS).func_200573_a(Items.MUSIC_DISC_13, Items.MUSIC_DISC_CAT, Items.MUSIC_DISC_BLOCKS, Items.MUSIC_DISC_CHIRP, Items.MUSIC_DISC_FAR, Items.MUSIC_DISC_MALL, Items.MUSIC_DISC_MELLOHI, Items.MUSIC_DISC_STAL, Items.MUSIC_DISC_STRAD, Items.MUSIC_DISC_WARD, Items.MUSIC_DISC_11, Items.MUSIC_DISC_WAIT);
      this.func_200426_a(ItemTags.COALS).func_200573_a(Items.COAL, Items.CHARCOAL);
      this.func_200426_a(ItemTags.ARROWS).func_200573_a(Items.ARROW, Items.TIPPED_ARROW, Items.SPECTRAL_ARROW);
      this.func_200426_a(ItemTags.LECTERN_BOOKS).func_200573_a(Items.WRITTEN_BOOK, Items.WRITABLE_BOOK);
   }

   protected void func_200438_a(Tag<Block> p_200438_1_, Tag<Item> p_200438_2_) {
      Tag.Builder<Item> builder = this.func_200426_a(p_200438_2_);

      for(Tag.ITagEntry<Block> itagentry : p_200438_1_.func_200570_b()) {
         Tag.ITagEntry<Item> itagentry1 = this.func_200439_a(itagentry);
         builder.func_200575_a(itagentry1);
      }

   }

   private Tag.ITagEntry<Item> func_200439_a(Tag.ITagEntry<Block> p_200439_1_) {
      if (p_200439_1_ instanceof Tag.TagEntry) {
         return new Tag.TagEntry<>(((Tag.TagEntry)p_200439_1_).func_200577_a());
      } else if (p_200439_1_ instanceof Tag.ListEntry) {
         List<Item> list = Lists.newArrayList();

         for(Block block : ((Tag.ListEntry<Block>)p_200439_1_).func_200578_a()) {
            Item item = block.asItem();
            if (item == Items.AIR) {
               field_203426_d.warn("Itemless block copied to item tag: {}", (Object)Registry.BLOCK.getKey(block));
            } else {
               list.add(item);
            }
         }

         return new Tag.ListEntry<>(list);
      } else {
         throw new UnsupportedOperationException("Unknown tag entry " + p_200439_1_);
      }
   }

   protected Path getPath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/items/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Item Tags";
   }

   protected void func_200429_a(TagCollection<Item> p_200429_1_) {
      ItemTags.func_199902_a(p_200429_1_);
   }
}