package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.NetworkTagManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class STagsListPacket implements IPacket<IClientPlayNetHandler> {
   private NetworkTagManager tags;

   public STagsListPacket() {
   }

   public STagsListPacket(NetworkTagManager p_i48211_1_) {
      this.tags = p_i48211_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.tags = NetworkTagManager.func_199714_b(p_148837_1_);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      this.tags.func_199716_a(p_148840_1_);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleUpdateTags(this);
   }

   @OnlyIn(Dist.CLIENT)
   public NetworkTagManager getTags() {
      return this.tags;
   }
}