package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;

public class EndGatewayConfig implements IFeatureConfig {
   private final Optional<BlockPos> exit;
   private final boolean exact;

   private EndGatewayConfig(Optional<BlockPos> p_i49882_1_, boolean p_i49882_2_) {
      this.exit = p_i49882_1_;
      this.exact = p_i49882_2_;
   }

   public static EndGatewayConfig knownExit(BlockPos p_214702_0_, boolean p_214702_1_) {
      return new EndGatewayConfig(Optional.of(p_214702_0_), p_214702_1_);
   }

   public static EndGatewayConfig delayedExitSearch() {
      return new EndGatewayConfig(Optional.empty(), false);
   }

   public Optional<BlockPos> getExit() {
      return this.exit;
   }

   public boolean isExitExact() {
      return this.exact;
   }

   public <T> Dynamic<T> func_214634_a(DynamicOps<T> p_214634_1_) {
      return new Dynamic<>(p_214634_1_, (T)this.exit.map((p_214703_2_) -> {
         return p_214634_1_.createMap(ImmutableMap.of(p_214634_1_.createString("exit_x"), p_214634_1_.createInt(p_214703_2_.getX()), p_214634_1_.createString("exit_y"), p_214634_1_.createInt(p_214703_2_.getY()), p_214634_1_.createString("exit_z"), p_214634_1_.createInt(p_214703_2_.getZ()), p_214634_1_.createString("exact"), p_214634_1_.createBoolean(this.exact)));
      }).orElse(p_214634_1_.emptyMap()));
   }

   public static <T> EndGatewayConfig func_214697_a(Dynamic<T> p_214697_0_) {
      Optional<BlockPos> optional = p_214697_0_.get("exit_x").asNumber().flatMap((p_214696_1_) -> {
         return p_214697_0_.get("exit_y").asNumber().flatMap((p_214695_2_) -> {
            return p_214697_0_.get("exit_z").asNumber().map((p_214699_2_) -> {
               return new BlockPos(p_214696_1_.intValue(), p_214695_2_.intValue(), p_214699_2_.intValue());
            });
         });
      });
      boolean flag = p_214697_0_.get("exact").asBoolean(false);
      return new EndGatewayConfig(optional, flag);
   }
}