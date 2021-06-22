package net.minecraft.client.resources;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraft.resources.IResourcePack;
import net.minecraft.util.text.LanguageMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LanguageManager implements IResourceManagerReloadListener {
   private static final Logger LOGGER = LogManager.getLogger();
   protected static final Locale field_135049_a = new Locale();
   private String currentCode;
   private final Map<String, Language> languages = Maps.newHashMap();

   public LanguageManager(String p_i48112_1_) {
      this.currentCode = p_i48112_1_;
      I18n.func_135051_a(field_135049_a);
   }

   public void func_135043_a(List<IResourcePack> p_135043_1_) {
      this.languages.clear();

      for(IResourcePack iresourcepack : p_135043_1_) {
         try {
            LanguageMetadataSection languagemetadatasection = iresourcepack.getMetadataSection(LanguageMetadataSection.SERIALIZER);
            if (languagemetadatasection != null) {
               for(Language language : languagemetadatasection.getLanguages()) {
                  if (!this.languages.containsKey(language.getCode())) {
                     this.languages.put(language.getCode(), language);
                  }
               }
            }
         } catch (IOException | RuntimeException runtimeexception) {
            LOGGER.warn("Unable to parse language metadata section of resourcepack: {}", iresourcepack.getName(), runtimeexception);
         }
      }

   }

   public void onResourceManagerReload(IResourceManager p_195410_1_) {
      List<String> list = Lists.newArrayList("en_us");
      if (!"en_us".equals(this.currentCode)) {
         list.add(this.currentCode);
      }

      field_135049_a.func_195811_a(p_195410_1_, list);
      LanguageMap.func_135063_a(field_135049_a.field_135032_a);
   }

   public boolean isBidirectional() {
      return this.getSelected() != null && this.getSelected().isBidirectional();
   }

   public void setSelected(Language p_135045_1_) {
      this.currentCode = p_135045_1_.getCode();
   }

   public Language getSelected() {
      String s = this.languages.containsKey(this.currentCode) ? this.currentCode : "en_us";
      return this.languages.get(s);
   }

   public SortedSet<Language> getLanguages() {
      return Sets.newTreeSet(this.languages.values());
   }

   public Language getLanguage(String p_191960_1_) {
      return this.languages.get(p_191960_1_);
   }

   @Override
   public net.minecraftforge.resource.IResourceType getResourceType() {
      return net.minecraftforge.resource.VanillaResourceType.LANGUAGES;
   }
}
