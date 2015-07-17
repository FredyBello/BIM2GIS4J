package co.edu.udistrital.bim2gis4j.bim2gisbm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.geometry.euclidean.threed.Line;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import org.apache.commons.math3.geometry.euclidean.threed.PolyhedronsSet;
import org.apache.commons.math3.geometry.euclidean.threed.Rotation;
import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.partitioning.Region;
import org.citygml4j.factory.geometry.DimensionMismatchException;
import org.citygml4j.factory.geometry.GMLGeometryFactory;
import org.citygml4j.impl.gml.geometry.primitives.InteriorImpl;
import org.citygml4j.model.gml.geometry.primitives.Interior;
import org.citygml4j.model.gml.geometry.primitives.Polygon;
import co.edu.udistrital.bim2gis4j.ifc.PlanoDeCorte;
import co.edu.udistrital.bim2gis4j.util.ComparadorAngulos;

import com.vividsolutions.jts.algorithm.CentroidPoint;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;

public class Poligono {
	
	private List<Coordenada> coordenadas;
	
	private boolean interno = false;
	
	//por defecto todas las caras de un vacio son adicionales
	//a menos que compartan plano con alguna cara del muro
	private boolean adicional = true;
	
	GMLGeometryFactory geom = new GMLGeometryFactory();
	
	private List<Poligono> carasInternas;
	
	private List<Poligono> carasTijera;
	
	private org.citygml4j.model.gml.geometry.primitives.Polygon poligonoGML = null;
	
	private Vacio vacioPadre = null;

	
	public List<Poligono> getCarasTijera() {
		return carasTijera;
	}

	public void setCarasTijera(List<Poligono> carasTijera) {
		this.carasTijera = carasTijera;
	}
	
	public Vacio getVacioPadre() {
		return vacioPadre;
	}

	public void setVacioPadre(Vacio vacioPadre) {
		this.vacioPadre = vacioPadre;
	}

	public boolean getAdicional() {
		return adicional;
	}

	public void setAdicional(boolean adicional) {
		this.adicional = adicional;
	}

	public org.citygml4j.model.gml.geometry.primitives.Polygon getPoligonoGML() throws DimensionMismatchException {
		
		if(poligonoGML==null){
			calcularPolygonGML();
		}
			
		return poligonoGML;
	}

	public void setPoligonoGML(
			org.citygml4j.model.gml.geometry.primitives.Polygon poligonoGML) {
		this.poligonoGML = poligonoGML;
	}

	public List<Poligono> getCarasInternas() {
		return carasInternas;
	}

	public void setCarasInternas(List<Poligono> carasInternas) {
		this.carasInternas = carasInternas;
	}

	public boolean getInterno() {
		return interno;
	}

	public void setInterno(boolean interno) {
		this.interno = interno;
	}

	public Poligono(){
		coordenadas = new ArrayList();
		carasInternas = new ArrayList();
		carasTijera = new ArrayList();
	}
	
	public Poligono(List<Coordenada> coord){
		
		//se clonan las coordenadas
		coordenadas = new ArrayList<Coordenada>(coord);
	}
	
	public List<Coordenada> getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(List<Coordenada> coordenadas) {
		this.coordenadas = coordenadas;
	}
	
	@Override 
	public String toString(){
		
		
		String cadena = " Polygon[";
		
		Iterator<Coordenada> i = this.getCoordenadas().iterator();
		while(i.hasNext()) {
			
			Coordenada coordenadaActual = i.next();
			cadena += "(" + coordenadaActual.getX() + ", " + coordenadaActual.getY() + ", " + coordenadaActual.getZ() + ")" ;
			
			if(i.hasNext()){
				
				cadena += " ,";
				
			}
			
		}
		
		cadena += "]"; 
			
		return cadena;
		
	}
	
	public boolean compartePlanoCon(Poligono poligonoEvaluado){
		
		//se crea un plano que contiene las 3 primeras coordenadas del poligono
		
		Plane planoActual = this.toPlane();
		Plane planoEvaluado = poligonoEvaluado.toPlane();
		try {
			return planoActual.isSimilarTo(planoEvaluado);
			
		} catch (Exception e) {
			return false;
		}
		
	}
	
