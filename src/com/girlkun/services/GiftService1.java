package com.girlkun.services;

import com.arriety.MaQuaTang1.MaQuaTang1;
import com.arriety.MaQuaTang1.MaQuaTangManager1;
import com.girlkun.data.ItemData;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;
import com.girlkun.server.Maintenance;

import com.arriety.MaQuaTang1.MaQuaTangManager1;

import java.sql.Timestamp;
import java.util.ArrayList;


/**
 *
 * @Stole By Arriety 💖
 *
 */
public class GiftService1 {

    private static GiftService1 i;
    
    private GiftService1(){
        
    }
    public String code;
    public int idGiftcode;
    public int gold;
    public int gem;
    public int dayexits;
    public Timestamp timecreate;
    public ArrayList<Item> listItem = new ArrayList<>();
    public static ArrayList<GiftService1> gifts = new ArrayList<>();
    public static GiftService1 gI(){
        if(i == null){
            i = new GiftService1();
        }
        return i;
    }
   
    public void giftCode(Player player, String code){
         MaQuaTang1 giftcode = MaQuaTangManager1.gI().checkUseGiftCode1((int)player.id, code);
               // if(!Maintenance.gI().canUseCode){Service.gI().sendThongBao(player, "Không thể thực hiện lúc này ");return;}
                       if(giftcode == null){
                      
                             Service.getInstance().sendThongBao(player,"Code đã được sử dụng, hoặc không tồn tại!");
                        
                       }
                       else if(giftcode.timeCode1()){
                            Service.getInstance().sendThongBao(player,"Code đã hết hạn");
                        }else {                       
                            InventoryServiceNew.gI().addItemGiftCodeToPlayer1(player, giftcode);
                               }
    }
    
}