package com.girlkun.models.npc;

import com.arriety.MaQuaTang.MaQuaTangManager;
import com.girlkun.consts.ConstMap;
import com.girlkun.services.*;
import com.girlkun.consts.ConstNpc;
import com.girlkun.consts.ConstPlayer;
import com.girlkun.consts.ConstTask;
import com.girlkun.database.GirlkunDB;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.boss.BossID;
import com.girlkun.models.boss.BossManager;

import com.girlkun.models.boss.list_boss.NhanBan;
import com.girlkun.models.clan.Clan;
import com.girlkun.models.clan.ClanMember;

import java.util.Date;
import java.util.logging.Level;

import java.util.HashMap;
import java.util.List;

import com.girlkun.services.func.ChangeMapService;
import com.girlkun.services.func.SummonDragon;

import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_1;
import static com.girlkun.services.func.SummonDragon.SHENRON_1_STAR_WISHES_2;
import static com.girlkun.services.func.SummonDragon.SHENRON_SAY;

import com.girlkun.models.player.Player;
import com.girlkun.models.item.Item;
import com.girlkun.models.item.Item.ItemOption;
import com.girlkun.models.map.Map;
import com.girlkun.models.map.Zone;
import com.girlkun.models.map.blackball.BlackBallWar;
import com.girlkun.models.map.MapMaBu.MapMaBu;
import com.girlkun.models.map.bdkb.Bdkb;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.map.doanhtrai.DoanhTraiService;
import com.girlkun.models.map.nguhanhson.nguhs;
import com.girlkun.models.player.Inventory;
import com.girlkun.models.player.NPoint;
import com.girlkun.models.matches.PVPService;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.matches.pvp.DaiHoiVoThuatService;
import com.girlkun.models.shop.ShopServiceNew;
import com.girlkun.models.skill.Skill;
import com.girlkun.server.Client;
import com.girlkun.server.Maintenance;
import com.girlkun.server.Manager;
import com.girlkun.services.func.CombineServiceNew;
import com.girlkun.services.func.Input;
import com.girlkun.services.func.LuckyRound;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import com.girlkun.services.func.ChonAiDay;
import com.kygui.ItemKyGui;
import com.kygui.ShopKyGuiService;
import com.kygui.ShopKyGuiManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.logging.Level;

import java.util.logging.Level;

public class NpcFactory {

    private static final int COST_HD = 50000000;

    private static boolean nhanVang = false;
    private static boolean nhanDeTu = false;

    //playerid - object
    public static final java.util.Map<Long, Object> PLAYERID_OBJECT = new HashMap<Long, Object>();

    private NpcFactory() {

    }

    private static Npc trungLinhThu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đổi Trứng Linh thú cần:\b|7|X99 Hồn Linh Thú + 1 Tỷ vàng", "Đổi Trứng\nLinh thú", "Nâng Chiến Linh", "Mở chỉ số ẩn\nChiến Linh", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Item honLinhThu = null;
                                    try {
                                        honLinhThu = InventoryServiceNew.gI().findItemBag(player, 2029);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (honLinhThu == null || honLinhThu.quantity < 99) {
                                        this.npcChat(player, "Bạn không đủ 99 Hồn Linh thú");
                                    } else if (player.inventory.gold < 1_000_000_000) {
                                        this.npcChat(player, "Bạn không đủ 1 Tỷ vàng");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        player.inventory.gold -= 1_000_000_000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, honLinhThu, 99);
                                        Service.gI().sendMoney(player);
                                        Item trungLinhThu = ItemService.gI().createNewItem((short) 2028);
                                        InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1 Trứng Linh thú");
                                    }
                                    break;
                                }

                                case 1:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.Nang_Chien_Linh);
                                    break;
                                case 2:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_Chien_Linh);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.Nang_Chien_Linh:
                                case CombineServiceNew.MO_CHI_SO_Chien_Linh:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc kyGui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, 0, "Cửa hàng chúng tôi chuyên mua bán hàng hiệu, hàng độc, cảm ơn bạn đã ghé thăm.", "Hướng\ndẫn\nthêm", "Mua bán\nKý gửi", "Từ chối");
                }
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    switch (select) {
                        case 0:
                            Service.getInstance().sendPopUpMultiLine(pl, tempId, avartar, "Cửa hàng chuyên nhận ký gửi mua bán vật phẩm\bChỉ với 5 hồng ngọc\bGiá trị ký gửi 10k-200Tr vàng hoặc 2-2k ngọc\bMột người bán, vạn người mua, mại dô, mại dô");
                            break;
                        case 1:
                            this.npcChat(pl, "Chức năng đang bảo trì, vui lòng thử lại sau");
                            //ShopKyGuiService.gI().openShopKyGui(pl);
                            break;

                    }
                }
            }
        };
    }

    private static Npc poTaGe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đa vũ trụ song song \b|7|Con muốn gọi con trong đa vũ trụ \b|1|Với giá 200tr vàng không?", "Gọi Boss\nNhân bản", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 140) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: {
                                    Boss oldBossClone = BossManager.gI().getBossById(Util.createIdBossClone((int) player.id));
                                    if (oldBossClone != null) {
                                        this.npcChat(player, "Nhà ngươi hãy tiêu diệt Boss lúc trước gọi ra đã, con boss đó đang ở khu " + oldBossClone.zone.zoneId);
                                    } else if (player.inventory.gold < 200_000_000) {
                                        this.npcChat(player, "Nhà ngươi không đủ 200 Triệu vàng ");
                                    } else {
                                        List<Skill> skillList = new ArrayList<>();
                                        for (byte i = 0; i < player.playerSkill.skills.size(); i++) {
                                            Skill skill = player.playerSkill.skills.get(i);
                                            if (skill.point > 0) {
                                                skillList.add(skill);
                                            }
                                        }
                                        int[][] skillTemp = new int[skillList.size()][3];
                                        for (byte i = 0; i < skillList.size(); i++) {
                                            Skill skill = skillList.get(i);
                                            if (skill.point > 0) {
                                                skillTemp[i][0] = skill.template.id;
                                                skillTemp[i][1] = skill.point;
                                                skillTemp[i][2] = skill.coolDown;
                                            }
                                        }
                                        BossData bossDataClone = new BossData(
                                                "Nhân Bản" + player.name,
                                                player.gender,
                                                new short[]{player.getHead(), player.getBody(), player.getLeg(), player.getFlagBag(), player.idAura, player.getEffFront()},
                                                player.nPoint.dame,
                                                new long[]{player.nPoint.hpMax},
                                                new int[]{140},
                                                skillTemp,
                                                new String[]{"|-2|Boss nhân bản đã xuất hiện rồi"}, //text chat 1
                                                new String[]{"|-1|Ta sẽ chiếm lấy thân xác của ngươi hahaha!"}, //text chat 2
                                                new String[]{"|-1|Lần khác ta sẽ xử đẹp ngươi"}, //text chat 3
                                                60
                                        );

                                        try {
                                            new NhanBan(Util.createIdBossClone((int) player.id), bossDataClone, player.zone);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        //trừ vàng khi gọi boss
                                        player.inventory.gold -= 200_000_000;
                                        Service.gI().sendMoney(player);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    private static Npc quyLaoKame(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (TaskService.gI().getIdTask(player) == ConstTask.TASK_18_4) {
                            TaskService.gI().sendNextTaskMain(player);
                        } else if (TaskService.gI().getIdTask(player) == ConstTask.TASK_16_4) {
                            TaskService.gI().sendNextTaskMain(player);
                        }
                        if (player.getSession().is_gift_box) {
//                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào?", "Giải tán bang hội", "Nhận quà\nđền bù");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Chào con, con muốn ta giúp gì nào? bdkb mở từ cả ngày up hồng ngọc trong đó", "Giải tán bang hội", "Lãnh địa Bang Hội", "Kho báu dưới biển");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                if (clan != null) {
                                    ClanMember cm = clan.getClanMember((int) player.id);
                                    if (cm != null) {
                                        if (clan.members.size() > 1) {
                                            Service.gI().sendThongBao(player, "Bang phải còn một người");
                                            break;
                                        }
                                        if (!clan.isLeader(player)) {
                                            Service.gI().sendThongBao(player, "Phải là bang chủ");
                                            break;
                                        }
//                                        
                                        NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DISSOLUTION_CLAN, -1, "Con có chắc chắn muốn giải tán bang hội không? Ta cho con 2 lựa chọn...",
                                                "Yes you do!", "Từ chối!");
                                    }
                                    break;
                                }
                                Service.gI().sendThongBao(player, "Có bang hội đâu ba!!!");
                                break;
                            case 1:
                                if (player.getSession().player.nPoint.power >= 39999999999L) {

                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ 40 tỷ sức mạnh để vào");
                                }
                                break; // qua lanh dia
                            case 2:
                                Bdkb.gI().setTimeJoinBdkb();

                                long now = System.currentTimeMillis();
                                if (now > Bdkb.TIME_OPEN_BDKB && now < Bdkb.TIME_CLOSE_BDKB) {
                                    Item bdkb = null;
                                    try {
                                        bdkb = InventoryServiceNew.gI().findItemBag(player, 611);
                                    } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                    }
                                    if (bdkb == null || bdkb.quantity < 1) {
                                        this.npcChat(player, "Bạn không có bản đồ kho báu!");
                                        break;
                                    } else if (player.inventory.gold < 500_000_000) {
                                        this.npcChat(player, "Bạn không đủ 500tr vàng");
                                    } else {
                                        player.inventory.gold -= 500000000;
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, bdkb, 1);
                                        PlayerService.gI().sendInfoHpMpMoney(player);
                                        ChangeMapService.gI().goToDBKB(player);
                                        break;
                                    }
                                } else {
                                    Service.getInstance().sendThongBao(player, "Chưa đến thời gian");
                                    break;
                                }
                        }

                    }
                }
            }
        };
    }

    public static Npc truongLaoGuru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc vuaVegeta(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc ongGohan_ongMoori_ongParagus(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (TaskService.gI().getIdTask(player) == ConstTask.TASK_4_3) {
                            TaskService.gI().sendNextTaskMain(player);
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Tốt lắm, con hoàn thành rất xuất xắc", "Đóng");
                        } else if (TaskService.gI().getIdTask(player) == ConstTask.TASK_10_1) {
                            TaskService.gI().sendNextTaskMain(player);
                        }
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Nạp Lần Đầu Đi Con!"
                                        .replaceAll("%1", player.gender == ConstPlayer.TRAI_DAT ? "Quy lão Kamê"
                                                : player.gender == ConstPlayer.NAMEC ? "Trưởng lão Guru" : "Vua Vegeta"),
                                "Đổi mật khẩu", "Nhận ngọc xanh", "Nhận đệ tử", "Cách kiếm vàng", "Mở\nThành Viên", "GiftCode\nthường", "Giftcode\nkhi MTV", "Mở map mới");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                Input.gI().createFormChangePassword(player);
                                break;
                            case 1:
                                if (player.inventory.gem == 200000) {
                                    this.npcChat(player, "Bú ít thôi con");
                                    break;
                                }
                                player.inventory.gem = 200000;
                                Service.gI().sendMoney(player);
                                Service.gI().sendThongBao(player, "Bạn vừa nhận được 200K ngọc xanh");
                                break;
                            case 2:
                                if (player.pet == null) {
                                    PetService.gI().createNormalPet(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                } else {
                                    this.npcChat(player, "Bạn đã có rồi");
                                }
                                break;
                            case 3:
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_CAY);
                                break;
                            case 4:
                                if (player.getSession().actived) {
                                    Service.gI().sendThongBao(player, "Bạn đã mở thành viên rồi");
                                    return;
                                }
                                if (player.tongnap < 20000) {
                                    Service.gI().sendThongBao(player, "Không đủ tiền");
                                    return;
                                }
                                if (PlayerDAO.subvnd(player, 20000)) {
                                    player.getSession().actived = true;

                                    if (PlayerDAO.activedUser(player)) {
                                        PlayerDAO.subtn(player, 20000);
                                        player.inventory.ruby += 10000000;
                                        Item tv = ItemService.gI().createNewItem((short) 457);
                                        tv.quantity = 10000000;
                                        tv.itemOptions.add(new Item.ItemOption(30, 0));
                                        InventoryServiceNew.gI().addItemBag(player, tv);
                                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                                "Bạn đã MTV thành công và nhận được 10tr hồng ngọc và 10tr thỏi vàng. Vui lòng thoát game vào lại", "Đóng");
                                        break;
                                    } else {
                                        Service.gI().sendThongBao(player, "Đã có lỗi xẩy ra khi kích hoạt tài khoản, vui long liên hệ admin nếu bị trừ tiền mà không kích hoạt được, chụp lại thông báo này");
                                        break;
                                    }
                                }
                                break;
                            case 5:
                                Input.gI().createFormGiftCode(player);
                                break;
                            case 6:
                                if (player.getSession().actived) {
                                    Input.gI().createFormGiftCode1(player);
                                    break;
                                } else {
                                    Service.gI().sendThongBao(player, "Chỉ có đại gia mới dùng được chức năng này");
                                    break;
                                }
                            case 7:
                                if (player.mapmoi == 0) {
                                    if (player.getSession().coin >= 100000) {
                                        if (player.getSession().actived) {
                                            PlayerDAO.subvnd(player, 100000);
                                            //player.getSession().coin-=100000;
                                            OpenPowerService.gI().mapmoi(player);
                                            player.mapmoi = 1;
                                            Service.gI().sendThongBao(player, "Mở map mới thành công");
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không đủ 100k COIN");
                                    }

                                } else {
                                    Service.gI().sendThongBao(player, "Bạn đã mở map mới rồi mà ?");
                                }

                                break;
                            case 8:

                                if (player.nhanngoc == 0) {
                                    if (player.getSession().actived) {

                                        player.inventory.ruby = player.inventory.ruby + 100000;
                                        player.nhanngoc = 1;
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được 100K Hồng Ngọc");
                                        break;

                                    }
                                    if (!player.getSession().actived) {

                                        player.inventory.ruby = player.inventory.ruby + 50000;
                                        player.nhanngoc = 1;
                                        Service.gI().sendMoney(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được 50K Hồng Ngọc");
                                        break;
                                    } else {
                                        Service.gI().sendThongBao(player, "Bú ít thôi ");
                                    }
                                    break;
                                } else {
                                    Service.gI().sendThongBao(player, "Đợi tuần sau đi ");
                                    break;
                                }
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.QUA_TAN_THU) {
                        switch (select) {
                            case 2:
                                if (nhanDeTu) {
                                    if (player.pet == null) {
                                        PetService.gI().createNormalPet(player);
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được đệ tử");
                                    } else {
                                        this.npcChat("Con đã nhận đệ tử rồi");
                                    }
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_THUONG) {
                        switch (select) {

                        }
                    }
                }

            }

        };
    }

    public static Npc bulmaQK(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Cậu cần trang bị gì cứ đến chỗ tôi nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.TRAI_DAT) {
                                    ShopServiceNew.gI().opendShop(player, "BUNMA", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi cưng, chị chỉ bán đồ cho người Trái Đất", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc dende(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        if (player.idNRNM != -1) {
                            if (player.zone.map.mapId == 7) {
                                this.createOtherMenu(player, 1, "Ồ, ngọc rồng namếc, bạn thật là may mắn\nnếu tìm đủ 7 viên sẽ được Rồng Thiêng Namếc ban cho điều ước", "Hướng\ndẫn\nGọi Rồng", "Gọi rồng", "Từ chối");
                            }
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Anh cần trang bị gì cứ đến chỗ em nhé", "Cửa\nhàng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.NAMEC) {
                                    ShopServiceNew.gI().opendShop(player, "DENDE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Xin lỗi anh, em chỉ bán đồ cho dân tộc Namếc", "Đóng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == 1) {
                        if (player.zone.map.mapId == 7 && player.idNRNM != -1) {
                            if (player.idNRNM == 353) {
                                NgocRongNamecService.gI().tOpenNrNamec = System.currentTimeMillis() + 86400000;
                                NgocRongNamecService.gI().firstNrNamec = true;
                                NgocRongNamecService.gI().timeNrNamec = 0;
                                NgocRongNamecService.gI().doneDragonNamec();
                                NgocRongNamecService.gI().initNgocRongNamec((byte) 1);
                                NgocRongNamecService.gI().reInitNrNamec((long) 86399000);
                                SummonDragon.gI().summonNamec(player);
                            } else {
                                Service.gI().sendThongBao(player, "Anh phải có viên ngọc rồng Namếc 1 sao");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc appule(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi cần trang bị gì cứ đến chỗ ta nhé", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop
                                if (player.gender == ConstPlayer.XAYDA) {
                                    ShopServiceNew.gI().opendShop(player, "APPULE", true);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Về hành tinh hạ đẳng của ngươi mà mua đồ cùi nhé. Tại đây ta chỉ bán đồ cho người Xayda thôi", "Đóng");
                                }
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc drDrief(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (this.mapId == 84) {
                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                pl.gender == ConstPlayer.TRAI_DAT ? "Đến\nTrái Đất" : pl.gender == ConstPlayer.NAMEC ? "Đến\nNamếc" : "Đến\nXayda");
                    } else if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nNamếc", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 84) {
                        ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 24, -1, -1);
                    } else if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cargo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                    "Tàu Vũ Trụ của ta có thể đưa cậu đến hành tinh khác chỉ trong 3 giây. Cậu muốn đi đâu?",
                                    "Đến\nTrái Đất", "Đến\nXayda", "Siêu thị");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                break;
                            case 1:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 26, -1, -1);
                                break;
                            case 2:
                                ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc cui(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_FIND_BOSS = 50000000;

            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(pl, this)) {
                        if (pl.playerTask.taskMain.id == 7) {
                            NpcService.gI().createTutorial(pl, this.avartar, "Hãy lên đường cứu đứa bé nhà tôi\n"
                                    + "Chắc bây giờ nó đang sợ hãi lắm rồi");
                        } else {
                            if (this.mapId == 19) {

                                int taskId = TaskService.gI().getIdTask(pl);
                                switch (taskId) {
                                    case ConstTask.TASK_19_0:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_KUKU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nKuku\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_1:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_MAP_DAU_DINH,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nMập đầu đinh\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến\nNappa", "Từ chối");
                                        break;
                                    case ConstTask.TASK_19_2:
                                        this.createOtherMenu(pl, ConstNpc.MENU_FIND_RAMBO,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến chỗ\nRambo\n(" + Util.numberToMoney(COST_FIND_BOSS) + " vàng)", "Đến\nNappa", "Từ chối");
                                        break;
                                    default:
                                        this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                                "Đội quân của Fide đang ở Thung lũng Nappa, ta sẽ đưa ngươi đến đó",
                                                "Đến Cold", "Đến\nNappa", "Từ chối");

                                        break;
                                }
                            } else if (this.mapId == 68) {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Ngươi muốn về Thành Phố Vegeta", "Đồng ý", "Từ chối");
                            } else {
                                this.createOtherMenu(pl, ConstNpc.BASE_MENU,
                                        "Tàu vũ trụ Xayda sử dụng công nghệ mới nhất, "
                                        + "có thể đưa ngươi đi bất kỳ đâu, chỉ cần trả tiền là được.",
                                        "Đến\nTrái Đất", "Đến\nNamếc", "Siêu thị");
                            }
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 26) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24, -1, -1);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 25, -1, -1);
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 84, -1, -1);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 19) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.taskMain.id >= 29) {
                                        NpcService.gI().createTutorial(player, this.avartar, "Hãy lên đường tìm hiểu về vùng đất băng giá\n"
                                                + "Nơi đây có nhiều thứ mới mẻ có thể giúp bạn");
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 109, -1, 295);
                                    } else {
                                        this.npcChat(player, "Sức mạnh của bạn chưa đủ! Hãy luyện tập thêm!");
                                    }
                                    break;

                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_KUKU) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.KUKU);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_MAP_DAU_DINH) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.MAP_DAU_DINH);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_FIND_RAMBO) {
                            switch (select) {
                                case 0:
                                    Boss boss = BossManager.gI().getBossById(BossID.RAMBO);
                                    if (boss != null && !boss.isDie()) {
                                        if (player.inventory.gold >= COST_FIND_BOSS) {
                                            Zone z = MapService.gI().getMapCanJoin(player, boss.zone.map.mapId, boss.zone.zoneId);
                                            if (z != null && z.getNumOfPlayers() < z.maxPlayer) {
                                                player.inventory.gold -= COST_FIND_BOSS;
                                                ChangeMapService.gI().changeMap(player, boss.zone, boss.location.x, boss.location.y);
                                                Service.gI().sendMoney(player);
                                            } else {
                                                Service.gI().sendThongBao(player, "Khu vực đang full.");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ vàng, còn thiếu "
                                                    + Util.numberToMoney(COST_FIND_BOSS - player.inventory.gold) + " vàng");
                                        }
                                        break;
                                    }
                                    Service.gI().sendThongBao(player, "Chết rồi ba...");
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 68, -1, 90);
                                    break;
                            }
                        }
                    }
                    if (this.mapId == 68) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 19, -1, 1100);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }
     public static Npc toSuKaiO(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Shop đồ Cho tân thủ giá cực hạt dẻ đây, mại dô mại dô!!!!",
                                "Shop Tân Thủ", "Khu Vực\nLuyện Tập", "Đóng");
                  
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                 
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                        ShopServiceNew.gI().opendShop(player, "TAN_THU_MOI", false);   
                                    break;

                                case 1:
                                     switch (player.gender) {
                                    case 0:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 175, -1, 432);
                                        break;
                                    case 1:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 176, -1, 432);
                                        break;
                                    case 2:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 177, -1, 432);
                                        break;
                                    default:
                                        this.npcChat(player, "Can not do it");
                                        break;
                                }
                                    break;
                                case 2:
                                    break;
                            }
                        }
                    
                }
            };
        
    };}


    public static Npc teThienDaiThanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 174) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Ngươi có muốn gia nhập vào thế giới của ta để thử thách bản thân hả????",
                                "Đến Map\nChí Tôn", "Mở Map\nChí Tôn", "Hướng Dẫn", "Đóng");
                    }else{
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Xin Chào Tiên Nhân! Đã đến được đây thì ta biết ngươi không phải dạng vừa. Hãy Cùng Nhau Trải Nghiệm Nơi Đặc Biệt Nhất hiện tại của Máy Chủ HYPER Nhé.!",
                        "Đổi Trang Bị\nChí Tôn", "Nhận Ấn\nChí Tôn", "Hướng Dẫn", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 174) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 13, "|7|Muốn qua bên đó lắm rồi hả?\n"
                                            + "|5|Trước tiên ngươi phải mở giới hạn của bản thân\n"
                                            + "|5|Hãy nhấn mở map Chí Tôn trước nhé\n",
                                            "Đi Thôi", "Không đi");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 12, "|5|Để mở map Chí Tôn, Ngươi Cần Có 400K Điểm Đổi Trong tài khoản.\n\n"
                                            + "|5|Khi mở xong ngươi sẽ được tới hành tinh Của Đại Thánh.\n",
                                            "Mở Map", "Đóng");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 21, "|7|Thông tin về Hành Tinh Của Đại Thánh:\n\n"
                                            + "|3|Sau khi đã có đủ các trang bị thiên tử,\n"
                                            + "|3|Ngươi có thể đến Map Chí Tôn để up những vật phẩm giá trị.\n\n"
                                            + "|3|Ngươi cũng có thể săn boss Tề Thiên Đại Thánh để nhận cải trang và những vật phẩm giá trị.\n\n"
                                            + "|3|Hoặc có thể đánh quái để lấy vật phẩm đổi đồ Cực Xịn\n",
                                            "Đóng");
                                    break;
                            }
                        }else if (player.iDMark.getIndexMenu() == 13) {
                            switch (select) {
                                case 0:
                                    if (player.moChiTon == 1) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 179, -1, 432);
                                    }else{
                                        this.npcChat(player, "Bạn Chưa Mở Map Chí Tôn. Hãy Nhấn Mở Map Trước Nhé ");
                                    }
                                    break;
                                case 1:
                                    break;
                            }
                        }else if(player.iDMark.getIndexMenu() == 12){
                            switch (select) {
                                case 0:
                                    if (player.moChiTon == 1) {
                                        this.npcChat(player, "Bạn đã mở rồi, không cần tốn tiền như thế đâu! ");
                                    } else {
                                        if (!player.getSession().actived) {
                                            this.npcChat(player, "Bạn chưa MTV");
                                        } else {
                                            if (player.tongnap < 400000) {
                                                this.npcChat(player, "Bạn không đủ 400k");
                                            } else {
                                                PlayerDAO.subtn(player, 400000);
                                                player.moChiTon = 1;
                                                this.npcChat(player, "Mở Map Chí Tôn thành công ");
                                            }
                                        }
                                    }
                                    break;
                                case 1:
                                    break;
                            }
                        }
                    }else{
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                this.createOtherMenu(player, ConstNpc.MUA_CHI_TON, "|3|Cần x999 đá Chí Tôn để đổi áo , giày, quần, lắc Chí Tôn"
                                + "\n|3|Cần x9999 đá Chí Tôn để đổi găng\n"
                                + "\n|3|LƯU Ý: Khi đổi đồ Chí Tôn bắt buộc ngươi phải có trang bị SKH Thiên Tử để trao đổi.\n Ví dụ: Đổi áo Chí Tôn cần x999 đá Chí Tôn + 1 Áo SKH Thiên Tử chỉ số bất kì",
                                "Áo", "Quần", "Găng", "Giày", "Lắc", "Không thèm");
                                break;
                                case 1:
                                     if (player.moChiTon == 1) {
                                        Item aott = ItemService.gI().createNewItem((short) 2211);
                                        aott.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, aott);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                    } else {
                                        this.npcChat(player, "Bạn chưa mở map Chí Tôn");
                                    }
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 21, "|7|Map Chí Tôn là nơi những Tiên Nhân Hội Tụ:\n"
                                            + "|4|1. Ở đây, ngươi có thể đánh quái để tìm kiếm vật phẩm Đá Chí Tôn. Sau khi có đủ đá Chí Tôn. Ngươi Có Thể Đổi Được Trang Bị Chí Tôn Thần Thoại. Với điều kiện ngươi phải vượt qua Cửa Ải Thiên Tử trước.\n"
                                            + "|4|2. Khi Đổi Đồ chí tôn, ngươi cần có đủ đá Chí Tôn và những Món Đồ Thiên Tử SKH\n"
                                            + "|4|3. Đồ Chí Tôn cũng có thể nâng Được Set Kich Hoạt.\n"
                                            + "|4|4. Sau khi đủ 5 món Chí Tôn, ngươi có thể sử dụng đươc ấn chí tôn và được x4 Chỉ Số Vĩnh Viễn.\n"
                                            + "|4|5. Ở Hành Tinh của ta, ngoài việc Up vật phẩm Đá Chí Tôn, ngươi cũng có thể nhận được Bùa Hòa Quang Từ Quái. Ngoài ra, Boss Tề Thiên Đại Thánh cũng sẽ xuất hiện ở đây. Khi tiêu diệt được Ông ta, ngươi sẽ nhận được những phần quà giá trị. Cải Trang VIP. Và sẽ có tỉ lệ rơi ra đồ Chí Tôn thường.\n",
                                            "Đóng");
                                    break;
                            }
                        }else if (player.iDMark.getIndexMenu() == ConstNpc.MUA_CHI_TON) {
                            switch (select) {
                                case 0: {
                                    Item thientu = null;
                                    Item aoTSTD = null;
    
                                    try {
                                        thientu = InventoryServiceNew.gI().findItemBag(player, 2210);
                                        aoTSTD = InventoryServiceNew.gI().findItemBag(player, 2156);
                                    } catch (Exception e) {
    //                                        throw new RuntimeException(e);
                                    }
    
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                        return;
                                    }
    
                                    if (thientu == null || thientu.quantity < 999) {
                                        this.npcChat(player, "Bạn không đủ 999 Đá Chí Tôn");
                                        return;
                                    }
    
                                    if (aoTSTD == null || aoTSTD.quantity < 1 || !aoTSTD.isSKH()) {
                                            this.npcChat(player, "Bạn không Có áo SKH Thiên Tử");
                                            return;
                                    } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, aoTSTD, 2156);
                                    }
                                  
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                    Item aott = ItemService.gI().createNewItem((short) 2212);
                                    aott.itemOptions.add(new Item.ItemOption(47, 4000));
                                    aott.itemOptions.add(new Item.ItemOption(21, 400));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
    
                                    break;
                                }
                                case 1: {
                                    Item thientu = null;
                                    Item quanTSTD = null;
                                  
                                    try {
                                        thientu = InventoryServiceNew.gI().findItemBag(player, 2210);
                                        quanTSTD = InventoryServiceNew.gI().findItemBag(player, 2157);
                                       
                                    } catch (Exception e) {
    //                                        throw new RuntimeException(e);
                                    }
    
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                        return;
                                    }
                                    if (thientu == null || thientu.quantity < 999) {
                                        this.npcChat(player, "Bạn không đủ 999 Đá Chí Tôn");
                                        return;
                                    }
    
                                   
                                    if (quanTSTD == null || quanTSTD.quantity < 1 || !quanTSTD.isSKH()) {
                                            this.npcChat(player, "Bạn không Có quần SKH Thiên Tử");
                                            return;
                                    } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, quanTSTD, 2157);
                                    }
                                    
                                   
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                    Item aott = ItemService.gI().createNewItem((short) 2213);
                                    aott.itemOptions.add(new Item.ItemOption(22, 1000));
                                    aott.itemOptions.add(new Item.ItemOption(21, 400));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                    break;
                                }
    
                                case 2: {
                                   Item thientu = null;
                                    Item quanTSTD = null;
                                  
                                    try {
                                        thientu = InventoryServiceNew.gI().findItemBag(player, 2210);
                                        quanTSTD = InventoryServiceNew.gI().findItemBag(player, 2158);
                                       
                                    } catch (Exception e) {
    //                                        throw new RuntimeException(e);
                                    }
    
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                        return;
                                    }
                                    if (thientu == null || thientu.quantity < 999) {
                                        this.npcChat(player, "Bạn không đủ 999 Đá Chí Tôn");
                                        return;
                                    }
    
                                   
                                    if (quanTSTD == null || quanTSTD.quantity < 1 || !quanTSTD.isSKH()) {
                                            this.npcChat(player, "Bạn không Có găng SKH Thiên Tử");
                                            return;
                                    } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, quanTSTD, 2158);
                                    }
                                    
                                   
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                    Item aott = ItemService.gI().createNewItem((short) 2214);
                                    aott.itemOptions.add(new Item.ItemOption(22, 1000));
                                    aott.itemOptions.add(new Item.ItemOption(21, 400));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                    break;
                                }
                                case 3: {
                                    Item thientu = null;
                                    Item giayTSTD = null;
                                  
                                    try {
                                        thientu = InventoryServiceNew.gI().findItemBag(player, 2210);
                                        giayTSTD = InventoryServiceNew.gI().findItemBag(player, 2159);
                                      
                                    } catch (Exception e) {
    //                                        throw new RuntimeException(e);
                                    }
    
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                        return;
                                    }
    
                                    if (thientu == null || thientu.quantity < 999) {
                                        this.npcChat(player, "Bạn không đủ 999 Đá Chí Tôn");
                                        return;
                                    }
    
                                   
                                    if (giayTSTD == null || giayTSTD.quantity < 1 || !giayTSTD.isSKH()) {
                                            this.npcChat(player, "Bạn không Có giày SKH Thiên Tử");
                                            return;
                                    } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, giayTSTD, 2159);
                                    }
                                    
    
                                  
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                    Item aott = ItemService.gI().createNewItem((short) 2215);
                                    aott.itemOptions.add(new Item.ItemOption(23, 1000));
                                    aott.itemOptions.add(new Item.ItemOption(21, 400));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
    
                                    break;
                                }
                                case 4: {
                                    Item thientu = null;
                                    Item nhanTSTD = null;
                                   
                                    try {
                                        thientu = InventoryServiceNew.gI().findItemBag(player, 2210);
                                        nhanTSTD = InventoryServiceNew.gI().findItemBag(player, 2160);
                                       
                                    } catch (Exception e) {
    //                                        throw new RuntimeException(e);
                                    }
    
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                        return;
                                    }
    
                                    if (thientu == null || thientu.quantity < 999) {
                                        this.npcChat(player, "Bạn không đủ 999 Đá Chí Tôn");
                                        return;
                                    }
    
                                   
                                    if (nhanTSTD == null || nhanTSTD.quantity < 1 || !nhanTSTD.isSKH()) {
                                            this.npcChat(player, "Bạn không Có lắc SKH Thiên Tử");
                                            return;
                                    } else {
                                            InventoryServiceNew.gI().subQuantityItemsBag(player, nhanTSTD, 2160);
                                    }
                                    
                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                    Item aott = ItemService.gI().createNewItem((short) 2216);
                                    aott.itemOptions.add(new Item.ItemOption(14, 20));
                                    aott.itemOptions.add(new Item.ItemOption(21, 400));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
    
                                    break;
                                }
                                case 5 :
                                    break;
                               
    
                        }
    
                        } 
                        
                    }
                }
            };
        
    };}


    public static Npc nongdan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 178) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, tìm ta có việc gì",
                                "Trồng hoa", "Tặng quà", "Sự Kiện\n8/3", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 178) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1, "|7|Đã tới vụ mùa trồng Hoa hồng\n"
                                            + "|5|Nguyên liệu cần 01 Hạt giống, 10 Phân bón và 20 Nước\n"
                                            + "|5|tưới Thời gian mỗi lần trồng hoa là 60 giây\n"
                                            + "|1|Ngươi muốn trồng số lượng bao nhiêu?",
                                            "Trồng 1 lần", "trồng 100 lần", "Trồng 1000 lần");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 12, "|7|Cần 03 Bông hoa và 01 Nước hoa để đóng gói tặng Quà thường\n"
                                            + "|5|Cần 05 Bông hoa và 02 Nước hoa để đóng gói tặng Quà cao cấp\n"
                                            + "|5|Sẽ nhận được lại quà tri ân tương ứng từ ta\n"
                                            + "|3|Ngươi muốn tặng quà loại nào?",
                                            "Tặng thường", "Tặng cao\ncấp", "Đóng");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 21, "Thông tin sự kiện 8/3"
                                            + "", "Điểm danh", "Mua Hàng", "TOP\nsự kiện", "Hướng dẫn");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 12) {
                            switch (select) {
                                case 0:
                                    Item hoa = null;
                                    Item nuoc = null;
                                    try {
                                        hoa = InventoryServiceNew.gI().findItemBag(player, 1526);
                                        nuoc = InventoryServiceNew.gI().findItemBag(player, 1540);
                                    } catch (Exception e) {
                                    }
                                    if (hoa == null || hoa.quantity < 1) {
                                        this.npcChat(player, "Con còn thiếu x1 Nước hoa");
                                    } else if (nuoc == null || nuoc.quantity < 3) {
                                        this.npcChat(player, "Bạn còn thiếu x3 Bông hoa");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, hoa, 3);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nuoc, 1);
//                                        Item capsule = ItemService.gI().createNewItem((short) 1526, 1);
//                                        Item nro = ItemService.gI().createNewItem((short) Util.nextInt(15,20));
//                                        InventoryServiceNew.gI().addItemBag(player, nro);
//                                        Item ct = ItemService.gI().createNewItem((short) Util.nextInt(1546,1548));
//                                        InventoryServiceNew.gI().addItemBag(player, ct);
                                        player.diem8thang3 += 1;
                                        int randomItem = Util.nextInt(6); // Random giữa 0 và 1
                                        if (randomItem == 0 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(1546, 1548));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 5)));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 1 || Util.isTrue(30, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(1546, 1548));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, Util.nextInt(1, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, Util.nextInt(1, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, Util.nextInt(1, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 2 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(15, 20));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 3 || Util.isTrue(60, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 1541);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 4 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 517);
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 5 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 861);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 6 || Util.isTrue(50, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(2033, 2036));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 5 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 1191);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        }
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Xin chúc mừng");
                                    }
                                    break;
                                case 1:
                                    Item hoaa = null;
                                    Item nuocc = null;
                                    try {
                                        hoaa = InventoryServiceNew.gI().findItemBag(player, 1526);
                                        nuocc = InventoryServiceNew.gI().findItemBag(player, 1540);
                                    } catch (Exception e) {
                                    }
                                    if (hoaa == null || hoaa.quantity < 1) {
                                        this.npcChat(player, "Con còn thiếu x1 Nước hoa");
                                    } else if (nuocc == null || nuocc.quantity < 3) {
                                        this.npcChat(player, "Bạn còn thiếu x3 Bông hoa");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, hoaa, 3);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nuocc, 1);
