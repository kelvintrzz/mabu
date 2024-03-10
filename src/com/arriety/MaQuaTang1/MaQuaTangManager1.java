/*
 * Beo Sờ tu đi ô
*/
package com.arriety.MaQuaTang1;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.player.Player;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NpcService;
import com.girlkun.services.Service;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 *
 * @author Administrator
 */
public class MaQuaTangManager1 {
     public String name;
    public final ArrayList<MaQuaTang1> listGiftCode1 = new ArrayList<>();
    private static MaQuaTangManager1 instance;

    public static MaQuaTangManager1 gI() {
        if (instance == null) {
            instance = new MaQuaTangManager1();
        }
        return instance;
    }

    public void init() {
        try (Connection con = GirlkunDB.getConnection();) {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM giftcode1 WHERE status = 1");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                MaQuaTang1 giftcode1 = new MaQuaTang1();
                giftcode1.id = rs.getInt("id");
                giftcode1.code = rs.getString("code");
                giftcode1.countLeft = rs.getInt("count_left");
                giftcode1.dateCreate = rs.getTimestamp("datecreate");
                giftcode1.dateExpired = rs.getTimestamp("expired");
                giftcode1.listIdPlayer = (List<Long>) JSONValue.parse(rs.getString("user_id_used"));
                giftcode1.bagCount = rs.getInt("bag_count");
                giftcode1.detail = rs.getString("detail");
                listGiftCode1.add(giftcode1);
            }
        } catch (Exception erorlog) {
            erorlog.printStackTrace();
        }
    }

    public void giftCode1(Player player, String code) {
        MaQuaTang1 giftCode1 = MaQuaTangManager1.gI().checkUseGiftCode1((int) player.id, code);
        if (giftCode1 == null) {
            Service.getInstance().sendThongBao(player, "Code đã được sử dụng, hoặc không tồn tại!");
            return;
        } else if (giftCode1.timeCode1()) {
            Service.getInstance().sendThongBao(player, "Code đã hết hạn");
            return;
        } else if (InventoryServiceNew.gI().getCountEmptyBag(player) < giftCode1.bagCount) {
            Service.getInstance().sendThongBao(player, "Hành trang không đủ chỗ trống");
            return;
        } else {
            giftCode1.countLeft -= 1;
            giftCode1.addPlayerUsed1((int) player.id);
            InventoryServiceNew.gI().addItemGiftCodeToPlayer1(player, giftCode1);
        }
    }



    public MaQuaTang1 checkUseGiftCode1(int idPlayer, String code) {
        for (MaQuaTang1 giftCode1 : listGiftCode1) {
            if (giftCode1.code.equals(code) && giftCode1.countLeft > 0 && !giftCode1.isUsedGiftCode1(idPlayer)) {
                return giftCode1;
            }
        }
        return null;
    }

    public void checkInfomationGiftCode1(Player p) {
        StringBuilder sb = new StringBuilder();
        for (MaQuaTang1 giftCode1 : listGiftCode1) {
            sb.append("Code: ").append(giftCode1.code).append(", Số lượng: ").append(giftCode1.countLeft).append("\b").append(", Ngày tạo: ")
                    .append(giftCode1.dateCreate).append("Ngày hết hạn").append(giftCode1.dateExpired);
        }

        NpcService.gI().createTutorial(p, 5073, sb.toString());
    }

    public void close1() {
        try (Connection con = GirlkunDB.getConnection()) {
            Statement s = con.createStatement();
            for (MaQuaTang1 maQuaTang : MaQuaTangManager1.gI().listGiftCode1) {
                if (maQuaTang != null) {
                    String query = "UPDATE giftcode1 set count_left = " + maQuaTang.countLeft + ", user_id_used = \"" + maQuaTang.listIdPlayer.toString() + "\" where id = " + maQuaTang.id;
                    s.executeUpdate(query);
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}