package me.Fupery.HeadShop;
import me.Fupery.HeadShop.Menu.InventoryMenu.InventoryMenu;
import me.Fupery.HeadShop.Menu.InventoryMenu.MenuButton;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;

public class MenuListener implements Listener {

    private HeadShop plugin;

    public MenuListener(HeadShop plugin) {
        this.plugin = plugin;
    }

    private static void handleClick(InventoryClickEvent event) {
        Inventory top = event.getWhoClicked().getOpenInventory().getTopInventory();
        Inventory bottom = event.getWhoClicked().getOpenInventory().getBottomInventory();

        if (event.getClickedInventory() == top) {
            event.setResult(Event.Result.DENY);
            event.setCancelled(true);

        } else if (event.getClickedInventory() == bottom) {

            switch (event.getAction()) {
                case MOVE_TO_OTHER_INVENTORY:
                case HOTBAR_MOVE_AND_READD:
                case COLLECT_TO_CURSOR:
                case UNKNOWN:
                    event.setResult(Event.Result.DENY);
                    event.setCancelled(true);
                    return;
                default:
                    break;
            }
        }
    }

    @EventHandler
    void onMenuInteract(final InventoryClickEvent event) {
        Inventory inventory = event.getWhoClicked().getOpenInventory().getTopInventory();

        if (inventory == null || inventory.getTitle() == null
                || !inventory.getTitle().contains(HeadShop.prefix)) {
            return;
        }

        handleClick(event);

        if (event.getClickedInventory() != inventory) {
            return;
        }
        InventoryMenu menu = InventoryMenu.openMenus.get(((Player) event.getWhoClicked()));

        final MenuButton button = menu.getButton(event.getSlot());

        if (button != null) {

            Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable() {
                @Override
                public void run() {
                    button.onClick(plugin, ((Player) event.getWhoClicked()));
                }
            });
        }
    }

    @EventHandler
    void onItemDrag(InventoryDragEvent event) {
        Inventory inventory = event.getWhoClicked().getOpenInventory().getTopInventory();

        if (inventory == null || inventory.getTitle() == null
                || !inventory.getTitle().contains(HeadShop.prefix)) {
            return;
        }
        event.setResult(Event.Result.DENY);
        event.setCancelled(true);
    }

    @EventHandler
    void onMenuClose(InventoryCloseEvent event) {
        Player player = ((Player) event.getPlayer());

        if (player != null && InventoryMenu.openMenus.containsKey(player)) {
            InventoryMenu.openMenus.get(player).close(player);
        }
    }

    public void closeMenus() {
        for (Player player : InventoryMenu.openMenus.keySet()) {
            InventoryMenu.openMenus.get(player).close(player);
        }
    }
}
