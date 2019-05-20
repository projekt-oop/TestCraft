package testcraft.blocks;

import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.graphics.Sprite;
import testcraft.Block;

import java.io.Serializable;

public class DirtBlock extends CollectibleBlock implements Serializable {

    private static int Id = 1;
    private static Texture texture = new Texture("DirtBlock.png");
    private static Sprite[] blockSprites=new Sprite[]{new Sprite(texture)};
    private static String blockName = "Dirt";

    public DirtBlock(){
        super(60);
    }

    @Override
    public boolean isSolid() {
        return true;
    }

    @Override
    public Sprite getBlockSprite() {
        return blockSprites[0];
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public Block getNewBlock(){
        return new DirtBlock();
    }

    @Override
    public int getId() {
        return Id;
    }


}
