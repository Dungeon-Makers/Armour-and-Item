package net.minecraft.village;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Objects;
import net.minecraft.util.IDynamicSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

public class PointOfInterest implements IDynamicSerializable {
   private final BlockPos pos;
   private final PointOfInterestType poiType;
   private int freeTickets;
   private final Runnable setDirty;

   private PointOfInterest(BlockPos p_i50295_1_, PointOfInterestType p_i50295_2_, int p_i50295_3_, Runnable p_i50295_4_) {
      this.pos = p_i50295_1_.immutable();
      this.poiType = p_i50295_2_;
      this.freeTickets = p_i50295_3_;
      this.setDirty = p_i50295_4_;
   }

   public PointOfInterest(BlockPos p_i50296_1_, PointOfInterestType p_i50296_2_, Runnable p_i50296_3_) {
      this(p_i50296_1_, p_i50296_2_, p_i50296_2_.getMaxTickets(), p_i50296_3_);
   }

   public <T> PointOfInterest(Dynamic<T> p_i50297_1_, Runnable p_i50297_2_) {
      this(p_i50297_1_.get("pos").map(BlockPos::func_218286_a).orElse(new BlockPos(0, 0, 0)), Registry.POINT_OF_INTEREST_TYPE.get(new ResourceLocation(p_i50297_1_.get("type").asString(""))), p_i50297_1_.get("free_tickets").asInt(0), p_i50297_2_);
   }

   public <T> T func_218175_a(DynamicOps<T> p_218175_1_) {
      return p_218175_1_.createMap(ImmutableMap.of(p_218175_1_.createString("pos"), this.pos.func_218175_a(p_218175_1_), p_218175_1_.createString("type"), p_218175_1_.createString(Registry.POINT_OF_INTEREST_TYPE.getKey(this.poiType).toString()), p_218175_1_.createString("free_tickets"), p_218175_1_.createInt(this.freeTickets)));
   }

   protected boolean acquireTicket() {
      if (this.freeTickets <= 0) {
         return false;
      } else {
         --this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   protected boolean releaseTicket() {
      if (this.freeTickets >= this.poiType.getMaxTickets()) {
         return false;
      } else {
         ++this.freeTickets;
         this.setDirty.run();
         return true;
      }
   }

   public boolean hasSpace() {
      return this.freeTickets > 0;
   }

   public boolean isOccupied() {
      return this.freeTickets != this.poiType.getMaxTickets();
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PointOfInterestType getPoiType() {
      return this.poiType;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ != null && this.getClass() == p_equals_1_.getClass() ? Objects.equals(this.pos, ((PointOfInterest)p_equals_1_).pos) : false;
      }
   }

   public int hashCode() {
      return this.pos.hashCode();
   }
}