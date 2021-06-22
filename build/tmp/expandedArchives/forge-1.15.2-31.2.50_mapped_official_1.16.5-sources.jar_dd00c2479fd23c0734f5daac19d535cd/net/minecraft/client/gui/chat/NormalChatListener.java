package net.minecraft.client.gui.chat;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ChatType;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NormalChatListener implements IChatListener {
   private final Minecraft minecraft;

   public NormalChatListener(Minecraft p_i47393_1_) {
      this.minecraft = p_i47393_1_;
   }

   public void handle(ChatType p_192576_1_, ITextComponent p_192576_2_) {
      this.minecraft.gui.getChat().addMessage(p_192576_2_);
   }
}