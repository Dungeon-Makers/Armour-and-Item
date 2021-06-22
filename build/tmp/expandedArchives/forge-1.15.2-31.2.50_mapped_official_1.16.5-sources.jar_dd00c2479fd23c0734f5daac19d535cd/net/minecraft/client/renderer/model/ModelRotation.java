package net.minecraft.client.renderer.model;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ModelRotation implements IModelTransform {
   X0_Y0(0, 0),
   X0_Y90(0, 90),
   X0_Y180(0, 180),
   X0_Y270(0, 270),
   X90_Y0(90, 0),
   X90_Y90(90, 90),
   X90_Y180(90, 180),
   X90_Y270(90, 270),
   X180_Y0(180, 0),
   X180_Y90(180, 90),
   X180_Y180(180, 180),
   X180_Y270(180, 270),
   X270_Y0(270, 0),
   X270_Y90(270, 90),
   X270_Y180(270, 180),
   X270_Y270(270, 270);

   private static final Map<Integer, ModelRotation> BY_INDEX = Arrays.stream(values()).collect(Collectors.toMap((p_229306_0_) -> {
      return p_229306_0_.index;
   }, (p_229305_0_) -> {
      return p_229305_0_;
   }));
   private final int index;
   private final Quaternion field_177544_s;
   private final int field_177543_t;
   private final int field_177542_u;

   private static int getIndex(int p_177521_0_, int p_177521_1_) {
      return p_177521_0_ * 360 + p_177521_1_;
   }

   private ModelRotation(int p_i46087_3_, int p_i46087_4_) {
      this.index = getIndex(p_i46087_3_, p_i46087_4_);
      Quaternion quaternion = new Quaternion(new Vector3f(0.0F, 1.0F, 0.0F), (float)(-p_i46087_4_), true);
      quaternion.mul(new Quaternion(new Vector3f(1.0F, 0.0F, 0.0F), (float)(-p_i46087_3_), true));
      this.field_177544_s = quaternion;
      this.field_177543_t = MathHelper.abs(p_i46087_3_ / 90);
      this.field_177542_u = MathHelper.abs(p_i46087_4_ / 90);
   }

   public TransformationMatrix getRotation() {
      return new TransformationMatrix((Vector3f)null, this.field_177544_s, (Vector3f)null, (Quaternion)null);
   }

   public static ModelRotation by(int p_177524_0_, int p_177524_1_) {
      return BY_INDEX.get(getIndex(MathHelper.positiveModulo(p_177524_0_, 360), MathHelper.positiveModulo(p_177524_1_, 360)));
   }
}