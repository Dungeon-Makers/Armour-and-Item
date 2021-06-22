package com.mojang.realmsclient.client;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.realmsclient.util.JsonUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class RealmsError {
   private static final Logger LOGGER = LogManager.getLogger();
   private String errorMessage;
   private int errorCode;

   public RealmsError(String p_i51789_1_) {
      try {
         JsonParser jsonparser = new JsonParser();
         JsonObject jsonobject = jsonparser.parse(p_i51789_1_).getAsJsonObject();
         this.errorMessage = JsonUtils.getStringOr("errorMsg", jsonobject, "");
         this.errorCode = JsonUtils.getIntOr("errorCode", jsonobject, -1);
      } catch (Exception exception) {
         LOGGER.error("Could not parse RealmsError: " + exception.getMessage());
         LOGGER.error("The error was: " + p_i51789_1_);
      }

   }

   public String getErrorMessage() {
      return this.errorMessage;
   }

   public int getErrorCode() {
      return this.errorCode;
   }
}