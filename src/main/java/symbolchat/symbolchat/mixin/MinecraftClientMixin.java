package symbolchat.symbolchat.mixin;


import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import symbolchat.symbolchat.Config;
import symbolchat.symbolchat.SymbolStorage;

import java.util.concurrent.CompletableFuture;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {
    @Inject(method = "reloadResources()Ljava/util/concurrent/CompletableFuture;",at = @At(value = "HEAD"))
    public void reloadCustomSymbols(CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        SymbolStorage.tryLoadCustomList();
        Config.loadConfig();
    }
}
