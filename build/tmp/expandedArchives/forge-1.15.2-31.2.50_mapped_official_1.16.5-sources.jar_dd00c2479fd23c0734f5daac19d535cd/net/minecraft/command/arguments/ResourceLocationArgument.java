package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.Advancement;
import net.minecraft.command.CommandSource;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.storage.loot.LootPredicateManager;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class ResourceLocationArgument implements ArgumentType<ResourceLocation> {
   private static final Collection<String> EXAMPLES = Arrays.asList("foo", "foo:bar", "012");
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_ADVANCEMENT = new DynamicCommandExceptionType((p_208676_0_) -> {
      return new TranslationTextComponent("advancement.advancementNotFound", p_208676_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_RECIPE = new DynamicCommandExceptionType((p_208677_0_) -> {
      return new TranslationTextComponent("recipe.notFound", p_208677_0_);
   });
   private static final DynamicCommandExceptionType ERROR_UNKNOWN_PREDICATE = new DynamicCommandExceptionType((p_208674_0_) -> {
      return new TranslationTextComponent("predicate.unknown", p_208674_0_);
   });

   public static ResourceLocationArgument id() {
      return new ResourceLocationArgument();
   }

   public static Advancement getAdvancement(CommandContext<CommandSource> p_197198_0_, String p_197198_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_197198_0_.getArgument(p_197198_1_, ResourceLocation.class);
      Advancement advancement = p_197198_0_.getSource().getServer().getAdvancements().getAdvancement(resourcelocation);
      if (advancement == null) {
         throw ERROR_UNKNOWN_ADVANCEMENT.create(resourcelocation);
      } else {
         return advancement;
      }
   }

   public static IRecipe<?> getRecipe(CommandContext<CommandSource> p_197194_0_, String p_197194_1_) throws CommandSyntaxException {
      RecipeManager recipemanager = p_197194_0_.getSource().getServer().getRecipeManager();
      ResourceLocation resourcelocation = p_197194_0_.getArgument(p_197194_1_, ResourceLocation.class);
      return recipemanager.byKey(resourcelocation).orElseThrow(() -> {
         return ERROR_UNKNOWN_RECIPE.create(resourcelocation);
      });
   }

   public static ILootCondition getPredicate(CommandContext<CommandSource> p_228259_0_, String p_228259_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = p_228259_0_.getArgument(p_228259_1_, ResourceLocation.class);
      LootPredicateManager lootpredicatemanager = p_228259_0_.getSource().getServer().getPredicateManager();
      ILootCondition ilootcondition = lootpredicatemanager.get(resourcelocation);
      if (ilootcondition == null) {
         throw ERROR_UNKNOWN_PREDICATE.create(resourcelocation);
      } else {
         return ilootcondition;
      }
   }

   public static ResourceLocation getId(CommandContext<CommandSource> p_197195_0_, String p_197195_1_) {
      return p_197195_0_.getArgument(p_197195_1_, ResourceLocation.class);
   }

   public ResourceLocation parse(StringReader p_parse_1_) throws CommandSyntaxException {
      return ResourceLocation.read(p_parse_1_);
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}