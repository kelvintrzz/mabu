package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class heartgod extends Boss {

    public heartgod() throws Exception {
        super(BossID.HEARTGOD, BossesData.HEARTGOD);
    }

    @Override
    public void reward(Player plKill) {
     
        if (Util.isTrue(50, 100)) {
            if(Util.isTrue(10,100)){
                int randomTC = Util.nextInt(500,520);
                ItemMap mvbt = new ItemMap(this.zone, 2158, 1, this.location.x, this.location.y, plKill.id);
                mvbt.options.add(new Item.ItemOption(220, randomTC));
                mvbt.options.add(new Item.ItemOption(21, 400));// giap
                mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141
                Service.gI().dropItemMap(this.zone, mvbt);
            }else{
                int randomCM = Util.nextInt(15,25);
                ItemMap mvbt = new ItemMap(this.zone, 2160, 1, this.location.x, this.location.y, plKill.id);
                mvbt.options.add(new Item.ItemOption(14, randomCM));
                mvbt.options.add(new Item.ItemOption(21, 400));// giap
                mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141
                Service.gI().dropItemMap(this.zone, mvbt);
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
            damage = this.nPoint.subDameInjureWithDeff(damage / 1500);
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
