package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class zamasfusion extends Boss {

    public zamasfusion() throws Exception {
        super(BossID.ZAMASFUSION, BossesData.ZAMASFUSION);
    }

    @Override
    public void reward(Player plKill) {
      
        if (Util.isTrue(40, 100)) {
             // random Do
            int randomDo =  Util.nextInt(1048, 1062);
            int randomGiap = Util.nextInt(2000, 10000);
            int randomHP = Util.nextInt(200, 300);
            int randomTC = Util.nextInt(70, 110);
            int randomKI = Util.nextInt(200, 300);
            int randomChiMang = Util.nextInt(30, 70);

            // random skh
            int ramdonSKHTD = Util.nextInt(127, 129);
            int ramdonSKHNM = Util.nextInt(130, 132);
            int ramdonSKHXD = Util.nextInt(133, 135);
            ItemMap mvbt = new ItemMap(this.zone, randomDo, 1, this.location.x, this.location.y, plKill.id);
            // khai bao chiso

            // ao
            if (randomDo == 1048) {
                mvbt.options.add(new Item.ItemOption(47, randomGiap));// giap
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD + 12, 0)); // hoac 140 ,141
            } else if (randomDo == 1049) {
                mvbt.options.add(new Item.ItemOption(47, randomGiap));// giap
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM, 0)); // hoac 131 , 132
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM + 12, 0)); // hoac 144 , 145
            } else if (randomDo == 1050) {
                mvbt.options.add(new Item.ItemOption(47, randomGiap));// giap
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD, 0)); // hoac 134 , 135
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD + 3, 0)); // hoac 137 , 138
                // quan
            } else if (randomDo == 1051) {
                mvbt.options.add(new Item.ItemOption(22, randomHP));// +hp
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD + 12, 0)); // hoac 140 ,141
            } else if (randomDo == 1052) {
                mvbt.options.add(new Item.ItemOption(22, randomHP)); // +hp
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM, 0)); // hoac 131 , 132
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM + 12, 0)); // hoac 144 , 145
            } else if (randomDo == 1053) {
                mvbt.options.add(new Item.ItemOption(22, randomHP)); // + hp
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD, 0)); // hoac 134 , 135
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD + 3, 0)); // hoac 137 , 138
                // gang
            } else if (randomDo == 1054) {
                mvbt.options.add(new Item.ItemOption(220, randomTC)); // Tan cong
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD + 12, 0)); // hoac 140 ,141
            } else if (randomDo == 1055) {
                mvbt.options.add(new Item.ItemOption(220, randomTC)); // Tan cong
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM, 0)); // hoac 131 , 132
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM + 12, 0)); // hoac 144 , 145
            } else if (randomDo == 1056) {
                mvbt.options.add(new Item.ItemOption(220, randomTC)); // Tan cong
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD, 0)); // hoac 134 , 135
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD + 3, 0)); // hoac 137 , 138
                // giay
            } else if (randomDo == 1057) {
                mvbt.options.add(new Item.ItemOption(23, randomKI)); // KI
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD + 12, 0)); // hoac 140 ,141
            } else if (randomDo == 1058) {
                mvbt.options.add(new Item.ItemOption(23, randomKI)); // KI
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM, 0)); // hoac 131 , 132
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM + 12, 0)); // hoac 144 , 145
            } else if (randomDo == 1059) {
                mvbt.options.add(new Item.ItemOption(23, randomKI)); // KI
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD, 0)); // hoac 134 , 135
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD + 3, 0)); // hoac 137 , 138
                // nhan
            } else if (randomDo == 1060) {
                mvbt.options.add(new Item.ItemOption(14, randomChiMang)); // + Chi mang
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(ramdonSKHTD + 12, 0)); // hoac 140 ,141
            } else if (randomDo == 1061) {
                mvbt.options.add(new Item.ItemOption(14, randomChiMang)); // + Chi mang
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM, 0)); // hoac 131 , 132
                mvbt.options.add(new Item.ItemOption(ramdonSKHNM + 12, 0)); // hoac 144 , 145
            } else if (randomDo == 1062) {
                mvbt.options.add(new Item.ItemOption(14, randomChiMang)); // + Chi mang
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD, 0)); // hoac 134 , 135
                mvbt.options.add(new Item.ItemOption(ramdonSKHXD + 3, 0)); // hoac 137 , 138
            }
           
            mvbt.options.add(new Item.ItemOption(21, 200));
            Service.gI().dropItemMap(this.zone, mvbt);
        } else {
            int randomItem =  Util.nextInt(2102, 2104 );
            ItemMap mvbt = new ItemMap(this.zone, randomItem, 1, this.location.x, this.location.y, plKill.id);
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
            damage = this.nPoint.subDameInjureWithDeff(damage / 50);
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
