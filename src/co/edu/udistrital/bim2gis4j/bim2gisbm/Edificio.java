package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Edificio {
	
	private String id;
	private List<Piso> pisos;
	
	public List<Piso> getPisos() {
		return pisos;
	}

	public void setPisos(List<Piso> pisos) {
		this.pisos = pisos;
	}

	public Edificio(){
		//pisos = new ArrayList();
	}

	
	public Piso buscarPiso(String id){
		
		Piso r = null;
		
		for (Piso pisoActual : getPisos()) {
			
			if ( pisoActual.getId().equals(id)){
				r = pisoActual;
				break;
			}
			
		}
		
		return r;
		
	}
}
