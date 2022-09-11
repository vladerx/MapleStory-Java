/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import client.inventory.Equip;
import client.inventory.Item;
import java.awt.Point;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MapleParty;
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
public class ChempolisPQ {
    private ScheduledFuture<?> timeoutSchedule = null;
    private ScheduledFuture<?> timecheckSchedule = null;
    private ScheduledFuture<?> timewinnerSchedule = null;
    private int PQStatus = 0; //0 : going ; 1 : lost ; 2 : won
    private int spawnStatus = 0; 
    private int timeCounter = 0;
    
   public boolean checkPQmapEmpty(MapleClient client){
       for (MapleCharacter chplayer : client.getChannelServer().getPlayerStorage().getAllCharacters()){
            if (chplayer.getMapId() == 101){
                return false;
            }
        }
       return true;
   }
   public void startChemPQ(MapleCharacter player){
        List<MapleCharacter> ptmembs = player.getPartyMembersOnSameMap();
        final MapleMonster leviboss = MapleLifeFactory.getMonster(8220016);
        final MapleMonster bigfboss = MapleLifeFactory.getMonster(8220017);
        final MapleMonster bigfboss1 = MapleLifeFactory.getMonster(8220017);
        final MapleMonster bigfboss2 = MapleLifeFactory.getMonster(8220017);
        final MapleMonster hourseboss = MapleLifeFactory.getMonster(8220018);
        final MapleMonster hourseboss1 = MapleLifeFactory.getMonster(8220018);
        final MapleMonster hourseboss2 = MapleLifeFactory.getMonster(8220018);
        Random rand = new Random();
        Equip reward = new Equip(1002799, (short) 0, 10);
        short basicstats = 20;
        int matt = rand.nextInt(3);
        int basic = rand.nextInt(4);
        reward.setStr((short)(basicstats+basic));
        basic = rand.nextInt(4);
        reward.setDex((short)(basicstats+basic));
        basic = rand.nextInt(4);
        reward.setInt((short)(basicstats+basic));
        basic = rand.nextInt(4);
        reward.setLuk((short)(basicstats+basic));
        reward.setWatk((short)(5+matt));
        matt = rand.nextInt(3);
        reward.setMatk((short)(5+matt));
        basic = rand.nextInt(4);
        reward.setAcc((short)(25+basic));
        basic = rand.nextInt(4);
        reward.setAvoid((short)(25+basic));
        basic = rand.nextInt(10);
        reward.setHp((short)(100+basic));
        basic = rand.nextInt(10);
        reward.setWdef((short)(150+basic));
        basic = rand.nextInt(10);
        reward.setMdef((short)(150+basic));
        
        Item rewardcoin = new Item(4001137, (short) 0,(short) 1, -1);
        Point p = new Point(-27, 71);
        StringBuilder winners = new StringBuilder();
        MapleMap map =  player.getMap();
        map.spawnMonsterOnGroundBelow(leviboss, p);
        for (MapleCharacter mem : ptmembs){
            mem.dropMessage("Legendary Leviathan is now awaken, prepare to fight.");
            mem.announce(MaplePacketCreator.getClock(3600));
        }
        
        timeoutSchedule = TimerManager.getInstance().schedule(new Runnable() {
         public void run() {
             List<MapleCharacter> ptmembs = player.getPartyMembersOnSameMap();
             for (MapleCharacter mem : ptmembs){
                  mem.dropMessage("You failed to kill Legendary Leviathan at the given time.");
                  mem.announce(MaplePacketCreator.removeClock());
                  mem.changeMap(100);
            }
            timeoutSchedule.cancel(true);
            timecheckSchedule.cancel(true);
            map.killAllMonsters();
            PQStatus = 1;
         }
     }, 3600000);
 
        timecheckSchedule = TimerManager.getInstance().register(new Runnable() {
         public void run() {
            timeCounter += 5;
            List<MapleCharacter> mapmembs = map.getAllPlayers();
            List<MapleCharacter> mapptmembs = player.getPartyMembersOnSameMap();
            boolean memsdead = false;
            for (MapleCharacter mem : mapptmembs){
                    if (!mem.isAlive()){
                        memsdead = true;
                    }
            }
            if ((mapptmembs.size() != 3 && PQStatus == 0) || memsdead){
                for (MapleCharacter loser : mapmembs){
                    loser.dropMessage("You failed the Chempolis PQ beacuse a party member is dead, or left the PQ map or the party.");
                    loser.announce(MaplePacketCreator.removeClock());
                    loser.changeMap(100);
                }
                timeoutSchedule.cancel(true);
                timecheckSchedule.cancel(true);
                map.killAllMonsters();
                timeCounter = 0;
                PQStatus = 1;
            }
            if (leviboss.getHp() < 300000000 && leviboss.getHp() > 100000000 && spawnStatus == 0){
                map.spawnMonsterOnGroundBelow(hourseboss, leviboss.getPosition());
                map.spawnMonsterOnGroundBelow(hourseboss1, leviboss.getPosition());
                map.spawnMonsterOnGroundBelow(hourseboss2, leviboss.getPosition());
                spawnStatus = 1;
            } else if (leviboss.getHp() < 100000000 && spawnStatus == 1){
                map.spawnMonsterOnGroundBelow(bigfboss, leviboss.getPosition());
                map.spawnMonsterOnGroundBelow(bigfboss1, leviboss.getPosition());
                map.spawnMonsterOnGroundBelow(bigfboss2, leviboss.getPosition());
                spawnStatus = 2;
            } else if (map.getAllMonsters().isEmpty() && PQStatus == 0){
                PQStatus = 2;
                for (MapleCharacter mem : ptmembs){
                  winners.append(mem.getName()).append(" ,");
                  mem.announce(MaplePacketCreator.removeClock());
                  mem.announce(MaplePacketCreator.getClock(60));
                  mem.dropMessage("You have completed the party quest in time, congratulations!");
                  map.spawnItemDrop(leviboss, mem, reward, mem.getPosition(), true, true);
                  Point adjp = new Point(mem.getPosition().x + 30, mem.getPosition().y);
                  map.spawnItemDrop(leviboss, mem, rewardcoin, adjp, true, true);
                  setBestScore(timeCounter, mapptmembs);
                  setChannelCD(player);
                }
                timewinnerSchedule = TimerManager.getInstance().schedule(new Runnable() {
                public void run() {
                    for (MapleCharacter mapmem : mapmembs){
                         mapmem.announce(MaplePacketCreator.removeClock());
                         mapmem.changeMap(100);
                    }
                   Server.getInstance().broadcastMessage(player.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] Legendary Leviathan has been defeated by the crew of "+winners+"congratulations!"));
                   timeoutSchedule.cancel(true);
                   timecheckSchedule.cancel(true);
                   timewinnerSchedule.cancel(true);
                   timeCounter = 0;
                }
           }, 60000);
            }
         }
     }, 5000);

   }
   public void setBestScore(int time, List<MapleCharacter> ptymems){
       int bestt = -1;
       try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT bestime FROM chempqwinners")) {
                try (ResultSet rs = ps.executeQuery()) {
                        if(!rs.next()){
                        } else {
                            bestt = rs.getInt("bestime");
                        }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("noooo");
                }
            } catch (SQLException e) {
                 e.printStackTrace();
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       if (bestt == 0 || bestt > time){
            StringBuilder winers = new StringBuilder();
            for(MapleCharacter wnr : ptymems){
                winers.append(wnr.getName()).append(" ");
            }
           try {
                Connection con = DatabaseConnection.getConnection();
                try (PreparedStatement ps = con.prepareStatement("UPDATE chempqwinners SET bestime = ?, winners = ?")) {
                    ps.setInt(1, time);
                    ps.setString(2, winers.toString());
                    ps.executeUpdate();
                } catch (SQLException e) {
                     e.printStackTrace();
                 }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
       }
       
   }
   public String getBestScore(){
       int bestt = -1;
       String winners = "";
       try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT bestime, winners FROM chempqwinners")) {
                try (ResultSet rs = ps.executeQuery()) {
                        if(!rs.next()){
                        } else {
                            bestt = rs.getInt("bestime");
                            winners = rs.getString("winners");
                        }
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("noooo");
                }
            } catch (SQLException e) {
                 e.printStackTrace();
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       if (bestt == 0){
           return "#dBest Time: None\r\nScorers: None#k";
       } else {
           int besttmin = bestt/60;
           int besttsec = bestt % 60;
           return "#dBest Time: "+String.valueOf(besttmin)+" mins, "+String.valueOf(besttsec)+" secs\r\nScorers: "+winners+"#k";
       }
   }
   public void setChannelCD(MapleCharacter chr){
       Date currentDate = new Date();
       int timeNow = (int) (currentDate.getTime() / 1000);
       int chan = chr.getClient().getChannel();
       try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("UPDATE chempqcd SET cooldown = ? WHERE channel = ?")) {
                ps.setInt(1, timeNow+18000);
                ps.setInt(2, chan);
                ps.executeUpdate();
            } catch (SQLException e) {
                 e.printStackTrace();
             }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
   }
   public boolean isChannelOnCD(int chan){
       Date currentDate = new Date();
       int timeNow = (int) (currentDate.getTime() / 1000);
       int cdtime = -1;
       try {
            Connection con = DatabaseConnection.getConnection();
            try (PreparedStatement ps = con.prepareStatement("SELECT cooldown FROM chempqcd WHERE channel = ?")) {
                ps.setInt(1, chan);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) {
                    } else {
                        cdtime = rs.getInt("cooldown");
                        if (timeNow > cdtime){
                            return false;
                        } else {
                            return true;
                        }
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("noooo");
                }
            }
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
       return true;
   }

}
