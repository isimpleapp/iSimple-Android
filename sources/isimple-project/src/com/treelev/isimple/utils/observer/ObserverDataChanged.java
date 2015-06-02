package com.treelev.isimple.utils.observer;

import java.util.HashSet;
import java.util.Set;

public class ObserverDataChanged  {

    private static ObserverDataChanged mInstant;

    private Set<Observer> mObservers;

    private ObserverDataChanged(){
        mObservers = new HashSet<Observer>();
    }

    public static ObserverDataChanged getInstant(){
        if(mInstant == null){
            mInstant = new ObserverDataChanged();
        }
        return  mInstant;
    }

    public void addObserver(Observer observer){
        mObservers.add(observer);
    }

    public void sendEvent(){
        for(Observer observer : mObservers){
            if(observer != null){
               observer.dataChanged();
            }
        }
    }

    public boolean removeObserver(Observer observer){
        return mObservers.remove(observer);
    }
}
