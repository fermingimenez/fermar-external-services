package py.com.fermar.external.utils;

/**
 *
 */
public class SoapResponse<T> {

    private String dEstRes;
    private Integer dCodRes;
    private String dMsgRes;
    private T dato;

    public SoapResponse() {
    	//do nothing
    }
    
    public String getdEstRes() {
		return dEstRes;
	}

	public void setdEstRes(String dEstRes) {
		this.dEstRes = dEstRes;
	}

	public Integer getdCodRes() {
		return dCodRes;
	}

	public void setdCodRes(Integer dCodRes) {
		this.dCodRes = dCodRes;
	}

	public String getdMsgRes() {
		return dMsgRes;
	}

	public void setdMsgRes(String dMsgRes) {
		this.dMsgRes = dMsgRes;
	}

	public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }

    @Override
    public String toString() {
        return "KResponse{" + "estado=" + dEstRes + ", mensaje=" + dMsgRes + ", dato=" + dato + '}';
    }

}
