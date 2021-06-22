package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CompassItem extends Item {
   public CompassItem(Item.Properties p_i48515_1_) {
      super(p_i48515_1_);
      this.func_185043_a(new ResourceLocation("angle"), new IItemPropertyGetter() {
         @OnlyIn(Dist.CLIENT)
         private double field_185095_a;
         @OnlyIn(Dist.CLIENT)
         private double field_185096_b;
         @OnlyIn(Dist.CLIENT)
         private long field_185097_c;

         @OnlyIn(Dist.CLIENT)
         public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
            if (p_call_3_ == null && !p_call_1_.isFramed()) {
               return 0.0F;
            } else {
               boolean flag = p_call_3_ != null;
               Entity entity = (Entity)(flag ? p_call_3_ : p_call_1_.getFrame());
               if (p_call_2_ == null) {
                  p_call_2_ = entity.level;
               }

               double d0;
               if (p_call_2_.dimension.func_76569_d()) {
                  double d1 = flag ? (double)entity.yRot : this.func_185094_a((ItemFrameEntity)entity);
                  d1 = MathHelper.positiveModulo(d1 / 360.0D, 1.0D);
                  double d2 = this.func_185092_a(p_call_2_, entity) / (double)((float)Math.PI * 2F);
                  d0 = 0.5D - (d1 - 0.25D - d2);
               } else {
                  d0 = Math.random();
               }

               if (flag) {
                  d0 = this.func_185093_a(p_call_2_, d0);
               }

               return MathHelper.positiveModulo((float)d0, 1.0F);
            }
         }

         @OnlyIn(Dist.CLIENT)
         private double func_185093_a(World p_185093_1_, double p_185093_2_) {
            if (p_185093_1_.getGameTime() != this.field_185097_c) {
               this.field_185097_c = p_185093_1_.getGameTime();
               double d0 = p_185093_2_ - this.field_185095_a;
               d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
               this.field_185096_b += d0 * 0.1D;
               this.field_185096_b *= 0.8D;
               this.field_185095_a = MathHelper.positiveModulo(this.field_185095_a + this.field_185096_b, 1.0D);
            }

            return this.field_185095_a;
         }

         @OnlyIn(Dist.CLIENT)
         private double func_185094_a(ItemFrameEntity p_185094_1_) {
            return (double)MathHelper.wrapDegrees(180 + p_185094_1_.getDirection().get2DDataValue() * 90);
         }

         @OnlyIn(Dist.CLIENT)
         private double func_185092_a(IWorld p_185092_1_, Entity p_185092_2_) {
            BlockPos blockpos = p_185092_1_.func_175694_M();
            return Math.atan2((double)blockpos.getZ() - p_185092_2_.getZ(), (double)blockpos.getX() - p_185092_2_.getX());
         }
      });
   }
}