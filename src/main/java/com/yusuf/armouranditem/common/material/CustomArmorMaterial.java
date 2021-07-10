package com.yusuf.armouranditem.common.material;

import com.yusuf.armouranditem.core.init.ItemInit;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.yusuf.realyusufismailcore.core.init.ItemInitCore;

import java.util.function.Supplier;

public enum CustomArmorMaterial implements IArmorMaterial {
    /**
     * @see net.minecraft.item.ArmorMaterial
     */
    BLACK_DIAMOND_ARMOUR("black_diamond", 40, new int[]{8, 9, 20, 6}, 20, SoundEvents.ARMOR_EQUIP_NETHERITE, 6.0F, 0.4f,
            () -> Ingredient.of(ItemInit.BLACK_DIAMOND.get())),
    PURPLE_DIAMOND_ARMOUR("purple_diamond", 50, new int[]{10, 10, 20, 8}, 20, SoundEvents.ARMOR_EQUIP_NETHERITE, 8.0F, 0.8f,
            () -> Ingredient.of(ItemInit.PURPLE_DIAMOND.get())),
    COPPER_ARMOUR("copper", 16, new int[]{3, 4, 7, 3}, 10, SoundEvents.ARMOR_EQUIP_IRON, 0.1F, 0.1F,
            () -> Ingredient.of(ItemInitCore.COPPER.get()));

    private static final int[] baseDurability = {128, 144, 160, 112};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] armorVal;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toghness;
    private final float knockbackResistance;
    private final Ingredient repairIngredient;


    CustomArmorMaterial(String name, int durabilityMultiplier, int[] armorVal, int enchantability,
                        SoundEvent equipSound, float toghness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.armorVal = armorVal;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toghness = toghness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient.get();
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slot) {
        return baseDurability[slot.getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slot) {
        return this.armorVal[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public String getName() {
        return this.name;
    }

    @Override
    public float getToughness() {
        return this.toghness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
