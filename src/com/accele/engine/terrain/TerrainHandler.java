package com.accele.engine.terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.accele.engine.core.Renderable;
import com.accele.engine.core.Tickable;
import com.accele.engine.gfx.Graphics;

public class TerrainHandler implements Tickable, Renderable {

	private List<Terrain> terrains;
	
	public TerrainHandler() {
		this.terrains = new ArrayList<>();
	}
	
	@Override
	public void onUpdate() {
		terrains.forEach(a -> a.onUpdate());
	}
	
	@Override
	public void onRender(Graphics g) {
		terrains.forEach(a -> a.onRender(g));
	}
	
	public void addTerrain(Terrain t) {
		terrains.add(t);
	}
	
	public void removeTerrain(Terrain t) {
		terrains.remove(t);
	}
	
	public Terrain getFirstTerrainByID(String localizedID) {
		return terrains.stream().filter(p -> p.localizedID.equals(localizedID)).findFirst().orElseGet(() -> null);
	}
	
	public Terrain[] getAllTerrainsByID(String localizedID) {
		List<Terrain> tmp = terrains.stream().filter(p -> p.localizedID.equals(localizedID)).collect(Collectors.toList());
		return tmp.toArray(new Terrain[tmp.size()]);
	}
	
	public Terrain[] getTerrains() {
		return terrains.toArray(new Terrain[terrains.size()]);
	}
	
}
