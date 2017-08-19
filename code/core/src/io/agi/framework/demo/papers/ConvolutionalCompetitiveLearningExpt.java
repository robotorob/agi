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

package io.agi.framework.demo.papers;

import io.agi.core.orm.AbstractPair;
import io.agi.framework.Framework;
import io.agi.framework.Node;
import io.agi.framework.demo.CreateEntityMain;
import io.agi.framework.demo.mnist.ImageLabelEntity;
import io.agi.framework.demo.mnist.ImageLabelEntityConfig;
import io.agi.framework.entities.*;
import io.agi.framework.entities.convolutional_competitive_learning.CompetitiveLearningConvolutionalNetworkEntity;
import io.agi.framework.entities.convolutional_competitive_learning.CompetitiveLearningConvolutionalNetworkEntityConfig;
import io.agi.framework.entities.stdp.*;
import io.agi.framework.persistence.models.ModelData;

import java.util.ArrayList;

/**
 * NOTE: 10k train / 1k test are 2 separate sets.
 *
 * 60% - 3 layer 30/100/100 10k train
 * 62% - same but 10x 10k train
 * 60% - 5x 10k train 1k test, layer 30/100/200  -- tested reconstruction, looks fine. (EDIT: Later found out the reconstructions were bogus)
 * 95.79 / 88.6% - 5x 10k train 1k test, layer 30/100 pooling 2,2
 * 28.3%, as above, but pooling 2,8. Inverse is fine. Good actually.
 * 99.98% / 93.5% - Back to the 88.6 config, but with pooling 2,1  (and disabled a bug with the stress thresholds). Overfitted.
 * 28.3% - VERIFIED - repeat the 28.3% result but with the fix to stress threshold. So the above results are valid.
 * 99.9% / 94.2% -- as 93.% but layer depths 30/70. pooling 2,1
 * 99.91% / 93.9% -- as before but depths 40/70
 * 99.99% / 94.1% --  depths 40/120   C1: 24x24 P1: 12x12 C2: 8x8 P2: 8x8
 * Created by dave on 12/08/17.
 */

//make a way to enter the model to invert and test that - froma single cell in L3. Verify that it inverts correctly.

// increase/decrease depth to check for better performance.
// bigger receptive fields
// increase training time

// POOLING logic
//maybe pooling should consider the number of votes rather than min error? - the big pooling doesnt work because areas with little content have lower error?
//the global pooling is a bad idea. Any min-error not necessarily the right choice.
//could do min(sum) rather than min( each )

public class ConvolutionalCompetitiveLearningExpt extends CreateEntityMain {

    public static void main( String[] args ) {
        ConvolutionalCompetitiveLearningExpt expt = new ConvolutionalCompetitiveLearningExpt();
        expt.mainImpl(args );
    }

// Big picture:
// - 3/  add prediction to help classification
// - 4/  add predictive coding (larger spike train output on prediction FN error)

