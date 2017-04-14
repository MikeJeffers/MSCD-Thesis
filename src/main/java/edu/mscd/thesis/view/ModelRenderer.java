package edu.mscd.thesis.view;

import edu.mscd.thesis.controller.CityData;
import edu.mscd.thesis.controller.UserData;
import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.World;
import javafx.scene.canvas.GraphicsContext;

public class ModelRenderer implements Renderer<Model<UserData, CityData>>, SpatialDataRender<Double>{
	private WorldRenderer worldRenderer;
	public ModelRenderer(RenderMode mode){
		this.worldRenderer = new WorldRenderer(mode);
	}

	@Override
	public void draw(Model<UserData, CityData> model, GraphicsContext g) {
		worldRenderer.draw(model.getWorld(), g);
	}

	@Override
	public void changeMode(RenderMode mode) {
		this.worldRenderer.changeMode(mode);
	}

	@Override
	public void setData(Double[] data) {
		this.worldRenderer.setData(data);
		
	}



}
