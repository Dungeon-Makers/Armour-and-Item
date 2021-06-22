package net.minecraft.command.arguments;

import com.google.common.collect.Maps;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic3CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.state.IProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.TranslationTextComponent;

public class BlockStateParser {
   public static final SimpleCommandExceptionType ERROR_NO_TAGS_ALLOWED = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.tag.disallowed"));
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_BLOCK = new DynamicCommandExceptionType((p_208687_0_) -> {
      return new TranslationTextComponent("argument.block.id.invalid", p_208687_0_);
   });
   public static final Dynamic2CommandExceptionType ERROR_UNKNOWN_PROPERTY = new Dynamic2CommandExceptionType((p_208685_0_, p_208685_1_) -> {
      return new TranslationTextComponent("argument.block.property.unknown", p_208685_0_, p_208685_1_);
   });
   public static final Dynamic2CommandExceptionType ERROR_DUPLICATE_PROPERTY = new Dynamic2CommandExceptionType((p_208690_0_, p_208690_1_) -> {
      return new TranslationTextComponent("argument.block.property.duplicate", p_208690_1_, p_208690_0_);
   });
   public static final Dynamic3CommandExceptionType ERROR_INVALID_VALUE = new Dynamic3CommandExceptionType((p_208684_0_, p_208684_1_, p_208684_2_) -> {
      return new TranslationTextComponent("argument.block.property.invalid", p_208684_0_, p_208684_2_, p_208684_1_);
   });
   public static final Dynamic2CommandExceptionType ERROR_EXPECTED_VALUE = new Dynamic2CommandExceptionType((p_208689_0_, p_208689_1_) -> {
      return new TranslationTextComponent("argument.block.property.novalue", p_208689_0_, p_208689_1_);
   });
   public static final SimpleCommandExceptionType ERROR_EXPECTED_END_OF_PROPERTIES = new SimpleCommandExceptionType(new TranslationTextComponent("argument.block.property.unclosed"));
   private static final Function<SuggestionsBuilder, CompletableFuture<Suggestions>> SUGGEST_NOTHING = SuggestionsBuilder::buildFuture;
   private final StringReader reader;
   private final boolean forTesting;
   private final Map<IProperty<?>, Comparable<?>> properties = Maps.newHashMap();
   private final Map<String, String> vagueProperties = Maps.newHashMap();
   private ResourceLocation id = new ResourceLocation("");
   private StateContainer<Block, BlockState> definition;
   private BlockState state;
   @Nullable
   private CompoundNBT nbt;
   private ResourceLocation tag = new ResourceLocation("");
   private int tagCursor;
   private Function<SuggestionsBuilder, CompletableFuture<Suggestions>> suggestions = SUGGEST_NOTHING;

   public BlockStateParser(StringReader p_i48214_1_, boolean p_i48214_2_) {
      this.reader = p_i48214_1_;
      this.forTesting = p_i48214_2_;
   }

   public Map<IProperty<?>, Comparable<?>> getProperties() {
      return this.properties;
   }

   @Nullable
   public BlockState getState() {
      return this.state;
   }

   @Nullable
   public CompoundNBT getNbt() {
      return this.nbt;
   }

   @Nullable
   public ResourceLocation getTag() {
      return this.tag;
   }

   public BlockStateParser parse(boolean p_197243_1_) throws CommandSyntaxException {
      this.suggestions = this::suggestBlockIdOrTag;
      if (this.reader.canRead() && this.reader.peek() == '#') {
         this.readTag();
         this.suggestions = this::suggestOpenVaguePropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readVagueProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      } else {
         this.readBlock();
         this.suggestions = this::suggestOpenPropertiesOrNbt;
         if (this.reader.canRead() && this.reader.peek() == '[') {
            this.readProperties();
            this.suggestions = this::suggestOpenNbt;
         }
      }

      if (p_197243_1_ && this.reader.canRead() && this.reader.peek() == '{') {
         this.suggestions = SUGGEST_NOTHING;
         this.readNbt();
      }

      return this;
   }

