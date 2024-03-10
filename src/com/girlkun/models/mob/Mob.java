package com.girlkun.models.mob;

import com.girlkun.consts.ConstMap;
import com.girlkun.consts.ConstMob;
import com.girlkun.consts.ConstTask;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;

import java.util.List;

import com.girlkun.models.map.Zone;
import com.girlkun.models.player.Location;
import com.girlkun.models.player.Pet;
import com.girlkun.models.player.Player;
import com.girlkun.models.reward.ItemMobReward;
import com.girlkun.models.reward.MobReward;
import com.girlkun.network.io.Message;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.server.ServerManager;
import com.girlkun.services.*;
import com.girlkun.services.func.ChangeMapService;
import com.girlkun.utils.Util;

import java.util.ArrayList;

public class Mob {

    public int id;
    public Zone zone;
    public int tempId;
    public String name;
    public byte level;

    public MobPoint point;
    public MobEffectSkill effectSkill;
    public Location location;

    public byte pDame;
    public int pTiemNang;
    private long maxTiemNang;

    public long lastTimeDie;
    public int lvMob = 0;
    public int status = 5;

    public Mob(Mob mob) {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
        this.id = mob.id;
        this.tempId = mob.tempId;
        this.level = mob.level;
        this.point.setHpFull(mob.point.getHpFull());
        this.point.sethp(this.point.getHpFull());
        this.location.x = mob.location.x;
        this.location.y = mob.location.y;
        this.pDame = mob.pDame;
        this.pTiemNang = mob.pTiemNang;
        this.setTiemNang();
    }

    public Mob() {
        this.point = new MobPoint(this);
        this.effectSkill = new MobEffectSkill(this);
        this.location = new Location();
    }

    public void setTiemNang() {
        this.maxTiemNang = (long) this.point.getHpFull() * (this.pTiemNang + Util.nextInt(-2, 2)) / 100;
    }

    private long lastTimeAttackPlayer;

    public boolean isDie() {
        return this.point.gethp() <= 0;
    }

    public synchronized void injured(Player plAtt, long damage, boolean dieWhenHpFull) {
        if (!this.isDie()) {
            if (damage >= this.point.hp) {
                damage = this.point.hp;
            }
            if (!dieWhenHpFull) {
                if (this.point.hp == this.point.maxHp && damage >= this.point.hp) {
                    damage = this.point.hp - 1;
                }
                if (this.tempId == 0 && damage > 10) {
                    damage = 10;
                }
            }
            this.point.hp -= damage;
            if (this.isDie()) {
                this.status = 0;
                this.sendMobDieAffterAttacked(plAtt, damage);
                TaskService.gI().checkDoneTaskKillMob(plAtt, this);
                TaskService.gI().checkDoneSideTaskKillMob(plAtt, this);
                this.lastTimeDie = System.currentTimeMillis();
            } else {
                this.sendMobStillAliveAffterAttacked(damage, plAtt != null ? plAtt.nPoint.isCrit : false);
            }
            if (plAtt != null) {
                Service.gI().addSMTN(plAtt, (byte) 2, getTiemNangForPlayer(plAtt, damage), true);
            }
        }
    }

    public long getTiemNangForPlayer(Player pl, long dame) {
        int levelPlayer = Service.gI().getCurrLevel(pl);
        int n = levelPlayer - this.level;
        long pDameHit = (dame) * 100 / point.getHpFull();
        long tiemNang = pDameHit * maxTiemNang / 100;
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        if (n >= 0) {
            for (int i = 0; i < n; i++) {
                long sub = tiemNang * 5 / 100;
                if (sub <= 0) {
                    sub = 1;
                }
                tiemNang -= sub;
            }
        } else {
            for (int i = 0; i < -n; i++) {
                long add = tiemNang * 5 / 100;
                if (add <= 0) {
                    add = 1;
                }
                tiemNang += add;
            }
        }
        if (tiemNang <= 0) {
            tiemNang = 1;
        }
        tiemNang = (pl.nPoint.calSucManhTiemNang(tiemNang));
        if (pl.zone.map.mapId == 122 || pl.zone.map.mapId == 123 || pl.zone.map.mapId == 124) {
            tiemNang *= 10;
        }
        if (pl.zone.map.mapId == 156 || pl.zone.map.mapId == 157) {
            tiemNang *= 5;
        }
        if (pl.zone.map.mapId == 158 || pl.zone.map.mapId == 159) {
            tiemNang /= 15;
        }
        tiemNang = (pl.nPoint.calSucManhTiemNang(tiemNang));
        return tiemNang;
    }

