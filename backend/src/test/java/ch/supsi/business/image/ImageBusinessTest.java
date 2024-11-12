package ch.supsi.business.image;

import ch.supsi.business.image.ImageBusiness;
import ch.supsi.business.strategy.SingleBit;
import ch.supsi.business.strategy.ConvertStrategy;
import ch.supsi.business.strategy.SingleChannel;
import ch.supsi.business.strategy.ThreeChannel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ImageBusinessTest {

    /* instance fields */
    private long[][] sampleMatrix;
    private int width;
    private int height;
    private ConvertStrategy strategy;

    /**
     * initialize a 2x2 matrix
     */
    @BeforeEach
    void setUp() {
        sampleMatrix = new long[][]{
                {1, 0},
                {0, 1}
        };
        width = 2;
        height = 2;
        strategy = new SingleBit();
    }
}

