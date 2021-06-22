package net.minecraft.world.gen.foliageplacer;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class FoliagePlacerType<P extends FoliagePlacer> extends net.minecraftforge.registries.ForgeRegistryEntry<FoliagePlacerType<?>> {
   public static final FoliagePlacerType<BlobFoliagePlacer> BLOB_FOLIAGE_PLACER = func_227392_a_("blob_foliage_placer", BlobFoliagePlacer::new);
   public static final FoliagePlacerType<SpruceFoliagePlacer> SPRUCE_FOLIAGE_PLACER = func_227392_a_("spruce_foliage_placer", SpruceFoliagePlacer::new);
   public static final FoliagePlacerType<PineFoliagePlacer> PINE_FOLIAGE_PLACER = func_227392_a_("pine_foliage_placer", PineFoliagePlacer::new);
   public static final FoliagePlacerType<AcaciaFoliagePlacer> ACACIA_FOLIAGE_PLACER = func_227392_a_("acacia_foliage_placer", AcaciaFoliagePlacer::new);
   private final Function<Dynamic<?>, P> field_227390_e_;

   private static <P extends FoliagePlacer> FoliagePlacerType<P> func_227392_a_(String p_227392_0_, Function<Dynamic<?>, P> p_227392_1_) {
      return Registry.register(Registry.FOLIAGE_PLACER_TYPES, p_227392_0_, new FoliagePlacerType<>(p_227392_1_));
   }

   public FoliagePlacerType(Function<Dynamic<?>, P> p_i225849_1_) {
      this.field_227390_e_ = p_i225849_1_;
   }

   public P func_227391_a_(Dynamic<?> p_227391_1_) {
      return (P)(this.field_227390_e_.apply(p_227391_1_));
   }
}
