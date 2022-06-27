package it.polito.tdp.food.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.food.db.FoodDao;

public class Model {
	
	private FoodDao dao;
	private Graph<Food, DefaultWeightedEdge> grafo;
	private Map<Integer, Food> idMap;
	
	public Model() {
		dao = new FoodDao();
		idMap = new HashMap<>();
		
		dao.listAllFoods(idMap);
		
	}
	
	public void creaGrafo(int min) {
		
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		
		Graphs.addAllVertices(this.grafo, dao.getVertexes(min, idMap));
		
		for(Food f1: grafo.vertexSet()) {
			for(Food f2: grafo.vertexSet()) {
				if(!f1.equals(f2) && f1.getFood_code()<f2.getFood_code()) {
					Double peso = dao.calorieCongiunte(f1, f2) ;
					if(peso!=null) {
						Graphs.addEdge(this.grafo, f1, f2, peso) ;
					}
				}
			}
			
		}

		
	}
	
	public Set<Food> getVertexes(){
		return this.grafo.vertexSet();
	}
	
	public List<Food> getMax(Food scelto) {
		
		List<Food> vicini = Graphs.neighborListOf(grafo, scelto);
		
		List<Edge> result = new LinkedList<>();
		List<Food> resultFood = new LinkedList<>();

		
		for(Food fi: vicini) {
			result.add(new Edge(scelto, fi, grafo.getEdgeWeight(grafo.getEdge(fi, scelto))));
		}
		
		Collections.sort(result, new Comparatore());
		
		for(int i=0;i<5;i++) {
			resultFood.add(result.get(i).getF2());
		}
		
		return resultFood;
		
	}
	
	public int getNArchi() {
		return this.grafo.edgeSet().size();
	}
	
	public int getNVertici() {
		return this.grafo.vertexSet().size();
	}

}
