package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.function.Function;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class SwampHutStructure extends ScatteredStructure<NoFeatureConfig> {
   private static final List<Biome.SpawnListEntry> SWAMPHUT_ENEMIES = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.WITCH, 1, 1, 1));
   private static final List<Biome.SpawnListEntry> SWAMPHUT_ANIMALS = Lists.newArrayList(new Biome.SpawnListEntry(EntityType.CAT, 1, 1, 1));

   public SwampHutStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51424_1_) {
      super(p_i51424_1_);
   }

   public String getFeatureName() {
      return "Swamp_Hut";
   }

   public int func_202367_b() {
      return 3;
   }

   public Structure.IStartFactory getStartFactory() {
      return SwampHutStructure.Start::new;
   }

   protected int func_202382_c() {
      return 14357620;
   }

   public List<Biome.SpawnListEntry> getSpecialEnemies() {
      return SWAMPHUT_ENEMIES;
   }

   public List<Biome.SpawnListEntry> getSpecialAnimals() {
      return SWAMPHUT_ANIMALS;
   }

   public boolean func_202383_b(IWorld p_202383_1_, BlockPos p_202383_2_) {
      StructureStart structurestart = this.func_202364_a(p_202383_1_, p_202383_2_, true);
      if (structurestart != StructureStart.INVALID_START && structurestart instanceof SwampHutStructure.Start && !structurestart.getPieces().isEmpty()) {
         StructurePiece structurepiece = structurestart.getPieces().get(0);
         return structurepiece instanceof SwampHutPiece;
      } else {
         return false;
      }
   }

   public static class Start extends StructureStart {
      public Start(Structure<?> p_i225819_1_, int p_i225819_2_, int p_i225819_3_, MutableBoundingBox p_i225819_4_, int p_i225819_5_, long p_i225819_6_) {
         super(p_i225819_1_, p_i225819_2_, p_i225819_3_, p_i225819_4_, p_i225819_5_, p_i225819_6_);
      }

      public void func_214625_a(ChunkGenerator<?> p_214625_1_, TemplateManager p_214625_2_, int p_214625_3_, int p_214625_4_, Biome p_214625_5_) {
         SwampHutPiece swamphutpiece = new SwampHutPiece(this.random, p_214625_3_ * 16, p_214625_4_ * 16);
         this.pieces.add(swamphutpiece);
         this.calculateBoundingBox();
      }
   }
}