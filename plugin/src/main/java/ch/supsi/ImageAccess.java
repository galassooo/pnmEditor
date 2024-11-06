package ch.supsi;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
@javax.annotation.processing.SupportedAnnotationTypes("ch.supsi.ImageAccess")
public @interface ImageAccess {
    String[] magicNumber();
    String extension() default "";
}
