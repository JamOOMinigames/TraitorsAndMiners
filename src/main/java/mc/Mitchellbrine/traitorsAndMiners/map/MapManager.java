package mc.Mitchellbrine.traitorsAndMiners.map;

import mc.Mitchellbrine.traitorsAndMiners.FileHelper;
import mc.Mitchellbrine.traitorsAndMiners.TraitorsAndMiners;

import java.io.*;
import java.util.Map.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * Created by Mitchellbrine on 2014.
 */
public class MapManager {

    public static ArrayList<Map> maps = new ArrayList<>();
    public static HashMap<Map,Integer> votes = new HashMap<>();
    public static ArrayList<UUID> voted = new ArrayList<>();

    public static void registerMap(Map map) {
        maps.add(map);
    }

    @SuppressWarnings("static-access")
    public static void resetMap() {
        try {
            FileHelper.deleteDir(new File(TraitorsAndMiners.instance.getServer().getWorldContainer(), "world"));
            int map = TraitorsAndMiners.instance.randomW.nextInt(maps.size());
            File lastVote = new File(TraitorsAndMiners.instance.getDataFolder(),"mapVote.txt");
            if (lastVote.exists()) {
                StringBuilder text = new StringBuilder();
                try (Scanner scanner = new Scanner(new FileInputStream(lastVote), "UTF-8")) {
                    while (scanner.hasNextLine()) {
                        text.append(scanner.nextLine());
                    }
                }
                map = Integer.parseInt(text.toString());
            }

            if (maps.get(map) != null) {
                FileHelper.unzip(maps.get(map).getMapFile(), new File(TraitorsAndMiners.instance.getServer().getWorldContainer(), "world"));
                TraitorsAndMiners.instance.getLogger().info("Loaded: " + maps.get(map).getMapName() + " by " + maps.get(map).getMapAuthor());
                TraitorsAndMiners.map = maps.get(map).getMapName() + " by " + maps.get(map).getMapAuthor();

                TraitorsAndMiners.instance.tamMap = maps.get(map);

                TraitorsAndMiners.instance.maxPlayers = maps.get(map).getMaxPlayers();
                if (TraitorsAndMiners.instance.maxPlayers == 0) {
                    TraitorsAndMiners.instance.maxPlayers = 24;
                }
            } else {
                FileHelper.unzip(new File(TraitorsAndMiners.instance.getDataFolder(), "world_tam1.zip"), new File(TraitorsAndMiners.instance.getServer().getWorldContainer(), "world"));
                TraitorsAndMiners.instance.getLogger().info("Loaded: Western Mines by MCG");
                TraitorsAndMiners.map = "Western Mines by MCG";

                TraitorsAndMiners.instance.maxPlayers = 24;
            }
            
            
        } catch (IOException ex) {
            ex.printStackTrace();
            TraitorsAndMiners.instance.getServer().shutdown();
        }
    }

    public static void recordVote() {


        try {
        Integer largestVal = 0;
        Map bestMap = null;
        File finalMap = new File(TraitorsAndMiners.instance.getDataFolder(), "mapVote.txt");
        
        if (finalMap.exists()) {
        	finalMap.delete();
        }
        
        if (!votes.isEmpty()) {
        for (Entry<Map, Integer> i : votes.entrySet()){
            if (largestVal  < i.getValue()){
                largestVal = i.getValue();
                bestMap = i.getKey();
            }
        }


        if (largestVal > 0) {
            if (bestMap != null && maps.contains(bestMap)) {
					PrintWriter writer = new PrintWriter(finalMap);
                    writer.println(maps.indexOf(bestMap));
                    System.err.println(maps.indexOf(bestMap));
                    writer.close();
            }
        }

        }
        
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }

}
