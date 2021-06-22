package net.minecraft.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;

public class Vec2Argument implements ArgumentType<ILocationArgument> {
   private static final Collection<String> EXAMPLES = Arrays.asList("0 0", "~ ~", "0.1 -0.5", "~1 ~-2");
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslationTextComponent("argument.pos2d.incomplete"));
   private final boolean centerCorrect;

   public Vec2Argument(boolean p_i47965_1_) {
      this.centerCorrect = p_i47965_1_;
   }

   public static Vec2Argument vec2() {
      return new Vec2Argument(true);
   }

   public static Vec2f getVec2(CommandContext<CommandSource> p_197295_0_, String p_197295_1_) throws CommandSyntaxException {
      Vec3d vec3d = p_197295_0_.getArgument(p_197295_1_, ILocationArgument.class).getPosition(p_197295_0_.getSource());
      return new Vec2f((float)vec3d.x, (float)vec3d.z);
   }

   public ILocationArgument parse(StringReader p_parse_1_) throws CommandSyntaxException {
      int i = p_parse_1_.getCursor();
      if (!p_parse_1_.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(p_parse_1_);
      } else {
         LocationPart locationpart = LocationPart.parseDouble(p_parse_1_, this.centerCorrect);
         if (p_parse_1_.canRead() && p_parse_1_.peek() == ' ') {
            p_parse_1_.skip();
            LocationPart locationpart1 = LocationPart.parseDouble(p_parse_1_, this.centerCorrect);
            return new LocationInput(locationpart, new LocationPart(true, 0.0D), locationpart1);
         } else {
            p_parse_1_.setCursor(i);
            throw ERROR_NOT_COMPLETE.createWithContext(p_parse_1_);
         }
      }
   }

   public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> p_listSuggestions_1_, SuggestionsBuilder p_listSuggestions_2_) {
      if (!(p_listSuggestions_1_.getSource() instanceof ISuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String s = p_listSuggestions_2_.getRemaining();
         Collection<ISuggestionProvider.Coordinates> collection;
         if (!s.isEmpty() && s.charAt(0) == '^') {
            collection = Collections.singleton(ISuggestionProvider.Coordinates.DEFAULT_LOCAL);
         } else {
            collection = ((ISuggestionProvider)p_listSuggestions_1_.getSource()).getAbsoluteCoordinates();
         }

         return ISuggestionProvider.suggest2DCoordinates(s, collection, p_listSuggestions_2_, Commands.createValidator(this::parse));
      }
   }

   public Collection<String> getExamples() {
      return EXAMPLES;
   }
}