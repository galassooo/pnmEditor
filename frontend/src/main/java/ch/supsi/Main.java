package ch.supsi;

import ch.supsi.application.translations.TranslationsApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath*:i18n/*.properties");

        System.out.println("Risorse trovate in i18n/ui_labels:");
        for (Resource resource : resources) {
            System.out.println(resource.getURL());  // Stampa il percorso di ogni risorsa
        }

        TranslationsApplication ap = TranslationsApplication.getInstance();

        System.out.println(ap.translate("ui_cancel_button"));
        //MainFx.main(args);
    }
}