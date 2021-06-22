package net.minecraft.command;

import net.minecraft.util.text.ITextComponent;

public interface ICommandSource {
   ICommandSource NULL = new ICommandSource() {
      public void sendMessage(ITextComponent p_145747_1_) {
      }

      public boolean acceptsSuccess() {
         return false;
      }

      public boolean acceptsFailure() {
         return false;
      }

      public boolean shouldInformAdmins() {
         return false;
      }
   };

   void sendMessage(ITextComponent p_145747_1_);

   boolean acceptsSuccess();

   boolean acceptsFailure();

   boolean shouldInformAdmins();
}