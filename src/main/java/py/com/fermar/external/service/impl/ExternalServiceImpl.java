package py.com.fermar.external.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import py.com.fermar.commons.exception.SIFENException;
import py.com.fermar.external.constants.ServiceConstants;
import py.com.fermar.external.service.ExternalService;
import py.com.fermar.external.service.SifenService;
import py.com.fermar.external.utils.RucDTO;
import py.com.fermar.external.utils.SoapResponse;

@Service
public class ExternalServiceImpl implements ExternalService{
	
	@Autowired
	private SifenService sifenService;
	
	@Override
	public RucDTO getRUCFromSifen(String ruc) throws SIFENException{
		
		RucDTO resProc = new RucDTO();
		

		SoapResponse<RucDTO> response = 
				sifenService.getRUCFromSifen(ruc);
		
		if(ServiceConstants.ValidationCodes.RUC_EXISTE.getCodigoInt()
				.compareTo(response.getdCodRes()) == 0) {
			if (response.getDato() != null) {
	            return response.getDato();
			}				
		}
		else {
			throw new SIFENException(
					response.getdCodRes() + 
					"-" +
					response.getdMsgRes());
		}
		
		return resProc;
	}
}
