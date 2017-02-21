package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.World;
import javafx.scene.canvas.GraphicsContext;

public class ModelRenderer implements Renderer<Model>{
	private Renderer<World> worldRenderer;
	private RenderMode renderMode;
	public ModelRenderer(RenderMode mode){
		this.worldRenderer = new WorldRenderer(mode);
		this.renderMode = mode;
	}

	@Override
	public void draw(Model model, GraphicsContext g) {
		worldRenderer.draw(model.getWorld(), g);
		
	}

	@Override
	public void changeMode(RenderMode mode) {
		this.renderMode = mode;
		this.worldRenderer.changeMode(mode);
	}

}
