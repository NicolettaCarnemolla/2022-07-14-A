package it.polito.tdp.nyc.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.nyc.db.NYCDao;

public class Model {
	
	NYCDao dao =  new NYCDao();
	private List<NTA> nta;
	private Graph<NTA,DefaultWeightedEdge> grafo;
	
	public List<String> getallBorough(){
		return dao.getAllBorough();
	}
	
	public void creaGrafo(String borough) {
		this.nta = dao.getAllNTAfromBorough(borough);
		//creo il grafo:
		grafo = new SimpleWeightedGraph<>(DefaultWeightedEdge.class);
		//Inserisco tutti i vertici:
		Graphs.addAllVertices(grafo, nta);
		
		for(NTA nta1 : nta) {
			for(NTA nta2 : nta) {
				if(!nta1.equals(nta2)) {
					Set<String> setun = new HashSet<>(nta1.getSSID());
					setun.addAll(nta2.getSSID());
					Graphs.addEdge(grafo, nta1, nta2,setun.size());
 				}
			}
		}
		
		
	}
	
	public List<Archi> AnalizzaArchi() {
		//calcolo il peso medio di tutti gli archi:
		double mediapeso = 0.0;
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			mediapeso += grafo.getEdgeWeight(e);
		}
		mediapeso = mediapeso/grafo.edgeSet().size();
		
		//Verifico che il peso degli archi sia maggiore del peso medio appena calcolato e lo inserisco in una lista da stampare:
		List<Archi> result = new ArrayList<>();
		for(DefaultWeightedEdge e : grafo.edgeSet()) {
			if(this.grafo.getEdgeWeight(e) > mediapeso) {
				result.add(new Archi(this.grafo.getEdgeSource(e).getNTACode(),this.grafo.getEdgeTarget(e).getNTACode(),(int) this.grafo.getEdgeWeight(e)));
				
			}
		}
		Collections.sort(result);
		return result;
	}
}
