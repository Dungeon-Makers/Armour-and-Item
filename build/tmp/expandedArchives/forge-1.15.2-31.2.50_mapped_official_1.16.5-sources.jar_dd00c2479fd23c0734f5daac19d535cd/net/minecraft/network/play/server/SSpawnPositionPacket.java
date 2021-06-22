package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnPositionPacket implements IPacket<IClientPlayNetHandler> {
   private BlockPos field_179801_a;

   public SSpawnPositionPacket() {
   }

   public SSpawnPositionPacket(BlockPos p_i46903_1_) {
      this.field_179801_a = p_i46903_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.field_179801_a = p_148837_1_.readBlockPos();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.field_179801_a);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_147271_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public BlockPos func_179800_a() {
      return this.field_179801_a;
   }
}