//                                        Item capsule = ItemService.gI().createNewItem((short) 1526, 1);
//                                        Item nro = ItemService.gI().createNewItem((short) Util.nextInt(15,20));
//                                        InventoryServiceNew.gI().addItemBag(player, nro);
//                                        Item ct = ItemService.gI().createNewItem((short) Util.nextInt(1546,1548));
//                                        InventoryServiceNew.gI().addItemBag(player, ct);
                                        player.diem8thang3 += 1;
                                        int randomItem = Util.nextInt(6); // Random giữa 0 và 1
                                        if (randomItem == 0 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(1546, 1548));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(78, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(93, Util.nextInt(3, 5)));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 1 || Util.isTrue(30, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(1546, 1548));
                                            VatPham.itemOptions.add(new Item.ItemOption(50, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(77, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(103, Util.nextInt(20, 35)));
                                            VatPham.itemOptions.add(new Item.ItemOption(5, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(78, Util.nextInt(1, 10)));
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 2 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(15, 20));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 3 || Util.isTrue(60, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 1541);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 4 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 517);
                                            VatPham.itemOptions.add(new Item.ItemOption(30, 0));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 5 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 861);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 6 || Util.isTrue(50, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) Util.nextInt(2033, 2036));
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        } else if (randomItem == 5 || Util.isTrue(80, 100)) {
                                            Item VatPham = ItemService.gI().createNewItem((short) 1191);
                                            InventoryServiceNew.gI().addItemBag(player, VatPham);
                                            createOtherMenu(player, ConstNpc.IGNORE_MENU, "Chúc mừng " + player.name + " nhận được " + VatPham.template.name + "", "Ok");
                                        }
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Xin chúc mừng");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
                                    Item hat = null;
                                    Item phan = null;
                                    Item nuoc = null;
                                    try {
                                        hat = InventoryServiceNew.gI().findItemBag(player, 1542);
                                        phan = InventoryServiceNew.gI().findItemBag(player, 1543);
                                        nuoc = InventoryServiceNew.gI().findItemBag(player, 1544);
                                    } catch (Exception e) {
                                    }
                                    if (hat == null || hat.quantity < 1) {
                                        this.npcChat(player, "Con còn thiếu x1 hạt giống");
                                    } else if (phan == null || phan.quantity < 10) {
                                        this.npcChat(player, "Bạn còn thiếu x10 Phân bón");
                                    } else if (nuoc == null || nuoc.quantity < 20) {
                                        this.npcChat(player, "Bạn còn thiếu x20 Nước tưới");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, hat, 1);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, phan, 10);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nuoc, 20);
                                        Item capsule = ItemService.gI().createNewItem((short) 1526, 1);
                                        InventoryServiceNew.gI().addItemBag(player, capsule);
                                        player.diem8thang3 += 1;
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        int TimeSeconds = 10;
                                        Service.gI().sendThongBao(player, "Chờ 10 giây để cây phát triển!");
                                        while (TimeSeconds > 0) {
                                            TimeSeconds--;
                                            long sl = 1000;
                                            try {
                                                Thread.sleep(sl, mapId);
                                            } catch (InterruptedException ex) {
                                                java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        this.npcChat(player, "Con nhận được Nước hoa");
                                    }
                                    break;
                                case 1:
                                    Item hatt = null;
                                    Item phann = null;
                                    Item nuocc = null;
                                    try {
                                        hatt = InventoryServiceNew.gI().findItemBag(player, 1542);
                                        phann = InventoryServiceNew.gI().findItemBag(player, 1543);
                                        nuocc = InventoryServiceNew.gI().findItemBag(player, 1544);
                                    } catch (Exception e) {
                                    }
                                    if (hatt == null || hatt.quantity < 1) {
                                        this.npcChat(player, "Con còn thiếu x100 hạt giống");
                                    } else if (phann == null || phann.quantity < 1000) {
                                        this.npcChat(player, "Bạn còn thiếu x10 Phân bón");
                                    } else if (nuocc == null || nuocc.quantity < 2000) {
                                        this.npcChat(player, "Bạn còn thiếu x2000 Nước tưới");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, hatt, 100);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, phann, 1000);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nuocc, 2000);
                                        Item capsule = ItemService.gI().createNewItem((short) 1526, 100);
                                        InventoryServiceNew.gI().addItemBag(player, capsule);
                                        player.diem8thang3 += 100;
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        int TimeSeconds = 10;
                                        Service.gI().sendThongBao(player, "Chờ 10 giây để cây phát triển!");
                                        while (TimeSeconds > 0) {
                                            TimeSeconds--;
                                            long sl = 1000;
                                            try {
                                                Thread.sleep(sl, mapId);
                                            } catch (InterruptedException ex) {
                                                java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        this.npcChat(player, "Con nhận được Nước hoa");
                                    }
                                    break;
                                case 2:
                                    Item hattt = null;
                                    Item phannn = null;
                                    Item nuoccc = null;
                                    try {
                                        hattt = InventoryServiceNew.gI().findItemBag(player, 1542);
                                        phannn = InventoryServiceNew.gI().findItemBag(player, 1543);
                                        nuoccc = InventoryServiceNew.gI().findItemBag(player, 1544);
                                    } catch (Exception e) {
                                    }
                                    if (hattt == null || hattt.quantity < 1000) {
                                        this.npcChat(player, "Con còn thiếu x1000 hạt giống");
                                    } else if (phannn == null || phannn.quantity < 10000) {
                                        this.npcChat(player, "Bạn còn thiếu x10000 Phân bón");
                                    } else if (nuoccc == null || nuoccc.quantity < 20000) {
                                        this.npcChat(player, "Bạn còn thiếu x20000 Nước tưới");
                                    } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                        this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, hattt, 1000);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, phannn, 10000);
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nuoccc, 20000);
                                        Item capsule = ItemService.gI().createNewItem((short) 1526, 1000);
                                        InventoryServiceNew.gI().addItemBag(player, capsule);
                                        player.diem8thang3 += 1000;
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        int TimeSeconds = 10;
                                        Service.gI().sendThongBao(player, "Chờ 10 giây để cây phát triển!");
                                        while (TimeSeconds > 0) {
                                            TimeSeconds--;
                                            long sl = 1000;
                                            try {
                                                Thread.sleep(sl, mapId);
                                            } catch (InterruptedException ex) {
                                                java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                            }
                                        }
                                        this.npcChat(player, "Con nhận được Nước hoa");
                                    }
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc santa(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    DecimalFormat decimalFormat = new DecimalFormat("#,###");
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Xin chào, ta có một số vật phẩm đặt biệt cậu có muốn xem không?"
                            + "\nĐiểm đổi: " + decimalFormat.format(player.tongnap)
                            + "\nCoin ảo; " + decimalFormat.format(player.getSession().coin),
                            "Shop Ngọc Xanh", "Shop Hồng Ngọc", "Menu mua đệ\nsiêu vip", "Đổi Coin\nQua TV,HN", "Mua hộp\nquà VIP", "Nhận SKH\nThiên sứ", "Mua SKH\nThiên sứ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5 || this.mapId == 13 || this.mapId == 20) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0: //shop
                                    ShopServiceNew.gI().opendShop(player, "SANTA", false);
                                    break;
                                case 1: //shop
                                    ShopServiceNew.gI().opendShop(player, "SHOP_NGU_SAC", false);
                                    break;
                                case 2: //shop
                                    createOtherMenu(player, ConstNpc.BUY_DE_TU_VIP,
                                            "Xin chào, ta có một số bé pet vip nè!\n|1|Số tiền của bạn còn : " + player.getSession().coin + "COIN"
                                            + "\n|7|Đệ Goku Vô Cực: 100k COIN, Hợp thể tăng 50% chỉ số"
                                            + "\n|7|Đệ Abinus: 500k COIN, Hợp thể tăng 100% chỉ số"
                                            + "\n|7|Đệ Black Goku: 1.000k COIN, Hợp thể tăng 140% chỉ số"
                                            + "\n|7|Đệ Đầu Moi: 3.000k COIN, Hợp thể tăng 170% chỉ số",
                                            "Goku Vô Cực", "Abinus", "Black Goku", "Đệ Đầu Moi");
                                    break;
                                case 3:
                                    this.createOtherMenu(player, ConstNpc.QUY_DOI, "|7|Số COIN của bạn còn : " + player.getSession().coin + "\n"
                                            + "|7|Điểm đổi: " + player.tongnap + "\n"
                                            + "Ngươi muốn đổi Thỏi vàng hay Hồng ngọc?", "Quy đổi\n Hồng ngọc", "Quy Đổi Thỏi Vàng");
                                    break;

                                case 4:
                                    this.createOtherMenu(player, ConstNpc.HOP_QUA, "|7|Điểm đổi của bạn còn : " + player.tongnap + "\n"
                                            + "|4|50k Điểm Đổi: Nhận BT cấp 4 + 2tr TV + 20tr COIN + 20tr hồng ngọc + 10 hộp đồ thần linh\n"
                                            + "|4|100k Điểm Đổi: Nhận 50tr COIN + 50tr HN + hộp quà x50 đá vip và có tỉ lệ nhận bông tai c5 \n"
                                            + "|4|200k Điểm Đổi: Nhận 500tr COIN + 500tr HN + 10 hộp quà x50 đá vip và 20% phiếu SKH HD, 10% SKH SHD, 5% SKH TS\n"
                                            + "|4|Ngươi muốn đổi quà nào?", "Quà 50k", "Quà 100k", "Quà 200k", "Đóng");
                                    break;
                                case 5:
                                    if (player.getSession().actived) {
                                        if (player.qv == 1) {
                                            int gender = player.gender;
                                            if (gender == 0) {
                                                this.createOtherMenu(player, ConstNpc.TTTD,
                                                        "|7|Chọn SKH",
                                                        "Songoku", "Thiên xin hăng", "Kaioken", "Đóng");
                                                break;
                                            } else if (gender == 2) {
                                                this.createOtherMenu(player, ConstNpc.TTXD,
                                                        "|7|Chọn SKH",
                                                        "Nappa", "Kakarot", "Caidc", "Đóng");
                                                break;
                                            } else if (gender == 1) {
                                                this.createOtherMenu(player, ConstNpc.TTNM,
                                                        "|7|Chọn SKH",
                                                        "Ốc tiêu", "Picolo", "Pikkoro Đai mao", "Đóng");
                                                break;
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Nhấn vào ô mua SKH Thiên Sứ,\nmua rồi mới nhận được ở đây nha chú bé");
                                        }
                                        break;
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                        break;
                                    }
                                case 6:
                                    if (player.getSession().actived && player.qv != 1) {
                                        if (player.tongnap >= 450000) {
                                            player.qv = 1;
                                            Service.gI().sendThongBao(player, "Mua thành công. Đến santa nhận quà thôi !");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 450000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Không đủ 450k, cho ad sống tí đi");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên hoặc đã nhấn mua từ trước đó rồi");
                                        break;
                                    }
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTTD) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.TTTD1,
                                            "|7|Chọn món thiên sứ Trái Đất",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.TTTD2,
                                            "|7|Chọn món thiên sứ Trái Đất",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, ConstNpc.TTTD3,
                                            "|7|Chọn món thiên sứ Trái Đất",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTTD1) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1048);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(129, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(141, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Songoku");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1051);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(129, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(141, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ Songoku");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1054);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(129, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(141, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ Songoku");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1057);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(129, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(141, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ Songoku");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1060);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(129, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(141, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được nhẫn thiên sứ Songoku");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTTD2) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1048);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(127, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(139, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Thiên xin hăng");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1051);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(127, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(139, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ Thiên xin hăng");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1054);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(127, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(139, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ Thiên xin hăng");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1057);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(127, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(139, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ Thiên xin hăng");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1060);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(127, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(139, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ Thiên xin hăng");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTTD3) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1048);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(128, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(140, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ kaioken");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1051);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(128, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(140, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ kaioken");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1054);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(128, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(140, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ kaioken");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1057);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(128, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(140, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ kaioken");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1060);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(128, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(140, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ kaioken");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTXD) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.TTXD1,
                                            "|7|Chọn món thiên sứ Xayda",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.TTXD2,
                                            "|7|Chọn món thiên sứ Xayda",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, ConstNpc.TTXD3,
                                            "|7|Chọn món thiên sứ Xayda",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTXD3) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1050);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(134, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(137, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ cadic");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1053);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(134, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(137, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ cadic");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1056);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(134, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(137, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ cadic");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1059);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(134, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(137, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ cadic");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1062);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(134, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(137, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ cadic");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTXD2) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1050);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(133, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(136, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ kakarot");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1053);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(133, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(136, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ kakarot");
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1056);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(133, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(136, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ kakarot");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1059);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(133, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(136, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ kakarot");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1062);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(133, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(136, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ kakarot");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTXD1) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1050);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(135, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(138, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Nappa");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1053);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(135, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(138, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ nappa");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1056);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(135, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(138, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ nappa");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1059);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(135, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(138, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ nappa");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1062);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(135, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(138, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ nappa");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTNM) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.TTNM1,
                                            "|7|Chọn món thiên sứ Namec",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.TTNM2,
                                            "|7|Chọn món thiên sứ Namec",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, ConstNpc.TTNM3,
                                            "|7|Chọn món thiên sứ Namec",
                                            "Áo", "Quần", "Găng", "Giày", "Rada");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTNM1) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1049);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(131, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(143, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Ốc tiêu");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1052);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(131, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(143, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ Ốc tiêu");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1055);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(131, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(143, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ Ốc tiêu");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1058);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(131, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(143, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ Ốc tiêu");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1061);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(131, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(143, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ Ốc tiêu");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTNM2) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1049);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(142, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Picolo");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1052);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(142, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ Picolo");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1055);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(142, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ Picolo");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1058);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(142, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ Picolo");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1061);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(142, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ Picolo");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.TTNM3) {
                            switch (select) {
                                case 0: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1049);
                                    yardart.itemOptions.add(new Item.ItemOption(47, 4000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(132, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(144, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được áo thiên sứ Pikkoro Đai Mao");

                                    break;
                                }
                                case 1: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1052);
                                    yardart.itemOptions.add(new Item.ItemOption(22, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(132, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(144, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được quần thiên sứ Pikkoro Đai Mao");
                                    break;
                                }
                                case 2: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1055);
                                    yardart.itemOptions.add(new Item.ItemOption(0, 25000));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(132, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(144, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được găng thiên sứ Pikkoro Đai Mao");
                                    break;
                                }
                                case 3: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1058);
                                    yardart.itemOptions.add(new Item.ItemOption(23, 250));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(132, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(144, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được giày thiên sứ Pikkoro Đai Mao");
                                    break;
                                }
                                case 4: {
                                    player.qtt++;
                                    Item yardart = ItemService.gI().createNewItem((short) 1061);
                                    yardart.itemOptions.add(new Item.ItemOption(14, 25));
                                    yardart.itemOptions.add(new Item.ItemOption(21, 200));
                                    yardart.itemOptions.add(new Item.ItemOption(130, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(132, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(144, 1));
                                    yardart.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, yardart);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Bạn vừa nhận được rada thiên sứ Pikkoro Đai Mao");
                                    break;
                                }
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.HOP_QUA) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 50000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2097));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được hộp quà 50k !");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 50000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 50k hoặc hành trang không đủ");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 1:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 100000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2105));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được hộp quà 100k !");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 100000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 100k hoặc hành trang không đủ");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 200000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2110));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được hộp quà 200k !");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 200000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 200k hoặc hành trang không đủ");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.DSK) {

                            switch (select) {
                                case 0:
                                    Item tc = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2119);
                                    if (tc.quantity > 0) {
                                        player.dsk += tc.quantity;
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được " + tc.quantity + " điểm");
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, tc, tc.quantity);
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có phiếu nào !");
                                    }
                                    break;
                                case 1:
                                    Item cc = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2120);
                                    if (cc.quantity > 0) {
                                        player.dsk += cc.quantity * 3;
                                        Service.gI().sendThongBao(player, "Bạn vừa nhận được " + cc.quantity * 3 + " điểm");
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, cc, cc.quantity);
                                        InventoryServiceNew.gI().sendItemBags(player);

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn không có phiếu nào !");
                                    }
                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.QUY_DOI) {
                            switch (select) {
                                case 0:
                                    Input.gI().createFormQDTV(player);
                                    break;
                                case 1:
                                    Input.gI().createFormQDHN(player);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.BUY_DE_TU_VIP) {

                            switch (select) {
                                case 0:
                                    if (player.getSession().coin < 100000) {
                                        Service.gI().sendThongBao(player, "Bạn không có đủ 100k COIN");
                                        return;
                                    }
                                    //player.getSession().coin -= 100000;
                                    PlayerDAO.subvnd(player, 100000);
                                    PetService.gI().createGokuVoCucPetVip(player, player.pet != null, player.gender);
                                    break;
                                case 1:
                                    if (player.getSession().coin < 500000) {
                                        Service.gI().sendThongBao(player, "Bạn không có đủ 500k COIN");
                                        return;
                                    }
                                    //player.getSession().coin -= 500000;
                                    PlayerDAO.subvnd(player, 500000);
                                    PetService.gI().createAbinusPetVip(player, player.pet != null, player.gender);
                                    break;
                                case 2:
                                    if (player.getSession().coin < 1000000) {
                                        Service.gI().sendThongBao(player, "Bạn không có đủ 1.000k COIN");
                                        return;
                                    }
                                    //player.getSession().coin -= 1000000;
                                    PlayerDAO.subvnd(player, 1000000);
                                    PetService.gI().createBlackGokuPetVip(player, player.pet != null, player.gender);
                                    break;
                                case 3:
                                    if (player.getSession().coin < 3000000) {
                                        Service.gI().sendThongBao(player, "Bạn không có đủ 3.000k COIN");
                                        return;
                                    }
                                    // player.getSession().coin -= 3000000;
                                    PlayerDAO.subvnd(player, 3000000);
                                    PetService.gI().createDauMoiPetVip(player, player.pet != null, player.gender);
                                    break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc thodaika(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 5) {
                        createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Đưa cho ta Hồng Ngọc và ngươi sẽ mua đc oto\nĐây không phải chẵn lẻ tài xỉu đâu=)))",
                                "Tài", "Xỉu", " Bán nhanh\n 40 thỏi vàng");
                    } 
                    // else {
                    //     createOtherMenu(player, ConstNpc.BASE_MENU,
                    //             "Ngươi Muốn Đầu Tư sinh Lời Dịp Tết À? Hãy Đưa Ta giữ giùm, tới ngày nhận ngươi sẽ được lãi cao và những phần quà hấp dẫn\n Nhận lại tiền sau khi hết sự kiện tết nhé \n 1 Lần gửi được 200k Nhé\n Tổng Tiền Gửi Của Ngươi Là :" + player.tiengui + "\n\n\n Và Ở Chỗ Ta cò có thể nhận quà mốc nạp đơn. Hãy nạp rồi nhận nhé, Tổng nạp đơn tuần này của ngươi là: " + player.napdon + "",
                    //             "Gửi Tiền", "Nhận quà\nNạp Đơn", "Éo Gửi");
                    // }

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.getSession().actived) {
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        Input.gI().TAI(player);
                                        break;
                                    case 1:
                                        Input.gI().XIU(player);
                                        break;
                                    case 2:
                                        if (InventoryServiceNew.gI().findItemBag(player, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(player, 457).quantity >= 40) {
                                            if (player.inventory.gold < 1000000000l) {
                                                InventoryServiceNew.gI().subQuantityItemsBag(player, InventoryServiceNew.gI().findItemBag(player, 457), 40);
                                                player.inventory.gold = 20000000000l;
                                                InventoryServiceNew.gI().sendItemBags(player);
                                                Service.gI().sendThongBao(player, "Bán thành công. Bạn đang có 20 tỷ vàng");
                                            } else {
                                                Service.gI().sendThongBao(player, "Vàng dưới 1 tỷ mới bán được");
                                            }
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn không đủ 40 thỏi vàng");
                                        }
                                        break;

                                }
                            }
                        } else {
                            Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");

                        }
                    } 
                    // else {
                    //     switch (select) {
                    //         case 0:
                    //             if (player.iDMark.isBaseMenu()) {
                    //                 if (player.getSession().actived) {
                    //                     if (player.tongnap >= 200000) {
                    //                         player.tiengui += 200000;
                    //                         PlayerDAO.subtn(player, 200000);
                    //                         Service.gI().sendThongBao(player, "Gửi Thành Công 200k");
                    //                     } else {
                    //                         Service.gI().sendThongBao(player, "Ngươi Không có Đủ 200k Điểm Đổi");

                    //                     }
                    //                 } else {
                    //                     Service.gI().sendThongBao(player, "Hãy Ủng Hộ admin bằng cách mở thành viên trước nhé");

                    //                 }
                    //             }
                    //             break;
                    //         case 1:
                    //             break;
                    //     }
                    // }

                }
            }
        };
    }

    public static Npc uron(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    ShopServiceNew.gI().opendShop(pl, "URON", false);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

     public static Npc baHatMit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi tìm ta có việc gì?",
                                    "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị", "Pha lê hóa VIP\n12-15s", "Nâng cấp\nđồ\nThiên sứ", "Pháp sư\nhóa\nLinh thú", "Quy đổi\nSao pha lê\n");
                            break;
                         case 29:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi tìm ta có việc gì?",
                                    "Ép sao\ntrang bị", "Pha lê\nhóa\ntrang bị", "Pha lê hóa VIP\n12-15s", "Nâng cấp\nđồ\nThiên sứ", "Pháp sư\nhóa\nLinh thú", "Quy đổi\nSao pha lê\n");
                            break;
                        case 121:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi tìm ta có việc gì?",
                                    "Về đảo\nrùa");
                            break;
                        case 174:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ở chỗ ta có bán hào quang, chân mệnh. Ta có thể làm phép để nâng cấp chúng!",
                                    "Mua chân mệnh", "Nâng cấp\nchân mệnh","Nâng SKH\nThiên Tử", "Về đảo\nrùa");
                            break;
                        case 179:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi muốn làm gì hả?",
                                   "Mua Hào Quang","Nâng cấp\nhào quang", "Nâng SKH\nChí Tôn", "Về đảo\nrùa");
                            break;
                        default:
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Ngươi tìm ta có việc gì?",
                                    "Cửa hàng\nBùa", "Nâng cấp\nVật phẩm",
                                    "Nâng cấp\nBông tai\nPorata", "Mở chỉ số\nBông tai\nPorata",
                                    "Nhập\nNgọc Rồng", "Phân Rã\nĐồ Thần Linh", "Nâng Cấp \nĐồ Thiên Sứ");
                            break;
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:   
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                        break;
                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                        break;
                                    case 2:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP);
                                        break;
                                    case 3:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_HD);
                                        break;
                                    case 4:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_PHAP_SU);
                                        break;
                                    case 5:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.SU_KIEN);
                                        break;
                                        
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                switch (player.combineNew.typeCombine) {
                                    case CombineServiceNew.SU_KIEN:
                                        //case CombineServiceNew.SU_KIEN:
                                    case CombineServiceNew.EP_SAO_TRANG_BI:
                                    case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                    case CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP:
                                    case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                    case CombineServiceNew.NANG_CAP_DO_HD:
                                    case CombineServiceNew.MO_CHI_SO_PHAP_SU:
                                    case CombineServiceNew.NANG_CAP_CHAN_MENH:
                                        switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 1;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 1;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 144:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 2;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 2;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 3391:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 3;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 3;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 21:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 4;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 4;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 11:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 5;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 5;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 91:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 6;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 6;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 105:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 7;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 7;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 112:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 8;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 8;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 96:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 9;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 9;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 10;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 10;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 2:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 100;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 100;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;

                                    }
                                    CombineServiceNew.gI().startCombine(player);
                                        break;
                                        
                                }
                            }   break;

                         case 29:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:   
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.EP_SAO_TRANG_BI);
                                        break;
                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI);
                                        break;
                                    case 2:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP);
                                        break;
                                    case 3:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_HD);
                                        break;
                                    case 4:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_PHAP_SU);
                                        break;
                                    case 5:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.SU_KIEN);
                                        break;
                                        
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                switch (player.combineNew.typeCombine) {
                                    case CombineServiceNew.SU_KIEN:
                                        //case CombineServiceNew.SU_KIEN:
                                    case CombineServiceNew.EP_SAO_TRANG_BI:
                                    case CombineServiceNew.PHA_LE_HOA_TRANG_BI:
                                    case CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP:
                                    case CombineServiceNew.CHUYEN_HOA_TRANG_BI:
                                    case CombineServiceNew.NANG_CAP_DO_HD:
                                    case CombineServiceNew.MO_CHI_SO_PHAP_SU:
                                    case CombineServiceNew.NANG_CAP_CHAN_MENH:
                                        switch (select) {
                                        case 0:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 1;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 1;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 144:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 2;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 2;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 3391:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 3;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 3;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 21:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 4;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 4;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 11:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 5;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 5;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 91:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 6;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 6;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 105:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 7;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 7;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 112:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 8;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 8;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 96:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 9;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 9;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 1:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 10;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 10;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;
                                        case 2:
                                            if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI) {
                                                player.combineNew.quantities = 100;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.PHA_LE_HOA_TRANG_BI_VIP) {
                                                player.combineNew.quantities = 100;
                                            } else if (player.combineNew.typeCombine == CombineServiceNew.NANG_CAP_CHAN_MENH) {
                                                player.combineNew.quantities = 1;
                                            }
                                            break;

                                    }
                                    CombineServiceNew.gI().startCombine(player);
                                        break;
                                        
                                }
                            }   break;
                      
                        case 112:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                        break;
                                }
                            }   break;
                        case 179:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        createOtherMenu(player, ConstNpc.MUA_HAO_QUANG,
                                                "Hào quang cấp 1 giá 200k Điểm đổi, ngươi có chắc chắn muốn mua không?"
                                                        + "\n Chỉ số hào quang sẽ được random từ 10-50% sức đánh"
                                                        + "\n Hào quang max là cấp 15, nâng cấp free không tốn phí",
                                                "Mua", "Không");
                                        break;

                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_CHAN_MENH);
                                        break;

                                     case 2:
                                          createOtherMenu(player, 1323,"|5|Món đồ này hiện tại đã là VIP nhất server.\n"
                                                + "Sau khi đủ 5 món chí tôn sẽ nhận được phần thưởng siêu đặc biệt từ phía ADMIN\n"
                                                + "Để nâng cấp SKH Chí Tôn, ngươi cần có 2 món chí tôn giống nhau và 200k Điểm Đổi"
                                                + "\nVí dụ: Để nâng cấp áo SKH Thiên Tử, ngươi cần có 2 áo Thiên Tử thường chỉ số bất kì và 200K Điểm Đổi"
                                                + "\nNgươi có muốn nâng cấp không?"
                                                +"\n",
                                                "Nâng thôi", "Không");
                                        break;

                                    case 3:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MUA_HAO_QUANG) {
                                switch (select) {
                                    case 0:
                                        if (player.tongnap < 200000) {
                                            Service.getInstance().sendThongBao(player, "Bạn không đủ 200k điểm đổi ");
                                            
                                            return;
                                        }
                                        if (player.inventory.itemsBag.isEmpty()) {
                                            Service.getInstance().sendThongBao(player, "Hành trang của bạn không đủ");
                                            
                                            return;
                                            
                                        }
                                        if (player.inventory.itemsBody.size() < 13) {
                                            Service.getInstance().sendThongBao(player, "Tài khoản của bạn đang bị lỗi, vui lòng liên hệ admin");
                                            
                                            return;
                                        }
                                        PlayerDAO.subtn(player, 200000);
                                        Item item = ItemService.gI().createNewItem((short) 1294, 1);
                                        item.itemOptions.add(new Item.ItemOption(72, 1));
                                        int rd = Util.nextInt(1, 10);
                                        if (rd == 1) {
                                            item.itemOptions.add(new Item.ItemOption(50, 50));
                                            item.itemOptions.add(new Item.ItemOption(77, 50));
                                            item.itemOptions.add(new Item.ItemOption(103, 50));
                                        } else if (rd > 1 && rd < 5) {
                                            item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(30, 49)));
                                            item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(30, 49)));
                                            item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 49)));
                                        } else {
                                            item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 29)));
                                            item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 29)));
                                            item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 29)));
                                        }
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + item.template.name);
                                        break;
                                        
                                }
                            }  
                            break;
                        case 174:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        createOtherMenu(player, ConstNpc.MUA_CHAN_MENH,
                                                "Chân mệnh cấp 1 giá 200k Điểm đổi, ngươi có chắc chắn muốn mua không?"
                                                        + "\n Chỉ số hào quang sẽ được random từ 10-50% sức đánh"
                                                        + "\n Chân mệnh max là cấp 9,nâng cấp free không tốn phí",
                                                "Mua", "Không");
                                        break;
                                  
                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_CHAN_MENH);
                                        break;
                                    case 2:
                                           createOtherMenu(player, 1323,
                                                "Để nâng cấp SKH Thiên Tử, ngươi cần có 2 món thiên tử giống nhau  và 100k Điểm Đổi"
                                                        + "\n Ví dụ: Để nâng cấp áo SKH Thiên Tử, ngươi cần có 2 áo Thiên Tử thường chỉ số bất kì và 100K Điểm Đổi"
                                                        + "\nNgươi có muốn nâng cấp không?",
                                                "Nâng thôi", "Không");
                                       
                                        break;
                                    case 3:
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 1156);
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MUA_CHAN_MENH) {
                                switch (select) {
                                    case 0:
                                        if (player.tongnap < 200000) {
                                            Service.getInstance().sendThongBao(player, "Bạn không đủ 200k điểm đổi ");
                                            return;
                                        }
                                        if (player.inventory.itemsBag.isEmpty()) {
                                            Service.getInstance().sendThongBao(player, "Hành trang của bạn không đủ");
                                            return;
                                        }
                                        if (player.inventory.itemsBody.size() < 13) {
                                            Service.getInstance().sendThongBao(player, "Tài khoản của bạn đang bị lỗi, vui lòng liên hệ admin");
                                            
                                            return;
                                        }
                                        PlayerDAO.subtn(player, 200000);
                                        Item item = ItemService.gI().createNewItem((short) 1285, 1);
                                        item.itemOptions.add(new Item.ItemOption(72, 1));
                                        int rd = Util.nextInt(1, 10);
                                        if (rd == 1) {
                                            item.itemOptions.add(new Item.ItemOption(50, 50));
                                            item.itemOptions.add(new Item.ItemOption(77, 50));
                                            item.itemOptions.add(new Item.ItemOption(103, 50));
                                        } else if (rd > 1 && rd < 5) {
                                            item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(30, 49)));
                                            item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(30, 49)));
                                            item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(30, 49)));
                                        } else {
                                            item.itemOptions.add(new Item.ItemOption(50, Util.nextInt(10, 29)));
                                            item.itemOptions.add(new Item.ItemOption(77, Util.nextInt(10, 29)));
                                            item.itemOptions.add(new Item.ItemOption(103, Util.nextInt(10, 29)));
                                        }
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        Service.getInstance().sendThongBao(player, "Bạn vừa nhận được " + item.template.name);
                                        break;
                                        
                                }
                            }  else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                    switch (player.combineNew.typeCombine) {
                                        case CombineServiceNew.NANG_CAP_CHAN_MENH:
                                            if (select == 0) {
                                                CombineServiceNew.gI().startCombine(player);
                                            }
                                            break;

                        }
                    }
                             break;
                        case 42:
                        case 43:
                        case 44:
                        case 84:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0: //shop bùa
                                        createOtherMenu(player, ConstNpc.MENU_OPTION_SHOP_BUA,
                                                "Bùa của ta rất lợi hại, nhìn ngươi yếu đuối thế này, chắc muốn mua bùa để "
                                                        + "mạnh mẽ à, mua không ta bán cho, xài rồi lại thích cho mà xem.",
                                                "Bùa\n1 giờ", "Bùa\n8 giờ", "Bùa\n1 tháng", "Đóng");
                                        break;
                                    case 1:
                                        
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_VAT_PHAM);
                                        break;
                                    case 2: //nâng cấp bông tai
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_BONG_TAI);
                                        break;
                                    case 3:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.MO_CHI_SO_BONG_TAI);
                                        break;
                                    case 4:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NHAP_NGOC_RONG);
                                        break;
                                    case 5: //phân rã đồ thần linh
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.PHAN_RA_DO_THAN_LINH);
                                        break;
                                    case 6:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_DO_TS);
                                        break;
                                        
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_SHOP_BUA) {
                                switch (select) {
                                    case 0:
                                        ShopServiceNew.gI().opendShop(player, "BUA_1H", true);
                                        break;
                                    case 1:
                                        ShopServiceNew.gI().opendShop(player, "BUA_8H", true);
                                        break;
                                    case 2:
                                        ShopServiceNew.gI().opendShop(player, "BUA_1M", true);
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                                switch (player.combineNew.typeCombine) {
                                    case CombineServiceNew.NANG_CAP_VAT_PHAM:
                                    case CombineServiceNew.NANG_CAP_BONG_TAI:
                                    case CombineServiceNew.MO_CHI_SO_BONG_TAI:
                                    case CombineServiceNew.NHAP_NGOC_RONG:
                                    case CombineServiceNew.PHAN_RA_DO_THAN_LINH:
                                    case CombineServiceNew.NANG_CAP_DO_TS:
                                    case CombineServiceNew.NANG_CAP_SKH_VIP:
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHAN_RA_DO_THAN_LINH) {
                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player);
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_DO_TS) {
                                if (select == 0) {
                                    CombineServiceNew.gI().startCombine(player);
                                }
                            }   break;
                        default:
                            break;
                    }
                }
            }
        };
    }

    public static Npc ruongDo(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    InventoryServiceNew.gI().sendItemBox(player);
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                }
            }
        };
    }

    public static Npc duongtank(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (mapId == 0) {
                        nguhs.gI().setTimeJoinnguhs();
                        long now = System.currentTimeMillis();
                        if (now > nguhs.TIME_OPEN_NHS || now < nguhs.TIME_CLOSE_NHS) {
                            this.createOtherMenu(player, 0, "Ngũ Hàng Sơn x10 Tnsm\nHỗ trợ cho Ae trên 80 Tỷ SM?\nThời gian từ 13h - 18h, 250 hồng ngọc 1 lần vào", "Éo", "OK");
                        } else {
                            this.createOtherMenu(player, 0, "Ngũ Hàng Sơn x10 Tnsm\nHỗ trợ cho Ae trên 80 Tỷ SM?\nThời gian từ 13h - 18h, 250 hồng ngọc 1 lần vào", "Éo");
                        }
                    }
                    if (mapId == 123) {
                        this.createOtherMenu(player, 0, "Bạn Muốn Quay Trở Lại Làng Ảru?", "OK", "Từ chối");

                    }
                    if (mapId == 122) {
                        this.createOtherMenu(player, 0, "Xia xia thua phùa\b|7|Thí chủ đang có: " + player.NguHanhSonPoint + " điểm ngũ hành sơn\b|1|Thí chủ muốn đổi cải trang x4 chưởng ko?", "Âu kê", "Top Ngu Hanh Son", "No");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                    if (mapId == 0) {
                        switch (select) {
                            case 0:
                                break;
                            case 1:
                                if (player.nPoint.power < 80000000000L) {
                                    Service.getInstance().sendThongBao(player, "Sức mạnh bạn Đéo đủ để qua map!");
                                    return;
                                } else if (player.inventory.ruby < 100000) {
                                    Service.getInstance().sendThongBao(player, "Phí vào là 100000 hồng ngọc một lần bạn ey!\nBạn đéo đủ!");
                                    return;
                                } else if (!player.getSession().actived) {
                                    Service.getInstance().sendThongBao(player, "Mở thành viên đi chú bé đần ơi! 20k thôi");
                                    return;
                                } else {
                                    player.inventory.ruby -= 100000;
                                    PlayerService.gI().sendInfoHpMpMoney(player);
                                    ChangeMapService.gI().changeMapInYard(player, 123, -1, -1);
                                }
                                break;
                        }
                    }
                    if (mapId == 123) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapInYard(player, 0, -1, 469);
                        }
                    }
                    if (mapId == 122) {
                        if (select == 0) {
                            if (player.NguHanhSonPoint >= 500) {
                                player.NguHanhSonPoint -= 500;
                                Item item = ItemService.gI().createNewItem((short) (711));
                                item.itemOptions.add(new Item.ItemOption(49, 80));
                                item.itemOptions.add(new Item.ItemOption(77, 80));
                                item.itemOptions.add(new Item.ItemOption(103, 50));
                                item.itemOptions.add(new Item.ItemOption(207, 0));
                                item.itemOptions.add(new Item.ItemOption(33, 0));
//                                      
                                InventoryServiceNew.gI().addItemBag(player, item);
                                Service.gI().sendThongBao(player, "Chúc Mừng Bạn Đổi Vật Phẩm Thành Công !");
                            } else {
                                Service.gI().sendThongBao(player, "Không đủ điểm, bạn còn " + (500 - player.pointPvp) + " điểm nữa");
                            }

                        }
                        /* if (select == 1) {
                            Service.gI().showListTop(player, Manager.topNHS);*/

                    }

                }
            }
        };
    }

    public static Npc dauThan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    player.magicTree.openMenuTree();
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_LEFT_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                if (player.magicTree.level == 10) {
                                    player.magicTree.fastRespawnPea();
                                } else {
                                    player.magicTree.showConfirmUpgradeMagicTree();
                                }
                            } else if (select == 2) {
                                player.magicTree.fastRespawnPea();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_NON_UPGRADE_FULL_PEA:
                            if (select == 0) {
                                player.magicTree.harvestPea();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUpgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UPGRADE:
                            if (select == 0) {
                                player.magicTree.upgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_UPGRADE:
                            if (select == 0) {
                                player.magicTree.fastUpgradeMagicTree();
                            } else if (select == 1) {
                                player.magicTree.showConfirmUnuppgradeMagicTree();
                            }
                            break;
                        case ConstNpc.MAGIC_TREE_CONFIRM_UNUPGRADE:
                            if (select == 0) {
                                player.magicTree.unupgradeMagicTree();
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc calick(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            private final byte COUNT_CHANGE = 50;
            private int count;

            private void changeMap() {
                if (this.mapId != 102) {
                    count++;
                    if (this.count >= COUNT_CHANGE) {
                        count = 0;
                        this.map.npcs.remove(this);
                        Map map = MapService.gI().getMapForCalich();
                        this.mapId = map.mapId;
                        this.cx = Util.nextInt(100, map.mapWidth - 100);
                        this.cy = map.yPhysicInTop(this.cx, 0);
                        this.map = map;
                        this.map.npcs.add(this);
                    }
                }
            }

            @Override
            public void openBaseMenu(Player player) {
                if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                    player.iDMark.setIndexMenu(ConstNpc.BASE_MENU);
                    if (TaskService.gI().getIdTask(player) < ConstTask.TASK_20_0) {
                        Service.gI().hideWaitDialog(player);
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                        return;
                    }
                    if (this.mapId != player.zone.map.mapId) {
                        Service.gI().sendThongBao(player, "Calích đã rời khỏi map!");
                        Service.gI().hideWaitDialog(player);
                        return;
                    }

                    if (this.mapId == 102) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Chào chú, cháu có thể giúp gì?",
                                "Kể\nChuyện", "Quay về\nQuá khứ");
                    } else {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Chào chú, cháu có thể giúp gì?", "Kể\nChuyện", "Đi đến\nTương lai", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (this.mapId == 102) {
                    if (player.iDMark.isBaseMenu()) {
                        if (select == 0) {
                            //kể chuyện
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                        } else if (select == 1) {
                            //về quá khứ
                            ChangeMapService.gI().goToQuaKhu(player);
                        }
                    }
                } else if (player.iDMark.isBaseMenu()) {
                    if (select == 0) {
                        //kể chuyện
                        NpcService.gI().createTutorial(player, this.avartar, ConstNpc.CALICK_KE_CHUYEN);
                    } else if (select == 1) {
                        //đến tương lai
//                                    changeMap();
                        if (TaskService.gI().getIdTask(player) >= ConstTask.TASK_20_0) {
                            ChangeMapService.gI().goToTuongLai(player);
                        }
                    } else {
                        Service.gI().sendThongBao(player, "Không thể thực hiện");
                    }
                }
            }
        };
    }

    public static Npc jaco(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Gô Tên, Calich và Monaka đang gặp chuyện ở hành tinh Potaufeu \n Hãy đến đó ngay", "Đến \nPotaufeu");
                    } else if (this.mapId == 139) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        if (player.getSession().player.nPoint.power >= 800000000L) {

                            ChangeMapService.gI().goToPotaufeu(player);
                        } else {
                            this.npcChat(player, "Bạn chưa đủ 800tr sức mạnh để vào!");
                        }
                    } else if (this.mapId == 139) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về trạm vũ trụ
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 24 + player.gender, -1, -1);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

