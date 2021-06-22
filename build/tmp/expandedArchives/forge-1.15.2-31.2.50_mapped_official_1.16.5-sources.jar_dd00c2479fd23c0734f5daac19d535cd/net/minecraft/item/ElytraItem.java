package net.minecraft.item;

import net.minecraft.block.DispenserBlock;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ElytraItem extends Item {
   public ElytraItem(Item.Properties p_i48507_1_) {
      super(p_i48507_1_);
      this.func_185043_a(new ResourceLocation("broken"), (p_210312_0_, p_210312_1_, p_210312_2_) -> {
         return isFlyEnabled(p_210312_0_) ? 0.0F : 1.0F;
      });
      DispenserBlock.registerBehavior(this, ArmorItem.DISPENSE_ITEM_BEHAVIOR);
   }

   public static boolean isFlyEnabled(ItemStack p_185069_0_) {
      return p_185069_0_.getDamageValue() < p_185069_0_.getMaxDamage() - 1;
   }

   public boolean isValidRepairItem(ItemStack p_82789_1_, ItemStack p_82789_2_) {
      return p_82789_2_.getItem() == Items.PHANTOM_MEMBRANE;
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      EquipmentSlotType equipmentslottype = MobEntity.getEquipmentSlotForItem(itemstack);
      ItemStack itemstack1 = p_77659_2_.getItemBySlot(equipmentslottype);
      if (itemstack1.isEmpty()) {
         p_77659_2_.setItemSlot(equipmentslottype, itemstack.copy());
         itemstack.setCount(0);
         return ActionResult.success(itemstack);
      } else {
         return ActionResult.fail(itemstack);
      }
   }

   @Override
   public boolean canElytraFly(ItemStack stack, net.minecraft.entity.LivingEntity entity) {
      return ElytraItem.isFlyEnabled(stack);
   }

   @Override
   public boolean elytraFlightTick(ItemStack stack, net.minecraft.entity.LivingEntity entity, int flightTicks) {
      if (!entity.level.isClientSide && (flightTicks + 1) % 20 == 0) {
         stack.hurtAndBreak(1, entity, e -> e.broadcastBreakEvent(net.minecraft.inventory.EquipmentSlotType.CHEST));
      }
      return true;
   }
}
