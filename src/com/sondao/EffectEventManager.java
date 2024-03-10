package com.sondao;

import com.girlkun.database.GirlkunDB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.girlkun.utils.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EffectEventManager {
    private static final EffectEventManager i = new EffectEventManager();

    public static EffectEventManager gI() {
        return i;
    }
    private final List<EffectEventTemplate> list = new ArrayList<>();

    public List<EffectEventTemplate> getList() {
        return list;
    }

    public void load(Connection con) {
        try {
            PreparedStatement ps = con.prepareStatement("select * from map_template");
            ResultSet rs = ps.executeQuery();
            int imap = 0;
            try {
                while (rs.next()) {
                    short mapID = (short) rs.getInt("id");
                    imap = mapID;
                    JSONArray jar = new JSONArray(rs.getString("eff_event"));
                    for (short j = 0; j < jar.length(); j++) {
                        JSONObject jobj = jar.getJSONObject(j);
                        //short evID = (short) jobj.getInt("event_id");
                        short effID = (short) jobj.getInt("eff_id");
                        short layer = (short) jobj.getInt("layer");
                        short x = (short) jobj.getInt("x");
                        short y = (short) jobj.getInt("y");
                        short loop = (short) jobj.getInt("loop");
                        short delay = (short) jobj.getInt("delay");
                        EffectEventTemplate ee = new EffectEventTemplate();
                        ee.setMapId(mapID);
                        //ee.setEventId(evID);
                        ee.setEffId(effID);
                        ee.setLayer(layer);
                        ee.setX(x);
                        ee.setY(y);
                        ee.setLoop(loop);
                        ee.setDelay(delay);
                        add(ee);
                    }
                }
                Logger.success("Init event_eff thành công!");
            } catch (JSONException e) {
                Logger.error(imap+",");
                //throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void add(EffectEventTemplate ee) {
        list.add(ee);
    }
}
