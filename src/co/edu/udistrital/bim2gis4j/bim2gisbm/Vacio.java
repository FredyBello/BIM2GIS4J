package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import co.edu.udistrital.bim2gis4j.ifc.Solido;
import co.edu.udistrital.bim2gis4j.util.Transformador;

public class Vacio extends Solido{
	
	Muro muroAlQueVacia = null;

	public Muro getMuroAlQueVacia() {
		return muroAlQueVacia;
	}

	public void setMuroAlQueVacia(Muro muroAlQueVacia) {
		this.muroAlQueVacia = muroAlQueVacia;
	}

	public void rotarCoordenadasVacio(){
		
		if(coordenadasAbsolutas !=null){
			//inicialmente se rotan las coordenadas absolutas
			//esto simplemente sirve para hacer que coincidan con la ubicacion final de las caras
			//las caras ya estaban calculadas anteriormente con base en las coordenadas absolutas sin rotar
			List absolutasOriginales = this.coordenadasAbsolutas;
			List absolutasRotadas = new ArrayList();
			
			for (Coordenada coordenadaActual : coordenadasAbsolutas) {
				
				Coordenada rotada = Transformador.rotarCoordenadaVacio(coordenadaActual, this);
				absolutasRotadas.add(rotada);
				
			}
			
			this.setCoordenadasAbsolutas(absolutasRotadas);
			
			
			//posteriormente se rota cada una de las coordenadas que somponen las caras del vacio

			List<Poligono> carasOriginales = this.getCaras();
			List<Poligono> carasRotadas = new ArrayList();
			
			for (Poligono caraActual : carasOriginales) {
				
				Poligono caraRotada = new Poligono();
				
				for (Coordenada coordenadaActual : caraActual.getCoordenadas()) {
					
					Coordenada rotada = Transformador.rotarCoordenadaVacio(coordenadaActual, this);
					
					caraRotada.getCoordenadas().add(rotada);
					
				}
				
				carasRotadas.add(caraRotada);
			}
			
			this.setCaras(carasRotadas);
			
		}
	}
}
