package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.util.JSONUtils;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class UserList<K, V extends UserListEntry<K>> {
   protected static final Logger LOGGER = LogManager.getLogger();
   protected final Gson field_152694_b;
   private final File file;
   private final Map<String, V> map = Maps.newHashMap();
   private boolean field_152697_e = true;
   private static final ParameterizedType field_152698_f = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{UserListEntry.class};
      }

      public Type getRawType() {
         return List.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };

   public UserList(File p_i1144_1_) {
      this.file = p_i1144_1_;
      GsonBuilder gsonbuilder = (new GsonBuilder()).setPrettyPrinting();
      gsonbuilder.registerTypeHierarchyAdapter(UserListEntry.class, new UserList.Serializer());
      this.field_152694_b = gsonbuilder.create();
   }

   public boolean func_152689_b() {
      return this.field_152697_e;
   }

   public void func_152686_a(boolean p_152686_1_) {
      this.field_152697_e = p_152686_1_;
   }

   public File getFile() {
      return this.file;
   }

   public void add(V p_152687_1_) {
      this.map.put(this.getKeyForUser(p_152687_1_.getUser()), p_152687_1_);

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after adding a user.", (Throwable)ioexception);
      }

   }

   @Nullable
   public V get(K p_152683_1_) {
      this.removeExpired();
      return (V)(this.map.get(this.getKeyForUser(p_152683_1_)));
   }

   public void remove(K p_152684_1_) {
      this.map.remove(this.getKeyForUser(p_152684_1_));

      try {
         this.save();
      } catch (IOException ioexception) {
         LOGGER.warn("Could not save the list after removing a user.", (Throwable)ioexception);
      }

   }

   public void remove(UserListEntry<K> p_199042_1_) {
      this.remove(p_199042_1_.getUser());
   }

   public String[] getUserList() {
      return this.map.keySet().toArray(new String[this.map.size()]);
   }

   public boolean isEmpty() {
      return this.map.size() < 1;
   }

   protected String getKeyForUser(K p_152681_1_) {
      return p_152681_1_.toString();
   }

   protected boolean contains(K p_152692_1_) {
      return this.map.containsKey(this.getKeyForUser(p_152692_1_));
   }

   private void removeExpired() {
      List<K> list = Lists.newArrayList();

      for(V v : this.map.values()) {
         if (v.hasExpired()) {
            list.add((K)v.getUser());
         }
      }

      for(K k : list) {
         this.map.remove(this.getKeyForUser(k));
      }

   }

   protected UserListEntry<K> createEntry(JsonObject p_152682_1_) {
      return new UserListEntry<>((K)null, p_152682_1_);
   }

   public Collection<V> getEntries() {
      return this.map.values();
   }

   public void save() throws IOException {
      Collection<V> collection = this.map.values();
      String s = this.field_152694_b.toJson(collection);
      BufferedWriter bufferedwriter = null;

      try {
         bufferedwriter = Files.newWriter(this.file, StandardCharsets.UTF_8);
         bufferedwriter.write(s);
      } finally {
         IOUtils.closeQuietly((Writer)bufferedwriter);
      }

   }

   public void load() throws FileNotFoundException {
      if (this.file.exists()) {
         BufferedReader bufferedreader = null;

         try {
            bufferedreader = Files.newReader(this.file, StandardCharsets.UTF_8);
            Collection<UserListEntry<K>> collection = JSONUtils.func_193841_a(this.field_152694_b, bufferedreader, field_152698_f);
            if (collection != null) {
               this.map.clear();

               for(UserListEntry<K> userlistentry : collection) {
                  if (userlistentry.getUser() != null) {
                     this.map.put(this.getKeyForUser(userlistentry.getUser()), (V)userlistentry);
                  }
               }
            }
         } finally {
            IOUtils.closeQuietly((Reader)bufferedreader);
         }

      }
   }

   class Serializer implements JsonDeserializer<UserListEntry<K>>, JsonSerializer<UserListEntry<K>> {
      private Serializer() {
      }

      public JsonElement serialize(UserListEntry<K> p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         p_serialize_1_.serialize(jsonobject);
         return jsonobject;
      }

      public UserListEntry<K> deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         if (p_deserialize_1_.isJsonObject()) {
            JsonObject jsonobject = p_deserialize_1_.getAsJsonObject();
            return UserList.this.createEntry(jsonobject);
         } else {
            return null;
         }
      }
   }
}