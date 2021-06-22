package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
   public static final Vec3i ZERO = new Vec3i(0, 0, 0);
   @Deprecated
   private final int x;
   @Deprecated
   private final int y;
   @Deprecated
   private final int z;

   public Vec3i(int p_i46007_1_, int p_i46007_2_, int p_i46007_3_) {
      this.x = p_i46007_1_;
      this.y = p_i46007_2_;
      this.z = p_i46007_3_;
   }

   public Vec3i(double p_i46008_1_, double p_i46008_3_, double p_i46008_5_) {
      this(MathHelper.floor(p_i46008_1_), MathHelper.floor(p_i46008_3_), MathHelper.floor(p_i46008_5_));
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof Vec3i)) {
         return false;
      } else {
         Vec3i vec3i = (Vec3i)p_equals_1_;
         if (this.getX() != vec3i.getX()) {
            return false;
         } else if (this.getY() != vec3i.getY()) {
            return false;
         } else {
            return this.getZ() == vec3i.getZ();
         }
      }
   }

   public int hashCode() {
      return (this.getY() + this.getZ() * 31) * 31 + this.getX();
   }

   public int compareTo(Vec3i p_compareTo_1_) {
      if (this.getY() == p_compareTo_1_.getY()) {
         return this.getZ() == p_compareTo_1_.getZ() ? this.getX() - p_compareTo_1_.getX() : this.getZ() - p_compareTo_1_.getZ();
      } else {
         return this.getY() - p_compareTo_1_.getY();
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public Vec3i below() {
      return this.below(1);
   }

   public Vec3i below(int p_177979_1_) {
      return this.relative(Direction.DOWN, p_177979_1_);
   }

   public Vec3i relative(Direction p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new Vec3i(this.getX() + p_177967_1_.getStepX() * p_177967_2_, this.getY() + p_177967_1_.getStepY() * p_177967_2_, this.getZ() + p_177967_1_.getStepZ() * p_177967_2_);
   }

   public Vec3i cross(Vec3i p_177955_1_) {
      return new Vec3i(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public boolean closerThan(Vec3i p_218141_1_, double p_218141_2_) {
      return this.distSqr((double)p_218141_1_.getX(), (double)p_218141_1_.getY(), (double)p_218141_1_.getZ(), false) < p_218141_2_ * p_218141_2_;
   }

   public boolean closerThan(IPosition p_218137_1_, double p_218137_2_) {
      return this.distSqr(p_218137_1_.x(), p_218137_1_.y(), p_218137_1_.z(), true) < p_218137_2_ * p_218137_2_;
   }

   public double distSqr(Vec3i p_177951_1_) {
      return this.distSqr((double)p_177951_1_.getX(), (double)p_177951_1_.getY(), (double)p_177951_1_.getZ(), true);
   }

   public double distSqr(IPosition p_218138_1_, boolean p_218138_2_) {
      return this.distSqr(p_218138_1_.x(), p_218138_1_.y(), p_218138_1_.z(), p_218138_2_);
   }

   public double distSqr(double p_218140_1_, double p_218140_3_, double p_218140_5_, boolean p_218140_7_) {
      double d0 = p_218140_7_ ? 0.5D : 0.0D;
      double d1 = (double)this.getX() + d0 - p_218140_1_;
      double d2 = (double)this.getY() + d0 - p_218140_3_;
      double d3 = (double)this.getZ() + d0 - p_218140_5_;
      return d1 * d1 + d2 * d2 + d3 * d3;
   }

   public int distManhattan(Vec3i p_218139_1_) {
      float f = (float)Math.abs(p_218139_1_.getX() - this.getX());
      float f1 = (float)Math.abs(p_218139_1_.getY() - this.getY());
      float f2 = (float)Math.abs(p_218139_1_.getZ() - this.getZ());
      return (int)(f + f1 + f2);
   }

   public String toString() {
      return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
   }

   @OnlyIn(Dist.CLIENT)
   public String toShortString() {
      return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
   }
}