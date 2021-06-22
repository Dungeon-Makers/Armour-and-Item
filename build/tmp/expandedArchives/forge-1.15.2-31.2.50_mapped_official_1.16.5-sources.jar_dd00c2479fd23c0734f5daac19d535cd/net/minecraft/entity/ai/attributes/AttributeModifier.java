package net.minecraft.entity.ai.attributes;

import io.netty.util.internal.ThreadLocalRandom;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.util.math.MathHelper;

public class AttributeModifier {
   private final double amount;
   private final AttributeModifier.Operation operation;
   private final Supplier<String> nameGetter;
   private final UUID id;
   private boolean field_111171_e = true;

   public AttributeModifier(String p_i50375_1_, double p_i50375_2_, AttributeModifier.Operation p_i50375_4_) {
      this(MathHelper.createInsecureUUID(ThreadLocalRandom.current()), () -> {
         return p_i50375_1_;
      }, p_i50375_2_, p_i50375_4_);
   }

   public AttributeModifier(UUID p_i50376_1_, String p_i50376_2_, double p_i50376_3_, AttributeModifier.Operation p_i50376_5_) {
      this(p_i50376_1_, () -> {
         return p_i50376_2_;
      }, p_i50376_3_, p_i50376_5_);
   }

   public AttributeModifier(UUID p_i50377_1_, Supplier<String> p_i50377_2_, double p_i50377_3_, AttributeModifier.Operation p_i50377_5_) {
      this.id = p_i50377_1_;
      this.nameGetter = p_i50377_2_;
      this.amount = p_i50377_3_;
      this.operation = p_i50377_5_;
   }

   public UUID getId() {
      return this.id;
   }

   public String getName() {
      return this.nameGetter.get();
   }

   public AttributeModifier.Operation getOperation() {
      return this.operation;
   }

   public double getAmount() {
      return this.amount;
   }

   public boolean func_111165_e() {
      return this.field_111171_e;
   }

   public AttributeModifier func_111168_a(boolean p_111168_1_) {
      this.field_111171_e = p_111168_1_;
      return this;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (p_equals_1_ != null && this.getClass() == p_equals_1_.getClass()) {
         AttributeModifier attributemodifier = (AttributeModifier)p_equals_1_;
         return Objects.equals(this.id, attributemodifier.id);
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id != null ? this.id.hashCode() : 0;
   }

   public String toString() {
      return "AttributeModifier{amount=" + this.amount + ", operation=" + this.operation + ", name='" + (String)this.nameGetter.get() + '\'' + ", id=" + this.id + ", serialize=" + this.field_111171_e + '}';
   }

   public static enum Operation {
      ADDITION(0),
      MULTIPLY_BASE(1),
      MULTIPLY_TOTAL(2);

      private static final AttributeModifier.Operation[] OPERATIONS = new AttributeModifier.Operation[]{ADDITION, MULTIPLY_BASE, MULTIPLY_TOTAL};
      private final int value;

      private Operation(int p_i50050_3_) {
         this.value = p_i50050_3_;
      }

      public int toValue() {
         return this.value;
      }

      public static AttributeModifier.Operation fromValue(int p_220372_0_) {
         if (p_220372_0_ >= 0 && p_220372_0_ < OPERATIONS.length) {
            return OPERATIONS[p_220372_0_];
         } else {
            throw new IllegalArgumentException("No operation with value " + p_220372_0_);
         }
      }
   }
}