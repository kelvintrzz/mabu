package com.girlkun.models.boss.list_boss.nappa;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.server.Manager;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import com.girlkun.services.TaskService;

import java.util.Random;

public class KhiUltra extends Boss {

    public KhiUltra() throws Exception {
        super(BossID.KHI_ULTRA, BossesData.KHI_ULTRA);
    }

    @Override
    public void reward(Player plKill) {

        byte randomDo = (byte) new Random().nextInt(Manager.itemIds_TL.length - 1);
        byte randomNR = (byte) new Random().nextInt(Manager.itemIds_NR_SB.length);
        if (Util.isTrue(20, 100)) {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 2106, 1, this.location.x, this.location.y, plKill.id));
            return;

        } else if (Util.isTrue(5, 100)) {
            ItemMap mvbt = new ItemMap(this.zone, Util.nextInt(1048, 1062), 1, this.location.x, this.location.y, plKill.id);
            for (int i = 0; i < 5; i++) {
                if (mvbt.itemTemplate.type == i) {

                    if (i == 0) {
                        mvbt.options.add(new Item.ItemOption(47, Util.nextInt(3000, 6000)));
                     
                    }
                    if (i == 1) {
                        mvbt.options.add(new Item.ItemOption(22, Util.nextInt(120, 150)));
                        if (Util.isTrue(25, 100)) {
                            if (mvbt.itemTemplate.id == 1053) {
                                mvbt.options.add(new Item.ItemOption(135, 1));
                                mvbt.options.add(new Item.ItemOption(138, 1));
                            }
                        }
                    }
                    if (i == 2) {
                        mvbt.options.add(new Item.ItemOption(21, 200)); // add ycsm
                        if(Util.isTrue(1, 100)){
                            mvbt.options.add(new Item.ItemOption(220, Util.nextInt(25, 50)));
                        } else mvbt.options.add(new Item.ItemOption(220, Util.nextInt(20, 25)));
                        if (Util.isTrue(5, 100)) {
                          
                            if (mvbt.itemTemplate.id == 1054) {
                                if (Util.isTrue(5, 100)) {
                                    mvbt.options.add(new Item.ItemOption(128, 1));
                                    mvbt.options.add(new Item.ItemOption(140, 1));
                                } else {
                                    mvbt.options.add(new Item.ItemOption(129, 1));
                                    mvbt.options.add(new Item.ItemOption(141, 1));
                                }
                            } else if (mvbt.itemTemplate.id == 1055) {
                                mvbt.options.add(new Item.ItemOption(130, 1));
                                mvbt.options.add(new Item.ItemOption(142, 1));
                                
                            } else if (mvbt.itemTemplate.id == 1056) {
                                mvbt.options.add(new Item.ItemOption(134, 1));
                                mvbt.options.add(new Item.ItemOption(137, 1));
                            }
                        }
                       
                    }
                    if (i == 3) {
                        mvbt.options.add(new Item.ItemOption(23, Util.nextInt(120, 150)));
                        if (Util.isTrue(50, 100)) {
                            if (mvbt.itemTemplate.id == 1058) {
                                mvbt.options.add(new Item.ItemOption(130, 1));
                                mvbt.options.add(new Item.ItemOption(142, 1));
                            }
                        }
                      
                    }
                    if (i == 4) {
                        mvbt.options.add(new Item.ItemOption(14, Util.nextInt(20, 25)));
                        if (Util.isTrue(20, 100)) {
                            if (Util.isTrue(20, 100) && mvbt.itemTemplate.id == 1060) {
                                mvbt.options.add(new Item.ItemOption(128, 1));
                                    mvbt.options.add(new Item.ItemOption(140, 1));
                            } else if (Util.isTrue(25, 100)&& mvbt.itemTemplate.id == 1060) {
                                mvbt.options.add(new Item.ItemOption(129, 1));
                                    mvbt.options.add(new Item.ItemOption(141, 1));
                            } else if (Util.isTrue(30, 100)&& mvbt.itemTemplate.id == 1062) {
                                 mvbt.options.add(new Item.ItemOption(135, 1));
                                mvbt.options.add(new Item.ItemOption(138, 1));
                            } else if (Util.isTrue(15, 100)&& mvbt.itemTemplate.id == 1062) {
                                mvbt.options.add(new Item.ItemOption(133, 1));
                                mvbt.options.add(new Item.ItemOption(136, 1));
                            } else if (Util.isTrue(10, 100)&& mvbt.itemTemplate.id == 1061) {
                                mvbt.options.add(new Item.ItemOption(131, 1));
                                mvbt.options.add(new Item.ItemOption(143, 1));
                            } 
                        }
                    
                    }
                }
            }
            Service.gI().dropItemMap(this.zone,mvbt);
            return;
        } else {
            Service.gI().dropItemMap(this.zone, new ItemMap(zone, 2106, 1, this.location.x, zone.map.yPhysicInTop(this.location.x, this.location.y - 24), plKill.id));
            return;
        }
    }

    @Override
    public void active() {
        super.active(); //To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1200000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 7);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 2;
            }
            this.nPoint.subHP(damage);
            if (isDie()) {
                this.setDie(plAtt);
                die(plAtt);
            }
            return damage;
        } else {
            return 0;
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); //To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;
}
