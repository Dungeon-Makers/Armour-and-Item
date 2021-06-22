package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SEntityEquipmentPacket implements IPacket<IClientPlayNetHandler> {
   private int entity;
   private EquipmentSlotType field_149392_b;
   private ItemStack field_149393_c = ItemStack.EMPTY;

   public SEntityEquipmentPacket() {
   }

   public SEntityEquipmentPacket(int p_i46913_1_, EquipmentSlotType p_i46913_2_, ItemStack p_i46913_3_) {
      this.entity = p_i46913_1_;
      this.field_149392_b = p_i46913_2_;
      this.field_149393_c = p_i46913_3_.copy();
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.entity = p_148837_1_.readVarInt();
      this.field_149392_b = p_148837_1_.readEnum(EquipmentSlotType.class);
      this.field_149393_c = p_148837_1_.readItem();
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeVarInt(this.entity);
      p_148840_1_.writeEnum(this.field_149392_b);
      p_148840_1_.writeItem(this.field_149393_c);
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleSetEquipment(this);
   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack func_149390_c() {
      return this.field_149393_c;
   }

   @OnlyIn(Dist.CLIENT)
   public int getEntity() {
      return this.entity;
   }

   @OnlyIn(Dist.CLIENT)
   public EquipmentSlotType func_186969_c() {
      return this.field_149392_b;
   }
}