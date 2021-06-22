package net.minecraft.client.gui.spectator;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftProfileTexture.Type;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.play.client.CSpectatePacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PlayerMenuObject implements ISpectatorMenuObject {
   private final GameProfile profile;
   private final ResourceLocation location;

   public PlayerMenuObject(GameProfile p_i45498_1_) {
      this.profile = p_i45498_1_;
      Minecraft minecraft = Minecraft.getInstance();
      Map<Type, MinecraftProfileTexture> map = minecraft.getSkinManager().getInsecureSkinInformation(p_i45498_1_);
      if (map.containsKey(Type.SKIN)) {
         this.location = minecraft.getSkinManager().registerTexture(map.get(Type.SKIN), Type.SKIN);
      } else {
         this.location = DefaultPlayerSkin.getDefaultSkin(PlayerEntity.createPlayerUUID(p_i45498_1_));
      }

   }

   public void selectItem(SpectatorMenu p_178661_1_) {
      Minecraft.getInstance().getConnection().send(new CSpectatePacket(this.profile.getId()));
   }

   public ITextComponent getName() {
      return new StringTextComponent(this.profile.getName());
   }

   public void func_178663_a(float p_178663_1_, int p_178663_2_) {
      Minecraft.getInstance().getTextureManager().bind(this.location);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, (float)p_178663_2_ / 255.0F);
      AbstractGui.blit(2, 2, 12, 12, 8.0F, 8.0F, 8, 8, 64, 64);
      AbstractGui.blit(2, 2, 12, 12, 40.0F, 8.0F, 8, 8, 64, 64);
   }

   public boolean isEnabled() {
      return true;
   }
}