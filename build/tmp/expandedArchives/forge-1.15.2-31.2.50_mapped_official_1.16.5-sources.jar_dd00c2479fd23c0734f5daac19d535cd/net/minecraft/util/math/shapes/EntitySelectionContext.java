package net.minecraft.util.math.shapes;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;

public class EntitySelectionContext implements ISelectionContext {
   protected static final ISelectionContext EMPTY = new EntitySelectionContext(false, -Double.MAX_VALUE, Items.AIR) {
      public boolean isAbove(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
         return p_216378_3_;
      }
   };
   private final boolean descending;
   private final double entityBottom;
   private final Item heldItem;

   protected EntitySelectionContext(boolean p_i51181_1_, double p_i51181_2_, Item p_i51181_4_) {
      this(null, p_i51181_1_, p_i51181_2_, p_i51181_4_);
   }

   protected EntitySelectionContext(@javax.annotation.Nullable Entity entityIn, boolean p_i51181_1_, double p_i51181_2_, Item p_i51181_4_) {
      this.entity = entityIn;
      this.descending = p_i51181_1_;
      this.entityBottom = p_i51181_2_;
      this.heldItem = p_i51181_4_;
   }

   @Deprecated
   protected EntitySelectionContext(Entity p_i51182_1_) {
      this(p_i51182_1_, p_i51182_1_.isDescending(), p_i51182_1_.getY(), p_i51182_1_ instanceof LivingEntity ? ((LivingEntity)p_i51182_1_).getMainHandItem().getItem() : Items.AIR);
   }

   public boolean isHoldingItem(Item p_216375_1_) {
      return this.heldItem == p_216375_1_;
   }

   public boolean isDescending() {
      return this.descending;
   }

   public boolean isAbove(VoxelShape p_216378_1_, BlockPos p_216378_2_, boolean p_216378_3_) {
      return this.entityBottom > (double)p_216378_2_.getY() + p_216378_1_.max(Direction.Axis.Y) - (double)1.0E-5F;
   }

   private final @javax.annotation.Nullable Entity entity;

   @Override
   public @javax.annotation.Nullable Entity getEntity() {
      return entity;
   }
}
