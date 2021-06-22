package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;

public class KilledByCrossbowTrigger extends AbstractCriterionTrigger<KilledByCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("killed_by_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   public KilledByCrossbowTrigger.Instance func_192166_a(JsonObject p_192166_1_, JsonDeserializationContext p_192166_2_) {
      EntityPredicate[] aentitypredicate = EntityPredicate.func_204849_b(p_192166_1_.get("victims"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(p_192166_1_.get("unique_entity_types"));
      return new KilledByCrossbowTrigger.Instance(aentitypredicate, minmaxbounds$intbound);
   }

   public void func_215105_a(ServerPlayerEntity p_215105_1_, Collection<Entity> p_215105_2_, int p_215105_3_) {
      this.func_227070_a_(p_215105_1_.getAdvancements(), (p_226842_3_) -> {
         return p_226842_3_.func_215115_a(p_215105_1_, p_215105_2_, p_215105_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate[] victims;
      private final MinMaxBounds.IntBound uniqueEntityTypes;

      public Instance(EntityPredicate[] p_i50580_1_, MinMaxBounds.IntBound p_i50580_2_) {
         super(KilledByCrossbowTrigger.ID);
         this.victims = p_i50580_1_;
         this.uniqueEntityTypes = p_i50580_2_;
      }

      public static KilledByCrossbowTrigger.Instance crossbowKilled(EntityPredicate.Builder... p_215116_0_) {
         EntityPredicate[] aentitypredicate = new EntityPredicate[p_215116_0_.length];

         for(int i = 0; i < p_215116_0_.length; ++i) {
            EntityPredicate.Builder entitypredicate$builder = p_215116_0_[i];
            aentitypredicate[i] = entitypredicate$builder.build();
         }

         return new KilledByCrossbowTrigger.Instance(aentitypredicate, MinMaxBounds.IntBound.ANY);
      }

      public static KilledByCrossbowTrigger.Instance crossbowKilled(MinMaxBounds.IntBound p_215117_0_) {
         EntityPredicate[] aentitypredicate = new EntityPredicate[0];
         return new KilledByCrossbowTrigger.Instance(aentitypredicate, p_215117_0_);
      }

      public boolean func_215115_a(ServerPlayerEntity p_215115_1_, Collection<Entity> p_215115_2_, int p_215115_3_) {
         if (this.victims.length > 0) {
            List<Entity> list = Lists.newArrayList(p_215115_2_);

            for(EntityPredicate entitypredicate : this.victims) {
               boolean flag = false;
               Iterator<Entity> iterator = list.iterator();

               while(iterator.hasNext()) {
                  Entity entity = iterator.next();
                  if (entitypredicate.matches(p_215115_1_, entity)) {
                     iterator.remove();
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }
         }

         if (this.uniqueEntityTypes == MinMaxBounds.IntBound.ANY) {
            return true;
         } else {
            Set<EntityType<?>> set = Sets.newHashSet();

            for(Entity entity1 : p_215115_2_) {
               set.add(entity1.getType());
            }

            return this.uniqueEntityTypes.matches(set.size()) && this.uniqueEntityTypes.matches(p_215115_3_);
         }
      }

      public JsonElement func_200288_b() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("victims", EntityPredicate.func_204850_a(this.victims));
         jsonobject.add("unique_entity_types", this.uniqueEntityTypes.serializeToJson());
         return jsonobject;
      }
   }
}