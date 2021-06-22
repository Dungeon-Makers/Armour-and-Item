package net.minecraft.world.gen.surfacebuilders;

import com.mojang.datafixers.Dynamic;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class SurfaceBuilderConfig implements ISurfaceBuilderConfig {
   private final BlockState topMaterial;
   private final BlockState underMaterial;
   private final BlockState underwaterMaterial;

   public SurfaceBuilderConfig(BlockState p_i48954_1_, BlockState p_i48954_2_, BlockState p_i48954_3_) {
      this.topMaterial = p_i48954_1_;
      this.underMaterial = p_i48954_2_;
      this.underwaterMaterial = p_i48954_3_;
   }

   public BlockState getTopMaterial() {
      return this.topMaterial;
   }

   public BlockState getUnderMaterial() {
      return this.underMaterial;
   }

   public BlockState getUnderwaterMaterial() {
      return this.underwaterMaterial;
   }

   public static SurfaceBuilderConfig func_215455_a(Dynamic<?> p_215455_0_) {
      BlockState blockstate = p_215455_0_.get("top_material").map(BlockState::func_215698_a).orElse(Blocks.AIR.defaultBlockState());
      BlockState blockstate1 = p_215455_0_.get("under_material").map(BlockState::func_215698_a).orElse(Blocks.AIR.defaultBlockState());
      BlockState blockstate2 = p_215455_0_.get("underwater_material").map(BlockState::func_215698_a).orElse(Blocks.AIR.defaultBlockState());
      return new SurfaceBuilderConfig(blockstate, blockstate1, blockstate2);
   }
}