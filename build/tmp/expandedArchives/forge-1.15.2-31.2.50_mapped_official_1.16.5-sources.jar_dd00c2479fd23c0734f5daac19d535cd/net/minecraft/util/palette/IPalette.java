package net.minecraft.util.palette;

import javax.annotation.Nullable;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IPalette<T> {
   int idFor(T p_186041_1_);

   boolean func_222626_b(T p_222626_1_);

   @Nullable
   T valueFor(int p_186039_1_);

   @OnlyIn(Dist.CLIENT)
   void read(PacketBuffer p_186038_1_);

   void write(PacketBuffer p_186037_1_);

   int getSerializedSize();

   void read(ListNBT p_196968_1_);
}