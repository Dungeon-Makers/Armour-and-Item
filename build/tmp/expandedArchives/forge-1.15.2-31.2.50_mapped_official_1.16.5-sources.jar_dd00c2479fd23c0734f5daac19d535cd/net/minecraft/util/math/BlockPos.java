package net.minecraft.util.math;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.List;
import java.util.Spliterator.OfInt;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.concurrent.Immutable;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisRotation;
import net.minecraft.util.Direction;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.Rotation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Immutable
public class BlockPos extends Vec3i implements IDynamicSerializable {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final BlockPos ZERO = new BlockPos(0, 0, 0);
   private static final int PACKED_X_LENGTH = 1 + MathHelper.log2(MathHelper.smallestEncompassingPowerOfTwo(30000000));
   private static final int PACKED_Z_LENGTH = PACKED_X_LENGTH;
   private static final int PACKED_Y_LENGTH = 64 - PACKED_X_LENGTH - PACKED_Z_LENGTH;
   private static final long PACKED_X_MASK = (1L << PACKED_X_LENGTH) - 1L;
   private static final long PACKED_Y_MASK = (1L << PACKED_Y_LENGTH) - 1L;
   private static final long PACKED_Z_MASK = (1L << PACKED_Z_LENGTH) - 1L;
   private static final int Z_OFFSET = PACKED_Y_LENGTH;
   private static final int X_OFFSET = PACKED_Y_LENGTH + PACKED_Z_LENGTH;

   public BlockPos(int p_i46030_1_, int p_i46030_2_, int p_i46030_3_) {
      super(p_i46030_1_, p_i46030_2_, p_i46030_3_);
   }

   public BlockPos(double p_i46031_1_, double p_i46031_3_, double p_i46031_5_) {
      super(p_i46031_1_, p_i46031_3_, p_i46031_5_);
   }

   public BlockPos(Entity p_i46032_1_) {
      this(p_i46032_1_.getX(), p_i46032_1_.getY(), p_i46032_1_.getZ());
   }

   public BlockPos(Vec3d p_i47100_1_) {
      this(p_i47100_1_.x, p_i47100_1_.y, p_i47100_1_.z);
   }

   public BlockPos(IPosition p_i50799_1_) {
      this(p_i50799_1_.x(), p_i50799_1_.y(), p_i50799_1_.z());
   }

   public BlockPos(Vec3i p_i46034_1_) {
      this(p_i46034_1_.getX(), p_i46034_1_.getY(), p_i46034_1_.getZ());
   }

   public static <T> BlockPos func_218286_a(Dynamic<T> p_218286_0_) {
      OfInt ofint = p_218286_0_.asIntStream().spliterator();
      int[] aint = new int[3];
      if (ofint.tryAdvance((Integer p_218285_1_) -> {
         aint[0] = p_218285_1_;
      }) && ofint.tryAdvance((Integer p_218280_1_) -> {
         aint[1] = p_218280_1_;
      })) {
         ofint.tryAdvance((Integer p_218284_1_) -> {
            aint[2] = p_218284_1_;
         });
      }

      return new BlockPos(aint[0], aint[1], aint[2]);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createIntList(IntStream.of(this.getX(), this.getY(), this.getZ()));
   }

   public static long offset(long p_218289_0_, Direction p_218289_2_) {
      return offset(p_218289_0_, p_218289_2_.getStepX(), p_218289_2_.getStepY(), p_218289_2_.getStepZ());
   }

   public static long offset(long p_218291_0_, int p_218291_2_, int p_218291_3_, int p_218291_4_) {
      return asLong(getX(p_218291_0_) + p_218291_2_, getY(p_218291_0_) + p_218291_3_, getZ(p_218291_0_) + p_218291_4_);
   }

   public static int getX(long p_218290_0_) {
      return (int)(p_218290_0_ << 64 - X_OFFSET - PACKED_X_LENGTH >> 64 - PACKED_X_LENGTH);
   }

