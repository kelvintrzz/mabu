package com.sondao;

public class EffectEventTemplate {
    private short mapId;
    private short eventId;
    private short effId;
    private short layer;
    private short x;
    private short y;
    private short loop;
    private short delay;

    public int getMapId() {
        return mapId;
    }

    public void setMapId(short mapId) {
        this.mapId = mapId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(short eventId) {
        this.eventId = eventId;
    }

    public int getEffId() {
        return effId;
    }

    public void setEffId(short effId) {
        this.effId = effId;
    }

    public int getLayer() {
        return layer;
    }

    public void setLayer(short layer) {
        this.layer = layer;
    }

    public int getX() {
        return x;
    }

    public void setX(short x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(short y) {
        this.y = y;
    }

    public int getLoop() {
        return loop;
    }

    public void setLoop(short loop) {
        this.loop = loop;
    }

    public int getDelay() {
        return delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }
}
