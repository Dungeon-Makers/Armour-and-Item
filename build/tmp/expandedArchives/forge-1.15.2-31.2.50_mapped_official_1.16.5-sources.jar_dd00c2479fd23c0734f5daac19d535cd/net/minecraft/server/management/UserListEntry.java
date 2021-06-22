package net.minecraft.server.management;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;

public class UserListEntry<T> {
   @Nullable
   private final T user;

   public UserListEntry(T p_i1146_1_) {
      this.user = p_i1146_1_;
   }

   protected UserListEntry(@Nullable T p_i1147_1_, JsonObject p_i1147_2_) {
      this.user = p_i1147_1_;
   }

   @Nullable
   T getUser() {
      return this.user;
   }

   boolean hasExpired() {
      return false;
   }

   protected void serialize(JsonObject p_152641_1_) {
   }
}