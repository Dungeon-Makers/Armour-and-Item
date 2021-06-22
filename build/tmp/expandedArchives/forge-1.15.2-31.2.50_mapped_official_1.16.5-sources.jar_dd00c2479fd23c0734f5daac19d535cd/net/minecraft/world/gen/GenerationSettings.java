package net.minecraft.world.gen;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public class GenerationSettings {
   protected int field_214971_a = 32;
   protected final int field_214972_b = 8;
   protected int field_214973_c = 32;
   protected int field_214974_d = 5;
   protected int field_214975_e = 32;
   protected int field_214976_f = 128;
   protected int field_214977_g = 3;
   protected int field_214978_h = 32;
   protected final int field_214979_i = 8;
   protected final int field_214980_j = 16;
   protected final int field_214981_k = 8;
   protected int field_214982_l = 20;
   protected final int field_214983_m = 11;
   protected final int field_214984_n = 16;
   protected final int field_214985_o = 8;
   protected int field_214986_p = 80;
   protected final int field_214987_q = 20;
   protected BlockState field_214988_r = Blocks.STONE.defaultBlockState();
   protected BlockState field_214989_s = Blocks.WATER.defaultBlockState();

   public int func_202173_a() {
      return this.field_214971_a;
   }

   public int func_211729_b() {
      return 8;
   }

   public int func_202174_b() {
      return this.field_214973_c;
   }

   public int func_202171_c() {
      return this.field_214974_d;
   }

   public int func_202172_d() {
      return this.field_214975_e;
   }

   public int func_202176_e() {
      return this.field_214976_f;
   }

   public int func_202175_f() {
      return this.field_214977_g;
   }

   public int func_202177_g() {
      return this.field_214978_h;
   }

   public int func_211731_i() {
      return 8;
   }

   public int func_204748_h() {
      return 16;
   }

   public int func_211730_k() {
      return 8;
   }

   public int func_204026_h() {
      return 16;
   }

   public int func_211727_m() {
      return 8;
   }

   public int func_202178_h() {
      return this.field_214982_l;
   }

   public int func_211728_o() {
      return 11;
   }

   public int func_202179_i() {
      return this.field_214986_p;
   }

   public int func_211726_q() {
      return 20;
   }

   public BlockState func_205532_l() {
      return this.field_214988_r;
   }

   public BlockState func_205533_m() {
      return this.field_214989_s;
   }

   public void func_214969_a(BlockState p_214969_1_) {
      this.field_214988_r = p_214969_1_;
   }

   public void func_214970_b(BlockState p_214970_1_) {
      this.field_214989_s = p_214970_1_;
   }

   public int func_214967_t() {
      return 0;
   }

   public int func_214968_u() {
      return 256;
   }
}