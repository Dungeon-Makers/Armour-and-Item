package net.minecraft.world;

import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.world.gen.feature.structure.StructureStart;

public interface IStructureReader {
   @Nullable
   StructureStart func_201585_a(String p_201585_1_);

   void func_201584_a(String p_201584_1_, StructureStart p_201584_2_);

   LongSet func_201578_b(String p_201578_1_);

   void func_201583_a(String p_201583_1_, long p_201583_2_);

   Map<String, LongSet> getAllReferences();

   void setAllReferences(Map<String, LongSet> p_201606_1_);
}