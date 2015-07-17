package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanClippingResult;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcBooleanOperand;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcExtrudedAreaSolid;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcRepresentation;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcSlab;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcWallStandardCase;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.LIST;

import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import co.edu.udistrital.bim2gis4j.ifc.Solido;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Polygon;

public class Muro  extends Solido {
	
	List<Vacio> vacios = null;

	public List<Vacio> getVacios() {
		return vacios;
	}


	public void setVacios(List<Vacio> vacios) {
		this.vacios = vacios;
	}


	public void abrirVaciosParaPuertasYVentanas() {

		List<Poligono> carasNuevas = new ArrayList();
		List<Poligono> carasTijera = null;
		
		
		//en este loop se determinan las caras de los vacios que cortan a las caras de los muros
		//una vez determinadas se procede a recortar la nueva silueta de cada cara de los muros
		//Las caras que cortan un muro son las que tiene un borde que coincide con el borde del muro
		for (Poligono caraMuroActual : this.getCaras()) {
			
			carasTijera = new ArrayList();
			
			for (Vacio vacioActual : this.getVacios()) {
				for (Poligono caraVacioActual : vacioActual.getCaras()) {
					
					//las puertas alteran la silueta del muro
					if(vacioActual.getTipo()!=null && vacioActual.getTipo().equals("puerta")){
						
						if(caraMuroActual.compartePlanoCon(caraVacioActual)){
							
							if(caraMuroActual.comparteBordeCon(caraVacioActual)){
								
								caraVacioActual.setVacioPadre(vacioActual);
								carasTijera.add(caraVacioActual);
								caraVacioActual.setAdicional(false);
								
							}
						}
					}
				}
			}
			
			Poligono siluetaMuroRecortada = new Poligono();
			siluetaMuroRecortada.getCoordenadas().addAll(caraMuroActual.getCoordenadas());
			boolean hay2NuevasCaras = false;
			Poligono nuevaCara01 = null;
			Poligono nuevaCara02 = null;
			
			//if(this.getId().equals("3PfS__Y_DBAfq5naM6zD2Z")){
				//System.err.println("Cara muro original = " + siluetaMuroRecortada);
				for (Poligono tijeraActual : carasTijera) {
					
					//System.err.println("Tijera " + tijeraActual);
					
					List<Poligono> silueta = siluetaMuroRecortada.diferencia(tijeraActual);
					if(silueta!=null && silueta.size()==1){
						siluetaMuroRecortada = silueta.get(0);
					}
					
					if(silueta!=null && silueta.size()==2){
						hay2NuevasCaras = true;
						nuevaCara01 = silueta.get(0);
						nuevaCara02 = silueta.get(1);
					}

					
					//System.err.println("Resultado = " + siluetaMuroRecortada);
				}
				
			//}
			
			if(hay2NuevasCaras){
				nuevaCara01.setCarasTijera(carasTijera);
				carasNuevas.add(nuevaCara01);
				carasNuevas.add(nuevaCara02);
			}else{
				siluetaMuroRecortada.setCarasTijera(carasTijera);
				carasNuevas.add(siluetaMuroRecortada);
			}			
		}

		this.setCaras(carasNuevas);
		
		//a las caras ya recortadas se les asigna sus caras internas
		//que son los huecos que genera una ventana, estos huecos no alteran la silueta del muro solo su interior
		for (Vacio vacioActual : this.getVacios()) {
			for (Poligono caraVacioActual : vacioActual.getCaras()) {
				for (Poligono caraMuroActual : this.getCaras()) {
					
					//las ventanas generan caras internas y caras adicionales pero no alteran la silueta
					if(vacioActual.getTipo()!=null && vacioActual.getTipo().equals("ventana")){
						
						if(caraMuroActual.compartePlanoCon(caraVacioActual)){
							
							caraVacioActual.setVacioPadre(vacioActual);
							caraVacioActual.setInterno(true);
							caraMuroActual.getCarasInternas().add(caraVacioActual);
							caraVacioActual.setAdicional(false);
							
						}
					}
					
					
				}
			}
		}
		
		
		for (Vacio vacioActual : this.getVacios()) {
			for (Poligono caraVacioActual : vacioActual.getCaras()) {
				
				if(vacioActual.getTipo()!=null && (vacioActual.getTipo().equals("ventana") || vacioActual.getTipo().equals("puerta"))){
					
					if(caraVacioActual.getAdicional()==true){
						
						this.getCaras().add(caraVacioActual);
						
					}
				}
			}
		}
		
		
	}
	
}
