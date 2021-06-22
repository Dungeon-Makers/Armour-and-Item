package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import net.minecraft.util.ResourceLocation;

public interface ICriterionInstance {
   ResourceLocation getCriterion();

   default JsonElement func_200288_b() {
      return JsonNull.INSTANCE;
   }
}