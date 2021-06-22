package net.minecraft.command.impl;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntitySummonArgument;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.command.arguments.SuggestionProviders;
import net.minecraft.command.arguments.Vec3Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public class SummonCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslationTextComponent("commands.summon.failed"));

   public static void register(CommandDispatcher<CommandSource> p_198736_0_) {
      p_198736_0_.register(Commands.literal("summon").requires((p_198740_0_) -> {
         return p_198740_0_.hasPermission(2);
      }).then(Commands.argument("entity", EntitySummonArgument.id()).suggests(SuggestionProviders.SUMMONABLE_ENTITIES).executes((p_198738_0_) -> {
         return spawnEntity(p_198738_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198738_0_, "entity"), p_198738_0_.getSource().getPosition(), new CompoundNBT(), true);
      }).then(Commands.argument("pos", Vec3Argument.vec3()).executes((p_198735_0_) -> {
         return spawnEntity(p_198735_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198735_0_, "entity"), Vec3Argument.getVec3(p_198735_0_, "pos"), new CompoundNBT(), true);
      }).then(Commands.argument("nbt", NBTCompoundTagArgument.compoundTag()).executes((p_198739_0_) -> {
         return spawnEntity(p_198739_0_.getSource(), EntitySummonArgument.getSummonableEntity(p_198739_0_, "entity"), Vec3Argument.getVec3(p_198739_0_, "pos"), NBTCompoundTagArgument.getCompoundTag(p_198739_0_, "nbt"), false);
      })))));
   }

   private static int spawnEntity(CommandSource p_198737_0_, ResourceLocation p_198737_1_, Vec3d p_198737_2_, CompoundNBT p_198737_3_, boolean p_198737_4_) throws CommandSyntaxException {
      CompoundNBT compoundnbt = p_198737_3_.copy();
      compoundnbt.putString("id", p_198737_1_.toString());
      if (EntityType.getKey(EntityType.LIGHTNING_BOLT).equals(p_198737_1_)) {
         LightningBoltEntity lightningboltentity = new LightningBoltEntity(p_198737_0_.getLevel(), p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, false);
         p_198737_0_.getLevel().func_217468_a(lightningboltentity);
         p_198737_0_.sendSuccess(new TranslationTextComponent("commands.summon.success", lightningboltentity.getDisplayName()), true);
         return 1;
      } else {
         ServerWorld serverworld = p_198737_0_.getLevel();
         Entity entity = EntityType.loadEntityRecursive(compoundnbt, serverworld, (p_218914_2_) -> {
            p_218914_2_.moveTo(p_198737_2_.x, p_198737_2_.y, p_198737_2_.z, p_218914_2_.yRot, p_218914_2_.xRot);
            return !serverworld.addWithUUID(p_218914_2_) ? null : p_218914_2_;
         });
         if (entity == null) {
            throw ERROR_FAILED.create();
         } else {
            if (p_198737_4_ && entity instanceof MobEntity) {
               ((MobEntity)entity).finalizeSpawn(p_198737_0_.getLevel(), p_198737_0_.getLevel().getCurrentDifficultyAt(new BlockPos(entity)), SpawnReason.COMMAND, (ILivingEntityData)null, (CompoundNBT)null);
            }

            p_198737_0_.sendSuccess(new TranslationTextComponent("commands.summon.success", entity.getDisplayName()), true);
            return 1;
         }
      }
   }
}