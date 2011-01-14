//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.2-7 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.05.20 at 05:17:49 PM CEST 
//


package it.cnr.jada.firma.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione"
})
@XmlRootElement(name = "IndirizzoPostale")
public class IndirizzoPostale {

    @XmlElements({
        @XmlElement(name = "Denominazione", required = true, type = Denominazione.class),
        @XmlElement(name = "Toponimo", required = true, type = Toponimo.class),
        @XmlElement(name = "Civico", required = true, type = Civico.class),
        @XmlElement(name = "CAP", required = true, type = CAP.class),
        @XmlElement(name = "Comune", required = true, type = Comune.class),
        @XmlElement(name = "Provincia", required = true, type = Provincia.class),
        @XmlElement(name = "Nazione", required = true, type = Nazione.class)
    })
    protected List<Object> denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione;

    /**
     * Gets the value of the denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDenominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Denominazione }
     * {@link Toponimo }
     * {@link Civico }
     * {@link CAP }
     * {@link Comune }
     * {@link Provincia }
     * {@link Nazione }
     * 
     * 
     */
    public List<Object> getDenominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione() {
        if (denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione == null) {
            denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione = new ArrayList<Object>();
        }
        return this.denominazioneOrToponimoOrCivicoOrCAPOrComuneOrProvinciaOrNazione;
    }

}
