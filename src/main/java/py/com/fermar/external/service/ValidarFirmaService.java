package py.com.fermar.external.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.xml.security.keys.KeyInfo;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.signature.Reference;
import org.apache.xml.security.signature.XMLSignature;
import org.apache.xml.security.signature.XMLSignatureException;
import org.opensaml.core.config.InitializationException;
import org.opensaml.xmlsec.signature.Signature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import net.shibboleth.utilities.java.support.xml.ElementSupport;
import py.com.fermar.commons.constants.AppConstants;
import py.com.fermar.commons.constants.ValidationCodes;
import py.com.fermar.commons.exception.FerMarRuntimeException;
import py.com.fermar.commons.exception.SIFENException;
import py.com.fermar.commons.exception.SignatureValidationException;
import py.com.fermar.tools.kcerts.CertChainValidator;
import py.com.fermar.tools.ksigner.InitializationSupport;
import py.com.fermar.tools.ksigner.KsignerXmlDsig;
import py.com.fermar.tools.ksigner.SignVerificationException;
import py.com.fermar.tools.ksigner.SignXmlSifenWithCryptoDsig;
import py.com.fermar.commons.utils.CipherUtils;
import py.com.fermar.commons.utils.XmlUtils;


/**
 * Implementacion del servicio  {@link ValidarFirmaService}
 *
 * @version 1 13/07/2018
 */
@Service
public class ValidarFirmaService {
	
	private static Logger log = LoggerFactory.getLogger(ValidarFirmaService.class.getName());
			

	@Autowired
	private String certificateFile;
	
	@Autowired
	private String certificatePassword;	

	private static final String OID_SERIALNUMBER = "2.5.4.5";

	private static final String XML_DATA_ELEMENT_TAG_NAME_DE = "rDE.DE";
	
	/**
	 * Inicializamos el keystore con los certificados de las CA
	 * habilitadas
	 * @return keystore
	 * @throws SignatureValidationException SignatureValidationException
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 */
	private static KeyStore initKeystore(String storename, String storepass) 
			throws SignatureValidationException, KeyStoreException, FileNotFoundException, IOException, NoSuchAlgorithmException, CertificateException  {

		KeyStore ks = KeyStore.getInstance("PKCS12");
		
		try (FileInputStream fin = new FileInputStream(storename)) {
			ks.load(fin, storepass.toCharArray());			
		} 
			
		return ks; 
	}
	
	/**
	 * Validamos la cadena de confianza
	 * @param keyInfo  keyInfo
	 * @throws SignatureValidationException SignatureValidationException
	 */
	public void validateCertChain(KeyInfo keyInfo) 
			throws SignatureValidationException {

		KeyStore kStore;
		try {
			kStore = initKeystore(certificateFile, certificatePassword);

		} catch (FileNotFoundException e1) {
			throw new SignatureValidationException(ValidationCodes.KEY_STORE_INVALIDO);
		} catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e1) {
			throw new SignatureValidationException(ValidationCodes.KEY_STORE_ERROR);
		}
		
