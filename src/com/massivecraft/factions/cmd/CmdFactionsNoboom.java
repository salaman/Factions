package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFFlag;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.arg.ARBoolean;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsNoboom extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsNoboom()
	{
		// Aliases
		this.addAliases("noboom");

		// Args
		this.addOptionalArg("yes/no", "read");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.NOBOOM.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform()
	{
		Faction faction = usenderFaction;
		if (faction == null) return;

		// FPerm
		if (!FPerm.NOBOOM.has(usender, faction, true)) return;

		FFlag flag = FFlag.EXPLOSIONS;

		if ( ! this.argIsSet(1))
		{
			boolean explosions = faction.getFlag(flag);
			faction.setFlag(flag, !explosions);
			msg(Txt.titleize("Flag for " + faction.describeTo(usender, true)));
			msg(flag.getStateInfo(faction.getFlag(flag), true));
			return;
		}

		Boolean targetValue = this.arg(1, ARBoolean.get());
		if (targetValue == null) return;

		// Do the change
		msg(Txt.titleize("Flag for " + faction.describeTo(usender, true)));
		faction.setFlag(flag, targetValue);
		msg(flag.getStateInfo(faction.getFlag(flag), true));
	}

}
