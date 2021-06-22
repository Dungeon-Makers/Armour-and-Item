package net.minecraft.entity.ai.goal;

import javax.annotation.Nullable;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.entity.ai.brain.BrainUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class MoveTowardsVillageGoal extends RandomWalkingGoal {
   public MoveTowardsVillageGoal(CreatureEntity p_i50325_1_, double p_i50325_2_) {
      super(p_i50325_1_, p_i50325_2_, 10);
   }

   public boolean canUse() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      BlockPos blockpos = new BlockPos(this.mob);
      return serverworld.isVillage(blockpos) ? false : super.canUse();
   }

   @Nullable
   protected Vec3d getPosition() {
      ServerWorld serverworld = (ServerWorld)this.mob.level;
      BlockPos blockpos = new BlockPos(this.mob);
      SectionPos sectionpos = SectionPos.of(blockpos);
      SectionPos sectionpos1 = BrainUtil.findSectionClosestToVillage(serverworld, sectionpos, 2);
      return sectionpos1 != sectionpos ? RandomPositionGenerator.getPosTowards(this.mob, 10, 7, new Vec3d(sectionpos1.center())) : null;
   }
}