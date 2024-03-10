package com.girlkun.services.func;

import com.arriety.MaQuaTang1.MaQuaTangManager1;
import com.arriety.MaQuaTang.MaQuaTangManager;
import com.girlkun.database.GirlkunDB;
import com.girlkun.consts.ConstNpc;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.item.Item;
import com.girlkun.models.map.Zone;
import com.girlkun.models.npc.Npc;
import com.girlkun.models.npc.NpcManager;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.Player;
import com.girlkun.network.io.Message;
import com.girlkun.network.session.ISession;
import com.girlkun.server.Client;
import com.girlkun.services.Service;
import com.girlkun.services.GiftService;
import com.girlkun.services.GiftService1;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.ItemService;
import com.girlkun.services.NapThe;
//import com.girlkun.services.NapThe;
import com.girlkun.services.NpcService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import java.util.HashMap;
import java.util.Map;

public class Input {

    public static String LOAI_THE;
    public static String MENH_GIA;
    private static final Map<Integer, Object> PLAYER_ID_OBJECT = new HashMap<Integer, Object>();

    public static final int CHANGE_PASSWORD = 500;
    public static final int GIFT_CODE = 501;
    public static final int GIFT_CODE_1 = 499;
    public static final int FIND_PLAYER = 502;
    public static final int CHANGE_NAME = 503;
    public static final int CHOOSE_LEVEL_BDKB = 504;
    public static final int NAP_THE = 505;
    public static final int CHANGE_NAME_BY_ITEM = 506;
    public static final int GIVE_IT = 507;

    public static final int QUY_DOI_COIN = 508;
    public static final int QUY_DOI_HONG_NGOC = 509;

    public static final int TAI = 510;
    public static final int XIU = 511;

    public static final int DOI_RUONG_DONG_VANG = 515;
    public static final int DOI_RUONG_DONG_VANG2 = 516;
    public static final int QUY_MH = 517;
    public static final int QUY_DXL = 518;
    public static final int QUY_DXV = 519;

    public static final int NAP_COIN = 520;
    public static final int NAP_COIN_KEY = 521;
    public static final int NAP_COIN_ALL = 522;

    public static final int TD1 = 523;
    public static final int TD2 = 524;
    public static final int TD3 = 525;
    public static final int TD4 = 526;
    public static final int TD5 = 527;
    public static final int TD6 = 528;
    public static final int TD7 = 529;

    public static final int XD1 = 530;
    public static final int XD2 = 531;
    public static final int XD3 = 532;
    public static final int XD4 = 533;
    public static final int XD5 = 534;
    public static final int XD6 = 535;
    public static final int XD7 = 536;

    public static final int NM1 = 540;
    public static final int NM2 = 541;
    public static final int NM3 = 542;
    public static final int NM4 = 543;
    public static final int NM5 = 544;
    public static final int NM6 = 545;
    public static final int NM7 = 546;

    public static final byte NUMERIC = 0;
    public static final byte ANY = 1;
    public static final byte PASSWORD = 2;

    private static Input intance;

    private Input() {

    }

    public static final int SEND_ITEM_OP = 9999;

    public void createFormSenditem1(Player pl) {
        createForm(pl, SEND_ITEM_OP, "SEND Vật Phẩm Option",
                new SubInput("Tên người chơi", ANY),
                new SubInput("ID Trang Bị", NUMERIC),
                new SubInput("ID Option", NUMERIC),
                new SubInput("Param", NUMERIC),
                new SubInput("Số lượng", NUMERIC));
    }

    public static Input gI() {
        if (intance == null) {
            intance = new Input();
        }
        return intance;
    }

