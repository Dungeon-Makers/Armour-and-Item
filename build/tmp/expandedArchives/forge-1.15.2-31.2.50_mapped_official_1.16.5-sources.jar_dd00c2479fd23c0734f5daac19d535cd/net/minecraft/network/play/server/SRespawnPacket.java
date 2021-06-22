package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRespawnPacket implements IPacket<IClientPlayNetHandler> {
   private DimensionType dimension;
   private long seed;
   private GameType playerGameType;
   private WorldType field_149085_d;
   private int dimensionInt;

   public SRespawnPacket() {
   }

   public SRespawnPacket(DimensionType p_i226091_1_, long p_i226091_2_, WorldType p_i226091_4_, GameType p_i226091_5_) {
      this.dimension = p_i226091_1_;
      this.seed = p_i226091_2_;
      this.playerGameType = p_i226091_5_;
      this.field_149085_d = p_i226091_4_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRespawn(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.dimensionInt = p_148837_1_.readInt();
      this.seed = p_148837_1_.readLong();
      this.playerGameType = GameType.byId(p_148837_1_.readUnsignedByte());
      this.field_149085_d = WorldType.func_77130_a(p_148837_1_.readUtf(16));
      if (this.field_149085_d == null) {
         this.field_149085_d = WorldType.field_77137_b;
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.dimension.func_186068_a());
      p_148840_1_.writeLong(this.seed);
      p_148840_1_.writeByte(this.playerGameType.getId());
      p_148840_1_.writeUtf(this.field_149085_d.func_211888_a());
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType func_212643_b() {
      return this.dimension == null ? this.dimension = net.minecraftforge.fml.network.NetworkHooks.getDummyDimType(this.dimensionInt) : this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getPlayerGameType() {
      return this.playerGameType;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType func_149080_f() {
      return this.field_149085_d;
   }
}
