package ch.supsi.business.state;

public interface EditorState {
    boolean canApplyFilters();
    boolean canSave();
    boolean canSaveAs();
    boolean canAddFilter();
    boolean canExport();
    boolean hasUnsavedChanges();
}