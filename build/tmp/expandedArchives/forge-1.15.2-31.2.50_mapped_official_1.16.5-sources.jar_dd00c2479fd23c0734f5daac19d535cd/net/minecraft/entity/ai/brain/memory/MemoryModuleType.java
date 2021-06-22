package net.minecraft.entity.ai.brain.memory;

import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.LongSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.util.math.IPosWrapper;
import net.minecraft.util.registry.Registry;

public class MemoryModuleType<U> extends net.minecraftforge.registries.ForgeRegistryEntry<MemoryModuleType<?>> {
   public static final MemoryModuleType<Void> DUMMY = register("dummy");
   public static final MemoryModuleType<GlobalPos> HOME = func_220937_a("home", Optional.of(GlobalPos::func_218176_a));
   public static final MemoryModuleType<GlobalPos> JOB_SITE = func_220937_a("job_site", Optional.of(GlobalPos::func_218176_a));
   public static final MemoryModuleType<GlobalPos> MEETING_POINT = func_220937_a("meeting_point", Optional.of(GlobalPos::func_218176_a));
   public static final MemoryModuleType<List<GlobalPos>> SECONDARY_JOB_SITE = register("secondary_job_site");
   public static final MemoryModuleType<List<LivingEntity>> LIVING_ENTITIES = register("mobs");
   public static final MemoryModuleType<List<LivingEntity>> VISIBLE_LIVING_ENTITIES = register("visible_mobs");
   public static final MemoryModuleType<List<LivingEntity>> VISIBLE_VILLAGER_BABIES = register("visible_villager_babies");
   public static final MemoryModuleType<List<PlayerEntity>> NEAREST_PLAYERS = register("nearest_players");
   public static final MemoryModuleType<PlayerEntity> NEAREST_VISIBLE_PLAYER = register("nearest_visible_player");
   public static final MemoryModuleType<WalkTarget> WALK_TARGET = register("walk_target");
   public static final MemoryModuleType<IPosWrapper> LOOK_TARGET = register("look_target");
   public static final MemoryModuleType<LivingEntity> INTERACTION_TARGET = register("interaction_target");
   public static final MemoryModuleType<VillagerEntity> BREED_TARGET = register("breed_target");
   public static final MemoryModuleType<Path> PATH = register("path");
   public static final MemoryModuleType<List<GlobalPos>> INTERACTABLE_DOORS = register("interactable_doors");
   public static final MemoryModuleType<Set<GlobalPos>> DOORS_TO_CLOSE = register("opened_doors");
   public static final MemoryModuleType<BlockPos> NEAREST_BED = register("nearest_bed");
   public static final MemoryModuleType<DamageSource> HURT_BY = register("hurt_by");
   public static final MemoryModuleType<LivingEntity> HURT_BY_ENTITY = register("hurt_by_entity");
   public static final MemoryModuleType<LivingEntity> NEAREST_HOSTILE = register("nearest_hostile");
   public static final MemoryModuleType<GlobalPos> HIDING_PLACE = register("hiding_place");
   public static final MemoryModuleType<Long> HEARD_BELL_TIME = register("heard_bell_time");
   public static final MemoryModuleType<Long> CANT_REACH_WALK_TARGET_SINCE = register("cant_reach_walk_target_since");
   public static final MemoryModuleType<Long> field_223542_x = register("golem_last_seen_time");
   public static final MemoryModuleType<LongSerializable> LAST_SLEPT = func_220937_a("last_slept", Optional.of(LongSerializable::func_223462_a));
   public static final MemoryModuleType<LongSerializable> LAST_WOKEN = func_220937_a("last_woken", Optional.of(LongSerializable::func_223462_a));
   public static final MemoryModuleType<LongSerializable> LAST_WORKED_AT_POI = func_220937_a("last_worked_at_poi", Optional.of(LongSerializable::func_223462_a));
   private final Optional<Function<Dynamic<?>, U>> field_220963_x;

   public MemoryModuleType(Optional<Function<Dynamic<?>, U>> p_i50306_1_) {
      this.field_220963_x = p_i50306_1_;
   }

   public String toString() {
      return Registry.MEMORY_MODULE_TYPE.getKey(this).toString();
   }

   public Optional<Function<Dynamic<?>, U>> func_220938_b() {
      return this.field_220963_x;
   }

   private static <U extends IDynamicSerializable> MemoryModuleType<U> func_220937_a(String p_220937_0_, Optional<Function<Dynamic<?>, U>> p_220937_1_) {
      return Registry.register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(p_220937_0_), new MemoryModuleType<>(p_220937_1_));
   }

   private static <U> MemoryModuleType<U> register(String p_223541_0_) {
      return Registry.register(Registry.MEMORY_MODULE_TYPE, new ResourceLocation(p_223541_0_), new MemoryModuleType<>(Optional.empty()));
   }
}
