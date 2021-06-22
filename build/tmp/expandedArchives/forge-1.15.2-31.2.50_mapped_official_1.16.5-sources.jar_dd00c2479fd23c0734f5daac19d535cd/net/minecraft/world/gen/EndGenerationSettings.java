package net.minecraft.world.gen;

import net.minecraft.util.math.BlockPos;

public class EndGenerationSettings extends GenerationSettings {
   private BlockPos field_205540_n;

   public EndGenerationSettings func_205538_a(BlockPos p_205538_1_) {
      this.field_205540_n = p_205538_1_;
      return this;
   }

   public BlockPos func_205539_n() {
      return this.field_205540_n;
   }
}