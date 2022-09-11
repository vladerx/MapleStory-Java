/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.server.channel.handlers;

import client.MapleCharacter;
import client.MapleClient;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import net.server.channel.Channel;
import net.server.world.MaplePartyCharacter;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import server.maps.MapleMapObject;
import tools.MaplePacketCreator;

/**
 *
 * @author fives
 */
public class LeafrePQ {
    
    private ScheduledFuture<?> pqmain = null;
    private static Map<Integer, Point> chChoosePos = new HashMap<>();
    private static List<Point> flowerpos = new ArrayList<Point>();
    private static int ch1time;
    private static int ch2time;
    private static int ch3time;
    private static Map<Integer, List<MapleCharacter>> playerschan = new HashMap<>();
    private static List<MapleCharacter> plysch1 = new ArrayList<>();
    private static List<MapleCharacter> plysch2 = new ArrayList<>();
    private static List<MapleCharacter> plysch3 = new ArrayList<>();
    private static ScheduledFuture<?> timeoutSchedule1 = null;
    private static ScheduledFuture<?> timeoutSchedule2 = null;
    private static ScheduledFuture<?> timeoutSchedule3 = null;
    
    public void coordinateleafrePQ(){
        flowerpos.add(new Point(-66,-228));//take x cord when y>0 or y<0 and add 40
        flowerpos.add(new Point(67,-228));
        flowerpos.add(new Point(200,-226));
        flowerpos.add(new Point(337,-227));
        flowerpos.add(new Point(471,-227));
        flowerpos.add(new Point(-525,267));
        flowerpos.add(new Point(-385,269));
        flowerpos.add(new Point(-130,269));
        flowerpos.add(new Point(-4,268));
        flowerpos.add(new Point(123,268));
        flowerpos.add(new Point(247,267));
        flowerpos.add(new Point(373,267));
        flowerpos.add(new Point(512,268));
        
        pqmain = TimerManager.getInstance().register(new Runnable() {
            public void run() {
                List<Channel> chans = Server.getInstance().getAllChannels();
                final MapleMonster PQBeetle = MapleLifeFactory.getMonster(7130005);
                //System.out.println(playerschan);
                for (Channel ch : chans){
                    MapleMap pqmap = ch.getMapFactory().getMap(106);
                    MapleMap pqmap1 = ch.getMapFactory().getMap(105);
                    MapleMap pqmap2 = ch.getMapFactory().getMap(107);
                    MapleMap pqmap3 = ch.getMapFactory().getMap(108);
                    MapleMap pqmap4 = ch.getMapFactory().getMap(109);
                    List<MapleMap> pqmaps = new ArrayList<>();
                    pqmaps.add(pqmap);
                    pqmaps.add(pqmap1);
                    pqmaps.add(pqmap2);
                    pqmaps.add(pqmap3);
                    pqmaps.add(pqmap4);
                    if (pqmap.getDroppedItemsCountById(4031349) == 1){
                        ScheduledFuture<?> timeoutSchedule = TimerManager.getInstance().schedule(new Runnable() {
                            public void run() {
                                List<MapleCharacter> mapmembs = pqmap.getAllPlayers();
                                for (MapleCharacter mem : mapmembs){
                                    if (mem.isPartyLeader()){
                                        Point leadpos = mem.getPosition();
                                        int chan = mem.getClient().getChannel();
                                        Point flrpos = chChoosePos.get(mem.getClient().getChannel());
                                        //System.out.println(leadpos);
                                        //System.out.println(flrpos);
                                        if (((leadpos.y > 0 && flrpos.y > 0)&&((flrpos.x+30>=leadpos.x)&&(flrpos.x-30<=leadpos.x)))|| ((leadpos.y < 0 && flrpos.y < 0)&&((flrpos.x+30>=leadpos.x)&&(flrpos.x-30<=leadpos.x)))){
                                            for (MapleCharacter memb : mapmembs){
                                                memb.getAbstractPlayerInteraction().gainItem(4031349,(short)-memb.getItemQuantity(4031349, false));
                                                memb.getAbstractPlayerInteraction().gainItem(4000304,(short)-memb.getItemQuantity(4000304, false));
                                                memb.changeMap(107);
                                                continueLeafPQClock(ch.getId(),mapmembs);
                                            }
                                            chChoosePos.remove(chan);
                                            spawnDarkCornians(mem.getClient());
                                        } else {
                                            pqmap.clearDrops();
                                            pqmap.clearItemDrops();
                                            pqmap.spawnMonsterOnGroundBelow(PQBeetle, leadpos);
                                        }
                                        break;
                                    }
                                }
                            }
                        }, 200);
                        
                    }
                    
                    boolean emptypq = true;
                    int chann = ch.getId();
                    List<MapleCharacter> plysonmap = new ArrayList<>();
                    for (MapleMap mappq : pqmaps){
                        List<MapleCharacter> pqmapers = mappq.getAllPlayers();
                        if (!pqmapers.isEmpty()){
                            emptypq = false;
                        }
                        for (MapleCharacter maper : pqmapers){
                            boolean intrude = false;
                            if(playerschan.isEmpty() || !playerschan.containsKey(chann) || !pqContainsMapleCharacter(playerschan.get(chann),maper) || !maper.isPartyMember(maper.getId())){
                                maper.dropMessage("not allowed to stay on this map, without valid party!");
                                maper.changeMap(70);
                                intrude = true;
                            }
                            if(intrude){
                                continue;
                            }
                            List<MaplePartyCharacter> mapptmembs = maper.getParty().getPartyMembers();
                            if (!mapptmembs.isEmpty()){
                                if (mapptmembs.size() != 3 && mappq.getId() != 109){
                                    maper.dropMessage("A party memeber left the party, the pq has terminated before the end!");
                                    maper.announce(MaplePacketCreator.removeClock());
                                    maper.changeMap(70);
                                }
                            }
                        }
                    }
                    if (emptypq){
                        if(!playerschan.isEmpty()){
                            if (chann == 1){
                                if (playerschan.containsKey(chann)){
                                    plysch1.clear();
                                    timeoutSchedule1.cancel(true);
                                }
                            } else if (chann == 2){
                                if (playerschan.containsKey(chann)){
                                    plysch2.clear();
                                    timeoutSchedule2.cancel(true);
                                }
                            } else if (chann == 3){
                                if (playerschan.containsKey(chann)){
                                    plysch3.clear();
                                    timeoutSchedule3.cancel(true);
                                }
                            }
                            playerschan.remove(chann);
                            
                        }
                    }
                }
            }
        }, 500);
    }
    
