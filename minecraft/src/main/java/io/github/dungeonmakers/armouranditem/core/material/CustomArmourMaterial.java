package io.github.dungeonmakers.armouranditem.core.material;


import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.material.base.BaseArmorMaterial;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public enum CustomArmourMaterial {
  ;
  public static final ArmorMaterial BLACK_DIAMOND_ARMOUR;
  public static final ArmorMaterial PURPLE_DIAMOND_ARMOUR;

  static {
    BLACK_DIAMOND_ARMOUR = register(new int[] {628, 628, 628, 628}, new int[] {90, 90, 90, 90},
        3.2f, 1.85f, ":black_diamond_armour", SoundEvents.ARMOR_EQUIP_DIAMOND,
        () -> Ingredient.of(ItemInit.BLACK_DIAMOND.get()));

    PURPLE_DIAMOND_ARMOUR = register(new int[] {630, 630, 630, 630}, new int[] {92, 92, 92, 92},
        3.3f, 1.87f, ":purple_diamond_armour", SoundEvents.ARMOR_EQUIP_DIAMOND,
        () -> Ingredient.of(ItemInit.PURPLE_DIAMOND.get()));
  }

  private static @NotNull BaseArmorMaterial register(int[] durability, int[] damageReduction,
      float knockbackResistance, float toughness, String armourName, SoundEvent equipSound,
      Supplier<Ingredient> repairMaterial) {
    return new BaseArmorMaterial(10, durability, damageReduction, knockbackResistance, toughness,
        ArmourAndItem.MOD_ID + armourName, equipSound, repairMaterial);
  }
}
