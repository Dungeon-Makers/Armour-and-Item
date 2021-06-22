package net.minecraft.command.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;

public class DimensionArgument implements ArgumentType<DimensionType> {
   private static final Collection<String> field_212597_b = Stream.of(DimensionType.field_223227_a_, DimensionType.field_223228_b_).map((p_212593_0_) -> {
      return DimensionType.func_212678_a(p_212593_0_).toString();
   }).collect(Collectors.toList());
   public static final DynamicCommandExceptionType field_212596_a = new DynamicCommandExceptionType((p_212594_0_) -> {
      return new TranslationTextComponent("argument.dimension.invalid", p_212594_0_);
   });

   public DimensionType parse(StringReader p_parse_1_) throws CommandSyntaxException {
      ResourceLocation resourcelocation = ResourceLocation.read(p_parse_1_);
      return Registry.field_212622_k.func_218349_b(resourcelocation).orElseThrow(() -> {
         return field_212596_a.create(resourcelocation);
      });
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      return ISuggestionProvider.suggestResource(Streams.stream(DimensionType.func_212681_b()).map(DimensionType::func_212678_a), p_listSuggestions_2_);
   }

   public Collection<String> getExamples() {
      return field_212597_b;
   }

   public static DimensionArgument dimension() {
      return new DimensionArgument();
   }

   public static DimensionType getDimension(CommandContext<CommandSource> p_212592_0_, String p_212592_1_) {
      return p_212592_0_.getArgument(p_212592_1_, DimensionType.class);
   }
}