    public void update() {
        if (this.tempId == 71) {
            try {
                Message msg = new Message(102);
                msg.writer().writeByte(5);
                msg.writer().writeShort(this.zone.getPlayers().get(0).location.x);
                Service.gI().sendMessAllPlayerInMap(zone, msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }

        if (this.isDie() && !Maintenance.isRuning) {
            switch (zone.map.type) {
                case ConstMap.MAP_DOANH_TRAI:
                    break;
                default:
                    if (Util.canDoWithTime(lastTimeDie, 5000)) {
                        this.hoiSinh();

                        this.sendMobHoiSinh();
                    }
            }
        }
        effectSkill.update();
        attackPlayer();
    }

    private void attackPlayer() {
        if (!isDie() && !effectSkill.isHaveEffectSkill() && !(tempId == 0)
                && Util.canDoWithTime(lastTimeAttackPlayer, 2000)) {
            Player pl = getPlayerCanAttack();
            if (pl != null) {
                // MobService.gI().mobAttackPlayer(this, pl);

                this.mobAttackPlayer(pl);

            }
            this.lastTimeAttackPlayer = System.currentTimeMillis();
        }
    }

    private Player getPlayerCanAttack() {
        int distance = 100;
        Player plAttack = null;
        try {
            List<Player> players = this.zone.getNotBosses();
            for (Player pl : players) {
                if (!pl.isDie() && !pl.isBoss && !pl.effectSkin.isVoHinh && !pl.isNewPet) {
                    int dis = Util.getDistance(pl, this);
                    if (dis <= distance) {
                        plAttack = pl;
                        distance = dis;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return plAttack;
    }

    // **************************************************************************
    private void mobAttackPlayer(Player player) {
        try {
            long dameMob = this.point.getDameAttack();
            if (player.charms.tdDaTrau > System.currentTimeMillis()) {
                dameMob /= 2;
            }
            long dame = player.injured(null, dameMob, false, true);
            this.sendMobAttackMe(player, dame);
            this.sendMobAttackPlayer(player);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void sendMobAttackMe(Player player, long dame) {
        if (!player.isPet && !player.isNewPet) {
            Message msg;
            try {
                msg = new Message(-11);
                msg.writer().writeByte(this.id);
                msg.writer().writeLong((dame)); // dame
                player.sendMessage(msg);
                msg.cleanup();
            } catch (Exception e) {
            }
        }
    }

    private void sendMobAttackPlayer(Player player) {
        Message msg;
        try {
            msg = new Message(-10);
            msg.writer().writeByte(this.id);
            msg.writer().writeInt((int) player.id);
            msg.writer().writeLong((player.nPoint.hp));
            Service.gI().sendMessAnotherNotMeInMap(player, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void hoiSinh() {
        this.status = 5;
        this.point.hp = this.point.maxHp;
        this.setTiemNang();
    }

    private void sendMobHoiSinh() {
        Message msg;
        try {
            msg = new Message(-13);
            msg.writer().writeByte(this.id);
            msg.writer().writeByte(this.tempId);
            msg.writer().writeByte(lvMob);
            msg.writer().writeLong(this.point.hp);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // **************************************************************************
    private void sendMobDieAffterAttacked(Player plKill, long dameHit) {
        Message msg;
        try {
            msg = new Message(-12);
            msg.writer().writeByte(this.id);
            msg.writer().writeLong((dameHit));
            msg.writer().writeBoolean(plKill.nPoint.isCrit); // crit
            List<ItemMap> items = mobReward(plKill, this.dropItemTask(plKill), msg);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
            hutItem(plKill, items);
        } catch (Exception e) {
        }
    }

    private void hutItem(Player player, List<ItemMap> items) {
        if (!player.isPet && !player.isNewPet) {
            if (player.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(player, item.itemMapId, true);
                    }
                }
            }
        } else {
            if (((Pet) player).master.charms.tdThuHut > System.currentTimeMillis()) {
                for (ItemMap item : items) {
                    if (item.itemTemplate.id != 590) {
                        ItemMapService.gI().pickItem(((Pet) player).master, item.itemMapId, true);
                    }
                }
            }
        }
    }

    private List<ItemMap> mobReward(Player player, ItemMap itemTask, Message msg) {
        // nplayer
        List<ItemMap> itemReward = new ArrayList<>();
        try {
            // if ((!player.isPet && player.getSession().actived && player.setClothes.setDHD
            // == 5) || (player.isPet && ((Pet) player).master.getSession().actived &&
            // ((Pet) player).setClothes.setDHD == 5)) {
            // byte random = 1;
            // if (Util.isTrue(5, 100)) {
            // random = 2;
            // }
            // Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size()
            // - 1));
            // i.quantity = random;
            // InventoryServiceNew.gI().addItemBag(player, i);
            // InventoryServiceNew.gI().sendItemBags(player);
            // Service.gI().sendThongBao(player, "Bạn vừa nhận được " + random + " hồng
            // ngọc");
            // }
            if ((!player.isPet)) {
                byte soluongngocrong = 1;
                if (Util.isTrue(1, 800)) {
                    Item i = Manager.RUBY_REWARDS.get(Util.nextInt(0, Manager.RUBY_REWARDS.size() - 1));
                    i.quantity = 1;
                    InventoryServiceNew.gI().addItemBag(player, i);
                    InventoryServiceNew.gI().sendItemBags(player);
                    Service.gI().sendThongBao(player, "Bạn vừa nhận được " + soluongngocrong + " viên ngọc rồng");
                }
            }

            itemReward = this.getItemMobReward(player, this.location.x + Util.nextInt(-10, 10),
                    this.zone.map.yPhysicInTop(this.location.x, this.location.y));
            if (itemTask != null) {
                itemReward.add(itemTask);
            }
            // chỗ chỉnh ngọc rồng nè, 10 / 100 nè, 17 đến 20 nè
//            if (Util.isTrue(5, 100)) {
//                ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(17, 20), 1, this.location.x, this.location.y,
//                        player.id);
//
//                itemReward.add(mvbt);
//            }

            if (player.zone.map.mapId == 170) {
                if (Util.isTrue(8, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2068, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);
                }
            }

            // if (Util.isTrue(5, 100)) {
            //     ItemMap mvbt = new ItemMap(this.zone, 2135, 1, this.location.x, this.location.y, player.id);
            //     itemReward.add(mvbt);
            // }

            // if (Util.isTrue(50, 100)) {
            //     ItemMap mvbt = new ItemMap(this.zone, 888, 1, this.location.x, this.location.y, player.id);
            //     itemReward.add(mvbt);
            // }
            // if (Util.isTrue(50, 100)) {
            //     ItemMap mvbt = new ItemMap(this.zone, 889, 1, this.location.x, this.location.y, player.id);
            //     itemReward.add(mvbt);
            // }

            // if (Util.isTrue(30, 100)) {
            //     ItemMap mvbt = new ItemMap(this.zone, 748, 1, this.location.x, this.location.y, player.id);
            //     itemReward.add(mvbt);
            // }

           
            // if (Util.isTrue(1, 100)) {
            //     ItemMap mvbt = new ItemMap(this.zone, 2147, 1, this.location.x, this.location.y, player.id);
            //     itemReward.add(mvbt);
            // }

        
               if (Util.isTrue(4, 100)) {
                   ItemMap mvbt = new ItemMap(this.zone, 2117, 1, this.location.x, this.location.y, player.id);
                   mvbt.options.add(new Item.ItemOption(30, 1));
                   itemReward.add(mvbt);
               }
               if (Util.isTrue(2, 300)) {
                   ItemMap mvbt = new ItemMap(this.zone, 2118, 1, this.location.x, this.location.y, player.id);
                   mvbt.options.add(new Item.ItemOption(30, 1));
                   itemReward.add(mvbt);
               }
                if (Util.isTrue(4, 100)) {
                   ItemMap mvbt = new ItemMap(this.zone, 2182, 1, this.location.x, this.location.y, player.id);
                   mvbt.options.add(new Item.ItemOption(30, 1));
                   itemReward.add(mvbt);
               }
              // return itemReward;
           
            if (!player.isPet && (player.zone.map.mapId == 1 || player.zone.map.mapId == 2 || player.zone.map.mapId == 3
                    || player.zone.map.mapId == 10 || player.zone.map.mapId == 8 || player.zone.map.mapId == 9
                    || player.zone.map.mapId == 15 || player.zone.map.mapId == 16 || player.zone.map.mapId == 17)) {

                if (Util.isTrue(7, 100)) {
                    if (player.gender == 0) {
                        ItemMap mvbt = new ItemMap(this.zone, 0, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(47, Util.nextInt(2, 40)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(127, 1));
                            mvbt.options.add(new Item.ItemOption(139, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(128, 1));
                            mvbt.options.add(new Item.ItemOption(140, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(129, 1));
                            mvbt.options.add(new Item.ItemOption(141, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 0) {
                        ItemMap mvbt = new ItemMap(this.zone, 6, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(6, Util.nextInt(30, 1000)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(127, 1));
                            mvbt.options.add(new Item.ItemOption(139, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(128, 1));
                            mvbt.options.add(new Item.ItemOption(140, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(129, 1));
                            mvbt.options.add(new Item.ItemOption(141, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 0) {
                        ItemMap mvbt = new ItemMap(this.zone, 21, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(0, Util.nextInt(4, 40)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(127, 1));
                            mvbt.options.add(new Item.ItemOption(139, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(128, 1));
                            mvbt.options.add(new Item.ItemOption(140, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(129, 1));
                            mvbt.options.add(new Item.ItemOption(141, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 0) {
                        ItemMap mvbt = new ItemMap(this.zone, 27, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(7, Util.nextInt(30, 1000)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(127, 1));
                            mvbt.options.add(new Item.ItemOption(139, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(128, 1));
                            mvbt.options.add(new Item.ItemOption(140, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(129, 1));
                            mvbt.options.add(new Item.ItemOption(141, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 0) {
                        ItemMap mvbt = new ItemMap(this.zone, 12, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(127, 1));
                            mvbt.options.add(new Item.ItemOption(139, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(128, 1));
                            mvbt.options.add(new Item.ItemOption(140, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(129, 1));
                            mvbt.options.add(new Item.ItemOption(141, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }

                    /////// namec
                    if (player.gender == 1) {
                        ItemMap mvbt = new ItemMap(this.zone, 1, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(47, Util.nextInt(1, 10)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(132, 1));
                            mvbt.options.add(new Item.ItemOption(144, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(130, 1));
                            mvbt.options.add(new Item.ItemOption(142, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(131, 1));
                            mvbt.options.add(new Item.ItemOption(143, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 1) {
                        ItemMap mvbt = new ItemMap(this.zone, 7, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(6, Util.nextInt(20, 100)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(132, 1));
                            mvbt.options.add(new Item.ItemOption(144, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(130, 1));
                            mvbt.options.add(new Item.ItemOption(142, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(131, 1));
                            mvbt.options.add(new Item.ItemOption(143, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 1) {
                        ItemMap mvbt = new ItemMap(this.zone, 22, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(0, Util.nextInt(1, 10)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(132, 1));
                            mvbt.options.add(new Item.ItemOption(144, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(130, 1));
                            mvbt.options.add(new Item.ItemOption(142, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(131, 1));
                            mvbt.options.add(new Item.ItemOption(143, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 1) {
                        ItemMap mvbt = new ItemMap(this.zone, 28, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(7, Util.nextInt(20, 100)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(132, 1));
                            mvbt.options.add(new Item.ItemOption(144, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(130, 1));
                            mvbt.options.add(new Item.ItemOption(142, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(131, 1));
                            mvbt.options.add(new Item.ItemOption(143, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 1) {
                        ItemMap mvbt = new ItemMap(this.zone, 12, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(132, 1));
                            mvbt.options.add(new Item.ItemOption(144, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(130, 1));
                            mvbt.options.add(new Item.ItemOption(142, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(131, 1));
                            mvbt.options.add(new Item.ItemOption(143, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }

                    ///////////// xayda
                    if (player.gender == 2) {
                        ItemMap mvbt = new ItemMap(this.zone, 2, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(47, Util.nextInt(1, 10)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(134, 1));
                            mvbt.options.add(new Item.ItemOption(137, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(135, 1));
                            mvbt.options.add(new Item.ItemOption(138, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(133, 1));
                            mvbt.options.add(new Item.ItemOption(136, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 2) {
                        ItemMap mvbt = new ItemMap(this.zone, 8, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(6, Util.nextInt(20, 100)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(134, 1));
                            mvbt.options.add(new Item.ItemOption(137, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(135, 1));
                            mvbt.options.add(new Item.ItemOption(138, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(133, 1));
                            mvbt.options.add(new Item.ItemOption(136, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 2) {
                        ItemMap mvbt = new ItemMap(this.zone, 23, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(0, Util.nextInt(1, 10)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(134, 1));
                            mvbt.options.add(new Item.ItemOption(137, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(135, 1));
                            mvbt.options.add(new Item.ItemOption(138, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(133, 1));
                            mvbt.options.add(new Item.ItemOption(136, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 2) {
                        ItemMap mvbt = new ItemMap(this.zone, 29, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(7, Util.nextInt(10, 100)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(134, 1));
                            mvbt.options.add(new Item.ItemOption(137, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(135, 1));
                            mvbt.options.add(new Item.ItemOption(138, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(133, 1));
                            mvbt.options.add(new Item.ItemOption(136, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                    if (player.gender == 2) {
                        ItemMap mvbt = new ItemMap(this.zone, 12, 1, this.location.x, this.location.y, player.id);

                        mvbt.options.add(new Item.ItemOption(14, Util.nextInt(1, 3)));
                        if (Util.isTrue(50, 100)) {
                            mvbt.options.add(new Item.ItemOption(134, 1));
                            mvbt.options.add(new Item.ItemOption(137, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(30, 100)) {
                            mvbt.options.add(new Item.ItemOption(135, 1));
                            mvbt.options.add(new Item.ItemOption(138, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        } else if (Util.isTrue(20, 100)) {
                            mvbt.options.add(new Item.ItemOption(133, 1));
                            mvbt.options.add(new Item.ItemOption(136, 1));
                            mvbt.options.add(new Item.ItemOption(30, 1));
                        }
                        itemReward.add(mvbt);
                    }
                }
            }
            if (player.zone.map.mapId == 171) {
                if (Util.isTrue(10, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2068, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == 172) {
                if (Util.isTrue(3, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2068, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == Util.nextInt(135, 138)) // up da trong bdkb
            {
                if (Util.isTrue(1, 1000)) {
                    ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(220, 224), 1, this.location.x, this.location.y,
                            player.id);
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == 170) {
                if (Util.isTrue(1, 1000)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2072, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == Util.nextInt(105, 110) && player.setClothes.setTL == true) { // up thuc an
                if (Util.isTrue(3, 10)) {
                    ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(664, 667), 1, this.location.x, this.location.y,
                            player.id);
                    mvbt.options.add(new Item.ItemOption(30, 1));
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == 171) {
                if (Util.isTrue(1, 1000)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2072, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);

                }
            }
            if (player.zone.map.mapId >= 160 && player.zone.map.mapId <= 163) {
                if (Util.isTrue(3, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2051, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);
                }
            }
            if (player.zone.map.mapId == 172) {
                if (Util.isTrue(3, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2072, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);

                }
            }
            if (player.zone.map.mapId == 170) {
                if (Util.isTrue(1, 10000)) {
                    ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(2053, 2065), 1, this.location.x, this.location.y,
                            player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);

                }
            }
            if (player.zone.map.mapId == 171) {
                if (Util.isTrue(1, 10000)) {
                    ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(2053, 2065), 1, this.location.x, this.location.y,
                            player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);

                }
            }
            if (player.zone.map.mapId == 172) {
                if (Util.isTrue(1, 50000)) {
                    ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(2053, 2065), 1, this.location.x, this.location.y,
                            player.id);
                    mvbt.options.add(new Item.ItemOption(30, 0));
                    itemReward.add(mvbt);

                }
            }
            msg.writer().writeByte(itemReward.size()); // sl item roi
            for (ItemMap itemMap : itemReward) {
                msg.writer().writeShort(itemMap.itemMapId);// itemmapid
                msg.writer().writeShort(itemMap.itemTemplate.id); // id item
                msg.writer().writeShort(itemMap.x); // xend item
                msg.writer().writeShort(itemMap.y); // yend item
                msg.writer().writeInt((int) itemMap.playerId); // id nhan nat
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return itemReward;
    }

    public List<ItemMap> getItemMobReward(Player player, int x, int yEnd) {
        List<ItemMap> list = new ArrayList<>();
        MobReward mobReward = Manager.MOB_REWARDS.get(this.tempId);
        if (mobReward == null) {
            return list;
        }
        List<ItemMobReward> items = mobReward.getItemReward();
        List<ItemMobReward> golds = mobReward.getGoldReward();
        if (!items.isEmpty()) {
            ItemMobReward item = items.get(Util.nextInt(0, items.size() - 1));
            ItemMap itemMap = item.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (!golds.isEmpty()) {
            ItemMobReward gold = golds.get(Util.nextInt(0, golds.size() - 1));
            ItemMap itemMap = gold.getItemMap(zone, player, x, yEnd);
            if (itemMap != null) {
                list.add(itemMap);
            }
        }
        if (player.itemTime.isUseMayDo && Util.isTrue(21, 100) && this.tempId > 57 && this.tempId < 66) {
            list.add(new ItemMap(zone, 380, 1, x, player.location.y, player.id));
        } // vat phẩm rơi khi user maaáy dò adu hoa r o day ti code choa
        if (player.itemTime.isUseMayDo2 && Util.isTrue(7, 100) && this.tempId > 1 && this.tempId < 81) {
            list.add(new ItemMap(zone, 2036, 1, x, player.location.y, player.id));// cai nay sua sau nha
        }
        // if (Util.nextInt(0,10) < 8){
        // int tlGold = (player.nPoint.getTlGold() + 100);
        // list.add(new ItemMap(zone, 190,
        // (Math.round(Util.nextInt(200000,200000)/100)*tlGold), x, player.location.y,
        // player.id));
        // }
        if (MapService.gI().isMapBanDoKhoBau(player.zone.map.mapId)) {
            if (Util.nextInt(0, 100) < 10) {
                list.add(new ItemMap(zone, 861, 1, x, player.location.y, player.id));
            }
        }
        if (Util.nextInt(0, 100) < 3) {
            list.add(new ItemMap(zone, 1229, 1, x, player.location.y, player.id));
        }

        if (player.zone.map.mapId == 174) {
            try {
                if (player.mott != 1) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                Service.gI().sendThongBao(player, "Địt bà già mày dịch chuyển con cặc, cút về nhà ");
                }
            } catch (Exception ex) {
            }
            if (player.mott == 1) {
                if (Util.isTrue(6, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 1309, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 1));
                    Service.gI().dropItemMap(this.zone, mvbt);

                }if (Util.isTrue(6, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2155, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 1));
                    Service.gI().dropItemMap(this.zone, mvbt);

                }
            }

        }

        
        if (player.zone.map.mapId == 179) {
            try {
                if (player.mott != 1) {
                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                Service.gI().sendThongBao(player, "Địt bà già mày dịch chuyển con cặc, cút về nhà ");
                }
            } catch (Exception ex) {
            }
            if (player.mott == 1) {
                if (Util.isTrue(6, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2190, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 1));
                    Service.gI().dropItemMap(this.zone, mvbt);

                }if (Util.isTrue(6, 100)) {
                    ItemMap mvbt = new ItemMap(this.zone, 2210, 1, this.location.x, this.location.y, player.id);
                    mvbt.options.add(new Item.ItemOption(30, 1));
                    Service.gI().dropItemMap(this.zone, mvbt);

                }
            }

        }
        
        // if (player.isPet && player.getSession().actived && Util.isTrue(15, 100)) {
        // list.add(new ItemMap(zone, 610, 1, x, player.location.y, player.id));
        // }
        return list;
    }

    private ItemMap dropItemTask(Player player) {
        ItemMap itemMap = null;
        switch (this.tempId) {
            case ConstMob.KHUNG_LONG:
            case ConstMob.LON_LOI:
            case ConstMob.QUY_DAT:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_2_0) {
                    itemMap = new ItemMap(this.zone, 73, 1, this.location.x, this.location.y, player.id);
                }
                break;
            case ConstMob.THAN_LAN_ME:
                if (TaskService.gI().getIdTask(player) == ConstTask.TASK_9_1) {
                    itemMap = new ItemMap(this.zone, 20, 1, this.location.x, this.location.y, player.id);
                }
                break;
        }

        if (TaskService.gI().getIdTask(player) == ConstTask.TASK_15_1 && Util.isTrue(50, 100)) {
            if (this.tempId == ConstMob.OC_MUON_HON && player.gender == 0) {
                itemMap = new ItemMap(this.zone, 85, 1, this.location.x, this.location.y, player.id);
            } else if (this.tempId == ConstMob.OC_SEN && player.gender == 1) {
                itemMap = new ItemMap(this.zone, 85, 1, this.location.x, this.location.y, player.id);
            } else if (this.tempId == ConstMob.HEO_XAYDA_ME && player.gender == 2) {
                itemMap = new ItemMap(this.zone, 85, 1, this.location.x, this.location.y, player.id);
            }
        }

        if (itemMap != null) {
            return itemMap;
        }
        return null;
    }

    private void sendMobStillAliveAffterAttacked(long dameHit, boolean crit) {
        Message msg;
        try {
            msg = new Message(-9);
            msg.writer().writeByte(this.id);
            msg.writer().writeLong(this.point.gethp());
            msg.writer().writeLong((dameHit));
            msg.writer().writeBoolean(crit); // chí mạng
            msg.writer().writeInt(-1);
            Service.gI().sendMessAllPlayerInMap(this.zone, msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }
}
