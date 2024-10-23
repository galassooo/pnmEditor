package ch.supsi.business.strategy;

public interface ConvertStrategy {
    long toArgb(long pixel);
    long ArgbToOriginal(long pixel);
}

