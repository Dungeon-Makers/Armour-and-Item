package net.minecraft.world.dimension;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.provider.BiomeProviderType;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.ChunkGeneratorType;
import net.minecraft.world.gen.EndGenerationSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndDimension extends Dimension {
   public static final BlockPos field_209958_g = new BlockPos(100, 50, 0);
   private final DragonFightManager field_186064_g;

   public EndDimension(World p_i49932_1_, DimensionType p_i49932_2_) {
      super(p_i49932_1_, p_i49932_2_, 0.0F);
      CompoundNBT compoundnbt = p_i49932_1_.getLevelData().func_186347_a(p_i49932_2_);
      this.field_186064_g = p_i49932_1_ instanceof ServerWorld ? new DragonFightManager((ServerWorld)p_i49932_1_, compoundnbt.getCompound("DragonFight"), this) : null;
   }

   public ChunkGenerator<?> func_186060_c() {
      EndGenerationSettings endgenerationsettings = ChunkGeneratorType.field_206913_d.func_205483_a();
      endgenerationsettings.func_214969_a(Blocks.END_STONE.defaultBlockState());
      endgenerationsettings.func_214970_b(Blocks.AIR.defaultBlockState());
      endgenerationsettings.func_205538_a(this.func_177496_h());
      return ChunkGeneratorType.field_206913_d.create(this.field_76579_a, BiomeProviderType.field_205463_e.func_205457_a(BiomeProviderType.field_205463_e.func_226840_a_(this.field_76579_a.getLevelData())), endgenerationsettings);
   }

   public float func_76563_a(long p_76563_1_, float p_76563_3_) {
      return 0.0F;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public float[] func_76560_a(float p_76560_1_, float p_76560_2_) {
      return null;
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d func_76562_b(float p_76562_1_, float p_76562_2_) {
      int i = 10518688;
      float f = MathHelper.cos(p_76562_1_ * ((float)Math.PI * 2F)) * 2.0F + 0.5F;
      f = MathHelper.clamp(f, 0.0F, 1.0F);
      float f1 = 0.627451F;
      float f2 = 0.5019608F;
      float f3 = 0.627451F;
      f1 = f1 * (f * 0.0F + 0.15F);
      f2 = f2 * (f * 0.0F + 0.15F);
      f3 = f3 * (f * 0.0F + 0.15F);
      return new Vec3d((double)f1, (double)f2, (double)f3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_76561_g() {
      return false;
   }

   public boolean func_76567_e() {
      return false;
   }

   public boolean func_76569_d() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_76571_f() {
      return 8.0F;
   }

   @Nullable
   public BlockPos func_206920_a(ChunkPos p_206920_1_, boolean p_206920_2_) {
      Random random = new Random(this.field_76579_a.getSeed());
      BlockPos blockpos = new BlockPos(p_206920_1_.getMinBlockX() + random.nextInt(15), 0, p_206920_1_.getMaxBlockZ() + random.nextInt(15));
      return this.field_76579_a.getTopBlockState(blockpos).getMaterial().blocksMotion() ? blockpos : null;
   }

   public BlockPos func_177496_h() {
      return field_209958_g;
   }

   @Nullable
   public BlockPos func_206921_a(int p_206921_1_, int p_206921_2_, boolean p_206921_3_) {
      return this.func_206920_a(new ChunkPos(p_206921_1_ >> 4, p_206921_2_ >> 4), p_206921_3_);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_76568_b(int p_76568_1_, int p_76568_2_) {
      return false;
   }

   public void func_186057_q() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (this.field_186064_g != null) {
         compoundnbt.put("DragonFight", this.field_186064_g.saveData());
      }

      this.field_76579_a.getLevelData().func_186345_a(this.field_76579_a.func_201675_m().func_186058_p(), compoundnbt);
   }

   public void func_186059_r() {
      if (this.field_186064_g != null) {
         this.field_186064_g.tick();
      }

   }

   @Nullable
   public DragonFightManager func_186063_s() {
      return this.field_186064_g;
   }
}
