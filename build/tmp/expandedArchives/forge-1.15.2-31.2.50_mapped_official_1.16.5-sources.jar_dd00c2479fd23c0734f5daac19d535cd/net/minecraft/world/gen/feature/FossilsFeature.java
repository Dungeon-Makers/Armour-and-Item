package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.BlockIgnoreStructureProcessor;
import net.minecraft.world.gen.feature.template.IntegrityProcessor;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.server.ServerWorld;

public class FossilsFeature extends Feature<NoFeatureConfig> {
   private static final ResourceLocation SPINE_1 = new ResourceLocation("fossil/spine_1");
   private static final ResourceLocation SPINE_2 = new ResourceLocation("fossil/spine_2");
   private static final ResourceLocation SPINE_3 = new ResourceLocation("fossil/spine_3");
   private static final ResourceLocation SPINE_4 = new ResourceLocation("fossil/spine_4");
   private static final ResourceLocation SPINE_1_COAL = new ResourceLocation("fossil/spine_1_coal");
   private static final ResourceLocation SPINE_2_COAL = new ResourceLocation("fossil/spine_2_coal");
   private static final ResourceLocation SPINE_3_COAL = new ResourceLocation("fossil/spine_3_coal");
   private static final ResourceLocation SPINE_4_COAL = new ResourceLocation("fossil/spine_4_coal");
   private static final ResourceLocation SKULL_1 = new ResourceLocation("fossil/skull_1");
   private static final ResourceLocation SKULL_2 = new ResourceLocation("fossil/skull_2");
   private static final ResourceLocation SKULL_3 = new ResourceLocation("fossil/skull_3");
   private static final ResourceLocation SKULL_4 = new ResourceLocation("fossil/skull_4");
   private static final ResourceLocation SKULL_1_COAL = new ResourceLocation("fossil/skull_1_coal");
   private static final ResourceLocation SKULL_2_COAL = new ResourceLocation("fossil/skull_2_coal");
   private static final ResourceLocation SKULL_3_COAL = new ResourceLocation("fossil/skull_3_coal");
   private static final ResourceLocation SKULL_4_COAL = new ResourceLocation("fossil/skull_4_coal");
   private static final ResourceLocation[] fossils = new ResourceLocation[]{SPINE_1, SPINE_2, SPINE_3, SPINE_4, SKULL_1, SKULL_2, SKULL_3, SKULL_4};
   private static final ResourceLocation[] fossilsCoal = new ResourceLocation[]{SPINE_1_COAL, SPINE_2_COAL, SPINE_3_COAL, SPINE_4_COAL, SKULL_1_COAL, SKULL_2_COAL, SKULL_3_COAL, SKULL_4_COAL};

   public FossilsFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49873_1_) {
      super(p_i49873_1_);
   }

   public boolean func_212245_a(IWorld p_212245_1_, ChunkGenerator<? extends GenerationSettings> p_212245_2_, Random p_212245_3_, BlockPos p_212245_4_, NoFeatureConfig p_212245_5_) {
      Random random = p_212245_1_.getRandom();
      Rotation[] arotation = Rotation.values();
      Rotation rotation = arotation[random.nextInt(arotation.length)];
      int i = random.nextInt(fossils.length);
      TemplateManager templatemanager = ((ServerWorld)p_212245_1_.getLevel()).func_217485_w().func_186340_h();
      Template template = templatemanager.getOrCreate(fossils[i]);
      Template template1 = templatemanager.getOrCreate(fossilsCoal[i]);
      ChunkPos chunkpos = new ChunkPos(p_212245_4_);
      MutableBoundingBox mutableboundingbox = new MutableBoundingBox(chunkpos.getMinBlockX(), 0, chunkpos.getMinBlockZ(), chunkpos.getMaxBlockX(), 256, chunkpos.getMaxBlockZ());
      PlacementSettings placementsettings = (new PlacementSettings()).setRotation(rotation).setBoundingBox(mutableboundingbox).setRandom(random).addProcessor(BlockIgnoreStructureProcessor.STRUCTURE_AND_AIR);
      BlockPos blockpos = template.getSize(rotation);
      int j = random.nextInt(16 - blockpos.getX());
      int k = random.nextInt(16 - blockpos.getZ());
      int l = 256;

      for(int i1 = 0; i1 < blockpos.getX(); ++i1) {
         for(int j1 = 0; j1 < blockpos.getZ(); ++j1) {
            l = Math.min(l, p_212245_1_.getHeight(Heightmap.Type.OCEAN_FLOOR_WG, p_212245_4_.getX() + i1 + j, p_212245_4_.getZ() + j1 + k));
         }
      }

      int k1 = Math.max(l - 15 - random.nextInt(10), 10);
      BlockPos blockpos1 = template.getZeroPositionWithTransform(p_212245_4_.offset(j, k1, k), Mirror.NONE, rotation);
      IntegrityProcessor integrityprocessor = new IntegrityProcessor(0.9F);
      placementsettings.clearProcessors().addProcessor(integrityprocessor);
      template.func_189962_a(p_212245_1_, blockpos1, placementsettings, 4);
      placementsettings.popProcessor(integrityprocessor);
      IntegrityProcessor integrityprocessor1 = new IntegrityProcessor(0.1F);
      placementsettings.clearProcessors().addProcessor(integrityprocessor1);
      template1.func_189962_a(p_212245_1_, blockpos1, placementsettings, 4);
      return true;
   }
}