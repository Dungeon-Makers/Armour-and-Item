package net.minecraft.item;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ClockItem extends Item {
   public ClockItem(Item.Properties p_i48517_1_) {
      super(p_i48517_1_);
      this.func_185043_a(new ResourceLocation("time"), new IItemPropertyGetter() {
         @OnlyIn(Dist.CLIENT)
         private double field_185088_a;
         @OnlyIn(Dist.CLIENT)
         private double field_185089_b;
         @OnlyIn(Dist.CLIENT)
         private long field_185090_c;

         @OnlyIn(Dist.CLIENT)
         public float call(ItemStack p_call_1_, @Nullable World p_call_2_, @Nullable LivingEntity p_call_3_) {
            boolean flag = p_call_3_ != null;
            Entity entity = (Entity)(flag ? p_call_3_ : p_call_1_.getFrame());
            if (p_call_2_ == null && entity != null) {
               p_call_2_ = entity.level;
            }

            if (p_call_2_ == null) {
               return 0.0F;
            } else {
               double d0;
               if (p_call_2_.dimension.func_76569_d()) {
                  d0 = (double)p_call_2_.func_72826_c(1.0F);
               } else {
                  d0 = Math.random();
               }

               d0 = this.func_185087_a(p_call_2_, d0);
               return (float)d0;
            }
         }

         @OnlyIn(Dist.CLIENT)
         private double func_185087_a(World p_185087_1_, double p_185087_2_) {
            if (p_185087_1_.getGameTime() != this.field_185090_c) {
               this.field_185090_c = p_185087_1_.getGameTime();
               double d0 = p_185087_2_ - this.field_185088_a;
               d0 = MathHelper.positiveModulo(d0 + 0.5D, 1.0D) - 0.5D;
               this.field_185089_b += d0 * 0.1D;
               this.field_185089_b *= 0.9D;
               this.field_185088_a = MathHelper.positiveModulo(this.field_185088_a + this.field_185089_b, 1.0D);
            }

            return this.field_185088_a;
         }
      });
   }
}