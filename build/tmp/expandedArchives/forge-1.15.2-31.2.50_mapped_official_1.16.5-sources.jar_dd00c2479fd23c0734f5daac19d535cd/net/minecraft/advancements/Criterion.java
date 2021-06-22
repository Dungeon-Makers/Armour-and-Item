package net.minecraft.advancements;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class Criterion {
   private final ICriterionInstance trigger;

   public Criterion(ICriterionInstance p_i47470_1_) {
      this.trigger = p_i47470_1_;
   }

   public Criterion() {
      this.trigger = null;
   }

   public void serializeToNetwork(PacketBuffer p_192140_1_) {
   }

   public static Criterion func_192145_a(JsonObject p_192145_0_, JsonDeserializationContext p_192145_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(p_192145_0_, "trigger"));
      ICriterionTrigger<?> icriteriontrigger = CriteriaTriggers.getCriterion(resourcelocation);
      if (icriteriontrigger == null) {
         throw new JsonSyntaxException("Invalid criterion trigger: " + resourcelocation);
      } else {
         ICriterionInstance icriterioninstance = icriteriontrigger.func_192166_a(JSONUtils.getAsJsonObject(p_192145_0_, "conditions", new JsonObject()), p_192145_1_);
         return new Criterion(icriterioninstance);
      }
   }

   public static Criterion criterionFromNetwork(PacketBuffer p_192146_0_) {
      return new Criterion();
   }

   public static Map<String, Criterion> func_192144_b(JsonObject p_192144_0_, JsonDeserializationContext p_192144_1_) {
      Map<String, Criterion> map = Maps.newHashMap();

      for(Entry<String, JsonElement> entry : p_192144_0_.entrySet()) {
         map.put(entry.getKey(), func_192145_a(JSONUtils.convertToJsonObject(entry.getValue(), "criterion"), p_192144_1_));
      }

      return map;
   }

   public static Map<String, Criterion> criteriaFromNetwork(PacketBuffer p_192142_0_) {
      Map<String, Criterion> map = Maps.newHashMap();
      int i = p_192142_0_.readVarInt();

      for(int j = 0; j < i; ++j) {
         map.put(p_192142_0_.readUtf(32767), criterionFromNetwork(p_192142_0_));
      }

      return map;
   }

   public static void serializeToNetwork(Map<String, Criterion> p_192141_0_, PacketBuffer p_192141_1_) {
      p_192141_1_.writeVarInt(p_192141_0_.size());

      for(Entry<String, Criterion> entry : p_192141_0_.entrySet()) {
         p_192141_1_.writeUtf(entry.getKey());
         entry.getValue().serializeToNetwork(p_192141_1_);
      }

   }

   @Nullable
   public ICriterionInstance getTrigger() {
      return this.trigger;
   }

   public JsonElement serializeToJson() {
      JsonObject jsonobject = new JsonObject();
      jsonobject.addProperty("trigger", this.trigger.getCriterion().toString());
      jsonobject.add("conditions", this.trigger.func_200288_b());
      return jsonobject;
   }
}