package net.minecraft.client.gui.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SystemToast implements IToast {
   private final SystemToast.Type id;
   private String title;
   private String field_193661_e;
   private long lastChanged;
   private boolean changed;

   public SystemToast(SystemToast.Type p_i47488_1_, ITextComponent p_i47488_2_, @Nullable ITextComponent p_i47488_3_) {
      this.id = p_i47488_1_;
      this.title = p_i47488_2_.getString();
      this.field_193661_e = p_i47488_3_ == null ? null : p_i47488_3_.getString();
   }

   public IToast.Visibility func_193653_a(ToastGui p_193653_1_, long p_193653_2_) {
      if (this.changed) {
         this.lastChanged = p_193653_2_;
         this.changed = false;
      }

      p_193653_1_.getMinecraft().getTextureManager().bind(TEXTURE);
      RenderSystem.color3f(1.0F, 1.0F, 1.0F);
      p_193653_1_.blit(0, 0, 0, 64, 160, 32);
      if (this.field_193661_e == null) {
         p_193653_1_.getMinecraft().font.func_211126_b(this.title, 18.0F, 12.0F, -256);
      } else {
         p_193653_1_.getMinecraft().font.func_211126_b(this.title, 18.0F, 7.0F, -256);
         p_193653_1_.getMinecraft().font.func_211126_b(this.field_193661_e, 18.0F, 18.0F, -1);
      }

      return p_193653_2_ - this.lastChanged < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
   }

   public void reset(ITextComponent p_193656_1_, @Nullable ITextComponent p_193656_2_) {
      this.title = p_193656_1_.getString();
      this.field_193661_e = p_193656_2_ == null ? null : p_193656_2_.getString();
      this.changed = true;
   }

   public SystemToast.Type getToken() {
      return this.id;
   }

   public static void addOrUpdate(ToastGui p_193657_0_, SystemToast.Type p_193657_1_, ITextComponent p_193657_2_, @Nullable ITextComponent p_193657_3_) {
      SystemToast systemtoast = p_193657_0_.getToast(SystemToast.class, p_193657_1_);
      if (systemtoast == null) {
         p_193657_0_.addToast(new SystemToast(p_193657_1_, p_193657_2_, p_193657_3_));
      } else {
         systemtoast.reset(p_193657_2_, p_193657_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static enum Type {
      TUTORIAL_HINT,
      NARRATOR_TOGGLE,
      WORLD_BACKUP,
      PACK_LOAD_FAILURE;
   }
}