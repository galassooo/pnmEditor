package ch.supsi;

public class Main {

    public static void main(String[] args) {
        assert 10 == BackendMain.TO_TEST_DEPENDENCY; //to be removed

        MainFx.main(args);
    }
}