    public void createEntities( Node n ) {

        // Dataset
//        String trainingPath = "/Users/gideon/Development/ProjectAGI/AGIEF/datasets/mnist/training-small";
//        String testingPath = "/Users/gideon/Development/ProjectAGI/AGIEF/datasets/mnist/training-small, /Users/gideon/Development/ProjectAGI/AGIEF/datasets/mnist/testing-small";

//        String trainingPath = "/home/dave/workspace/agi.io/data/mnist/1k_test";
//        String  testingPath = "/home/dave/workspace/agi.io/data/mnist/1k_test";

        String trainingPath = "/home/dave/workspace/agi.io/data/mnist/10k_train";
        String  testingPath = "/home/dave/workspace/agi.io/data/mnist/10k_train,/home/dave/workspace/agi.io/data/mnist/1k_test";

//        String trainingPath = "/home/dave/workspace/agi.io/data/mnist/cycle10";
//        String testingPath = "/home/dave/workspace/agi.io/data/mnist/cycle10";
//        String trainingPath = "/home/dave/workspace/agi.io/data/mnist/cycle3";
//        String testingPath = "/home/dave/workspace/agi.io/data/mnist/cycle3";

//        String trainingPath = "/Users/gideon/Development/ProjectAGI/AGIEF/datasets/mnist/training-small";
//        String testingPath = "/Users/gideon/Development/ProjectAGI/AGIEF/datasets/mnist/testing-small";

        // Parameters
        boolean logDuringTraining = false;
        boolean debug = false;
//        boolean logDuringTraining = false;
        boolean cacheAllData = true;
        boolean terminateByAge = false;
        int terminationAge = 1000;//50000;//25000;
//        int trainingEpochs = 250;//20; // = 5 * 10 images * 30 repeats = 1500      30*10*30 =
//        int trainingEpochs = 50;//20; // = 5 * 10 images * 30 repeats = 1500      30*10*30 =
        int trainingEpochs = 5;//20; // = 5 * 10 images * 30 repeats = 1500      30*10*30 =
        int testingEpochs = 1; // = 1 * 10 images * 30 repeats = 300

        // Entity names
        String experimentName           = Framework.GetEntityName( "experiment" );
        String imageLabelName           = Framework.GetEntityName( "image-class" );
        String vectorSeriesName         = Framework.GetEntityName( "feature-series" );
        String valueSeriesName          = Framework.GetEntityName( "label-series" );

        // Algorithm
        String convolutionalName = Framework.GetEntityName( "cl-cnn" );

        // Create entities
        String parentName = null;
        parentName = Framework.CreateEntity( experimentName, ExperimentEntity.ENTITY_TYPE, n.getName(), null ); // experiment is the root entity
        parentName = Framework.CreateEntity( imageLabelName, ImageLabelEntity.ENTITY_TYPE, n.getName(), parentName );

        parentName = Framework.CreateEntity( convolutionalName, CompetitiveLearningConvolutionalNetworkEntity.ENTITY_TYPE, n.getName(), parentName );

        parentName = Framework.CreateEntity( vectorSeriesName, VectorSeriesEntity.ENTITY_TYPE, n.getName(), parentName ); // 2nd, class region updates after first to get its feedback
        parentName = Framework.CreateEntity( valueSeriesName, ValueSeriesEntity.ENTITY_TYPE, n.getName(), parentName ); // 2nd, class region updates after first to get its feedback

        // Connect the entities' data
        Framework.SetDataReference( convolutionalName, CompetitiveLearningConvolutionalNetworkEntity.DATA_INPUT, imageLabelName, ImageLabelEntity.OUTPUT_IMAGE );

        ArrayList< AbstractPair< String, String > > featureDatas = new ArrayList<>();
        featureDatas.add( new AbstractPair<>( convolutionalName, CompetitiveLearningConvolutionalNetworkEntity.DATA_OUTPUT ) );
        Framework.SetDataReferences( vectorSeriesName, VectorSeriesEntity.INPUT, featureDatas ); // get current state from the region to be used to predict

        // Experiment config
        if( !terminateByAge ) {
            Framework.SetConfig( experimentName, "terminationEntityName", imageLabelName );
            Framework.SetConfig( experimentName, "terminationConfigPath", "terminate" );
            Framework.SetConfig( experimentName, "terminationAge", "-1" ); // wait for mnist to decide
        }
        else {
            Framework.SetConfig( experimentName, "terminationAge", String.valueOf( terminationAge ) ); // fixed steps
        }

        // cache all data for speed, when enabled
        Framework.SetConfig( experimentName, "cache", String.valueOf( cacheAllData ) );
        Framework.SetConfig( imageLabelName, "cache", String.valueOf( cacheAllData ) );
        Framework.SetConfig( convolutionalName, "cache", String.valueOf( cacheAllData ) );
        Framework.SetConfig( vectorSeriesName, "cache", String.valueOf( cacheAllData ) );
        Framework.SetConfig( valueSeriesName, "cache", String.valueOf( cacheAllData ) );

        // MNIST config
        String trainingEntities = convolutionalName;
        String testingEntities = "";
        if( logDuringTraining ) {
            trainingEntities += "," + vectorSeriesName + "," + valueSeriesName;
        }
        testingEntities = vectorSeriesName + "," + valueSeriesName;
        int imageRepeats = 1;
        SetImageLabelEntityConfig( imageLabelName, trainingPath, testingPath, trainingEpochs, testingEpochs, imageRepeats, trainingEntities, testingEntities );

        // Algorithm config
        int inputWidth = 28;
        int inputHeight = 28;
        int inputDepth = 1;

        SetConvolutionalEntityConfig(
                convolutionalName,
                inputWidth, inputHeight, inputDepth );

        // LOGGING config
        // NOTE about logging: We accumulate the labels and features for all images, but then we only append a new sample of (features,label) every N steps
        // This timing corresponds with the change from one image to another. In essence we allow the network to respond to the image for a few steps, while recording its output
        int accumulatePeriod = imageRepeats;
        int period = -1;
        VectorSeriesEntityConfig.Set( vectorSeriesName, accumulatePeriod, period, ModelData.ENCODING_SPARSE_BINARY );

        // Log image label for each set of features
        String valueSeriesInputEntityName = imageLabelName;
        String valueSeriesInputConfigPath = "imageLabel";
        String valueSeriesInputDataName = "";
        int inputDataOffset = 0;
        float accumulateFactor = 1f / imageRepeats;
        ValueSeriesEntityConfig.Set( valueSeriesName, accumulatePeriod, accumulateFactor, -1, period, valueSeriesInputEntityName, valueSeriesInputConfigPath, valueSeriesInputDataName, inputDataOffset );
        // LOGGING config

        // Debug the algorithm
        if( debug == false ) {
            return; // we're done
        }

    }

