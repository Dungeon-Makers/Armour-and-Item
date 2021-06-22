package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CUpdateJigsawBlockPacket implements IPacket<IServerPlayNetHandler> {
   private BlockPos pos;
   private ResourceLocation field_218791_b;
   private ResourceLocation field_218792_c;
   private String finalState;

   public CUpdateJigsawBlockPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CUpdateJigsawBlockPacket(BlockPos p_i50757_1_, ResourceLocation p_i50757_2_, ResourceLocation p_i50757_3_, String p_i50757_4_) {
      this.pos = p_i50757_1_;
      this.field_218791_b = p_i50757_2_;
      this.field_218792_c = p_i50757_3_;
      this.finalState = p_i50757_4_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.pos = p_148837_1_.readBlockPos();
      this.field_218791_b = p_148837_1_.readResourceLocation();
      this.field_218792_c = p_148837_1_.readResourceLocation();
      this.finalState = p_148837_1_.readUtf(32767);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeBlockPos(this.pos);
      p_148840_1_.writeResourceLocation(this.field_218791_b);
      p_148840_1_.writeResourceLocation(this.field_218792_c);
      p_148840_1_.writeUtf(this.finalState);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetJigsawBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public ResourceLocation func_218786_c() {
      return this.field_218792_c;
   }

   public ResourceLocation func_218787_d() {
      return this.field_218791_b;
   }

   public String getFinalState() {
      return this.finalState;
   }
}