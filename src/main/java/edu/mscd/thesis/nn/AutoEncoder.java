package edu.mscd.thesis.nn;

import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.layers.Layer;


public class AutoEncoder extends BasicNetwork {


    public AutoEncoder(int inputSize, int codeSize, int layers) {
        super();
        int difference = inputSize-codeSize;
        int stepSize = difference/layers;
        this.addLayer(new BasicLayer(null, true, inputSize));
        for(int i=-layers; i<layers; i++){
        	this.addLayer(new BasicLayer(new ActivationSigmoid(), true, codeSize+(Math.abs(i)*stepSize)));
        }
        this.addLayer(new BasicLayer(new ActivationSigmoid(), false, inputSize));
        this.getStructure().finalizeStructure();
        this.reset();
    }
    
    @Override
    public void addLayer(Layer layer){
    	System.out.print("Adding layer of size:");
    	System.out.println(layer.getNeuronCount());
    	super.addLayer(layer);
    }

}