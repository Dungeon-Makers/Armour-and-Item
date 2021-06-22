package net.minecraft.fluid;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.IProperty;
import net.minecraft.state.IStateHolder;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IFluidState extends IStateHolder<IFluidState>, net.minecraftforge.common.extensions.IForgeFluidState {
   Fluid getType();

   default boolean isSource() {
      return this.getType().isSource(this);
   }

   default boolean isEmpty() {
      return this.getType().isEmpty();
   }

   default float getHeight(IBlockReader p_215679_1_, BlockPos p_215679_2_) {
      return this.getType().getHeight(this, p_215679_1_, p_215679_2_);
   }

   default float getOwnHeight() {
      return this.getType().getOwnHeight(this);
   }

   default int getAmount() {
      return this.getType().getAmount(this);
   }

   @OnlyIn(Dist.CLIENT)
   default boolean shouldRenderBackwardUpFace(IBlockReader p_205586_1_, BlockPos p_205586_2_) {
      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos = p_205586_2_.offset(i, 0, j);
            IFluidState ifluidstate = p_205586_1_.getFluidState(blockpos);
            if (!ifluidstate.getType().isSame(this.getType()) && !p_205586_1_.getBlockState(blockpos).isSolidRender(p_205586_1_, blockpos)) {
               return true;
            }
         }
      }

      return false;
   }

   default void tick(World p_206880_1_, BlockPos p_206880_2_) {
      this.getType().tick(p_206880_1_, p_206880_2_, this);
   }

   @OnlyIn(Dist.CLIENT)
   default void animateTick(World p_206881_1_, BlockPos p_206881_2_, Random p_206881_3_) {
      this.getType().animateTick(p_206881_1_, p_206881_2_, this, p_206881_3_);
   }

   default boolean isRandomlyTicking() {
      return this.getType().isRandomlyTicking();
   }

   default void randomTick(World p_206891_1_, BlockPos p_206891_2_, Random p_206891_3_) {
      this.getType().randomTick(p_206891_1_, p_206891_2_, this, p_206891_3_);
   }

   default Vec3d getFlow(IBlockReader p_215673_1_, BlockPos p_215673_2_) {
      return this.getType().getFlow(p_215673_1_, p_215673_2_, this);
   }

   default BlockState createLegacyBlock() {
      return this.getType().createLegacyBlock(this);
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   default IParticleData getDripParticle() {
      return this.getType().getDripParticle();
   }

   default boolean is(Tag<Fluid> p_206884_1_) {
      return this.getType().is(p_206884_1_);
   }

   @Deprecated //Forge: Use more sensitive version.
   default float getExplosionResistance() {
      return this.getType().getExplosionResistance();
   }

   default boolean canBeReplacedWith(IBlockReader p_215677_1_, BlockPos p_215677_2_, Fluid p_215677_3_, Direction p_215677_4_) {
      return this.getType().canBeReplacedWith(this, p_215677_1_, p_215677_2_, p_215677_3_, p_215677_4_);
   }

   static <T> Dynamic<T> func_215680_a(DynamicOps<T> p_215680_0_, IFluidState p_215680_1_) {
      ImmutableMap<IProperty<?>, Comparable<?>> immutablemap = p_215680_1_.getValues();
      T t;
      if (immutablemap.isEmpty()) {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getType()).toString())));
      } else {
         t = p_215680_0_.createMap(ImmutableMap.of(p_215680_0_.createString("Name"), p_215680_0_.createString(Registry.FLUID.getKey(p_215680_1_.getType()).toString()), p_215680_0_.createString("Properties"), p_215680_0_.createMap(immutablemap.entrySet().stream().map((p_215675_1_) -> {
            return Pair.of(p_215680_0_.createString(p_215675_1_.getKey().getName()), p_215680_0_.createString(IStateHolder.func_215670_b(p_215675_1_.getKey(), p_215675_1_.getValue())));
         }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)))));
      }

      return new Dynamic<>(p_215680_0_, t);
   }

   static <T> IFluidState func_215681_a(Dynamic<T> p_215681_0_) {
      Fluid fluid = Registry.FLUID.get(new ResourceLocation(p_215681_0_.getElement("Name").flatMap(p_215681_0_.getOps()::getStringValue).orElse("minecraft:empty")));
      Map<String, String> map = p_215681_0_.get("Properties").asMap((p_215678_0_) -> {
         return p_215678_0_.asString("");
      }, (p_215674_0_) -> {
         return p_215674_0_.asString("");
      });
      IFluidState ifluidstate = fluid.defaultFluidState();
      StateContainer<Fluid, IFluidState> statecontainer = fluid.getStateDefinition();

      for(Entry<String, String> entry : map.entrySet()) {
         String s = entry.getKey();
         IProperty<?> iproperty = statecontainer.getProperty(s);
         if (iproperty != null) {
            ifluidstate = IStateHolder.func_215671_a(ifluidstate, iproperty, s, p_215681_0_.toString(), entry.getValue());
         }
      }

      return ifluidstate;
   }

   default VoxelShape getShape(IBlockReader p_215676_1_, BlockPos p_215676_2_) {
      return this.getType().getShape(this, p_215676_1_, p_215676_2_);
   }
}
