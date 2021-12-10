package io.github.dungeonmakers.armouranditem.core.util;

import io.github.dungeonmakers.armouranditem.ArmourAndItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class InitUtil {
  @Contract("_ -> new")
  public static <T extends IForgeRegistryEntry<T>> @NotNull DeferredRegister<T> create(
      IForgeRegistry<T> registry) {
    return DeferredRegister.create(registry, ArmourAndItem.MOD_ID);
  }
}
