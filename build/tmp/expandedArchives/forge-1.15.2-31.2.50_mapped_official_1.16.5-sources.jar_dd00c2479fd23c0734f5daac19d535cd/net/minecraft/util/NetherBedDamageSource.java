package net.minecraft.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;

public class NetherBedDamageSource extends DamageSource {
   protected NetherBedDamageSource() {
      super("netherBed");
      this.setScalesWithDifficulty();
      this.setExplosion();
   }

   public ITextComponent getLocalizedDeathMessage(LivingEntity p_151519_1_) {
      ITextComponent itextcomponent = TextComponentUtils.func_197676_a(new TranslationTextComponent("death.attack.netherBed.link")).func_211710_a((p_211694_0_) -> {
         p_211694_0_.func_150241_a(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://bugs.mojang.com/browse/MCPE-28723")).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent("MCPE-28723")));
      });
      return new TranslationTextComponent("death.attack.netherBed.message", p_151519_1_.getDisplayName(), itextcomponent);
   }
}