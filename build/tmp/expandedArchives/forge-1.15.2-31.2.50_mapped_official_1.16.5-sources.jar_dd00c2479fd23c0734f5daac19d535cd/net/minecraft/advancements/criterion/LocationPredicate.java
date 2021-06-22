package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.server.ServerWorld;

public class LocationPredicate {
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (Biome)null, (Structure<?>)null, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   private final MinMaxBounds.FloatBound x;
   private final MinMaxBounds.FloatBound y;
   private final MinMaxBounds.FloatBound z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final Structure<?> feature;
   @Nullable
   private final DimensionType dimension;
   private final LightPredicate light;
   private final BlockPredicate block;
   private final FluidPredicate fluid;

   public LocationPredicate(MinMaxBounds.FloatBound p_i225755_1_, MinMaxBounds.FloatBound p_i225755_2_, MinMaxBounds.FloatBound p_i225755_3_, @Nullable Biome p_i225755_4_, @Nullable Structure<?> p_i225755_5_, @Nullable DimensionType p_i225755_6_, LightPredicate p_i225755_7_, BlockPredicate p_i225755_8_, FluidPredicate p_i225755_9_) {
      this.x = p_i225755_1_;
      this.y = p_i225755_2_;
      this.z = p_i225755_3_;
      this.biome = p_i225755_4_;
      this.feature = p_i225755_5_;
      this.dimension = p_i225755_6_;
      this.light = p_i225755_7_;
      this.block = p_i225755_8_;
      this.fluid = p_i225755_9_;
   }

   public static LocationPredicate func_204010_a(Biome p_204010_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, p_204010_0_, (Structure<?>)null, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate func_204008_a(DimensionType p_204008_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (Biome)null, (Structure<?>)null, p_204008_0_, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public static LocationPredicate inFeature(Structure<?> p_218020_0_) {
      return new LocationPredicate(MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, MinMaxBounds.FloatBound.ANY, (Biome)null, p_218020_0_, (DimensionType)null, LightPredicate.ANY, BlockPredicate.ANY, FluidPredicate.ANY);
   }

   public boolean matches(ServerWorld p_193452_1_, double p_193452_2_, double p_193452_4_, double p_193452_6_) {
      return this.matches(p_193452_1_, (float)p_193452_2_, (float)p_193452_4_, (float)p_193452_6_);
   }

   public boolean matches(ServerWorld p_193453_1_, float p_193453_2_, float p_193453_3_, float p_193453_4_) {
      if (!this.x.matches(p_193453_2_)) {
         return false;
      } else if (!this.y.matches(p_193453_3_)) {
         return false;
      } else if (!this.z.matches(p_193453_4_)) {
         return false;
      } else if (this.dimension != null && this.dimension != p_193453_1_.dimension.func_186058_p()) {
         return false;
      } else {
         BlockPos blockpos = new BlockPos((double)p_193453_2_, (double)p_193453_3_, (double)p_193453_4_);
         boolean flag = p_193453_1_.isLoaded(blockpos);
         if (this.biome == null || flag && this.biome == p_193453_1_.getBiome(blockpos)) {
            if (this.feature == null || flag && this.feature.func_202366_b(p_193453_1_, blockpos)) {
               if (!this.light.matches(p_193453_1_, blockpos)) {
                  return false;
               } else if (!this.block.matches(p_193453_1_, blockpos)) {
                  return false;
               } else {
                  return this.fluid.matches(p_193453_1_, blockpos);
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("x", this.x.serializeToJson());
            jsonobject1.add("y", this.y.serializeToJson());
            jsonobject1.add("z", this.z.serializeToJson());
            jsonobject.add("position", jsonobject1);
         }

         if (this.dimension != null) {
            jsonobject.addProperty("dimension", DimensionType.func_212678_a(this.dimension).toString());
         }

         if (this.feature != null) {
            jsonobject.addProperty("feature", Feature.field_202300_at.inverse().get(this.feature));
         }

         if (this.biome != null) {
            jsonobject.addProperty("biome", Registry.field_212624_m.getKey(this.biome).toString());
         }

         jsonobject.add("light", this.light.serializeToJson());
         jsonobject.add("block", this.block.serializeToJson());
         jsonobject.add("fluid", this.fluid.serializeToJson());
         return jsonobject;
      }
   }

   public static LocationPredicate fromJson(@Nullable JsonElement p_193454_0_) {
      if (p_193454_0_ != null && !p_193454_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_193454_0_, "location");
         JsonObject jsonobject1 = JSONUtils.getAsJsonObject(jsonobject, "position", new JsonObject());
         MinMaxBounds.FloatBound minmaxbounds$floatbound = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("x"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound1 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("y"));
         MinMaxBounds.FloatBound minmaxbounds$floatbound2 = MinMaxBounds.FloatBound.fromJson(jsonobject1.get("z"));
         DimensionType dimensiontype = jsonobject.has("dimension") ? DimensionType.func_193417_a(new ResourceLocation(JSONUtils.getAsString(jsonobject, "dimension"))) : null;
         Structure<?> structure = jsonobject.has("feature") ? Feature.field_202300_at.get(JSONUtils.getAsString(jsonobject, "feature")) : null;
         Biome biome = null;
         if (jsonobject.has("biome")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "biome"));
            biome = Registry.field_212624_m.func_218349_b(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown biome '" + resourcelocation + "'");
            });
         }

         LightPredicate lightpredicate = LightPredicate.fromJson(jsonobject.get("light"));
         BlockPredicate blockpredicate = BlockPredicate.fromJson(jsonobject.get("block"));
         FluidPredicate fluidpredicate = FluidPredicate.fromJson(jsonobject.get("fluid"));
         return new LocationPredicate(minmaxbounds$floatbound, minmaxbounds$floatbound1, minmaxbounds$floatbound2, biome, structure, dimensiontype, lightpredicate, blockpredicate, fluidpredicate);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.FloatBound x = MinMaxBounds.FloatBound.ANY;
      private MinMaxBounds.FloatBound y = MinMaxBounds.FloatBound.ANY;
      private MinMaxBounds.FloatBound z = MinMaxBounds.FloatBound.ANY;
      @Nullable
      private Biome biome;
      @Nullable
      private Structure<?> feature;
      @Nullable
      private DimensionType dimension;
      private LightPredicate light = LightPredicate.ANY;
      private BlockPredicate block = BlockPredicate.ANY;
      private FluidPredicate fluid = FluidPredicate.ANY;

      public static LocationPredicate.Builder location() {
         return new LocationPredicate.Builder();
      }

      public LocationPredicate.Builder func_218012_a(@Nullable Biome p_218012_1_) {
         this.biome = p_218012_1_;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension, this.light, this.block, this.fluid);
      }
   }
}