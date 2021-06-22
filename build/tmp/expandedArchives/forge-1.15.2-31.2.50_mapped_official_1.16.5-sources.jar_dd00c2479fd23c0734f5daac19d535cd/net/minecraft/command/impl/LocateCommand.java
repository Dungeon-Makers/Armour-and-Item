package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class LocateCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.locate.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198528_0_) {
      p_198528_0_.register(Commands.literal("locate").requires((p_198533_0_) -> {
         return p_198533_0_.hasPermission(2);
      }).then(Commands.literal("Pillager_Outpost").executes((p_198530_0_) -> {
         return func_198534_a(p_198530_0_.getSource(), "Pillager_Outpost");
      })).then(Commands.literal("Mineshaft").executes((p_198535_0_) -> {
         return func_198534_a(p_198535_0_.getSource(), "Mineshaft");
      })).then(Commands.literal("Mansion").executes((p_198527_0_) -> {
         return func_198534_a(p_198527_0_.getSource(), "Mansion");
      })).then(Commands.literal("Igloo").executes((p_198529_0_) -> {
         return func_198534_a(p_198529_0_.getSource(), "Igloo");
      })).then(Commands.literal("Desert_Pyramid").executes((p_198526_0_) -> {
         return func_198534_a(p_198526_0_.getSource(), "Desert_Pyramid");
      })).then(Commands.literal("Jungle_Pyramid").executes((p_198531_0_) -> {
         return func_198534_a(p_198531_0_.getSource(), "Jungle_Pyramid");
      })).then(Commands.literal("Swamp_Hut").executes((p_198525_0_) -> {
         return func_198534_a(p_198525_0_.getSource(), "Swamp_Hut");
      })).then(Commands.literal("Stronghold").executes((p_198532_0_) -> {
         return func_198534_a(p_198532_0_.getSource(), "Stronghold");
      })).then(Commands.literal("Monument").executes((p_202686_0_) -> {
         return func_198534_a(p_202686_0_.getSource(), "Monument");
      })).then(Commands.literal("Fortress").executes((p_202685_0_) -> {
         return func_198534_a(p_202685_0_.getSource(), "Fortress");
      })).then(Commands.literal("EndCity").executes((p_202687_0_) -> {
         return func_198534_a(p_202687_0_.getSource(), "EndCity");
      })).then(Commands.literal("Ocean_Ruin").executes((p_204104_0_) -> {
         return func_198534_a(p_204104_0_.getSource(), "Ocean_Ruin");
      })).then(Commands.literal("Buried_Treasure").executes((p_204297_0_) -> {
         return func_198534_a(p_204297_0_.getSource(), "Buried_Treasure");
      })).then(Commands.literal("Shipwreck").executes((p_204758_0_) -> {
         return func_198534_a(p_204758_0_.getSource(), "Shipwreck");
      })).then(Commands.literal("Village").executes((p_218858_0_) -> {
         return func_198534_a(p_218858_0_.getSource(), "Village");
      // FORGE: Support modded structures via registry name
      })).then(Commands.argument("structure_type", net.minecraft.command.arguments.ResourceLocationArgument.id())
              .suggests((ctx, sb) -> net.minecraft.command.ISuggestionProvider.suggest(
                      net.minecraftforge.registries.GameData.getStructureFeatures().keySet().stream()
                          .map(net.minecraft.util.ResourceLocation::toString), sb))
              .executes(ctx -> func_198534_a(ctx.getSource(), ctx.getArgument("structure_type", net.minecraft.util.ResourceLocation.class).toString().replace("minecraft:", ""))
      )));
   }

   private static int func_198534_a(CommandSource p_198534_0_, String p_198534_1_) throws CommandSyntaxException {
      BlockPos blockpos = new BlockPos(p_198534_0_.getPosition());
      BlockPos blockpos1 = p_198534_0_.getLevel().func_211157_a(p_198534_1_, blockpos, 100, false);
      if (blockpos1 == null) {
         throw ERROR_FAILED.create();
      } else {
         int i = MathHelper.floor(dist(blockpos.getX(), blockpos.getZ(), blockpos1.getX(), blockpos1.getZ()));
         ITextComponent itextcomponent = TextComponentUtils.func_197676_a(new TranslationTextComponent("chat.coordinates", blockpos1.getX(), "~", blockpos1.getZ())).func_211710_a((p_211746_1_) -> {
            p_211746_1_.func_150238_a(TextFormatting.GREEN).func_150241_a(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/tp @s " + blockpos1.getX() + " ~ " + blockpos1.getZ())).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TranslationTextComponent("chat.coordinates.tooltip")));
         });
         p_198534_0_.sendSuccess(new TranslationTextComponent("commands.locate.success", p_198534_1_, itextcomponent, i), false);
         return i;
      }
   }

   private static float dist(int p_211907_0_, int p_211907_1_, int p_211907_2_, int p_211907_3_) {
      int i = p_211907_2_ - p_211907_0_;
      int j = p_211907_3_ - p_211907_1_;
      return MathHelper.sqrt((float)(i * i + j * j));
   }
}
