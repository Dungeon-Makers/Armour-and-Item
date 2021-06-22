package net.minecraft.command.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic4CommandExceptionType;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.Vec2Argument;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.server.ServerWorld;

public class SpreadPlayersCommand {
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_TEAMS = new Dynamic4CommandExceptionType((p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_) -> {
      return new TranslationTextComponent("commands.spreadplayers.failed.teams", p_208910_0_, p_208910_1_, p_208910_2_, p_208910_3_);
   });
   private static final Dynamic4CommandExceptionType ERROR_FAILED_TO_SPREAD_ENTITIES = new Dynamic4CommandExceptionType((p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_) -> {
      return new TranslationTextComponent("commands.spreadplayers.failed.entities", p_208912_0_, p_208912_1_, p_208912_2_, p_208912_3_);
   });

   public static void register(CommandDispatcher<CommandSource> p_198716_0_) {
      p_198716_0_.register(Commands.literal("spreadplayers").requires((p_198721_0_) -> {
         return p_198721_0_.hasPermission(2);
      }).then(Commands.argument("center", Vec2Argument.vec2()).then(Commands.argument("spreadDistance", FloatArgumentType.floatArg(0.0F)).then(Commands.argument("maxRange", FloatArgumentType.floatArg(1.0F)).then(Commands.argument("respectTeams", BoolArgumentType.bool()).then(Commands.argument("targets", EntityArgument.entities()).executes((p_198718_0_) -> {
         return func_198722_a(p_198718_0_.getSource(), Vec2Argument.getVec2(p_198718_0_, "center"), FloatArgumentType.getFloat(p_198718_0_, "spreadDistance"), FloatArgumentType.getFloat(p_198718_0_, "maxRange"), BoolArgumentType.getBool(p_198718_0_, "respectTeams"), EntityArgument.getEntities(p_198718_0_, "targets"));
      })))))));
   }

   private static int func_198722_a(CommandSource p_198722_0_, Vec2f p_198722_1_, float p_198722_2_, float p_198722_3_, boolean p_198722_4_, Collection<? extends Entity> p_198722_5_) throws CommandSyntaxException {
      Random random = new Random();
      double d0 = (double)(p_198722_1_.x - p_198722_3_);
      double d1 = (double)(p_198722_1_.y - p_198722_3_);
      double d2 = (double)(p_198722_1_.x + p_198722_3_);
      double d3 = (double)(p_198722_1_.y + p_198722_3_);
      SpreadPlayersCommand.Position[] aspreadplayerscommand$position = createInitialPositions(random, p_198722_4_ ? getNumberOfTeams(p_198722_5_) : p_198722_5_.size(), d0, d1, d2, d3);
      func_198717_a(p_198722_1_, (double)p_198722_2_, p_198722_0_.getLevel(), random, d0, d1, d2, d3, aspreadplayerscommand$position, p_198722_4_);
      double d4 = func_198719_a(p_198722_5_, p_198722_0_.getLevel(), aspreadplayerscommand$position, p_198722_4_);
      p_198722_0_.sendSuccess(new TranslationTextComponent("commands.spreadplayers.success." + (p_198722_4_ ? "teams" : "entities"), aspreadplayerscommand$position.length, p_198722_1_.x, p_198722_1_.y, String.format(Locale.ROOT, "%.2f", d4)), true);
      return aspreadplayerscommand$position.length;
   }

   private static int getNumberOfTeams(Collection<? extends Entity> p_198715_0_) {
      Set<Team> set = Sets.newHashSet();

      for(Entity entity : p_198715_0_) {
         if (entity instanceof PlayerEntity) {
            set.add(entity.getTeam());
         } else {
            set.add((Team)null);
         }
      }

      return set.size();
   }

