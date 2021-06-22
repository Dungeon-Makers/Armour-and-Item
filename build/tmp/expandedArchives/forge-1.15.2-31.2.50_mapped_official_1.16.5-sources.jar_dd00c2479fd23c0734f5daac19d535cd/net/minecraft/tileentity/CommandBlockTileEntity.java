package net.minecraft.tileentity;

import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CommandBlockBlock;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CommandBlockTileEntity extends TileEntity {
   private boolean powered;
   private boolean auto;
   private boolean conditionMet;
   private boolean sendToClient;
   private final CommandBlockLogic commandBlock = new CommandBlockLogic() {
      public void setCommand(String p_145752_1_) {
         super.setCommand(p_145752_1_);
         CommandBlockTileEntity.this.setChanged();
      }

      public ServerWorld getLevel() {
         return (ServerWorld)CommandBlockTileEntity.this.level;
      }

      public void onUpdated() {
         BlockState blockstate = CommandBlockTileEntity.this.level.getBlockState(CommandBlockTileEntity.this.worldPosition);
         this.getLevel().sendBlockUpdated(CommandBlockTileEntity.this.worldPosition, blockstate, blockstate, 3);
      }

      @OnlyIn(Dist.CLIENT)
      public Vec3d getPosition() {
         return new Vec3d((double)CommandBlockTileEntity.this.worldPosition.getX() + 0.5D, (double)CommandBlockTileEntity.this.worldPosition.getY() + 0.5D, (double)CommandBlockTileEntity.this.worldPosition.getZ() + 0.5D);
      }

      public CommandSource createCommandSourceStack() {
         return new CommandSource(this, new Vec3d((double)CommandBlockTileEntity.this.worldPosition.getX() + 0.5D, (double)CommandBlockTileEntity.this.worldPosition.getY() + 0.5D, (double)CommandBlockTileEntity.this.worldPosition.getZ() + 0.5D), Vec2f.ZERO, this.getLevel(), 2, this.getName().getString(), this.getName(), this.getLevel().getServer(), (Entity)null);
      }
   };

   public CommandBlockTileEntity() {
      super(TileEntityType.COMMAND_BLOCK);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      this.commandBlock.save(p_189515_1_);
      p_189515_1_.putBoolean("powered", this.isPowered());
      p_189515_1_.putBoolean("conditionMet", this.wasConditionMet());
      p_189515_1_.putBoolean("auto", this.isAutomatic());
      return p_189515_1_;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      this.commandBlock.load(p_145839_1_);
      this.powered = p_145839_1_.getBoolean("powered");
      this.conditionMet = p_145839_1_.getBoolean("conditionMet");
      this.setAutomatic(p_145839_1_.getBoolean("auto"));
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      if (this.isSendToClient()) {
         this.setSendToClient(false);
         CompoundNBT compoundnbt = this.save(new CompoundNBT());
         return new SUpdateTileEntityPacket(this.worldPosition, 2, compoundnbt);
      } else {
         return null;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public CommandBlockLogic getCommandBlock() {
      return this.commandBlock;
   }

   public void setPowered(boolean p_184250_1_) {
      this.powered = p_184250_1_;
   }

   public boolean isPowered() {
      return this.powered;
   }

   public boolean isAutomatic() {
      return this.auto;
   }

   public void setAutomatic(boolean p_184253_1_) {
      boolean flag = this.auto;
      this.auto = p_184253_1_;
      if (!flag && p_184253_1_ && !this.powered && this.level != null && this.getMode() != CommandBlockTileEntity.Mode.SEQUENCE) {
         this.scheduleTick();
      }

   }

   public void onModeSwitch() {
      CommandBlockTileEntity.Mode commandblocktileentity$mode = this.getMode();
      if (commandblocktileentity$mode == CommandBlockTileEntity.Mode.AUTO && (this.powered || this.auto) && this.level != null) {
         this.scheduleTick();
      }

   }

   private void scheduleTick() {
      Block block = this.getBlockState().getBlock();
      if (block instanceof CommandBlockBlock) {
         this.markConditionMet();
         this.level.getBlockTicks().scheduleTick(this.worldPosition, block, block.getTickDelay(this.level));
      }

   }

   public boolean wasConditionMet() {
      return this.conditionMet;
   }

   public boolean markConditionMet() {
      this.conditionMet = true;
      if (this.isConditional()) {
         BlockPos blockpos = this.worldPosition.relative(this.level.getBlockState(this.worldPosition).getValue(CommandBlockBlock.FACING).getOpposite());
         if (this.level.getBlockState(blockpos).getBlock() instanceof CommandBlockBlock) {
            TileEntity tileentity = this.level.getBlockEntity(blockpos);
            this.conditionMet = tileentity instanceof CommandBlockTileEntity && ((CommandBlockTileEntity)tileentity).getCommandBlock().getSuccessCount() > 0;
         } else {
            this.conditionMet = false;
         }
      }

      return this.conditionMet;
   }

   public boolean isSendToClient() {
      return this.sendToClient;
   }

   public void setSendToClient(boolean p_184252_1_) {
      this.sendToClient = p_184252_1_;
   }

   public CommandBlockTileEntity.Mode getMode() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.REDSTONE;
      } else if (block == Blocks.REPEATING_COMMAND_BLOCK) {
         return CommandBlockTileEntity.Mode.AUTO;
      } else {
         return block == Blocks.CHAIN_COMMAND_BLOCK ? CommandBlockTileEntity.Mode.SEQUENCE : CommandBlockTileEntity.Mode.REDSTONE;
      }
   }

   public boolean isConditional() {
      BlockState blockstate = this.level.getBlockState(this.getBlockPos());
      return blockstate.getBlock() instanceof CommandBlockBlock ? blockstate.getValue(CommandBlockBlock.CONDITIONAL) : false;
   }

   public void clearRemoved() {
      this.clearCache();
      super.clearRemoved();
   }

   public static enum Mode {
      SEQUENCE,
      AUTO,
      REDSTONE;
   }
}