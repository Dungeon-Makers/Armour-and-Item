package net.minecraft.resources;

import java.util.Map;

public class ServerPackFinder implements IPackFinder {
   private final VanillaPack vanillaPack = new VanillaPack("minecraft");

   public <T extends ResourcePackInfo> void func_195730_a(Map<String, T> p_195730_1_, ResourcePackInfo.IFactory<T> p_195730_2_) {
      T t = ResourcePackInfo.create("vanilla", false, () -> {
         return this.vanillaPack;
      }, p_195730_2_, ResourcePackInfo.Priority.BOTTOM);
      if (t != null) {
         p_195730_1_.put("vanilla", t);
      }

   }
}