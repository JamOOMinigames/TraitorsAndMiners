package mc.Mitchellbrine.traitorsAndMiners.map;

import mc.Mitchellbrine.traitorsAndMiners.FileHelper;
import mc.Mitchellbrine.traitorsAndMiners.TraitorsAndMiners;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Mitchellbrine on 2014.
 */
public class MapManager {

    public static ArrayList<Map> maps = new ArrayList<>();

    @SuppressWarnings("static-access")
    public static void resetMap() {
        try {
            FileHelper.deleteDir(new File(TraitorsAndMiners.instance.getServer().getWorldContainer(), "world"));
            int map = TraitorsAndMiners.instance.randomW.nextInt(maps.size());
            FileHelper.unzip(maps.get(map).getMapFile(), new File(TraitorsAndMiners.instance.getServer().getWorldContainer(), "world"));
            TraitorsAndMiners.instance.getLogger().info("Loaded: " + maps.get(map).getMapName() + " by " + maps.get(map).getMapAuthor());
            TraitorsAndMiners.map = maps.get(map).getMapName() + " by " + maps.get(map).getMapAuthor();
            TraitorsAndMiners.instance.spawn = maps.get(map).getSpawn();
            TraitorsAndMiners.instance.lobby = maps.get(map).getLobby();
            TraitorsAndMiners.instance.maxPlayers = maps.get(map).getMaxPlayers();
        } catch (IOException ex) {
            ex.printStackTrace();
            TraitorsAndMiners.instance.getServer().shutdown();
        }
    }

}
