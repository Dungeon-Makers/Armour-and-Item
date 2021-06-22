package net.minecraft.entity.ai.attributes;

import javax.annotation.Nullable;

public interface IAttribute {
   String func_111108_a();

   double sanitizeValue(double p_111109_1_);

   double getDefaultValue();

   boolean isClientSyncable();

   @Nullable
   IAttribute func_180372_d();
}