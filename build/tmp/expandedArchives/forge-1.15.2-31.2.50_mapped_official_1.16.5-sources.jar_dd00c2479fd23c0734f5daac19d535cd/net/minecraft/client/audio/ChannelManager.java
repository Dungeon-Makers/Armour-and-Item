package net.minecraft.client.audio;

import com.google.common.collect.Sets;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ChannelManager {
   private final Set<ChannelManager.Entry> channels = Sets.newIdentityHashSet();
   private final SoundSystem library;
   private final Executor executor;

   public ChannelManager(SoundSystem p_i50894_1_, Executor p_i50894_2_) {
      this.library = p_i50894_1_;
      this.executor = p_i50894_2_;
   }

   public ChannelManager.Entry func_217895_a(SoundSystem.Mode p_217895_1_) {
      ChannelManager.Entry channelmanager$entry = new ChannelManager.Entry();
      this.executor.execute(() -> {
         SoundSource soundsource = this.library.acquireChannel(p_217895_1_);
         if (soundsource != null) {
            channelmanager$entry.channel = soundsource;
            this.channels.add(channelmanager$entry);
         }

      });
      return channelmanager$entry;
   }

   public void executeOnChannels(Consumer<Stream<SoundSource>> p_217897_1_) {
      this.executor.execute(() -> {
         p_217897_1_.accept(this.channels.stream().map((p_217896_0_) -> {
            return p_217896_0_.channel;
         }).filter(Objects::nonNull));
      });
   }

   public void scheduleTick() {
      this.executor.execute(() -> {
         Iterator<ChannelManager.Entry> iterator = this.channels.iterator();

         while(iterator.hasNext()) {
            ChannelManager.Entry channelmanager$entry = iterator.next();
            channelmanager$entry.channel.updateStream();
            if (channelmanager$entry.channel.stopped()) {
               channelmanager$entry.release();
               iterator.remove();
            }
         }

      });
   }

   public void clear() {
      this.channels.forEach(ChannelManager.Entry::release);
      this.channels.clear();
   }

   @OnlyIn(Dist.CLIENT)
   public class Entry {
      private SoundSource channel;
      private boolean stopped;

      public boolean isStopped() {
         return this.stopped;
      }

      public void execute(Consumer<SoundSource> p_217888_1_) {
         ChannelManager.this.executor.execute(() -> {
            if (this.channel != null) {
               p_217888_1_.accept(this.channel);
            }

         });
      }

      public void release() {
         this.stopped = true;
         ChannelManager.this.library.releaseChannel(this.channel);
         this.channel = null;
      }
   }
}