package net.minecraft.client.gui.screen;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenCustomHashMap;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.Util;
import net.minecraft.util.WorldOptimizer;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.storage.SaveFormat;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptimizeWorldScreen extends Screen {
   private static final Object2IntMap<DimensionType> DIMENSION_COLORS = Util.make(new Object2IntOpenCustomHashMap<>(Util.identityStrategy()), (p_212346_0_) -> {
      p_212346_0_.put(DimensionType.field_223227_a_, -13408734);
      p_212346_0_.put(DimensionType.field_223228_b_, -10075085);
      p_212346_0_.put(DimensionType.field_223229_c_, -8943531);
      p_212346_0_.defaultReturnValue(-2236963);
   });
   private final BooleanConsumer callback;
   private final WorldOptimizer upgrader;

   public OptimizeWorldScreen(BooleanConsumer p_i51072_1_, String p_i51072_2_, SaveFormat p_i51072_3_, boolean p_i51072_4_) {
      super(new TranslationTextComponent("optimizeWorld.title", p_i51072_3_.func_75803_c(p_i51072_2_).getLevelName()));
      this.callback = p_i51072_1_;
      this.upgrader = new WorldOptimizer(p_i51072_2_, p_i51072_3_, p_i51072_3_.func_75803_c(p_i51072_2_), p_i51072_4_);
   }

   protected void init() {
      super.init();
      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 150, 200, 20, I18n.get("gui.cancel"), (p_214331_1_) -> {
         this.upgrader.cancel();
         this.callback.accept(false);
      }));
   }

   public void tick() {
      if (this.upgrader.isFinished()) {
         this.callback.accept(true);
      }

   }

   public void removed() {
      this.upgrader.cancel();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 20, 16777215);
      int i = this.width / 2 - 150;
      int j = this.width / 2 + 150;
      int k = this.height / 4 + 100;
      int l = k + 10;
      this.drawCenteredString(this.font, this.upgrader.getStatus().func_150254_d(), this.width / 2, k - 9 - 2, 10526880);
      if (this.upgrader.getTotalChunks() > 0) {
         fill(i - 1, k - 1, j + 1, l + 1, -16777216);
         this.drawString(this.font, I18n.get("optimizeWorld.info.converted", this.upgrader.getConverted()), i, 40, 10526880);
         this.drawString(this.font, I18n.get("optimizeWorld.info.skipped", this.upgrader.getSkipped()), i, 40 + 9 + 3, 10526880);
         this.drawString(this.font, I18n.get("optimizeWorld.info.total", this.upgrader.getTotalChunks()), i, 40 + (9 + 3) * 2, 10526880);
         int i1 = 0;

         for(DimensionType dimensiontype : DimensionType.func_212681_b()) {
            int j1 = MathHelper.floor(this.upgrader.func_212543_a(dimensiontype) * (float)(j - i));
            fill(i + i1, k, i + i1 + j1, l, DIMENSION_COLORS.getInt(dimensiontype));
            i1 += j1;
         }

         int k1 = this.upgrader.getConverted() + this.upgrader.getSkipped();
         this.drawCenteredString(this.font, k1 + " / " + this.upgrader.getTotalChunks(), this.width / 2, k + 2 * 9 + 2, 10526880);
         this.drawCenteredString(this.font, MathHelper.floor(this.upgrader.getProgress() * 100.0F) + "%", this.width / 2, k + (l - k) / 2 - 9 / 2, 10526880);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }
}