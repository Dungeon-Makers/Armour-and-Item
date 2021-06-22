package net.minecraft.client.resources;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.IllegalFormatException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class Locale {
   private static final Gson field_200700_b = new Gson();
   private static final Logger field_199755_b = LogManager.getLogger();
   private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   protected final Map<String, String> field_135032_a = Maps.newHashMap();

   public synchronized void func_195811_a(IResourceManager p_195811_1_, List<String> p_195811_2_) {
      this.field_135032_a.clear();

      for(String s : p_195811_2_) {
         String s1 = String.format("lang/%s.json", s);

         for(String s2 : p_195811_1_.getNamespaces()) {
            try {
               ResourceLocation resourcelocation = new ResourceLocation(s2, s1);
               this.func_135028_a(p_195811_1_.getResources(resourcelocation));
            } catch (FileNotFoundException var9) {
               ;
            } catch (Exception exception) {
               field_199755_b.warn("Skipped language file: {}:{} ({})", s2, s1, exception.toString());
            }
         }
      }

   }

   private void func_135028_a(List<IResource> p_135028_1_) {
      for(IResource iresource : p_135028_1_) {
         InputStream inputstream = iresource.getInputStream();

         try {
            this.func_135021_a(inputstream);
         } finally {
            IOUtils.closeQuietly(inputstream);
         }
      }

   }

   private void func_135021_a(InputStream p_135021_1_) {
      JsonElement jsonelement = field_200700_b.fromJson(new InputStreamReader(p_135021_1_, StandardCharsets.UTF_8), JsonElement.class);
      JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonelement, "strings");

      for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
         String s = field_135031_c.matcher(JSONUtils.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
         this.field_135032_a.put(entry.getKey(), s);
      }

   }

   private String func_135026_c(String p_135026_1_) {
      String s = this.field_135032_a.get(p_135026_1_);
      return s == null ? p_135026_1_ : s;
   }

   public String func_135023_a(String p_135023_1_, Object[] p_135023_2_) {
      String s = this.func_135026_c(p_135023_1_);

      try {
         return String.format(s, p_135023_2_);
      } catch (IllegalFormatException var5) {
         return "Format error: " + s;
      }
   }

   public boolean func_188568_a(String p_188568_1_) {
      return this.field_135032_a.containsKey(p_188568_1_);
   }
}