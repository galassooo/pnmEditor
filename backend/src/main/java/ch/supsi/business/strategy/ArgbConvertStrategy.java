package ch.supsi.business.strategy;

public interface ArgbConvertStrategy {
    long toArgb(long pixel);
    long toOriginal(long pixel);
}

