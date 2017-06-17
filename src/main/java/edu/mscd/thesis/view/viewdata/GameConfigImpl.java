package edu.mscd.thesis.view.viewdata;

import edu.mscd.thesis.util.Util;

public class GameConfigImpl extends AbstractConfigData implements GameConfig {

	private boolean isPaused;
	private boolean isStep;
	private double speed;
	private AiMode aiMode;
	private DocumentMode documentMode;
	private int interval;

	public GameConfigImpl() {
		this.speed = 0.5;
		this.isStep = false;
		this.isPaused = true;
		this.aiMode = AiMode.ON_FOLLOW;
		this.documentMode = DocumentMode.EVERY_MOVE;
		this.interval = 10;
	}

	@Override
	public boolean isPaused() {
		return this.isPaused;
	}

	@Override
	public boolean isStep() {
		return this.isStep;
	}

	@Override
	public double getSpeed() {
		return this.speed;
	}

	@Override
	public AiMode getAiMode() {
		return this.aiMode;
	}

	public void setPaused(boolean isPaused) {
		this.isPaused = isPaused;
	}

	public void setStep(boolean isStep) {
		this.isStep = isStep;
	}

	public void setSpeed(double speed) {
		if (speed > 0.0 && speed <= 1.0) {
			this.speed = speed;
		}
	}

	public void setAiMode(AiMode aiMode) {
		this.aiMode = aiMode;
	}

	public void setDocumentMode(DocumentMode docMode) {
		this.documentMode = docMode;
	}
	
	public void setInterval(int interval){
		this.interval = (int) Util.boundValue(interval, 1, 100);
	}

	@Override
	public GameConfig copy() {
		GameConfigImpl g = new GameConfigImpl();
		g.setPaused(this.isPaused());
		g.setSpeed(this.getSpeed());
		g.setStep(this.isStep());
		g.setAiMode(this.getAiMode());
		g.setDocumentMode(this.getDocumentMode());
		g.setInterval(this.getInterval());
		return g;
	}

	@Override
	public int getInterval() {
		return this.interval;
	}

	@Override
	public DocumentMode getDocumentMode() {
		return this.documentMode;
	}

	@Override
	public boolean isGameConfig() {
		return true;
	}

	@Override
	public boolean isAiConfig() {
		return false;
	}

	@Override
	public GameConfig getGameConfiguration() {
		return this;
	}

	@Override
	public AiConfig getAiConfig() {
		return null;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.getClass().getName());
		sb.append("{");
		sb.append("Paused?:");
		sb.append(this.isPaused);
		sb.append(" speed:");
		sb.append(this.getSpeed());
		sb.append(" step?:");
		sb.append(this.isStep());
		sb.append(" AIMode:");
		sb.append(this.getAiMode());
		sb.append(" docMode:");
		sb.append(this.getDocumentMode());
		sb.append(" int:");
		sb.append(this.getInterval());
		sb.append("}");
		return sb.toString();
	}

}
