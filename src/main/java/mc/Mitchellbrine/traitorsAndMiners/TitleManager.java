package mc.Mitchellbrine.traitorsAndMiners;

import io.puharesource.mc.titlemanager.api.TabTitleObject;
import io.puharesource.mc.titlemanager.api.TitleObject;

import org.bukkit.entity.Player;

public class TitleManager {
	
	public static void sendFloatingText(Player player, String title, String subtitle, int fadeIn, int stay, int fadeOut) {
		  new TitleObject(title, subtitle).setFadeIn(fadeIn).setStay(stay).setFadeOut(fadeOut).send(player);
	}
	
	public static void sendHeaderAndFooter(Player player, String header, String footer) {
		  new TabTitleObject(header, footer).send(player);
	}
	
	public static void sendHeader(Player player, String header) {
		  new TabTitleObject(header, TabTitleObject.Position.HEADER).send(player);
	}
}
