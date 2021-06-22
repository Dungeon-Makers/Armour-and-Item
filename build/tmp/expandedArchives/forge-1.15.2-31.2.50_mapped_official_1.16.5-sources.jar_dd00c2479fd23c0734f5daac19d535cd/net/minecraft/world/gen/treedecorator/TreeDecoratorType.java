package net.minecraft.world.gen.treedecorator;

import com.mojang.datafixers.Dynamic;
import java.util.function.Function;
import net.minecraft.util.registry.Registry;

public class TreeDecoratorType<P extends TreeDecorator> extends net.minecraftforge.registries.ForgeRegistryEntry<TreeDecoratorType<?>> {
   public static final TreeDecoratorType<TrunkVineTreeDecorator> TRUNK_VINE = func_227432_a_("trunk_vine", TrunkVineTreeDecorator::new);
   public static final TreeDecoratorType<LeaveVineTreeDecorator> LEAVE_VINE = func_227432_a_("leave_vine", LeaveVineTreeDecorator::new);
   public static final TreeDecoratorType<CocoaTreeDecorator> COCOA = func_227432_a_("cocoa", CocoaTreeDecorator::new);
   public static final TreeDecoratorType<BeehiveTreeDecorator> BEEHIVE = func_227432_a_("beehive", BeehiveTreeDecorator::new);
   public static final TreeDecoratorType<AlterGroundTreeDecorator> ALTER_GROUND = func_227432_a_("alter_ground", AlterGroundTreeDecorator::new);
   private final Function<Dynamic<?>, P> field_227430_f_;

   private static <P extends TreeDecorator> TreeDecoratorType<P> func_227432_a_(String p_227432_0_, Function<Dynamic<?>, P> p_227432_1_) {
      return Registry.register(Registry.TREE_DECORATOR_TYPES, p_227432_0_, new TreeDecoratorType<>(p_227432_1_));
   }

   public TreeDecoratorType(Function<Dynamic<?>, P> p_i225872_1_) {
      this.field_227430_f_ = p_i225872_1_;
   }

   public P func_227431_a_(Dynamic<?> p_227431_1_) {
      return (P)(this.field_227430_f_.apply(p_227431_1_));
   }
}
