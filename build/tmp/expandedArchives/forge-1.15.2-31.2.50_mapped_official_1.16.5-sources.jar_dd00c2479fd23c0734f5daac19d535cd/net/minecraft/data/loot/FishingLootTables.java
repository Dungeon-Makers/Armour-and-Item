package net.minecraft.data.loot;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.block.Blocks;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.storage.loot.ConstantRange;
import net.minecraft.world.storage.loot.ItemLootEntry;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.StandaloneLootEntry;
import net.minecraft.world.storage.loot.TableLootEntry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LocationCheck;
import net.minecraft.world.storage.loot.functions.EnchantWithLevels;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraft.world.storage.loot.functions.SetNBT;

public class FishingLootTables implements Consumer<BiConsumer<ResourceLocation, LootTable.Builder>> {
   public static final ILootCondition.IBuilder IN_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.JUNGLE));
   public static final ILootCondition.IBuilder IN_JUNGLE_HILLS = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.JUNGLE_HILLS));
   public static final ILootCondition.IBuilder IN_JUNGLE_EDGE = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.BAMBOO_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.MODIFIED_JUNGLE));
   public static final ILootCondition.IBuilder IN_MODIFIED_JUNGLE_EDGE = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.MODIFIED_JUNGLE_EDGE));
   public static final ILootCondition.IBuilder IN_BAMBOO_JUNGLE_HILLS = LocationCheck.checkLocation(LocationPredicate.Builder.location().func_218012_a(Biomes.BAMBOO_JUNGLE_HILLS));

   public void accept(BiConsumer<ResourceLocation, LootTable.Builder> p_accept_1_) {
      p_accept_1_.accept(LootTables.FISHING, LootTable.lootTable().withPool(LootPool.lootPool().setRolls(ConstantRange.exactly(1)).add(TableLootEntry.lootTableReference(LootTables.FISHING_JUNK).setWeight(10).setQuality(-2)).add(TableLootEntry.lootTableReference(LootTables.FISHING_TREASURE).setWeight(5).setQuality(2)).add(TableLootEntry.lootTableReference(LootTables.FISHING_FISH).setWeight(85).setQuality(-1))));
      p_accept_1_.accept(LootTables.FISHING_FISH, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.COD).setWeight(60)).add(ItemLootEntry.lootTableItem(Items.SALMON).setWeight(25)).add(ItemLootEntry.lootTableItem(Items.TROPICAL_FISH).setWeight(2)).add(ItemLootEntry.lootTableItem(Items.PUFFERFISH).setWeight(13))));
      p_accept_1_.accept(LootTables.FISHING_JUNK, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Items.LEATHER_BOOTS).setWeight(10).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.9F)))).add(ItemLootEntry.lootTableItem(Items.LEATHER).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.BONE).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.POTION).setWeight(10).apply(SetNBT.setTag(Util.make(new CompoundNBT(), (p_218588_0_) -> {
         p_218588_0_.putString("Potion", "minecraft:water");
      })))).add(ItemLootEntry.lootTableItem(Items.STRING).setWeight(5)).add(ItemLootEntry.lootTableItem(Items.FISHING_ROD).setWeight(2).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.9F)))).add(ItemLootEntry.lootTableItem(Items.BOWL).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.STICK).setWeight(5)).add(ItemLootEntry.lootTableItem(Items.INK_SAC).setWeight(1).apply(SetCount.setCount(ConstantRange.exactly(10)))).add(ItemLootEntry.lootTableItem(Blocks.TRIPWIRE_HOOK).setWeight(10)).add(ItemLootEntry.lootTableItem(Items.ROTTEN_FLESH).setWeight(10)).add(((StandaloneLootEntry.Builder)ItemLootEntry.lootTableItem(Blocks.BAMBOO).when(IN_JUNGLE.or(IN_JUNGLE_HILLS).or(IN_JUNGLE_EDGE).or(IN_BAMBOO_JUNGLE).or(IN_MODIFIED_JUNGLE).or(IN_MODIFIED_JUNGLE_EDGE).or(IN_BAMBOO_JUNGLE_HILLS))).setWeight(10))));
      p_accept_1_.accept(LootTables.FISHING_TREASURE, LootTable.lootTable().withPool(LootPool.lootPool().add(ItemLootEntry.lootTableItem(Blocks.LILY_PAD)).add(ItemLootEntry.lootTableItem(Items.NAME_TAG)).add(ItemLootEntry.lootTableItem(Items.SADDLE)).add(ItemLootEntry.lootTableItem(Items.BOW).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.25F))).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.FISHING_ROD).apply(SetDamage.setDamage(RandomValueRange.between(0.0F, 0.25F))).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.BOOK).apply(EnchantWithLevels.enchantWithLevels(ConstantRange.exactly(30)).allowTreasure())).add(ItemLootEntry.lootTableItem(Items.NAUTILUS_SHELL))));
   }
}