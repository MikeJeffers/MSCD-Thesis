package edu.mscd.thesis.view;

import edu.mscd.thesis.model.Model;
import edu.mscd.thesis.model.World;
import javafx.scene.canvas.GraphicsContext;

public class ModelRenderer implements Renderer<Model>{
	private Renderer<World> worldRenderer;
	public ModelRenderer(){
		this.worldRenderer = new WorldRenderer();
	}

	@Override
	public void draw(Model model, GraphicsContext g) {
		worldRenderer.draw(model.getWorld(), g);
		
	}

}
