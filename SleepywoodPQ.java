/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Item;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import net.server.channel.Channel;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.DatabaseConnection;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class SleepywoodPQ {
    private static List<MapleCharacter> chosench1  = new ArrayList<>();
    private static List<MapleCharacter> readych1 = new ArrayList<>();
    private static List<MapleCharacter> chosench2  = new ArrayList<>();
    private static List<MapleCharacter> readych2 = new ArrayList<>();
    private static List<MapleCharacter> chosench3 = new ArrayList<>();
    private static List<MapleCharacter> readych3 = new ArrayList<>();
    private static boolean ch1st = false;
    private static MapleCharacter remain1 = new MapleCharacter();
    private static MapleCharacter remain2 = new MapleCharacter();
    private static MapleCharacter remain3 = new MapleCharacter();
    private static boolean ch2st = false;
    private static boolean ch3st = false;
    private static Map<Integer, Integer> amount = new HashMap<>();
    private static Map<Integer, Integer> level = new HashMap<>();
    private ScheduledFuture<?> timecheckStart = null;
    private ScheduledFuture<?> timeoutSchedule = null;
    private ScheduledFuture<?> timextraSchedule = null;
    private ScheduledFuture<?> pqmain = null;
    private ScheduledFuture<?> spawnmob = null;
    private ScheduledFuture<?> checkonmap = null;
    private List<ScheduledFuture<?>> events = new ArrayList<ScheduledFuture<?>>();
    private static Map<Integer, List<MapleCharacter>> mapprop = new HashMap<>();
    private static Map<Integer, MapleMap> pqmapsdic = new HashMap<>();
    private static Map<Integer, Integer> points = new HashMap<>();
    List<Integer> codes = new ArrayList<Integer>();
    List<Integer> freemaps1 = new ArrayList<Integer>();
    List<Integer> freemaps2 = new ArrayList<Integer>();
    List<Integer> freemaps3 = new ArrayList<Integer>();

    public void coordinateSWPQ(){
        pqmain = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                int indx = 0;
                /*System.out.println(level);
                System.out.println(codes);
                System.out.println(pqmapsdic);
                System.out.println(mapprop);*/
                List<Integer> remindx = new ArrayList<Integer>();
                for (int code : codes){
                    level.replace(code, level.get(code)+1);
                    if (level.get(code) == 1780){
                        mapprop.get(code).get(0).dropMessage("Sleepywood PQ is about to end, make sure you have free ETC space for the reward!");
                        mapprop.get(code).get(1).dropMessage("Sleepywood PQ is about to end, make sure you have free ETC space for the reward!");
                    }
                    if (level.get(code) >= 1812){
                        mapprop.get(code).get(0).announce(MaplePacketCreator.removeClock());
                        mapprop.get(code).get(1).announce(MaplePacketCreator.removeClock());
                        calcReward(mapprop.get(code).get(0), mapprop.get(code).get(1), code);
                        mapprop.get(code).get(0).changeMap(105040301);
                        mapprop.get(code).get(1).changeMap(105040301);
                        mapprop.remove(code);
                        pqmapsdic.get(code).clearMapObjects();
                        level.remove(code);
                        pqmapsdic.remove(code);
                        remindx.add(code);
                        
                    }
                    indx += 1;
                }
                if (!remindx.isEmpty()){
                    for (int i : remindx){
                        codes.remove(codes.indexOf(i));
                    }
                    remindx.clear();
                }
            }
        }, 1000);
        spawnmob = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                for (int code : codes){
                    MapleMap pqmap =  pqmapsdic.get(code);
                    Point p1 = new Point(-500, -45);
                    Point p2 = new Point(-500, 235);
                    Point p3 = new Point(460, -45);
                    Point p4 = new Point(460, 235);
                    List<MapleCharacter> mapplys = pqmap.getAllPlayers();
                    List<MapleMonster> monlist = pqmap.getAllMonsters(); 
                    MapleCharacter chrl = new MapleCharacter();
                    MapleCharacter chrr = new MapleCharacter();       
                    int insum = 10;
                    int calcl = 0;
                    int calcr = 0;
                    int halfl = 0;
                    int halfr = 0;
                    for (MapleCharacter chr : mapplys){
                        if (chr.getPosition().getX() < -20) {
                            chrl = chr;
                        } else {
                            chrr = chr;
                        }
                    }
                    if (monlist != null){
                    }
                    for (MapleMonster moba : monlist){
                        if (moba.getBelongsTo() == chrl.getId()){
                            calcl += 1;
                        } else {
                            calcr += 1;
                        }
                    }
                    int suml = insum - calcl;
                    int sumr = insum - calcr;
                    int multi = (level.get(code)/300) + 1;
                    if (points.containsKey(chrl.getId())){
                        points.replace(chrl.getId(), points.get(chrl.getId())+(suml*multi));
                    }
                    if (points.containsKey(chrr.getId())){
                        points.replace(chrr.getId(), points.get(chrr.getId())+(sumr*multi));
                    }
                    System.out.println(points);
                    if (suml > 0){
                        halfl = suml/2;
                    }
                    if (sumr > 0){
                        halfr = sumr/2;
                    }

                    while (suml > 0){
                        MapleMonster sliml = MapleLifeFactory.getMonster(9420510);
                        MapleMonster Windl = MapleLifeFactory.getMonster(9400576);
                        MapleMonster klockl = MapleLifeFactory.getMonster(8140300);
                        MapleMonster spiderl = MapleLifeFactory.getMonster(9400545);
                        MapleMonster kental = MapleLifeFactory.getMonster(9300261);
                        MapleMonster gallol = MapleLifeFactory.getMonster(9420540);
                        sliml.setBelongTo(chrl);
                        Windl.setBelongTo(chrl);
                        klockl.setBelongTo(chrl);
                        spiderl.setBelongTo(chrl);
                        kental.setBelongTo(chrl);
                        gallol.setBelongTo(chrl);
                        if ((level.get(code) >= 10) && (level.get(code) < 310)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(sliml, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(sliml, p2);
                                    suml -= 1;
                                }
                        } else if ((level.get(code) >= 310) && (level.get(code) < 610)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(Windl, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(Windl, p2);
                                    suml -= 1;
                                }
                        } else if ((level.get(code) >= 610) && (level.get(code) < 910)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(klockl, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(klockl, p2);
                                    suml -= 1;
                                }
                        } else if ((level.get(code) >= 910) && (level.get(code) < 1210)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(spiderl, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(spiderl, p2);
                                    suml -= 1;
                                }
                        } else if ((level.get(code) >= 1210) && (level.get(code) < 1510)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(kental, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(kental, p2);
                                    suml -= 1;
                                }
                        } else if ((level.get(code) >= 1510) && (level.get(code) < 1810)){
                                if (suml > halfl){
                                    pqmap.spawnMonsterOnGroundBelow(gallol, p1);
                                    suml -= 1;
                                } else {
                                    pqmap.spawnMonsterOnGroundBelow(gallol, p2);
                                    suml -= 1;
                                }
                        }
                }
                while (sumr > 0){
                    MapleMonster slimr = MapleLifeFactory.getMonster(9420510);
                    MapleMonster Windr = MapleLifeFactory.getMonster(9400576);
                    MapleMonster klockr = MapleLifeFactory.getMonster(8140300);
                    MapleMonster spiderr = MapleLifeFactory.getMonster(9400545);
                    MapleMonster kentar = MapleLifeFactory.getMonster(9300261);
                    MapleMonster gallor = MapleLifeFactory.getMonster(9420540);
                    slimr.setBelongTo(chrr);
                    Windr.setBelongTo(chrr);
                    klockr.setBelongTo(chrr);
                    spiderr.setBelongTo(chrr);
                    kentar.setBelongTo(chrr);
                    gallor.setBelongTo(chrr);
                    if ((level.get(code) >= 10) && (level.get(code) < 310)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(slimr, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(slimr, p4);
                                sumr -= 1;
                            }
                    } else if ((level.get(code) >= 310) && (level.get(code) < 610)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(Windr, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(Windr, p4);
                                sumr -= 1;
                            }
                    } else if ((level.get(code) >= 610) && (level.get(code) < 910)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(klockr, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(klockr, p4);
                                sumr -= 1;
                            }
                    } else if ((level.get(code) >= 910) && (level.get(code) < 1210)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(spiderr, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(spiderr, p4);
                                sumr -= 1;
                            }
                    } else if ((level.get(code) >= 1210) && (level.get(code) < 1510)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(kentar, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(kentar, p4);
                                sumr -= 1;
                            }
                    } else if ((level.get(code) >= 1510) && (level.get(code) < 1810)){
                            if (sumr > halfr){
                                pqmap.spawnMonsterOnGroundBelow(gallor, p3);
                                sumr -= 1;
                            } else {
                                pqmap.spawnMonsterOnGroundBelow(gallor, p4);
                                sumr -= 1;
                            }
                    
                        }
                    }
                }
            }
        }, 10000);
        checkonmap = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                int indx = 0;
                List<Integer> remindx = new ArrayList<Integer>();
                List<MapleCharacter> left = new ArrayList<MapleCharacter>();
                for (int code : codes){
                    MapleMap pqmap =  pqmapsdic.get(code);
                    left = pqmap.getAllPlayers();
                    if (!left.isEmpty()){
                        if (left.size() != 2){ //need to add if size is empty
                            for (MapleCharacter rem : left){
                                rem.announce(MaplePacketCreator.removeClock());
                                int exprate = rem.getClient().getWorldServer().getExpRate();
                                int point = points.get(rem.getId());
                                float prop = (float)point/18900;
                                int maxallowed = 14400000;
                                int total = (int)(Math.round(prop*2000000*exprate));
                                if (total>maxallowed){
                                    total = maxallowed;
                                    System.out.println("[EXP Violation] possible bug or exp edit on Sleepywood PQ. win ratio: "+Float.toString(prop)+".");
                                }
                                rem.gainExp(total, true, true);
                                rem.dropMessage("Your opponent left the map and therefore the PQ is cancelled!");
                                rem.changeMap(105040301);
                            }
                            points.remove(mapprop.get(code).get(0).getId());
                            points.remove(mapprop.get(code).get(1).getId());
                            mapprop.remove(code);
                            pqmapsdic.get(code).clearMapObjects();
                            level.remove(code);
                            pqmapsdic.remove(code);
                            remindx.add(code);
                        }
                    } else {
                        points.remove(mapprop.get(code).get(0).getId());
                        points.remove(mapprop.get(code).get(1).getId());
                        mapprop.remove(code);
                        pqmapsdic.get(code).clearMapObjects();
                        level.remove(code);
                        pqmapsdic.remove(code);
                        remindx.add(code);
                    }
                    indx += 1;
                }
                if (!remindx.isEmpty()){
                    for (int i : remindx){
                        codes.remove(codes.indexOf(i));
                    }
                    remindx.clear();
                }
            }
        }, 2000);
    
        timecheckStart = TimerManager.getInstance().register(new Runnable() {
        public void run() {
            boolean onpqmap2 = false;
            boolean onpqmap3 = false;
            boolean onpqmap4 = false;
            for (Channel chan : Server.getInstance().getAllChannels()){
                int chid = chan.getId();
                amount.put(chid, 6);
                Collection<MapleCharacter> players = chan.getPlayerStorage().getAllCharacters();
                for (MapleCharacter chr : players){
                    if (chr.getMapId() == 102){
                        onpqmap2 = true;
                        amount.replace(chid, amount.get(chid)-2);
                    } else if (chr.getMapId() == 103){
                        onpqmap3 = true;
                        amount.replace(chid, amount.get(chid)-2);
                    } else if (chr.getMapId() == 104){
                        onpqmap4 = true;
                        amount.replace(chid, amount.get(chid)-2);
                    }
                }
                if (amount.get(chid) != 0){
                    if (chid == 1){
                        freemaps1.clear();
                        if(!onpqmap2){
                            freemaps1.add(102);
                        }
                        if(!onpqmap3){
                            freemaps1.add(103);
                        }
                        if(!onpqmap4){
                            freemaps1.add(104);
                        }
                    } else if(chid == 2){
                        freemaps2.clear();
                        if(!onpqmap2){
                            freemaps2.add(102);
                        }
                        if(!onpqmap3){
                            freemaps2.add(103);
                        }
                        if(!onpqmap4){
                            freemaps2.add(104);
                        }
                    } else if(chid == 3){
                        freemaps3.clear();
                        if(!onpqmap2){
                            freemaps3.add(102);
                        }
                        if(!onpqmap3){
                            freemaps3.add(103);
                        }
                        if(!onpqmap4){
                            freemaps3.add(104);
                        }
                    }
                    int playersOnMap = 0;
                    List<MapleCharacter> duomates = new ArrayList<>();
                    List<MapleCharacter> toremove = new ArrayList<>();
                    for (MapleCharacter remchr : chosench1){         //removing players that has reserved place and cc
                        if (remchr.getClient().getChannel() != 1){
                            toremove.add(remchr);
                        }
                    }
                    if (!toremove.isEmpty()){
                        for (MapleCharacter chr : toremove){
                            chosench1.remove(chosench1.indexOf(chr));
                        }
                        toremove.clear();
                    }
                    for (MapleCharacter remchr : chosench2){
                        if (remchr.getClient().getChannel() != 2){
                            toremove.add(remchr);
                        }
                    }
                    if (!toremove.isEmpty()){
                        for (MapleCharacter chr : toremove){
                            chosench2.remove(chosench2.indexOf(chr));
                        }
                        toremove.clear();
                    }
                    for (MapleCharacter remchr : chosench3){
                        if (remchr.getClient().getChannel() != 3){
                            toremove.add(remchr);
                        }
                    }
                    if (!toremove.isEmpty()){
                        for (MapleCharacter chr : toremove){
                            chosench3.remove(chosench3.indexOf(chr));
                        }
                        toremove.clear();
                    }
                    if (chid == 1){
                        if (!chosench1.isEmpty()){
                            duomates.addAll(choosePlayers(chan, amount.get(chid)-chosench1.size()));
                        } else {
                             duomates.addAll(choosePlayers(chan, amount.get(chid)));
                        }
                    } else if (chid == 2){
                        if (!chosench2.isEmpty()){
                            duomates.addAll(choosePlayers(chan, amount.get(chid)-chosench2.size()));
                        } else {
                             duomates.addAll(choosePlayers(chan, amount.get(chid)));
                        }
                    } else if (chid == 3){
                        if (!chosench3.isEmpty()){
                            duomates.addAll(choosePlayers(chan, amount.get(chid)-chosench3.size()));
                        } else {
                             duomates.addAll(choosePlayers(chan, amount.get(chid)));
                        }
                    }
                    amount.clear();
                    if (duomates.size() > 1){
                        for(MapleCharacter cand : duomates){
                            if (cand.getMapId() == 105040301){
                                playersOnMap += 1;
                            }
                        }
                        if (playersOnMap > 1){
                            for(MapleCharacter cand : duomates){
                                cand.announce(MaplePacketCreator.getClock(30));
                                cand.dropMessage("You have been chosen to compete in the next duo, please confirm with Battle Statue.");
                                switch (chid) {
                                    case 1:
                                        chosench1.add(cand);
                                        break;
                                    case 2:
                                        chosench2.add(cand);
                                        break;
                                    case 3:
                                        chosench3.add(cand);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    }
                }
                onpqmap2 = false;
                onpqmap3 = false;
                onpqmap4 = false;
            }
                            
            timeoutSchedule = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    int k = 0;
                    remain1 = null;
                    remain2 = null;
                    remain3 = null;
                        if (!readych1.isEmpty()){
                            System.out.println(readych1);
                            if (readych1.size() <= chosench1.size()){ // check if those who accepted still on the same channel and map
                                for(MapleCharacter cand : readych1){
                                    if(cand.getMapId() != 105040301 || cand.getClient().getChannel() != 1 || !cand.isLoggedin()){
                                       readych1.remove(k);
                                    } else {
                                        cand.announce(MaplePacketCreator.removeClock());
                                    }
                                    k += 1;
                                }
                                if (!readych1.isEmpty()){
                                    int j = 0;                         //for each map choose 2 players capped by max players accepted the challenge
                                    int recr = (readych1.size()/2)*2;
                                    if (recr > 0){
                                        ch1st = true;
                                    }
                                    List<MapleCharacter> senduo = new ArrayList<>();
                                    for (int mapid : freemaps1){
                                        int i = 0;
                                        while ((i < 2) && (j <= (recr-1)) && (recr != 0)){
                                            if (i == 0){
                                                readych1.get(j).changeMap(mapid, "portalleft");
                                            } else {
                                                readych1.get(j).changeMap(mapid, "portalright");
                                            }
                                            readych1.get(j).dropMessage("The Duel has begun! please clear some ETC space for the reward before finishing!");
                                            if ((j+1) % 2 == 0){
                                                senduo.add(readych1.get(j));
                                                senduo.add(readych1.get(j-1));
                                                startSWPQ(senduo);
                                                senduo.clear();
                                            }
                                            i += 1;
                                            j += 1;
                                        }
                                    }
                                    List<MapleCharacter> rmv = new ArrayList<>(); //remove players that didnt accept the duo from WL
                                    for(MapleCharacter expl : chosench1){
                                        boolean found = false;
                                        for(MapleCharacter chr : readych1){
                                            if (expl.getId() == chr.getId()){
                                                found = true;
                                            }
                                        }
                                        if (!found){
                                            rmv.add(expl);
                                            deletePlayerFromWL(rmv);
                                            rmv.clear();
                                        }
                                    }
                                    if (readych1.size() % 2 == 1){ //get the remainder character
                                        remain1 = readych1.get(j);
                                        readych1.get(j).dropMessage("Other players choosen for the duo, your place is reserved for next one.");
                                        readych1.remove(j);
                                    } else {
                                        remain1 = null;
                                    }
                                } else {
                                    deletePlayerFromWL(chosench1);
                                }
                            }
                        } else {
                            deletePlayerFromWL(chosench1);
                        }
                        if (!readych2.isEmpty()){
                            if (readych2.size() <= chosench2.size()){ // check if those who accepted still on the same channel and map
                                for(MapleCharacter cand : readych2){
                                    if(cand.getMapId() != 105040301 || cand.getClient().getChannel() != 2 || !cand.isLoggedin()){
                                       readych2.remove(k);
                                    } else {
                                        cand.announce(MaplePacketCreator.removeClock());
                                    }
                                    k += 1;
                                }
                                if (!readych2.isEmpty()){
                                    int j = 0;                         //for each map choose 2 players capped by max players accepted the challenge
                                    int recr = (readych2.size()/2)*2;
                                    if (recr > 0){
                                        ch2st = true;
                                    }
                                    List<MapleCharacter> senduo = new ArrayList<>();
                                    for (int mapid : freemaps2){
                                        int i = 0;
                                        while ((i < 2) && (j <= (recr-1)) && (recr != 0)){
                                            if (i == 0){
                                                readych2.get(j).changeMap(mapid, "portalleft");
                                            } else {
                                                readych2.get(j).changeMap(mapid, "portalright");
                                            }
                                            readych2.get(j).dropMessage("The Duel has begun! please clear some ETC space for the reward before finishing!");
                                            if ((j+1) % 2 == 0){
                                                senduo.add(readych2.get(j));
                                                senduo.add(readych2.get(j-1));
                                                startSWPQ(senduo);
                                                senduo.clear();
                                            }
                                            i += 1;
                                            j += 1;
                                        }
                                    }
                                    List<MapleCharacter> rmv = new ArrayList<>(); //remove players that didnt accept the duo
                                    for(MapleCharacter expl : chosench2){
                                        boolean found = false;
                                        for(MapleCharacter chr : readych2){
                                            if (expl.getId() == chr.getId()){
                                                found = true;
                                            }
                                        }
                                        if (!found){
                                            rmv.add(expl);
                                            deletePlayerFromWL(rmv);
                                            rmv.clear();
                                        }
                                    }
                                    if (readych2.size() % 2 == 1){ //get the remainder character
                                        remain2 = readych2.get(j);
                                        readych2.get(j).dropMessage("Other players choosen for the duo, your place is reserved for next one.");
                                        readych2.remove(j);
                                    } else {
                                        remain2 = null;
                                    }
                                } else {
                                    deletePlayerFromWL(chosench2);
                                }
                            }
                        } else {
                            deletePlayerFromWL(chosench2);
                        }
                        if (!readych3.isEmpty()){
                            if (readych3.size() <= chosench3.size()){ // check if those who accepted still on the same channel and map
                                for(MapleCharacter cand : readych3){
                                    if(cand.getMapId() != 105040301 || cand.getClient().getChannel() != 3 || !cand.isLoggedin()){
                                       readych3.remove(k);
                                    } else {
                                        cand.announce(MaplePacketCreator.removeClock());
                                    }
                                    k += 1;
                                }
                                if (!readych3.isEmpty()){
                                    int j = 0;                         //for each map choose 2 players capped by max players accepted the challenge
                                    int recr = (readych3.size()/2)*2;
                                    if (recr > 0){
                                        ch3st = true;
                                    }
                                    List<MapleCharacter> senduo = new ArrayList<>();
                                    for (int mapid : freemaps3){
                                        int i = 0;
                                        while ((i < 2) && (j <= (recr-1)) && (recr != 0)){
                                            if (i == 0){
                                                readych3.get(j).changeMap(mapid, "portalleft");
                                            } else {
                                                readych3.get(j).changeMap(mapid, "portalright");
                                            }
                                            readych3.get(j).dropMessage("The Duel has begun! please clear some ETC space for the reward before finishing!");
                                            if ((j+1) % 2 == 0){
                                                senduo.add(readych3.get(j));
                                                senduo.add(readych3.get(j-1));
                                                startSWPQ(senduo);
                                                senduo.clear();
                                            }
                                            i += 1;
                                            j += 1;
                                        }
                                    }
                                    List<MapleCharacter> rmv = new ArrayList<>(); //remove players that didnt accept the duo
                                    for(MapleCharacter expl : chosench3){
                                        boolean found = false;
                                        for(MapleCharacter chr : readych3){
                                            if (expl.getId() == chr.getId()){
                                                found = true;
                                            }
                                        }
                                        if (!found){
                                            rmv.add(expl);
                                            deletePlayerFromWL(rmv);
                                            rmv.clear();
                                        }
                                    }
                                    if (readych3.size() % 2 == 1){ //get the remainder character
                                        remain3 = readych3.get(j);
                                        readych3.get(j).dropMessage("Other players choosen for the duo, your place is reserved for next one.");
                                        readych3.remove(j);
                                    } else {
                                        remain3 = null;
                                    }
                                } else {
                                    deletePlayerFromWL(chosench3);
                                }
                            }
                        } else {
                            deletePlayerFromWL(chosench3);
                        }
                timextraSchedule = TimerManager.getInstance().schedule(new Runnable() {
                    public void run() {
                        if (ch1st){
                            deletePlayerFromWL(readych1);
                            chosench1.clear();
                            readych1.clear();
                            if (remain1 != null){
                                chosench1.add(remain1);
                            }
                        } else {
                            readych1.clear();
                            for (MapleCharacter chr: chosench1){
                                chr.dropMessage("PQ failed to start, because one or both players aren't ready");
                            }
                            chosench1.clear();
                        }
                        ch1st = false;
                        if (ch2st){
                            deletePlayerFromWL(readych2);
                            chosench2.clear();
                            readych2.clear();
                            if (remain2 != null){
                                chosench2.add(remain2);
                            }
                        } else {
                            readych2.clear();
                            for (MapleCharacter chr: chosench2){
                                chr.dropMessage("PQ failed to start, because one or both players aren't ready");
                            }
                            chosench2.clear();
                        }
                        ch2st = false;
                        if (ch3st){
                            deletePlayerFromWL(readych3);
                            chosench3.clear();
                            readych3.clear();
                            if (remain3 != null){
                                chosench3.add(remain3);
                            }
                        } else {
                            readych3.clear();
                            for (MapleCharacter chr: chosench3){
                                chr.dropMessage("PQ failed to start, because one or both players aren't ready");
                            }
                            chosench3.clear();
                        }
                        ch3st = false;
                        }
                    }, 2);
                }
                }, 30000);
            }
        }, 60000);
        }
    //Slimy - 9420510, Windraider - 9400576, dark klock - 8140300, W.S - 9400545, black kenta - 9300261, gallo - 9420540
    public void startSWPQ(List<MapleCharacter> duos){
        int code = (duos.get(0).getClient().getChannel()*10)+(duos.get(0).getMapId()-100);
        List<MapleCharacter> pqplys = new ArrayList<MapleCharacter>();
        List<MapleMap> pqmaps = new ArrayList<MapleMap>();
        boolean found = false;
        for (int i : codes){
            if(i == code){
                found = true;
            }
        }
        if (!found){
            codes.add(code);
        }
        level.put(code, 0);
        for (MapleCharacter chr : duos){
                chr.announce(MaplePacketCreator.getClock(1810));
                pqplys.add(chr);
                points.put(chr.getId(), 0);
        }
        if (!pqplys.isEmpty()){
            mapprop.put(code, pqplys);
        }
        MapleCharacter player1 = duos.get(0);
        MapleMap pqmap = player1.getMap();
        pqmapsdic.put(code, pqmap);
    }
    
    
    public boolean checkPQmapEmpty(MapleClient client){
        List<Integer> occmaps = new ArrayList<Integer>();
        for (MapleCharacter chplayer : client.getChannelServer().getPlayerStorage().getAllCharacters()){
            int mapid = chplayer.getMapId();
            if (mapid == 102 || mapid == 103 || mapid == 104){
                 if (!occmaps.contains(mapid)){
                     occmaps.add(mapid);
                 }
            }
        }
        if (!occmaps.isEmpty()){
            if(occmaps.size() != 3){
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
    public boolean registerPlayerForWL(MapleCharacter player, int mode){
        int chn = 0;
        int cid = 0;
        if (mode == 0){
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM swpqwaitlist")) {
                    try (ResultSet rs = ps.executeQuery()) {
                            while(rs.next()){
                                cid = rs.getInt("chrid");
                                chn = rs.getInt("chan");
                                if (player.getId() == cid){
                                    if (player.getClient().getChannel() == chn){
                                        return true;
                                    } else {
                                        List<MapleCharacter> swtch = new ArrayList<>();
                                        swtch.add(player);
                                        deletePlayerFromWL(swtch);
                                        registerPlayerForWL(player, 1);
                                        return true;
                                    }
                                }
                            }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                     e.printStackTrace();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false; 
        } else {
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("INSERT INTO swpqwaitlist (chrid, chan) VALUES (?,?)")) {
                        ps.setInt(1, player.getId());
                        ps.setInt(2, player.getClient().getChannel());
                        ps.executeUpdate();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }
    }
    public List<MapleCharacter> choosePlayers(Channel chnl,int num){
         List<MapleCharacter> duomates = new ArrayList<MapleCharacter>();
         List<MapleCharacter> removeaway = new ArrayList<MapleCharacter>();
         int memcount = 0;
         int chid;
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("SELECT * FROM swpqwaitlist WHERE chan = ?")) {
                    ps.setInt(1, chnl.getId());
                    try (ResultSet rs = ps.executeQuery()) {
                            while(rs.next()){
                                if (memcount != num){
                                    chid = rs.getInt("chrid");
                                    MapleCharacter cand = chnl.getPlayerStorage().getCharacterById(chid);
                                    if (cand != null){
                                        if (cand.getMapId() == 105040301){
                                            duomates.add(cand);
                                            memcount += 1;
                                        } else {
                                            removeaway.add(cand);
                                            deletePlayerFromWL(removeaway);
                                            removeaway.clear();
                                        }
                                    }
                                } else {
                                    break;
                                }
                            }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                     e.printStackTrace();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        int duosize = duomates.size();
        if (duosize == 1){
            duomates.clear();
        }
        return duomates;
    }
    public void deletePlayerFromWL(List<MapleCharacter> chrdel){
        for (MapleCharacter chr:chrdel){
            try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("DELETE FROM swpqwaitlist WHERE chrid = ?")) {
                        ps.setInt(1, chr.getId());
                        ps.execute();

                } catch (SQLException e) {
                    e.printStackTrace();
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public boolean isCandidate(int chan,int chrid){
        List<MapleCharacter> change = new ArrayList<MapleCharacter>(); 
        boolean remain = true;
        switch (chan) {
            case 1:
                if (chosench1 != null){
                    if (remain1 != null){
                        if (remain1.getId() == chrid){
                            remain = false;
                            change.add(remain1);
                            for (MapleCharacter exch : readych1){
                                if (exch.getId() != chrid){
                                    change.add(exch);
                                }
                            }
                            readych1.clear();
                            for (MapleCharacter ply : change){
                                readych1.add(ply);
                            }
                            change.clear();
                        }
                    }
                    if (remain){
                        for (MapleCharacter chr : chosench1){
                            if (chr.getId() == chrid){
                                if (readych1 != null){
                                    for (MapleCharacter cand : readych1){
                                        if (cand.getId() == chrid){
                                            return true;
                                        }
                                    }
                                }
                                readych1.add(chr);
                                return true;
                            }
                        }
                    }
                }
                break;
            case 2:
                if (chosench2 != null){
                    if (remain2 != null){
                        if (remain2.getId() == chrid){
                            remain = false;
                            change.add(remain2);
                            for (MapleCharacter exch : readych2){
                                if (exch.getId() != chrid){
                                    change.add(exch);
                                }
                            }
                            readych2.clear();
                            for (MapleCharacter ply : change){
                                readych2.add(ply);
                            }
                            change.clear();
                        }
                    }
                    if (remain){
                        for (MapleCharacter chr : chosench2){
                            if (chr.getId() == chrid){
                                if (readych2 != null){
                                    for (MapleCharacter cand : readych2){
                                        if (cand.getId() == chrid){
                                            return true;
                                        }
                                    }
                                }
                                readych2.add(chr);
                                return true;
                            }
                        }
                    }
                }
                break;
            case 3:
                if (chosench3 != null){
                    if (remain3 != null){
                        if (remain3.getId() == chrid){
                            remain = false;
                            change.add(remain3);
                            for (MapleCharacter exch : readych3){
                                if (exch.getId() != chrid){
                                    change.add(exch);
                                }
                            }
                            readych3.clear();
                            for (MapleCharacter ply : change){
                                readych3.add(ply);
                            }
                            change.clear();
                        }
                    } 
                    if (remain){
                        for (MapleCharacter chr : chosench3){
                            if (chr.getId() == chrid){
                                if (readych3 != null){
                                    for (MapleCharacter cand : readych3){
                                        if (cand.getId() == chrid){
                                            return true;
                                        }
                                    }
                                }
                                readych3.add(chr);
                                return true;
                            }
                        }
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }
    public void calcReward(MapleCharacter chr1, MapleCharacter chr2, int code){
        boolean noreward = false;
        int exprate = chr1.getClient().getWorldServer().getExpRate();
        String ip1 = chr1.getClient().getSession().getRemoteAddress().toString().split(":")[0];
        String ip2 = chr2.getClient().getSession().getRemoteAddress().toString().split(":")[0];
        int point1 = points.get(chr1.getId());
        int point2 = points.get(chr2.getId());
        if (ip1.equals(ip2)){
            point1 = (point1+point2)/3;
            point2 = (point1+point2)/3;
            noreward = true;
        }
        float prop1 = (float)point1/6300;
        float prop2 = (float)point2/6300;
        if (prop1>prop2){
            prop1 += (0.2*prop2);
            chr1.dropMessage("congratulations on finishing the Sleepywood PQthe winner is: "+chr1.getName()+"!");
            chr2.dropMessage("congratulations on finishing the Sleepywood PQthe winner is: "+chr1.getName()+"!");
        } else {
            prop2 += (0.2*prop1);
            chr1.dropMessage("congratulations on finishing the Sleepywood PQthe winner is: "+chr2.getName()+"!");
            chr2.dropMessage("congratulations on finishing the Sleepywood PQthe winner is: "+chr2.getName()+"!");
        }
        int maxallowed = 14400000;
        int total1 = (int)(Math.round(prop1*2000000*exprate));
        int total2 = (int)(Math.round(prop2*2000000*exprate));
        if (total1>maxallowed){
            total1 = maxallowed;
            System.out.println("[EXP Violation] possible bug or exp edit on Sleepywood PQ. win ratio: "+Float.toString(prop1)+".");
        }
        if (total2>maxallowed){
            total2 = maxallowed;
            System.out.println("[EXP Violation] possible bug or exp edit on Sleepywood PQ. win ratio: "+Float.toString(prop2)+".");
        }
        chr1.gainExp(total1, true, true);
        chr2.gainExp(total2, true, true);
        if (!noreward){
            if (chr1.canHold(4001138)){
                chr1.getAbstractPlayerInteraction().gainItem(4001138, true);
            } else {
                chr1.dropMessage("Your inventory is full, you gained 5mil mesos instead");
                chr1.gainMeso(5000000, true);
            }
            if (chr2.canHold(4001138)){
                chr2.getAbstractPlayerInteraction().gainItem(4001138, true);
            } else {
                chr2.dropMessage("Your inventory is full you gained 5mil mesos instead");
                chr2.gainMeso(5000000, true);
            } 
        }
        points.remove(chr1.getId());
        points.remove(chr2.getId());
    }
    
}
