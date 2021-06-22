package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;

public class ShearableDoublePlantBlock extends DoublePlantBlock implements net.minecraftforge.common.IShearable {
   public static final EnumProperty<DoubleBlockHalf> field_208063_b = DoublePlantBlock.HALF;

   public ShearableDoublePlantBlock(Block.Properties p_i49975_1_) {
      super(p_i49975_1_);
   }

   public boolean canBeReplaced(BlockState p_196253_1_, BlockItemUseContext p_196253_2_) {
      boolean flag = super.canBeReplaced(p_196253_1_, p_196253_2_);
      return flag && p_196253_2_.getItemInHand().getItem() == this.asItem() ? false : flag;
   }
}
