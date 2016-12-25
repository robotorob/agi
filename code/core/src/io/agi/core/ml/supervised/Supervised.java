/*
 * Copyright (c) 2016.
 *
 * This file is part of Project AGI. <http://agi.io>
 *
 * Project AGI is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Project AGI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Project AGI.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.agi.core.ml.supervised;

import io.agi.core.data.Data;

/**
 * Created by gideon on 14/12/16.
 */
public interface Supervised {

    void reset();
    int predict();

    // train the model (subclasses should ensure that the model is saved to config)
    void train( Data featuresMatrix, Data classTruthVector );

    // load from config
    void loadModel( );

    // load model from a string (and set config to be consistent)
    void loadModel( String modelString );
}