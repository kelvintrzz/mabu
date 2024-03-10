package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

public class rongtaac extends Boss {

    public rongtaac() throws Exception {
        super(BossID.RONGTAAC, BossesData.RONGTAAC);
    }

    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(50, 100)) {
            if (Util.isTrue(5, 100)) {
                int randomTC = Util.nextInt(600, 800);

                ItemMap mvbt = new ItemMap(this.zone, 2158, 1, this.location.x, this.location.y, plKill.id);
                mvbt.options.add(new Item.ItemOption(220, randomTC));
                mvbt.options.add(new Item.ItemOption(21, 400));// giap
                mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141
                Service.gI().dropItemMap(this.zone, mvbt);
            } else {
                int item[] = {2128, 2129, 2130, 2156, 2157, 2158, 2159, 2160, 2164, 2169};
                int randomITEM = Util.nextInt(0, 9);
                int randomCM = Util.nextInt(20, 30);
                int randomGIAP = Util.nextInt(4000, 6000);
                int randomhp = Util.nextInt(1000, 1500);
                int randomTC = Util.nextInt(500, 600);
                int randomSD = Util.nextInt(300, 600);
                int randomHP = Util.nextInt(350, 450);
                int randomCMM = Util.nextInt(130, 180);

                ItemMap mvbt = new ItemMap(this.zone, item[randomITEM], 1, this.location.x, this.location.y, plKill.id);

                if (item[randomITEM] >= 2156 && item[randomITEM] <= 2169 && item[randomITEM] != 2164) {
                    switch (item[randomITEM]) {
                        case 2156:
                            mvbt.options.add(new Item.ItemOption(47, randomGIAP));
                            break;
                        case 2157:
                            mvbt.options.add(new Item.ItemOption(22, randomhp));
                            break;
                        case 2158:
                            mvbt.options.add(new Item.ItemOption(220, randomTC));
                            break;
                        case 2159:
                            mvbt.options.add(new Item.ItemOption(23, randomhp));
                            break;
                        case 2160:
                            mvbt.options.add(new Item.ItemOption(14, randomCM));
                            break;
                        case 2169:
                            mvbt.options.add(new Item.ItemOption(50, randomSD));
                            mvbt.options.add(new Item.ItemOption(77, randomHP));
                            mvbt.options.add(new Item.ItemOption(103, randomHP));
                            mvbt.options.add(new Item.ItemOption(5, randomCMM));
                            mvbt.options.add(new Item.ItemOption(14, 60));
                            mvbt.options.add(new Item.ItemOption(93, 10));

                            break;

                    }

                    mvbt.options.add(new Item.ItemOption(21, 400));// giap
                    mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                    mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141                
                    mvbt.options.add(new Item.ItemOption(34, 0)); // hoac 140 ,141

                    Service.gI().dropItemMap(this.zone, mvbt);
                } else {
                    mvbt.options.add(new Item.ItemOption(73, 0));// giap
                    Service.gI().dropItemMap(this.zone, mvbt);

                }
            }

        } else {
            ItemMap mvbt = new ItemMap(this.zone, 2128, 3, this.location.x, this.location.y, plKill.id);
            Service.gI().dropItemMap(this.zone, mvbt);
        }
        return;
    }

    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;

            }
            damage = this.nPoint.subDameInjureWithDeff(damage / 10000000);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 100;
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
    public void active() {
        super.active(); // To change body of generated methods, choose Tools | Templates.
        if (Util.canDoWithTime(st, 1200000)) {
            this.changeStatus(BossStatus.LEAVE_MAP);
        }
    }

    @Override
    public void joinMap() {
        super.joinMap(); // To change body of generated methods, choose Tools | Templates.
        st = System.currentTimeMillis();
    }

    private long st;

}