//public static Npc Potage(int mapId, int status, int cx, int cy, int tempId, int avartar) {
//        return new Npc(mapId, status, cx, cy, tempId, avartar) {
//            @Override
//            public void openBaseMenu(Player player) {
//                if (canOpenNpc(player)) {
//                    if (this.mapId == 149) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "tét", "Gọi nhân bản");
//                    }
//                }
//            }
//            @Override
//            public void confirmMenu(Player player, int select) {
//                if (canOpenNpc(player)) {
//                   if (select == 0){
//                        BossManager.gI().createBoss(-214);
//                   }
//                }
//            }
//        };
//    }
    public static Npc npclytieunuong54(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                createOtherMenu(player, 0, "Trò chơi Chọn ai đây đang được diễn ra, nếu bạn tin tưởng mình đang tràn đầy may mắn thì có thể tham gia thử", "Thể lệ", "Chọn\nThỏi vàng");
            }

            @Override
            public void confirmMenu(Player pl, int select) {
                if (canOpenNpc(pl)) {
                    String time = ((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) + " giây";
                    if (((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) < 0) {
                        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
                    }
                    if (pl.iDMark.getIndexMenu() == 0) {
                        if (select == 0) {
                            createOtherMenu(pl, ConstNpc.IGNORE_MENU, "Thời gian giữa các giải là 5 phút\nKhi hết giờ, hệ thống sẽ ngẫu nhiên chọn ra 1 người may mắn.\nLưu ý: Số thỏi vàng nhận được sẽ bị nhà cái lụm đi 5%!Trong quá trình diễn ra khi đặt cược nếu thoát game mọi phần đặt đều sẽ bị hủy", "Ok");
                        } else if (select == 1) {
                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                        }
                    } else if (pl.iDMark.getIndexMenu() == 1) {
                        if (((ChonAiDay.gI().lastTimeEnd - System.currentTimeMillis()) / 1000) > 0) {
                            switch (select) {
                                case 0:
                                    createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                    break;
                                case 1: {
                                    try {
                                        if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 20) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 20);
                                            InventoryServiceNew.gI().sendItemBags(pl);
                                            pl.goldNormar += 20;
                                            ChonAiDay.gI().goldNormar += 20;
                                            ChonAiDay.gI().addPlayerNormar(pl);
                                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                        }
                                    } catch (Exception ex) {
                                        java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;

                                case 2: {
                                    try {
                                        if (InventoryServiceNew.gI().findItemBag(pl, 457).isNotNullItem() && InventoryServiceNew.gI().findItemBag(pl, 457).quantity >= 200) {
                                            InventoryServiceNew.gI().subQuantityItemsBag(pl, InventoryServiceNew.gI().findItemBag(pl, 457), 200);
                                            InventoryServiceNew.gI().sendItemBags(pl);
                                            pl.goldVIP += 200;
                                            ChonAiDay.gI().goldVip += 200;
                                            ChonAiDay.gI().addPlayerVIP(pl);
                                            createOtherMenu(pl, 1, "Tổng giải thường: " + ChonAiDay.gI().goldNormar + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(0) + "%\nTổng giải VIP: " + ChonAiDay.gI().goldVip + " thỏi vàng, cơ hội trúng của bạn là: " + pl.percentGold(1) + "%\nSố thỏi vàng đặt thường: " + pl.goldNormar + "\nSố thỏi vàng đặt VIP: " + pl.goldVIP + "\n Thời gian còn lại: " + time, "Cập nhập", "Thường\n20 thỏi\nvàng", "VIP\n200 thỏi\nvàng", "Đóng");
                                        } else {
                                            Service.gI().sendThongBao(pl, "Bạn không đủ thỏi vàng");
                                        }
                                    } catch (Exception ex) {
//                                            java.util.logging.Logger.getLogger(NpcFactory.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                                break;

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc thuongDe(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Đến Kaio", "Quay số\nmay mắn");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 45) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 48, -1, 354);
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MENU_CHOOSE_LUCKY_ROUND,
                                            "Con muốn làm gì nào?", "Quay bằng\nvàng",
                                            "Rương phụ\n("
                                            + (player.inventory.itemsBoxCrackBall.size()
                                            - InventoryServiceNew.gI().getCountEmptyListItem(player.inventory.itemsBoxCrackBall))
                                            + " món)",
                                            "Xóa hết\ntrong rương", "Đóng");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_CHOOSE_LUCKY_ROUND) {
                            switch (select) {
                                case 0:
                                    LuckyRound.gI().openCrackBallUI(player, LuckyRound.USING_GOLD);
                                    break;
                                case 1:
                                    ShopServiceNew.gI().opendShop(player, "ITEMS_LUCKY_ROUND", true);
                                    break;
                                case 2:
                                    NpcService.gI().createMenuConMeo(player,
                                            ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND, this.avartar,
                                            "Con có chắc muốn xóa hết vật phẩm trong rương phụ? Sau khi xóa "
                                            + "sẽ không thể khôi phục!",
                                            "Đồng ý", "Hủy bỏ");
                                    break;
                            }
                        }
                    }

                }
            }
        };
    }

    public static Npc thanVuTru(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Con muốn làm gì nào", "Di chuyển");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 48) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.MENU_DI_CHUYEN,
                                            "Con muốn đi đâu?", "Về\nthần điện", "Thánh địa\nKaio", "Con\nđường\nrắn độc", "Từ chối");
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_DI_CHUYEN) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 45, -1, 354);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 2:
                                    //con đường rắn độc
                                    break;
                            }
                        }
                    }
                }
            }

        };
    }

    public static Npc kibit(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Từ chối");
                    }
                    if (this.mapId == 114) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc osin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Đến\nKaio", "Đến\nhành tinh\nBill", "Từ chối");
                    } else if (this.mapId == 154) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Về thánh địa", "Đến\nhành tinh\nngục tù", "Từ chối");
                    } else if (this.mapId == 155) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else if (this.mapId == 52) {
                        try {
                            MapMaBu.gI().setTimeJoinMapMaBu();
                            if (this.mapId == 52) {
                                long now = System.currentTimeMillis();
                                if (now > MapMaBu.TIME_OPEN_MABU && now < MapMaBu.TIME_CLOSE_MABU) {
                                    this.createOtherMenu(player, ConstNpc.MENU_OPEN_MMB, "Đại chiến Ma Bư đã mở, "
                                            + "ngươi có muốn tham gia không?",
                                            "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_MMB,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }

                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu osin");
                        }

                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.fightMabu.pointMabu >= player.fightMabu.POINT_MAX) {
                            this.createOtherMenu(player, ConstNpc.GO_UPSTAIRS_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Lên Tầng!", "Quay về", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                    "Quay về", "Từ chối");
                        }
                    } else if (this.mapId == 120) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta có thể giúp gì cho ngươi ?",
                                "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 50) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 48, -1, 354, 240);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                                    break;
                            }
                        }
                    } else if (this.mapId == 154) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMap(player, 50, -1, 318, 336);
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMap(player, 155, -1, 111, 792);
                                    break;
                            }
                        }
                    } else if (this.mapId == 155) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMap(player, 154, -1, 200, 312);
                            }
                        }
                    } else if (this.mapId == 52) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.MENU_REWARD_MMB:
                                break;
                            case ConstNpc.MENU_OPEN_MMB:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                } else if (select == 1) {
//                                    if (!player.getSession().actived) {
//                                        Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//                                    } else
                                    ChangeMapService.gI().changeMap(player, 114, -1, 318, 336);
                                }
                                break;
                            case ConstNpc.MENU_NOT_OPEN_BDW:
                                if (select == 0) {
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_MAP_MA_BU);
                                }
                                break;
                        }
                    } else if (this.mapId >= 114 && this.mapId < 120 && this.mapId != 116) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.GO_UPSTAIRS_MENU) {
                            if (select == 0) {
                                player.fightMabu.clear();
                                ChangeMapService.gI().changeMap(player, this.map.mapIdNextMabu((short) this.mapId), -1, this.cx, this.cy);
                            } else if (select == 1) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        } else {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    } else if (this.mapId == 120) {
                        if (player.iDMark.getIndexMenu() == ConstNpc.BASE_MENU) {
                            if (select == 0) {
                                ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, 0, -1);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc linhCanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.clan == null) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Chỉ tiếp các bang hội, miễn tiếp khách vãng lai", "Đóng");
                        return;
                    }
                    if (player.clan.getMembers().size() < DoanhTrai.N_PLAYER_CLAN) {
                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội phải có ít nhất 5 thành viên mới có thể mở", "Đóng");
                        return;
                    }
                    if (player.clan.doanhTrai != null) {
                        createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                                "Bang hội của ngươi đang đánh trại độc nhãn\n"
                                + "Thời gian còn lại là "
                                + TimeUtil.getSecondLeft(player.clan.doanhTrai.getLastTimeOpen(), DoanhTrai.TIME_DOANH_TRAI / 1000)
                                + ". Ngươi có muốn tham gia không?",
                                "Tham gia", "Không", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    int nPlSameClan = 0;
                    for (Player pl : player.zone.getPlayers()) {
                        if (!pl.equals(player) && pl.clan != null
                                && pl.clan.equals(player.clan) && pl.location.x >= 1285
                                && pl.location.x <= 1645) {
                            nPlSameClan++;
                        }
                    }
                    if (nPlSameClan < DoanhTrai.N_PLAYER_MAP) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Ngươi phải có ít nhất " + DoanhTrai.N_PLAYER_MAP + " đồng đội cùng bang đứng gần mới có thể\nvào\n"
                                + "tuy nhiên ta khuyên ngươi nên đi cùng với 3-4 người để khỏi chết.\n"
                                + "Hahaha.", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clanMember.getNumDateFromJoinTimeToToday() < 1) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Doanh trại chỉ cho phép những người ở trong bang trên 1 ngày. Hẹn ngươi quay lại vào lúc khác",
                                "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    if (player.clan.haveGoneDoanhTrai) {
                        createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                "Bang hội của ngươi đã đi trại lúc " + TimeUtil.formatTime(player.clan.lastTimeOpenDoanhTrai, "HH:mm:ss") + " hôm nay. Người mở\n"
                                + "(" + player.clan.playerOpenDoanhTrai + "). Hẹn ngươi quay lại vào ngày mai", "OK", "Hướng\ndẫn\nthêm");
                        return;
                    }
                    createOtherMenu(player, ConstNpc.MENU_JOIN_DOANH_TRAI,
                            "Hôm nay bang hội của ngươi chưa vào trại lần nào. Ngươi có muốn vào\n"
                            + "không?\nĐể vào, ta khuyên ngươi nên có 3-4 người cùng bang đi cùng",
                            "Vào\n(miễn phí)", "Không", "Hướng\ndẫn\nthêm");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_JOIN_DOANH_TRAI:
                            if (select == 0) {
                                DoanhTraiService.gI().joinDoanhTrai(player);
                            } else if (select == 2) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                        case ConstNpc.IGNORE_MENU:
                            if (select == 1) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOANH_TRAI);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc quaTrung(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            private final int COST_AP_TRUNG_NHANH = 1000000000;

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == (21 + player.gender)) {
                        player.mabuEgg.sendMabuEgg();
                        if (player.mabuEgg.getSecondDone() != 0) {
                            this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                    "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                        }
                    }
                    if (this.mapId == 154) {
                        player.billEgg.sendBillEgg();
                        if (player.billEgg.getSecondDone() != 0) {
                            this.createOtherMenu(player, ConstNpc.CAN_NOT_OPEN_EGG, "Bư bư bư...",
                                    "Hủy bỏ\ntrứng", "Ấp nhanh\n" + Util.numberToMoney(COST_AP_TRUNG_NHANH) + " vàng", "Đóng");
                        } else {
                            this.createOtherMenu(player, ConstNpc.CAN_OPEN_EGG, "Bư bư bư...", "Nở", "Hủy bỏ\ntrứng", "Đóng");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == (21 + player.gender)) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.CAN_NOT_OPEN_EGG:
                                if (select == 0) {
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                } else if (select == 1) {
                                    if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                        player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                        player.mabuEgg.timeDone = 0;
                                        Service.gI().sendMoney(player);
                                        player.mabuEgg.sendMabuEgg();
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                    }
                                }
                                break;
                            case ConstNpc.CAN_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_EGG,
                                                "Bạn có chắc chắn cho trứng nở?\n"
                                                + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_EGG,
                                                "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        player.mabuEgg.openEgg(ConstPlayer.TRAI_DAT);
                                        break;
                                    case 1:
                                        player.mabuEgg.openEgg(ConstPlayer.NAMEC);
                                        break;
                                    case 2:
                                        player.mabuEgg.openEgg(ConstPlayer.XAYDA);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_DESTROY_EGG:
                                if (select == 0) {
                                    player.mabuEgg.destroyEgg();
                                }
                                break;
                        }
                    }
                    if (this.mapId == 154) {
                        switch (player.iDMark.getIndexMenu()) {
                            case ConstNpc.CAN_NOT_OPEN_BILL:
                                if (select == 0) {
                                    this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_BILL,
                                            "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                } else if (select == 1) {
                                    if (player.inventory.gold >= COST_AP_TRUNG_NHANH) {
                                        player.inventory.gold -= COST_AP_TRUNG_NHANH;
                                        player.billEgg.timeDone = 0;
                                        Service.gI().sendMoney(player);
                                        player.billEgg.sendBillEgg();
                                    } else {
                                        Service.gI().sendThongBao(player,
                                                "Bạn không đủ vàng để thực hiện, còn thiếu "
                                                + Util.numberToMoney((COST_AP_TRUNG_NHANH - player.inventory.gold)) + " vàng");
                                    }
                                }
                                break;
                            case ConstNpc.CAN_OPEN_EGG:
                                switch (select) {
                                    case 0:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_OPEN_BILL,
                                                "Bạn có chắc chắn cho trứng nở?\n"
                                                + "Đệ tử của bạn sẽ được thay thế bằng đệ Mabư",
                                                "Đệ mabư\nTrái Đất", "Đệ mabư\nNamếc", "Đệ mabư\nXayda", "Từ chối");
                                        break;
                                    case 1:
                                        this.createOtherMenu(player, ConstNpc.CONFIRM_DESTROY_BILL,
                                                "Bạn có chắc chắn muốn hủy bỏ trứng Mabư?", "Đồng ý", "Từ chối");
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_OPEN_BILL:
                                switch (select) {
                                    case 0:
                                        player.billEgg.openEgg(ConstPlayer.TRAI_DAT);
                                        break;
                                    case 1:
                                        player.billEgg.openEgg(ConstPlayer.NAMEC);
                                        break;
                                    case 2:
                                        player.billEgg.openEgg(ConstPlayer.XAYDA);
                                        break;
                                    default:
                                        break;
                                }
                                break;
                            case ConstNpc.CONFIRM_DESTROY_BILL:
                                if (select == 0) {
                                    player.billEgg.destroyEgg();
                                }
                                break;
                        }
                    }

                }
            }
        };
    }

    public static Npc quocVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                this.createOtherMenu(player, ConstNpc.BASE_MENU,
                        "Con muốn nâng giới hạn sức mạnh cho bản thân hay đệ tử?",
                        "Bản thân", "Đệ tử", "Từ chối");
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0:
                                if (player.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                    this.createOtherMenu(player, ConstNpc.OPEN_POWER_MYSEFT,
                                            "Ta sẽ truyền năng lượng giúp con mở giới hạn sức mạnh của bản thân lên "
                                            + Util.numberToMoney(player.nPoint.getPowerNextLimit()),
                                            "Nâng\ngiới hạn\nsức mạnh",
                                            "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                } else {
                                    this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                            "Sức mạnh của con đã đạt tới giới hạn",
                                            "Đóng");
                                }
                                break;
                            case 1:
                                if (player.pet != null) {
                                    if (player.pet.nPoint.limitPower < NPoint.MAX_LIMIT) {
                                        this.createOtherMenu(player, ConstNpc.OPEN_POWER_PET,
                                                "Ta sẽ truền năng lượng giúp con mở giới hạn sức mạnh của đệ tử lên "
                                                + Util.numberToMoney(player.pet.nPoint.getPowerNextLimit()),
                                                "Nâng ngay\n" + Util.numberToMoney(OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) + " vàng", "Đóng");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.IGNORE_MENU,
                                                "Sức mạnh của đệ con đã đạt tới giới hạn",
                                                "Đóng");
                                    }
                                } else {
                                    Service.gI().sendThongBao(player, "Không thể thực hiện");
                                }
                                //giới hạn đệ tử
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_MYSEFT) {
                        switch (select) {
                            case 0:
                                OpenPowerService.gI().openPowerBasic(player);
                                break;
                            case 1:
                                if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                    if (OpenPowerService.gI().openPowerSpeed(player)) {
                                        player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                        Service.gI().sendMoney(player);
                                    }
                                } else {
                                    Service.gI().sendThongBao(player,
                                            "Bạn không đủ vàng để mở, còn thiếu "
                                            + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                                }
                                break;
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.OPEN_POWER_PET) {
                        if (select == 0) {
                            if (player.inventory.gold >= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER) {
                                if (OpenPowerService.gI().openPowerSpeed(player.pet)) {
                                    player.inventory.gold -= OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER;
                                    Service.gI().sendMoney(player);
                                }
                            } else {
                                Service.gI().sendThongBao(player,
                                        "Bạn không đủ vàng để mở, còn thiếu "
                                        + Util.numberToMoney((OpenPowerService.COST_SPEED_OPEN_LIMIT_POWER - player.inventory.gold)) + " vàng");
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc bulmaTL(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu bé muốn mua gì nào?", "Cửa hàng", "Đóng");
                        }
                    } else if (this.mapId == 5) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 102) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_FUTURE", true);
                            }
                        }
                    } else if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc rongOmega(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    BlackBallWar.gI().setTime();
                    if (this.mapId == 24 || this.mapId == 25 || this.mapId == 26) {
                        try {
                            long now = System.currentTimeMillis();
                            if (now > BlackBallWar.TIME_OPEN && now < BlackBallWar.TIME_CLOSE) {
                                this.createOtherMenu(player, ConstNpc.MENU_OPEN_BDW, "Đường đến với ngọc rồng sao đen đã mở, "
                                        + "ngươi có muốn tham gia không?",
                                        "Hướng dẫn\nthêm", "Tham gia", "Từ chối");
                            } else {
                                String[] optionRewards = new String[7];
                                int index = 0;
                                for (int i = 0; i < 7; i++) {
                                    if (player.rewardBlackBall.timeOutOfDateReward[i] > System.currentTimeMillis()) {
                                        String quantily = player.rewardBlackBall.quantilyBlackBall[i] > 1
                                                ? "x" + player.rewardBlackBall.quantilyBlackBall[i] + " "
                                                : "";
                                        optionRewards[index] = quantily + (i + 1) + " sao";
                                        index++;
                                    }
                                }
                                if (index != 0) {
                                    String[] options = new String[index + 1];
                                    for (int i = 0; i < index; i++) {
                                        options[i] = optionRewards[i];
                                    }
                                    options[options.length - 1] = "Từ chối";
                                    this.createOtherMenu(player, ConstNpc.MENU_REWARD_BDW,
                                            "Ngươi có một vài phần thưởng ngọc "
                                            + "rồng sao đen đây!",
                                            options);
                                } else {
                                    this.createOtherMenu(player, ConstNpc.MENU_NOT_OPEN_BDW,
                                            "Ta có thể giúp gì cho ngươi?", "Hướng dẫn", "Từ chối");
                                }
                            }
                        } catch (Exception ex) {
                            Logger.error("Lỗi mở menu rồng Omega");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.MENU_REWARD_BDW:
                            player.rewardBlackBall.getRewardSelect((byte) select);
                            break;
                        case ConstNpc.MENU_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            } else if (select == 1) {
//                                if (!player.getSession().actived) {
//                                    Service.gI().sendThongBao(player, "Vui lòng kích hoạt tài khoản để sử dụng chức năng này");
//
//                                } else
                                player.iDMark.setTypeChangeMap(ConstMap.CHANGE_BLACK_BALL);
                                ChangeMapService.gI().openChangeMapTab(player);
                            }
                            break;
                        case ConstNpc.MENU_NOT_OPEN_BDW:
                            if (select == 0) {
                                NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_BLACK_BALL_WAR);
                            }
                            break;
                    }
                }
            }

        };
    }

    public static Npc rong1_to_7s(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isHoldBlackBall()) {
                        this.createOtherMenu(player, ConstNpc.MENU_PHU_HP, "Ta có thể giúp gì cho ngươi?", "Phù hộ", "Từ chối");
                    } else {
                        if (BossManager.gI().existBossOnPlayer(player)
                                || player.zone.items.stream().anyMatch(itemMap -> ItemMapService.gI().isBlackBall(itemMap.itemTemplate.id))
                                || player.zone.getPlayers().stream().anyMatch(p -> p.iDMark.isHoldBlackBall())) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối");
                        } else {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_GO_HOME, "Ta có thể giúp gì cho ngươi?", "Về nhà", "Từ chối", "Gọi BOSS");
                        }
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.getIndexMenu() == ConstNpc.MENU_PHU_HP) {
                        if (select == 0) {
                            this.createOtherMenu(player, ConstNpc.MENU_OPTION_PHU_HP,
                                    "Ta sẽ giúp ngươi tăng HP lên mức kinh hoàng, ngươi chọn đi",
                                    "x3 HP\n" + Util.numberToMoney(BlackBallWar.COST_X3) + " vàng",
                                    "x5 HP\n" + Util.numberToMoney(BlackBallWar.COST_X5) + " vàng",
                                    "x7 HP\n" + Util.numberToMoney(BlackBallWar.COST_X7) + " vàng",
                                    "Từ chối"
                            );
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_GO_HOME) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, player.gender + 21, -1, 250);
                        } else if (select == 2) {
                            BossManager.gI().callBoss(player, mapId);
                        } else if (select == 1) {
                            this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                        }
                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PHU_HP) {
                        if (player.effectSkin.xHPKI > 1) {
                            Service.gI().sendThongBao(player, "Bạn đã được phù hộ rồi!");
                            return;
                        }
                        switch (select) {
                            case 0:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X3);
                                break;
                            case 1:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X5);
                                break;
                            case 2:
                                BlackBallWar.gI().xHPKI(player, BlackBallWar.X7);
                                break;
                            case 3:
                                this.npcChat(player, "Để ta xem ngươi trụ được bao lâu");
                                break;
                        }
                    }
                }
            }
        };
    }

    public static Npc npcThienSu64(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 7) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 0) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (this.mapId == 146) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 147) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 148) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Cậu không chịu nổi khi ở đây sao?\nCậu sẽ khó mà mạnh lên được", "Trốn về", "Ở lại");
                }
                if (this.mapId == 48) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Đã tìm đủ nguyên liệu cho tôi chưa?\n Tôi sẽ giúp cậu mạnh lên kha khá đấy!", "Hướng Dẫn",
                            "Đổi SKH VIP", "Từ Chối");
                }
            }

            //if (player.inventory.gold < 500000000) {
