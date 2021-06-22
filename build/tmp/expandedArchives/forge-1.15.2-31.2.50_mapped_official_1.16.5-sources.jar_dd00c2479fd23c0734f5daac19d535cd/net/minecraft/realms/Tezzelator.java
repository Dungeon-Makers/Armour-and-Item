package net.minecraft.realms;

import net.minecraft.client.renderer.Tessellator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class Tezzelator {
   public static final Tessellator t = Tessellator.getInstance();
   public static final Tezzelator instance = new Tezzelator();

   public void end() {
      t.end();
   }

   public Tezzelator vertex(double p_vertex_1_, double p_vertex_3_, double p_vertex_5_) {
      t.getBuilder().vertex(p_vertex_1_, p_vertex_3_, p_vertex_5_);
      return this;
   }

   public void begin(int p_begin_1_, RealmsVertexFormat p_begin_2_) {
      t.getBuilder().begin(p_begin_1_, p_begin_2_.getVertexFormat());
   }

   public void endVertex() {
      t.getBuilder().endVertex();
   }

   public Tezzelator color(int p_color_1_, int p_color_2_, int p_color_3_, int p_color_4_) {
      t.getBuilder().color(p_color_1_, p_color_2_, p_color_3_, p_color_4_);
      return this;
   }

   public Tezzelator tex(float p_tex_1_, float p_tex_2_) {
      t.getBuilder().uv(p_tex_1_, p_tex_2_);
      return this;
   }
}