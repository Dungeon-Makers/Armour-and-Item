package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.mojang.blaze3d.systems.RenderSystem;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.network.LanServerInfo;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.DefaultUncaughtExceptionHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ServerSelectionList extends ExtendedList<ServerSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final ThreadPoolExecutor THREAD_POOL = new ScheduledThreadPoolExecutor(5, (new ThreadFactoryBuilder()).setNameFormat("Server Pinger #%d").setDaemon(true).setUncaughtExceptionHandler(new DefaultUncaughtExceptionHandler(LOGGER)).build());
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/server_selection.png");
   private final MultiplayerScreen screen;
   private final List<ServerSelectionList.NormalEntry> onlineServers = Lists.newArrayList();
   private final ServerSelectionList.Entry lanHeader = new ServerSelectionList.LanScanEntry();
   private final List<ServerSelectionList.LanDetectedEntry> networkServers = Lists.newArrayList();

   public ServerSelectionList(MultiplayerScreen p_i45049_1_, Minecraft p_i45049_2_, int p_i45049_3_, int p_i45049_4_, int p_i45049_5_, int p_i45049_6_, int p_i45049_7_) {
      super(p_i45049_2_, p_i45049_3_, p_i45049_4_, p_i45049_5_, p_i45049_6_, p_i45049_7_);
      this.screen = p_i45049_1_;
   }

   private void refreshEntries() {
      this.clearEntries();
      this.onlineServers.forEach(this::addEntry);
      this.addEntry(this.lanHeader);
      this.networkServers.forEach(this::addEntry);
   }

   public void setSelected(ServerSelectionList.Entry p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      if (this.getSelected() instanceof ServerSelectionList.NormalEntry) {
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", ((ServerSelectionList.NormalEntry)this.getSelected()).serverData.name)).getString());
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      ServerSelectionList.Entry serverselectionlist$entry = this.getSelected();
      return serverselectionlist$entry != null && serverselectionlist$entry.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) || super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
   }

   protected void moveSelection(int p_moveSelection_1_) {
      int i = this.children().indexOf(this.getSelected());
      int j = MathHelper.clamp(i + p_moveSelection_1_, 0, this.getItemCount() - 1);
      ServerSelectionList.Entry serverselectionlist$entry = this.children().get(j);
      if (serverselectionlist$entry instanceof ServerSelectionList.LanScanEntry) {
         j = MathHelper.clamp(j + (p_moveSelection_1_ > 0 ? 1 : -1), 0, this.getItemCount() - 1);
         serverselectionlist$entry = this.children().get(j);
      }

      super.setSelected(serverselectionlist$entry);
      this.ensureVisible(serverselectionlist$entry);
      this.screen.onSelectedChange();
   }

   public void updateOnlineServers(ServerList p_148195_1_) {
      this.onlineServers.clear();

      for(int i = 0; i < p_148195_1_.size(); ++i) {
         this.onlineServers.add(new ServerSelectionList.NormalEntry(this.screen, p_148195_1_.get(i)));
      }

      this.refreshEntries();
   }

   public void updateNetworkServers(List<LanServerInfo> p_148194_1_) {
      this.networkServers.clear();

      for(LanServerInfo lanserverinfo : p_148194_1_) {
         this.networkServers.add(new ServerSelectionList.LanDetectedEntry(this.screen, lanserverinfo));
      }

      this.refreshEntries();
   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 30;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 85;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   @OnlyIn(Dist.CLIENT)
   public abstract static class Entry extends ExtendedList.AbstractListEntry<ServerSelectionList.Entry> {
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanDetectedEntry extends ServerSelectionList.Entry {
      private final MultiplayerScreen screen;
      protected final Minecraft minecraft;
      protected final LanServerInfo serverData;
      private long lastClickTime;

      protected LanDetectedEntry(MultiplayerScreen p_i47141_1_, LanServerInfo p_i47141_2_) {
         this.screen = p_i47141_1_;
         this.serverData = p_i47141_2_;
         this.minecraft = Minecraft.getInstance();
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         this.minecraft.font.func_211126_b(I18n.get("lanServer.title"), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         this.minecraft.font.func_211126_b(this.serverData.getMotd(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12), 8421504);
         if (this.minecraft.options.hideServerAddress) {
            this.minecraft.font.func_211126_b(I18n.get("selectServer.hiddenAddress"), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 11), 3158064);
         } else {
            this.minecraft.font.func_211126_b(this.serverData.getAddress(), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 11), 3158064);
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public LanServerInfo getServerData() {
         return this.serverData;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class LanScanEntry extends ServerSelectionList.Entry {
      private final Minecraft minecraft = Minecraft.getInstance();

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         int i = p_render_2_ + p_render_5_ / 2 - 9 / 2;
         this.minecraft.font.func_211126_b(I18n.get("lanServer.scanning"), (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(I18n.get("lanServer.scanning")) / 2), (float)i, 16777215);
         String s;
         switch((int)(Util.getMillis() / 300L % 4L)) {
         case 0:
         default:
            s = "O o o";
            break;
         case 1:
         case 3:
            s = "o O o";
            break;
         case 2:
            s = "o o O";
         }

         this.minecraft.font.func_211126_b(s, (float)(this.minecraft.screen.width / 2 - this.minecraft.font.width(s) / 2), (float)(i + 9), 8421504);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public class NormalEntry extends ServerSelectionList.Entry {
      private final MultiplayerScreen screen;
      private final Minecraft minecraft;
      private final ServerData serverData;
      private final ResourceLocation iconLocation;
      private String lastIconB64;
      private DynamicTexture icon;
      private long lastClickTime;

      protected NormalEntry(MultiplayerScreen p_i50669_2_, ServerData p_i50669_3_) {
         this.screen = p_i50669_2_;
         this.serverData = p_i50669_3_;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("servers/" + Hashing.sha1().hashUnencodedChars(p_i50669_3_.ip) + "/icon");
         this.icon = (DynamicTexture)this.minecraft.getTextureManager().getTexture(this.iconLocation);
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         if (!this.serverData.pinged) {
            this.serverData.pinged = true;
            this.serverData.ping = -2L;
            this.serverData.motd = "";
            this.serverData.status = "";
            ServerSelectionList.THREAD_POOL.submit(() -> {
               try {
                  this.screen.getPinger().pingServer(this.serverData);
               } catch (UnknownHostException var2) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = TextFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_resolve");
               } catch (Exception var3) {
                  this.serverData.ping = -1L;
                  this.serverData.motd = TextFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect");
               }

            });
         }

         boolean flag = this.serverData.protocol > SharedConstants.getCurrentVersion().getProtocolVersion();
         boolean flag1 = this.serverData.protocol < SharedConstants.getCurrentVersion().getProtocolVersion();
         boolean flag2 = flag || flag1;
         this.minecraft.font.func_211126_b(this.serverData.name, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         List<String> list = this.minecraft.font.func_78271_c(this.serverData.motd, p_render_4_ - 32 - 2);

         for(int i = 0; i < Math.min(list.size(), 2); ++i) {
            this.minecraft.font.func_211126_b(list.get(i), (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 12 + 9 * i), 8421504);
         }

         String s2 = flag2 ? TextFormatting.DARK_RED + this.serverData.version : this.serverData.status;
         int j = this.minecraft.font.width(s2);
         this.minecraft.font.func_211126_b(s2, (float)(p_render_3_ + p_render_4_ - j - 15 - 2), (float)(p_render_2_ + 1), 8421504);
         int k = 0;
         String s = null;
         int l;
         String s1;
         if (flag2) {
            l = 5;
            s1 = I18n.get(flag ? "multiplayer.status.client_out_of_date" : "multiplayer.status.server_out_of_date");
            s = this.serverData.playerList;
         } else if (this.serverData.pinged && this.serverData.ping != -2L) {
            if (this.serverData.ping < 0L) {
               l = 5;
            } else if (this.serverData.ping < 150L) {
               l = 0;
            } else if (this.serverData.ping < 300L) {
               l = 1;
            } else if (this.serverData.ping < 600L) {
               l = 2;
            } else if (this.serverData.ping < 1000L) {
               l = 3;
            } else {
               l = 4;
            }

            if (this.serverData.ping < 0L) {
               s1 = I18n.get("multiplayer.status.no_connection");
            } else {
               s1 = this.serverData.ping + "ms";
               s = this.serverData.playerList;
            }
         } else {
            k = 1;
            l = (int)(Util.getMillis() / 100L + (long)(p_render_1_ * 2) & 7L);
            if (l > 4) {
               l = 8 - l;
            }

            s1 = I18n.get("multiplayer.status.pinging");
         }

         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
         AbstractGui.blit(p_render_3_ + p_render_4_ - 15, p_render_2_, (float)(k * 10), (float)(176 + l * 8), 10, 8, 256, 256);
         if (this.serverData.getIconB64() != null && !this.serverData.getIconB64().equals(this.lastIconB64)) {
            this.lastIconB64 = this.serverData.getIconB64();
            this.func_148297_b();
            this.screen.getServers().save();
         }

         if (this.icon != null) {
            this.func_178012_a(p_render_3_, p_render_2_, this.iconLocation);
         } else {
            this.func_178012_a(p_render_3_, p_render_2_, ServerSelectionList.ICON_MISSING);
         }

         int i1 = p_render_6_ - p_render_3_;
         int j1 = p_render_7_ - p_render_2_;
         if (i1 >= p_render_4_ - 15 && i1 <= p_render_4_ - 5 && j1 >= 0 && j1 <= 8) {
            this.screen.func_146793_a(s1);
         } else if (i1 >= p_render_4_ - j - 15 - 2 && i1 <= p_render_4_ - 15 - 2 && j1 >= 0 && j1 <= 8) {
            this.screen.func_146793_a(s);
         }

         net.minecraftforge.fml.client.ClientHooks.drawForgePingInfo(this.screen, serverData, p_render_3_, p_render_2_, p_render_4_, i1, j1);

         if (this.minecraft.options.touchscreen || p_render_8_) {
            this.minecraft.getTextureManager().bind(ServerSelectionList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int k1 = p_render_6_ - p_render_3_;
            int l1 = p_render_7_ - p_render_2_;
            if (this.canJoin()) {
               if (k1 < 32 && k1 > 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_render_1_ > 0) {
               if (k1 < 16 && l1 < 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, 0.0F, 32, 32, 256, 256);
               }
            }

            if (p_render_1_ < this.screen.getServers().size() - 1) {
               if (k1 < 16 && l1 > 16) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 32.0F, 32, 32, 256, 256);
               } else {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, 0.0F, 32, 32, 256, 256);
               }
            }
         }

      }

      protected void func_178012_a(int p_178012_1_, int p_178012_2_, ResourceLocation p_178012_3_) {
         this.minecraft.getTextureManager().bind(p_178012_3_);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_178012_1_, p_178012_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
      }

      private boolean canJoin() {
         return true;
      }

      private void func_148297_b() {
         String s = this.serverData.getIconB64();
         if (s == null) {
            this.minecraft.getTextureManager().release(this.iconLocation);
            if (this.icon != null && this.icon.getPixels() != null) {
               this.icon.getPixels().close();
            }

            this.icon = null;
         } else {
            try {
               NativeImage nativeimage = NativeImage.fromBase64(s);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               if (this.icon == null) {
                  this.icon = new DynamicTexture(nativeimage);
               } else {
                  this.icon.setPixels(nativeimage);
                  this.icon.upload();
               }

               this.minecraft.getTextureManager().register(this.iconLocation, this.icon);
            } catch (Throwable throwable) {
               ServerSelectionList.LOGGER.error("Invalid icon for server {} ({})", this.serverData.name, this.serverData.ip, throwable);
               this.serverData.setIconB64((String)null);
            }
         }

      }

      public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
         if (Screen.hasShiftDown()) {
            ServerSelectionList serverselectionlist = this.screen.serverSelectionList;
            int i = serverselectionlist.children().indexOf(this);
            if (p_keyPressed_1_ == 264 && i < this.screen.getServers().size() - 1 || p_keyPressed_1_ == 265 && i > 0) {
               this.swap(i, p_keyPressed_1_ == 264 ? i + 1 : i - 1);
               return true;
            }
         }

         return super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }

      private void swap(int p_228196_1_, int p_228196_2_) {
         this.screen.getServers().swap(p_228196_1_, p_228196_2_);
         this.screen.serverSelectionList.updateOnlineServers(this.screen.getServers());
         ServerSelectionList.Entry serverselectionlist$entry = this.screen.serverSelectionList.children().get(p_228196_2_);
         this.screen.serverSelectionList.setSelected(serverselectionlist$entry);
         ServerSelectionList.this.ensureVisible(serverselectionlist$entry);
      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         double d0 = p_mouseClicked_1_ - (double)ServerSelectionList.this.getRowLeft();
         double d1 = p_mouseClicked_3_ - (double)ServerSelectionList.this.getRowTop(ServerSelectionList.this.children().indexOf(this));
         if (d0 <= 32.0D) {
            if (d0 < 32.0D && d0 > 16.0D && this.canJoin()) {
               this.screen.setSelected(this);
               this.screen.joinSelectedServer();
               return true;
            }

            int i = this.screen.serverSelectionList.children().indexOf(this);
            if (d0 < 16.0D && d1 < 16.0D && i > 0) {
               this.swap(i, i - 1);
               return true;
            }

            if (d0 < 16.0D && d1 > 16.0D && i < this.screen.getServers().size() - 1) {
               this.swap(i, i + 1);
               return true;
            }
         }

         this.screen.setSelected(this);
         if (Util.getMillis() - this.lastClickTime < 250L) {
            this.screen.joinSelectedServer();
         }

         this.lastClickTime = Util.getMillis();
         return false;
      }

      public ServerData getServerData() {
         return this.serverData;
      }
   }
}
