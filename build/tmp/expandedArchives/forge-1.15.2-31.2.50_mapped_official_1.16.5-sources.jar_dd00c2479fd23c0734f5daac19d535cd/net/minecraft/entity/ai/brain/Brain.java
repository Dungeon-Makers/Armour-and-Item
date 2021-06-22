package net.minecraft.entity.ai.brain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleStatus;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.schedule.Activity;
import net.minecraft.entity.ai.brain.schedule.Schedule;
import net.minecraft.entity.ai.brain.sensor.Sensor;
import net.minecraft.entity.ai.brain.sensor.SensorType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.server.ServerWorld;

public class Brain<E extends LivingEntity> implements IDynamicSerializable {
   private final Map<MemoryModuleType<?>, Optional<?>> memories = Maps.newHashMap();
   private final Map<SensorType<? extends Sensor<? super E>>, Sensor<? super E>> sensors = Maps.newLinkedHashMap();
   private final Map<Integer, Map<Activity, Set<Task<? super E>>>> availableBehaviorsByPriority = Maps.newTreeMap();
   private Schedule schedule = Schedule.EMPTY;
   private final Map<Activity, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>>> activityRequirements = Maps.newHashMap();
   private Set<Activity> coreActivities = Sets.newHashSet();
   private final Set<Activity> activeActivities = Sets.newHashSet();
   private Activity defaultActivity = Activity.IDLE;
   private long lastScheduleUpdate = -9999L;

   public <T> Brain(Collection<MemoryModuleType<?>> p_i50378_1_, Collection<SensorType<? extends Sensor<? super E>>> p_i50378_2_, Dynamic<T> p_i50378_3_) {
      p_i50378_1_.forEach((p_218228_1_) -> {
         Optional optional = this.memories.put(p_218228_1_, Optional.empty());
      });
      p_i50378_2_.forEach((p_218204_1_) -> {
         Sensor sensor = this.sensors.put(p_218204_1_, p_218204_1_.create());
      });
      this.sensors.values().forEach((p_218225_1_) -> {
         for(MemoryModuleType<?> memorymoduletype : p_218225_1_.requires()) {
            this.memories.put(memorymoduletype, Optional.empty());
         }

      });

      for(Entry<Dynamic<T>, Dynamic<T>> entry : p_i50378_3_.get("memories").asMap(Function.identity(), Function.identity()).entrySet()) {
         this.func_218216_a(Registry.MEMORY_MODULE_TYPE.get(new ResourceLocation(entry.getKey().asString(""))), entry.getValue());
      }

   }

   public boolean hasMemoryValue(MemoryModuleType<?> p_218191_1_) {
      return this.checkMemory(p_218191_1_, MemoryModuleStatus.VALUE_PRESENT);
   }

   private <T, U> void func_218216_a(MemoryModuleType<U> p_218216_1_, Dynamic<T> p_218216_2_) {
      this.setMemory(p_218216_1_, p_218216_1_.func_220938_b().orElseThrow(RuntimeException::new).apply(p_218216_2_));
   }

   public <U> void eraseMemory(MemoryModuleType<U> p_218189_1_) {
      this.setMemory(p_218189_1_, Optional.empty());
   }

   public <U> void setMemory(MemoryModuleType<U> p_218205_1_, @Nullable U p_218205_2_) {
      this.setMemory(p_218205_1_, Optional.ofNullable(p_218205_2_));
   }

   public <U> void setMemory(MemoryModuleType<U> p_218226_1_, Optional<U> p_218226_2_) {
      if (this.memories.containsKey(p_218226_1_)) {
         if (p_218226_2_.isPresent() && this.isEmptyCollection(p_218226_2_.get())) {
            this.eraseMemory(p_218226_1_);
         } else {
            this.memories.put(p_218226_1_, p_218226_2_);
         }
      }

   }

   public <U> Optional<U> getMemory(MemoryModuleType<U> p_218207_1_) {
      return (Optional<U>) this.memories.get(p_218207_1_);
   }

   public boolean checkMemory(MemoryModuleType<?> p_218196_1_, MemoryModuleStatus p_218196_2_) {
      Optional<?> optional = this.memories.get(p_218196_1_);
      if (optional == null) {
         return false;
      } else {
         return p_218196_2_ == MemoryModuleStatus.REGISTERED || p_218196_2_ == MemoryModuleStatus.VALUE_PRESENT && optional.isPresent() || p_218196_2_ == MemoryModuleStatus.VALUE_ABSENT && !optional.isPresent();
      }
   }

   public Schedule getSchedule() {
      return this.schedule;
   }

   public void setSchedule(Schedule p_218203_1_) {
      this.schedule = p_218203_1_;
   }

   public void setCoreActivities(Set<Activity> p_218199_1_) {
      this.coreActivities = p_218199_1_;
   }

   @Deprecated
   public Stream<Task<? super E>> func_218193_d() {
      return this.availableBehaviorsByPriority.values().stream().flatMap((p_218221_0_) -> {
         return p_218221_0_.values().stream();
      }).flatMap(Collection::stream).filter((p_218187_0_) -> {
         return p_218187_0_.getStatus() == Task.Status.RUNNING;
      });
   }

