package net.minecraft.util.math;

public class CubeCoordinateIterator {
   private final int field_218305_a;
   private final int field_218306_b;
   private final int field_218307_c;
   private final int field_218308_d;
   private final int field_218309_e;
   private final int field_218310_f;
   private int x;
   private int y;
   private int z;
   private boolean field_218314_j;

   public CubeCoordinateIterator(int p_i50798_1_, int p_i50798_2_, int p_i50798_3_, int p_i50798_4_, int p_i50798_5_, int p_i50798_6_) {
      this.field_218305_a = p_i50798_1_;
      this.field_218306_b = p_i50798_2_;
      this.field_218307_c = p_i50798_3_;
      this.field_218308_d = p_i50798_4_;
      this.field_218309_e = p_i50798_5_;
      this.field_218310_f = p_i50798_6_;
   }

   public boolean advance() {
      if (!this.field_218314_j) {
         this.x = this.field_218305_a;
         this.y = this.field_218306_b;
         this.z = this.field_218307_c;
         this.field_218314_j = true;
         return true;
      } else if (this.x == this.field_218308_d && this.y == this.field_218309_e && this.z == this.field_218310_f) {
         return false;
      } else {
         if (this.x < this.field_218308_d) {
            ++this.x;
         } else if (this.y < this.field_218309_e) {
            this.x = this.field_218305_a;
            ++this.y;
         } else if (this.z < this.field_218310_f) {
            this.x = this.field_218305_a;
            this.y = this.field_218306_b;
            ++this.z;
         }

         return true;
      }
   }

   public int nextX() {
      return this.x;
   }

   public int nextY() {
      return this.y;
   }

   public int nextZ() {
      return this.z;
   }

   public int getNextType() {
      int i = 0;
      if (this.x == this.field_218305_a || this.x == this.field_218308_d) {
         ++i;
      }

      if (this.y == this.field_218306_b || this.y == this.field_218309_e) {
         ++i;
      }

      if (this.z == this.field_218307_c || this.z == this.field_218310_f) {
         ++i;
      }

      return i;
   }
}