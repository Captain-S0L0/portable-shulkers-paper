package xyz.terriblefriends.portableshulkers;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.PlayerInventory;

public class Listeners implements Listener {

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        // handle opening
        if (event.getAction() == Action.RIGHT_CLICK_AIR & event.hasItem() && MaterialSetTag.SHULKER_BOXES.isTagged(event.getItem().getType())) {

            if (!PortableShulkers.INSTANCE.canOpenGUI(event.getPlayer())) {
                event.getPlayer().sendMessage("§4[§6PortableShulkers§4] §cError! You can't do that right now!");
                return;
            }

            // close the current inventory in case it's another shulker inventory (so the event item updates properly)
            event.getPlayer().closeInventory();

            event.getPlayer().openInventory(new PortableShulkerInventoryHolder(event.getItem(), event.getHand() == EquipmentSlot.OFF_HAND).getInventory());
            event.setCancelled(true);
            return;
        }

        // prevent placing into blocks while GUI is open (decorated pot)
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder holder) {
            if (
                    (!holder.isOffhand() && event.getHand() == EquipmentSlot.OFF_HAND)
                    || (holder.isOffhand() && event.getHand() == EquipmentSlot.HAND)
            ) {
                return;
            }

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onInventoryCloseEvent(InventoryCloseEvent event) {
        if (!(event.getInventory().getHolder(false) instanceof PortableShulkerInventoryHolder holder)) {
            return;
        }

        holder.onClose();
    }

    @EventHandler
    public void onInventoryClickEvent(InventoryClickEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getView().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder holder)) {
            return;
        }

        // prevent moving the shulker box in or out of the inventory and causing item loss when the GUI closes
        if (event.getClickedInventory() instanceof PlayerInventory && event.getSlot() == event.getView().getPlayer().getInventory().getHeldItemSlot()) {
            event.setCancelled(true);
        }

        if (!holder.isOffhand() && event.getClick() == ClickType.NUMBER_KEY && event.getHotbarButton() == event.getView().getPlayer().getInventory().getHeldItemSlot()) {
            event.setCancelled(true);
        }

        if (holder.isOffhand() && event.getClick() != ClickType.SWAP_OFFHAND) {
            event.setCancelled(true);
        }
    }

    // below are all events captured to prevent moving the shulker box in or out of the inventory and causing item loss when the GUI closes
    @EventHandler
    public void onPlayerSwapHandItemsEvent(PlayerSwapHandItemsEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlaceEvent(BlockPlaceEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (!(event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntiyEvent(PlayerInteractEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder holder) {
            if (
                    (!holder.isOffhand() && event.getHand() == EquipmentSlot.OFF_HAND)
                            || (holder.isOffhand() && event.getHand() == EquipmentSlot.HAND)
            ) {
                return;
            }

            event.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        if (event.isCancelled()) {
            return;
        }

        if (event.getPlayer().getOpenInventory().getTopInventory().getHolder(false) instanceof PortableShulkerInventoryHolder holder) {
            if (
                    (!holder.isOffhand() && event.getHand() == EquipmentSlot.OFF_HAND)
                            || (holder.isOffhand() && event.getHand() == EquipmentSlot.HAND)
            ) {
                return;
            }

            event.setCancelled(true);
            return;
        }
    }
}
