package ch.supsi.business.filter;

import ch.supsi.application.filters.FilterPipelineInterface;
import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.command.FilterCommand;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

public class FilterManager implements FilterPipelineInterface {
    private static FilterManager instance;
    private final List<FilterCommand> pipeline = new ArrayList<>();

    public static FilterManager getInstance() {
        if(instance == null) {
            instance = new FilterManager();
        }
        return instance;
    }

    private FilterManager() {}

    @Override
    public void executePipeline(ImageBusinessInterface image) {
        if (image == null) return;

        pipeline.stream()
                .filter(Objects::nonNull)
                .forEach(command -> {
                    command.execute(image);
                });

        pipeline.clear();
    }

    @Override
    public void addFilter(FilterCommand command) {
        pipeline.add(command);
    }

    @Override
    public void addFilter(FilterCommand command, int index) {
        pipeline.add(index, command);
    }

    @Override
    public void remove(int index) {
        pipeline.remove(index);
    }
    

    @Override
    public List<FilterCommand> getPipeline() {
        return new ArrayList<>(pipeline);
    }
}