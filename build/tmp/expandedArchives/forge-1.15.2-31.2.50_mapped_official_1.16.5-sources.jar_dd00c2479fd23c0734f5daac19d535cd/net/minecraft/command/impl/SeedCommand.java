package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class SeedCommand {
   public static void func_198671_a(CommandDispatcher<CommandSource> p_198671_0_) {
      p_198671_0_.register(Commands.literal("seed").requires((p_198673_0_) -> {
         return p_198673_0_.getServer().isSingleplayer() || p_198673_0_.hasPermission(2);
      }).executes((p_198672_0_) -> {
         long i = p_198672_0_.getSource().getLevel().getSeed();
         ITextComponent itextcomponent = TextComponentUtils.func_197676_a((new StringTextComponent(String.valueOf(i))).func_211710_a((p_211752_2_) -> {
            p_211752_2_.func_150238_a(TextFormatting.GREEN).func_150241_a(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, String.valueOf(i))).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.copy.click"))).func_179989_a(String.valueOf(i));
         }));
         p_198672_0_.getSource().sendSuccess(new TranslationTextComponent("commands.seed.success", itextcomponent), false);
         return (int)i;
      }));
   }
}