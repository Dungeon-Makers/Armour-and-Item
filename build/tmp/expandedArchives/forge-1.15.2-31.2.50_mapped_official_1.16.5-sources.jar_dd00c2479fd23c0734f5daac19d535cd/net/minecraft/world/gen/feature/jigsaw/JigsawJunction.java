package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class JigsawJunction {
   private final int sourceX;
   private final int sourceGroundY;
   private final int sourceZ;
   private final int deltaY;
   private final JigsawPattern.PlacementBehaviour destProjection;

   public JigsawJunction(int p_i51408_1_, int p_i51408_2_, int p_i51408_3_, int p_i51408_4_, JigsawPattern.PlacementBehaviour p_i51408_5_) {
      this.sourceX = p_i51408_1_;
      this.sourceGroundY = p_i51408_2_;
      this.sourceZ = p_i51408_3_;
      this.deltaY = p_i51408_4_;
      this.destProjection = p_i51408_5_;
   }

   public int getSourceX() {
      return this.sourceX;
   }

   public int getSourceGroundY() {
      return this.sourceGroundY;
   }

   public int getSourceZ() {
      return this.sourceZ;
   }

   public <T> Dynamic<T> func_214897_a(DynamicOps<T> p_214897_1_) {
      Builder<T, T> builder = ImmutableMap.builder();
      builder.put(p_214897_1_.createString("source_x"), p_214897_1_.createInt(this.sourceX)).put(p_214897_1_.createString("source_ground_y"), p_214897_1_.createInt(this.sourceGroundY)).put(p_214897_1_.createString("source_z"), p_214897_1_.createInt(this.sourceZ)).put(p_214897_1_.createString("delta_y"), p_214897_1_.createInt(this.deltaY)).put(p_214897_1_.createString("dest_proj"), p_214897_1_.createString(this.destProjection.getName()));
      return new Dynamic<>(p_214897_1_, p_214897_1_.createMap(builder.build()));
   }

   public static <T> JigsawJunction func_214894_a(Dynamic<T> p_214894_0_) {
      return new JigsawJunction(p_214894_0_.get("source_x").asInt(0), p_214894_0_.get("source_ground_y").asInt(0), p_214894_0_.get("source_z").asInt(0), p_214894_0_.get("delta_y").asInt(0), JigsawPattern.PlacementBehaviour.byName(p_214894_0_.get("dest_proj").asString("")));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         JigsawJunction jigsawjunction = (JigsawJunction)p_equals_1_;
         if (this.sourceX != jigsawjunction.sourceX) {
            return false;
         } else if (this.sourceZ != jigsawjunction.sourceZ) {
            return false;
         } else if (this.deltaY != jigsawjunction.deltaY) {
            return false;
         } else {
            return this.destProjection == jigsawjunction.destProjection;
         }
      } else {
         return false;
      }
   }

   public int hashCode() {
      int i = this.sourceX;
      i = 31 * i + this.sourceGroundY;
      i = 31 * i + this.sourceZ;
      i = 31 * i + this.deltaY;
      i = 31 * i + this.destProjection.hashCode();
      return i;
   }

   public String toString() {
      return "JigsawJunction{sourceX=" + this.sourceX + ", sourceGroundY=" + this.sourceGroundY + ", sourceZ=" + this.sourceZ + ", deltaY=" + this.deltaY + ", destProjection=" + this.destProjection + '}';
   }
}