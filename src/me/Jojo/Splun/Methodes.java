package me.Jojo.Splun;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class Methodes {
	
	Main m;
	
	public Methodes(Main m){
		this.m = m;
	}
	
	@SuppressWarnings("deprecation")
	public void BlockLaunch(final Block b) {
		
     new BukkitRunnable()
     {
       FallingBlock fb;
       int level = 0;
        public void run()
        {
            if (level == 0){
    		    b.setData((byte) 4);
    		} else if (level == 1){
      			b.setData((byte) 14);
      		} else if (level == 2){
        		b.setData((byte) 15);
        	} else if (level == 3){
        	    fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
        	        
        	    b.setType(Material.AIR);
        	        
        	    float x = (float) -0.3 + (float) (Math.random() * ((0.3 - -0.3) + 1));
        	    float y = 1.2F;
        	    float z = (float) -0.3 + (float)(Math.random() * ((0.3 - -0.3) + 1));
        	        
        	    fb.setVelocity(new Vector(x, y, z));
        	 } else if (level == 4){
        		fb.remove();
        		cancel();
        	 }
    		  level++;
		  }
	  }.runTaskTimer(m, 0L, 5L);
    } 
	
    public void poweruptime (final Player p, int ticks)
    {
        p.setExp(1F);

        final float division = p.getExp() / ticks;
       
        new BukkitRunnable()
        {
            @SuppressWarnings("deprecation")
			public void run()
            {
                float currentLevel = p.getExp();
                p.setExp(currentLevel - division);
                currentLevel = p.getExp();

                if (currentLevel <= 0) {
                    cancel();
                    m.Items--;
                    
                    if (m.Schutz.contains(p)) {
                       m.Schutz.remove(p);
                    }
                    	
                    p.getInventory().clear();
                    p.updateInventory();
                    p.sendMessage(m.prefix + ChatColor.WHITE + "Dein Powerup ist wieder weg");	
                  } else {
                }
            }
        }.runTaskTimer(m, 0L, 1L);
    }
    
    public void stats(Player player, String stat, int add, String Tabel)
    {
		   try {
			 Statement statement = this.m.c.createStatement();
		     ResultSet res = statement.executeQuery("SELECT * FROM " + Tabel + " WHERE PlayerName = '" + player.getName() + "';");
		     res.next();
		     
		     int wertneu = res.getInt(stat) + add;
			 
			 Statement statement2 = this.m.c.createStatement();
			 statement2.executeUpdate("UPDATE  "+ Tabel +"  SET " + stat + " ='" + wertneu + "' WHERE PlayerName ='" + player.getName() + "';");
		   } catch (SQLException e) {
		     e.printStackTrace();
		   }
    }
    
	public void startTimer()
	{
	  this.m.isTimer = true;
	  this.m.start = Bukkit.getScheduler().scheduleSyncRepeatingTask(this.m, new Runnable()
	   {
	     int counter = m.starttime;
	      @SuppressWarnings("deprecation")
		  public void run()
	      {
	        for (Player player : Bukkit.getOnlinePlayers()) {
	          if (player != null) {
	            player.setLevel(counter);
	            player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1.0F, 0.0F);
	          }
	        }

	        if ((counter == 30) && (counter == 20) && (counter == 15)) {
	          Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "Das Spiel startet in " + ChatColor.GOLD + counter + ChatColor.WHITE + " Sekunden!");
	        }

	        if (counter <= 10) {
	        	Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "Das Spiel startet in " + ChatColor.GOLD + counter + ChatColor.WHITE + " Sekunden!");
	        }

	        if (counter > 0) {
	          counter -= 1;
	        } else if (counter == 0){
	          Bukkit.getScheduler().cancelTask(m.start);
	          m.isTimer = false;
	          m.isOn = true;
	          m.canJoin = false;
	          spawnItem(5);
	          Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "Das Spiel ist Gestartet");
	        	
	          for (Player player : Bukkit.getOnlinePlayers()) {
	            if ((player != null) && (m.isliving.contains(player)))
	            {
	            	player.setExp(0.0F);
	            	player.getInventory().clear();
	            	
	            	ItemStack feather = new ItemStack(Material.FEATHER, 1);
	            	ItemMeta ft = feather.getItemMeta();
	            	ft.setDisplayName(ChatColor.GREEN + "3 Skunden Spawn Schutz");
	            	feather.setItemMeta(ft);
	            	player.getInventory().addItem(feather);
	            	
	            	player.updateInventory();
	                
	            	m.Schutz.add(player);
	            	poweruptime(player, 60);
	            	
	                m.mth.tpRandomSpawn(player);
	                m.mth.stats(player, "Games", 1, "Splun");
	            }
	            else Bukkit.shutdown();
	          }
	        }
	      }
	    }
	    , 0L, 20L);
	}
	
	public void StopServer(){
		 Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.m, new Runnable()
		 {
			 int fire = 8;
		      public void run()
		      {
		    	 if (fire == 8){
		         if (m.Plätze.containsKey(1)) {
		    	 Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "1 : " + ChatColor.GOLD + m.Plätze.get(1).getName());
		    	   if (m.Plätze.get(1).hasPermission("splun.vip")){
			        stats(m.Plätze.get(1), "Xp", 60, "Player");
			        stats(m.Plätze.get(1), "coins", 100, "Player");
			        stats(m.Plätze.get(1), "Wins", 1, "Splun");
			        m.Plätze.get(1).sendMessage(ChatColor.GREEN + "+100 Coins +60 Xp");	 
		    	   } else {
		    	    stats(m.Plätze.get(1), "Xp", 30, "Player");
		    	    stats(m.Plätze.get(1), "coins", 50, "Player");
		    	    stats(m.Plätze.get(1), "Wins", 1, "Splun");
		    	    m.Plätze.get(1).sendMessage(ChatColor.GREEN + "+50 Coins +30 Xp");
		    	   }
		         } else {
			     Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "1 : " + ChatColor.GOLD + "Niemand");
		         }
		         if (m.Plätze.containsKey(2)) {
		    	 Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "2 : " + ChatColor.GOLD + m.Plätze.get(2).getName());
		    	   if (m.Plätze.get(1).hasPermission("splun.vip")){
		    	    stats(m.Plätze.get(2), "Xp", 40, "Player");
		    	    stats(m.Plätze.get(2), "coins", 60, "Player");
		    	    m.Plätze.get(2).sendMessage(ChatColor.GREEN + "+60 Coins +40 Xp");
		    	   } else {
			    	stats(m.Plätze.get(2), "Xp", 20, "Player");
			    	stats(m.Plätze.get(2), "coins", 30, "Player");
			    	m.Plätze.get(2).sendMessage(ChatColor.GREEN + "+20 Coins +20 Xp");   
		    	   }
		         } else {
			     Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "2 : " + ChatColor.GOLD + "Niemand");
		         }
		         if (m.Plätze.containsKey(3)) {
		    	 Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "3 : " + ChatColor.GOLD + m.Plätze.get(3).getName());
		    	   if (m.Plätze.get(1).hasPermission("splun.vip")){
		    	    stats(m.Plätze.get(3), "Xp", 20, "Player");
		    	    stats(m.Plätze.get(3), "coins", 20, "Player");
		    	    m.Plätze.get(3).sendMessage(ChatColor.GREEN + "+20 Coins +20 Xp");
		    	   } else {
			    	stats(m.Plätze.get(3), "Xp", 10, "Player");
			    	stats(m.Plätze.get(3), "coins", 10, "Player");
			    	m.Plätze.get(3).sendMessage(ChatColor.GREEN + "+10 Coins + 10 Xp");   
		    	   }
		         } else {
			     Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "3 : " + ChatColor.GOLD + "Niemand");
		         }
		         
		         for(Player player : Bukkit.getOnlinePlayers())
		         {
		        	 if (!m.Plätze.containsValue(player)){
				    	 stats(player, "Xp", 5, "Player");
				    	 stats(player, "coins", 5, "Player");
				    	 player.sendMessage(ChatColor.GREEN + "+5 Coins + 5 Xp"); 
		        	 }
		         }
		         
		    	 Bukkit.broadcastMessage("");
		    	 Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "Der Server wird in 5 Sekunden Neugestartet");
		    	 }
		    	 
		    	 if (fire == 3){
		    	   for(Player player : Bukkit.getOnlinePlayers())
		    		{
		    		  m.connectTo(player, m.lobbyserver);
		    		}
		    	 }
		    	 
		    	 if (fire > 0) {
			       fire -= 1;
			     } else if (fire == 0){
			       Bukkit.shutdown();
		        }
		      }
		    }, 0L, 20L);
	}
	
	public void spawnItem(int delay) {
	  Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this.m, new Runnable()
		 {
		   public void run()
		   {
			 if(m.isOn) {
			   if(m.Items < m.maxItems) {
				  m.Items++;
				  
				  Random r1 = new Random();
				  ItemStack Packup = new ItemStack(m.ITEMS[r1.nextInt(m.ITEMS.length - 1)]);
				  Location RandomItemSpawn = getRandomItemSpawn();
				  List<Block> spawns = getNearbyBlocks(RandomItemSpawn, 6);
				  
				  if (!spawns.isEmpty()) {
				  Block b = spawns.get(new Random().nextInt(spawns.size()));
				  Bukkit.getWorld(m.world).dropItemNaturally(b.getLocation().add(0, 1, 0), Packup);
				  Bukkit.broadcastMessage(m.prefix + ChatColor.WHITE + "Ein neues Powerup ist gespawnt");  
				  spawnItem(5);
				  
				  } else {
				  spawnItem(5);
				  }
			   } else {
				  spawnItem(5);
			   }
			 }
		   }
		   
		}, 20L * delay);
	}
	
	@SuppressWarnings("deprecation")
	public List<Block> getNearbyBlocks(Location location, int Radius)
    {
        List<Block> Blocks = new ArrayList<Block>();
        
        for (int X = location.getBlockX() - Radius ; X <= location.getBlockX() + Radius ; X++)
        {
            for (int Y = location.getBlockY() - Radius ; Y <= location.getBlockY() + Radius ; Y++)
            {
                for (int Z = location.getBlockZ() - Radius ; Z <= location.getBlockZ() + Radius ; Z++)
                {
                    Block block = location.getWorld().getBlockAt(X, Y, Z);
                    if ((block.getType() == Material.STAINED_CLAY) && (block.getData() == (byte) 0))
                    {
                        Blocks.add(block);
                    }
                }
            }
        }
        
        return Blocks;
    }
	
	public void CheckWin(Player player){
      if (this.m.isliving.size() == 2){
    	  this.m.Plätze.put(3, player);
      } else if (this.m.isliving.size() == 1){
    	  this.m.Plätze.put(2, player);
		  for (Player playerON : Bukkit.getOnlinePlayers()){
			  if (this.m.isliving.contains(playerON)) {
				 this.m.Plätze.put(1, playerON);
				 this.m.Schutz.add(playerON);
				 StopServer();
			  }
		  }
	  }
	}
	
	public void equip(Player player){
	  player.getInventory().clear();
	 
	  ItemStack switcher = new ItemStack(Material.WATCH, 1);
	  ItemMeta sw = switcher.getItemMeta();
	  sw.setDisplayName(ChatColor.RED + "Back to Hub");
	  switcher.setItemMeta(sw);
	  
	  player.getInventory().addItem(switcher);
	}
	
	//Spawns Erstellen/Herausnehmen
	
	  public void setLobby(Location loc)
	  {
	    if (!this.m.getConfig().contains("Splun.Lobby")) {
	      this.m.getConfig().addDefault("Splun.Lobby.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().addDefault("Splun.Lobby.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().addDefault("Splun.Lobby.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().addDefault("Splun.Lobby.World", loc.getWorld().getName());
	      this.m.getConfig().addDefault("Splun.Lobby.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().addDefault("Splun.Lobby.Yaw", Float.valueOf(loc.getYaw()));
	    } else {
	      this.m.getConfig().set("Splun.Lobby.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().set("Splun.Lobby.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().set("Splun.Lobby.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().set("Splun.Lobby.World", loc.getWorld().getName());
	      this.m.getConfig().set("Splun.Lobby.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().set("Splun.Lobby.Yaw", Float.valueOf(loc.getYaw()));
	    }
	    this.m.saveConfig();
	  }
	  
	  public void setSpec(Location loc)
	  {
	    if (!this.m.getConfig().contains("Splun.Spectate")) {
	      this.m.getConfig().addDefault("Splun.Spectate.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().addDefault("Splun.Spectate.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().addDefault("Splun.Spectate.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().addDefault("Splun.Spectate.World", loc.getWorld().getName());
	      this.m.getConfig().addDefault("Splun.Spectate.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().addDefault("Splun.Spectate.Yaw", Float.valueOf(loc.getYaw()));
	    } else {
	      this.m.getConfig().set("Splun.Spectate.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().set("Splun.Spectate.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().set("Splun.Spectate.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().set("Splun.Spectate.World", loc.getWorld().getName());
	      this.m.getConfig().set("Splun.Spectate.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().set("Splun.Spectate.Yaw", Float.valueOf(loc.getYaw()));
	    }
	    this.m.saveConfig();
	  }
	  
	  public void addSpawn(Location loc)
	  {
	    if (!this.m.getConfig().contains("Splun.Spawns")) {
		  this.m.getConfig().addDefault("Splun.Spawns.Count", 1);
	      this.m.getConfig().addDefault("Splun.Spawns.1.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().addDefault("Splun.Spawns.1.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().addDefault("Splun.Spawns.1.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().addDefault("Splun.Spawns.1.World", loc.getWorld().getName());
	      this.m.getConfig().addDefault("Splun.Spawns.1.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().addDefault("Splun.Spawns.1.Yaw", Float.valueOf(loc.getYaw()));
	    } else {
	      int count = this.m.getConfig().getInt("Splun.Spawns.Count") + 1;
		  this.m.getConfig().set("Splun.Spawns.Count", count);
	      this.m.getConfig().set("Splun.Spawns." + count + ".X", Double.valueOf(loc.getX()));
	      this.m.getConfig().set("Splun.Spawns." + count + ".Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().set("Splun.Spawns." + count + ".Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().set("Splun.Spawns." + count + ".World", loc.getWorld().getName());
	      this.m.getConfig().set("Splun.Spawns." + count + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().set("Splun.Spawns." + count + ".Yaw", Float.valueOf(loc.getYaw()));
	    }
	    this.m.saveConfig();
	  }
	  
	  public void addItemSpawn(Location loc)
	  {
	    if (!this.m.getConfig().contains("Splun.ItemSpawns")) {
		  this.m.getConfig().addDefault("Splun.ItemSpawns.Count", 1);
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.X", Double.valueOf(loc.getX()));
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.World", loc.getWorld().getName());
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().addDefault("Splun.ItemSpawns.1.Yaw", Float.valueOf(loc.getYaw()));
	    } else {
	      int count = this.m.getConfig().getInt("Splun.ItemSpawns.Count") + 1;
		  this.m.getConfig().set("Splun.ItemSpawns.Count", count);
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".X", Double.valueOf(loc.getX()));
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".Y", Double.valueOf(loc.getY()));
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".Z", Double.valueOf(loc.getZ()));
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".World", loc.getWorld().getName());
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".Pitch", Float.valueOf(loc.getPitch()));
	      this.m.getConfig().set("Splun.ItemSpawns." + count + ".Yaw", Float.valueOf(loc.getYaw()));
	    }
	    this.m.saveConfig();
	  }
	  
	  public Location getLobby()
	  {
	    Location loc = new Location(Bukkit.getWorld(this.m.getConfig().getString("Splun.Lobby.World")), 
	      this.m.getConfig().getDouble("Splun.Lobby.X"), 
	      this.m.getConfig().getDouble("Splun.Lobby.Y"), 
	      this.m.getConfig().getDouble("Splun.Lobby.Z"));
	    loc.setPitch((float)this.m.getConfig().getDouble("Splun.Lobby.Pitch"));
	    loc.setYaw((float)this.m.getConfig().getDouble("Splun.Lobby.Yaw"));
	    return loc;
	  }
	  
	  public Location getSpectate()
	  {
	    Location loc = new Location(Bukkit.getWorld(this.m.getConfig().getString("Splun.Spectate.World")), 
	      this.m.getConfig().getDouble("Splun.Spectate.X"), 
	      this.m.getConfig().getDouble("Splun.Spectate.Y"), 
	      this.m.getConfig().getDouble("Splun.Spectate.Z"));
	    loc.setPitch((float)this.m.getConfig().getDouble("Splun.Spectate.Pitch"));
	    loc.setYaw((float)this.m.getConfig().getDouble("Splun.Spectate.Yaw"));
	    return loc;
	  }
	  
	  public Location getRandomSpawn()
	  {
	    Random r = new Random();
	    int rand = r.nextInt(this.m.getConfig().getInt("Splun.Spawns.Count")) + 1;
	      
	      Location loc = new Location(Bukkit.getWorld(this.m.getConfig().getString("Splun.Spawns." + rand + ".World")), 
	        this.m.getConfig().getDouble("Splun.Spawns." + rand + ".X"), 
	        this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Y"), 
	        this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Z"));

	      loc.setPitch((float)this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Pitch"));
	      loc.setYaw((float)this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Yaw"));
	    
	    return loc;
	  }
	  
	  public Location getRandomItemSpawn()
	  {
	    Random r = new Random();
	    int rand = r.nextInt(this.m.getConfig().getInt("Splun.ItemSpawns.Count")) + 1;
	      
	      Location loc = new Location(Bukkit.getWorld(this.m.getConfig().getString("Splun.ItemSpawns." + rand + ".World")), 
	        this.m.getConfig().getDouble("Splun.ItemSpawns." + rand + ".X"), 
	        this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Y"), 
	        this.m.getConfig().getDouble("Splun.Spawns." + rand + ".Z"));

	      loc.setPitch((float)this.m.getConfig().getDouble("Splun.ItemSpawns." + rand + ".Pitch"));
	      loc.setYaw((float)this.m.getConfig().getDouble("Splun.ItemSpawns." + rand + ".Yaw"));
	    
	    return loc;
	  }
	  
	  public void tpRandomSpawn(Player player)
	  {
	    player.teleport(this.getRandomSpawn());
	  }
	  
	  public void tpSpectate(Player player)
	  {
	    player.teleport(this.getSpectate());
	  }
	  
	  public void tpLobby(Player player)
	  {
	    player.teleport(this.getLobby());
	  }
}
