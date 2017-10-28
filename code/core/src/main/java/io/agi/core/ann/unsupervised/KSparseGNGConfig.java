/*
 * Copyright (c) 2017.
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

package io.agi.core.ann.unsupervised;

import io.agi.core.orm.ObjectMap;

import java.util.Random;

/**
 * Created by dave on 17/09/17.
 */
public class KSparseGNGConfig extends GrowingNeuralGasConfig {

    public static final String SPARSITY = "sparsity";

    public KSparseGNGConfig() {

    }

    public void setup(
            ObjectMap om,
            String name,
            Random r,
            int inputs,
            int w,
            int h,
            float learningRate,
            float learningRateNeighbours,
            float noiseMagnitude,
            int edgeMaxAge,
            float stressLearningRate,
            float stressSplitLearningRate,
            float stressThreshold,
            float utilityLearningRate,
            float utilityThreshold,
            int growthInterval,
            float denoisePercentage,
            float sparsity ) {
        super.setup( om, name, r, inputs, w, h, learningRate, learningRateNeighbours, noiseMagnitude, edgeMaxAge, stressLearningRate, stressSplitLearningRate, stressThreshold, utilityLearningRate, utilityThreshold, growthInterval, denoisePercentage );
        setSparsity( sparsity );
    }

    public void setSparsity( float r ) {
        _om.put( getKey( SPARSITY ), r );
    }

    public float getSparsity() {
        return _om.getFloat( getKey( SPARSITY ) );
    }

}