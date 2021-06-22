package net.minecraft.data;

import java.nio.file.Path;
import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EntityTypeTagsProvider extends TagsProvider<EntityType<?>> {
   public EntityTypeTagsProvider(DataGenerator p_i50784_1_) {
      super(p_i50784_1_, Registry.ENTITY_TYPE);
   }

   protected void addTags() {
      this.func_200426_a(EntityTypeTags.SKELETONS).func_200573_a(EntityType.SKELETON, EntityType.STRAY, EntityType.WITHER_SKELETON);
      this.func_200426_a(EntityTypeTags.RAIDERS).func_200573_a(EntityType.EVOKER, EntityType.PILLAGER, EntityType.RAVAGER, EntityType.VINDICATOR, EntityType.ILLUSIONER, EntityType.WITCH);
      this.func_200426_a(EntityTypeTags.BEEHIVE_INHABITORS).func_200048_a(EntityType.BEE);
      this.func_200426_a(EntityTypeTags.ARROWS).func_200573_a(EntityType.ARROW, EntityType.SPECTRAL_ARROW);
   }

   protected Path getPath(ResourceLocation p_200431_1_) {
      return this.generator.getOutputFolder().resolve("data/" + p_200431_1_.getNamespace() + "/tags/entity_types/" + p_200431_1_.getPath() + ".json");
   }

   public String getName() {
      return "Entity Type Tags";
   }

   protected void func_200429_a(TagCollection<EntityType<?>> p_200429_1_) {
      EntityTypeTags.func_219759_a(p_200429_1_);
   }
}