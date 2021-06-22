package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;
import net.minecraft.util.math.MathHelper;

public class RangedAttribute extends Attribute {
   private final double minValue;
   private final double maxValue;
   private String field_111119_c;

   public RangedAttribute(@Nullable IAttribute p_i45891_1_, String p_i45891_2_, double p_i45891_3_, double p_i45891_5_, double p_i45891_7_) {
      super(p_i45891_1_, p_i45891_2_, p_i45891_3_);
      this.minValue = p_i45891_5_;
      this.maxValue = p_i45891_7_;
      if (p_i45891_5_ > p_i45891_7_) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      } else if (p_i45891_3_ < p_i45891_5_) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      } else if (p_i45891_3_ > p_i45891_7_) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute func_111117_a(String p_111117_1_) {
      this.field_111119_c = p_111117_1_;
      return this;
   }

   public String func_111116_f() {
      return this.field_111119_c;
   }

   public double sanitizeValue(double p_111109_1_) {
      p_111109_1_ = MathHelper.clamp(p_111109_1_, this.minValue, this.maxValue);
      return p_111109_1_;
   }
}