   public static int getY(long p_218274_0_) {
      return (int)(p_218274_0_ << 64 - PACKED_Y_LENGTH >> 64 - PACKED_Y_LENGTH);
   }

   public static int getZ(long p_218282_0_) {
      return (int)(p_218282_0_ << 64 - Z_OFFSET - PACKED_Z_LENGTH >> 64 - PACKED_Z_LENGTH);
   }

   public static BlockPos of(long p_218283_0_) {
      return new BlockPos(getX(p_218283_0_), getY(p_218283_0_), getZ(p_218283_0_));
   }

   public static long asLong(int p_218276_0_, int p_218276_1_, int p_218276_2_) {
      long i = 0L;
      i = i | ((long)p_218276_0_ & PACKED_X_MASK) << X_OFFSET;
      i = i | ((long)p_218276_1_ & PACKED_Y_MASK) << 0;
      i = i | ((long)p_218276_2_ & PACKED_Z_MASK) << Z_OFFSET;
      return i;
   }

   public static long getFlatIndex(long p_218288_0_) {
      return p_218288_0_ & -16L;
   }

   public long asLong() {
      return asLong(this.getX(), this.getY(), this.getZ());
   }

   public BlockPos offset(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
      return p_177963_1_ == 0.0D && p_177963_3_ == 0.0D && p_177963_5_ == 0.0D ? this : new BlockPos((double)this.getX() + p_177963_1_, (double)this.getY() + p_177963_3_, (double)this.getZ() + p_177963_5_);
   }

   public BlockPos offset(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
      return p_177982_1_ == 0 && p_177982_2_ == 0 && p_177982_3_ == 0 ? this : new BlockPos(this.getX() + p_177982_1_, this.getY() + p_177982_2_, this.getZ() + p_177982_3_);
   }

   public BlockPos offset(Vec3i p_177971_1_) {
      return this.offset(p_177971_1_.getX(), p_177971_1_.getY(), p_177971_1_.getZ());
   }

   public BlockPos subtract(Vec3i p_177973_1_) {
      return this.offset(-p_177973_1_.getX(), -p_177973_1_.getY(), -p_177973_1_.getZ());
   }

   public BlockPos above() {
      return this.relative(Direction.UP);
   }

   public BlockPos above(int p_177981_1_) {
      return this.relative(Direction.UP, p_177981_1_);
   }

   public BlockPos below() {
      return this.relative(Direction.DOWN);
   }

   public BlockPos below(int p_177979_1_) {
      return this.relative(Direction.DOWN, p_177979_1_);
   }

   public BlockPos north() {
      return this.relative(Direction.NORTH);
   }

   public BlockPos north(int p_177964_1_) {
      return this.relative(Direction.NORTH, p_177964_1_);
   }

   public BlockPos south() {
      return this.relative(Direction.SOUTH);
   }

   public BlockPos south(int p_177970_1_) {
      return this.relative(Direction.SOUTH, p_177970_1_);
   }

   public BlockPos west() {
      return this.relative(Direction.WEST);
   }

   public BlockPos west(int p_177985_1_) {
      return this.relative(Direction.WEST, p_177985_1_);
   }

   public BlockPos east() {
      return this.relative(Direction.EAST);
   }

   public BlockPos east(int p_177965_1_) {
      return this.relative(Direction.EAST, p_177965_1_);
   }

   public BlockPos relative(Direction p_177972_1_) {
      return new BlockPos(this.getX() + p_177972_1_.getStepX(), this.getY() + p_177972_1_.getStepY(), this.getZ() + p_177972_1_.getStepZ());
   }

   public BlockPos relative(Direction p_177967_1_, int p_177967_2_) {
      return p_177967_2_ == 0 ? this : new BlockPos(this.getX() + p_177967_1_.getStepX() * p_177967_2_, this.getY() + p_177967_1_.getStepY() * p_177967_2_, this.getZ() + p_177967_1_.getStepZ() * p_177967_2_);
   }

