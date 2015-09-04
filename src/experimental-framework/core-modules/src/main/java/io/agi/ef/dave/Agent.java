/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package io.agi.ef.dave;

import io.agi.ef.experiment.Asynchronous;

/**
 *
 * @author dave
 */
public interface Agent extends Asynchronous, Stateful {
 
    public void reset();
    public String getName();
    public void setName( String name );
    public Experiment getExperiment();
    public void setExperiment( Experiment e );
    
}
