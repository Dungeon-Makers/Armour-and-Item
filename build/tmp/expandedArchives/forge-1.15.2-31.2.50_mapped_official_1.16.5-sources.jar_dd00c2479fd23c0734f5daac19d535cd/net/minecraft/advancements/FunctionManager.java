package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.brigadier.CommandDispatcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.SimpleResource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.GameRules;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FunctionManager implements IResourceManagerReloadListener {
   private static final Logger field_193067_a = LogManager.getLogger();
   private static final ResourceLocation TICK_FUNCTION_TAG = new ResourceLocation("tick");
   private static final ResourceLocation LOAD_FUNCTION_TAG = new ResourceLocation("load");
   public static final int field_195454_a = "functions/".length();
   public static final int field_195455_b = ".mcfunction".length();
   private final MinecraftServer server;
   private final Map<ResourceLocation, FunctionObject> field_193070_d = Maps.newHashMap();
   private boolean isInFunction;
   private final ArrayDeque<FunctionManager.QueuedCommand> commandQueue = new ArrayDeque<>();
   private final List<FunctionManager.QueuedCommand> nestedCalls = Lists.newArrayList();
   private final TagCollection<FunctionObject> field_200002_i = new TagCollection<>(this::get, "tags/functions", true, "function");
   private final List<FunctionObject> ticking = Lists.newArrayList();
   private boolean postReload;

   public FunctionManager(MinecraftServer p_i47920_1_) {
      this.server = p_i47920_1_;
   }

   public Optional<FunctionObject> get(ResourceLocation p_215361_1_) {
      return Optional.ofNullable(this.field_193070_d.get(p_215361_1_));
   }

   public MinecraftServer getServer() {
      return this.server;
   }

   public int getCommandLimit() {
      return this.server.getGameRules().getInt(GameRules.RULE_MAX_COMMAND_CHAIN_LENGTH);
   }

   public Map<ResourceLocation, FunctionObject> func_193066_d() {
      return this.field_193070_d;
   }

   public CommandDispatcher<CommandSource> getDispatcher() {
      return this.server.getCommands().getDispatcher();
   }

   public void tick() {
      this.server.getProfiler().push(TICK_FUNCTION_TAG::toString);

      for(FunctionObject functionobject : this.ticking) {
         this.execute(functionobject, this.getGameLoopSender());
      }

      this.server.getProfiler().pop();
      if (this.postReload) {
         this.postReload = false;
         Collection<FunctionObject> collection = this.func_200000_g().func_199915_b(LOAD_FUNCTION_TAG).func_199885_a();
         this.server.getProfiler().push(LOAD_FUNCTION_TAG::toString);

         for(FunctionObject functionobject1 : collection) {
            this.execute(functionobject1, this.getGameLoopSender());
         }

         this.server.getProfiler().pop();
      }

   }

   public int execute(FunctionObject p_195447_1_, CommandSource p_195447_2_) {
      int i = this.getCommandLimit();
      if (this.isInFunction) {
         if (this.commandQueue.size() + this.nestedCalls.size() < i) {
            this.nestedCalls.add(new FunctionManager.QueuedCommand(this, p_195447_2_, new FunctionObject.FunctionEntry(p_195447_1_)));
         }

         return 0;
      } else {
         try {
            this.isInFunction = true;
            int j = 0;
            FunctionObject.IEntry[] afunctionobject$ientry = p_195447_1_.getEntries();

            for(int k = afunctionobject$ientry.length - 1; k >= 0; --k) {
               this.commandQueue.push(new FunctionManager.QueuedCommand(this, p_195447_2_, afunctionobject$ientry[k]));
            }

            while(!this.commandQueue.isEmpty()) {
               try {
                  FunctionManager.QueuedCommand functionmanager$queuedcommand = this.commandQueue.removeFirst();
                  this.server.getProfiler().push(functionmanager$queuedcommand::toString);
                  functionmanager$queuedcommand.execute(this.commandQueue, i);
                  if (!this.nestedCalls.isEmpty()) {
                     Lists.reverse(this.nestedCalls).forEach(this.commandQueue::addFirst);
                     this.nestedCalls.clear();
                  }
               } finally {
                  this.server.getProfiler().pop();
               }

               ++j;
               if (j >= i) {
                  return j;
               }
            }

            return j;
         } finally {
            this.commandQueue.clear();
            this.nestedCalls.clear();
            this.isInFunction = false;
         }
      }
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.field_193070_d.clear();
      this.ticking.clear();
      Collection<ResourceLocation> collection = p_195410_1_.listResources("functions", (p_215364_0_) -> {
         return p_215364_0_.endsWith(".mcfunction");
      });
      List<CompletableFuture<FunctionObject>> list = Lists.newArrayList();

      for(ResourceLocation resourcelocation : collection) {
         String s = resourcelocation.getPath();
         ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring(field_195454_a, s.length() - field_195455_b));
         list.add(CompletableFuture.supplyAsync(() -> {
            return func_195449_a(p_195410_1_, resourcelocation);
         }, SimpleResource.field_199031_a).thenApplyAsync((p_215365_2_) -> {
            return FunctionObject.func_197000_a(resourcelocation1, this, p_215365_2_);
         }, this.server.getBackgroundTaskExecutor()).handle((p_215362_2_, p_215362_3_) -> {
            return this.func_212250_a(p_215362_2_, p_215362_3_, resourcelocation);
         }));
      }

      CompletableFuture.allOf(list.toArray(new CompletableFuture[0])).join();
      if (!this.field_193070_d.isEmpty()) {
         field_193067_a.info("Loaded {} custom command functions", (int)this.field_193070_d.size());
      }

      this.field_200002_i.func_219779_a(this.field_200002_i.func_219781_a(p_195410_1_, this.server.getBackgroundTaskExecutor()).join());
      this.ticking.addAll(this.field_200002_i.func_199915_b(TICK_FUNCTION_TAG).func_199885_a());
      this.postReload = true;
   }

   @Nullable
   private FunctionObject func_212250_a(FunctionObject p_212250_1_, @Nullable Throwable p_212250_2_, ResourceLocation p_212250_3_) {
      if (p_212250_2_ != null) {
         field_193067_a.error("Couldn't load function at {}", p_212250_3_, p_212250_2_);
         return null;
      } else {
         synchronized(this.field_193070_d) {
            this.field_193070_d.put(p_212250_1_.getId(), p_212250_1_);
            return p_212250_1_;
         }
      }
   }

   private static List<String> func_195449_a(IResourceManager p_195449_0_, ResourceLocation p_195449_1_) {
      try (IResource iresource = p_195449_0_.getResource(p_195449_1_)) {
         List list = IOUtils.readLines(iresource.getInputStream(), StandardCharsets.UTF_8);
         return list;
      } catch (IOException ioexception) {
         throw new CompletionException(ioexception);
      }
   }

   public CommandSource getGameLoopSender() {
      return this.server.createCommandSourceStack().withPermission(2).withSuppressedOutput();
   }

   public CommandSource func_223402_g() {
      return new CommandSource(ICommandSource.NULL, Vec3d.ZERO, Vec2f.ZERO, (ServerWorld)null, this.server.getFunctionCompilationLevel(), "", new StringTextComponent(""), this.server, (Entity)null);
   }

   public TagCollection<FunctionObject> func_200000_g() {
      return this.field_200002_i;
   }

   public static class QueuedCommand {
      private final FunctionManager manager;
      private final CommandSource sender;
      private final FunctionObject.IEntry entry;

      public QueuedCommand(FunctionManager p_i48018_1_, CommandSource p_i48018_2_, FunctionObject.IEntry p_i48018_3_) {
         this.manager = p_i48018_1_;
         this.sender = p_i48018_2_;
         this.entry = p_i48018_3_;
      }

      public void execute(ArrayDeque<FunctionManager.QueuedCommand> p_194222_1_, int p_194222_2_) {
         try {
            this.entry.execute(this.manager, this.sender, p_194222_1_, p_194222_2_);
         } catch (Throwable var4) {
            ;
         }

      }

      public String toString() {
         return this.entry.toString();
      }
   }
}