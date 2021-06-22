package net.minecraft.item;

import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class CarrotOnAStickItem extends Item {
   public CarrotOnAStickItem(Item.Properties p_i48519_1_) {
      super(p_i48519_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = p_77659_2_.getItemInHand(p_77659_3_);
      if (p_77659_1_.isClientSide) {
         return ActionResult.pass(itemstack);
      } else {
         if (p_77659_2_.isPassenger() && p_77659_2_.getVehicle() instanceof PigEntity) {
            PigEntity pigentity = (PigEntity)p_77659_2_.getVehicle();
            if (itemstack.getMaxDamage() - itemstack.getDamageValue() >= 7 && pigentity.boost()) {
               itemstack.hurtAndBreak(7, p_77659_2_, (p_219991_1_) -> {
                  p_219991_1_.broadcastBreakEvent(p_77659_3_);
               });
               if (itemstack.isEmpty()) {
                  ItemStack itemstack1 = new ItemStack(Items.FISHING_ROD);
                  itemstack1.setTag(itemstack.getTag());
                  return ActionResult.success(itemstack1);
               }

               return ActionResult.success(itemstack);
            }
         }

         p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
         return ActionResult.pass(itemstack);
      }
   }
}