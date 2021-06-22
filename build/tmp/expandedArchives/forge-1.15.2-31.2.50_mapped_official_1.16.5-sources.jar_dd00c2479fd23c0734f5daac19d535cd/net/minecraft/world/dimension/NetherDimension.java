package net.minecraft.world.dimension;

import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.NetherGenSettings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NetherDimension extends Dimension {
   private static final Vec3d field_227177_f_ = new Vec3d((double)0.2F, (double)0.03F, (double)0.03F);

   public NetherDimension(World p_i49934_1_, DimensionType p_i49934_2_) {
      super(p_i49934_1_, p_i49934_2_, 0.1F);
      this.field_76575_d = true;
      this.field_76576_e = true;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d func_76562_b(float p_76562_1_, float p_76562_2_) {
      return field_227177_f_;
   }

   public ChunkGenerator<?> func_186060_c() {
      NetherGenSettings nethergensettings = ChunkGeneratorType.field_206912_c.func_205483_a();
      nethergensettings.func_214969_a(Blocks.NETHERRACK.defaultBlockState());
      nethergensettings.func_214970_b(Blocks.LAVA.defaultBlockState());
      return ChunkGeneratorType.field_206912_c.create(this.field_76579_a, BiomeProviderType.field_205461_c.func_205457_a(BiomeProviderType.field_205461_c.func_226840_a_(this.field_76579_a.getLevelData()).func_205436_a(Biomes.field_76778_j)), nethergensettings);
   }

   public boolean func_76569_d() {
      return false;
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos p_206920_1_, boolean p_206920_2_) {
      return null;
   }

   @Nullable
   public BlockPos func_206921_a(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      return null;
   }

   public float func_76563_a(long p_76563_1_, float p_76563_3_) {
      return 0.5F;
   }

   public boolean func_76567_e() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_76568_b(int p_76568_1_, int p_76568_2_) {
      return true;
   }

   public WorldBorder func_177501_r() {
      return new WorldBorder() {
         public double func_177731_f() {
            return super.func_177731_f() / 8.0D;
         }

         public double func_177721_g() {
            return super.func_177721_g() / 8.0D;
         }
      };
   }
}
