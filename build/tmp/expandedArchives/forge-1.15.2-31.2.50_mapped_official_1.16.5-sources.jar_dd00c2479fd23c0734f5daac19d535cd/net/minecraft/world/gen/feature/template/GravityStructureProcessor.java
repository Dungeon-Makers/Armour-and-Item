package net.minecraft.world.gen.feature.template;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.gen.Heightmap;

public class GravityStructureProcessor extends StructureProcessor {
   private final Heightmap.Type heightmap;
   private final int offset;

   public GravityStructureProcessor(Heightmap.Type p_i51328_1_, int p_i51328_2_) {
      this.heightmap = p_i51328_1_;
      this.offset = p_i51328_2_;
   }

   public GravityStructureProcessor(Dynamic<?> p_i51329_1_) {
      this(Heightmap.Type.getFromKey(p_i51329_1_.get("heightmap").asString(Heightmap.Type.WORLD_SURFACE_WG.getSerializationKey())), p_i51329_1_.get("offset").asInt(0));
   }

   @Nullable
   public Template.BlockInfo func_215194_a(IWorldReader p_215194_1_, BlockPos p_215194_2_, Template.BlockInfo p_215194_3_, Template.BlockInfo p_215194_4_, PlacementSettings p_215194_5_) {
      int i = p_215194_1_.getHeight(this.heightmap, p_215194_4_.pos.getX(), p_215194_4_.pos.getZ()) + this.offset;
      int j = p_215194_3_.pos.getY();
      return new Template.BlockInfo(new BlockPos(p_215194_4_.pos.getX(), i + j, p_215194_4_.pos.getZ()), p_215194_4_.state, p_215194_4_.nbt);
   }

   protected IStructureProcessorType getType() {
      return IStructureProcessorType.GRAVITY;
   }

   protected <T> Dynamic<T> func_215193_a(DynamicOps<T> p_215193_1_) {
      return new Dynamic<>(p_215193_1_, p_215193_1_.createMap(ImmutableMap.of(p_215193_1_.createString("heightmap"), p_215193_1_.createString(this.heightmap.getSerializationKey()), p_215193_1_.createString("offset"), p_215193_1_.createInt(this.offset))));
   }
}