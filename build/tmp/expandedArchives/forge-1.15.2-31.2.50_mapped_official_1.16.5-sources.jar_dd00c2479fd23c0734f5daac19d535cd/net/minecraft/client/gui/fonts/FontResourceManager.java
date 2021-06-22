package net.minecraft.client.gui.fonts;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.fonts.providers.DefaultGlyphProvider;
import net.minecraft.client.gui.fonts.providers.GlyphProviderTypes;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.profiler.EmptyProfiler;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class FontResourceManager implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Map<ResourceLocation, FontRenderer> field_211510_b = Maps.newHashMap();
   private final TextureManager textureManager;
   private boolean field_211826_d;
   private final IFutureReloadListener reloadListener = new ReloadListener<Map<ResourceLocation, List<IGlyphProvider>>>() {
      protected Map<ResourceLocation, List<IGlyphProvider>> prepare(IResourceManager p_212854_1_, IProfiler p_212854_2_) {
         p_212854_2_.startTick();
         Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
         Map<ResourceLocation, List<IGlyphProvider>> map = Maps.newHashMap();

         for(ResourceLocation resourcelocation : p_212854_1_.listResources("font", (p_215274_0_) -> {
            return p_215274_0_.endsWith(".json");
         })) {
            String s = resourcelocation.getPath();
            ResourceLocation resourcelocation1 = new ResourceLocation(resourcelocation.getNamespace(), s.substring("font/".length(), s.length() - ".json".length()));
            List<IGlyphProvider> list = map.computeIfAbsent(resourcelocation1, (p_215272_0_) -> {
               return Lists.newArrayList(new DefaultGlyphProvider());
            });
            p_212854_2_.push(resourcelocation1::toString);

            try {
               for(IResource iresource : p_212854_1_.getResources(resourcelocation)) {
                  p_212854_2_.push(iresource::getSourceName);

                  try (
                     InputStream inputstream = iresource.getInputStream();
                     Reader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));
                  ) {
                     p_212854_2_.push("reading");
                     JsonArray jsonarray = JSONUtils.getAsJsonArray(JSONUtils.fromJson(gson, reader, JsonObject.class), "providers");
                     p_212854_2_.popPush("parsing");

                     for(int i = jsonarray.size() - 1; i >= 0; --i) {
                        JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonarray.get(i), "providers[" + i + "]");

                        try {
                           String s1 = JSONUtils.getAsString(jsonobject, "type");
                           GlyphProviderTypes glyphprovidertypes = GlyphProviderTypes.byName(s1);
                           if (!FontResourceManager.this.field_211826_d || glyphprovidertypes == GlyphProviderTypes.LEGACY_UNICODE || !resourcelocation1.equals(Minecraft.DEFAULT_FONT)) {
                              p_212854_2_.push(s1);
                              list.add(glyphprovidertypes.create(jsonobject).create(p_212854_1_));
                              p_212854_2_.pop();
                           }
                        } catch (RuntimeException runtimeexception) {
                           FontResourceManager.LOGGER.warn("Unable to read definition '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getSourceName(), runtimeexception.getMessage());
                        }
                     }

                     p_212854_2_.pop();
                  } catch (RuntimeException runtimeexception1) {
                     FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json in resourcepack: '{}': {}", resourcelocation1, iresource.getSourceName(), runtimeexception1.getMessage());
                  }

                  p_212854_2_.pop();
               }
            } catch (IOException ioexception) {
               FontResourceManager.LOGGER.warn("Unable to load font '{}' in fonts.json: {}", resourcelocation1, ioexception.getMessage());
            }

            p_212854_2_.push("caching");

            for(char c0 = 0; c0 < '\uffff'; ++c0) {
               if (c0 != ' ') {
                  for(IGlyphProvider iglyphprovider : Lists.reverse(list)) {
                     if (iglyphprovider.getGlyph(c0) != null) {
                        break;
                     }
                  }
               }
            }

            p_212854_2_.pop();
            p_212854_2_.pop();
         }

         p_212854_2_.endTick();
         return map;
      }

      protected void apply(Map<ResourceLocation, List<IGlyphProvider>> p_212853_1_, IResourceManager p_212853_2_, IProfiler p_212853_3_) {
         p_212853_3_.startTick();
         p_212853_3_.push("reloading");
         Stream.concat(FontResourceManager.this.field_211510_b.keySet().stream(), p_212853_1_.keySet().stream()).distinct().forEach((p_215271_2_) -> {
            List<IGlyphProvider> list = p_212853_1_.getOrDefault(p_215271_2_, Collections.emptyList());
            Collections.reverse(list);
            FontResourceManager.this.field_211510_b.computeIfAbsent(p_215271_2_, (p_215273_1_) -> {
               return new FontRenderer(FontResourceManager.this.textureManager, new Font(FontResourceManager.this.textureManager, p_215273_1_));
            }).func_211568_a(list);
         });
         p_212853_3_.pop();
         p_212853_3_.endTick();
      }

      public String getName() {
         return "FontManager";
      }
   };

   public FontResourceManager(TextureManager p_i49787_1_, boolean p_i49787_2_) {
      this.textureManager = p_i49787_1_;
      this.field_211826_d = p_i49787_2_;
   }

   @Nullable
   public FontRenderer func_211504_a(ResourceLocation p_211504_1_) {
      return this.field_211510_b.computeIfAbsent(p_211504_1_, (p_212318_1_) -> {
         FontRenderer fontrenderer = new FontRenderer(this.textureManager, new Font(this.textureManager, p_212318_1_));
         fontrenderer.func_211568_a(Lists.newArrayList(new DefaultGlyphProvider()));
         return fontrenderer;
      });
   }

   public void func_216883_a(boolean p_216883_1_, Executor p_216883_2_, Executor p_216883_3_) {
      if (p_216883_1_ != this.field_211826_d) {
         this.field_211826_d = p_216883_1_;
         IResourceManager iresourcemanager = Minecraft.getInstance().getResourceManager();
         IFutureReloadListener.IStage ifuturereloadlistener$istage = new IFutureReloadListener.IStage() {
            public <T> CompletableFuture<T> wait(T p_216872_1_) {
               return CompletableFuture.completedFuture(p_216872_1_);
            }
         };
         this.reloadListener.reload(ifuturereloadlistener$istage, iresourcemanager, EmptyProfiler.INSTANCE, EmptyProfiler.INSTANCE, p_216883_2_, p_216883_3_);
      }
   }

   public IFutureReloadListener getReloadListener() {
      return this.reloadListener;
   }

   public void close() {
      this.field_211510_b.values().forEach(FontRenderer::close);
   }
}