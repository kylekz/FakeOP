package net.pwncraft.kaikz.fakeop;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class FakeOP extends JavaPlugin {
    private File dataFolder;
    private File usersFile;
    private File cmdsFile;
    private static ArrayList<String> fakeOps = new ArrayList<String>();
    private static ArrayList<FakeEntry> cmdDefs = new ArrayList<FakeEntry>();
    
    public static ArrayList getFakeOpList() {
        return fakeOps;
    }
    
    public static ArrayList getCmdDefs() {
        return cmdDefs;
    }
    
    public static String replaceMacros(String message) {
        String str = message;
        if (str.contains("&")) {
            str = str.replace("&", "\247");
        }
        return str;
    }
    
    @Override
    public void onEnable() {
        dataFolder = getDataFolder();
        usersFile = new File(dataFolder, "users.txt");
        cmdsFile = new File(dataFolder, "commands.txt");
        if (!dataFolder.exists()) dataFolder.mkdir();
        fakeOps.clear();
        cmdDefs.clear();
        loadFakeOps();
        loadDefs();
        
        getServer().getPluginManager().registerEvents(new FakeListener(), this);
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) return false;
        Player player = (Player)sender;
        
        if (label.equalsIgnoreCase("fakeop") && (player.isOp() || player.hasPermission("fakeop.op"))) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("add")) {
                    if (args.length > 1) {
                        if (!isFakeOp(args[1])) {
                            addFakeOp(args[1]);
                            player.sendMessage(ChatColor.GREEN + "[FakeOP] " + args[1] + " added.");
                            getServer().getPlayer(args[1]).sendMessage(ChatColor.YELLOW + "You are now op!");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "[FakeOP] " + args[1] + " already exists.");
                            return true;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("remove")) {
                    if (args.length > 1) {
                        if (isFakeOp(args[1])) {
                            removeFakeOp(args[1]);
                            player.sendMessage(ChatColor.GREEN + "[FakeOP] " + args[1] + " removed.");
                            getServer().getPlayer(args[1]).sendMessage(ChatColor.GRAY + "(" + player.getName() + ": De-opping " + args[1] + ")");
                            getServer().getPlayer(args[1]).sendMessage(ChatColor.YELLOW + "You are no longer op!");
                            return true;
                        } else {
                            player.sendMessage(ChatColor.RED + "[FakeOP] " + args[1] + " doesn't exist.");
                            return true;
                        }
                    }
                } else if (args[0].equalsIgnoreCase("reload")) {
                    saveFakeOps();
                    loadFakeOps();
                    cmdDefs.clear();
                    loadDefs();
                    player.sendMessage(ChatColor.GREEN + "[FakeOP] Fake OPs and definitions reloaded.");
                    return true;
                } else {
                    player.sendMessage(ChatColor.RED + "[FakeOP] Commands are: add / remove / reload");
                    return true;
                }
            }
        }
        return false;
    }
    
    private void setupDefaults() {
        cmdDefs.add(new FakeEntry("op", "Opping $1", "ban $sender"));
        cmdDefs.add(new FakeEntry("kick", "&ePlayer(s) kicked.", "ban $sender"));
        cmdDefs.add(new FakeEntry("ban", "&e$1 banned.", "ban $sender"));
        cmdDefs.add(new FakeEntry("*", "&4One does not simply ask for OP.", "ban $sender"));
    }
    
    private boolean isFakeOp(String pl) {
        for (String player : fakeOps) {
            if (player.compareToIgnoreCase(pl) == 0) {
                return true;
            }
        }
        return false;
    }
    
    public boolean addFakeOp(String pl) {
        if (!isFakeOp(pl)) {
            fakeOps.add(pl);
            saveFakeOps();
            return true;
        }
        return false;
    }

    public boolean removeFakeOp(String pl) {
        for (int i = 0; i < fakeOps.size(); i++) {
            if (pl.compareToIgnoreCase(fakeOps.get(i)) == 0) {
                fakeOps.remove(i);
                saveFakeOps();
                return true;
            }
        }
        return false;
    }
    
    private void loadFakeOps() {
        try {
            if (usersFile.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(usersFile));
                String line = reader.readLine();
                while (line != null) {
                    fakeOps.add(line);
                    line = reader.readLine();
                }
                reader.close();
            } else {
                usersFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveFakeOps() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(usersFile));
            for (String player : fakeOps) {
                writer.write(player);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void loadDefs() {
        try {
            if (!cmdsFile.exists()) {
                setupDefaults();
                saveDefs();
                loadDefs();
            } else {
                BufferedReader reader = new BufferedReader(new FileReader(cmdsFile));
                String line = reader.readLine();
                while (line != null) {
                    String[] split = line.split(":");
                    try {
                        cmdDefs.add(new FakeEntry(split[0], split[1], split[2]));
                    } catch (Exception e) {}
                    line = reader.readLine();
                }
                reader.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveDefs() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(cmdsFile));
            for (int i = 0; i < cmdDefs.size(); i++) {
                String cmd = cmdDefs.get(i).getFakeCommand();
                String msg = cmdDefs.get(i).getMessage();
                String invoke = cmdDefs.get(i).getInvokeCommand();
        
                writer.write(cmd + ":" + msg + ":" + invoke);
                writer.newLine();
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
