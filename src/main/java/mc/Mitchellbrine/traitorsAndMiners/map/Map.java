package mc.Mitchellbrine.traitorsAndMiners.map;

import mc.Mitchellbrine.traitorsAndMiners.TraitorsAndMiners;
import org.bukkit.Location;

import java.io.File;

/**
 * Created by Mitchellbrine on 2014.
 */
@SuppressWarnings("unused")
public class Map {

    private String mapName;
    private String mapAuthor;
    private File mapFile;

    private Location spawn;
    private Location lobby;

    private int maxPlayers;

    public Map(String name, String author) {
        this.mapName = name;
        this.mapAuthor = author;
        mapFile = new File(TraitorsAndMiners.instance.getDataFolder(), name.replaceAll(" ", "").replaceAll(",","").toLowerCase() + ".zip");
        MapManager.maps.add(this);
    }

    @Deprecated
    public Map(String name, String author, String fileName) {
        this.mapName = name;
        this.mapAuthor = author;
        mapFile = new File(TraitorsAndMiners.instance.getDataFolder(), fileName + ".zip");
    }
    
    public Map(String name, String author, String fileName,Location lobby, Location spawn) {
        this.mapName = name;
        this.mapAuthor = author;
        mapFile = new File(TraitorsAndMiners.instance.getDataFolder(), fileName + ".zip");
        this.lobby = lobby;
        this.spawn = spawn;
    }

    public String getMapName() {
        return this.mapName;
    }

    public String getMapAuthor() {
        return this.mapAuthor;
    }

    public File getMapFile() {
        return this.mapFile;
    }

    public Location getSpawn() {
        return this.spawn;
    }

    public Location getLobby() {
        return this.lobby;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public Map setMapName(String name) {
        this.mapName = name;
        return this;
    }

    public Map setMapAuthor(String author) {
        this.mapAuthor = author;
        return this;
    }

    public Map setMapFile(File file) {
        this.mapFile = file;
        return this;
    }

    public Map setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
        return this;
    }

    public Map setSpawn(Location spawn) {
        this.spawn = spawn;
        return this;
    }

    public Map setLobby(Location lobby) {
        this.lobby = lobby;
        return this;
    }

}