    public void doInput(Player player, Message msg) {
        try {
            String[] text = new String[msg.reader().readByte()];
            for (int i = 0; i < text.length; i++) {
                text[i] = msg.reader().readUTF();
            }
            switch (player.iDMark.getTypeInput()) {
                case NAP_COIN: {
                    String name = text[0];
                    int coin = Integer.valueOf(text[1]);
                    //int id = 0;
                    Player pl = Client.gI().getPlayer(name);

                    if (pl != null) {
                        pl.getSession().coin += coin;

                        //p2.getSession().id = id;
                        PreparedStatement ps = null;
                        PreparedStatement ps1 = null;
                        PreparedStatement ps2 = null;
                        // Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                        try ( Connection con = GirlkunDB.getConnection();) {
                            ps = con.prepareStatement("update account set coin = (coin + ?) where id = ? ");
                            ps.setInt(1, coin);
                            ps.setInt(2, pl.getSession().userId);
                            ps.executeUpdate();

                            ps1 = con.prepareStatement("update player set tongnap=(tongnap + ?/4) where account_id = ?");
                            ps1.setInt(1, coin);
                            ps1.setInt(2, pl.getSession().userId);
                            ps1.executeUpdate();

                            ps2 = con.prepareStatement("update player set tongnap2=(tongnap2 + ?/4) where account_id = ?");
                            ps2.setInt(1, coin);
                            ps2.setInt(2, pl.getSession().userId);
                            ps2.executeUpdate();
                        } catch (Exception e) {
                            Logger.logException(PlayerDAO.class, e, "Lỗi update coin " + pl.name);
                        } finally {
                            try {
                                ps.close();
                                ps1.close();
                                ps2.close();
                            } catch (SQLException ex) {
                                System.out.println("Lỗi khi update tongnap");
                            }
                        }
                        Service.getInstance().sendThongBao(player, "Đã nạp " + coin + " coin cho " + pl.name);

                    } else {
                        Service.getInstance().sendThongBao(player, "Người chơi không online");
                    }
                    break;
                }
                case NAP_COIN_KEY:
                    String Name = text[0];
                    int coin = Integer.parseInt(text[1]);
                    if (Client.gI().getPlayer(Name) != null) {
                        PlayerDAO.addvnd(Client.gI().getPlayer(Name), coin);
                        Service.getInstance().sendThongBao(player, "Đã nạp " + coin + " coin cho " + player.name);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case NAP_COIN_ALL:
                    String NameAll = text[0];
                    int coinAll = Integer.parseInt(text[1]);
                    PreparedStatement ps = null;
                    if (NameAll == "all") {
                        try ( Connection con1 = GirlkunDB.getConnection();) {
                            ps = con1.prepareStatement("update account set coin = (coin + ?) ");
                            ps.setInt(1, coinAll);
                            ps.executeUpdate();
                            Service.getInstance().sendThongBao(player, "Đã thưởng " + coinAll + " coin cho toàn sever ! ");
                        } catch (SQLException ex) {
                            System.out.println("Lỗi khi update coin all");
                        }
                    }
                    case SEND_ITEM_OP:
                    if (player.isAdmin()) {
                        int idItemBuff = Integer.parseInt(text[1]);
                        int idOptionBuff = Integer.parseInt(text[2]);
                        int slOptionBuff = Integer.parseInt(text[3]);
                        int slItemBuff = Integer.parseInt(text[4]);
                        Player pBuffItem = Client.gI().getPlayer(text[0]);
                        if (pBuffItem != null) {
                            String txtBuff = "Buff to player: " + pBuffItem.name + "\b";
                            if (idItemBuff == -1) {
                                pBuffItem.inventory.gold = Math.min(pBuffItem.inventory.gold + (long) slItemBuff, (Inventory.LIMIT_GOLD ));
                                txtBuff += slItemBuff + " vàng\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -2) {
                                pBuffItem.inventory.gem = Math.min(pBuffItem.inventory.gem + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc\b";
                                Service.getInstance().sendMoney(player);
                            } else if (idItemBuff == -3) {
                                pBuffItem.inventory.ruby = Math.min(pBuffItem.inventory.ruby + slItemBuff, 2000000000);
                                txtBuff += slItemBuff + " ngọc khóa\b";
                                Service.getInstance().sendMoney(player);
                            } else {
                                //Item itemBuffTemplate = ItemBuff.getItem(idItemBuff);
                                Item itemBuffTemplate = ItemService.gI().createNewItem((short) idItemBuff);
                                itemBuffTemplate.itemOptions.add(new Item.ItemOption(idOptionBuff, slOptionBuff));
                                itemBuffTemplate.quantity = slItemBuff;
                                txtBuff += "x" + slItemBuff + " " + itemBuffTemplate.template.name + "\b";
                                InventoryServiceNew.gI().addItemBag(pBuffItem, itemBuffTemplate);
                                InventoryServiceNew.gI().sendItemBags(pBuffItem);
                            }
                            NpcService.gI().createTutorial(player, 24, txtBuff);
                            if (player.id != pBuffItem.id) {
                                NpcService.gI().createTutorial(player, 24, txtBuff);
                            }
                        } else {
                            Service.getInstance().sendThongBao(player, "Player không online");
                        }
                        break;
                    }
                    break;
                case GIVE_IT:
                    String name = text[0];
                    int id = Integer.valueOf(text[1]);
                    int q = Integer.valueOf(text[2]);
                    if (Client.gI().getPlayer(name) != null) {
                        Item item = ItemService.gI().createNewItem(((short) id));
                        item.quantity = q;
                        InventoryServiceNew.gI().addItemBag(Client.gI().getPlayer(name), item);
                        InventoryServiceNew.gI().sendItemBags(Client.gI().getPlayer(name));
                        Service.gI().sendThongBao(Client.gI().getPlayer(name), "Nhận " + item.template.name + " từ " + player.name);

                    } else {
                        Service.gI().sendThongBao(player, "Không online");
                    }
                    break;

                case CHANGE_PASSWORD:
                    Service.gI().changePassword(player, text[0], text[1], text[2]);
                    break;
                case GIFT_CODE:
                    MaQuaTangManager.gI().giftCode(player, text[0]);
                    break;
                case GIFT_CODE_1:
                    MaQuaTangManager1.gI().giftCode1(player, text[0]);
                    break;
                case FIND_PLAYER:
                    Player pl = Client.gI().getPlayer(text[0]);
                    if (pl != null) {
                        NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_FIND_PLAYER, -1, "Ngài muốn..?",
                                new String[]{"Đi tới\n" + pl.name, "Gọi " + pl.name + "\ntới đây", "Đổi tên", "Ban", "Kick"},
                                pl);
                    } else {
                        Service.gI().sendThongBao(player, "Người chơi không tồn tại hoặc đang offline");
                    }
                    break;
                case CHANGE_NAME: {
                    Player plChanged = (Player) PLAYER_ID_OBJECT.get((int) player.id);
                    if (plChanged != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                        } else {
                            plChanged.name = text[0];
                            GirlkunDB.executeUpdate("update player set name = ? where id = ?", plChanged.name, plChanged.id);
                            Service.gI().player(plChanged);
                            Service.gI().Send_Caitrang(plChanged);
                            Service.gI().sendFlagBag(plChanged);
                            Zone zone = plChanged.zone;
                            ChangeMapService.gI().changeMap(plChanged, zone, plChanged.location.x, plChanged.location.y);
                            Service.gI().sendThongBao(plChanged, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            Service.gI().sendThongBao(player, "Đổi tên người chơi thành công");
                        }
                    }
                }
                break;

                case CHANGE_NAME_BY_ITEM: {
                    if (player != null) {
                        if (GirlkunDB.executeQuery("select * from player where name = ?", text[0]).next()) {
                            Service.gI().sendThongBao(player, "Tên nhân vật đã tồn tại");
                            createFormChangeNameByItem(player);
                        } else {
                            Item theDoiTen = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2006);
                            if (theDoiTen == null) {
                                Service.gI().sendThongBao(player, "Không tìm thấy thẻ đổi tên");
                            } else {
                                InventoryServiceNew.gI().subQuantityItemsBag(player, theDoiTen, 1);
                                player.name = text[0];
                                GirlkunDB.executeUpdate("update player set name = ? where id = ?", player.name, player.id);
                                Service.gI().player(player);
                                Service.gI().Send_Caitrang(player);
                                Service.gI().sendFlagBag(player);
                                Zone zone = player.zone;
                                ChangeMapService.gI().changeMap(player, zone, player.location.x, player.location.y);
                                Service.gI().sendThongBao(player, "Chúc mừng bạn đã có cái tên mới đẹp đẽ hơn tên ban đầu");
                            }
                        }
                    }
                }
                break;

                case TAI:
                    if (player != null) {
                        int sohntai = Integer.valueOf(text[0]);
                        if (sohntai > 500000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 500000 Hồng Ngọc!!");
                            return;
                        }
                        if (sohntai <= 0) {
                            Service.getInstance().sendThongBao(player, "Nu Nu ai cho mày bug!!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
//                    Item tv1 = null;
//                    for (Item item : player.inventory.itemsBag) {
//                        if (item.isNotNullItem() && item.template.id == 457) {
//                            tv1 = item;
//                            break;
//                        }
//                    }
                        try {
                            if (player.inventory.ruby >= sohntai) {
//                            InventoryServiceNew.gI().subQuantityItemsBag(player, tv1, sotvtai);
                                player.inventory.ruby -= sohntai;
                                Service.gI().sendMoney(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                                + sohntai + " Hồng Ngọc vào Tài" + "\nKết quả : Xỉu" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohntai + " Hồng Ngọc vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {

                                    if (player != null) {
//                                    Item tvthang = ItemService.gI().createNewItem((short) 457);
//                                    tvthang.quantity = (int) Math.round(sotvtai * 1.8);
//                                    InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        player.inventory.ruby += sohntai * 1.8;
                                        Service.gI().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohntai
                                                + " Hồng Ngọc vào Tài" + "\nKết quả : Tài" + "\n\nVề bờ");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng Ngọc để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Lỗi.");
                        }
                    }
                case XIU:
                    if (player != null) {
                        int sohnxiu = Integer.valueOf(text[0]);
                        if (sohnxiu > 500000) {
                            Service.getInstance().sendThongBao(player, "Tối đa 500000 Hồng Ngọc!!");
                            return;
                        }
                        if (sohnxiu <= 0) {
                            Service.getInstance().sendThongBao(player, "Nu Nu ai cho mày bug!!");
                            return;
                        }
                        if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 1) {
                            Service.getInstance().sendThongBao(player, "Ít nhất 2 ô trống trong hành trang!!");
                            return;
                        }
//                    Item tv2 = null;
//                    for (Item item : player.inventory.itemsBag) {
//                        if (item.isNotNullItem() && item.template.id == 457) {
//                            tv2 = item;
//                            break;
//                        }
//                    }
                        try {
                            if (player.inventory.ruby >= sohnxiu) {
//                            InventoryServiceNew.gI().subQuantityItemsBag(player, tv2, sotvxiu);
                                player.inventory.ruby -= sohnxiu;
                                Service.gI().sendMoney(player);
                                int TimeSeconds = 10;
                                Service.getInstance().sendThongBao(player, "Chờ 10 giây để biết kết quả.");
                                while (TimeSeconds > 0) {
                                    TimeSeconds--;
                                    Thread.sleep(1000);
                                }
                                int x = Util.nextInt(1, 6);
                                int y = Util.nextInt(1, 6);
                                int z = Util.nextInt(1, 6);
                                int tong = (x + y + z);
                                if (4 <= (x + y + z) && (x + y + z) <= 10) {
                                    if (player != null) {
//                                    Item tvthang = ItemService.gI().createNewItem((short) 457);
//                                    tvthang.quantity = (int) Math.round(sotvxiu * 1.8);
//                                    InventoryServiceNew.gI().addItemBag(player, tvthang);
                                        player.inventory.ruby += sohnxiu * 1.8;
                                        Service.gI().sendMoney(player);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra : " + x + " "
                                                + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohnxiu
                                                + " Hồng Ngọc vào Xỉu" + "\nKết quả : Xỉu" + "\n\nVề bờ");
                                        return;
                                    }
                                } else if (x == y && x == z) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "Số hệ thống quay ra : " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : " + sohnxiu + " Hồng Ngọc vào Xỉu" + "\nKết quả : Tam hoa" + "\nCòn cái nịt.");
                                        return;
                                    }
                                } else if ((x + y + z) > 10) {
                                    if (player != null) {
                                        Service.getInstance().sendThongBaoOK(player, "Kết quả" + "\nSố hệ thống quay ra là :"
                                                + " " + x + " " + y + " " + z + "\nTổng là : " + tong + "\nBạn đã cược : "
                                                + sohnxiu + " Hồng Ngọc vào Xỉu" + "\nKết quả : Tài" + "\nCòn cái nịt.");
                                        return;
                                    }
                                }
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ Hồng Ngọc để chơi.");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            Service.getInstance().sendThongBao(player, "Lỗi.");
                        }
                    }

                case CHOOSE_LEVEL_BDKB:
                    int level = Integer.parseInt(text[0]);
                    if (level >= 1 && level <= 110) {
                        Npc npc = NpcManager.getByIdAndMap(ConstNpc.QUY_LAO_KAME, player.zone.map.mapId);
                        if (npc != null) {
                            npc.createOtherMenu(player, ConstNpc.MENU_ACCEPT_GO_TO_BDKB,
                                    "Con có chắc chắn muốn tới bản đồ kho báu cấp độ " + level + "?",
                                    new String[]{"Đồng ý", "Từ chối"}, level);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }

//                    BanDoKhoBauService.gI().openBanDoKhoBau(player, (byte) );
                    break;
                case NAP_THE:

                    NapThe.SendCard(player, LOAI_THE, MENH_GIA, text[0], text[1]);
                    break;
                case DOI_RUONG_DONG_VANG:
                    int slruongcandoi = Integer.parseInt(text[0]);
                    int sldongxuvangbitru = slruongcandoi * 99;
                    if (slruongcandoi > 100) {
                        Service.getInstance().sendThongBao(player, "Tối đa 100 rương 1 lần!!");
                        return;
                    }
                    if (slruongcandoi <= 0) {
                        Service.getInstance().sendThongBao(player, "Số Lượng không hợp lệ!!");
                        return;
                    }
                    Item dongxuvang = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 1229) {
                            dongxuvang = item;
                            break;
                        }
                    }
                    if (dongxuvang != null && dongxuvang.quantity >= sldongxuvangbitru) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxuvang, sldongxuvangbitru);
                        Item ruongdongvang = ItemService.gI().createNewItem((short) 1230);
                        ruongdongvang.quantity = slruongcandoi;
                        InventoryServiceNew.gI().addItemBag(player, ruongdongvang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Chúc Mừng Bạn Đổi x" + slruongcandoi + " " + ruongdongvang.template.name + " Thành Công !");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ Đồng XU bạn còn thiếu " + (sldongxuvangbitru - dongxuvang.quantity) + " Đồng Xu Vàng nữa!");
                    }
                    break;
                case TD1: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2073, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD2: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2074, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD3: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2075, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD4: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2076, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD5: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2077, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD6: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2078, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case TD7: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2079, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM1: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2080, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM2: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2081, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM3: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2082, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM4: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2083, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM5: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2084, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM6: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2085, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case NM7: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2086, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD1: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2087, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD2: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2088, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD3: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2089, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD4: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2090, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD5: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2091, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD6: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2092, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case XD7: {
                    Item bikiep = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 2072) {
                            bikiep = item;
                            break;
                        }
                    }
                    if (bikiep != null && bikiep.quantity >= 9999) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, bikiep, 9999);
                        Item skill = ItemService.gI().createNewItem((short) 2093, 1);
                        InventoryServiceNew.gI().addItemBag(player, skill);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Bạn đã nhận được skill mới");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ bí kiếp !");
                    }
                }
                case DOI_RUONG_DONG_VANG2:
                    int slruongcandoi2 = Integer.parseInt(text[0]);
                    int sldongxuvangbitru2 = slruongcandoi2 * 5000;
                    if (slruongcandoi2 > 100) {
                        Service.getInstance().sendThongBao(player, "Tối đa 100 rương 1 lần!!");
                        return;
                    }
                    if (slruongcandoi2 <= 0) {
                        Service.getInstance().sendThongBao(player, "Số Lượng không hợp lệ!!");
                        return;
                    }
                    Item dongxuvang2 = null;
                    for (Item item : player.inventory.itemsBag) {
                        if (item.isNotNullItem() && item.template.id == 1229) {
                            dongxuvang2 = item;
                            break;
                        }
                    }
                    if (dongxuvang2 != null && dongxuvang2.quantity >= sldongxuvangbitru2) {
                        InventoryServiceNew.gI().subQuantityItemsBag(player, dongxuvang2, sldongxuvangbitru2);
                        Item ruongdongvang2 = ItemService.gI().createNewItem((short) 1284);
                        ruongdongvang2.quantity = slruongcandoi2;
                        InventoryServiceNew.gI().addItemBag(player, ruongdongvang2);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.getInstance().sendThongBao(player, "Chúc Mừng Bạn Đổi x" + slruongcandoi2 + " " + ruongdongvang2.template.name + " Thành Công !");
                    } else {
                        Service.getInstance().sendThongBao(player, "Không đủ Đồng Xu bạn còn thiếu " + (sldongxuvangbitru2 - dongxuvang2.quantity) + " Đồng Xu Vàng nữa!");
                    }
                    break;
                case QUY_DOI_COIN:
                    int ratioGold = 1; // tỉ lệ đổi tv
                    int coinGold = 1; // là cái loz
                    int goldTrade = Integer.parseInt(text[0]);
                    if (goldTrade <= 0 || goldTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "giới hạn");
                    } else if (player.getSession().coin >= goldTrade * coinGold) {
                        PlayerDAO.subvnd(player, goldTrade * coinGold);
                        Item thoiVang = ItemService.gI().createNewItem((short) 861, goldTrade * 1);// x3
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + goldTrade * ratioGold
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coin + " không đủ để quy "
                                + " đổi " + goldTrade + " Hồng Ngọc " + " " + "bạn cần thêm" + (player.getSession().coin - goldTrade));
                    }
                    break;
                case QUY_DOI_HONG_NGOC:
                    //int ratioGem = 10; // tỉ lệ đổi tv
                    //int coinGem = 1000; // là cái loz
                    int gemTrade = Integer.parseInt(text[0]);
                    if (gemTrade <= 0 || gemTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "giới hạn");
                    } else if (player.getSession().coin >= gemTrade) {
                        PlayerDAO.subvnd(player, gemTrade);
                        Item thoiVang = ItemService.gI().createNewItem((short) 457, gemTrade / 10);// x4
                        thoiVang.itemOptions.add(new Item.ItemOption(30, 0));
                        InventoryServiceNew.gI().addItemBag(player, thoiVang);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + gemTrade / 10
                                + " " + thoiVang.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coin + " không đủ để quy "
                                + " đổi " + gemTrade / 10 + " Thỏi Vàng" + " " + "bạn cần thêm" + (player.getSession().coin - gemTrade));
                    }
                    break;
                case QUY_MH: {
                    //int ratioMH = 50; // tỉ lệ đổi tv
                    int coinMH = 100; // là cái loz
                    int MHTrade = Integer.parseInt(text[0]); //200
                    if (MHTrade < 200 || MHTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "Tối thiểu 200v");
                    } else if (player.getSession().coin >= MHTrade * coinMH) {
                        PlayerDAO.subvnd(player, MHTrade * coinMH);
                        Item mh = ItemService.gI().createNewItem((short) 934, MHTrade);// x4
                        InventoryServiceNew.gI().addItemBag(player, mh);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + MHTrade
                                + " " + mh.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coin + " không đủ để quy "
                                + " đổi " + MHTrade + " MẢNH HỒN" + " " + "bạn cần thêm" + (MHTrade * coinMH - player.getSession().coin));
                    }
                    break;
                }
                case QUY_DXV: {
                    //int ratioDXV = 1; // tỉ lệ đổi tv
                    int coinDXV = 1; // là cái loz
                    int DXVTrade = Integer.parseInt(text[0]);
                    if (DXVTrade <= 0 || DXVTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "giới hạn");
                    } else if (player.getSession().coin >= DXVTrade * coinDXV) {
                        PlayerDAO.subvnd(player, DXVTrade * coinDXV);
                        Item dxv = ItemService.gI().createNewItem((short) 1229, DXVTrade);// x4
                        InventoryServiceNew.gI().addItemBag(player, dxv);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + DXVTrade
                                + "Đồng xu vàng " + dxv.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coin + " không đủ để quy "
                                + " đổi " + DXVTrade + " Đồng xu vàng" + " " + "bạn cần thêm" + (DXVTrade * coinDXV - player.getSession().coin));
                    }
                    break;
                }
                case QUY_DXL: {
                    //int ratioDXL = 50; // tỉ lệ đổi tv
                    int coinDXL = 500; // là cái loz
                    int DXLTrade = Integer.parseInt(text[0]); //1

                    if (DXLTrade < 1 || DXLTrade >= 50000000) {
                        Service.gI().sendThongBao(player, "Tối thiểu 1 viên"); //1v 500
                    } else if (player.getSession().coin >= DXLTrade * coinDXL) { //10k >= 200 x 50
                        PlayerDAO.subvnd(player, DXLTrade * coinDXL);
                        Item dxl = ItemService.gI().createNewItem((short) 935, DXLTrade);// 
                        InventoryServiceNew.gI().addItemBag(player, dxl);
                        InventoryServiceNew.gI().sendItemBags(player);
                        Service.gI().sendThongBao(player, "bạn nhận được " + DXLTrade + " " + dxl.template.name);
                    } else {
                        Service.gI().sendThongBao(player, "Số tiền của bạn là " + player.getSession().coin + " không đủ để quy "
                                + " đổi " + DXLTrade + " ĐÁ XANH LAM" + " " + "bạn cần thêm" + (DXLTrade * coinDXL - player.getSession().coin));
                    }
                    break;
                }

            }
        } catch (Exception e) {
        }
    }

    public void createForm(Player pl, int typeInput, String title, SubInput... subInputs) {
        pl.iDMark.setTypeInput(typeInput);
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            pl.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createForm(ISession session, int typeInput, String title, SubInput... subInputs) {
        Message msg;
        try {
            msg = new Message(-125);
            msg.writer().writeUTF(title);
            msg.writer().writeByte(subInputs.length);
            for (SubInput si : subInputs) {
                msg.writer().writeUTF(si.name);
                msg.writer().writeByte(si.typeInput);
            }
            session.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void createFormChangePassword(Player pl) {
        createForm(pl, CHANGE_PASSWORD, "Quên Mật Khẩu", new SubInput("Nhập mật khẩu đã quên", PASSWORD),
                new SubInput("Mật khẩu mới", PASSWORD),
                new SubInput("Nhập lại mật khẩu mới", PASSWORD));
    }

    public void createFormGiveItem(Player pl) {
        createForm(pl, GIVE_IT, "Tặng vật phẩm", new SubInput("Tên", ANY), new SubInput("Id Item", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormGiftCode(Player pl) {
        createForm(pl, GIFT_CODE, "Gift code Dân thường", new SubInput("Gift-code", ANY));
    }

    public void createFormGiftCode1(Player pl) {
        createForm(pl, GIFT_CODE_1, "Gift code Đại gia", new SubInput("Gift-code", ANY));
    }

    public void createFormFindPlayer(Player pl) {
        createForm(pl, FIND_PLAYER, "Tìm kiếm người chơi", new SubInput("Tên người chơi", ANY));
    }

    public void TAI(Player pl) {
        createForm(pl, TAI, "Nhập số Hồng ngọc (Tài)", new SubInput("Số Hồng ngọc", ANY));
    }

    public void XIU(Player pl) {
        createForm(pl, XIU, "Nhập số Hồng ngọc (Xỉu)", new SubInput("Số Hồng ngọc", ANY));
    }

    public void TD1(Player pl) {
        createForm(pl, TD1, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 1 ?");
    }

    public void TD2(Player pl) {
        createForm(pl, TD2, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 2 ?");
    }

    public void TD3(Player pl) {
        createForm(pl, TD3, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 3 ?");
    }

    public void TD4(Player pl) {
        createForm(pl, TD4, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 4 ?");
    }

    public void TD5(Player pl) {
        createForm(pl, TD5, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 5 ?");
    }

    public void TD6(Player pl) {
        createForm(pl, TD6, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 6 ?");
    }

    public void TD7(Player pl) {
        createForm(pl, TD7, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Super Kame cấp 7 ?");
    }

    public void XD1(Player pl) {
        createForm(pl, XD1, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 1 ?");
    }

    public void XD2(Player pl) {
        createForm(pl, XD2, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 2 ?");
    }

    public void XD3(Player pl) {
        createForm(pl, XD3, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 3 ?");
    }

    public void XD4(Player pl) {
        createForm(pl, XD4, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 4 ?");
    }

    public void XD5(Player pl) {
        createForm(pl, XD5, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 5 ?");
    }

    public void XD6(Player pl) {
        createForm(pl, XD6, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 6 ?");
    }

    public void XD7(Player pl) {
        createForm(pl, XD7, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Liên hoàn chưởng cấp 7 ?");
    }

    public void NM1(Player pl) {
        createForm(pl, NM1, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 1 ?");
    }

    public void NM2(Player pl) {
        createForm(pl, NM2, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 2 ?");
    }

    public void NM3(Player pl) {
        createForm(pl, NM3, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 3 ?");
    }

    public void NM4(Player pl) {
        createForm(pl, NM4, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 4 ?");
    }

    public void NM5(Player pl) {
        createForm(pl, NM5, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 5 ?");
    }

    public void NM6(Player pl) {
        createForm(pl, NM6, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 6 ?");
    }

    public void NM7(Player pl) {
        createForm(pl, NM7, "Ngươi có chắc chắn đổi 9999 bí kiếp để lấy Sách Ma phong ba cấp 7 ?");
    }

    public void createFormNapThe(Player pl, String loaiThe, String menhGia) {
        LOAI_THE = loaiThe;
        MENH_GIA = menhGia;
        createForm(pl, NAP_THE, "Nạp thẻ", new SubInput("Số Seri", ANY), new SubInput("Mã thẻ", ANY));
    }

    public void createFormQDTV(Player pl) {

        createForm(pl, QUY_DOI_COIN, "Quy đổi Hồng Ngọc tỉ lệ 1:1"
                + "\nVí dụ: Nhập 10.000, nhận 10.000 Hồng ngọc, trừ 10.000đ ", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
        /*+ "\nLiên hệ Key vàng trong box zalo để nạp tiền "
                + "\nCó thể nạp card trên Website: "
                + "\nĐăng Nhập và Chọn nạp VNĐ "
                + "\nLưu Ý : Khi nạp tiền trên Website vui lòng chọn đúng mệnh giá. ", new SubInput("Nhập số lượng muốn đổi", NUMERIC));*/
    }

    public void createFormQDHN(Player pl) {

        createForm(pl, QUY_DOI_HONG_NGOC, "Quy đổi Thỏi Vàng"
                // + "\nNhập 10 Có nghĩa là  10.000đ"
                + "Ví dụ nhập 10.000 VNĐ = 1000 tv", new SubInput("Nhập số VNĐ muốn đổi", NUMERIC));
        // + "\nLiên hệ Key vàng trong box zalo để nạp tiền "
        //  + "\nCó thể nạp card trên Website: "
        //  + "\nĐăng Nhập và Chọn nạp VNĐ "
        //  + "\nLưu Ý : Khi nạp tiền trên Website vui lòng chọn đúng mệnh giá. ", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public void createFormChangeName(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormNapCoin(Player pl) {
        createForm(pl, NAP_COIN, "Nạp coin", new SubInput("Tên nhân vật", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormNapCoinKey(Player pl) {
        createForm(pl, NAP_COIN_KEY, "Nạp coin", new SubInput("Tên nhân vật", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormNapCoinAll(Player p1) {
        createForm(p1, NAP_COIN_ALL, "Thưởng coin", new SubInput("Pass", ANY), new SubInput("Số lượng", ANY));
    }

    public void createFormNapTienAdmin(Player pl, Player plChanged) {
        PLAYER_ID_OBJECT.put((int) pl.id, plChanged);
        createForm(pl, CHANGE_NAME, "Đổi tên " + plChanged.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChangeNameByItem(Player pl) {
        createForm(pl, CHANGE_NAME_BY_ITEM, "Đổi tên " + pl.name, new SubInput("Tên mới", ANY));
    }

    public void createFormChooseLevelBDKB(Player pl) {
        createForm(pl, CHOOSE_LEVEL_BDKB, "Chọn cấp độ", new SubInput("Cấp độ (1-110)", NUMERIC));
    }

    public void createFormTradeRuongDongVang(Player pl) {
        createForm(pl, DOI_RUONG_DONG_VANG, "Nhập Số Lượng Muốn Đổi", new SubInput("Số Lượng", NUMERIC));
    }

    public void createFormTradeRuongDongVang2(Player pl) {
        createForm(pl, DOI_RUONG_DONG_VANG2, "Nhập Số Lượng Muốn Đổi", new SubInput("Số Lượng", NUMERIC));
    }

    public void createFormQDMH(Player pl) {

        createForm(pl, QUY_DXL, "Quy đổi Đá xanh lam"
                + "\nTỉ Lệ: 1 viên = 500 VNĐ"
                + "\nNhập số đá xanh lam muốn đổi ", new SubInput("Nhập số lượng đá muốn đổi", NUMERIC));
    }

    public void createFormQDDXL(Player pl) {

        createForm(pl, QUY_MH, "Quy đổi Mảnh hồn"
                + "\nTỉ Lệ: 200 mảnh = 20000 VNĐ"
                + "\nNhập tối thiểu 200 ", new SubInput("Nhập số lượng mảnh muốn đổi", NUMERIC));
    }

    public void createFormQDDXV(Player pl) {

        createForm(pl, QUY_DXV, "Quy đổi Đồng XU Vàng"
                + "\nTỉ Lệ 10k Đồng xu = 10k VNĐ"
                + "\nNhập số đồng xu muốn đổi ", new SubInput("Nhập số lượng muốn đổi", NUMERIC));
    }

    public static class SubInput {

        private String name;
        private byte typeInput;

        public SubInput(String name, byte typeInput) {
            this.name = name;
            this.typeInput = typeInput;
        }
    }

}
