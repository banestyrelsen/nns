package com.stk.nns.nn;

import java.util.Arrays;

public class Network {

    public final int[] NETWORK_LAYER_SIZES;
    public final int INPUT_SIZE;
    public final int OUTPUT_SIZE;
    public final int NETWORK_SIZE;

    private double[][] output;  // [layer][neuron]
    private double[][][] weight; // [layer][currentNeuron][connectedNeuronInPreviousLayer]
    private double[][] bias; // [layer][neuron]

    public Network(int... NETWORK_LAYER_SIZES) {
        this.NETWORK_LAYER_SIZES = NETWORK_LAYER_SIZES;
        this.NETWORK_SIZE = NETWORK_LAYER_SIZES.length;
        this.INPUT_SIZE = NETWORK_LAYER_SIZES[0];
        this.OUTPUT_SIZE = NETWORK_LAYER_SIZES[NETWORK_SIZE-1];

        this.output = new double[NETWORK_SIZE][];
        this.weight = new double[NETWORK_SIZE][][];
        this.bias = new double[NETWORK_SIZE][];

        for (int i = 0; i < NETWORK_SIZE; i++) {
            this.output[i] = new double[NETWORK_LAYER_SIZES[i]];
            this.bias[i] = new double[NETWORK_LAYER_SIZES[i]];

            // Create weights array for every layer except the first (input) layer
            if (i > 0) {
                this.weight[i] = new double[NETWORK_LAYER_SIZES[i]][NETWORK_LAYER_SIZES[i-1]];
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
            }
        }
        return output[NETWORK_SIZE-1];
    }

    private double sigmoid(double x) {
        return 1d/ (1 + Math.exp(-x));
    }

    public static void main(String[] args) {
        Network net = new Network(4,1,3,4);
        double[] output = net.calculate(.54,.132,.78,.87);
        System.out.println(Arrays.toString(output));
    }
}

