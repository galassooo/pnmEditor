package ch.supsi.business.filter.chain;

import ch.supsi.application.image.ImageBusinessInterface;

public abstract class FilterChainLink implements FilterCommand{

    private FilterChainLink next;


    @Override
    public  void execute(ImageBusinessInterface image){
        executeFilter(image);
        if(next != null){
            next.execute(image);
            next = null;
        }
    }

    public void setNext(FilterChainLink next){
        this.next = next;
    }

    public FilterChainLink getNext(){
        return next;
    }
    public abstract void executeFilter(ImageBusinessInterface image);
    @Override
    public abstract String getName();



}
