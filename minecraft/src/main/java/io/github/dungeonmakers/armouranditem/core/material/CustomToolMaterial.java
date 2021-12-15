package io.github.dungeonmakers.armouranditem.core.material;

import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.material.base.BaseToolMaterial;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum CustomToolMaterial {
  ;
  public static final Tier BLACK_DIAMOND_SWORD;

  static {
    BLACK_DIAMOND_SWORD = new BaseToolMaterial(2, 1662, 20, 9f, 15,
        () -> Ingredient.of(ItemInit.BLACK_DIAMOND.get()));
  }
}
