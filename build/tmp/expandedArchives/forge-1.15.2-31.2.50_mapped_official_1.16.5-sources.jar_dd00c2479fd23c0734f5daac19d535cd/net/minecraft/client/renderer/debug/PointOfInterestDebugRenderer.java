package net.minecraft.client.renderer.debug;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.RandomObjectDescriptor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.SectionPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class PointOfInterestDebugRenderer implements DebugRenderer.IDebugRenderer {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final Map<BlockPos, PointOfInterestDebugRenderer.POIInfo> pois = Maps.newHashMap();
   private final Set<SectionPos> field_217714_d = Sets.newHashSet();
   private final Map<UUID, PointOfInterestDebugRenderer.BrainInfo> field_217715_e = Maps.newHashMap();
   private UUID lastLookedAtUuid;

   public PointOfInterestDebugRenderer(Minecraft p_i50976_1_) {
      this.minecraft = p_i50976_1_;
   }

   public void clear() {
      this.pois.clear();
      this.field_217714_d.clear();
      this.field_217715_e.clear();
      this.lastLookedAtUuid = null;
   }

   public void addPoi(PointOfInterestDebugRenderer.POIInfo p_217691_1_) {
      this.pois.put(p_217691_1_.pos, p_217691_1_);
   }

   public void removePoi(BlockPos p_217698_1_) {
      this.pois.remove(p_217698_1_);
   }

   public void setFreeTicketCount(BlockPos p_217706_1_, int p_217706_2_) {
      PointOfInterestDebugRenderer.POIInfo pointofinterestdebugrenderer$poiinfo = this.pois.get(p_217706_1_);
      if (pointofinterestdebugrenderer$poiinfo == null) {
         LOGGER.warn("Strange, setFreeTicketCount was called for an unknown POI: " + p_217706_1_);
      } else {
         pointofinterestdebugrenderer$poiinfo.freeTicketCount = p_217706_2_;
      }
   }

   public void func_217701_a(SectionPos p_217701_1_) {
      this.field_217714_d.add(p_217701_1_);
   }

   public void func_217700_b(SectionPos p_217700_1_) {
      this.field_217714_d.remove(p_217700_1_);
   }

   public void addOrUpdateBrainDump(PointOfInterestDebugRenderer.BrainInfo p_217692_1_) {
      this.field_217715_e.put(p_217692_1_.uuid, p_217692_1_);
   }

   public void render(MatrixStack p_225619_1_, IRenderTypeBuffer p_225619_2_, double p_225619_3_, double p_225619_5_, double p_225619_7_) {
      RenderSystem.pushMatrix();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.disableTexture();
      this.doRender(p_225619_3_, p_225619_5_, p_225619_7_);
      RenderSystem.enableTexture();
      RenderSystem.disableBlend();
      RenderSystem.popMatrix();
      if (!this.minecraft.player.isSpectator()) {
         this.updateLastLookedAtUuid();
      }

   }

   private void doRender(double p_229035_1_, double p_229035_3_, double p_229035_5_) {
      BlockPos blockpos = new BlockPos(p_229035_1_, p_229035_3_, p_229035_5_);
      this.field_217714_d.forEach((p_222924_1_) -> {
         if (blockpos.closerThan(p_222924_1_.center(), 60.0D)) {
            func_217702_c(p_222924_1_);
         }

      });
      this.field_217715_e.values().forEach((p_229036_7_) -> {
         if (this.isPlayerCloseEnoughToMob(p_229036_7_)) {
            this.renderBrainInfo(p_229036_7_, p_229035_1_, p_229035_3_, p_229035_5_);
         }

      });

      for(BlockPos blockpos1 : this.pois.keySet()) {
         if (blockpos.closerThan(blockpos1, 30.0D)) {
            highlightPoi(blockpos1);
         }
      }

      this.pois.values().forEach((p_222916_2_) -> {
         if (blockpos.closerThan(p_222916_2_.pos, 30.0D)) {
            this.renderPoiInfo(p_222916_2_);
         }

      });
      this.getGhostPois().forEach((p_222925_2_, p_222925_3_) -> {
         if (blockpos.closerThan(p_222925_2_, 30.0D)) {
            this.renderGhostPoi(p_222925_2_, p_222925_3_);
         }

      });
   }

   private static void func_217702_c(SectionPos p_217702_0_) {
      float f = 1.0F;
      BlockPos blockpos = p_217702_0_.center();
      BlockPos blockpos1 = blockpos.offset(-1.0D, -1.0D, -1.0D);
      BlockPos blockpos2 = blockpos.offset(1.0D, 1.0D, 1.0D);
      DebugRenderer.renderFilledBox(blockpos1, blockpos2, 0.2F, 1.0F, 0.2F, 0.15F);
   }

   private static void highlightPoi(BlockPos p_217699_0_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_217699_0_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
   }

   private void renderGhostPoi(BlockPos p_222921_1_, List<String> p_222921_2_) {
      float f = 0.05F;
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      DebugRenderer.renderFilledBox(p_222921_1_, 0.05F, 0.2F, 0.2F, 1.0F, 0.3F);
      renderTextOverPos("" + p_222921_2_, p_222921_1_, 0, -256);
      renderTextOverPos("Ghost POI", p_222921_1_, 1, -65536);
   }

   private void renderPoiInfo(PointOfInterestDebugRenderer.POIInfo p_217705_1_) {
      int i = 0;
      if (this.getTicketHolderNames(p_217705_1_).size() < 4) {
         renderTextOverPoi("" + this.getTicketHolderNames(p_217705_1_), p_217705_1_, i, -256);
      } else {
         renderTextOverPoi("" + this.getTicketHolderNames(p_217705_1_).size() + " ticket holders", p_217705_1_, i, -256);
      }

      ++i;
      renderTextOverPoi("Free tickets: " + p_217705_1_.freeTicketCount, p_217705_1_, i, -256);
      ++i;
      renderTextOverPoi(p_217705_1_.type, p_217705_1_, i, -1);
   }

   private void renderPath(PointOfInterestDebugRenderer.BrainInfo p_229037_1_, double p_229037_2_, double p_229037_4_, double p_229037_6_) {
      if (p_229037_1_.path != null) {
         PathfindingDebugRenderer.renderPath(p_229037_1_.path, 0.5F, false, false, p_229037_2_, p_229037_4_, p_229037_6_);
      }

   }

   private void renderBrainInfo(PointOfInterestDebugRenderer.BrainInfo p_229038_1_, double p_229038_2_, double p_229038_4_, double p_229038_6_) {
      boolean flag = this.isMobSelected(p_229038_1_);
      int i = 0;
      renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.name, -1, 0.03F);
      ++i;
      if (flag) {
         renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.profession + " " + p_229038_1_.xp + "xp", -1, 0.02F);
         ++i;
      }

      if (flag && !p_229038_1_.inventory.equals("")) {
         renderTextOverMob(p_229038_1_.pos, i, p_229038_1_.inventory, -98404, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s : p_229038_1_.behaviors) {
            renderTextOverMob(p_229038_1_.pos, i, s, -16711681, 0.02F);
            ++i;
         }
      }

      if (flag) {
         for(String s1 : p_229038_1_.activities) {
            renderTextOverMob(p_229038_1_.pos, i, s1, -16711936, 0.02F);
            ++i;
         }
      }

      if (p_229038_1_.wantsGolem) {
         renderTextOverMob(p_229038_1_.pos, i, "Wants Golem", -23296, 0.02F);
         ++i;
      }

      if (flag) {
         for(String s2 : p_229038_1_.gossips) {
            if (s2.startsWith(p_229038_1_.name)) {
               renderTextOverMob(p_229038_1_.pos, i, s2, -1, 0.02F);
            } else {
               renderTextOverMob(p_229038_1_.pos, i, s2, -23296, 0.02F);
            }

            ++i;
         }
      }

      if (flag) {
         for(String s3 : Lists.reverse(p_229038_1_.memories)) {
            renderTextOverMob(p_229038_1_.pos, i, s3, -3355444, 0.02F);
            ++i;
         }
      }

      if (flag) {
         this.renderPath(p_229038_1_, p_229038_2_, p_229038_4_, p_229038_6_);
      }

   }

   private static void renderTextOverPoi(String p_217695_0_, PointOfInterestDebugRenderer.POIInfo p_217695_1_, int p_217695_2_, int p_217695_3_) {
      BlockPos blockpos = p_217695_1_.pos;
      renderTextOverPos(p_217695_0_, blockpos, p_217695_2_, p_217695_3_);
   }

   private static void renderTextOverPos(String p_222923_0_, BlockPos p_222923_1_, int p_222923_2_, int p_222923_3_) {
      double d0 = 1.3D;
      double d1 = 0.2D;
      double d2 = (double)p_222923_1_.getX() + 0.5D;
      double d3 = (double)p_222923_1_.getY() + 1.3D + (double)p_222923_2_ * 0.2D;
      double d4 = (double)p_222923_1_.getZ() + 0.5D;
      DebugRenderer.renderFloatingText(p_222923_0_, d2, d3, d4, p_222923_3_, 0.02F, true, 0.0F, true);
   }

   private static void renderTextOverMob(IPosition p_217693_0_, int p_217693_1_, String p_217693_2_, int p_217693_3_, float p_217693_4_) {
      double d0 = 2.4D;
      double d1 = 0.25D;
      BlockPos blockpos = new BlockPos(p_217693_0_);
      double d2 = (double)blockpos.getX() + 0.5D;
      double d3 = p_217693_0_.y() + 2.4D + (double)p_217693_1_ * 0.25D;
      double d4 = (double)blockpos.getZ() + 0.5D;
      float f = 0.5F;
      DebugRenderer.renderFloatingText(p_217693_2_, d2, d3, d4, p_217693_3_, p_217693_4_, false, 0.5F, true);
   }

   private Set<String> getTicketHolderNames(PointOfInterestDebugRenderer.POIInfo p_217696_1_) {
      return this.func_217697_c(p_217696_1_.pos).stream().map(RandomObjectDescriptor::getEntityName).collect(Collectors.toSet());
   }

   private boolean isMobSelected(PointOfInterestDebugRenderer.BrainInfo p_217703_1_) {
      return Objects.equals(this.lastLookedAtUuid, p_217703_1_.uuid);
   }

   private boolean isPlayerCloseEnoughToMob(PointOfInterestDebugRenderer.BrainInfo p_217694_1_) {
      PlayerEntity playerentity = this.minecraft.player;
      BlockPos blockpos = new BlockPos(playerentity.getX(), p_217694_1_.pos.y(), playerentity.getZ());
      BlockPos blockpos1 = new BlockPos(p_217694_1_.pos);
      return blockpos.closerThan(blockpos1, 30.0D);
   }

   private Collection<UUID> func_217697_c(BlockPos p_217697_1_) {
      return this.field_217715_e.values().stream().filter((p_217690_1_) -> {
         return p_217690_1_.hasPoi(p_217697_1_);
      }).map(PointOfInterestDebugRenderer.BrainInfo::getUuid).collect(Collectors.toSet());
   }

   private Map<BlockPos, List<String>> getGhostPois() {
      Map<BlockPos, List<String>> map = Maps.newHashMap();

      for(PointOfInterestDebugRenderer.BrainInfo pointofinterestdebugrenderer$braininfo : this.field_217715_e.values()) {
         for(BlockPos blockpos : pointofinterestdebugrenderer$braininfo.pois) {
            if (!this.pois.containsKey(blockpos)) {
               List<String> list = map.get(blockpos);
               if (list == null) {
                  list = Lists.newArrayList();
                  map.put(blockpos, list);
               }

               list.add(pointofinterestdebugrenderer$braininfo.name);
            }
         }
      }

      return map;
   }

   private void updateLastLookedAtUuid() {
      DebugRenderer.getTargetedEntity(this.minecraft.getCameraEntity(), 8).ifPresent((p_217707_1_) -> {
         this.lastLookedAtUuid = p_217707_1_.getUUID();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public static class BrainInfo {
      public final UUID uuid;
      public final int id;
      public final String name;
      public final String profession;
      public final int xp;
      public final IPosition pos;
      public final String inventory;
      public final Path path;
      public final boolean wantsGolem;
      public final List<String> activities = Lists.newArrayList();
      public final List<String> behaviors = Lists.newArrayList();
      public final List<String> memories = Lists.newArrayList();
      public final List<String> gossips = Lists.newArrayList();
      public final Set<BlockPos> pois = Sets.newHashSet();

      public BrainInfo(UUID p_i51529_1_, int p_i51529_2_, String p_i51529_3_, String p_i51529_4_, int p_i51529_5_, IPosition p_i51529_6_, String p_i51529_7_, @Nullable Path p_i51529_8_, boolean p_i51529_9_) {
         this.uuid = p_i51529_1_;
         this.id = p_i51529_2_;
         this.name = p_i51529_3_;
         this.profession = p_i51529_4_;
         this.xp = p_i51529_5_;
         this.pos = p_i51529_6_;
         this.inventory = p_i51529_7_;
         this.path = p_i51529_8_;
         this.wantsGolem = p_i51529_9_;
      }

      private boolean hasPoi(BlockPos p_217744_1_) {
         return this.pois.stream().anyMatch(p_217744_1_::equals);
      }

      public UUID getUuid() {
         return this.uuid;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class POIInfo {
      public final BlockPos pos;
      public String type;
      public int freeTicketCount;

      public POIInfo(BlockPos p_i50886_1_, String p_i50886_2_, int p_i50886_3_) {
         this.pos = p_i50886_1_;
         this.type = p_i50886_2_;
         this.freeTicketCount = p_i50886_3_;
      }
   }
}