    public boolean checkLeafrePQmapEmpty(MapleClient client){
       for (MapleCharacter chplayer : client.getChannelServer().getPlayerStorage().getAllCharacters()){
            int plymap = chplayer.getMapId(); 
            if (plymap == 105 || plymap == 106 || plymap == 107 || plymap == 108 || plymap == 109){
                return false;
            }
        }
       return true;
   }
    public void choosePosPoints(int chan){
        Random rand = new Random();
        int pickPos = rand.nextInt(13);
        chChoosePos.put(chan, flowerpos.get(pickPos));

    }
    public void spawnDarkCornians(MapleClient client){
        MapleMap pqmap = client.getPlayer().getMap();
        final MapleMonster blueDcor = MapleLifeFactory.getMonster(8150203);
        final MapleMonster greenDcor = MapleLifeFactory.getMonster(8150204);
        final MapleMonster blackDcor = MapleLifeFactory.getMonster(8150205);
        Point pos1 = new Point(315,-167);
        Point pos2 = new Point(-189,-166);
        Point pos3 = new Point(217,264);
        pqmap.spawnMonsterOnGroundBelow(blueDcor, pos1);
        pqmap.spawnMonsterOnGroundBelow(greenDcor, pos2);
        pqmap.spawnMonsterOnGroundBelow(blackDcor, pos3);
        
    }
    public void checkEmptyCornians(MapleClient client){
        MapleMap pqmap = client.getPlayer().getMap();
        List<MapleMonster> pqmons = pqmap.getAllMonsters();
        boolean isdonepq = true;
        for (MapleMonster mob : pqmons){
            int mobid = mob.getId();
            if(mobid == 8150203 || mobid == 8150204 || mobid == 8150205){
                isdonepq = false;
            }
        }
        if (isdonepq){
            List<MapleCharacter> mapmembs = pqmap.getAllPlayers();
            for (MapleCharacter memb : mapmembs){
                memb.getAbstractPlayerInteraction().gainItem(4001208,(short)-memb.getItemQuantity(4001208, false));
                memb.getAbstractPlayerInteraction().gainItem(4001209,(short)-memb.getItemQuantity(4001209, false));
                memb.getAbstractPlayerInteraction().gainItem(4001210,(short)-memb.getItemQuantity(4001210, false));
                memb.changeMap(108);
                continueLeafPQClock(client.getChannel(),mapmembs);
            }
        }
    }
    public void setChanTime(int channel){
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        if (channel == 1){
            ch1time = timeNow;
        } else if(channel == 2){
            ch2time = timeNow;
        } else if(channel == 3){
            ch3time = timeNow;
        }
    }
    public boolean getClockOnReturn(MapleCharacter target){
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        int endtime = 0;
        int chan = target.getClient().getChannel();
        MaplePartyCharacter ptlead = target.getParty().getLeader();
        if(!playerschan.isEmpty()){
            if (playerschan.containsKey(chan)){
                if (pqContainsMapleCharacter(playerschan.get(chan),target)){
                    if (chan == 1){
                        endtime = 3600+ch1time;
                    } else if(chan == 2){
                        endtime = 3600+ch2time;
                    } else if(chan == 3){
                        endtime = 3600+ch3time;
                    }
                    target.changeMap(ptlead.getPlayer().getMapId());
                    target.announce(MaplePacketCreator.getClock(endtime-timeNow));
                    return true;
                }
            }
        }
        return false;
    }
    public void setPlayersPQ(int chan, List<MapleCharacter> ptplayers){
        if (chan == 1){
            for (MapleCharacter ptmem : ptplayers){
                plysch1.add(ptmem);
            }
            playerschan.put(chan, plysch1);
        } else if (chan == 2){
            for (MapleCharacter ptmem : ptplayers){
                plysch2.add(ptmem);
            }
            playerschan.put(chan, plysch2);
        } else if (chan == 3){
            for (MapleCharacter ptmem : ptplayers){
                plysch3.add(ptmem);
            }
            playerschan.put(chan, plysch3);
        }
    }
    
