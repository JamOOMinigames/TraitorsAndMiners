package mc.Mitchellbrine.traitorsAndMiners;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

/**
 * Created by Mitchellbrine on 2014.
 */
public class ItemStackHelper {

    private static ArrayList<String> funnyNames = new ArrayList<>();

    public static void init() {
        funnyNames.add("Oswolt");
        funnyNames.add("Loppy");
        funnyNames.add("Poppy's Knockback Stick of Justice");
        funnyNames.add("Thy Valiant Kroostyl Stick");
        funnyNames.add("java.lang.NullNameException");
        funnyNames.add("*insert name here*");
    }

    public static void setItemName(ItemStack itemStack) {
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(funnyNames.get(TraitorsAndMiners.instance.random.nextInt(funnyNames.size())));
        itemStack.setItemMeta(meta);
    }

    public static void setItemName(ItemStack stack, String itemName) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(itemName);
        stack.setItemMeta(meta);
    }

    public static void setInRandomSlot(Inventory inventory, ItemStack stack) {
            inventory.setItem(TraitorsAndMiners.instance.random.nextInt(inventory.getSize()),stack);
    }

    public static int getSlotOfMaterial(Inventory inventory, Material material) {
        for (int i = 0; i < inventory.getSize();i++) {
            if (inventory.getItem(i) != null && inventory.getItem(i).getType() == material) {
                return i;
            }
        }
        return -1;
    }

}