//                this.baHatMit.createOtherMenu(player, ConstNpc.IGNORE_MENU, "Hết tiền rồi\nẢo ít thôi con", "Đóng");
//                return;
//            }
            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu() && this.mapId == 7) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.gI().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 146, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 14) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.gI().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 148, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 0) {
                        if (select == 0) {
                            if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                player.inventory.gold -= COST_HD;
                                Service.gI().sendMoney(player);
                                ChangeMapService.gI().changeMapBySpaceShip(player, 147, -1, 168);
                            } else {
                                this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                            }
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 147) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 0, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 148) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 14, -1, 450);
                        }
                        if (select == 1) {
                        }
                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 146) {
                        if (select == 0) {
                            ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, 450);
                        }
                        if (select == 1) {
                        }

                    }
                    if (player.iDMark.isBaseMenu() && this.mapId == 48) {
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_DOI_SKH_VIP);
                        }
                        if (select == 1) {
                            CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_VIP);
                        }

                    } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_DOI_SKH_VIP) {
                        if (select == 0) {
                            CombineServiceNew.gI().startCombine(player);
                        }
                    }
                }
            }

        };
    }

    public static Npc bill(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Ngươi chỉ cần mang Mảnh Chiến Lực đến đây\n Ta sẽ giúp ngươi có được những trang bị\n xịn nhất của ta!",
                            "Nâng Cấp\nHủy Diệt", "Nâng cấp\nThiên Sứ", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_HD);
                                        break;

                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_TS);
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_1) {
                                switch (player.combineNew.typeCombine) {
                                    case CombineServiceNew.NANG_CAP_SKH_HD:
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                        break;
                                    case CombineServiceNew.NANG_CAP_SKH_TS:
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                        break;
                                }
                            }
                        case 48:
                            if (player.iDMark.isBaseMenu()) {
                                switch (select) {
                                    case 0:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_HD);
                                        break;

                                    case 1:
                                        CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_SKH_TS);
                                        break;
                                }
                            } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_CAP_1) {
                                switch (player.combineNew.typeCombine) {
                                    case CombineServiceNew.NANG_CAP_SKH_HD:
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                        break;
                                    case CombineServiceNew.NANG_CAP_SKH_TS:
                                        if (select == 0) {
                                            CombineServiceNew.gI().startCombine(player);
                                        }
                                        break;
                                }
                            }
                    }

                }
            }
        };
    }

    public static Npc boMong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "|2|Hông nàm mà đoài cóa ăn thì chỉ cóa ăn \b|7|dau buoi an cut!", "Nhiệm vụ\nhàng ngày", "Từ chối");
                    }
