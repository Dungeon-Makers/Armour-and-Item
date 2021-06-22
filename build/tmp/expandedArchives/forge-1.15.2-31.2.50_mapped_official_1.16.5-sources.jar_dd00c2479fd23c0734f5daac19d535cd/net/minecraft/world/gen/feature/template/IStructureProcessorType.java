package net.minecraft.world.gen.feature.template;

import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.registry.Registry;

public interface IStructureProcessorType extends IDynamicDeserializer<StructureProcessor> {
   IStructureProcessorType BLOCK_IGNORE = func_214917_a("block_ignore", BlockIgnoreStructureProcessor::new);
   IStructureProcessorType BLOCK_ROT = func_214917_a("block_rot", IntegrityProcessor::new);
   IStructureProcessorType GRAVITY = func_214917_a("gravity", GravityStructureProcessor::new);
   IStructureProcessorType JIGSAW_REPLACEMENT = func_214917_a("jigsaw_replacement", (p_214919_0_) -> {
      return JigsawReplacementStructureProcessor.INSTANCE;
   });
   IStructureProcessorType RULE = func_214917_a("rule", RuleStructureProcessor::new);
   IStructureProcessorType NOP = func_214917_a("nop", (p_214918_0_) -> {
      return NopProcessor.INSTANCE;
   });

   static IStructureProcessorType func_214917_a(String p_214917_0_, IStructureProcessorType p_214917_1_) {
      return Registry.register(Registry.STRUCTURE_PROCESSOR, p_214917_0_, p_214917_1_);
   }
}