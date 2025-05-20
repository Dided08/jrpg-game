package ru.samsung.multiscreen;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class PokeButton {
    BitmapFont font;
    String text;
    float x, y;
    float width, height;

    public PokeButton(BitmapFont font, String text, float x, float y) {
        this.font = font;
        this.font.getData().setScale(1.0f);
        this.text = text;
        this.x = x;
        this.y = y;
        updateSize();
    }

    public void setText(String text) {
        this.text = text;
        updateSize();
    }

    public boolean hit(float tx, float ty) {
        return x < tx && tx < x + width && y > ty && ty > y - height;
    }

    private void updateSize() {
        GlyphLayout layout = new GlyphLayout(font, text);
        this.width = layout.width + 20;
        this.height = layout.height + 20;
    }

    public void render(SpriteBatch batch, Color color) {
        font.setColor(color);
        font.draw(batch, text, x, y);
    }

    public void setPositionBottomLeft(float x, float y) {
        this.x = x;
        this.y = y + height;
    }
}
