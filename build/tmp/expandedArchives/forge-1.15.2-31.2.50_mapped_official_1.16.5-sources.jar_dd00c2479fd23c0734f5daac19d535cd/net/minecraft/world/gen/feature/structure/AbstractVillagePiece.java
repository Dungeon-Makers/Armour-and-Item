package net.minecraft.world.gen.feature.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.IDynamicDeserializer;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.jigsaw.EmptyJigsawPiece;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPiece;
import net.minecraft.world.gen.feature.template.TemplateManager;

public abstract class AbstractVillagePiece extends StructurePiece {
   protected final JigsawPiece element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List<JigsawJunction> junctions = Lists.newArrayList();
   private final TemplateManager structureManager;

   public AbstractVillagePiece(IStructurePieceType p_i51346_1_, TemplateManager p_i51346_2_, JigsawPiece p_i51346_3_, BlockPos p_i51346_4_, int p_i51346_5_, Rotation p_i51346_6_, MutableBoundingBox p_i51346_7_) {
      super(p_i51346_1_, 0);
      this.structureManager = p_i51346_2_;
      this.element = p_i51346_3_;
      this.position = p_i51346_4_;
      this.groundLevelDelta = p_i51346_5_;
      this.rotation = p_i51346_6_;
      this.boundingBox = p_i51346_7_;
   }

   public AbstractVillagePiece(TemplateManager p_i51347_1_, CompoundNBT p_i51347_2_, IStructurePieceType p_i51347_3_) {
      super(p_i51347_3_, p_i51347_2_);
      this.structureManager = p_i51347_1_;
      this.position = new BlockPos(p_i51347_2_.getInt("PosX"), p_i51347_2_.getInt("PosY"), p_i51347_2_.getInt("PosZ"));
      this.groundLevelDelta = p_i51347_2_.getInt("ground_level_delta");
      this.element = IDynamicDeserializer.func_214907_a(new Dynamic<>(NBTDynamicOps.INSTANCE, p_i51347_2_.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyJigsawPiece.INSTANCE);
      this.rotation = Rotation.valueOf(p_i51347_2_.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(p_i51347_1_, this.position, this.rotation);
      ListNBT listnbt = p_i51347_2_.getList("junctions", 10);
      this.junctions.clear();
      listnbt.forEach((p_214827_1_) -> {
         this.junctions.add(JigsawJunction.func_214894_a(new Dynamic<>(NBTDynamicOps.INSTANCE, p_214827_1_)));
      });
   }

   protected void addAdditionalSaveData(CompoundNBT p_143011_1_) {
      p_143011_1_.putInt("PosX", this.position.getX());
      p_143011_1_.putInt("PosY", this.position.getY());
      p_143011_1_.putInt("PosZ", this.position.getZ());
      p_143011_1_.putInt("ground_level_delta", this.groundLevelDelta);
      p_143011_1_.put("pool_element", this.element.func_214847_b(NBTDynamicOps.INSTANCE).getValue());
      p_143011_1_.putString("rotation", this.rotation.name());
      ListNBT listnbt = new ListNBT();

      for(JigsawJunction jigsawjunction : this.junctions) {
         listnbt.add(jigsawjunction.func_214897_a(NBTDynamicOps.INSTANCE).getValue());
      }

      p_143011_1_.put("junctions", listnbt);
   }

   public boolean func_225577_a_(IWorld p_225577_1_, ChunkGenerator<?> p_225577_2_, Random p_225577_3_, MutableBoundingBox p_225577_4_, ChunkPos p_225577_5_) {
      return this.element.func_225575_a_(this.structureManager, p_225577_1_, p_225577_2_, this.position, this.rotation, p_225577_4_, p_225577_3_);
   }

   public void move(int p_181138_1_, int p_181138_2_, int p_181138_3_) {
      super.move(p_181138_1_, p_181138_2_, p_181138_3_);
      this.position = this.position.offset(p_181138_1_, p_181138_2_, p_181138_3_);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format("<%s | %s | %s | %s>", this.getClass().getSimpleName(), this.position, this.rotation, this.element);
   }

   public JigsawPiece getElement() {
      return this.element;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction p_214831_1_) {
      this.junctions.add(p_214831_1_);
   }

   public List<JigsawJunction> getJunctions() {
      return this.junctions;
   }
}