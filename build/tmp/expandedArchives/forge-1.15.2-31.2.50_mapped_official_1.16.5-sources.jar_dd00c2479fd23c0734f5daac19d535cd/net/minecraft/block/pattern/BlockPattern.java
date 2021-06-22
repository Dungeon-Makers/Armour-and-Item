package net.minecraft.block.pattern;

import com.google.common.base.MoreObjects;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.LoadingCache;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.util.CachedBlockInfo;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IWorldReader;

public class BlockPattern {
   private final Predicate<CachedBlockInfo>[][][] pattern;
   private final int depth;
   private final int height;
   private final int width;

   public BlockPattern(Predicate<CachedBlockInfo>[][][] p_i48279_1_) {
      this.pattern = p_i48279_1_;
      this.depth = p_i48279_1_.length;
      if (this.depth > 0) {
         this.height = p_i48279_1_[0].length;
         if (this.height > 0) {
            this.width = p_i48279_1_[0][0].length;
         } else {
            this.width = 0;
         }
      } else {
         this.height = 0;
         this.width = 0;
      }

   }

   public int getDepth() {
      return this.depth;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   @Nullable
   private BlockPattern.PatternHelper matches(BlockPos p_177682_1_, Direction p_177682_2_, Direction p_177682_3_, LoadingCache<BlockPos, CachedBlockInfo> p_177682_4_) {
      for(int i = 0; i < this.width; ++i) {
         for(int j = 0; j < this.height; ++j) {
            for(int k = 0; k < this.depth; ++k) {
               if (!this.pattern[k][j][i].test(p_177682_4_.getUnchecked(translateAndRotate(p_177682_1_, p_177682_2_, p_177682_3_, i, j, k)))) {
                  return null;
               }
            }
         }
      }

      return new BlockPattern.PatternHelper(p_177682_1_, p_177682_2_, p_177682_3_, p_177682_4_, this.width, this.height, this.depth);
   }

   @Nullable
   public BlockPattern.PatternHelper find(IWorldReader p_177681_1_, BlockPos p_177681_2_) {
      LoadingCache<BlockPos, CachedBlockInfo> loadingcache = createLevelCache(p_177681_1_, false);
      int i = Math.max(Math.max(this.width, this.height), this.depth);

      for(BlockPos blockpos : BlockPos.betweenClosed(p_177681_2_, p_177681_2_.offset(i - 1, i - 1, i - 1))) {
         for(Direction direction : Direction.values()) {
            for(Direction direction1 : Direction.values()) {
               if (direction1 != direction && direction1 != direction.getOpposite()) {
                  BlockPattern.PatternHelper blockpattern$patternhelper = this.matches(blockpos, direction, direction1, loadingcache);
                  if (blockpattern$patternhelper != null) {
                     return blockpattern$patternhelper;
                  }
               }
            }
         }
      }

      return null;
   }

   public static LoadingCache<BlockPos, CachedBlockInfo> createLevelCache(IWorldReader p_181627_0_, boolean p_181627_1_) {
      return CacheBuilder.newBuilder().build(new BlockPattern.CacheLoader(p_181627_0_, p_181627_1_));
   }

   protected static BlockPos translateAndRotate(BlockPos p_177683_0_, Direction p_177683_1_, Direction p_177683_2_, int p_177683_3_, int p_177683_4_, int p_177683_5_) {
      if (p_177683_1_ != p_177683_2_ && p_177683_1_ != p_177683_2_.getOpposite()) {
         Vec3i vec3i = new Vec3i(p_177683_1_.getStepX(), p_177683_1_.getStepY(), p_177683_1_.getStepZ());
         Vec3i vec3i1 = new Vec3i(p_177683_2_.getStepX(), p_177683_2_.getStepY(), p_177683_2_.getStepZ());
         Vec3i vec3i2 = vec3i.cross(vec3i1);
         return p_177683_0_.offset(vec3i1.getX() * -p_177683_4_ + vec3i2.getX() * p_177683_3_ + vec3i.getX() * p_177683_5_, vec3i1.getY() * -p_177683_4_ + vec3i2.getY() * p_177683_3_ + vec3i.getY() * p_177683_5_, vec3i1.getZ() * -p_177683_4_ + vec3i2.getZ() * p_177683_3_ + vec3i.getZ() * p_177683_5_);
      } else {
         throw new IllegalArgumentException("Invalid forwards & up combination");
      }
   }

   static class CacheLoader extends com.google.common.cache.CacheLoader<BlockPos, CachedBlockInfo> {
      private final IWorldReader level;
      private final boolean loadChunks;

      public CacheLoader(IWorldReader p_i48983_1_, boolean p_i48983_2_) {
         this.level = p_i48983_1_;
         this.loadChunks = p_i48983_2_;
      }

      public CachedBlockInfo load(BlockPos p_load_1_) throws Exception {
         return new CachedBlockInfo(this.level, p_load_1_, this.loadChunks);
      }
   }

   public static class PatternHelper {
      private final BlockPos frontTopLeft;
      private final Direction forwards;
      private final Direction up;
      private final LoadingCache<BlockPos, CachedBlockInfo> cache;
      private final int width;
      private final int height;
      private final int depth;

      public PatternHelper(BlockPos p_i46378_1_, Direction p_i46378_2_, Direction p_i46378_3_, LoadingCache<BlockPos, CachedBlockInfo> p_i46378_4_, int p_i46378_5_, int p_i46378_6_, int p_i46378_7_) {
         this.frontTopLeft = p_i46378_1_;
         this.forwards = p_i46378_2_;
         this.up = p_i46378_3_;
         this.cache = p_i46378_4_;
         this.width = p_i46378_5_;
         this.height = p_i46378_6_;
         this.depth = p_i46378_7_;
      }

      public BlockPos getFrontTopLeft() {
         return this.frontTopLeft;
      }

      public Direction getForwards() {
         return this.forwards;
      }

      public Direction getUp() {
         return this.up;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public CachedBlockInfo getBlock(int p_177670_1_, int p_177670_2_, int p_177670_3_) {
         return this.cache.getUnchecked(BlockPattern.translateAndRotate(this.frontTopLeft, this.getForwards(), this.getUp(), p_177670_1_, p_177670_2_, p_177670_3_));
      }

      public String toString() {
         return MoreObjects.toStringHelper(this).add("up", this.up).add("forwards", this.forwards).add("frontTopLeft", this.frontTopLeft).toString();
      }

      public BlockPattern.PortalInfo func_222504_a(Direction p_222504_1_, BlockPos p_222504_2_, double p_222504_3_, Vec3d p_222504_5_, double p_222504_6_) {
         Direction direction = this.getForwards();
         Direction direction1 = direction.getClockWise();
         double d1 = (double)(this.getFrontTopLeft().getY() + 1) - p_222504_3_ * (double)this.getHeight();
         double d0;
         double d2;
         if (direction1 == Direction.NORTH) {
            d0 = (double)p_222504_2_.getX() + 0.5D;
            d2 = (double)(this.getFrontTopLeft().getZ() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (direction1 == Direction.SOUTH) {
            d0 = (double)p_222504_2_.getX() + 0.5D;
            d2 = (double)this.getFrontTopLeft().getZ() + (1.0D - p_222504_6_) * (double)this.getWidth();
         } else if (direction1 == Direction.WEST) {
            d0 = (double)(this.getFrontTopLeft().getX() + 1) - (1.0D - p_222504_6_) * (double)this.getWidth();
            d2 = (double)p_222504_2_.getZ() + 0.5D;
         } else {
            d0 = (double)this.getFrontTopLeft().getX() + (1.0D - p_222504_6_) * (double)this.getWidth();
            d2 = (double)p_222504_2_.getZ() + 0.5D;
         }

         double d3;
         double d4;
         if (direction.getOpposite() == p_222504_1_) {
            d3 = p_222504_5_.x;
            d4 = p_222504_5_.z;
         } else if (direction.getOpposite() == p_222504_1_.getOpposite()) {
            d3 = -p_222504_5_.x;
            d4 = -p_222504_5_.z;
         } else if (direction.getOpposite() == p_222504_1_.getClockWise()) {
            d3 = -p_222504_5_.z;
            d4 = p_222504_5_.x;
         } else {
            d3 = p_222504_5_.z;
            d4 = -p_222504_5_.x;
         }

         int i = (direction.get2DDataValue() - p_222504_1_.getOpposite().get2DDataValue()) * 90;
         return new BlockPattern.PortalInfo(new Vec3d(d0, d1, d2), new Vec3d(d3, p_222504_5_.y, d4), i);
      }
   }

   public static class PortalInfo {
      public final Vec3d pos;
      public final Vec3d speed;
      public final int field_222507_c;

      public PortalInfo(Vec3d p_i50457_1_, Vec3d p_i50457_2_, int p_i50457_3_) {
         this.pos = p_i50457_1_;
         this.speed = p_i50457_2_;
         this.field_222507_c = p_i50457_3_;
      }
   }
}