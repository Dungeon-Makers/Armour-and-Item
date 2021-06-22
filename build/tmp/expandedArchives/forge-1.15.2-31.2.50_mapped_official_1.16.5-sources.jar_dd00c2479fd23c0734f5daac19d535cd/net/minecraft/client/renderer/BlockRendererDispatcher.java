package net.minecraft.client.renderer;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Random;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.ItemStackTileEntityRenderer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BlockRendererDispatcher implements IResourceManagerReloadListener {
   private final BlockModelShapes blockModelShaper;
   private final BlockModelRenderer modelRenderer;
   private final FluidBlockRenderer liquidBlockRenderer;
   private final Random random = new Random();
   private final BlockColors blockColors;

   public BlockRendererDispatcher(BlockModelShapes p_i46577_1_, BlockColors p_i46577_2_) {
      this.blockModelShaper = p_i46577_1_;
      this.blockColors = p_i46577_2_;
      this.modelRenderer = new net.minecraftforge.client.model.pipeline.ForgeBlockModelRenderer(this.blockColors);
      this.liquidBlockRenderer = new FluidBlockRenderer();
   }

   public BlockModelShapes getBlockModelShaper() {
      return this.blockModelShaper;
   }

   @Deprecated //Forge: Model parameter
   public void renderBreakingTexture(BlockState p_228792_1_, BlockPos p_228792_2_, ILightReader p_228792_3_, MatrixStack p_228792_4_, IVertexBuilder p_228792_5_) {
      renderModel(p_228792_1_, p_228792_2_, p_228792_3_, p_228792_4_, p_228792_5_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderModel(BlockState p_228792_1_, BlockPos p_228792_2_, ILightReader p_228792_3_, MatrixStack p_228792_4_, IVertexBuilder p_228792_5_, net.minecraftforge.client.model.data.IModelData modelData) {
      if (p_228792_1_.getRenderShape() == BlockRenderType.MODEL) {
         IBakedModel ibakedmodel = this.blockModelShaper.getBlockModel(p_228792_1_);
         long i = p_228792_1_.getSeed(p_228792_2_);
         this.modelRenderer.renderModel(p_228792_3_, ibakedmodel, p_228792_1_, p_228792_2_, p_228792_4_, p_228792_5_, true, this.random, i, OverlayTexture.NO_OVERLAY, modelData);
      }
   }

   @Deprecated //Forge: Model parameter
   public boolean renderBatched(BlockState p_228793_1_, BlockPos p_228793_2_, ILightReader p_228793_3_, MatrixStack p_228793_4_, IVertexBuilder p_228793_5_, boolean p_228793_6_, Random p_228793_7_) {
      return renderModel(p_228793_1_, p_228793_2_, p_228793_3_, p_228793_4_, p_228793_5_, p_228793_6_, p_228793_7_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public boolean renderModel(BlockState p_228793_1_, BlockPos p_228793_2_, ILightReader p_228793_3_, MatrixStack p_228793_4_, IVertexBuilder p_228793_5_, boolean p_228793_6_, Random p_228793_7_, net.minecraftforge.client.model.data.IModelData modelData) {
      try {
         BlockRenderType blockrendertype = p_228793_1_.getRenderShape();
         return blockrendertype != BlockRenderType.MODEL ? false : this.modelRenderer.renderModel(p_228793_3_, this.getBlockModel(p_228793_1_), p_228793_1_, p_228793_2_, p_228793_4_, p_228793_5_, p_228793_6_, p_228793_7_, p_228793_1_.getSeed(p_228793_2_), OverlayTexture.NO_OVERLAY, modelData);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating block in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(crashreportcategory, p_228793_2_, p_228793_1_);
         throw new ReportedException(crashreport);
      }
   }

   public boolean renderLiquid(BlockPos p_228794_1_, ILightReader p_228794_2_, IVertexBuilder p_228794_3_, IFluidState p_228794_4_) {
      try {
         return this.liquidBlockRenderer.tesselate(p_228794_2_, p_228794_1_, p_228794_3_, p_228794_4_);
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Tesselating liquid in world");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(crashreportcategory, p_228794_1_, (BlockState)null);
         throw new ReportedException(crashreport);
      }
   }

   public BlockModelRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public IBakedModel getBlockModel(BlockState p_184389_1_) {
      return this.blockModelShaper.getBlockModel(p_184389_1_);
   }

   @Deprecated //Forge: Model parameter
   public void renderSingleBlock(BlockState p_228791_1_, MatrixStack p_228791_2_, IRenderTypeBuffer p_228791_3_, int p_228791_4_, int p_228791_5_) {
      renderBlock(p_228791_1_, p_228791_2_, p_228791_3_, p_228791_4_, p_228791_5_, net.minecraftforge.client.model.data.EmptyModelData.INSTANCE);
   }
   public void renderBlock(BlockState p_228791_1_, MatrixStack p_228791_2_, IRenderTypeBuffer p_228791_3_, int p_228791_4_, int p_228791_5_, net.minecraftforge.client.model.data.IModelData modelData) {
      BlockRenderType blockrendertype = p_228791_1_.getRenderShape();
      if (blockrendertype != BlockRenderType.INVISIBLE) {
         switch(blockrendertype) {
         case MODEL:
            IBakedModel ibakedmodel = this.getBlockModel(p_228791_1_);
            int i = this.blockColors.getColor(p_228791_1_, (ILightReader)null, (BlockPos)null, 0);
            float f = (float)(i >> 16 & 255) / 255.0F;
            float f1 = (float)(i >> 8 & 255) / 255.0F;
            float f2 = (float)(i & 255) / 255.0F;
            this.modelRenderer.renderModel(p_228791_2_.last(), p_228791_3_.getBuffer(RenderTypeLookup.func_228394_b_(p_228791_1_)), p_228791_1_, ibakedmodel, f, f1, f2, p_228791_4_, p_228791_5_, modelData);
            break;
         case ENTITYBLOCK_ANIMATED:
            ItemStack stack = new ItemStack(p_228791_1_.getBlock());
            stack.getItem().getItemStackTileEntityRenderer().func_228364_a_(stack, p_228791_2_, p_228791_3_, p_228791_4_, p_228791_5_);
         }

      }
   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      this.liquidBlockRenderer.setupSprites();
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.MODELS;
   }
}
