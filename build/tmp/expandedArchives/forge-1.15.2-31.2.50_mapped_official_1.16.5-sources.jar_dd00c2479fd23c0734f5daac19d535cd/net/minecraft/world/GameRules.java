package net.minecraft.world;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SChangeGameStatePacket;
import net.minecraft.network.play.server.SEntityStatusPacket;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameRules {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Map<GameRules.RuleKey<?>, GameRules.RuleType<?>> GAME_RULE_TYPES = Maps.newTreeMap(Comparator.comparing((p_223597_0_) -> {
      return p_223597_0_.id;
   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOFIRETICK = func_223595_a("doFireTick", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_MOBGRIEFING = func_223595_a("mobGriefing", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_KEEPINVENTORY = func_223595_a("keepInventory", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOMOBSPAWNING = func_223595_a("doMobSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOMOBLOOT = func_223595_a("doMobLoot", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOBLOCKDROPS = func_223595_a("doTileDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOENTITYDROPS = func_223595_a("doEntityDrops", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_COMMANDBLOCKOUTPUT = func_223595_a("commandBlockOutput", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_NATURAL_REGENERATION = func_223595_a("naturalRegeneration", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DAYLIGHT = func_223595_a("doDaylightCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_LOGADMINCOMMANDS = func_223595_a("logAdminCommands", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_SHOWDEATHMESSAGES = func_223595_a("showDeathMessages", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> RULE_RANDOMTICKING = func_223595_a("randomTickSpeed", GameRules.IntegerValue.create(3));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_SENDCOMMANDFEEDBACK = func_223595_a("sendCommandFeedback", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_REDUCEDDEBUGINFO = func_223595_a("reducedDebugInfo", GameRules.BooleanValue.create(false, (p_223589_0_, p_223589_1_) -> {
      byte b0 = (byte)(p_223589_1_.get() ? 22 : 23);

      for(ServerPlayerEntity serverplayerentity : p_223589_0_.getPlayerList().getPlayers()) {
         serverplayerentity.connection.send(new SEntityStatusPacket(serverplayerentity, b0));
      }

   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_SPECTATORSGENERATECHUNKS = func_223595_a("spectatorsGenerateChunks", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.IntegerValue> RULE_SPAWN_RADIUS = func_223595_a("spawnRadius", GameRules.IntegerValue.create(10));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DISABLE_ELYTRA_MOVEMENT_CHECK = func_223595_a("disableElytraMovementCheck", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> RULE_MAX_ENTITY_CRAMMING = func_223595_a("maxEntityCramming", GameRules.IntegerValue.create(24));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_WEATHER_CYCLE = func_223595_a("doWeatherCycle", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_LIMITED_CRAFTING = func_223595_a("doLimitedCrafting", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.IntegerValue> RULE_MAX_COMMAND_CHAIN_LENGTH = func_223595_a("maxCommandChainLength", GameRules.IntegerValue.create(65536));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_ANNOUNCE_ADVANCEMENTS = func_223595_a("announceAdvancements", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DISABLE_RAIDS = func_223595_a("disableRaids", GameRules.BooleanValue.create(false));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DOINSOMNIA = func_223595_a("doInsomnia", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DO_IMMEDIATE_RESPAWN = func_223595_a("doImmediateRespawn", GameRules.BooleanValue.create(false, (p_226686_0_, p_226686_1_) -> {
      for(ServerPlayerEntity serverplayerentity : p_226686_0_.getPlayerList().getPlayers()) {
         serverplayerentity.connection.send(new SChangeGameStatePacket(11, p_226686_1_.get() ? 1.0F : 0.0F));
      }

   }));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DROWNING_DAMAGE = func_223595_a("drowningDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_FALL_DAMAGE = func_223595_a("fallDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_FIRE_DAMAGE = func_223595_a("fireDamage", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DO_PATROL_SPAWNING = func_223595_a("doPatrolSpawning", GameRules.BooleanValue.create(true));
   public static final GameRules.RuleKey<GameRules.BooleanValue> RULE_DO_TRADER_SPAWNING = func_223595_a("doTraderSpawning", GameRules.BooleanValue.create(true));
   private final Map<GameRules.RuleKey<?>, GameRules.RuleValue<?>> rules = GAME_RULE_TYPES.entrySet().stream().collect(ImmutableMap.toImmutableMap(Entry::getKey, (p_226684_0_) -> {
      return p_226684_0_.getValue().createRule();
   }));

   public static <T extends GameRules.RuleValue<T>> GameRules.RuleKey<T> func_223595_a(String p_223595_0_, GameRules.RuleType<T> p_223595_1_) {
      GameRules.RuleKey<T> rulekey = new GameRules.RuleKey<>(p_223595_0_);
      GameRules.RuleType<?> ruletype = GAME_RULE_TYPES.put(rulekey, p_223595_1_);
      if (ruletype != null) {
         throw new IllegalStateException("Duplicate game rule registration for " + p_223595_0_);
      } else {
         return rulekey;
      }
   }

   public <T extends GameRules.RuleValue<T>> T getRule(GameRules.RuleKey<T> p_223585_1_) {
      return (T)(this.rules.get(p_223585_1_));
   }

   public CompoundNBT createTag() {
      CompoundNBT compoundnbt = new CompoundNBT();
      this.rules.forEach((p_226688_1_, p_226688_2_) -> {
         compoundnbt.putString(p_226688_1_.id, p_226688_2_.serialize());
      });
      return compoundnbt;
   }

   public void func_82768_a(CompoundNBT p_82768_1_) {
      this.rules.forEach((p_226685_1_, p_226685_2_) -> {
         if (p_82768_1_.contains(p_226685_1_.id)) {
            p_226685_2_.deserialize(p_82768_1_.getString(p_226685_1_.id));
         }

      });
   }

   public static void visitGameRuleTypes(GameRules.IRuleEntryVisitor p_223590_0_) {
      GAME_RULE_TYPES.forEach((p_226687_1_, p_226687_2_) -> {
         func_223596_a(p_223590_0_, p_226687_1_, p_226687_2_);
      });
   }

   private static <T extends GameRules.RuleValue<T>> void func_223596_a(GameRules.IRuleEntryVisitor p_223596_0_, GameRules.RuleKey<?> p_223596_1_, GameRules.RuleType<?> p_223596_2_) {
      p_223596_0_.visit((GameRules.RuleKey)p_223596_1_, p_223596_2_);
   }

   public boolean getBoolean(GameRules.RuleKey<GameRules.BooleanValue> p_223586_1_) {
      return this.getRule(p_223586_1_).get();
   }

   public int getInt(GameRules.RuleKey<GameRules.IntegerValue> p_223592_1_) {
      return this.getRule(p_223592_1_).get();
   }

   public static class BooleanValue extends GameRules.RuleValue<GameRules.BooleanValue> {
      private boolean value;

      private static GameRules.RuleType<GameRules.BooleanValue> create(boolean p_223567_0_, BiConsumer<MinecraftServer, GameRules.BooleanValue> p_223567_1_) {
         return new GameRules.RuleType<>(BoolArgumentType::bool, (p_223574_1_) -> {
            return new GameRules.BooleanValue(p_223574_1_, p_223567_0_);
         }, p_223567_1_);
      }

      private static GameRules.RuleType<GameRules.BooleanValue> create(boolean p_223568_0_) {
         return create(p_223568_0_, (p_223569_0_, p_223569_1_) -> {
         });
      }

      public BooleanValue(GameRules.RuleType<GameRules.BooleanValue> p_i51535_1_, boolean p_i51535_2_) {
         super(p_i51535_1_);
         this.value = p_i51535_2_;
      }

      protected void updateFromArgument(CommandContext<CommandSource> p_223555_1_, String p_223555_2_) {
         this.value = BoolArgumentType.getBool(p_223555_1_, p_223555_2_);
      }

      public boolean get() {
         return this.value;
      }

      public void set(boolean p_223570_1_, @Nullable MinecraftServer p_223570_2_) {
         this.value = p_223570_1_;
         this.onChanged(p_223570_2_);
      }

      protected String serialize() {
         return Boolean.toString(this.value);
      }

      protected void deserialize(String p_223553_1_) {
         this.value = Boolean.parseBoolean(p_223553_1_);
      }

      public int getCommandResult() {
         return this.value ? 1 : 0;
      }

      protected GameRules.BooleanValue getSelf() {
         return this;
      }
   }

   @FunctionalInterface
   public interface IRuleEntryVisitor {
      <T extends GameRules.RuleValue<T>> void visit(GameRules.RuleKey<T> p_223481_1_, GameRules.RuleType<T> p_223481_2_);
   }

   public static class IntegerValue extends GameRules.RuleValue<GameRules.IntegerValue> {
      private int value;

      private static GameRules.RuleType<GameRules.IntegerValue> create(int p_223564_0_, BiConsumer<MinecraftServer, GameRules.IntegerValue> p_223564_1_) {
         return new GameRules.RuleType<>(IntegerArgumentType::integer, (p_223565_1_) -> {
            return new GameRules.IntegerValue(p_223565_1_, p_223564_0_);
         }, p_223564_1_);
      }

      private static GameRules.RuleType<GameRules.IntegerValue> create(int p_223559_0_) {
         return create(p_223559_0_, (p_223561_0_, p_223561_1_) -> {
         });
      }

      public IntegerValue(GameRules.RuleType<GameRules.IntegerValue> p_i51534_1_, int p_i51534_2_) {
         super(p_i51534_1_);
         this.value = p_i51534_2_;
      }

      protected void updateFromArgument(CommandContext<CommandSource> p_223555_1_, String p_223555_2_) {
         this.value = IntegerArgumentType.getInteger(p_223555_1_, p_223555_2_);
      }

      public int get() {
         return this.value;
      }

      protected String serialize() {
         return Integer.toString(this.value);
      }

      protected void deserialize(String p_223553_1_) {
         this.value = safeParse(p_223553_1_);
      }

      private static int safeParse(String p_223563_0_) {
         if (!p_223563_0_.isEmpty()) {
            try {
               return Integer.parseInt(p_223563_0_);
            } catch (NumberFormatException var2) {
               GameRules.LOGGER.warn("Failed to parse integer {}", (Object)p_223563_0_);
            }
         }

         return 0;
      }

      public int getCommandResult() {
         return this.value;
      }

      protected GameRules.IntegerValue getSelf() {
         return this;
      }
   }

   public static final class RuleKey<T extends GameRules.RuleValue<T>> {
      private final String id;

      public RuleKey(String p_i51533_1_) {
         this.id = p_i51533_1_;
      }

      public String toString() {
         return this.id;
      }

      public boolean equals(Object p_equals_1_) {
         if (this == p_equals_1_) {
            return true;
         } else {
            return p_equals_1_ instanceof GameRules.RuleKey && ((GameRules.RuleKey)p_equals_1_).id.equals(this.id);
         }
      }

      public int hashCode() {
         return this.id.hashCode();
      }

      public String getId() {
         return this.id;
      }
   }

   public static class RuleType<T extends GameRules.RuleValue<T>> {
      private final Supplier<ArgumentType<?>> argument;
      private final Function<GameRules.RuleType<T>, T> constructor;
      private final BiConsumer<MinecraftServer, T> callback;

      private RuleType(Supplier<ArgumentType<?>> p_i51531_1_, Function<GameRules.RuleType<T>, T> p_i51531_2_, BiConsumer<MinecraftServer, T> p_i51531_3_) {
         this.argument = p_i51531_1_;
         this.constructor = p_i51531_2_;
         this.callback = p_i51531_3_;
      }

      public RequiredArgumentBuilder<CommandSource, ?> createArgument(String p_223581_1_) {
         return Commands.argument(p_223581_1_, this.argument.get());
      }

      public T createRule() {
         return (T)(this.constructor.apply(this));
      }
   }

   public abstract static class RuleValue<T extends GameRules.RuleValue<T>> {
      private final GameRules.RuleType<T> type;

      public RuleValue(GameRules.RuleType<T> p_i51530_1_) {
         this.type = p_i51530_1_;
      }

      protected abstract void updateFromArgument(CommandContext<CommandSource> p_223555_1_, String p_223555_2_);

      public void setFromArgument(CommandContext<CommandSource> p_223554_1_, String p_223554_2_) {
         this.updateFromArgument(p_223554_1_, p_223554_2_);
         this.onChanged(p_223554_1_.getSource().getServer());
      }

      protected void onChanged(@Nullable MinecraftServer p_223556_1_) {
         if (p_223556_1_ != null) {
            this.type.callback.accept(p_223556_1_, (T)this.getSelf());
         }

      }

      protected abstract void deserialize(String p_223553_1_);

      protected abstract String serialize();

      public String toString() {
         return this.serialize();
      }

      public abstract int getCommandResult();

      protected abstract T getSelf();
   }
}