   private static void func_198717_a(Vec2f p_198717_0_, double p_198717_1_, ServerWorld p_198717_3_, Random p_198717_4_, double p_198717_5_, double p_198717_7_, double p_198717_9_, double p_198717_11_, SpreadPlayersCommand.Position[] p_198717_13_, boolean p_198717_14_) throws CommandSyntaxException {
      boolean flag = true;
      double d0 = (double)Float.MAX_VALUE;

      int i;
      for(i = 0; i < 10000 && flag; ++i) {
         flag = false;
         d0 = (double)Float.MAX_VALUE;

         for(int j = 0; j < p_198717_13_.length; ++j) {
            SpreadPlayersCommand.Position spreadplayerscommand$position = p_198717_13_[j];
            int k = 0;
            SpreadPlayersCommand.Position spreadplayerscommand$position1 = new SpreadPlayersCommand.Position();

            for(int l = 0; l < p_198717_13_.length; ++l) {
               if (j != l) {
                  SpreadPlayersCommand.Position spreadplayerscommand$position2 = p_198717_13_[l];
                  double d1 = spreadplayerscommand$position.dist(spreadplayerscommand$position2);
                  d0 = Math.min(d1, d0);
                  if (d1 < p_198717_1_) {
                     ++k;
                     spreadplayerscommand$position1.x = spreadplayerscommand$position1.x + (spreadplayerscommand$position2.x - spreadplayerscommand$position.x);
                     spreadplayerscommand$position1.z = spreadplayerscommand$position1.z + (spreadplayerscommand$position2.z - spreadplayerscommand$position.z);
                  }
               }
            }

            if (k > 0) {
               spreadplayerscommand$position1.x = spreadplayerscommand$position1.x / (double)k;
               spreadplayerscommand$position1.z = spreadplayerscommand$position1.z / (double)k;
               double d2 = (double)spreadplayerscommand$position1.getLength();
               if (d2 > 0.0D) {
                  spreadplayerscommand$position1.normalize();
                  spreadplayerscommand$position.moveAway(spreadplayerscommand$position1);
               } else {
                  spreadplayerscommand$position.randomize(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
               }

               flag = true;
            }

            if (spreadplayerscommand$position.clamp(p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_)) {
               flag = true;
            }
         }

         if (!flag) {
            for(SpreadPlayersCommand.Position spreadplayerscommand$position3 : p_198717_13_) {
               if (!spreadplayerscommand$position3.func_198706_b(p_198717_3_)) {
                  spreadplayerscommand$position3.randomize(p_198717_4_, p_198717_5_, p_198717_7_, p_198717_9_, p_198717_11_);
                  flag = true;
               }
            }
         }
      }

      if (d0 == (double)Float.MAX_VALUE) {
         d0 = 0.0D;
      }

      if (i >= 10000) {
         if (p_198717_14_) {
            throw ERROR_FAILED_TO_SPREAD_TEAMS.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", d0));
         } else {
            throw ERROR_FAILED_TO_SPREAD_ENTITIES.create(p_198717_13_.length, p_198717_0_.x, p_198717_0_.y, String.format(Locale.ROOT, "%.2f", d0));
         }
      }
   }

   private static double func_198719_a(Collection<? extends Entity> p_198719_0_, ServerWorld p_198719_1_, SpreadPlayersCommand.Position[] p_198719_2_, boolean p_198719_3_) {
      double d0 = 0.0D;
      int i = 0;
      Map<Team, SpreadPlayersCommand.Position> map = Maps.newHashMap();

      for(Entity entity : p_198719_0_) {
         SpreadPlayersCommand.Position spreadplayerscommand$position;
         if (p_198719_3_) {
            Team team = entity instanceof PlayerEntity ? entity.getTeam() : null;
            if (!map.containsKey(team)) {
               map.put(team, p_198719_2_[i++]);
            }

            spreadplayerscommand$position = map.get(team);
         } else {
            spreadplayerscommand$position = p_198719_2_[i++];
         }

         entity.teleportToWithTicket((double)((float)MathHelper.floor(spreadplayerscommand$position.x) + 0.5F), (double)spreadplayerscommand$position.getSpawnY(p_198719_1_), (double)MathHelper.floor(spreadplayerscommand$position.z) + 0.5D);
         double d2 = Double.MAX_VALUE;

         for(SpreadPlayersCommand.Position spreadplayerscommand$position1 : p_198719_2_) {
            if (spreadplayerscommand$position != spreadplayerscommand$position1) {
               double d1 = spreadplayerscommand$position.dist(spreadplayerscommand$position1);
               d2 = Math.min(d1, d2);
            }
         }

         d0 += d2;
      }

      if (p_198719_0_.size() < 2) {
         return 0.0D;
      } else {
         d0 = d0 / (double)p_198719_0_.size();
         return d0;
      }
   }

   private static SpreadPlayersCommand.Position[] createInitialPositions(Random p_198720_0_, int p_198720_1_, double p_198720_2_, double p_198720_4_, double p_198720_6_, double p_198720_8_) {
      SpreadPlayersCommand.Position[] aspreadplayerscommand$position = new SpreadPlayersCommand.Position[p_198720_1_];

      for(int i = 0; i < aspreadplayerscommand$position.length; ++i) {
         SpreadPlayersCommand.Position spreadplayerscommand$position = new SpreadPlayersCommand.Position();
         spreadplayerscommand$position.randomize(p_198720_0_, p_198720_2_, p_198720_4_, p_198720_6_, p_198720_8_);
         aspreadplayerscommand$position[i] = spreadplayerscommand$position;
      }

      return aspreadplayerscommand$position;
   }

   static class Position {
      private double x;
      private double z;

      double dist(SpreadPlayersCommand.Position p_198708_1_) {
         double d0 = this.x - p_198708_1_.x;
         double d1 = this.z - p_198708_1_.z;
         return Math.sqrt(d0 * d0 + d1 * d1);
      }

      void normalize() {
         double d0 = (double)this.getLength();
         this.x /= d0;
         this.z /= d0;
      }

      float getLength() {
         return MathHelper.sqrt(this.x * this.x + this.z * this.z);
      }

      public void moveAway(SpreadPlayersCommand.Position p_198705_1_) {
         this.x -= p_198705_1_.x;
         this.z -= p_198705_1_.z;
      }

      public boolean clamp(double p_198709_1_, double p_198709_3_, double p_198709_5_, double p_198709_7_) {
         boolean flag = false;
         if (this.x < p_198709_1_) {
            this.x = p_198709_1_;
            flag = true;
         } else if (this.x > p_198709_5_) {
            this.x = p_198709_5_;
            flag = true;
         }

         if (this.z < p_198709_3_) {
            this.z = p_198709_3_;
            flag = true;
         } else if (this.z > p_198709_7_) {
            this.z = p_198709_7_;
            flag = true;
         }

         return flag;
      }

      public int getSpawnY(IBlockReader p_198710_1_) {
         BlockPos blockpos = new BlockPos(this.x, 256.0D, this.z);

         while(blockpos.getY() > 0) {
            blockpos = blockpos.below();
            if (!p_198710_1_.getBlockState(blockpos).isAir()) {
               return blockpos.getY() + 1;
            }
         }

         return 257;
      }

      public boolean func_198706_b(IBlockReader p_198706_1_) {
         BlockPos blockpos = new BlockPos(this.x, 256.0D, this.z);

         while(blockpos.getY() > 0) {
            blockpos = blockpos.below();
            BlockState blockstate = p_198706_1_.getBlockState(blockpos);
            if (!blockstate.isAir()) {
               Material material = blockstate.getMaterial();
               return !material.isLiquid() && material != Material.FIRE;
            }
         }

         return false;
      }

      public void randomize(Random p_198711_1_, double p_198711_2_, double p_198711_4_, double p_198711_6_, double p_198711_8_) {
         this.x = MathHelper.nextDouble(p_198711_1_, p_198711_2_, p_198711_6_);
         this.z = MathHelper.nextDouble(p_198711_1_, p_198711_4_, p_198711_8_);
      }
   }
}