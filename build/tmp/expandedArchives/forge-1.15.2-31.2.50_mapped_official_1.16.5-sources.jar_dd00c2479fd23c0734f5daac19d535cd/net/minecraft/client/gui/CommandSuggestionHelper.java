package net.minecraft.client.gui;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.context.ParsedArgument;
import com.mojang.brigadier.context.SuggestionContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.Commands;
import net.minecraft.command.ISuggestionProvider;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CommandSuggestionHelper {
   private static final Pattern WHITESPACE_PATTERN = Pattern.compile("(\\s+)");
   private final Minecraft minecraft;
   private final Screen screen;
   private final TextFieldWidget input;
   private final FontRenderer font;
   private final boolean commandsOnly;
   private final boolean onlyShowIfCursorPastError;
   private final int lineStartOffset;
   private final int suggestionLineLimit;
   private final boolean anchorToBottom;
   private final int fillColor;
   private final List<String> commandUsage = Lists.newArrayList();
   private int commandUsagePosition;
   private int commandUsageWidth;
   private ParseResults<ISuggestionProvider> currentParse;
   private CompletableFuture<com.mojang.brigadier.suggestion.Suggestions> pendingSuggestions;
   private CommandSuggestionHelper.Suggestions suggestions;
   private boolean allowSuggestions;
   private boolean keepSuggestions;

   public CommandSuggestionHelper(Minecraft p_i225919_1_, Screen p_i225919_2_, TextFieldWidget p_i225919_3_, FontRenderer p_i225919_4_, boolean p_i225919_5_, boolean p_i225919_6_, int p_i225919_7_, int p_i225919_8_, boolean p_i225919_9_, int p_i225919_10_) {
      this.minecraft = p_i225919_1_;
      this.screen = p_i225919_2_;
      this.input = p_i225919_3_;
      this.font = p_i225919_4_;
      this.commandsOnly = p_i225919_5_;
      this.onlyShowIfCursorPastError = p_i225919_6_;
      this.lineStartOffset = p_i225919_7_;
      this.suggestionLineLimit = p_i225919_8_;
      this.anchorToBottom = p_i225919_9_;
      this.fillColor = p_i225919_10_;
      p_i225919_3_.setFormatter(this::formatChat);
   }

   public void setAllowSuggestions(boolean p_228124_1_) {
      this.allowSuggestions = p_228124_1_;
      if (!p_228124_1_) {
         this.suggestions = null;
      }

   }

   public boolean keyPressed(int p_228115_1_, int p_228115_2_, int p_228115_3_) {
      if (this.suggestions != null && this.suggestions.keyPressed(p_228115_1_, p_228115_2_, p_228115_3_)) {
         return true;
      } else if (this.screen.getFocused() == this.input && p_228115_1_ == 258) {
         this.showSuggestions(true);
         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double p_228112_1_) {
      return this.suggestions != null && this.suggestions.mouseScrolled(MathHelper.clamp(p_228112_1_, -1.0D, 1.0D));
   }

   public boolean mouseClicked(double p_228113_1_, double p_228113_3_, int p_228113_5_) {
      return this.suggestions != null && this.suggestions.mouseClicked((int)p_228113_1_, (int)p_228113_3_, p_228113_5_);
   }

   public void showSuggestions(boolean p_228128_1_) {
      if (this.pendingSuggestions != null && this.pendingSuggestions.isDone()) {
         com.mojang.brigadier.suggestion.Suggestions suggestions = this.pendingSuggestions.join();
         if (!suggestions.isEmpty()) {
            int i = 0;

            for(Suggestion suggestion : suggestions.getList()) {
               i = Math.max(i, this.font.width(suggestion.getText()));
            }

            int j = MathHelper.clamp(this.input.getScreenX(suggestions.getRange().getStart()), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
            int k = this.anchorToBottom ? this.screen.height - 12 : 72;
            this.suggestions = new CommandSuggestionHelper.Suggestions(j, k, i, suggestions, p_228128_1_);
         }
      }

   }

   public void updateCommandInfo() {
      String s = this.input.getValue();
      if (this.currentParse != null && !this.currentParse.getReader().getString().equals(s)) {
         this.currentParse = null;
      }

      if (!this.keepSuggestions) {
         this.input.setSuggestion((String)null);
         this.suggestions = null;
      }

      this.commandUsage.clear();
      StringReader stringreader = new StringReader(s);
      boolean flag = stringreader.canRead() && stringreader.peek() == '/';
      if (flag) {
         stringreader.skip();
      }

      boolean flag1 = this.commandsOnly || flag;
      int i = this.input.getCursorPosition();
      if (flag1) {
         CommandDispatcher<ISuggestionProvider> commanddispatcher = this.minecraft.player.connection.getCommands();
         if (this.currentParse == null) {
            this.currentParse = commanddispatcher.parse(stringreader, this.minecraft.player.connection.getSuggestionsProvider());
         }

         int j = this.onlyShowIfCursorPastError ? stringreader.getCursor() : 1;
         if (i >= j && (this.suggestions == null || !this.keepSuggestions)) {
            this.pendingSuggestions = commanddispatcher.getCompletionSuggestions(this.currentParse, i);
            this.pendingSuggestions.thenRun(() -> {
               if (this.pendingSuggestions.isDone()) {
                  this.updateUsageInfo();
               }
            });
         }
      } else {
         String s1 = s.substring(0, i);
         int k = getLastWordIndex(s1);
         Collection<String> collection = this.minecraft.player.connection.getSuggestionsProvider().getOnlinePlayerNames();
         this.pendingSuggestions = ISuggestionProvider.suggest(collection, new SuggestionsBuilder(s1, k));
      }

   }

   private static int getLastWordIndex(String p_228121_0_) {
      if (Strings.isNullOrEmpty(p_228121_0_)) {
         return 0;
      } else {
         int i = 0;

         for(Matcher matcher = WHITESPACE_PATTERN.matcher(p_228121_0_); matcher.find(); i = matcher.end()) {
            ;
         }

         return i;
      }
   }

   public void updateUsageInfo() {
      if (this.input.getCursorPosition() == this.input.getValue().length()) {
         if (this.pendingSuggestions.join().isEmpty() && !this.currentParse.getExceptions().isEmpty()) {
            int i = 0;

            for(Entry<CommandNode<ISuggestionProvider>, CommandSyntaxException> entry : this.currentParse.getExceptions().entrySet()) {
               CommandSyntaxException commandsyntaxexception = entry.getValue();
               if (commandsyntaxexception.getType() == CommandSyntaxException.BUILT_IN_EXCEPTIONS.literalIncorrect()) {
                  ++i;
               } else {
                  this.commandUsage.add(commandsyntaxexception.getMessage());
               }
            }

            if (i > 0) {
               this.commandUsage.add(CommandSyntaxException.BUILT_IN_EXCEPTIONS.dispatcherUnknownCommand().create().getMessage());
            }
         } else if (this.currentParse.getReader().canRead()) {
            this.commandUsage.add(Commands.getParseException(this.currentParse).getMessage());
         }
      }

      this.commandUsagePosition = 0;
      this.commandUsageWidth = this.screen.width;
      if (this.commandUsage.isEmpty()) {
         this.fillNodeUsage(TextFormatting.GRAY);
      }

      this.suggestions = null;
      if (this.allowSuggestions && this.minecraft.options.autoSuggestions) {
         this.showSuggestions(false);
      }

   }

   private void fillNodeUsage(TextFormatting p_228120_1_) {
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = this.currentParse.getContext();
      SuggestionContext<ISuggestionProvider> suggestioncontext = commandcontextbuilder.findSuggestionContext(this.input.getCursorPosition());
      Map<CommandNode<ISuggestionProvider>, String> map = this.minecraft.player.connection.getCommands().getSmartUsage(suggestioncontext.parent, this.minecraft.player.connection.getSuggestionsProvider());
      List<String> list = Lists.newArrayList();
      int i = 0;

      for(Entry<CommandNode<ISuggestionProvider>, String> entry : map.entrySet()) {
         if (!(entry.getKey() instanceof LiteralCommandNode)) {
            list.add(p_228120_1_ + (String)entry.getValue());
            i = Math.max(i, this.font.width(entry.getValue()));
         }
      }

      if (!list.isEmpty()) {
         this.commandUsage.addAll(list);
         this.commandUsagePosition = MathHelper.clamp(this.input.getScreenX(suggestioncontext.startPos), 0, this.input.getScreenX(0) + this.input.getInnerWidth() - i);
         this.commandUsageWidth = i;
      }

   }

   private String formatChat(String p_228122_1_, int p_228122_2_) {
      return this.currentParse != null ? formatText(this.currentParse, p_228122_1_, p_228122_2_) : p_228122_1_;
   }

   @Nullable
   private static String calculateSuggestionSuffix(String p_228127_0_, String p_228127_1_) {
      return p_228127_1_.startsWith(p_228127_0_) ? p_228127_1_.substring(p_228127_0_.length()) : null;
   }

   public static String formatText(ParseResults<ISuggestionProvider> p_228116_0_, String p_228116_1_, int p_228116_2_) {
      TextFormatting[] atextformatting = new TextFormatting[]{TextFormatting.AQUA, TextFormatting.YELLOW, TextFormatting.GREEN, TextFormatting.LIGHT_PURPLE, TextFormatting.GOLD};
      String s = TextFormatting.GRAY.toString();
      StringBuilder stringbuilder = new StringBuilder(s);
      int i = 0;
      int j = -1;
      CommandContextBuilder<ISuggestionProvider> commandcontextbuilder = p_228116_0_.getContext().getLastChild();

      for(ParsedArgument<ISuggestionProvider, ?> parsedargument : commandcontextbuilder.getArguments().values()) {
         ++j;
         if (j >= atextformatting.length) {
            j = 0;
         }

         int k = Math.max(parsedargument.getRange().getStart() - p_228116_2_, 0);
         if (k >= p_228116_1_.length()) {
            break;
         }

         int l = Math.min(parsedargument.getRange().getEnd() - p_228116_2_, p_228116_1_.length());
         if (l > 0) {
            stringbuilder.append((CharSequence)p_228116_1_, i, k);
            stringbuilder.append((Object)atextformatting[j]);
            stringbuilder.append((CharSequence)p_228116_1_, k, l);
            stringbuilder.append(s);
            i = l;
         }
      }

      if (p_228116_0_.getReader().canRead()) {
         int i1 = Math.max(p_228116_0_.getReader().getCursor() - p_228116_2_, 0);
         if (i1 < p_228116_1_.length()) {
            int j1 = Math.min(i1 + p_228116_0_.getReader().getRemainingLength(), p_228116_1_.length());
            stringbuilder.append((CharSequence)p_228116_1_, i, i1);
            stringbuilder.append((Object)TextFormatting.RED);
            stringbuilder.append((CharSequence)p_228116_1_, i1, j1);
            i = j1;
         }
      }

      stringbuilder.append((CharSequence)p_228116_1_, i, p_228116_1_.length());
      return stringbuilder.toString();
   }

   public void func_228114_a_(int p_228114_1_, int p_228114_2_) {
      if (this.suggestions != null) {
         this.suggestions.func_228149_a_(p_228114_1_, p_228114_2_);
      } else {
         int i = 0;

         for(String s : this.commandUsage) {
            int j = this.anchorToBottom ? this.screen.height - 14 - 13 - 12 * i : 72 + 12 * i;
            AbstractGui.fill(this.commandUsagePosition - 1, j, this.commandUsagePosition + this.commandUsageWidth + 1, j + 12, this.fillColor);
            this.font.func_175063_a(s, (float)this.commandUsagePosition, (float)(j + 2), -1);
            ++i;
         }
      }

   }

   public String getNarrationMessage() {
      return this.suggestions != null ? "\n" + this.suggestions.getNarrationMessage() : "";
   }

   @OnlyIn(Dist.CLIENT)
   public class Suggestions {
      private final Rectangle2d rect;
      private final com.mojang.brigadier.suggestion.Suggestions field_228139_c_;
      private final String originalContents;
      private int offset;
      private int current;
      private Vec2f lastMouse = Vec2f.ZERO;
      private boolean tabCycles;
      private int lastNarratedEntry;

      private Suggestions(int p_i225920_2_, int p_i225920_3_, int p_i225920_4_, com.mojang.brigadier.suggestion.Suggestions p_i225920_5_, boolean p_i225920_6_) {
         int i = p_i225920_2_ - 1;
         int j = CommandSuggestionHelper.this.anchorToBottom ? p_i225920_3_ - 3 - Math.min(p_i225920_5_.getList().size(), CommandSuggestionHelper.this.suggestionLineLimit) * 12 : p_i225920_3_;
         this.rect = new Rectangle2d(i, j, p_i225920_4_ + 1, Math.min(p_i225920_5_.getList().size(), CommandSuggestionHelper.this.suggestionLineLimit) * 12);
         this.field_228139_c_ = p_i225920_5_;
         this.originalContents = CommandSuggestionHelper.this.input.getValue();
         this.lastNarratedEntry = p_i225920_6_ ? -1 : 0;
         this.select(0);
      }

      public void func_228149_a_(int p_228149_1_, int p_228149_2_) {
         int i = Math.min(this.field_228139_c_.getList().size(), CommandSuggestionHelper.this.suggestionLineLimit);
         int j = -5592406;
         boolean flag = this.offset > 0;
         boolean flag1 = this.field_228139_c_.getList().size() > this.offset + i;
         boolean flag2 = flag || flag1;
         boolean flag3 = this.lastMouse.x != (float)p_228149_1_ || this.lastMouse.y != (float)p_228149_2_;
         if (flag3) {
            this.lastMouse = new Vec2f((float)p_228149_1_, (float)p_228149_2_);
         }

         if (flag2) {
            AbstractGui.fill(this.rect.getX(), this.rect.getY() - 1, this.rect.getX() + this.rect.getWidth(), this.rect.getY(), CommandSuggestionHelper.this.fillColor);
            AbstractGui.fill(this.rect.getX(), this.rect.getY() + this.rect.getHeight(), this.rect.getX() + this.rect.getWidth(), this.rect.getY() + this.rect.getHeight() + 1, CommandSuggestionHelper.this.fillColor);
            if (flag) {
               for(int k = 0; k < this.rect.getWidth(); ++k) {
                  if (k % 2 == 0) {
                     AbstractGui.fill(this.rect.getX() + k, this.rect.getY() - 1, this.rect.getX() + k + 1, this.rect.getY(), -1);
                  }
               }
            }

            if (flag1) {
               for(int i1 = 0; i1 < this.rect.getWidth(); ++i1) {
                  if (i1 % 2 == 0) {
                     AbstractGui.fill(this.rect.getX() + i1, this.rect.getY() + this.rect.getHeight(), this.rect.getX() + i1 + 1, this.rect.getY() + this.rect.getHeight() + 1, -1);
                  }
               }
            }
         }

         boolean flag4 = false;

         for(int l = 0; l < i; ++l) {
            Suggestion suggestion = this.field_228139_c_.getList().get(l + this.offset);
            AbstractGui.fill(this.rect.getX(), this.rect.getY() + 12 * l, this.rect.getX() + this.rect.getWidth(), this.rect.getY() + 12 * l + 12, CommandSuggestionHelper.this.fillColor);
            if (p_228149_1_ > this.rect.getX() && p_228149_1_ < this.rect.getX() + this.rect.getWidth() && p_228149_2_ > this.rect.getY() + 12 * l && p_228149_2_ < this.rect.getY() + 12 * l + 12) {
               if (flag3) {
                  this.select(l + this.offset);
               }

               flag4 = true;
            }

            CommandSuggestionHelper.this.font.func_175063_a(suggestion.getText(), (float)(this.rect.getX() + 1), (float)(this.rect.getY() + 2 + 12 * l), l + this.offset == this.current ? -256 : -5592406);
         }

         if (flag4) {
            Message message = this.field_228139_c_.getList().get(this.current).getTooltip();
            if (message != null) {
               CommandSuggestionHelper.this.screen.renderTooltip(TextComponentUtils.fromMessage(message).func_150254_d(), p_228149_1_, p_228149_2_);
            }
         }

      }

      public boolean mouseClicked(int p_228150_1_, int p_228150_2_, int p_228150_3_) {
         if (!this.rect.contains(p_228150_1_, p_228150_2_)) {
            return false;
         } else {
            int i = (p_228150_2_ - this.rect.getY()) / 12 + this.offset;
            if (i >= 0 && i < this.field_228139_c_.getList().size()) {
               this.select(i);
               this.useSuggestion();
            }

            return true;
         }
      }

      public boolean mouseScrolled(double p_228147_1_) {
         int i = (int)(CommandSuggestionHelper.this.minecraft.mouseHandler.xpos() * (double)CommandSuggestionHelper.this.minecraft.getWindow().getGuiScaledWidth() / (double)CommandSuggestionHelper.this.minecraft.getWindow().getScreenWidth());
         int j = (int)(CommandSuggestionHelper.this.minecraft.mouseHandler.ypos() * (double)CommandSuggestionHelper.this.minecraft.getWindow().getGuiScaledHeight() / (double)CommandSuggestionHelper.this.minecraft.getWindow().getScreenHeight());
         if (this.rect.contains(i, j)) {
            this.offset = MathHelper.clamp((int)((double)this.offset - p_228147_1_), 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
            return true;
         } else {
            return false;
         }
      }

      public boolean keyPressed(int p_228154_1_, int p_228154_2_, int p_228154_3_) {
         if (p_228154_1_ == 265) {
            this.cycle(-1);
            this.tabCycles = false;
            return true;
         } else if (p_228154_1_ == 264) {
            this.cycle(1);
            this.tabCycles = false;
            return true;
         } else if (p_228154_1_ == 258) {
            if (this.tabCycles) {
               this.cycle(Screen.hasShiftDown() ? -1 : 1);
            }

            this.useSuggestion();
            return true;
         } else if (p_228154_1_ == 256) {
            this.hide();
            return true;
         } else {
            return false;
         }
      }

      public void cycle(int p_228148_1_) {
         this.select(this.current + p_228148_1_);
         int i = this.offset;
         int j = this.offset + CommandSuggestionHelper.this.suggestionLineLimit - 1;
         if (this.current < i) {
            this.offset = MathHelper.clamp(this.current, 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
         } else if (this.current > j) {
            this.offset = MathHelper.clamp(this.current + CommandSuggestionHelper.this.lineStartOffset - CommandSuggestionHelper.this.suggestionLineLimit, 0, Math.max(this.field_228139_c_.getList().size() - CommandSuggestionHelper.this.suggestionLineLimit, 0));
         }

      }

      public void select(int p_228153_1_) {
         this.current = p_228153_1_;
         if (this.current < 0) {
            this.current += this.field_228139_c_.getList().size();
         }

         if (this.current >= this.field_228139_c_.getList().size()) {
            this.current -= this.field_228139_c_.getList().size();
         }

         Suggestion suggestion = this.field_228139_c_.getList().get(this.current);
         CommandSuggestionHelper.this.input.setSuggestion(CommandSuggestionHelper.calculateSuggestionSuffix(CommandSuggestionHelper.this.input.getValue(), suggestion.apply(this.originalContents)));
         if (NarratorChatListener.INSTANCE.isActive() && this.lastNarratedEntry != this.current) {
            NarratorChatListener.INSTANCE.sayNow(this.getNarrationMessage());
         }

      }

      public void useSuggestion() {
         Suggestion suggestion = this.field_228139_c_.getList().get(this.current);
         CommandSuggestionHelper.this.keepSuggestions = true;
         CommandSuggestionHelper.this.input.setValue(suggestion.apply(this.originalContents));
         int i = suggestion.getRange().getStart() + suggestion.getText().length();
         CommandSuggestionHelper.this.input.setCursorPosition(i);
         CommandSuggestionHelper.this.input.setHighlightPos(i);
         this.select(this.current);
         CommandSuggestionHelper.this.keepSuggestions = false;
         this.tabCycles = true;
      }

      private String getNarrationMessage() {
         this.lastNarratedEntry = this.current;
         List<Suggestion> list = this.field_228139_c_.getList();
         Suggestion suggestion = list.get(this.current);
         Message message = suggestion.getTooltip();
         return message != null ? I18n.get("narration.suggestion.tooltip", this.current + 1, list.size(), suggestion.getText(), message.getString()) : I18n.get("narration.suggestion", this.current + 1, list.size(), suggestion.getText());
      }

      public void hide() {
         CommandSuggestionHelper.this.suggestions = null;
      }
   }
}