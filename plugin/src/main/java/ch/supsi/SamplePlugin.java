package ch.supsi;

import com.sun.source.util.JavacTask;
import com.sun.source.util.Plugin;

public class SamplePlugin implements Plugin {
    @Override
    public String getName() {
        return "MyPlugin";
    }

    @Override
    public void init(JavacTask task, String... args) {
        System.out.println("ciao dal plugin!!!!!!");
    }
}