package net.minecraft.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.PigEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class SaddleItem extends Item {
   public SaddleItem(Item.Properties p_i48474_1_) {
      super(p_i48474_1_);
   }

   public boolean interactLivingEntity(ItemStack p_111207_1_, PlayerEntity p_111207_2_, LivingEntity p_111207_3_, Hand p_111207_4_) {
      if (p_111207_3_ instanceof PigEntity) {
         PigEntity pigentity = (PigEntity)p_111207_3_;
         if (pigentity.isAlive() && !pigentity.func_70901_n() && !pigentity.isBaby()) {
            pigentity.func_70900_e(true);
            pigentity.level.playSound(p_111207_2_, pigentity.getX(), pigentity.getY(), pigentity.getZ(), SoundEvents.PIG_SADDLE, SoundCategory.NEUTRAL, 0.5F, 1.0F);
            p_111207_1_.shrink(1);
            return true;
         }
      }

      return false;
   }
}