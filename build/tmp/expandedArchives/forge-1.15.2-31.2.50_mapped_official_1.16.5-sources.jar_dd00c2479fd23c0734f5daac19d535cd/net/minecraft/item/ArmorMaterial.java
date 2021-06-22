package net.minecraft.item;

import java.util.function.Supplier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum ArmorMaterial implements IArmorMaterial {
   LEATHER("leather", 5, new int[]{1, 2, 3, 1}, 15, SoundEvents.ARMOR_EQUIP_LEATHER, 0.0F, () -> {
      return Ingredient.of(Items.LEATHER);
   }),
   CHAIN("chainmail", 15, new int[]{1, 4, 5, 2}, 12, SoundEvents.ARMOR_EQUIP_CHAIN, 0.0F, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   IRON("iron", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, () -> {
      return Ingredient.of(Items.IRON_INGOT);
   }),
   GOLD("gold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, () -> {
      return Ingredient.of(Items.GOLD_INGOT);
   }),
   DIAMOND("diamond", 33, new int[]{3, 6, 8, 3}, 10, SoundEvents.ARMOR_EQUIP_DIAMOND, 2.0F, () -> {
      return Ingredient.of(Items.DIAMOND);
   }),
   TURTLE("turtle", 25, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_TURTLE, 0.0F, () -> {
      return Ingredient.of(Items.SCUTE);
   });

   private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
   private final String name;
   private final int durabilityMultiplier;
   private final int[] slotProtections;
   private final int enchantmentValue;
   private final SoundEvent sound;
   private final float toughness;
   private final LazyValue<Ingredient> repairIngredient;

   private ArmorMaterial(String p_i48533_3_, int p_i48533_4_, int[] p_i48533_5_, int p_i48533_6_, SoundEvent p_i48533_7_, float p_i48533_8_, Supplier<Ingredient> p_i48533_9_) {
      this.name = p_i48533_3_;
      this.durabilityMultiplier = p_i48533_4_;
      this.slotProtections = p_i48533_5_;
      this.enchantmentValue = p_i48533_6_;
      this.sound = p_i48533_7_;
      this.toughness = p_i48533_8_;
      this.repairIngredient = new LazyValue<>(p_i48533_9_);
   }

   public int getDurabilityForSlot(EquipmentSlotType p_200896_1_) {
      return HEALTH_PER_SLOT[p_200896_1_.getIndex()] * this.durabilityMultiplier;
   }

   public int getDefenseForSlot(EquipmentSlotType p_200902_1_) {
      return this.slotProtections[p_200902_1_.getIndex()];
   }

   public int getEnchantmentValue() {
      return this.enchantmentValue;
   }

   public SoundEvent getEquipSound() {
      return this.sound;
   }

   public Ingredient getRepairIngredient() {
      return this.repairIngredient.get();
   }

   @OnlyIn(Dist.CLIENT)
   public String getName() {
      return this.name;
   }

   public float getToughness() {
      return this.toughness;
   }
}