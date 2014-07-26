package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFaction extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFaction()
	{
		// Aliases
		this.addAliases("f", "faction");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.FACTION.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(usenderFaction), usenderFaction);
		if (faction == null) return;
		
		// Data precalculation 
		UConf uconf = UConf.get(faction);
		//boolean none = faction.isNone();
		boolean normal = faction.isNormal();
		
		// INFO: Title
		msg(Txt.titleize(Txt.upperCaseFirst(faction.getUniverse()) + " Faction " + faction.getName(usender)));
		
		// INFO: Description
		msg("<a>Description: <i>%s", faction.getDescription());	
		
		if (normal)
		{
			// INFO: Age
			long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageString = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");
			msg("<a>Age: <i>%s", ageString);
			
			// INFO: Open
			msg("<a>Open: <i>"+(faction.isOpen() ? "<lime>Yes<i>, anyone can join" : "<rose>No<i>, only invited people can join"));
	
			// INFO: Power
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			msg("<a>Land / Power / Maxpower: <i> %d/%d/%d %s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			
			// show the land value
			if (Econ.isEnabled(faction))
			{
				long landCount = faction.getLandCount();
				
				for (EventFactionsChunkChangeType type : EventFactionsChunkChangeType.values())
				{
					Double money = uconf.econChunkCost.get(type);
					if (money == null) continue;
					if (money == 0D) continue;
					money *= landCount;
					
					String word = null;
					if (money > 0)
					{
						word = "cost";
					}
					else
					{
						word = "reward";
						money *= -1;
					}
					
					msg("<a>Total land %s %s: <i>%s", type.toString().toLowerCase(), word, Money.format(money));
				}
				
				// Show bank contents
				if (UConf.get(faction).bankEnabled)
				{
					msg("<a>Bank contains: <i>"+Money.format(Money.get(faction)));
				}
			}
			
			// Display important flags
			// TODO: Find the non default flags, and display them instead.
			if (faction.getFlag(FFlag.PERMANENT))
			{
				msg("<a>This faction is permanent - remaining even with no followers.");
			}
			
			if (faction.getFlag(FFlag.PEACEFUL))
			{
				msg("<a>This faction is peaceful - in truce with everyone.");
			}
		}
		
		String sepparator = Txt.parse("<i>")+", ";
		
		// List the relations to other factions
		Map<Rel, List<String>> relationNames = faction.getFactionNamesPerRelation(usender, true);
		
		if (faction.getFlag(FFlag.PEACEFUL))
		{
			sendMessage(Txt.parse("<a>In Truce with:<i> *everyone*"));
		}
		else
		{
			sendMessage(Txt.parse("<a>In Truce with: ") + Txt.implode(relationNames.get(Rel.TRUCE), sepparator));
		}
		
		sendMessage(Txt.parse("<a>Allies: ") + Txt.implode(relationNames.get(Rel.ALLY), sepparator));
		sendMessage(Txt.parse("<a>Enemies: ") + Txt.implode(relationNames.get(Rel.ENEMY), sepparator));
		
		// List the followers...
		List<String> followerNamesOnline = new ArrayList<String>();
		List<String> followerNamesOffline = new ArrayList<String>();
		
		List<UPlayer> followers = faction.getUPlayers();
		Collections.sort(followers, PlayerRoleComparator.get());
		
		for (UPlayer follower : followers)
		{
			boolean online = false;

			if (follower.isOnline() && Mixin.canSee(sender, follower.getId()))
			{
				// Check if the player is currently inside the universe of the faction
				PS ps = Mixin.getSenderPs(follower.getId());

				if (ps != null)
				{
					String psUniverse = Factions.get().getMultiverse().getUniverseForWorldName(ps.getWorld());

					if (psUniverse.equals(faction.getUniverse()))
					{
						followerNamesOnline.add(follower.getNameAndTitle(usender));
						online = true;
					}
				}
			}

			if (normal && !online)
			{
				// For the non-faction we skip the offline members since they are far to many (infinate almost)
				followerNamesOffline.add(follower.getNameAndTitle(usender));
			}
		}
		
		sendMessage(Txt.parse("<a>Followers online (%s): ", followerNamesOnline.size()) + Txt.implode(followerNamesOnline, sepparator));
		
		if (normal)
		{
			sendMessage(Txt.parse("<a>Followers offline (%s): ", followerNamesOffline.size()) + Txt.implode(followerNamesOffline, sepparator));
		}
	}
	
}
