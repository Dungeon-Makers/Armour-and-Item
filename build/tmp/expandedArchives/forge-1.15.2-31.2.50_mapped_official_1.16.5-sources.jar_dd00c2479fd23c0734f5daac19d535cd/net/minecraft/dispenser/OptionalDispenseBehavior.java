package net.minecraft.dispenser;

public abstract class OptionalDispenseBehavior extends DefaultDispenseItemBehavior {
   protected boolean success = true;

   protected void playSound(IBlockSource p_82485_1_) {
      p_82485_1_.getLevel().levelEvent(this.success ? 1000 : 1001, p_82485_1_.getPos(), 0);
   }
}