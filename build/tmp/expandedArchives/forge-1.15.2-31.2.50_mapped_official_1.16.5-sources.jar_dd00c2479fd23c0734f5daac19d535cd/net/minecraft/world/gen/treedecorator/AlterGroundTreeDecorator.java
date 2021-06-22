package net.minecraft.world.gen.treedecorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.AbstractTreeFeature;

public class AlterGroundTreeDecorator extends TreeDecorator {
   private final BlockStateProvider provider;

   public AlterGroundTreeDecorator(BlockStateProvider p_i225864_1_) {
      super(TreeDecoratorType.ALTER_GROUND);
      this.provider = p_i225864_1_;
   }

   public <T> AlterGroundTreeDecorator(Dynamic<T> p_i225865_1_) {
      this(Registry.BLOCKSTATE_PROVIDER_TYPES.get(new ResourceLocation(p_i225865_1_.get("provider").get("type").asString().orElseThrow(RuntimeException::new))).func_227399_a_(p_i225865_1_.get("provider").orElseEmptyMap()));
   }

   public void place(IWorld p_225576_1_, Random p_225576_2_, List<BlockPos> p_225576_3_, List<BlockPos> p_225576_4_, Set<BlockPos> p_225576_5_, MutableBoundingBox p_225576_6_) {
      int i = p_225576_3_.get(0).getY();
      p_225576_3_.stream().filter((p_227411_1_) -> {
         return p_227411_1_.getY() == i;
      }).forEach((p_227412_3_) -> {
         this.placeCircle(p_225576_1_, p_225576_2_, p_227412_3_.west().north());
         this.placeCircle(p_225576_1_, p_225576_2_, p_227412_3_.east(2).north());
         this.placeCircle(p_225576_1_, p_225576_2_, p_227412_3_.west().south(2));
         this.placeCircle(p_225576_1_, p_225576_2_, p_227412_3_.east(2).south(2));

         for(int j = 0; j < 5; ++j) {
            int k = p_225576_2_.nextInt(64);
            int l = k % 8;
            int i1 = k / 8;
            if (l == 0 || l == 7 || i1 == 0 || i1 == 7) {
               this.placeCircle(p_225576_1_, p_225576_2_, p_227412_3_.offset(-3 + l, 0, -3 + i1));
            }
         }

      });
   }

   private void placeCircle(IWorldGenerationReader p_227413_1_, Random p_227413_2_, BlockPos p_227413_3_) {
      for(int i = -2; i <= 2; ++i) {
         for(int j = -2; j <= 2; ++j) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.placeBlockAt(p_227413_1_, p_227413_2_, p_227413_3_.offset(i, 0, j));
            }
         }
      }

   }

   private void placeBlockAt(IWorldGenerationReader p_227414_1_, Random p_227414_2_, BlockPos p_227414_3_) {
      for(int i = 2; i >= -3; --i) {
         BlockPos blockpos = p_227414_3_.above(i);
         if (AbstractTreeFeature.func_214589_h(p_227414_1_, blockpos)) {
            p_227414_1_.setBlock(blockpos, this.provider.getState(p_227414_2_, p_227414_3_), 19);
            break;
         }

         if (!AbstractTreeFeature.func_214574_b(p_227414_1_, blockpos) && i < 0) {
            break;
         }
      }

   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return (new Dynamic<>(p_218175_1_, p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("type"), p_218175_1_.createString(Registry.TREE_DECORATOR_TYPES.getKey(this.field_227422_a_).toString()), p_218175_1_.createString("provider"), this.provider.func_218175_a(p_218175_1_))))).getValue();
   }
}