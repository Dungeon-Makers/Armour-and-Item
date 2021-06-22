package net.minecraft.util;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   private final long[] data;
   private final int bits;
   private final long mask;
   private final int size;

   public BitArray(int p_i46832_1_, int p_i46832_2_) {
      this(p_i46832_1_, p_i46832_2_, new long[MathHelper.roundUp(p_i46832_2_ * p_i46832_1_, 64) / 64]);
   }

   public BitArray(int p_i47901_1_, int p_i47901_2_, long[] p_i47901_3_) {
      Validate.inclusiveBetween(1L, 32L, (long)p_i47901_1_);
      this.size = p_i47901_2_;
      this.bits = p_i47901_1_;
      this.data = p_i47901_3_;
      this.mask = (1L << p_i47901_1_) - 1L;
      int i = MathHelper.roundUp(p_i47901_2_ * p_i47901_1_, 64) / 64;
      if (p_i47901_3_.length != i) {
         throw (RuntimeException)Util.pauseInIde(new RuntimeException("Invalid length given for storage, got: " + p_i47901_3_.length + " but expected: " + i));
      }
   }

   public int getAndSet(int p_219789_1_, int p_219789_2_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_219789_1_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_219789_2_);
      int i = p_219789_1_ * this.bits;
      int j = i >> 6;
      int k = (p_219789_1_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      int i1 = 0;
      i1 = i1 | (int)(this.data[j] >>> l & this.mask);
      this.data[j] = this.data[j] & ~(this.mask << l) | ((long)p_219789_2_ & this.mask) << l;
      if (j != k) {
         int j1 = 64 - l;
         int k1 = this.bits - j1;
         i1 |= (int)(this.data[k] << j1 & this.mask);
         this.data[k] = this.data[k] >>> k1 << k1 | ((long)p_219789_2_ & this.mask) >> j1;
      }

      return i1;
   }

   public void set(int p_188141_1_, int p_188141_2_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_188141_1_);
      Validate.inclusiveBetween(0L, this.mask, (long)p_188141_2_);
      int i = p_188141_1_ * this.bits;
      int j = i >> 6;
      int k = (p_188141_1_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      this.data[j] = this.data[j] & ~(this.mask << l) | ((long)p_188141_2_ & this.mask) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bits - i1;
         this.data[k] = this.data[k] >>> j1 << j1 | ((long)p_188141_2_ & this.mask) >> i1;
      }

   }

   public int get(int p_188142_1_) {
      Validate.inclusiveBetween(0L, (long)(this.size - 1), (long)p_188142_1_);
      int i = p_188142_1_ * this.bits;
      int j = i >> 6;
      int k = (p_188142_1_ + 1) * this.bits - 1 >> 6;
      int l = i ^ j << 6;
      if (j == k) {
         return (int)(this.data[j] >>> l & this.mask);
      } else {
         int i1 = 64 - l;
         return (int)((this.data[j] >>> l | this.data[k] << i1) & this.mask);
      }
   }

   public long[] getRaw() {
      return this.data;
   }

   public int getSize() {
      return this.size;
   }

   public int getBits() {
      return this.bits;
   }

   public void getAll(IntConsumer p_225421_1_) {
      int i = this.data.length;
      if (i != 0) {
         int j = 0;
         long k = this.data[0];
         long l = i > 1 ? this.data[1] : 0L;

         for(int i1 = 0; i1 < this.size; ++i1) {
            int j1 = i1 * this.bits;
            int k1 = j1 >> 6;
            int l1 = (i1 + 1) * this.bits - 1 >> 6;
            int i2 = j1 ^ k1 << 6;
            if (k1 != j) {
               k = l;
               l = k1 + 1 < i ? this.data[k1 + 1] : 0L;
               j = k1;
            }

            if (k1 == l1) {
               p_225421_1_.accept((int)(k >>> i2 & this.mask));
            } else {
               int j2 = 64 - i2;
               p_225421_1_.accept((int)((k >>> i2 | l << j2) & this.mask));
            }
         }

      }
   }
}