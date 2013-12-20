package com.massivecraft.factions.integration.essentials;

import com.massivecraft.mcore.integration.IntegrationFeaturesAbstract;

public class EssentialsFeatures extends IntegrationFeaturesAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EssentialsFeatures i = new EssentialsFeatures();
	public static EssentialsFeatures get() { return i; }
	private EssentialsFeatures() { super("Essentials"); }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void activate()
	{
		EssentialsEngine.get().activate();
	}

	@Override
	public void deactivate()
	{
		EssentialsEngine.get().deactivate();
	}

}
