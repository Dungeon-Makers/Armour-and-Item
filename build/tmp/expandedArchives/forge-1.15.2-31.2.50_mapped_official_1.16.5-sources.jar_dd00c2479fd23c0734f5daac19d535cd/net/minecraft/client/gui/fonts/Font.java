package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.chars.CharList;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Font implements AutoCloseable {
   private static final Logger field_211189_a = LogManager.getLogger();
   private static final EmptyGlyph SPACE_GLYPH = new EmptyGlyph();
   private static final IGlyph SPACE_INFO = () -> {
      return 4.0F;
   };
   private static final Random RANDOM = new Random();
   private final TextureManager textureManager;
   private final ResourceLocation name;
   private TexturedGlyph missingGlyph;
   private TexturedGlyph whiteGlyph;
   private final List<IGlyphProvider> providers = Lists.newArrayList();
   private final Char2ObjectMap<TexturedGlyph> glyphs = new Char2ObjectOpenHashMap<>();
   private final Char2ObjectMap<IGlyph> glyphInfos = new Char2ObjectOpenHashMap<>();
   private final Int2ObjectMap<CharList> glyphsByWidth = new Int2ObjectOpenHashMap<>();
   private final List<FontTexture> textures = Lists.newArrayList();

   public Font(TextureManager p_i49771_1_, ResourceLocation p_i49771_2_) {
      this.textureManager = p_i49771_1_;
      this.name = p_i49771_2_;
   }

   public void reload(List<IGlyphProvider> p_211570_1_) {
      this.closeProviders();
      this.closeTextures();
      this.glyphs.clear();
      this.glyphInfos.clear();
      this.glyphsByWidth.clear();
      this.missingGlyph = this.stitch(DefaultGlyph.INSTANCE);
      this.whiteGlyph = this.stitch(WhiteGlyph.INSTANCE);
      Set<IGlyphProvider> set = Sets.newHashSet();

      for(char c0 = 0; c0 < '\uffff'; ++c0) {
         for(IGlyphProvider iglyphprovider : p_211570_1_) {
            IGlyph iglyph = (IGlyph)(c0 == ' ' ? SPACE_INFO : iglyphprovider.getGlyph(c0));
            if (iglyph != null) {
               set.add(iglyphprovider);
               if (iglyph != DefaultGlyph.INSTANCE) {
                  this.glyphsByWidth.computeIfAbsent(MathHelper.ceil(iglyph.getAdvance(false)), (p_212456_0_) -> {
                     return new CharArrayList();
                  }).add(c0);
               }
               break;
            }
         }
      }

      p_211570_1_.stream().filter(set::contains).forEach(this.providers::add);
   }

   public void close() {
      this.closeProviders();
      this.closeTextures();
   }

   private void closeProviders() {
      for(IGlyphProvider iglyphprovider : this.providers) {
         iglyphprovider.close();
      }

      this.providers.clear();
   }

   private void closeTextures() {
      for(FontTexture fonttexture : this.textures) {
         fonttexture.close();
      }

      this.textures.clear();
   }

   public IGlyph func_211184_b(char p_211184_1_) {
      return this.glyphInfos.computeIfAbsent(p_211184_1_, (p_212457_1_) -> {
         return (IGlyph)(p_212457_1_ == 32 ? SPACE_INFO : this.getRaw((char)p_212457_1_));
      });
   }

   private IGlyphInfo getRaw(char p_212455_1_) {
      for(IGlyphProvider iglyphprovider : this.providers) {
         IGlyphInfo iglyphinfo = iglyphprovider.getGlyph(p_212455_1_);
         if (iglyphinfo != null) {
            return iglyphinfo;
         }
      }

      return DefaultGlyph.INSTANCE;
   }

   public TexturedGlyph func_211187_a(char p_211187_1_) {
      return this.glyphs.computeIfAbsent(p_211187_1_, (p_212458_1_) -> {
         return (TexturedGlyph)(p_212458_1_ == 32 ? SPACE_GLYPH : this.stitch(this.getRaw((char)p_212458_1_)));
      });
   }

   private TexturedGlyph stitch(IGlyphInfo p_211185_1_) {
      for(FontTexture fonttexture : this.textures) {
         TexturedGlyph texturedglyph = fonttexture.add(p_211185_1_);
         if (texturedglyph != null) {
            return texturedglyph;
         }
      }

      FontTexture fonttexture1 = new FontTexture(new ResourceLocation(this.name.getNamespace(), this.name.getPath() + "/" + this.textures.size()), p_211185_1_.isColored());
      this.textures.add(fonttexture1);
      this.textureManager.register(fonttexture1.getName(), fonttexture1);
      TexturedGlyph texturedglyph1 = fonttexture1.add(p_211185_1_);
      return texturedglyph1 == null ? this.missingGlyph : texturedglyph1;
   }

   public TexturedGlyph getRandomGlyph(IGlyph p_211188_1_) {
      CharList charlist = this.glyphsByWidth.get(MathHelper.ceil(p_211188_1_.getAdvance(false)));
      return charlist != null && !charlist.isEmpty() ? this.func_211187_a(charlist.get(RANDOM.nextInt(charlist.size()))) : this.missingGlyph;
   }

   public TexturedGlyph whiteGlyph() {
      return this.whiteGlyph;
   }
}