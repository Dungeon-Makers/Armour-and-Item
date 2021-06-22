package net.minecraft.nbt;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CompoundNBT implements INBT {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Pattern SIMPLE_VALUE = Pattern.compile("[A-Za-z0-9._+-]+");
   public static final INBTType<CompoundNBT> TYPE = new INBTType<CompoundNBT>() {
      public CompoundNBT load(DataInput p_225649_1_, int p_225649_2_, NBTSizeTracker p_225649_3_) throws IOException {
         p_225649_3_.accountBits(384L);
         if (p_225649_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
         } else {
            Map<String, INBT> map = Maps.newHashMap();

            byte b0;
            while((b0 = CompoundNBT.readNamedTagType(p_225649_1_, p_225649_3_)) != 0) {
               String s = CompoundNBT.readNamedTagName(p_225649_1_, p_225649_3_);
               p_225649_3_.accountBits((long)(224 + 16 * s.length()));
               p_225649_3_.accountBits(32); //Forge: 4 extra bytes for the object allocation.
               INBT inbt = CompoundNBT.readNamedTagData(NBTTypes.getType(b0), s, p_225649_1_, p_225649_2_ + 1, p_225649_3_);
               if (map.put(s, inbt) != null) {
                  p_225649_3_.accountBits(288L);
               }
            }

            return new CompoundNBT(map);
         }
      }

      public String getName() {
         return "COMPOUND";
      }

      public String getPrettyName() {
         return "TAG_Compound";
      }
   };
   private final Map<String, INBT> tags;

   private CompoundNBT(Map<String, INBT> p_i226075_1_) {
      this.tags = p_i226075_1_;
   }

   public CompoundNBT() {
      this(Maps.newHashMap());
   }

   public void write(DataOutput p_74734_1_) throws IOException {
      for(String s : this.tags.keySet()) {
         INBT inbt = this.tags.get(s);
         writeNamedTag(s, inbt, p_74734_1_);
      }

      p_74734_1_.writeByte(0);
   }

   public Set<String> getAllKeys() {
      return this.tags.keySet();
   }

   public byte getId() {
      return 10;
   }

   public INBTType<CompoundNBT> getType() {
      return TYPE;
   }

   public int size() {
      return this.tags.size();
   }

   @Nullable
   public INBT put(String p_218657_1_, INBT p_218657_2_) {
      if (p_218657_2_ == null) throw new IllegalArgumentException("Invalid null NBT value with key " + p_218657_1_);
      return this.tags.put(p_218657_1_, p_218657_2_);
   }

   public void putByte(String p_74774_1_, byte p_74774_2_) {
      this.tags.put(p_74774_1_, ByteNBT.valueOf(p_74774_2_));
   }

   public void putShort(String p_74777_1_, short p_74777_2_) {
      this.tags.put(p_74777_1_, ShortNBT.valueOf(p_74777_2_));
   }

   public void putInt(String p_74768_1_, int p_74768_2_) {
      this.tags.put(p_74768_1_, IntNBT.valueOf(p_74768_2_));
   }

   public void putLong(String p_74772_1_, long p_74772_2_) {
      this.tags.put(p_74772_1_, LongNBT.valueOf(p_74772_2_));
   }

   public void putUUID(String p_186854_1_, UUID p_186854_2_) {
      this.putLong(p_186854_1_ + "Most", p_186854_2_.getMostSignificantBits());
      this.putLong(p_186854_1_ + "Least", p_186854_2_.getLeastSignificantBits());
   }

   public UUID getUUID(String p_186857_1_) {
      return new UUID(this.getLong(p_186857_1_ + "Most"), this.getLong(p_186857_1_ + "Least"));
   }

   public boolean hasUUID(String p_186855_1_) {
      return this.contains(p_186855_1_ + "Most", 99) && this.contains(p_186855_1_ + "Least", 99);
   }

   public void func_229681_c_(String p_229681_1_) {
      this.remove(p_229681_1_ + "Most");
      this.remove(p_229681_1_ + "Least");
   }

   public void putFloat(String p_74776_1_, float p_74776_2_) {
      this.tags.put(p_74776_1_, FloatNBT.valueOf(p_74776_2_));
   }

   public void putDouble(String p_74780_1_, double p_74780_2_) {
      this.tags.put(p_74780_1_, DoubleNBT.valueOf(p_74780_2_));
   }

   public void putString(String p_74778_1_, String p_74778_2_) {
      this.tags.put(p_74778_1_, StringNBT.valueOf(p_74778_2_));
   }

   public void putByteArray(String p_74773_1_, byte[] p_74773_2_) {
      this.tags.put(p_74773_1_, new ByteArrayNBT(p_74773_2_));
   }

   public void putIntArray(String p_74783_1_, int[] p_74783_2_) {
      this.tags.put(p_74783_1_, new IntArrayNBT(p_74783_2_));
   }

   public void putIntArray(String p_197646_1_, List<Integer> p_197646_2_) {
      this.tags.put(p_197646_1_, new IntArrayNBT(p_197646_2_));
   }

   public void putLongArray(String p_197644_1_, long[] p_197644_2_) {
      this.tags.put(p_197644_1_, new LongArrayNBT(p_197644_2_));
   }

   public void putLongArray(String p_202168_1_, List<Long> p_202168_2_) {
      this.tags.put(p_202168_1_, new LongArrayNBT(p_202168_2_));
   }

   public void putBoolean(String p_74757_1_, boolean p_74757_2_) {
      this.tags.put(p_74757_1_, ByteNBT.valueOf(p_74757_2_));
   }

   @Nullable
   public INBT get(String p_74781_1_) {
      return this.tags.get(p_74781_1_);
   }

   public byte getTagType(String p_150299_1_) {
      INBT inbt = this.tags.get(p_150299_1_);
      return inbt == null ? 0 : inbt.getId();
   }

   public boolean contains(String p_74764_1_) {
      return this.tags.containsKey(p_74764_1_);
   }

   public boolean contains(String p_150297_1_, int p_150297_2_) {
      int i = this.getTagType(p_150297_1_);
      if (i == p_150297_2_) {
         return true;
      } else if (p_150297_2_ != 99) {
         return false;
      } else {
         return i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6;
      }
   }

   public byte getByte(String p_74771_1_) {
      try {
         if (this.contains(p_74771_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74771_1_)).getAsByte();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   public short getShort(String p_74765_1_) {
      try {
         if (this.contains(p_74765_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74765_1_)).getAsShort();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   public int getInt(String p_74762_1_) {
      try {
         if (this.contains(p_74762_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74762_1_)).getAsInt();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0;
   }

   public long getLong(String p_74763_1_) {
      try {
         if (this.contains(p_74763_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74763_1_)).getAsLong();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0L;
   }

   public float getFloat(String p_74760_1_) {
      try {
         if (this.contains(p_74760_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74760_1_)).getAsFloat();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0F;
   }

   public double getDouble(String p_74769_1_) {
      try {
         if (this.contains(p_74769_1_, 99)) {
            return ((NumberNBT)this.tags.get(p_74769_1_)).getAsDouble();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return 0.0D;
   }

   public String getString(String p_74779_1_) {
      try {
         if (this.contains(p_74779_1_, 8)) {
            return this.tags.get(p_74779_1_).getAsString();
         }
      } catch (ClassCastException var3) {
         ;
      }

      return "";
   }

   public byte[] getByteArray(String p_74770_1_) {
      try {
         if (this.contains(p_74770_1_, 7)) {
            return ((ByteArrayNBT)this.tags.get(p_74770_1_)).getAsByteArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createReport(p_74770_1_, ByteArrayNBT.TYPE, classcastexception));
      }

      return new byte[0];
   }

   public int[] getIntArray(String p_74759_1_) {
      try {
         if (this.contains(p_74759_1_, 11)) {
            return ((IntArrayNBT)this.tags.get(p_74759_1_)).getAsIntArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createReport(p_74759_1_, IntArrayNBT.TYPE, classcastexception));
      }

      return new int[0];
   }

   public long[] getLongArray(String p_197645_1_) {
      try {
         if (this.contains(p_197645_1_, 12)) {
            return ((LongArrayNBT)this.tags.get(p_197645_1_)).getAsLongArray();
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createReport(p_197645_1_, LongArrayNBT.TYPE, classcastexception));
      }

      return new long[0];
   }

   public CompoundNBT getCompound(String p_74775_1_) {
      try {
         if (this.contains(p_74775_1_, 10)) {
            return (CompoundNBT)this.tags.get(p_74775_1_);
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createReport(p_74775_1_, TYPE, classcastexception));
      }

      return new CompoundNBT();
   }

   public ListNBT getList(String p_150295_1_, int p_150295_2_) {
      try {
         if (this.getTagType(p_150295_1_) == 9) {
            ListNBT listnbt = (ListNBT)this.tags.get(p_150295_1_);
            if (!listnbt.isEmpty() && listnbt.func_150303_d() != p_150295_2_) {
               return new ListNBT();
            }

            return listnbt;
         }
      } catch (ClassCastException classcastexception) {
         throw new ReportedException(this.createReport(p_150295_1_, ListNBT.TYPE, classcastexception));
      }

      return new ListNBT();
   }

   public boolean getBoolean(String p_74767_1_) {
      return this.getByte(p_74767_1_) != 0;
   }

   public void remove(String p_82580_1_) {
      this.tags.remove(p_82580_1_);
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder("{");
      Collection<String> collection = this.tags.keySet();
      if (LOGGER.isDebugEnabled()) {
         List<String> list = Lists.newArrayList(this.tags.keySet());
         Collections.sort(list);
         collection = list;
      }

      for(String s : collection) {
         if (stringbuilder.length() != 1) {
            stringbuilder.append(',');
         }

         stringbuilder.append(handleEscape(s)).append(':').append(this.tags.get(s));
      }

      return stringbuilder.append('}').toString();
   }

   public boolean isEmpty() {
      return this.tags.isEmpty();
   }

   private CrashReport createReport(String p_229677_1_, INBTType<?> p_229677_2_, ClassCastException p_229677_3_) {
      CrashReport crashreport = CrashReport.forThrowable(p_229677_3_, "Reading NBT data");
      CrashReportCategory crashreportcategory = crashreport.addCategory("Corrupt NBT tag", 1);
      crashreportcategory.setDetail("Tag type found", () -> {
         return this.tags.get(p_229677_1_).getType().getName();
      });
      crashreportcategory.setDetail("Tag type expected", p_229677_2_::getName);
      crashreportcategory.setDetail("Tag name", p_229677_1_);
      return crashreport;
   }

   public CompoundNBT copy() {
      Map<String, INBT> map = Maps.newHashMap(Maps.transformValues(this.tags, INBT::copy));
      return new CompoundNBT(map);
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else {
         return p_equals_1_ instanceof CompoundNBT && Objects.equals(this.tags, ((CompoundNBT)p_equals_1_).tags);
      }
   }

   public int hashCode() {
      return this.tags.hashCode();
   }

   private static void writeNamedTag(String p_150298_0_, INBT p_150298_1_, DataOutput p_150298_2_) throws IOException {
      p_150298_2_.writeByte(p_150298_1_.getId());
      if (p_150298_1_.getId() != 0) {
         p_150298_2_.writeUTF(p_150298_0_);
         p_150298_1_.write(p_150298_2_);
      }
   }

   private static byte readNamedTagType(DataInput p_152447_0_, NBTSizeTracker p_152447_1_) throws IOException {
      p_152447_1_.accountBits(8);
      return p_152447_0_.readByte();
   }

   private static String readNamedTagName(DataInput p_152448_0_, NBTSizeTracker p_152448_1_) throws IOException {
      return p_152448_1_.readUTF(p_152448_0_.readUTF());
   }

   private static INBT readNamedTagData(INBTType<?> p_229680_0_, String p_229680_1_, DataInput p_229680_2_, int p_229680_3_, NBTSizeTracker p_229680_4_) {
      try {
         return p_229680_0_.load(p_229680_2_, p_229680_3_, p_229680_4_);
      } catch (IOException ioexception) {
         CrashReport crashreport = CrashReport.forThrowable(ioexception, "Loading NBT data");
         CrashReportCategory crashreportcategory = crashreport.addCategory("NBT Tag");
         crashreportcategory.setDetail("Tag name", p_229680_1_);
         crashreportcategory.setDetail("Tag type", p_229680_0_.getName());
         throw new ReportedException(crashreport);
      }
   }

   public CompoundNBT merge(CompoundNBT p_197643_1_) {
      for(String s : p_197643_1_.tags.keySet()) {
         INBT inbt = p_197643_1_.tags.get(s);
         if (inbt.getId() == 10) {
            if (this.contains(s, 10)) {
               CompoundNBT compoundnbt = this.getCompound(s);
               compoundnbt.merge((CompoundNBT)inbt);
            } else {
               this.put(s, inbt.copy());
            }
         } else {
            this.put(s, inbt.copy());
         }
      }

      return this;
   }

   protected static String handleEscape(String p_193582_0_) {
      return SIMPLE_VALUE.matcher(p_193582_0_).matches() ? p_193582_0_ : StringNBT.quoteAndEscape(p_193582_0_);
   }

   protected static ITextComponent handleEscapePretty(String p_197642_0_) {
      if (SIMPLE_VALUE.matcher(p_197642_0_).matches()) {
         return (new StringTextComponent(p_197642_0_)).func_211708_a(SYNTAX_HIGHLIGHTING_KEY);
      } else {
         String s = StringNBT.quoteAndEscape(p_197642_0_);
         String s1 = s.substring(0, 1);
         ITextComponent itextcomponent = (new StringTextComponent(s.substring(1, s.length() - 1))).func_211708_a(SYNTAX_HIGHLIGHTING_KEY);
         return (new StringTextComponent(s1)).func_150257_a(itextcomponent).func_150258_a(s1);
      }
   }

   public ITextComponent getPrettyDisplay(String p_199850_1_, int p_199850_2_) {
      if (this.tags.isEmpty()) {
         return new StringTextComponent("{}");
      } else {
         ITextComponent itextcomponent = new StringTextComponent("{");
         Collection<String> collection = this.tags.keySet();
         if (LOGGER.isDebugEnabled()) {
            List<String> list = Lists.newArrayList(this.tags.keySet());
            Collections.sort(list);
            collection = list;
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.func_150258_a("\n");
         }

         ITextComponent itextcomponent1;
         for(Iterator<String> iterator = collection.iterator(); iterator.hasNext(); itextcomponent.func_150257_a(itextcomponent1)) {
            String s = iterator.next();
            itextcomponent1 = (new StringTextComponent(Strings.repeat(p_199850_1_, p_199850_2_ + 1))).func_150257_a(handleEscapePretty(s)).func_150258_a(String.valueOf(':')).func_150258_a(" ").func_150257_a(this.tags.get(s).getPrettyDisplay(p_199850_1_, p_199850_2_ + 1));
            if (iterator.hasNext()) {
               itextcomponent1.func_150258_a(String.valueOf(',')).func_150258_a(p_199850_1_.isEmpty() ? " " : "\n");
            }
         }

         if (!p_199850_1_.isEmpty()) {
            itextcomponent.func_150258_a("\n").func_150258_a(Strings.repeat(p_199850_1_, p_199850_2_));
         }

         itextcomponent.func_150258_a("}");
         return itextcomponent;
      }
   }
}
