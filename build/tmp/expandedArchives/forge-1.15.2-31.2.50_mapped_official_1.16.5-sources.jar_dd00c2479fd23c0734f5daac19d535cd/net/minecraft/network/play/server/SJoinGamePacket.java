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

public class SJoinGamePacket implements IPacket<IClientPlayNetHandler> {
   private int playerId;
   private long seed;
   private boolean hardcore;
   private GameType gameType;
   private DimensionType dimension;
   private int maxPlayers;
   private WorldType field_149201_g;
   private int chunkRadius;
   private boolean reducedDebugInfo;
   private boolean showDeathScreen;
   private int dimensionInt;

   public SJoinGamePacket() {
   }

   public SJoinGamePacket(int p_i226090_1_, GameType p_i226090_2_, long p_i226090_3_, boolean p_i226090_5_, DimensionType p_i226090_6_, int p_i226090_7_, WorldType p_i226090_8_, int p_i226090_9_, boolean p_i226090_10_, boolean p_i226090_11_) {
      this.playerId = p_i226090_1_;
      this.dimension = p_i226090_6_;
      this.seed = p_i226090_3_;
      this.gameType = p_i226090_2_;
      this.maxPlayers = p_i226090_7_;
      this.hardcore = p_i226090_5_;
      this.field_149201_g = p_i226090_8_;
      this.chunkRadius = p_i226090_9_;
      this.reducedDebugInfo = p_i226090_10_;
      this.showDeathScreen = p_i226090_11_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.playerId = p_148837_1_.readInt();
      int i = p_148837_1_.readUnsignedByte();
      this.hardcore = (i & 8) == 8;
      i = i & -9;
      this.gameType = GameType.byId(i);
      this.dimensionInt = p_148837_1_.readInt();
      this.seed = p_148837_1_.readLong();
      this.maxPlayers = p_148837_1_.readUnsignedByte();
      this.field_149201_g = WorldType.func_77130_a(p_148837_1_.readUtf(16));
      if (this.field_149201_g == null) {
         this.field_149201_g = WorldType.field_77137_b;
      }

      this.chunkRadius = p_148837_1_.readVarInt();
      this.reducedDebugInfo = p_148837_1_.readBoolean();
      this.showDeathScreen = p_148837_1_.readBoolean();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeInt(this.playerId);
      int i = this.gameType.getId();
      if (this.hardcore) {
         i |= 8;
      }

      p_148840_1_.writeByte(i);
      p_148840_1_.writeInt(this.dimension.func_186068_a());
      p_148840_1_.writeLong(this.seed);
      p_148840_1_.writeByte(this.maxPlayers);
      p_148840_1_.writeUtf(this.field_149201_g.func_211888_a());
      p_148840_1_.writeVarInt(this.chunkRadius);
      p_148840_1_.writeBoolean(this.reducedDebugInfo);
      p_148840_1_.writeBoolean(this.showDeathScreen);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleLogin(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int getPlayerId() {
      return this.playerId;
   }

   @OnlyIn(Dist.CLIENT)
   public long getSeed() {
      return this.seed;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isHardcore() {
      return this.hardcore;
   }

   @OnlyIn(Dist.CLIENT)
   public GameType getGameType() {
      return this.gameType;
   }

   @OnlyIn(Dist.CLIENT)
   public DimensionType func_212642_e() {
      return this.dimension == null ? this.dimension = net.minecraftforge.fml.network.NetworkHooks.getDummyDimType(this.dimensionInt) : this.dimension;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldType func_149196_i() {
      return this.field_149201_g;
   }

   @OnlyIn(Dist.CLIENT)
   public int getChunkRadius() {
      return this.chunkRadius;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isReducedDebugInfo() {
      return this.reducedDebugInfo;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowDeathScreen() {
      return this.showDeathScreen;
   }
}
