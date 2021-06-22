package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

public abstract class AbstractCriterionTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> players = Maps.newIdentityHashMap();

   public final void addPlayerListener(PlayerAdvancements p_192165_1_, ICriterionTrigger.Listener<T> p_192165_2_) {
      this.players.computeIfAbsent(p_192165_1_, (p_227072_0_) -> {
         return Sets.newHashSet();
      }).add(p_192165_2_);
   }

   public final void removePlayerListener(PlayerAdvancements p_192164_1_, ICriterionTrigger.Listener<T> p_192164_2_) {
      Set<ICriterionTrigger.Listener<T>> set = this.players.get(p_192164_1_);
      if (set != null) {
         set.remove(p_192164_2_);
         if (set.isEmpty()) {
            this.players.remove(p_192164_1_);
         }
      }

   }

   public final void removePlayerListeners(PlayerAdvancements p_192167_1_) {
      this.players.remove(p_192167_1_);
   }

   protected void func_227070_a_(PlayerAdvancements p_227070_1_, Predicate<T> p_227070_2_) {
      Set<ICriterionTrigger.Listener<T>> set = this.players.get(p_227070_1_);
      if (set != null) {
         List<ICriterionTrigger.Listener<T>> list = null;

         for(ICriterionTrigger.Listener<T> listener : set) {
            if (p_227070_2_.test(listener.getTriggerInstance())) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<T> listener1 : list) {
               listener1.run(p_227070_1_);
            }
         }

      }
   }

   protected void func_227071_b_(PlayerAdvancements p_227071_1_) {
      Set<ICriterionTrigger.Listener<T>> set = this.players.get(p_227071_1_);
      if (set != null && !set.isEmpty()) {
         for(ICriterionTrigger.Listener<T> listener : ImmutableSet.copyOf(set)) {
            listener.run(p_227071_1_);
         }
      }

   }
}