    protected static void SetImageLabelEntityConfig( String entityName, String trainingPath, String testingPath, int trainingEpochs, int testingEpochs, int repeats, String trainingEntities, String testingEntities ) {

        ImageLabelEntityConfig entityConfig = new ImageLabelEntityConfig();
        entityConfig.cache = true;
        entityConfig.receptiveField.receptiveFieldX = 0;
        entityConfig.receptiveField.receptiveFieldY = 0;
        entityConfig.receptiveField.receptiveFieldW = 28;
        entityConfig.receptiveField.receptiveFieldH = 28;
        entityConfig.resolution.resolutionX = 28;
        entityConfig.resolution.resolutionY = 28;

        entityConfig.greyscale = true;
        entityConfig.invert = true;
//        entityConfig.sourceType = BufferedImageSourceFactory.TYPE_IMAGE_FILES;
//        entityConfig.sourceFilesPrefix = "postproc";
        entityConfig.sourceFilesPathTraining = trainingPath;
        entityConfig.sourceFilesPathTesting = testingPath;
        entityConfig.trainingEpochs = trainingEpochs;
        entityConfig.testingEpochs = testingEpochs;
        entityConfig.trainingEntities = trainingEntities;
        entityConfig.testingEntities = testingEntities;
        entityConfig.resolution.resolutionY = 28;

        entityConfig.shuffleTraining = false;
        entityConfig.imageRepeats = repeats;

        Framework.SetConfig( entityName, entityConfig );
    }

