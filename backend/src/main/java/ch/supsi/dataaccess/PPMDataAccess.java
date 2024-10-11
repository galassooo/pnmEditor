package ch.supsi.dataaccess;

import ch.supsi.business.Image.ImageBusiness;
import ch.supsi.business.strategy.ArgbSingleChannelNoLevels;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public final class PPMDataAccess extends PNMDataAccess {
    @Override
    protected @NotNull ImageBusiness processBinary(InputStream is) throws IOException {
        return new ImageBusiness(new int[][]{{1}}, 1, 1, new ArgbSingleChannelNoLevels());

    }

    @Override
    protected @NotNull ImageBusiness processAscii(InputStream is) throws IOException {
        return new ImageBusiness(new int[][]{{1}}, 1, 1, new ArgbSingleChannelNoLevels());
    }
}
