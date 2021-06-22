package net.minecraft.world.gen.blockplacer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class ColumnBlockPlacer extends BlockPlacer {
   private final int minSize;
   private final int extraSize;

   public ColumnBlockPlacer(int p_i225826_1_, int p_i225826_2_) {
      super(BlockPlacerType.COLUMN_PLACER);
      this.minSize = p_i225826_1_;
      this.extraSize = p_i225826_2_;
   }

   public <T> ColumnBlockPlacer(Dynamic<T> p_i225827_1_) {
      this(p_i225827_1_.get("min_size").asInt(1), p_i225827_1_.get("extra_size").asInt(2));
   }

   public void place(IWorld p_225567_1_, BlockPos p_225567_2_, BlockState p_225567_3_, Random p_225567_4_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_225567_2_);
      int i = this.minSize + p_225567_4_.nextInt(p_225567_4_.nextInt(this.extraSize + 1) + 1);

      for(int j = 0; j < i; ++j) {
         p_225567_1_.setBlock(blockpos$mutable, p_225567_3_, 2);
         blockpos$mutable.move(Direction.UP);
      }

   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_PLACER_TYPES.getKey(this.field_227258_a_).toString()), p_218175_1_.createString("min_size"), p_218175_1_.createInt(this.minSize), p_218175_1_.createString("extra_size"), p_218175_1_.createInt(this.extraSize))))).getValue();
   }
}