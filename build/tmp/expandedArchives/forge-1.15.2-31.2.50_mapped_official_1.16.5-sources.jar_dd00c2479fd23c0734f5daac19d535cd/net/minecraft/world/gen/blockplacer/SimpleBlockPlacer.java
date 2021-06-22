package net.minecraft.world.gen.blockplacer;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;

public class SimpleBlockPlacer extends BlockPlacer {
   public SimpleBlockPlacer() {
      super(BlockPlacerType.SIMPLE_BLOCK_PLACER);
   }

   public <T> SimpleBlockPlacer(Dynamic<T> p_i225829_1_) {
      this();
   }

   public void place(IWorld p_225567_1_, BlockPos p_225567_2_, BlockState p_225567_3_, Random p_225567_4_) {
      p_225567_1_.setBlock(p_225567_2_, p_225567_3_, 2);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.BLOCK_PLACER_TYPES.getKey(this.field_227258_a_).toString()))))).getValue();
   }
}