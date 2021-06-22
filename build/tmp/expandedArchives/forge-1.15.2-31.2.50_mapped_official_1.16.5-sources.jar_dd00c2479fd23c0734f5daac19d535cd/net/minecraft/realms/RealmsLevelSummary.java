package net.minecraft.realms;

import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RealmsLevelSummary implements Comparable<RealmsLevelSummary> {
   private final WorldSummary levelSummary;

   public RealmsLevelSummary(WorldSummary p_i1109_1_) {
      this.levelSummary = p_i1109_1_;
   }

   public int getGameMode() {
      return this.levelSummary.getGameMode().getId();
   }

   public String getLevelId() {
      return this.levelSummary.getLevelId();
   }

   public boolean hasCheats() {
      return this.levelSummary.hasCheats();
   }

   public boolean isHardcore() {
      return this.levelSummary.isHardcore();
   }

   public boolean isRequiresConversion() {
      return this.levelSummary.isRequiresConversion();
   }

   public String getLevelName() {
      return this.levelSummary.getLevelName();
   }

   public long getLastPlayed() {
      return this.levelSummary.getLastPlayed();
   }

   public int compareTo(WorldSummary p_compareTo_1_) {
      return this.levelSummary.compareTo(p_compareTo_1_);
   }

   public long getSizeOnDisk() {
      return this.levelSummary.func_207744_c();
   }

   public int compareTo(RealmsLevelSummary p_compareTo_1_) {
      if (this.levelSummary.getLastPlayed() < p_compareTo_1_.getLastPlayed()) {
         return 1;
      } else {
         return this.levelSummary.getLastPlayed() > p_compareTo_1_.getLastPlayed() ? -1 : this.levelSummary.getLevelId().compareTo(p_compareTo_1_.getLevelId());
      }
   }
}