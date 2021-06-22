package net.minecraft.state;

import com.google.common.base.MoreObjects;

public abstract class Property<T extends Comparable<T>> implements IProperty<T> {
   private final Class<T> field_177704_a;
   private final String field_177703_b;
   private Integer field_206907_c;

   protected Property(String p_i45652_1_, Class<T> p_i45652_2_) {
      this.field_177704_a = p_i45652_2_;
      this.field_177703_b = p_i45652_1_;
   }

   public String getName() {
      return this.field_177703_b;
   }

   public Class<T> getValueClass() {
      return this.field_177704_a;
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.field_177703_b).add("clazz", this.field_177704_a).add("values", this.getPossibleValues()).toString();
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Property)) {
         return false;
      } else {
         Property<?> property = (Property)p_equals_1_;
         return this.field_177704_a.equals(property.field_177704_a) && this.field_177703_b.equals(property.field_177703_b);
      }
   }

   public final int hashCode() {
      if (this.field_206907_c == null) {
         this.field_206907_c = this.generateHashCode();
      }

      return this.field_206907_c;
   }

   public int generateHashCode() {
      return 31 * this.field_177704_a.hashCode() + this.field_177703_b.hashCode();
   }
}