   public void setActiveActivityIfPossible(Activity p_218202_1_) {
      this.activeActivities.clear();
      this.activeActivities.addAll(this.coreActivities);
      boolean flag = this.activityRequirements.keySet().contains(p_218202_1_) && this.activityRequirementsAreMet(p_218202_1_);
      this.activeActivities.add(flag ? p_218202_1_ : this.defaultActivity);
   }

   public void updateActivityFromSchedule(long p_218211_1_, long p_218211_3_) {
      if (p_218211_3_ - this.lastScheduleUpdate > 20L) {
         this.lastScheduleUpdate = p_218211_3_;
         Activity activity = this.getSchedule().getActivityAt((int)(p_218211_1_ % 24000L));
         if (!this.activeActivities.contains(activity)) {
            this.setActiveActivityIfPossible(activity);
         }
      }

   }

   public void setDefaultActivity(Activity p_218200_1_) {
      this.defaultActivity = p_218200_1_;
   }

   public void addActivity(Activity p_218208_1_, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218208_2_) {
      this.func_218224_a(p_218208_1_, p_218208_2_, ImmutableSet.of());
   }

   public void func_218224_a(Activity p_218224_1_, ImmutableList<Pair<Integer, ? extends Task<? super E>>> p_218224_2_, Set<Pair<MemoryModuleType<?>, MemoryModuleStatus>> p_218224_3_) {
      this.activityRequirements.put(p_218224_1_, p_218224_3_);
      p_218224_2_.forEach((p_218223_2_) -> {
         this.availableBehaviorsByPriority.computeIfAbsent(p_218223_2_.getFirst(), (p_218212_0_) -> {
            return Maps.newHashMap();
         }).computeIfAbsent(p_218224_1_, (p_218195_0_) -> {
            return Sets.newLinkedHashSet();
         }).add(p_218223_2_.getSecond());
      });
   }

   public boolean isActive(Activity p_218214_1_) {
      return this.activeActivities.contains(p_218214_1_);
   }

   public Brain<E> copyWithoutBehaviors() {
      Brain<E> brain = new Brain<>(this.memories.keySet(), this.sensors.keySet(), new Dynamic<>(NBTDynamicOps.INSTANCE, new CompoundNBT()));
      this.memories.forEach((p_218188_1_, p_218188_2_) -> {
         p_218188_2_.ifPresent((p_218209_2_) -> {
            Optional optional = brain.memories.put(p_218188_1_, Optional.of(p_218209_2_));
         });
      });
      return brain;
   }

   public void tick(ServerWorld p_218210_1_, E p_218210_2_) {
      this.func_218229_c(p_218210_1_, p_218210_2_);
      this.startEachNonRunningBehavior(p_218210_1_, p_218210_2_);
      this.tickEachRunningBehavior(p_218210_1_, p_218210_2_);
   }

   public void stopAll(ServerWorld p_218227_1_, E p_218227_2_) {
      long i = p_218227_2_.level.getGameTime();
      this.func_218193_d().forEach((p_218206_4_) -> {
         p_218206_4_.doStop(p_218227_1_, p_218227_2_, i);
      });
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      T t = p_218175_1_.createMap(this.memories.entrySet().stream().filter((p_218197_0_) -> {
         return p_218197_0_.getKey().func_220938_b().isPresent() && p_218197_0_.getValue().isPresent();
      }).map((p_218186_1_) -> {
         return Pair.of(p_218175_1_.createString(Registry.MEMORY_MODULE_TYPE.getKey(p_218186_1_.getKey()).toString()), ((IDynamicSerializable)p_218186_1_.getValue().get()).func_218175_a(p_218175_1_));
      }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("memories"), t));
   }

   private void func_218229_c(ServerWorld p_218229_1_, E p_218229_2_) {
      this.sensors.values().forEach((p_218201_2_) -> {
         p_218201_2_.tick(p_218229_1_, p_218229_2_);
      });
   }

   private void startEachNonRunningBehavior(ServerWorld p_218218_1_, E p_218218_2_) {
      long i = p_218218_1_.getGameTime();
      this.availableBehaviorsByPriority.values().stream().flatMap((p_218219_0_) -> {
         return p_218219_0_.entrySet().stream();
      }).filter((p_218215_1_) -> {
         return this.activeActivities.contains(p_218215_1_.getKey());
      }).map(Entry::getValue).flatMap(Collection::stream).filter((p_218194_0_) -> {
         return p_218194_0_.getStatus() == Task.Status.STOPPED;
      }).forEach((p_218192_4_) -> {
         p_218192_4_.tryStart(p_218218_1_, p_218218_2_, i);
      });
   }

   private void tickEachRunningBehavior(ServerWorld p_218222_1_, E p_218222_2_) {
      long i = p_218222_1_.getGameTime();
      this.func_218193_d().forEach((p_218220_4_) -> {
         p_218220_4_.tickOrStop(p_218222_1_, p_218222_2_, i);
      });
   }

   private boolean activityRequirementsAreMet(Activity p_218217_1_) {
      return this.activityRequirements.get(p_218217_1_).stream().allMatch((p_218190_1_) -> {
         MemoryModuleType<?> memorymoduletype = p_218190_1_.getFirst();
         MemoryModuleStatus memorymodulestatus = p_218190_1_.getSecond();
         return this.checkMemory(memorymoduletype, memorymodulestatus);
      });
   }

   private boolean isEmptyCollection(Object p_218213_1_) {
      return p_218213_1_ instanceof Collection && ((Collection)p_218213_1_).isEmpty();
   }
}