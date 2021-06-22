package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.BlockPosArgument;
import net.minecraft.network.play.server.SSpawnPositionPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;

public class SetWorldSpawnCommand {
   public static void register(CommandDispatcher<CommandSource> p_198702_0_) {
      p_198702_0_.register(Commands.literal("setworldspawn").requires((p_198704_0_) -> {
         return p_198704_0_.hasPermission(2);
      }).executes((p_198700_0_) -> {
         return setSpawn(p_198700_0_.getSource(), new BlockPos(p_198700_0_.getSource().getPosition()));
      }).then(Commands.argument("pos", BlockPosArgument.blockPos()).executes((p_198703_0_) -> {
         return setSpawn(p_198703_0_.getSource(), BlockPosArgument.getOrLoadBlockPos(p_198703_0_, "pos"));
      })));
   }

   private static int setSpawn(CommandSource p_198701_0_, BlockPos p_198701_1_) {
      p_198701_0_.getLevel().func_175652_B(p_198701_1_);
      p_198701_0_.getServer().getPlayerList().broadcastAll(new SSpawnPositionPacket(p_198701_1_));
      p_198701_0_.sendSuccess(new TranslationTextComponent("commands.setworldspawn.success", p_198701_1_.getX(), p_198701_1_.getY(), p_198701_1_.getZ()), true);
      return 1;
   }
}