   private CompletableFuture<Suggestions> suggestPropertyNameOrEnd(SuggestionsBuilder p_197252_1_) {
      if (p_197252_1_.getRemaining().isEmpty()) {
         p_197252_1_.suggest(String.valueOf(']'));
      }

      return this.suggestPropertyName(p_197252_1_);
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyNameOrEnd(SuggestionsBuilder p_200136_1_) {
      if (p_200136_1_.getRemaining().isEmpty()) {
         p_200136_1_.suggest(String.valueOf(']'));
      }

      return this.suggestVaguePropertyName(p_200136_1_);
   }

   private CompletableFuture<Suggestions> suggestPropertyName(SuggestionsBuilder p_197256_1_) {
      String s = p_197256_1_.getRemaining().toLowerCase(Locale.ROOT);

      for(IProperty<?> iproperty : this.state.func_206869_a()) {
         if (!this.properties.containsKey(iproperty) && iproperty.getName().startsWith(s)) {
            p_197256_1_.suggest(iproperty.getName() + '=');
         }
      }

      return p_197256_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestVaguePropertyName(SuggestionsBuilder p_200134_1_) {
      String s = p_200134_1_.getRemaining().toLowerCase(Locale.ROOT);
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getAllTags().getTag(this.tag);
         if (tag != null) {
            for(Block block : tag.func_199885_a()) {
               for(IProperty<?> iproperty : block.getStateDefinition().getProperties()) {
                  if (!this.vagueProperties.containsKey(iproperty.getName()) && iproperty.getName().startsWith(s)) {
                     p_200134_1_.suggest(iproperty.getName() + '=');
                  }
               }
            }
         }
      }

      return p_200134_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenNbt(SuggestionsBuilder p_197244_1_) {
      if (p_197244_1_.getRemaining().isEmpty() && this.hasBlockEntity()) {
         p_197244_1_.suggest(String.valueOf('{'));
      }

      return p_197244_1_.buildFuture();
   }

   private boolean hasBlockEntity() {
      if (this.state != null) {
         return this.state.hasTileEntity();
      } else {
         if (this.tag != null) {
            Tag<Block> tag = BlockTags.getAllTags().getTag(this.tag);
            if (tag != null) {
               for(Block block : tag.func_199885_a()) {
                  if (block.defaultBlockState().hasTileEntity()) {
                     return true;
                  }
               }
            }
         }

         return false;
      }
   }

   private CompletableFuture<Suggestions> suggestEquals(SuggestionsBuilder p_197246_1_) {
      if (p_197246_1_.getRemaining().isEmpty()) {
         p_197246_1_.suggest(String.valueOf('='));
      }

      return p_197246_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestNextPropertyOrEnd(SuggestionsBuilder p_197248_1_) {
      if (p_197248_1_.getRemaining().isEmpty()) {
         p_197248_1_.suggest(String.valueOf(']'));
      }

      if (p_197248_1_.getRemaining().isEmpty() && this.properties.size() < this.state.func_206869_a().size()) {
         p_197248_1_.suggest(String.valueOf(','));
      }

      return p_197248_1_.buildFuture();
   }

   private static <T extends Comparable<T>> SuggestionsBuilder addSuggestions(SuggestionsBuilder p_201037_0_, IProperty<T> p_201037_1_) {
      for(T t : p_201037_1_.getPossibleValues()) {
         if (t instanceof Integer) {
            p_201037_0_.suggest((Integer)t);
         } else {
            p_201037_0_.suggest(p_201037_1_.getName(t));
         }
      }

      return p_201037_0_;
   }

   private CompletableFuture<Suggestions> func_200140_a(SuggestionsBuilder p_200140_1_, String p_200140_2_) {
      boolean flag = false;
      if (this.tag != null && !this.tag.getPath().isEmpty()) {
         Tag<Block> tag = BlockTags.getAllTags().getTag(this.tag);
         if (tag != null) {
            label40:
            for(Block block : tag.func_199885_a()) {
               IProperty<?> iproperty = block.getStateDefinition().getProperty(p_200140_2_);
               if (iproperty != null) {
                  addSuggestions(p_200140_1_, iproperty);
               }

               if (!flag) {
                  Iterator iterator = block.getStateDefinition().getProperties().iterator();

                  while(true) {
                     if (!iterator.hasNext()) {
                        continue label40;
                     }

                     IProperty<?> iproperty1 = (IProperty)iterator.next();
                     if (!this.vagueProperties.containsKey(iproperty1.getName())) {
                        break;
                     }
                  }

                  flag = true;
               }
            }
         }
      }

      if (flag) {
         p_200140_1_.suggest(String.valueOf(','));
      }

      p_200140_1_.suggest(String.valueOf(']'));
      return p_200140_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestOpenVaguePropertiesOrNbt(SuggestionsBuilder p_212599_1_) {
      if (p_212599_1_.getRemaining().isEmpty()) {
         Tag<Block> tag = BlockTags.getAllTags().getTag(this.tag);
         if (tag != null) {
            boolean flag = false;
            boolean flag1 = false;

            for(Block block : tag.func_199885_a()) {
               flag |= !block.getStateDefinition().getProperties().isEmpty();
               flag1 |= block.func_149716_u();
               if (flag && flag1) {
                  break;
               }
            }

            if (flag) {
               p_212599_1_.suggest(String.valueOf('['));
            }

            if (flag1) {
               p_212599_1_.suggest(String.valueOf('{'));
            }
         }
      }

      return this.suggestTag(p_212599_1_);
   }

   private CompletableFuture<Suggestions> suggestOpenPropertiesOrNbt(SuggestionsBuilder p_197255_1_) {
      if (p_197255_1_.getRemaining().isEmpty()) {
         if (!this.state.getBlock().getStateDefinition().getProperties().isEmpty()) {
            p_197255_1_.suggest(String.valueOf('['));
         }

         if (this.state.hasTileEntity()) {
            p_197255_1_.suggest(String.valueOf('{'));
         }
      }

      return p_197255_1_.buildFuture();
   }

   private CompletableFuture<Suggestions> suggestTag(SuggestionsBuilder p_201953_1_) {
      return ISuggestionProvider.suggestResource(BlockTags.getAllTags().getAvailableTags(), p_201953_1_.createOffset(this.tagCursor).add(p_201953_1_));
   }

   private CompletableFuture<Suggestions> suggestBlockIdOrTag(SuggestionsBuilder p_197250_1_) {
      if (this.forTesting) {
         ISuggestionProvider.suggestResource(BlockTags.getAllTags().getAvailableTags(), p_197250_1_, String.valueOf('#'));
      }

      ISuggestionProvider.suggestResource(Registry.BLOCK.keySet(), p_197250_1_);
      return p_197250_1_.buildFuture();
   }

   public void readBlock() throws CommandSyntaxException {
      int i = this.reader.getCursor();
      this.id = ResourceLocation.read(this.reader);
      Block block = Registry.BLOCK.func_218349_b(this.id).orElseThrow(() -> {
         this.reader.setCursor(i);
         return ERROR_UNKNOWN_BLOCK.createWithContext(this.reader, this.id.toString());
      });
      this.definition = block.getStateDefinition();
      this.state = block.defaultBlockState();
   }

   public void readTag() throws CommandSyntaxException {
      if (!this.forTesting) {
         throw ERROR_NO_TAGS_ALLOWED.create();
      } else {
         this.suggestions = this::suggestTag;
         this.reader.expect('#');
         this.tagCursor = this.reader.getCursor();
         this.tag = ResourceLocation.read(this.reader);
      }
   }

   public void readProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestPropertyNameOrEnd;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int i = this.reader.getCursor();
            String s = this.reader.readString();
            IProperty<?> iproperty = this.definition.getProperty(s);
            if (iproperty == null) {
               this.reader.setCursor(i);
               throw ERROR_UNKNOWN_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            if (this.properties.containsKey(iproperty)) {
               this.reader.setCursor(i);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skipWhitespace();
            this.suggestions = this::suggestEquals;
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (p_197251_1_) -> {
               return addSuggestions(p_197251_1_, iproperty).buildFuture();
            };
            int j = this.reader.getCursor();
            this.setValue(iproperty, this.reader.readString(), j);
            this.suggestions = this::suggestNextPropertyOrEnd;
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestPropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readVagueProperties() throws CommandSyntaxException {
      this.reader.skip();
      this.suggestions = this::suggestVaguePropertyNameOrEnd;
      int i = -1;
      this.reader.skipWhitespace();

      while(true) {
         if (this.reader.canRead() && this.reader.peek() != ']') {
            this.reader.skipWhitespace();
            int j = this.reader.getCursor();
            String s = this.reader.readString();
            if (this.vagueProperties.containsKey(s)) {
               this.reader.setCursor(j);
               throw ERROR_DUPLICATE_PROPERTY.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skipWhitespace();
            if (!this.reader.canRead() || this.reader.peek() != '=') {
               this.reader.setCursor(j);
               throw ERROR_EXPECTED_VALUE.createWithContext(this.reader, this.id.toString(), s);
            }

            this.reader.skip();
            this.reader.skipWhitespace();
            this.suggestions = (p_200138_2_) -> {
               return this.func_200140_a(p_200138_2_, s);
            };
            i = this.reader.getCursor();
            String s1 = this.reader.readString();
            this.vagueProperties.put(s, s1);
            this.reader.skipWhitespace();
            if (!this.reader.canRead()) {
               continue;
            }

            i = -1;
            if (this.reader.peek() == ',') {
               this.reader.skip();
               this.suggestions = this::suggestVaguePropertyName;
               continue;
            }

            if (this.reader.peek() != ']') {
               throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
            }
         }

         if (this.reader.canRead()) {
            this.reader.skip();
            return;
         }

         if (i >= 0) {
            this.reader.setCursor(i);
         }

         throw ERROR_EXPECTED_END_OF_PROPERTIES.createWithContext(this.reader);
      }
   }

   public void readNbt() throws CommandSyntaxException {
      this.nbt = (new JsonToNBT(this.reader)).readStruct();
   }

   private <T extends Comparable<T>> void setValue(IProperty<T> p_197253_1_, String p_197253_2_, int p_197253_3_) throws CommandSyntaxException {
      Optional<T> optional = p_197253_1_.getValue(p_197253_2_);
      if (optional.isPresent()) {
         this.state = this.state.setValue(p_197253_1_, (T)(optional.get()));
         this.properties.put(p_197253_1_, optional.get());
      } else {
         this.reader.setCursor(p_197253_3_);
         throw ERROR_INVALID_VALUE.createWithContext(this.reader, this.id.toString(), p_197253_1_.getName(), p_197253_2_);
      }
   }

   public static String serialize(BlockState p_197247_0_) {
      StringBuilder stringbuilder = new StringBuilder(Registry.BLOCK.getKey(p_197247_0_.getBlock()).toString());
      if (!p_197247_0_.func_206869_a().isEmpty()) {
         stringbuilder.append('[');
         boolean flag = false;

         for(Entry<IProperty<?>, Comparable<?>> entry : p_197247_0_.getValues().entrySet()) {
            if (flag) {
               stringbuilder.append(',');
            }

            appendProperty(stringbuilder, entry.getKey(), entry.getValue());
            flag = true;
         }

         stringbuilder.append(']');
      }

      return stringbuilder.toString();
   }

   private static <T extends Comparable<T>> void appendProperty(StringBuilder p_211375_0_, IProperty<T> p_211375_1_, Comparable<?> p_211375_2_) {
      p_211375_0_.append(p_211375_1_.getName());
      p_211375_0_.append('=');
      p_211375_0_.append(p_211375_1_.getName((T)p_211375_2_));
   }

   public CompletableFuture<Suggestions> fillSuggestions(SuggestionsBuilder p_197245_1_) {
      return this.suggestions.apply(p_197245_1_.createOffset(this.reader.getCursor()));
   }

   public Map<String, String> getVagueProperties() {
      return this.vagueProperties;
   }
}
