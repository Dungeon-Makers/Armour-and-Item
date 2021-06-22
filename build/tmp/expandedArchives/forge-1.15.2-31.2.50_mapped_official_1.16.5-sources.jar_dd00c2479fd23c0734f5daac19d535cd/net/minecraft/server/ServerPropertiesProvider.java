package net.minecraft.server;

import java.nio.file.Path;
import java.util.function.UnaryOperator;
import net.minecraft.server.dedicated.ServerProperties;

public class ServerPropertiesProvider {
   private final Path source;
   private ServerProperties properties;

   public ServerPropertiesProvider(Path p_i50718_1_) {
      this.source = p_i50718_1_;
      this.properties = ServerProperties.func_218985_a(p_i50718_1_);
   }

   public ServerProperties getProperties() {
      return this.properties;
   }

   public void forceSave() {
      this.properties.store(this.source);
   }

   public ServerPropertiesProvider update(UnaryOperator<ServerProperties> p_219033_1_) {
      (this.properties = p_219033_1_.apply(this.properties)).store(this.source);
      return this;
   }
}