package net.minecraft.world;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public final class WorldSettings {
   private final long field_77174_a;
   private final GameType gameType;
   private final boolean field_77173_c;
   private final boolean hardcore;
   private final WorldType field_77171_e;
   private boolean allowCommands;
   private boolean field_77169_g;
   private JsonElement field_82751_h = new JsonObject();

   public WorldSettings(long p_i1957_1_, GameType p_i1957_3_, boolean p_i1957_4_, boolean p_i1957_5_, WorldType p_i1957_6_) {
      this.field_77174_a = p_i1957_1_;
      this.gameType = p_i1957_3_;
      this.field_77173_c = p_i1957_4_;
      this.hardcore = p_i1957_5_;
      this.field_77171_e = p_i1957_6_;
   }

   public WorldSettings(WorldInfo p_i1958_1_) {
      this(p_i1958_1_.func_76063_b(), p_i1958_1_.getGameType(), p_i1958_1_.func_76089_r(), p_i1958_1_.isHardcore(), p_i1958_1_.func_76067_t());
   }

   public WorldSettings func_77159_a() {
      this.field_77169_g = true;
      return this;
   }

   @OnlyIn(Dist.CLIENT)
   public WorldSettings func_77166_b() {
      this.allowCommands = true;
      return this;
   }

   public WorldSettings func_205390_a(JsonElement p_205390_1_) {
      this.field_82751_h = p_205390_1_;
      return this;
   }

   public boolean func_77167_c() {
      return this.field_77169_g;
   }

   public long func_77160_d() {
      return this.field_77174_a;
   }

   public GameType func_77162_e() {
      return this.gameType;
   }

   public boolean func_77158_f() {
      return this.hardcore;
   }

   public boolean func_77164_g() {
      return this.field_77173_c;
   }

   public WorldType func_77165_h() {
      return this.field_77171_e;
   }

   public boolean func_77163_i() {
      return this.allowCommands;
   }

   public JsonElement func_205391_j() {
      return this.field_82751_h;
   }
}