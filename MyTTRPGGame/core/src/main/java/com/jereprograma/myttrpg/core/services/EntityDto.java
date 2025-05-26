package com.jereprograma.myttrpg.core.services;

public class EntityDto {
    public int x;
    public int y;
    public String spritePath;

    // Jackson necesita un constructor sin parámetros
    public EntityDto() {}

    public EntityDto(int x, int y, String spritePath) {
        this.x = x;
        this.y = y;
        this.spritePath = spritePath;
    }
}
