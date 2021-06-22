package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SSpawnGlobalEntityPacket implements IPacket<IClientPlayNetHandler> {
   private int field_149059_a;
   private double field_149057_b;
   private double field_149058_c;
   private double field_149055_d;
   private int field_149056_e;

   public SSpawnGlobalEntityPacket() {
   }

   public SSpawnGlobalEntityPacket(Entity p_i46974_1_) {
      this.field_149059_a = p_i46974_1_.getId();
      this.field_149057_b = p_i46974_1_.getX();
      this.field_149058_c = p_i46974_1_.getY();
      this.field_149055_d = p_i46974_1_.getZ();
      if (p_i46974_1_ instanceof LightningBoltEntity) {
         this.field_149056_e = 1;
      }

   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.field_149059_a = p_148837_1_.readVarInt();
      this.field_149056_e = p_148837_1_.readByte();
      this.field_149057_b = p_148837_1_.readDouble();
      this.field_149058_c = p_148837_1_.readDouble();
      this.field_149055_d = p_148837_1_.readDouble();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.field_149059_a);
      p_148840_1_.writeByte(this.field_149056_e);
      p_148840_1_.writeDouble(this.field_149057_b);
      p_148840_1_.writeDouble(this.field_149058_c);
      p_148840_1_.writeDouble(this.field_149055_d);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.func_147292_a(this);
   }

   @OnlyIn(Dist.CLIENT)
   public int func_149052_c() {
      return this.field_149059_a;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_186888_b() {
      return this.field_149057_b;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_186889_c() {
      return this.field_149058_c;
   }

   @OnlyIn(Dist.CLIENT)
   public double func_186887_d() {
      return this.field_149055_d;
   }

   @OnlyIn(Dist.CLIENT)
   public int func_149053_g() {
      return this.field_149056_e;
   }
}