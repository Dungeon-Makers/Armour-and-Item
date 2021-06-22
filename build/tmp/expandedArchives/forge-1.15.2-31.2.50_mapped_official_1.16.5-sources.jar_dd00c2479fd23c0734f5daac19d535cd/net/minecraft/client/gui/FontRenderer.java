package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
   public final int lineHeight = 9;
   public final Random random = new Random();
   private final TextureManager field_78298_i;
   private final Font fonts;
   private boolean field_78294_m;

   public FontRenderer(TextureManager p_i49744_1_, Font p_i49744_2_) {
      this.field_78298_i = p_i49744_1_;
      this.fonts = p_i49744_2_;
   }

   public void func_211568_a(List<IGlyphProvider> p_211568_1_) {
      this.fonts.reload(p_211568_1_);
   }

   public void close() {
      this.fonts.close();
   }

   public int func_175063_a(String p_175063_1_, float p_175063_2_, float p_175063_3_, int p_175063_4_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_175063_1_, p_175063_2_, p_175063_3_, p_175063_4_, TransformationMatrix.identity().getMatrix(), true);
   }

   public int func_211126_b(String p_211126_1_, float p_211126_2_, float p_211126_3_, int p_211126_4_) {
      RenderSystem.enableAlphaTest();
      return this.drawInternal(p_211126_1_, p_211126_2_, p_211126_3_, p_211126_4_, TransformationMatrix.identity().getMatrix(), false);
   }

   public String bidirectionalShaping(String p_147647_1_) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(p_147647_1_), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return p_147647_1_;
      }
   }

   private int drawInternal(String p_228078_1_, float p_228078_2_, float p_228078_3_, int p_228078_4_, Matrix4f p_228078_5_, boolean p_228078_6_) {
      if (p_228078_1_ == null) {
         return 0;
      } else {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.immediate(Tessellator.getInstance().getBuilder());
         int i = this.drawInBatch(p_228078_1_, p_228078_2_, p_228078_3_, p_228078_4_, p_228078_6_, p_228078_5_, irendertypebuffer$impl, false, 0, 15728880);
         irendertypebuffer$impl.endBatch();
         return i;
      }
   }

   public int drawInBatch(String p_228079_1_, float p_228079_2_, float p_228079_3_, int p_228079_4_, boolean p_228079_5_, Matrix4f p_228079_6_, IRenderTypeBuffer p_228079_7_, boolean p_228079_8_, int p_228079_9_, int p_228079_10_) {
      return this.func_228080_b_(p_228079_1_, p_228079_2_, p_228079_3_, p_228079_4_, p_228079_5_, p_228079_6_, p_228079_7_, p_228079_8_, p_228079_9_, p_228079_10_);
   }

   private int func_228080_b_(String p_228080_1_, float p_228080_2_, float p_228080_3_, int p_228080_4_, boolean p_228080_5_, Matrix4f p_228080_6_, IRenderTypeBuffer p_228080_7_, boolean p_228080_8_, int p_228080_9_, int p_228080_10_) {
      if (this.field_78294_m) {
         p_228080_1_ = this.bidirectionalShaping(p_228080_1_);
      }

      if ((p_228080_4_ & -67108864) == 0) {
         p_228080_4_ |= -16777216;
      }

      if (p_228080_5_) {
         this.renderText(p_228080_1_, p_228080_2_, p_228080_3_, p_228080_4_, true, p_228080_6_, p_228080_7_, p_228080_8_, p_228080_9_, p_228080_10_);
      }

      Matrix4f matrix4f = p_228080_6_.copy();
      matrix4f.translate(new Vector3f(0.0F, 0.0F, 0.001F));
      p_228080_2_ = this.renderText(p_228080_1_, p_228080_2_, p_228080_3_, p_228080_4_, false, matrix4f, p_228080_7_, p_228080_8_, p_228080_9_, p_228080_10_);
      return (int)p_228080_2_ + (p_228080_5_ ? 1 : 0);
   }

   private float renderText(String p_228081_1_, float p_228081_2_, float p_228081_3_, int p_228081_4_, boolean p_228081_5_, Matrix4f p_228081_6_, IRenderTypeBuffer p_228081_7_, boolean p_228081_8_, int p_228081_9_, int p_228081_10_) {
      float f = p_228081_5_ ? 0.25F : 1.0F;
      float f1 = (float)(p_228081_4_ >> 16 & 255) / 255.0F * f;
      float f2 = (float)(p_228081_4_ >> 8 & 255) / 255.0F * f;
      float f3 = (float)(p_228081_4_ & 255) / 255.0F * f;
      float f4 = p_228081_2_;
      float f5 = f1;
      float f6 = f2;
      float f7 = f3;
      float f8 = (float)(p_228081_4_ >> 24 & 255) / 255.0F;
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      List<TexturedGlyph.Effect> list = Lists.newArrayList();

      for(int i = 0; i < p_228081_1_.length(); ++i) {
         char c0 = p_228081_1_.charAt(i);
         if (c0 == 167 && i + 1 < p_228081_1_.length()) {
            TextFormatting textformatting = TextFormatting.getByCode(p_228081_1_.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.func_211166_f()) {
                  flag = false;
                  flag1 = false;
                  flag4 = false;
                  flag3 = false;
                  flag2 = false;
                  f5 = f1;
                  f6 = f2;
                  f7 = f3;
               }

               if (textformatting.getColor() != null) {
                  int j = textformatting.getColor();
                  f5 = (float)(j >> 16 & 255) / 255.0F * f;
                  f6 = (float)(j >> 8 & 255) / 255.0F * f;
                  f7 = (float)(j & 255) / 255.0F * f;
               } else if (textformatting == TextFormatting.OBFUSCATED) {
                  flag = true;
               } else if (textformatting == TextFormatting.BOLD) {
                  flag1 = true;
               } else if (textformatting == TextFormatting.STRIKETHROUGH) {
                  flag4 = true;
               } else if (textformatting == TextFormatting.UNDERLINE) {
                  flag3 = true;
               } else if (textformatting == TextFormatting.ITALIC) {
                  flag2 = true;
               }
            }

            ++i;
         } else {
            IGlyph iglyph = this.fonts.func_211184_b(c0);
            TexturedGlyph texturedglyph = flag && c0 != ' ' ? this.fonts.getRandomGlyph(iglyph) : this.fonts.func_211187_a(c0);
            if (!(texturedglyph instanceof EmptyGlyph)) {
               float f9 = flag1 ? iglyph.getBoldOffset() : 0.0F;
               float f10 = p_228081_5_ ? iglyph.getShadowOffset() : 0.0F;
               IVertexBuilder ivertexbuilder = p_228081_7_.getBuffer(texturedglyph.renderType(p_228081_8_));
               this.renderChar(texturedglyph, flag1, flag2, f9, f4 + f10, p_228081_3_ + f10, p_228081_6_, ivertexbuilder, f5, f6, f7, f8, p_228081_10_);
            }

            float f15 = iglyph.getAdvance(flag1);
            float f16 = p_228081_5_ ? 1.0F : 0.0F;
            if (flag4) {
               list.add(new TexturedGlyph.Effect(f4 + f16 - 1.0F, p_228081_3_ + f16 + 4.5F, f4 + f16 + f15, p_228081_3_ + f16 + 4.5F - 1.0F, -0.01F, f5, f6, f7, f8));
            }

            if (flag3) {
               list.add(new TexturedGlyph.Effect(f4 + f16 - 1.0F, p_228081_3_ + f16 + 9.0F, f4 + f16 + f15, p_228081_3_ + f16 + 9.0F - 1.0F, -0.01F, f5, f6, f7, f8));
            }

            f4 += f15;
         }
      }

      if (p_228081_9_ != 0) {
         float f11 = (float)(p_228081_9_ >> 24 & 255) / 255.0F;
         float f12 = (float)(p_228081_9_ >> 16 & 255) / 255.0F;
         float f13 = (float)(p_228081_9_ >> 8 & 255) / 255.0F;
         float f14 = (float)(p_228081_9_ & 255) / 255.0F;
         list.add(new TexturedGlyph.Effect(p_228081_2_ - 1.0F, p_228081_3_ + 9.0F, f4 + 1.0F, p_228081_3_ - 1.0F, 0.01F, f12, f13, f14, f11));
      }

      if (!list.isEmpty()) {
         TexturedGlyph texturedglyph1 = this.fonts.whiteGlyph();
         IVertexBuilder ivertexbuilder1 = p_228081_7_.getBuffer(texturedglyph1.renderType(p_228081_8_));

         for(TexturedGlyph.Effect texturedglyph$effect : list) {
            texturedglyph1.renderEffect(texturedglyph$effect, p_228081_6_, ivertexbuilder1, p_228081_10_);
         }
      }

      return f4;
   }

   private void renderChar(TexturedGlyph p_228077_1_, boolean p_228077_2_, boolean p_228077_3_, float p_228077_4_, float p_228077_5_, float p_228077_6_, Matrix4f p_228077_7_, IVertexBuilder p_228077_8_, float p_228077_9_, float p_228077_10_, float p_228077_11_, float p_228077_12_, int p_228077_13_) {
      p_228077_1_.render(p_228077_3_, p_228077_5_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      if (p_228077_2_) {
         p_228077_1_.render(p_228077_3_, p_228077_5_ + p_228077_4_, p_228077_6_, p_228077_7_, p_228077_8_, p_228077_9_, p_228077_10_, p_228077_11_, p_228077_12_, p_228077_13_);
      }

   }

   public int width(String p_78256_1_) {
      if (p_78256_1_ == null) {
         return 0;
      } else {
         float f = 0.0F;
         boolean flag = false;

         for(int i = 0; i < p_78256_1_.length(); ++i) {
            char c0 = p_78256_1_.charAt(i);
            if (c0 == 167 && i < p_78256_1_.length() - 1) {
               ++i;
               TextFormatting textformatting = TextFormatting.getByCode(p_78256_1_.charAt(i));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.func_211166_f()) {
                  flag = false;
               }
            } else {
               f += this.fonts.func_211184_b(c0).getAdvance(flag);
            }
         }

         return MathHelper.ceil(f);
      }
   }

   public float func_211125_a(char p_211125_1_) {
      return p_211125_1_ == 167 ? 0.0F : this.fonts.func_211184_b(p_211125_1_).getAdvance(false);
   }

   public String func_78269_a(String p_78269_1_, int p_78269_2_) {
      return this.func_78262_a(p_78269_1_, p_78269_2_, false);
   }

   public String func_78262_a(String p_78262_1_, int p_78262_2_, boolean p_78262_3_) {
      StringBuilder stringbuilder = new StringBuilder();
      float f = 0.0F;
      int i = p_78262_3_ ? p_78262_1_.length() - 1 : 0;
      int j = p_78262_3_ ? -1 : 1;
      boolean flag = false;
      boolean flag1 = false;

      for(int k = i; k >= 0 && k < p_78262_1_.length() && f < (float)p_78262_2_; k += j) {
         char c0 = p_78262_1_.charAt(k);
         if (flag) {
            flag = false;
            TextFormatting textformatting = TextFormatting.getByCode(c0);
            if (textformatting == TextFormatting.BOLD) {
               flag1 = true;
            } else if (textformatting != null && textformatting.func_211166_f()) {
               flag1 = false;
            }
         } else if (c0 == 167) {
            flag = true;
         } else {
            f += this.func_211125_a(c0);
            if (flag1) {
               ++f;
            }
         }

         if (f > (float)p_78262_2_) {
            break;
         }

         if (p_78262_3_) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   private String func_78273_d(String p_78273_1_) {
      while(p_78273_1_ != null && p_78273_1_.endsWith("\n")) {
         p_78273_1_ = p_78273_1_.substring(0, p_78273_1_.length() - 1);
      }

      return p_78273_1_;
   }

   public void func_78279_b(String p_78279_1_, int p_78279_2_, int p_78279_3_, int p_78279_4_, int p_78279_5_) {
      p_78279_1_ = this.func_78273_d(p_78279_1_);
      this.func_211124_b(p_78279_1_, p_78279_2_, p_78279_3_, p_78279_4_, p_78279_5_);
   }

   private void func_211124_b(String p_211124_1_, int p_211124_2_, int p_211124_3_, int p_211124_4_, int p_211124_5_) {
      List<String> list = this.func_78271_c(p_211124_1_, p_211124_4_);
      Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();

      for(String s : list) {
         float f = (float)p_211124_2_;
         if (this.field_78294_m) {
            int i = this.width(this.bidirectionalShaping(s));
            f += (float)(p_211124_4_ - i);
         }

         this.drawInternal(s, f, (float)p_211124_3_, p_211124_5_, matrix4f, false);
         p_211124_3_ += 9;
      }

   }

   public int wordWrapHeight(String p_78267_1_, int p_78267_2_) {
      return 9 * this.func_78271_c(p_78267_1_, p_78267_2_).size();
   }

   public void func_78275_b(boolean p_78275_1_) {
      this.field_78294_m = p_78275_1_;
   }

   public List<String> func_78271_c(String p_78271_1_, int p_78271_2_) {
      return Arrays.asList(this.func_78280_d(p_78271_1_, p_78271_2_).split("\n"));
   }

   public String func_78280_d(String p_78280_1_, int p_78280_2_) {
      String s;
      String s1;
      for(s = ""; !p_78280_1_.isEmpty(); s = s + s1 + "\n") {
         int i = this.func_78259_e(p_78280_1_, p_78280_2_);
         if (p_78280_1_.length() <= i) {
            return s + p_78280_1_;
         }

         s1 = p_78280_1_.substring(0, i);
         char c0 = p_78280_1_.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         p_78280_1_ = TextFormatting.func_211164_a(s1) + p_78280_1_.substring(i + (flag ? 1 : 0));
      }

      return s;
   }

   public int func_78259_e(String p_78259_1_, int p_78259_2_) {
      int i = Math.max(1, p_78259_2_);
      int j = p_78259_1_.length();
      float f = 0.0F;
      int k = 0;
      int l = -1;
      boolean flag = false;

      for(boolean flag1 = true; k < j; ++k) {
         char c0 = p_78259_1_.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            if (f != 0.0F) {
               flag1 = false;
            }

            f += this.func_211125_a(c0);
            if (flag) {
               ++f;
            }
            break;
         case '\u00a7':
            if (k < j - 1) {
               ++k;
               TextFormatting textformatting = TextFormatting.getByCode(p_78259_1_.charAt(k));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.func_211166_f()) {
                  flag = false;
               }
            }
         }

         if (c0 == '\n') {
            ++k;
            l = k;
            break;
         }

         if (f > (float)i) {
            if (flag1) {
               ++k;
            }
            break;
         }
      }

      return k != j && l != -1 && l < k ? l : k;
   }

   public int func_216863_a(String p_216863_1_, int p_216863_2_, int p_216863_3_, boolean p_216863_4_) {
      int i = p_216863_3_;
      boolean flag = p_216863_2_ < 0;
      int j = Math.abs(p_216863_2_);

      for(int k = 0; k < j; ++k) {
         if (flag) {
            while(p_216863_4_ && i > 0 && (p_216863_1_.charAt(i - 1) == ' ' || p_216863_1_.charAt(i - 1) == '\n')) {
               --i;
            }

            while(i > 0 && p_216863_1_.charAt(i - 1) != ' ' && p_216863_1_.charAt(i - 1) != '\n') {
               --i;
            }
         } else {
            int l = p_216863_1_.length();
            int i1 = p_216863_1_.indexOf(32, i);
            int j1 = p_216863_1_.indexOf(10, i);
            if (i1 == -1 && j1 == -1) {
               i = -1;
            } else if (i1 != -1 && j1 != -1) {
               i = Math.min(i1, j1);
            } else if (i1 != -1) {
               i = i1;
            } else {
               i = j1;
            }

            if (i == -1) {
               i = l;
            } else {
               while(p_216863_4_ && i < l && (p_216863_1_.charAt(i) == ' ' || p_216863_1_.charAt(i) == '\n')) {
                  ++i;
               }
            }
         }
      }

      return i;
   }

   public boolean isBidirectional() {
      return this.field_78294_m;
   }
}