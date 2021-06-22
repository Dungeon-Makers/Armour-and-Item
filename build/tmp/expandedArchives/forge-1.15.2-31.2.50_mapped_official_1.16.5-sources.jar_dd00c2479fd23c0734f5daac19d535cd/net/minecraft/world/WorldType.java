package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldType implements net.minecraftforge.common.extensions.IForgeWorldType {
   public static WorldType[] field_77139_a = new WorldType[16];
   public static final WorldType field_77137_b = (new WorldType(0, "default", 1)).func_77129_f();
   public static final WorldType field_77138_c = (new WorldType(1, "flat")).func_205392_a(true);
   public static final WorldType field_77135_d = new WorldType(2, "largeBiomes");
   public static final WorldType field_151360_e = (new WorldType(3, "amplified")).func_151358_j();
   public static final WorldType field_180271_f = (new WorldType(4, "customized", "normal", 0)).func_205392_a(true).func_77124_a(false);
   public static final WorldType field_205394_h = (new WorldType(5, "buffet")).func_205392_a(true);
   public static final WorldType field_180272_g = new WorldType(6, "debug_all_block_states");
   public static final WorldType field_77136_e = (new WorldType(8, "default_1_1", 0)).func_77124_a(false);
   private final int field_82748_f;
   private final String field_77133_f;
   private final String field_211890_l;
   private final int field_77134_g;
   private boolean field_77140_h;
   private boolean field_77141_i;
   private boolean field_151361_l;
   private boolean field_205395_p;

   public WorldType(String name) {
      this(getNextID(), name);
   }

   private WorldType(int p_i1959_1_, String p_i1959_2_) {
      this(p_i1959_1_, p_i1959_2_, p_i1959_2_, 0);
   }

   private WorldType(int p_i1960_1_, String p_i1960_2_, int p_i1960_3_) {
      this(p_i1960_1_, p_i1960_2_, p_i1960_2_, p_i1960_3_);
   }

   private WorldType(int p_i49778_1_, String p_i49778_2_, String p_i49778_3_, int p_i49778_4_) {
      if (p_i49778_2_.length() > 16 && field_180272_g != null) throw new IllegalArgumentException("World type names must not be longer then 16: " + p_i49778_2_);
      this.field_77133_f = p_i49778_2_;
      this.field_211890_l = p_i49778_3_;
      this.field_77134_g = p_i49778_4_;
      this.field_77140_h = true;
      this.field_82748_f = p_i49778_1_;
      field_77139_a[p_i49778_1_] = this;
   }

   private static int getNextID() {
      for (int x = 0; x < field_77139_a.length; x++) {
         if (field_77139_a[x] == null)
            return x;
      }
      int old = field_77139_a.length;
      field_77139_a = java.util.Arrays.copyOf(field_77139_a, old + 16);
      return old;
   }

   public String func_211888_a() {
      return this.field_77133_f;
   }

   public String func_211889_b() {
      return this.field_211890_l;
   }

   @OnlyIn(Dist.CLIENT)
   public String func_77128_b() {
      return "generator." + this.field_77133_f;
   }

   @OnlyIn(Dist.CLIENT)
   public String func_151359_c() {
      return this.func_77128_b() + ".info";
   }

   public int func_77131_c() {
      return this.field_77134_g;
   }

   public WorldType func_77132_a(int p_77132_1_) {
      return this == field_77137_b && p_77132_1_ == 0 ? field_77136_e : this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_205393_e() {
      return this.field_205395_p;
   }

   public WorldType func_205392_a(boolean p_205392_1_) {
      this.field_205395_p = p_205392_1_;
      return this;
   }

   private WorldType func_77124_a(boolean p_77124_1_) {
      this.field_77140_h = p_77124_1_;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_77126_d() {
      return this.field_77140_h;
   }

   private WorldType func_77129_f() {
      this.field_77141_i = true;
      return this;
   }

   public boolean func_77125_e() {
      return this.field_77141_i;
   }

   @Nullable
   public static WorldType func_77130_a(String p_77130_0_) {
      for(WorldType worldtype : field_77139_a) {
         if (worldtype != null && worldtype.field_77133_f.equalsIgnoreCase(p_77130_0_)) {
            return worldtype;
         }
      }

      return null;
   }

   public int func_82747_f() {
      return this.field_82748_f;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_151357_h() {
      return this.field_151361_l;
   }

   private WorldType func_151358_j() {
      this.field_151361_l = true;
      return this;
   }
}
