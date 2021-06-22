package net.minecraft.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ProxyBlockSource implements IBlockSource {
   private final World level;
   private final BlockPos pos;

   public ProxyBlockSource(World p_i46023_1_, BlockPos p_i46023_2_) {
      this.level = p_i46023_1_;
      this.pos = p_i46023_2_;
   }

   public World getLevel() {
      return this.level;
   }

   public double x() {
      return (double)this.pos.getX() + 0.5D;
   }

   public double y() {
      return (double)this.pos.getY() + 0.5D;
   }

   public double z() {
      return (double)this.pos.getZ() + 0.5D;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public BlockState getBlockState() {
      return this.level.getBlockState(this.pos);
   }

   public <T extends TileEntity> T getEntity() {
      return (T)this.level.getBlockEntity(this.pos);
   }
}