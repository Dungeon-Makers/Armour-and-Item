package net.minecraft.network.rcon;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.util.DefaultWithNameUncaughtExceptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class RConThread implements Runnable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger UNIQUE_THREAD_ID = new AtomicInteger(0);
   protected boolean running;
   protected final IServer field_72617_b;
   protected final String name;
   protected Thread thread;
   protected final int field_72615_d = 5;
   protected final List<DatagramSocket> field_72616_e = Lists.newArrayList();
   protected final List<ServerSocket> field_72614_f = Lists.newArrayList();

   protected RConThread(IServer p_i45300_1_, String p_i45300_2_) {
      this.field_72617_b = p_i45300_1_;
      this.name = p_i45300_2_;
      if (this.field_72617_b.func_71239_B()) {
         this.func_72606_c("Debugging is enabled, performance maybe reduced!");
      }

   }

   public synchronized void func_72602_a() {
      this.thread = new Thread(this, this.name + " #" + UNIQUE_THREAD_ID.incrementAndGet());
      this.thread.setUncaughtExceptionHandler(new DefaultWithNameUncaughtExceptionHandler(LOGGER));
      this.thread.start();
      this.running = true;
   }

   public synchronized void stop() {
      this.running = false;
      if (null != this.thread) {
         int i = 0;

         while(this.thread.isAlive()) {
            try {
               this.thread.join(1000L);
               ++i;
               if (5 <= i) {
                  this.func_72606_c("Waited " + i + " seconds attempting force stop!");
                  this.func_72612_a(true);
               } else if (this.thread.isAlive()) {
                  this.func_72606_c("Thread " + this + " (" + this.thread.getState() + ") failed to exit after " + i + " second(s)");
                  this.func_72606_c("Stack:");

                  for(StackTraceElement stacktraceelement : this.thread.getStackTrace()) {
                     this.func_72606_c(stacktraceelement.toString());
                  }

                  this.thread.interrupt();
               }
            } catch (InterruptedException var6) {
               ;
            }
         }

         this.func_72612_a(true);
         this.thread = null;
      }
   }

   public boolean isRunning() {
      return this.running;
   }

   protected void func_72607_a(String p_72607_1_) {
      this.field_72617_b.func_71198_k(p_72607_1_);
   }

   protected void func_72609_b(String p_72609_1_) {
      this.field_72617_b.func_71244_g(p_72609_1_);
   }

   protected void func_72606_c(String p_72606_1_) {
      this.field_72617_b.func_71236_h(p_72606_1_);
   }

   protected void func_72610_d(String p_72610_1_) {
      this.field_72617_b.func_71201_j(p_72610_1_);
   }

   protected int func_72603_d() {
      return this.field_72617_b.getPlayerCount();
   }

   protected void func_72601_a(DatagramSocket p_72601_1_) {
      this.func_72607_a("registerSocket: " + p_72601_1_);
      this.field_72616_e.add(p_72601_1_);
   }

   protected boolean func_72604_a(DatagramSocket p_72604_1_, boolean p_72604_2_) {
      this.func_72607_a("closeSocket: " + p_72604_1_);
      if (null == p_72604_1_) {
         return false;
      } else {
         boolean flag = false;
         if (!p_72604_1_.isClosed()) {
            p_72604_1_.close();
            flag = true;
         }

         if (p_72604_2_) {
            this.field_72616_e.remove(p_72604_1_);
         }

         return flag;
      }
   }

   protected boolean func_72608_b(ServerSocket p_72608_1_) {
      return this.func_72605_a(p_72608_1_, true);
   }

   protected boolean func_72605_a(ServerSocket p_72605_1_, boolean p_72605_2_) {
      this.func_72607_a("closeSocket: " + p_72605_1_);
      if (null == p_72605_1_) {
         return false;
      } else {
         boolean flag = false;

         try {
            if (!p_72605_1_.isClosed()) {
               p_72605_1_.close();
               flag = true;
            }
         } catch (IOException ioexception) {
            this.func_72606_c("IO: " + ioexception.getMessage());
         }

         if (p_72605_2_) {
            this.field_72614_f.remove(p_72605_1_);
         }

         return flag;
      }
   }

   protected void func_72611_e() {
      this.func_72612_a(false);
   }

   protected void func_72612_a(boolean p_72612_1_) {
      int i = 0;

      for(DatagramSocket datagramsocket : this.field_72616_e) {
         if (this.func_72604_a(datagramsocket, false)) {
            ++i;
         }
      }

      this.field_72616_e.clear();

      for(ServerSocket serversocket : this.field_72614_f) {
         if (this.func_72605_a(serversocket, false)) {
            ++i;
         }
      }

      this.field_72614_f.clear();
      if (p_72612_1_ && 0 < i) {
         this.func_72606_c("Force closed " + i + " sockets");
      }

   }
}