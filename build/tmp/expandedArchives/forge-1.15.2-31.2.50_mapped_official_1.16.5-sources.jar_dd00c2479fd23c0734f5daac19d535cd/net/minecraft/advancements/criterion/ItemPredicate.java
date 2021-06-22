package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSyntaxException;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class ItemPredicate {
   private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> custom_predicates = new java.util.HashMap<>();
   private static final Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> unmod_predicates = java.util.Collections.unmodifiableMap(custom_predicates);
   public static final ItemPredicate ANY = new ItemPredicate();
   @Nullable
   private final Tag<Item> tag;
   @Nullable
   private final Item item;
   private final MinMaxBounds.IntBound count;
   private final MinMaxBounds.IntBound durability;
   private final EnchantmentPredicate[] enchantments;
   private final EnchantmentPredicate[] storedEnchantments;
   @Nullable
   private final Potion potion;
   private final NBTPredicate nbt;

   public ItemPredicate() {
      this.tag = null;
      this.item = null;
      this.potion = null;
      this.count = MinMaxBounds.IntBound.ANY;
      this.durability = MinMaxBounds.IntBound.ANY;
      this.enchantments = EnchantmentPredicate.NONE;
      this.storedEnchantments = EnchantmentPredicate.NONE;
      this.nbt = NBTPredicate.ANY;
   }

   public ItemPredicate(@Nullable Tag<Item> p_i225740_1_, @Nullable Item p_i225740_2_, MinMaxBounds.IntBound p_i225740_3_, MinMaxBounds.IntBound p_i225740_4_, EnchantmentPredicate[] p_i225740_5_, EnchantmentPredicate[] p_i225740_6_, @Nullable Potion p_i225740_7_, NBTPredicate p_i225740_8_) {
      this.tag = p_i225740_1_;
      this.item = p_i225740_2_;
      this.count = p_i225740_3_;
      this.durability = p_i225740_4_;
      this.enchantments = p_i225740_5_;
      this.storedEnchantments = p_i225740_6_;
      this.potion = p_i225740_7_;
      this.nbt = p_i225740_8_;
   }

   public boolean matches(ItemStack p_192493_1_) {
      if (this == ANY) {
         return true;
      } else if (this.tag != null && !this.tag.func_199685_a_(p_192493_1_.getItem())) {
         return false;
      } else if (this.item != null && p_192493_1_.getItem() != this.item) {
         return false;
      } else if (!this.count.matches(p_192493_1_.getCount())) {
         return false;
      } else if (!this.durability.isAny() && !p_192493_1_.isDamageableItem()) {
         return false;
      } else if (!this.durability.matches(p_192493_1_.getMaxDamage() - p_192493_1_.getDamageValue())) {
         return false;
      } else if (!this.nbt.matches(p_192493_1_)) {
         return false;
      } else {
         if (this.enchantments.length > 0) {
            Map<Enchantment, Integer> map = EnchantmentHelper.deserializeEnchantments(p_192493_1_.getEnchantmentTags());

            for(EnchantmentPredicate enchantmentpredicate : this.enchantments) {
               if (!enchantmentpredicate.containedIn(map)) {
                  return false;
               }
            }
         }

         if (this.storedEnchantments.length > 0) {
            Map<Enchantment, Integer> map1 = EnchantmentHelper.deserializeEnchantments(EnchantedBookItem.getEnchantments(p_192493_1_));

            for(EnchantmentPredicate enchantmentpredicate1 : this.storedEnchantments) {
               if (!enchantmentpredicate1.containedIn(map1)) {
                  return false;
               }
            }
         }

         Potion potion = PotionUtils.getPotion(p_192493_1_);
         return this.potion == null || this.potion == potion;
      }
   }

   public static ItemPredicate fromJson(@Nullable JsonElement p_192492_0_) {
      if (p_192492_0_ != null && !p_192492_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.convertToJsonObject(p_192492_0_, "item");
         if (jsonobject.has("type")) {
            final ResourceLocation rl = new ResourceLocation(JSONUtils.getAsString(jsonobject, "type"));
            if (custom_predicates.containsKey(rl)) return custom_predicates.get(rl).apply(jsonobject);
            else throw new JsonSyntaxException("There is no ItemPredicate of type "+rl);
         }
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("count"));
         MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("durability"));
         if (jsonobject.has("data")) {
            throw new JsonParseException("Disallowed data tag found");
         } else {
            NBTPredicate nbtpredicate = NBTPredicate.fromJson(jsonobject.get("nbt"));
            Item item = null;
            if (jsonobject.has("item")) {
               ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getAsString(jsonobject, "item"));
               item = Registry.ITEM.func_218349_b(resourcelocation).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown item id '" + resourcelocation + "'");
               });
            }

            Tag<Item> tag = null;
            if (jsonobject.has("tag")) {
               ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getAsString(jsonobject, "tag"));
               tag = ItemTags.getAllTags().getTag(resourcelocation1);
               if (tag == null) {
                  throw new JsonSyntaxException("Unknown item tag '" + resourcelocation1 + "'");
               }
            }

            Potion potion = null;
            if (jsonobject.has("potion")) {
               ResourceLocation resourcelocation2 = new ResourceLocation(JSONUtils.getAsString(jsonobject, "potion"));
               potion = Registry.POTION.func_218349_b(resourcelocation2).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown potion '" + resourcelocation2 + "'");
               });
            }

            EnchantmentPredicate[] aenchantmentpredicate1 = EnchantmentPredicate.fromJsonArray(jsonobject.get("enchantments"));
            EnchantmentPredicate[] aenchantmentpredicate = EnchantmentPredicate.fromJsonArray(jsonobject.get("stored_enchantments"));
            return new ItemPredicate(tag, item, minmaxbounds$intbound, minmaxbounds$intbound1, aenchantmentpredicate1, aenchantmentpredicate, potion, nbtpredicate);
         }
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.item != null) {
            jsonobject.addProperty("item", Registry.ITEM.getKey(this.item).toString());
         }

         if (this.tag != null) {
            jsonobject.addProperty("tag", this.tag.func_199886_b().toString());
         }

         jsonobject.add("count", this.count.serializeToJson());
         jsonobject.add("durability", this.durability.serializeToJson());
         jsonobject.add("nbt", this.nbt.serializeToJson());
         if (this.enchantments.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(EnchantmentPredicate enchantmentpredicate : this.enchantments) {
               jsonarray.add(enchantmentpredicate.serializeToJson());
            }

            jsonobject.add("enchantments", jsonarray);
         }

         if (this.storedEnchantments.length > 0) {
            JsonArray jsonarray1 = new JsonArray();

            for(EnchantmentPredicate enchantmentpredicate1 : this.storedEnchantments) {
               jsonarray1.add(enchantmentpredicate1.serializeToJson());
            }

            jsonobject.add("stored_enchantments", jsonarray1);
         }

         if (this.potion != null) {
            jsonobject.addProperty("potion", Registry.POTION.getKey(this.potion).toString());
         }

         return jsonobject;
      }
   }

   public static ItemPredicate[] fromJsonArray(@Nullable JsonElement p_192494_0_) {
      if (p_192494_0_ != null && !p_192494_0_.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.convertToJsonArray(p_192494_0_, "items");
         ItemPredicate[] aitempredicate = new ItemPredicate[jsonarray.size()];

         for(int i = 0; i < aitempredicate.length; ++i) {
            aitempredicate[i] = fromJson(jsonarray.get(i));
         }

         return aitempredicate;
      } else {
         return new ItemPredicate[0];
      }
   }

   public static void register(ResourceLocation name, java.util.function.Function<JsonObject, ItemPredicate> deserializer) {
      custom_predicates.put(name, deserializer);
   }

   public static Map<ResourceLocation, java.util.function.Function<JsonObject, ItemPredicate>> getPredicates() {
      return unmod_predicates;
   }

   public static class Builder {
      private final List<EnchantmentPredicate> enchantments = Lists.newArrayList();
      private final List<EnchantmentPredicate> storedEnchantments = Lists.newArrayList();
      @Nullable
      private Item item;
      @Nullable
      private Tag<Item> tag;
      private MinMaxBounds.IntBound count = MinMaxBounds.IntBound.ANY;
      private MinMaxBounds.IntBound durability = MinMaxBounds.IntBound.ANY;
      @Nullable
      private Potion potion;
      private NBTPredicate nbt = NBTPredicate.ANY;

      private Builder() {
      }

      public static ItemPredicate.Builder item() {
         return new ItemPredicate.Builder();
      }

      public ItemPredicate.Builder of(IItemProvider p_200308_1_) {
         this.item = p_200308_1_.asItem();
         return this;
      }

      public ItemPredicate.Builder of(Tag<Item> p_200307_1_) {
         this.tag = p_200307_1_;
         return this;
      }

      public ItemPredicate.Builder hasNbt(CompoundNBT p_218002_1_) {
         this.nbt = new NBTPredicate(p_218002_1_);
         return this;
      }

      public ItemPredicate.Builder hasEnchantment(EnchantmentPredicate p_218003_1_) {
         this.enchantments.add(p_218003_1_);
         return this;
      }

      public ItemPredicate build() {
         return new ItemPredicate(this.tag, this.item, this.count, this.durability, this.enchantments.toArray(EnchantmentPredicate.NONE), this.storedEnchantments.toArray(EnchantmentPredicate.NONE), this.potion, this.nbt);
      }
   }
}
