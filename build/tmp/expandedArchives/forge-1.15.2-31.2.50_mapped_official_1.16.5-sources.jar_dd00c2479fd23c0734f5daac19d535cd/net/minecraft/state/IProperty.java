package net.minecraft.state;

import java.util.Collection;
import java.util.Optional;

public interface IProperty<T extends Comparable<T>> {
   String getName();

   Collection<T> getPossibleValues();

   Class<T> getValueClass();

   Optional<T> getValue(String p_185929_1_);

   String getName(T p_177702_1_);
}