		try {
			X509Certificate cert = keyInfo.getX509Certificate();
			if(kStore.size() > 0) {
				boolean result = CertChainValidator.validateKeyChain(cert, kStore);
				if(!result) {
					throw new SignatureValidationException(ValidationCodes.CADENA_INVALIDO);
				}
			}
		} catch (KeyResolverException | KeyStoreException | CertificateException | 
				InvalidAlgorithmParameterException | NoSuchAlgorithmException 
				| NoSuchProviderException e) {
			throw new SignatureValidationException(ValidationCodes.CERT_INVALIDO);
		}
	}

	/**
	 * Obtenemos el DigestValue del xml Document
 	 * @param xmlDocument  documento xml
 	 * @return digestVal 
	 * @throws SIFENException  SIFENException
	 * 
	 */
	public String obtenerDigestValue(Document xmlDocument) throws SIFENException{
		String digestVal = null;
		try {
			XMLSignature signature = getSignature(xmlDocument);
			KeyInfo keyInfo = signature.getKeyInfo();
			if (keyInfo != null) {
				final Reference ref = getReference(signature);
				digestVal = getDigest(ref);
			}
		}catch (Exception e) {
			log.error(e.getMessage());
			throw new SIFENException(ValidationCodes.ERROR_DIGEST_VALUE.getMensaje());
		}
		return digestVal;
		
	}

	/**
	 * Obtenemos el Digest Value del reference
	 * @param ref reference
	 * @return digest
	 * @throws SignatureValidationException SignatureValidationException
	 */
	private String getDigest(Reference ref) throws SignatureValidationException {
		String digest = null;
		try {
			digest = new String(Base64.encodeBase64(ref.getDigestValue()));
		} catch (Exception e) {
			log.error(e.getMessage());
			throw new SignatureValidationException(ValidationCodes.ERROR_DIGEST_VALUE);
		}
		return digest;
	}
	
	/**
	 * Método para extraer del SERIALNUMBER el ruc
	 *
	 * @param dn domain name
	 * @return username
	 */
	private static String extractSerialNumber(String dn) {
		dn = replaceOID(dn);
		String username = dn;

		// ensure the dn is specified
		if (StringUtils.isNotBlank(dn)) {
			// determine the separate
			final String separator =
					StringUtils.indexOfIgnoreCase(dn, "/SERIALNUMBER=") > 0 ? "/" : ",";

			// attempt to locate the SERIALNUMBER
			final String cnPattern = "SERIALNUMBER=";
			final int cnIndex = StringUtils.indexOfIgnoreCase(dn, cnPattern);
			if (cnIndex >= 0) {
				int separatorIndex = StringUtils.indexOf(dn, separator, cnIndex);
				if (separatorIndex > 0) {
					username =
							StringUtils.substring(dn, cnIndex + cnPattern.length(), separatorIndex);
				} else {
					username = StringUtils.substring(dn, cnIndex + cnPattern.length());
				}
			}
		}

		return username;
	}

	/**
	 * Método para obtener la CI del subjectAlternativeNames
	 *
	 * @param certificate certificado
	 * @return result
	 * @throws CertificateParsingException CertificateParsingException
	 */
	private static List<String> getSubjectAlternativeNames(final X509Certificate certificate)
			throws CertificateParsingException {

		final Collection<List<?>> altNames = certificate.getSubjectAlternativeNames();
		if (altNames == null) {
			return new ArrayList<>();
		}

		final List<String> result = new ArrayList<>();
		for (final List<?> generalName : altNames) {
			/**
			 * generalName has the name type as the first element a String or byte array for the second element. We return any general names that are String types.
			 *
			 * We don't inspect the numeric name type because some certificates incorrectly put IPs and DNS names under the wrong name types.
			 */
			final Object value = generalName.get(1);
			if (value instanceof String && ((String) value).indexOf(OID_SERIALNUMBER) != -1) {
				String varTemp = ((String) value).toLowerCase();
				X500Principal p = new X500Principal(varTemp);
				String[] varTemp2 = p.toString().split(",");
				for (String rdn : varTemp2) {
					result.add(rdn);
				}
			}

		}

		return result;
	}

	/**
	 * Método que reemplaza el estandar a SERIALNUMBER
	 *
	 * @param dn domain name
	 * @return dn domain name con estandar serial number
	 */
	private static String replaceOID(String dn) {
		return dn.replace("OID.2.5.4.5", "SERIALNUMBER");
	}

	/**
	 * Método para obtener el Signature y validar con el Core
	 *
	 * @param xmlDocument documento xml
	 * @return signature
	 * @throws SignatureValidationException SignatureValidationException
	 */
	private XMLSignature getSignature(Document xmlDocument) throws SignatureValidationException {
		XMLSignature signature = null;
		Element signatureElement;
		try {
			InitializationSupport.initialize();
			// Find Signature element
			signatureElement = getSignatureElement(xmlDocument);
			if (signatureElement == null) {
				log.error("El documento XML no está firmado");
				throw new XMLSecurityException("El documento XML no está firmado");
			}
			
			List<Node> listaNodosDE = XmlUtils.getNodesListFromPath(xmlDocument, XML_DATA_ELEMENT_TAG_NAME_DE);
			
			// Marcamos el atributo de referencia como un atributo de ID válido
			if(listaNodosDE != null && !listaNodosDE.isEmpty()) {
				marcamosIdValido(listaNodosDE);
			}
			signature = new XMLSignature(signatureElement, "");	

		} catch (XMLSecurityException | InitializationException e) {
			log.error(e.getMessage());
			throw new SignatureValidationException(ValidationCodes.CERT_INVALIDO);//(FE.4.1)

		}
		return signature;
	}

	/**
	 * Marcamos el atributo Id como valido
	 * @param listaNodos lista nodos
	 */
	private void marcamosIdValido(List<Node> listaNodos) {
		if(listaNodos != null && !listaNodos.isEmpty()) {
			Element id = (Element) listaNodos.get(0);
			if(id.getAttributes().getNamedItem("Id")!= null) { 
				id.setIdAttributeNS(null, "Id", true);
			}
		}	
	}

	/**
	 * Método para obtener el Elemento Signature
	 *
	 * @param xmlDoc documento xml
	 * @return sigElements
	 * @throws XMLSecurityException XMLSecurityException
	 */
	public static Element getSignatureElement(final Document xmlDoc) throws XMLSecurityException {
		final List<Element> sigElements = ElementSupport
				.getChildElementsByTagNameNS(xmlDoc.getDocumentElement(),
						Signature.DEFAULT_ELEMENT_NAME.getNamespaceURI(),
						Signature.DEFAULT_ELEMENT_NAME.getLocalPart());

		if (sigElements.isEmpty()) {
			return null;
		}

		if (sigElements.size() > 1) {
			throw new XMLSecurityException(
					"El documento XML contiene " + sigElements.size() + " firma/s, No se puede procesar");
		}
		return sigElements.get(0);
	}

	/**
	 * Método para extraer referencias del signature
	 *
	 * @param signature firma
	 * @return ref
	 * @throws SignatureValidationException SignatureValidationException
	 */
	private static Reference getReference(final XMLSignature signature)
			throws SignatureValidationException {
		final int numReferences = signature.getSignedInfo().getLength();
		final Reference ref;
		try {
			if (numReferences != 1) {
				log.error("La firma SignedInfo tiene un número inválido de referencias.");
				throw new XMLSignatureException(
						"La firma SignedInfo tiene un número inválido de " + "referencias. "
								+ numReferences);
			}
			ref = signature.getSignedInfo().item(0);

			if (ref == null) {
				log.error("Signature Reference es null");
				throw new XMLSignatureException("Signature Reference es null. ");

			}
		} catch (final XMLSecurityException e) {
			log.error(e.getMessage());
			throw new SignatureValidationException(ValidationCodes.FIRMA_DIF_ESTANDAR);//(FE.5.1)
		}
		return ref;
	}
	
	public static void validateFMID(Document xmlDocument, String tagNameSigned)
			throws SignatureValidationException, KeyResolverException, CertificateParsingException, SignVerificationException {

		ValidarFirmaService validation = new ValidarFirmaService();
		// 1.a- El mensaje debe tener certificado de firma.
		XMLSignature signature = validation.getSignature(xmlDocument);

		KeyInfo keyInfo = signature.getKeyInfo();
		if (keyInfo == null) {
			throw new SignVerificationException("KeyInfo not found");
		}

    	Element elementSigned = (Element) XmlUtils.getNodeFromPath(xmlDocument, tagNameSigned);
		KsignerXmlDsig.validateSignatureWithReferences(xmlDocument, elementSigned);
		
		String subject = CipherUtils.md5(extractSerialNumber(
				keyInfo.getX509Certificate().getSubjectX500Principal()
						.getName(X500Principal.RFC1779)));
		String subjectAltNames = CipherUtils.md5(extractSerialNumber(
				getSubjectAlternativeNames(keyInfo.getX509Certificate()).toString()));

		if(!subject.contains(AppConstants.FM_ID_MD5) && 
		   !subjectAltNames.contains(AppConstants.FM_ID_MD5) ) {
			log.error(ValidationCodes.RUC_NO_FERMAR.getMensaje());
			throw new SignatureValidationException(ValidationCodes.RUC_NO_FERMAR);
		}
	}
}
