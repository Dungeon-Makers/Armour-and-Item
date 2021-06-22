package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SChangeGameStatePacket implements IPacket<IClientPlayNetHandler> {
   public static final String[] field_149142_a = new String[]{"block.minecraft.bed.not_valid"};
   private int event;
   private float param;

   public SChangeGameStatePacket() {
   }

   public SChangeGameStatePacket(int p_i46943_1_, float p_i46943_2_) {
      this.event = p_i46943_1_;
      this.param = p_i46943_2_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.event = p_148837_1_.readUnsignedByte();
      this.param = p_148837_1_.readFloat();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeByte(this.event);
      p_148840_1_.writeFloat(this.param);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleGameEvent(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_149138_c() {
      return this.event;
   }

   @OnlyIn(Dist.CLIENT)
   public float getParam() {
      return this.param;
   }
}