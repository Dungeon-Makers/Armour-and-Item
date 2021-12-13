package io.github.dungeonmakers.armouranditem.core.material;

import io.github.dungeonmakers.armouranditem.core.ItemInit;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum CustomToolMaterial implements Tier {
  BLACK_DIAMOND_SWORD(2, 1662, 20f, 9f, 15, () -> Ingredient.of(ItemInit.BLACK_DIAMOND.get()));


  private final int harvestLevel;
  private final int maxUses;
  private final float efficiency;
  private final float attackDamage;
  private final int enchantability;
  private final Ingredient repairMaterial;

  CustomToolMaterial(int harvestLevel, int maxUses, float efficiency, float attackDamage,
      int enchantability, @NotNull Supplier<Ingredient> repairMaterial) {
    this.harvestLevel = harvestLevel;
    this.maxUses = maxUses;
    this.efficiency = efficiency;
    this.attackDamage = attackDamage;
    this.enchantability = enchantability;
    this.repairMaterial = repairMaterial.get();
  }

  @Override
  public int getUses() {
    return this.maxUses;
  }

  @Override
  public float getSpeed() {
    return this.efficiency;
  }

  @Override
  public float getAttackDamageBonus() {
    return this.attackDamage;
  }

  @Override
  public int getLevel() {
    return this.harvestLevel;
  }

  @Override
  public int getEnchantmentValue() {
    return this.enchantability;
  }

  @Override
  public @NotNull Ingredient getRepairIngredient() {
    return this.repairMaterial;
  }
}
