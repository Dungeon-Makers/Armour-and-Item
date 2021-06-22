package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SMultiBlockChangePacket implements IPacket<IClientPlayNetHandler> {
   private ChunkPos field_148925_b;
   private SMultiBlockChangePacket.UpdateData[] field_179845_b;

   public SMultiBlockChangePacket() {
   }

   public SMultiBlockChangePacket(int p_i46959_1_, short[] p_i46959_2_, Chunk p_i46959_3_) {
      this.field_148925_b = p_i46959_3_.getPos();
      this.field_179845_b = new SMultiBlockChangePacket.UpdateData[p_i46959_1_];

      for(int i = 0; i < this.field_179845_b.length; ++i) {
         this.field_179845_b[i] = new SMultiBlockChangePacket.UpdateData(p_i46959_2_[i], p_i46959_3_);
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.field_148925_b = new ChunkPos(p_148837_1_.readInt(), p_148837_1_.readInt());
      this.field_179845_b = new SMultiBlockChangePacket.UpdateData[p_148837_1_.readVarInt()];

      for(int i = 0; i < this.field_179845_b.length; ++i) {
         this.field_179845_b[i] = new SMultiBlockChangePacket.UpdateData(p_148837_1_.readShort(), Block.BLOCK_STATE_REGISTRY.byId(p_148837_1_.readVarInt()));
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.field_148925_b.x);
      p_148840_1_.writeInt(this.field_148925_b.z);
      p_148840_1_.writeVarInt(this.field_179845_b.length);

      for(SMultiBlockChangePacket.UpdateData smultiblockchangepacket$updatedata : this.field_179845_b) {
         p_148840_1_.writeShort(smultiblockchangepacket$updatedata.func_180089_b());
         p_148840_1_.writeVarInt(Block.getId(smultiblockchangepacket$updatedata.func_180088_c()));
      }

   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleChunkBlocksUpdate(this);
   }

   @OnlyIn(Dist.CLIENT)
   public SMultiBlockChangePacket.UpdateData[] func_179844_a() {
      return this.field_179845_b;
   }

   public class UpdateData {
      private final short field_180091_b;
      private final BlockState field_180092_c;

      public UpdateData(short p_i46544_2_, BlockState p_i46544_3_) {
         this.field_180091_b = p_i46544_2_;
         this.field_180092_c = p_i46544_3_;
      }

      public UpdateData(short p_i46545_2_, Chunk p_i46545_3_) {
         this.field_180091_b = p_i46545_2_;
         this.field_180092_c = p_i46545_3_.getBlockState(this.func_180090_a());
      }

      public BlockPos func_180090_a() {
         return new BlockPos(SMultiBlockChangePacket.this.field_148925_b.getBlockAt(this.field_180091_b >> 12 & 15, this.field_180091_b & 255, this.field_180091_b >> 8 & 15));
      }

      public short func_180089_b() {
         return this.field_180091_b;
      }

      public BlockState func_180088_c() {
         return this.field_180092_c;
      }
   }
}