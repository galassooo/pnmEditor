package ch.supsi.application.filters;


import ch.supsi.application.image.ImageBusinessInterface;
import ch.supsi.business.filter.command.FilterCommand;

import java.util.List;

public interface FilterPipelineInterface {

    void  addFilter(FilterCommand filterStrategy);
    void  addFilter(FilterCommand filterStrategy, int index);
    void remove(int index);
    List<FilterCommand> getPipeline();
    void executePipeline(ImageBusinessInterface image);
}
