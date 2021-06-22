package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.IServerPlayNetHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CRecipeInfoPacket implements IPacket<IServerPlayNetHandler> {
   private CRecipeInfoPacket.Purpose field_194157_a;
   private ResourceLocation field_193649_d;
   private boolean field_192631_e;
   private boolean field_192632_f;
   private boolean field_202498_e;
   private boolean field_202499_f;
   private boolean field_218782_g;
   private boolean field_218783_h;
   private boolean field_218784_i;
   private boolean field_218785_j;

   public CRecipeInfoPacket() {
   }

   public CRecipeInfoPacket(IRecipe<?> p_i47518_1_) {
      this.field_194157_a = CRecipeInfoPacket.Purpose.SHOWN;
      this.field_193649_d = p_i47518_1_.getId();
   }

   @OnlyIn(Dist.CLIENT)
   public CRecipeInfoPacket(boolean p_i50758_1_, boolean p_i50758_2_, boolean p_i50758_3_, boolean p_i50758_4_, boolean p_i50758_5_, boolean p_i50758_6_) {
      this.field_194157_a = CRecipeInfoPacket.Purpose.SETTINGS;
      this.field_192631_e = p_i50758_1_;
      this.field_192632_f = p_i50758_2_;
      this.field_202498_e = p_i50758_3_;
      this.field_202499_f = p_i50758_4_;
      this.field_218782_g = p_i50758_5_;
      this.field_218783_h = p_i50758_6_;
      this.field_218784_i = p_i50758_5_;
      this.field_218785_j = p_i50758_6_;
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.field_194157_a = p_148837_1_.readEnum(CRecipeInfoPacket.Purpose.class);
      if (this.field_194157_a == CRecipeInfoPacket.Purpose.SHOWN) {
         this.field_193649_d = p_148837_1_.readResourceLocation();
      } else if (this.field_194157_a == CRecipeInfoPacket.Purpose.SETTINGS) {
         this.field_192631_e = p_148837_1_.readBoolean();
         this.field_192632_f = p_148837_1_.readBoolean();
         this.field_202498_e = p_148837_1_.readBoolean();
         this.field_202499_f = p_148837_1_.readBoolean();
         this.field_218782_g = p_148837_1_.readBoolean();
         this.field_218783_h = p_148837_1_.readBoolean();
         this.field_218784_i = p_148837_1_.readBoolean();
         this.field_218785_j = p_148837_1_.readBoolean();
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.field_194157_a);
      if (this.field_194157_a == CRecipeInfoPacket.Purpose.SHOWN) {
         p_148840_1_.writeResourceLocation(this.field_193649_d);
      } else if (this.field_194157_a == CRecipeInfoPacket.Purpose.SETTINGS) {
         p_148840_1_.writeBoolean(this.field_192631_e);
         p_148840_1_.writeBoolean(this.field_192632_f);
         p_148840_1_.writeBoolean(this.field_202498_e);
         p_148840_1_.writeBoolean(this.field_202499_f);
         p_148840_1_.writeBoolean(this.field_218782_g);
         p_148840_1_.writeBoolean(this.field_218783_h);
         p_148840_1_.writeBoolean(this.field_218784_i);
         p_148840_1_.writeBoolean(this.field_218785_j);
      }

   }

   public void handle(IServerPlayNetHandler p_148833_1_) {
      p_148833_1_.handleRecipeBookSeenRecipePacket(this);
   }

   public CRecipeInfoPacket.Purpose func_194156_a() {
      return this.field_194157_a;
   }

   public ResourceLocation func_199619_b() {
      return this.field_193649_d;
   }

   public boolean func_192624_c() {
      return this.field_192631_e;
   }

   public boolean func_192625_d() {
      return this.field_192632_f;
   }

   public boolean func_202496_e() {
      return this.field_202498_e;
   }

   public boolean func_202497_f() {
      return this.field_202499_f;
   }

   public boolean func_218779_h() {
      return this.field_218782_g;
   }

   public boolean func_218778_i() {
      return this.field_218783_h;
   }

   public boolean func_218780_j() {
      return this.field_218784_i;
   }

   public boolean func_218781_k() {
      return this.field_218785_j;
   }

   public static enum Purpose {
      SHOWN,
      SETTINGS;
   }
}