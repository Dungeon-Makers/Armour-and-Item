package net.minecraft.client.audio;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MusicTicker {
   private final Random random = new Random();
   private final Minecraft minecraft;
   private ISound currentMusic;
   private int nextSongDelay = 100;

   public MusicTicker(Minecraft p_i45112_1_) {
      this.minecraft = p_i45112_1_;
   }

   public void tick() {
      MusicTicker.MusicType musicticker$musictype = this.minecraft.func_147109_W();
      if (this.currentMusic != null) {
         if (!musicticker$musictype.func_188768_a().getLocation().equals(this.currentMusic.getLocation())) {
            this.minecraft.getSoundManager().stop(this.currentMusic);
            this.nextSongDelay = MathHelper.nextInt(this.random, 0, musicticker$musictype.func_148634_b() / 2);
         }

         if (!this.minecraft.getSoundManager().isActive(this.currentMusic)) {
            this.currentMusic = null;
            this.nextSongDelay = Math.min(MathHelper.nextInt(this.random, musicticker$musictype.func_148634_b(), musicticker$musictype.func_148633_c()), this.nextSongDelay);
         }
      }

      this.nextSongDelay = Math.min(this.nextSongDelay, musicticker$musictype.func_148633_c());
      if (this.currentMusic == null && this.nextSongDelay-- <= 0) {
         this.func_181558_a(musicticker$musictype);
      }

   }

   public void func_181558_a(MusicTicker.MusicType p_181558_1_) {
      this.currentMusic = SimpleSound.forMusic(p_181558_1_.func_188768_a());
      this.minecraft.getSoundManager().play(this.currentMusic);
      this.nextSongDelay = Integer.MAX_VALUE;
   }

   public void stopPlaying() {
      if (this.currentMusic != null) {
         this.minecraft.getSoundManager().stop(this.currentMusic);
         this.currentMusic = null;
         this.nextSongDelay = 0;
      }

   }

   public boolean func_209100_b(MusicTicker.MusicType p_209100_1_) {
      return this.currentMusic == null ? false : p_209100_1_.func_188768_a().getLocation().equals(this.currentMusic.getLocation());
   }

   @OnlyIn(Dist.CLIENT)
   public static enum MusicType {
      MENU(SoundEvents.MUSIC_MENU, 20, 600),
      GAME(SoundEvents.MUSIC_GAME, 12000, 24000),
      CREATIVE(SoundEvents.MUSIC_CREATIVE, 1200, 3600),
      CREDITS(SoundEvents.MUSIC_CREDITS, 0, 0),
      NETHER(SoundEvents.field_187673_dD, 1200, 3600),
      END_BOSS(SoundEvents.MUSIC_DRAGON, 0, 0),
      END(SoundEvents.MUSIC_END, 6000, 24000),
      UNDER_WATER(SoundEvents.MUSIC_UNDER_WATER, 12000, 24000);

      private final SoundEvent field_148645_h;
      private final int field_148646_i;
      private final int field_148643_j;

      private MusicType(SoundEvent p_i47050_3_, int p_i47050_4_, int p_i47050_5_) {
         this.field_148645_h = p_i47050_3_;
         this.field_148646_i = p_i47050_4_;
         this.field_148643_j = p_i47050_5_;
      }

      public SoundEvent func_188768_a() {
         return this.field_148645_h;
      }

      public int func_148634_b() {
         return this.field_148646_i;
      }

      public int func_148633_c() {
         return this.field_148643_j;
      }
   }
}