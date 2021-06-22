package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrays;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.template.GravityStructureProcessor;
import net.minecraft.world.gen.feature.template.StructureProcessor;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class JigsawPattern {
   public static final JigsawPattern field_214949_a = new JigsawPattern(new ResourceLocation("empty"), new ResourceLocation("empty"), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID);
   public static final JigsawPattern field_214950_b = new JigsawPattern(new ResourceLocation("invalid"), new ResourceLocation("invalid"), ImmutableList.of(), JigsawPattern.PlacementBehaviour.RIGID);
   private final ResourceLocation name;
   private final ImmutableList<Pair<JigsawPiece, Integer>> rawTemplates;
   private final List<JigsawPiece> templates;
   private final ResourceLocation fallback;
   private final JigsawPattern.PlacementBehaviour field_214955_g;
   private int maxSize = Integer.MIN_VALUE;

   public JigsawPattern(ResourceLocation p_i51397_1_, ResourceLocation p_i51397_2_, List<Pair<JigsawPiece, Integer>> p_i51397_3_, JigsawPattern.PlacementBehaviour p_i51397_4_) {
      this.name = p_i51397_1_;
      this.rawTemplates = ImmutableList.copyOf(p_i51397_3_);
      this.templates = Lists.newArrayList();

      for(Pair<JigsawPiece, Integer> pair : p_i51397_3_) {
         for(Integer integer = 0; integer < pair.getSecond(); integer = integer + 1) {
            this.templates.add(pair.getFirst().setProjection(p_i51397_4_));
         }
      }

      this.fallback = p_i51397_2_;
      this.field_214955_g = p_i51397_4_;
   }

   public int getMaxSize(TemplateManager p_214945_1_) {
      if (this.maxSize == Integer.MIN_VALUE) {
         this.maxSize = this.templates.stream().mapToInt((p_214942_1_) -> {
            return p_214942_1_.getBoundingBox(p_214945_1_, BlockPos.ZERO, Rotation.NONE).getYSpan();
         }).max().orElse(0);
      }

      return this.maxSize;
   }

   public ResourceLocation getFallback() {
      return this.fallback;
   }

   public JigsawPiece getRandomTemplate(Random p_214944_1_) {
      return this.templates.get(p_214944_1_.nextInt(this.templates.size()));
   }

   public List<JigsawPiece> getShuffledTemplates(Random p_214943_1_) {
      return ImmutableList.copyOf(ObjectArrays.shuffle(this.templates.toArray(new JigsawPiece[0]), p_214943_1_));
   }

   public ResourceLocation getName() {
      return this.name;
   }

   public int size() {
      return this.templates.size();
   }

   public static enum PlacementBehaviour implements net.minecraftforge.common.IExtensibleEnum {
      TERRAIN_MATCHING("terrain_matching", ImmutableList.of(new GravityStructureProcessor(Heightmap.Type.WORLD_SURFACE_WG, -1))),
      RIGID("rigid", ImmutableList.of());

      private static final Map<String, JigsawPattern.PlacementBehaviour> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(JigsawPattern.PlacementBehaviour::getName, (p_214935_0_) -> {
         return p_214935_0_;
      }));
      private final String name;
      private final ImmutableList<StructureProcessor> processors;

      private PlacementBehaviour(String p_i50487_3_, ImmutableList<StructureProcessor> p_i50487_4_) {
         this.name = p_i50487_3_;
         this.processors = p_i50487_4_;
      }

      public String getName() {
         return this.name;
      }

      public static JigsawPattern.PlacementBehaviour byName(String p_214938_0_) {
         return BY_NAME.get(p_214938_0_);
      }

      public ImmutableList<StructureProcessor> getProcessors() {
         return this.processors;
      }
      
      public static PlacementBehaviour create(String enumName, String p_i50487_3_, ImmutableList<StructureProcessor> p_i50487_4_) {
         throw new IllegalStateException("Enum not extended");
      }

      @Override
      @Deprecated
      public void init() {
         BY_NAME.put(getName(), this);
      }
   }
}
