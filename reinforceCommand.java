/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client.command.commands.gm3;

import client.MapleCharacter;
import client.MapleClient;
import client.command.Command;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledFuture;
import net.server.Server;
import net.server.channel.Channel;
import server.TimerManager;
import server.life.MapleLifeFactory;
import server.life.MapleMonster;
import server.maps.MapleMap;
import tools.MaplePacketCreator;
import static tools.Randomizer.rand;

/**
 *
 * @author fives
 */
public class reinforceCommand extends Command {
    private ScheduledFuture<?> spawnmobSchedule = null;
    private ScheduledFuture<?> changemapSchedule = null;
    private static boolean isEventOn = false;
    private static int place = -1;
    private static int timelimit = 0;
    private static MapleCharacter player;
    private static Channel hostch;
    private static MapleMap eventmap;
    private static List<Integer> maps = Arrays.asList(102000000,221000000,211000000,251000000,222000000,300000000,250000100,200000000,220000000,260000000,261000000,240000000);
    private static List<Integer> mapindx = new ArrayList<Integer>();
    {
        setDescription("");
    }
    @Override
    public void execute(MapleClient client, String[] params){
        player = client.getPlayer();
        System.out.println(client.getWorld());
        int seton = Integer.parseInt(params[0]);
        if (seton == 1){
            if (!isEventOn){
                if (params.length != 1) {
                        player.yellowMessage("Syntax: !reinforce [1/0]");
                        return;
                }
                player.yellowMessage("You have started the reinforcements event!");
                Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] reinforcements event is now active, IndigoMS is under attack!"));
                hostch = client.getChannelServer();
                isEventOn = true;
                Random rand = new Random();
                for(int i : maps){
                    int pickmap = rand.nextInt(12);
                    mapindx.add(pickmap);
                }
                eventmap = hostch.getMapFactory().getMap(maps.get(mapindx.get(0)));
                changemapSchedule = TimerManager.getInstance().register(new Runnable() {
                    public void run() {
                        System.out.println(Integer.toString(place));
                        if (place < 11){
                            eventmap.clearMapObjects();
                            place += 1;
                            timelimit = 0;
                            //player.changeMap(maps.get(mapindx.get(place)));
                            eventmap = hostch.getMapFactory().getMap(maps.get(mapindx.get(place)));
                            int chid = hostch.getId();
                            Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] Channel : "+Integer.toString(chid)+", "+eventmap.getMapName()+" is now under attack, please help to stop the invasion."));
                            client.getWorldServer().setServerMessage("[Server Notice] Channel : "+Integer.toString(chid)+", "+eventmap.getMapName()+" is now under attack, please help to stop the invasion.");
                        } else {
                            isEventOn = false;
                            eventmap = hostch.getMapFactory().getMap(maps.get(mapindx.get(place)));
                            Server.getInstance().broadcastMessage(client.getWorld(), MaplePacketCreator.serverNotice(6, "[Server Notice] reinforcements event has ended, thank you for fighting for IndigoMS!"));
                            place = -1;
                            timelimit = 0;
                            mapindx.clear();
                            spawnmobSchedule.cancel(true);
                            changemapSchedule.cancel(true);
                        }
                    }
                }, 7200000);
                spawnmobSchedule = TimerManager.getInstance().register(new Runnable() {
                    public void run() {
                        timelimit += 1;
                        if (timelimit < 30 && place > -1){
                            int countboss = 0;
                            int countscar = 0;
                            int countazur = 0;
                            int playercount = 0;
                            for (Channel ch : Server.getInstance().getChannelsFromWorld(player.getWorld())) {
                                for (MapleCharacter chr : ch.getPlayerStorage().getAllCharacters()) {
                                    if (chr.getLevel() > 109){
                                        playercount += 1;
                                    }
                                }
                            }
                            if (playercount>30){
                                playercount = 30;
                            }
                            eventmap = hostch.getMapFactory().getMap(maps.get(mapindx.get(place)));
                            Point ps = new Point();
                            int eventmapid = eventmap.getId();
                            switch (eventmapid) {
                                case 102000000:
                                    ps.x = 1466;
                                    ps.y = 1875;
                                    break;
                                case 221000000:
                                    ps.x = 5253;
                                    ps.y = 127;
                                    break;
                                case 211000000:
                                    ps.x = -1915;
                                    ps.y = 94;
                                    break;
                                case 251000000:
                                    ps.x = -1032;
                                    ps.y = 238;
                                    break;
                                case 222000000:
                                    ps.x = 511;
                                    ps.y = 313;
                                    break;
                                case 300000000:
                                    ps.x = -845;
                                    ps.y = -25;
                                    break;
                                case 250000100:
                                    ps.x = -2435;
                                    ps.y = 51;
                                    break;
                                case 200000000:
                                    ps.x = -1095;
                                    ps.y = 125;
                                    break;
                                case 220000000:
                                    ps.x = -2294;
                                    ps.y = 88;
                                    break;
                                case 260000000:
                                    ps.x = 1862;
                                    ps.y = 230;
                                    break;
                                case 261000000:
                                    ps.x = 430;
                                    ps.y = 371;
                                    break;
                                case 240000000:
                                    ps.x = 260;
                                    ps.y = 462;
                                    break;
                                default:
                                    break;
                            }
                            List<MapleMonster> monlist = eventmap.getAllMonsters();
                            for(MapleMonster mob : monlist){
                                if (mob.getId() == 9400596){
                                    countscar += 1;
                                } else if(mob.getId() == 9400597){
                                    countazur += 1;
                                } else if(mob.getId() == 9400624){
                                    countboss += 1;
                                }
                            }
                            int dec1 = (2*playercount)-countscar;
                            while (dec1 > 0){
                                MapleMonster scarpho = MapleLifeFactory.getMonster(9400596);
                                eventmap.spawnMonsterOnGroundBelow(scarpho, ps);
                                dec1 -= 1;
                            }
                            int dec2 = (2*playercount)-countazur;
                            while (dec2 > 0){
                                MapleMonster azureoce = MapleLifeFactory.getMonster(9400597);
                                eventmap.spawnMonsterOnGroundBelow(azureoce, ps);
                                dec2 -= 1;
                            }
                            int dec3 = (2*playercount)-countboss;
                            while (dec3 > 0){
                                MapleMonster boss = MapleLifeFactory.getMonster(9400624);
                                eventmap.spawnMonsterOnGroundBelow(boss, ps);
                                dec3 -= 1;
                            }
                        }
                    }
                }, 60000);
            } else {
                player.yellowMessage("event already taking place!");
            }
        } else {
            if (isEventOn){
                place = -1;
                timelimit = 0;
                mapindx.clear();
                isEventOn = false;
                spawnmobSchedule.cancel(true);
                changemapSchedule.cancel(true);
                player.yellowMessage("You just cancelled the reinforcements event!");
            } else {
                player.yellowMessage("event isnt active!");
            }
        }
    }
}
