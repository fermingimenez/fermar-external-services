package py.com.fermar.external.service;


import py.com.fermar.external.constants.ServiceConstants;
import py.com.fermar.external.utils.RucDTO;
import py.com.fermar.external.utils.ServiciosSoap;
import py.com.fermar.external.utils.SoapResponse;

import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SifenService {
	private static final Logger LOGGER = 
			LoggerFactory.getLogger(SifenService.class.getName());

	@Autowired
    private ServiciosSoap serviciosSoap;
    
	@Autowired
	private String sifenUrlRecibeDe;
	
	private static final String  WS_CONSULTA_RUC = "consultas/consulta-ruc.wsdl";

    private SOAPMessage sendXmlSoap(SOAPMessage xmlSoapEvent, String sifenUrl) 
    		throws SOAPException, IOException {
        
        try {
            serviciosSoap.doTrustToCertificates();
            
        } catch (Exception e) {
            LOGGER.error("Error en metodo sendXmlSoap", e);
            throw new SOAPException(e);
        }
        
        //Crear la conexion SOAP
        SOAPConnectionFactory soapConnectionFactory = SOAPConnectionFactory.newInstance();
        SOAPConnection soapConnection = soapConnectionFactory.createConnection();
        
        SOAPMessage sifenResponse = null;
        try {
            sifenResponse = soapConnection.call(xmlSoapEvent, sifenUrl);
            
            if (sifenResponse == null) {
                throw new SOAPException("El servicio de la SET respondió vacío");            	
            }
            
            //Imprime la respuesta SOAP
            //sifenResponse.writeTo(System.out);
            
            return sifenResponse;
            
        }catch (SOAPException e){
            LOGGER.error("Error en metodo sendXmlSoap", e);
            String message = 
            		e.getMessage().replaceAll("com.sun.xml.messaging.saaj.SOAPExceptionImpl", "");
            message = "La SET respondió: " + message.replaceFirst(": ", "");
            throw new SOAPException(message);
        }
        finally {
            soapConnection.close();        	
        }
    }
    
    public SoapResponse<RucDTO> getRUCFromSifen(String ruc) {
    	
        SoapResponse<RucDTO> response = new SoapResponse<>();
        SOAPMessage sifenResponse = null;
        SOAPMessage xmlSoapConsultaDE = null;

        try {

            xmlSoapConsultaDE = 
            		serviciosSoap.createSoapPart4ConsRUC(ruc);
            
        } catch (Exception e) {
            response.setdCodRes(HttpStatus.CONFLICT.value());
            response.setdMsgRes(e.getMessage());
            return response;
        }

        try {
        	
        	String serviceURI = sifenUrlRecibeDe + WS_CONSULTA_RUC;
            sifenResponse = sendXmlSoap(xmlSoapConsultaDE, serviceURI);
            
        } catch (Exception e) {
            response.setdCodRes(HttpStatus.CONFLICT.value());
            response.setdMsgRes("Error al enviar el mensaje SOAP: " + e.getMessage());
            return response;
        }

        try {
        	response.setdCodRes(Integer.valueOf(
            		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dCodRes").item(0).getTextContent()
            		));

        	response.setdMsgRes(
            		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dMsgRes").item(0).getTextContent()
            		);
        	
        	String xContRUC = "";
        	RucDTO dto = null;
        	if(ServiceConstants.ValidationCodes.RUC_EXISTE.getCodigoInt()
					.compareTo(response.getdCodRes()) == 0) {
        		xContRUC = 
            		sifenResponse.getSOAPBody().getElementsByTagName("ns2:xContRUC").item(0).getTextContent();

        		
        		if(xContRUC != null &&
        				!xContRUC.isEmpty()) {
        			
        			dto = new  RucDTO();
        			
        			dto.setdRUCCons(
                    		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dRUCCons").item(0).getTextContent());
        			dto.setdRazCons(
                    		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dRazCons").item(0).getTextContent());
        			dto.setdCodEstCons(
                    		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dCodEstCons").item(0).getTextContent());
        			dto.setdDesEstCons(
                    		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dDesEstCons").item(0).getTextContent());
        			dto.setdRUCFactElec(
                    		sifenResponse.getSOAPBody().getElementsByTagName("ns2:dRUCFactElec").item(0).getTextContent());
        		}
            }
            
            response.setDato(dto);

        } catch (Exception ex) {
            response.setdCodRes(HttpStatus.CONFLICT.value());
            response.setdMsgRes(ex.getMessage());
            LOGGER.error("Error al consultar RUC en SIFEN",ex);
        }
        return response;
    }
}
