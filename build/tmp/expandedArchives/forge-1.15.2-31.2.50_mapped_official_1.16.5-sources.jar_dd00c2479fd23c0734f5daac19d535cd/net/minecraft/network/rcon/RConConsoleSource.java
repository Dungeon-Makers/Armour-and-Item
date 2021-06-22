package net.minecraft.network.rcon;

import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;

public class RConConsoleSource implements ICommandSource {
   private final StringBuffer buffer = new StringBuffer();
   private final MinecraftServer server;

   public RConConsoleSource(MinecraftServer p_i46835_1_) {
      this.server = p_i46835_1_;
   }

   public void prepareForCommand() {
      this.buffer.setLength(0);
   }

   public String getCommandResponse() {
      return this.buffer.toString();
   }

   public CommandSource createCommandSourceStack() {
      ServerWorld serverworld = this.server.getLevel(DimensionType.field_223227_a_);
      return new CommandSource(this, new Vec3d(serverworld.func_175694_M()), Vec2f.ZERO, serverworld, 4, "Recon", new StringTextComponent("Rcon"), this.server, (Entity)null);
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.buffer.append(p_145747_1_.getString());
   }

   public boolean acceptsSuccess() {
      return true;
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return this.server.shouldRconBroadcast();
   }
}