package net.minecraft.world;

import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.dimension.Dimension;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IWorld extends IEntityReader, IWorldReader, IWorldGenerationReader {
   long getSeed();

   default float func_130001_d() {
      return this.func_201675_m().getCurrentMoonPhaseFactor(this.getLevel().getDayTime());
   }

   default float func_72826_c(float p_72826_1_) {
      return this.func_201675_m().func_76563_a(this.getLevel().getDayTime(), p_72826_1_);
   }

   @OnlyIn(Dist.CLIENT)
   default int func_72853_d() {
      return this.func_201675_m().func_76559_b(this.getLevel().getDayTime());
   }

   ITickList<Block> getBlockTicks();

   ITickList<Fluid> getLiquidTicks();

   World getLevel();

   WorldInfo getLevelData();

   DifficultyInstance getCurrentDifficultyAt(BlockPos p_175649_1_);

   default Difficulty getDifficulty() {
      return this.getLevelData().getDifficulty();
   }

   AbstractChunkProvider getChunkSource();

   default boolean hasChunk(int p_217354_1_, int p_217354_2_) {
      return this.getChunkSource().hasChunk(p_217354_1_, p_217354_2_);
   }

   Random getRandom();

   void func_195592_c(BlockPos p_195592_1_, Block p_195592_2_);

   @OnlyIn(Dist.CLIENT)
   BlockPos func_175694_M();

   void playSound(@Nullable PlayerEntity p_184133_1_, BlockPos p_184133_2_, SoundEvent p_184133_3_, SoundCategory p_184133_4_, float p_184133_5_, float p_184133_6_);

   void addParticle(IParticleData p_195594_1_, double p_195594_2_, double p_195594_4_, double p_195594_6_, double p_195594_8_, double p_195594_10_, double p_195594_12_);

   void levelEvent(@Nullable PlayerEntity p_217378_1_, int p_217378_2_, BlockPos p_217378_3_, int p_217378_4_);

   default void levelEvent(int p_217379_1_, BlockPos p_217379_2_, int p_217379_3_) {
      this.levelEvent((PlayerEntity)null, p_217379_1_, p_217379_2_, p_217379_3_);
   }

   default Stream<VoxelShape> func_223439_a(@Nullable Entity p_223439_1_, AxisAlignedBB p_223439_2_, Set<Entity> p_223439_3_) {
      return IEntityReader.super.func_223439_a(p_223439_1_, p_223439_2_, p_223439_3_);
   }

   default boolean isUnobstructed(@Nullable Entity p_195585_1_, VoxelShape p_195585_2_) {
      return IEntityReader.super.isUnobstructed(p_195585_1_, p_195585_2_);
   }

   default BlockPos getHeightmapPos(Heightmap.Type p_205770_1_, BlockPos p_205770_2_) {
      return IWorldReader.super.getHeightmapPos(p_205770_1_, p_205770_2_);
   }
}
