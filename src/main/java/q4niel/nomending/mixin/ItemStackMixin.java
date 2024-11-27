package q4niel.nomending.mixin;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import q4niel.nomending.Tuple;
import java.util.ArrayList;

@Mixin(ItemStack.class)
public class ItemStackMixin {
    final String MENDING = "minecraft:mending";

    @Inject (
            method = "onClicked(Lnet/minecraft/item/ItemStack;Lnet/minecraft/screen/slot/Slot;Lnet/minecrft/util/ClickType;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/inventory/StackReference;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    void onClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> slotEnchants = excludeMending(slot.getStack());

        if (!isMendingBook(slotEnchants, slot.getStack())) {
            applyEnchants(slot.getStack(), slotEnchants);
            return;
        }

        if (stack.getItem() != Items.BOOK) {
            slot.setStack(new ItemStack(stack.getItem(), stack.getCount()));
            cursorStackReference.set(new ItemStack(Items.BOOK, 1));
            cir.setReturnValue(true); return;
        }

        switch (clickType) {
            case ClickType.LEFT -> {
                if (stack.getCount() == stack.getMaxCount()) {
                    slot.setStack(new ItemStack(Items.BOOK, Items.BOOK.getMaxCount()));
                    cursorStackReference.set(new ItemStack(Items.BOOK, 1));
                    cir.setReturnValue(true); return;
                }

                slot.setStack(new ItemStack(Items.BOOK, 1 + stack.getCount()));
                cursorStackReference.set(new ItemStack(Items.AIR));
                cir.setReturnValue(true); return;
            }

            case ClickType.RIGHT -> {
                slot.setStack(new ItemStack(Items.BOOK, 2));
                cursorStackReference.get().decrement(1);
                cir.setReturnValue(true); return;
            }
        }
    }

    ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> excludeMending(ItemStack stack) {
        ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants = new ArrayList<>();

        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries()) {
            if (entry.getKey().getKey().get().getValue().toString().equals(MENDING)) continue;
            enchants.add(new Tuple(entry.getKey(), entry.getIntValue()));
        }

        return enchants;
    }

    boolean isMendingBook(ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants, ItemStack stack) {
        return enchants.isEmpty() && stack.getItem() == Items.ENCHANTED_BOOK;
    }

    void applyEnchants(ItemStack stack, ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants) {
        EnchantmentHelper.apply (
                stack,
                components -> { components.remove(enchantment -> true); }
        );

        EnchantmentHelper.apply (
                stack,
                components -> {
                    for (Tuple<RegistryEntry<Enchantment>, Integer> t : enchants) {
                        components.add(t.First, t.Second);
                    }
                }
        );
    }
}