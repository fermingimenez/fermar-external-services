package py.com.fermar.external.service;

import py.com.fermar.commons.exception.SIFENException;
import py.com.fermar.external.utils.RucDTO;

public interface ExternalService {
	
	RucDTO getRUCFromSifen(String ruc) throws SIFENException;
	
}
