package io.github.dungeonmakers.armouranditem.core.material.base;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.function.Supplier;

public record BaseArmorMaterial(int enchantability, int[] durability, int[] damageReduction,
    float knockbackResistance, float toughness, String name, SoundEvent equipSound,
    Supplier<Ingredient> repairMaterial) implements ArmorMaterial {

  @Override
  public int getDefenseForSlot(@NotNull EquipmentSlot slot) {
    return this.damageReduction[slot.getIndex()];
  }

  @Override
  public int getDurabilityForSlot(@NotNull EquipmentSlot slot) {
    return this.durability[slot.getIndex()];
  }

  @Override
  public int getEnchantmentValue() {
    return this.enchantability;
  }

  @Override
  public @NotNull SoundEvent getEquipSound() {
    return this.equipSound;
  }

  @Override
  public float getKnockbackResistance() {
    return this.knockbackResistance;
  }

  @Override
  public @NotNull String getName() {
    return this.name;
  }

  @Override
  public @NotNull Ingredient getRepairIngredient() {
    return this.repairMaterial.get();
  }

  @Override
  public float getToughness() {
    return this.toughness;
  }

  @Override
  public int hashCode() {
    return enchantability + Arrays.hashCode(durability) + Arrays.hashCode(damageReduction)
        + Float.hashCode(knockbackResistance) + Float.hashCode(toughness) + name.hashCode()
        + equipSound.hashCode() + repairMaterial.hashCode();
  }

  @Contract(pure = true)
  @Override
  public @NotNull String toString() {
    return "BaseArmorMaterial{" + "enchantability=" + enchantability + ", durability="
        + Arrays.toString(durability) + ", damageReduction=" + Arrays.toString(damageReduction)
        + ", knockbackResistance=" + knockbackResistance + ", toughness=" + toughness + ", name='"
        + name + '\'' + ", equipSound=" + equipSound + ", repairMaterial=" + repairMaterial + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BaseArmorMaterial that = (BaseArmorMaterial) o;
    return enchantability == that.enchantability && Arrays.equals(durability, that.durability)
        && Arrays.equals(damageReduction, that.damageReduction)
        && Float.compare(that.knockbackResistance, knockbackResistance) == 0
        && Float.compare(that.toughness, toughness) == 0 && name.equals(that.name)
        && equipSound == that.equipSound && repairMaterial.equals(that.repairMaterial);
  }
}
