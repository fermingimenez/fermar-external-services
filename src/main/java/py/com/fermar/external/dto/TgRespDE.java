//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.11.08 a las 02:31:37 PM PYST 
//


package py.com.fermar.external.dto;

import java.io.Serializable;
import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 				Grupo de Campos que identifican al responsable de la generación del DE 
 * 			
 * 
 * <p>Clase Java para tgRespDE complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgRespDE"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="iTipIDRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tiTipIDRespDE"/&gt;
 *         &lt;element name="dDTipIDRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDTipIDRespDE"/&gt;
 *         &lt;element name="dNumIDRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNumDocId"/&gt;
 *         &lt;element name="dNomRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdNombre"/&gt;
 *         &lt;element name="dCarRespDE" type="{http://ekuatia.set.gov.py/sifen/xsd}tdCargo"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgRespDE", propOrder = {
    "iTipIDRespDE",
    "ddTipIDRespDE",
    "dNumIDRespDE",
    "dNomRespDE",
    "dCarRespDE"
})
public class TgRespDE
    implements Serializable
{

    @XmlElement(required = true)
    protected BigInteger iTipIDRespDE;
    @XmlElement(name = "dDTipIDRespDE", required = true)
    @XmlSchemaType(name = "anySimpleType")
    protected String ddTipIDRespDE;
    @XmlElement(required = true)
    protected String dNumIDRespDE;
    @XmlElement(required = true)
    protected String dNomRespDE;
    @XmlElement(required = true)
    protected String dCarRespDE;

    /**
     * Obtiene el valor de la propiedad iTipIDRespDE.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getITipIDRespDE() {
        return iTipIDRespDE;
    }

    /**
     * Define el valor de la propiedad iTipIDRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setITipIDRespDE(BigInteger value) {
        this.iTipIDRespDE = value;
    }

    /**
     * Obtiene el valor de la propiedad ddTipIDRespDE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDTipIDRespDE() {
        return ddTipIDRespDE;
    }

    /**
     * Define el valor de la propiedad ddTipIDRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDTipIDRespDE(String value) {
        this.ddTipIDRespDE = value;
    }

    /**
     * Obtiene el valor de la propiedad dNumIDRespDE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNumIDRespDE() {
        return dNumIDRespDE;
    }

    /**
     * Define el valor de la propiedad dNumIDRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNumIDRespDE(String value) {
        this.dNumIDRespDE = value;
    }

    /**
     * Obtiene el valor de la propiedad dNomRespDE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDNomRespDE() {
        return dNomRespDE;
    }

    /**
     * Define el valor de la propiedad dNomRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDNomRespDE(String value) {
        this.dNomRespDE = value;
    }

    /**
     * Obtiene el valor de la propiedad dCarRespDE.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDCarRespDE() {
        return dCarRespDE;
    }

    /**
     * Define el valor de la propiedad dCarRespDE.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDCarRespDE(String value) {
        this.dCarRespDE = value;
    }

}
