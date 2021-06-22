package net.minecraft.network.play.server;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.IPacket;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SRecipeBookPacket implements IPacket<IClientPlayNetHandler> {
   private SRecipeBookPacket.State state;
   private List<ResourceLocation> recipes;
   private List<ResourceLocation> toHighlight;
   private boolean field_192598_c;
   private boolean field_192599_d;
   private boolean field_202494_f;
   private boolean field_202495_g;

   public SRecipeBookPacket() {
   }

   public SRecipeBookPacket(SRecipeBookPacket.State p_i48735_1_, Collection<ResourceLocation> p_i48735_2_, Collection<ResourceLocation> p_i48735_3_, boolean p_i48735_4_, boolean p_i48735_5_, boolean p_i48735_6_, boolean p_i48735_7_) {
      this.state = p_i48735_1_;
      this.recipes = ImmutableList.copyOf(p_i48735_2_);
      this.toHighlight = ImmutableList.copyOf(p_i48735_3_);
      this.field_192598_c = p_i48735_4_;
      this.field_192599_d = p_i48735_5_;
      this.field_202494_f = p_i48735_6_;
      this.field_202495_g = p_i48735_7_;
   }

   public void handle(IClientPlayNetHandler p_148833_1_) {
      p_148833_1_.handleAddOrRemoveRecipes(this);
   }

   public void read(PacketBuffer p_148837_1_) throws IOException {
      this.state = p_148837_1_.readEnum(SRecipeBookPacket.State.class);
      this.field_192598_c = p_148837_1_.readBoolean();
      this.field_192599_d = p_148837_1_.readBoolean();
      this.field_202494_f = p_148837_1_.readBoolean();
      this.field_202495_g = p_148837_1_.readBoolean();
      int i = p_148837_1_.readVarInt();
      this.recipes = Lists.newArrayList();

      for(int j = 0; j < i; ++j) {
         this.recipes.add(p_148837_1_.readResourceLocation());
      }

      if (this.state == SRecipeBookPacket.State.INIT) {
         i = p_148837_1_.readVarInt();
         this.toHighlight = Lists.newArrayList();

         for(int k = 0; k < i; ++k) {
            this.toHighlight.add(p_148837_1_.readResourceLocation());
         }
      }

   }

   public void write(PacketBuffer p_148840_1_) throws IOException {
      p_148840_1_.writeEnum(this.state);
      p_148840_1_.writeBoolean(this.field_192598_c);
      p_148840_1_.writeBoolean(this.field_192599_d);
      p_148840_1_.writeBoolean(this.field_202494_f);
      p_148840_1_.writeBoolean(this.field_202495_g);
      p_148840_1_.writeVarInt(this.recipes.size());

      for(ResourceLocation resourcelocation : this.recipes) {
         p_148840_1_.writeResourceLocation(resourcelocation);
      }

      if (this.state == SRecipeBookPacket.State.INIT) {
         p_148840_1_.writeVarInt(this.toHighlight.size());

         for(ResourceLocation resourcelocation1 : this.toHighlight) {
            p_148840_1_.writeResourceLocation(resourcelocation1);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public List<ResourceLocation> getRecipes() {
      return this.recipes;
   }

   @OnlyIn(Dist.CLIENT)
   public List<ResourceLocation> getHighlights() {
      return this.toHighlight;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_192593_c() {
      return this.field_192598_c;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_192594_d() {
      return this.field_192599_d;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202492_e() {
      return this.field_202494_f;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean func_202493_f() {
      return this.field_202495_g;
   }

   @OnlyIn(Dist.CLIENT)
   public SRecipeBookPacket.State getState() {
      return this.state;
   }

   public static enum State {
      INIT,
      ADD,
      REMOVE;
   }
}