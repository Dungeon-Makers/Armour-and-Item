package com.mojang.realmsclient.dto;

import com.google.gson.JsonObject;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ServerActivity extends ValueObject {
   public String profileUuid;
   public long joinTime;
   public long leaveTime;

   public static ServerActivity parse(JsonObject p_parse_0_) {
      ServerActivity serveractivity = new ServerActivity();

      try {
         serveractivity.profileUuid = JsonUtils.getStringOr("profileUuid", p_parse_0_, (String)null);
         serveractivity.joinTime = JsonUtils.getLongOr("joinTime", p_parse_0_, Long.MIN_VALUE);
         serveractivity.leaveTime = JsonUtils.getLongOr("leaveTime", p_parse_0_, Long.MIN_VALUE);
      } catch (Exception var3) {
         ;
      }

      return serveractivity;
   }
}