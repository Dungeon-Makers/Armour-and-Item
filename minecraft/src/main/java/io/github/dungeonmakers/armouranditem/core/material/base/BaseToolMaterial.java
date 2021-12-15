package io.github.dungeonmakers.armouranditem.core.material.base;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public record BaseToolMaterial(float attackDamageBonus, int enchantability, int harvestLevel,
    float speed, int durability, Supplier<Ingredient> repairMaterial) implements Tier {

  @Override
  public float getAttackDamageBonus() {
    return this.attackDamageBonus;
  }

  @Override
  public int getEnchantmentValue() {
    return this.enchantability;
  }

  @Override
  public int getLevel() {
    return this.harvestLevel;
  }

  @Override
  public @NotNull Ingredient getRepairIngredient() {
    return this.repairMaterial.get();
  }

  @Override
  public float getSpeed() {
    return this.speed;
  }

  @Override
  public int getUses() {
    return this.durability;
  }
}
