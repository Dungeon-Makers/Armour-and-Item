package net.minecraft.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class MapItem extends AbstractMapItem {
   public MapItem(Item.Properties p_i48506_1_) {
      super(p_i48506_1_);
   }

   public ActionResult<ItemStack> use(World p_77659_1_, PlayerEntity p_77659_2_, Hand p_77659_3_) {
      ItemStack itemstack = FilledMapItem.create(p_77659_1_, MathHelper.floor(p_77659_2_.getX()), MathHelper.floor(p_77659_2_.getZ()), (byte)0, true, false);
      ItemStack itemstack1 = p_77659_2_.getItemInHand(p_77659_3_);
      if (!p_77659_2_.abilities.instabuild) {
         itemstack1.shrink(1);
      }

      if (itemstack1.isEmpty()) {
         return ActionResult.success(itemstack);
      } else {
         if (!p_77659_2_.inventory.add(itemstack.copy())) {
            p_77659_2_.drop(itemstack, false);
         }

         p_77659_2_.awardStat(Stats.ITEM_USED.get(this));
         return ActionResult.success(itemstack1);
      }
   }
}