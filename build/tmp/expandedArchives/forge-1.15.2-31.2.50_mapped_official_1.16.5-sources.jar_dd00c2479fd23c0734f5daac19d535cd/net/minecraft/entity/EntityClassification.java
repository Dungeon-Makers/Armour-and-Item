package net.minecraft.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum EntityClassification implements net.minecraftforge.common.IExtensibleEnum {
   MONSTER("monster", 70, false, false),
   CREATURE("creature", 10, true, true),
   AMBIENT("ambient", 15, true, false),
   WATER_CREATURE("water_creature", 15, true, false),
   MISC("misc", 15, true, false);

   private static final Map<String, EntityClassification> BY_NAME = Arrays.stream(values()).collect(Collectors.toMap(EntityClassification::getName, (p_220362_0_) -> {
      return p_220362_0_;
   }));
   private final int max;
   private final boolean isFriendly;
   private final boolean isPersistent;
   private final String name;

   private EntityClassification(String p_i50381_3_, int p_i50381_4_, boolean p_i50381_5_, boolean p_i50381_6_) {
      this.name = p_i50381_3_;
      this.max = p_i50381_4_;
      this.isFriendly = p_i50381_5_;
      this.isPersistent = p_i50381_6_;
   }

   public String getName() {
      return this.name;
   }

   public int getMaxInstancesPerChunk() {
      return this.max;
   }

   public boolean isFriendly() {
      return this.isFriendly;
   }

   public boolean isPersistent() {
      return this.isPersistent;
   }

   public static EntityClassification create(String name, String p_i50381_3_, int p_i50381_4_, boolean p_i50381_5_, boolean p_i50381_6_) {
      throw new IllegalStateException("Enum not extended");
   }
}
