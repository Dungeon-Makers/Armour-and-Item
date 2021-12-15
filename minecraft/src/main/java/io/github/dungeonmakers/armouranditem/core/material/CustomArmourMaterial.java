package io.github.dungeonmakers.armouranditem.core.material;


import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import io.github.dungeonmakers.armouranditem.core.ItemInit;
import io.github.dungeonmakers.armouranditem.core.material.base.BaseArmorMaterial;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum CustomArmourMaterial {
    ;
    public static final ArmorMaterial BLACK_DIAMOND_ARMOUR;

    static {
        BLACK_DIAMOND_ARMOUR = new BaseArmorMaterial(100, new int[] { 950, 1400, 1800, 1000 },
                new int[] { 20, 35, 50, 27 }, 3.2f, 1.85f, ArmourAndItem.MOD_ID + ":black_diamond_armour",
                SoundEvents.ARMOR_EQUIP_DIAMOND, () -> Ingredient.of(ItemInit.BLACK_DIAMOND.get()));
    }
}