	public boolean comparteBordeCon(Poligono poligonoEvaluado){
		
		Iterator<Coordenada> i = coordenadas.iterator();
		Iterator<Coordenada> t = coordenadas.iterator();
		t.next();
		boolean contienePunto = false;
		
		while(contienePunto==false && t.hasNext()) {
			
			Line linea = null;
			Vector3D a = null;
			Vector3D b = null;
			
			try {
			
				a = i.next().toVector3D();
				b = t.next().toVector3D();
				
				linea = new Line(a,b);
				
			} catch (Exception e) {
				
				//esto sucede normalmente cuando los 2 puntos son iguales (hay redundancia en el poligono)
				System.err.println("ERROR AL CREAR LINEA desde " + a + " hasta " + b);

			}
			
			if(linea!=null){
				
				Iterator<Coordenada> s = poligonoEvaluado.coordenadas.iterator();
				
				while(s.hasNext()){
					
					if(linea.contains(s.next().toVector3D())){
						
						contienePunto = true;
						break;
						
					}
					
				}
				
			}
			
		}
		
		return contienePunto;
	}
	
	public Poligono cortar(/*Poligono caraActual,*/ PlanoDeCorte pPlano /*, PolyhedronsSet cajaFrontera*/){
		
		
		Plane planoDeCorte = pPlano.getPlanoApache();
		
		List<Coordenada> coordenadasDeCorte = new ArrayList();
		
		
		Vector3D normal = planoDeCorte.getNormal();
		
		Poligono caraCortada = new Poligono();
		
		Iterator<Coordenada> iA = this.getCoordenadas().iterator();
		Iterator<Coordenada> iB = this.getCoordenadas().iterator();
		
		iB.next();
		
		PolyhedronsSet planoComoSolido = planoDeCorte.wholeSpace();
		
		boolean agregarA = true;
		
		//System.err.println("ORIGEN = " + new Coordenada(planoEvaluado.getOrigin()) );
		
		
		
		while(iB.hasNext()) {
			
			Coordenada coordenadaA = iA.next();
			Coordenada coordenadaB = iB.next();
			

			boolean ultimaCoordenadaB = !iB.hasNext();
			
			double posicionA = planoDeCorte.getOffset(coordenadaA.toVector3D());
			double posicionB = planoDeCorte.getOffset(coordenadaB.toVector3D());
			
		    // si un poligono contiene dos coordenadas contiguas exactamente iguales
			// se pasa a las siguientes coordenadas porque si son iguales no puede existir una linea
			if(coordenadaA.equals(coordenadaB)){
				
				coordenadaA = iA.next();
				coordenadaB = iB.next();
				

				ultimaCoordenadaB = !iB.hasNext();
				
				posicionA = planoDeCorte.getOffset(coordenadaA.toVector3D());
				posicionB = planoDeCorte.getOffset(coordenadaB.toVector3D());

			}
			
			Line lineaDeAHastaB = new Line(coordenadaA.toVector3D(), coordenadaB.toVector3D());
			
			Vector3D interseccion = planoDeCorte.intersection(lineaDeAHastaB);
			
			/*
			Boolean interseccionDentroDeCajaFrontera = null;
			
			if( interseccion != null){
				if(cajaFrontera.checkPoint(interseccion)!= Region.Location.OUTSIDE){
					//la interseccion de la linea con el plano esta DENTRO de la caja frontera del solido
					interseccionDentroDeCajaFrontera = true;
				}else{
					//la interseccion de la linea con el plano esta FUERA de la caja frontera del solido
					interseccionDentroDeCajaFrontera = false;
				}
			}else{
				//la interseccion de la linea con el plano es INEXISTENTE porque son paralelos
			}
			
			if(interseccionDentroDeCajaFrontera!= null && interseccionDentroDeCajaFrontera){
				
				Coordenada i = new Coordenada(interseccion);
				
				if(!caraSuperior.contieneCoordenada(i)) caraSuperior.getCoordenadas().add(i);
			}
			*/
			

			//se toma lo que esta DEBAJO del plano o en el plano
			if(pPlano.getAgreementFlagIfc() == false){
				
				//A y B debajo del plano
				if(posicionA < 0 && posicionB < 0){
					caraCortada.coordenadas.add(coordenadaA);
					if(ultimaCoordenadaB) caraCortada.coordenadas.add(coordenadaB);
				}
				
				//A debajo y B en el plano
				if(posicionA < 0 && posicionB == 0){
					caraCortada.coordenadas.add(coordenadaA);
					coordenadasDeCorte.add(coordenadaB);
				}
				
				//A debajo y B encima el plano
				if(posicionA < 0 && posicionB > 0){
					Coordenada i = new Coordenada(interseccion);
					caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(i);
					coordenadasDeCorte.add(new Coordenada(interseccion));
				}
				
				//A en y B encima el plano
				if(posicionA == 0 && posicionB > 0){
					caraCortada.coordenadas.add(coordenadaA);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//A encima y B encima del plano
				if(posicionA > 0 && posicionB > 0){
					//nada
				}
				
				//A y B en el plano
				if(posicionA == 0 && posicionB == 0){
					caraCortada.coordenadas.add(coordenadaA);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//ahora para B primero y A despues
				
				//B debajo y A en el plano
				if(posicionB < 0 && posicionA == 0){
					if(ultimaCoordenadaB) caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//B debajo y A encima el plano
				if(posicionB < 0 && posicionA > 0){
					Coordenada i = new Coordenada(interseccion);
					caraCortada.coordenadas.add(i);
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(new Coordenada(interseccion));
				}
				
				//B en y A encima el plano
				if(posicionB == 0 && posicionA > 0){
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(coordenadaB);
				}
				
			}
			
			//se toma lo que esta ENCIMA del plano o en el plano
			if(pPlano.getAgreementFlagIfc() == true){
				
				//A y B debajo del plano
				if(posicionA < 0 && posicionB < 0){
					//nada
				}
				
				//A debajo y B en el plano
				if(posicionA < 0 && posicionB == 0){
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(coordenadaB);
				}
				
				//A debajo y B encima el plano
				if(posicionA < 0 && posicionB > 0){
					Coordenada i = new Coordenada(interseccion);
					caraCortada.coordenadas.add(i);
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(new Coordenada(interseccion));
				}
				
				//A en y B encima el plano
				if(posicionA == 0 && posicionB > 0){
					caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(coordenadaB);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//A encima y B encima del plano
				if(posicionA > 0 && posicionB > 0){
					caraCortada.coordenadas.add(coordenadaA);
					if(ultimaCoordenadaB) caraCortada.coordenadas.add(coordenadaB);
				}
				
				//A y B en el plano
				if(posicionA == 0 && posicionB == 0){
					caraCortada.coordenadas.add(coordenadaA);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//ahora para B primero y A despues
				
				//B debajo y A en el plano
				if(posicionB < 0 && posicionA == 0){
					//if(ultimaCoordenadaB) caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(coordenadaA);
					coordenadasDeCorte.add(coordenadaA);
				}
				
				//B debajo y A encima el plano
				if(posicionB < 0 && posicionA > 0){
					Coordenada i = new Coordenada(interseccion);
					caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(i);
					coordenadasDeCorte.add(new Coordenada(interseccion));
				}
				
				//B en y A encima el plano
				if(posicionB == 0 && posicionA > 0){
					caraCortada.coordenadas.add(coordenadaA);
					caraCortada.coordenadas.add(coordenadaB);
					
					coordenadasDeCorte.add(coordenadaB);
				}
				
			}
			
			
					
		}
		
		if(pPlano.getCaraDeCorte() == null){
			pPlano.setCaraDeCorte(new Poligono());
		}
		
		for (Coordenada coordenada : coordenadasDeCorte) {
			//se evitan insertar coordenadas repetidas, las coordenadas
			//se consideran iguales mediante el metodo "public boolean equals(Object otraCoordenada)" de la clase Coordenada
			if(!pPlano.getCaraDeCorte().getCoordenadas().contains(coordenada)){
				pPlano.getCaraDeCorte().getCoordenadas().add(coordenada);
			}
		}
		
		
		return caraCortada;
		
		
	}
	
	public boolean contieneCoordenada(Coordenada c){
		boolean r = false;
		
		for (Coordenada coordenadaActual : coordenadas) {
			
			if(coordenadaActual.esIgual(c)){
				r = true;
				break;
			}
			
		}
		
		return r;
	}
	
	
	
	public void ordenarVerticesRespectoACentroide(){
		
		//se crea un plano que contiene las 3 primeras coordenadas del poligono
		
		Plane plano = this.toPlane();
		
		if(plano==null) return;

		Vector3D ejeZ = new Vector3D(0,0,1); //Vector que representa al eje Z 
		Rotation rotacionRespectoAZ = new Rotation(plano.getNormal(),ejeZ);
		Rotation rotacionOriginal = new Rotation(ejeZ,plano.getNormal());
		
		List<Coordenada> coordenadasRotadas = new ArrayList<Coordenada>();
		
		CentroidPoint cp = new CentroidPoint();
		double coordenadaZ = 0;
		
		//System.out.println("Puntos Rotados a plano en Z");
		
		for (Coordenada coordActual : this.getCoordenadas()) {
			
			Coordenada rotada = new Coordenada(rotacionRespectoAZ.applyTo(coordActual.toVector3D()));
			coordenadasRotadas.add(rotada);
			
			//System.out.println(rotada);
			
			cp.add(new Coordinate(rotada.getX(), rotada.getY()));
			
			//se puede tomar de cualquier coordenada porque todas comparten el mismo plano en Z
			coordenadaZ = rotada.getZ();
			
		}
		
		Coordinate centroideRotado = cp.getCentroid();
		
		//ordena las coordenadas respecto al centroide para que el poligono se genere correctamente, evitando cruces de bordes por ejemplo
		Collections.sort(coordenadasRotadas, new ComparadorAngulos(centroideRotado));
		
		//se limpian las coordenadas originales del poligono, porque pueden estar en desorden
		this.setCoordenadas(new ArrayList<Coordenada>());
		
		//se devuelven las coordenadas a su ubicacion original
		for (Coordenada coordActual : coordenadasRotadas){
			
			Coordenada original = new Coordenada(rotacionOriginal.applyTo(coordActual.toVector3D()));
			
			this.getCoordenadas().add(original);
		}
		
		
		//el centroide ubicado en su posicion absoluta seria este
		//Vector3D centroideReal = rotacionOriginal.applyTo(new Vector3D(centroideRotado.x, centroideRotado.y, coordenadaZ));
		
	}

	public Plane toPlane(){
		

		//se crea un plano que contiene las 3 primeras coordenadas del poligono
		
		Plane plano = null; 

		int n = this.getCoordenadas().size();
		try {
			
			plano = new Plane(this.getCoordenadas().get(0).toVector3D(), this.getCoordenadas().get(1).toVector3D(), this.getCoordenadas().get(2).toVector3D());
			
		} catch (Exception e) {

			//si las 3 primeras coordenadas generan error se escogen las 3 ultimas
			//esto podria hacerse mas sofisticado verificando que las 3 coordenadas no compartan la misma linea que es cuando no se puede crear el pano
			//y se genera la excepcion
			//si las 3 ultimas tambien generan excepcion simplemente se aborta el metodo y los vertices no veran alterado su orden
			try {
				
				plano = new Plane(this.getCoordenadas().get(n-1).toVector3D(), this.getCoordenadas().get(n-2).toVector3D(), this.getCoordenadas().get(n-3).toVector3D());
				
			} catch (Exception e2) {
				return plano;
			}
			
		}
		
		return plano;
	}


	private List<Coordenada> coordenadasParalelasAPlanoXY;
	private double coordenadaZParalelaAPlanoXY;
	private Rotation rotacionOriginal = null;
	
	//esta funcion rota cualquier poligono haciendo que su nueva ubicacion sea paralela al plano XY
	//el nuevo poligono tiene constante la coordenada Z en todos sus puntos
	//se usa cuando se necesita utilizar Java Topology Suite porque dicha librería solo trabaja en 2D
	//retorna el valor de Z para todos los puntos del polígono
	public boolean calcularPoligonoParaleloAPlanoXY(Poligono pPoligonoEvaluado){
		
		Vector3D ejeZ = new Vector3D(0,0,1); //Vector que representa al eje Z
		
		Plane planoMuro = this.toPlane();

		//si se recibio pPoligonoEvaluado se debe evaluar que la normal del muro sea identica
		//a la normal de pPoligonoEvaluado
		//esto garantiza que los dos poligonos serán rotados hacia el mismo plano
		//si las normales son contrarias las coordenadas Z de cada polígono serán opuestas
		//y no puede realizarse el recorte de la silueta
		if(pPoligonoEvaluado!= null){
			Plane planoPoligonoEvaluado = pPoligonoEvaluado.toPlane();
			//los dos planos deben tener la misma normal para que al rotarlos ocupen el mismo plano
			//System.out.println("normal muro = " + planoMuro.getNormal() + " normal tijera = " + planoPoligonoEvaluado.getNormal() + " angulo = " + Vector3D.angle( planoMuro.getNormal() , planoPoligonoEvaluado.getNormal()));
		
			double angulo = 0;
				
			try {
				angulo = Vector3D.angle( planoMuro.getNormal() , planoPoligonoEvaluado.getNormal());
			} catch (Exception e) {
				return false;
			}
			
			
		
			if( angulo > 0.001 ){
			
				//System.out.println("Invirtiendo plano");
				planoMuro.revertSelf();
				
			}
			
		}

		
		Rotation rotacionRespectoAZ = new Rotation(planoMuro.getNormal(),ejeZ);
		//se debe guardar la rotacion original para restaurar las coordenadas a su ubicación 3D original
		rotacionOriginal = new Rotation(ejeZ,planoMuro.getNormal()) ;
		
		List<Coordenada> coordenadasRotadas = new ArrayList<Coordenada>();
		
		//System.out.println("Rotadas = ");
		for (Coordenada coordActual : this.getCoordenadas()) {
			
			Coordenada rotada = new Coordenada(rotacionRespectoAZ.applyTo(coordActual.toVector3D()));
			coordenadasRotadas.add(rotada);
			
			//System.out.println(rotada);

		}
		//la coordenada Z es igual para todos los puntos, se toma del primero
		coordenadaZParalelaAPlanoXY = coordenadasRotadas.get(0).getZ();
		
		coordenadasParalelasAPlanoXY = coordenadasRotadas;
		
		return true;
	}
	
	public static com.vividsolutions.jts.geom.Polygon obtenerPolygonJTS(List<Coordenada> pCoordenadas){
		
		GeometryFactory geometryFactory = new GeometryFactory();
		
		Coordinate[] coordenadas = new Coordinate[pCoordenadas.size()];
		int c =  0;
		for (Coordenada actual : pCoordenadas) {
			
			Coordinate coord = new Coordinate(actual.getX(), actual.getY());
			coordenadas [c] = coord; 
			c++;
		}
		
		LinearRing anillo = geometryFactory.createLinearRing(coordenadas);
		com.vividsolutions.jts.geom.Polygon poligono = geometryFactory.createPolygon(anillo, null); //se asume que no hay HUECOS en el poligono y por eso se envía NULL 
		
		return poligono;
		
	}
	
	public List<Poligono> diferencia(Poligono poligonoEvaluado){
		
		List<Poligono> r = null;
		
		//se envia el poligonoEvaluado como parametro para evaluar que la normal del plano sea identica
		boolean sePudoA = this.calcularPoligonoParaleloAPlanoXY(poligonoEvaluado);
		boolean sePudoB = poligonoEvaluado.calcularPoligonoParaleloAPlanoXY(null);
		
		if(sePudoA && sePudoB){
			com.vividsolutions.jts.geom.Polygon poligonoA = obtenerPolygonJTS(coordenadasParalelasAPlanoXY);
			com.vividsolutions.jts.geom.Polygon poligonoB = obtenerPolygonJTS(poligonoEvaluado.coordenadasParalelasAPlanoXY);
			
			//se usa la tolerancia porque hay ciertas geometrias que aunque ocupan el mismo plano
			//no cortan a la linea que se necesita del poligono del muro porque están alejadas unas milesimas 
			//si no se usa el buffer de la tolerancia definida en la clase Coordenada los muros no quedan bien cortados
			//para ubicar las puertas
			Geometry diferencia = poligonoA.difference(poligonoB.buffer(Coordenada.tolerancia));
			
			int geometriasResultantes = diferencia.getNumGeometries();
			
			if(geometriasResultantes > 0) {
				r = new ArrayList();
				
				for (int i = 0; i < geometriasResultantes; i++) {
					
					Geometry geomActual = diferencia.getGeometryN(i);
					
					Coordinate[] coordenadasResultado = geomActual.getCoordinates();
					
					Poligono poligonoResultado = new Poligono();
					
					for (int p = 0; p < coordenadasResultado.length; p++) {
						
						Coordinate actual = coordenadasResultado[p];
						Coordenada coorRes = new Coordenada(actual.x, actual.y, this.coordenadaZParalelaAPlanoXY);
						coorRes = new Coordenada(rotacionOriginal.applyTo(coorRes.toVector3D()));
						poligonoResultado.getCoordenadas().add(coorRes);
					}
					
					r.add(poligonoResultado);
				}
			}
		}
		
		
		return r;
	}
	
	public void calcularPolygonGML() throws DimensionMismatchException{
		
		List<Double> coordenadasEstePoligono = new ArrayList();
		
		for(Coordenada coordenadaActual : this.getCoordenadas()){
			coordenadasEstePoligono.add(coordenadaActual.getX());
			coordenadasEstePoligono.add(coordenadaActual.getY());
			coordenadasEstePoligono.add(coordenadaActual.getZ());
		}
		//se calcula el poligono GML
		org.citygml4j.model.gml.geometry.primitives.Polygon polygonGML = geom.createLinearPolygon(coordenadasEstePoligono, 3);
		
		for (Poligono caraInternaActual : this.getCarasInternas()) {
			
			List<Double> coordenadasEstaCaraInterna = new ArrayList(); 
			
			for(Coordenada coordenadaActual : caraInternaActual.getCoordenadas()){
				coordenadasEstaCaraInterna.add(coordenadaActual.getX());
				coordenadasEstaCaraInterna.add(coordenadaActual.getY());
				coordenadasEstaCaraInterna.add(coordenadaActual.getZ());
			}
			
			org.citygml4j.model.gml.geometry.primitives.LinearRing anillo = geom.createLinearRing(coordenadasEstaCaraInterna, 3);
			Interior interior = new InteriorImpl();
			interior.setRing(anillo);
			
			//se agrega un hueco al poligono GML, tambien llamado "cara interna"
			polygonGML.addInterior(interior);
			
		}
		
		setPoligonoGML(polygonGML);
		
	}
	
	public void corregirOrientacion(){

//  http://stackoverflow.com/questions/1988100/how-to-determine-ordering-of-3d-vertices
//	
//		As others have noted, your question isn't entirely clear. Is the for something like a 3D backface culling test? 
//		If so, you need a point to determine the winding direction relative to. Viewed from one side of the polygon the 
//		vertices will appear to wind clockwise. From the other side they'll appear to wind counter clockwise.
//
//		But suppose your polygon is convex and properly planar. Take any three consecutive vertices A, B, and C. 
//		Then you can find the surface normal vector using the cross product:
//
//		N = (B - A) x (C - A)
//		Taking the dot product of the normal with a vector from the given view point, V, to one of the vertices 
//		will give you a value whose sign indicates which way the vertices appear to wind when viewed from V:
//
//		w = N . (A - V)
//	
//		Whether this is positive for clockwise and negative for anticlockwise or the opposite will depend on 
//		the handedness of your coordinate system.
		
		if(this.getCoordenadas().size()>=3){
			
			Vector3D A = this.getCoordenadas().get(0).toVector3D();
			Vector3D B = this.getCoordenadas().get(1).toVector3D(); 
			Vector3D C = this.getCoordenadas().get(2).toVector3D();
			Vector3D B_A = B.subtract(A);
			Vector3D C_A = C.subtract(A);
			
			Vector3D V = new Vector3D(0,0,0); //el punto de vista es el origen
			Vector3D A_V = A.subtract(V);
			
			Vector3D N = B_A.crossProduct(C_A);
			
			//si sentido es negativo el poligono es antihorario, si no es horario
			double sentido = N.dotProduct(A_V);
			
			if(sentido<0){
				invertirOrientacion();
			}
			
		}
		
	}
	
	public void invertirOrientacion(){
		Collections.reverse(this.getCoordenadas());
	}
}
