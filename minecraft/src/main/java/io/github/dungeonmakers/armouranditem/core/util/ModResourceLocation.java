package io.github.dungeonmakers.armouranditem.core.util;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class ModResourceLocation extends ResourceLocation {
  public ModResourceLocation(String resourceName) {
    super(addModNamespace(resourceName));
  }

  @Contract(pure = true)
  private static @NotNull String addModNamespace(@NotNull String resourceName) {
    if (resourceName.contains(":")) {
      return resourceName;
    }
    return ArmourAndItem.MOD_ID + ":" + resourceName;
  }
}
