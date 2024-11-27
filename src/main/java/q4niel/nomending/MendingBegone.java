package q4niel.nomending;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.ModInitializer;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class MendingBegone implements ModInitializer {
	public static final String MOD_ID = "nomending";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {}

	public static <T> void ScreenHandlerQuickMoveInjection(T handler, int index) {
		Slot slot = ((ScreenHandler)(Object)handler).slots.get(index);
		ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants = MendingBegone.ExcludeMending(slot.getStack());

		if (!MendingBegone.IsMendingBook(enchants, slot.getStack())) {
			MendingBegone.ApplyEnchants(slot.getStack(), enchants);
			return;
		}

		slot.setStack(new ItemStack(Items.BOOK, 1));
	}

	public static ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> ExcludeMending(ItemStack stack) {
		ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants = new ArrayList<>();

		for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : EnchantmentHelper.getEnchantments(stack).getEnchantmentEntries()) {
			if (entry.getKey().getKey().get().getValue().toString().equals("minecraft:mending")) continue;
			enchants.add(new Tuple(entry.getKey(), entry.getIntValue()));
		}

		return enchants;
	}

	public static boolean IsMendingBook(ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants, ItemStack stack) {
		return enchants.isEmpty() && stack.getItem() == Items.ENCHANTED_BOOK;
	}

	public static void ApplyEnchants(ItemStack stack, ArrayList<Tuple<RegistryEntry<Enchantment>, Integer>> enchants) {
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