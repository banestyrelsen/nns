package com.stk.nns.nn;

import java.time.Instant;
import java.util.Arrays;
import java.util.Random;

public class Network {

    public final int[] NETWORK_LAYER_SIZES;
    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;
    public final int NETWORK_SIZE;

    protected double[][] output;  // [layer][neuron]
    protected double[][][] weight; // [layer][currentNeuron][connectedNeuronInPreviousLayer]
    protected double[][] bias; // [layer][neuron]

    protected double[][] error_signal;
    protected double[][] output_derivative;

    public Network(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE-1];

        this.output = new double[NETWORK_SIZE][];
        this.weight = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];
        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for (int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.bias[i] = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i], 0.3, 0.7);

            // Create weights array for every layer except the first (input) layer
            if (i > 0) {
                this.weight[i]  = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i],NETWORK_LAYER_SIZES[i-1], -0.3,0.5);
            }
        }
    }

    // Recombination
    public Network (Network a, Network b) {
        Random random = new Random();

        this.NETWORK_LAYER_SIZES = a.NETWORK_LAYER_SIZES;
        this.NETWORK_SIZE = a.NETWORK_LAYER_SIZES.length;
        this.INPUT_SIZE = a.NETWORK_LAYER_SIZES[0];
        this.OUTPUT_SIZE = a.NETWORK_LAYER_SIZES[NETWORK_SIZE-1];

        this.output = new double[NETWORK_SIZE][];
        this.weight = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];
        this.error_signal = new double[NETWORK_SIZE][];
        this.output_derivative = new double[NETWORK_SIZE][];

        for (int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.error_signal[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.output_derivative[i] = new double[NETWORK_LAYER_SIZES[i]];

            this.bias[i] = new double[a.bias[i].length];
            for (int biasIndex = 0; biasIndex < bias[i].length; biasIndex++) {
                bias[i][biasIndex] = random.nextBoolean() ? a.bias[i][biasIndex] : b.bias[i][biasIndex];
                if (random.nextFloat() > 0.03f) {
                    System.out.println("BIAS MUTATION!");
                    bias[i][biasIndex] = random.nextFloat();
                }
            }

            // Create weights array for every layer except the first (input) layer
            if (i > 0) {
                this.weight[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i-1]];
                for(int currentNeuron = 0; currentNeuron < NETWORK_LAYER_SIZES[i]; currentNeuron++){
                    for(int connectedNeuronInPreviousLayer = 0; connectedNeuronInPreviousLayer < NETWORK_LAYER_SIZES[i-1]; connectedNeuronInPreviousLayer++){
                        weight[i][currentNeuron][connectedNeuronInPreviousLayer] = random.nextBoolean() ?
                                a.weight[i][currentNeuron][connectedNeuronInPreviousLayer] :
                                b.weight[i][currentNeuron][connectedNeuronInPreviousLayer];
                        if (random.nextFloat() > 0.03f) {
                            System.out.println("WEIGHT MUTATION!");
                            weight[i][currentNeuron][connectedNeuronInPreviousLayer] = random.nextFloat();
                        }
                    }
                }


                this.weight[i]  = NetworkTools.createRandomArray(NETWORK_LAYER_SIZES[i],NETWORK_LAYER_SIZES[i-1], -0.3,0.5);
            }
        }
    }




    public double[] calculate(double... input) {
        if (input.length != this.INPUT_SIZE) {
            throw new IllegalStateException(String.format("input.length %s != this.INPUT_SIZE %s", input.length, this.INPUT_SIZE));
        }
        this.output[0] = input;

        for (int layer = 1; layer < NETWORK_SIZE; layer++) {
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                // Calculate sum
                double sum = bias[layer][neuron];
                for (int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer-1]; prevNeuron++) {
                    sum += output[layer-1][prevNeuron] * weight[layer][neuron][prevNeuron];
                }
                // Apply activation function
                output[layer][neuron] = sigmoid(sum);
                output_derivative[layer][neuron] = (output[layer][neuron] * (1 -output[layer][neuron] ));
            }
        }
        return output[NETWORK_SIZE-1];
    }

    private double sigmoid(double x) {
        return 1d/ (1 + Math.exp(-x));
    }

    public void train(double[] input, double[] target, double learningRate) {
        if (input.length != INPUT_SIZE || target.length != OUTPUT_SIZE) {
            throw new IllegalStateException(String.format("input.length %s != this.INPUT_SIZE %s", input.length, this.INPUT_SIZE));
        }
        calculate(input);
        backpropError(target);
        updateWeights(learningRate);
    }

    public void backpropError(double[] target) {
        for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[NETWORK_SIZE-1]; neuron++) {
            error_signal[NETWORK_SIZE-1][neuron] = (output[NETWORK_SIZE-1][neuron] - target[neuron])
                    * output_derivative[NETWORK_SIZE-1][neuron];
        }
        for (int layer = NETWORK_SIZE-2; layer > 0; layer--) {
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                double sum = 0;
                for (int nextNeuron = 0; nextNeuron < NETWORK_LAYER_SIZES[layer+1]; nextNeuron++) {
                    sum += weight[layer+1][nextNeuron][neuron] * error_signal[layer+1][nextNeuron];
                }
                this.error_signal[layer][neuron] = sum * output_derivative[layer][neuron];
            }
        }
    }

    public void updateWeights(double learningRate) {
        for (int layer = 1; layer < NETWORK_SIZE; layer++) {
            for (int neuron = 0; neuron < NETWORK_LAYER_SIZES[layer]; neuron++) {
                double delta = -learningRate * error_signal[layer][neuron];
                bias[layer][neuron] += delta;
                for (int prevNeuron = 0; prevNeuron < NETWORK_LAYER_SIZES[layer-1]; prevNeuron++) {
                    weight[layer][neuron][prevNeuron] += delta * output[layer-1][prevNeuron];
                }

            }
        }
    }

    public static void main(String[] args) {
        Network a = new Network(1024,16,16,4);
        Network b = new Network(1024,16,16,4);
        Network c = new Network(a, b);

        Network net  = c;


/*        Network net = new Network(1024,16,16,4);*/

        double[] input = new double[1024];
        Random rnd = new Random();
        for (int i = 0; i < 1024; i++) {
            input[i] = rnd.nextFloat();
        }


        double[] target = new double[]{0,1,0.5,1};
        Instant before;
        for (int i = 0; i < 1000; i++) {
            net.train(input, target, 0.3);
        }

        double[] o = net.calculate(input);
        System.out.println(Arrays.toString(o));

    }
}

