package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.Hand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CEditBookPacket implements IPacket<IServerPlayNetHandler> {
   private ItemStack book;
   private boolean signing;
   private Hand field_212645_c;

   public CEditBookPacket() {
   }

   @OnlyIn(Dist.CLIENT)
   public CEditBookPacket(ItemStack p_i49823_1_, boolean p_i49823_2_, Hand p_i49823_3_) {
      this.book = p_i49823_1_.copy();
      this.signing = p_i49823_2_;
      this.field_212645_c = p_i49823_3_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.book = p_148837_1_.readItem();
      this.signing = p_148837_1_.readBoolean();
      this.field_212645_c = p_148837_1_.readEnum(Hand.class);
   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeItem(this.book);
      p_148840_1_.writeBoolean(this.signing);
      p_148840_1_.writeEnum(this.field_212645_c);
   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleEditBook(this);
   }

   public ItemStack getBook() {
      return this.book;
   }

   public boolean isSigning() {
      return this.signing;
   }

   public Hand func_212644_d() {
      return this.field_212645_c;
   }
}