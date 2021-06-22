package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class EntityPredicate {
   public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, PlayerPredicate.ANY, (String)null, (ResourceLocation)null);
   public static final EntityPredicate[] field_204851_b = new EntityPredicate[0];
   private final EntityTypePredicate entityType;
   private final DistancePredicate distanceToPlayer;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NBTPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final PlayerPredicate player;
   @Nullable
   private final String team;
   @Nullable
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate p_i225735_1_, DistancePredicate p_i225735_2_, LocationPredicate p_i225735_3_, MobEffectsPredicate p_i225735_4_, NBTPredicate p_i225735_5_, EntityFlagsPredicate p_i225735_6_, EntityEquipmentPredicate p_i225735_7_, PlayerPredicate p_i225735_8_, @Nullable String p_i225735_9_, @Nullable ResourceLocation p_i225735_10_) {
      this.entityType = p_i225735_1_;
      this.distanceToPlayer = p_i225735_2_;
      this.location = p_i225735_3_;
      this.effects = p_i225735_4_;
      this.nbt = p_i225735_5_;
      this.flags = p_i225735_6_;
      this.equipment = p_i225735_7_;
      this.player = p_i225735_8_;
      this.team = p_i225735_9_;
      this.catType = p_i225735_10_;
   }

   public boolean matches(ServerPlayerEntity p_192482_1_, @Nullable Entity p_192482_2_) {
      return this.matches(p_192482_1_.getLevel(), p_192482_1_.position(), p_192482_2_);
   }

   public boolean matches(ServerWorld p_217993_1_, @Nullable Vec3d p_217993_2_, @Nullable Entity p_217993_3_) {
      if (this == ANY) {
         return true;
      } else if (p_217993_3_ == null) {
         return false;
      } else if (!this.entityType.matches(p_217993_3_.getType())) {
         return false;
      } else {
         if (p_217993_2_ == null) {
            if (this.distanceToPlayer != DistancePredicate.ANY) {
               return false;
            }
         } else if (!this.distanceToPlayer.matches(p_217993_2_.x, p_217993_2_.y, p_217993_2_.z, p_217993_3_.getX(), p_217993_3_.getY(), p_217993_3_.getZ())) {
            return false;
         }

         if (!this.location.matches(p_217993_1_, p_217993_3_.getX(), p_217993_3_.getY(), p_217993_3_.getZ())) {
            return false;
         } else if (!this.effects.matches(p_217993_3_)) {
            return false;
         } else if (!this.nbt.matches(p_217993_3_)) {
            return false;
         } else if (!this.flags.matches(p_217993_3_)) {
            return false;
         } else if (!this.equipment.matches(p_217993_3_)) {
            return false;
         } else if (!this.player.matches(p_217993_3_)) {
            return false;
         } else {
            if (this.team != null) {
               Team team = p_217993_3_.getTeam();
               if (team == null || !this.team.equals(team.getName())) {
                  return false;
               }
            }

            return this.catType == null || p_217993_3_ instanceof CatEntity && ((CatEntity)p_217993_3_).getResourceLocation().equals(this.catType);
         }
      }
   }

   public static EntityPredicate fromJson(@Nullable JsonElement p_192481_0_) {
      if (p_192481_0_ != null && !p_192481_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192481_0_, "entity");
         EntityTypePredicate entitytypepredicate = EntityTypePredicate.fromJson(jsonobject.get("type"));
         DistancePredicate distancepredicate = DistancePredicate.fromJson(jsonobject.get("distance"));
         LocationPredicate locationpredicate = LocationPredicate.fromJson(jsonobject.get("location"));
         MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.fromJson(jsonobject.get("effects"));
         NBTPredicate nbtpredicate = NBTPredicate.fromJson(jsonobject.get("nbt"));
         EntityFlagsPredicate entityflagspredicate = EntityFlagsPredicate.fromJson(jsonobject.get("flags"));
         EntityEquipmentPredicate entityequipmentpredicate = EntityEquipmentPredicate.fromJson(jsonobject.get("equipment"));
         PlayerPredicate playerpredicate = PlayerPredicate.fromJson(jsonobject.get("player"));
         String s = JSONUtils.getAsString(jsonobject, "team", (String)null);
         ResourceLocation resourcelocation = jsonobject.has("catType") ? new ResourceLocation(JSONUtils.getAsString(jsonobject, "catType")) : null;
         return (new EntityPredicate.Builder()).entityType(entitytypepredicate).distance(distancepredicate).located(locationpredicate).effects(mobeffectspredicate).nbt(nbtpredicate).flags(entityflagspredicate).equipment(entityequipmentpredicate).player(playerpredicate).team(s).catType(resourcelocation).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] func_204849_b(@Nullable JsonElement p_204849_0_) {
      if (p_204849_0_ != null && !p_204849_0_.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.convertToJsonArray(p_204849_0_, "entities");
         EntityPredicate[] aentitypredicate = new EntityPredicate[jsonarray.size()];

         for(int i = 0; i < jsonarray.size(); ++i) {
            aentitypredicate[i] = fromJson(jsonarray.get(i));
         }

         return aentitypredicate;
      } else {
         return field_204851_b;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("type", this.entityType.serializeToJson());
         jsonobject.add("distance", this.distanceToPlayer.serializeToJson());
         jsonobject.add("location", this.location.serializeToJson());
         jsonobject.add("effects", this.effects.serializeToJson());
         jsonobject.add("nbt", this.nbt.serializeToJson());
         jsonobject.add("flags", this.flags.serializeToJson());
         jsonobject.add("equipment", this.equipment.serializeToJson());
         jsonobject.add("player", this.player.serializeToJson());
         jsonobject.addProperty("team", this.team);
         if (this.catType != null) {
            jsonobject.addProperty("catType", this.catType.toString());
         }

         return jsonobject;
      }
   }

   public static JsonElement func_204850_a(EntityPredicate[] p_204850_0_) {
      if (p_204850_0_ == field_204851_b) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray jsonarray = new JsonArray();

         for(EntityPredicate entitypredicate : p_204850_0_) {
            JsonElement jsonelement = entitypredicate.serializeToJson();
            if (!jsonelement.isJsonNull()) {
               jsonarray.add(jsonelement);
            }
         }

         return jsonarray;
      }
   }

   public static class Builder {
      private EntityTypePredicate entityType = EntityTypePredicate.ANY;
      private DistancePredicate distanceToPlayer = DistancePredicate.ANY;
      private LocationPredicate location = LocationPredicate.ANY;
      private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
      private NBTPredicate nbt = NBTPredicate.ANY;
      private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
      private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
      private PlayerPredicate player = PlayerPredicate.ANY;
      private String team;
      private ResourceLocation catType;

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType<?> p_203998_1_) {
         this.entityType = EntityTypePredicate.of(p_203998_1_);
         return this;
      }

      public EntityPredicate.Builder of(Tag<EntityType<?>> p_217989_1_) {
         this.entityType = EntityTypePredicate.of(p_217989_1_);
         return this;
      }

      public EntityPredicate.Builder of(ResourceLocation p_217986_1_) {
         this.catType = p_217986_1_;
         return this;
      }

      public EntityPredicate.Builder entityType(EntityTypePredicate p_209366_1_) {
         this.entityType = p_209366_1_;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate p_203997_1_) {
         this.distanceToPlayer = p_203997_1_;
         return this;
      }

      public EntityPredicate.Builder located(LocationPredicate p_203999_1_) {
         this.location = p_203999_1_;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate p_209367_1_) {
         this.effects = p_209367_1_;
         return this;
      }

      public EntityPredicate.Builder nbt(NBTPredicate p_209365_1_) {
         this.nbt = p_209365_1_;
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate p_217987_1_) {
         this.flags = p_217987_1_;
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate p_217985_1_) {
         this.equipment = p_217985_1_;
         return this;
      }

      public EntityPredicate.Builder player(PlayerPredicate p_226613_1_) {
         this.player = p_226613_1_;
         return this;
      }

      public EntityPredicate.Builder team(@Nullable String p_226614_1_) {
         this.team = p_226614_1_;
         return this;
      }

      public EntityPredicate.Builder catType(@Nullable ResourceLocation p_217988_1_) {
         this.catType = p_217988_1_;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.player, this.team, this.catType);
      }
   }
}