package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Dimension implements net.minecraftforge.common.extensions.IForgeDimension {
   public static final float[] field_111203_a = new float[]{1.0F, 0.75F, 0.5F, 0.25F, 0.0F, 0.25F, 0.5F, 0.75F};
   protected final World field_76579_a;
   private final DimensionType field_222669_f;
   protected boolean field_76575_d;
   protected boolean field_76576_e;
   protected final float[] field_76573_f = new float[16];
   private final float[] field_76580_h = new float[4];

   public Dimension(World p_i225788_1_, DimensionType p_i225788_2_, float p_i225788_3_) {
      this.field_76579_a = p_i225788_1_;
      this.field_222669_f = p_i225788_2_;

      for(int i = 0; i <= 15; ++i) {
         float f = (float)i / 15.0F;
         float f1 = f / (4.0F - 3.0F * f);
         this.field_76573_f[i] = MathHelper.lerp(p_i225788_3_, f1, 1.0F);
      }

   }

   public int func_76559_b(long p_76559_1_) {
      return (int)(p_76559_1_ / 24000L % 8L + 8L) % 8;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] func_76560_a(float p_76560_1_, float p_76560_2_) {
      float f = 0.4F;
      float f1 = MathHelper.cos(p_76560_1_ * ((float)Math.PI * 2F)) - 0.0F;
      float f2 = -0.0F;
      if (f1 >= -0.4F && f1 <= 0.4F) {
         float f3 = (f1 - -0.0F) / 0.4F * 0.5F + 0.5F;
         float f4 = 1.0F - (1.0F - MathHelper.sin(f3 * (float)Math.PI)) * 0.99F;
         f4 = f4 * f4;
         this.field_76580_h[0] = f3 * 0.3F + 0.7F;
         this.field_76580_h[1] = f3 * f3 * 0.7F + 0.2F;
         this.field_76580_h[2] = f3 * f3 * 0.0F + 0.2F;
         this.field_76580_h[3] = f4;
         return this.field_76580_h;
      } else {
         return null;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float func_76571_f() {
      return this.getWorld().getLevelData().func_76067_t().getCloudHeight();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_76561_g() {
      return true;
   }

   @Nullable
   public BlockPos func_177496_h() {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_76565_k() {
      return this.field_76579_a.getLevelData().func_76067_t().voidFadeMagnitude();
   }

   public boolean func_177500_n() {
      return this.field_76575_d;
   }

   public boolean func_191066_m() {
      return this.field_222669_f.hasSkyLight();
   }

   public boolean func_177495_o() {
      return this.field_76576_e;
   }

   public float func_227174_a_(int p_227174_1_) {
      return this.field_76573_f[p_227174_1_];
   }

   public WorldBorder func_177501_r() {
      return new WorldBorder();
   }

   public void func_186057_q() {
   }

   public void func_186059_r() {
   }

   @Deprecated //Forge: Use WorldType.createChunkGenerator
   public abstract ChunkGenerator<?> func_186060_c();

   @Nullable
   public abstract BlockPos func_206920_a(ChunkPos p_206920_1_, boolean p_206920_2_);

   @Nullable
   public abstract BlockPos func_206921_a(int p_206921_1_, int p_206921_2_, boolean p_206921_3_);

   public abstract float func_76563_a(long p_76563_1_, float p_76563_3_);

   public abstract boolean func_76569_d();

   @OnlyIn(Dist.CLIENT)
   public abstract Vec3d func_76562_b(float p_76562_1_, float p_76562_2_);

   public abstract boolean func_76567_e();

   @OnlyIn(Dist.CLIENT)
   public abstract boolean func_76568_b(int p_76568_1_, int p_76568_2_);

   public DimensionType func_186058_p() {
       return this.field_222669_f;
   }

   /*======================================= Forge Start =========================================*/
   private net.minecraftforge.client.IRenderHandler skyRenderer = null;
   private net.minecraftforge.client.IRenderHandler cloudRenderer = null;
   private net.minecraftforge.client.IRenderHandler weatherRenderer = null;

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getSkyRenderer() {
      return this.skyRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setSkyRenderer(net.minecraftforge.client.IRenderHandler skyRenderer) {
      this.skyRenderer = skyRenderer;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getCloudRenderer() {
      return cloudRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setCloudRenderer(net.minecraftforge.client.IRenderHandler renderer) {
      cloudRenderer = renderer;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   @Override
   public net.minecraftforge.client.IRenderHandler getWeatherRenderer() {
      return weatherRenderer;
   }

   @OnlyIn(Dist.CLIENT)
   @Override
   public void setWeatherRenderer(net.minecraftforge.client.IRenderHandler renderer) {
      weatherRenderer = renderer;
   }

   @Override
   public void resetRainAndThunder() {
      field_76579_a.getLevelData().setRainTime(0);
      field_76579_a.getLevelData().setRaining(false);
      field_76579_a.getLevelData().setThunderTime(0);
      field_76579_a.getLevelData().setThundering(false);
   }

   @Override
   public World getWorld() {
      return this.field_76579_a;
   }
}
