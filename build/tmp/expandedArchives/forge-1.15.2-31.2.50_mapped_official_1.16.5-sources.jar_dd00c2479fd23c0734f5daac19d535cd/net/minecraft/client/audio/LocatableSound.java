package net.minecraft.client.audio;

import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class LocatableSound implements ISound {
   protected Sound sound;
   @Nullable
   private SoundEventAccessor field_184369_l;
   protected final SoundCategory source;
   protected final ResourceLocation location;
   protected float volume = 1.0F;
   protected float pitch = 1.0F;
   protected float x;
   protected float y;
   protected float z;
   protected boolean looping;
   protected int delay;
   protected ISound.AttenuationType attenuation = ISound.AttenuationType.LINEAR;
   protected boolean priority;
   protected boolean relative;

   protected LocatableSound(SoundEvent p_i46533_1_, SoundCategory p_i46533_2_) {
      this(p_i46533_1_.getLocation(), p_i46533_2_);
   }

   protected LocatableSound(ResourceLocation p_i46534_1_, SoundCategory p_i46534_2_) {
      this.location = p_i46534_1_;
      this.source = p_i46534_2_;
   }

   public ResourceLocation getLocation() {
      return this.location;
   }

   public SoundEventAccessor resolve(SoundHandler p_184366_1_) {
      this.field_184369_l = p_184366_1_.getSoundEvent(this.location);
      if (this.field_184369_l == null) {
         this.sound = SoundHandler.EMPTY_SOUND;
      } else {
         this.sound = this.field_184369_l.getSound();
      }

      return this.field_184369_l;
   }

   public Sound getSound() {
      return this.sound;
   }

   public SoundCategory getSource() {
      return this.source;
   }

   public boolean isLooping() {
      return this.looping;
   }

   public int getDelay() {
      return this.delay;
   }

   public float getVolume() {
      return this.volume * this.sound.getVolume();
   }

   public float getPitch() {
      return this.pitch * this.sound.getPitch();
   }

   public float getX() {
      return this.x;
   }

   public float getY() {
      return this.y;
   }

   public float getZ() {
      return this.z;
   }

   public ISound.AttenuationType getAttenuation() {
      return this.attenuation;
   }

   public boolean isRelative() {
      return this.relative;
   }
}