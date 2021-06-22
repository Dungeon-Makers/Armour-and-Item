package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class JigsawTileEntity extends TileEntity {
   private ResourceLocation field_214059_a = new ResourceLocation("empty");
   private ResourceLocation field_214060_b = new ResourceLocation("empty");
   private String finalState = "minecraft:air";

   public JigsawTileEntity(TileEntityType<?> p_i49960_1_) {
      super(p_i49960_1_);
   }

   public JigsawTileEntity() {
      this(TileEntityType.JIGSAW);
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_214053_c() {
      return this.field_214059_a;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation func_214056_d() {
      return this.field_214060_b;
   }

   @OnlyIn(Dist.CLIENT)
   public String getFinalState() {
      return this.finalState;
   }

   public void func_214057_a(ResourceLocation p_214057_1_) {
      this.field_214059_a = p_214057_1_;
   }

   public void func_214058_b(ResourceLocation p_214058_1_) {
      this.field_214060_b = p_214058_1_;
   }

   public void setFinalState(String p_214055_1_) {
      this.finalState = p_214055_1_;
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putString("attachement_type", this.field_214059_a.toString());
      p_189515_1_.putString("target_pool", this.field_214060_b.toString());
      p_189515_1_.putString("final_state", this.finalState);
      return p_189515_1_;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      this.field_214059_a = new ResourceLocation(p_145839_1_.getString("attachement_type"));
      this.field_214060_b = new ResourceLocation(p_145839_1_.getString("target_pool"));
      this.finalState = p_145839_1_.getString("final_state");
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 12, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }
}