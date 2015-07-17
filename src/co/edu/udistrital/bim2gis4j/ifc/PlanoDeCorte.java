package co.edu.udistrital.bim2gis4j.ifc;

import java.util.ArrayList;
import java.util.List;
import openifctools.com.openifcjavatoolbox.ifc2x3tc1.IfcPlane;
import org.apache.commons.math3.geometry.euclidean.threed.Plane;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Coordenada;
import co.edu.udistrital.bim2gis4j.bim2gisbm.Poligono;

public class PlanoDeCorte {

	IfcPlane planoIfc;
	Coordenada locationAbsolutaIfc;
	Coordenada normalAbsolutaIfc;
	
	Plane planoApache;
	Poligono caraDeCorte; //es la cara que corta al solido, es diferente del plano porque el plano es infinito, la cara de corte está limitada al
	//polígono que corta exactamente al sólido y está definida por sus vertices
	
	// AgreementFlag : The agreement flag is TRUE if the normal to the BaseSurface points away from the material of the IfcHalfSpaceSolid. Otherwise it is FALSE.
	boolean agreementFlagIfc;
	


	public boolean getAgreementFlagIfc() {
		return agreementFlagIfc;
	}


	public void setAgreementFlagIfc(boolean agreementFlagIfc) {
		this.agreementFlagIfc = agreementFlagIfc;
	}


	//caras que se van a cortar
	private List<Poligono> carasACortar = new ArrayList<Poligono>();
	
	public Poligono getCaraDeCorte() {
		return caraDeCorte;
	}


	public void setCaraDeCorte(Poligono caraDeCorte) {
		this.caraDeCorte = caraDeCorte;
	}


	//caras resultado del corte
	private List<Poligono> carasResultado = new ArrayList<Poligono>();
	
	public PlanoDeCorte(IfcPlane pOriginal, Plane pApache) {
		
		planoIfc = pOriginal;
		planoApache = pApache;
	}
	
	
	public Coordenada getLocationAbsolutaIfc() {
		return locationAbsolutaIfc;
	}


	public void setLocationAbsolutaIfc(Coordenada locationAbsolutaIfc) {
		this.locationAbsolutaIfc = locationAbsolutaIfc;
	}


	public Coordenada getNormalAbsolutaIfc() {
		return normalAbsolutaIfc;
	}


	public void setNormalAbsolutaIfc(Coordenada normalAbsolutaIfc) {
		this.normalAbsolutaIfc = normalAbsolutaIfc;
	}


	
	
	
	public IfcPlane getPlanoIfc() {
		return planoIfc;
	}


	public void setPlanoIfc(IfcPlane planoIfc) {
		this.planoIfc = planoIfc;
	}


	public Plane getPlanoApache() {
		return planoApache;
	}


	public void setPlanoApache(Plane planoApache) {
		this.planoApache = planoApache;
	}
	
	public List<Poligono> getCarasACortar() {
		return carasACortar;
	}


	public void setCarasACortar(List<Poligono> carasACortar) {
		this.carasACortar = carasACortar;
	}


	public List<Poligono> getCarasResultado() {
		return carasResultado;
	}


	public void setCarasResultado(List<Poligono> carasResultado) {
		this.carasResultado = carasResultado;
	}

}
