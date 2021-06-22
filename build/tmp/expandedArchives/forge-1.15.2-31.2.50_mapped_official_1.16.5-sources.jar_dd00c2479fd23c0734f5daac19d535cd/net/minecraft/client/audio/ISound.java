package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISound {
   ResourceLocation getLocation();

   @Nullable
   SoundEventAccessor resolve(SoundHandler p_184366_1_);

   Sound getSound();

   SoundCategory getSource();

   boolean isLooping();

   boolean isRelative();

   int getDelay();

   float getVolume();

   float getPitch();

   float getX();

   float getY();

   float getZ();

   ISound.AttenuationType getAttenuation();

   default boolean canStartSilent() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum AttenuationType {
      NONE,
      LINEAR;
   }
}