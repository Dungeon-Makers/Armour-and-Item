package net.minecraft.util;

import net.minecraft.nbt.CompoundNBT;

public class WeightedSpawnerEntity extends WeightedRandom.Item {
   private final CompoundNBT tag;

   public WeightedSpawnerEntity() {
      super(1);
      this.tag = new CompoundNBT();
      this.tag.putString("id", "minecraft:pig");
   }

   public WeightedSpawnerEntity(CompoundNBT p_i46715_1_) {
      this(p_i46715_1_.contains("Weight", 99) ? p_i46715_1_.getInt("Weight") : 1, p_i46715_1_.getCompound("Entity"));
   }

   public WeightedSpawnerEntity(int p_i46716_1_, CompoundNBT p_i46716_2_) {
      super(p_i46716_1_);
      this.tag = p_i46716_2_;
   }

   public CompoundNBT save() {
      CompoundNBT compoundnbt = new CompoundNBT();
      if (!this.tag.contains("id", 8)) {
         this.tag.putString("id", "minecraft:pig");
      } else if (!this.tag.getString("id").contains(":")) {
         this.tag.putString("id", (new ResourceLocation(this.tag.getString("id"))).toString());
      }

      compoundnbt.put("Entity", this.tag);
      compoundnbt.putInt("Weight", this.weight);
      return compoundnbt;
   }

   public CompoundNBT getTag() {
      return this.tag;
   }
}