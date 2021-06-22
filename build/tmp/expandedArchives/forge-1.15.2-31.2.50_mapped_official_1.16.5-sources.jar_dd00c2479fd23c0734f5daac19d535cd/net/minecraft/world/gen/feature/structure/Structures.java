package net.minecraft.world.gen.feature.structure;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.template.TemplateManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Structures {
   private static final Logger field_151687_a = LogManager.getLogger();
   public static final Structure<?> field_215143_a = func_215141_a("Mineshaft", Feature.field_202329_g);
   public static final Structure<?> field_215144_b = func_215141_a("Pillager_Outpost", Feature.field_214536_b);
   public static final Structure<?> field_215145_c = func_215141_a("Fortress", Feature.field_202337_o);
   public static final Structure<?> field_215146_d = func_215141_a("Stronghold", Feature.field_202335_m);
   public static final Structure<?> field_215147_e = func_215141_a("Jungle_Pyramid", Feature.field_202331_i);
   public static final Structure<?> field_215148_f = func_215141_a("Ocean_Ruin", Feature.field_204029_o);
   public static final Structure<?> field_215149_g = func_215141_a("Desert_Pyramid", Feature.field_202332_j);
   public static final Structure<?> field_215150_h = func_215141_a("Igloo", Feature.field_202333_k);
   public static final Structure<?> field_215151_i = func_215141_a("Swamp_Hut", Feature.field_202334_l);
   public static final Structure<?> field_215152_j = func_215141_a("Monument", Feature.field_202336_n);
   public static final Structure<?> field_215153_k = func_215141_a("EndCity", Feature.field_204292_r);
   public static final Structure<?> field_215154_l = func_215141_a("Mansion", Feature.field_202330_h);
   public static final Structure<?> field_215155_m = func_215141_a("Buried_Treasure", Feature.field_214549_o);
   public static final Structure<?> field_215156_n = func_215141_a("Shipwreck", Feature.field_204751_l);
   public static final Structure<?> field_215157_o = func_215141_a("Village", Feature.field_214550_p);

   private static Structure<?> func_215141_a(String p_215141_0_, Structure<?> p_215141_1_) {
      if (true) return p_215141_1_; // FORGE: Registry replaced with slave map
      return Registry.register(Registry.STRUCTURE_FEATURE, p_215141_0_.toLowerCase(Locale.ROOT), p_215141_1_);
   }

   public static void func_215140_a() {
   }

   @Nullable
   public static StructureStart func_227456_a_(ChunkGenerator<?> p_227456_0_, TemplateManager p_227456_1_, CompoundNBT p_227456_2_) {
      String s = p_227456_2_.getString("id");
      if ("INVALID".equals(s)) {
         return StructureStart.INVALID_START;
      } else {
         Structure<?> structure = Registry.STRUCTURE_FEATURE.get(new ResourceLocation(s.toLowerCase(Locale.ROOT)));
         if (structure == null) {
            field_151687_a.error("Unknown feature id: {}", (Object)s);
            return null;
         } else {
            int i = p_227456_2_.getInt("ChunkX");
            int j = p_227456_2_.getInt("ChunkZ");
            int k = p_227456_2_.getInt("references");
            MutableBoundingBox mutableboundingbox = p_227456_2_.contains("BB") ? new MutableBoundingBox(p_227456_2_.getIntArray("BB")) : MutableBoundingBox.getUnknownBox();
            ListNBT listnbt = p_227456_2_.getList("Children", 10);

            try {
               StructureStart structurestart = structure.getStartFactory().create(structure, i, j, mutableboundingbox, k, p_227456_0_.func_202089_c());

               for(int l = 0; l < listnbt.size(); ++l) {
                  CompoundNBT compoundnbt = listnbt.getCompound(l);
                  String s1 = compoundnbt.getString("id");
                  IStructurePieceType istructurepiecetype = Registry.STRUCTURE_PIECE.get(new ResourceLocation(s1.toLowerCase(Locale.ROOT)));
                  if (istructurepiecetype == null) {
                     field_151687_a.error("Unknown structure piece id: {}", (Object)s1);
                  } else {
                     try {
                        StructurePiece structurepiece = istructurepiecetype.load(p_227456_1_, compoundnbt);
                        structurestart.pieces.add(structurepiece);
                     } catch (Exception exception) {
                        field_151687_a.error("Exception loading structure piece with id {}", s1, exception);
                     }
                  }
               }

               return structurestart;
            } catch (Exception exception1) {
               field_151687_a.error("Failed Start with id {}", s, exception1);
               return null;
            }
         }
      }
   }
}
