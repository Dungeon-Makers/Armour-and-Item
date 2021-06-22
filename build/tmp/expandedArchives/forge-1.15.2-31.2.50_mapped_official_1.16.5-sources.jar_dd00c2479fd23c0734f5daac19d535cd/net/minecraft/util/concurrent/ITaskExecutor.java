package net.minecraft.util.concurrent;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

public interface ITaskExecutor<Msg> extends AutoCloseable {
   String name();

   void tell(Msg p_212871_1_);

   default void close() {
   }

   default <Source> CompletableFuture<Source> ask(Function<? super ITaskExecutor<Source>, ? extends Msg> p_213141_1_) {
      CompletableFuture<Source> completablefuture = new CompletableFuture<>();
      Msg msg = p_213141_1_.apply(of("ask future procesor handle", completablefuture::complete));
      this.tell(msg);
      return completablefuture;
   }

   static <Msg> ITaskExecutor<Msg> of(final String p_213140_0_, final Consumer<Msg> p_213140_1_) {
      return new ITaskExecutor<Msg>() {
         public String name() {
            return p_213140_0_;
         }

         public void tell(Msg p_212871_1_) {
            p_213140_1_.accept(p_212871_1_);
         }

         public String toString() {
            return p_213140_0_;
         }
      };
   }
}