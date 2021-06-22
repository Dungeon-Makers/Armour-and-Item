package net.minecraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SoundEvent extends net.minecraftforge.registries.ForgeRegistryEntry<SoundEvent> {
   private final ResourceLocation location;

   public SoundEvent(ResourceLocation p_i46834_1_) {
      this.location = p_i46834_1_;
   }

   @OnlyIn(Dist.CLIENT)
   public ResourceLocation getLocation() {
      return this.location;
   }
}
