package testcraft.blocks;

import com.badlogic.gdx.graphics.Texture;
import org.mini2Dx.core.engine.geom.CollisionBox;
import org.mini2Dx.core.graphics.Sprite;
import testcraft.Block;

import java.io.Serializable;

public class DirtBlock extends Block implements Serializable {

    private static Sprite[] blockSprites=new Sprite[]{new Sprite(new Texture("DirtBlock.png"))};
    private static String blockName = "Dirt";

    private CollisionBox collisionBox;

    public DirtBlock(){ }

    @Override
    public Sprite getBlockSprite() {
        return blockSprites[0];
    }
}
