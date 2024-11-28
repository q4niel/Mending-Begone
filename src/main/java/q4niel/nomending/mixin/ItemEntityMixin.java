package q4niel.nomending.mixin;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import q4niel.nomending.MendingBegone;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
    @Inject (
            method = "setStack(Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    void setStack(ItemStack stack, CallbackInfo ci) {
        if (!MendingBegone.IsMendingBook(MendingBegone.ExcludeMending(stack), stack)) return;
        ItemEntity self = (ItemEntity)(Object)this;
        self.setStack(new ItemStack(Items.BOOK, 1));
        ci.cancel();
    }
}