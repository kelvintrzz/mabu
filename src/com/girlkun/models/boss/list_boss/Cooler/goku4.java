package com.girlkun.models.boss.list_boss.Cooler;

import com.girlkun.models.boss.*;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.player.Player;
import com.girlkun.services.EffectSkillService;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;

import java.util.Random;

public class goku4 extends Boss {

    public goku4() throws Exception {
        super(BossID.GOKU4, BossesData.GOKU4);
    }

    @Override
    public void reward(Player plKill) {
     
        if (Util.isTrue(50, 100)) {
            if(Util.isTrue(5,100)){
                int randomTC = Util.nextInt(600,700); 
               
                ItemMap mvbt = new ItemMap(this.zone, 2158, 1, this.location.x, this.location.y, plKill.id);
                mvbt.options.add(new Item.ItemOption(220, randomTC));
                mvbt.options.add(new Item.ItemOption(21, 400));// giap
                mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141
                Service.gI().dropItemMap(this.zone, mvbt);
            }else{
               
                int randomITEM = Util.nextInt(2156,2162);
                int randomCM = Util.nextInt(20,30);
                int randomGIAP = Util.nextInt(4000,6000);
                int randomhp = Util.nextInt(1000,1500);
                int randomTC = Util.nextInt(500,600); 
                int randomSD = Util.nextInt(300,400);
                int randomHP = Util.nextInt(350,450);
                int randomCMM = Util.nextInt(130,180);
               
                ItemMap mvbt = new ItemMap(this.zone, randomITEM, 1, this.location.x, this.location.y, plKill.id);
               
                switch(randomITEM){
                    case 2156 :
                         mvbt.options.add(new Item.ItemOption(47, randomGIAP));
                        break;
                    case 2157 :
                         mvbt.options.add(new Item.ItemOption(22, randomhp));
                        break;
                    case 2158 :
                         mvbt.options.add(new Item.ItemOption(220, randomTC));
                        break;
                    case 2159 :
                         mvbt.options.add(new Item.ItemOption(23, randomhp));
                        break;
                    case 2160 :
                         mvbt.options.add(new Item.ItemOption(14, randomCM));
                        break;
                    case 2161:
                         mvbt.options.add(new Item.ItemOption(50, randomSD));
                         mvbt.options.add(new Item.ItemOption(77, randomHP));                                    
                         mvbt.options.add(new Item.ItemOption(103, randomHP));                                    
                         mvbt.options.add(new Item.ItemOption(5, randomCMM));
                         mvbt.options.add(new Item.ItemOption(14, 60));
                        
                        break;
                    case 2162:
                         mvbt.options.add(new Item.ItemOption(50, randomSD));
                         mvbt.options.add(new Item.ItemOption(77, randomHP));                                    
                         mvbt.options.add(new Item.ItemOption(103, randomHP));                                    
                         mvbt.options.add(new Item.ItemOption(5, randomCMM));
                         mvbt.options.add(new Item.ItemOption(14, 60));
                         break;
                   
                }
                
                mvbt.options.add(new Item.ItemOption(21, 400));// giap
                mvbt.options.add(new Item.ItemOption(36, 0)); // hoac 128 , 129
                mvbt.options.add(new Item.ItemOption(76, 0)); // hoac 140 ,141                
                mvbt.options.add(new Item.ItemOption(34, 0)); // hoac 140 ,141

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
            damage = this.nPoint.subDameInjureWithDeff(damage / 15000);
            if (!piercing && effectSkill.isShielding) {
                if (damage > nPoint.hpMax) {
                    EffectSkillService.gI().breakShield(this);
                }
                damage = damage / 1000;
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
