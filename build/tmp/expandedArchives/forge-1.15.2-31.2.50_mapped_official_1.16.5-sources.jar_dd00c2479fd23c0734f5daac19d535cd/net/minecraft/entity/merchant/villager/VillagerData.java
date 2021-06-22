package net.minecraft.entity.merchant.villager;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import net.minecraft.entity.villager.IVillagerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class VillagerData {
   private static final int[] NEXT_LEVEL_XP_THRESHOLDS = new int[]{0, 10, 70, 150, 250};
   private final IVillagerType type;
   private final VillagerProfession profession;
   private final int level;

   public VillagerData(IVillagerType p_i50180_1_, VillagerProfession p_i50180_2_, int p_i50180_3_) {
      this.type = p_i50180_1_;
      this.profession = p_i50180_2_;
      this.level = Math.max(1, p_i50180_3_);
   }

   public VillagerData(Dynamic<?> p_i50181_1_) {
      this(Registry.VILLAGER_TYPE.get(ResourceLocation.tryParse(p_i50181_1_.get("type").asString(""))), Registry.VILLAGER_PROFESSION.get(ResourceLocation.tryParse(p_i50181_1_.get("profession").asString(""))), p_i50181_1_.get("level").asInt(1));
   }

   public IVillagerType getType() {
      return this.type;
   }

   public VillagerProfession getProfession() {
      return this.profession;
   }

   public int getLevel() {
      return this.level;
   }

   public VillagerData setType(IVillagerType p_221134_1_) {
      return new VillagerData(p_221134_1_, this.profession, this.level);
   }

   public VillagerData setProfession(VillagerProfession p_221126_1_) {
      return new VillagerData(this.type, p_221126_1_, this.level);
   }

   public VillagerData setLevel(int p_221135_1_) {
      return new VillagerData(this.type, this.profession, p_221135_1_);
   }

   public <T> T func_221131_a(DynamicOps<T> p_221131_1_) {
      return p_221131_1_.createMap(ImmutableMap.of(p_221131_1_.createString("type"), p_221131_1_.createString(Registry.VILLAGER_TYPE.getKey(this.type).toString()), p_221131_1_.createString("profession"), p_221131_1_.createString(Registry.VILLAGER_PROFESSION.getKey(this.profession).toString()), p_221131_1_.createString("level"), p_221131_1_.createInt(this.level)));
   }

   @OnlyIn(Dist.CLIENT)
   public static int getMinXpPerLevel(int p_221133_0_) {
      return canLevelUp(p_221133_0_) ? NEXT_LEVEL_XP_THRESHOLDS[p_221133_0_ - 1] : 0;
   }

   public static int getMaxXpPerLevel(int p_221127_0_) {
      return canLevelUp(p_221127_0_) ? NEXT_LEVEL_XP_THRESHOLDS[p_221127_0_] : 0;
   }

   public static boolean canLevelUp(int p_221128_0_) {
      return p_221128_0_ >= 1 && p_221128_0_ < 5;
   }
}