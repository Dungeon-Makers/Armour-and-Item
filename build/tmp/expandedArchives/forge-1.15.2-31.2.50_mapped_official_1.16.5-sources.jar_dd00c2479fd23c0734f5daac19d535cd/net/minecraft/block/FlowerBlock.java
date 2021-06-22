package net.minecraft.block;

import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class FlowerBlock extends BushBlock {
   protected static final VoxelShape SHAPE = Block.box(5.0D, 0.0D, 5.0D, 11.0D, 10.0D, 11.0D);
   private final Effect suspiciousStewEffect;
   private final int effectDuration;

   public FlowerBlock(Effect p_i49984_1_, int p_i49984_2_, Block.Properties p_i49984_3_) {
      super(p_i49984_3_);
      this.suspiciousStewEffect = p_i49984_1_;
      if (p_i49984_1_.isInstantenous()) {
         this.effectDuration = p_i49984_2_;
      } else {
         this.effectDuration = p_i49984_2_ * 20;
      }

   }

   public VoxelShape getShape(BlockState p_220053_1_, IBlockReader p_220053_2_, BlockPos p_220053_3_, ISelectionContext p_220053_4_) {
      Vec3d vec3d = p_220053_1_.getOffset(p_220053_2_, p_220053_3_);
      return SHAPE.move(vec3d.x, vec3d.y, vec3d.z);
   }

   public Block.OffsetType getOffsetType() {
      return Block.OffsetType.XZ;
   }

   public Effect getSuspiciousStewEffect() {
      return this.suspiciousStewEffect;
   }

   public int getEffectDuration() {
      return this.effectDuration;
   }
}