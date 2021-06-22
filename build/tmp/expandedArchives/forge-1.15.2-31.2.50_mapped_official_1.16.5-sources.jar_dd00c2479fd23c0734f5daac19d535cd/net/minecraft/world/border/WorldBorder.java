package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class WorldBorder {
   private final List<IBorderListener> listeners = Lists.newArrayList();
   private double damagePerBlock = 0.2D;
   private double damageSafeZone = 5.0D;
   private int warningTime = 15;
   private int warningBlocks = 5;
   private double centerX;
   private double centerZ;
   private int absoluteMaxSize = 29999984;
   private WorldBorder.IBorderInfo extent = new WorldBorder.StationaryBorderInfo(6.0E7D);

   public boolean isWithinBounds(BlockPos p_177746_1_) {
      return (double)(p_177746_1_.getX() + 1) > this.getMinX() && (double)p_177746_1_.getX() < this.getMaxX() && (double)(p_177746_1_.getZ() + 1) > this.getMinZ() && (double)p_177746_1_.getZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(ChunkPos p_177730_1_) {
      return (double)p_177730_1_.getMaxBlockX() > this.getMinX() && (double)p_177730_1_.getMinBlockX() < this.getMaxX() && (double)p_177730_1_.getMaxBlockZ() > this.getMinZ() && (double)p_177730_1_.getMinBlockZ() < this.getMaxZ();
   }

   public boolean isWithinBounds(AxisAlignedBB p_177743_1_) {
      return p_177743_1_.maxX > this.getMinX() && p_177743_1_.minX < this.getMaxX() && p_177743_1_.maxZ > this.getMinZ() && p_177743_1_.minZ < this.getMaxZ();
   }

   public double getDistanceToBorder(Entity p_177745_1_) {
      return this.getDistanceToBorder(p_177745_1_.getX(), p_177745_1_.getZ());
   }

   public VoxelShape getCollisionShape() {
      return this.extent.getCollisionShape();
   }

   public double getDistanceToBorder(double p_177729_1_, double p_177729_3_) {
      double d0 = p_177729_3_ - this.getMinZ();
      double d1 = this.getMaxZ() - p_177729_3_;
      double d2 = p_177729_1_ - this.getMinX();
      double d3 = this.getMaxX() - p_177729_1_;
      double d4 = Math.min(d2, d3);
      d4 = Math.min(d4, d0);
      return Math.min(d4, d1);
   }

   @OnlyIn(Dist.CLIENT)
   public BorderStatus getStatus() {
      return this.extent.getStatus();
   }

   public double getMinX() {
      return this.extent.getMinX();
   }

   public double getMinZ() {
      return this.extent.getMinZ();
   }

   public double getMaxX() {
      return this.extent.getMaxX();
   }

   public double getMaxZ() {
      return this.extent.getMaxZ();
   }

   public double func_177731_f() {
      return this.centerX;
   }

   public double func_177721_g() {
      return this.centerZ;
   }

   public void setCenter(double p_177739_1_, double p_177739_3_) {
      this.centerX = p_177739_1_;
      this.centerZ = p_177739_3_;
      this.extent.onCenterChange();

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderCenterSet(this, p_177739_1_, p_177739_3_);
      }

   }

   public double getSize() {
      return this.extent.getSize();
   }

   public long getLerpRemainingTime() {
      return this.extent.getLerpRemainingTime();
   }

   public double getLerpTarget() {
      return this.extent.getLerpTarget();
   }

   public void setSize(double p_177750_1_) {
      this.extent = new WorldBorder.StationaryBorderInfo(p_177750_1_);

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSizeSet(this, p_177750_1_);
      }

   }

   public void lerpSizeBetween(double p_177738_1_, double p_177738_3_, long p_177738_5_) {
      this.extent = (WorldBorder.IBorderInfo)(p_177738_1_ == p_177738_3_ ? new WorldBorder.StationaryBorderInfo(p_177738_3_) : new WorldBorder.MovingBorderInfo(p_177738_1_, p_177738_3_, p_177738_5_));

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSizeLerping(this, p_177738_1_, p_177738_3_, p_177738_5_);
      }

   }

   protected List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener p_177737_1_) {
      this.listeners.add(p_177737_1_);
   }

   public void removeListener(IBorderListener listener) {
      this.listeners.remove(listener);
   }

   public void setAbsoluteMaxSize(int p_177725_1_) {
      this.absoluteMaxSize = p_177725_1_;
      this.extent.onAbsoluteMaxSizeChange();
   }

   public int getAbsoluteMaxSize() {
      return this.absoluteMaxSize;
   }

   public double getDamageSafeZone() {
      return this.damageSafeZone;
   }

   public void setDamageSafeZone(double p_177724_1_) {
      this.damageSafeZone = p_177724_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSetDamageSafeZOne(this, p_177724_1_);
      }

   }

   public double getDamagePerBlock() {
      return this.damagePerBlock;
   }

   public void setDamagePerBlock(double p_177744_1_) {
      this.damagePerBlock = p_177744_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSetDamagePerBlock(this, p_177744_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public double getLerpSpeed() {
      return this.extent.getLerpSpeed();
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int p_177723_1_) {
      this.warningTime = p_177723_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSetWarningTime(this, p_177723_1_);
      }

   }

   public int getWarningBlocks() {
      return this.warningBlocks;
   }

   public void setWarningBlocks(int p_177747_1_) {
      this.warningBlocks = p_177747_1_;

      for(IBorderListener iborderlistener : this.getListeners()) {
         iborderlistener.onBorderSetWarningBlocks(this, p_177747_1_);
      }

   }

   public void tick() {
      this.extent = this.extent.update();
   }

   public void func_222520_a(WorldInfo p_222520_1_) {
      p_222520_1_.func_176145_a(this.getSize());
      p_222520_1_.func_176124_d(this.func_177731_f());
      p_222520_1_.func_176141_c(this.func_177721_g());
      p_222520_1_.func_176129_e(this.getDamageSafeZone());
      p_222520_1_.func_176125_f(this.getDamagePerBlock());
      p_222520_1_.func_176122_j(this.getWarningBlocks());
      p_222520_1_.func_176136_k(this.getWarningTime());
      p_222520_1_.func_176118_b(this.getLerpTarget());
      p_222520_1_.func_176135_e(this.getLerpRemainingTime());
   }

   public void func_222519_b(WorldInfo p_222519_1_) {
      this.setCenter(p_222519_1_.func_176120_C(), p_222519_1_.func_176126_D());
      this.setDamagePerBlock(p_222519_1_.func_176140_I());
      this.setDamageSafeZone(p_222519_1_.func_176138_H());
      this.setWarningBlocks(p_222519_1_.func_176131_J());
      this.setWarningTime(p_222519_1_.func_176139_K());
      if (p_222519_1_.func_176134_F() > 0L) {
         this.lerpSizeBetween(p_222519_1_.func_176137_E(), p_222519_1_.func_176132_G(), p_222519_1_.func_176134_F());
      } else {
         this.setSize(p_222519_1_.func_176137_E());
      }

   }

   interface IBorderInfo {
      double getMinX();

      double getMaxX();

      double getMinZ();

      double getMaxZ();

      double getSize();

      @OnlyIn(Dist.CLIENT)
      double getLerpSpeed();

      long getLerpRemainingTime();

      double getLerpTarget();

      @OnlyIn(Dist.CLIENT)
      BorderStatus getStatus();

      void onAbsoluteMaxSizeChange();

      void onCenterChange();

      WorldBorder.IBorderInfo update();

      VoxelShape getCollisionShape();
   }

   class MovingBorderInfo implements WorldBorder.IBorderInfo {
      private final double from;
      private final double to;
      private final long lerpEnd;
      private final long lerpBegin;
      private final double lerpDuration;

      private MovingBorderInfo(double p_i49838_2_, double p_i49838_4_, long p_i49838_6_) {
         this.from = p_i49838_2_;
         this.to = p_i49838_4_;
         this.lerpDuration = (double)p_i49838_6_;
         this.lerpBegin = Util.getMillis();
         this.lerpEnd = this.lerpBegin + p_i49838_6_;
      }

      public double getMinX() {
         return Math.max(WorldBorder.this.func_177731_f() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMinZ() {
         return Math.max(WorldBorder.this.func_177721_g() - this.getSize() / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
      }

      public double getMaxX() {
         return Math.min(WorldBorder.this.func_177731_f() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getMaxZ() {
         return Math.min(WorldBorder.this.func_177721_g() + this.getSize() / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
      }

      public double getSize() {
         double d0 = (double)(Util.getMillis() - this.lerpBegin) / this.lerpDuration;
         return d0 < 1.0D ? MathHelper.lerp(d0, this.from, this.to) : this.to;
      }

      @OnlyIn(Dist.CLIENT)
      public double getLerpSpeed() {
         return Math.abs(this.from - this.to) / (double)(this.lerpEnd - this.lerpBegin);
      }

      public long getLerpRemainingTime() {
         return this.lerpEnd - Util.getMillis();
      }

      public double getLerpTarget() {
         return this.to;
      }

      @OnlyIn(Dist.CLIENT)
      public BorderStatus getStatus() {
         return this.to < this.from ? BorderStatus.SHRINKING : BorderStatus.GROWING;
      }

      public void onCenterChange() {
      }

      public void onAbsoluteMaxSizeChange() {
      }

      public WorldBorder.IBorderInfo update() {
         return (WorldBorder.IBorderInfo)(this.getLerpRemainingTime() <= 0L ? WorldBorder.this.new StationaryBorderInfo(this.to) : this);
      }

      public VoxelShape getCollisionShape() {
         return VoxelShapes.join(VoxelShapes.INFINITY, VoxelShapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), IBooleanFunction.ONLY_FIRST);
      }
   }

   class StationaryBorderInfo implements WorldBorder.IBorderInfo {
      private final double size;
      private double minX;
      private double minZ;
      private double maxX;
      private double maxZ;
      private VoxelShape shape;

      public StationaryBorderInfo(double p_i49837_2_) {
         this.size = p_i49837_2_;
         this.updateBox();
      }

      public double getMinX() {
         return this.minX;
      }

      public double getMaxX() {
         return this.maxX;
      }

      public double getMinZ() {
         return this.minZ;
      }

      public double getMaxZ() {
         return this.maxZ;
      }

      public double getSize() {
         return this.size;
      }

      @OnlyIn(Dist.CLIENT)
      public BorderStatus getStatus() {
         return BorderStatus.STATIONARY;
      }

      @OnlyIn(Dist.CLIENT)
      public double getLerpSpeed() {
         return 0.0D;
      }

      public long getLerpRemainingTime() {
         return 0L;
      }

      public double getLerpTarget() {
         return this.size;
      }

      private void updateBox() {
         this.minX = Math.max(WorldBorder.this.func_177731_f() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.minZ = Math.max(WorldBorder.this.func_177721_g() - this.size / 2.0D, (double)(-WorldBorder.this.absoluteMaxSize));
         this.maxX = Math.min(WorldBorder.this.func_177731_f() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.maxZ = Math.min(WorldBorder.this.func_177721_g() + this.size / 2.0D, (double)WorldBorder.this.absoluteMaxSize);
         this.shape = VoxelShapes.join(VoxelShapes.INFINITY, VoxelShapes.box(Math.floor(this.getMinX()), Double.NEGATIVE_INFINITY, Math.floor(this.getMinZ()), Math.ceil(this.getMaxX()), Double.POSITIVE_INFINITY, Math.ceil(this.getMaxZ())), IBooleanFunction.ONLY_FIRST);
      }

      public void onAbsoluteMaxSizeChange() {
         this.updateBox();
      }

      public void onCenterChange() {
         this.updateBox();
      }

      public WorldBorder.IBorderInfo update() {
         return this;
      }

      public VoxelShape getCollisionShape() {
         return this.shape;
      }
   }
}
