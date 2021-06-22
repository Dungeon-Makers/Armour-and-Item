package net.minecraft.world.gen.feature.jigsaw;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public class JigsawPatternRegistry {
   private final Map<ResourceLocation, JigsawPattern> field_214934_a = Maps.newHashMap();

   public JigsawPatternRegistry() {
      this.func_214932_a(JigsawPattern.field_214949_a);
   }

   public void func_214932_a(JigsawPattern p_214932_1_) {
      this.field_214934_a.put(p_214932_1_.getName(), p_214932_1_);
   }

   public JigsawPattern func_214933_a(ResourceLocation p_214933_1_) {
      JigsawPattern jigsawpattern = this.field_214934_a.get(p_214933_1_);
      return jigsawpattern != null ? jigsawpattern : JigsawPattern.field_214950_b;
   }
}