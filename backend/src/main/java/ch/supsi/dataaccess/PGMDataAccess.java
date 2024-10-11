package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbConvertStrategy;
import ch.supsi.business.strategy.ArgbSingleChannelNoLevels;
import org.jetbrains.annotations.NotNull;

import java.io.InputStream;

public final class PGMDataAccess extends PNMDataAccess {


    @Override
    protected @NotNull ImageBusiness processBinary(InputStream is) {
        return new ImageBusiness(new int[][]{{1}}, 1, 1, new ArgbSingleChannelNoLevels());
    }

    @Override
    protected @NotNull ImageBusiness processAscii(InputStream is) {
        return new ImageBusiness(new int[][]{{1}}, 1, 1, new ArgbSingleChannelNoLevels());
    }

}
