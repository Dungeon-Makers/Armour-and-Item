package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.IFluidState;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.palette.IPalette;
import net.minecraft.util.palette.PaletteIdentity;
import net.minecraft.util.palette.PalettedContainer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ChunkSection {
   private static final IPalette<BlockState> GLOBAL_BLOCKSTATE_PALETTE = new PaletteIdentity<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState());
   private final int bottomBlockY;
   private short nonEmptyBlockCount;
   private short tickingBlockCount;
   private short tickingFluidCount;
   private final PalettedContainer<BlockState> states;

   public ChunkSection(int p_i49943_1_) {
      this(p_i49943_1_, (short)0, (short)0, (short)0);
   }

   public ChunkSection(int p_i49944_1_, short p_i49944_2_, short p_i49944_3_, short p_i49944_4_) {
      this.bottomBlockY = p_i49944_1_;
      this.nonEmptyBlockCount = p_i49944_2_;
      this.tickingBlockCount = p_i49944_3_;
      this.tickingFluidCount = p_i49944_4_;
      this.states = new PalettedContainer<>(GLOBAL_BLOCKSTATE_PALETTE, Block.BLOCK_STATE_REGISTRY, NBTUtil::readBlockState, NBTUtil::writeBlockState, Blocks.AIR.defaultBlockState());
   }

   public BlockState getBlockState(int p_177485_1_, int p_177485_2_, int p_177485_3_) {
      return this.states.get(p_177485_1_, p_177485_2_, p_177485_3_);
   }

   public IFluidState getFluidState(int p_206914_1_, int p_206914_2_, int p_206914_3_) {
      return this.states.get(p_206914_1_, p_206914_2_, p_206914_3_).getFluidState();
   }

   public void acquire() {
      this.states.acquire();
   }

   public void release() {
      this.states.release();
   }

   public BlockState setBlockState(int p_222629_1_, int p_222629_2_, int p_222629_3_, BlockState p_222629_4_) {
      return this.setBlockState(p_222629_1_, p_222629_2_, p_222629_3_, p_222629_4_, true);
   }

   public BlockState setBlockState(int p_177484_1_, int p_177484_2_, int p_177484_3_, BlockState p_177484_4_, boolean p_177484_5_) {
      BlockState blockstate;
      if (p_177484_5_) {
         blockstate = this.states.getAndSet(p_177484_1_, p_177484_2_, p_177484_3_, p_177484_4_);
      } else {
         blockstate = this.states.getAndSetUnchecked(p_177484_1_, p_177484_2_, p_177484_3_, p_177484_4_);
      }

      IFluidState ifluidstate = blockstate.getFluidState();
      IFluidState ifluidstate1 = p_177484_4_.getFluidState();
      if (!blockstate.isAir()) {
         --this.nonEmptyBlockCount;
         if (blockstate.isRandomlyTicking()) {
            --this.tickingBlockCount;
         }
      }

      if (!ifluidstate.isEmpty()) {
         --this.tickingFluidCount;
      }

      if (!p_177484_4_.isAir()) {
         ++this.nonEmptyBlockCount;
         if (p_177484_4_.isRandomlyTicking()) {
            ++this.tickingBlockCount;
         }
      }

      if (!ifluidstate1.isEmpty()) {
         ++this.tickingFluidCount;
      }

      return blockstate;
   }

   public boolean isEmpty() {
      return this.nonEmptyBlockCount == 0;
   }

   public static boolean isEmpty(@Nullable ChunkSection p_222628_0_) {
      return p_222628_0_ == Chunk.EMPTY_SECTION || p_222628_0_.isEmpty();
   }

   public boolean isRandomlyTicking() {
      return this.isRandomlyTickingBlocks() || this.isRandomlyTickingFluids();
   }

   public boolean isRandomlyTickingBlocks() {
      return this.tickingBlockCount > 0;
   }

   public boolean isRandomlyTickingFluids() {
      return this.tickingFluidCount > 0;
   }

   public int bottomBlockY() {
      return this.bottomBlockY;
   }

   public void recalcBlockCounts() {
      this.nonEmptyBlockCount = 0;
      this.tickingBlockCount = 0;
      this.tickingFluidCount = 0;
      this.states.count((p_225496_1_, p_225496_2_) -> {
         IFluidState ifluidstate = p_225496_1_.getFluidState();
         if (!p_225496_1_.isAir()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + p_225496_2_);
            if (p_225496_1_.isRandomlyTicking()) {
               this.tickingBlockCount = (short)(this.tickingBlockCount + p_225496_2_);
            }
         }

         if (!ifluidstate.isEmpty()) {
            this.nonEmptyBlockCount = (short)(this.nonEmptyBlockCount + p_225496_2_);
            if (ifluidstate.isRandomlyTicking()) {
               this.tickingFluidCount = (short)(this.tickingFluidCount + p_225496_2_);
            }
         }

      });
   }

   public PalettedContainer<BlockState> getStates() {
      return this.states;
   }

   @OnlyIn(Dist.CLIENT)
   public void read(PacketBuffer p_222634_1_) {
      this.nonEmptyBlockCount = p_222634_1_.readShort();
      this.states.read(p_222634_1_);
   }

   public void write(PacketBuffer p_222630_1_) {
      p_222630_1_.writeShort(this.nonEmptyBlockCount);
      this.states.write(p_222630_1_);
   }

   public int getSerializedSize() {
      return 2 + this.states.getSerializedSize();
   }

   public boolean func_222636_a(BlockState p_222636_1_) {
      return this.states.func_222640_a(p_222636_1_);
   }
}