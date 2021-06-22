package net.minecraft.tags;

import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class NetworkTagManager implements IFutureReloadListener {
   private final NetworkTagCollection<Block> blocks = new NetworkTagCollection<>(Registry.BLOCK, "tags/blocks", "block");
   private final NetworkTagCollection<Item> items = new NetworkTagCollection<>(Registry.ITEM, "tags/items", "item");
   private final NetworkTagCollection<Fluid> fluids = new NetworkTagCollection<>(Registry.FLUID, "tags/fluids", "fluid");
   private final NetworkTagCollection<EntityType<?>> entityTypes = new NetworkTagCollection<>(Registry.ENTITY_TYPE, "tags/entity_types", "entity_type");

   public NetworkTagCollection<Block> func_199717_a() {
      return this.blocks;
   }

   public NetworkTagCollection<Item> func_199715_b() {
      return this.items;
   }

   public NetworkTagCollection<Fluid> func_205704_c() {
      return this.fluids;
   }

   public NetworkTagCollection<EntityType<?>> func_215297_d() {
      return this.entityTypes;
   }

   public void func_199716_a(PacketBuffer p_199716_1_) {
      this.blocks.func_200042_a(p_199716_1_);
      this.items.func_200042_a(p_199716_1_);
      this.fluids.func_200042_a(p_199716_1_);
      this.entityTypes.func_200042_a(p_199716_1_);
   }

   public static NetworkTagManager func_199714_b(PacketBuffer p_199714_0_) {
      NetworkTagManager networktagmanager = new NetworkTagManager();
      networktagmanager.func_199717_a().func_200043_b(p_199714_0_);
      networktagmanager.func_199715_b().func_200043_b(p_199714_0_);
      networktagmanager.func_205704_c().func_200043_b(p_199714_0_);
      networktagmanager.func_215297_d().func_200043_b(p_199714_0_);
      return networktagmanager;
   }

   public CompletableFuture<Void> reload(IFutureReloadListener.IStage p_215226_1_, IResourceManager p_215226_2_, IProfiler p_215226_3_, IProfiler p_215226_4_, Executor p_215226_5_, Executor p_215226_6_) {
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Block>>> completablefuture = this.blocks.func_219781_a(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Item>>> completablefuture1 = this.items.func_219781_a(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<Fluid>>> completablefuture2 = this.fluids.func_219781_a(p_215226_2_, p_215226_5_);
      CompletableFuture<Map<ResourceLocation, Tag.Builder<EntityType<?>>>> completablefuture3 = this.entityTypes.func_219781_a(p_215226_2_, p_215226_5_);
      return completablefuture.thenCombine(completablefuture1, Pair::of).thenCombine(completablefuture2.thenCombine(completablefuture3, Pair::of), (p_215296_0_, p_215296_1_) -> {
         return new NetworkTagManager.ReloadResults(p_215296_0_.getFirst(), p_215296_0_.getSecond(), p_215296_1_.getFirst(), p_215296_1_.getSecond());
      }).thenCompose(p_215226_1_::wait).thenAcceptAsync((p_215298_1_) -> {
         this.blocks.func_219779_a(p_215298_1_.field_219785_a);
         this.items.func_219779_a(p_215298_1_.field_219786_b);
         this.fluids.func_219779_a(p_215298_1_.field_219787_c);
         this.entityTypes.func_219779_a(p_215298_1_.field_219788_d);
         BlockTags.func_199895_a(this.blocks);
         ItemTags.func_199902_a(this.items);
         FluidTags.func_206953_a(this.fluids);
         EntityTypeTags.func_219759_a(this.entityTypes);
         net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.TagsUpdatedEvent(this));
      }, p_215226_6_);
   }

   public static class ReloadResults {
      final Map<ResourceLocation, Tag.Builder<Block>> field_219785_a;
      final Map<ResourceLocation, Tag.Builder<Item>> field_219786_b;
      final Map<ResourceLocation, Tag.Builder<Fluid>> field_219787_c;
      final Map<ResourceLocation, Tag.Builder<EntityType<?>>> field_219788_d;

      public ReloadResults(Map<ResourceLocation, Tag.Builder<Block>> p_i50480_1_, Map<ResourceLocation, Tag.Builder<Item>> p_i50480_2_, Map<ResourceLocation, Tag.Builder<Fluid>> p_i50480_3_, Map<ResourceLocation, Tag.Builder<EntityType<?>>> p_i50480_4_) {
         this.field_219785_a = p_i50480_1_;
         this.field_219786_b = p_i50480_2_;
         this.field_219787_c = p_i50480_3_;
         this.field_219788_d = p_i50480_4_;
      }
   }
}
