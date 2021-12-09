package io.github.dungeonmakers.armouranditem.core.util;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.resources.ResourceLocation;

public class ModResourceLocation extends ResourceLocation {
  public ModResourceLocation(String resourceName) {
    super(addModNamespace(resourceName));
  }

  private static String addModNamespace(String resourceName) {
    if (resourceName.contains(":")) {
      return resourceName;
    }
    return ArmourAndItem.MOD_ID + ":" + resourceName;
  }
}
