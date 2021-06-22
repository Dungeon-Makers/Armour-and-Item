package net.minecraft.profiler;

import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IProfiler {
   void startTick();

   void endTick();

   void push(String p_76320_1_);

   void push(Supplier<String> p_194340_1_);

   void pop();

   void popPush(String p_219895_1_);

   @OnlyIn(Dist.CLIENT)
   void popPush(Supplier<String> p_194339_1_);

   void incrementCounter(String p_230035_1_);

   void incrementCounter(Supplier<String> p_230036_1_);
}