   public BlockPos rotate(Rotation p_190942_1_) {
      switch(p_190942_1_) {
      case NONE:
      default:
         return this;
      case CLOCKWISE_90:
         return new BlockPos(-this.getZ(), this.getY(), this.getX());
      case CLOCKWISE_180:
         return new BlockPos(-this.getX(), this.getY(), -this.getZ());
      case COUNTERCLOCKWISE_90:
         return new BlockPos(this.getZ(), this.getY(), -this.getX());
      }
   }

   public BlockPos cross(Vec3i p_177955_1_) {
      return new BlockPos(this.getY() * p_177955_1_.getZ() - this.getZ() * p_177955_1_.getY(), this.getZ() * p_177955_1_.getX() - this.getX() * p_177955_1_.getZ(), this.getX() * p_177955_1_.getY() - this.getY() * p_177955_1_.getX());
   }

   public BlockPos immutable() {
      return this;
   }

   public static Iterable<BlockPos> betweenClosed(BlockPos p_218278_0_, BlockPos p_218278_1_) {
      return betweenClosed(Math.min(p_218278_0_.getX(), p_218278_1_.getX()), Math.min(p_218278_0_.getY(), p_218278_1_.getY()), Math.min(p_218278_0_.getZ(), p_218278_1_.getZ()), Math.max(p_218278_0_.getX(), p_218278_1_.getX()), Math.max(p_218278_0_.getY(), p_218278_1_.getY()), Math.max(p_218278_0_.getZ(), p_218278_1_.getZ()));
   }

   public static Stream<BlockPos> betweenClosedStream(BlockPos p_218281_0_, BlockPos p_218281_1_) {
      return betweenClosedStream(Math.min(p_218281_0_.getX(), p_218281_1_.getX()), Math.min(p_218281_0_.getY(), p_218281_1_.getY()), Math.min(p_218281_0_.getZ(), p_218281_1_.getZ()), Math.max(p_218281_0_.getX(), p_218281_1_.getX()), Math.max(p_218281_0_.getY(), p_218281_1_.getY()), Math.max(p_218281_0_.getZ(), p_218281_1_.getZ()));
   }

   public static Stream<BlockPos> betweenClosedStream(MutableBoundingBox p_229383_0_) {
      return betweenClosedStream(Math.min(p_229383_0_.x0, p_229383_0_.x1), Math.min(p_229383_0_.y0, p_229383_0_.y1), Math.min(p_229383_0_.z0, p_229383_0_.z1), Math.max(p_229383_0_.x0, p_229383_0_.x1), Math.max(p_229383_0_.y0, p_229383_0_.y1), Math.max(p_229383_0_.z0, p_229383_0_.z1));
   }

