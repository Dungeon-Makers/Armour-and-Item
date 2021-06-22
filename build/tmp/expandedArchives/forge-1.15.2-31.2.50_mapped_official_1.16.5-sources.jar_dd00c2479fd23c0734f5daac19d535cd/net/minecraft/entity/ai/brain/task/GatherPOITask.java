package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.longs.Long2LongMap;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.village.PointOfInterestManager;
import net.minecraft.village.PointOfInterestType;
import net.minecraft.world.server.ServerWorld;

public class GatherPOITask extends Task<CreatureEntity> {
   private final PointOfInterestType poiType;
   private final MemoryModuleType<GlobalPos> memoryToAcquire;
   private final boolean onlyIfAdult;
   private long nextScheduledStart;
   private final Long2LongMap batchCache = new Long2LongOpenHashMap();
   private int field_223014_f;

   public GatherPOITask(PointOfInterestType p_i50374_1_, MemoryModuleType<GlobalPos> p_i50374_2_, boolean p_i50374_3_) {
      super(ImmutableMap.of(p_i50374_2_, MemoryModuleStatus.VALUE_ABSENT));
      this.poiType = p_i50374_1_;
      this.memoryToAcquire = p_i50374_2_;
      this.onlyIfAdult = p_i50374_3_;
   }

   protected boolean checkExtraStartConditions(ServerWorld p_212832_1_, CreatureEntity p_212832_2_) {
      if (this.onlyIfAdult && p_212832_2_.isBaby()) {
         return false;
      } else {
         return p_212832_1_.getGameTime() - this.nextScheduledStart >= 20L;
      }
   }

   protected void start(ServerWorld p_212831_1_, CreatureEntity p_212831_2_, long p_212831_3_) {
      this.field_223014_f = 0;
      this.nextScheduledStart = p_212831_1_.getGameTime() + (long)p_212831_1_.getRandom().nextInt(20);
      PointOfInterestManager pointofinterestmanager = p_212831_1_.getPoiManager();
      Predicate<BlockPos> predicate = (p_220603_1_) -> {
         long i = p_220603_1_.asLong();
         if (this.batchCache.containsKey(i)) {
            return false;
         } else if (++this.field_223014_f >= 5) {
            return false;
         } else {
            this.batchCache.put(i, this.nextScheduledStart + 40L);
            return true;
         }
      };
      Stream<BlockPos> stream = pointofinterestmanager.findAll(this.poiType.getPredicate(), predicate, new BlockPos(p_212831_2_), 48, PointOfInterestManager.Status.HAS_SPACE);
      Path path = p_212831_2_.getNavigation().createPath(stream, this.poiType.getValidRange());
      if (path != null && path.canReach()) {
         BlockPos blockpos = path.getTarget();
         pointofinterestmanager.getType(blockpos).ifPresent((p_225441_5_) -> {
            pointofinterestmanager.take(this.poiType.getPredicate(), (p_225442_1_) -> {
               return p_225442_1_.equals(blockpos);
            }, blockpos, 1);
            p_212831_2_.getBrain().setMemory(this.memoryToAcquire, GlobalPos.func_218179_a(p_212831_1_.func_201675_m().func_186058_p(), blockpos));
            DebugPacketSender.sendPoiTicketCountPacket(p_212831_1_, blockpos);
         });
      } else if (this.field_223014_f < 5) {
         this.batchCache.long2LongEntrySet().removeIf((p_223011_1_) -> {
            return p_223011_1_.getLongValue() < this.nextScheduledStart;
         });
      }

   }
}