package net.minecraft.util.math;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.world.dimension.DimensionType;

public final class GlobalPos implements IDynamicSerializable {
   private final DimensionType dimension;
   private final BlockPos pos;

   private GlobalPos(DimensionType p_i50796_1_, BlockPos p_i50796_2_) {
      this.dimension = p_i50796_1_;
      this.pos = p_i50796_2_;
   }

   public static GlobalPos func_218179_a(DimensionType p_218179_0_, BlockPos p_218179_1_) {
      return new GlobalPos(p_218179_0_, p_218179_1_);
   }

   public static GlobalPos func_218176_a(Dynamic<?> p_218176_0_) {
      return p_218176_0_.get("dimension").map(DimensionType::func_218271_a).flatMap((p_218181_1_) -> {
         return p_218176_0_.get("pos").map(BlockPos::func_218286_a).map((p_218182_1_) -> {
            return new GlobalPos(p_218181_1_, p_218182_1_);
         });
      }).orElseThrow(() -> {
         return new IllegalArgumentException("Could not parse GlobalPos");
      });
   }

   public DimensionType func_218177_a() {
      return this.dimension;
   }

   public BlockPos pos() {
      return this.pos;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         GlobalPos globalpos = (GlobalPos)p_equals_1_;
         return Objects.equals(this.dimension, globalpos.dimension) && Objects.equals(this.pos, globalpos.pos);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return Objects.hash(this.dimension, this.pos);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("dimension"), this.dimension.func_218175_a(p_218175_1_), p_218175_1_.createString("pos"), this.pos.func_218175_a(p_218175_1_)));
   }

   public String toString() {
      return this.dimension.toString() + " " + this.pos;
   }
}