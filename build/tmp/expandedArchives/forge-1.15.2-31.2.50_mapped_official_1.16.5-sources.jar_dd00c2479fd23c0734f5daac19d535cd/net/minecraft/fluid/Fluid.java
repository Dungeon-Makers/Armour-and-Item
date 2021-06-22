package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.particles.IParticleData;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.ObjectIntIdentityMap;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Fluid extends net.minecraftforge.registries.ForgeRegistryEntry<Fluid> implements net.minecraftforge.common.extensions.IForgeFluid {
   public static final ObjectIntIdentityMap<IFluidState> FLUID_STATE_REGISTRY = new ObjectIntIdentityMap<>();
   protected final StateContainer<Fluid, IFluidState> stateDefinition;
   private IFluidState defaultFluidState;

   protected Fluid() {
      StateContainer.Builder<Fluid, IFluidState> builder = new StateContainer.Builder<>(this);
      this.createFluidStateDefinition(builder);
      this.stateDefinition = builder.func_206893_a(FluidState::new);
      this.registerDefaultState(this.stateDefinition.any());
   }

   protected void createFluidStateDefinition(StateContainer.Builder<Fluid, IFluidState> p_207184_1_) {
   }

   public StateContainer<Fluid, IFluidState> getStateDefinition() {
      return this.stateDefinition;
   }

   protected final void registerDefaultState(IFluidState p_207183_1_) {
      this.defaultFluidState = p_207183_1_;
   }

   public final IFluidState defaultFluidState() {
      return this.defaultFluidState;
   }

   public abstract Item getBucket();

   @OnlyIn(Dist.CLIENT)
   protected void animateTick(World p_204522_1_, BlockPos p_204522_2_, IFluidState p_204522_3_, Random p_204522_4_) {
   }

   protected void tick(World p_207191_1_, BlockPos p_207191_2_, IFluidState p_207191_3_) {
   }

   protected void randomTick(World p_207186_1_, BlockPos p_207186_2_, IFluidState p_207186_3_, Random p_207186_4_) {
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   protected IParticleData getDripParticle() {
      return null;
   }

   protected abstract boolean canBeReplacedWith(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_);

   protected abstract Vec3d getFlow(IBlockReader p_215663_1_, BlockPos p_215663_2_, IFluidState p_215663_3_);

   public abstract int getTickDelay(IWorldReader p_205569_1_);

   protected boolean isRandomlyTicking() {
      return false;
   }

   protected boolean isEmpty() {
      return false;
   }

   protected abstract float getExplosionResistance();

   public abstract float getHeight(IFluidState p_215662_1_, IBlockReader p_215662_2_, BlockPos p_215662_3_);

   public abstract float getOwnHeight(IFluidState p_223407_1_);

   protected abstract BlockState createLegacyBlock(IFluidState p_204527_1_);

   public abstract boolean isSource(IFluidState p_207193_1_);

   public abstract int getAmount(IFluidState p_207192_1_);

   public boolean isSame(Fluid p_207187_1_) {
      return p_207187_1_ == this;
   }

   public boolean is(Tag<Fluid> p_207185_1_) {
      return p_207185_1_.func_199685_a_(this);
   }

   public abstract VoxelShape getShape(IFluidState p_215664_1_, IBlockReader p_215664_2_, BlockPos p_215664_3_);

   private final net.minecraftforge.common.util.ReverseTagWrapper<Fluid> reverseTags = new net.minecraftforge.common.util.ReverseTagWrapper<>(this, net.minecraft.tags.FluidTags::getGeneration, net.minecraft.tags.FluidTags::getAllTags);
   @Override
   public java.util.Set<net.minecraft.util.ResourceLocation> getTags() {
      return reverseTags.getTagNames();
   }

   /**
    * Creates the fluid attributes object, which will contain all the extended values for the fluid that aren't part of the vanilla system.
    * Do not call this from outside. To retrieve the values use {@link Fluid#getAttributes()}
    */
   protected net.minecraftforge.fluids.FluidAttributes createAttributes()
   {
      return net.minecraftforge.common.ForgeHooks.createVanillaFluidAttributes(this);
   }

   private net.minecraftforge.fluids.FluidAttributes forgeFluidAttributes;
   public final net.minecraftforge.fluids.FluidAttributes getAttributes() {
      if (forgeFluidAttributes == null)
         forgeFluidAttributes = createAttributes();
      return forgeFluidAttributes;
   }
}
