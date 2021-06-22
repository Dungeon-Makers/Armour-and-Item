package net.minecraft.util.math;

public abstract class RayTraceResult {
   protected final Vec3d location;

   protected RayTraceResult(Vec3d p_i51183_1_) {
      this.location = p_i51183_1_;
   }

   public abstract RayTraceResult.Type getType();
   /** Used to determine what sub-segment is hit */
   public int subHit = -1;

   /** Used to add extra hit info */
   public Object hitInfo = null;

   public Vec3d getLocation() {
      return this.location;
   }

   public static enum Type {
      MISS,
      BLOCK,
      ENTITY;
   }
}
