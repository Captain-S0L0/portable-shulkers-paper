package xyz.terriblefriends.portableshulkers;

import com.destroystokyo.paper.MaterialSetTag;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class PortableShulkers extends JavaPlugin {

    public static PortableShulkers INSTANCE;
    private boolean closing = false;

    private final List<PortableShulkerInventoryHolder> openHolders = new ArrayList<>();

    @Override
    public void onEnable() {
        INSTANCE = this;
        getServer().getPluginManager().registerEvents(new Listeners(), this);

        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, (event) -> {
            final Commands commands = event.registrar();

            commands.register(
                    Commands.literal("openshulker")
                            .executes(ctx -> {
                                if (!(ctx.getSource().getExecutor() instanceof Player player)) {
                                    ctx.getSource().getSender().sendMessage("§4[§6PortableShulkers§4] §cError! You're not a player!");
                                    return 0;
                                }

                                ItemStack main = player.getInventory().getItemInMainHand();
                                ItemStack off = player.getInventory().getItemInOffHand();
                                boolean offhand = false;
                                ItemStack shulker = null;

                                if (MaterialSetTag.SHULKER_BOXES.isTagged(main.getType())) {
                                    shulker = main;
                                }
                                else if (MaterialSetTag.SHULKER_BOXES.isTagged(off.getType())) {
                                    shulker = off;
                                    offhand = true;
                                }

                                if (shulker == null) {
                                    ctx.getSource().getSender().sendMessage("§4[§6PortableShulkers§4] §cError! You're not holding a shulker!");
                                    return 0;
                                }

                                // close the current inventory in case it's another shulker inventory (so the event item updates properly)
                                player.closeInventory();

                                player.openInventory(new PortableShulkerInventoryHolder(shulker, offhand).getInventory());

                                return 1;
                            }).build(),
                    "Open the shulker box in your hand (if you are holding one)",
                    List.of()
            );
        });

    }

    @Override
    public void onDisable() {
        this.closing = true;
        for (PortableShulkerInventoryHolder holder : this.openHolders) {
            holder.onClose();
        }
        this.closing = false;
    }

    public void addHolder(PortableShulkerInventoryHolder holder) {
        this.openHolders.add(holder);
    }

    public void removeHolder(PortableShulkerInventoryHolder holder) {
        if (!this.closing) {
            this.openHolders.remove(holder);
        }
    }
}
