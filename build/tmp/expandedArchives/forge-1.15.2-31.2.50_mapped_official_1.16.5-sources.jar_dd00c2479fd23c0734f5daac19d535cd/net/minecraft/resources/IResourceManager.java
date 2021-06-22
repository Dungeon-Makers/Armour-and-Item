package net.minecraft.resources;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;

public interface IResourceManager {
   Set<String> getNamespaces();

   IResource getResource(ResourceLocation p_199002_1_) throws IOException;

   boolean hasResource(ResourceLocation p_219533_1_);

   List<IResource> getResources(ResourceLocation p_199004_1_) throws IOException;

   Collection<ResourceLocation> listResources(String p_199003_1_, Predicate<String> p_199003_2_);
}