    protected static void SetConvolutionalEntityConfig(
            String entityName,
            int inputWidth,
            int inputHeight,
            int inputDepth ) {

        CompetitiveLearningConvolutionalNetworkEntityConfig entityConfig = new CompetitiveLearningConvolutionalNetworkEntityConfig();

        entityConfig.cache = true;

        entityConfig.learningRate = 0.015f;
        entityConfig.learningRateNeighbours = entityConfig.learningRate * 0.2f;;
        entityConfig.noiseMagnitude = 0f;
        entityConfig.stressLearningRate = 0.005f;
        entityConfig.stressSplitLearningRate = 0.5f;
        entityConfig.stressThreshold = 0.01f;
        entityConfig.utilityLearningRate = 0;
        entityConfig.utilityThreshold = -1f;

// These are autoscaled to layer area inside the network config setup
//        entityConfig.edgeMaxAge = 750 * areaScale;//750;
//        entityConfig.growthInterval = 100 * areaScale;

        entityConfig.nbrLayers = 2;

//        int[] layerDepths = { 30,100 }; // from paper
//        int[] layerDepths = { 30,70 }; //
//        int[] layerDepths = { 40,70 }; //
        int[] layerDepths = { 40,120 }; //
//        int[] layerPoolingSize = { 2,2 }; // for classification in Z
//        int[] layerPoolingSize = { 2,8 }; // for classification in Z
        int[] layerPoolingSize = { 2,1 }; // for classification in Z
//        int[] layerPoolingSize = { 2,4 }; // for reconstruction, reduce pooling in 2nd layer
//        int[] layerPoolingSize = { 2,2 }; // for reconstruction, reduce pooling in 2nd layer
        int[] layerFieldSize = { 5,5 };
        int[] layerInputPaddings = { 0,0 };

//        entityConfig.nbrLayers = 3;
//
//        int[] layerDepths = { 30,100,200 }; // from paper
//        int[] layerPoolingSize = { 2,2,1 }; // for classification in Z
//        int[] layerFieldSize = { 5,5,4 };
//        int[] layerInputPaddings = { 0,0,0 };

        int iw = inputWidth;
        int ih = inputHeight;
        int id = inputDepth;

        // Generate config properties from these values:
        for( int layer = 0; layer < entityConfig.nbrLayers; ++layer ) {

            // Geometry of layer
            String prefix = "";
            if( layer > 0 ) prefix = ",";

            int layerInputPadding = layerInputPaddings[ layer ];
            int layerInputStride = 1;//layerInputStrides[ layer ];
            int ld = layerDepths[ layer ];
            int pw = layerPoolingSize[ layer ];
            int ph = pw;
            int fw = layerFieldSize[ layer ];
            int fh = fw;
            int fd = id;
            int lw = iw - fw +1;//layerWidths[ layer ];;
            int lh = ih - fh +1;//layerHeights[ layer ];;

            // Geometric parameters:
            entityConfig.layerInputPadding += prefix + layerInputPadding;
            entityConfig.layerInputStride  += prefix + layerInputStride;
            entityConfig.layerWidth  += prefix + lw;
            entityConfig.layerHeight += prefix + lh;
            entityConfig.layerDepth  += prefix + ld;
            entityConfig.layerfieldWidth += prefix + fw;
            entityConfig.layerfieldHeight += prefix + fh;
            entityConfig.layerfieldDepth += prefix + fd;
            entityConfig.layerPoolingWidth += prefix + pw;
            entityConfig.layerPoolingHeight += prefix + ph;

            // Auto calculate layer widths and heights
            iw = lw / pw;
            ih = lh / ph;
            id = ld;
        }

        Framework.SetConfig( entityName, entityConfig );
    }

    // Input 1: 28 x 28 (x2)
    // Window: 5x5, stride 2, padding = 0
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |
    //  F1 -- -- -- -- --                                                                      |
    //  F2          -- -- -- -- --                                                             |
    //  F3                   -- -- -- -- --                                                    |
    //  F4                            -- -- -- -- --                                           |
    //  F5                                     -- -- -- -- --                                  |
    //  F6                                              -- -- -- -- --                         |
    //  F7                                                       -- -- -- -- --
    //  F8                                                                -- -- -- -- --
    //  F9                                                                         -- -- -- -- xx

    // Max Pooling:
    // 0 1  2 3  4 5  6 7  8 *
    //  0    1    2    3    4
    // So output is 5x5

    // Input 1: 5 x 5 (x30)
    // Window: 5x5, stride 1, padding = 0
    //     00 01 02 03 04
    //  F1 -- -- -- -- --
    // Output is 1x1 by depth 100



    // Input 1: 28 x 28 (x2 in Z, for DoG + and -)
    // Window: 5x5, stride 1, padding = 0
    // iw - kw +1 = 28-5+1 = 24
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |
    //  F1 -- -- -- -- --
    //  F2    -- -- -- -- --
    //  F3       -- -- -- -- --
    //  F4          -- -- -- -- --
    //  F5             -- -- -- -- --
    //  F6                -- -- -- -- --
    //  F7                   -- -- -- -- --
    //  F8                      -- -- -- -- --
    //  F9                         -- -- -- -- --
    //  F10                           -- -- -- -- --
    //  F11                              -- -- -- -- --
    //  F12                                 -- -- -- -- --
    //  F13                                    -- -- -- -- --
    //  F14                                       -- -- -- -- --
    //  F15                                          -- -- -- -- --
    //  F16                                             -- -- -- -- --
    //  F17                                                -- -- -- -- --
    //  F18                                                   -- -- -- -- --
    //  F19                                                      -- -- -- -- --
    //  F20                                                         -- -- -- -- --
    //  F21                                                            -- -- -- -- --
    //  F22                                                               -- -- -- -- --
    //  F23                                                                  -- -- -- -- --
    //  F24                                                                     -- -- -- -- --
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |

