package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.MessageArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;

public class MessageCommand {
   public static void register(CommandDispatcher<CommandSource> p_198537_0_) {
      LiteralCommandNode<CommandSource> literalcommandnode = p_198537_0_.register(Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((p_198539_0_) -> {
         return sendMessage(p_198539_0_.getSource(), EntityArgument.getPlayers(p_198539_0_, "targets"), MessageArgument.getMessage(p_198539_0_, "message"));
      }))));
      p_198537_0_.register(Commands.literal("tell").redirect(literalcommandnode));
      p_198537_0_.register(Commands.literal("w").redirect(literalcommandnode));
   }

   private static int sendMessage(CommandSource p_198538_0_, Collection<ServerPlayerEntity> p_198538_1_, ITextComponent p_198538_2_) {
      for(ServerPlayerEntity serverplayerentity : p_198538_1_) {
         serverplayerentity.sendMessage((new TranslationTextComponent("commands.message.display.incoming", p_198538_0_.getDisplayName(), p_198538_2_.func_212638_h())).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}));
         p_198538_0_.sendSuccess((new TranslationTextComponent("commands.message.display.outgoing", serverplayerentity.getDisplayName(), p_198538_2_.func_212638_h())).func_211709_a(new TextFormatting[]{TextFormatting.GRAY, TextFormatting.ITALIC}), false);
      }

      return p_198538_1_.size();
   }
}