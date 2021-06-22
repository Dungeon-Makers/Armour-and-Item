package net.minecraft.util.text;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LanguageMap {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern UNSUPPORTED_FORMAT_PATTERN = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
   private static final LanguageMap field_197636_c = new LanguageMap();
   private final Map<String, String> field_74816_c = Maps.newHashMap();
   private long field_150511_e;

   public LanguageMap() {
      try (InputStream inputstream = LanguageMap.class.getResourceAsStream("/assets/minecraft/lang/en_us.json")) {
         JsonElement jsonelement = (new Gson()).fromJson(new InputStreamReader(inputstream, StandardCharsets.UTF_8), JsonElement.class);
         JsonObject jsonobject = JSONUtils.convertToJsonObject(jsonelement, "strings");

         for(Entry<String, JsonElement> entry : jsonobject.entrySet()) {
            String s = UNSUPPORTED_FORMAT_PATTERN.matcher(JSONUtils.convertToString(entry.getValue(), entry.getKey())).replaceAll("%$1s");
            this.field_74816_c.put(entry.getKey(), s);
         }

         net.minecraftforge.fml.server.LanguageHook.captureLanguageMap(this.field_74816_c);
         this.field_150511_e = Util.getMillis();
      } catch (JsonParseException | IOException ioexception) {
         LOGGER.error("Couldn't read strings from /assets/minecraft/lang/en_us.json", (Throwable)ioexception);
      }

   }

   public static LanguageMap getInstance() {
      return field_197636_c;
   }

   @OnlyIn(Dist.CLIENT)
   public static synchronized void func_135063_a(Map<String, String> p_135063_0_) {
      field_197636_c.field_74816_c.clear();
      field_197636_c.field_74816_c.putAll(p_135063_0_);
      field_197636_c.field_150511_e = Util.getMillis();
   }

   public synchronized String func_74805_b(String p_74805_1_) {
      return this.func_135064_c(p_74805_1_);
   }

   private String func_135064_c(String p_135064_1_) {
      String s = this.field_74816_c.get(p_135064_1_);
      return s == null ? p_135064_1_ : s;
   }

   public synchronized boolean func_210813_b(String p_210813_1_) {
      return this.field_74816_c.containsKey(p_210813_1_);
   }

   public long func_150510_c() {
      return this.field_150511_e;
   }
}