//                    if (this.mapId == 47) {
//                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
//                                "Xin chào, cậu muốn tôi giúp gì?", "Từ chối");
//                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 47 || this.mapId == 84) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    if (player.playerTask.sideTask.template != null) {
                                        String npcSay = "Nhiệm vụ hiện tại: " + player.playerTask.sideTask.getName() + " ("
                                                + player.playerTask.sideTask.getLevel() + ")"
                                                + "\nHiện tại đã hoàn thành: " + player.playerTask.sideTask.count + "/"
                                                + player.playerTask.sideTask.maxCount + " ("
                                                + player.playerTask.sideTask.getPercentProcess() + "%)\nSố nhiệm vụ còn lại trong ngày: "
                                                + player.playerTask.sideTask.leftTask + "/" + ConstTask.MAX_SIDE_TASK;
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_PAY_SIDE_TASK,
                                                npcSay, "Trả nhiệm\nvụ", "Hủy nhiệm\nvụ");
                                    } else {
                                        this.createOtherMenu(player, ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK,
                                                "Tôi có vài nhiệm vụ theo cấp bậc, "
                                                + "sức cậu có thể làm được cái nào?",
                                                "Dễ", "Bình thường", "Khó", "Siêu khó", "Địa ngục", "Từ chối");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_LEVEL_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                case 1:
                                case 2:
                                case 3:
                                case 4:
                                    TaskService.gI().changeSideTask(player, (byte) select);
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_OPTION_PAY_SIDE_TASK) {
                            switch (select) {
                                case 0:
                                    TaskService.gI().paySideTask(player);
                                    break;
                                case 1:
                                    TaskService.gI().removeSideTask(player);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc karin(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "\nTa sẽ giúp ngươi chuyển sinh nếu ngươi\nđã đạt tối đa sức mạnh"
                                    + "\nNgươi sẽ được reset về 2k sức mạnh"
                                    + "\nvà ngươi sẽ nhận được 2000sdg + 10k ki,hpg"
                                    // + "\n"
                                    // + "\nNgươi cũng có thể chuyển sinh nhanh với giá 5k Điểm Đổi"
                                    // + "\nNgươi có thể chuyển sinh nhanh 100 lần với giá 500k Điểm Đổi"
                                    // + "\n Khi chuyển sinh 100 lần sẽ được BONUS thêm 20k SDG"
                                    + "\n Bạn đã chuyển sinh được " + player.chuyenSinh + " Lần",
                                    "Chuyển sinh",
                                    "Đóng");
                        }
                    } else if (this.mapId == 104) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Kính chào Ngài Linh thú sư!", "Cửa hàng", "Đóng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                OpenPowerService.gI().chuyenSinh(player);
                            }
                            // if (select == 1) {
                            //     if (player.getSession().actived) {
                            //         if (player.chuyenSinh >= 3 && player.ngoccs == 0) {
                            //             player.inventory.ruby += 300000;
                            //             player.ngoccs = 1;
                            //             Service.gI().sendThongBao(player, "Bạn vừa nhận được 300k Hồng ngọc");
                            //         } else {
                            //             Service.gI().sendThongBao(player, "Chưa đủ điều kiện");
                            //         }

                            //     } else {
                            //         Service.gI().sendThongBao(player, "Mở thành viên mới được nhận ngọc này");
                            //     }
                            // }
                            // if (select == 2) {
                            //     if (player.tongnap >= 5000) {
                            //         OpenPowerService.gI().chuyenSinhNhanh(player);
                            //     } else {
                            //         Service.gI().sendThongBao(player, "Điểm Đổi Méo Đủ");
                            //     }
                            // }
                            // if (select == 3) {
                            //     if (player.tongnap >= 500000) {
                            //         OpenPowerService.gI().chuyenSinhNhanhVIP(player);
                            //     } else {
                            //         Service.gI().sendThongBao(player, "Điểm Đổi Méo Đủ");
                            //     }
                            // }
                        }

                    } else if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                ShopServiceNew.gI().opendShop(player, "BUNMA_LINHTHU", true);
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc vados(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player) && this.mapId == 5) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|2|Ta Vừa Hắc Mắp Xêm Được Tóp Của Toàn Server\b|7|Người Muốn Xem Tóp Gì?",
                            "Top Nhiệm Vụ", "Top Chuyển Sinh", "Top Nạp", "Đóng");
                }
                if (canOpenNpc(player) && this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (canOpenNpc(player) && this.mapId == 7) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }
                if (canOpenNpc(player) && this.mapId == 0) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Ta sẽ dẫn cậu tới hành tinh Berrus với điều kiện\n 2. đạt 80 tỷ sức mạnh "
                            + "\n 3. chi phí vào cổng  50 triệu vàng", "Tới ngay", "Từ chối");
                }

            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:

                                    if (select == 0) {
                                        Service.gI().showListTop(player, Manager.topNV);
                                        break;
                                    }

                                    if (select == 1) {
                                        Service.gI().showListTop(player, Manager.topCS);
                                        break;
                                    }
                                    if (select == 2) {
                                        Service.gI().showListTop(player, Manager.topNap);
                                        break;
                                    }
                                    // if (select == 3) {
                                    //     Service.gI().showListTop(player, Manager.topKhi);
                                    //     break;
                                    // }
                                    // if (select == 4) {
                                    //     Service.gI().showListTop(player, Manager.topDiem);
                                    //     break;
                                    // }
                                    break;
                            }
                            break;
                        case 0:
                        case 7:
                        case 14:
                            if (select == 0) {
                                if (player.getSession().player.nPoint.power >= 80000000000L && player.inventory.gold > COST_HD) {
                                    player.inventory.gold -= COST_HD;
                                    Service.gI().sendMoney(player);
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 146, -1, 168);
                                } else {
                                    this.npcChat(player, "Bạn chưa đủ điều kiện để vào");
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_1(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 80) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Tới hành tinh\nYardart", "Từ chối");
                    } else if (this.mapId == 131) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin chào, tôi có thể giúp gì cho cậu?", "Quay về", "Từ chối");
                    } else {
                        super.openBaseMenu(player);
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (player.iDMark.getIndexMenu()) {
                        case ConstNpc.BASE_MENU:
                            if (this.mapId == 131) {
                                if (select == 0) {
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 80, -1, 870);
                                }
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc mavuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 153) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Xin chào, tôi có thể giúp gì cho cậu?", "Tây thánh địa", "Từ chối");
                    } else if (this.mapId == 156) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn trở về?", "Quay về", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 153) {
                        if (player.iDMark.isBaseMenu()) {
                            if (select == 0) {
                                //đến tay thanh dia
                                ChangeMapService.gI().changeMapBySpaceShip(player, 156, -1, 360);
                            }
                        }
                    } else if (this.mapId == 156) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                //về lanh dia bang hoi
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 153, -1, 432);
                                    break;
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc gokuSSJ_2(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU, "Bạn đang có " + biKiep.quantity + " bí kiếp.\n"
                                    + "Hãy kiếm đủ 10000 bí kiếp tôi sẽ dạy bạn cách dịch chuyển tức thời của người Yardart", "Học dịch\nchuyển", "Đóng");
                        }

                    } catch (Exception ex) {
                        ex.printStackTrace();

                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    try {
                        Item biKiep = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 590);
                        if (biKiep != null) {
                            if (biKiep.quantity >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item yardart = ItemService.gI().createNewItem((short) (player.gender + 592));
                                yardart.itemOptions.add(new Item.ItemOption(47, 400));
                                yardart.itemOptions.add(new Item.ItemOption(108, 10));
                                InventoryServiceNew.gI().addItemBag(player, yardart);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, biKiep, 10000);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn vừa nhận được trang phục tộc Yardart");
                            }
                        }
                    } catch (Exception ex) {

                    }
                }
            }
        };
    }

    public static Npc khidaumoi(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (this.mapId == 5) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Mày muốn nâng cấp khỉ ư? Hay Mày muốn đổi cải trang mới?", "Nâng cấp\nkhỉ", "Shop của Khỉ", "Mua khỉ", "Từ chối");
                }
                if (this.mapId == 14) {
                    this.createOtherMenu(player, ConstNpc.BASE_MENU,
                            "Mày biến qua đảo kame hộ bố", "Concac", "Dạ");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 1,
                                            "|7|Cần Khỉ Lv1,2,3,4,5,6,7 để nâng cấp lên ct khỉ cấp cao hơn\b|2|Mỗi lần nâng cấp tiếp thì mỗi cấp cần thêm 10 đá ngũ sắc",
                                            "Nâng cấp",
                                            "Từ chối");
                                    break;
                                case 1: //shop
                                    ShopServiceNew.gI().opendShop(player, "KHI", false);
                                    break;
                                case 2: //shop
                                    this.createOtherMenu(player, 2,
                                            "Ngươi muốn mua khỉ loại gì?"
                                            + "\n|7|100k: Khỉ 8 Ultra thường là khỉ nâng được từ shop\n"
                                            + "\n300k: Khỉ 8 Ultra trung cấp sẽ mạnh hơn 50% khỉ 8 thường\n"
                                            + "\n|7|500k: Khir8 Ultra cao cấp sẽ mạnh hơn 50% khỉ 8 trung cấp\n",
                                            "Mua\nKhỉ 8 thường", "Mua\nKhỉ 8\ntrung cấp", "Mua\nKhỉ 8\ncao cấp",
                                            "Từ chối");

                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
                                    CombineServiceNew.gI().openTabCombine(player, CombineServiceNew.NANG_CAP_KHI);
                                    break;
                                case 1:
                                    break;
                            }

                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_NANG_KHI) {
                            switch (player.combineNew.typeCombine) {
                                case CombineServiceNew.NANG_CAP_KHI:
                                    if (select == 0) {
                                        CombineServiceNew.gI().startCombine(player);
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 2) {
                            switch (select) {
                                case 0: {
                                    if (player.tongnap < 100000) {

                                        Service.gI().sendThongBao(player, "Điểm đổi của bạn không đủ 100k");
                                        return;
                                    }
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
                                        Service.gI().sendThongBao(player, "Hành trang của bạn không đủ");
                                        return;
                                    }
                                    PlayerDAO.subtn(player, 100000);
                                    Item item = ItemService.gI().createNewItem((short) (1136));
                                    item.itemOptions.add(new Item.ItemOption(50, 80));
                                    item.itemOptions.add(new Item.ItemOption(77, 120));
                                    item.itemOptions.add(new Item.ItemOption(103, 120));
                                    item.itemOptions.add(new Item.ItemOption(14, 40));
                                    item.itemOptions.add(new Item.ItemOption(5, 50));
                                    item.itemOptions.add(new Item.ItemOption(106, 1));
                                    item.itemOptions.add(new Item.ItemOption(34, 1));
                                    item.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Mua thành công khỉ 8 thường");
                                    break;
                                }
                                case 1: {
                                    if (player.tongnap < 300000) {

                                        Service.gI().sendThongBao(player, "Điểm đổi của bạn không đủ 300k");
                                        return;
                                    }
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
                                        Service.gI().sendThongBao(player, "Hành trang của bạn không đủ");
                                        return;
                                    }
                                    PlayerDAO.subtn(player, 300000);
                                    Item item = ItemService.gI().createNewItem((short) (1136));
                                    item.itemOptions.add(new Item.ItemOption(50, 120));
                                    item.itemOptions.add(new Item.ItemOption(77, 180));
                                    item.itemOptions.add(new Item.ItemOption(103, 180));
                                    item.itemOptions.add(new Item.ItemOption(14, 50));
                                    item.itemOptions.add(new Item.ItemOption(5, 75));
                                    item.itemOptions.add(new Item.ItemOption(106, 1));
                                    item.itemOptions.add(new Item.ItemOption(34, 1));
                                    item.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Mua thành công khỉ 8 trung cấp");
                                    break;
                                }
                                case 2: {
                                    if (player.tongnap < 500000) {

                                        Service.gI().sendThongBao(player, "Điểm đổi của bạn không đủ 500k");
                                        return;
                                    }
                                    if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
                                        Service.gI().sendThongBao(player, "Hành trang của bạn không đủ");
                                        return;
                                    }
                                    PlayerDAO.subtn(player, 500000);
                                    Item item = ItemService.gI().createNewItem((short) (1136));
                                    item.itemOptions.add(new Item.ItemOption(50, 180));
                                    item.itemOptions.add(new Item.ItemOption(77, 270));
                                    item.itemOptions.add(new Item.ItemOption(103, 270));
                                    item.itemOptions.add(new Item.ItemOption(14, 55));
                                    item.itemOptions.add(new Item.ItemOption(5, 113));
                                    item.itemOptions.add(new Item.ItemOption(106, 1));
                                    item.itemOptions.add(new Item.ItemOption(34, 1));
                                    item.itemOptions.add(new Item.ItemOption(30, 1));
                                    InventoryServiceNew.gI().addItemBag(player, item);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    Service.gI().sendThongBao(player, "Mua thành công khỉ 8 cao cấp");
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc GhiDanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player pl) {
                if (canOpenNpc(pl)) {
                    createOtherMenu(pl, 0, DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Giai(pl), "Thông tin\nChi tiết", DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(pl) ? "Đăng ký" : "OK", "Đại Hội\nVõ Thuật\nLần thứ\n23");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (select) {
                        case 0:
                            Service.gI().sendPopUpMultiLine(player, tempId, avartar, DaiHoiVoThuat.gI().Info());
                            break;
                        case 1:
                            if (DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).CanReg(player)) {
                                DaiHoiVoThuatService.gI(DaiHoiVoThuat.gI().getDaiHoiNow()).Reg(player);
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc unkonw(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        this.createOtherMenu(player, 0,
                                "Éc éc Bạn muốn gì ở tôi :3?", "Đến Võ đài Unknow", "Đổi Rương Đồng Vàng", "Hướng Dẫn Sự Kiện");
                    }
                    if (this.mapId == 112) {
                        this.createOtherMenu(player, 0,
                                "Bạn đang còn : " + player.pointPvp + " điểm PvP Point", "Về đảo Kame", "Đổi Cải trang sự kiên", "Top PVP");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    if (player.getSession().player.nPoint.power >= 10000000000L) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 112, -1, 495);
                                        Service.gI().changeFlag(player, Util.nextInt(8));
                                    } else {
                                        this.npcChat(player, "Bạn cần 10 tỷ sức mạnh mới có thể vào");
                                    }
                                    break; // qua vo dai
                                case 1:
                                    Input.gI().createFormTradeRuongDongVang(player);
                                    break;
                                case 2:
                                    NpcService.gI().createTutorial(player, this.avartar, ConstNpc.HUONG_DAN_SK);
                                    break;

                            }
                        }
                    }

                    if (this.mapId == 112) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 319);
                                    break; // ve dao kame
                                case 1:  // 
                                    this.createOtherMenu(player, 1,
                                            "Bạn có muốn đổi 500 điểm PVP lấy \n|6|Cải trang Mèo Kid Lân với tất cả chỉ số là 30%\n ", "Ok", "Không");
                                    // bat menu doi item
                                    break;

                                /*case 2:  // 
                                    Service.gI().showListTop(player, Manager.topPVP);
                                    // mo top pvp
                                    break;*/
                            }
                        }
                        if (player.iDMark.getIndexMenu() == 1) { // action doi item
                            switch (select) {
                                case 0: // trade
                                    if (player.pointPvp >= 500) {
                                        player.pointPvp -= 500;
                                        Item item = ItemService.gI().createNewItem((short) (1104));
                                        item.itemOptions.add(new Item.ItemOption(49, 30));
                                        item.itemOptions.add(new Item.ItemOption(77, 15));
                                        item.itemOptions.add(new Item.ItemOption(103, 20));
                                        item.itemOptions.add(new Item.ItemOption(207, 0));
                                        item.itemOptions.add(new Item.ItemOption(33, 0));
//                                      
                                        InventoryServiceNew.gI().addItemBag(player, item);
                                        Service.gI().sendThongBao(player, "Chúc Mừng Bạn Đổi Cải Trang Thành Công !");
                                    } else {
                                        Service.gI().sendThongBao(player, "Không đủ điểm bạn còn " + (500 - player.pointPvp) + " Điểm nữa");
                                    }
                                    break;

                            }

                        }
                    }
                }
            }
        };
    }

    public static Npc monaito(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        this.createOtherMenu(player, 0,
                                "Chào bạn tôi sẽ đưa bạn đến hành tinh Cereal?\n"
                                + "Tỉ lệ SPL đổi tv sẽ x2 ở ngoài\n"
                                + "Sẽ có tỉ lệ cao rơi đồ siêu hủy diệt",
                                "Đồng ý", "Từ chối");
                    }
                    if (this.mapId == 170) {
                        this.createOtherMenu(player, 0,
                                "Ta ở đây để đưa con về", "Về Làng Mori", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 7) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    if (player.getSession().actived) {
                                        if (player.mapmoi != 0) {
                                            ChangeMapService.gI().changeMapBySpaceShip(player, 170, -1, 264);
                                            break; // den hanh tinh cereal
                                        } else {
                                            Service.gI().sendThongBao(player, "Bạn cần mở khu vực. Đến đảo kame gặp ông gohan");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                            }
                        }
                    }
                    if (this.mapId == 170) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 7, -1, 432);
                                    break; // quay ve

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc granala(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {

            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 171) {
                        this.createOtherMenu(player, 0,
                                "Ngươi!\n Hãy cầm đủ 7 viên ngọc rồng \n Monaito đến đây gặp ta ta sẽ ban cho ngươi\n 1 điều ước ", "Gọi rồng", "Từ chối");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {

                    if (this.mapId == 171) {
                        if (player.iDMark.getIndexMenu() == 0) { // 
                            switch (select) {
                                case 0:
                                    this.npcChat(player, "Chức Năng Đang Được Update!");
                                    break; // goi rong

                            }
                        }
                    }
                }
            }
        };
    }

    public static Npc vihop(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|2|Ngươi cần gì ở ta?",
                            "Quy Đổi Mảnh Hồn", "Quy Đổi Đá Xanh Lam", "Quy Đổi Đồng Xu Vàng", "Shop Sự Kiện", "Shop Sơ Cấp", "Shop Cao Cấp", "Đóng");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    switch (this.mapId) {
                        case 5:
                            switch (player.iDMark.getIndexMenu()) {
                                case ConstNpc.BASE_MENU:

                                    if (select == 0) {
                                        Input.gI().createFormQDDXL(player);
                                        break;
                                    }
                                    if (select == 1) {
                                        Input.gI().createFormQDMH(player);
                                        break;
                                    }
                                    if (select == 2) {
                                        Input.gI().createFormQDDXV(player);
                                        break;
                                    }
                                    if (select == 3) {
                                        ShopServiceNew.gI().opendShop(player, "VIHOP", true);

                                        break;
                                    }
                                    if (select == 4) {
                                        ShopServiceNew.gI().opendShop(player, "NOEL1", true);

                                        break;
                                    }
                                    if (select == 5) {
                                        ShopServiceNew.gI().opendShop(player, "NOEL2", true);

                                        break;
                                    }
                                    break;
                            }
                            break;
                    }
                }
            }
        };
    }

    public static Npc khudohieu(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                        this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                "Người muốn mua đồ hiệu à ?", "Cửa\nhàng");
                    }
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        switch (select) {
                            case 0://Shop

                                ShopServiceNew.gI().opendShop(player, "KHUDOHIEU", true);

                                break;

                        }
                    }
                }
            }
        };
    }

    public static Npc Nak(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 5) {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Thiên tử sở hữu 1 sức mạnh khủng khiếp !", "Hướng dẫn", "Đến máp thiên tử", "Mở giới hạn\nthiên tử", "Nâng Cấp Cải\nTrang Thiên Tử");
                        }
                    } else {
                        if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
                            this.createOtherMenu(player, ConstNpc.BASE_MENU,
                                    "Thiên tử sở hữu 1 sức mạnh khủng khiếp !", "Hướng dẫn", "Đổi đồ\nthiên tử", "Nhận Ấn thiên tử");
                        }

                    }

                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (player.iDMark.isBaseMenu()) {
                        if (this.mapId == 5) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, "Xin Chào"
                                            + "\n|3|Muốn có được đồ Thiên Tử thì phải mở giới hạn Thiên Tử, cần 200k điểm đổi"
                                            + "\n|3|Mở mới giạn Thiên Tử mới được qua Map Thiên Tử úp Lõi Thiên Tử"
                                            + "\n|3|Dùng Lõi Thiên Tử kết hợp với Trang Bị SKH Thiên Sứ để đổi đồ Thiên Tử"
                                            + "\n|3|Đồ Thiên Tử cũng có thể nâng SKH ở Map Thiên Tử (NPC Bà Hạt Mít)"
                                            + "\n|3|Sau khi mang full set Thiên Tử và cải trang Goku SSJ Thien Tu, sẽ được chọn lõi sức mạnh Thiên Tử"
                                            + "\n|1|Thiên tử sát thương: x2 tấn công"
                                            + "\n|1|Thiên tử huyết long: x2 HP"
                                            + "\n|1|Thiên tử thể trạng: x2 KI", "Đóng"
                                    );
                                    break;
                                case 1://Shop
                                    if (player.mott == 1) {
                                        ChangeMapService.gI().changeMapBySpaceShip(player, 174, -1, 432);
                                    } else {
                                        this.npcChat(player, "Bạn chưa mở giới hạn thiên tử. ");
                                    }
                                    break;
                                case 2:
                                    if (player.mott == 1) {
                                        this.npcChat(player, "Bạn mở rồi mà ?? ");
                                    } else {
                                        if (!player.getSession().actived) {
                                            this.npcChat(player, "Bạn chưa MTV");
                                        } else {
                                            if (player.tongnap < 200000) {
                                                this.npcChat(player, "Bạn không đủ 200k");
                                            } else {
                                                PlayerDAO.subtn(player, 200000);
                                                player.mott = 1;
                                                this.npcChat(player, "Mở thiên tử thành công");
                                            }
                                        }
                                    }
                                    break;
                                case 3: //shop
                                    this.createOtherMenu(player, 3,
                                            "|7|Ngươi Muốn Nâng Cấp Cải Trang Hả?"
                                            + "\n|3| Khi Nâng Cấp Cần Có 1 Khỉ Ultra và 1 Vé Nâng Cấp GOKU Thiên Tử"
                                            + "\n|3| Ngươi phải chắc chắn trong hành trang có 1 cải trang khỉ ultra bất kì"
                                            + "\n|3| Và một Vé Nâng Cấp Goku Thiên Tử\n"
                                            + "\n|3|Tỉ lệ cải trang sau khi đổi sẽ từ 100% - 300% sức đánh và HP,KI tương đương , tùy nhân phẩm của ngươi "
                                            + "\n|3|Nếu ngươi muốn Nâng Cấp , Hãy nhấn Nâng Cấp\n",
                                            "Nâng Cấp\nCải Trang",
                                            "Từ chối");
                                    break;

                            }
                        } else {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, ConstNpc.BASE_MENU, ""
                                            + "\n|3|Muốn có được đồ Thiên Tử thì phải mở giới hạn Thiên Tử, cần 200k điểm đổi"
                                            + "\n|3|Mở mới giạn Thiên Tử mới được qua Map Thiên Tử úp Lõi Thiên Tử"
                                            + "\n|3|Dùng Lõi Thiên Tử kết hợp với Trang Bị SKH Thiên Sứ để đổi đồ Thiên Tử"
                                            + "\n|3|Đồ Thiên Tử cũng có thể nâng SKH ở Map Thiên Tử (NPC Bà Hạt Mít)"
                                            + "\n|3|Sau khi mang full set Thiên Tử và cải trang Goku SSJ Thien Tu, sẽ được chọn lõi sức mạnh Thiên Tử"
                                            + "\n|1|Thiên tử sát thương: x2 tấn công"
                                            + "\n|1|Thiên tử huyết long: x2 HP"
                                            + "\n|1|Thiên tử thể trạng: x2 KI", "Ok"
                                    );
                                    break;
                                case 1:
                                    this.createOtherMenu(player, ConstNpc.MUA_TT, "|3|Cần x999 lõi thiên tử để đổi áo , giày, quần, lắc thiên tử"
                                            + "\n|3|Cần x9999 lõi thiên tử để đổi găng, cải trang GoKu Thiên Tử\n"
                                            + "\n|3|LƯU Ý: Khi đổi đồ thiên tử bắt buộc ngươi phải có trang bị SKH Thiên Sứ để trao đổi.\n Ví dụ: Đổi áo thiên tử cần x999 Lõi Thiên Tử + 1 Áo SKH Thiên Sứ chỉ số bất kì",
                                            "Áo", "Quần", "Găng", "Giày", "Lắc", "GoKu Thiên Tử", "Không thèm");
                                    break;
                                case 2:
                                    if (player.mott == 1) {
                                        Item aott = ItemService.gI().createNewItem((short) 2137);
                                        aott.itemOptions.add(new Item.ItemOption(30, 1));
                                        InventoryServiceNew.gI().addItemBag(player, aott);
                                        InventoryServiceNew.gI().sendItemBags(player);
                                        this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                    } else {
                                        this.npcChat(player, "Bạn chưa mở giới hạn thiên tử");
                                    }
                                    break;
                            }
                        }

                    } 
                    // else if (player.iDMark.getIndexMenu() == ConstNpc.MENU_START_COMBINE) {
                    //     switch (player.combineNew.typeCombine) {
                    //         case CombineServiceNew.NANG_CAP_CHAN_MENH:
                    //             if (select == 0) {
                    //                 CombineServiceNew.gI().startCombine(player);
                    //             }
                    //             break;

                    //     }
                    // }
                   else if (player.iDMark.getIndexMenu() == ConstNpc.MUA_TT) {
                        switch (select) {
                            case 0: {
                                Item thientu = null;
                                Item aoTSTD = null;
                                Item aoTSNM = null;
                                Item aoTSXD = null;

                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                    aoTSTD = InventoryServiceNew.gI().findItemBag(player, 1048);
                                    aoTSNM = InventoryServiceNew.gI().findItemBag(player, 1049);
                                    aoTSXD = InventoryServiceNew.gI().findItemBag(player, 1050);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }

                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }

                                if (thientu == null || thientu.quantity < 999) {
                                    this.npcChat(player, "Bạn không đủ 999 Lõi thiên tử");
                                    return;
                                }

                                if (player.gender == 0) {
                                    if (aoTSTD == null || aoTSTD.quantity < 1 || !aoTSTD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có áo SKH Thiên Sứ TD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, aoTSTD, 1048);
                                    }
                                }

                                if (player.gender == 1) {
                                    if (aoTSNM == null || aoTSNM.quantity < 1 || !aoTSNM.isSKH()) {
                                        this.npcChat(player, "Bạn không Có áo SKH Thiên Sứ XD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, aoTSNM, 1049);
                                    }
                                }

                                if (player.gender == 2) {
                                    if (aoTSXD == null || aoTSXD.quantity < 1 || !aoTSXD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có áo SKH Thiên Sứ NM");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, aoTSXD, 1050);
                                    }
                                }
                                InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                Item aott = ItemService.gI().createNewItem((short) 2156);
                                aott.itemOptions.add(new Item.ItemOption(47, 4000));
                                aott.itemOptions.add(new Item.ItemOption(21, 400));
                                aott.itemOptions.add(new Item.ItemOption(30, 1));
                                aott.itemOptions.add(new Item.ItemOption(36, 1));
                                InventoryServiceNew.gI().addItemBag(player, aott);
                                InventoryServiceNew.gI().sendItemBags(player);
                                this.npcChat(player, "Bạn nhận được 1" + aott.template.name);

                                break;
                            }
                            case 1: {
                                Item thientu = null;
                                Item quanTSTD = null;
                                Item quanTSNM = null;
                                Item quanTSXD = null;
                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                    quanTSTD = InventoryServiceNew.gI().findItemBag(player, 1051);
                                    quanTSNM = InventoryServiceNew.gI().findItemBag(player, 1052);
                                    quanTSXD = InventoryServiceNew.gI().findItemBag(player, 1053);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }

                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }
                                if (thientu == null || thientu.quantity < 999) {
                                    this.npcChat(player, "Bạn không đủ 999 Lõi thiên tử");
                                    return;
                                }

                                if (player.gender == 0) {
                                    if (quanTSTD == null || quanTSTD.quantity < 1 || !quanTSTD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có quần SKH Thiên Sứ TD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, quanTSTD, 1051);
                                    }
                                }
                                if (player.gender == 1) {
                                    if (quanTSNM == null || quanTSNM.quantity < 1 || !quanTSNM.isSKH()) {
                                        this.npcChat(player, "Bạn không Có quần SKH Thiên Sứ NM");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, quanTSNM, 1052);
                                    }
                                }
                                if (player.gender == 2) {
                                    if (quanTSXD == null || quanTSXD.quantity < 1 || !quanTSXD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có quần SKH Thiên Sứ XD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, quanTSXD, 1053);
                                    }
                                }
                                InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                Item aott = ItemService.gI().createNewItem((short) 2157);
                                aott.itemOptions.add(new Item.ItemOption(22, 1000));
                                aott.itemOptions.add(new Item.ItemOption(21, 400));
                                aott.itemOptions.add(new Item.ItemOption(30, 1));
                                aott.itemOptions.add(new Item.ItemOption(36, 1));
                                InventoryServiceNew.gI().addItemBag(player, aott);
                                InventoryServiceNew.gI().sendItemBags(player);
                                this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                break;
                            }

                            case 2: {
                                Item thientu = null;
                                Item gangTSTD = null;
                                Item gangTSNM = null;
                                Item gangTSXD = null;

                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                    gangTSTD = InventoryServiceNew.gI().findItemBag(player, 1054);
                                    gangTSNM = InventoryServiceNew.gI().findItemBag(player, 1055);
                                    gangTSXD = InventoryServiceNew.gI().findItemBag(player, 1056);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }

                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }

                                if (thientu == null || thientu.quantity < 9999) {
                                    this.npcChat(player, "Bạn không đủ 9999 Lõi thiên tử");
                                    return;
                                }

                                if (player.gender == 0) {
                                    if (gangTSTD == null || gangTSTD.quantity < 1 || !gangTSTD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có găng SKH Thiên Sứ TD ");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, gangTSTD, 1054);
                                    }
                                }

                                if (player.gender == 1) {
                                    if (gangTSNM == null || gangTSNM.quantity < 1 || !gangTSNM.isSKH()) {
                                        this.npcChat(player, "Bạn không Có găng SKH Thiên Sứ NM");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, gangTSNM, 1055);
                                    }
                                }

                                if (player.gender == 2) {
                                    if (gangTSXD == null || gangTSXD.quantity < 1 || !gangTSXD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có găng SKH Thiên Sứ XD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, gangTSXD, 1056);
                                    }
                                }

                                InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 9999);
                                Item aott = ItemService.gI().createNewItem((short) 2158);
                                aott.itemOptions.add(new Item.ItemOption(220, 500));
                                aott.itemOptions.add(new Item.ItemOption(21, 400));
                                aott.itemOptions.add(new Item.ItemOption(30, 1));
                                aott.itemOptions.add(new Item.ItemOption(36, 1));
                                InventoryServiceNew.gI().addItemBag(player, aott);
                                InventoryServiceNew.gI().sendItemBags(player);
                                this.npcChat(player, "Bạn nhận được 1" + aott.template.name);

                                break;
                            }
                            case 3: {
                                Item thientu = null;
                                Item giayTSTD = null;
                                Item giayTSNM = null;
                                Item giayTSXD = null;
                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                    giayTSTD = InventoryServiceNew.gI().findItemBag(player, 1057);
                                    giayTSNM = InventoryServiceNew.gI().findItemBag(player, 1058);
                                    giayTSXD = InventoryServiceNew.gI().findItemBag(player, 1059);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }

                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }

                                if (thientu == null || thientu.quantity < 999) {
                                    this.npcChat(player, "Bạn không đủ 999 Lõi thiên tử");
                                    return;
                                }

                                if (player.gender == 0) {
                                    if (giayTSTD == null || giayTSTD.quantity < 1 || !giayTSTD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có giày SKH Thiên Sứ TD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, giayTSTD, 1057);
                                    }
                                }

                                if (player.gender == 1) {
                                    if (giayTSNM == null || giayTSNM.quantity < 1 || !giayTSNM.isSKH()) {
                                        this.npcChat(player, "Bạn không Có giày SKH Thiên Sứ NM");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, giayTSNM, 1058);
                                    }
                                }

                                if (player.gender == 2) {
                                    if (giayTSXD == null || giayTSXD.quantity < 1 || !giayTSXD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có giày SKH Thiên Sứ XD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, giayTSXD, 1059);
                                    }
                                }

                                InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                Item aott = ItemService.gI().createNewItem((short) 2159);
                                aott.itemOptions.add(new Item.ItemOption(23, 1000));
                                aott.itemOptions.add(new Item.ItemOption(21, 400));
                                aott.itemOptions.add(new Item.ItemOption(30, 1));
                                aott.itemOptions.add(new Item.ItemOption(36, 1));
                                InventoryServiceNew.gI().addItemBag(player, aott);
                                InventoryServiceNew.gI().sendItemBags(player);
                                this.npcChat(player, "Bạn nhận được 1" + aott.template.name);

                                break;
                            }
                            case 4: {
                                Item thientu = null;
                                Item nhanTSTD = null;
                                Item nhanTSNM = null;
                                Item nhanTSXD = null;
                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                    nhanTSTD = InventoryServiceNew.gI().findItemBag(player, 1060);
                                    nhanTSNM = InventoryServiceNew.gI().findItemBag(player, 1061);
                                    nhanTSXD = InventoryServiceNew.gI().findItemBag(player, 1062);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }

                                if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                    return;
                                }

                                if (thientu == null || thientu.quantity < 999) {
                                    this.npcChat(player, "Bạn không đủ 999 Lõi thiên tử");
                                    return;
                                }

                                if (player.gender == 0) {
                                    if (nhanTSTD == null || nhanTSTD.quantity < 1 || !nhanTSTD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có nhẫn SKH Thiên Sứ TD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nhanTSTD, 1060);
                                    }
                                }

                                if (player.gender == 1) {
                                    if (nhanTSNM == null || nhanTSNM.quantity < 1 || !nhanTSNM.isSKH()) {
                                        this.npcChat(player, "Bạn không Có giày SKH Thiên Sứ NM");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nhanTSNM, 1061);
                                    }
                                }

                                if (player.gender == 2) {
                                    if (nhanTSXD == null || nhanTSXD.quantity < 1 || !nhanTSXD.isSKH()) {
                                        this.npcChat(player, "Bạn không Có giày SKH Thiên Sứ XD");
                                        return;
                                    } else {
                                        InventoryServiceNew.gI().subQuantityItemsBag(player, nhanTSXD, 1062);
                                    }
                                }

                                InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 999);
                                Item aott = ItemService.gI().createNewItem((short) 2160);
                                aott.itemOptions.add(new Item.ItemOption(14, 20));
                                aott.itemOptions.add(new Item.ItemOption(21, 400));
                                aott.itemOptions.add(new Item.ItemOption(30, 1));
                                aott.itemOptions.add(new Item.ItemOption(36, 1));
                                InventoryServiceNew.gI().addItemBag(player, aott);
                                InventoryServiceNew.gI().sendItemBags(player);
                                this.npcChat(player, "Bạn nhận được 1" + aott.template.name);

                                break;
                            }
                            case 5: { //caitrang thientu
                                Item thientu = null;
                                try {
                                    thientu = InventoryServiceNew.gI().findItemBag(player, 2155);
                                } catch (Exception e) {
//                                        throw new RuntimeException(e);
                                }
                                if (thientu == null || thientu.quantity < 9999) {
                                    this.npcChat(player, "Bạn không đủ 9999 Lõi thiên tử");
                                } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
                                    this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
                                } else {

                                    InventoryServiceNew.gI().subQuantityItemsBag(player, thientu, 9999);

                                    Item aott = ItemService.gI().createNewItem((short) 2162);
                                    aott.itemOptions.add(new Item.ItemOption(50, 300));
                                    aott.itemOptions.add(new Item.ItemOption(77, 350));
                                    aott.itemOptions.add(new Item.ItemOption(103, 350));
                                    aott.itemOptions.add(new Item.ItemOption(5, 130));
                                    aott.itemOptions.add(new Item.ItemOption(14, 60));
                                    aott.itemOptions.add(new Item.ItemOption(30, 1));
                                    aott.itemOptions.add(new Item.ItemOption(36, 1));
                                    InventoryServiceNew.gI().addItemBag(player, aott);
                                    InventoryServiceNew.gI().sendItemBags(player);
                                    this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
                                }
                                break;
                            }

                    }

                    } else if (player.iDMark.getIndexMenu() == 3) {
                        int randomSD = Util.nextInt(50, 150);
                        int randomSDCao = Util.nextInt(150, 299);
                        int randomHP = Util.nextInt(150, 200);
                        switch (select) {
                            case 0: {
                                if (!player.getSession().actived) {
                                    Service.gI().sendThongBao(player, "Chưa mở thành viên bố ơi!");
                                    return;
                                }
                                if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
                                    Service.gI().sendThongBao(player, "Hành trang của mày không đủ");
                                    return;
                                }
                                if (InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 1136) == null) {
                                    Service.gI().sendThongBao(player, "Bạn Không Có Cải Trang Khỉ Ultra");
                                    return;
                                }
                                if (InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2182) == null) {
                                    Service.gI().sendThongBao(player, "Bạn Không Có Vé Nâng Cấp Goku Thiên Tử");
                                    return;
                                }

                                Item caiTrangKhi = InventoryServiceNew.gI().findItemBag(player, 1136);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, caiTrangKhi, 1);

                                Item veNangCap = InventoryServiceNew.gI().findItemBag(player, 2182);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, veNangCap, 1);

                                Item item = ItemService.gI().createNewItem((short) (2161));

                                if (Util.isTrue(10, 100)) {
                                    item.itemOptions.add(new Item.ItemOption(50, randomSDCao));
                                } else {
                                    item.itemOptions.add(new Item.ItemOption(50, randomSD));
                                }

                                item.itemOptions.add(new Item.ItemOption(77, randomHP));
                                item.itemOptions.add(new Item.ItemOption(103, randomHP));
                                item.itemOptions.add(new Item.ItemOption(14, 20));
                                item.itemOptions.add(new Item.ItemOption(5, 50));
                                item.itemOptions.add(new Item.ItemOption(106, 1));
                                item.itemOptions.add(new Item.ItemOption(34, 1));
                                item.itemOptions.add(new Item.ItemOption(30, 1));
                                InventoryServiceNew.gI().addItemBag(player, item);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Đổi thành công cải trang :Goku Thiên Tử  , Chúc mừng nổ lực của mày");
                                break;
                            }
                        }
                    }
                }
            }
        };
    }

    // public static Npc HungVuong(int mapId, int status, int cx, int cy, int tempId, int avartar) {
    //     return new Npc(mapId, status, cx, cy, tempId, avartar) {
    //         @Override
    //         public void openBaseMenu(Player player) {
    //             if (canOpenNpc(player)) {

    //                 if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
    //                     this.createOtherMenu(player, ConstNpc.BASE_MENU,
    //                             "Chúc mừng năm mới  Chúc ngươi gặp được nhiều may mắn và thành công, Đặc biệt là những người ở xa quê hương\nChúc ngươi có 1 cái tết ở nơi xứ người vui vẻ\nTa sẽ tặng ngươi một số món quà tri ân dip Tết Nguyên Đán, ngươi có thể tặng ta lại bánh chưng và bánh tét nhé!\nỞ chỗ ta có bán lá chuối để làm bánh chưng,bánh tét.(Thường :1k , Cao Cấp :5k)", "Nhận Lì Xì", "Đổi Bánh\nlấy quà", "Mua lá chuối", "Mua lá chuối\nCao Cấp", "Đổi Bao\nLì Xì", "Xui Bỏ mẹ");
    //                 }

    //             }
    //         }

    //         @Override
    //         public void confirmMenu(Player player, int select) {
    //             if (canOpenNpc(player)) {
    //                 if (player.iDMark.isBaseMenu()) {
    //                     switch (select) {
    //                         case 0:
    //                             if (player.nhanqua == 1) {
    //                                 Service.gI().sendThongBao(player, "Năm Mới Đừng Tham ăn con ơi");
    //                                 return;
    //                             }
    //                             if (InventoryServiceNew.gI().getCountEmptyBag(player) <= 0) {
    //                                 Service.gI().sendThongBao(player, "Hành trang của ngươi không đủ");
    //                                 return;
    //                             }
    //                             player.nhanqua = 1;

    //                             Item item = ItemService.gI().createNewItem((short) 2128, 1);
    //                             Item item2 = ItemService.gI().createNewItem((short) 2129, 1);
    //                             Item item3 = ItemService.gI().createNewItem((short) 2130, 1);

    //                             InventoryServiceNew.gI().addItemBag(player, item);
    //                             InventoryServiceNew.gI().addItemBag(player, item2);
    //                             InventoryServiceNew.gI().addItemBag(player, item3);
    //                             InventoryServiceNew.gI().sendItemBags(player);
    //                             Service.gI().sendThongBao(player, "Nhận Quà thành Công, Chúc Con Năm Mới Vui Vẻ, Ta Iu Con Rất Nhiều");
    //                             break;
    //                         case 1://Shop
    //                             this.createOtherMenu(player, 1500,
    //                                     "Ngươi Muốn Đổi Bánh Lấy Quà À!", "Đổi", "Đếu");
    //                             break;

    //                         case 2:
    //                             if (player.getSession().actived) {
    //                                 if (player.tongnap >= 1000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
    //                                     Item item1 = ItemService.gI().createNewItem((short) (2167), 1);
    //                                     InventoryServiceNew.gI().addItemBag(player, item1);
    //                                     Service.gI().sendThongBao(player, "Bạn đã nhận được " + item1.template.name);
    //                                     InventoryServiceNew.gI().sendItemBags(player);
    //                                     PlayerDAO.subtn(player, 1000);
    //                                 } else {
    //                                     Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 1k hoặc hành trang không có chỗ trống");
    //                                 }
    //                             } else {
    //                                 Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
    //                             }
    //                             break;
    //                         case 3:
    //                             if (player.getSession().actived) {
    //                                 if (player.tongnap >= 5000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
    //                                     Item item4 = ItemService.gI().createNewItem((short) (2168));
    //                                     InventoryServiceNew.gI().addItemBag(player, item4);
    //                                     Service.gI().sendThongBao(player, "Bạn đã nhận được " + item4.template.name);
    //                                     InventoryServiceNew.gI().sendItemBags(player);
    //                                     PlayerDAO.subtn(player, 5000);
    //                                 } else {
    //                                     Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 5k hoặc hành trang không có chỗ trống");
    //                                 }
    //                             } else {
    //                                 Service.gI().sendThongBao(player, "Bạn Chưa Mở thành viên");

    //                             }
    //                             break;
    //                         case 4:
    //                             this.createOtherMenu(player, 9990,
    //                                     "Đổi 1 Bao Lì Xì Sẽ Được 1 Điểm. \nĐiểm này sẽ tính vào Đua top trong suốt quá trình diễn ra sự kiện Nhé!\n Nếu nguoi không muốn đổi thì có thể sử dụng để lấy phần thưởng hấp dẫn\nĐiểm Top Lì Xì Hiện Tại Của Ngươi là:" + player.lambanh, "Đổi", "Không");
    //                             break;
    //                         case 5:
    //                             this.createOtherMenu(player, 9999,
    //                                     "Cờ Bạc Là Bác Thằng Bần! Đừng Đánh Bạc mà hãy chơi NROMABU nhé con", "Đếu");
    //                             break;
    //                     }
    //                 } else if (player.iDMark.getIndexMenu() == 9990) {
    //                     if (player.getSession().actived) {
    //                         Item lixi = InventoryServiceNew.gI().findItemBag(player, 2135);
    //                         if (lixi == null || lixi.quantity < 99) {
    //                             this.npcChat(player, "Bạn không đủ bao lì xì");
    //                             return;
    //                         } else {
    //                             InventoryServiceNew.gI().subQuantityItemsBag(player, lixi, 99);
    //                             player.lambanh += 99;
    //                             this.npcChat(player, "Đổi Thành Công 99 Bao Lì Xì");
    //                         }
    //                     } else {
    //                         Service.gI().sendThongBao(player, "Bạn Chưa Mở thành viên");
    //                     }
    //                 } else if (player.iDMark.getIndexMenu() == 1500) {
    //                     this.createOtherMenu(player, 1501,
    //                             "Ngươi Muốn Đổi Bánh Lấy Quà À!", "Đổi bánh chưng", "Đổi bánh tét", "Đếu");

    //                 } else if (player.iDMark.getIndexMenu() == 1501) {

    //                     switch (select) {
    //                         case 0:
    //                             if (player.getSession().actived) {
    //                                 Item banhchung = null;
    //                                 try {
    //                                     banhchung = InventoryServiceNew.gI().findItemBag(player, 753);
    //                                 } catch (Exception e) {
    //                                     //                                        throw new RuntimeException(e);
    //                                 }
    //                                 if (banhchung == null || banhchung.quantity < 1) {
    //                                     this.npcChat(player, "Bạn không đủ bánh chưng");
    //                                     return;
    //                                 } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
    //                                     this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
    //                                     return;
    //                                 } else {
    //                                     player.topdiem += 2;
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, banhchung, 1);

    //                                     int[] itemDos = new int[]{1309, 2155, 2128, 2129, 2130, 2135, 2102, 2103, 2104, 2146, 2161, 2162, 1054, 1055, 1056};
    //                                     int radomItem = Util.nextInt(0, 14);
    //                                     if (itemDos[radomItem] == 2161 || itemDos[radomItem] == 2162 || itemDos[radomItem] == 2146) {
    //                                         int randomSD = Util.nextInt(0, 250);
    //                                         int randomHP = Util.nextInt(0, 350);
    //                                         int randomCM = Util.nextInt(0, 100);

    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem]);
    //                                         aott.itemOptions.add(new Item.ItemOption(50, randomSD));
    //                                         aott.itemOptions.add(new Item.ItemOption(77, randomHP));
    //                                         aott.itemOptions.add(new Item.ItemOption(103, randomHP));
    //                                         aott.itemOptions.add(new Item.ItemOption(5, randomCM));
    //                                         aott.itemOptions.add(new Item.ItemOption(14, 20));
    //                                         aott.itemOptions.add(new Item.ItemOption(93, 5));
    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     } else if (itemDos[radomItem] == 1054 || itemDos[radomItem] == 1055 || itemDos[radomItem] == 1056) {
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem]);

    //                                         int randomTC = Util.nextInt(0, 50);
    //                                         int randomSM = Util.nextInt(50, 200);
    //                                         aott.itemOptions.add(new Item.ItemOption(21, randomSM));
    //                                         aott.itemOptions.add(new Item.ItemOption(220, randomTC));

    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     } else {
    //                                         int randomQuantity = Util.nextInt(1, 7);
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem], randomQuantity);
    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     }
    //                                 }
    //                             } else {
    //                                 this.npcChat(player, "hãy ủng hộ ta để có nhiều sự kiện chơi, mở thành viên trước nhé");
    //                             }
    //                             break;
    //                         case 1:
    //                             if (player.getSession().actived) {
    //                                 Item banhtet = null;
    //                                 try {
    //                                     banhtet = InventoryServiceNew.gI().findItemBag(player, 752);
    //                                 } catch (Exception e) {
    //                                     //                                        throw new RuntimeException(e);
    //                                 }
    //                                 if (banhtet == null || banhtet.quantity < 1) {
    //                                     this.npcChat(player, "Bạn không đủ bánh tét");
    //                                 } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
    //                                     this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
    //                                 } else {
    //                                     player.topdiem += 10;
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, banhtet, 1);
    //                                     int randomSD = Util.nextInt(250, 450);
    //                                     int randomHP = Util.nextInt(250, 450);
    //                                     int randomCM = Util.nextInt(50, 250);
    //                                     int[] itemDos = new int[]{1309, 2155, 2128, 2129, 2130, 2135, 2102, 2103, 2104, 2146, 2161, 2162, 1054, 1055, 1056, 2164, 2166, 987, 2158};
    //                                     int radomItem = Util.nextInt(0, 17);
    //                                     if (itemDos[radomItem] == 2161 || itemDos[radomItem] == 2162 || itemDos[radomItem] == 2146) {
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem]);
    //                                         aott.itemOptions.add(new Item.ItemOption(50, randomSD));
    //                                         aott.itemOptions.add(new Item.ItemOption(77, randomHP));
    //                                         aott.itemOptions.add(new Item.ItemOption(103, randomHP));
    //                                         aott.itemOptions.add(new Item.ItemOption(5, randomCM));
    //                                         aott.itemOptions.add(new Item.ItemOption(14, 20));
    //                                         if (Util.isTrue(90, 100)) {
    //                                             aott.itemOptions.add(new Item.ItemOption(93, 5));
    //                                         }
    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     } else if (itemDos[radomItem] == 1054 || itemDos[radomItem] == 1055 || itemDos[radomItem] == 1056 || itemDos[radomItem] == 2158) {
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem]);

    //                                         int randomTC = Util.nextInt(30, 200);
    //                                         int randomTC2 = Util.nextInt(300, 600);
    //                                         int randomSM = Util.nextInt(10, 100);
    //                                         aott.itemOptions.add(new Item.ItemOption(21, randomSM));
    //                                         if (itemDos[radomItem] == 2158) {
    //                                             aott.itemOptions.add(new Item.ItemOption(220, randomTC2));
    //                                         } else {
    //                                             aott.itemOptions.add(new Item.ItemOption(220, randomTC));
    //                                         }

    //                                         if (Util.isTrue(20, 100)) {

    //                                             switch (itemDos[radomItem]) {
    //                                                 case 1054:
    //                                                     int randomSKH = Util.nextInt(127, 129);
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH, 0));
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH + 12, 0));
    //                                                     break;
    //                                                 case 1055:
    //                                                     int randomSKH1 = Util.nextInt(130, 132);
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH1, 0));
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH1 + 12, 0));
    //                                                     break;
    //                                                 case 1056:
    //                                                     int randomSKH2 = Util.nextInt(133, 135);
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH2, 0));
    //                                                     aott.itemOptions.add(new Item.ItemOption(randomSKH2 + 3, 0));
    //                                                     break;
    //                                             }
    //                                         }

    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     } else if (itemDos[radomItem] == 2164 || itemDos[radomItem] == 2166 || itemDos[radomItem] == 987) {
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem], 1);
    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     } else {
    //                                         int ranDom = Util.nextInt(7, 15);
    //                                         Item aott = ItemService.gI().createNewItem((short) itemDos[radomItem], ranDom);
    //                                         InventoryServiceNew.gI().addItemBag(player, aott);
    //                                         InventoryServiceNew.gI().sendItemBags(player);
    //                                         this.npcChat(player, "Bạn nhận được " + aott.template.name);
    //                                     }
    //                                 }
    //                             } else {
    //                                 this.npcChat(player, "hãy ủng hộ admin, mở thành viên trước nhé");
    //                             }
    //                             break;
    //                     }

    //                 }
    //             }
    //         }
    //     };
    // }

    // public static Npc NoiBanh(int mapId, int status, int cx, int cy, int tempId, int avartar) {
    //     return new Npc(mapId, status, cx, cy, tempId, avartar) {
    //         @Override
    //         public void openBaseMenu(Player player) {
    //             if (canOpenNpc(player)) {
    //                 if (!TaskService.gI().checkDoneTaskTalkNpc(player, this)) {
    //                     this.createOtherMenu(player, ConstNpc.BASE_MENU,
    //                             "Bánh Chưng Và Bánh Tét là những đặc sản vào dịp Tết của người dân Việt Nam ta\nHãy cùng nhau làm một chiếc bánh để cùng nhau ăn bên gia đình nhé!\nChúc Ngươi Năm Mới Vui Vẻ, An khang Thịnh Vượng\nNấu bánh Chưng cần 50 Bột Mì,50 Đậu Xanh, 10 Thịt Heo và 1 Lá Chuối thường\n Nấu Bánh Tét Cần 70 Bột Mì,70 Đậu Xanh,10 Thịt Heo và lá Chuối Cao Cấp\n Lá Chuối mua ở NPC Hùng Vương Nhé!", "Nấu Bánh Chưng", "Nấu Bánh Tét", "Xin Bánh");
    //                 }

    //             }
    //         }

    //         @Override
    //         public void confirmMenu(Player player, int select) {
    //             if (canOpenNpc(player)) {
    //                 if (player.iDMark.isBaseMenu()) {
    //                     switch (select) {
    //                         case 0:
    //                             if (player.getSession().actived) {
    //                                 Item botmi = null;
    //                                 Item dauxanh = null;
    //                                 Item thiheo = null;
    //                                 Item chuoithuong = null;
    //                                 try {
    //                                     botmi = InventoryServiceNew.gI().findItemBag(player, 888);
    //                                     dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
    //                                     thiheo = InventoryServiceNew.gI().findItemBag(player, 748);
    //                                     chuoithuong = InventoryServiceNew.gI().findItemBag(player, 2167);
    //                                 } catch (Exception e) {
    //                                     //                                        throw new RuntimeException(e);
    //                                 }
    //                                 if (botmi == null || botmi.quantity < 50 || dauxanh == null || dauxanh.quantity < 50 || thiheo == null || thiheo.quantity < 10 || chuoithuong == null || chuoithuong.quantity < 1) {
    //                                     this.npcChat(player, "Bạn không đủ vật phẩm");
    //                                 } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
    //                                     this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
    //                                 } else {

    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 50);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 50);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, thiheo, 10);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, chuoithuong, 1);

    //                                     Item aott = ItemService.gI().createNewItem((short) 753);
    //                                     InventoryServiceNew.gI().addItemBag(player, aott);
    //                                     InventoryServiceNew.gI().sendItemBags(player);
    //                                     this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
    //                                 }
    //                             } else {
    //                                 this.npcChat(player, "hãy ủng hộ admin, mở thành viên trước nhé");
    //                             }
    //                             break;
    //                         case 1://Shop
    //                             if (player.getSession().actived) {
    //                                 Item botmi = null;
    //                                 Item dauxanh = null;
    //                                 Item thiheo = null;
    //                                 Item chuoicaocap = null;
    //                                 try {
    //                                     botmi = InventoryServiceNew.gI().findItemBag(player, 888);
    //                                     dauxanh = InventoryServiceNew.gI().findItemBag(player, 889);
    //                                     thiheo = InventoryServiceNew.gI().findItemBag(player, 748);
    //                                     chuoicaocap = InventoryServiceNew.gI().findItemBag(player, 2168);
    //                                 } catch (Exception e) {
    //                                     //                                        throw new RuntimeException(e);
    //                                 }
    //                                 if (botmi == null || botmi.quantity < 70 || dauxanh == null || dauxanh.quantity < 70 || thiheo == null || thiheo.quantity < 10 || chuoicaocap == null || chuoicaocap.quantity < 1) {
    //                                     this.npcChat(player, "Bạn không đủ vật phẩm");
    //                                 } else if (InventoryServiceNew.gI().getCountEmptyBag(player) == 0) {
    //                                     this.npcChat(player, "Hành trang của bạn không đủ chỗ trống");
    //                                 } else {

    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, botmi, 70);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, dauxanh, 70);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, thiheo, 10);
    //                                     InventoryServiceNew.gI().subQuantityItemsBag(player, chuoicaocap, 1);

    //                                     Item aott = ItemService.gI().createNewItem((short) 752);
    //                                     InventoryServiceNew.gI().addItemBag(player, aott);
    //                                     InventoryServiceNew.gI().sendItemBags(player);
    //                                     this.npcChat(player, "Bạn nhận được 1" + aott.template.name);
    //                                 }
    //                             } else {
    //                                 this.npcChat(player, "hãy ủng hộ admin, mở thành viên trước nhé");
    //                             }
    //                             break;

    //                         case 2:
    //                             this.createOtherMenu(player, 1503,
    //                                     "Năm Mới Đừng Lười Nhé, Ráng Cày Đi", "Dạ Em Biết Rồi");
    //                             break;

    //                     }
    //                 }
    //             }
    //         }
    //     };
    // }

    public static Npc bolao(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|1|Nạp rồi bú quà mốc nạp thôi"
                            + "\n|7|ở chỗ ta để nhận quà mốc nạp và mốc nạp đơn (reset theo tuần)",
                            "Nhận quà\n Mốc Nạp", "Nhận quà mốc\n nạp Đơn", "Không");
                }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                // case 0:
                                //     this.createOtherMenu(player, ConstNpc.MENUCC, "\n"
                                //             + "|7|Ngươi có thể dùng điểm đổi ở Santa để mua hộp quà"
                                //             + "\n|7|Hộp quà 10k chứa:"
                                //             + "\n|1|Noel ball, bông tai cấp 5, bông tai cấp 6, sao pha lê vip..."
                                //             + "\n|2|Thú cưỡi vip random chỉ số(1-30%), giáp tl noel, hồn đệ vip... "
                                //             + "\n|3|Đồ thiên sứ SKH random từ sơ cấp đến cao cấp"
                                //             + "\n|7|Thiệp mừng xuân VIP 30k chứa:"
                                //             + "\n|1|Coin trong game ,Thú cưỡi vip chỉ số(1-30%) ,NOEL BALL, Ngọc Sức Đánh"
                                //             + "\n|2|Gang thiên sứ SKH (có thể không trùng hành tinh)"
                                //             + "\n|3|Hộp quà 100K điểm đổi"
                                //             + "\n|4|Có tỉ lệ nhận được cải trang Vệ Thần Mùa Xuân (Mới)",
                                //             "Mua quà 10k", "Mua Thiệp\n VIP 30k", "Không thèm");
                                //     break;

                                // case 1:
                                //     if (player.getSession().actived) {
                                //         if (player.tongnap >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                //             Item item = ItemService.gI().createNewItem((short) (2133));
                                //             InventoryServiceNew.gI().addItemBag(player, item);
                                //             Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                //             InventoryServiceNew.gI().sendItemBags(player);
                                //             PlayerDAO.subtn(player, 10000);
                                //         } else {
                                //             Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 10k hoặc hành trang không có chỗ trống");
                                //         }
                                //     } else {
                                //         Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                //     }
                                //     break;
                                // case 2:
                                //     if (player.getSession().actived) {
                                //         if (player.tongnap >= 30000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                //             Item item = ItemService.gI().createNewItem((short) (2136));
                                //             InventoryServiceNew.gI().addItemBag(player, item);
                                //             Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                //             InventoryServiceNew.gI().sendItemBags(player);
                                //             PlayerDAO.subtn(player, 30000);
                                //         } else {
                                //             Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 30k hoặc hành trang không có chỗ trống");
                                //         }
                                //     } else {
                                //         Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                //     }
                                //     break;
                                // case 3:
                                //     this.createOtherMenu(player, 1, "\n"
                                //             + "|7|Ngươi có thể mua đồ Thiên Sứ không yêu cầu sức mạnh tại đây"
                                //             + "\n|1|Áo sẽ là 30.000 Điểm đổi/món "
                                //             + "\n|1|Quần sẽ là 50.000 Điểm đổi/món"
                                //             + "\n|1|Găng sẽ là 70.000 Điểm đổi/món"
                                //             + "\n|1|Giày sẽ là 40.000 Điểm đổi/món"
                                //             + "\n|1|Nhẫn sẽ là 60.000 Điểm đổi/món"
                                //             + "\n|3|Hãy cho ta biết ngươi thuộc hành tinh nào ?",
                                //             "Trái đất", "Namec", "Xayda", "Không mua");
                                //     break;
                                case 0:
                                    this.createOtherMenu(player, 2006, "|1|Tổng nạp của ngươi đang là:" + player.tongnap2 + "Quà mốc nạp là quà mà ngươi nhận được khi nạp tiền"
                                            + "\n|3|500k : Nhận được Hào Quang Cấp Độ 1 Chỉ Số VIP, 200 triệu COIN, 300K Điểm Đổi, cải trang GOKU SSJ GOD\n "
                                            + "\n|3|1000k: Nhận được Chân Mệnh Cấp Độ 1 Chỉ Số VIP, 500 triệu COIN, 500K Điểm Đổi, cải trang GOHAN Hợp Thể\n"
                                            + "\n|3|3000k: Danh hiệu Chỉ Số VIP, 1000k Điểm đổi,cải trang Hakai Toppo\n"
                                            + "\n|3|5000k: Vật Phẩm Đeo Lưng Chỉ Số VIP, 3000k Điểm Đổi, cải trang Jiren Full power \n"
                                            + "\n|3|10000k: Pet mới Chỉ Số Siêu VIP, 5000k Điểm Đổi ,Goku Long Nhân ,500 Ngọc Rồng Sức Đánh, Full set Thiên Tử Kích Hoạt Chỉ Số VIP",
                                            "Nhận Quà");
                                    break;
                                case 1:
                                    createOtherMenu(player, 1111, "|1|Ngươi Muốn nhận quà mốc nạp đơn à?"
                                            + " Quà mốc nạp đơn sẽ reset theo tuần nhé. Tổng nạp tuần này của ngươi là :" + player.napdon + "\n"
                                            + "|4|\n100k : 50Tr COIN, 10 Ngọc Rồng Sức Đánh, 20 Đá Bảo Vệ, Bông Tai C6\n"
                                            + "|4|\n200k : 100Tr COIN, 20 Ngọc Rồng Sức Đánh, 40 Đá Bảo Vệ, 2000 Bùa Chân Mệnh\n"
                                            + "|4|\n400k : 250Tr COIN, 50  Ngọc Rồng Sức Đánh ,80 Đá Bảo Vệ, 2000 Bùa Hào Quang, 1000 Bùa Chân Mệnh\n"
                                            + "|4|\n1000k : 500Tr COIN, 100 Ngọc Rồng Sức Đánh,200 đá bảo vệ, 5000 Bùa Hào quang, 5000 Bùa Chân Mệnh\n"
                                            + "|4|\n2000k : 220  Ngọc Rồng Sức Đánh, 400 Đá Bảo vệ, 10 ngọc vip 10% SD hoặc HPKI nếu muốn đổi Ib Admin",
                                            "Nhận quà");

                                    break;
                                // case 5:
                                //     this.createOtherMenu(player, 2007, "\n"
                                //             + "|7|Để mua Thiệp Đổi Hào Quang , Ngươi phải có 20k Điểm Đổi",
                                //             "Mua Thiệp");
                                //     break;
                                // case 6:
                                //     this.createOtherMenu(player, 2006, ""
                                //             + "|7|Tổng nạp của ngươi đang là:" + player.tongnap2
                                //             + "|7|Quà mốc nạp VIP là quà mà ngươi nạp trên 10 Củ Khoai"
                                //             + "\n|1|15000k: Nhận được Hào Quang Chỉ số VIP ,10 Tỷ COIN ,5000k Điểm Đổi ,Full Set Thiên Tử Chỉ Số Cực Cao ,Vật Phẩm Đeo Lưng VIP Chỉ Số Cao, PET VIP Chỉ Số Cao ,400 Viên Noel Ball 1s, 100 Viên SPL Siêu Phẩm (10% SD), 100 Ngọc Tăng Cường Thể Chất (X2 Chỉ số Trong 10p)"
                                //             + "\n|1|20000k: Nhận được Hào Quang Chỉ số VIP2 ,10 Tỷ COIN ,5000k Điểm Đổi, Cải Trang Jiren Hiền Hòa ,Vật Phẩm Đeo Lưng VIP2 Chỉ Số Cao, PET VIP2 Chỉ Số Cao ,600 Viên Noel Ball 1s, Full Set thiên tử SKH và Găng 500k TC, x2 nạp Vĩnh Viễn , 100 Viên SPL Siêu Phẩm VIP (15% SD và 10% SD Chí mạng), 100 Ngọc Tăng Cường Thể Chất VIP (X4 Chỉ số Trong 10p)",
                                //             "Nhận Quà");
                                //     break;
                            }
                        } else if (player.iDMark.getIndexMenu() == ConstNpc.MENUCC) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 10000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2133));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 10000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 10k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 1:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 30000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2136));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 30000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 30k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;

                            }

                        } else if (player.iDMark.getIndexMenu() == 1) {
                            switch (select) {
                                case 0:
                                    this.createOtherMenu(player, 2, "\n"
                                            + "|7|Ngươi muốn mua loại đồ nào? , Tên Trái Đất Kia",
                                            "Áo", "Quần", "Găng", "Giày", "Nhẫn");
                                    break;
                                case 1:
                                    this.createOtherMenu(player, 3, "\n"
                                            + "|7|Ngươi muốn mua loại đồ nào? , Tên Namek Kia",
                                            "Áo", "Quần", "Găng", "Giày", "Nhẫn");
                                    break;
                                case 2:
                                    this.createOtherMenu(player, 4, "\n"
                                            + "|7|Ngươi muốn mua loại đồ nào? , Tên Xayda kia",
                                            "Áo", "Quần", "Găng", "Giày", "Nhẫn");
                                    break;

                            }

                        } else if (player.iDMark.getIndexMenu() == 2006) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().actived && InventoryServiceNew.gI().getCountEmptyBag(player) < 3) {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên hoặc hành trang không đủ chỗ trống");
                                        return;
                                    }
                                    if (player.tongnap2 >= 500000) {
                                        if (player.tongnap2 >= 500000 && player.mocnap < 1) {
                                            Item item = ItemService.gI().createNewItem((short) (1294));
                                            Item item2 = ItemService.gI().createNewItem((short) (2153));

                                            item.itemOptions.add(new Item.ItemOption(50, 55));
                                            item.itemOptions.add(new Item.ItemOption(77, 60));
                                            item.itemOptions.add(new Item.ItemOption(77, 60));

                                            item2.itemOptions.add(new Item.ItemOption(50, 250));
                                            item2.itemOptions.add(new Item.ItemOption(77, 300));
                                            item2.itemOptions.add(new Item.ItemOption(77, 300));
                                            item2.itemOptions.add(new Item.ItemOption(5, 130));
                                            item2.itemOptions.add(new Item.ItemOption(14, 60));
                                            item2.itemOptions.add(new Item.ItemOption(30, 0));

                                            PlayerDAO.addvnd(player, 200000000);
                                            PlayerDAO.addtn(player, 300000);
                                            player.mocnap = 1;
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            InventoryServiceNew.gI().addItemBag(player, item2);

                                            Service.gI().sendThongBao(player, "Nhận được Hào Quang Cấp 1 Chỉ Số VIP ,200 triệu COIN  , 300K Điểm Đổi ,  cải trang GOKU SSJ GOD");
                                            InventoryServiceNew.gI().sendItemBags(player);

                                        } else if (player.tongnap2 >= 1000000 && player.mocnap < 2) {
                                            Item item = ItemService.gI().createNewItem((short) (1285));
                                            Item item2 = ItemService.gI().createNewItem((short) (2150));

                                            item.itemOptions.add(new Item.ItemOption(50, 55));
                                            item.itemOptions.add(new Item.ItemOption(77, 60));
                                            item.itemOptions.add(new Item.ItemOption(77, 60));

                                            item2.itemOptions.add(new Item.ItemOption(50, 270));
                                            item2.itemOptions.add(new Item.ItemOption(77, 300));
                                            item2.itemOptions.add(new Item.ItemOption(77, 300));
                                            item2.itemOptions.add(new Item.ItemOption(5, 150));
                                            item2.itemOptions.add(new Item.ItemOption(14, 60));
                                            item2.itemOptions.add(new Item.ItemOption(30, 0));

                                            PlayerDAO.addvnd(player, 500000000);
                                            PlayerDAO.addtn(player, 500000);
                                            player.mocnap = 2;
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            InventoryServiceNew.gI().addItemBag(player, item2);

                                            Service.gI().sendThongBao(player, "Nhận được quà 1 triệu");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                        } else if (player.tongnap2 >= 3000000 && player.mocnap < 3) {
                                            Item item = ItemService.gI().createNewItem((short) (2226));
                                            Item item2 = ItemService.gI().createNewItem((short) (2172));

                                            item.itemOptions.add(new Item.ItemOption(50, 70));
                                            item.itemOptions.add(new Item.ItemOption(77, 80));
                                            item.itemOptions.add(new Item.ItemOption(77, 80));

                                            item2.itemOptions.add(new Item.ItemOption(50, 300));
                                            item2.itemOptions.add(new Item.ItemOption(77, 350));
                                            item2.itemOptions.add(new Item.ItemOption(77, 350));
                                            item2.itemOptions.add(new Item.ItemOption(5, 170));
                                            item2.itemOptions.add(new Item.ItemOption(14, 60));
                                            item2.itemOptions.add(new Item.ItemOption(30, 0));

                                            PlayerDAO.addtn(player, 1000000);
                                            player.mocnap = 3;
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            InventoryServiceNew.gI().addItemBag(player, item2);
                                            Service.gI().sendThongBao(player, "Nhận được quà 3tr");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                        } else if (player.tongnap2 >= 5000000 && player.mocnap < 4) {
                                            Item item = ItemService.gI().createNewItem((short) (2219));
                                            Item item2 = ItemService.gI().createNewItem((short) (2173));

                                            item.itemOptions.add(new Item.ItemOption(50, 100));
                                            item.itemOptions.add(new Item.ItemOption(77, 130));
                                            item.itemOptions.add(new Item.ItemOption(77, 130));

                                            item2.itemOptions.add(new Item.ItemOption(50, 350));
                                            item2.itemOptions.add(new Item.ItemOption(77, 450));
                                            item2.itemOptions.add(new Item.ItemOption(77, 450));
                                            item2.itemOptions.add(new Item.ItemOption(5, 190));
                                            item2.itemOptions.add(new Item.ItemOption(14, 60));
                                            item2.itemOptions.add(new Item.ItemOption(30, 0));

                                            
                                            PlayerDAO.addtn(player, 3000000);
                                            player.mocnap = 4;
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            InventoryServiceNew.gI().addItemBag(player, item2);
                                            Service.gI().sendThongBao(player, " Nhận Được Quà 5 triệu");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                        } else if (player.tongnap2 >= 10000000 && player.mocnap < 5) {
                                            player.mocnap = 5;
                                            Item item = ItemService.gI().createNewItem((short) (2229)); // hao quang
                                            Item item2 = ItemService.gI().createNewItem((short) (2185)); // cai trang
                                            Item item5 = ItemService.gI().createNewItem((short) (2128) , 500);// ball vip

                                            item.itemOptions.add(new Item.ItemOption(50, 150));
                                            item.itemOptions.add(new Item.ItemOption(77, 170));
                                            item.itemOptions.add(new Item.ItemOption(77, 170));
                                            item.itemOptions.add(new Item.ItemOption(5, 200));
                                            item.itemOptions.add(new Item.ItemOption(30, 0));

                                            item2.itemOptions.add(new Item.ItemOption(50, 400));
                                            item2.itemOptions.add(new Item.ItemOption(77, 450));
                                            item2.itemOptions.add(new Item.ItemOption(103, 450));
                                            item2.itemOptions.add(new Item.ItemOption(5, 230));
                                            item2.itemOptions.add(new Item.ItemOption(14, 60));
                                            item2.itemOptions.add(new Item.ItemOption(30, 0));

                                           


                                            item5.itemOptions.add(new Item.ItemOption(30, 0));

                                            PlayerDAO.addtn(player, 5000000);

                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            InventoryServiceNew.gI().addItemBag(player, item2);
                                         
                                            
                                            InventoryServiceNew.gI().addItemBag(player, item5);

                                            Service.gI().sendThongBao(player, "Nhận Được quà 10 triệu");
                                            InventoryServiceNew.gI().sendItemBags(player);
                                        } else {
                                            Service.gI().sendThongBao(player, "Bú Ít Thôi Chú Bé Đần ");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa đủ khả năng nhận mốc nạp , hãy cố gắng nạp thêm ");
                                    }
                                    break;
                            }

                        }
                        if (player.iDMark.getIndexMenu() == 1111) {
                            switch (select) {
                                case 0:
                                    if (player.napdon >= 100000 && player.mocnapdon < 1) {
                                        player.mocnapdon = 1;
                                        PlayerDAO.addvnd(player, 50000000);
                                        Item item2 = ItemService.gI().createNewItem((short) (2128), 10);
                                        Item item5 = ItemService.gI().createNewItem((short) (987), 20);
                                        Item item7 = ItemService.gI().createNewItem((short) (2119), 1);
                                        InventoryServiceNew.gI().addItemBag(player, item2);
                                        InventoryServiceNew.gI().addItemBag(player, item5);
                                        InventoryServiceNew.gI().addItemBag(player, item7);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 100k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 200000 && player.mocnapdon < 2) {
                                        player.mocnapdon = 2;
                                        PlayerDAO.addvnd(player, 100000000);
                                        Item ittem2 = ItemService.gI().createNewItem((short) (2128), 20);
                                        Item ittem3 = ItemService.gI().createNewItem((short) (1309), 2000);
                                        Item ittem5 = ItemService.gI().createNewItem((short) (987), 40);
                                       
                                        InventoryServiceNew.gI().addItemBag(player, ittem2);
                                        InventoryServiceNew.gI().addItemBag(player, ittem3);
                                        InventoryServiceNew.gI().addItemBag(player, ittem5);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 200k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 400000 && player.mocnapdon < 3) {
                                        player.mocnapdon = 3;
                                        PlayerDAO.addvnd(player, 250000000);
                                        Item itttem2 = ItemService.gI().createNewItem((short) (2128), 50);
                                        Item itttem3 = ItemService.gI().createNewItem((short) (2190), 2000);
                                        Item itttem5 = ItemService.gI().createNewItem((short) (987), 80);
                                        Item itttem6 = ItemService.gI().createNewItem((short) (1309), 1000);

                                        InventoryServiceNew.gI().addItemBag(player, itttem2);
                                        InventoryServiceNew.gI().addItemBag(player, itttem3);
                                        InventoryServiceNew.gI().addItemBag(player, itttem5);
                                        InventoryServiceNew.gI().addItemBag(player, itttem6);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 400k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 1000000 && player.mocnapdon < 4) {
                                        player.mocnapdon = 4;

                                        PlayerDAO.addvnd(player, 500000000);
                                        Item iitem2 = ItemService.gI().createNewItem((short) (2128), 100);
                                        Item iitem4 = ItemService.gI().createNewItem((short) (2190), 5000);
                                        Item iitem5 = ItemService.gI().createNewItem((short) (987), 200);
                                        Item iitem6 = ItemService.gI().createNewItem((short) (1309), 5000);

                                        

                                        InventoryServiceNew.gI().addItemBag(player, iitem2);
                                        InventoryServiceNew.gI().addItemBag(player, iitem4);
                                        InventoryServiceNew.gI().addItemBag(player, iitem5);
                                        InventoryServiceNew.gI().addItemBag(player, iitem6);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 1000k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 2000000 && player.mocnapdon < 5) {
                                        player.mocnapdon = 5;

                                        Item iiitem2 = ItemService.gI().createNewItem((short) (2128), 220);
                                        Item iiitem5 = ItemService.gI().createNewItem((short) (987), 400);
                                        Item iiitem6 = ItemService.gI().createNewItem((short) (2164), 10);

                                        InventoryServiceNew.gI().addItemBag(player, iiitem2);
                                        InventoryServiceNew.gI().addItemBag(player, iiitem5);
                                        InventoryServiceNew.gI().addItemBag(player, iiitem6);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 2000k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 4000000 && player.mocnapdon < 6) {
                                        player.mocnapdon = 6;
                                        Item iteem2 = ItemService.gI().createNewItem((short) (2128), 500);
                                        Item iteem3 = ItemService.gI().createNewItem((short) (2170), 20);
                                        Item iteem4 = ItemService.gI().createNewItem((short) (2165), 100);
                                        Item iteem5 = ItemService.gI().createNewItem((short) (987), 200);
                                        Item iteem6 = ItemService.gI().createNewItem((short) (2164), 60);

                                        InventoryServiceNew.gI().addItemBag(player, iteem2);
                                        InventoryServiceNew.gI().addItemBag(player, iteem3);
                                        InventoryServiceNew.gI().addItemBag(player, iteem4);
                                        InventoryServiceNew.gI().addItemBag(player, iteem5);
                                        InventoryServiceNew.gI().addItemBag(player, iteem6);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 4000k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else if (player.napdon >= 10000000 && player.mocnapdon < 7) {
                                        player.mocnapdon = 7;
                                        Item itteem2 = ItemService.gI().createNewItem((short) (2128), 1000);
                                        Item itteem3 = ItemService.gI().createNewItem((short) (2170), 50);
                                        Item itteem4 = ItemService.gI().createNewItem((short) (2165), 200);
                                        Item itteem5 = ItemService.gI().createNewItem((short) (987), 500);
                                        Item itteem6 = ItemService.gI().createNewItem((short) (2164), 130);

                                        InventoryServiceNew.gI().addItemBag(player, itteem2);
                                        InventoryServiceNew.gI().addItemBag(player, itteem3);
                                        InventoryServiceNew.gI().addItemBag(player, itteem4);
                                        InventoryServiceNew.gI().addItemBag(player, itteem5);
                                        InventoryServiceNew.gI().addItemBag(player, itteem6);

                                        Service.gI().sendThongBao(player, "Nhận được quà mốc 10000k");
                                        InventoryServiceNew.gI().sendItemBags(player);
                                    } else {
                                        Service.gI().sendThongBao(player, "Ko the nhan");
                                    }

                                    break;

                            }
                        } else if (player.iDMark.getIndexMenu() == 2007) {
                            switch (select) {
                                case 0:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 20000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (2148));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 20000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 20k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                            }
                        } else if (player.iDMark.getIndexMenu() == 2 || player.iDMark.getIndexMenu() == 3 || player.iDMark.getIndexMenu() == 4) {

                            int itemID = 0;
                            switch (player.iDMark.getIndexMenu()) {
                                case 2:
                                    itemID = 1048;
                                    break;
                                case 3:
                                    itemID = 1049;
                                    break;
                                case 4:
                                    itemID = 1050;
                                    break;
                            }

                            switch (select) {
                                case 0:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 30000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (itemID));
                                            item.itemOptions.add(new Item.ItemOption(47, 2500));
                                            item.itemOptions.add(new Item.ItemOption(30, 1));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 30000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 30k hoặc hành trang không có chỗ trống");
                                        }
                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 1:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 50000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (itemID + 3));
                                            item.itemOptions.add(new Item.ItemOption(22, 150));
                                            item.itemOptions.add(new Item.ItemOption(30, 1));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 50000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 50k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 2:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 70000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (itemID + 6));
                                            item.itemOptions.add(new Item.ItemOption(220, 25));
                                            item.itemOptions.add(new Item.ItemOption(30, 1));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 70000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 70k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 3:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 40000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (itemID + 9));
                                            item.itemOptions.add(new Item.ItemOption(23, 120));
                                            item.itemOptions.add(new Item.ItemOption(30, 1));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 40000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 40k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;
                                case 4:
                                    if (player.getSession().actived) {
                                        if (player.tongnap >= 60000 && InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                            Item item = ItemService.gI().createNewItem((short) (itemID + 12));
                                            item.itemOptions.add(new Item.ItemOption(14, 24));
                                            item.itemOptions.add(new Item.ItemOption(30, 1));
                                            InventoryServiceNew.gI().addItemBag(player, item);
                                            Service.gI().sendThongBao(player, "Bạn đã nhận được " + item.template.name);
                                            InventoryServiceNew.gI().sendItemBags(player);
                                            PlayerDAO.subtn(player, 60000);
                                        } else {
                                            Service.gI().sendThongBao(player, "Tổng nạp của bạn không đủ 60k hoặc hành trang không có chỗ trống");
                                        }

                                    } else {
                                        Service.gI().sendThongBao(player, "Bạn chưa mở thành viên");
                                    }
                                    break;

                            }

                        }
                    }
                }
            }
        };
    }

     public static Npc goKuLongNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
                if(this.mapId == 0){
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|5|Sự kiện 8/3 với muôn vàng món quà hấp hẫn đang chờ ngươi.\n Hãy nhanh chân theo ta đến đó",
                            "Di Chuyển", "Không");
                }}else{
                     if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|5|Tại Đây Có Rất nhiều sự kiện về ngày 8/3 đang diễn ra. Hãy Cùng ta khám phá nhé",
                            "Hướng Dẫn","Về Đảo Rùa", "Không");
                }}
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 178, -1, 432);
                                    break;
                                case 1:
                                    break;
                            }
                        } 
                       
                    }else{
                         if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    break;
                                case 1:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 432);
                                    break;
                                case 2:
                                    break;
                            }
                        } 
                    }
                }
            }
        };
    }
    public static Npc pclLongNhan(int mapId, int status, int cx, int cy, int tempId, int avartar) {
        return new Npc(mapId, status, cx, cy, tempId, avartar) {
            @Override
            public void openBaseMenu(Player player) {
               if(this.mapId == 0){
                 if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|5|Ngươi muốn đến chỗ của ta để thử thách bản thân sao?\n Đây nơi những Anh Tài hội tụ để vương lên vị trí cao nhất!!!",
                            "Di Chuyển", "Không");
                }
               }else{
                if (canOpenNpc(player)) {
                    createOtherMenu(player, ConstNpc.BASE_MENU,
                            "|5|Tại đây ngươi có thể solo với bất cứ đối thủ nào ngươi muốn!!!",
                            "Hướng dẫn","Xếp Hạng\nĐấu Trường ","Về Đảo Rùa", "Không");
                }
               }
            }

            @Override
            public void confirmMenu(Player player, int select) {
                if (canOpenNpc(player)) {
                    if (this.mapId == 0) {
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 180, -1, 432);
                                    break;
                                case 1:
                                    break;
                            }
                        } 
                       
                    }else{
                        if (player.iDMark.isBaseMenu()) {
                            switch (select) {
                                case 0:
                                    break;
                                case 1:
                                    break;
                                case 2:
                                    ChangeMapService.gI().changeMapBySpaceShip(player, 5, -1, 432);
                                    break;
                                case 3:
                                    break;
                            }
                        } 
                    }
                }
            }
        };
    }

