package me.NoChance.PvPManager.Utils;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import me.NoChance.PvPManager.Config.Variables;

public class CombatUtils {

	public static boolean checkToggleCooldown(long toggleTime) {
		if (toggleTime == 0)
			return true;
		else if (System.currentTimeMillis() - toggleTime < Variables.toggleCooldown * 1000)
			return false;
		else
			return true;
	}

	public static boolean isPvP(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			if (event.getDamager().hasMetadata("NPC") || event.getEntity().hasMetadata("NPC"))
				return false;
			return true;
		}
		if (event.getEntity() instanceof Player) {
			if (event.getDamager() instanceof Projectile) {
				Projectile proj = (Projectile) event.getDamager();
				if (proj.getShooter() instanceof Player && !proj.getShooter().equals(event.getEntity())) {
					if (((Player) proj.getShooter()).hasMetadata("NPC"))
						return false;
					return true;
				}
			}
		}
		return false;
	}
}
