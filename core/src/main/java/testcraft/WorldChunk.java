package testcraft;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import org.mini2Dx.core.graphics.Graphics;
import org.mini2Dx.core.graphics.Sprite;
import testcraft.blocks.*;
import testcraft.blocks.Void;

import java.io.Serializable;
import java.util.Random;

public abstract class WorldChunk implements Serializable {

    private static Random random=new Random();
    private static Sprite lowBlockDmg=new Sprite(new Texture("LowBlockDmg.png"));
    private static Sprite mediumBlockDmg=new Sprite(new Texture("MedBlockDmg.png"));
    private static Sprite highBlockDmg=new Sprite(new Texture("HighBlockDmg.png"));


    public static int CHUNK_SIZE = 64;

    protected Block[][] blocks;                       //array containing chunk's blocks' information first coordinate is X, second Y

    final int chunkPosX, chunkPosY;                 //chunk's left top corner's world coordinates

    public WorldChunk(int xPos, int yPos, ChunkLoader chunkLoader, Block[][] blocks){
        this.blocks = blocks;
        chunkPosX = xPos;
        chunkPosY = yPos;

        for(int i = 0; i < blocks.length; i++)
            for(int j = 0; j < blocks[i].length; j++)
                blocks[i][j] = new Void();

    }

    public WorldChunk(int xPos, int yPos, ChunkLoader chunkLoader){
        this(xPos, yPos, chunkLoader, new Block[CHUNK_SIZE][CHUNK_SIZE]);
    }


    void renderChunk(Graphics g, float shiftX, float shiftY){
        /*
         * Draw each block at integer coordinate.
         *
         */
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks[i].length; j++){

                //calculate blocks world coordinates
                float posX = (chunkPosX - shiftX + i)*Block.PIXEL_COUNT;
                float posY = (chunkPosY - shiftY + j)*Block.PIXEL_COUNT;

                if(posX < -Block.PIXEL_COUNT || posX > InGameScreen.WIDTH
                || posY < -Block.PIXEL_COUNT || posY > InGameScreen.HEIGHT)
                    continue;   //don't render things off-screen

                //get block sprite and set sprite coordinates
                Sprite blockSprite = blocks[i][j].getBlockSprite();
                blockSprite.setPosition(posX+2, posY+2);
                blockSprite.setScale(2f);
                //finally, draw the sprite
                g.drawSprite(blockSprite);

                //draw damage
                boolean skip=false;
                Sprite visibleDmg=lowBlockDmg;
                float percent=blocks[i][j].getDurabilityPercentage();
                if(percent>=0.7f){
                    skip=true;
                } else if(percent<0.7f && percent>0.4f){
                    visibleDmg=lowBlockDmg;
                } else if(percent<=0.4f && percent>0.2f){
                    visibleDmg=mediumBlockDmg;
                } else {
                    visibleDmg=highBlockDmg;
                }
                if(!skip) {
                    visibleDmg.setPosition(posX + 2, posY + 2);
                    visibleDmg.setScale(2f);
                    g.drawSprite(visibleDmg);
                }
            }
        }
    }
    Rectangle getRectangle(int a, int b)
    {

        return new Rectangle((chunkPosX+a)*Block.PIXEL_COUNT,(chunkPosY+b)*Block.PIXEL_COUNT, 1*Block.PIXEL_COUNT,1 *Block.PIXEL_COUNT);
    }



    //Function to get block from chunk by its chunk coordinates
    Block getBlock(int x, int y){
        return blocks[x][y];
    }

    void setBlock(int x, int y, Block block){
        if(block!=null)
            blocks[x][y]=block;
    }

    //For World-aware checks
    protected Block getBlock(int x, int y, ChunkLoader chunkLoader){
        if(x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE)
            return blocks[x][y];
        Block result = chunkLoader.world.findBlock(chunkPosX + x, chunkPosY + y);
        return result == null ? new OneBlockyBoy() : result;
    }

    protected void setBlock(int x, int y, Block block, ChunkLoader chunkLoader){
        if(block!=null)
            if(x >= 0 && x < CHUNK_SIZE && y >= 0 && y < CHUNK_SIZE)
                blocks[x][y]=block;
            else chunkLoader.world.setBlock(chunkPosX+x, chunkPosY+y, block);               //to not have issues with nullity
    }

    boolean isBlockSolid(int a, int b){
        return blocks[a][b].isSolid();
    }

    boolean isBlockOccupied(int a, int b){ return blocks[a][b].isOccupied(); }

    public void update(ChunkLoader chunkLoader){
        //place to check some block-specific updates

        for(int i = 0; i < CHUNK_SIZE; i++){
            for(int j = 0; j < CHUNK_SIZE; j++){
                blocks[i][j].update();
                Block block = blocks[i][j];
                if(block instanceof GrassDirtBlock){
                    if(j > 0){
                        if(getBlock(i, j-1, chunkLoader).isSolid()) setBlock(i, j, new DirtBlock(), chunkLoader);
                        else if(getBlock(i, j-1, chunkLoader) instanceof Void && new Random().nextInt(50) == 0) setBlock(i, j-1, getGrassOrFlower(), chunkLoader);
                    }
                } else if(block instanceof DirtBlock){
                    if(checkGrass(i-1, j, chunkLoader) || checkGrass(i+1, j, chunkLoader)){
                        if(!getBlock(i, j-1, chunkLoader).isSolid() && new Random().nextInt(20)==0) setBlock(i, j, new GrassDirtBlock(), chunkLoader);
                    }
                } else if(block instanceof GrassBlock || block instanceof Flower){
                    if(!getBlock(i, j+1, chunkLoader).isSolid()) setBlock(i, j, new Void(), chunkLoader);
                }
            }
        }
    }

    private boolean checkGrass(int i, int j, ChunkLoader chunkLoader){
        return getBlock(i, j, chunkLoader) instanceof GrassDirtBlock || getBlock(i, j-1, chunkLoader) instanceof GrassDirtBlock || getBlock(i, j+1, chunkLoader) instanceof GrassDirtBlock;
    }

    protected void createCluster(int i, int j, int c){
        if(i>0)
            blocks[i-1][j]=pickBlock(c);
        if(i<CHUNK_SIZE-1)
            blocks[i+1][j]=pickBlock(c);
        blocks[i][j]=pickBlock(c);
        if(j>0)
            blocks[i][j-1]=pickBlock(c);
        if(j<CHUNK_SIZE-1)
            blocks[i][j+1]=pickBlock(c);
    }

    private Block pickBlock(int c){
        switch (c){
            case 1: return new DirtBlock();
            case 2: return new CobblestoneBlock();
            case 3: return new CoalBlock();
            case 10: return new IronBlock();
            case 17: return new DiamondBlock();
            default: return new Void();
        }
    }

    protected Block getGrassOrFlower(){
        if(random.nextBoolean())
            return new GrassBlock();
        else{
            int a=random.nextInt()%3;
            switch (a){
                case 0: return new RedFlower();
                case 1: return new BlueFlower();
                case 2: return new YellowFlower();
                default: return new GrassBlock();
            }
        }
    }

}
