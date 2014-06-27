package com.massivecraft.factions.integration.essentials;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.Teleport;
import com.earth2me.essentials.Trade;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.EventFactionsHomeTeleport;

import java.math.BigDecimal;

public class EngineEssentials implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineEssentials i = new EngineEssentials();
	public static EngineEssentials get() { return i; }
	private EngineEssentials() {}

	// -------------------------------------------- //
	// ACTIVATE & DEACTIVATE
	// -------------------------------------------- //

	public void activate()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}

	public void deactivate()
	{
		HandlerList.unregisterAll(this);
	}

	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //

	// FactionsEventHomeTeleport

	@EventHandler(priority = EventPriority.NORMAL)
	public void handleTeleportOnHomeCommand(EventFactionsHomeTeleport event)
	{
		UPlayer usender = event.getUSender();
		UConf uconf = UConf.get(usender);
		if (!uconf.homesTeleportCommandEssentialsIntegration) return;

		Essentials essentials = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
		if (essentials == null) return;

		Player player = (event.getSender() instanceof Player) ? (Player) event.getSender() : null;
		if (player == null) return;

		Teleport teleport = essentials.getUser(player).getTeleport();
		Trade trade = new Trade(new BigDecimal(uconf.econCostHome), essentials);

		try
		{
			// Attempt to teleport player using Essentials' handler
			Location homeLoc = usender.getFaction().getHome().getLocation().asBukkitLocation();
			teleport.teleport(homeLoc, trade, TeleportCause.COMMAND);
		}
		catch (Exception e)
		{
			// Exceptions could be thrown for cooldowns, etc
            usender.msg("<b>Error: " + e.getMessage());
		}

		event.setCancelled(true);
	}

}
