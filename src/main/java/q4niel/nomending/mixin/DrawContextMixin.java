package q4niel.nomending.mixin;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipData;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import q4niel.nomending.MendingBegone;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Mixin(DrawContext.class)
public class DrawContextMixin {
    ItemStack drawItem(ItemStack stack) {
        return MendingBegone.IsMendingBook(MendingBegone.ExcludeMending(stack), stack)
                ? Items.BOOK.getDefaultStack()
                : stack
        ;
    }

    @ModifyArg (
            method = "drawItem(Lnet/minecraft/item/ItemStack;IIII)V",
            at = @At (
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
                    ordinal = 0
            ),
            index = 2
    )
    ItemStack publicDrawItem(ItemStack stack) {
        return drawItem(stack);
    }

    @ModifyArg (
            method = "drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;III)V",
            at = @At (
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/DrawContext;drawItem(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;IIII)V",
                    ordinal = 0
            ),
            index = 2
    )
    ItemStack privateDrawItem(ItemStack stack) {
        return drawItem(stack);
    }

    @Inject (
            method = "drawTooltip(Lnet/minecraft/client/font/TextRenderer;Ljava/util/List;Ljava/util/Optional;II)V",
            at = @At("HEAD")
    )
    void drawTooltip(TextRenderer textRenderer, List<Text> text, Optional<TooltipData> data, int x, int y, CallbackInfo ci) {
        ArrayList<Text> newText = new ArrayList<Text>();

        for (Text t : text) {
            if (t.getString().equals("Mending")) continue;
            newText.add(t);
        }

        if (newText.size() == 1 && newText.getFirst().getString().equals("Enchanted Book")) {
            newText.set(0, Text.literal("Book"));
        }

        text.clear();
        text.addAll(newText);
    }
}