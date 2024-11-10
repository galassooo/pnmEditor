package org.supsi.view.image;

import org.supsi.view.filter.FilterUpdateListener;
import org.supsi.view.filter.IFilterEvent;

public interface IExportEvent {
    void registerListener(ExportEventListener listener);
    void deregisterListener(ExportEventListener listener);
}
