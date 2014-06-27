package com.massivecraft.factions.integration.essentials;

import com.massivecraft.massivecore.integration.IntegrationAbstract;

public class IntegrationEssentials extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static IntegrationEssentials i = new IntegrationEssentials();
	public static IntegrationEssentials get() { return i; }
	private IntegrationEssentials() { super("Essentials"); }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void activate()
	{
		EngineEssentials.get().activate();
	}

	@Override
	public void deactivate()
	{
		EngineEssentials.get().deactivate();
	}

}
