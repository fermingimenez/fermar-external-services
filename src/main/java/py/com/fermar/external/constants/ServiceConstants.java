package py.com.fermar.external.constants;

public abstract class ServiceConstants {
	
	public enum ValidationCodes {

		RUC_NO_EXISTE("0500","RUC no existe"),
		SIN_PERMISO("0501","RUC sin permiso consulta WS"),
		RUC_EXISTE("0502", "RUC encontrado");


		private String codigo;
		private String mensaje;

		ValidationCodes(String codigo, String mensaje) {
			this.codigo = codigo;
			this.mensaje = mensaje;
		}

		public String getCodigo() {
			return codigo;
		}

		public Integer getCodigoInt() {
			return Integer.valueOf(codigo);
		}

		public String getMensaje() {
			return mensaje;
		}

	}
}
