package io.agi.ef.core.sensors;


import io.agi.core.data.Data;
import io.agi.ef.core.UniversalState;
import io.agi.ef.core.actuators.LightActuator;

/**
 * Created by gideon on 1/08/15.
 */
public class LightSensor extends Sensor {

    private Data _outData = null;
    private float _gain = 1.0f;
    private float _brightness;

    public LightSensor( float gain ) {
        super( 1, 1 );

        _gain = gain;
        _outData = new Data( 1 );
    }

    @Override
    public void update( UniversalState worldState ) {

        if ( worldState == null ) {
            System.out.println( "WARNING: LightSensor update with null worldState" );
            return;
        }

        if ( worldState._state == null ) {
            System.out.println( "WARNING: LightSensor update with null worldState._state" );
            return;
        }

        String key = LightActuator.class.getName();
        if ( worldState._state.containsKey( key ) == false ) {
            System.out.println( "WARNING: LightSensor update could not get value of LightActuator from the world state" );
        }

        String val = worldState._state.get( key );
        Float fVal = Float.valueOf( val );
        _brightness = fVal * _gain;
        _outData.set( _brightness );
    }

    @Override
    public Data getOutput() {
        return _outData;
    }

    public float getBrightness() {
        return _brightness;
    }
}
