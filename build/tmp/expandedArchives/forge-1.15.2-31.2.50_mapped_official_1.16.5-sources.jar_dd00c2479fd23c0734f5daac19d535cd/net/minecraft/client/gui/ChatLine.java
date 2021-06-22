package net.minecraft.client.gui;

import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChatLine {
   private final int addedTime;
   private final ITextComponent message;
   private final int id;

   public ChatLine(int p_i45000_1_, ITextComponent p_i45000_2_, int p_i45000_3_) {
      this.message = p_i45000_2_;
      this.addedTime = p_i45000_1_;
      this.id = p_i45000_3_;
   }

   public ITextComponent func_151461_a() {
      return this.message;
   }

   public int getAddedTime() {
      return this.addedTime;
   }

   public int getId() {
      return this.id;
   }
}