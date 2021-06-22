package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SPlayerAbilitiesPacket implements IPacket<IClientPlayNetHandler> {
   private boolean invulnerable;
   private boolean isFlying;
   private boolean canFly;
   private boolean instabuild;
   private float flyingSpeed;
   private float walkingSpeed;

   public SPlayerAbilitiesPacket() {
   }

   public SPlayerAbilitiesPacket(PlayerAbilities p_i46933_1_) {
      this.func_149108_a(p_i46933_1_.invulnerable);
      this.func_149102_b(p_i46933_1_.flying);
      this.func_149109_c(p_i46933_1_.mayfly);
      this.func_149111_d(p_i46933_1_.instabuild);
      this.func_149104_a(p_i46933_1_.getFlyingSpeed());
      this.func_149110_b(p_i46933_1_.getWalkingSpeed());
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      byte b0 = p_148837_1_.readByte();
      this.func_149108_a((b0 & 1) > 0);
      this.func_149102_b((b0 & 2) > 0);
      this.func_149109_c((b0 & 4) > 0);
      this.func_149111_d((b0 & 8) > 0);
      this.func_149104_a(p_148837_1_.readFloat());
      this.func_149110_b(p_148837_1_.readFloat());
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      byte b0 = 0;
      if (this.isInvulnerable()) {
         b0 = (byte)(b0 | 1);
      }

      if (this.isFlying()) {
         b0 = (byte)(b0 | 2);
      }

      if (this.canFly()) {
         b0 = (byte)(b0 | 4);
      }

      if (this.canInstabuild()) {
         b0 = (byte)(b0 | 8);
      }

      p_148840_1_.writeByte(b0);
      p_148840_1_.writeFloat(this.flyingSpeed);
      p_148840_1_.writeFloat(this.walkingSpeed);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handlePlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void func_149108_a(boolean p_149108_1_) {
      this.invulnerable = p_149108_1_;
   }

   public boolean isFlying() {
      return this.isFlying;
   }

   public void func_149102_b(boolean p_149102_1_) {
      this.isFlying = p_149102_1_;
   }

   public boolean canFly() {
      return this.canFly;
   }

   public void func_149109_c(boolean p_149109_1_) {
      this.canFly = p_149109_1_;
   }

   public boolean canInstabuild() {
      return this.instabuild;
   }

   public void func_149111_d(boolean p_149111_1_) {
      this.instabuild = p_149111_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getFlyingSpeed() {
      return this.flyingSpeed;
   }

   public void func_149104_a(float p_149104_1_) {
      this.flyingSpeed = p_149104_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public float getWalkingSpeed() {
      return this.walkingSpeed;
   }

   public void func_149110_b(float p_149110_1_) {
      this.walkingSpeed = p_149110_1_;
   }
}