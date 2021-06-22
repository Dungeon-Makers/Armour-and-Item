package net.minecraft.client.gui.overlay;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Map;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ClientBossInfo;
import net.minecraft.network.play.server.SUpdateBossInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.BossInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BossOverlayGui extends AbstractGui {
   private static final ResourceLocation GUI_BARS_LOCATION = new ResourceLocation("textures/gui/bars.png");
   private final Minecraft minecraft;
   private final Map<UUID, ClientBossInfo> events = Maps.newLinkedHashMap();

   public BossOverlayGui(Minecraft p_i46606_1_) {
      this.minecraft = p_i46606_1_;
   }

   public void func_184051_a() {
      if (!this.events.isEmpty()) {
         int i = this.minecraft.getWindow().getGuiScaledWidth();
         int j = 12;

         for(ClientBossInfo clientbossinfo : this.events.values()) {
            int k = i / 2 - 91;
            net.minecraftforge.client.event.RenderGameOverlayEvent.BossInfo event =
               net.minecraftforge.client.ForgeHooksClient.bossBarRenderPre(this.minecraft.getWindow(), clientbossinfo, k, j, 10 + this.minecraft.font.lineHeight);
            if (!event.isCanceled()) {
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.minecraft.getTextureManager().bind(GUI_BARS_LOCATION);
            this.func_184052_a(k, j, clientbossinfo);
            String s = clientbossinfo.getName().func_150254_d();
            int l = this.minecraft.font.width(s);
            int i1 = i / 2 - l / 2;
            int j1 = j - 9;
            this.minecraft.font.func_175063_a(s, (float)i1, (float)j1, 16777215);
            }
            j += event.getIncrement();
            net.minecraftforge.client.ForgeHooksClient.bossBarRenderPost(this.minecraft.getWindow());
            if (j >= this.minecraft.getWindow().getGuiScaledHeight() / 3) {
               break;
            }
         }

      }
   }

   private void func_184052_a(int p_184052_1_, int p_184052_2_, BossInfo p_184052_3_) {
      this.blit(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2, 182, 5);
      if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
         this.blit(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2, 182, 5);
      }

      int i = (int)(p_184052_3_.getPercent() * 183.0F);
      if (i > 0) {
         this.blit(p_184052_1_, p_184052_2_, 0, p_184052_3_.getColor().ordinal() * 5 * 2 + 5, i, 5);
         if (p_184052_3_.getOverlay() != BossInfo.Overlay.PROGRESS) {
            this.blit(p_184052_1_, p_184052_2_, 0, 80 + (p_184052_3_.getOverlay().ordinal() - 1) * 5 * 2 + 5, i, 5);
         }
      }

   }

   public void update(SUpdateBossInfoPacket p_184055_1_) {
      if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.ADD) {
         this.events.put(p_184055_1_.getId(), new ClientBossInfo(p_184055_1_));
      } else if (p_184055_1_.getOperation() == SUpdateBossInfoPacket.Operation.REMOVE) {
         this.events.remove(p_184055_1_.getId());
      } else {
         this.events.get(p_184055_1_.getId()).update(p_184055_1_);
      }

   }

   public void reset() {
      this.events.clear();
   }

   public boolean shouldPlayMusic() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldPlayBossMusic()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldDarkenScreen() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldDarkenScreen()) {
               return true;
            }
         }
      }

      return false;
   }

   public boolean shouldCreateWorldFog() {
      if (!this.events.isEmpty()) {
         for(BossInfo bossinfo : this.events.values()) {
            if (bossinfo.shouldCreateWorldFog()) {
               return true;
            }
         }
      }

      return false;
   }
}
