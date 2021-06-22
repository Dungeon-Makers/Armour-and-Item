package net.minecraft.util;

import javax.annotation.Nullable;

public interface IObjectIntIterable<T> extends Iterable<T> {
   @Nullable
   T byId(int p_148745_1_);
}