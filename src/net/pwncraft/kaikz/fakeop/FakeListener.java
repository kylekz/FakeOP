package net.pwncraft.kaikz.fakeop;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class FakeListener implements Listener {
    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        String[] split = event.getMessage().split(" ");
        String cmdName = split[0].substring(1);
        
        if (FakeOP.getFakeOpList().contains(player.getName())) {
            String message = "";
            for (int i = 0; i < FakeOP.getCmdDefs().size(); i++) {
                FakeEntry entry = (FakeEntry)FakeOP.getCmdDefs().get(i);
                String cmd = entry.getFakeCommand();
                
                if (cmd.equalsIgnoreCase(cmdName)) {
                    try {
                        String msg = entry.getMessage();
                        String run = entry.getInvokeCommand();
                    
                        // Args replacement
                        for (int j = 0; j < split.length; j++) {
                            if (message.contains("$"+j)) {
                                message = message.replace("$"+j, split[j]);
                            }
                        }
                        player.sendMessage(FakeOP.replaceMacros(message));
                        if (run != null || !run.equals("")) {
                            run = run.replace("$sender", player.getName());
                            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), run);
                        }
                        event.setCancelled(true);
                        break;
                    } catch (Exception e) {}
                } else {
                    continue;
                    /*
                     * for (int k = 0; k < FakeOP.getCmdDefs().size(); k++) {
                        FakeEntry entry2 = (FakeEntry)FakeOP.getCmdDefs().get(k);
                        String cmd2 = entry2.getFakeCommand();
                    
                        if (cmd2.equalsIgnoreCase("*")) {
                            if (entry2.getMessage() == null || entry2.getMessage().equals("")) {
                                player.sendMessage(FakeOP.replaceMacros("&4One does not simply ask for OP."));
                            } else {
                                player.sendMessage(FakeOP.replaceMacros(entry2.getMessage()));
                            }
                            event.setCancelled(true);
                            break;
                        } else {
                            continue;
                        }
                    }
                     */
                }
            }
        }
    }
}
