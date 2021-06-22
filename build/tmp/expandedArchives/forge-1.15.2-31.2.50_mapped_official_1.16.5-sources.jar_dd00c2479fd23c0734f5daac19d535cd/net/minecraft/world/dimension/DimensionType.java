package net.minecraft.world.dimension;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.io.File;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.biome.ColumnFuzzedBiomeMagnifier;
import net.minecraft.world.biome.FuzzedBiomeMagnifier;
import net.minecraft.world.biome.IBiomeMagnifier;

public class DimensionType extends net.minecraftforge.registries.ForgeRegistryEntry<DimensionType> implements IDynamicSerializable {
   public static final DimensionType field_223227_a_ = func_212677_a("overworld", new DimensionType(1, "", "", OverworldDimension::new, true, ColumnFuzzedBiomeMagnifier.INSTANCE));
   public static final DimensionType field_223228_b_ = func_212677_a("the_nether", new DimensionType(0, "_nether", "DIM-1", NetherDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
   public static final DimensionType field_223229_c_ = func_212677_a("the_end", new DimensionType(2, "_end", "DIM1", EndDimension::new, false, FuzzedBiomeMagnifier.INSTANCE));
   private final int field_186074_d;
   private final String field_186076_f;
   private final String field_212682_f;
   private final BiFunction<World, DimensionType, ? extends Dimension> field_201038_g;
   private final boolean hasSkylight;
   private final IBiomeMagnifier biomeZoomer;
   private final boolean isVanilla;
   private final net.minecraftforge.common.ModDimension modType;
   private final net.minecraft.network.PacketBuffer data;

   private static DimensionType func_212677_a(String p_212677_0_, DimensionType p_212677_1_) {
      return Registry.registerMapping(Registry.field_212622_k, p_212677_1_.field_186074_d, p_212677_0_, p_212677_1_);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   protected DimensionType(int p_i225789_1_, String p_i225789_2_, String p_i225789_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i225789_4_, boolean p_i225789_5_, IBiomeMagnifier p_i225789_6_) {
      this(p_i225789_1_, p_i225789_2_, p_i225789_3_, p_i225789_4_, p_i225789_5_, p_i225789_6_, null, null);
   }

   //Forge, Internal use only. Use DimensionManager instead.
   @Deprecated
   public DimensionType(int p_i225789_1_, String p_i225789_2_, String p_i225789_3_, BiFunction<World, DimensionType, ? extends Dimension> p_i225789_4_, boolean p_i225789_5_, IBiomeMagnifier p_i225789_6_, @Nullable net.minecraftforge.common.ModDimension modType, @Nullable net.minecraft.network.PacketBuffer data) {
      this.field_186074_d = p_i225789_1_;
      this.field_186076_f = p_i225789_2_;
      this.field_212682_f = p_i225789_3_;
      this.field_201038_g = p_i225789_4_;
      this.hasSkylight = p_i225789_5_;
      this.biomeZoomer = p_i225789_6_;
      this.isVanilla = this.field_186074_d >= 0 && this.field_186074_d <= 2;
      this.modType = modType;
      this.data = data;
   }

   public static DimensionType func_218271_a(Dynamic<?> p_218271_0_) {
      return Registry.field_212622_k.get(new ResourceLocation(p_218271_0_.asString("")));
   }

   public static Iterable<DimensionType> func_212681_b() {
      return Registry.field_212622_k;
   }

   public int func_186068_a() {
      return this.field_186074_d + -1;
   }

   @Deprecated //Forge Do not use, only used for villages backwards compatibility
   public String getFileSuffix() {
      return isVanilla ? this.field_186076_f : "";
   }

   public File func_212679_a(File p_212679_1_) {
      return this.field_212682_f.isEmpty() ? p_212679_1_ : new File(p_212679_1_, this.field_212682_f);
   }

   public Dimension func_218270_a(World p_218270_1_) {
      return this.field_201038_g.apply(p_218270_1_, this);
   }

   public String toString() {
      return "DimensionType{" + func_212678_a(this) + "}";
   }

   @Nullable
   public static DimensionType func_186069_a(int p_186069_0_) {
      return Registry.field_212622_k.byId(p_186069_0_ - -1);
   }

   public boolean isVanilla() {
      return this.isVanilla;
   }

   @Nullable
   public net.minecraftforge.common.ModDimension getModType() {
      return this.modType;
   }

   @Nullable
   public net.minecraft.network.PacketBuffer getData() {
      return this.data;
   }

   @Nullable
   public static DimensionType func_193417_a(ResourceLocation p_193417_0_) {
      return Registry.field_212622_k.get(p_193417_0_);
   }

   @Nullable
   public static ResourceLocation func_212678_a(DimensionType p_212678_0_) {
      return Registry.field_212622_k.getKey(p_212678_0_);
   }

   public boolean hasSkyLight() {
      return this.hasSkylight;
   }

   public IBiomeMagnifier getBiomeZoomer() {
      return this.biomeZoomer;
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createString(Registry.field_212622_k.getKey(this).toString());
   }
}
