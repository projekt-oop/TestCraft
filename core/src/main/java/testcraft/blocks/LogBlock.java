package testcraft.blocks;

import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.graphics.Sprite;
import testcraft.Block;

public class LogBlock extends Block {

    private static Texture texture = new Texture("LogBlock.png");
    private static int Id = 6;
    private static Sprite[] blockSprites=new Sprite[]{new Sprite(texture)};
    private static String blockName = "Log Block";

    public LogBlock(){}

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public Sprite getBlockSprite() {
        return blockSprites[0];
    }

    @Override
    public Block getNewBlock() {
        return new LogBlock();
    }

    @Override
    public int getId() {
        return Id;
    }
}
