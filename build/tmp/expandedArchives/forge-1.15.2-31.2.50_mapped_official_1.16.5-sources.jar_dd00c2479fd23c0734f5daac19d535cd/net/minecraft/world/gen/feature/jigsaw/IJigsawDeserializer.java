package net.minecraft.world.gen.feature.jigsaw;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IJigsawDeserializer extends IDynamicDeserializer<JigsawPiece> {
   IJigsawDeserializer SINGLE = func_214926_a("single_pool_element", SingleJigsawPiece::new);
   IJigsawDeserializer LIST = func_214926_a("list_pool_element", ListJigsawPiece::new);
   IJigsawDeserializer FEATURE = func_214926_a("feature_pool_element", FeatureJigsawPiece::new);
   IJigsawDeserializer EMPTY = func_214926_a("empty_pool_element", (p_214927_0_) -> {
      return EmptyJigsawPiece.INSTANCE;
   });

   static IJigsawDeserializer func_214926_a(String p_214926_0_, IJigsawDeserializer p_214926_1_) {
      return Registry.register(Registry.STRUCTURE_POOL_ELEMENT, p_214926_0_, p_214926_1_);
   }
}