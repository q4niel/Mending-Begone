package q4niel.nomending.mixin.screen_handler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.HopperScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import q4niel.nomending.MendingBegone;

@Mixin(HopperScreenHandler.class)
public class HopperScreenHandlerMixin {
    @Inject(
            method = "quickMove(Lnet/minecraft/entity/player/PlayerEntity;I)Lnet/minecraft/item/ItemStack;",
            at = @At("HEAD")
    )
    void quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        MendingBegone.ScreenHandlerQuickMoveInjection(this, slot);
    }
}