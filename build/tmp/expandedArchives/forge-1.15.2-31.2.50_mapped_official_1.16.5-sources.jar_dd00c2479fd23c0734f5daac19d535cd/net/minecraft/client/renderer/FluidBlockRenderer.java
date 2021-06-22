package net.minecraft.client.renderer;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.StainedGlassBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.ILightReader;
import net.minecraft.world.biome.BiomeColors;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FluidBlockRenderer {
   private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
   private TextureAtlasSprite waterOverlay;

   protected void setupSprites() {
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = ModelBakery.LAVA_FLOW.sprite();
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = ModelBakery.WATER_FLOW.sprite();
      this.waterOverlay = ModelBakery.WATER_OVERLAY.sprite();
   }

   private static boolean isNeighborSameFluid(IBlockReader p_209557_0_, BlockPos p_209557_1_, Direction p_209557_2_, IFluidState p_209557_3_) {
      BlockPos blockpos = p_209557_1_.relative(p_209557_2_);
      IFluidState ifluidstate = p_209557_0_.getFluidState(blockpos);
      return ifluidstate.getType().isSame(p_209557_3_.getType());
   }

   private static boolean func_209556_a(IBlockReader p_209556_0_, BlockPos p_209556_1_, Direction p_209556_2_, float p_209556_3_) {
      BlockPos blockpos = p_209556_1_.relative(p_209556_2_);
      BlockState blockstate = p_209556_0_.getBlockState(blockpos);
      if (blockstate.canOcclude()) {
         VoxelShape voxelshape = VoxelShapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)p_209556_3_, 1.0D);
         VoxelShape voxelshape1 = blockstate.getBlockSupportShape(p_209556_0_, blockpos);
         return VoxelShapes.blockOccudes(voxelshape, voxelshape1, p_209556_2_);
      } else {
         return false;
      }
   }

   public boolean tesselate(ILightReader p_228796_1_, BlockPos p_228796_2_, IVertexBuilder p_228796_3_, IFluidState p_228796_4_) {
      boolean flag = p_228796_4_.is(FluidTags.LAVA);
      TextureAtlasSprite[] atextureatlassprite = net.minecraftforge.client.ForgeHooksClient.getFluidSprites(p_228796_1_, p_228796_2_, p_228796_4_);
      int i = p_228796_4_.getType().getAttributes().getColor(p_228796_1_, p_228796_2_);
      float alpha = (float)(i >> 24 & 255) / 255.0F;
      float f = (float)(i >> 16 & 255) / 255.0F;
      float f1 = (float)(i >> 8 & 255) / 255.0F;
      float f2 = (float)(i & 255) / 255.0F;
      boolean flag1 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.UP, p_228796_4_);
      boolean flag2 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.DOWN, p_228796_4_) && !func_209556_a(p_228796_1_, p_228796_2_, Direction.DOWN, 0.8888889F);
      boolean flag3 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.NORTH, p_228796_4_);
      boolean flag4 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.SOUTH, p_228796_4_);
      boolean flag5 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.WEST, p_228796_4_);
      boolean flag6 = !isNeighborSameFluid(p_228796_1_, p_228796_2_, Direction.EAST, p_228796_4_);
      if (!flag1 && !flag2 && !flag6 && !flag5 && !flag3 && !flag4) {
         return false;
      } else {
         boolean flag7 = false;
         float f3 = 0.5F;
         float f4 = 1.0F;
         float f5 = 0.8F;
         float f6 = 0.6F;
         float f7 = this.getWaterHeight(p_228796_1_, p_228796_2_, p_228796_4_.getType());
         float f8 = this.getWaterHeight(p_228796_1_, p_228796_2_.south(), p_228796_4_.getType());
         float f9 = this.getWaterHeight(p_228796_1_, p_228796_2_.east().south(), p_228796_4_.getType());
         float f10 = this.getWaterHeight(p_228796_1_, p_228796_2_.east(), p_228796_4_.getType());
         double d0 = (double)(p_228796_2_.getX() & 15);
         double d1 = (double)(p_228796_2_.getY() & 15);
         double d2 = (double)(p_228796_2_.getZ() & 15);
         float f11 = 0.001F;
         float f12 = flag2 ? 0.001F : 0.0F;
         if (flag1 && !func_209556_a(p_228796_1_, p_228796_2_, Direction.UP, Math.min(Math.min(f7, f8), Math.min(f9, f10)))) {
            flag7 = true;
            f7 -= 0.001F;
            f8 -= 0.001F;
            f9 -= 0.001F;
            f10 -= 0.001F;
            Vec3d vec3d = p_228796_4_.getFlow(p_228796_1_, p_228796_2_);
            float f13;
            float f14;
            float f15;
            float f16;
            float f17;
            float f18;
            float f19;
            float f20;
            if (vec3d.x == 0.0D && vec3d.z == 0.0D) {
               TextureAtlasSprite textureatlassprite1 = atextureatlassprite[0];
               f13 = textureatlassprite1.getU(0.0D);
               f17 = textureatlassprite1.getV(0.0D);
               f14 = f13;
               f18 = textureatlassprite1.getV(16.0D);
               f15 = textureatlassprite1.getU(16.0D);
               f19 = f18;
               f16 = f15;
               f20 = f17;
            } else {
               TextureAtlasSprite textureatlassprite = atextureatlassprite[1];
               float f21 = (float)MathHelper.atan2(vec3d.z, vec3d.x) - ((float)Math.PI / 2F);
               float f22 = MathHelper.sin(f21) * 0.25F;
               float f23 = MathHelper.cos(f21) * 0.25F;
               float f24 = 8.0F;
               f13 = textureatlassprite.getU((double)(8.0F + (-f23 - f22) * 16.0F));
               f17 = textureatlassprite.getV((double)(8.0F + (-f23 + f22) * 16.0F));
               f14 = textureatlassprite.getU((double)(8.0F + (-f23 + f22) * 16.0F));
               f18 = textureatlassprite.getV((double)(8.0F + (f23 + f22) * 16.0F));
               f15 = textureatlassprite.getU((double)(8.0F + (f23 + f22) * 16.0F));
               f19 = textureatlassprite.getV((double)(8.0F + (f23 - f22) * 16.0F));
               f16 = textureatlassprite.getU((double)(8.0F + (f23 - f22) * 16.0F));
               f20 = textureatlassprite.getV((double)(8.0F + (-f23 - f22) * 16.0F));
            }

            float f43 = (f13 + f14 + f15 + f16) / 4.0F;
            float f44 = (f17 + f18 + f19 + f20) / 4.0F;
            float f45 = (float)atextureatlassprite[0].getWidth() / (atextureatlassprite[0].getU1() - atextureatlassprite[0].getU0());
            float f46 = (float)atextureatlassprite[0].getHeight() / (atextureatlassprite[0].getV1() - atextureatlassprite[0].getV0());
            float f47 = 4.0F / Math.max(f46, f45);
            f13 = MathHelper.lerp(f47, f13, f43);
            f14 = MathHelper.lerp(f47, f14, f43);
            f15 = MathHelper.lerp(f47, f15, f43);
            f16 = MathHelper.lerp(f47, f16, f43);
            f17 = MathHelper.lerp(f47, f17, f44);
            f18 = MathHelper.lerp(f47, f18, f44);
            f19 = MathHelper.lerp(f47, f19, f44);
            f20 = MathHelper.lerp(f47, f20, f44);
            int j = this.getLightColor(p_228796_1_, p_228796_2_);
            float f25 = 1.0F * f;
            float f26 = 1.0F * f1;
            float f27 = 1.0F * f2;
            this.vertex(p_228796_3_, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f25, f26, f27, alpha, f13, f17, j);
            this.vertex(p_228796_3_, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f25, f26, f27, alpha, f14, f18, j);
            this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f25, f26, f27, alpha, f15, f19, j);
            this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f25, f26, f27, alpha, f16, f20, j);
            if (p_228796_4_.shouldRenderBackwardUpFace(p_228796_1_, p_228796_2_.above())) {
               this.vertex(p_228796_3_, d0 + 0.0D, d1 + (double)f7, d2 + 0.0D, f25, f26, f27, alpha, f13, f17, j);
               this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f10, d2 + 0.0D, f25, f26, f27, alpha, f16, f20, j);
               this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f9, d2 + 1.0D, f25, f26, f27, alpha, f15, f19, j);
               this.vertex(p_228796_3_, d0 + 0.0D, d1 + (double)f8, d2 + 1.0D, f25, f26, f27, alpha, f14, f18, j);
            }
         }

         if (flag2) {
            float f34 = atextureatlassprite[0].getU0();
            float f35 = atextureatlassprite[0].getU1();
            float f37 = atextureatlassprite[0].getV0();
            float f39 = atextureatlassprite[0].getV1();
            int i1 = this.getLightColor(p_228796_1_, p_228796_2_.below());
            float f40 = 0.5F * f;
            float f41 = 0.5F * f1;
            float f42 = 0.5F * f2;
            this.vertex(p_228796_3_, d0, d1 + (double)f12, d2 + 1.0D, f40, f41, f42, alpha, f34, f39, i1);
            this.vertex(p_228796_3_, d0, d1 + (double)f12, d2, f40, f41, f42, alpha, f34, f37, i1);
            this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f12, d2, f40, f41, f42, alpha, f35, f37, i1);
            this.vertex(p_228796_3_, d0 + 1.0D, d1 + (double)f12, d2 + 1.0D, f40, f41, f42, alpha, f35, f39, i1);
            flag7 = true;
         }

         for(int l = 0; l < 4; ++l) {
            float f36;
            float f38;
            double d3;
            double d4;
            double d5;
            double d6;
            Direction direction;
            boolean flag8;
            if (l == 0) {
               f36 = f7;
               f38 = f10;
               d3 = d0;
               d5 = d0 + 1.0D;
               d4 = d2 + (double)0.001F;
               d6 = d2 + (double)0.001F;
               direction = Direction.NORTH;
               flag8 = flag3;
            } else if (l == 1) {
               f36 = f9;
               f38 = f8;
               d3 = d0 + 1.0D;
               d5 = d0;
               d4 = d2 + 1.0D - (double)0.001F;
               d6 = d2 + 1.0D - (double)0.001F;
               direction = Direction.SOUTH;
               flag8 = flag4;
            } else if (l == 2) {
               f36 = f8;
               f38 = f7;
               d3 = d0 + (double)0.001F;
               d5 = d0 + (double)0.001F;
               d4 = d2 + 1.0D;
               d6 = d2;
               direction = Direction.WEST;
               flag8 = flag5;
            } else {
               f36 = f10;
               f38 = f9;
               d3 = d0 + 1.0D - (double)0.001F;
               d5 = d0 + 1.0D - (double)0.001F;
               d4 = d2;
               d6 = d2 + 1.0D;
               direction = Direction.EAST;
               flag8 = flag6;
            }

            if (flag8 && !func_209556_a(p_228796_1_, p_228796_2_, direction, Math.max(f36, f38))) {
               flag7 = true;
               BlockPos blockpos = p_228796_2_.relative(direction);
               TextureAtlasSprite textureatlassprite2 = atextureatlassprite[1];
               if (atextureatlassprite[2] != null) {
                  if (p_228796_1_.getBlockState(blockpos).shouldDisplayFluidOverlay(p_228796_1_, blockpos, p_228796_4_)) {
                     textureatlassprite2 = atextureatlassprite[2];
                  }
               }

               float f48 = textureatlassprite2.getU(0.0D);
               float f49 = textureatlassprite2.getU(8.0D);
               float f50 = textureatlassprite2.getV((double)((1.0F - f36) * 16.0F * 0.5F));
               float f28 = textureatlassprite2.getV((double)((1.0F - f38) * 16.0F * 0.5F));
               float f29 = textureatlassprite2.getV(8.0D);
               int k = this.getLightColor(p_228796_1_, blockpos);
               float f30 = l < 2 ? 0.8F : 0.6F;
               float f31 = 1.0F * f30 * f;
               float f32 = 1.0F * f30 * f1;
               float f33 = 1.0F * f30 * f2;
               this.vertex(p_228796_3_, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
               this.vertex(p_228796_3_, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
               this.vertex(p_228796_3_, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
               this.vertex(p_228796_3_, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
               if (textureatlassprite2 != atextureatlassprite[2]) {
                  this.vertex(p_228796_3_, d3, d1 + (double)f12, d4, f31, f32, f33, alpha, f48, f29, k);
                  this.vertex(p_228796_3_, d5, d1 + (double)f12, d6, f31, f32, f33, alpha, f49, f29, k);
                  this.vertex(p_228796_3_, d5, d1 + (double)f38, d6, f31, f32, f33, alpha, f49, f28, k);
                  this.vertex(p_228796_3_, d3, d1 + (double)f36, d4, f31, f32, f33, alpha, f48, f50, k);
               }
            }
         }

         return flag7;
      }
   }

   @Deprecated
   private void vertex(IVertexBuilder p_228797_1_, double p_228797_2_, double p_228797_4_, double p_228797_6_, float p_228797_8_, float p_228797_9_, float p_228797_10_, float p_228797_11_, float p_228797_12_, int p_228797_13_) {
      vertex(p_228797_1_, p_228797_2_, p_228797_4_, p_228797_6_, p_228797_8_, p_228797_9_, p_228797_10_, 1.0F, p_228797_11_, p_228797_12_, p_228797_13_);
   }

   private void vertex(IVertexBuilder p_228797_1_, double p_228797_2_, double p_228797_4_, double p_228797_6_, float p_228797_8_, float p_228797_9_, float p_228797_10_, float alpha, float p_228797_11_, float p_228797_12_, int p_228797_13_) {
      p_228797_1_.vertex(p_228797_2_, p_228797_4_, p_228797_6_).color(p_228797_8_, p_228797_9_, p_228797_10_, alpha).uv(p_228797_11_, p_228797_12_).uv2(p_228797_13_).normal(0.0F, 1.0F, 0.0F).endVertex();
   }

   private int getLightColor(ILightReader p_228795_1_, BlockPos p_228795_2_) {
      int i = WorldRenderer.getLightColor(p_228795_1_, p_228795_2_);
      int j = WorldRenderer.getLightColor(p_228795_1_, p_228795_2_.above());
      int k = i & 255;
      int l = j & 255;
      int i1 = i >> 16 & 255;
      int j1 = j >> 16 & 255;
      return (k > l ? k : l) | (i1 > j1 ? i1 : j1) << 16;
   }

   private float getWaterHeight(IBlockReader p_217640_1_, BlockPos p_217640_2_, Fluid p_217640_3_) {
      int i = 0;
      float f = 0.0F;

      for(int j = 0; j < 4; ++j) {
         BlockPos blockpos = p_217640_2_.offset(-(j & 1), 0, -(j >> 1 & 1));
         if (p_217640_1_.getFluidState(blockpos.above()).getType().isSame(p_217640_3_)) {
            return 1.0F;
         }

         IFluidState ifluidstate = p_217640_1_.getFluidState(blockpos);
         if (ifluidstate.getType().isSame(p_217640_3_)) {
            float f1 = ifluidstate.getHeight(p_217640_1_, blockpos);
            if (f1 >= 0.8F) {
               f += f1 * 10.0F;
               i += 10;
            } else {
               f += f1;
               ++i;
            }
         } else if (!p_217640_1_.getBlockState(blockpos).getMaterial().isSolid()) {
            ++i;
         }
      }

      return f / (float)i;
   }
}
