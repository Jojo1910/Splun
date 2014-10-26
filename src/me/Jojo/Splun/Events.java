package me.Jojo.Splun;

import net.minecraft.server.v1_7_R3.AxisAlignedBB;
import net.minecraft.server.v1_7_R3.EntityPlayer;
import net.minecraft.server.v1_7_R3.EnumClientCommand;
import net.minecraft.server.v1_7_R3.PacketPlayInClientCommand;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

public class Events implements Listener{
	
	Main m;
	
	public Events(Main m){
		this.m = m;
	}
	
	@EventHandler
    public void onDamage(EntityDamageEvent event) {
	  if (event.getEntity() instanceof Player){
        if (!this.m.isOn){
          if (event.getCause() == DamageCause.VOID)  {
        	this.m.mth.tpLobby((Player) event.getEntity());
        	event.setCancelled(true);
          } else {
        	event.setCancelled(true);
          }
        } else { 
        	if (event.getCause() == DamageCause.FALL){
        		event.setCancelled(true);
        	} else if (event.getCause() == DamageCause.VOID) {
        		((Player) event.getEntity()).setHealth(0.0);
         }
	  }
     }
	}
    
    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event) {
        if ((event.getAction() == Action.RIGHT_CLICK_AIR) && (event.getPlayer().getItemInHand().getType() == Material.WATCH)) {
            this.m.connectTo(event.getPlayer(), this.m.lobbyserver);
        } else if ((event.getAction() == Action.RIGHT_CLICK_BLOCK) && (event.getPlayer().getItemInHand().getType() == Material.WATCH)) {
            this.m.connectTo(event.getPlayer(), this.m.lobbyserver);
        } else { 
        	event.setCancelled(true);
        }
        
    }
    
    @EventHandler
    public void IneventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onDamagebyPlayer(EntityDamageByEntityEvent event) {
        if (!this.m.isOn) {
            event.setCancelled(true);
        } else {
        	if ((event.getDamager() instanceof Player) && (event.getEntity() instanceof Player)){
        	   if (((Player) event.getDamager()).getInventory().getItemInHand().getType() == Material.DIAMOND_SWORD){
        		   event.setDamage(0);
        	   } else {
           		event.setCancelled(true);
        	  }
        	} else {
        		event.setCancelled(true);
        	}
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if ((this.m.isOn) && (this.m.isliving.contains(event.getPlayer()))) {
        	this.m.times.put(event.getPlayer().getName(), 0);
        	
        	  EntityPlayer entityPlayer = ((CraftPlayer) event.getPlayer()).getHandle();
        	  AxisAlignedBB boundingBox = entityPlayer.boundingBox;
        	  World world = event.getPlayer().getWorld();
        	  double yBelow = event.getPlayer().getLocation().getY() - 0.0001;
        	  Block northEast = new Location(world, boundingBox.d, yBelow, boundingBox.c).getBlock();
        	  Block northWest = new Location(world, boundingBox.a, yBelow, boundingBox.c).getBlock();
        	  Block southEast = new Location(world, boundingBox.d, yBelow, boundingBox.f).getBlock();
        	  Block southWest = new Location(world, boundingBox.a, yBelow, boundingBox.f).getBlock();
        	  Block[] blocks = {northEast, northWest, southEast, southWest};
        	  for (Block block : blocks) {
              	if ((block.getType() == Material.STAINED_CLAY) && ((block.getData() == (byte) 0) || (block.getData() == (byte) 5) || (block.getData() == (byte) 12) || (block.getData() == (byte) 13)) && (!event.getPlayer().getInventory().contains(new ItemStack(Material.FEATHER, 1)) && (!this.m.Schutz.contains(event.getPlayer())))) {
              	   block.setData((byte) 4);
              	   this.m.mth.BlockLaunch(block);
        	    } else if ((block.getType() == Material.STAINED_CLAY) && (block.getData() == (byte) 6) && (!event.getPlayer().getInventory().contains(new ItemStack(Material.FEATHER, 1)))) {
               	   block.setType(Material.AIR);
               	   TNTPrimed pt = (TNTPrimed) block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);
               	   pt.setFuseTicks(20);
               	   pt.setVelocity(new Vector(pt.getVelocity().getX(), pt.getVelocity().getY() + 0.3D, pt.getVelocity().getZ()));
         	    } else if ((block.getType() == Material.STAINED_CLAY) && (block.getData() == (byte) 15) && (!event.getPlayer().getInventory().contains(new ItemStack(Material.FEATHER, 1)))) {
         	       block.setType(Material.AIR);
             	}
        	  }
         }
    }
	
    @EventHandler
    public void onBlockFall(EntityChangeBlockEvent event) {
        if ((event.getBlock().getType() == Material.STAINED_CLAY)) {
            event.getBlock().setType(Material.AIR);
        }
    }
    
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onExplosion(EntityExplodeEvent event) {
    	if (event.blockList().isEmpty()){return;}
		event.setYield(1F);

		Location eLoc = event.getLocation();
		World w = eLoc.getWorld();
		for (int i = 0; i < event.blockList().size();i++){
			Block b = event.blockList().get(i);
			if ((b.getType() == Material.STAINED_CLAY) && ((b.getData() == (byte) 15) || (b.getData() == (byte) 14) || (b.getData() == (byte) 2) || (b.getData() == (byte) 4))) {
			   b.setType(Material.AIR);
			}
			
			Location bLoc =b.getLocation();
			
			double x = bLoc.getX() - eLoc.getX();
			double y = bLoc.getY() - eLoc.getY() + .5;
			double z = bLoc.getZ() - eLoc.getZ();
			FallingBlock fb = w.spawnFallingBlock(bLoc, b.getType(), (byte)b.getData());
			fb.setDropItem(false);
			fb.setVelocity(new Vector(x,y,z));
		}
    }
    
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        event.setCancelled(true);
    }
    
    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        if ((this.m.isliving.contains(event.getEntity())) && (event.getEntity() instanceof Player)) {
        	event.setDeathMessage(m.prefix + ChatColor.GOLD + event.getEntity().getName() + ChatColor.WHITE + " is Ausgeschieden");
            m.isliving.remove(event.getEntity());
            m.mth.CheckWin(event.getEntity());
            Bukkit.getScheduler().scheduleSyncDelayedTask(this.m, new Runnable()
            {
              public void run() {
                ((CraftPlayer)event.getEntity()).getHandle().playerConnection.a(new PacketPlayInClientCommand(EnumClientCommand.PERFORM_RESPAWN));
                m.mth.equip(event.getEntity());
                m.mth.tpSpectate(event.getEntity());
                for (Player player : Bukkit.getOnlinePlayers()){
                    if(player != event.getEntity()){
                      player.hidePlayer(event.getEntity());
                    } else {
                      player.setAllowFlight(true);
                      player.setFlying(true);
                    }
                }
              }
            }
            , 7L);
        } else {
        	event.setDeathMessage("");
        }
    }
    
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if ((!this.m.isOn) && (this.m.canJoin) && ((this.m.isliving.size() + 1) <= this.m.maxPlayers)) {
          String name = event.getPlayer().getName();
          this.m.isliving.add(event.getPlayer());
          m.mth.equip(event.getPlayer());
          m.mth.tpLobby(event.getPlayer());
          event.getPlayer().setGameMode(GameMode.SURVIVAL);
          
          event.setJoinMessage(this.m.prefix + ChatColor.GOLD + name + ChatColor.WHITE + " ist dem Spiel beigetreten");
          Bukkit.broadcastMessage(this.m.prefix + "Es sind jetzt " + ChatColor.GOLD + this.m.isliving.size() + ChatColor.WHITE + " Spieler im Spiel");
          
          if(this.m.canStart == this.m.isliving.size()){
        	 m.mth.startTimer();
          }
        } else {
        	event.getPlayer().kickPlayer("Es ist ein Fehler passiert deshalb wurdest du vom Server gekickt");
        }
    }
    
    @EventHandler
    public void onLoginin(PlayerLoginEvent event){
      if ((this.m.isOn) && (!this.m.canJoin)) {	
    	  event.disallow(Result.KICK_OTHER, "Das Spiel ist schon gestartet");
      }
    }
    
    @EventHandler
    public void onServerPing(ServerListPingEvent event) {
    	if (this.m.isOn) {
    		event.setMotd("&cInGame");
    	} else {
    		if (Bukkit.getMaxPlayers() == this.m.isliving.size()) {
    			event.setMotd("&cFull");
    		} else {
    			event.setMotd("&aLobby");
    		}
    	}
    }
    
    @EventHandler
    public void onPickup(PlayerPickupItemEvent event){
      if (this.m.isOn) {
    	ItemStack[] items = event.getPlayer().getInventory().getContents();
    	for (ItemStack item : items) {
    	  if (item != null) {
    	    event.setCancelled(true);
    	   return;
    	  }
    	}
    	if ((event.getItem().getItemStack().getType() == Material.DIAMOND_SWORD) || (event.getItem().getItemStack().getType() == Material.FEATHER)) {
    	  event.getPlayer().sendMessage(this.m.prefix + ChatColor.WHITE + "Du hast ein Powerup eingesamelt : " + ChatColor.GOLD + event.getItem().getItemStack().getType());
    	  this.m.mth.poweruptime(event.getPlayer(), 100);
    	  this.m.mth.stats(event.getPlayer(), "Items", 1, "Splun");
    	} else if (event.getItem().getItemStack().getType() == Material.GOLD_BOOTS) {
      	  event.getPlayer().sendMessage(this.m.prefix + ChatColor.WHITE + "Du hast ein Powerup eingesamelt : " + ChatColor.GOLD + event.getItem().getItemStack().getType());
      	  this.m.mth.poweruptime(event.getPlayer(), 100);
      	  this.m.mth.stats(event.getPlayer(), "Items", 1, "Splun");
      	  event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 2));
    	} else if (event.getItem().getItemStack().getType() == Material.FIREWORK) {
          event.getPlayer().sendMessage(this.m.prefix + ChatColor.WHITE + "Du hast ein Powerup eingesamelt : " + ChatColor.GOLD + event.getItem().getItemStack().getType());
          this.m.mth.poweruptime(event.getPlayer(), 100);
          this.m.mth.stats(event.getPlayer(), "Items", 1, "Splun");
          event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 100, 2));
    	} else {
    	  event.setCancelled(true);
    	  event.getItem().remove();
    	}
      } else {
    	  event.setCancelled(true);
      }
    }
    
    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
      event.setCancelled(true);
    }
    
    @EventHandler
    public void onWeather(WeatherChangeEvent event){
      event.setCancelled(true);
    }
    
    @EventHandler
    public void onLeft(PlayerQuitEvent event){
      event.setQuitMessage("");
      if (this.m.isOn) {
    	  if (this.m.isliving.contains(event.getPlayer())) {
    	    this.m.isliving.remove(event.getPlayer());
  		    Bukkit.broadcastMessage(this.m.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " ist geleftet und wurde somit aus dem Spiel entfernt");
    	      if (this.m.isliving.size() == 1) {
    	        this.m.mth.CheckWin(event.getPlayer());
    	     } else if (this.m.isliving.size() == 0) {
     	        Bukkit.shutdown();
     	     }
    	  }
    	  
      } else if ((this.m.isTimer) && (this.m.isliving.size() == 2)){
  		this.m.isliving.remove(event.getPlayer());
  		Bukkit.broadcastMessage(this.m.prefix + ChatColor.WHITE + "Der Timer wurde zurück gesetzt dar " + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " geleftet ist");
  		this.m.isTimer = false;
  		Bukkit.getScheduler().cancelTask(this.m.start);
  		
  		for (Player player : Bukkit.getOnlinePlayers()){
  			player.setLevel(0);
  		}
      } else {
		this.m.isliving.remove(event.getPlayer());
		Bukkit.broadcastMessage(this.m.prefix + ChatColor.GOLD + event.getPlayer().getName() + ChatColor.WHITE + " ist geleftet und wurde somit aus dem Spiel entfernt");
      }
    }
}