    // Layer 1 pooling: 24x24 cells with 2x2 pooling brings it to 12x12 input to layer 2.

    // Conv layer 2: 12 inputs. Needs 8 cells.
    // iw - kw +1 = 12-5+1 = 8
    //     00 01 02 03 04 05 06 07 08 09 10 11 |
    //  F1 -- -- -- -- --                          input width = 5 * 2 = 10 + 2
    //  F2    -- -- -- -- --
    //  F3       -- -- -- -- --
    //  F4          -- -- -- -- --
    //  F5             -- -- -- -- --
    //  F6                -- -- -- -- --
    //  F7                   -- -- -- -- --
    //  F8                      -- -- -- -- --
    //     00 01 02 03 04 05 06 07 08 09 10 11 |

    // Max pooling layer 2: over all, so output 1x1 by depth.
    // Layer 2 pooling: 8x8 cells with 2x2 pooling brings it to 4x4 input to layer 3.

    // Conv layer 3: 4 inputs. Needs 1 cells.
    //     00 01 02 03 |
    //  F1 -- -- -- --
    //     00 01 02 03 |

    //   C1        P1    C2     i.e. C2 has a 14 pixel span, or about half the 28 pixel image
    //*00 |              _00
    // 01 | |
    //*02 |-|-----|_01   _01
    // 03 | |-----|
    //*04 | |            _02
    // 05   |
    //*06                _03
    // 07
    //*08 |              _04
    // 09 | |
    // 10 |-|-----|_04
    // 11 | |-----|
    // 12 | |
    // 13   |
    // 14
    // 15
    // 16

//    FIELD    LAYER   DEPTH POOL  OUTPUT
//    3x3      26x26   8     2x2   13x13 x8
//    3x3      11x11   64    2x2   6x6   x64 = 2304
//
//    FIELD    LAYER   DEPTH POOL  OUTPUT
//    6x6      8x8     50    2x2   4x4   x 50   = 800


    // 6x6 field
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |
    //  F1 -- -- -- -- -- --
    //  F2          -- -- -- -- -- --
    //  F3                   -- -- -- -- -- --
    //  F4                            -- -- -- -- -- --
    //  F5                                     -- -- -- -- -- --
    //  F6                                              -- -- -- -- -- --
    //  F7                                                       -- -- -- -- -- --
    //  F8                                                                -- -- -- -- -- --


    // Layer 1
    // 3x3 field 26x26 2x2 13x13
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |
    //  F1 -- -- --
    //  F2    -- -- --
    //  F3       -- -- --
    //  F4          -- -- --
    //  F5             -- -- --
    //  F6                -- -- --
    //  F7                   -- -- --
    //  F8                      -- -- --
    //  F9                         -- -- --
    //  F10                           -- -- --
    //  F11                              -- -- --
    //  F12                                 -- -- --
    //  F13                                    -- -- --
    //  F14                                       -- -- --
    //  F15                                          -- -- --
    //  F16                                             -- -- --
    //  F17                                                -- -- --
    //  F18                                                   -- -- --
    //  F19                                                      -- -- --
    //  F20                                                         -- -- --
    //  F21                                                            -- -- --
    //  F22                                                               -- -- --
    //  F23                                                                  -- -- --
    //  F24                                                                     -- -- --
    //  F25                                                                        -- -- --
    //  F26                                                                           -- -- --
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 13 14 15 16 17 18 19 20 21 22 23 24 25 26 27 |

    // Layer 2
    // 3x3 field input 13x13 layer 11x11 2x2    output 6x6
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 |
    //  F1 -- -- --
    //  F2    -- -- --
    //  F3       -- -- --
    //  F4          -- -- --
    //  F5             -- -- --
    //  F6                -- -- --
    //  F7                   -- -- --
    //  F8                      -- -- --
    //  F9                         -- -- --
    //  F10                           -- -- --
    //  F11                              -- -- --
    //     00 01 02 03 04 05 06 07 08 09 10 11 12 |

}