    public void setPQClock(MapleClient c){
              List<MapleCharacter> ptmembs = c.getPlayer().getPartyMembersOnSameMap();
              for (MapleCharacter mem : ptmembs){      
                  mem.dropMessage("Leafre PQ has begun, good luck and have fun!");
                  mem.announce(MaplePacketCreator.getClock(3600));
              }
              int chan = c.getChannel();
              setChanTime(chan);
              setPlayersPQ(chan, ptmembs);
              if (chan == 1) {
                timeoutSchedule1 = TimerManager.getInstance().schedule(new Runnable() {
                  public void run() {
                      List<MapleCharacter> ptmembs = playerschan.get(chan);
                      for (MapleCharacter mem : ptmembs){
                          mem.dropMessage("The time has run out, you were unable to finish Leafre PQ!");
                          mem.announce(MaplePacketCreator.removeClock());
                          mem.changeMap(70);
                      }
                      plysch1.clear();
                      playerschan.remove(1);
                      timeoutSchedule1.cancel(true);
                      
                  }
                }, 3600000);
              } else if (chan == 2){
                  timeoutSchedule2 = TimerManager.getInstance().schedule(new Runnable() {
                  public void run() {
                      List<MapleCharacter> ptmembs = playerschan.get(chan);
                      for (MapleCharacter mem : ptmembs){
                          mem.dropMessage("The time has run out, you were unable to finish Leafre PQ!");
                          mem.announce(MaplePacketCreator.removeClock());
                          mem.changeMap(70);
                      }
                      plysch2.clear();
                      playerschan.remove(2);
                      timeoutSchedule2.cancel(true);
                  }
                }, 3600000);
              } else if (chan == 3){
                  timeoutSchedule3 = TimerManager.getInstance().schedule(new Runnable() {
                  public void run() {
                      List<MapleCharacter> ptmembs = playerschan.get(chan);
                      for (MapleCharacter mem : ptmembs){
                          mem.dropMessage("The time has run out, you were unable to finish Leafre PQ!");
                          mem.announce(MaplePacketCreator.removeClock());
                          mem.changeMap(70);
                      }
                      plysch3.clear();
                      playerschan.remove(3);
                      timeoutSchedule3.cancel(true);
                  }
                }, 3600000);
              }
        }
    public void continueLeafPQClock(int chan, List<MapleCharacter> targets){
        Date currentDate = new Date();
        int timeNow = (int) (currentDate.getTime() / 1000);
        int endtime = 0;
        if (chan == 1){
            endtime = 3600+ch1time;
        } else if(chan == 2){
            endtime = 3600+ch2time;
        } else if(chan == 3){
            endtime = 3600+ch3time;
        }
        for (MapleCharacter target : targets){
            target.announce(MaplePacketCreator.getClock(endtime-timeNow));
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
    public void removeOnPQEnd(MapleCharacter ender, int chan){
        if(!playerschan.isEmpty()){
            if (playerschan.containsKey(chan)){
                if (pqContainsMapleCharacter(playerschan.get(chan),ender)){
                    if (chan == 1){
                        plysch1.remove(ender);
                    } else if(chan == 2){
                        plysch2.remove(ender);
                    } else if(chan == 3){
                        plysch3.remove(ender);
                    }
                }
            }
        }
    }
}
