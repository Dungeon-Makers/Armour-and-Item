package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.EndGatewayTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class EndGatewayFeature extends Feature<EndGatewayConfig> {
   public EndGatewayFeature(Function<Dynamic<?>, ? extends EndGatewayConfig> p_i49881_1_) {
      super(p_i49881_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, EndGatewayConfig p_212245_5_) {
      for(BlockPos blockpos : BlockPos.betweenClosed(p_212245_4_.offset(-1, -2, -1), p_212245_4_.offset(1, 2, 1))) {
         boolean flag = blockpos.getX() == p_212245_4_.getX();
         boolean flag1 = blockpos.getY() == p_212245_4_.getY();
         boolean flag2 = blockpos.getZ() == p_212245_4_.getZ();
         boolean flag3 = Math.abs(blockpos.getY() - p_212245_4_.getY()) == 2;
         if (flag && flag1 && flag2) {
            BlockPos blockpos1 = blockpos.immutable();
            this.func_202278_a(p_212245_1_, blockpos1, Blocks.END_GATEWAY.defaultBlockState());
            p_212245_5_.getExit().ifPresent((p_214624_3_) -> {
               TileEntity tileentity = p_212245_1_.getBlockEntity(blockpos1);
               if (tileentity instanceof EndGatewayTileEntity) {
                  EndGatewayTileEntity endgatewaytileentity = (EndGatewayTileEntity)tileentity;
                  endgatewaytileentity.setExitPosition(p_214624_3_, p_212245_5_.isExitExact());
                  tileentity.setChanged();
               }

            });
         } else if (flag1) {
            this.func_202278_a(p_212245_1_, blockpos, Blocks.AIR.defaultBlockState());
         } else if (flag3 && flag && flag2) {
            this.func_202278_a(p_212245_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
         } else if ((flag || flag2) && !flag3) {
            this.func_202278_a(p_212245_1_, blockpos, Blocks.BEDROCK.defaultBlockState());
         } else {
            this.func_202278_a(p_212245_1_, blockpos, Blocks.AIR.defaultBlockState());
         }
      }

      return true;
   }
}