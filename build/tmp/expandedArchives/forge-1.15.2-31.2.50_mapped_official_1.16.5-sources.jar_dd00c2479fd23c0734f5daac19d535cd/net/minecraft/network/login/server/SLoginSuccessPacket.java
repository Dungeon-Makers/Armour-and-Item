package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.client.network.login.IClientLoginNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SLoginSuccessPacket implements IPacket<IClientLoginNetHandler> {
   private GameProfile gameProfile;

   public SLoginSuccessPacket() {
   }

   public SLoginSuccessPacket(GameProfile p_i46856_1_) {
      this.gameProfile = p_i46856_1_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      String s = p_148837_1_.readUtf(36);
      String s1 = p_148837_1_.readUtf(16);
      UUID uuid = s.length() > 0 ? UUID.fromString(s) : null; // Forge: prevent exception with bad data.
      this.gameProfile = new GameProfile(uuid, s1);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      UUID uuid = this.gameProfile.getId();
      p_148840_1_.writeUtf(uuid == null ? "" : uuid.toString());
      p_148840_1_.writeUtf(this.gameProfile.getName());
   }

   public void handle(IClientLoginNetHandler p_148833_1_) {
      p_148833_1_.handleGameProfile(this);
   }

   @OnlyIn(Dist.CLIENT)
   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
