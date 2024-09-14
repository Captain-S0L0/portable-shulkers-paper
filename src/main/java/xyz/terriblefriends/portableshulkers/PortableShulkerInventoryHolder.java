package xyz.terriblefriends.portableshulkers;

import com.destroystokyo.paper.MaterialSetTag;
import org.bukkit.Bukkit;
import org.bukkit.block.ShulkerBox;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.jetbrains.annotations.NotNull;

public class PortableShulkerInventoryHolder implements InventoryHolder {

    private final Inventory shulkerInventory;
    private final ItemStack shulker;

    private final boolean offhand;

    public PortableShulkerInventoryHolder(ItemStack shulker, boolean offhand) {
        if (!MaterialSetTag.SHULKER_BOXES.isTagged(shulker.getType())) {
            throw new IllegalArgumentException("Not a shulker box!");
        }

        this.shulkerInventory = Bukkit.createInventory(this, InventoryType.SHULKER_BOX, shulker.displayName());
        this.shulker = shulker;
        this.offhand = offhand;
        PortableShulkers.INSTANCE.addHolder(this);

        if (
                this.shulker.getItemMeta() instanceof BlockStateMeta blockStateMeta
                        && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBoxState
        ) {
            for (int i = 0; i < shulkerBoxState.getInventory().getSize(); i++) {
                this.shulkerInventory.setItem(i, shulkerBoxState.getInventory().getItem(i));
            }
            // clear the inventory of the item so even if someone is cheeky, all they do is delete their items >:)
            shulkerBoxState.getInventory().clear();

            blockStateMeta.setBlockState(shulkerBoxState);
            this.shulker.setItemMeta(blockStateMeta);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return shulkerInventory;
    }

    public void onClose() {
        PortableShulkers.INSTANCE.removeHolder(this);

        if (
                this.shulker.getItemMeta() instanceof BlockStateMeta blockStateMeta
                && blockStateMeta.getBlockState() instanceof ShulkerBox shulkerBoxState
        ) {
            // make sure to put the inventory back!
            for (int i = 0; i < this.shulkerInventory.getSize(); i++) {
                shulkerBoxState.getInventory().setItem(i, this.shulkerInventory.getItem(i));
            }
            this.shulkerInventory.clear();

            blockStateMeta.setBlockState(shulkerBoxState);
            this.shulker.setItemMeta(blockStateMeta);
        }
    }

    public boolean isOffhand() {
        return this.offhand;
    }
}
