package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.StructureMode;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class StructureBlock extends ContainerBlock {
   public static final EnumProperty<StructureMode> MODE = BlockStateProperties.STRUCTUREBLOCK_MODE;

   protected StructureBlock(Block.Properties p_i48314_1_) {
      super(p_i48314_1_);
   }

   public TileEntity newBlockEntity(IBlockReader p_196283_1_) {
      return new StructureBlockTileEntity();
   }

   public ActionResultType use(BlockState p_225533_1_, World p_225533_2_, BlockPos p_225533_3_, PlayerEntity p_225533_4_, Hand p_225533_5_, BlockRayTraceResult p_225533_6_) {
      TileEntity tileentity = p_225533_2_.getBlockEntity(p_225533_3_);
      if (tileentity instanceof StructureBlockTileEntity) {
         return ((StructureBlockTileEntity)tileentity).usedBy(p_225533_4_) ? ActionResultType.SUCCESS : ActionResultType.PASS;
      } else {
         return ActionResultType.PASS;
      }
   }

   public void setPlacedBy(World p_180633_1_, BlockPos p_180633_2_, BlockState p_180633_3_, @Nullable LivingEntity p_180633_4_, ItemStack p_180633_5_) {
      if (!p_180633_1_.isClientSide) {
         if (p_180633_4_ != null) {
            TileEntity tileentity = p_180633_1_.getBlockEntity(p_180633_2_);
            if (tileentity instanceof StructureBlockTileEntity) {
               ((StructureBlockTileEntity)tileentity).createdBy(p_180633_4_);
            }
         }

      }
   }

   public BlockRenderType getRenderShape(BlockState p_149645_1_) {
      return BlockRenderType.MODEL;
   }

   public BlockState getStateForPlacement(BlockItemUseContext p_196258_1_) {
      return this.defaultBlockState().setValue(MODE, StructureMode.DATA);
   }

   protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> p_206840_1_) {
      p_206840_1_.add(MODE);
   }

   public void neighborChanged(BlockState p_220069_1_, World p_220069_2_, BlockPos p_220069_3_, Block p_220069_4_, BlockPos p_220069_5_, boolean p_220069_6_) {
      if (!p_220069_2_.isClientSide) {
         TileEntity tileentity = p_220069_2_.getBlockEntity(p_220069_3_);
         if (tileentity instanceof StructureBlockTileEntity) {
            StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)tileentity;
            boolean flag = p_220069_2_.hasNeighborSignal(p_220069_3_);
            boolean flag1 = structureblocktileentity.isPowered();
            if (flag && !flag1) {
               structureblocktileentity.setPowered(true);
               this.func_189874_a(structureblocktileentity);
            } else if (!flag && flag1) {
               structureblocktileentity.setPowered(false);
            }

         }
      }
   }

   private void func_189874_a(StructureBlockTileEntity p_189874_1_) {
      switch(p_189874_1_.getMode()) {
      case SAVE:
         p_189874_1_.saveStructure(false);
         break;
      case LOAD:
         p_189874_1_.func_189714_c(false);
         break;
      case CORNER:
         p_189874_1_.unloadStructure();
      case DATA:
      }

   }
}