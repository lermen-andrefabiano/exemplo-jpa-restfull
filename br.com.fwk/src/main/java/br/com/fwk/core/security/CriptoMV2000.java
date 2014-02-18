package br.com.fwk.core.security;

/**
 * Classe que implementa a criptografia utilizada no sistema MV2000
 * 
 * @author: thiago.vieira
 * @since: 12/08/2011
 * 
 * */    
     
public class CriptoMV2000 {

	private static final char CARACTER_PREENCHIMENTO = 'K';
	private static final int NUMERO_CARACTER_PREENCHIMENTO = 30;
	private static final String STRING_VAZIA = " ";
	
	// --
	
	/**
	 * Metodo que criptografa a senha do usuario utilizando a mesma criptografia do sistemas legados MV2000
	 * 
	 * @param usuario id do usu�rio
	 * @param senha 
	 * 
	 * @return A senha criptografada
	 * */
	public static String criptografa(String usuario, String senha){
		String valor = usuario.toUpperCase()+senha.toUpperCase();
		String valorPadronizado = padroniza(valor);
		return criptografa(valorPadronizado);
	}	
	
	// --
	
	private static String padroniza(String value) {
		StringBuffer buffer = new StringBuffer(value);
		if (value != null && value.length() < NUMERO_CARACTER_PREENCHIMENTO) {
			for (int i = 0; i < (NUMERO_CARACTER_PREENCHIMENTO - value.length()); i++) {
				buffer.append(CARACTER_PREENCHIMENTO);
			}
		}
		return buffer.toString();
	}

	private static String criptografa(String valor) {
		if (valor == null || valor.equals("")) {
			valor = STRING_VAZIA;
		}

		String retorno = "";
		for (int i = 0; i < valor.length(); i++) {
			int indice = i + 1;
			retorno += (char) (valor.charAt(i) + 2 * indice + indice / indice);
		}
		return retorno;
	}
	
	
	/**
	 * METODO RETIRADO DO PEP
	 * 
     * Inverte o valor passado e calcula um 'dígito verificador' baseado na soma dos digitos, concatenando o último
     * dígito da soma obtida ao final do valor invertido e o penúltimo dígito ao início.
     * 
     * ex: 12312 inverte para 21321 soma dos digitos = 9 -> 09 valor encriptado = 0213219
     * 
     * @param value
     * @return
     */
    public static String encriptReverseNumber(String value) {
        // Inverte o valor original
        String reverseValue = new StringBuilder(value).reverse().toString();

        // Variável que conterá a soma dos dígitos
        Long sum = 0L;

        // Somando os dígitos
        for (int i = 0; i < reverseValue.length(); i++) {
            char individualValue = reverseValue.charAt(i);

            sum += new Long(new String(new char[] {individualValue}));
        }

        // Obtendo dígido verificador
        String verifierDigit = sum.toString();

        // Se for um valor de um dígito ( < 10 ) acrescenta um '0' (zero)
        if (sum < 10) {
            verifierDigit = "0" + verifierDigit;
        }

        // O penúltimo dígito de verifierDigit é concatenado ao início do número invertido e o último dígito ao final
        String encriptValue = verifierDigit.charAt(verifierDigit.length() - 2) + reverseValue
                + verifierDigit.charAt(verifierDigit.length() - 1);

        return encriptValue;
    }

    /**
	 * METODO RETIRADO DO PEP
     * 
     * @param encriptValue
     * @return
     */
    public static String decriptReverseNumber(String encriptValue) {
        // Desconsidera o primeito e o último dígito (proveniente do dígito verificador)
        encriptValue = encriptValue.substring(1, encriptValue.length() - 1);

        // Faz a inversão para obter o valor original
        String decriptValue = new StringBuilder(encriptValue).reverse().toString();

        return decriptValue;
    }

    
    
    

    /**
     * baseado no metodo do framework do PEP
     * br.com.mv.framework.utilities.Cryptography.criptografe2(..) 
     */
    public static String criptografe2(String valor) {
        final String EMPTY_STR = " ";
        // o primeiro caracter # foi colocado para se igualar ao forms
        String ascii3 = "#ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABC"
                + "DEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLM"
                + "NOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        if (valor == null || valor.equals("")) {
			valor = EMPTY_STR ;
        }

        valor = valor.toUpperCase();

        String retorno = "";

        for (int i = 1; i <= valor.length(); i++) {
            char caracter = valor.charAt(i - 1);
            int posicao = ascii3.indexOf(caracter);

            if (posicao > 0) {
                if (i == 1) {
                    if ("0123456789".indexOf(ascii3.charAt(2 * posicao + i / i)) != -1) {
                        retorno += 'x';
                    } else {
                        retorno += ascii3.charAt(2 * posicao + i / i);
                    }
                } else {
                    retorno += ascii3.charAt(2 * posicao + i / i);
                }
            } else {
                retorno += ascii3.charAt(i);
            }
        }
        return retorno;
    }	


}
