package me.Jojo.Splun;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.util.com.google.common.io.ByteArrayDataOutput;
import net.minecraft.util.com.google.common.io.ByteStreams;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	// Events Register
	public Events ev = new Events(this);
	public Methodes mth = new Methodes(this);
	
	public boolean isOn;  
	public boolean isTimer;  
	public boolean canJoin;
    public ConcurrentHashMap<String, Integer> times = new ConcurrentHashMap<String, Integer>();
	public ArrayList<Player> isliving = new ArrayList<Player>();
	public ArrayList<Player> Schutz = new ArrayList<Player>();
	public HashMap<Integer,Player> Plätze = new HashMap<Integer,Player>();
	public int Items;
	
	Mysql MySQL = new Mysql(this, getConfig().get("Mysql.Host") + "", "3306", getConfig().get("Mysql.Database") + "",getConfig().get("Mysql.User") + "",getConfig().get("Mysql.Password") + "");
	Connection c = null;

	// Chat
	public String prefix = ChatColor.WHITE + "[" + ChatColor.GOLD + "Splun" + ChatColor.WHITE + "] ";
	
	// Optionen
	
	public Material[] ITEMS =  {
	  Material.FIREWORK, Material.GOLD_BOOTS, Material.FEATHER, Material.DIAMOND_SWORD, Material.TNT
	  
	};
	 
	public String world = "world"; 
	public String lobbyserver = "Hub"; 
	public int canStart = 2; 
	public int maxPlayers = 20;
	public int starttime = 30;
	public int maxItems = 4;
	
	// Tasks
	int end;
	int start;
	
	public void onEnable()
	{
	   getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
	   getServer().getPluginManager().registerEvents(this.ev, this);
	   
	   this.getConfig().options().copyDefaults(true);
	   if(!this.getConfig().contains("Mysql")) {
		   this.getConfig().addDefault("Mysql.Host", "localhost");
		   this.getConfig().addDefault("Mysql.User", "User");
		   this.getConfig().addDefault("Mysql.Database", "Database");
		   this.getConfig().addDefault("Mysql.Password", "Password");
	   }
	   this.saveConfig();
	   
	   c = MySQL.openConnection();
	   
	   Items = 0;
	   isOn = false;
	   canJoin = true;
	   isTimer = false;  
	   
       Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
        @SuppressWarnings("deprecation")
		public void run() {
        	   if (isOn) {
               for (Player player : Bukkit.getOnlinePlayers()) {
                   int time = times.containsKey(player.getName()) ? times.get(player.getName()) : 0;
                   times.put(player.getName(), time+1);
                   
                   if ((times.get(player.getName()) >= 3) && (isliving.contains(player)) && (!Schutz.contains(player))){
                	  player.setHealth(0);
                	  player.sendMessage(prefix  + ChatColor.WHITE + "Du kannst nicht Afk rum stehen");
                	  times.put(player.getName(), 0);
                   }
                 }
              }
           }
       }, (long)0, (long)20);
	}
	  
	public void onDisable()
	{

	}
	
	public void connectTo(Player p,String name){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(name);
        p.sendPluginMessage(this, "BungeeCord", out.toByteArray());
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	{
	    Player player = (Player)sender;
	    if (label.equalsIgnoreCase("Splun")) {
	      if (args.length == 0) {
	        player.sendMessage(ChatColor.DARK_GRAY + "-----" + ChatColor.GOLD + "Splun" + ChatColor.DARK_GRAY + "-------");
	        player.sendMessage(ChatColor.DARK_GRAY + "Plugin von" + ChatColor.DARK_RED + " Jojo191098");
	      }
	      if ((args.length == 1) && (sender.hasPermission("Splun.Admin"))) {
	        if (args[0].equalsIgnoreCase("setLobby")) {
	          this.mth.setLobby(player.getLocation());
	          player.sendMessage(prefix + ChatColor.WHITE + "Lobby gesetzt");
	        }
	        if (args[0].equalsIgnoreCase("setSpectate")) {
		      this.mth.setSpec(player.getLocation());
		      player.sendMessage(prefix + ChatColor.WHITE + "Spectatepoint gesetzt");
		    }
	        if (args[0].equalsIgnoreCase("addSpawn")) {
		      this.mth.addSpawn(player.getLocation());
		      player.sendMessage(prefix + ChatColor.WHITE + "Spawnpoint hinzugefügt gesetzt");
		    }
	        if (args[0].equalsIgnoreCase("addItemSpawn")) {
		      this.mth.addItemSpawn(player.getLocation());
		      player.sendMessage(prefix + ChatColor.WHITE + "Itemspawnpoint hinzugefügt gesetzt");
		    }
	      }
	    }
	    if (label.equalsIgnoreCase("Hub")) {
		  if (args.length == 0) {
		      player.sendMessage(prefix + ChatColor.WHITE + "Zurueck zum Hub....");
		      this.connectTo(player, this.lobbyserver);
		  }
		}
       return true;
	}
}
