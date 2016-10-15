package me.NoChance.PvPManager.Managers;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import me.NoChance.PvPManager.Dependencies.Dependency;
import me.NoChance.PvPManager.Dependencies.Hook;
import me.NoChance.PvPManager.Dependencies.PvPlugin;
import me.NoChance.PvPManager.Dependencies.Hooks.Vault;
import me.NoChance.PvPManager.Settings.Settings;
import me.NoChance.PvPManager.Utils.Log;
import net.milkbowl.vault.economy.Economy;

public class DependencyManager {

	private final HashMap<Hook, Dependency> dependencies = new HashMap<>();
	private final HashSet<PvPlugin> attackChecks = new HashSet<>();

	public DependencyManager() {
		checkForVault();
		checkForWorldguard();
		checkForFactions();
		checkForSimpleClans();
	}

	private void checkForVault() {
		if (Bukkit.getPluginManager().isPluginEnabled("Vault")) {
			final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
			Economy economy = null;
			if (economyProvider != null) {
				economy = economyProvider.getProvider();
			}
			if (economy != null) {
				Log.info("Vault Found! Using it for currency related features");
				dependencies.put(Hook.VAULT, new Vault(economy));
			} else {
				Log.severe("Error! No Economy plugin found");
			}
		} else {
			Log.severe("Vault not found! Features requiring Vault won't work!");
			Settings.setFineAmount(0);
			Settings.setMoneyPenalty(0);
			Settings.setMoneyReward(0);
		}
	}

	private void checkForWorldguard() {
	}

	private void checkForFactions() {
	}

	private void checkForSimpleClans() {
	}

	public final boolean canAttack(final Player attacker, final Player defender) {
		for (final PvPlugin pvPlugin : attackChecks)
			if (!pvPlugin.canAttack(attacker, defender))
				return false;
		return true;
	}

	public boolean worldguardCanAttack(final Location l) {
		return ((PvPlugin) dependencies.get(Hook.WORLDGUARD)).canBeAttacked(null, l);
	}

	public final boolean isDependencyEnabled(final Hook d) {
		return dependencies.containsKey(d);
	}

	public Dependency getDependency(final Hook h) {
		return dependencies.get(h);
	}

	public JavaPlugin getDepencyMainClass(final Hook h) {
		if (isDependencyEnabled(h))
			return dependencies.get(h).getMainClass();
		return null;
	}

	public final Economy getEconomy() {
		if (isDependencyEnabled(Hook.VAULT))
			return ((Vault) dependencies.get(Hook.VAULT)).getEconomy();
		return null;
	}

}
