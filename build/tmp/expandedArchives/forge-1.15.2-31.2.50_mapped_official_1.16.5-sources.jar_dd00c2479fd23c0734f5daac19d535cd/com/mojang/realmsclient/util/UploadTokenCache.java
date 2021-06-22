package com.mojang.realmsclient.util;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class UploadTokenCache {
   private static final Map<Long, String> TOKEN_CACHE = Maps.newHashMap();

   public static String get(long p_225235_0_) {
      return TOKEN_CACHE.get(p_225235_0_);
   }

   public static void invalidate(long p_225233_0_) {
      TOKEN_CACHE.remove(p_225233_0_);
   }

   public static void put(long p_225234_0_, String p_225234_2_) {
      TOKEN_CACHE.put(p_225234_0_, p_225234_2_);
   }
}