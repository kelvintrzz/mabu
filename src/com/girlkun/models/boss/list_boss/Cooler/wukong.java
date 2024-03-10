package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class wukong extends Boss {

    public wukong() throws Exception {
        super(BossID.WUKONG, BossesData.WUKONG);
    }

    @Override
    public void reward(Player plKill) {
        int randomSD = Util.nextInt(10, 350);
        int randomCm = Util.nextInt(10, 100);
        int randomHSD = Util.nextInt(5, 15);
        ItemMap mvbt = new ItemMap(this.zone, 547, 1, this.location.x, this.location.y, plKill.id);
        ItemMap mvbt1 = new ItemMap(this.zone, 888, 100, this.location.x, this.location.y, plKill.id);
        ItemMap mvbt2 = new ItemMap(this.zone, 889, 100, this.location.x, this.location.y, plKill.id);
        ItemMap mvbt3 = new ItemMap(this.zone, 748, 20, this.location.x, this.location.y, plKill.id);
        ItemMap mvbt4 = new ItemMap(this.zone, 2102, 20, this.location.x, this.location.y, plKill.id);
        ItemMap mvbt5 = new ItemMap(this.zone, 2135, 50, this.location.x, this.location.y, plKill.id);
        mvbt.options.add(new Item.ItemOption(50, randomSD)); // + Chi mang
        mvbt.options.add(new Item.ItemOption(77, randomSD)); // hoac 134 , 135
        mvbt.options.add(new Item.ItemOption(103, randomSD)); // hoac 137 , 138
        mvbt.options.add(new Item.ItemOption(14, randomCm));
        mvbt.options.add(new Item.ItemOption(5, randomCm));
        mvbt.options.add(new Item.ItemOption(93, randomHSD));
        Service.gI().dropItemMap(this.zone, mvbt);
        Service.gI().dropItemMap(this.zone, mvbt1);
        Service.gI().dropItemMap(this.zone, mvbt2);
        Service.gI().dropItemMap(this.zone, mvbt3);
        Service.gI().dropItemMap(this.zone, mvbt4);
        Service.gI().dropItemMap(this.zone, mvbt5);

    }

    public long injured(Player plAtt, long damage, boolean piercing, boolean isMobAttack) {
        if (!this.isDie()) {
            if (!piercing && Util.isTrue(this.nPoint.tlNeDon, 1000)) {
                this.chat("Xí hụt");
                return 0;
            }
            damage = this.nPoint.subDameInjureWithDeff(10000);
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
