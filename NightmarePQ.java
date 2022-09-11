/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class NightmarePQ {
    private static Map<Integer, List<MapleCharacter>> playerschan = new HashMap<>();
    private static int lvl1 = 0;
    private static int lvl2 = 0;
    private static int lvl3 = 0;
    private static int score1 = 0;
    private static int score2 = 0;
    private static int score3 = 0;
    private static Map<Integer, MapleMap> pqmaps = new HashMap<>();
    private static Point pos1 = new Point(276, -504);
    private static Point pos2 = new Point(-62, -8);
    private static Point pos3 = new Point(442, 589);
    private static List<Point> spwanpos = new ArrayList<>();
    private static ScheduledFuture<?> spawnSchedule1 = null;
    private static ScheduledFuture<?> spawnSchedule2 = null;
    private static ScheduledFuture<?> spawnSchedule3 = null;
    private static ScheduledFuture<?> pqch1 = null;
    private static ScheduledFuture<?> pqch2 = null;
    private static ScheduledFuture<?> pqch3 = null;
    private static ScheduledFuture<?> checkpq = null;
    
    public void coordinateNightmarePQ(){
        spwanpos.add(pos1);
        spwanpos.add(pos2);
        spwanpos.add(pos3);
        spawnSchedule1 = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                List<Integer> posindx = new ArrayList<>();
                if (pqmaps.containsKey(1)){
                    if (!pqmaps.get(1).getAllPlayers().isEmpty() && !playerschan.get(1).isEmpty()){
                        List<MapleMonster> mobs = pqmaps.get(1).getAllMonsters();
                        if (!mobs.isEmpty()){
                            int spawcord[] = {-389,106,704};
                            int indx = 0;
                            while (indx<3){
                                boolean posfound = false;
                                for (MapleMonster mob : mobs){
                                    if ((spawcord[indx]-150) <= mob.getPosition().y && (spawcord[indx]+150) >= mob.getPosition().y){
                                        posfound = true;
                                        break;
                                    }
                                }
                                if (!posfound){
                                    posindx.add(indx);
                                }
                                indx += 1;
                            }
                        } else {
                            posindx.add(0);
                            posindx.add(1);
                            posindx.add(2);
                        }
                        for (int posi : posindx){
                            if ((lvl1 >= 0 && lvl1 < 300) && (lvl1 % 60 == 0)){
                                MapleMonster headless = MapleLifeFactory.getMonster(9400549);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(headless, spwanpos.get(posi));
                                score1 += 1;
                            } else if ((lvl1 >= 300 && lvl1 < 600) && (lvl1 % 60 == 0)){
                                MapleMonster scar = MapleLifeFactory.getMonster(9400596);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(scar, spwanpos.get(posi));
                                score1 += 2;
                            } else if ((lvl1 >= 600 && lvl1 < 900) && (lvl1 % 60 == 0)){
                                MapleMonster azur = MapleLifeFactory.getMonster(9400597);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(azur, spwanpos.get(posi));
                                score1 += 3;
                            } else if ((lvl1 >= 900 && lvl1 < 1200) && (lvl1 % 60 == 0)){
                                MapleMonster levi = MapleLifeFactory.getMonster(8220003);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(levi, spwanpos.get(posi));
                                score1 += 4;
                            } else if ((lvl1 >= 1200 && lvl1 < 1500) && (lvl1 % 60 == 0)){
                                MapleMonster bigf = MapleLifeFactory.getMonster(9400575);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(bigf, spwanpos.get(posi));
                                score1 += 5;
                            } else if ((lvl1 >= 1500 && lvl1 < 1800) && (lvl1 % 60 == 0)){
                                MapleMonster bcrow = MapleLifeFactory.getMonster(9400015);
                                pqmaps.get(1).spawnMonsterOnGroundBelow(bcrow, spwanpos.get(posi));
                                score1 += 6;
                            }
                        }
                        lvl1 += 1;
                    }
                }
            }
        }, 1000);
        spawnSchedule2 = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                List<Integer> posindx = new ArrayList<>();
                if (pqmaps.containsKey(2)){
                    if (!pqmaps.get(2).getAllPlayers().isEmpty() && !playerschan.get(2).isEmpty()){
                        List<MapleMonster> mobs = pqmaps.get(2).getAllMonsters();
                        if (!mobs.isEmpty()){
                            int spawcord[] = {-389,106,704};
                            int indx = 0;
                            while (indx<3){
                                boolean posfound = false;
                                for (MapleMonster mob : mobs){
                                    if ((spawcord[indx]-150) <= mob.getPosition().y && (spawcord[indx]+150) >= mob.getPosition().y){
                                        posfound = true;
                                        break;
                                    }
                                }
                                if (!posfound){
                                    posindx.add(indx);
                                }
                                indx += 1;
                            }
                        } else {
                            posindx.add(0);
                            posindx.add(1);
                            posindx.add(2);
                        }
                        for (int posi : posindx){
                            if ((lvl2 >= 0 && lvl2 < 300) && (lvl2 % 60 == 0)){
                                MapleMonster headless = MapleLifeFactory.getMonster(9400549);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(headless, spwanpos.get(posi));
                                score2 += 1;
                            } else if ((lvl2 >= 300 && lvl2 < 600) && (lvl2 % 60 == 0)){
                                MapleMonster scar = MapleLifeFactory.getMonster(9400596);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(scar, spwanpos.get(posi));
                                score2 += 2;
                            } else if ((lvl2 >= 600 && lvl2 < 900) && (lvl2 % 60 == 0)){
                                MapleMonster azur = MapleLifeFactory.getMonster(9400597);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(azur, spwanpos.get(posi));
                                score2 += 3;
                            } else if ((lvl2 >= 900 && lvl2 < 1200) && (lvl2 % 60 == 0)){
                                MapleMonster levi = MapleLifeFactory.getMonster(8220003);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(levi, spwanpos.get(posi));
                                score2 += 4;
                            } else if ((lvl2 >= 1200 && lvl2 < 1500) && (lvl2 % 60 == 0)){
                                MapleMonster bigf = MapleLifeFactory.getMonster(9400575);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(bigf, spwanpos.get(posi));
                                score2 += 5;
                            } else if ((lvl2 >= 1500 && lvl2 < 1800) && (lvl2 % 60 == 0)){
                                MapleMonster bcrow = MapleLifeFactory.getMonster(9400015);
                                pqmaps.get(2).spawnMonsterOnGroundBelow(bcrow, spwanpos.get(posi));
                                score2 += 6;
                            }
                        }
                        lvl2 += 1;
                    }
                }
            }
        }, 1000);
        spawnSchedule3 = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                
                List<Integer> posindx = new ArrayList<>();
                if (pqmaps.containsKey(3)){
                    if (!pqmaps.get(3).getAllPlayers().isEmpty() && !playerschan.get(3).isEmpty()){
                        List<MapleMonster> mobs = pqmaps.get(3).getAllMonsters();
                        if (!mobs.isEmpty()){
                            int spawcord[] = {-389,106,704};
                            int indx = 0;
                            while (indx<3){
                                boolean posfound = false;
                                for (MapleMonster mob : mobs){
                                    if ((spawcord[indx]-150) <= mob.getPosition().y && (spawcord[indx]+150) >= mob.getPosition().y){
                                        posfound = true;
                                        break;
                                    }
                                }
                                if (!posfound){
                                    posindx.add(indx);
                                }
                                indx += 1;
                            }
                        } else {
                            posindx.add(0);
                            posindx.add(1);
                            posindx.add(2);
                        }
                        for (int posi : posindx){
                            if ((lvl3 >= 0 && lvl3 < 300) && (lvl3 % 60 == 0)){
                                MapleMonster headless = MapleLifeFactory.getMonster(9400549);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(headless, spwanpos.get(posi));
                                score3 += 1;
                            } else if ((lvl3 >= 300 && lvl3 < 600) && (lvl3 % 60 == 0)){
                                MapleMonster scar = MapleLifeFactory.getMonster(9400596);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(scar, spwanpos.get(posi));
                                score3 += 2;
                            } else if ((lvl3 >= 600 && lvl3 < 900) && (lvl3 % 60 == 0)){
                                MapleMonster azur = MapleLifeFactory.getMonster(9400597);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(azur, spwanpos.get(posi));
                                score3 += 3;
                            } else if ((lvl3 >= 900 && lvl3 < 1200) && (lvl3 % 60 == 0)){
                                MapleMonster levi = MapleLifeFactory.getMonster(8220003);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(levi, spwanpos.get(posi));
                                score3 += 4;
                            } else if ((lvl3 >= 1200 && lvl3 < 1500) && (lvl3 % 60 == 0)){
                                MapleMonster bigf = MapleLifeFactory.getMonster(9400575);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(bigf, spwanpos.get(posi));
                                score3 += 5;
                            } else if ((lvl3 >= 1500 && lvl3 < 1800) && (lvl3 % 60 == 0)){
                                MapleMonster bcrow = MapleLifeFactory.getMonster(9400015);
                                pqmaps.get(3).spawnMonsterOnGroundBelow(bcrow, spwanpos.get(posi));
                                score3 += 6;
                            }
                        }
                        lvl3 += 1;
                    }
                }
            }
        }, 1000);
        checkpq = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                List<Channel> chans = Server.getInstance().getAllChannels();
                for (Channel ch : chans){
                    MapleMap pqmap = ch.getMapFactory().getMap(110);
                    List<MapleCharacter> mapplys = pqmap.getAllPlayers();
                    if (!mapplys.isEmpty()){
                        for (MapleCharacter maper : mapplys){
                            boolean intrude = false;
                            if(playerschan.isEmpty() || !pqContainsMapleCharacter(playerschan.get(ch.getId()),maper) || !maper.isPartyMember(maper.getId())){
                                maper.dropMessage("not allowed to stay on this map, without valid party!");
                                maper.changeMap(610030000);
                                intrude = true;
                            }
                            if(intrude){
                                continue;
                            }
                            if (!mapplys.isEmpty()){
                                if (mapplys.size() != 3){
                                    maper.dropMessage("A party memeber left the party, the pq has terminated before the end!");
                                    maper.announce(MaplePacketCreator.removeClock());
                                    maper.changeMap(610030000);
                                }
                            }
                        }
                    } else {
                        int chid = ch.getId();
                        if (chid == 1 && playerschan.containsKey(chid)){
                            playerschan.remove(chid);
                            pqch1.cancel(true);
                            pqmaps.get(chid).clearMapObjects();
                            pqmaps.remove(chid);
                        } else if (chid == 2 && playerschan.containsKey(chid)){
                            playerschan.remove(chid);
                            pqch2.cancel(true);
                            pqmaps.get(chid).clearMapObjects();
                            pqmaps.remove(chid);
                        } else if (chid == 3  && playerschan.containsKey(chid)){
                            playerschan.remove(chid);
                            pqch3.cancel(true);
                            pqmaps.get(chid).clearMapObjects();
                            pqmaps.remove(chid);
                        }
                    }
                }
            }
        }, 1000);
    }
    
    public void startPQ(MapleCharacter ptleader, int chanid){
        List<MapleCharacter> ptmems = ptleader.getPartyMembersOnSameMap();
        pqmaps.put(chanid, ptleader.getMap());
        playerschan.put(chanid, ptmems);
        if (chanid == 1){
            lvl1 = 0;
            for (MapleCharacter mem : ptmems){
                mem.announce(MaplePacketCreator.getClock(1800));
            }
            pqch1 = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    calcReward(playerschan.get(chanid), score1, playerschan.get(chanid).get(0));
                    for (MapleCharacter mem : playerschan.get(chanid)){
                        mem.changeMap(610030000);
                        mem.dropMessage("The pq has ended, thanks for playing!");
                    }
                    playerschan.get(chanid).clear();
                    System.out.println("Nightmare pq score: "+score1);
                    lvl1 = 0;
                    score1 = 0;
                }
            }, 1800000);
        } else if (chanid == 2){
            playerschan.replace(chanid, ptmems);
            lvl2 = 0;
            for (MapleCharacter mem : ptmems){
                mem.announce(MaplePacketCreator.getClock(1800));
            }
            pqch2 = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    calcReward(playerschan.get(chanid), score2, playerschan.get(chanid).get(0));
                    for (MapleCharacter mem : playerschan.get(chanid)){
                        mem.changeMap(610030000);
                        mem.dropMessage("The pq has ended, thanks for playing!");
                    }
                    playerschan.get(chanid).clear();
                    System.out.println("Nightmare pq score: "+score2);
                    lvl2 = 0;
                    score2 = 0;
                }
            }, 1800000);
        } else if (chanid == 3){
            playerschan.replace(chanid, ptmems);
            lvl3 = 0;
            for (MapleCharacter mem : ptmems){
                mem.announce(MaplePacketCreator.getClock(1800));
            }
            pqch3 = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    calcReward(playerschan.get(chanid), score3, playerschan.get(chanid).get(0));
                    for (MapleCharacter mem : playerschan.get(chanid)){
                        mem.changeMap(610030000);
                        mem.dropMessage("The pq has ended, thanks for playing!");
                    }
                    System.out.println("Nightmare pq score: "+score3);
                    playerschan.get(chanid).clear();
                    lvl3 = 0;
                    score3 = 0;
                }
            }, 1800000);
        }
    }
    public boolean pqContainsMapleCharacter(List<MapleCharacter> pqchars, MapleCharacter target){
        int tarid = target.getId();
        for (MapleCharacter pqers : pqchars){
            if (tarid == pqers.getId()){
                return true;
            }
        }
        return false;
    }
    
    public void calcReward(List<MapleCharacter> ptchars, int score, MapleCharacter ptmem){
        int exprate = ptmem.getClient().getWorldServer().getExpRate();
        float prop = (float)score/315;
        int maxallowed = 150000000;
        int total = (int)(Math.round(prop*50000000*exprate));
        if (total > maxallowed){
            total = maxallowed;
            System.out.println("[EXP Violation] possible bug or exp edit in Nightmare PQ. win ratio: "+Float.toString(prop)+".");
        }
        for (MapleCharacter chr : ptchars){
            chr.gainExp(total, true, true);
        }
    }
    
}
