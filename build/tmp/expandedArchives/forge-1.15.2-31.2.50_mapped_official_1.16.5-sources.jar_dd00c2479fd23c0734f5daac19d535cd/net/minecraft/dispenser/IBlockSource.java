package net.minecraft.dispenser;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public interface IBlockSource extends ILocatableSource {
   double x();

   double y();

   double z();

   BlockPos getPos();

   BlockState getBlockState();

   <T extends TileEntity> T getEntity();
}