   public static Stream<BlockPos> betweenClosedStream(final int p_218287_0_, final int p_218287_1_, final int p_218287_2_, final int p_218287_3_, final int p_218287_4_, final int p_218287_5_) {
      return StreamSupport.stream(new AbstractSpliterator<BlockPos>((long)((p_218287_3_ - p_218287_0_ + 1) * (p_218287_4_ - p_218287_1_ + 1) * (p_218287_5_ - p_218287_2_ + 1)), 64) {
         final CubeCoordinateIterator field_218296_a = new CubeCoordinateIterator(p_218287_0_, p_218287_1_, p_218287_2_, p_218287_3_, p_218287_4_, p_218287_5_);
         final BlockPos.Mutable nextPos = new BlockPos.Mutable();

         public boolean tryAdvance(Consumer<? super BlockPos> p_tryAdvance_1_) {
            if (this.field_218296_a.advance()) {
               p_tryAdvance_1_.accept(this.nextPos.set(this.field_218296_a.nextX(), this.field_218296_a.nextY(), this.field_218296_a.nextZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }

   public static Iterable<BlockPos> betweenClosed(int p_191531_0_, int p_191531_1_, int p_191531_2_, int p_191531_3_, int p_191531_4_, int p_191531_5_) {
      return () -> {
         return new AbstractIterator<BlockPos>() {
            final CubeCoordinateIterator cursor = new CubeCoordinateIterator(p_191531_0_, p_191531_1_, p_191531_2_, p_191531_3_, p_191531_4_, p_191531_5_);
            final BlockPos.Mutable field_218299_b = new BlockPos.Mutable();

            protected BlockPos computeNext() {
               return (BlockPos)(this.cursor.advance() ? this.field_218299_b.set(this.cursor.nextX(), this.cursor.nextY(), this.cursor.nextZ()) : this.endOfData());
            }
         };
      };
   }

   public static class Mutable extends BlockPos {
      protected int field_177997_b;
      protected int field_177998_c;
      protected int field_177996_d;

      public Mutable() {
         this(0, 0, 0);
      }

      public Mutable(BlockPos p_i46587_1_) {
         this(p_i46587_1_.getX(), p_i46587_1_.getY(), p_i46587_1_.getZ());
      }

      public Mutable(int p_i46024_1_, int p_i46024_2_, int p_i46024_3_) {
         super(0, 0, 0);
         this.field_177997_b = p_i46024_1_;
         this.field_177998_c = p_i46024_2_;
         this.field_177996_d = p_i46024_3_;
      }

      public Mutable(double p_i50824_1_, double p_i50824_3_, double p_i50824_5_) {
         this(MathHelper.floor(p_i50824_1_), MathHelper.floor(p_i50824_3_), MathHelper.floor(p_i50824_5_));
      }

      public Mutable(Entity p_i226062_1_) {
         this(p_i226062_1_.getX(), p_i226062_1_.getY(), p_i226062_1_.getZ());
      }

      public BlockPos offset(double p_177963_1_, double p_177963_3_, double p_177963_5_) {
         return super.offset(p_177963_1_, p_177963_3_, p_177963_5_).immutable();
      }

      public BlockPos offset(int p_177982_1_, int p_177982_2_, int p_177982_3_) {
         return super.offset(p_177982_1_, p_177982_2_, p_177982_3_).immutable();
      }

      public BlockPos relative(Direction p_177967_1_, int p_177967_2_) {
         return super.relative(p_177967_1_, p_177967_2_).immutable();
      }

      public BlockPos rotate(Rotation p_190942_1_) {
         return super.rotate(p_190942_1_).immutable();
      }

      public int getX() {
         return this.field_177997_b;
      }

      public int getY() {
         return this.field_177998_c;
      }

      public int getZ() {
         return this.field_177996_d;
      }

      public BlockPos.Mutable set(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         this.field_177997_b = p_181079_1_;
         this.field_177998_c = p_181079_2_;
         this.field_177996_d = p_181079_3_;
         return this;
      }

      public BlockPos.Mutable func_189535_a(Entity p_189535_1_) {
         return this.set(p_189535_1_.getX(), p_189535_1_.getY(), p_189535_1_.getZ());
      }

      public BlockPos.Mutable set(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return this.set(MathHelper.floor(p_189532_1_), MathHelper.floor(p_189532_3_), MathHelper.floor(p_189532_5_));
      }

      public BlockPos.Mutable set(Vec3i p_189533_1_) {
         return this.set(p_189533_1_.getX(), p_189533_1_.getY(), p_189533_1_.getZ());
      }

      public BlockPos.Mutable set(long p_218294_1_) {
         return this.set(getX(p_218294_1_), getY(p_218294_1_), getZ(p_218294_1_));
      }

      public BlockPos.Mutable set(AxisRotation p_218295_1_, int p_218295_2_, int p_218295_3_, int p_218295_4_) {
         return this.set(p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.X), p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Y), p_218295_1_.cycle(p_218295_2_, p_218295_3_, p_218295_4_, Direction.Axis.Z));
      }

      public BlockPos.Mutable move(Direction p_189536_1_) {
         return this.move(p_189536_1_, 1);
      }

      public BlockPos.Mutable move(Direction p_189534_1_, int p_189534_2_) {
         return this.set(this.field_177997_b + p_189534_1_.getStepX() * p_189534_2_, this.field_177998_c + p_189534_1_.getStepY() * p_189534_2_, this.field_177996_d + p_189534_1_.getStepZ() * p_189534_2_);
      }

      public BlockPos.Mutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return this.set(this.field_177997_b + p_196234_1_, this.field_177998_c + p_196234_2_, this.field_177996_d + p_196234_3_);
      }

      public void setX(int p_223471_1_) {
         this.field_177997_b = p_223471_1_;
      }

      public void setY(int p_185336_1_) {
         this.field_177998_c = p_185336_1_;
      }

      public void setZ(int p_223472_1_) {
         this.field_177996_d = p_223472_1_;
      }

      public BlockPos immutable() {
         return new BlockPos(this);
      }
   }

   public static final class PooledMutable extends BlockPos.Mutable implements AutoCloseable {
      private boolean field_185350_f;
      private static final List<BlockPos.PooledMutable> field_185351_g = Lists.newArrayList();

      private PooledMutable(int p_i46586_1_, int p_i46586_2_, int p_i46586_3_) {
         super(p_i46586_1_, p_i46586_2_, p_i46586_3_);
      }

      public static BlockPos.PooledMutable func_185346_s() {
         return func_185339_c(0, 0, 0);
      }

      public static BlockPos.PooledMutable func_209907_b(Entity p_209907_0_) {
         return func_185345_c(p_209907_0_.getX(), p_209907_0_.getY(), p_209907_0_.getZ());
      }

      public static BlockPos.PooledMutable func_185345_c(double p_185345_0_, double p_185345_2_, double p_185345_4_) {
         return func_185339_c(MathHelper.floor(p_185345_0_), MathHelper.floor(p_185345_2_), MathHelper.floor(p_185345_4_));
      }

      public static BlockPos.PooledMutable func_185339_c(int p_185339_0_, int p_185339_1_, int p_185339_2_) {
         synchronized(field_185351_g) {
            if (!field_185351_g.isEmpty()) {
               BlockPos.PooledMutable blockpos$pooledmutable = field_185351_g.remove(field_185351_g.size() - 1);
               if (blockpos$pooledmutable != null && blockpos$pooledmutable.field_185350_f) {
                  blockpos$pooledmutable.field_185350_f = false;
                  blockpos$pooledmutable.set(p_185339_0_, p_185339_1_, p_185339_2_);
                  return blockpos$pooledmutable;
               }
            }
         }

         return new BlockPos.PooledMutable(p_185339_0_, p_185339_1_, p_185339_2_);
      }

      public BlockPos.PooledMutable set(int p_181079_1_, int p_181079_2_, int p_181079_3_) {
         return (BlockPos.PooledMutable)super.set(p_181079_1_, p_181079_2_, p_181079_3_);
      }

      public BlockPos.PooledMutable func_189535_a(Entity p_189535_1_) {
         return (BlockPos.PooledMutable)super.func_189535_a(p_189535_1_);
      }

      public BlockPos.PooledMutable set(double p_189532_1_, double p_189532_3_, double p_189532_5_) {
         return (BlockPos.PooledMutable)super.set(p_189532_1_, p_189532_3_, p_189532_5_);
      }

      public BlockPos.PooledMutable set(Vec3i p_189533_1_) {
         return (BlockPos.PooledMutable)super.set(p_189533_1_);
      }

      public BlockPos.PooledMutable move(Direction p_189536_1_) {
         return (BlockPos.PooledMutable)super.move(p_189536_1_);
      }

      public BlockPos.PooledMutable move(Direction p_189534_1_, int p_189534_2_) {
         return (BlockPos.PooledMutable)super.move(p_189534_1_, p_189534_2_);
      }

      public BlockPos.PooledMutable move(int p_196234_1_, int p_196234_2_, int p_196234_3_) {
         return (BlockPos.PooledMutable)super.move(p_196234_1_, p_196234_2_, p_196234_3_);
      }

      public void close() {
         synchronized(field_185351_g) {
            if (field_185351_g.size() < 100) {
               field_185351_g.add(this);
            }

            this.field_185350_f = true;
         }
      }
   }
}