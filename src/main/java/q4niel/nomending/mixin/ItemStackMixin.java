package q4niel.nomending.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import q4niel.nomending.MendingBegone;
import q4niel.nomending.Tuple;
import java.util.ArrayList;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    final String MENDING = "minecraft:mending";

    /**@author q4niel @reason imagine commenting */@Overwrite()
    public boolean onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference) {
        ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> slotEnchants = MendingBegone.ExcludeMending(slot.getStack());

        if (!MendingBegone.IsMendingBook(slotEnchants, slot.getStack())) {
            MendingBegone.ApplyEnchants(slot.getStack(), slotEnchants);
            return false;
        }

        if (stack.getItem() != Items.BOOK) {
            slot.setStack(new ItemStack(stack.getItem(), stack.getCount()));
            cursorStackReference.set(new ItemStack(Items.BOOK, 1));
            return true;
        }

        switch (clickType) {
            case ClickType.LEFT -> {
                if (stack.getCount() == stack.getMaxCount()) {
                    slot.setStack(new ItemStack(Items.BOOK, Items.BOOK.getMaxCount()));
                    cursorStackReference.set(new ItemStack(Items.BOOK, 1));
                    return true;
                }

                slot.setStack(new ItemStack(Items.BOOK, 1 + stack.getCount()));
                cursorStackReference.set(new ItemStack(Items.AIR));
                return true;
            }

            case ClickType.RIGHT -> {
                slot.setStack(new ItemStack(Items.BOOK, 2));
                cursorStackReference.get().decrement(1);
                return true;
            }
        }

        return false;
    }

    @Inject (
            method = "hasGlint()Z",
            at = @At("HEAD"),
            cancellable = true
    )
    void hasGlint(CallbackInfoReturnable<Boolean> cir) {
        ItemStack self = (ItemStack)(Object)this;
        if (MendingBegone.IsMendingBook(MendingBegone.ExcludeMending(self), self)) {
            cir.setReturnValue(Items.BOOK.getDefaultStack().hasGlint());
        }
    }

    @Inject (
            method = "getRarity()Lnet/minecraft/util/Rarity;",
            at = @At("HEAD"),
            cancellable = true
    )
    void getRarity(CallbackInfoReturnable<Rarity> cir) {
        ItemStack self = (ItemStack)(Object)this;
        if (MendingBegone.IsMendingBook(MendingBegone.ExcludeMending(self), self)) {
            cir.setReturnValue(Items.BOOK.getDefaultStack().getRarity());
        }
    }
}