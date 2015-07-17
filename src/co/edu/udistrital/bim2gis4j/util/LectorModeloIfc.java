package co.edu.udistrital.bim2gis4j.util;

import java.io.File;

import openifctools.com.openifcjavatoolbox.ifcmodel.IfcModel;
import openifctools.com.openifcjavatoolbox.step.parser.util.ProgressEvent;
import openifctools.com.openifcjavatoolbox.step.parser.util.StepParserProgressListener;

public class LectorModeloIfc {
	
	public IfcModel cargarModeloIfc(String rutaArchivoIfc){
		
		IfcModel modeloEnMemoria = null;
		
		// Se carga el modelo IFC en memoria
		final File file = new File(rutaArchivoIfc);
		modeloEnMemoria = new IfcModel();
		modeloEnMemoria.addStepParserProgressListener(new StepParserProgressListener() {
			@Override
			public void progressActionPerformed(final ProgressEvent event) {

				if (((int) event.getCurrentState() % 10 == 0)
						&& (event.getCurrentState() > 0)) {

					System.out.println(event.getMessage() + " - "
							+ event.getCurrentState() + "%");
				}
			}
		});
		try {
			modeloEnMemoria.readStepFile(file);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return modeloEnMemoria;
		
	}

}
