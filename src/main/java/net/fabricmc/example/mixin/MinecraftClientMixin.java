package net.fabricmc.example.mixin;

import net.fabricmc.example.client.ExampleModClient;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.RunArgs;
import net.minecraft.client.font.FontManager;
import net.minecraft.client.font.FontStorage;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Shadow @Final private FontManager fontManager;

    @Inject(method="<init>",at=@At("RETURN"))
    private void inject(RunArgs args, CallbackInfo ci){
        Map<Identifier, FontStorage> fontStorages = ((FontManagerAccessor)this.fontManager).getFontStorages();
        ExampleModClient.LOGGER.info(String.valueOf(fontStorages.size()));
    }
}
