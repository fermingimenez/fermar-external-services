package py.com.fermar.external.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.xml.soap.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

@Component
public class ServiciosSoap {
	
	private static final String TRANSPORT_LAYER_SECURITY = "TLSv1.2";
	private static final String FERMAR_CONSULTA_ID = "17082017";

	@Autowired
	private String certificateFile;
	
	@Autowired
	private String certificatePassword;
	

    public void doTrustToCertificates() throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException, KeyManagementException {

        InputStream inputStream = null;
        
        try {
            // Set keystore that contains private key
	        String keystoreURL = certificateFile;
	        inputStream = new FileInputStream(keystoreURL);

	        String pKeyPassword = certificatePassword;

	        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
	        KeyStore keyStore = KeyStore.getInstance("PKCS12");
	        
	        keyStore.load(inputStream, pKeyPassword.toCharArray());
	        keyManagerFactory.init(keyStore, pKeyPassword.toCharArray());
	
	        // Set ssl context with private key and truststore details
	        SSLContext sc = SSLContext.getInstance(TRANSPORT_LAYER_SECURITY);
	        sc.init(keyManagerFactory.getKeyManagers(), null, new SecureRandom());
	        SSLSocketFactory sockFact = sc.getSocketFactory();
	
	        // Add ssl context to https connection
	        HttpsURLConnection.setDefaultSSLSocketFactory(sockFact);
	        
        } catch (Exception e) {
            throw new IOException("Error doTrustToCertificates");
            
        } finally {
            if (inputStream!=null) {
                inputStream.close();
            }
        }
        
    }

    public SOAPMessage createSoapPart4ConsRUC(String ruc) throws SOAPException{
    	
        System.setProperty(
        		"javax.xml.soap.MessageFactory", 
        		"com.sun.xml.internal.messaging.saaj.soap.ver1_2.SOAPMessageFactory1_2Impl");

        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.SOAP_1_2_PROTOCOL);

        SOAPMessage soapRequest = messageFactory.createMessage();
        SOAPPart part = soapRequest.getSOAPPart();
        SOAPEnvelope envelope = part.getEnvelope();

        String serverURI = "http://ekuatia.set.gov.py/sifen/xsd";
        String nameSpace = "xsd";

        envelope.addNamespaceDeclaration(nameSpace, serverURI);
        envelope.removeNamespaceDeclaration("env");
        envelope.setPrefix("soap");

        SOAPHeader header = envelope.getHeader();
        header.setPrefix("soap");

        //Obtiene el cuerpo del mensaje SOAP
        SOAPBody body = envelope.getBody();
        body.setPrefix("soap");

        //Crea la primera etiqueta del cuerpo del mensaje SOAP
        SOAPElement rEnviConsDe = body.addChildElement("rEnviConsRUC", nameSpace);

        SOAPElement dId = rEnviConsDe.addChildElement("dId", nameSpace);
        dId.addTextNode(FERMAR_CONSULTA_ID);

        SOAPElement dCDC = rEnviConsDe.addChildElement("dRUCCons", nameSpace);
        dCDC.addTextNode(ruc);

        return soapRequest;
    }
}
