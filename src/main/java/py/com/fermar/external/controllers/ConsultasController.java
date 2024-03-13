package py.com.fermar.external.controllers;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.Api;
import py.com.fermar.commons.exception.AttributeNotFoundException;
import py.com.fermar.commons.exception.SIFENException;
import py.com.fermar.external.constants.ServiceConstants;
import py.com.fermar.external.service.ExternalService;
import py.com.fermar.external.utils.RucDTO;

@RestController
@RequestMapping(value = "/")
@Api(tags = "FerMar External Services", value= "API Rest para consultas de servicios externos")
public class ConsultasController {

	@Autowired
	private ExternalService externalService;
	
	private static final Logger LOGGER = LoggerFactory
			.getLogger(ConsultasController.class.getName());

	public static final String MENSAJE_ERROR = "Error Inesperado";
	private static final String MENSAJE = "mensaje";
	
	private static final String HEADER_USERNAME = "consumer-username";
	
	public static final String ADMIN_USER = "Administrador";
	public static final String NON_EMITTER_USERS = ADMIN_USER + ",anonymousUser";
	
	
	@GetMapping(value = "ruc-sifen/{ruc}")
	public ResponseEntity<Object> getRUCFromSifen(
				@RequestHeader Map<String, Object> header,
				@PathVariable("ruc") String ruc) throws SIFENException {
		Map<String, Object> mapDTEResponse = new HashMap<>();

		try {

			String cUser = 
					getValidateParam(header, HEADER_USERNAME).toString();
			
			if(!NON_EMITTER_USERS.contains(cUser)) {
				cUser = null;
			}
			
			if(ruc.isEmpty() ||
					ruc.length()<5) {
				
				throw new SIFENException(
						ServiceConstants.ValidationCodes.RUC_NO_EXISTE.getCodigo() + 
						"-" +
						ServiceConstants.ValidationCodes.RUC_NO_EXISTE.getMensaje());				
			}
			
			RucDTO dto = 
					externalService.getRUCFromSifen(cUser, ruc);
			
			mapDTEResponse.put("datosRUC", dto);
			
			return new ResponseEntity<>(
					mapDTEResponse, 
					HttpStatus.OK);

		} catch (SIFENException | AttributeNotFoundException e) {
			mapDTEResponse.put(MENSAJE, e.getMessage());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(mapDTEResponse);
			
		} catch (Exception e) {
			mapDTEResponse.put(MENSAJE, MENSAJE_ERROR);
			LOGGER.error(e.getMessage(), e);
			return ResponseEntity.status(HttpStatus.CONFLICT).body(mapDTEResponse);

		}
	}
	
	private Object getValidateParam(Map<String, Object> map, String key){
		if (map == null || 
				map.get(key) == null ||
				map.get(key).toString().isEmpty()) {
			throw new AttributeNotFoundException("Falta el parámetro obligatorio [" + key + "]");
		}
		
		return map.get(key);
	}
}
