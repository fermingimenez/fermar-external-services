//
// Este archivo ha sido generado por la arquitectura JavaTM para la implantación de la referencia de enlace (JAXB) XML v2.2.11 
// Visite <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Todas las modificaciones realizadas en este archivo se perderán si se vuelve a compilar el esquema de origen. 
// Generado el: 2019.11.08 a las 02:31:37 PM PYST 
//


package py.com.fermar.external.dto;

import java.io.Serializable;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                 Grupo de Campos de la Actividad Economica
 *             
 * 
 * <p>Clase Java para tgActEco complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="tgActEco"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="cActEco" type="{http://ekuatia.set.gov.py/sifen/xsd}tcActEco"/&gt;
 *         &lt;element name="dDesActEco" type="{http://ekuatia.set.gov.py/sifen/xsd}tdDesActEco"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "tgActEco", propOrder = {
    "cActEco",
    "dDesActEco"
})
public class TgActEco
    implements Serializable
{

    @XmlElement(required = true)
    protected String cActEco;
    @XmlElement(required = true)
    protected String dDesActEco;

    /**
     * Obtiene el valor de la propiedad cActEco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCActEco() {
        return cActEco;
    }

    /**
     * Define el valor de la propiedad cActEco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCActEco(String value) {
        this.cActEco = value;
    }

    /**
     * Obtiene el valor de la propiedad dDesActEco.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDDesActEco() {
        return dDesActEco;
    }

    /**
     * Define el valor de la propiedad dDesActEco.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDDesActEco(String value) {
        this.dDesActEco = value;
    }

}