//    Service.gI().showListTop(player, Manager.topNV);
    public static Npc createNPC(int mapId, int status, int cx, int cy, int tempId) {
        int avatar = Manager.NPC_TEMPLATES.get(tempId).avatar;
        try {
            switch (tempId) {
                case ConstNpc.UNKOWN:
                    return unkonw(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VIHOP:
                    return vihop(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_THIENTU:
                    return Nak(mapId, status, cx, cy, tempId, avatar);
                // case ConstNpc.HUNG_VUONG:
                //     return HungVuong(mapId, status, cx, cy, tempId, avatar);
                // case ConstNpc.NOI_BANH:
                //     return NoiBanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KHUDOHIEU:
                    return khudohieu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BOLAO:
                    return bolao(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GHI_DANH:
                    return GhiDanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUNG_LINH_THU:
                    return trungLinhThu(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.POTAGE:
                    return poTaGe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUY_LAO_KAME:
                    return quyLaoKame(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THO_DAI_CA:
                    return thodaika(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TRUONG_LAO_GURU:
                    return truongLaoGuru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VUA_VEGETA:
                    return vuaVegeta(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TO_SU_KAIO:
                    return toSuKaiO(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.ONG_GOHAN:
                case ConstNpc.ONG_MOORI:
                case ConstNpc.ONG_PARAGUS:
                    return ongGohan_ongMoori_ongParagus(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA:
                    return bulmaQK(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DENDE:
                    return dende(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.APPULE:
                    return appule(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DR_DRIEF:
                    return drDrief(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CARGO:
                    return cargo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUI:
                    return cui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.SANTA:
                    return santa(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.URON:
                    return uron(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BA_HAT_MIT:
                    return baHatMit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RUONG_DO:
                    return ruongDo(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DAU_THAN:
                    return dauThan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CALICK:
                    return calick(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.JACO:
                    return jaco(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THUONG_DE:
                    return thuongDe(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NONG_DAN:
                    return nongdan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.TE_THIEN_DAI_THANH:
                    return teThienDaiThanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.CUA_HANG_KY_GUI:
                    return kyGui(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Granola:
                    return granala(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GIUMA_DAU_BO:
                    return mavuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_LONG_NHAN:
                    return goKuLongNhan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.PCL_LONG_NHAN:
                    return pclLongNhan(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.Monaito:
                    return monaito(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.VADOS:
                    return vados(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KHI_DAU_MOI:
                    return khidaumoi(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_VU_TRU:
                    return thanVuTru(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.KIBIT:
                    return kibit(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.OSIN:
                    return osin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LY_TIEU_NUONG:
                    return npclytieunuong54(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.LINH_CANH:
                    return linhCanh(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUA_TRUNG:
                    return quaTrung(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.QUOC_VUONG:
                    return quocVuong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BUNMA_TL:
                    return bulmaTL(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_OMEGA:
                    return rongOmega(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.RONG_1S:
                case ConstNpc.RONG_2S:
                case ConstNpc.RONG_3S:
                case ConstNpc.RONG_4S:
                case ConstNpc.RONG_5S:
                case ConstNpc.RONG_6S:
                case ConstNpc.RONG_7S:
                    return rong1_to_7s(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.NPC_64:
                    return npcThienSu64(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BILL:
                    return bill(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.BO_MONG:
                    return boMong(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.THAN_MEO_KARIN:
                    return karin(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ:
                    return gokuSSJ_1(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.GOKU_SSJ_:
                    return gokuSSJ_2(mapId, status, cx, cy, tempId, avatar);
                case ConstNpc.DUONG_TANG:
                    return duongtank(mapId, status, cx, cy, tempId, avatar);
                default:
                    return new Npc(mapId, status, cx, cy, tempId, avatar) {
                        @Override
                        public void openBaseMenu(Player player) {
                            if (canOpenNpc(player)) {
                                super.openBaseMenu(player);
                            }
                        }

                        @Override
                        public void confirmMenu(Player player, int select) {
                            if (canOpenNpc(player)) {
//                                ShopService.gI().openShopNormal(player, this, ConstNpc.SHOP_BUNMA_TL_0, 0, player.gender);
                            }
                        }
                    };
            }
        } catch (Exception e) {
            Logger.logException(NpcFactory.class, e, "Lỗi load npc");
            return null;
        }
    }

    //girlbeo-mark
    public static void createNpcRongThieng() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_THIENG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRON_CONFIRM:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;
                    case ConstNpc.SHENRON_1_1:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_1 && select == SHENRON_1_STAR_WISHES_1.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_2, SHENRON_SAY, SHENRON_1_STAR_WISHES_2);
                            break;
                        }
                    case ConstNpc.SHENRON_1_2:
                        if (player.iDMark.getIndexMenu() == ConstNpc.SHENRON_1_2 && select == SHENRON_1_STAR_WISHES_2.length - 1) {
                            NpcService.gI().createMenuRongThieng(player, ConstNpc.SHENRON_1_1, SHENRON_SAY, SHENRON_1_STAR_WISHES_1);
                            break;
                        }
                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcRongBang() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.RONG_BANG, -1) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.SHENRONBANG_1:
                        if (select == 0) {
                            SummonDragon.gI().confirmWish();
                        } else if (select == 1) {
                            SummonDragon.gI().reOpenShenronWishes(player);
                        }
                        break;

                    default:
                        SummonDragon.gI().showConfirmShenron(player, player.iDMark.getIndexMenu(), (byte) select);
                        break;
                }
            }
        };
    }

    public static void createNpcConMeo() {
        Npc npc = new Npc(-1, -1, -1, -1, ConstNpc.CON_MEO, 351) {
            @Override
            public void confirmMenu(Player player, int select) {
                switch (player.iDMark.getIndexMenu()) {
                    case ConstNpc.IGNORE_MENU:

                        break;
                    case ConstNpc.MAKE_MATCH_PVP: //                        if (player.getSession().actived) 
                    {
                        if (Maintenance.isRuning) {
                            break;
                        }
                        PVPService.gI().sendInvitePVP(player, (byte) select);
                        break;
                    }
//                        else {
//                            Service.gI().sendThongBao(player, "|5|VUI LÒNG KÍCH HOẠT TÀI KHOẢN TẠI\n|7|NROGOD.COM\n|5|ĐỂ MỞ KHÓA TÍNH NĂNG");
//                            break;
//                        }
                    case ConstNpc.MAKE_FRIEND:
                        if (select == 0) {
                            Object playerId = PLAYERID_OBJECT.get(player.id);
                            if (playerId != null) {
                                FriendAndEnemyService.gI().acceptMakeFriend(player,
                                        Integer.parseInt(String.valueOf(playerId)));
                            }
                        }
                        break;
                    case ConstNpc.CALL_BOSS: {
                        switch (select) {
                            case 0:
                                BossManager.gI().createBoss(BossID.KAMIRIN);
                                BossManager.gI().createBoss(BossID.KAMILOC);
                                BossManager.gI().createBoss(BossID.KAMI_SOOME);
                                BossManager.gI().createBoss(BossID.CUMBERYELLOW);
                                BossManager.gI().createBoss(BossID.DR_KORE);
                                BossManager.gI().createBoss(BossID.KING_KONG);
                                BossManager.gI().createBoss(BossID.PIC);
                                BossManager.gI().createBoss(BossID.POC);
                                break;
                            case 1:
                                BossManager.gI().createBoss(BossID.BLACK);
                                break;
                            case 2:
                                BossManager.gI().createBoss(BossID.BROLY);
                                break;
                            case 3:
                                BossManager.gI().createBoss(BossID.SIEU_BO_HUNG);
                                BossManager.gI().createBoss(BossID.XEN_BO_HUNG);
                                break;
                            case 4:
                                Service.getInstance().sendThongBao(player, "Không có boss");
                                break;
                            case 5:
                                BossManager.gI().createBoss(BossID.CHAIEN);
                                BossManager.gI().createBoss(BossID.XEKO);
                                BossManager.gI().createBoss(BossID.XUKA);
                                BossManager.gI().createBoss(BossID.NOBITA);
                                BossManager.gI().createBoss(BossID.DORAEMON);
                                break;
                            case 6:
                                BossManager.gI().createBoss(BossID.FIDE);
                                break;
                            case 7:
                                BossManager.gI().createBoss(BossID.FIDE_ROBOT);
                                BossManager.gI().createBoss(BossID.VUA_COLD);
                                break;
                            case 8:
                                BossManager.gI().createBoss(BossID.SO_1);
                                BossManager.gI().createBoss(BossID.SO_2);
                                BossManager.gI().createBoss(BossID.SO_3);
                                BossManager.gI().createBoss(BossID.SO_4);
                                BossManager.gI().createBoss(BossID.TIEU_DOI_TRUONG);
                                break;
                            case 9:
                                BossManager.gI().createBoss(BossID.KUKU);
                                BossManager.gI().createBoss(BossID.MAP_DAU_DINH);
                                BossManager.gI().createBoss(BossID.RAMBO);
                                break;
                            case 10:
                                BossManager.gI().createBoss(BossID.COOLER_GOLD);
                                BossManager.gI().createBoss(BossID.CUMBER);
                                BossManager.gI().createBoss(BossID.SONGOKU_TA_AC);
                                break;
                            case 11:
                                BossManager.gI().createBoss(BossID.KHI_ULTRA);

                        }
                        break;
                    }
                    case ConstNpc.REVENGE:
                        if (select == 0) {
                            PVPService.gI().acceptRevenge(player);
                        }
                        break;
                    case ConstNpc.TUTORIAL_SUMMON_DRAGON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_BANG_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenronBang(player);
                        }
                        break;
                    case ConstNpc.SUMMON_SHENRON:
                        if (select == 0) {
                            NpcService.gI().createTutorial(player, -1, SummonDragon.SUMMON_SHENRON_TUTORIAL);
                        } else if (select == 1) {
                            SummonDragon.gI().summonShenron(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM1105:
                        if (select == 0) {
                            IntrinsicService.gI().sattd(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().satnm(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().setxd(player);
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2000:
                    case ConstNpc.MENU_OPTION_USE_ITEM2001:
                    case ConstNpc.MENU_OPTION_USE_ITEM2002:
                        try {
                            ItemService.gI().OpenSKH(player, player.iDMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            Logger.error("Lỗi mở hộp quà");
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2094:
                    case ConstNpc.MENU_OPTION_USE_ITEM2095:
                    case ConstNpc.MENU_OPTION_USE_ITEM2096:
                        try {
                            ItemService.gI().OpenDTL(player, player.iDMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            Logger.error("Lỗi mở hộp quà");
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2106:
                        Item itemUse = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2106);

                        if (select == 0) {

                            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item trungLinhThu = ItemService.gI().createNewItem((short) 2102, 50);
                                InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + trungLinhThu.template.name);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
                                InventoryServiceNew.gI().sendItemBags(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
                            }
                        } else if (select == 1) {
                            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item trungLinhThu = ItemService.gI().createNewItem((short) 2103, 50);
                                InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + trungLinhThu.template.name);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
                                InventoryServiceNew.gI().sendItemBags(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
                            }
                        } else if (select == 2) {
                            if (InventoryServiceNew.gI().getCountEmptyBag(player) > 0) {
                                Item trungLinhThu = ItemService.gI().createNewItem((short) 2104, 50);
                                InventoryServiceNew.gI().addItemBag(player, trungLinhThu);
                                InventoryServiceNew.gI().sendItemBags(player);
                                Service.gI().sendThongBao(player, "Bạn đã nhận được " + trungLinhThu.template.name);
                                InventoryServiceNew.gI().subQuantityItemsBag(player, itemUse, 1);
                                InventoryServiceNew.gI().sendItemBags(player);
                            } else {
                                Service.gI().sendThongBao(player, "Bạn phải có ít nhất 1 ô trống hành trang");
                            }
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM2137:
                        Item itemUse1 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2137);
                        if (select < 0 || select > 2) {
                            Service.gI().sendThongBao(player, "Chọn lại đi");

                            return;
                        }
                        if (player.mott == 0) {
                            Service.gI().sendThongBao(player, "Cần mở giới hạn thiên tử");

                            return;
                        }
                        if (player.inventory.itemsBody.size() < 6) {
                            Service.gI().sendThongBao(player, "Không đủ đồ thiên tử");

                            return;
                        }
                        if (itemUse1.isNotNullItem() && itemUse1.template.id == 2137) {
                            int thientu = player.thientu;
                            Item item0 = player.inventory.itemsBody.get(0);
                            Item item1 = player.inventory.itemsBody.get(1);
                            Item item2 = player.inventory.itemsBody.get(2);
                            Item item3 = player.inventory.itemsBody.get(3);
                            Item item4 = player.inventory.itemsBody.get(4);
                            Item item5 = player.inventory.itemsBody.get(5);
                            if (item0.isNotNullItem() && item1.isNotNullItem() && item2.isNotNullItem() && item3.isNotNullItem() && item4.isNotNullItem() && item5.isNotNullItem()) {

                                if (item0.template.id == 2156 && item1.template.id == 2157 && item2.template.id == 2158 && item3.template.id == 2159 && item4.template.id == 2160 && item5.template.id == 2162) { //ctthientu

                                    if (select == 0) {
                                        if (thientu == 1) {
                                            Service.gI().sendThongBao(player, "Đang là sát thương rồi !!!");
                                            return;
                                        } else {
                                            if (thientu == 2) {
                                                player.nPoint.hpMax /= 2;
                                            }
                                            if (thientu == 3) {
                                                player.nPoint.mpMax /= 2;
                                            }
                                            player.nPoint.dame *= 2;
                                            player.thientu = 1;
                                            Service.gI().sendThongBao(player, "Đã chọn thiên tử sát thương");
                                        }
                                    } else if (select == 1) {
                                        if (thientu == 2) {
                                            Service.gI().sendThongBao(player, "Đang là huyết long rồi !!!");
                                            return;
                                        } else {
                                            if (thientu == 1) {
                                                player.nPoint.dame /= 2;
                                            }
                                            if (thientu == 3) {
                                                player.nPoint.mpMax /= 2;
                                            }
                                            player.nPoint.hpMax *= 2;
                                            player.thientu = 2;
                                            Service.gI().sendThongBao(player, "Đã chọn thiên tử huyết long");
                                        }
                                    } else if (select == 2) {
                                        if (thientu == 3) {
                                            Service.gI().sendThongBao(player, "Đang là thể trạng rồi 1!!");
                                            return;
                                        } else {
                                            if (thientu == 1) {
                                                player.nPoint.dame /= 2;
                                            }
                                            if (thientu == 2) {
                                                player.nPoint.hpMax /= 2;
                                            }
                                            player.nPoint.mpMax *= 2;
                                            player.thientu = 3;
                                            Service.gI().sendThongBao(player, "Đã chọn thiên tử thể trạng");
                                        }
                                    }

                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ đồ thiên tử");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ đồ thiên tử");

                            }

                        }
                        break;
                  
                    case ConstNpc.MENU_OPTION_USE_ITEM2211:
                        Item itemUse2 = InventoryServiceNew.gI().findItem(player.inventory.itemsBag, 2211);
                        if (select < 0 || select > 2) {
                            Service.gI().sendThongBao(player, "Chọn lại đi");

                            return;
                        }
                        if (player.mott == 0) {
                            Service.gI().sendThongBao(player, "Cần mở giới hạn thiên tử");

                            return;
                        }
                        if (player.inventory.itemsBody.size() < 5) {
                            Service.gI().sendThongBao(player, "Không đủ đồ Chí Tôn");

                            return;
                        }
                        if (itemUse2.isNotNullItem() && itemUse2.template.id == 2211) {
                            int thientu = player.chiTon;
                            Item item0 = player.inventory.itemsBody.get(0);
                            Item item1 = player.inventory.itemsBody.get(1);
                            Item item2 = player.inventory.itemsBody.get(2);
                            Item item3 = player.inventory.itemsBody.get(3);
                            Item item4 = player.inventory.itemsBody.get(4);
                            Item item5 = player.inventory.itemsBody.get(5);
                            if (item0.isNotNullItem() && item1.isNotNullItem() && item2.isNotNullItem() && item3.isNotNullItem() && item4.isNotNullItem() && item5.isNotNullItem()) {

                                if (item0.template.id == 2212 && item1.template.id == 2213 && item2.template.id == 2214 && item3.template.id == 2215 && item4.template.id == 2216 ) { //ctthientu

                                    if (select == 0) {
                                        if (thientu == 1) {
                                            Service.gI().sendThongBao(player, "Đang là Chí Tôn sát thương rồi !!!");
                                            return;
                                        } else {
                                            if (thientu == 2) {
                                                player.nPoint.hpMax /= 4;
                                            }
                                            if (thientu == 3) {
                                                player.nPoint.mpMax /= 4;
                                            }
                                            player.nPoint.dame *= 4;
                                            player.thientu = 1;
                                            Service.gI().sendThongBao(player, "Đã chọn Chí Tôn sát thương");
                                        }
                                    } else if (select == 1) {
                                        if (thientu == 2) {
                                            Service.gI().sendThongBao(player, "Đang là Chí Tôn huyết long rồi !!!");
                                            return;
                                        } else {
                                            if (thientu == 1) {
                                                player.nPoint.dame /= 4;
                                            }
                                            if (thientu == 3) {
                                                player.nPoint.mpMax /= 4;
                                            }
                                            player.nPoint.hpMax *= 4;
                                            player.thientu = 2;
                                            Service.gI().sendThongBao(player, "Đã chọn Chí Tôn huyết long");
                                        }
                                    } else if (select == 2) {
                                        if (thientu == 3) {
                                            Service.gI().sendThongBao(player, "Đang là Chí Tôn thể trạng rồi 1!!");
                                            return;
                                        } else {
                                            if (thientu == 1) {
                                                player.nPoint.dame /= 4;
                                            }
                                            if (thientu == 2) {
                                                player.nPoint.hpMax /= 4;
                                            }
                                            player.nPoint.mpMax *= 4;
                                            player.thientu = 3;
                                            Service.gI().sendThongBao(player, "Đã chọn Chí Tôn thể trạng");
                                        }
                                    }

                                } else {
                                    Service.gI().sendThongBao(player, "Bạn không đủ đồ Chí Tôn");
                                }
                            } else {
                                Service.gI().sendThongBao(player, "Bạn không đủ đồ Chí Tôn");

                            }

                        }
                        break;
                  
                    
                    case ConstNpc.MENU_OPTION_USE_ITEM2003:
                    case ConstNpc.MENU_OPTION_USE_ITEM2004:
                    case ConstNpc.MENU_OPTION_USE_ITEM2005:
                        try {
                            ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            Logger.error("Lỗi mở hộp quà");
                        }
                        break;
                    case ConstNpc.MENU_OPTION_USE_ITEM736:
                        try {
                            ItemService.gI().OpenDHD(player, player.iDMark.getIndexMenu(), select);
                        } catch (Exception e) {
                            Logger.error("Lỗi mở hộp quà");
                        }
                        break;
                    case ConstNpc.INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().showAllIntrinsic(player);
                        } else if (select == 1) {
                            IntrinsicService.gI().showConfirmOpen(player);
                        } else if (select == 2) {
                            IntrinsicService.gI().showConfirmOpenVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC:
                        if (select == 0) {
                            IntrinsicService.gI().open(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_OPEN_INTRINSIC_VIP:
                        if (select == 0) {
                            IntrinsicService.gI().openVip(player);
                        }
                        break;

                    case ConstNpc.CONFIRM_LEAVE_CLAN:
                        if (select == 0) {
                            ClanService.gI().leaveClan(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_NHUONG_PC:
                        if (select == 0) {
                            ClanService.gI().phongPc(player, (int) PLAYERID_OBJECT.get(player.id));
                        }
                        break;
                    case ConstNpc.BAN_PLAYER:
                        if (select == 0) {
                            PlayerService.gI().banPlayer((Player) PLAYERID_OBJECT.get(player.id));
                            Service.gI().sendThongBao(player, "Ban người chơi " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                        }
                        break;

                    case ConstNpc.BUFF_PET:
                        if (select == 0) {
                            Player pl = (Player) PLAYERID_OBJECT.get(player.id);
                            if (pl.pet == null) {
                                PetService.gI().createNormalPet(pl);
                                Service.gI().sendThongBao(player, "Phát đệ tử cho " + ((Player) PLAYERID_OBJECT.get(player.id)).name + " thành công");
                            }
                        }
                        break;
                    case ConstNpc.UP_TOP_ITEM:
                        if (select == 0) {
                            if (player.inventory.gem >= 50 && player.iDMark.getIdItemUpTop() != -1) {
                                ItemKyGui it = ShopKyGuiService.gI().getItemBuy(player.iDMark.getIdItemUpTop());
                                if (it == null || it.isBuy) {
                                    Service.getInstance().sendThongBao(player, "Vật phẩm không tồn tại hoặc đã được bán");
                                    return;
                                }
                                if (it.player_sell != player.id) {
                                    Service.getInstance().sendThongBao(player, "Vật phẩm không thuộc quyền sở hữu");
                                    ShopKyGuiService.gI().openShopKyGui(player);
                                    return;
                                }
                                player.inventory.gem -= 50;
                                Service.getInstance().sendMoney(player);
                                Service.getInstance().sendThongBao(player, "Thành công");
                                it.isUpTop += 1;
                                ShopKyGuiService.gI().openShopKyGui(player);
                            } else {
                                Service.getInstance().sendThongBao(player, "Bạn không đủ hồng ngọc");
                                player.iDMark.setIdItemUpTop(-1);
                            }
                        }
                        break;

                    case ConstNpc.MENU_ADMIN:
                        switch (select) {

                            case 0:
                                if (player.isAdmin()) {
                                    System.out.println(player.name);
//                                PlayerService.gI().baoTri();
                                    Maintenance.gI().start(15);
                                    System.out.println(player.name);
                                }
                                break;
                            case 1:
                                Input.gI().createFormFindPlayer(player);
                                break;
                            case 2:
                                BossManager.gI().showListBoss(player);
                                break;
                            case 3:
                                MaQuaTangManager.gI().checkInfomationGiftCode(player);
                                break;
                            case 4:
                                Input.gI().createFormNapCoin(player);
                                break;
                            case 5:
                                Input.gI().createFormGiveItem(player);
                                break;
                            case 6:
                                Input.gI().createFormNapCoinKey(player);
                                break;
                            case 7:
                                Input.gI().createFormNapCoinAll(player);
                                break;
                            case 8:
                                this.createOtherMenu(player, ConstNpc.CALL_BOSS,
                                        "Chọn Boss?", "Full Cụm\nANDROID", "BLACK", "BROLY", "Cụm\nCell",
                                        "Cụm\nDoanh trại", "DOREMON", "FIDE", "FIDE\nBlack", "Cụm\nGINYU", "Cụm\nNAPPA", "NGỤC\nTÙ", "Khỉ 8 Ultra");
                                break;
                        }
                        break;

                    case ConstNpc.menutd:
                        switch (select) {
                            case 0:
                                try {
                                    ItemService.gI().settaiyoken(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                try {
                                    ItemService.gI().setgenki(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 2:
                                try {
                                    ItemService.gI().setkamejoko(player);
                                } catch (Exception e) {
                                }
                                break;
                        }
                        break;

                    case ConstNpc.menunm:
                        switch (select) {
                            case 0:
                                try {
                                    ItemService.gI().setgodki(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                try {
                                    ItemService.gI().setgoddam(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 2:
                                try {
                                    ItemService.gI().setsummon(player);
                                } catch (Exception e) {
                                }
                                break;
                        }
                        break;

                    case ConstNpc.menuxd:
                        switch (select) {
                            case 0:
                                try {
                                    ItemService.gI().setgodgalick(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 1:
                                try {
                                    ItemService.gI().setmonkey(player);
                                } catch (Exception e) {
                                }
                                break;
                            case 2:
                                try {
                                    ItemService.gI().setgodhp(player);
                                } catch (Exception e) {
                                }
                                break;
                        }
                        break;

                    case ConstNpc.CONFIRM_DISSOLUTION_CLAN:
                        switch (select) {
                            case 0:
                                Clan clan = player.clan;
                                clan.deleteDB(clan.id);
                                Manager.CLANS.remove(clan);
                                player.clan = null;
                                player.clanMember = null;
                                ClanService.gI().sendMyClan(player);
                                ClanService.gI().sendClanId(player);
                                Service.gI().sendThongBao(player, "Đã giải tán bang hội.");
                                break;
                        }
                        break;
//                    case ConstNpc.CONFIRM_ACTIVE:
//                        switch (select) {
//                            case 0:
//                                if (player.getSession().goldBar >= 20) {
//                                    player.getSession().actived = true;
//                                    if (PlayerDAO.subGoldBar(player, 20)) {
//                                        Service.gI().sendThongBao(player, "Đã mở thành viên thành công!");
//                                        break;
//                                    } else {
//                                        this.npcChat(player, "Lỗi vui lòng báo admin...");
//                                    }
//                                }
////                                Service.gI().sendThongBao(player, "Bạn không có vàng\n Vui lòng NROGOD.COM để nạp thỏi vàng");
//                                break;
//                        }
//                        break;
                    case ConstNpc.CONFIRM_REMOVE_ALL_ITEM_LUCKY_ROUND:
                        if (select == 0) {
                            for (int i = 0; i < player.inventory.itemsBoxCrackBall.size(); i++) {
                                player.inventory.itemsBoxCrackBall.set(i, ItemService.gI().createItemNull());
                            }
                            player.inventory.itemsBoxCrackBall.clear();
                            Service.gI().sendThongBao(player, "Đã xóa hết vật phẩm trong rương");
                        }
                        break;
                    case ConstNpc.MENU_FIND_PLAYER:
                        Player p = (Player) PLAYERID_OBJECT.get(player.id);
                        if (p != null) {
                            switch (select) {
                                case 0:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMapYardrat(player, p.zone, p.location.x, p.location.y);
                                    }
                                    break;
                                case 1:
                                    if (p.zone != null) {
                                        ChangeMapService.gI().changeMap(p, player.zone, player.location.x, player.location.y);
                                    }
                                    break;
                                case 2:
                                    Input.gI().createFormChangeName(player, p);
                                    break;
                                case 3:
                                    String[] selects = new String[]{"Đồng ý", "Hủy"};
                                    NpcService.gI().createMenuConMeo(player, ConstNpc.BAN_PLAYER, -1,
                                            "Bạn có chắc chắn muốn ban " + p.name, selects, p);
                                    break;
                                case 4:
                                    Service.gI().sendThongBao(player, "Kik người chơi " + p.name + " thành công");
                                    Client.gI().getPlayers().remove(p);
                                    Client.gI().kickSession(p.getSession());
                                    break;
                            }
                        }
                        break;
                    case ConstNpc.MENU_EVENT:
                        switch (select) {
                            case 0:
                                Service.gI().sendThongBaoOK(player, "Điểm sự kiện: " + player.inventory.event + " ngon ngon...");
                                break;
                            /*case 1:
                                Service.gI().showListTop(player, Manager.topSK);
                                break;*/
                            case 2:
                                Service.gI().sendThongBao(player, "Sự kiện đã kết thúc...");
                                NpcService.gI().createMenuConMeo(player, ConstNpc.MENU_GIAO_BONG, -1, "Người muốn giao bao nhiêu bông...",
                                        "100 bông", "1000 bông", "10000 bông");
                                break;
                            case 3:
                                Service.gI().sendThongBao(player, "Sự kiện đã kết thúc...");
                                NpcService.gI().createMenuConMeo(player, ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN, -1, "Con có thực sự muốn đổi thưởng?\nPhải giao cho ta 3000 điểm sự kiện đấy... ",
                                        "Đồng ý", "Từ chối");
                                break;

                        }
                        break;
                    case ConstNpc.MENU_GIAO_BONG:
                        ItemService.gI().giaobong(player, (int) Util.tinhLuyThua(10, select + 2));
                        break;
                    case ConstNpc.CONFIRM_DOI_THUONG_SU_KIEN:
                        if (select == 0) {
                            ItemService.gI().openBoxVip(player);
                        }
                        break;
                    case ConstNpc.CONFIRM_TELE_NAMEC:
                        if (select == 0) {
                            NgocRongNamecService.gI().teleportToNrNamec(player);
                            player.inventory.subGemAndRuby(50);
                            Service.gI().sendMoney(player);
                        }
                        break;
                }
            }
        };
    }

}
