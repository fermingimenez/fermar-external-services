package py.com.fermar.external.config;


import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import py.com.fermar.tools.ksigner.KsignerXmlDsig;
import py.com.fermar.commons.exception.FerMarRuntimeException;
import py.com.fermar.commons.utils.CipherUtils;
import py.com.fermar.commons.utils.XmlUtils;
import py.com.fermar.external.dto.TgEmis;
import py.com.fermar.external.service.ValidarFirmaService;

@Configuration
public class EmisorConfig {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EmisorConfig.class.getName());
	
	@Autowired
	private String emisorSifenFile;

	@Autowired
	private String sifenUrl;
	
	@Autowired
	private String certificateCipherPassword;
	
	private Document signedEmisorDocument;
	private String rucEmis;
	private String loadErrors;
	
	private static final String ROOT_NODE = "rFerMar";
	public static final String NODO_EMISOR = "gEmis";
	private static final String NODO_EMISOR_PATH = ROOT_NODE  + "." + NODO_EMISOR;
	
	@Bean("gEmis") 
	public TgEmis gEmis() {
				
		try {
        	signedEmisorDocument = KsignerXmlDsig.readXMLDocument(emisorSifenFile);

        	ValidarFirmaService.validateFMID(signedEmisorDocument, NODO_EMISOR_PATH);

			Node nodeXML = XmlUtils.getNodeFromPath(signedEmisorDocument, NODO_EMISOR_PATH);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(TgEmis.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		       
		    JAXBElement<TgEmis> jaxbElement = jaxbUnmarshaller
	          .unmarshal(nodeXML, TgEmis.class);

		    TgEmis tgEmis =  fixJaxB(nodeXML, jaxbElement.getValue(), NODO_EMISOR_PATH);
		    rucEmis = tgEmis.getDRucEm();
		    return tgEmis;			
			
		} catch (Exception e) {
			LOGGER.error("Cargar {}", NODO_EMISOR_PATH, e);
			
			return new TgEmis();
		}
	}
	
	private TgEmis fixJaxB(Node nodeXML, TgEmis tgEmis, String NODO) 
			throws TransformerException, IOException, SAXException, ParserConfigurationException, JAXBException {

		if (tgEmis.getDRucEm() != null) {
			return tgEmis;
		}
		
		JAXBContext jaxbContext = JAXBContext.newInstance(String.class);
	    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		Node node = XmlUtils.getNodeFromPath(nodeXML, "dRucEm");	       
	    JAXBElement<String> jaxbDRucEm = jaxbUnmarshaller
          .unmarshal(node, String.class);

		node = XmlUtils.getNodeFromPath(nodeXML, "dNomEmi");
	    JAXBElement<String> jaxbDNomEmi = jaxbUnmarshaller
	            .unmarshal(node, String.class);
	    
		tgEmis = new TgEmis();
		tgEmis.setDRucEm(jaxbDRucEm.getValue());
		tgEmis.setDNomEmi(jaxbDNomEmi.getValue());
		
	    return tgEmis;  
	}
	
	
	@Bean
	@DependsOn({"gEmis"})
	public boolean isProduction() {		
		
		String value = "";
		final String NODO = "rFerMar.esProduccion";
		
		if(rucEmis != null) {
			return true;
		}
		
		try {

			Node nodeXML = XmlUtils.getNodeFromPath(signedEmisorDocument, NODO);
			
			JAXBContext jaxbContext = JAXBContext.newInstance(String.class);
		    Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		       
		    JAXBElement<String> jaxbElement = jaxbUnmarshaller
	          .unmarshal(nodeXML, String.class);
	       
		    value = jaxbElement.getValue();		    
			
		} catch (Exception e) {
			LOGGER.error("Cargar {}, {}", NODO, e.getMessage());
		}
		
		return value != null && value.equals("S");
	}
	
	@Bean
	@DependsOn({"isProduction", "sifenUrl"})
	public String sifenUrlRecibeDe() {
		Map<String, String> map = getStringAsMap(sifenUrl);
		
		String url = map.get("sifenUrlRecibeDeTest");
		if (isProduction()) {
			url = map.get("sifenUrlRecibeDe");
		}
		
		return url;
	}
	
	@Bean
	@DependsOn({"gEmis"})
	public String certificatePassword() {

		String clearText = "";
		try {
			if (rucEmis == null ||
					certificateCipherPassword == null) {
				throw new FerMarRuntimeException("Datos emisor no cargados");
			}
			
			String key = Base64.encodeBase64String(rucEmis.getBytes());
			clearText = CipherUtils.decipherText(certificateCipherPassword, key);
				
		} catch (FerMarRuntimeException e) {
			LOGGER.error("Password don't decipher {}, {}", rucEmis, certificateCipherPassword, e);
		}
		
		return clearText;
	}
	
	@Bean
	@DependsOn({"isProduction", "gEmis", "certificatePassword"})
	public String loadErrors() {
		if (loadErrors == null) {
			return "";
		}
		
		return loadErrors;
	}
	
	private Map<String, String> getStringAsMap(String mapAsString) {
		return Arrays.stream(mapAsString.split(","))
			      .map(entry -> entry.split("="))
			      .collect(Collectors.toMap(entry -> entry[0], entry -> entry[1]));
	}
}
